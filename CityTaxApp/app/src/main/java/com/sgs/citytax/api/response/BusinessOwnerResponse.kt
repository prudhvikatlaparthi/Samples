package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class BusinessOwnerResponse(
        @SerializedName("Results")
        var results: BusinessOwner
)