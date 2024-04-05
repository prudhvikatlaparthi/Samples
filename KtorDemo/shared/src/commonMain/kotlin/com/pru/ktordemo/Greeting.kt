package com.pru.ktordemo

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}