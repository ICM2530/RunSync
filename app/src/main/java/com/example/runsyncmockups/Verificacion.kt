package com.example.runsyncmockups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun PantallaVerificacion(navController: NavController, name : String? = "User"){
    var code by remember { mutableStateOf("") }
    var pressed by remember { mutableStateOf(false) }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        Text(
            "RunSync",
            fontSize = 50.sp,
            fontFamily = FontFamily.Default,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left
        )

        Column (modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de RUNSYNC",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.FillWidth

            )
        Text("Te hemos enviado un código de 6 dígitos a tu número", fontSize = 16 .sp, fontFamily = FontFamily.Default , textAlign = TextAlign.Center,modifier = Modifier.fillMaxWidth() )
            CodeTextField(value = code, onValueChange = { code = it }, modifier = Modifier.padding(12.dp))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Button(onClick = {
                pressed = true
            }, modifier = Modifier.fillMaxWidth().weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )) { Text("No lo recibí") }

            Button(onClick = {
                pressed = true
            }, modifier = Modifier.fillMaxWidth().weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White 
                )) { Text("Reenviar", fontSize = 15.sp) }
        }

    }
}


@Composable
fun CodeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Código de 6 dígitos") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedBorderColor = Color(0xFF9CA3AF),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            cursorColor = Color(0xFF111827)
        )
    )
}

@Preview
@Composable
fun VerificacionPreview(){
    val navController = rememberNavController()
    PantallaVerificacion(navController)
}