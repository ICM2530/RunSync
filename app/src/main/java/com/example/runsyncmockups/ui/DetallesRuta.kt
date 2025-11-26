package com.example.runsyncmockups.ui

import BottomBarView
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.ui.mocks.RutaDetalleMock
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.R
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.MyMarker


@Composable
fun PantallaDetallesRutas(
    navController: NavController,
    vm : LocationViewModel
) {
    val initialTitle: String = "Parque el Virrey"
    val initialDescription: String =
        "Cuenta con circuitos marcados y es muy frecuentado por corredores"
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var points by remember { mutableStateOf("") }
    val context = LocalContext.current




    Scaffold(
        bottomBar = { BottomBarView(navController) },


    ) { padding ->
        Card(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre del lugar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                    singleLine = true
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.virrey),
                        contentDescription = "Imagen Destino",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 90.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    maxLines = 5
                )


                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Puntos de interés:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = points,
                        onValueChange = { points = it },
                        placeholder = { Text("Agrega puntos cercanos, referencias, etc.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        maxLines = 8
                    )
                }


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Button(
                        onClick = {
                            val latLng = findLocation(initialTitle, context) ?: return@Button
                            val marker = MyMarker(latLng, initialTitle)
                            vm.replaceWith(marker)
                            navController.navigate(AppScreens.Rutas.name) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5722),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Iniciar ruta") }
                }
            }
        }
    }
}


@Preview
@Composable
fun PantallaDetallesRutas() {
    PantallaDetallesRutas(navController = rememberNavController(), vm = LocationViewModel())
}







