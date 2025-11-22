package com.example.runsyncmockups.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.runsyncmockups.model.Friend
import com.example.runsyncmockups.model.FriendsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaAmigos(
    onNavigateBack: () -> Unit,
    friendsViewModel: FriendsViewModel = viewModel()
) {
    val friendsState by friendsViewModel.friendsState.collectAsState()
    var mostrarAgregarAmigo by remember { mutableStateOf(false) }
    var amigoAEliminar by remember { mutableStateOf<Friend?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mis Amigos")
                        Text(
                            text = "${friendsState.friends.size} amigos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarAgregarAmigo = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Agregar amigo")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                friendsState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                friendsState.friends.isEmpty() -> {
                    EmptyFriendsState(
                        onAgregarAmigo = { mostrarAgregarAmigo = true },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(friendsState.friends) { friend ->
                            FriendCard(
                                friend = friend,
                                onEliminar = { amigoAEliminar = friend },
                                onChat = { /* TODO: Navegar al chat */ }
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarAgregarAmigo) {
        AgregarAmigoDialog(
            onDismiss = { mostrarAgregarAmigo = false },
            friendsViewModel = friendsViewModel
        )
    }

    if (amigoAEliminar != null) {
        ConfirmarEliminarDialog(
            friend = amigoAEliminar!!,
            onConfirm = {
                friendsViewModel.removeFriend(amigoAEliminar!!.id)
                amigoAEliminar = null
            },
            onDismiss = { amigoAEliminar = null }
        )
    }
}

@Composable
fun FriendCard(
    friend: Friend,
    onEliminar: () -> Unit,
    onChat: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto de perfil
            if (friend.profileImageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(friend.profileImageUrl),
                    contentDescription = "Foto de ${friend.nombre}",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto de ${friend.nombre}",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Información del amigo
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = friend.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = friend.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Botón de chat
            IconButton(onClick = onChat) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chatear",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Menú de opciones
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Eliminar amigo") },
                        onClick = {
                            showMenu = false
                            onEliminar()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.PersonRemove,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyFriendsState(
    onAgregarAmigo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Text(
            text = "Aún no tienes amigos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        /*Text(
            text = "Agrega amigos para comenzar a chatear y compartir tus carreras",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )*/
        Button(
            onClick = onAgregarAmigo,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar Amigo")
        }
    }
}

@Composable
fun ConfirmarEliminarDialog(
    friend: Friend,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Eliminar amigo") },
        text = {
            Text("¿Estás seguro de que quieres eliminar a ${friend.nombre} de tu lista de amigos?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaListaAmigos() {
    PantallaListaAmigos(onNavigateBack = {})
}