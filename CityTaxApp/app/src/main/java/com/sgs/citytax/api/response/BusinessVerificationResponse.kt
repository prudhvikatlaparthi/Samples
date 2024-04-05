package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class BusinessVerificationResponse(
        @SerializedName("Results")
        var results: BusinessVerificationResult? = null
)