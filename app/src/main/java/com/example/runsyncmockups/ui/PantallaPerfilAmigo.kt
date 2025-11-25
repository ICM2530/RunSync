package com.example.runsyncmockups.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.model.FriendsViewModel

// Colores principales definidos aquí
private val OrangePrimary = Color(0xFFFF9800)
private val BlackText = Color(0xFF000000)
private val SoftWhite = Color(0xFFFFFFFF)
private val SoftGray = Color(0xFFEEEEEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfilAmigo(
    friendId: String,
    onNavigateBack: () -> Unit,
    viewModel: FriendsViewModel = viewModel()
) {
    val friendProfile by viewModel.friendProfile.collectAsState()
    val friendPosts by viewModel.friendPosts.collectAsState()
    val friendStats by viewModel.friendStats.collectAsState()
    val isLoading by viewModel.isLoadingProfile.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(friendId) {
        viewModel.loadFriendProfile(friendId)
        viewModel.loadFriendPosts(friendId)
        viewModel.loadFriendStats(friendId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearFriendProfile()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoftWhite,
                    navigationIconContentColor = OrangePrimary,
                    titleContentColor = BlackText
                ),
                title = { Text(friendProfile?.name ?: "Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        containerColor = SoftWhite
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ---------------- HEADER ----------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (friendProfile?.profileImage?.isNotEmpty() == true) {
                        Image(
                            painter = rememberAsyncImagePainter(friendProfile?.profileImage),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Foto",
                            modifier = Modifier.size(100.dp),
                            tint = OrangePrimary
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = friendProfile?.name ?: "Usuario",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlackText
                        )

                        if (friendProfile?.email?.isNotEmpty() == true) {
                            Text(
                                text = friendProfile?.email ?: "",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            FriendStatColumn("Posts", friendPosts.size.toString())
                            FriendStatColumn("Carreras", friendStats.races.toString())
                        }
                    }
                }

                Divider(thickness = 1.dp, color = SoftGray)

                // ---------------- TABS ----------------
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SoftWhite,
                    contentColor = OrangePrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = OrangePrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                Icons.Default.GridOn,
                                contentDescription = "Grid",
                                tint = if (selectedTab == 0) OrangePrimary else Color.Gray
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = "Stats",
                                tint = if (selectedTab == 1) OrangePrimary else Color.Gray
                            )
                        }
                    )
                }

                // ---------------- CONTENIDO SEGÚN TAB ----------------
                when (selectedTab) {

                    // ---------- TAB GRID ----------
                    0 -> {
                        if (friendPosts.isEmpty()) {
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
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No hay publicaciones",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlackText
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                contentPadding = PaddingValues(1.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(friendPosts) { post ->
                                    Image(
                                        painter = rememberAsyncImagePainter(post.imageUrl),
                                        contentDescription = "Post",
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .padding(1.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    // ---------- TAB ESTADÍSTICAS ----------
                    1 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = SoftGray)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Estadísticas de Running",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BlackText
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        FriendStatItem(
                                            Icons.Default.DirectionsRun,
                                            "Distancia Total",
                                            "${friendStats.totalDistance} km"
                                        )
                                        FriendStatItem(
                                            Icons.Default.Timer,
                                            "Tiempo Total",
                                            "${friendStats.totalTime} hrs"
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        FriendStatItem(
                                            Icons.Default.Speed,
                                            "Velocidad Prom.",
                                            "${friendStats.avgSpeed} km/h"
                                        )
                                        FriendStatItem(
                                            Icons.Default.LocalFireDepartment,
                                            "Calorías",
                                            "${friendStats.totalCalories} kcal"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendStatColumn(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BlackText)
        Text(text = titulo, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
private fun FriendStatItem(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valor: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icono,
            contentDescription = label,
            tint = OrangePrimary,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = valor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = BlackText
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
