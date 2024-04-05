package com.sgs.citytax.model

import com.sgs.citytax.api.response.SalesProductData
import com.sgs.citytax.util.getRoundValue
import java.math.BigDecimal
import java.math.BigInteger

data class ProductItem (
    var product: SalesProductData? = null,
    var quantity: BigDecimal = BigDecimal.ZERO,
    var no_of_days: BigInteger = BigInteger.ZERO,
    var no_of_persons: BigInteger = BigInteger.ZERO,
    var total: BigDecimal = BigDecimal.ZERO,
    var rounding: BigDecimal = BigDecimal.ZERO
){
    fun getFinalPrice(): BigDecimal {
        return product?.unitPrice?.multiply(quantity) ?: BigDecimal.ZERO
    }

    fun getRoundedFinalPrice(roundingPlace: Int = 0): BigDecimal {
        return getRoundValue(getFinalPrice(), roundingPlace)
    }

    fun getRounding(roundingPlace: Int = 0): BigDecimal {
        return getFinalPrice() - getRoundedFinalPrice(roundingPlace)
    }
}