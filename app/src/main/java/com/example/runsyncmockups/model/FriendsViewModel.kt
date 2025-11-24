    package com.example.runsyncmockups.model

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.ListenerRegistration
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.tasks.await

    data class Friend(
        val id: String = "",
        val nombre: String = "",
        val email: String = "",
        val profileImageUrl: String? = null
    )

    data class FriendsState(
        val friends: List<Friend> = emptyList(),
        val pendingRequests: List<Friend> = emptyList(),
        val searchError: String = "",
        val isLoading: Boolean = false
    )

    class FriendsViewModel : ViewModel() {
        private val _friendsState = MutableStateFlow(FriendsState())
        val friendsState: StateFlow<FriendsState> = _friendsState.asStateFlow()

        private val auth = FirebaseAuth.getInstance()
        private val firestore = FirebaseFirestore.getInstance()
        private var friendsListener: ListenerRegistration? = null

        init {
            loadFriends()
        }

        // Cargar amigos desde Firebase en tiempo real
        private fun loadFriends() {
            val currentUserId = auth.currentUser?.uid ?: return

            _friendsState.value = _friendsState.value.copy(isLoading = true)

            // Listener en tiempo real para los amigos
            friendsListener = firestore.collection("users")
                .document(currentUserId)
                .collection("friends")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        _friendsState.value = _friendsState.value.copy(
                            isLoading = false,
                            searchError = "Error al cargar amigos"
                        )
                        return@addSnapshotListener
                    }

                    val friendsList = mutableListOf<Friend>()
                    snapshot?.documents?.forEach { doc ->
                        val friend = Friend(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            email = doc.getString("email") ?: "",
                            profileImageUrl = doc.getString("profileImageUrl")
                        )
                        friendsList.add(friend)
                    }

                    _friendsState.value = _friendsState.value.copy(
                        friends = friendsList,
                        isLoading = false
                    )
                }
        }

        // Buscar usuario por email en Firebase
        fun searchUserByEmail(email: String, onResult: (Friend?) -> Unit) {
            viewModelScope.launch {
                try {
                    if (email.isBlank()) {
                        _friendsState.value = _friendsState.value.copy(searchError = "Ingresa un email")
                        onResult(null)
                        return@launch
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        _friendsState.value = _friendsState.value.copy(searchError = "Email inválido")
                        onResult(null)
                        return@launch
                    }

                    val currentUserEmail = auth.currentUser?.email
                    if (email == currentUserEmail) {
                        _friendsState.value = _friendsState.value.copy(searchError = "No puedes agregarte a ti mismo")
                        onResult(null)
                        return@launch
                    }

                    // Buscar en Realtime Database
                    val db = com.google.firebase.database.FirebaseDatabase.getInstance()
                    val ref = db.getReference("users")

                    ref.get().addOnSuccessListener { snapshot ->
                        var foundFriend: Friend? = null

                        for (userSnap in snapshot.children) {
                            val userEmail = userSnap.child("email").getValue(String::class.java)
                            if (userEmail == email) {
                                foundFriend = Friend(
                                    id = userSnap.key ?: "",
                                    nombre = userSnap.child("name").getValue(String::class.java) ?: "",
                                    email = userEmail,
                                    profileImageUrl = userSnap.child("profileImage").getValue(String::class.java)
                                )
                                break
                            }
                        }

                        if (foundFriend == null) {
                            _friendsState.value = _friendsState.value.copy(searchError = "Usuario no encontrado")
                        } else {
                            _friendsState.value = _friendsState.value.copy(searchError = "")
                        }

                        onResult(foundFriend)
                    }
                } catch (e: Exception) {
                    _friendsState.value = _friendsState.value.copy(searchError = "Error: ${e.message}")
                    onResult(null)
                }
            }
        }


        // Agregar amigo a Firebase
        fun addFriend(friend: Friend) {
            viewModelScope.launch {
                try {
                    val currentUserId = auth.currentUser?.uid ?: return@launch

                    // Agregar a la colección de amigos del usuario actual
                    firestore.collection("users")
                        .document(currentUserId)
                        .collection("friends")
                        .document(friend.id)
                        .set(
                            hashMapOf(
                                "nombre" to friend.nombre,
                                "email" to friend.email,
                                "profileImageUrl" to friend.profileImageUrl,
                                "addedAt" to com.google.firebase.Timestamp.now()
                            )
                        )
                        .await()

                    // También agregar al usuario actual como amigo del otro usuario
                    firestore.collection("users")
                        .document(friend.id)
                        .collection("friends")
                        .document(currentUserId)
                        .set(
                            hashMapOf(
                                "nombre" to (auth.currentUser?.displayName ?: "Usuario"),
                                "email" to (auth.currentUser?.email ?: ""),
                                "profileImageUrl" to auth.currentUser?.photoUrl?.toString(),
                                "addedAt" to com.google.firebase.Timestamp.now()
                            )
                        )
                        .await()

                } catch (e: Exception) {
                    _friendsState.value = _friendsState.value.copy(
                        searchError = "Error al agregar amigo: ${e.message}"
                    )
                }
            }
        }

        // Eliminar amigo de Firebase
        fun removeFriend(friendId: String) {
            viewModelScope.launch {
                try {
                    val currentUserId = auth.currentUser?.uid ?: return@launch

                    // Eliminar de la colección de amigos del usuario actual
                    firestore.collection("users")
                        .document(currentUserId)
                        .collection("friends")
                        .document(friendId)
                        .delete()
                        .await()

                    // También eliminar al usuario actual de los amigos del otro usuario
                    firestore.collection("users")
                        .document(friendId)
                        .collection("friends")
                        .document(currentUserId)
                        .delete()
                        .await()

                } catch (e: Exception) {
                    _friendsState.value = _friendsState.value.copy(
                        searchError = "Error al eliminar amigo: ${e.message}"
                    )
                }
            }
        }

        fun getFriendsCount(): Int {
            return _friendsState.value.friends.size
        }

        fun getFriendsList(): List<Friend> {
            return _friendsState.value.friends
        }

        fun clearSearchError() {
            _friendsState.value = _friendsState.value.copy(searchError = "")
        }

        override fun onCleared() {
            super.onCleared()
            friendsListener?.remove()
        }
    }