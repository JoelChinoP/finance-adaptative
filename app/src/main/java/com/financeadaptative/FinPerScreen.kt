package com.financeadaptative

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.financeadaptative.ui.theme.FinPerTheme

@Composable fun FinPerApp() = FinPerScreen()

@Composable
fun FinPerScreen() {
    val cfg = LocalConfiguration.current
    val useRow = with(cfg) { orientation == Configuration.ORIENTATION_LANDSCAPE || screenWidthDp >= 600 }
    var balance by rememberSaveable { mutableStateOf(0.0) }
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(.1f),
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.background
        )
    )
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            Modifier
                .fillMaxSize()
                .background(gradient)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            AdaptiveLayout(
                useRowLayout = useRow,
                balance = balance,
                onBalanceChange = { balance = it }
            )
        }
    }
}

@Composable
private fun AdaptiveLayout(
    useRowLayout: Boolean,
    balance: Double,
    onBalanceChange: (Double) -> Unit
) {
    val mod = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 32.dp)
        .animateContentSize()
    val add = { onBalanceChange(balance + 100.0) }
    val reset = { onBalanceChange(0.0) }
    val spacing = 32.dp
    if (useRowLayout) {
        Row(
            mod,
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleSection(balance, Modifier.weight(1f))
            ActionSection(add, reset, Modifier.widthIn(max = 300.dp))
        }
    } else {
        Column(
            mod,
            verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleSection(balance)
            ActionSection(add, reset)
        }
    }
}

@Composable
private fun TitleSection(balance: Double, modifier: Modifier = Modifier) {
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
                "FinanceAdaptive",
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
                "Balance Actual",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                "S/. ${"%.2f".format(balance)}",
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
            ) { Text("Agregar S/. 100", style = MaterialTheme.typography.labelLarge) }
            FilledTonalButton(
                onClick = onResetBalance,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = shape16
            ) { Text("Reiniciar Balance", style = MaterialTheme.typography.labelLarge) }
        }
    }
}
