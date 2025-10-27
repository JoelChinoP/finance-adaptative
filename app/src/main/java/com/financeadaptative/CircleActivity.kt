package com.financeadaptative

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.financeadaptative.ui.theme.FinPerTheme

class CircleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinPerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CircleScreen(onClose = { finish() })
                }
            }
        }
    }
}

@Composable
fun CircleScreen(onClose: () -> Unit) {
    // Controlaremos el tamaño mediante un porcentaje (0% .. 100%).
    var percent by remember { mutableStateOf(30f) } // porcentaje inicial

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Text("Dibujo y animación simple", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // BoxWithConstraints permite conocer el espacio disponible y limitar el tamaño máximo.
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            contentAlignment = Alignment.Center
        ) {
            // Obtener el máximo diámetro posible (ancho o alto disponible menos paddings)
            val maxDiameterDp = remember(maxWidth, maxHeight) {
                // Reservamos algo de espacio para textos y controles, por eso usamos 0.6..0.8 del menor lado.
                val minSide = if (maxWidth < maxHeight) maxWidth else maxHeight
                // Limitar a 90% del menor lado para evitar que se salga
                (minSide * 0.9f)
            }

            // Mapear porcentaje (0..100) a dp entre minDp y maxDiameterDp
            val minDp = 40.dp
            val maxDp = maxDiameterDp
            val clampedPercent = percent.coerceIn(0f, 100f)
            val targetDp = remember(clampedPercent, maxDp) { minDp + (maxDp - minDp) * (clampedPercent / 100f) }

            val animatedSize by animateDpAsState(targetValue = targetDp, animationSpec = tween(durationMillis = 500))

            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(animatedSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                ) {}
            }
        }

        Spacer(Modifier.height(18.dp))

        // Barra de porcentaje y controles
        Column(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tamaño: ", style = MaterialTheme.typography.bodyLarge)
                Text(String.format("%.0f%%", percent), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.weight(1f))
                Button(onClick = { percent = 100f }, shape = MaterialTheme.shapes.small) {
                    Text("100%")
                }
            }

            Spacer(Modifier.height(8.dp))

            Slider(
                value = percent,
                onValueChange = { percent = it.coerceIn(0f, 100f) },
                valueRange = 0f..100f,
                steps = 0,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(12.dp))

            Spacer(Modifier.height(8.dp))

            // Botones sutiles para aumentar/reducir el porcentaje en pasos
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row {
                    // Decrementos
                    OutlinedButton(onClick = { percent = (percent - 10f).coerceIn(0f, 100f) }, shape = MaterialTheme.shapes.small) { Text("-10%") }
                    Spacer(Modifier.width(8.dp))
                    //OutlinedButton(onClick = { percent = (percent - 5f).coerceIn(0f, 100f) }, shape = MaterialTheme.shapes.small) { Text("-5%") }
                }
                Spacer(Modifier.width(8.dp))
                Row {
                    OutlinedButton(onClick = { percent = (percent + 5f).coerceIn(0f, 100f) }, shape = MaterialTheme.shapes.small) { Text("+10%") }
                    Spacer(Modifier.width(8.dp))
                    //OutlinedButton(onClick = { percent = (percent + 10f).coerceIn(0f, 100f) }, shape = MaterialTheme.shapes.small) { Text("+10%") }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { percent = 0f }) { Text("Min") }
                TextButton(onClick = onClose) { Text("Cerrar") }
            }
        }
    }
}
