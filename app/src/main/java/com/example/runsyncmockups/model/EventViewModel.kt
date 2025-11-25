package com.example.runsyncmockups.model

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

data class Event(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val place: String = "",
    val date: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val attendeeCount: Int = 0
)

data class SaveEventState(
    val saving: Boolean = false,
    val savedId: String? = null,
    val error: String? = null
)

data class EventListUiState(
    val loading: Boolean = true,
    val items: List<Event> = emptyList(),
    val error: String? = null
)

class EventRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
) {

    private val eventsRef get() = rootRef.child("events")
    private val attendeesRef get() = rootRef.child("event_attendees")

    suspend fun createEvent(
        title: String,
        description: String,
        place: String,
        date: String
    ): Result<String> = runCatching {
        val uid = auth.currentUser?.uid ?: error("No autenticado")
        val key = eventsRef.push().key ?: error("No se pudo generar ID")

        val e = Event(
            id = key,
            userId = uid,
            title = title.trim(),
            description = description.trim(),
            place = place.trim(),
            date = date,
            createdAt = System.currentTimeMillis()
        )
        eventsRef.child(key).setValue(e).await()
        key
    }

    fun listenMyEvents(): Flow<List<Event>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) { trySend(emptyList()); close(); return@callbackFlow }

        // Filtra por dueño y ordena por fecha de inicio (próximos primero)
        val q = eventsRef.orderByChild("userId").equalTo(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                val list = s.children.mapNotNull { it.getValue(Event::class.java) }
                    .sortedWith(compareBy<Event> { it.date }.thenByDescending { it.createdAt })
                trySend(list)
            }
            override fun onCancelled(e: DatabaseError) {
                trySend(emptyList()); close()
            }
        }
        q.addValueEventListener(listener)
        awaitClose { q.removeEventListener(listener) }
    }

    suspend fun deleteMyEvent(id: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error("No autenticado")
        val snap = eventsRef.child(id).get().await()
        val owner = snap.getValue(Event::class.java)?.userId
        require(owner == uid) { "Sin permiso" }
        eventsRef.child(id).removeValue().await()
    }

    /** ¿El usuario actual está inscrito a este evento? */
    fun listenIsRegistered(eventId: String): Flow<Boolean> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) { trySend(false); close(); return@callbackFlow }

        val ref = attendeesRef.child(eventId).child(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                trySend(s.exists())
            }
            override fun onCancelled(e: DatabaseError) {
                trySend(false); close(e.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /** Inscribirse al evento */
    suspend fun joinEvent(eventId: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error("No autenticado")
        val payload = mapOf(
            "userId" to uid,
            "joinedAt" to System.currentTimeMillis()
        )
        attendeesRef.child(eventId).child(uid).setValue(payload).await()
    }

    /** Cancelar inscripción */
    suspend fun leaveEvent(eventId: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error("No autenticado")
        attendeesRef.child(eventId).child(uid).removeValue().await()
    }
}

class EventViewModel(
    private val repo: EventRepository = EventRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(SaveEventState())
    val state = _state.asStateFlow()

    fun save(title: String, description: String, place: String, date: String) {
        if (title.isBlank() || place.isBlank() || date.isBlank() || description.isBlank()) {
            _state.value = SaveEventState(error = "Completa título, lugar y fecha/hora")
            return
        }
        viewModelScope.launch {
            _state.value = SaveEventState(saving = true)
            val res = repo.createEvent(title, description, place, date)
            _state.value = res.fold(
                onSuccess = { id -> SaveEventState(saving = false, savedId = id) },
                onFailure = { e -> SaveEventState(saving = false, error = e.message ?: "Error al guardar") }
            )
        }
    }
}

class EventListViewModel(
    private val repo: EventRepository = EventRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(EventListUiState())
    val state: StateFlow<EventListUiState> = _state

    init {
        viewModelScope.launch {
            repo.listenMyEvents()
                .onStart { _state.value = EventListUiState(loading = true) }
                .catch { e ->
                    _state.value = EventListUiState(loading = false, error = e.message ?: "Error cargando eventos")
                }
                .collect { list ->
                    _state.value = EventListUiState(loading = false, items = list)
                }
        }
    }
}