package com.example.runsyncmockups.ui.mocks

import BottomBarView
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.model.FriendsViewModel
import com.example.runsyncmockups.ui.AgregarAmigoDialog
import com.example.runsyncmockups.ui.model.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(),
    friendsViewModel: FriendsViewModel = viewModel(),
    onNavigateToFriendsList: () -> Unit = {}
) {
    val user by userViewModel.user.collectAsState()
    val friendsState by friendsViewModel.friendsState.collectAsState()
    val stats by userViewModel.userStats.collectAsState()
    val isUploadingImage by userViewModel.isUploadingImage.collectAsState()
    val userPosts by userViewModel.userPosts.collectAsState()
    val isUploadingPost by userViewModel.isUploadingPost.collectAsState()

    var mostrarAgregarAmigo by remember { mutableStateOf(false) }
    var mostrarEditarPerfil by remember { mutableStateOf(false) }
    var nombreEditado by remember { mutableStateOf("") }
    var mostrarOpcionesFoto by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    // Launcher para seleccionar imagen de perfil
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

    // Launcher para agregar posts (múltiples imágenes)
    val agregarPostLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            userViewModel.uploadPost(it) { success ->
                if (success) {
                    userViewModel.loadUserPosts()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.loadUserData()
        userViewModel.loadUserStats()
        userViewModel.loadUserPosts()
    }
    Scaffold(

        bottomBar = {BottomBarView(navController)}
    ) {  paddingValues ->
    Column(
        modifier = modifier.fillMaxSize().padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Encabezado con foto de perfil y nombre
        BarraUsuarioPerfil (
            modifier = Modifier,
            userViewModel = userViewModel,
            friendsViewModel = friendsViewModel,
            onNavigateToFriendsList = onNavigateToFriendsList
        )

        Divider()

        // Tabs para cambiar entre Grid y Stats
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = { Icon(Icons.Default.GridOn, contentDescription = "Grid") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") }
            )
        }

        // Contenido según la tab seleccionada
        when (selectedTab) {
            0 -> {
                // Grid de posts estilo Instagram
                Box(modifier = Modifier.fillMaxSize()) {
                    if (userPosts.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay publicaciones aún",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Comparte tus mejores momentos",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(1.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(userPosts) { post ->
                                Image(
                                    painter = rememberAsyncImagePainter(post.imageUrl),
                                    contentDescription = "Post",
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .padding(1.dp)
                                        .clickable { /* TODO: Ver detalle del post */ },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    // Botón flotante para agregar post
                    FloatingActionButton(
                        onClick = { agregarPostLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        if (isUploadingPost) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Icon(Icons.Default.Add, contentDescription = "Agregar post")
                        }
                    }
                }
            }
            1 -> {
                // Estadísticas
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Estadísticas de Running",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatItem(
                                    icono = Icons.Default.DirectionsRun,
                                    label = "Distancia Total",
                                    valor = "${stats.totalDistance} km"
                                )
                                StatItem(
                                    icono = Icons.Default.Timer,
                                    label = "Tiempo Total",
                                    valor = "${stats.totalTime} hrs"
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatItem(
                                    icono = Icons.Default.Speed,
                                    label = "Velocidad Prom.",
                                    valor = "${stats.avgSpeed} km/h"
                                )
                                StatItem(
                                    icono = Icons.Default.LocalFireDepartment,
                                    label = "Calorías",
                                    valor = "${stats.totalCalories} kcal"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para seleccionar foto de perfil
    if (mostrarOpcionesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarOpcionesFoto = false },
            title = { Text("Foto de perfil") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            galeriaLauncher.launch("image/*")
                            mostrarOpcionesFoto = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar de galería")
                    }

                    if (user.profileImage.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                userViewModel.removeProfileImage()
                                mostrarOpcionesFoto = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminar foto")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarOpcionesFoto = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para editar nombre
    if (mostrarEditarPerfil) {
        AlertDialog(
            onDismissRequest = { mostrarEditarPerfil = false },
            title = { Text("Editar nombre") },
            text = {
                OutlinedTextField(
                    value = nombreEditado,
                    onValueChange = { nombreEditado = it },
                    label = { Text("Nombre") },
                    singleLine = true
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
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEditarPerfil = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarAgregarAmigo) {
        AgregarAmigoDialog(
            onDismiss = { mostrarAgregarAmigo = false },
            friendsViewModel = friendsViewModel
        )
    }
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

@Composable
fun StatItem(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valor: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icono,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = valor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}