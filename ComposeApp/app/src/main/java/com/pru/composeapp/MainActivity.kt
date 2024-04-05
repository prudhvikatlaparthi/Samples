package com.pru.composeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pru.composeapp.presentation.navigation.ScreenRoute
import com.pru.composeapp.presentation.theme.ComposeAppTheme
import com.pru.composeapp.presentation.ui.ShopByCategoryScreen
import com.pru.composeapp.presentation.ui.help.HelpScreen
import com.pru.composeapp.presentation.ui.home.HomeScreen
import com.pru.composeapp.presentation.ui.login.LoginScreen
import com.pru.composeapp.presentation.ui.productdetails.ProductDetailsScreen
import com.pru.composeapp.presentation.ui.search.SearchScreen
import com.pru.composeapp.presentation.ui.splash.MySplashScreen

class MainActivity : ComponentActivity() {
    lateinit var navHost: NavHostController
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeAppTheme {
                navHost = rememberNavController()

                NavHost(
                    navController = navHost,
                    startDestination = ScreenRoute.HomeScreen.route
                ) {
                    composable(route = ScreenRoute.MySplashScreen.route) {
                        MySplashScreen(navHost)
                    }
                    composable(route = ScreenRoute.HomeScreen.route) { nav ->
                        HomeScreen(navHost)
                    }
                    composable(route = ScreenRoute.SearchScreen.route) {
                        SearchScreen()
                    }
                    composable(route = ScreenRoute.ProductDetailsScreen.route) {
                        ProductDetailsScreen(navHost)
                    }
                    composable(route = ScreenRoute.ShopByCategoryScreen.route) {
                        ShopByCategoryScreen(navHost)
                    }
                    composable(route = ScreenRoute.HelpScreen.route) {
                        HelpScreen(navHost)
                    }
                    composable(route = ScreenRoute.LoginScreen.route) {
                        LoginScreen(navHost)
                    }
                }
            }
        }
    }

    fun displayMessage() {
        Log.i("Prudhvi Log", "displayMessage: ")
    }

    /*override fun onBackPressed() {
        if (navHost.currentBackStackEntry?.destination?.route == ScreenRoute.HomeScreen.route) {

        } else {
            super.onBackPressed()
        }
        Log.i("Prudhvi Log", "onBackPressed: ")
    }*/
}

