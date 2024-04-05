package com.pru.playground

fun main() {
    println(isValidParentheses("()[]{}"))
}

fun isValidParentheses(s: String): Boolean {
    val stack = mutableListOf<Char>()
    for (c in s) {
        when (c) {
            '(', '{', '[' -> stack.add(c)
            ')' -> if (stack.isEmpty() || stack.removeAt(stack.size - 1) != '(') return false
            '}' -> if (stack.isEmpty() || stack.removeAt(stack.size - 1) != '{') return false
            ']' -> if (stack.isEmpty() || stack.removeAt(stack.size - 1) != '[') return false
        }
    }
    return stack.isEmpty()
}