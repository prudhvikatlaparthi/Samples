package com.pru.misc

import java.util.*
import kotlin.collections.HashMap

fun main() {
    val data = "[({})]"
    println(if (isBalanced(data)) "isBalanced" else "Not Balanced")
    println(reverseString("ASDFGHJKL"))
    println(removeDuplicates("12234434334er1eerrr2"))
    mostlyRepeatedString("aasssabbbb")
}

private fun isBalanced(
    data: String
): Boolean {
    val stack = ArrayDeque<Char>()
    for (c in data) {
        if (c == '[' || c == '(' || c == '{') {
            stack.push(c)
            continue
        }
        if (stack.isEmpty()) {
            return false
        }
        when (c) {
            '}' ->
                if (stack.pop() != '{') {
                    return false
                }
            ')' ->
                if (stack.pop() != '(') {
                    return false
                }
            ']' ->
                if (stack.pop() != '[') {
                    return false
                }
        }
    }
    return stack.isEmpty()
}

private fun reverseString(value: String): String {
    val stack = ArrayDeque<Char>()
    value.forEach {
        stack.push(it)
    }
    return stack.joinToString("") {
        it.toString()
    }
}

private fun removeDuplicates(value: String): String {
    val stack = HashMap<Char, Int>()
    value.forEach {
        if (stack.containsKey(it)) {
            stack[it] = stack[it] as Int +1
        } else {
            stack[it] = 1
        }
    }
    return stack.toString()
}

private fun mostlyRepeatedString(value: String) {
    val map = TreeMap<Char, Int>()
    value.forEach {
        if (map.containsKey(it)) {
            map[it] = map[it] as Int +1
        } else {
            map[it] = 1
        }
    }
    println(map)
    var char  = value[0]
    var numb = 1
    for (e in map.entries){
        if (e.value >= numb){
            numb = e.value
            char = e.key
        }
    }
    println("$char - $numb")
}