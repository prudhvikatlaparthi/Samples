package com.pru.lib

import java.math.BigInteger

fun main() {
    
    println(
        addBinary(
            "10100000100100110110010000010101111011011001101110111111111101000000101111001110001111100001101",
            "110101001011101110001111100110001010100001101011101010000011011011001011101111001100000011011110011"
        )
    )
}

fun addBinary(a: String, b: String): String {
    return toBinary(toDecimal(a) + toDecimal(b))
}

fun toBinary(value: BigInteger): String {
    if (value == BigInteger.valueOf(0) || value == BigInteger.valueOf(1)) {
        return value.toString()
    }
    var quotiant = value
    var result = ""
    while (true) {
        val r = quotiant % BigInteger.valueOf(2)
        quotiant /= BigInteger.valueOf(2)
        if (quotiant == BigInteger.valueOf(1)) {
            result = quotiant.toString().plus(r.toString()).plus(result)
            break
        }
        result = r.toString().plus(result)
    }
    return result
}

private fun toDecimal(a: String): BigInteger {
    var result = BigInteger.valueOf(0)
    for ((pow, i) in ((a.length - 1) downTo 0).withIndex()) {
        result = (result + (a[i].toString().toBigInteger() * (pow.power(BigInteger.valueOf(2)))))
    }
    return result
}

fun Int.power(value: BigInteger): BigInteger {
    var exponent = this
    var result: BigInteger = BigInteger.valueOf(1)
    while (exponent != 0) {
        result *= value
        --exponent
    }
    return result
}