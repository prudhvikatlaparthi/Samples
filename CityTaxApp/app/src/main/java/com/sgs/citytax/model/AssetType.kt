package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AssetType(
        @SerializedName("AssetTypeCode")
        var assetTypeCode: String? = "",
        @SerializedName("IsMovable")
        var isMovable: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(assetTypeCode)
        parcel.writeString(isMovable)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssetType> {
        override fun createFromParcel(parcel: Parcel): AssetType {
            return AssetType(parcel)
        }

        override fun newArray(size: Int): Array<AssetType?> {
            return arrayOfNulls(size)
        }
    }
}

