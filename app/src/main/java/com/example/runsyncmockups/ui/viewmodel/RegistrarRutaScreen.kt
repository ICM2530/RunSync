package com.example.runsyncmockups.ui.viewmodel

import BottomBarView
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.RouteRepository
import com.example.runsyncmockups.model.RouteViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun PantallaRegistrarRutas(navController: NavController) {


    val vm: RouteViewModel = viewModel()
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    var nombreRuta by remember { mutableStateOf("") }
    var descripcionRuta by remember { mutableStateOf("") }
    var destinoRuta by remember { mutableStateOf("") }
    var poiRutas by remember {mutableStateOf("")}


    Scaffold(
        bottomBar = { BottomBarView(navController) },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Título
            Text(
                text = "Registro Ruta",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color(0xFFFF5722),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Campo: nombre de la ruta
            Text(
                text = "Nombre de la ruta",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = nombreRuta,
                onValueChange = { nombreRuta = it },
                label = { Text("Nombre de la ruta") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFFFF5722)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo: Nombre del destino
            Text(
                text = "Nombre del destino",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = destinoRuta,
                onValueChange = { destinoRuta = it },
                label = { Text("Nombre del destino") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFFFF5722)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo: Descripcion de la ruta
            Text(
                text = "Descripción de la ruta",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = descripcionRuta,
                onValueChange = { descripcionRuta = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFFFF5722)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo: Puntos de interés de la ruta
            Text(
                text = "Puntos de interés cercanos",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = poiRutas,
                onValueChange = { poiRutas = it },
                label = { Text("Puntos de interés") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFFFF5722)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrar
            Button(
                onClick = {
                    vm.save(nombreRuta, descripcionRuta, poiRutas, destinoRuta)
                    if(nombreRuta.isEmpty() || descripcionRuta.isEmpty() || poiRutas.isEmpty() || destinoRuta.isNotEmpty()){
                        Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color(0xFF000000)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (state.saving) "Registrando..." else "✅ Registrar", fontWeight = FontWeight.Bold)
            }

        }
    }
}


@Preview
@Composable
fun RegistrarRutaPreview(){
    val navController = NavController(LocalContext.current)
    PantallaRegistrarRutas(navController)
}