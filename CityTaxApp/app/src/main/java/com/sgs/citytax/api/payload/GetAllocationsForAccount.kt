package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetAllocationsForAccount(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("accID")
        var accountID: Int? = 0,
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            TODO("context"),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(accountID)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetAllocationsForAccount> {
        override fun createFromParcel(parcel: Parcel): GetAllocationsForAccount {
            return GetAllocationsForAccount(parcel)
        }

        override fun newArray(size: Int): Array<GetAllocationsForAccount?> {
            return arrayOfNulls(size)
        }
    }
}