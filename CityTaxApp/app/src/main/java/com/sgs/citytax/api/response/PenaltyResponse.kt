package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class PenaltyResponse(
        @SerializedName("Results")
        var results: PenaltyList
)