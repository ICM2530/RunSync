package com.example.runsyncmockups.ui

import BottomBarView
import android.provider.ContactsContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.ui.mocks.PantallaActividad
import com.example.runsyncmockups.ui.mocks.PantallaPerfil


@Composable
fun ProfileScreen(navController: NavController) {

    Scaffold(
        bottomBar = { BottomBarView(navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PantallaPerfil(modifier = Modifier.fillMaxSize())
            Button(
                onClick = {
                    navController.navigate(AppScreens.Voz.name)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Ir a Comando de Voz de prueba")
            }

            // Contenedor para alinear los botones horizontalmente
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        navController.navigate(AppScreens.Scanner.name)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("SCAN ME!")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        navController.navigate(AppScreens.GeneradorQR.name)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("GENERA CUPON")
                }
            }
        }
    }
}