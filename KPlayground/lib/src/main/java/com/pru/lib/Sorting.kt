package com.pru.lib


fun exclusiveTime(n: Int, logs: Array<String>): Array<Int> {
    val exclusiveTimes = Array(n)
    {
        0
    }
    val stack = mutableListOf<Pair<Int, Int>>() // Stack to keep track of function calls (function_id, start_timestamp)
    var prevTimestamp = 0
    for (log in logs) {
        val (functionId, action, timestamp) = log.split(":"         )
        val currentFunctionId = functionId.toInt()
        val currentTimestamp = timestamp.toInt()
        if (action ==
            "start"
        ) {
            if (stack.
                isNotEmpty
                    ()) {
                val prevFunctionId = stack.last().first
                exclusiveTimes[prevFunctionId] += currentTimestamp - prevTimestamp
            }
            stack.add(Pair(currentFunctionId, currentTimestamp))
            prevTimestamp = currentTimestamp
        } else {
            val (startFunctionId, startTimestamp) = stack.removeAt(stack.size - 1)
            exclusiveTimes[currentFunctionId] += currentTimestamp - prevTimestamp + 1
            prevTimestamp = currentTimestamp + 1
        }
    }
    return exclusiveTimes
}









