package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class TaxPaymentHistory(
        @SerializedName("dt")
        var transactionDate: String? = "",
        @SerializedName("amt")
        var amount: Double? = 0.0,
        @SerializedName("pmtmode")
        var paymentMode: String? = "",
        @SerializedName("ChequeStatus")
        var chequeStatus: String? = "",
        @Transient
        var isLoading: Boolean = false
)