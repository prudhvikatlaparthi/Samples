package com.pru.printlib.utilities

import android.content.Context

object Constants {

    enum class INCH2DP(val dp: Int) {
        TWO_INCH(400),
        THREE_INCH(480),
        FOUR_INCH(640),
        FIVE_INCH(800),
    }

    enum class DIMENSION(val d: Float) {
        NONE(0.0f),
        LDPI(0.75f),
        MDPI(1.0f),
        HDPI(1.5f),
        XHDPI(2.0f),
        XXHDPI(3.0f),
        XXXHDPI(4.0f);

        companion object {
            fun getDimension(context: Context): DIMENSION {
                return when (context.resources.displayMetrics.density) {
                    0.75f -> LDPI
                    1.0f -> MDPI
                    1.5f -> HDPI
                    2.0f -> XHDPI
                    3.0f -> XXHDPI
                    4.0f -> XXXHDPI
                    else -> NONE

                }
            }
        }
    }
}