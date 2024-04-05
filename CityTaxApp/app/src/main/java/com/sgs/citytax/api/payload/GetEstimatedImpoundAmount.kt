package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.DataTaxableMatter

data class GetEstimatedImpoundAmount(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("impoundmenttypeid")
        var impoundmentTypeID: Int? = 0,
        @SerializedName("impoundmentid")
        var impoundmentid: Int? = 0,
        @SerializedName("qty")
        var quantity: String? = ""
)