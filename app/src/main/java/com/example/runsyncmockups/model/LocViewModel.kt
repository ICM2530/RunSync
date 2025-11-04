package com.example.runsyncmockups.model

import android.graphics.drawable.Icon
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


data class MyMarker(val position: LatLng, val title: String = "Marker", val icon: Icon? = null)
data class LocationState(
    val latitude : Double =0.0,
    val longitude : Double =0.0
)

class LocationViewModel : ViewModel(){
    private val _markers = MutableStateFlow<List<MyMarker>>(emptyList())

    val markers: StateFlow<List<MyMarker>> = _markers
    private val _uiState = MutableStateFlow(LocationState())
    val state : StateFlow<LocationState> = _uiState

    private val _pendingRouteTo = MutableStateFlow<LatLng?>(null)
    val pendingRouteTo: StateFlow<LatLng?> = _pendingRouteTo
    fun update(lat : Double, long : Double){
        _uiState.update { it.copy(lat, long) }
    }

    fun addMarker(m : MyMarker){
        _markers.value = _markers.value + m
        _pendingRouteTo.value = m.position
    }

    fun clearPendingRoute() { _pendingRouteTo.value = null }

    fun replaceWith(marker: MyMarker) {
        _markers.value = listOf(marker)
        _pendingRouteTo.value = marker.position
    }

    fun clearMarkers() {
        _markers.value = emptyList()
    }

}



