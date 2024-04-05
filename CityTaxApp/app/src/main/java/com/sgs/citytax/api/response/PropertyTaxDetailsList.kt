package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PropertyTax4Business

data class PropertyTaxDetailsList(
        @SerializedName("PropetyTaxDetails",alternate = ["PropertyTaxDetails"])
        var propertyTaxDetails: ArrayList<PropertyTax4Business> = arrayListOf()
)
