package com.example.runsyncmockups.ui.mocks

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.runsyncmockups.ui.model.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.ui.viewmodel.PerfilViewModel

@Composable
fun PantallaPerfil(modifier: Modifier = Modifier, userViewModel: UserViewModel = viewModel() ) {

    val userState = userViewModel.user.collectAsState()
    val user = userState.value

    LaunchedEffect(Unit) {
        userViewModel.loadUserData()
    }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            userViewModel.uploadProfileImage(
                imageUri = it,
                onComplete = { url ->

                    userViewModel.loadUserData()
                },

            )
        }
    }


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
            // Foto de perfil
            if (user.profileImage.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(user.profileImage),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { launcher.launch("image/*") }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text( text = "${user.name}", fontSize = 14.sp)
                }
                // Estadísticas (3 columnas iguales)
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
}

@Composable
fun PerfilStat(titulo: String, valor: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = titulo, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaPerfil() {
    PantallaPerfil()
}
