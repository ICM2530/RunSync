package com.example.runsyncmockups.ui

import BottomBarView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

@Composable
fun EventsScreen(navController: NavController) {

    Scaffold(
        bottomBar = { BottomBarView(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            item {
                // Encabezado principal
                Text(
                    text = "Eventos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                // Próximos eventos
                Text(
                    text = "Próximos eventos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                eventCard(
                    nombreEmpresa = "Carrera atlética Compensar",
                    fotoUrl = "https://yt3.googleusercontent.com/MwyQ8t3V1UVsOC0pvRxwDUWTUKWT65sgbrWlGKP0V6n7iC5zWQlzGEVfnZUmdPp6Tvhn9IDm=s900-c-k-c0x00ffffff-no-rj",
                    navController = navController
                )
            }
            item {
                eventCard(
                    nombreEmpresa = "Carrera Addidas 15k",
                    fotoUrl ="https://www.google.com/imgres?q=addidas&imgurl=https%3A%2F%2Flookaside.fbsbx.com%2Flookaside%2Fcrawler%2Fmedia%2F%3Fmedia_id%3D100064509036682&imgrefurl=https%3A%2F%2Fwww.facebook.com%2FadidasCO%2F&docid=-Vt4_xe4ncYK2M&tbnid=8Pi9542U3mcf7M&vet=12ahUKEwjXh5uZ3amQAxWdSjABHWbhAwUQM3oECCQQAA..i&w=800&h=800&hcb=2&ved=2ahUKEwjXh5uZ3amQAxWdSjABHWbhAwUQM3oECCQQAA",
                    navController = navController
                )
            }
            item {
                eventCard(
                    nombreEmpresa = "Carrera Nike 10k",
                    fotoUrl = "https://www.google.com/imgres?q=Nike%20marca&imgurl=http%3A%2F%2Fokeysport.com%2Fcdn%2Fshop%2Fcollections%2FLogo_de_marcas_page-0020.jpg%3Fv%3D1738807740&imgrefurl=https%3A%2F%2Fokeysport.com%2Fcollections%2Fnike%3Fsrsltid%3DAfmBOopJ4-3h0ogN50YMDh8tBcWWcyklGcUGPGrtJrnpSZD0T09mwl53&docid=UQBpY6C7CLORaM&tbnid=UJIECg65vWDmQM&vet=12ahUKEwiNq62a3qmQAxWMRTABHTsYO_UQM3oECBgQAA..i&w=782&h=782&hcb=2&ved=2ahUKEwiNq62a3qmQAxWMRTABHTsYO_UQM3oECBgQAA",
                    navController = navController
                )
            }
            item {
                eventCard(
                    nombreEmpresa = "Carrera Puma 5k",
                    fotoUrl = "https://www.google.com/imgres?q=Puma%20marca&imgurl=https%3A%2F%2Fassets.turbologo.com%2Fblog%2Fes%2F2019%2F11%2F19132946%2Fpuma-logo-cover-958x575.png&imgrefurl=https%3A%2F%2Fturbologo.com%2Fes%2Fblog%2Fpuma-logo%2F&docid=jRWCJQqt2j3eoM&tbnid=kseYNF8vFHdAqM&vet=12ahUKEwiRsOuJ3qmQAxVpQzABHTdLNcsQM3oECBUQAA..i&w=958&h=575&hcb=2&ved=2ahUKEwiRsOuJ3qmQAxVpQzABHTdLNcsQM3oECBUQAA",
                    navController = navController
                )
            }

            item {
                // Eventos pasados
                Text(
                    text = "Eventos pasados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                )
            }

            item {
                eventPastCard(
                    nombreEmpresa = "Carrera Allianz 15k",
                    fotoUrl = "https://www.allianz-partners.com/content/onemarketing/awp/azpartnerscom/es_CO.thumb.1280.1280.png?ck=1755816753",
                    navController = navController
                )
            }
            item {
                eventCard(
                    nombreEmpresa = "Carrera EPM 10k",
                    fotoUrl = "https://www.google.com/imgres?q=EPM&imgurl=https%3A%2F%2Fsibcolombia.net%2Fwp-content%2Fuploads%2F2021%2F05%2FEPM.jpg&imgrefurl=https%3A%2F%2Fsibcolombia.net%2Fsocios%2Fepm%2F&docid=OJJdyK-2vEJ3rM&tbnid=-z2zBThDkvPjiM&vet=12ahUKEwjS1PP93amQAxVxQzABHW0JATAQM3oECBcQAA..i&w=500&h=500&hcb=2&ved=2ahUKEwjS1PP93amQAxVxQzABHW0JATAQM3oECBcQAA ",
                    navController = navController
                )
            }
            item {
                eventCard(
                    nombreEmpresa = "Carrera Coldeportes 5k",
                    fotoUrl = "https://www.google.com/imgres?q=Coldeportes&imgurl=https%3A%2F%2Fpbs.twimg.com%2Fprofile_images%2F1155914875450920960%2FV80lZ-Kq_400x400.jpg&imgrefurl=https%3A%2F%2Fx.com%2Fcoldeportes&docid=fqFHjeCC7ZI1PM&tbnid=kddWFWxkhhosvM&vet=12ahUKEwjOtdDU3amQAxUFVzABHQgYJp4QM3oECB4QAA..i&w=400&h=400&hcb=2&ved=2ahUKEwjOtdDU3amQAxUFVzABHQgYJp4QM3oECB4QAA",
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun eventCard(nombreEmpresa: String, fotoUrl: String, navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Imagen circular
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.LightGray, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fotoUrl,
                    contentDescription = "Logo del evento",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Nombre del evento
            Text(
                text = nombreEmpresa,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botón de inscripción
            Button(
                onClick = { /* navController.navigate("inscripcion") */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.White
                )
            ) {
                Text("Inscribirse")
            }
        }
    }
}

@Composable
fun eventPastCard(nombreEmpresa: String, fotoUrl: String, navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Imagen circular con transparencia
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage (
                    model = fotoUrl,
                    contentDescription = "Logo del evento pasado",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Nombre del evento pasado
            Text(
                text = nombreEmpresa,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
fun EventsScreenPreview() {
    val navController = rememberNavController()
    EventsScreen(navController)
}
