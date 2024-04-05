package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class DocumentReferenceImpoundReturn(
        @SerializedName("handoverimage")
        var handoverimage: String? = "",
        @SerializedName("handoverimagefn")
        var handoverimagefn: String? = null,
        @SerializedName("ownersignatureimg")
        var ownersignatureimg: String? = "",
        @SerializedName("ownersignatureidfn")
        var ownersignatureidfn: String? = null,
        @SerializedName("returnagentsignatureimg")
        var returnagentsignatureimg: String? = "",
        @SerializedName("returnagentsignaturefn")
        var returnagentsignaturefn: String? = null,
        @SerializedName("ReturnRemarks")
        var returnRemarks: String? = null

) : Parcelable {
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
        parcel.writeString(handoverimage)
        parcel.writeString(handoverimagefn)
        parcel.writeString(ownersignatureimg)
        parcel.writeString(ownersignatureidfn)
        parcel.writeString(returnagentsignatureimg)
        parcel.writeString(returnagentsignaturefn)
        parcel.writeString(returnRemarks)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DocumentReferenceImpoundReturn> {
        override fun createFromParcel(parcel: Parcel): DocumentReferenceImpoundReturn {
            return DocumentReferenceImpoundReturn(parcel)
        }

        override fun newArray(size: Int): Array<DocumentReferenceImpoundReturn?> {
            return arrayOfNulls(size)
        }
    }

}