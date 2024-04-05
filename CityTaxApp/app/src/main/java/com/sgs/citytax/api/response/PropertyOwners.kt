package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class PropertyOwners(

        @SerializedName("PropertyOwners")
        var propertyOwners: ArrayList<StorePropertyOwnershipWithPropertyOwnerResponse> = arrayListOf()

)
