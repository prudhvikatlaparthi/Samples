package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AccountPhone(
        @SerializedName("AccountPhoneID")
        var accountPhoneID: Int? = 0,
        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("PhoneType")
        var phoneType: String? = "",
        @SerializedName("Number")
        var number: String? = "",
        @SerializedName("defntn")
        var default: String? = "",
        @SerializedName("telcode")
        var telCode: Int? = 0,
        @SerializedName("PhoneVerified", alternate = ["IsVerified"])
        var verified: String? = "N"
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(accountPhoneID)
        parcel.writeValue(accountID)
        parcel.writeString(phoneType)
        parcel.writeString(number)
        parcel.writeString(default)
        parcel.writeValue(telCode)
        parcel.writeString(verified)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccountPhone> {
        override fun createFromParcel(parcel: Parcel): AccountPhone {
            return AccountPhone(parcel)
        }

        override fun newArray(size: Int): Array<AccountPhone?> {
            return arrayOfNulls(size)
        }
    }
}