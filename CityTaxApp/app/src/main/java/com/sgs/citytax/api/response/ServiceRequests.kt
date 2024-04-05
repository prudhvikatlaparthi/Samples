package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.payload.NewServiceRequest

data class ServiceRequests(
        @SerializedName("ServiceTaxRequestList")
        var serviceRequests: List<NewServiceRequest> = arrayListOf(),
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords: Int = 0
)