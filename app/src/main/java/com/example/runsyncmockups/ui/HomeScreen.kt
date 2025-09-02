package com.example.runsyncmockups.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.runsyncmockups.ui.BottomBarView


@Preview
@Composable
fun homeScreen(){

    Scaffold(
        bottomBar = {BottomBarView()}
    )

    { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {



        }
    }
}
