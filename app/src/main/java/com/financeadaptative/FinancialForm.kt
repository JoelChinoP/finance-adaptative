package com.financeadaptative

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.financeadaptative.ui.theme.FinPerTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun FinancialForm(
    modifier: Modifier = Modifier,
    currencySymbol: String = "S/.",
    defaultCategory: String = "General",
    defaultPaymentMethod: String = "Efectivo",
    showTips: Boolean = true,
    onSubmit: (title: String, amount: Double, category: String, date: String, note: String?, paymentMethod: String, location: String, recurring: Boolean) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onCancel: () -> Unit = {}
) {
    val ctx = LocalContext.current

    // Estados del formulario (guardados en recomposiciones)
    var title by remember { mutableStateOf("") }
    var amountRaw by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var category by remember { mutableStateOf(defaultCategory) }
    var date by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    // Campos adicionales
    var paymentMethod by remember { mutableStateOf(defaultPaymentMethod) }
    var location by remember { mutableStateOf("") }
    var recurring by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Validación simple
    val amount = amountRaw.replace(",", ".").toDoubleOrNull() ?: -1.0
    val isValid = title.isNotBlank() && amount > 0 && category.isNotBlank() && date.isNotBlank()

    // Estilo de tarjeta contenedora
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Nuevo movimiento", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(12.dp))
        ElevatedCard(
            shape = shape,
            elevation = CardDefaults.elevatedCardElevation(4.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                // Tipo: Gasto/Ingreso
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = isExpense,
                        onClick = { isExpense = true },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        icon = { Icon(Icons.Default.Remove, contentDescription = null) },
                        label = { Text("Gasto") }
                    )
                    SegmentedButton(
                        selected = !isExpense,
                        onClick = { isExpense = false },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        label = { Text("Ingreso") }
                    )
                }

                Spacer(Modifier.height(12.dp))
                // Título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Title, contentDescription = "Icono de título") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Monto
                OutlinedTextField(
                    value = amountRaw,
                    onValueChange = { input -> if (input.matches(Regex("[0-9]*[.,]?[0-9]*"))) amountRaw = input },
                    label = { Text("Monto") },
                    placeholder = { Text("0.00") },
                    leadingIcon = { Icon(imageVector = Icons.Default.AttachMoney, contentDescription = "Icono de monto") },
                    prefix = { Text(currencySymbol) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Categoría
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        placeholder = { Text("Ej: Alimentación") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Label, contentDescription = "Icono de categoría") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )
                    val suggestions = listOf("Alimentación", "Transporte", "Hogar", "Salud", "Ocio", "Educación")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        suggestions.forEach { s ->
                            AssistChip(onClick = { category = s }, label = { Text(s) }, leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) })
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Fecha
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha") },
                    placeholder = { Text("YYYY-MM-DD") },
                    leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Icono de fecha") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Nota opcional
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota (opcional)") },
                    placeholder = { Text("Detalle breve") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                // Método de pago
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Método de pago") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    val methods = listOf("Efectivo", "Tarjeta", "Transferencia", "Billetera")
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        methods.forEach { m ->
                            DropdownMenuItem(text = { Text(m) }, onClick = { paymentMethod = m; expanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                // Ubicación
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación") },
                    placeholder = { Text("Ej: Supermercado, Oficina") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Recurrente", modifier = Modifier.weight(1f))
                    Switch(checked = recurring, onCheckedChange = { recurring = it })
                }

                AnimatedVisibility(visible = showTips, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                    Text(
                        "Consejo: si es Gasto, el monto se restará del balance; si es Ingreso, se sumará.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Botones
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    val clearEnabled = title.isNotBlank() || amountRaw.isNotBlank() || category.isNotBlank() || date.isNotBlank() || note.isNotBlank()
                    TextButton(onClick = { title = ""; amountRaw = ""; category = ""; date = ""; note = "" }, enabled = clearEnabled, modifier = Modifier.semantics { }) { Text("Limpiar") }

                    Spacer(Modifier.weight(1f))

                    Button(onClick = {
                        if (!isValid) {
                            Toast.makeText(ctx, "Completa los campos y usa un monto > 0", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSubmitting = true
                        val signed = if (isExpense) -amount else amount
                        val summary = "$title | $currencySymbol ${"%.2f".format(kotlin.math.abs(signed))} ${if (isExpense) "(Gasto)" else "(Ingreso)"} | $category | $date | $paymentMethod | $location | ${if (recurring) "Recurrente" else "Único"}"
                        Toast.makeText(ctx, summary, Toast.LENGTH_LONG).show()
                        onSubmit(title, signed, category, date, note.ifBlank { null }, paymentMethod, location, recurring)

                        Handler(Looper.getMainLooper()).postDelayed({ isSubmitting = false }, 800)
                    }, enabled = !isSubmitting, modifier = Modifier.height(48.dp), shape = RoundedCornerShape(12.dp)) {
                        if (isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Enviando…")
                        } else {
                            Text("Añadir")
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
                FinancialForm(onSubmit = { _, _, _, _, _, _, _, _ -> }, onCancel = {})
            }
        }
    }
}
