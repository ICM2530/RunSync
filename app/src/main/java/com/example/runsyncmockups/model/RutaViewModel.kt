package com.example.runsyncmockups.model

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Route(
    val id: String = "",
    val userId: String = "",
    val routeName: String = "",     // <- Título de la ruta (ej: "Ruta Monserrate")
    val destTitle: String = "",     // <- Nombre/dirección del destino (ej: "Monserrate")
    val description: String = "",   // Descripción general
    val poiNotes: String = "",      // Puntos de interés cercanos (texto)
    val createdAt: Long = System.currentTimeMillis()
)

class RouteRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
) {
    private val routesRef get() = rootRef.child("routes")

    suspend fun createRoute(
        routeName: String,
        destTitle: String,
        description: String,
        poiNotes: String,
    ): Result<String> = runCatching {
        val uid = auth.currentUser?.uid ?: error("No autenticado")
        val key = routesRef.push().key ?: error("No se pudo generar ID")

        val r = Route(
            id = key,
            userId = uid,
            routeName = routeName.trim(),
            destTitle = destTitle.trim(),
            description = description.trim(),
            poiNotes = poiNotes.trim(),
            createdAt = System.currentTimeMillis()
        )
        routesRef.child(key).setValue(r).await()
        key
    }

    fun listenMyRoutes(): Flow<List<Route>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }

        val q = routesRef.orderByChild("userId").equalTo(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                val list = s.children.mapNotNull { it.getValue(Route::class.java) }
                    .sortedByDescending { it.createdAt }
                trySend(list)
            }
            override fun onCancelled(e: DatabaseError) {
                trySend(emptyList()); close()
            }
        }
        q.addValueEventListener(listener)
        awaitClose { q.removeEventListener(listener) }
    }


    fun listenMyRouteCount(): Flow<Int> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) { trySend(0); close(); return@callbackFlow }
        val q = routesRef.orderByChild("userId").equalTo(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                trySend(s.children.count())
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(0); close()
            }
        }
        q.addValueEventListener(listener)
        awaitClose { q.removeEventListener(listener) }
    }


    suspend fun deleteMyRoute(id: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error("No autenticado")
        val snap = routesRef.child(id).get().await()
        val owner = snap.getValue(Route::class.java)?.userId
        require(owner == uid) { "Sin permiso" }
        routesRef.child(id).removeValue().await()
    }
}

data class SaveRouteState(
    val saving: Boolean = false,
    val savedId: String? = null,
    val error: String? = null
)

class RouteViewModel(
    private val routes: RouteRepository = RouteRepository(),
) : ViewModel() {
    private val _state = MutableStateFlow(SaveRouteState())
    val state = _state.asStateFlow()

    fun save(routeName: String, description: String, poiNotes: String, destTitle: String) {

        if(routeName.isEmpty() || description.isEmpty() || poiNotes.isEmpty() || destTitle.isEmpty()){
            _state.value = SaveRouteState(error = "Por favor, complete todos los campos")

            return
        }

        viewModelScope.launch {
            _state.value = SaveRouteState(saving = true)
            val res = routes.createRoute(
                routeName = routeName,
                destTitle = destTitle,
                description = description,
                poiNotes = poiNotes,
            )
            _state.value = res.fold(
                onSuccess = { id -> SaveRouteState(saving = false, savedId = id) },
                onFailure = { e -> SaveRouteState(saving = false, error = e.message ?: "Error") }
            )
        }
    }
}

data class RouteListUiState(
    val loading: Boolean = true,
    val items: List<Route> = emptyList(),
    val error: String? = null
)

class RouteListViewModel(
    private val repo: RouteRepository = RouteRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(RouteListUiState())
    val state: StateFlow<RouteListUiState> = _state

    init {
        viewModelScope.launch {
            repo.listenMyRoutes()
                .onStart { _state.value = RouteListUiState(loading = true) }
                .catch { e ->
                    _state.value = RouteListUiState(
                        loading = false,
                        error = e.message ?: "Error cargando rutas"
                    )
                }
                .collect { list ->
                    _state.value = RouteListUiState(
                        loading = false,
                        items = list,
                        error = null
                    )
                }
        }
    }
}