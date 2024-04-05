package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class COMNotes(
        @SerializedName("sub")
        var Subject: String? = "",
        @SerializedName("note")
        var Note: String? = "",
        @SerializedName("noteid")
        var NoteID: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Subject)
        parcel.writeString(Note)
        parcel.writeValue(NoteID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<COMNotes> {
        override fun createFromParcel(parcel: Parcel): COMNotes {
            return COMNotes(parcel)
        }

        override fun newArray(size: Int): Array<COMNotes?> {
            return arrayOfNulls(size)
        }
    }

}