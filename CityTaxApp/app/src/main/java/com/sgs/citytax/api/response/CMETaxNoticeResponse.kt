package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CMETaxNoticeDetails
import com.sgs.citytax.model.CMEVehicleDetails

data class CMETaxNoticeResponse(
        @SerializedName("Table")
        var cmeTaxNoticeDetails: ArrayList<CMETaxNoticeDetails> = arrayListOf(),
        @SerializedName("Table1")
        var cmeVehicleDetails: ArrayList<CMEVehicleDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)