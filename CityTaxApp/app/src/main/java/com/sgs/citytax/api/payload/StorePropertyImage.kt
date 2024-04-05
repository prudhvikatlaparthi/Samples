package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMPropertyImage

data class StorePropertyImage(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("propertyimage")
        var propertyImage: COMPropertyImage? = null
)