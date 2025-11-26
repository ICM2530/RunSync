package com.example.runsyncmockups.ui

import BottomBarView
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.MyMarker
import com.example.runsyncmockups.model.Route
import com.example.runsyncmockups.model.RouteListViewModel
import com.example.runsyncmockups.ui.components.DashboardCard

@Composable
fun PantallaListaRutas(
    vm: RouteListViewModel,
    navController: NavController,
    locationVm: LocationViewModel
) {
    val state by vm.state.collectAsState()

    Scaffold(
        bottomBar = { BottomBarView(navController) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    Text(
                        "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.items.isEmpty() -> {
                    Text(
                        "No hay rutas disponibles.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items, key = { it.id }) { route ->
                            ListaRutas(route, navController, locationVm)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListaRutas(
    rut: Route,
    navController: NavController,
    locationVm: LocationViewModel
) {
    val context = LocalContext.current

    DashboardCard(
        title = rut.routeName,
        content = {
            Text("Descripción: ${rut.description}")
            Text("Puntos de interés: ${rut.poiNotes}")
            Text("Destino: ${rut.destTitle}")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón INICIAR RUTA
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val latLng = findLocation(rut.destTitle, context) ?: return@Button
                        val marker = MyMarker(latLng, rut.destTitle)
                        locationVm.replaceWith(marker)
                        navController.navigate(AppScreens.Rutas.name)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Iniciar ruta")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate("detalleRuta/${rut.id}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ver Detalle")
                }
            }
        },
    )
}


@Composable
fun PantallaDetalleRuta(
    navController: NavController,
    ruta: Route,
    locationVm: LocationViewModel
) {
    val context = LocalContext.current

    Scaffold(
        bottomBar = { BottomBarView(navController) },
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFB3E5FC), Color.White)
            )
        )
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDFDFD))
                .padding(16.dp)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = ruta.routeName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            DashboardCard(
                title = "Información de la ruta",
                content = {
                    Text("Destino: ${ruta.destTitle}")
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Descripción:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = ruta.description.ifBlank { "Sin descripción" },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Puntos de interés:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = ruta.poiNotes.ifBlank { "No se han agregado puntos de interés." },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    // Si quieres algo extra:
                    Text(
                        text = "Detalles adicionales:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "Dificultad: Fácil",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Iniciar ruta
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val latLng = findLocation(ruta.destTitle, context) ?: return@Button
                                val marker = MyMarker(latLng, ruta.destTitle)
                                locationVm.replaceWith(marker)
                                navController.navigate(AppScreens.Rutas.name)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5722),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Iniciar ruta")
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5722),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Volver")
                        }
                    }
                },
            )
        }
    }
}


