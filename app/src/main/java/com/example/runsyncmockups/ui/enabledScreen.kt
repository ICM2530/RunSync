package com.example.runsyncmockups.ui


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.runsyncmockups.model.ChallengeViewModel
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.MyMarker
import com.example.runsyncmockups.model.MyUsersViewModel
import com.example.runsyncmockups.ui.components.DashboardCard
import com.google.android.gms.maps.model.LatLng

@Composable
fun enabledList(
    navcontroller: NavController,
    viewModel: MyUsersViewModel = viewModel(),
    locationVm: LocationViewModel,
    challengeVm: ChallengeViewModel
) {
    val users by viewModel.users.collectAsState()

    val enabled = remember(users) {
        users.filter { it.status?.trim() == "Disponible" }
    }
    LaunchedEffect (enabled) {
        enabled.forEach { user ->
            Log.d("ENABLED_LIST_DEBUG", "Usuario: ${user.name}, Imagen URL: ${user.profileImageUrl}")
        }
    }


    Scaffold { paddingValues ->
        if (enabled.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay usuarios disponibles")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(enabled) { item ->
                    Log.d("USERS", "infoUSER=${item.name}")

                    DashboardCard(
                        title = "",
                        content = {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {

                                if (!item.profileImageUrl.isNullOrEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(item.profileImageUrl),
                                        contentDescription = "Foto de ${item.name}",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "${item.name} ${item.lastName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Estado: ${item.status}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        },
                        buttonText = "Retar",
                        onClick = {
                            challengeVm.sendChallenge(item)
                        }
                    )
                }
            }
        }
    }
}