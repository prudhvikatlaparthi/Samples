package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ASTAssetRentPreCheckLists(
        @SerializedName("AssetRentID")
        var assetRentId: Int? = 0,
        @SerializedName("specid")
        var specificationId: Int? = 0,
        @SerializedName("val")
        var value: String? = "",
        @SerializedName("SpecificationValueID")
        var specificationValueId: Int? = 0,
        @SerializedName("DateValue")
        var dateValue: String? = ""
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(assetRentId)
                parcel.writeValue(specificationId)
                parcel.writeString(value)
                parcel.writeValue(specificationValueId)
                parcel.writeString(dateValue)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<ASTAssetRentPreCheckLists> {
                override fun createFromParcel(parcel: Parcel): ASTAssetRentPreCheckLists {
                        return ASTAssetRentPreCheckLists(parcel)
                }

                override fun newArray(size: Int): Array<ASTAssetRentPreCheckLists?> {
                        return arrayOfNulls(size)
                }
        }
}