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
import com.example.runsyncmockups.model.ChallengeViewModel
import com.example.runsyncmockups.model.EventListViewModel
import com.example.runsyncmockups.model.EventRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.notificaciones.NotificationPermissionHelper
import com.example.runsyncmockups.model.MyUsersViewModel
import com.example.runsyncmockups.model.RouteListViewModel
import com.example.runsyncmockups.model.UserAuthViewModel
import com.example.runsyncmockups.ui.theme.RunSyncMockUpsTheme


val firebaseAuth = FirebaseAuth.getInstance()
class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var lightSensor : Sensor? = null

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Solicitar permisos de notificación
        if (!NotificationPermissionHelper.hasNotificationPermission(this)) {
            NotificationPermissionHelper.requestNotificationPermission(this)
        }
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            Navigation(RouteListViewModel(),LocationViewModel(),
                UserAuthViewModel(),EventListViewModel(),
                EventRepository(), LocationViewModel(), UserAuthViewModel(), MyUsersViewModel(),
                ChallengeViewModel()
            )
        }
    }
}


