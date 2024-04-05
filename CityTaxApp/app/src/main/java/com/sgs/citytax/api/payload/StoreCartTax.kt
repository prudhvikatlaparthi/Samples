package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.CartTax

data class StoreCartTax(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("carts")
        var cart: CartTax? = null
)