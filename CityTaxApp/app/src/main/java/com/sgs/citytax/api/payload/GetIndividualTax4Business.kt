package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetIndividualTax4Business(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tblname")
        var tableName: String = "",
        @SerializedName("acctid")
        var primaryKeyValue: Int? = 0,
        @SerializedName("indtaxvchrno")
        var indtaxvchrno:Int? =0,
        @SerializedName("isdetailsforsummary")
        var isdetailsforsummary: Boolean? = false
)