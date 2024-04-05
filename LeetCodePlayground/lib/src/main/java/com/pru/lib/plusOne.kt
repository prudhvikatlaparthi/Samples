package com.pru.lib

import java.math.BigInteger

fun main() {
    println(plusOne(intArrayOf(1,9)).contentToString())
}

fun plusOne(digits: IntArray): IntArray {
    val value = (digits.joinToString("").toBigIntegerOrNull() ?: BigInteger.ZERO) + BigInteger.ONE
    return value.toString().split("").filter { it.isNotEmpty() }.map { it.toInt() }.toIntArray()
}