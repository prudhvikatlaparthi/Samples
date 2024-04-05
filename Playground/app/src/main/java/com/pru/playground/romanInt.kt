package com.pru.playground

fun main() {
    println(romanToInt("MCMXCIV"))
}

fun romanToInt(s: String): Int {
    var result = s.replace("IV", "4$")
    result = result.replace("IX", "9$")
    result = result.replace("XL", "40$")
    result = result.replace("XC", "90$")
    result = result.replace("CD", "400$")
    result = result.replace("CM", "900$")
    result = result.replace("I", "1$")
    result = result.replace("V", "5$")
    result = result.replace("X", "10$")
    result = result.replace("L", "50$")
    result = result.replace("C", "100$")
    result = result.replace("D", "500$")
    result = result.replace("M", "1000$")

    return result.split("$").filter { it.trim().isNotEmpty() }.map { it.toIntOrNull() ?: 0 }
        .fold(0) { a, n ->
            a + n
        }
}