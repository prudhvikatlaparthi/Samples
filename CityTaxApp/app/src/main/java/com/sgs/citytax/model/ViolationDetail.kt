package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ViolationDetail(
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int = 0,
        @SerializedName("ViolationTicketID")
        var violationTicketID: Int = 0,
        @SerializedName("ViolationTicketDate")
        var violationTicketDate: String? = "",
        @SerializedName("ViolationTypeID")
        var violationTypeID: Int? = 0,
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("netrec")
        var netReceivable: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int? = 0,
        @SerializedName("vehno")
        var vehicleNo: String? = "",
        @SerializedName("VehicleOwner")
        var vehicleOwner: String? = "",
        @SerializedName("VehicleOwnerAccountID")
        var vehicleOwnerAccountID: Int? = 0,
        @SerializedName("Driver")
        var driver: String? = "",
        @SerializedName("DrivingLicenseNo")
        var drivingLicenseNo: String? = "",
        @SerializedName("DriverAccountID")
        var driverAccountID: Int? = 0,
        @SerializedName("ViolatorAccountID")
        var violatorAccountID: Int? = 0,
        @SerializedName("ViolationOwnerSignatureID")
        var violationOwnerSignatureID: Int? = 0,
        @SerializedName("AWSPath")
        var awsPath: String? = "",
        @SerializedName("Violator")
        var violator: String? = "",
        @SerializedName("mob")
        var mobile: String? = "",
        @SerializedName("cntrycode")
        var countryCode: String? = "",
        @SerializedName("stid")
        var stateID: Int = 0,
        @SerializedName("ctyid")
        var cityID: Int = 0,
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("SectorID")
        var sectorID: Int = 0,
        @SerializedName("lat")
        var latitude: String? = "",
        @SerializedName("long")
        var longitude: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as BigDecimal?,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
    parcel.readSerializable() as BigDecimal?)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(taxInvoiceID)
        parcel.writeInt(violationTicketID)
        parcel.writeString(violationTicketDate)
        parcel.writeValue(violationTypeID)
        parcel.writeString(violationDetails)
        parcel.writeString(remarks)
        parcel.writeSerializable(netReceivable)
        parcel.writeValue(userOrgBranchID)
        parcel.writeString(vehicleNo)
        parcel.writeString(vehicleOwner)
        parcel.writeValue(vehicleOwnerAccountID)
        parcel.writeString(driver)
        parcel.writeString(drivingLicenseNo)
        parcel.writeValue(driverAccountID)
        parcel.writeValue(violatorAccountID)
        parcel.writeValue(violationOwnerSignatureID)
        parcel.writeString(awsPath)
        parcel.writeString(violator)
        parcel.writeString(mobile)
        parcel.writeString(countryCode)
        parcel.writeInt(stateID)
        parcel.writeInt(cityID)
        parcel.writeString(zone)
        parcel.writeInt(sectorID)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(street)
        parcel.writeString(plot)
        parcel.writeString(block)
        parcel.writeString(doorNo)
        parcel.writeString(zipCode)
        parcel.writeSerializable(currentDue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ViolationDetail> {
        override fun createFromParcel(parcel: Parcel): ViolationDetail {
            return ViolationDetail(parcel)
        }

        override fun newArray(size: Int): Array<ViolationDetail?> {
            return arrayOfNulls(size)
        }
    }
}