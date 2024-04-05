package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AdvertisementTaxNoticeDetails

data class AdvertisementTaxNoticeResponse(
        @SerializedName("Table")
        var advertisementTaxNoticeDetails: ArrayList<AdvertisementTaxNoticeDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)