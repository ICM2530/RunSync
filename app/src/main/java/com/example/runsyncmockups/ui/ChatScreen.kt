package com.example.runsyncmockups.ui

import BottomBarView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.runsyncmockups.ui.mocks.PantallaChat

@Composable
fun ChatScreen(navController: NavController) {
    Scaffold(

    )
    { padding ->
        PantallaChat(modifier = Modifier.padding(padding))
    }

}