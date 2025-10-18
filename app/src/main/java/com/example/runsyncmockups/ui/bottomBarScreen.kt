import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBarView(navController: NavController) {
    // Ruta actual (normalizada por si hay argumentos tipo "ruta/123")
    val currentRoute = navController.currentBackStackEntryAsState().value
        ?.destination?.route?.substringBefore("/")

    NavigationBar(
        containerColor = Color(0xFF0A0000),

    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate( "home") },
            icon   = { Icon(Icons.Filled.Home, contentDescription = "Home",tint = Color(0xFFFF5722)) },
            label  = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == "activities",
            onClick = {navController.navigate("Activities") },
            icon   = { Icon(Icons.Filled.FlashOn, contentDescription = "Activities", tint = Color(0xFFFFFF00)
            ) },
            label  = { Text("Activities") }
        )
        NavigationBarItem(
            selected = currentRoute == "mapa",
            onClick = { navController.navigate("rutas") },
            icon   = { Icon(Icons.Filled.Place, contentDescription = "Rutas",tint = Color(0xFF35D0D0)) },
            label  = { Text("Mapa") }
        )
        NavigationBarItem(
            selected = currentRoute == "events",
            onClick = { navController.navigate("events") },
            icon   = { Icon(Icons.Filled.Event, contentDescription = "Events", tint = Color(0xFFFF9800)
            ) },
            label  = { Text("Events") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate( "profile") },
            icon   = { Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color(0xFFF8F6F6)
            ) },
            label  = { Text("Profile") }
        )
    }
}

// Navegación segura (evita duplicados y mantiene estado)
// Asegúrate de que las rutas existen en tu NavHost con esos mismos ids.

