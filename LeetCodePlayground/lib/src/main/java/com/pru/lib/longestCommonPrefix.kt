package com.pru.lib

fun main() {
    println(longestCommonPrefix(arrayOf("flower", "flow", "flight")))
}

fun longestCommonPrefix(strs: Array<String>): String {
    var result = ""
    val temp = strs.minBy(String::length)
    val fr = strs.toMutableList()
    fr.remove(temp)
    for (c in temp) {
        var counter = 0
        for (t in temp) {
            if (c == t) {
                counter++
            }
        }
        if (counter == temp.length) {
            result += c
        }
    }
    return result
}