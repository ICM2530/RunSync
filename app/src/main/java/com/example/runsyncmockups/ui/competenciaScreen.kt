package com.example.runsyncmockups.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
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

@SuppressLint("MissingPermission")
@Composable
fun SeguimientoScreen(
    name: String,
    userId: String?,
    navController: NavController,
    locVm: LocationViewModel,
    authVm: UserAuthViewModel,
    myUsersVm: MyUsersViewModel //PARAMETROS
) {
    val context = LocalContext.current

    LaunchedEffect(true) {
        val client = LocationServices.getFusedLocationProviderClient(context) //
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, //SOLICITUD DE ACTUALIZACION CADA 2 SEG
            2000
        ).setWaitForAccurateLocation(true).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                // Actualiza estado del ViewModel compartido
                locVm.update(loc.latitude, loc.longitude) // GUARDA LA UBI EN EL VIEW MODEL

                authVm.updateLocActual(
                    LatLng(loc.latitude, loc.longitude)) // GUARDA LA UBI ACTUAL EN EL USUARIO PARA ACTUALIZAR
            }
        }
        //PERMSOS DE LOCALIZACION Y ACTUALIZACION
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        }
    }

    // Se obtiene la ubicación actual desde el mismo ViewModel que usa PantallaMapa
    val state by locVm.state.collectAsState()

    val miUbi = LatLng(state.latitude, state.longitude)
    authVm.updateLocActual(miUbi)
    val users by myUsersVm.users.collectAsState()
    //UBICACION DEL USUARIO QUE ESTA DISPONIBLE SEGUN LA NOTIFICACIÓN
    val selectedUser = users.firstOrNull { it.id == userId }

    val ubiUser = remember(selectedUser?.lat, selectedUser?.lon) {
        LatLng(
            selectedUser?.lat ?: 0.0, // POR DEFAULT 0,0, SI NO EXISTE
            selectedUser?.lon ?: 0.0
        )
    }

    //degus mios
    Log.d("SEGUI", "Mi ubicación REAL = $miUbi")
    Log.d("SEGUI", "Ubicación usuario = $ubiUser")

    // Cámara apuntando al usuario disponible
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubiUser, 16f)
    }
    val currentMiUbi by rememberUpdatedState(miUbi)
    val currentUbiUser by rememberUpdatedState(ubiUser)
    val currentSelectedUser by rememberUpdatedState(selectedUser) //MANTIENE VALORES ACTUALIZADOS EN EL LAUNCH



    val destinoState = remember { mutableStateOf<LatLng?>(null) }
    val destinoNameState = remember { mutableStateOf("Meta") }
    val currentUid = firebaseAuth.uid
    LaunchedEffect(currentUid, userId) {
        if (currentUid == null || userId == null) return@LaunchedEffect

        val db = FirebaseDatabase.getInstance()

        // 1️⃣ Primero intento leer el challenge desde MI usuario (caso: soy el retado)
        val myRef = db.getReference("users")
            .child(currentUid)
            .child("challenge")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(mySnap: DataSnapshot) {
                val destLat = mySnap.child("destLat").getValue(Double::class.java)
                val destLon = mySnap.child("destLon").getValue(Double::class.java)
                val destName = mySnap.child("destName").getValue(String::class.java)

                if (destLat != null && destLon != null) {
                    // ✅ Caso: soy el retado, aquí está la meta
                    destinoState.value = LatLng(destLat, destLon)
                    destinoNameState.value = destName ?: "Meta"
                } else {
                    // 2️⃣ Si en mí no hay challenge con destino, lo busco en el OPONENTE (caso: soy el retador)
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

            if (metros > 5f && currentSelectedUser != null) {
                Toast.makeText(
                    context,
                    "Estás a $metros metros de $destinoName",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (metros < 10f){

            }
            kotlinx.coroutines.delay(3000)
        }
    }


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraState
    ) {

        // UBICACIÓN DEL USUARIO ACTUAL
        Marker(
            state = rememberUpdatedMarkerState(miUbi),
            title = "Mi ubicación",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        )

        //UBICACIÓN DEL USUARIO DISPONIBLE
        Marker(
            state = rememberUpdatedMarkerState(ubiUser),
            title = name,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
        )

        destino?.let { meta ->
            Marker(
                state = rememberUpdatedMarkerState(meta),
                title = destinoName,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        }


    }
}
