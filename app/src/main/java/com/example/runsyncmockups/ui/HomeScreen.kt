package com.example.runsyncmockups.ui

import BottomBarView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.R
import com.example.runsyncmockups.ui.components.DashboardCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHome(navController: NavController){

    Scaffold(
        bottomBar = {BottomBarView(navController)},
        modifier = Modifier.background(brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFB3E5FC), Color.White)
        ))
    )
    { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDFDFD))
                .padding(16.dp).padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Hola, sebas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )


            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.running),
                    contentDescription = "Corredor",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }


            DashboardCard(
                title = "Esta semana",
                content = {
                    Text("Distancia: 12 km")
                    Text("Tiempo: 1h 30min")
                    Text("Calorías: 180 kcal")
                },
                buttonText = "Ver detalle",
                onClick = { navController.navigate(AppScreens.Estadisticas.name)}
            )


            DashboardCard(
                title = "Ruta sugerida: Zona T",
                content = {
                    Text("Nivel fácil • 45min • 3.5 km")
                },
                buttonText = "Ver ruta",
                onClick = { /*navController.navigate()*/ }
            )


            DashboardCard(
                title = "Logros",
                content = {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ){
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Logro de estrella",
                        tint = Color(0xFFFFD700),

                    )
                    Icon(
                        imageVector = Icons.Default.AccessAlarm,
                        contentDescription = "Logro de tiempo",
                        tint = Color(0xFF4CAF8C)
                    )

                        Icon(
                            imageVector = Icons.Default.Anchor,
                            contentDescription = "Logro de tiempo",
                            tint = Color(0xFF000000)
                        )

                    }
                },
                buttonText = "Ver todos mis logros",
                onClick = { /*navController.navigate()*/ }
            )

            DashboardCard(
                title = "10K Bogotá Night Run",
                content = {
                    Text("Fecha: 25 Sept • Cupos disponibles")
                },
                buttonText = "Inscribirme",
                onClick = { /*navController.navigate()*/ }
            )
        }
}}

@Preview
@Composable
fun PreviewPantallaHome(){
    val navController = rememberNavController()

    PantallaHome(navController)
}
