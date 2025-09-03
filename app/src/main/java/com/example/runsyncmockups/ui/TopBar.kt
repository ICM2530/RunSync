package com.example.runsyncmockups.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(text = "Chat")
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFC0BDC7),
            titleContentColor = Color(0xFF000000)
        )
    )
}

@Preview
@Composable
fun PreviewTopBar() {
    TopBar()
}