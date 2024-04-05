package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.math.BigInteger

data class AssetInsuranceData(
        @SerializedName("InsuranceID")
        var insuranceID: Int? = 0,
        @SerializedName("InsuranceTypeID")
        var insuranceTypeID: Int? = 0,
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("InsuranceNo")
        var insuranceNo: BigInteger? = null,
        @SerializedName("InsuranceDate")
        var insuranceDate: String? = null,
        @SerializedName("Insurer")
        var insurer: String? = null,
        @SerializedName("FromDate")
        var fromDate: String? = null,
        @SerializedName("ExpiryDate")
        var expiryDate: String? = null,
        @SerializedName("InvoiceReference")
        var insuranceReferenceNo: String? = null,
        @SerializedName("DocumentID")
        var documentID: Int? = 0,
        @SerializedName("Remarks")
        var remarks: String? = null,
        @SerializedName("InsuranceType")
        var insuranceType: String? = null,
        @SerializedName("AssetNo")
        var assetNo: String? = null,
        @SerializedName("FileName")
        var fileName: String? = null,
        @SerializedName("FileExt")
        var fileExt: String? = null,
        @SerializedName("AWSPath")
        var awsFile: String? = "",
        @SerializedName("Cost")
        var cost: BigDecimal? = BigDecimal.ZERO
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(BigInteger::class.java.classLoader) as? BigInteger,
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
        parcel.writeValue(insuranceID)
        parcel.writeValue(insuranceTypeID)
        parcel.writeValue(assetID)
        parcel.writeValue(insuranceNo)
        parcel.writeString(insuranceDate)
        parcel.writeString(insurer)
        parcel.writeString(fromDate)
        parcel.writeString(expiryDate)
        parcel.writeString(insuranceReferenceNo)
        parcel.writeValue(documentID)
        parcel.writeString(remarks)
        parcel.writeString(insuranceType)
        parcel.writeString(assetNo)
        parcel.writeString(fileName)
        parcel.writeString(fileExt)
        parcel.writeString(awsFile)
        parcel.writeValue(cost)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssetInsuranceData> {
        override fun createFromParcel(parcel: Parcel): AssetInsuranceData {
            return AssetInsuranceData(parcel)
        }

        override fun newArray(size: Int): Array<AssetInsuranceData?> {
            return arrayOfNulls(size)
        }
    }
}