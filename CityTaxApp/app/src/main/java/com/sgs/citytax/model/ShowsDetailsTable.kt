package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ShowsDetailsTable(
        @SerializedName("acctid")
        var acctid: Int? = 0,
        @SerializedName("ShowID")
        var showID: Int? = 0,
        @SerializedName("orgzid")
        var oraganisationId: Int? = 0,
        @SerializedName("ShowName")
        var showName: String? = "",
        @SerializedName("OperatorTypeID")
        var operatorTypeID: Int? = 0,
        @SerializedName("GeoAddressID")
        var geoAddressId: Int? = 0,
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("desc")
        var description: String? = "",
        @SerializedName("OperatorType")
        var operatorType: String? = "",
        @SerializedName("OperatorTypeCode")
        var operatorTypeCode: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("AllowDelete")
        var allowDelete: String? = "",
        @SerializedName("GeoAddress")
        var geoAddress: ArrayList<GeoAddress>? = arrayListOf(),

        @Transient
        var isLoading:Boolean = false,
        var documents :ArrayList<COMDocumentReference> = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(GeoAddress)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(acctid)
        parcel.writeValue(showID)
        parcel.writeValue(oraganisationId)
        parcel.writeString(showName)
        parcel.writeValue(operatorTypeID)
        parcel.writeValue(geoAddressId)
        parcel.writeString(startDate)
        parcel.writeString(active)
        parcel.writeString(description)
        parcel.writeString(operatorType)
        parcel.writeString(operatorTypeCode)
        parcel.writeString(billingCycle)
        parcel.writeString(allowDelete)
        parcel.writeTypedList(geoAddress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShowsDetailsTable> {
        override fun createFromParcel(parcel: Parcel): ShowsDetailsTable {
            return ShowsDetailsTable(parcel)
        }

        override fun newArray(size: Int): Array<ShowsDetailsTable?> {
            return arrayOfNulls(size)
        }
    }
}