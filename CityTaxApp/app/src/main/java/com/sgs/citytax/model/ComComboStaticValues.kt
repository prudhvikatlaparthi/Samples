package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ComComboStaticValues(
        @SerializedName("cmbcode")
        var comboCode: String? = "",
        @SerializedName("cmbval")
        var comboValue: String? = "",
        @SerializedName("code")
        var code: String? = null,
        @SerializedName("desc")
        var desc: String? = null,
        @SerializedName("defntn")
        var defntn: String? = null,
        @SerializedName("IsVisible")
        var isVisible: String? = null,
        @SerializedName("act")
        var act: String? = null
):Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(comboCode)
        parcel.writeString(comboValue)
        parcel.writeString(code)
        parcel.writeString(desc)
        parcel.writeString(defntn)
        parcel.writeString(isVisible)
        parcel.writeString(act)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "${comboValue.toString()}\n"
    }

    companion object CREATOR : Parcelable.Creator<ComComboStaticValues> {
        override fun createFromParcel(parcel: Parcel): ComComboStaticValues {
            return ComComboStaticValues(parcel)
        }

        override fun newArray(size: Int): Array<ComComboStaticValues?> {
            return arrayOfNulls(size)
        }
    }



}