package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetIndividualTax(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tblname")
        var tableName: String? = "",
        @SerializedName("colname")
        var columnName: String? = "",
        @SerializedName("sycotaxid")
        var sycoTaxID: String? = ""
)