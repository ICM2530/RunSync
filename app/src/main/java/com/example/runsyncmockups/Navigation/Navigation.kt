package com.example.runsyncmockups.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.TemperatureViewModel
import com.example.runsyncmockups.ui.PantallaDetallesRutas
import com.example.runsyncmockups.ui.PantallaHome
import com.example.runsyncmockups.ui.PantallaInicioSesion
import com.example.runsyncmockups.ui.PantallaRegistro
import com.example.runsyncmockups.ui.PantallaRutas
import com.example.runsyncmockups.ui.ActivitiesScreen
import com.example.runsyncmockups.ui.ChatScreen
import com.example.runsyncmockups.ui.EventsScreen
import com.example.runsyncmockups.ui.LocationScreen
import com.example.runsyncmockups.ui.ProfileScreen
import com.example.runsyncmockups.ui.SpeechText
import com.example.runsyncmockups.ui.EstadisticaScreen
import com.example.runsyncmockups.ui.QRGeneratorScreen
import com.example.runsyncmockups.ui.ScannerScreen
import com.example.runsyncmockups.ui.components.TemperatureAlert


enum class AppScreens{
    Registro,
    Verificacion,
    InicioSesion,
    Home,
    Rutas,
    DetalleRutas,
    Activities,
    Events,
    Chat,
    Profile,
    Voz,
    Estadistica,
    Scanner,
    GeneradorQR,

}



@Composable
fun Navigation(viewModel: LocationViewModel){
    val navController = rememberNavController()
    val temperatureViewModel: TemperatureViewModel = viewModel()
    val context = LocalContext.current
    val temperatureState by temperatureViewModel.temperatureState.collectAsState()
    var showAlert by remember { mutableStateOf(false) }
    var isUserLoggedIn by remember { mutableStateOf(false) }


    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)

    LaunchedEffect(currentRoute.value) {
        val route = currentRoute.value?.destination?.route

        val wasLoggedIn = isUserLoggedIn
        isUserLoggedIn = route != AppScreens.InicioSesion.name && route != AppScreens.Registro.name

        // Iniciar sensor solo cuando el usuario acaba de iniciar sesión
        if (!wasLoggedIn && isUserLoggedIn) {
            temperatureViewModel.initializeSensor(context)
            temperatureViewModel.startListening()
        }

        // Detener sensor si el usuario cierra sesión
        if (wasLoggedIn && !isUserLoggedIn) {
            temperatureViewModel.stopListening()
        }
    }

    // Detener el sensor cuando se destruye la navegación
    DisposableEffect(Unit) {
        onDispose {
            temperatureViewModel.stopListening()
        }
    }

    // Mostrar alerta automáticamente solo si el usuario está logueado Y ya hay lectura del sensor
    LaunchedEffect(temperatureState.isHot, temperatureState.isCold, isUserLoggedIn, temperatureState.hasReceivedFirstReading) {
        if (isUserLoggedIn && temperatureState.hasReceivedFirstReading && (temperatureState.isHot || temperatureState.isCold)) {
            showAlert = true
        }
    }

    Box {
        NavHost(navController = navController, startDestination = AppScreens.InicioSesion.name) {

            composable(route = AppScreens.Registro.name) {
                PantallaRegistro(navController)
            }

            composable(route = AppScreens.InicioSesion.name) {
                PantallaInicioSesion(navController)
            }

            composable(route = AppScreens.Home.name) {
                PantallaHome(navController)
            }
            composable(route = AppScreens.Rutas.name) {
                LocationScreen(viewModel, navController)
            }
            composable(route = AppScreens.DetalleRutas.name) {
                PantallaDetallesRutas(navController, viewModel)
            }
            composable(route = AppScreens.Activities.name) {
                ActivitiesScreen(navController)
            }
            composable(route = AppScreens.Events.name) {
                EventsScreen(navController)
            }
            composable(route = AppScreens.Chat.name) {
                ChatScreen(navController)
            }
            composable(route = AppScreens.Profile.name) {
                ProfileScreen(navController)
            }
            composable(route = AppScreens.Voz.name) {
                SpeechText(navController)
            }
            composable(route = AppScreens.Estadistica.name) {
                EstadisticaScreen(navController)
            }
            composable(route = AppScreens.Scanner.name) {
                ScannerScreen(navController)
            }
            composable(route = AppScreens.GeneradorQR.name) {
                QRGeneratorScreen(navController)
            }

        }

        if (showAlert && isUserLoggedIn) {
            TemperatureAlert(
                temperatureState = temperatureState,
                onDismiss = { showAlert = false }
            )
        }
    }
}

