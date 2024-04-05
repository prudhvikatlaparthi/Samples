package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PaymentCycle

data class AssetDistanceAndDurationRateResponse(
        @SerializedName("paymentcycle")
        var paymentCycles:ArrayList<PaymentCycle> = arrayListOf()
)