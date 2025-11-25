package com.example.runsyncmockups.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.runsyncmockups.model.ChallengeViewModel
import com.example.runsyncmockups.model.EventListViewModel
import com.example.runsyncmockups.model.EventRepository
import com.example.runsyncmockups.model.HumidityViewModel
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.MyUsersViewModel
import com.example.runsyncmockups.model.Route
import com.example.runsyncmockups.model.RouteListViewModel
import com.example.runsyncmockups.model.UserAuthViewModel
import com.example.runsyncmockups.model.TemperatureViewModel
import com.example.runsyncmockups.ui.PantallaDetallesRutas
import com.example.runsyncmockups.ui.PantallaHome
import com.example.runsyncmockups.ui.PantallaInicioSesion
import com.example.runsyncmockups.ui.PantallaRegistro
import com.example.runsyncmockups.ui.ActivitiesScreen
import com.example.runsyncmockups.ui.ChallengeListener
import com.example.runsyncmockups.ui.ChatScreen
import com.example.runsyncmockups.ui.PantallaChatIndividual
import com.example.runsyncmockups.ui.EventsScreen
import com.example.runsyncmockups.ui.LocationScreen
import com.example.runsyncmockups.ui.EstadisticaScreen
import com.example.runsyncmockups.ui.components.TemperatureAlert
import com.example.runsyncmockups.ui.components.rainAlert
import com.example.runsyncmockups.ui.PantallaListaAmigos
import com.example.runsyncmockups.ui.OutgoingChallengeListener
import com.example.runsyncmockups.ui.PantallaListaRutas
import com.example.runsyncmockups.ui.PantallaListaEvents
import com.example.runsyncmockups.ui.PantallaRegistrarEvento
import com.example.runsyncmockups.ui.PantallaRegistrarRutas
import com.example.runsyncmockups.ui.SeguimientoScreen
import com.example.runsyncmockups.ui.enabledList
import com.example.runsyncmockups.ui.PantallaPerfilAmigo
import com.example.runsyncmockups.ui.mocks.ProfileScreen
import com.example.runsyncmockups.ui.model.UserViewModel


enum class AppScreens{
    Registro,

    InicioSesion,
    Home,
    Rutas,
    DetalleRutas,
    Activities,
    Events,
    Chat,
    ChatIndividual,
    Profile,
    ListaAmigos,
    PerfilAmigo,
    Estadistica,
    RegisrarRuta,
    ListaRutas,
    RegistrarEvento,
    ListaEventos,
    listaUsers,
}



@Composable
fun Navigation(RoutViewModel: RouteListViewModel, LocviewModel: LocationViewModel, userVm: UserViewModel, EventViewModel: EventListViewModel, repoEvent: EventRepository, locVm: LocationViewModel, authVm: UserAuthViewModel, myUsersVm: MyUsersViewModel, challengeVm: ChallengeViewModel){
    val navController = rememberNavController()
    val rut = Route()
    val temperatureViewModel: TemperatureViewModel = viewModel()
    val humidityViewModel: HumidityViewModel = viewModel()
    val context = LocalContext.current
    val temperatureState by temperatureViewModel.temperatureState.collectAsState()
    var showAlert by remember { mutableStateOf(false) }
    var isUserLoggedIn by remember { mutableStateOf(false) }
    val humidityState by humidityViewModel.humidityState.collectAsState()


    val currentRoute =
        navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)

    LaunchedEffect(currentRoute.value) {
        val route = currentRoute.value?.destination?.route

        val wasLoggedIn = isUserLoggedIn
        isUserLoggedIn = route != AppScreens.InicioSesion.name && route != AppScreens.Registro.name

        // Iniciar sensor solo cuando el usuario acaba de iniciar sesión
        if (!wasLoggedIn && isUserLoggedIn) {
            temperatureViewModel.initializeSensor(context)
            temperatureViewModel.startListening()
            humidityViewModel.initializeSensor(context)
            humidityViewModel.startListening()
        }

        // Detener sensor si el usuario cierra sesión
        if (wasLoggedIn && !isUserLoggedIn) {
            humidityViewModel.stopListening()
            temperatureViewModel.stopListening()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            humidityViewModel.stopListening()
            temperatureViewModel.stopListening()
        }
    }

    // Mostrar alerta automáticamente solo si el usuario está logueado Y ya hay lectura del sensor
    LaunchedEffect(
        temperatureState.isHot,
        temperatureState.isCold,
        isUserLoggedIn,
        temperatureState.hasReceivedFirstReading,
        humidityState.hasReceivedFirstReading
    ) {
        if (isUserLoggedIn && temperatureState.hasReceivedFirstReading && (temperatureState.isHot || temperatureState.isCold) && humidityState.hasReceivedFirstReading) {
            showAlert = true
        }
    }


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
            LocationScreen(LocviewModel, authVm,navController)
        }
        composable(route = AppScreens.DetalleRutas.name){
            PantallaDetallesRutas(navController, LocviewModel)
        }
        composable(route = AppScreens.Activities.name) {
            ActivitiesScreen(navController)
        }

        composable(route = AppScreens.Events.name){
            EventsScreen(navController)
        }
        composable(route = AppScreens.Chat.name){
            ChatScreen(navController)
        }

        composable(
            route = "${AppScreens.ChatIndividual.name}/{friendId}/{friendName}/{friendEmail}",
            arguments = listOf(
                navArgument("friendId") { type = NavType.StringType },
                navArgument("friendName") { type = NavType.StringType },
                navArgument("friendEmail") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
            val friendName = backStackEntry.arguments?.getString("friendName") ?: ""
            val friendEmail = backStackEntry.arguments?.getString("friendEmail") ?: ""

            PantallaChatIndividual(
                navController = navController,
                friendId = friendId,
                friendName = friendName,
                friendEmail = friendEmail
            )
        }

        composable(route = AppScreens.Profile.name){
            ProfileScreen(navController, userVm)
        }
        composable(route = AppScreens.Estadistica.name) {
            EstadisticaScreen(navController)
        }

        composable(route = AppScreens.RegisrarRuta.name) {
            PantallaRegistrarRutas(navController)
        }
        composable(route = AppScreens.ListaRutas.name) {
            PantallaListaRutas(RoutViewModel,  navController, LocviewModel)
        }
        composable(route = AppScreens.RegistrarEvento.name) {
            PantallaRegistrarEvento(  navController)
        composable(route = AppScreens.ListaEventos.name) {
            PantallaListaEvents(EventViewModel, navController, repoEvent)
        }
        composable("seguimiento/{name}/{uid}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            SeguimientoScreen(name, uid, navController, locVm, authVm, myUsersVm)
        }
        composable(AppScreens.listaUsers.name) {
            enabledList(navController, myUsersVm, locVm, challengeVm)
        }
        composable(route = AppScreens.ListaAmigos.name) {
            PantallaListaAmigos(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChat = { friendId, friendName, friendEmail ->
                    navController.navigate(
                        "${AppScreens.ChatIndividual.name}/$friendId/$friendName/$friendEmail"
                    )
                },
                onNavigateToFriendProfile = { friendId ->
                    navController.navigate("${AppScreens.PerfilAmigo.name}/$friendId")
                }
            )
        }
        composable(
            route = "${AppScreens.PerfilAmigo.name}/{friendId}",
            arguments = listOf(
                navArgument("friendId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
            PantallaPerfilAmigo(
                friendId = friendId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
    }

    if (showAlert && isUserLoggedIn) {
        TemperatureAlert(
            temperatureState = temperatureState,
            onDismiss = { showAlert = false }
        )
        rainAlert(
            humidityState = humidityState,
            onDismiss = { showAlert = false }
        )}
    ChallengeListener(navController = navController)
    OutgoingChallengeListener(navController = navController)

        }



