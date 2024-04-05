package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class VUADMVehicleOwnership(
        @SerializedName("VehicleOwnershipID")
        var vehicleOwnershipID: Int? = 0,
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @SerializedName("vehno")
        var vehicleNo: String? = null,
        @SerializedName("regno")
        var registrationNo: String? = "",
        @SerializedName("Transmission")
        var transmission: String? = "",
        @SerializedName("CO2Emissions")
        var co2Emission: String? = "",
        @SerializedName("Power")
        var power: String? = "",
        @SerializedName("SeatsNumber")
        var seatNumber: Int? = 0,
        @SerializedName("Color")
        var color: String? = "",
        @SerializedName("4rmdt")
        var fromDate: String? = null,
        @SerializedName("2dt")
        var toDate: String? = null,
        @SerializedName("ChassisNo")
        var chassisNo: String? = "",
        @SerializedName("RegistrationDate")
        var registrationDate: String? = "",
        @SerializedName("FuelType")
        var fuelType: String? = "",
        @SerializedName("Horsepower")
        var horsepower: String? = "",
        @SerializedName("val")
        var value: Int? = 0,
        @SerializedName("LoadCapacity")
        var loadCapacity: Int? = 0,
        @SerializedName("vehtypcode")
        var vehicleTypeCode: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("VehicleSycotaxID")
        var vehicleSycotaxID: String? = "",
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("EngineNo")
        var engineNo: String? = "",
        @SerializedName("mfg")
        var mfg: String? = "",
        @SerializedName("Model")
        var model: String? = "",
        @SerializedName("Variant")
        var variant: String? = "",
        @SerializedName("make")
        var make: String? = "",
        @SerializedName("ManufacturingYear")
        var manufacturingYear: Int? = null,
        @SerializedName("CubicCapacity")
        var cubicCapacity: Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
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
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readDouble()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(vehicleOwnershipID)
        parcel.writeValue(accountId)
        parcel.writeString(vehicleNo)
        parcel.writeString(registrationNo)
        parcel.writeString(transmission)
        parcel.writeString(co2Emission)
        parcel.writeString(power)
        parcel.writeValue(seatNumber)
        parcel.writeString(color)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
        parcel.writeString(chassisNo)
        parcel.writeString(registrationDate)
        parcel.writeString(fuelType)
        parcel.writeString(horsepower)
        parcel.writeValue(value)
        parcel.writeValue(loadCapacity)
        parcel.writeString(vehicleTypeCode)
        parcel.writeString(statusCode)
        parcel.writeString(vehicleSycotaxID)
        parcel.writeString(remarks)
        parcel.writeString(engineNo)
        parcel.writeString(mfg)
        parcel.writeString(model)
        parcel.writeString(variant)
        parcel.writeString(make)
        parcel.writeValue(manufacturingYear)
        parcel.writeDouble(cubicCapacity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VUADMVehicleOwnership> {
        override fun createFromParcel(parcel: Parcel): VUADMVehicleOwnership {
            return VUADMVehicleOwnership(parcel)
        }

        override fun newArray(size: Int): Array<VUADMVehicleOwnership?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return vehicleSycotaxID.toString()+"\n"+vehicleNo
    }

}