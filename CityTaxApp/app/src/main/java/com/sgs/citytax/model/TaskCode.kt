package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TaskCode(
        @SerializedName("tskcode")
        var taskCode: String? = "",
        var IsMultiple: Char = 'N'
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt().toChar())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(taskCode)
        parcel.writeInt(IsMultiple.toInt())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskCode> {
        override fun createFromParcel(parcel: Parcel): TaskCode {
            return TaskCode(parcel)
        }

        override fun newArray(size: Int): Array<TaskCode?> {
            return arrayOfNulls(size)
        }
    }
}