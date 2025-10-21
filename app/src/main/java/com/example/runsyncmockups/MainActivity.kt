package com.example.runsyncmockups

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.runsyncmockups.Navigation.Navigation
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.ui.theme.RunSyncMockUpsTheme

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var lightSensor : Sensor? = null

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation(LocationViewModel())
        }
    }
}


