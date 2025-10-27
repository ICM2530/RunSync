package com.example.runsyncmockups.ui

import BottomBarView
import android.Manifest
import android.R.attr.onClick
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.runsyncmockups.ui.components.DashboardCard
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
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




@Composable
fun LocationScreen(vm: LocationViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity // Necesitamos la Actividad para el rationale

    // --- (El resto de tus inicializaciones se mantienen igual: locationClient, locationRequest, etc.) ---
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setWaitForAccurateLocation(true)
            .build()
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                Log.i("LocationApp", "lat=${loc.latitude}, lon=${loc.longitude}")
                vm.update(loc.latitude, loc.longitude)
            }
        }
    }

    // --- LANZADOR DE PERMISOS ---
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            hasPermission = true
            // El permiso fue concedido, iniciamos las actualizaciones
            startLocationUpdatesIfGranted(locationClient, locationRequest, locationCallback, context)
        } else {
            // El permiso fue denegado
            hasPermission = false
        }
    }

    // --- LÓGICA DE COMPOSICIÓN ---

    if (hasPermission) {
        // Si tenemos permiso, mostramos la pantalla del mapa
        PantallaRutas(navController = navController, viewModel = vm)
    } else {
        // Si no tenemos permiso, gestionamos la UI para solicitarlo
        PermissionRationaleScreen(
            onPermissionRequested = {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        )
    }

    // --- EFECTOS PARA GESTIONAR EL CICLO DE VIDA ---
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    DisposableEffect(hasPermission) {
        if (hasPermission) {
            startLocationUpdatesIfGranted(locationClient, locationRequest, locationCallback, context)
        }
        onDispose {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }
}




@SuppressLint("ContextCastToActivity")
@Composable
fun PermissionRationaleScreen(onPermissionRequested: () -> Unit) {
    val activity = LocalContext.current as Activity
    val shouldShowRationale = remember {
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val title: String
    val contentText: String

    if (shouldShowRationale) {
        // El usuario ya ha denegado el permiso al menos una vez.
        // Mostramos una explicación detallada.
        title = "Permiso Requerido"
        contentText = "Para mostrarte tu posición en el mapa y crear rutas, necesitamos acceder a tu ubicación. Por favor, considera conceder el permiso."
    } else {
        // Es la primera vez que pedimos el permiso, o el usuario marcó "No volver a preguntar".
        title = "PERMISOS DE LOCALIZACIÓN"
        contentText = "Necesitamos tu permiso para acceder a tu ubicación y activar las funciones del mapa."
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DashboardCard(
            title = title,
            content = {
                Text(contentText)
            },
            buttonText = "Conceder permiso",
            onClick = onPermissionRequested // Llama a la función para lanzar la petición
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



@Composable
fun PantallaRutas(navController: NavController,viewModel: LocationViewModel = viewModel()){

    var place by remember { mutableStateOf("") }
    val sensorManager =
        LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val markers by viewModel.markers.collectAsState()
    val LocActual = LatLng(state.latitude, state.longitude)
    val lightMapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.default_map)
    val darkMapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.night_map)
    var currentMapStyle by remember { mutableStateOf(lightMapStyle) }


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
                properties = MapProperties(mapStyleOptions = currentMapStyle),
            )
            {
                Marker(
                    state = actualMarkerState,
                    title = "Actual",
                    snippet = "Posición Actual"
                )
                markers.forEach {
                    Marker(
                        state = rememberUpdatedMarkerState(it.position),
                        title = it.title,
                    )

                    if (routePoints.isNotEmpty()) {
                        Polyline(points = routePoints.toList(), width = 12f)
                    }

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
                )
            )
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



@Preview
@Composable
fun PreviewPantallaRutas(){
    val navController = rememberNavController()
    //PantallaRutas(navController)
}