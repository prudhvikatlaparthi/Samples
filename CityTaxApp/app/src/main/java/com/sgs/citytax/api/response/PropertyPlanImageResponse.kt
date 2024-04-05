package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.COMPropertyImage
import com.sgs.citytax.model.COMPropertyPlanImage

data class PropertyPlanImageResponse(
        @SerializedName("PropertyPlans")
        var propertyplans:ArrayList<COMPropertyPlanImage> = arrayListOf()
)