import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.invest.FavoritesScreen
import com.example.invest.homeScreen.FounderHomeScreen
import com.example.invest.FounderProfileScreen
import com.example.invest.InvestorProfileScreen

import com.example.invest.homeScreen.InvestorHomeScreen
import com.example.invest.MessagesScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(accountType: String) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                if (accountType == "Founder") {
                    FounderHomeScreen(

                    )
                } else {
                    InvestorHomeScreen() // ViewModel is used internally
                }
            }
            composable("profile") {
                if (userId != null) {
                    if (accountType == "Founder") {
                        FounderProfileScreen(founderId = userId)
                    } else if(accountType == "Investor") {
                        InvestorProfileScreen()
                    } else {
                        MessagesScreen()
                    }
                }
            }
            composable("favorites") { FavoritesScreen() }
            composable("messages") { MessagesScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Profile", Icons.Default.AccountCircle, "profile"),
        BottomNavItem("Favorites", Icons.Default.Favorite, "favorites"),
        BottomNavItem("Messages", Icons.Default.Email, "messages")
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = false,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}


data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)



