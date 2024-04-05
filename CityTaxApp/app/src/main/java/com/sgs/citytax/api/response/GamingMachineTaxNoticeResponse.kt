package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.GamingMachineTaxNoticeDetails

data class GamingMachineTaxNoticeResponse(
        @SerializedName("Table")
        var gamingMachineTaxNoticeDetails: ArrayList<GamingMachineTaxNoticeDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)