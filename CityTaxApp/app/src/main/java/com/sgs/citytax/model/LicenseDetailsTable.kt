package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class LicenseDetailsTable(
        @SerializedName("LicensesDetails")
        var licenseDetails:ArrayList<LicenseDetails> = arrayListOf()
)