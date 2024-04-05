package com.pru.playground

fun main() {
    val hex = "4C040000B3FBFFFF4C0400000DF20DF2"
    println(hex)
    println(hex.hexToInteger())
}

fun String.hexToInteger(): Int {
    return unsignedInt2IntLE(this.decodeHex(), 0)
}

fun unsignedInt2IntLE(src: ByteArray, offset: Int): Int {
    var value = 0
    for (i in offset until offset + 4) {
        value = value or (src[i].toInt() and 0xff shl (i - offset) * 8)
    }
    return value
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}