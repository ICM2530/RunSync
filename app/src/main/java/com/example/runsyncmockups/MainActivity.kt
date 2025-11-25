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
import com.example.runsyncmockups.ui.model.UserViewModel
import com.example.runsyncmockups.ui.theme.RunSyncMockUpsTheme
import com.google.firebase.messaging.FirebaseMessaging

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

        //obtenemos el token de mensaje
        getFCMToken()

        enableEdgeToEdge()
        setContent {
            Navigation(RouteListViewModel(),LocationViewModel(),
                UserViewModel(),EventListViewModel(),
                EventRepository(), LocationViewModel(), UserAuthViewModel(), MyUsersViewModel(),
                ChallengeViewModel()
            )
        }
    }
}

    // FUNCIÓN PARA OBTENER Y GUARDAR EL TOKEN FCM
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                println("TOKEN FCM GENERADO: ${token.take(10)}...")

                // Guardar el token en Realtime Database
                saveTokenToDatabase(token)
            } else {
                println("ERROR generando token: ${task.exception}")
            }
        }
    }

    // FUNCIÓN PARA GUARDAR EL TOKEN EN LA BASE DE DATOS
    private fun saveTokenToDatabase(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("fcmToken")
                .setValue(token)
                .addOnSuccessListener {
                    println("TOKEN GUARDADO EN BASE DE DATOS para usuario: $userId")
                }
                .addOnFailureListener { e ->
                    println("ERROR guardando token: ${e.message}")
                }
        } else {
            println("Usuario no autenticado, no se puede guardar token")
        }
    }
}