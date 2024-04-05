package com.pru.misc.utils

sealed class ScreenRoutes(val route: String) {
    object PostsScreen : ScreenRoutes("PostsScreen")
    object PostDetailScreen : ScreenRoutes("PostDetailScreen")
}