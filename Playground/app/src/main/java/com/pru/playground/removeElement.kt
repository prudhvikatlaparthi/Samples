package com.pru.playground

fun main() {
    println(removeElement1(intArrayOf(3, 2, 2, 3), 3).contentToString())
}

fun removeElement1(nums: IntArray, s: Int): IntArray {
    var diff = 0
    nums.forEachIndexed { i, v ->
        if (v != s) {
            diff++
            nums[diff] = v
        }
    }
    return nums
}