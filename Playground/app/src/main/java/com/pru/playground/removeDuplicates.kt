package com.pru.playground

fun main() {
    println(removeDuplicates(intArrayOf(0, 0, 1, 1, 1, 2, 2, 3, 3, 4)))
}

fun removeDuplicates(nums: IntArray): Int {
    /*val map = mutableMapOf<Int, Int>()
    nums.forEach {
        if (map.containsKey(it)) {
            val prev = map[it] ?: 1
            map[it] = prev + 1
        } else {
            map[it] = 1
        }
    }
    var index = 0
    map.forEach { (t, _) ->
        nums[index] = t
        index++
    }
    return map.size*/
    val distinct = nums.toSet()
    distinct.forEachIndexed { index, value ->
        nums[index] = value
    }
    return distinct.size
}