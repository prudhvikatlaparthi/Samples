package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable

data class PropertyOwnersData (
        var owner:BusinessOwnership? = null,
        var nominee:BusinessOwnership? = null,
        var relation:ComComboStaticValues? = null
):Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(BusinessOwnership::class.java.classLoader),
            parcel.readParcelable(BusinessOwnership::class.java.classLoader),
            parcel.readParcelable(ComComboStaticValues::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(owner, flags)
        parcel.writeParcelable(nominee, flags)
        parcel.writeParcelable(relation, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PropertyOwnersData> {
        override fun createFromParcel(parcel: Parcel): PropertyOwnersData {
            return PropertyOwnersData(parcel)
        }

        override fun newArray(size: Int): Array<PropertyOwnersData?> {
            return arrayOfNulls(size)
        }
    }

}