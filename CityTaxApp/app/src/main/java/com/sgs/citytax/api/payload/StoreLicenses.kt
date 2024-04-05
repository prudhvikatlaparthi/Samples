package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.LicensePayloadData

data class StoreLicenses(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var data: LicensePayloadData? = null
)