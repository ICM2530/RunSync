package com.example.runsyncmockups.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.PantallaInicioSesion
import com.example.runsyncmockups.PantallaRegistro
import com.example.runsyncmockups.PantallaVerificacion


enum class AppScreens{
    Registro,
    Verificacion,
    InicioSesion
}



@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController =navController, startDestination = AppScreens.Registro.name)  {
        composable(route = AppScreens.Registro.name){
            PantallaRegistro(navController)
        }
        composable(route = "${AppScreens.Verificacion.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            PantallaVerificacion(navController, name)
        }
        composable(route = "${AppScreens.InicioSesion.name}/{name}"){ backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            PantallaInicioSesion(navController, name)
        }
    }
}