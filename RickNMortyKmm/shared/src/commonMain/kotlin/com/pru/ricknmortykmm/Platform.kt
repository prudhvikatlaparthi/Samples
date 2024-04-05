package com.pru.ricknmortykmm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform