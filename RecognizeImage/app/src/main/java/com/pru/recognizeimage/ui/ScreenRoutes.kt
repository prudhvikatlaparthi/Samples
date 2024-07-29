package com.pru.recognizeimage.ui

import kotlinx.serialization.Serializable


@Serializable
sealed interface ScreenRoutes {
    @Serializable
    data object ScreenA : ScreenRoutes

    @Serializable
    data object ScreenB : ScreenRoutes
}