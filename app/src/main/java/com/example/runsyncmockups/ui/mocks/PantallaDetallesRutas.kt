// RutaDetalleMock.kt
package com.example.runsyncmockups.ui.mocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.runsyncmockups.R

@Composable
fun RutaDetalleMock(
    modifier: Modifier = Modifier,
    // Aquí pegas tu imagen. Mantengo las medidas en el contenedor.
    hero: @Composable () -> Unit = { HeroPlaceholder() }
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Parque el virrey", style = MaterialTheme.typography.titleSmall)

        // Contenedor de la imagen (medidas fijas)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            hero()
        }

        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)

        Text(
            "Cuenta con circuitos marcados y es muy frecuentado por corredores",
            style = MaterialTheme.typography.bodyMedium
        )

        Text("Puntos de interés:", style = MaterialTheme.typography.bodyMedium)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Bullet()
            Bullet()
            Bullet()
            Bullet()
        }

        FilledTonalButton(
            onClick = { /* TODO: iniciar ruta */ },
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text("Iniciar ruta", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun Bullet() {
    // Punto de lista vacío, solo el marcador
    Text("•", style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun HeroPlaceholder() {
    // Placeholder neutro (mantiene las medidas del contenedor)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.mapabirrey),
            contentDescription = "mapa Ruta",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
                .height(250.dp)
        )

    }
}
