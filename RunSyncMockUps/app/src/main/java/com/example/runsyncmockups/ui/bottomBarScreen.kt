import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBarView(navController: NavController) {
    // Ruta actual (normalizada por si hay argumentos tipo "ruta/123")
    val currentRoute = navController.currentBackStackEntryAsState().value
        ?.destination?.route?.substringBefore("/")

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate( "home") },
            icon   = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label  = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "rutas",
            onClick = { navController.navigate("rutas") },
            icon   = { Icon(Icons.Filled.Place, contentDescription = "Rutas") },
            label  = { Text("Rutas") }
        )
        NavigationBarItem(
            selected = currentRoute == "activities",
            onClick = {navController.navigate("Activities") },
            icon   = { Icon(Icons.Filled.FlashOn, contentDescription = "Activities") },
            label  = { Text("Activities") }
        )
        NavigationBarItem(
            selected = currentRoute == "events",
            onClick = { navController.navigate("events") },
            icon   = { Icon(Icons.Filled.Event, contentDescription = "Events") },
            label  = { Text("Events") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate( "profile") },
            icon   = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label  = { Text("Profile") }
        )
    }
}

// Navegación segura (evita duplicados y mantiene estado)
// Asegúrate de que las rutas existen en tu NavHost con esos mismos ids.

