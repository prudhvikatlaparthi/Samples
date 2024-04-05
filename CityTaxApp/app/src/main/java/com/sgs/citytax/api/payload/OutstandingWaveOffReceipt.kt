package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class OutstandingWaveOffReceipt(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("outstnwaveoffid")
        var outStandingWaiveOffId: Int? = 0,
        @SerializedName("recptcode")
        var receiptCode: String? = ""
)