package com.pru.lib

class MyClass {


}

fun main() {
    print(singleNumber(intArrayOf(2,2,1)))
}

fun singleNumber(nums: IntArray): Int {
    var result = 0
    for (num in nums) {
        result = result xor num
    }
    return result
}

fun isPalindrome(s: String): Boolean {
    val word =  s.filter { it.isLetterOrDigit() }.lowercase()
    val reverseWord = word.reversed().lowercase()
    return word == reverseWord
}

open class Stu{
     open fun tripper() {

     }
}

class SStu : Stu() {
    override fun tripper() {
        super.tripper()
        println("sdffd")
    }
}