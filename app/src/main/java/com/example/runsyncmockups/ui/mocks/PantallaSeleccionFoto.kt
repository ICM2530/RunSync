package com.example.runsyncmockups.ui.mocks

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.ui.theme.black
import com.example.runsyncmockups.ui.viewmodel.PerfilViewModel
import java.io.File

@Composable
fun SeleccionarFotoScreen(
    viewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var mostrarOpciones by remember { mutableStateOf(true) }
    var mostrarDialogo by remember { mutableStateOf(true) }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            mostrarOpciones = false
        }
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            mostrarOpciones = false
        }
    }

    val permisosCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File.createTempFile("profile_photo", ".jpg", context.cacheDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            imageUri = uri
            camaraLauncher.launch(uri)
        }
    }

    if (!mostrarDialogo) return

    if (mostrarOpciones) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cambiar foto de perfil") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            permisosCamaraLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = black)

                    ) {
                        Text("Tomar una foto")
                    }
                    Button(
                        onClick = {
                            // No necesita permisos en Android 13+
                            galeriaLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = black)

                    ) {
                        Text("Seleccionar de galería")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Vista previa") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    imageUri?.let { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Vista previa",
                            modifier = Modifier
                                .size(200.dp)
                                .padding(16.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateProfileImage(imageUri)
                    mostrarDialogo = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    imageUri = null
                    mostrarOpciones = true
                }) {
                    Text("Volver")
                }
            }
        )
    }
}