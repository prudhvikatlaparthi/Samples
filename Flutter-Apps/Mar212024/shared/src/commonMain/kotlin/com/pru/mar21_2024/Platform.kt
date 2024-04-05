package com.pru.mar21_2024

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform