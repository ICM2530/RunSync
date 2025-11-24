package com.example.runsyncmockups.model


import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


//definir un estado para la vista
data class UserAuthState(
    val name: String = "",
    val lastName: String = "",
    val email : String = "",
    val password : String = "",
    val id: String? = null,
    val status: String? = null,
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

    fun updateStatus(newStatus: String) {
        _user.value = _user.value.copy(status = newStatus)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updates = mapOf(
            "status" to newStatus
        )
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .updateChildren(updates)

    }
}

class MyUsersViewModel : ViewModel() {
    val database = FirebaseDatabase.getInstance()
    val dbReference = database.getReference("users")
    val _users = MutableStateFlow(listOf<UserAuthState>())
    val users: StateFlow<List<UserAuthState>> = _users.asStateFlow()

    private fun DataSnapshot.doubleAt(path: String): Double? {
        val v = child(path).value ?: return null
        return (v as? Number)?.toDouble() ?: v.toString().toDoubleOrNull()
    }

    var vel: ValueEventListener =
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedList = snapshot.children.mapNotNull { c ->

                    val uid = c.key ?: return@mapNotNull null

                    val name = c.child("name").getValue(String::class.java) ?: ""
                    val lastName = c.child("lastName").getValue(String::class.java) ?: ""
                    val status = c.child("status").getValue(String::class.java) ?: ""
                    val contactImageUrl = c.child("contactImageUrl").getValue(String::class.java)

                    val lat = c.doubleAt("locActual/lat")
                    val lon = c.doubleAt("locActual/lng")

                    UserAuthState(
                        id = uid,
                        name = name,
                        lastName = lastName,
                        status = status,
                        lat = lat,
                        lon = lon,
                    )
                }
                _users.value = updatedList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MyUsersViewModel", "Error al cargar usuarios: ${error.message}")
            }
        })
}
