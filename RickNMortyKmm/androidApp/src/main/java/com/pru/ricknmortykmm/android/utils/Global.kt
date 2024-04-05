package com.pru.ricknmortykmm.android.utils

import java.math.BigDecimal
import java.math.RoundingMode

object Global {

    val Double?.bigDecimal: BigDecimal
        get() = this?.toBigDecimal() ?: BigDecimal.ZERO

    val Double?.bigDecimalScale: BigDecimal
        get() = this?.toBigDecimal()?.setScale(2, RoundingMode.HALF_EVEN) ?: BigDecimal.ZERO
}