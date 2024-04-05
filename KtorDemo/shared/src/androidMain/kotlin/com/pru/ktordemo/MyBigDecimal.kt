package com.pru.ktordemo

import java.math.BigDecimal

actual class MyBigDecimal actual constructor(private val value: String?) : BigDecimal(value ?: "0.0") {
    override fun toByte(): Byte {
        return BigDecimal(value).toByte()
    }

    override fun toChar(): Char {
        return BigDecimal(value).toChar()
    }

    override fun toShort(): Short {
        return BigDecimal(value).toShort()
    }
}