package com.example.runsyncmockups.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.R
import com.example.runsyncmockups.firebaseAuth
import com.example.runsyncmockups.ui.components.CustomTextField
import com.example.runsyncmockups.ui.components.PasswordField
import com.example.runsyncmockups.ui.model.UserAuthViewModel

@Composable
fun PantallaInicioSesion(navController: NavController, model: UserAuthViewModel = viewModel()) {
    val user by model.user.collectAsState()
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        firebaseAuth.currentUser?.let {
            navController.navigate(AppScreens.Home.name) {
                popUpTo(AppScreens.InicioSesion.name) {
                    inclusive = true
                }
            }
        }
    }



    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "RunSync",
                fontSize = 50.sp,
                fontFamily = FontFamily.Default,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Column (
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de RUNSYNC",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.FillWidth

            )
            Text("Inicia Sesión", fontSize = 25.sp, fontFamily = FontFamily.Default, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)
            CustomTextField(
                value = user.email,
                onValueChange = { model.updateEmailClass(it)},
                textfield = "email@domain.com",
            )

            PasswordField(
                value = user.password,
                onValueChange = { model.updatePassClass(it) },
                textfield = "Contraseña"
            )
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            SignUpText(
                onSignUpClick = {

                    navController.navigate(AppScreens.Registro.name)
                }
            )


            Button(
                onClick = {
                    login(user.email, user.password, navController, context) { msg ->
                        errorMessage = msg
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.White
                )
            ) {
                Text("Login", fontSize = 15.sp)
            }

        }


    }

}


@Composable
fun SignUpText(
    onSignUpClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "No tienes cuenta?",
            color = Color.Black,
            fontSize = 14.sp
        )
        Text(
            text = " Registrate",
            color = Color(0xFF1E40AF), // Azul tipo enlace
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.clickable{ onSignUpClick() }
        )
    }
}

fun validateForm(email:String, password:String):Boolean{
    if (!email.isEmpty() &&
        validEmailAddress(email) &&
        !password.isEmpty() &&
        password.length >= 6)
    {
        return true
    }
    return false
}
private fun validEmailAddress(email:String):Boolean{
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return email.matches(regex.toRegex())
}


fun login(email:String, password:String, controller: NavController, context: Context, onError: (String) -> Unit){
    if(validateForm(email, password)){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                controller.navigate(AppScreens.Home.name)
            }else{
                onError("Correo o contraseña incorrecta")
            }
        }
    }else{
        onError("Por favor ingresa un correo y contraseña válidos")
    }
}





@Preview
@Composable
fun InicioSesionPreview(){
    val navController = rememberNavController()
    PantallaInicioSesion(navController)
}