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
import com.example.runsyncmockups.ui.viewmodel.PerfilViewModel
import com.example.runsyncmockups.model.FriendsViewModel
import com.example.runsyncmockups.ui.AgregarAmigoDialog
import com.example.runsyncmockups.ui.model.UserViewModel

@Composable
fun PantallaPerfil(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(),
    viewModel: PerfilViewModel = viewModel(),
    friendsViewModel: FriendsViewModel = viewModel(),
    onNavigateToFriendsList: () -> Unit = {}
) {
    val user by userViewModel.user.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val friendsState by friendsViewModel.friendsState.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarAgregarAmigo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.loadUserData()
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
                    Text(text = "${user.name}", fontSize = 14.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PerfilStat(titulo = "Carreras", valor = "10")
                    PerfilStat(
                        titulo = "Amigos",
                        valor = friendsState.friends.size.toString(),
                        onClick = { onNavigateToFriendsList() }
                    )
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

    if (mostrarAgregarAmigo) {
        AgregarAmigoDialog(
            onDismiss = { mostrarAgregarAmigo = false },
            friendsViewModel = friendsViewModel
        )
    }
}

@Composable
fun PerfilStat(
    titulo: String,
    valor: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) {
            Modifier.clickable { onClick() }
        } else {
            Modifier
        }
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