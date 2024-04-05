package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ROPTaxNoticeDetails

data class ROPTaxNoticeResponse(
        @SerializedName("Table")
        var ropTaxNoticeDetails: ArrayList<ROPTaxNoticeDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)