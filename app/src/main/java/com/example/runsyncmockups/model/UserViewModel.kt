package com.example.runsyncmockups.ui.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runsyncmockups.firebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class UserData(
    val name: String = "",
    val email: String = "",
    val profileImage: String = ""
)

data class UserStats(
    val races: Int = 0,
    val totalDistance: Double = 0.0,
    val totalTime: Double = 0.0,
    val avgSpeed: Double = 0.0,
    val totalCalories: Int = 0,
    val following: Int = 0
)

data class Post(
    val id: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val caption: String = ""
)

class UserViewModel : ViewModel() {
    private val _user = MutableStateFlow(UserData())
    val user: StateFlow<UserData> = _user.asStateFlow()

    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    private val _isUploadingPost = MutableStateFlow(false)
    val isUploadingPost: StateFlow<Boolean> = _isUploadingPost.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            val user = firebaseAuth.currentUser ?: return@launch
            val uid = user.uid

            val db = FirebaseDatabase.getInstance()
            val ref = db.getReference("users").child(uid)

            ref.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java) ?: "Usuario"
                    val email = snapshot.child("email").getValue(String::class.java) ?: ""
                    val imageUrl = snapshot.child("profileImage").getValue(String::class.java) ?: ""

                    _user.value = UserData(name, email, imageUrl)
                }
            }
        }
    }

    fun loadUserStats() {
        viewModelScope.launch {
            val user = firebaseAuth.currentUser ?: return@launch
            val uid = user.uid

            val db = FirebaseDatabase.getInstance()
            val ref = db.getReference("users").child(uid).child("stats")

            ref.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val races = snapshot.child("races").getValue(Int::class.java) ?: 0
                    val totalDistance = snapshot.child("totalDistance").getValue(Double::class.java) ?: 0.0
                    val totalTime = snapshot.child("totalTime").getValue(Double::class.java) ?: 0.0
                    val avgSpeed = snapshot.child("avgSpeed").getValue(Double::class.java) ?: 0.0
                    val totalCalories = snapshot.child("totalCalories").getValue(Int::class.java) ?: 0
                    val following = snapshot.child("following").getValue(Int::class.java) ?: 0

                    _userStats.value = UserStats(
                        races = races,
                        totalDistance = totalDistance,
                        totalTime = totalTime,
                        avgSpeed = avgSpeed,
                        totalCalories = totalCalories,
                        following = following
                    )
                }
            }
        }
    }

    fun loadUserPosts() {
        viewModelScope.launch {
            val user = firebaseAuth.currentUser ?: return@launch
            val uid = user.uid

            val db = FirebaseDatabase.getInstance()
            val ref = db.getReference("users").child(uid).child("posts")

            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val posts = mutableListOf<Post>()
                    snapshot.children.forEach { postSnapshot ->
                        val id = postSnapshot.key ?: ""
                        val imageUrl = postSnapshot.child("imageUrl").getValue(String::class.java) ?: ""
                        val timestamp = postSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                        val caption = postSnapshot.child("caption").getValue(String::class.java) ?: ""

                        posts.add(Post(id, imageUrl, timestamp, caption))
                    }
                    // Ordenar por timestamp descendente (más recientes primero)
                    _userPosts.value = posts.sortedByDescending { it.timestamp }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar error
                }
            })
        }
    }

    fun uploadProfileImage(imageUri: Uri, onComplete: (Boolean) -> Unit) {
        val user = firebaseAuth.currentUser ?: return
        val uid = user.uid

        _isUploadingImage.value = true

        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    dbRef.child("profileImage").setValue(imageUrl)
                        .addOnSuccessListener {
                            _user.value = _user.value.copy(profileImage = imageUrl)
                            _isUploadingImage.value = false
                            onComplete(true)
                        }
                        .addOnFailureListener {
                            _isUploadingImage.value = false
                            onComplete(false)
                        }
                }
            }
            .addOnFailureListener {
                _isUploadingImage.value = false
                onComplete(false)
            }
    }

    fun uploadPost(imageUri: Uri, caption: String = "", onComplete: (Boolean) -> Unit) {
        val user = firebaseAuth.currentUser ?: return
        val uid = user.uid
        val postId = UUID.randomUUID().toString()

        _isUploadingPost.value = true

        val storageRef = FirebaseStorage.getInstance()
            .reference.child("posts/$uid/$postId.jpg")
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("users").child(uid).child("posts").child(postId)

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val postData = mapOf(
                        "imageUrl" to imageUrl,
                        "timestamp" to System.currentTimeMillis(),
                        "caption" to caption
                    )

                    dbRef.setValue(postData)
                        .addOnSuccessListener {
                            _isUploadingPost.value = false
                            onComplete(true)
                        }
                        .addOnFailureListener {
                            _isUploadingPost.value = false
                            onComplete(false)
                        }
                }
            }
            .addOnFailureListener {
                _isUploadingPost.value = false
                onComplete(false)
            }
    }

    fun deletePost(postId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = firebaseAuth.currentUser ?: return@launch
            val uid = user.uid

            val storageRef = FirebaseStorage.getInstance()
                .reference.child("posts/$uid/$postId.jpg")
            val dbRef = FirebaseDatabase.getInstance()
                .getReference("users").child(uid).child("posts").child(postId)

            // Eliminar de Storage
            storageRef.delete().addOnSuccessListener {
                // Eliminar de Database
                dbRef.removeValue()
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }.addOnFailureListener { onComplete(false) }
        }
    }

    fun removeProfileImage() {
        viewModelScope.launch {
            val user = firebaseAuth.currentUser ?: return@launch
            val uid = user.uid

            val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")
            val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

            // Eliminar de Storage
            storageRef.delete().addOnSuccessListener {
                // Eliminar URL de la base de datos
                dbRef.child("profileImage").setValue("")
                    .addOnSuccessListener {
                        _user.value = _user.value.copy(profileImage = "")
                    }
            }
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            val user = firebaseAuth.currentUser ?: return@launch
            val uid = user.uid

            val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

            dbRef.child("name").setValue(newName)
                .addOnSuccessListener {
                    _user.value = _user.value.copy(name = newName)
                }
        }
    }
}