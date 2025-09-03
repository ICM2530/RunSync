package com.example.runsyncmockups.ui.mocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.runsyncmockups.R

@Composable
fun PantallaExplorarMock(modifier: Modifier = Modifier,navController: NavController) {
    val chipBg = MaterialTheme.colorScheme.surfaceVariant
    val chipStyle = MaterialTheme.typography.labelMedium

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado
        Text(
            text = "Explorar rutas",
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(Modifier.height(12.dp))

        // Caja de búsqueda
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "  Chapinero",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(12.dp))

        // Filtros
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Distance",
                style = chipStyle,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(chipBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
            Text(
                text = "Zone",
                style = chipStyle,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(chipBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
            Text(
                text = "Tipo",
                style = chipStyle,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(chipBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Ruta destacada
        Text(
            text = "Ruta destacada de la semana",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "Parque Simón Bolívar",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))

        // Listado de rutas
        Text(
            text = "Listado de rutas",
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(Modifier.height(12.dp))

        // Placeholder de mapa (aquí va tu Image)

            Image(
                painter = painterResource(id = R.drawable.mapabirrey),
                contentDescription = "mapa de virrey",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
                    .height(250.dp)


            )


        Spacer(Modifier.height(12.dp))

        // Detalles ruta
        Text("Nombre: Parque el Virrey", style = MaterialTheme.typography.bodyMedium)
        Text("Distancia: Nivel fácil — 5 km", style = MaterialTheme.typography.bodyMedium)
        Text("Tiempo estimado: 40 min", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {navController.navigate("DetalleRutas")},
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)

        ) {
            Text(
                text = "Ver detalle",
                style = chipStyle,

            )
        }

    }
}
