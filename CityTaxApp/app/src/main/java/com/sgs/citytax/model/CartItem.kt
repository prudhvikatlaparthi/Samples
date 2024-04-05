package com.sgs.citytax.model

import com.sgs.citytax.api.response.SellableProduct
import com.sgs.citytax.util.getRoundValue
import java.math.BigDecimal

data class CartItem(
        var item: SellableProduct? = null,
        var quantity: Int = 0
) {
    fun getFinalPrice(): BigDecimal {
        return item?.unitPrice?.multiply(BigDecimal(quantity)) ?: BigDecimal.ZERO
    }

    fun getRoundedFinalPrice(roundingPlace: Int = 0): BigDecimal {
        return getRoundValue(getFinalPrice(), roundingPlace)
    }

    fun getRounding(roundingPlace: Int = 0): BigDecimal {
        return getFinalPrice() - getRoundedFinalPrice(roundingPlace)
    }
}