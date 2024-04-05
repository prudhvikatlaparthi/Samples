package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.BusinessOwnership

data class BusinessOwner(
        @SerializedName("VU_CRM_OwnerDetails")
        var businessOwner: ArrayList<BusinessOwnership> = arrayListOf()
)
