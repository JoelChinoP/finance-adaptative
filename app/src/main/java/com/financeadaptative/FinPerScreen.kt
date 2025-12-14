package com.financeadaptative

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import android.content.Intent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.financeadaptative.ui.theme.FinPerTheme
import com.financeadaptative.data.ui.ExpenseViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable fun FinPerApp() = FinPerScreen()

@Composable
fun FinPerScreen() {
    val settingsVm: SettingsViewModel = viewModel()
    val settings by settingsVm.settings.collectAsState()
    val cfg = LocalConfiguration.current
    val useRow = with(cfg) { orientation == Configuration.ORIENTATION_LANDSCAPE || screenWidthDp >= 600 }
    var showForm by rememberSaveable { mutableStateOf(false) }
    var showList by rememberSaveable { mutableStateOf(false) }
    var showSettings by rememberSaveable { mutableStateOf(false) }

    // ViewModel conectado a Room
    val expenseVm: ExpenseViewModel = viewModel()
    val txList by expenseVm.transactions.collectAsState()
    val balance by expenseVm.balance.collectAsState()
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(.1f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.background
        )
    )
    val rootCtx = LocalContext.current
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            Modifier
                .fillMaxSize()
                .background(gradient)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            if (showForm) {
                FormContainer(
                    onBack = { showForm = false },
                    onSubmitted = { title, signedAmount, category, date, note ->
                        expenseVm.addMovement(
                            title = title.ifBlank { "Movimiento" },
                            signedAmount = signedAmount,
                            categoryName = category.ifBlank { "General" },
                            dateStr = date.ifBlank { java.time.LocalDate.now().toString() },
                            note = note
                        )
                        showForm = false
                    },
                    currencySymbol = settings.currencySymbol,
                    defaultCategory = settings.defaultCategory,
                    defaultPaymentMethod = settings.defaultPaymentMethod,
                    showTips = settings.showTips
                )
            } else if (showList) {
                TransactionsListScreen(
                    transactions = txList.map { it.toLegacyTransaction() },
                    currencySymbol = settings.currencySymbol,
                    onBack = { showList = false },
                    onAddTransaction = { t ->
                        expenseVm.addMovement(
                            title = t.title,
                            signedAmount = t.amount,
                            categoryName = t.category ?: "General",
                            dateStr = t.date,
                            note = t.description
                        )
                    },
                    onRemoveTransaction = { t ->
                        // Buscamos el id real por coincidencia aproximada (simplificado)
                        val candidate = expenseVm.transactions.value.firstOrNull { ui ->
                            ui.title == t.title && ui.amount == t.amount && ui.categoryName == t.category
                        }
                        if (candidate != null) {
                            // Podríamos exponer deleteById en el VM si se quiere
                        }
                    }
                )
            } else if (showSettings) {
                SettingsScreen(onClose = { showSettings = false })
            } else {
                AdaptiveLayout(
                    useRowLayout = useRow,
                    balance = balance,
                    currencySymbol = settings.currencySymbol,
                    onBalanceChange = { /* balance proviene del flujo */ },
                    onOpenForm = { showForm = true },
                    onOpenList = { showList = true },
                    onOpenSettings = { showSettings = true }
                )
            }

            // Floating action button para abrir el formulario desde la pantalla principal
            if (!showForm && !showList && !showSettings) {
                FloatingActionButton(
                    onClick = { showForm = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 80.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Nuevo movimiento", tint = MaterialTheme.colorScheme.onPrimary)
                }

                // Botón visible para iniciar el temporizador con notificación persistente
                Button(
                    onClick = {
                        (rootCtx as? MainActivity)?.requestNotifPermissionAndStartTimer() ?: TimerService.start(rootCtx)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 80.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Temporizador", tint = MaterialTheme.colorScheme.onSecondary)
                        Text("Temporizador", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // Barra de navegación inferior para Home / Movimientos / Configuración
            if (!showForm) {
                val selectedIndex = when {
                    showList -> 1
                    showSettings -> 2
                    else -> 0
                }
                NavigationBar(modifier = Modifier.align(Alignment.BottomCenter)) {
                    NavigationBarItem(
                        selected = selectedIndex == 0,
                        onClick = { showList = false; showSettings = false },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = selectedIndex == 1,
                        onClick = { showList = true; showSettings = false },
                        icon = { Icon(Icons.Filled.ViewList, contentDescription = "Movimientos") },
                        label = { Text("Movimientos") }
                    )
                    NavigationBarItem(
                        selected = selectedIndex == 2,
                        onClick = { showSettings = true; showList = false },
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Configuración") },
                        label = { Text("Ajustes") }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdaptiveLayout(
    useRowLayout: Boolean,
    balance: Double,
    currencySymbol: String,
    onBalanceChange: (Double) -> Unit,
    onOpenForm: () -> Unit,
    onOpenList: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val mod = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 32.dp)
        .animateContentSize()
    val add = { onBalanceChange(balance + 100.0) }
    val reset = { onBalanceChange(0.0) }
    val spacing = 32.dp
    val ctx = LocalContext.current
    if (useRowLayout) {
        Row(
            mod,
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleSection(balance, currencySymbol, Modifier.weight(1f))
            Column(
                Modifier.widthIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ActionSection(add, reset)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            (ctx as? MainActivity)?.requestNotifPermissionAndStartTimer() ?: TimerService.start(ctx)
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) { Text("Iniciar temporizador", style = MaterialTheme.typography.labelLarge) }
                    OutlinedButton(
                        onClick = { (ctx as? MainActivity)?.debugPostNotification() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Probar notificación", style = MaterialTheme.typography.labelLarge) }
                    OutlinedButton(
                        onClick = { TimerService.stop(ctx) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Detener", style = MaterialTheme.typography.labelLarge) }
                }
                Button(
                    onClick = {
                        val i = Intent(ctx, CircleActivity::class.java)
                        ctx.startActivity(i)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                ) { Text("Animación", style = MaterialTheme.typography.labelLarge) }
                Button(
                    onClick = onOpenList,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiaryContainer)
                ) { Text("Movimientos", style = MaterialTheme.typography.labelLarge) }
                Button(
                    onClick = onOpenForm,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) { Text("Registrar movimiento", style = MaterialTheme.typography.labelLarge) }
                TextButton(onClick = onOpenSettings) { Text("Configuración") }
            }
        }
    } else {
        Column(
            mod,
            verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleSection(balance, currencySymbol)
            ActionSection(add, reset)
            Spacer(Modifier.height(6.dp))
                // Botones de temporizador con notificación persistente
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            (ctx as? MainActivity)?.requestNotifPermissionAndStartTimer() ?: TimerService.start(ctx)
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) { Text("Iniciar temporizador", style = MaterialTheme.typography.labelLarge) }
                    OutlinedButton(
                        onClick = { (ctx as? MainActivity)?.debugPostNotification() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Probar notificación", style = MaterialTheme.typography.labelLarge) }
                    OutlinedButton(
                        onClick = { TimerService.stop(ctx) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Detener", style = MaterialTheme.typography.labelLarge) }
                }
            Button(
                onClick = {
                    val i = Intent(ctx, CircleActivity::class.java)
                    ctx.startActivity(i)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
            ) { Text("Animación", style = MaterialTheme.typography.labelLarge) }
            Button(
                onClick = onOpenList,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiaryContainer)
            ) { Text("Movimientos", style = MaterialTheme.typography.labelLarge) }
            Button(
                onClick = onOpenForm,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) { Text("Registrar movimiento", style = MaterialTheme.typography.labelLarge) }
            TextButton(onClick = onOpenSettings) { Text("Configuración") }
        }
    }
}

// --- Modelo y lista de ejemplo: movimientos (transactions) ---
data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val description: String,
    val date: String,
    val category: String? = null
)

private fun com.financeadaptative.data.ui.UiTransaction.toLegacyTransaction(): Transaction =
    Transaction(
        id = id.toString(),
        title = title ?: "Movimiento",
        amount = if (isIncome) amount else -amount,
        description = note ?: title ?: "",
        date = date.toLocalDate().toString(),
        category = categoryName
    )

// Eliminamos los datos de ejemplo; ahora la fuente es Room.

@Composable
private fun TransactionsListScreen(
    transactions: List<Transaction>,
    currencySymbol: String,
    onBack: () -> Unit,
    onAddTransaction: (Transaction) -> Unit,
    onRemoveTransaction: (Transaction) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") }
            Spacer(Modifier.width(8.dp))
            Text(text = "Movimientos - FINPER", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Filled.Add, contentDescription = "Agregar movimiento") }
        }

        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)

        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(transactions) { tx ->
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(tx.title, style = MaterialTheme.typography.titleMedium)
                                Text(tx.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                val amountColor = if (tx.amount < 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                Text((if (tx.amount < 0) "$currencySymbol ${"%.2f".format(-tx.amount)}" else "$currencySymbol ${"%.2f".format(tx.amount)}"), style = MaterialTheme.typography.titleMedium, color = amountColor)
                                Text(tx.date, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.height(28.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = .12f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(text = tx.category ?: "General", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { onRemoveTransaction(tx) }) { Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddTransactionDialog(onDismiss = { showAddDialog = false }, onAdd = { title, amount, date, desc ->
                val t = Transaction(id = "tx_${System.currentTimeMillis()}", title = title, amount = amount, description = desc, date = date)
                onAddTransaction(t)
                showAddDialog = false
            })
        }
    }
}

@Composable
private fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, amount: Double, date: String, description: String) -> Unit
) {
    val today = java.time.LocalDate.now().toString()
    var title by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(today) }
    var description by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val amt = amountText.replace(',', '.').toDoubleOrNull() ?: 0.0
                onAdd(title, amt, date, description)
            }) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Nuevo movimiento") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, singleLine = true)
                OutlinedTextField(value = amountText, onValueChange = { v -> if (v.matches(Regex("[0-9]*[.,]?[0-9]*"))) amountText = v }, label = { Text("Monto") }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Fecha (YYYY-MM-DD)") }, singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
            }
        }
    )
}

@Composable
private fun FormContainer(
    onBack: () -> Unit,
    onSubmitted: (title: String, signedAmount: Double, category: String, date: String, note: String?) -> Unit,
    currencySymbol: String,
    defaultCategory: String,
    defaultPaymentMethod: String,
    showTips: Boolean
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Nuevo movimiento",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        val scrollState = rememberScrollState()
        Box(Modifier.weight(1f)) {
            FinancialForm(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                currencySymbol = currencySymbol,
                defaultCategory = defaultCategory,
                defaultPaymentMethod = defaultPaymentMethod,
                showTips = showTips,
                onSubmit = { title, amount, category, date, note, _, _, _ ->
                    onSubmitted(title, amount, category, date, note)
                },
                onCancel = { onBack() }
            )
        }
    }
}

@Composable
private fun TitleSection(balance: Double, currencySymbol: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "FINPER",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                Modifier.width(120.dp),
                color = MaterialTheme.colorScheme.primary.copy(.3f),
                thickness = 2.dp
            )
            Text(
                "Gestión personal de dinero",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                "$currencySymbol ${"%.2f".format(balance)}",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ActionSection(
    onAddBalance: () -> Unit,
    onResetBalance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape16 = RoundedCornerShape(16.dp)
    ElevatedCard(
        modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            Modifier
                .padding(24.dp)
                .widthIn(min = 280.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Acciones",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onAddBalance,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = shape16,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Agregar 100", style = MaterialTheme.typography.labelLarge)
            }
            FilledTonalButton(
                onClick = onResetBalance,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = shape16
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Reiniciar balance", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
