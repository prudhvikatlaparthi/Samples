package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class PenaltyWaiveOffReceiptDetails(
        var context:SecurityContext = SecurityContext(),
        @SerializedName("waiveoffid")
        var waiveOffId:Int?=0,
        @SerializedName("recptcode")
        var receiptCode: String? = "",
)