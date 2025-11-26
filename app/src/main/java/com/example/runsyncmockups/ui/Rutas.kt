package com.example.runsyncmockups.ui

import BottomBarView
import android.Manifest
import android.R.attr.onClick
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.R
import com.example.runsyncmockups.api.DirectionsRepo
import com.example.runsyncmockups.model.LocationViewModel
import com.example.runsyncmockups.model.MyMarker
import com.example.runsyncmockups.model.UserAuthViewModel
import com.example.runsyncmockups.ui.components.DashboardCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.ktx.utils.collection.addMarker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import java.util.Locale


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun LocationScreen(vm: LocationViewModel = viewModel(), userVm: UserAuthViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val historial = remember { mutableStateListOf<LatLng>() }
    val LocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    val LocationPermissionState = rememberPermissionState(LocationPermission)
    var showRationale by remember { mutableStateOf(false) }
    var showScreen by remember { mutableStateOf(false)}

    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setWaitForAccurateLocation(true)
            .build()
    }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher para permiso de audio
    val requestAudioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Se necesita permiso de micrófono", Toast.LENGTH_SHORT).show()
        }
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val curr = LatLng(loc.latitude, loc.longitude)
                if (historial.isNotEmpty()) {
                    val previo = historial.last()
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(
                        previo.latitude, previo.longitude,
                        curr.latitude, curr.longitude,
                        results
                    )
                }
                historial.add(curr)
                Log.i("LocationApp", "lat=${loc.latitude}, lon=${loc.longitude}")
                vm.update(loc.latitude, loc.longitude)
            }
        }
    }

    LaunchedEffect(LocationPermissionState.status) {
        if(LocationPermissionState.status.isGranted){
            showRationale = false
            showScreen = true
        } else if(LocationPermissionState.status.shouldShowRationale){
            showRationale = true
            showScreen = false
        } else{
            LocationPermissionState.launchPermissionRequest()
            showScreen = false
        }
    }


    DisposableEffect(LocationPermissionState.status) {
        if (LocationPermissionState.status.isGranted) {
            startLocationUpdatesIfGranted(
                locationClient, locationRequest, locationCallback, context
            )
        }
        onDispose { locationClient.removeLocationUpdates(locationCallback) }
    }

    if (showScreen){
        PantallaRutas(
            navController = navController,
            viewModel = vm,
            hasAudioPermission = hasAudioPermission,
            onRequestAudioPermission = {
                requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }, userVm = userVm
        )
    }


    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Permiso de Ubicación") },
            text = { Text("Necesitamos los permisos de ubicación para mostrar tu ubicación.") },
            confirmButton = {
                TextButton(onClick = {
                    showRationale = false
                    LocationPermissionState.launchPermissionRequest()
                }) { Text("Conceder") }
            },
            dismissButton = {
                TextButton(onClick = { showRationale = false }) { Text("Cancelar") }
            }
        )


    }

}


private fun startLocationUpdatesIfGranted(
    client: FusedLocationProviderClient,
    request: LocationRequest,
    callback: LocationCallback,
    context: android.content.Context
) {
    if (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }
}



//navController: NavController,
//    viewModel: LocationViewModel = viewModel(),
//    hasAudioPermission: Boolean,
//    onRequestAudioPermission: () -> Unit
@Composable
fun PantallaRutas(navController: NavController,viewModel: LocationViewModel,hasAudioPermission: Boolean, onRequestAudioPermission: () -> Unit ,userVm: UserAuthViewModel = viewModel()) {

    var place by remember { mutableStateOf("") }
    val sensorManager =
        LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)



    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val markers by viewModel.markers.collectAsState()
    val LocActual = LatLng(state.latitude, state.longitude)
    userVm.updateLocActual(LocActual)
    val lightMapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.default_map)
    val darkMapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.night_map)
    var currentMapStyle by remember { mutableStateOf(lightMapStyle) }
    val sitios = remember {
        mutableStateListOf(
            MyMarker(LatLng(4.60583, -74.05639), "Cerro de Monserrate"),
            MyMarker(LatLng(4.59806, -74.07609), "Plaza de Bolívar"),
            MyMarker(LatLng(4.60111, -74.07306), "Museo del Oro"),
            MyMarker(LatLng(4.59889, -74.07278), "La Candelaria (Centro Histórico)"),
            MyMarker(LatLng(4.59667, -74.07333), "Museo Botero"),
            MyMarker(LatLng(4.65806, -74.09389), "Parque Metropolitano Simón Bolívar"),
            MyMarker(LatLng(4.66833, -74.10028), "Jardín Botánico José Celestino Mutis"),
            MyMarker(LatLng(4.59725, -74.06975), "Chorro de Quevedo"),
            MyMarker(LatLng(4.70333, -74.02667), "Usaquén (Parque y Mercado)"),
            MyMarker(LatLng(4.61333, -74.07222), "Torre Colpatria"),
            MyMarker(LatLng(4.61558, -74.06817), "Museo Nacional de Colombia"),
            MyMarker(LatLng(4.59667, -74.07444), "Teatro Colón"),
            MyMarker(LatLng(4.59972, -74.06639), "Quinta de Bolívar"),
            MyMarker(LatLng(4.63722, -74.05556), "Zona G (Corredor Gastronómico)"),
            MyMarker(LatLng(4.66583, -74.05333), "Zona T"),
            MyMarker(LatLng(4.67694, -74.04861), "Parque de la 93"),
            MyMarker(LatLng(5.01889, -74.00917), "Catedral de Sal de Zipaquirá"),
            MyMarker(LatLng(4.64250, -74.05056), "Ciclovía Dominical (Av. Cr. 7 con Cl. 72)"),
            MyMarker(LatLng(4.65056, -74.05278), "Sendero Quebrada La Vieja"),
            MyMarker(LatLng(4.61917, -74.08389), "Plaza de Mercado de Paloquemao"),
            MyMarker(LatLng(4.68806, -73.97806), "Mirador de La Calera (Vía a La Calera)"),
            MyMarker(LatLng(4.61222, -74.06889), "Planetario de Bogotá"),
            MyMarker(LatLng(4.65528, -74.10944), "Maloka (Museo Interactivo)"),
            MyMarker(LatLng(4.59944, -74.07306), "Santuario de Nuestra Señora del Carmen"),
            MyMarker(LatLng(4.59200, -74.05444), "Cerro de Guadalupe")
        )
    }



    val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                val lux = event.values[0]
                Log.i("MapApp", lux.toString())
                currentMapStyle = if (lux < 3000) darkMapStyle else lightMapStyle
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
// Handle accuracy changes if needed
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(
            sensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    val actualMarkerState = rememberUpdatedMarkerState(position = LocActual)

    val searchMarker = rememberUpdatedMarkerState()
    var searchMarkerTitle = remember { mutableStateOf("") }



    val routePoints = remember { mutableStateListOf<LatLng>() }

    val scope = rememberCoroutineScope()



    val directionsKey = context.getString(R.string.google_directions_key)



    val pendingDest by viewModel.pendingRouteTo.collectAsState()

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.get(0).toString()
            place = spokenText
            // Buscar automáticamente después de reconocer la voz
            searchLocation(place, context , LocActual, viewModel, scope , directionsKey, routePoints)
        }
    }

    LaunchedEffect(pendingDest) {
        val dest = pendingDest ?: return@LaunchedEffect
        if (directionsKey.isBlank()) {
            Toast.makeText(context, "API key vacía.", Toast.LENGTH_SHORT).show()
            return@LaunchedEffect
        }
        val pts = withContext(Dispatchers.IO) {
            DirectionsRepo.fetchRoutePoints(
                origin = LocActual,
                dest = dest,
                apiKey = directionsKey
            )
        }
        routePoints.clear()
        routePoints.addAll(pts)
        if (pts.isEmpty()) {
            Toast.makeText(context, "No se pudo obtener la ruta.", Toast.LENGTH_SHORT).show()
        }

        viewModel.clearPendingRoute()

    }
    Scaffold(
        bottomBar = {BottomBarView(navController)},
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.clearMarkers()
                    routePoints.clear()
                },
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFFFF5722),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar marcadores")
            }
        }, floatingActionButtonPosition = FabPosition.Start
    )

    { padding ->

        val cameraPositionState = key(LocActual) {
            rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LocActual, 18f)
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapStyleOptions = currentMapStyle, isMyLocationEnabled = true),
            )
            {
               /* Marker(
                    state = actualMarkerState,
                    title = "Actual",
                    snippet = "Posición Actual",
                    //icon = BitmapDescriptorFactory.fromResource(R.drawable.marcador)
                ) */
                markers.forEach {

                    Marker(
                        state = rememberUpdatedMarkerState(it.position),
                        title = it.title,
                    )

                    if (routePoints.isNotEmpty()) {
                        Polyline(points = routePoints.toList(), width = 12f)
                    }

                }

                sitios.forEach {
                    Marker(
                        state = rememberUpdatedMarkerState(it.position),
                        title = it.title,
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.marcador)
                    )
                }
            }

            TextField(
                value = place,
                onValueChange = {place = it},
                label = {Text("Place")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 48.dp, end = 16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        Log.i("MapApp", place)
                        val location = findLocation(place, context)
                        location?.let{
                            searchMarker.position = location
                            searchMarkerTitle.value =place
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(location, 18F)
                            viewModel.replaceWith(MyMarker(location, place))

                            val results = FloatArray(1)
                            android.location.Location.distanceBetween(
                                LocActual.latitude,
                                LocActual.longitude,
                                location.latitude,
                                location.longitude,
                                results
                            )
                            val metros = results[0]
                            Toast.makeText(context, "Estas a : $metros metros de $place", Toast.LENGTH_SHORT).show()

                            scope.launch {
                                if (directionsKey.isBlank()) {
                                    Toast.makeText(context, "API key vacía.", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                val pts = withContext(Dispatchers.IO) {
                                    DirectionsRepo.fetchRoutePoints(
                                        origin = LocActual,
                                        dest = location,
                                        apiKey = directionsKey
                                    )
                                }
                                routePoints.clear()
                                routePoints.addAll(pts)
                                if (pts.isEmpty()) {
                                    Toast.makeText(context, "No se pudo obtener la ruta.", Toast.LENGTH_SHORT).show()
                                }
                                searchLocation(place, context , LocActual, viewModel, scope , directionsKey, routePoints)
                            }


                        }
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.transparentFocused),
                    unfocusedContainerColor = colorResource(R.color.transparentUnfocused),
                    focusedLabelColor = colorResource(R.color.black),
                    unfocusedLabelColor = colorResource(R.color.transparentWhite),
                    focusedTextColor = Color.Black
                ),  trailingIcon = {
                    IconButton(
                        onClick = {
                            if (hasAudioPermission) {
                                getSpeechInput(context, speechLauncher)
                            } else {
                                onRequestAudioPermission()
                            }


                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Buscar por voz",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                })
        }

    }
}

fun findLocation(address : String, context: Context):LatLng?{
    var geocoder = Geocoder(context)
    val addresses = geocoder.getFromLocationName(address, 2)
    if(addresses != null && !addresses.isEmpty()){
        val addr = addresses.get(0)
        val location = LatLng(addr.
        latitude, addr.
        longitude)
        return location
    }
    return null
}
private fun searchLocation(
    place: String,
    context: Context,
    currentLocation: LatLng,
    viewModel: LocationViewModel,
    scope: CoroutineScope,
    directionsKey: String,
    routePoints: androidx.compose.runtime.snapshots.SnapshotStateList<LatLng>
) {
    val location = findLocation(place, context)
    location?.let {
        viewModel.replaceWith(MyMarker(location, place))

        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            location.latitude,
            location.longitude,
            results
        )
        val metros = results[0]
        Toast.makeText(context, "Estás a : $metros metros de $place", Toast.LENGTH_SHORT).show()

        scope.launch {
            if (directionsKey.isBlank()) {
                Toast.makeText(context, "API key vacía.", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val pts = withContext(Dispatchers.IO) {
                DirectionsRepo.fetchRoutePoints(
                    origin = currentLocation,
                    dest = location,
                    apiKey = directionsKey
                )
            }
            routePoints.clear()
            routePoints.addAll(pts)
            if (pts.isEmpty()) {
                Toast.makeText(context, "No se pudo obtener la ruta.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun getSpeechInput(context: Context, launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        Toast.makeText(context, "Reconocimiento no disponible", Toast.LENGTH_SHORT).show()
    } else {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿A dónde quieres ir?")
        launcher.launch(intent)
    }
}

@Preview
@Composable
fun PreviewPantallaRutas(){
    val navController = rememberNavController()
    //PantallaRutas(navController)
}