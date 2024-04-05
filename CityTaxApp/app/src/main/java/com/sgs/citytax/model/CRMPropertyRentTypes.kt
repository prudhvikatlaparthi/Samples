package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMPropertyRentTypes(
        @SerializedName("RentType")
        var rentType: String? = "",
        @SerializedName("RentTypeCode")
        var rentTypeCode: String? = "",
        @SerializedName("RentTypeID")
        var rentTypeId: Int? = 0
) {
    override fun toString(): String {
        return rentType.toString()
    }
}