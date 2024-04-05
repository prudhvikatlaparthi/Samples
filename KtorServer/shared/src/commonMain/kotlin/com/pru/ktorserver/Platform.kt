package com.pru.ktorserver

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform