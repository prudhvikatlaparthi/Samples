package com.pru.july16tue

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform