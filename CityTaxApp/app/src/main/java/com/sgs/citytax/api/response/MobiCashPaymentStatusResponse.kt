package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class MobiCashPaymentStatusResponse(
        @SerializedName("message")
        var message: String? = null,
        @SerializedName("PaymentStatus")
        var paymentStatus: String? = null
)