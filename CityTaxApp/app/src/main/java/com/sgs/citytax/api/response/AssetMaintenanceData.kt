package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AssetMaintenanceData(
        @SerializedName("MaintenanceID")
        var maintenanceID: Int? = 0,
        @SerializedName("MaintenanceTypeID")
        var maintenanceTypeID: Int? = 0,
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("MaintenanceDate")
        var maintenanceDate: String? = null,
        @SerializedName("Vendor")
        var vendor: String? = null,
        @SerializedName("DistanceTravelled")
        var distanceTravelled: Int? = 0,
        @SerializedName("MaintenanceDetails")
        var maintenanceDetails: String? = null,
        @SerializedName("InvoiceReference")
        var invoiceReference: String? = null,
        @SerializedName("DocumentID")
        var documentID: Int? = 0,
        @SerializedName("Remarks")
        var remarks: String? = null,
        @SerializedName("MaintenanceType")
        var maintenanceType: String? = null,
        @SerializedName("FileName")
        var fileName: String? = null,
        @SerializedName("FileExt")
        var fileExt: String? = null,
        @SerializedName("AWSPath")
        var awsPath: String? = "",
        @SerializedName("TotalCost")
        var totalCost: BigDecimal? = BigDecimal.ZERO
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(maintenanceID)
        parcel.writeValue(maintenanceTypeID)
        parcel.writeValue(assetID)
        parcel.writeString(maintenanceDate)
        parcel.writeString(vendor)
        parcel.writeValue(distanceTravelled)
        parcel.writeString(maintenanceDetails)
        parcel.writeString(invoiceReference)
        parcel.writeValue(documentID)
        parcel.writeString(remarks)
        parcel.writeString(maintenanceType)
        parcel.writeString(fileName)
        parcel.writeString(fileExt)
        parcel.writeString(awsPath)
        parcel.writeValue(totalCost)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssetMaintenanceData> {
        override fun createFromParcel(parcel: Parcel): AssetMaintenanceData {
            return AssetMaintenanceData(parcel)
        }

        override fun newArray(size: Int): Array<AssetMaintenanceData?> {
            return arrayOfNulls(size)
        }
    }
}