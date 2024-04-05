package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AssetFitnessesData(
        @SerializedName("FitnessID")
        var fitnessID: Int? = 0,
        @SerializedName("FitnessTypeID")
        var fitnessTypeID: Int? = 0,
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("FitnessNo")
        var fitnessNo: Int? = 0,
        @SerializedName("FitnessDate")
        var fitnessDate: String? = null,
        @SerializedName("Vendor")
        var vendor: String? = null,
        @SerializedName("FromDate")
        var fromDate: String? = null,
        @SerializedName("ExpiryDate")
        var expiryDate: String? = null,
        @SerializedName("InvoiceReference")
        var invoiceReference: String? = null,
        @SerializedName("DocumentID")
        var documentID: Int? = 0,
        @SerializedName("Remarks")
        var remarks: String? = null,
        @SerializedName("FitnessType")
        var fitnessType: String? = null,
        @SerializedName("AssetNo")
        var assetNo: String? = null,
        @SerializedName("FileName")
        var fileName: String? = null,
        @SerializedName("FileExt")
        var fileExt: String? = null,
        @SerializedName("AWSPath")
        var awsPath: String? = "",
        @SerializedName("Cost")
        var cost: BigDecimal? = BigDecimal.ZERO
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(fitnessID)
        parcel.writeValue(fitnessTypeID)
        parcel.writeValue(assetID)
        parcel.writeValue(fitnessNo)
        parcel.writeString(fitnessDate)
        parcel.writeString(vendor)
        parcel.writeString(fromDate)
        parcel.writeString(expiryDate)
        parcel.writeString(invoiceReference)
        parcel.writeValue(documentID)
        parcel.writeString(remarks)
        parcel.writeString(fitnessType)
        parcel.writeString(assetNo)
        parcel.writeString(fileName)
        parcel.writeString(fileExt)
        parcel.writeString(awsPath)
        parcel.writeValue(cost)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssetFitnessesData> {
        override fun createFromParcel(parcel: Parcel): AssetFitnessesData {
            return AssetFitnessesData(parcel)
        }

        override fun newArray(size: Int): Array<AssetFitnessesData?> {
            return arrayOfNulls(size)
        }
    }
}