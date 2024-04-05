package com.pru.firsthybridapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform