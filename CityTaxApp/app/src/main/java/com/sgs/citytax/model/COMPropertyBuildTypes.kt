package com.sgs.citytax.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class COMPropertyBuildTypes(
        @SerializedName("PropertyBuildTypeCode")
        var propertyBuildTypeCode: String? = "",
        @SerializedName("PropertyBuildType")
        var propertyBuildType: String? = "",
        @SerializedName("PropertyBuildTypeID")
        var propertyBuildTypeID: Int? = null,
        @SerializedName("prodcode")
        var prodcode: String? = ""

) : Parcelable {
    override fun toString(): String {
        return propertyBuildType ?: ""
    }
}
