package com.example.runsyncmockups.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.runsyncmockups.ui.mocks.RutaDetalleMock

@Preview
@Composable
fun PantallaDetallesRutas(){

    Scaffold()
    { padding ->
        RutaDetalleMock(modifier = Modifier.padding(padding))
    }
}

