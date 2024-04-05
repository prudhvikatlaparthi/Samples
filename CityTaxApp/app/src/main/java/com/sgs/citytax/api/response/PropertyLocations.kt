package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PropertyDetailLocation

data class PropertyLocations(
        @SerializedName("PropertyLocations")
        var propertyDetailLocations: List<PropertyDetailLocation> = arrayListOf(),
        @SerializedName("TotalRecordCounts")
        var totalRecordCounts: Int = 0
)