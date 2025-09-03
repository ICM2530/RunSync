package com.example.runsyncmockups.ui.mocks

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PantallaActividad(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título principal
        Text(
            text = "Mis actividades",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        // Resumen general
        Text(
            text = "Resumen general",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Text(text = "Distancia total recorrida: 120 km")
        Text(text = "Tiempo total entrenado: 15 h 40 min")
        Text(text = "Elevación acumulada: 800 m")
        // Rutas pasadas
        Text(
            text = "Rutas pasadas",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        // Card de ejemplo 1
        RutaCard(
            nombreRuta = "Nombre de la ruta:",
            fecha = "Fecha del entrenamiento:",
            distancia = "Distancia recorrida:",
            tiempo = "Tiempo total:",
            ritmo = "Ritmo promedio:"
        )
        // Card de ejemplo 2
        RutaCard(
            nombreRuta = "Nombre de la ruta:",
            fecha = "Fecha del entrenamiento:",
            distancia = "Distancia recorrida:",
            tiempo = "Tiempo total:",
            ritmo = "Ritmo promedio:"
        )
        RutaCard(
            nombreRuta = "Nombre de la ruta:",
            fecha = "Fecha del entrenamiento:",
            distancia = "Distancia recorrida:",
            tiempo = "Tiempo total:",
            ritmo = "Ritmo promedio:"
        )
        RutaCard(
            nombreRuta = "Nombre de la ruta:",
            fecha = "Fecha del entrenamiento:",
            distancia = "Distancia recorrida:",
            tiempo = "Tiempo total:",
            ritmo = "Ritmo promedio:"
        )
    }
}

@Composable
fun RutaCard(
    nombreRuta: String,
    fecha: String,
    distancia: String,
    tiempo: String,
    ritmo: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8E8585) // gris como en la imagen
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = nombreRuta, color = Color.White)
            Text(text = fecha, color = Color.White)
            Text(text = distancia, color = Color.White)
            Text(text = tiempo, color = Color.White)
            Text(text = ritmo, color = Color.White)
        }
    }


}

@Preview(showBackground = true)
@Composable
fun PantallaActividadPreview() {
    PantallaActividad()
}