package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class COMDocumentReference(
        @SerializedName("doctypid")
        var documentTypeID: Int? = 0,
        @SerializedName("docprftyp")
        var documentProofType: String? = null,
        @SerializedName("docno")
        var documentNo: String? = null,
        @SerializedName("verifd")
        var verified: String? = "N",
        @SerializedName("filetitle")
        var documentName: String? = null,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("DataSource")
        var data: String? = "",
        @SerializedName("fileext")
        var extension: String? = null,
        @SerializedName("docid")
        var documentID: String? = "",
        @SerializedName("docrefid")
        var documentReferenceID: String? = "",
        @SerializedName("AWSPath")
        var awsfile: String? =  "",
        @SerializedName("docrtyp")
        var documentTypeName: String? = "",

        //mdoel for parent document page
        @SerializedName("proprtyid")
        var propertyID: String? = "",
        var localPath : String? = null

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
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(documentTypeID)
        parcel.writeString(documentProofType)
        parcel.writeString(documentNo)
        parcel.writeString(verified)
        parcel.writeString(documentName)
        parcel.writeString(remarks)
        parcel.writeString(data)
        parcel.writeString(extension)
        parcel.writeString(documentID)
        parcel.writeString(documentReferenceID)
        parcel.writeString(awsfile)
        parcel.writeString(documentTypeName)
        parcel.writeString(propertyID)
        parcel.writeString(propertyID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<COMDocumentReference> {
        override fun createFromParcel(parcel: Parcel): COMDocumentReference {
            return COMDocumentReference(parcel)
        }

        override fun newArray(size: Int): Array<COMDocumentReference?> {
            return arrayOfNulls(size)
        }
    }
}