package com.example.runsyncmockups.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class SavedRoute(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val poiNotes: String = "",
    val destTitle: String = "",
    val destLat: Double = 0.0,
    val destLng: Double = 0.0,
    val createdAt: Long = 0L,
    val createdBy: String = ""
)


data class SaveRouteUiState(
    val saving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)


class RoutesRepository(
    private val db: DatabaseReference =
        FirebaseDatabase.getInstance().reference,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun routesRefFor(uid: String) = db.child("routes").child(uid)

    suspend fun createRoute(route: SavedRoute) {
        val uid = auth.currentUser?.uid ?: error("Usuario no autenticado")
        val id = if (route.id.isBlank())
            routesRefFor(uid).push().key ?: UUID.randomUUID().toString()
        else route.id

        val payload = route.copy(
            id = id,
            createdAt = System.currentTimeMillis(),
            createdBy = uid
        )

        routesRefFor(uid).child(id).setValue(payload).await()
    }
}


class SaveRouteViewModel(
    private val repo: RoutesRepository = RoutesRepository(),
    private val locationVm: LocationViewModel
) : ViewModel() {

    var ui by mutableStateOf(SaveRouteUiState())
        private set

    fun save(name: String, description: String, poiNotes: String) {
        val lastMarker = locationVm.markers.value.lastOrNull()
            ?: run {
                ui = ui.copy(error = "No hay destino seleccionado")
                return
            }

        if (name.isBlank()) {
            ui = ui.copy(error = "El nombre del destino es obligatorio")
            return
        }

        viewModelScope.launch {
            ui = ui.copy(saving = true, error = null, saved = false)
            try {
                val route = SavedRoute(
                    name = name.trim(),
                    description = description.trim(),
                    poiNotes = poiNotes.trim(),
                    destTitle = lastMarker.title,
                    destLat = lastMarker.position.latitude,
                    destLng = lastMarker.position.longitude
                )
                repo.createRoute(route)
                ui = ui.copy(saving = false, saved = true)
            } catch (e: Exception) {
                ui = ui.copy(saving = false, error = e.message ?: "Error al guardar")
            }
        }
    }

    fun resetSavedFlag() {
        ui = ui.copy(saved = false)
    }
}