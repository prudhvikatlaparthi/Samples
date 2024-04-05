package com.pru.usersapp.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.pru.usersapp.App
import com.pru.usersapp.ui.destinations.LoginScreenDestination
import com.pru.usersapp.ui.destinations.MainScreenDestination
import com.pru.usersapp.ui.destinations.SplashScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@Destination()
@Composable
fun SplashScreen(navigator: DestinationsNavigator) {
    var isLoggedIn by remember {
        mutableStateOf<Boolean?>(null)
    }
    LaunchedEffect(key1 = Unit) {
        delay(2000)
        isLoggedIn = App.preference.isLoggedIn
    }
    when (isLoggedIn) {
        true -> {
            navigator.navigate(MainScreenDestination.route, builder = {
                popUpTo(SplashScreenDestination.route) {
                    inclusive = true
                }
            })
        }
        false -> {
            navigator.navigate(LoginScreenDestination(), builder = {
                popUpTo(SplashScreenDestination.route) {
                    inclusive = true
                }
            })
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFFBEFF6C))) {
                Text(modifier = Modifier.align(Alignment.Center), text = "Hi", fontWeight = FontWeight.Bold, color = Color(0xFF333637))
            }
        }
    }
}