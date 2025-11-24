package com.example.runsyncmockups

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.runsyncmockups.Navigation.Navigation
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.ui.theme.RunSyncMockUpsTheme


val firebaseAuth = FirebaseAuth.getInstance()
class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var lightSensor : Sensor? = null

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            Navigation(LocationViewModel())
        }
    }
}


