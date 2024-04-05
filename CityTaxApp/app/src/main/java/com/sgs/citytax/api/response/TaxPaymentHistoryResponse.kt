package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TaxPaymentHistory

data class TaxPaymentHistoryResponse(
        @SerializedName("Results")
        var paymentHistories:ArrayList<TaxPaymentHistory> = arrayListOf()
)