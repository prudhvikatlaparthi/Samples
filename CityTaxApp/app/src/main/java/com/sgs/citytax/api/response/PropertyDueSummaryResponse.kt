package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PropertyDueSummary

data class PropertyDueSummaryResponse(
        @SerializedName("PropertyTaxDueYearSummary")
        var propertyDueSummary:List<PropertyDueSummary> = arrayListOf()
)