package com.example.runsyncmockups.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    onNavigateToChat: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToFriendProfile: (String) -> Unit = {},
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
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarAgregarAmigo = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Agregar amigo")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarAgregarAmigo = true },
                containerColor = Color(0xFF0C0C0C)
            ) {
                Icon(Icons.Default.PersonAdd,
                    contentDescription = "Agregar amigo",
                    tint = Color.White
                )
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
                                onChat = {
                                    onNavigateToChat(friend.id, friend.nombre, friend.email)
                                },
                                onVerPerfil = {
                                    onNavigateToFriendProfile(friend.id)
                                }
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
    onChat: () -> Unit,
    onVerPerfil: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val orange = Color(0xFFFF5722)
    val black = Color(0xFF050505)
    val white = Color(0xFFFFFFFF)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = white
        ),
        border = BorderStroke(1.dp, orange.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (friend.profileImageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(friend.profileImageUrl),
                    contentDescription = "Foto de ${friend.nombre}",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(2.dp, orange, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto",
                    modifier = Modifier
                        .size(56.dp)
                        .border(2.dp, orange, CircleShape),
                    tint = orange
                )
            }

            // INFORMACIÓN
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = friend.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = black
                )
                Text(
                    text = friend.email,
                    fontSize = 14.sp,
                    color = black.copy(alpha = 0.6f)
                )
            }

            IconButton(
                onClick = onChat,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(orange)
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chatear",
                    tint = white
                )
            }

            // MENU
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Más opciones",
                        tint = black
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Ver perfil", color = black) },
                        onClick = {
                            showMenu = false
                            onVerPerfil()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = orange
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar amigo", color = black) },
                        onClick = {
                            showMenu = false
                            onEliminar()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.PersonRemove,
                                contentDescription = null,
                                tint = orange
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
        Text(
            text = "Agrega amigos para comenzar a chatear y compartir tus carreras",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
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