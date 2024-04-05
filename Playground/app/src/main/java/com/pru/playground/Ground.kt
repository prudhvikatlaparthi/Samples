package com.pru.playground

import java.math.BigInteger

fun main() {
    val map = mutableMapOf<Int, Int>()
    val input = listOf(4, 6, 6, 4, 4, 6)
    val index = listOf(0, 1, 2, 3, 4, 5)
    val sum = 10
    val output = listOf(
        listOf(0, 1),
        listOf(0, 2),
        listOf(1, 3),
        listOf(2, 4),
        listOf(0, 5),
        listOf(3, 5),
        listOf(4, 5)
    )
    println(output)

    val result = mutableListOf<MutableList<Int>>()

    for (i in input.indices) {
        val value = input[i]
        val diff = sum - value
        if (map.contains(diff)) {
            val add = map.filterValues { it == diff }.keys.toMutableList()
            add.add(i)
            result.add(add)
        } else {
            map[value] = i
        }
    }
    println(result)

}


fun fillStringWithDots(input: String): String {
    val words = input.split('.')
        .filter { it.isNotEmpty() } // Split input string by dots and remove empty strings
    val wordSize = words[0].length // Length of the words
    val numDots = input.length - words.joinToString("").length // Count of dots

    val dotSpacing = numDots / (words.size - 1) // Calculate the spacing between dots
    var extraDots = numDots % (words.size - 1) // Calculate any remaining dots to be added

    val result = StringBuilder()
    for (i in words.indices) {
        result.append(words[i]) // Append word

        if (i < words.size - 1) {
            // Append dots with calculated spacing
            for (j in 0 until dotSpacing) {
                result.append('.')
            }

            if (extraDots > 0) {
                // Append extra dots, if any
                result.append('.')
                extraDots--
            }
        }
    }

    return result.toString()
}


fun problem2(input: String) {
    val words = input.split(".").filter { it.isNotEmpty() }
    if (words.size == 1) {
        return
    }
    val totalDots = input.length - (words.joinToString("")).length
    var eqDots = totalDots / (words.size - 1)
    val extraDots = eqDots % 2
    if (extraDots >= 1) {
        eqDots += extraDots
    }
    val result = StringBuilder()
    for (w in words.indices) {
        result.append(words[w])
        if (w != words.size - 1) {
            for (i in 1..eqDots) {
                result.append(".")
            }
        }
    }
    println(input)
    println(result)
}

private fun extractDots(input: String): Int {
    var dotsCount1 = 0
    for (item in input) {
        if (item == '.') {
            dotsCount1++
        } else {
            break
        }
    }
    return dotsCount1
}

fun countTrailingZeroes(n: Int): Int {
    var count = 0
    var i = 5
    while (n / i >= 1) {
        count += n / i
        i *= 5
    }
    return count
}

fun problem1() {
    var n: Long = 5
    var result: BigInteger = BigInteger.ONE
    while (n > 0) {
        result = (result * BigInteger.valueOf(n))
        n--
    }
    println(result)
    var trailingZeros = 0
    for (i in (result.toString().length - 1) downTo 0) {
        val c = result.toString()[i]
        if (c == '0') {
            trailingZeros++
        } else {
            break
        }
    }
    println(trailingZeros)
}

fun getSpaces(count: Int): List<String> {
    return List(count - 1) {
        " "
    }
}

fun countUserLogins(logs: Array<Array<String>>): Array<Array<String>> {
    val map : MutableMap<String,Array<String>> = mutableMapOf()
    logs.forEach {row ->
        if (map.containsKey(row[0])){
            val prevMap = map[row[0]]!!
            val count = prevMap[2].toInt() + 1
            prevMap[2] = count.toString()
            map[row[0]] = prevMap
        }else{
            map[row[0]] = arrayOf(row[0],row[2],"1")
        }
    }
    return  map.filter { it.value[3].toInt() > 1 }.map { it.value }.toTypedArray()

}