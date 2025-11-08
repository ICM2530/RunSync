package com.example.runsyncmockups.ui.viewmodel

import BottomBarView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.model.Event
import com.example.runsyncmockups.model.EventListViewModel
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.MyMarker
import com.example.runsyncmockups.model.Route
import com.example.runsyncmockups.ui.ListaRutas
import com.example.runsyncmockups.ui.components.DashboardCard
import com.example.runsyncmockups.ui.findLocation

@Composable
fun PantallaListaEvents(vm: EventListViewModel, navController: NavController){

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
                    Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center))
                }
                state.items.isEmpty() -> {
                    Text("No hay solicitudes pendientes.",
                        modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items, key = { it.id }) { route ->
                            ListaEventos(route, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListaEventos(
    event: Event,
    navController: NavController,
) {
    val context = LocalContext.current

    DashboardCard(
        title = event.title,
        content = {
            Text("Descripción: ${event.description}")
            Text("Puntos de interés: ${event.place}")
            Text("Destino: ${event.date}")
        },
        buttonText = "Iniciar ruta",
        /*onClick = {
            val latLng = findLocation(rut.destTitle, context) ?: return@DashboardCard
            val marker = MyMarker(latLng, rut.destTitle)
            locationVm.replaceWith(marker)
            navController.navigate(AppScreens.Rutas.name)
        },

         */
    )
}