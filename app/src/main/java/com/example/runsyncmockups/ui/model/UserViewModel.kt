package com.example.runsyncmockups.ui.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runsyncmockups.firebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserData(
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = ""
)

class UserViewModel : ViewModel() {
    private val _user = MutableStateFlow(UserData())
    val user: StateFlow<UserData> = _user.asStateFlow()

    /** Cargar datos del usuario desde la BD **/
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
                    val imageUrl = snapshot.child("profileImageUrl").getValue(String::class.java) ?: ""

                    _user.value = UserData(name, email, imageUrl)
                }
            }
        }
    }

    /** Subir foto de perfil y actualizar BD **/
    fun uploadProfileImage(imageUri: Uri, onComplete: (Boolean) -> Unit) {
        val user = firebaseAuth.currentUser ?: return
        val uid = user.uid

        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    dbRef.child("profileImageUrl").setValue(imageUrl)
                        .addOnSuccessListener {
                            _user.value = _user.value.copy(profileImageUrl = imageUrl)
                            onComplete(true)
                        }
                        .addOnFailureListener { onComplete(false) }
                }
            }
            .addOnFailureListener { onComplete(false) }
    }
}
