package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.LicensePlanElements
import com.sgs.citytax.model.LicenseReceiptDetails

data class LicenseRenewalReceiptResponse(
        @SerializedName("Table")
        var licenseReceiptDetails: ArrayList<LicenseReceiptDetails> = arrayListOf(),
        @SerializedName("Table1")
        var licenseElementPlans: ArrayList<LicensePlanElements> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)