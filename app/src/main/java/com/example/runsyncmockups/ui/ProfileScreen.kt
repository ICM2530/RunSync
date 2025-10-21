package com.example.runsyncmockups.ui

import BottomBarView
import android.provider.ContactsContract
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.runsyncmockups.ui.mocks.PantallaPerfil

@Composable
fun ProfileScreen(navController: NavController) {


    Scaffold(
        bottomBar = {BottomBarView(navController)}
    )
    { padding ->
        PantallaPerfil(modifier = Modifier.padding(padding))
    }
}