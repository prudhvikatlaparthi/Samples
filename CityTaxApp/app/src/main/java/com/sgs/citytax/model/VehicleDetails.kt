package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class VehicleDetails(
        @SerializedName("vehno")
        var vehicleNumber: String? = "",
        @SerializedName("ChassisNo")
        var chasisNumber: String? = "",
        @SerializedName("regno")
        var registrionNumber: String? = "",
        @SerializedName("RegistrationDate")
        var registrationDate: String? = "",
        @SerializedName("Transmission")
        var transmission: String? = "",
        @SerializedName("FuelType")
        var fuelType: String? = "",
        @SerializedName("CO2Emissions")
        var coEmmissions: String? = "",
        @SerializedName("Horsepower")
        var horsePower: String? = "",
        @SerializedName("Power")
        var power: String? = "",
        @SerializedName("val")
        var value: Double? = 0.0,
        @SerializedName("SeatsNumber")
        var seatsNumber: Int? = 0,
        @SerializedName("LoadCapacity")
        var loadCapacity: Int? = 0,
        @SerializedName("Color")
        var color: String? = "",
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("vehtypcode")
        var vehicleTypeCode: String? = "",
        @SerializedName("VehicleSycotaxID")
        var vehicleSycoTaxID: String? = "",
        @SerializedName("vehtyp")
        var vehicleType: String? = "",
        @SerializedName("sts")
        var status: String? = "",
        @SerializedName("VehicleOwnershipID")
        var vehicleOwnerShipID: Int? = 0,
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @SerializedName("acctname")
        var accountName: String? = "",
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = "",
        @SerializedName("Owner")
        var owner: String? = ""
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(vehicleNumber)
                parcel.writeString(chasisNumber)
                parcel.writeString(registrionNumber)
                parcel.writeString(registrationDate)
                parcel.writeString(transmission)
                parcel.writeString(fuelType)
                parcel.writeString(coEmmissions)
                parcel.writeString(horsePower)
                parcel.writeString(power)
                parcel.writeValue(value)
                parcel.writeValue(seatsNumber)
                parcel.writeValue(loadCapacity)
                parcel.writeString(color)
                parcel.writeString(remarks)
                parcel.writeString(statusCode)
                parcel.writeString(vehicleTypeCode)
                parcel.writeString(vehicleSycoTaxID)
                parcel.writeString(vehicleType)
                parcel.writeString(status)
                parcel.writeValue(vehicleOwnerShipID)
                parcel.writeValue(accountId)
                parcel.writeString(accountName)
                parcel.writeString(fromDate)
                parcel.writeString(toDate)
                parcel.writeString(owner)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<VehicleDetails> {
                override fun createFromParcel(parcel: Parcel): VehicleDetails {
                        return VehicleDetails(parcel)
                }

                override fun newArray(size: Int): Array<VehicleDetails?> {
                        return arrayOfNulls(size)
                }
        }
}