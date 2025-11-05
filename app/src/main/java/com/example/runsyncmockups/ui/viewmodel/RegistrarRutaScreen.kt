/*
package com.example.runsyncmockups.ui.viewmodel

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistrarEntrega() {
    val context = LocalContext.current
    val vm: RegisterDeliveryViewModel = viewModel()

    var lugarRecoleccion by remember { mutableStateOf("") }
    var pesoRegistrado by remember { mutableStateOf("") }
    var photoCapturada by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoCapturada = true
            Toast.makeText(context, "📸 Foto capturada correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    val state by vm.state.collectAsState()

    // Feedback
    LaunchedEffect(state.successId, state.error) {
        state.successId?.let {
            Toast.makeText(context, "¡Registro exitoso! ♻", Toast.LENGTH_LONG).show()
            lugarRecoleccion = ""; pesoRegistrado = ""; photoCapturada = false
            vm.clear()
        }
        state.error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Imagen decorativa superior
        Image(
            painter = painterResource(id = R.drawable.decor_top),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillBounds
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Título
            Text(
                text = "Registro entrega",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = DarkGreen,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Campo: Lugar de recolección
            Text(
                text = "Lugar de recolección",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = lugarRecoleccion,
                onValueChange = { lugarRecoleccion = it },
                label = { Text("Lugar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = DarkGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo: Peso registrado
            Text(
                text = "Peso registrado (kg)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = pesoRegistrado,
                onValueChange = { pesoRegistrado = it },
                label = { Text("Peso") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = DarkGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Escanear QR
            Button(
                onClick = {
                    lugarRecoleccion = "Punto escaneado #${(1..100).random()}"
                    Toast.makeText(context, "QR escaneado correctamente ✅", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGreen,
                    contentColor = DarkGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📷 Escanear QR", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Tomar Foto
            Button(
                onClick = {
                    val photoFile = createImageFile(context)
                    val photoUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile
                    )
                    cameraLauncher.launch(photoUri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGreen,
                    contentColor = DarkGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📸 Tomar Foto", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Registrar
            Button(
                onClick = {
                    // Validamos solo lugar y peso (se ignoran foto/QR)
                    vm.register(lugarRecoleccion, pesoRegistrado)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGreen,
                    contentColor = DarkGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (state.loading) "Registrando..." else "✅ Registrar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

*/