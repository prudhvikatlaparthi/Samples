package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GetUpdateAssetSpecifications(
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("specid")
        var specificationID: Int? = 0,
        @SerializedName("val")
        var value: String? = "",
        @SerializedName("SpecificationValueID")
        var specificationValueID: Int? = 0,
        @SerializedName("DateValue")
        var dateValue: String? = null,
        @SerializedName("IsUpdateable")
        var isUpdateable: Boolean? = false,
        @SerializedName("spec")
        var specification: String? = "",
        @SerializedName("datatyp")
        var dataType: String? = "",
        @SerializedName("mand")
        var mandatory: String? = null,
        @SerializedName("DynamicForm")
        var dynamicForm: AssetSpecs? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(assetID)
        parcel.writeValue(specificationID)
        parcel.writeString(value)
        parcel.writeValue(specificationValueID)
        parcel.writeString(dateValue)
        parcel.writeValue(isUpdateable)
        parcel.writeString(specification)
        parcel.writeString(dataType)
        parcel.writeString(mandatory)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetUpdateAssetSpecifications> {
        override fun createFromParcel(parcel: Parcel): GetUpdateAssetSpecifications {
            return GetUpdateAssetSpecifications(parcel)
        }

        override fun newArray(size: Int): Array<GetUpdateAssetSpecifications?> {
            return arrayOfNulls(size)
        }
    }
}