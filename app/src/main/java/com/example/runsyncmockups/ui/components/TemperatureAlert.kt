package com.example.runsyncmockups.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.runsyncmockups.model.TemperatureState

@Composable
fun TemperatureAlert(
    temperatureState: TemperatureState,
    onDismiss: () -> Unit = {}
) {
    val isExtreme = temperatureState.isHot || temperatureState.isCold
    val alertColor = if (temperatureState.isHot) Color(0xFFFFF3E0) else Color(0xFFE3F2FD)
    val iconColor = if (temperatureState.isHot) Color(0xFFFF6F00) else Color(0xFF1976D2)
    val titleColor = if (temperatureState.isHot) Color(0xFFE65100) else Color(0xFF0D47A1)
    val textColor = if (temperatureState.isHot) Color(0xFFFF6F00) else Color(0xFF1976D2)
    val title = if (temperatureState.isHot) "¡Alerta de Calor!" else "¡Alerta de Frío!"

    AnimatedVisibility(
        visible = isExtreme,
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
                    containerColor = alertColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta de temperatura",
                        tint = iconColor,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Temperatura actual: ${String.format("%.1f", temperatureState.currentTemperature)}°C",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Precauciones necesarias:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF424242)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (temperatureState.isHot) {
                            "• Mantente hidratado\n" +
                                    "• Evita la exposición directa al sol\n" +
                                    "• Usa protector solar\n" +
                                    "• Reduce la actividad física intensa\n" +
                                    "• Busca lugares frescos"
                        } else {
                            "• Abrígate adecuadamente\n" +
                                    "• Protege las extremidades\n" +
                                    "• Evita la exposición prolongada\n" +
                                    "• Mantén el cuerpo en movimiento\n" +
                                    "• Busca lugares cálidos"
                        },
                        fontSize = 14.sp,
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = iconColor
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

@Composable
fun TemperatureIndicator(temperatureState: TemperatureState) {
    if (!temperatureState.sensorAvailable) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "Sensor de temperatura no disponible",
                fontSize = 12.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = if (temperatureState.isHot) Color(0xFFFFE0B2) else Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (temperatureState.isHot) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF6F00),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Temperatura: ${String.format("%.1f", temperatureState.currentTemperature)}°C",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (temperatureState.isHot) Color(0xFFE65100) else Color(0xFF2E7D32)
                )
            }
        }
    }
}

