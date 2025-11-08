package com.example.runsyncmockups.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.model.EventListViewModel
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.Route
import com.example.runsyncmockups.model.RouteListViewModel
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
import com.example.runsyncmockups.ui.PantallaListaRutas
import com.example.runsyncmockups.ui.QRGeneratorScreen
import com.example.runsyncmockups.ui.ScannerScreen
import com.example.runsyncmockups.ui.viewmodel.PantallaListaEvents
import com.example.runsyncmockups.ui.viewmodel.PantallaRegistrarEvento
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

    RegisrarRuta,
    ListaRutas,
    RegistrarEvento,
    ListaEventos

}



@Composable
fun Navigation(RoutViewModel: RouteListViewModel,LocviewModel: LocationViewModel, EventViewModel: EventListViewModel){
    val navController = rememberNavController()
    val rut = Route()
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
            LocationScreen(LocviewModel, navController)
        }
        composable(route = AppScreens.DetalleRutas.name){
            PantallaDetallesRutas(navController, LocviewModel)
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

        composable(route = AppScreens.ListaRutas.name) {
            PantallaListaRutas(RoutViewModel,  navController, LocviewModel)
        }
        composable(route = AppScreens.RegistrarEvento.name) {
            PantallaRegistrarEvento(  navController)
        }
        composable(route = AppScreens.ListaEventos.name) {
            PantallaListaEvents(EventViewModel, navController)
        }


    }
    }

