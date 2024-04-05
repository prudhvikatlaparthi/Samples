package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ChildTabCountResponse(
        @SerializedName("Count")
        var count: Int? = 0
)
