package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.payload.Asset
import com.sgs.citytax.model.GeoAddress

data class GetUpdateAsset(
        @SerializedName("asset", alternate = ["Assets"])
        var asset: Asset? = null,
        @SerializedName("add")
        var geoAddress: GeoAddress? = GeoAddress(),
        @SerializedName("Specifications")
        var assetSpecifications: ArrayList<GetUpdateAssetSpecifications>? = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Asset::class.java.classLoader),
            parcel.readParcelable(GeoAddress::class.java.classLoader),
            parcel.createTypedArrayList(GetUpdateAssetSpecifications))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(asset, flags)
        parcel.writeParcelable(geoAddress, flags)
        parcel.writeTypedList(assetSpecifications)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetUpdateAsset> {
        override fun createFromParcel(parcel: Parcel): GetUpdateAsset {
            return GetUpdateAsset(parcel)
        }

        override fun newArray(size: Int): Array<GetUpdateAsset?> {
            return arrayOfNulls(size)
        }
    }
}