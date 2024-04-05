package com.pru.diceapp.ui.home

import androidx.lifecycle.ViewModel
import com.pru.diceapp.model.Dice

class HomeViewModel : ViewModel() {
    val diceItems = List(6) {
        Dice(number = it + 1)
    }
}