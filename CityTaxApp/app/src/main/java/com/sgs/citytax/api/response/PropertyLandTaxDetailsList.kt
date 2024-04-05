package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PropertyTax4Business

data class PropertyLandTaxDetailsList(
        @SerializedName("Results")
        var results: PropertyTaxDetailsList? = null,
        @SerializedName("TotalRecords")
        var totalRecords: Int = 0
)
