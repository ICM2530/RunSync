package com.example.runsyncmockups.model


import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


//definir un estado para la vista
data class UserAuthState(
    val name: String = "",
    val lastName: String = "",
    val email : String = "",
    val password : String = "",
    val id: String? = null,
    val lat: Double? = 0.0,
    val lon: Double? = 0.0,
)

//metodos y funciones para actualizar el estado
class UserAuthViewModel : ViewModel(){
    val _user = MutableStateFlow<UserAuthState>(UserAuthState())
    val user = _user.asStateFlow()
    fun updateEmailClass(newEmail : String){
        _user.value = _user.value.copy(email=newEmail)
    }
    fun updatePassClass(newPass : String){
        _user.value = _user.value.copy(password=newPass)
    }
    fun updateLocActual(newLoc: LatLng) {
        _user.value = _user.value.copy(
            lat = newLoc.latitude,
            lon = newLoc.longitude
        )

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updates = mapOf(
            "locActual/lat" to newLoc.latitude,
            "locActual/lng" to newLoc.longitude,
        )
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .updateChildren(updates)

    }
}

