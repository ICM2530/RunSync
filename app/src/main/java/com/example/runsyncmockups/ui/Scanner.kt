package com.example.runsyncmockups.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanOptions.ALL_CODE_TYPES

@Composable
fun ScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity
    var scanResult by remember { mutableStateOf("Presiona el botón para escanear") }

    // Launcher para permisos de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_LONG).show()
        }
    }

    // Launcher para el scanner
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intentResult = IntentIntegrator.parseActivityResult(
            result.resultCode,
            result.data
        )
        if (intentResult != null) {
            if (intentResult.contents == null) {
                scanResult = "Escaneo cancelado"
                Toast.makeText(context, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                scanResult = "Resultado: ${intentResult.contents}"
                Toast.makeText(
                    context,
                    "Código: ${intentResult.contents}",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("SCANNER", "Código escaneado: ${intentResult.contents}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(scanResult)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Verificar y solicitar permiso
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.CAMERA
                    ) -> {
                        // Permiso ya concedido, iniciar scanner
                        activity?.let {
                            val integrator = IntentIntegrator(it)
                            integrator.setDesiredBarcodeFormats(ALL_CODE_TYPES)
                            integrator.setPrompt("Escanea un código QR o de barras")
                            integrator.setBeepEnabled(true)
                            integrator.setOrientationLocked(false)
                            integrator.setBarcodeImageEnabled(false)
                            scannerLauncher.launch(integrator.createScanIntent())
                        }
                    }
                    else -> {
                        // Solicitar permiso
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Iniciar Escaneo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Volver")
        }
    }
}