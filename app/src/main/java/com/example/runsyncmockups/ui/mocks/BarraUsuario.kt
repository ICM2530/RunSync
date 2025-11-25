package com.example.runsyncmockups.ui.mocks

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.model.FriendsViewModel
import com.example.runsyncmockups.ui.model.UserViewModel


private val OrangePrimary = Color(0xFFFF5722)
private val OrangeSecondary = Color(0xFFFF9800)
private val Black = Color(0xFF000000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraUsuarioPerfil(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    friendsViewModel: FriendsViewModel,
    navController: NavController
) {
    val user by userViewModel.user.collectAsState()
    val friendsState by friendsViewModel.friendsState.collectAsState()
    val userPosts by userViewModel.userPosts.collectAsState()
    val stats by userViewModel.userStats.collectAsState()

    var mostrarEditarPerfil by remember { mutableStateOf(false) }
    var nombreEditado by remember { mutableStateOf("") }
    var mostrarOpcionesFoto by remember { mutableStateOf(false) }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            userViewModel.uploadProfileImage(it) { success ->
                if (success) {
                    userViewModel.loadUserData()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.loadUserData()
        userViewModel.loadUserStats()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // FOTO DE PERFIL
            Box {
                if (user.profileImage.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(user.profileImage),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clickable { mostrarOpcionesFoto = true },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Foto de perfil",
                        tint = OrangePrimary,
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { mostrarOpcionesFoto = true }
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clickable { mostrarOpcionesFoto = true },
                    shape = CircleShape,
                    color = OrangePrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar foto",
                        modifier = Modifier.padding(6.dp),
                        tint = Color.White
                    )
                }
            }

            // INFO DEL USUARIO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = user.name.ifEmpty { "Usuario" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )

                    IconButton(
                        onClick = {
                            nombreEditado = user.name
                            mostrarEditarPerfil = true
                        },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar nombre",
                            tint = OrangePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (user.email.isNotEmpty()) {
                    Text(
                        text = user.email,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // STATS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PerfilStatBarra(
                        titulo = "Posts",
                        valor = userPosts.size.toString()
                    )

                    PerfilStatBarra(
                        titulo = "Amigos",
                        valor = friendsState.friends.size.toString(),
                        onClick = { navController.navigate(AppScreens.ListaAmigos.name) }
                    )
                }
            }
        }
    }


    if (mostrarOpcionesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarOpcionesFoto = false },
            title = { Text("Foto de perfil", color = OrangePrimary) },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            galeriaLauncher.launch("image/*")
                            mostrarOpcionesFoto = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = OrangePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar de galería", color = Black)
                    }

                    if (user.profileImage.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                userViewModel.removeProfileImage()
                                mostrarOpcionesFoto = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = OrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminar foto", color = Black)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarOpcionesFoto = false }) {
                    Text("Cerrar", color = OrangePrimary)
                }
            }
        )
    }

    if (mostrarEditarPerfil) {
        AlertDialog(
            onDismissRequest = { mostrarEditarPerfil = false },
            title = { Text("Editar nombre", color = OrangePrimary) },
            text = {
                OutlinedTextField(
                    value = nombreEditado,
                    onValueChange = { nombreEditado = it },
                    label = { Text("Nombre", color = OrangePrimary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = OrangeSecondary,
                        cursorColor = OrangePrimary,
                        focusedLabelColor = OrangePrimary,
                        unfocusedLabelColor = OrangeSecondary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nombreEditado.isNotBlank()) {
                            userViewModel.updateUserName(nombreEditado)
                            mostrarEditarPerfil = false
                        }
                    }
                ) {
                    Text("Guardar", color = OrangePrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEditarPerfil = false }) {
                    Text("Cancelar", color = OrangeSecondary)
                }
            }
        )
    }
}

@Composable
private fun PerfilStatBarra(
    titulo: String,
    valor: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) {
            Modifier.clickable { onClick() }
        } else Modifier
    ) {
        Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OrangePrimary)
        Text(text = titulo, fontSize = 14.sp, color = Black)
    }
}
