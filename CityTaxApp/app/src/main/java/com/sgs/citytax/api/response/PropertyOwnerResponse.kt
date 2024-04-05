package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.COMPropertyImage
import com.sgs.citytax.model.COMPropertyOwner

data class PropertyOwnerResponse(
        @SerializedName("PropertyOwners")
        var propertyOwners:ArrayList<COMPropertyOwner>? = arrayListOf()
)