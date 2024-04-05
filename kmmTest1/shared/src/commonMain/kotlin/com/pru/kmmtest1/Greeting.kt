package com.pru.kmmtest1

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}