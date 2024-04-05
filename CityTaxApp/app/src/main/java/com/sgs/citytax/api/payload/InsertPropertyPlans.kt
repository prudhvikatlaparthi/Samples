package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMPropertyPlanImage

data class InsertPropertyPlans(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("propertyplan")
        var propertyPlan: COMPropertyPlanImage? = null
)