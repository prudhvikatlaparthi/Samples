package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class StoreFitnessData(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("data")
        var data: StoreFitness? = null,
        @SerializedName("filenameWithExt")
        var filenameWithExt: String? = "",
        @SerializedName("fileData")
        var fileData: String? = ""
)