import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController

@Composable
fun BottomBarView(navController: NavHostController) {
    val items = listOf(
        "home" to Icons.Filled.Home,
        "routes" to Icons.Filled.Place,
        "activities" to Icons.Filled.FlashOn,
        "events" to Icons.Filled.Event,
        "profile" to Icons.Filled.Person
    )
    val currentRoute = navController.currentBackStackEntryAsState().value
        ?.destination?.route?.substringBefore("/") // por si usas args tipo "verificacion/{name}"

    NavigationBar {
        items.forEach { (route, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { navController.navigate(route) },
                icon = { Icon(icon, contentDescription = route) }
                // sin label para que quede simple (solo íconos)
            )
        }
    }
}
