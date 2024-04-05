package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class StoreInsuranceData(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("data")
        var data: StoreInsuranceDocument? = null,
        @SerializedName("filenameWithExt")
        var filenameWithExt: String? = "",
        @SerializedName("fileData")
        var fileData: String? = ""
)