package com.pru.lib

fun searchInsert(nums: IntArray, target: Int): Int {
    for (i in nums.indices) {
        if (nums[i] == target) {
            return i
        } else if (nums[i] > target) {
            return i + 1
        }
    }
    return nums.size
}