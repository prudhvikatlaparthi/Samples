package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.RoadTaxNoticeDetails

data class RoadTaxNoticeResponse(
        @SerializedName("Table")
        var  roadTaxNoticeDetails: ArrayList<RoadTaxNoticeDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)