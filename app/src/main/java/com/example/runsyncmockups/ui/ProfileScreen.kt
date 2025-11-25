package com.example.runsyncmockups.ui.mocks

import BottomBarView
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.model.FriendsViewModel
import com.example.runsyncmockups.ui.AgregarAmigoDialog
import com.example.runsyncmockups.ui.model.UserViewModel

// 🎨 Tus colores
private val PrimaryColor = Color(0xFFFF5722)
private val SecondaryColor = Color(0xFFFF9800)
private val DarkText = Color(0xFF000000)
private val LightBackground = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel(),
    friendsViewModel: FriendsViewModel = viewModel(),
) {
    val user by userViewModel.user.collectAsState()
    val stats by userViewModel.userStats.collectAsState()
    val userPosts by userViewModel.userPosts.collectAsState()

    var mostrarAgregarAmigo by remember { mutableStateOf(false) }
    var mostrarEditarPerfil by remember { mutableStateOf(false) }
    var nombreEditado by remember { mutableStateOf("") }
    var mostrarOpcionesFoto by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            userViewModel.uploadProfileImage(it) { success ->
                if (success) userViewModel.loadUserData()
            }
        }
    }

    val agregarPostLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            userViewModel.uploadPost(it) { success ->
                if (success) userViewModel.loadUserPosts()
            }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.loadUserData()
        userViewModel.loadUserStats()
        userViewModel.loadUserPosts()
    }

    Scaffold(
        containerColor = LightBackground,
        bottomBar = { BottomBarView(navController) }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Encabezado
            BarraUsuarioPerfil(
                modifier = Modifier,
                userViewModel = userViewModel,
                friendsViewModel = friendsViewModel,
                navController = navController

            )

            Divider(color = PrimaryColor.copy(alpha = 0.4f))

            // Tabs con color naranja
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PrimaryColor,
                contentColor = DarkText
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(Icons.Default.GridOn, contentDescription = "Grid", tint = DarkText)
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(Icons.Default.BarChart, contentDescription = "Stats", tint = DarkText)
                    }
                )
            }

            when (selectedTab) {
                0 -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (userPosts.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = SecondaryColor
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No hay publicaciones aún",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
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
                                            .padding(1.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        // FAB naranja
                        FloatingActionButton(
                            onClick = { agregarPostLauncher.launch("image/*") },
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomEnd),
                            containerColor = PrimaryColor,
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }

                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = LightBackground),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Estadísticas de Running",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
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

        // Diálogo cambiar foto
        if (mostrarOpcionesFoto) {
            AlertDialog(
                onDismissRequest = { mostrarOpcionesFoto = false },
                title = { Text("Foto de perfil", color = DarkText) },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                galeriaLauncher.launch("image/*")
                                mostrarOpcionesFoto = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PrimaryColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Seleccionar de galería", color = DarkText)
                        }

                        if (user.profileImage.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    userViewModel.removeProfileImage()
                                    mostrarOpcionesFoto = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Eliminar foto", color = DarkText)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { mostrarOpcionesFoto = false }) {
                        Text("Cancelar", color = PrimaryColor)
                    }
                }
            )
        }

        // Diálogo editar nombre
        if (mostrarEditarPerfil) {
            AlertDialog(
                onDismissRequest = { mostrarEditarPerfil = false },
                title = { Text("Editar nombre", color = DarkText) },
                text = {
                    OutlinedTextField(
                        value = nombreEditado,
                        onValueChange = { nombreEditado = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            focusedLabelColor = PrimaryColor,
                            cursorColor = PrimaryColor
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (nombreEditado.isNotBlank()) {
                            userViewModel.updateUserName(nombreEditado)
                            mostrarEditarPerfil = false
                        }
                    }) {
                        Text("Guardar", color = PrimaryColor)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarEditarPerfil = false }) {
                        Text("Cancelar", color = SecondaryColor)
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
fun StatItem(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valor: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icono, contentDescription = label, tint = PrimaryColor)
        Text(valor, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
        Text(label, fontSize = 12.sp, color = DarkText.copy(alpha = 0.6f))
    }
}
