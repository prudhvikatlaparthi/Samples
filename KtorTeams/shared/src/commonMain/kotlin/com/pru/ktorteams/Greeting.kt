package com.pru.ktorteams

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}