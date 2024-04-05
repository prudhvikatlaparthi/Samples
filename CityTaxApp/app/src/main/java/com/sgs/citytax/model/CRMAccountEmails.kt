package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CRMAccountEmails(
        @SerializedName("acctid")
        var accountId: Int? = 0,
        var EmailType: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("AccountEmailID")
        var accountEmailID: Int? = 0,
        @SerializedName("defntn")
        var default: String? = null,
        @SerializedName("EmailVerified", alternate = ["IsVerified"])
        var verified: String? = "N"
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(accountId)
        parcel.writeString(EmailType)
        parcel.writeString(email)
        parcel.writeValue(accountEmailID)
        parcel.writeString(default)
        parcel.writeString(verified)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CRMAccountEmails> {
        override fun createFromParcel(parcel: Parcel): CRMAccountEmails {
            return CRMAccountEmails(parcel)
        }

        override fun newArray(size: Int): Array<CRMAccountEmails?> {
            return arrayOfNulls(size)
        }
    }
}