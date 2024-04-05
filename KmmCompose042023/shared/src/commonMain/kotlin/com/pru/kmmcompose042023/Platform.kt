package com.pru.kmmcompose042023

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform