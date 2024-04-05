package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LAWImpoundmentReason(
        @SerializedName("ImpoundmentReason")
        var impoundmentReason: String? = "",
        @SerializedName("ImpoundmentReasonID")
        var impoundmentReasonID: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    override fun toString(): String {
        return impoundmentReason ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(impoundmentReason)
        parcel.writeString(impoundmentReasonID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LAWImpoundmentReason> {
        override fun createFromParcel(parcel: Parcel): LAWImpoundmentReason {
            return LAWImpoundmentReason(parcel)
        }

        override fun newArray(size: Int): Array<LAWImpoundmentReason?> {
            return arrayOfNulls(size)
        }
    }
}