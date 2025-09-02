package com.example.runsyncmockups.ui

import BottomBarView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.ui.mocks.PantallaExplorarMock



@Composable
fun PantallaRutas(navController: NavController){
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {BottomBarView(navController)}
    )

    { padding ->
        PantallaExplorarMock(Modifier.padding(padding),navController)
    }
}

@Preview
@Composable
fun PreviewPantallaRutas(){
    val navController = rememberNavController()
    PantallaRutas(navController)
}