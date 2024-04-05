package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.COMPropertyImage

data class PropertyImageResponse(
        @SerializedName("PropertyImages")
        var propertyImages:ArrayList<COMPropertyImage> = arrayListOf()
)