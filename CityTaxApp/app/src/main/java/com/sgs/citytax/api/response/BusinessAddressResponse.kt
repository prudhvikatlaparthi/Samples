package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class BusinessAddressResponse(
        @SerializedName("Results")
        var results: BusinessAddress
)