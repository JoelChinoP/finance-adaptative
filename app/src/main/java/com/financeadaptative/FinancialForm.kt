package com.financeadaptative

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.financeadaptative.ui.theme.FinPerTheme

/**
 * FinancialForm - Formulario simple para registrar movimientos financieros (FINPER)
 *
 * Estructura basada solo en Column, Row y Spacer. Incluye validación básica y feedback con Toast.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialForm(
    modifier: Modifier = Modifier,
    onSubmit: (title: String, amount: Double, category: String, date: String, note: String?) -> Unit = { _,_,_,_,_ -> }
) {
    val ctx = LocalContext.current

    // Estados del formulario (guardados en recomposiciones)
    var title by remember { mutableStateOf("") }
    var amountRaw by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Validación simple
    val amount = amountRaw.replace(",", ".").toDoubleOrNull() ?: -1.0
    val isValid = title.isNotBlank() && amount > 0 && category.isNotBlank() && date.isNotBlank()

    // Estilo de tarjeta contenedora
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nuevo movimiento",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))
        ElevatedCard(
            shape = shape,
            elevation = CardDefaults.elevatedCardElevation(4.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                // Título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Title,
                            contentDescription = "Icono de título"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Monto
                OutlinedTextField(
                    value = amountRaw,
                    onValueChange = { input ->
                        // Permitir solo números, punto y coma
                        if (input.matches(Regex("[0-9]*[.,]?[0-9]*"))) amountRaw = input
                    },
                    label = { Text("Monto") },
                    placeholder = { Text("0.00") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Icono de monto"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Categoría (texto simple para mantenerlo básico)
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría") },
                    placeholder = { Text("Ej: Alimentación") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Label,
                            contentDescription = "Icono de categoría"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Fecha (texto con placeholder "YYYY-MM-DD")
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha") },
                    placeholder = { Text("YYYY-MM-DD") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Icono de fecha"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Nota opcional para enriquecer el formulario
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota (opcional)") },
                    placeholder = { Text("Detalle breve") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // Botón Enviar con microinteracción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val clearEnabled = title.isNotBlank() || amountRaw.isNotBlank() || category.isNotBlank() || date.isNotBlank() || note.isNotBlank()
                    TextButton(
                        onClick = {
                            title = ""; amountRaw = ""; category = ""; date = ""; note = ""
                        },
                        enabled = clearEnabled,
                        modifier = Modifier.semantics { contentDescription = "Limpiar formulario" }
                    ) { Text("Limpiar") }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (!isValid) {
                                Toast.makeText(ctx, "Completa los campos y usa un monto > 0", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isSubmitting = true
                            // Simular una espera corta
                            val summary = "$title | S/. ${"%.2f".format(amount)} | $category | $date"
                            Toast.makeText(ctx, summary, Toast.LENGTH_LONG).show()
                            onSubmit(title, amount, category, date, note.ifBlank { null })

                            // Deshabilitar 1s sin coroutines
                            Handler(Looper.getMainLooper()).postDelayed({
                                isSubmitting = false
                            }, 1000)
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .height(48.dp)
                            .semantics { contentDescription = "Enviar formulario" },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Enviando…")
                        } else {
                            Text("Enviar")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FinancialFormPreview() {
    FinPerTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.padding(16.dp)) {
                FinancialForm()
            }
        }
    }
}
