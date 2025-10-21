package com.example.runsyncmockups.ui

    import BottomBarView
    import androidx.compose.foundation.layout.padding
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Logout
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.material3.TopAppBar
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import com.example.runsyncmockups.Navigation.AppScreens
    import com.example.runsyncmockups.firebaseAuth
    import com.example.runsyncmockups.ui.mocks.PantallaPerfil

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Button

import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfileScreen(navController: NavController) {


        Scaffold(
            topBar = {
                TopAppBar(
                    title= { Text("Perfil",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )}, actions =  {
                        IconButton(onClick = {
                            firebaseAuth.signOut()
                            navController.navigate(AppScreens.InicioSesion.name){
                                popUpTo(AppScreens.Profile.name){
                                    inclusive = true
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                )
            },
            bottomBar = {BottomBarView(navController)}
        )
        { padding ->
            PantallaPerfil(modifier = Modifier.padding(padding))


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)

        ) {

            Button(
                onClick = {
                    navController.navigate(AppScreens.Voz.name)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text("Ir a Comando de Voz de prueba")
            }
        }}}

