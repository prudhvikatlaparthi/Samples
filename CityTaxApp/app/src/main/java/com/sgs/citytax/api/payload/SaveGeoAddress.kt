package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.GeoAddress

data class SaveGeoAddress(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("geoaddress")
        var geoAddress: GeoAddress? = null
)