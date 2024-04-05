package com.sgs.citytax.api.payload


import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetInventoryProductsDetailsPayload(
    var context: SecurityContext = SecurityContext(),
    @SerializedName("FromAccountID")
    var FromAccountID: Int? = null,
    @SerializedName("ToAccountID")
    var ToAccountID: Int? = null
)