package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetAgentList(
        @SerializedName("Results")
        var results: GetAgentResult? = null
)