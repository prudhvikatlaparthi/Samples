package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Business(
        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("acctname")
        var acctname: String? = "",
        @SerializedName("SycotaxID")
        var sycotaxID: String? = "",
        @SerializedName("Number")
        var number: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("Numbers")
        var numbers: String? = "",
        @SerializedName("Emails")
        var emails: String? = "",
        @SerializedName("Owners")
        var owners: String? = "",
        @SerializedName("ActivityDomainID")
        var activityDomainID: Int? = 0,
        @SerializedName("ActivityClassID")
        var activityClassID: Int? = 0,
        @SerializedName("znid")
        var znid: Int? = 0,
        @SerializedName("SectorID")
        var sectorID: Int? = 0,
        @SerializedName("stscode")
        var stscode: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(accountID)
        parcel.writeString(acctname)
        parcel.writeString(sycotaxID)
        parcel.writeString(number)
        parcel.writeString(email)
        parcel.writeString(numbers)
        parcel.writeString(emails)
        parcel.writeString(owners)
        parcel.writeValue(activityDomainID)
        parcel.writeValue(activityClassID)
        parcel.writeValue(znid)
        parcel.writeValue(sectorID)
        parcel.writeString(stscode)
        parcel.writeString(businessName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Business> {
        override fun createFromParcel(parcel: Parcel): Business {
            return Business(parcel)
        }

        override fun newArray(size: Int): Array<Business?> {
            return arrayOfNulls(size)
        }
    }
}