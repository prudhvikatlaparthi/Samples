package com.pru.playground

fun main() {
//    println(isHappy(7))
    println(isHappy(1111111))
}

fun isHappy(n: Int): Boolean {
    var result = false
    if (n > 0) {
        val sum = sum(n)
        if (sum == 1) {
            result = true
        }
    }

    return result
}

private fun sum(n: Int): Int {
    if (n.toString().length == 1) {
        return n
    }
    return sum(n.toString().split("").filter { it.trim().isNotEmpty() }
        .map { it.toIntOrNull() ?: 0 }.fold(0) { acc, next ->
            acc + (next * next)
        })
}
