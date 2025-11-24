package com.example.runsyncmockups.ui


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.MyMarker
import com.example.runsyncmockups.model.MyUsersViewModel
import com.example.runsyncmockups.ui.components.DashboardCard
import com.google.android.gms.maps.model.LatLng

@Composable
fun enabledList(
    navcontroller: NavController,
    viewModel: MyUsersViewModel = viewModel(),
    locationVm: LocationViewModel
) {
    val users by viewModel.users.collectAsState()

    val enabled = remember(users) {
        users.filter { it.status?.trim() == "Disponible" }
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
                            Text(text = "${item.name} ${item.lastName}")
                            Text(text = "Estado: ${item.status}")
                        },
                        buttonText = "Ver ubicación",
                        onClick = {
                            val lat = item.lat
                            val lon = item.lon
                            if ((lat == 0.0 && lon == 0.0) || lat == null || lon == null) {
                                Log.d("OTRO", "LAT Y LON SON 0")
                                return@DashboardCard
                            }

                            val latLng = LatLng(lat, lon)


                            Log.d("OTRO", "LOCATION=${item.lat} ${item.lon}")
                            val marker = MyMarker(latLng, item.name)
                            locationVm.replaceWith(marker)
                            navcontroller.navigate("seguimiento/${item.name}/${item.id}")
                        },


                    )
                }
            }
        }
    }
}