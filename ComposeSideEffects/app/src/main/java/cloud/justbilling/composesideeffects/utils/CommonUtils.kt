package cloud.justbilling.composesideeffects.utils

import java.util.*

object CommonUtils {
    fun rand(start: Int = 100, end: Int = 1000): Int {
        require(!(start > end || end - start + 1 > Int.MAX_VALUE)) { "Illegal Argument" }
        return Random(System.nanoTime()).nextInt(end - start + 1) + start
    }
}