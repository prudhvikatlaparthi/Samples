package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ImpoundmentReturnResponse(
        @SerializedName("ImpoundmentID")
        var impoundmentID: Int? = 0,
        @SerializedName("ReturnLineID")
        var returnLineID: Int? = null
)
