package com.pru.kmmvariants

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}