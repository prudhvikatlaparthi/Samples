package com.pru.birdapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform