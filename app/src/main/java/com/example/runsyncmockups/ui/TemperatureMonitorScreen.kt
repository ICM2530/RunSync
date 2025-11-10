package com.example.runsyncmockups.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.runsyncmockups.model.TemperatureViewModel
import com.example.runsyncmockups.ui.components.TemperatureAlert
import com.example.runsyncmockups.ui.components.TemperatureIndicator

@Composable
fun TemperatureMonitorScreen(
    temperatureViewModel: TemperatureViewModel = viewModel()
) {
    val context = LocalContext.current
    val temperatureState by temperatureViewModel.temperatureState.collectAsState()
    var showAlert by remember { mutableStateOf(false) }

    // Inicializar el sensor cuando se crea la pantalla
    LaunchedEffect(Unit) {
        temperatureViewModel.initializeSensor(context)
        temperatureViewModel.startListening()
    }

    // Detener el sensor cuando se destruye la pantalla
    DisposableEffect(Unit) {
        onDispose {
            temperatureViewModel.stopListening()
        }
    }

    // Mostrar alerta automáticamente cuando hace calor
    LaunchedEffect(temperatureState.isHot) {
        if (temperatureState.isHot) {
            showAlert = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Thermostat,
                contentDescription = "Termómetro",
                tint = if (temperatureState.isHot) Color(0xFFFF6F00) else Color(0xFF1976D2),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Monitor de Temperatura",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (temperatureState.sensorAvailable) {
                        Text(
                            text = "Temperatura Actual",
                            fontSize = 18.sp,
                            color = Color(0xFF757575)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${String.format("%.1f", temperatureState.currentTemperature)}°C",
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (temperatureState.isHot) Color(0xFFFF6F00) else Color(0xFF1976D2)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (temperatureState.isHot) "¡Hace mucho calor!" else "Temperatura normal",
                            fontSize = 16.sp,
                            color = if (temperatureState.isHot) Color(0xFFE65100) else Color(0xFF388E3C),
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Sensor no disponible",
                            fontSize = 18.sp,
                            color = Color(0xFF757575)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tu dispositivo no tiene sensor de temperatura ambiente",
                            fontSize = 14.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TemperatureIndicator(temperatureState = temperatureState)
        }

        // Alerta de temperatura alta
        if (showAlert) {
            TemperatureAlert(
                temperatureState = temperatureState,
                onDismiss = { showAlert = false }
            )
        }
    }
}

