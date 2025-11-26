package com.example.runsyncmockups.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.runsyncmockups.model.HumidityState

@Composable
fun rainAlert(humidityState: HumidityState, onDismiss: () -> Unit) {
    val isRainy = humidityState.willLikelyRain

    AnimatedVisibility(
        visible = isRainy,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F4F8)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta de lluvia",
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡Alerta de Lluvia!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Humedad actual: ${
                            String.format(
                                "%.1f",
                                humidityState.currentHumidity
                            )
                        }%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1565C0)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Precauciones necesarias:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF37474F)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text =
                            "• Lleva un paraguas o impermeable\n" +
                                    "• Usa calzado antideslizante\n" +
                                    "• Ten cuidado con superficies mojadas\n" +
                                    "• Mantén tus dispositivos protegidos\n" +
                                    "• Busca refugio si es necesario",
                        fontSize = 14.sp,
                        color = Color(0xFF546E7A),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Entendido", fontSize = 16.sp)
                    }
                }
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun RainAlertPreview() {
    val humidityState = HumidityState(
        currentHumidity = 85.5f,
        willLikelyRain = true
    )

    rainAlert(
        humidityState = humidityState,
        onDismiss = {}
    )
}