package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class PenaltyWaiveOff(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("taxinvoiceid")
        var taxInvoiceID: Int? = null,
        @SerializedName("PenaltyID")
        var penaltyID: Int? = null,
        @SerializedName("PenaltyAmount")
        var penaltyAmount: BigDecimal? = null,
        @SerializedName("Pecentage")
        var percentage: BigDecimal? = null,
        @SerializedName("WaveOffAmt")
        var waiveOffAmount: BigDecimal? = null,
        @SerializedName("Remarks")
        var remarks: String? = "",
        @SerializedName("filenameWithExt")
        var filenameWithExt: String? = "",
        @SerializedName("fileData")
        var fileData: String? = ""
)