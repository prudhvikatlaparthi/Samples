package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.BusinessLocations

data class BusinessLocation4Agent(
        @SerializedName("BusinessLocation4Agent")
        var businessLocations: List<BusinessLocations> = arrayListOf(),
        @SerializedName("TotalRecordCounts")
        var totalRecordCounts: Int = 0
)