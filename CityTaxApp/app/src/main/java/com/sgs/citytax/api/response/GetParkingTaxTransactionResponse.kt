package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetParkingTaxTransactionResponse(
        @SerializedName("Table")
        var results: List<ParkingPaymentTrans> = arrayListOf()
)