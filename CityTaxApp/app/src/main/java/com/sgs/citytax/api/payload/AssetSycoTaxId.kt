package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AssetSycoTaxId(
        @SerializedName("AssetSycotaxID")
        var assetSycotaxID : String ?= ""

): Parcelable
{
        constructor(parcel: Parcel) : this(parcel.readString()) {
        }

        override fun toString(): String {
                return "$assetSycotaxID"
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(assetSycotaxID)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<AssetSycoTaxId> {
                override fun createFromParcel(parcel: Parcel): AssetSycoTaxId {
                        return AssetSycoTaxId(parcel)
                }

                override fun newArray(size: Int): Array<AssetSycoTaxId?> {
                        return arrayOfNulls(size)
                }
        }

}