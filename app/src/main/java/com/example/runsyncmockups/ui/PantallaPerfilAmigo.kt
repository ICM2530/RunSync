package com.example.runsyncmockups.ui

import androidx.compose.foundation.Image
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

// Colores principales
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
    val isLoading by viewModel.isLoadingProfile.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(friendId) {
        viewModel.loadFriendProfile(friendId)
        viewModel.loadFriendPosts(friendId)
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Foto de perfil
                    if (friendProfile?.profileImage?.isNotEmpty() == true) {
                        Image(
                            painter = rememberAsyncImagePainter(friendProfile?.profileImage),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Foto",
                            modifier = Modifier.size(120.dp),
                            tint = OrangePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre
                    Text(
                        text = friendProfile?.name ?: "Usuario",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText
                    )

                    // Email
                    if (friendProfile?.email?.isNotEmpty() == true) {
                        Text(
                            text = friendProfile?.email ?: "",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Solo Posts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FriendStatColumn("Posts", friendPosts.size.toString())
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
                }

                // ---------------- CONTENIDO DE LOS TABS ----------------
                when (selectedTab) {
                    0 -> {
                        // Grid de posts
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
                }
            }
        }
    }
}

@Composable
private fun FriendStatColumn(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = BlackText
        )
        Text(
            text = titulo,
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}