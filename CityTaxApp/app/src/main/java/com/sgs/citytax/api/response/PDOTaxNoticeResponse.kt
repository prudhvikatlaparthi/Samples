package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PDOTaxNoticeDetails

data class PDOTaxNoticeResponse(
        @SerializedName("Table")
        var pdoTaxNoticeDetails: ArrayList<PDOTaxNoticeDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null

)