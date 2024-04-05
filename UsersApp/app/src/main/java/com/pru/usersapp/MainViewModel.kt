package com.pru.usersapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var bottomNavState by mutableStateOf<BottomNav>(BottomNav.General)
    val data = List(20) {
        "Item $it"
    }
}


sealed interface BottomNav {
    object General : BottomNav
    object Favor : BottomNav
    object Settings : BottomNav
}