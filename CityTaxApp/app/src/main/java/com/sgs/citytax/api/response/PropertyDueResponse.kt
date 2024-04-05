package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class PropertyDueResponse(
        @SerializedName("PropertyDueSummary")
        var dueSummaries: ArrayList<BusinessDueSummary> = arrayListOf()
)