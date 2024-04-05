package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteAssetInsurance(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("InsuranceID")
        var primaryKeyValue: Int? = 0
)