package com.pru.lib

fun main() {
    println(strStr(haystack = "codet", needle = "tedosdfsdfsdfdsfc"))
}

fun strStr(haystack: String, needle: String): Int {
    println(haystack)
    println(needle)
    if ((needle.length > haystack.length) || !haystack.contains(needle)) {
        return -1
    }
    var k: Int
    var returnIndex: Int
    for (i in haystack.indices) {
        k = i
        returnIndex = i
        var incCounter = 0
        for (j in needle.indices) {
            if (needle[j] != haystack[k]) {
                break
            } else {
                k++
                incCounter++
            }
            if (needle.length == incCounter) {
                return returnIndex
            }
        }
    }
    return -1
}