package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ROPTaxNoticeDetails
import com.sgs.citytax.model.WeaponTaxNoticeDetails

data class WeaponTaxNoticeResponse(
        @SerializedName("Table")
        var weaponTaxNoticeDetails: ArrayList<WeaponTaxNoticeDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)