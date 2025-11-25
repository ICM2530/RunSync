package com.example.runsyncmockups.ui.components

import android.R.attr.title
import android.R.attr.visible
import android.app.Dialog
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
    ){
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF717D8C)
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
                        tint = Color(0xFF535C67),
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡Alerta de Lluvia!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3A47)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Humedad actual: ${String.format("%.1f", humidityState.currentHumidity)}°C",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color( 0xFF3B3B98)
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
                        text =
                            "• Mantente hidratado\n" +
                                    "• Evita la exposición directa al sol\n" +
                                    "• Usa protector solar\n" +
                                    "• Reduce la actividad física intensa\n" +
                                    "• Busca lugares frescos"
                        ,
                        fontSize = 14.sp,
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4B6584)
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