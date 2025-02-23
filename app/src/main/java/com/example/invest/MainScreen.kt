import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.invest.FounderProfileScreen
import com.example.invest.homeScreen.FounderHomeScreen
import com.example.invest.InvestorProfileScreen
import com.example.invest.LoginScreen
import com.example.invest.favoriteScreen.FavoritesScreen

import com.example.invest.homeScreen.InvestorHomeScreen
import com.example.invest.messageScreen.ChatRoomScreen

import com.example.invest.messageScreen.MessagesScreen
import com.example.invest.projectScreen.FounderProjectScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(accountType: String) {
    val navController = rememberNavController()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

        Scaffold(
            bottomBar = { BottomNavigationBar(navController, accountType) }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {

                composable("home") {
                    if (accountType == "Founder") {
                        FounderHomeScreen()
                    } else {
                        InvestorHomeScreen()
                    }
                }
                composable("profile") {
                    if (userId != null) {
                        if (accountType == "Founder") {
                            FounderProfileScreen(founderId = userId)
                        } else if(accountType == "Investor") {
                            InvestorProfileScreen(navController = navController)
                        }
                    }
                }
                composable("project") {
                    if (userId != null) {
                        FounderProjectScreen(founderId = userId)
                    }
                }
                composable("favorites") { FavoritesScreen() }
                composable("messages") { MessagesScreen(navController = navController) }
                composable("chats/{chatId}") { backStackEntry ->
                    val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                    println("ChatRoomId trying to open: $chatId")
                    ChatRoomScreen(chatId = chatId)
                }
            }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, accountType: String) {
    val items = if(accountType == "Founder") {
        listOf(
            BottomNavItem("Home", Icons.Default.Home, "home"),
            BottomNavItem("Favorites", Icons.Default.Favorite, "favorites"),
            BottomNavItem("Profile", Icons.Default.AccountCircle, "profile"),
            BottomNavItem("project", Icons.Default.Build, "project"),
            BottomNavItem("Messages", Icons.Default.Email, "messages")
        )
    } else {
        listOf(
            BottomNavItem("Home", Icons.Default.Home, "home"),
            BottomNavItem("Favorites", Icons.Default.Favorite, "favorites"),
            BottomNavItem("Profile", Icons.Default.AccountCircle, "profile"),
            BottomNavItem("Messages", Icons.Default.Email, "messages"))
    }

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



