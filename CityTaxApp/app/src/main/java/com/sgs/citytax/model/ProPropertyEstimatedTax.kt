package com.sgs.citytax.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProPropertyEstimatedTax(
        @SerializedName("PropertyBuildTypeID")
        var propertyBuildTypeID: Int? = 0,
        @SerializedName("PropertyValue")
        var propertyValue: Double? = 0.0
) : Parcelable
