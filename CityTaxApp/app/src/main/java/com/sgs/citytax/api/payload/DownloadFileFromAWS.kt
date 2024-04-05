package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DownloadFileFromAWS(
        val context:SecurityContext = SecurityContext(),
        @SerializedName("documentid")
        var documentId:Int?=0
)