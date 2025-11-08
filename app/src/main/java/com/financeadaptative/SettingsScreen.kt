package com.financeadaptative

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onClose: () -> Unit, vm: SettingsViewModel = viewModel()) {
    val settings by vm.settings.collectAsState()
    var currency by remember(settings) { mutableStateOf(settings.currencySymbol) }
    var category by remember(settings) { mutableStateOf(settings.defaultCategory) }
    var method by remember(settings) { mutableStateOf(settings.defaultPaymentMethod) }
    var showTips by remember(settings) { mutableStateOf(settings.showTips) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Configuración") }, navigationIcon = {
            IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Cerrar") }
        }, actions = {
            Icon(Icons.Filled.Settings, contentDescription = null)
        })
    }) { pad ->
        val bottomPadding = 96.dp
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp)
                .padding(bottom = bottomPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Preferencias", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

            // Moneda
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AttachMoney, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Símbolo de moneda", style = MaterialTheme.typography.titleMedium)
                    }
                    OutlinedTextField(value = currency, onValueChange = { currency = it }, singleLine = true, placeholder = { Text("Ej: S/., $, €") })
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("S/.", "$", "€", "£", "¥").forEach { s ->
                            AssistChip(onClick = { currency = s }, label = { Text(s) })
                        }
                    }
                    Button(onClick = { vm.setCurrency(currency) }, enabled = currency.isNotBlank(), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar moneda")
                    }
                }
            }

            // Categoría por defecto
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.TipsAndUpdates, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.width(8.dp))
                        Text("Categoría predeterminada", style = MaterialTheme.typography.titleMedium)
                    }
                    val categories = listOf("General", "Alimentación", "Transporte", "Hogar", "Salud", "Ocio")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach { c ->
                            FilterChip(selected = c == category, onClick = { category = c }, label = { Text(c) })
                        }
                    }
                    Button(onClick = { vm.setDefaultCategory(category) }, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar categoría")
                    }
                }
            }

            // Método de pago
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(8.dp))
                        Text("Método de pago predeterminado", style = MaterialTheme.typography.titleMedium)
                    }
                    val methods = listOf("Efectivo", "Tarjeta", "Transferencia", "Billetera")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        methods.forEach { m ->
                            FilterChip(selected = m == method, onClick = { method = m }, label = { Text(m) })
                        }
                    }
                    Button(onClick = { vm.setDefaultPayment(method) }, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar método")
                    }
                }
            }

            // Consejos
            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.TipsAndUpdates, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(Modifier.width(8.dp))
                            Text("Mostrar consejos en formularios", style = MaterialTheme.typography.titleMedium)
                        }
                        AnimatedVisibility(visible = showTips, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                            Text("Verás pequeños tips y placeholders inteligentes para acelerar el registro.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(checked = showTips, onCheckedChange = { showTips = it; vm.setShowTips(it) })
                }
            }
        }
    }
}
