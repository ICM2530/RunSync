import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.runsyncmockups.Navigation.AppScreens
import com.example.runsyncmockups.model.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


@Composable
fun BottomBarView(navController: NavController, vm: LocationViewModel = viewModel()) {
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
            onClick = { navController.navigate("Rutas")},
            icon   = { Icon(Icons.Filled.Place, contentDescription = "Rutas",tint = Color(0xFF35D0D0)) },
            label  = { Text("Mapa") }
        )
        NavigationBarItem(
            selected = currentRoute == "Chats",
            onClick = { navController.navigate(AppScreens.Chat.name) },
            icon   = { Icon(Icons.Filled.Chat, contentDescription = "Chats", tint = Color(0xFFFF9800)
            ) },
            label  = { Text("Chats") }
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







