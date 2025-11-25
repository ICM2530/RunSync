package com.example.runsyncmockups.ui.mocks

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.model.FriendsViewModel
import com.example.runsyncmockups.ui.AgregarAmigoDialog
import com.example.runsyncmockups.ui.model.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraUsuarioPerfil(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel, // Sin viewModel()
    friendsViewModel: FriendsViewModel, // Sin viewModel()
    onNavigateToFriendsList: () -> Unit = {}
) {
    val user by userViewModel.user.collectAsState()
    val friendsState by friendsViewModel.friendsState.collectAsState()
    val stats by userViewModel.userStats.collectAsState()
    val isUploadingImage by userViewModel.isUploadingImage.collectAsState()

    var mostrarAgregarAmigo by remember { mutableStateOf(false) }
    var mostrarEditarPerfil by remember { mutableStateOf(false) }
    var nombreEditado by remember { mutableStateOf("") }
    var mostrarOpcionesFoto by remember { mutableStateOf(false) }

    // Launcher para seleccionar imagen de galería
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Header con foto y stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto de perfil clickeable
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
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { mostrarOpcionesFoto = true }
                    )
                }

                // Loading indicator mientras sube la imagen
                if (isUploadingImage) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(40.dp)
                    )
                }

                // Badge de editar foto
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clickable { mostrarOpcionesFoto = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar foto",
                        modifier = Modifier.padding(6.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name.ifEmpty { "Usuario" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            nombreEditado = user.name
                            mostrarEditarPerfil = true
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar nombre",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (user.email.isNotEmpty()) {
                    Text(
                        text = user.email,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PerfilStat(titulo = "Carreras", valor = stats.races.toString())
                    PerfilStat(
                        titulo = "Amigos",
                        valor = friendsState.friends.size.toString(),
                        onClick = { onNavigateToFriendsList() }
                    )

                }
            }
        }
    }
}


