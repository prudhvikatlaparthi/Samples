package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteShow(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("id")
        var showID: Int? = 0
)