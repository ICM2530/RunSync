package com.example.runsyncmockups.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.model.LocationViewModel
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
import com.example.runsyncmockups.ui.viewmodel.PantallaRegistrarRutas


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

    RegisrarRuta

}



@Composable
fun Navigation(viewModel: LocationViewModel){
    val navController = rememberNavController()
    NavHost(navController =navController, startDestination = AppScreens.InicioSesion.name)  {

        composable(route = AppScreens.Registro.name){
            PantallaRegistro(navController)
        }

        composable(route = AppScreens.InicioSesion.name){
            PantallaInicioSesion(navController)
        }

        composable(route = AppScreens.Home.name) {
            PantallaHome(navController)
        }
        composable(route = AppScreens.Rutas.name) {
            LocationScreen(viewModel, navController)
        }
        composable(route = AppScreens.DetalleRutas.name){
            PantallaDetallesRutas(navController, viewModel)
        }
        composable(route = AppScreens.Activities.name){
            ActivitiesScreen(navController)
        }
        composable(route = AppScreens.Events.name){
            EventsScreen(navController)
        }
        composable(route = AppScreens.Chat.name){
            ChatScreen(navController)
        }
        composable(route = AppScreens.Profile.name){
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

        composable(route = AppScreens.RegisrarRuta.name) {
            PantallaRegistrarRutas(navController)
        }

    }
    }

