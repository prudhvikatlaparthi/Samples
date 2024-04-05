package com.mindorks.framework.mvi.ui.main.viewstate

import java.lang.Exception

sealed class MainState {
    object Loading : MainState()
    data class Success(val data: Any) : MainState()
    data class Error(val error: Exception) : MainState()
}