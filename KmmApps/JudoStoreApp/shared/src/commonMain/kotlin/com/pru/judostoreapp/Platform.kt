package com.pru.judostoreapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform