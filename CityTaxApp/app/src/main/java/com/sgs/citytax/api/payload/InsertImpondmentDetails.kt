package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.DocumentReferenceImpoundReturn
import com.sgs.citytax.model.ImpoundReturnLines

data class InsertImpondmentDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("id")
        var id: String? = "",
        @SerializedName("data")
        var data: DocumentReferenceImpoundReturn? = null,
        @SerializedName("impreturnlines")
        var impoundReturnLines: ImpoundReturnLines? = null
)