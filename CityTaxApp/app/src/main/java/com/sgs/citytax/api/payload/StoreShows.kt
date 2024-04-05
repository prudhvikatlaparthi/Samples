package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.model.ShowTaxData

data class StoreShows(
        val context:SecurityContext = SecurityContext(),
        @SerializedName("data")
        var taxData: ShowTaxData?=null,
        @SerializedName("geoaddress")
        var geoAddress: GeoAddress? = null
)