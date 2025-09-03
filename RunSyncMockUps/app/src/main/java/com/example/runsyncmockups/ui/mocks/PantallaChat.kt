package com.example.runsyncmockups.ui.mocks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaChat(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Spacer(Modifier.padding(8.dp))
        MensajeRecibido("Hola, ¿en qué puedo ayudarte?")
        MensajeEnviado("Hola, tengo un problema con la app.")
        MensajeEnviado("Tengo un problema con la app.")
        MensajeRecibido("Hola, ¿en qué puedo ayudarte?")
        MensajeEnviado("La app se cierra inesperadamente.")
        MensajeRecibido("Hola, ¿en qué puedo ayudarte?")
        MensajeEnviado("La app se cierra inesperadamente.")
        MensajeRecibido("Hola, ¿en qué puedo ayudarte?")
        MensajeEnviado("La app se cierra inesperadamente.")
        MensajeRecibido("Hola, ¿en qué puedo ayudarte?")

        Spacer( Modifier.weight(1f) )
        HorizontalDivider(
            thickness = 3.dp,
            color = Color.Gray
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = "Escribe un mensaje...", fontSize = 14.sp, modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_send),
                contentDescription = "Enviar",
                tint = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )

        }


    }

}

@Composable
fun MensajeEnviado(msj: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.End) {

        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFDCF8C6),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(text = msj, fontSize = 14.sp, textAlign = TextAlign.Start)
        }
    }
}

@Composable
fun MensajeRecibido(msj: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.Start) {

        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFF5325A4),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(text = msj, fontSize = 14.sp, textAlign = TextAlign.Start, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    PantallaChat(modifier = Modifier.padding(16.dp))
}