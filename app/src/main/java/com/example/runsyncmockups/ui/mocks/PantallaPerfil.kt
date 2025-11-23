package com.example.runsyncmockups.ui.mocks

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.model.PerfilViewModel

@Composable
fun PantallaPerfil(
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = viewModel(),
) {
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto de perfil clickeable
            if (profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { mostrarDialogo = true },
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { mostrarDialogo = true }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(horizontalArrangement = Arrangement.Start) {
                    Text(text = "Usuario123", fontSize = 14.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PerfilStat(titulo = "Carreras", valor = "10")
                    PerfilStat(titulo = "Seguidores", valor = "250")
                    PerfilStat(titulo = "Siguiendo", valor = "180")
                }
            }
        }
    }

    if (mostrarDialogo) {
        SeleccionarFotoScreen(viewModel = viewModel)
        LaunchedEffect(profileImageUri) {
            if (profileImageUri != null) {
                mostrarDialogo = false
            }
        }
    }
}

@Composable
fun PerfilStat(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = titulo, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaPerfil() {
    PantallaPerfil()
}
