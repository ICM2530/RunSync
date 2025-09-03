package com.example.runsyncmockups.ui

import BottomBarView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.ui.mocks.PantallaHomeMock


@Composable
fun PantallaHome(navController: NavController){


    Scaffold(
        bottomBar = {BottomBarView(navController)}
    )
    { padding ->

        PantallaHomeMock(modifier = Modifier.padding(padding))

    }
}

@Preview
@Composable
fun PreviewPantallaHome(){
    val navController = rememberNavController()

    PantallaHome(navController)
}
