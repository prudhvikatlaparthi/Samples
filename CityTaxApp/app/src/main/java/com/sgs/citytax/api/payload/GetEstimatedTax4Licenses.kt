package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.LicenseEstimatedTax

data class GetEstimatedTax4Licenses(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("licensesestmtdtax")
        var licenseEstimatedData: LicenseEstimatedTax? = null
)