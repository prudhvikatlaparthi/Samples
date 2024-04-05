package com.pru.lib


fun main(){
    println(removeElement(nums = intArrayOf(0,1,2,2,3,0,4,2), `val` = 2))
}
fun removeElement(nums: IntArray, `val`: Int): Int {
    println(nums.contentToString())
    var k =0
    for (i in nums.indices){
        if (nums[i] != `val`){
            nums[k] = nums[i]
            k++
        }
    }
    println(nums.contentToString())
    return k
}