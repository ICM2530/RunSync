package com.example.runsyncmockups.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.R
import com.example.runsyncmockups.api.DirectionsRepo
import com.example.runsyncmockups.firebaseAuth
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.MyUsersViewModel
import com.example.runsyncmockups.model.UserAuthViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
@Composable
fun SeguimientoScreen(
    name: String,
    userId: String?,              // uid del oponente
    navController: NavController,
    locVm: LocationViewModel,
    authVm: UserAuthViewModel,
    myUsersVm: MyUsersViewModel,  // lista de usuarios (para ubi del rival)
) {
    val context = LocalContext.current
    val myRoutePoints = remember { mutableStateListOf<LatLng>() }
    val opponentRoutePoints = remember { mutableStateListOf<LatLng>() }
    val directionsKey = context.getString(R.string.google_directions_key)

    // 👉 Localización y actualización de mi posición
    LaunchedEffect(true) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000
        ).setWaitForAccurateLocation(true).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                locVm.update(loc.latitude, loc.longitude)
                authVm.updateLocActual(LatLng(loc.latitude, loc.longitude))
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        }
    }

    val state by locVm.state.collectAsState()

    var showWinDialog by remember { mutableStateOf(false) }
    var showLoseDialog by remember { mutableStateOf(false) }
    var raceFinished by remember { mutableStateOf(false) }

    val miUbi = LatLng(state.latitude, state.longitude)
    authVm.updateLocActual(miUbi)

    val users by myUsersVm.users.collectAsState()
    val selectedUser = users.firstOrNull { it.id == userId }

    val ubiUser = remember(selectedUser?.lat, selectedUser?.lon) {
        LatLng(
            selectedUser?.lat ?: 0.0,
            selectedUser?.lon ?: 0.0
        )
    }

    Log.d("SEGUI", "Mi ubicación REAL = $miUbi")
    Log.d("SEGUI", "Ubicación usuario = $ubiUser")

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubiUser, 16f)
    }
    val currentMiUbi by rememberUpdatedState(miUbi)
    val currentUbiUser by rememberUpdatedState(ubiUser)
    val currentSelectedUser by rememberUpdatedState(selectedUser)

    val destinoState = remember { mutableStateOf<LatLng?>(null) }
    val destinoNameState = remember { mutableStateOf("Meta") }
    val currentUid = firebaseAuth.uid

    // 🔹 Cargamos la meta (destino) desde mi nodo o el del rival
    LaunchedEffect(currentUid, userId) {
        if (currentUid == null || userId == null) return@LaunchedEffect

        val db = FirebaseDatabase.getInstance()

        // 1️⃣ Intento leer challenge desde MI nodo (si soy el retado)
        val myRef = db.getReference("users")
            .child(currentUid)
            .child("challenge")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(mySnap: DataSnapshot) {
                val destLat = mySnap.child("destLat").getValue(Double::class.java)
                val destLon = mySnap.child("destLon").getValue(Double::class.java)
                val destName = mySnap.child("destName").getValue(String::class.java)

                if (destLat != null && destLon != null) {
                    destinoState.value = LatLng(destLat, destLon)
                    destinoNameState.value = destName ?: "Meta"
                } else {
                    // 2️⃣ Si en mí no hay destino, intento cargarlo desde el rival (caso retador)
                    val oppRef = db.getReference("users")
                        .child(userId)
                        .child("challenge")

                    oppRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(oppSnap: DataSnapshot) {
                            val dLat = oppSnap.child("destLat").getValue(Double::class.java)
                            val dLon = oppSnap.child("destLon").getValue(Double::class.java)
                            val dName = oppSnap.child("destName").getValue(String::class.java)

                            if (dLat != null && dLon != null) {
                                destinoState.value = LatLng(dLat, dLon)
                                destinoNameState.value = dName ?: "Meta"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    val destino = destinoState.value
    val destinoName = destinoNameState.value

    // 🔹 Lógica de distancia y declarar ganador
    LaunchedEffect(destino) {
        val meta = destino ?: return@LaunchedEffect

        while (true) {
            val results = FloatArray(1)

            android.location.Location.distanceBetween(
                currentMiUbi.latitude,
                currentMiUbi.longitude,
                meta.latitude,
                meta.longitude,
                results
            )

            val metros = results[0]

            if (!showWinDialog && !showLoseDialog && metros > 20f && currentSelectedUser != null) {
                Toast.makeText(
                    context,
                    "Estás a $metros metros de $destinoName",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Si llego a menos de 20m, intento declarar ganador
            if (metros < 20f && !raceFinished) {
                val uid = currentUid           // yo
                val opp = userId               // rival
                if (uid != null && opp != null) {
                    declareWinnerForBoth(
                        winnerUid = uid,
                        opponentUid = opp
                    )
                }
                // El diálogo no se muestra aquí; lo hace el listener cuando vea winnerId
                break
            }

            kotlinx.coroutines.delay(3000)
        }
    }

    // 🔹 Listener de resultado: cada usuario escucha su propio nodo /users/{miUid}/challenge
    DisposableEffect(currentUid) {
        val uid = currentUid

        if (uid == null) {
            onDispose { }
        } else {
            val ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("challenge")

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val winnerId = snapshot.child("winnerId").getValue(String::class.java)

                    if (winnerId != null && !raceFinished) {
                        raceFinished = true

                        if (winnerId == uid) {
                            // Yo soy el ganador
                            showWinDialog = true
                            showLoseDialog = false
                        } else {
                            // Yo soy el perdedor
                            showLoseDialog = true
                            showWinDialog = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("SEGUI", "Error escuchando challenge: ${error.message}")
                }
            }

            ref.addValueEventListener(listener)

            onDispose {
                ref.removeEventListener(listener)
            }
        }
    }

    // 🔹 Ruta desde MI ubicación hasta la meta
    LaunchedEffect(destino, currentMiUbi) {
        val meta = destino ?: return@LaunchedEffect
        if (directionsKey.isBlank()) {
            Toast.makeText(context, "API key vacía.", Toast.LENGTH_SHORT).show()
            return@LaunchedEffect
        }

        val pts = withContext(Dispatchers.IO) {
            DirectionsRepo.fetchRoutePoints(
                origin = currentMiUbi,
                dest = meta,
                apiKey = directionsKey
            )
        }

        myRoutePoints.clear()
        myRoutePoints.addAll(pts)

        if (pts.isEmpty()) {
            Toast.makeText(context, "No se pudo obtener la ruta (tú → meta).", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔹 Ruta desde la UBICACIÓN DEL RIVAL hasta la meta
    LaunchedEffect(destino, currentUbiUser) {
        val meta = destino ?: return@LaunchedEffect
        if (directionsKey.isBlank()) {
            Toast.makeText(context, "API key vacía.", Toast.LENGTH_SHORT).show()
            return@LaunchedEffect
        }

        val pts = withContext(Dispatchers.IO) {
            DirectionsRepo.fetchRoutePoints(
                origin = currentUbiUser,
                dest = meta,
                apiKey = directionsKey
            )
        }

        opponentRoutePoints.clear()
        opponentRoutePoints.addAll(pts)

        if (pts.isEmpty()) {
            Toast.makeText(context, "No se pudo obtener la ruta (rival → meta).", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔹 Mapa
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraState
    ) {

        // Mi ubicación
        Marker(
            state = rememberUpdatedMarkerState(miUbi),
            title = "Mi ubicación",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )

        // Rival
        Marker(
            state = rememberUpdatedMarkerState(ubiUser),
            title = name,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
        )

        // Meta
        destino?.let { meta ->
            Marker(
                state = rememberUpdatedMarkerState(meta),
                title = destinoName,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        }

        // Ruta MI → META
        if (myRoutePoints.isNotEmpty()) {
            Polyline(
                points = myRoutePoints.toList(),
                width = 10f,
                color = Color(0xFF2196F3)   // azul
            )
        }

        // Ruta RIVAL → META
        if (opponentRoutePoints.isNotEmpty()) {
            Polyline(
                points = opponentRoutePoints.toList(),
                width = 10f,
                color = Color(0xFFFF5722)   // naranja
            )
        }
    }

    // 🔹 Diálogo ganador
    if (showWinDialog) {
        AlertDialog(
            onDismissRequest = { /* opcional: no permitir cerrar tocando fuera */ },
            title = { Text("¡Has ganado! 🏁") },
            text = {
                Text("Has llegado a la meta. Reclama tus puntos de recompensa.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authVm.addPoints(500)

                        navController.navigate(AppScreens.Home.name) {
                            popUpTo(AppScreens.Home.name) { inclusive = true }
                        }
                    }
                ) {
                    Text("Reclamar recompensas")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWinDialog = false }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }

    // 🔹 Diálogo perdedor
    if (showLoseDialog) {
        AlertDialog(
            onDismissRequest = { /* puedes forzar a usar el botón */ },
            title = { Text("Has perdido 😢") },
            text = {
                Text("Tu oponente llegó primero a la meta.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLoseDialog = false
                        navController.navigate(AppScreens.Home.name) {
                            popUpTo(AppScreens.Home.name) { inclusive = true }
                        }
                    }
                ) {
                    Text("Volver al inicio")
                }
            }
        )
    }
}


fun declareWinnerForBoth(
    winnerUid: String,
    opponentUid: String
) {
    val db = FirebaseDatabase.getInstance()

    val myRef = db.getReference("users")
        .child(winnerUid)
        .child("challenge")
        .child("winnerId")

    val oppRef = db.getReference("users")
        .child(opponentUid)
        .child("challenge")
        .child("winnerId")

    listOf(myRef, oppRef).forEach { ref ->
        ref.runTransaction(object : com.google.firebase.database.Transaction.Handler {
            override fun doTransaction(currentData: com.google.firebase.database.MutableData)
                    : com.google.firebase.database.Transaction.Result {

                val existing = currentData.getValue(String::class.java)
                if (existing != null) {
                    // Ya hay un ganador, no sobrescribimos
                    return com.google.firebase.database.Transaction.success(currentData)
                }
                currentData.value = winnerUid
                return com.google.firebase.database.Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    Log.e("SEGUI", "Error declarando ganador: ${error.message}")
                }
            }
        })
    }
}
