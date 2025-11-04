package com.example.runsyncmockups.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.ContentScale
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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

@Composable
fun PantallaRegistro(navController: NavController, model: UserAuthViewModel = viewModel()) {

    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de RUNSYNC",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.FillWidth

            )
            Text("Crea una cuenta", fontSize = 25.sp, fontFamily = FontFamily.Default, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)
            Text("Ingresa tu datos para registrarte", fontSize = 15.sp, fontFamily = FontFamily.Default, fontStyle = FontStyle.Italic)
            CustomTextField(
                value = name,
                onValueChange = { name = it },
                textfield = "Nombre"
            )
            CustomTextField(
                value = lastName,
                onValueChange = { lastName = it },
                textfield = "Apellido"
            )
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                textfield = "email@domain.com",)
            PasswordField(
                value = password,
                onValueChange = { password = it },
                textfield = "Contraseña"
            )


            Button(
                onClick = {
                    registerUser(name, lastName, email, password, navController, context)
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
                Text("Registrarse")
            }

        }
         }

            }


fun registerUser(
    name: String,
    lastName: String,
    email: String,
    password: String,
    navController: NavController,
    context: Context
) {
    if (validateForm(email, password)) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid ?: return@addOnCompleteListener

                    // 🔹 Actualiza el perfil con nombre completo
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$name $lastName")
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                // ✅ Guarda los datos en Firebase Realtime Database
                                val db = FirebaseDatabase.getInstance()
                                val userRef = db.getReference("users").child(uid)

                                val userData = mapOf(
                                    "name" to name,
                                    "lastName" to lastName,
                                    "email" to email,
                                    "createdAt" to System.currentTimeMillis()
                                )

                                userRef.setValue(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Usuario registrado y guardado correctamente ✅",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // 🔹 Redirige a la pantalla Home
                                        navController.navigate(AppScreens.Home.name) {
                                            popUpTo(AppScreens.Registro.name) { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Error al guardar datos: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error al actualizar perfil: ${profileTask.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Error al registrar usuario: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    } else {
        Toast.makeText(context, "Datos inválidos. Verifica el correo y la contraseña.", Toast.LENGTH_LONG).show()
    }
}



@Preview
@Composable
fun PantallaPreview(){
    val navController = rememberNavController()
    PantallaRegistro(navController)
}