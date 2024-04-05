package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class UploadLogFilePayload(
    var context: SecurityContext = SecurityContext(),
    @SerializedName("filename")
    var fileName: String? = null,
    @SerializedName("lfstr")
    var logFileString: String? = null
)
