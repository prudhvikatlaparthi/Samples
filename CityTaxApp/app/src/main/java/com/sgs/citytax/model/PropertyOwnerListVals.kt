package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PropertyOwnerListVals(
        @SerializedName("propertyOwner")
        var propertyOwner: BusinessOwnership? = null,
        @SerializedName("propertyNominee")
        var propertyNominee: BusinessOwnership? = null,
        @SerializedName("relationShip")
        var relationShip: ComComboStaticValues? = null
)