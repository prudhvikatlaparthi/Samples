package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.payload.StorePropertyData
import com.sgs.citytax.model.GeoAddress

data class PropertyDetailsBySycoTax(
        @SerializedName("IsSycotaxAvailable")
        var isSycoTaxAvailable: Boolean = false,
        @SerializedName("PropertyDetails")
        var propertyDetails: StorePropertyData? = null,
        @SerializedName("Address")
        val address: ArrayList<GeoAddress> = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(),
            parcel.readParcelable(StorePropertyData::class.java.classLoader),
            parcel.createTypedArrayList(GeoAddress) as ArrayList<GeoAddress>

//            parcel.createTypedArrayList(GeoAddress.CREATOR) as ArrayList<GeoAddress>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isSycoTaxAvailable) 1 else 0)
        parcel.writeParcelable(propertyDetails, flags)
        parcel.writeTypedList(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PropertyDetailsBySycoTax> {
        override fun createFromParcel(parcel: Parcel): PropertyDetailsBySycoTax {
            return PropertyDetailsBySycoTax(parcel)
        }

        override fun newArray(size: Int): Array<PropertyDetailsBySycoTax?> {
            return arrayOfNulls(size)
        }
    }
}
