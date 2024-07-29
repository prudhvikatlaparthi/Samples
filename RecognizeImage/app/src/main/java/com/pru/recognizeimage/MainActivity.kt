package com.pru.recognizeimage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pru.recognizeimage.ui.ScreenRoutes
import com.pru.recognizeimage.ui.theme.RecognizeImageTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<CameraViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecognizeImageTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = ScreenRoutes.ScreenA
                ) {
                    composable<ScreenRoutes.ScreenA> {
                        ScanView(viewModel) {
                            navController.navigate(ScreenRoutes.ScreenB)
                        }
                    }
                    composable<ScreenRoutes.ScreenB> {
                        CameraView(viewModel) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}