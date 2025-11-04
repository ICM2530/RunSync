package com.example.runsyncmockups.model


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


//definir un estado para la vista
data class UserAuthState(
    val email : String = "",
    val password : String = "",
    val emailError:String = "",
    val passError : String = ""
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
    fun updateEmailError(error:String){
        _user.value = _user.value.copy(emailError = error)
    }
    fun updatePassError(error:String){
        _user.value = _user.value.copy(passError = error)
    }
}

