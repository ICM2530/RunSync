package com.example.runsyncmockups.ui

import BottomBarView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.ui.components.DashboardCard



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
            title= { Text("Mis actividades",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp

            )}

            )
        },
        bottomBar = {BottomBarView(navController)}
    )
    { paddingValues ->

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ){
            DashboardCard(

                title = "Resumen general",
                content = {
                    Text(text = "Distancia total recorrida: 120 km")
                    Text(text = "Tiempo total entrenado: 15 h 40 min")
                    Text(text = "Elevación acumulada: 800 m")
                }

            )

                Text(
                    text = "Rutas pasadas",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)
            DashboardCard(
                title = "Nombre de la ruta: Parque Central",
                content = {
                    Text(text = "Fecha del entrenamiento: 12/06/2025")
                    Text(text = "Distancia recorrida: 10 km")
                    Text(text = "Tiempo total: 50 min")
                    Text(text = "Ritmo promedio: 5 min/km")
                },
                backgroundColor = Color(0xFFC2C0C0)
            )
            DashboardCard(
                title = "Nombre de la ruta: Ruta del Río",
                content = {
                    Text(text = "Fecha del entrenamiento: 10/06/2025")
                    Text(text = "Distancia recorrida: 8 km")
                    Text(text = "Tiempo total:  Forty-five min")
                    Text(text = "Ritmo promedio: 5.5 min/km")
                },
                backgroundColor = Color(0xFFC2C0C0)
            )

        }


    }
}

@Preview
@Composable
fun PreviewActivitiesScreen(){
    val navController = rememberNavController()

    ActivitiesScreen(navController)
}