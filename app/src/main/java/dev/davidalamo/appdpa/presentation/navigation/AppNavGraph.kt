package dev.davidalamo.appdpa.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.davidalamo.appdpa.presentation.auth.LoginScreen
import dev.davidalamo.appdpa.presentation.auth.RegisterScreen
import dev.davidalamo.appdpa.presentation.home.HomeScreen
import dev.davidalamo.appdpa.presentation.permissions.GalleryPermissionsScreen
import dev.davidalamo.appdpa.presentation.realtime.FirestoreRealtimeScreen

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login")
    {
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") {
            DrawerScaffold(navController) {
                HomeScreen()
            }
        }
        composable("permissions") {
            DrawerScaffold(navController) {
                GalleryPermissionsScreen()
            }
        }
        composable("realtime") {
            DrawerScaffold(navController) {
                FirestoreRealtimeScreen()
            }
        }
    }
}