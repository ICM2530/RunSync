package com.example.runsyncmockups.ui.mocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.runsyncmockups.R



@Composable
fun PantallaHomeMock(modifier: Modifier = Modifier) {
    val chipBg = MaterialTheme.colorScheme.surfaceVariant
    val chipStyle = MaterialTheme.typography.labelMedium

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado
        Text(
            text = "Hola, nombre",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Image(
            painter = painterResource(id = R.drawable.running),
            contentDescription = "Imagen de running",
            modifier = Modifier.size(200.dp)
        )
        Spacer(Modifier.height(16.dp))

        // Sección: Esta semana
        Text(
            text = "Esta semana",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text("Distancia: 12k", style = MaterialTheme.typography.bodyMedium)
        Text("Tiempo: 1h 30min", style = MaterialTheme.typography.bodyMedium)
        Text("Calorías: 180 kcal", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Ver detalle",
            style = chipStyle,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(chipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Sección: Ruta sugerida
        Text(
            text = "Ruta sugerida:  Zona T",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Nivel fácil • 45min • 3.5 km",
            style = chipStyle,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(chipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Iniciar ruta",
            style = chipStyle,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(chipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Sección: Logros
        Text(
            text = "Has desbloqueado: Primera ruta turística",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Ver todos mis logros",
            style = chipStyle,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(chipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Sección: Evento
        Text(
            text = "10K Bogotá Night Run",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Fecha: 25 Sept • Cupos disponibles",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Inscribirme",
            style = chipStyle,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(chipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
