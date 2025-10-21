package com.example.runsyncmockups

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.runsyncmockups.Navigation.Navigation
import com.google.firebase.auth.FirebaseAuth


val firebaseAuth = FirebaseAuth.getInstance()
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation()
        }
    }
}


