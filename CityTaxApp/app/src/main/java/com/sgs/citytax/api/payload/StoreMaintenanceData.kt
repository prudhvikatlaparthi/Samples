package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class StoreMaintenanceData(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("data")
        var data: StoreMaintenance? = null,
        @SerializedName("filenameWithExt")
        var filenameWithExt: String? = "",
        @SerializedName("fileData")
        var fileData: String? = ""
)