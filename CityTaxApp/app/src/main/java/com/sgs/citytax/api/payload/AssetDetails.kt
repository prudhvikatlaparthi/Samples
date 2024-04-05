package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AssetDetails(
        @SerializedName("AssetID")
        var assetID : String ?= "",
        @SerializedName("AssetNo")
        var assetNumber : String ?= "",
        @SerializedName("AssetCategoryID")
        var assetCategoryID : Int ?= 0,
        @SerializedName("AssetCategoryCode")
        var assetCategoryCode : String ?= "",
        @SerializedName("AssetCategory")
        var assetCategory : String ?= "",
        @SerializedName("AssetSycotaxID")
        var assetSycotaxID : String ?= ""

): Parcelable
{
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(assetID)
                parcel.writeString(assetNumber)
                parcel.writeValue(assetCategoryID)
                parcel.writeString(assetCategoryCode)
                parcel.writeString(assetCategory)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<AssetDetails> {
                override fun createFromParcel(parcel: Parcel): AssetDetails {
                        return AssetDetails(parcel)
                }

                override fun newArray(size: Int): Array<AssetDetails?> {
                        return arrayOfNulls(size)
                }
        }
        override fun toString(): String {
                return assetNumber + "\n" + assetSycotaxID + "\n"
        }

}