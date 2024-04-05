package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class InitialOutstandingWaiveOff(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("InitialOutstandingID")
        var initialOutstandingID: Int? = 0,
        @SerializedName("Pecentage")
        var percentage: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("WaveOffAmt")
        var waiveOffAmt: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("Remarks")
        var remarks: String? = "",
        @SerializedName("filenameWithExt")
        var fileNameWithExtension: String? = "",
        @SerializedName("fileData")
        var fileData: String? = ""
)