package com.sgs.citytax.api.payload


import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GenerateSalesTaxAndPaymentPayload(
    @SerializedName("context")
    val context: SecurityContext?,
    @SerializedName("data")
    val data: Data?
)
