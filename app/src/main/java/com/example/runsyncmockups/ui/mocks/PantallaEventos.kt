package com.example.runsyncmockups.ui.mocks

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EventosScreen(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Eventos",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Text(
            text = "Próximos eventos",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        EventoCard(
            titulo = "Carrera atlética Compensar",
            fecha = "Fecha: 27 de julio 2025",
            lugar = "Lugar: Parque Simón Bolívar"
        )
        EventoCard(
            titulo = "Carrera Allianz 15k",
            fecha = "Fecha: 19 de octubre 2025",
            lugar = "Lugar: Inicia en Predio Country Comodoro y finalizará en el Parque Simón Bolívar"
        )
    }
}

@Composable
fun EventoCard(titulo: String, fecha: String, lugar: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(text = fecha)
        Text(text = lugar)

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.width(400.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF948CAB)
                ),
                shape = RectangleShape
            ) {
                Text(text = "Me interesa", fontSize = 14.sp)
            }
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF948CAB)
                ),
                shape = RectangleShape
            ) {
                Text(text = "Unete al chat", fontSize = 14.sp)
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun EventosScreenPreview() {
    EventosScreen(navController = NavController(LocalContext.current))
}