package com.pru.offlineapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.pru.offlineapp.ui.prop_master.PropMasterScreen
import com.pru.offlineapp.ui.theme.OfflineAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OfflineAppTheme {
                Navigator(PropMasterScreen()) {
                    SlideTransition(navigator = it)
                }
            }
        }
    }
}

