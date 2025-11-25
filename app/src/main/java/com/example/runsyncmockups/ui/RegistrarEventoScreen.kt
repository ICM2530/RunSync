package com.example.runsyncmockups.ui

import BottomBarView
import android.app.DatePickerDialog
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runsyncmockups.model.EventViewModel
import java.util.Calendar

@Composable
fun PantallaRegistrarEvento(navController: NavController) {

    val ctx = LocalContext.current
    val vm: EventViewModel = viewModel()
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    var nombreEvento by remember { mutableStateOf("") }
    var descripcionEvento by remember { mutableStateOf("") }
    var destinoEvento by remember { mutableStateOf("") }
    var horaEvento by remember { mutableStateOf("") }
    var distanciaEvento by remember { mutableStateOf("") }
    var fechaEvento by remember { mutableStateOf("") }



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
                text = "Registro Evento",
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
                text = "Nombre del evento",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = nombreEvento,
                onValueChange = { nombreEvento = it },
                label = { Text("Nombre del evento") },
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

            Spacer(modifier = Modifier.height(20.dp))

            // Campo: Nombre del destino
            Text(
                text = "Nombre del destino",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722)
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextField(
                value = destinoEvento,
                onValueChange = { destinoEvento = it },
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
                text = "Descripción del evento",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722)
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextField(
                value = descripcionEvento,
                onValueChange = { descripcionEvento = it },
                label = { Text("Descripción del evento") },
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



            Spacer(modifier = Modifier.height(4.dp))



            Text(
                text = "Fecha del evento",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722)
                )
            )

            OutlinedTextField(
                value = fechaEvento,
                onValueChange = {fechaEvento = it},
                label = { Text("Fecha del evento") },
                readOnly = true,
                leadingIcon = { Icon(Icons.Outlined.Event, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            ctx,
                            { _, y, m, d -> fechaEvento = "%02d/%02d/%04d".format(d, m + 1, y) },
                            cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
                        ).show()
                    }) { Icon(Icons.Outlined.Event, contentDescription = "Elegir fecha") }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF5722),
                    unfocusedBorderColor = Color.White,
                    cursorColor = Color(0xFFFF5722),
                    focusedLabelColor = Color(0xFFFF5722),
                    focusedLeadingIconColor = Color(0xFFFF5722),
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))


            // Botón Registrar
            Button(
                onClick = {
                    vm.save(nombreEvento, descripcionEvento, destinoEvento, fechaEvento)
                    navController.navigate("home")
                    if (nombreEvento.isEmpty() || descripcionEvento.isEmpty() || destinoEvento.isEmpty() ||  fechaEvento.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Por favor, complete todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
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
                Text(
                    if (state.saving) "Registrando..." else "Registrar",
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}