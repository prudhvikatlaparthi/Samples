package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.CRMPropertyMaster
import com.sgs.citytax.model.GeoAddress

data class InsertPropertyOwnership(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("PropertyOwnership")
        var savePropertyOwnership: SavePropertyOwnership? = null,
        @SerializedName("PropertyMaster")
        var propertyMaster: CRMPropertyMaster? = null,
        @SerializedName("dicGeoAddress")
        var geoAddress: GeoAddress? = null
)