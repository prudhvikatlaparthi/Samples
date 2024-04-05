package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class PayPointLicensePaymentPrinting(
        var context:SecurityContext = SecurityContext(),
        @SerializedName("advrecvid")
        var advanceReceivedId:Int?=0,
        @SerializedName("recptcode")
        var receiptCode:String = "Paypoint_Payment_License"
)