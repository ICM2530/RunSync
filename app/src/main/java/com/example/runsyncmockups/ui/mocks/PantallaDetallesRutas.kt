package com.example.runsyncmockups.ui.mocks

import androidx.compose.foundation.Image
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Parque el virrey", style = MaterialTheme.typography.titleSmall)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.mapabirrey),
                contentDescription = "Mapa / Imagen de la ruta",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)

        Text(
            "Cuenta con circuitos marcados y es muy frecuentado por corredores",
            style = MaterialTheme.typography.bodyMedium
        )

        Text("Puntos de interés:", style = MaterialTheme.typography.bodyMedium)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("•", style = MaterialTheme.typography.bodyMedium)
            Text("•", style = MaterialTheme.typography.bodyMedium)
            Text("•", style = MaterialTheme.typography.bodyMedium)
            Text("•", style = MaterialTheme.typography.bodyMedium)
        }

        FilledTonalButton(
            onClick = { },
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text("Iniciar ruta", style = MaterialTheme.typography.labelMedium)
        }
    }
}
