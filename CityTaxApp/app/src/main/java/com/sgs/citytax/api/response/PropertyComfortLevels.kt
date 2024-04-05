package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PropertyComfortLevel

data class PropertyComfortLevels(
        @SerializedName("PropertyComfortLevels")
        var propertyComfortLevels: PropertyComfortLevel? = null
)