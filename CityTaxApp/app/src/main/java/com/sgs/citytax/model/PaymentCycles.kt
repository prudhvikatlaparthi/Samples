package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PaymentCycles(
        @SerializedName("PaymentCycle")
        var paymentCycle: String? = "",
        @SerializedName("PaymentCycleID")
        var paymentCycleID: Int? = 0,
        @SerializedName("TaxPeriod")
        var taxPeriod: Int? = 0

) {
    override fun toString(): String {
        return "$paymentCycle"
    }
}