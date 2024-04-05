package com.pru.playground

fun main() {
    println(twoSum(intArrayOf(2, 7, 11, 15), 9).contentToString())
}

fun twoSum(nums: IntArray, target: Int): IntArray {
    val map = mutableMapOf<Int, Int>()
    for (i in nums.indices) {
        val n = nums[i]
        if (map.containsKey(n)) {
            return intArrayOf(map[n]!!, i)
        } else {
            map[target - n] = i
        }
    }
    return intArrayOf()
}