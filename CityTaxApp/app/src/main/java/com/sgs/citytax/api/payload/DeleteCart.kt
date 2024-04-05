package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteCart(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("cartid")
        var cartId: Int? = 0
)