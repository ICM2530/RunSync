package com.example.runsyncmockups.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.runsyncmockups.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person



@Composable
fun BottomBarView() {
    var selectedItem by remember { mutableStateOf(0) }

    val items = listOf(
        Pair("Inicio", Icons.Filled.Home),
        Pair("Rutas", Icons.Filled.Place),
        Pair("Actividades", Icons.Filled.FlashOn),
        Pair("Eventos", Icons.Filled.Event),
        Pair("Perfil", Icons.Filled.Person)
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                icon = {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first
                    )
                },
                //label = { Text(item.first) }
            )
        }
    }
}
