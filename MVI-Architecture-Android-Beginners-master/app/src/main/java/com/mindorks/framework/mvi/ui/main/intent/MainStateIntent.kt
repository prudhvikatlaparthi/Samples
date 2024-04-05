package com.mindorks.framework.mvi.ui.main.intent

sealed class MainStateIntent {

    object FetchUser : MainStateIntent()

    object PrepareValue : MainStateIntent()

}