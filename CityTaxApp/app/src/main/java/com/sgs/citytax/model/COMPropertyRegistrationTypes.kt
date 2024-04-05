package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class COMPropertyRegistrationTypes(
        @SerializedName("PropertyRegistrationType")
        var propertyRegistrationType: String? = "",
        @SerializedName("PropertyRegistrationTypeCode")
        var propertyRegistrationTypeCode: String? = "",
        @SerializedName("act")
        var act: String? = "",
        @SerializedName("PropertyRegistrationTypeID")
        var propertyRegistrationTypeID: Int? = null
){
    override fun toString(): String {
        return "$propertyRegistrationType"
    }
}