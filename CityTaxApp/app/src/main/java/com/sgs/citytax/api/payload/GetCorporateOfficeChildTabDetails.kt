package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetCorporateOfficeChildTabDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tablname")
        var tableName: String = "",
        @SerializedName("acctid")
        var taxPayerAccountID: Int = 0,
        @SerializedName("orgzid")
        var taxPayerOrganizationID: Int = 0,
        @SerializedName("prykeyval")
        var primaryKeyValue: Int = 0,
        @SerializedName("tskcode")
        var taskCode: String? = null
)