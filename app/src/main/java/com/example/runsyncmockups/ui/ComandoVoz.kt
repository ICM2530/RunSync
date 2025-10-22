package com.example.runsyncmockups.ui

import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import java.util.Locale

@Composable
fun SpeechText(navController: androidx.navigation.NavController) {
    val context = LocalContext.current // Obtiene el contexto de la actividad
    var outputTxt by remember { mutableStateOf("Da click en el botón para hablar") }

   // ESTADO DE PERMISO PARA GRABAR AUDIO
    val recordAudioPermission = RECORD_AUDIO
    val permissionGranted = ContextCompat.checkSelfPermission(
        context, recordAudioPermission
    ) == PackageManager.PERMISSION_GRANTED // Permiso concedido

    //para pedir permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Se necesita permiso de micrófono", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para iniciar reconocimiento de voz
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) { // Si el reconocimiento fue exitoso
            val results = result.data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS // Obtiene los resultados del reconocimiento
            )
            outputTxt = results?.get(0).toString() // Muestra el primer resultado
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Háblame!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (permissionGranted) {
                    getSpeechInput(context, speechLauncher)
                } else {
                    permissionLauncher.launch(recordAudioPermission)
                }
            },
            shape = CircleShape,
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = "Mic",
                tint = Color.Black,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = outputTxt,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

private fun getSpeechInput(context: Context, launcher: ActivityResultLauncher<Intent>) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        Toast.makeText(context, "Reconocimiento no disponible", Toast.LENGTH_SHORT).show()
    } else {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH) // Crea un intent para reconocimiento de voz
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, // Configura el modelo de lenguaje
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM // Modelo de lenguaje libre
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()) // Configura el idioma
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo...") // Mensaje de solicitud

        launcher.launch(intent) // Inicia el reconocimiento de voz
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVoz() {
    val navController = rememberNavController()
    SpeechText(navController)
}