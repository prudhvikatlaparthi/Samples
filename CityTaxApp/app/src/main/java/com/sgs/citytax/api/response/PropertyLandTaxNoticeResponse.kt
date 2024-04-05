package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PropertyLandTaxNoticeDetails

data class PropertyLandTaxNoticeResponse(
        @SerializedName("Table")
        var  propertLandTaxNoticeDetails: ArrayList<PropertyLandTaxNoticeDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)