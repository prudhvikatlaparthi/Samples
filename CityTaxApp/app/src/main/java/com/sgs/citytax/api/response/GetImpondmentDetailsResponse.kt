package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetImpondmentDetailsResponse(
        @SerializedName("Impoundment")
        var impoundment: ImpondmentDetails
)