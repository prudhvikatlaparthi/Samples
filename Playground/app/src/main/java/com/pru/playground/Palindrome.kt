package com.pru.playground


fun main() {
    println(isPalindrome(101))
}

fun isPalindrome(x: Int): Boolean {
    val digits = x.toString().split("").filter { it.trim().isNotEmpty() }
    val outputSB = StringBuilder()
    for (i in (digits.size - 1) downTo 0) {
        outputSB.append(digits[i])
    }
    return x == outputSB.fold("") { a, n -> a.plus(n.toString()) }.toInt()
}