package com.example.runsyncmockups.ui

import BottomBarView
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.runsyncmockups.model.Event
import com.example.runsyncmockups.model.EventListViewModel
import com.example.runsyncmockups.model.EventRepository
import com.example.runsyncmockups.ui.components.DashboardCard
import kotlinx.coroutines.launch

@Composable
fun PantallaListaEvents(vm: EventListViewModel, navController: NavController, repo: EventRepository){

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
                            ListaEventos(route, navController, repo)
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
    repo: EventRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isRegistered by repo.listenIsRegistered(event.id).collectAsState(initial = false)
    var loading by remember { mutableStateOf(false) }

    DashboardCard(
        title = event.title,
        content = {
            Text("Descripción: ${event.description}")
            Text("Destino: ${event.place}")
            Text("Fecha: ${event.date}")
        },
        buttonText = when {
            loading -> "Procesando..."
            isRegistered -> "Cancelar inscripción"
            else -> "Inscribirme"
        },
        onClick ={
            if (loading) return@DashboardCard
            loading = true
            scope.launch {
                val res = if (isRegistered) repo.leaveEvent(event.id) else repo.joinEvent(event.id)
                loading = false
                res.fold(
                    onSuccess = {
                        val msg = if (isRegistered) "Inscrito al evento" else "Inscripción cancelada"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { e ->
                        Toast.makeText(context, e.message ?: "Error al inscribirse", Toast.LENGTH_SHORT).show()
                    }
                )
        }
        }
    )
}