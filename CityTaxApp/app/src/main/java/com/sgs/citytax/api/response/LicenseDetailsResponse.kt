package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.LicenseDetailsTable

data class LicenseDetailsResponse(
        @SerializedName("Results")
        var details: LicenseDetailsTable? = null
)