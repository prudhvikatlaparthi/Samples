package com.pru.composeapp.presentation.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pru.composeapp.presentation.navigation.ScreenRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MySplashScreen(navHostController: NavHostController) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        scope.launch {
            delay(2_000)
            navHostController.navigate(ScreenRoute.HomeScreen.route) {
                popUpTo(ScreenRoute.MySplashScreen.route) {
                    inclusive = true
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Welcome I'm splash screen", modifier = Modifier.align(Alignment.Center))
    }
}