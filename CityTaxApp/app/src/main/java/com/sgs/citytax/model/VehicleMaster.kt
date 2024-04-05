package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.R
import com.sgs.citytax.util.getString

data class VehicleMaster(
        @SerializedName("vehno", alternate = ["VehicleNo"])
        var vehicleNo: String? = null,
        @SerializedName("ChassisNo")
        var chassisNo: String? = null,
        @SerializedName("regno", alternate = ["RegistrationNo"])
        var registrationNo: String? = null,
        @SerializedName("RegistrationDate")
        var registrationDate: String? = "",
        @SerializedName("Transmission")
        var transmission: String? = "",
        @SerializedName("FuelType")
        var fuelType: String? = "",
        @SerializedName("CO2Emissions")
        var cO2Emissions: String? = null,
        @SerializedName("Horsepower")
        var horsepower: String? = null,
        @SerializedName("Power")
        var power: String? = null,
        @SerializedName("val", alternate = ["Value"])
        var value: Int? = null,
        @SerializedName("SeatsNumber")
        var seatsNumber: Int? = 0,
        @SerializedName("LoadCapacity")
        var loadCapacity: Int? = 0,

        @SerializedName("mfg", alternate = ["Manufacturer"])
        var mfg: String? = "",

        @SerializedName("make", alternate = ["Make"])
        var make: String? = "",

        @SerializedName("Model")
        var model: String? = "",

        @SerializedName("Variant")
        var variant: String? = "",

        @SerializedName("ManufacturingYear")
        var manufacturingYear: Int? = 0,

        @SerializedName("EngineNo")
        var engineNo: String? = "",

        @SerializedName("Color")
        var color: String? = "",
        @SerializedName("rmks", alternate = ["Remarks"])
        var remarks: String? = null,
        @SerializedName("stscode", alternate = ["StatusCode"])
        var statusCode: String? = null,
        @SerializedName("vehtypcode", alternate = ["VehicleTypeCode"])
        var vehicleTypeCode: String? = null,
        @SerializedName("VehicleSycotaxID")
        var vehicleSycotaxID: String? = null,
        @SerializedName("Owner")
        var owner: String? = null,
        @SerializedName("Street", alternate = ["st"])
        var street: String? = null,
        @SerializedName("cty")
        var city: String? = null,
        @SerializedName("zn")
        var zn: String? = null,
        @SerializedName("sec")
        var sec: String? = null,
        @SerializedName("Plot")
        var Plot: String? = null,
        @SerializedName("Block")
        var Block: String? = null,
        @SerializedName("doorno")
        var doorno: String? = null,
        @SerializedName("zip")
        var zip: String? = null,
        @SerializedName("VehicleImpounded")
        var vehicleImpounded: String? = null,
        @SerializedName("CubicCapacity")
        var cubicCapacity: Double = 0.0,
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = null,
        @SerializedName("CitizenCardNo")
        var citizenCardNo: String? = null,
        @SerializedName("CitizenSycotaxID")
        var citizenSycotaxID: String? = null,
        @SerializedName("PhoneNumbers")
        var phoneNumbers: String? = null,
        @SerializedName("Emails")
        var emails: String? = null

):Parcelable {
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
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
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
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readDouble(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(vehicleNo)
                parcel.writeString(chassisNo)
                parcel.writeString(registrationNo)
                parcel.writeString(registrationDate)
                parcel.writeString(transmission)
                parcel.writeString(fuelType)
                parcel.writeString(cO2Emissions)
                parcel.writeString(horsepower)
                parcel.writeString(power)
                parcel.writeValue(value)
                parcel.writeValue(seatsNumber)
                parcel.writeValue(loadCapacity)
                parcel.writeString(mfg)
                parcel.writeString(make)
                parcel.writeString(model)
                parcel.writeString(variant)
                parcel.writeValue(manufacturingYear)
                parcel.writeString(engineNo)
                parcel.writeString(color)
                parcel.writeString(remarks)
                parcel.writeString(statusCode)
                parcel.writeString(vehicleTypeCode)
                parcel.writeString(vehicleSycotaxID)
                parcel.writeString(owner)
                parcel.writeString(street)
                parcel.writeString(city)
                parcel.writeString(zn)
                parcel.writeString(sec)
                parcel.writeString(Plot)
                parcel.writeString(Block)
                parcel.writeString(doorno)
                parcel.writeString(zip)
                parcel.writeString(vehicleImpounded)
                parcel.writeDouble(cubicCapacity)
                parcel.writeString(sycoTaxID)
                parcel.writeString(citizenCardNo)
                parcel.writeString(citizenSycotaxID)
                parcel.writeString(phoneNumbers)
                parcel.writeString(emails)

        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<VehicleMaster> {
                override fun createFromParcel(parcel: Parcel): VehicleMaster {
                        return VehicleMaster(parcel)
                }

                override fun newArray(size: Int): Array<VehicleMaster?> {
                        return arrayOfNulls(size)
                }
        }

        override fun toString(): String {
                return "${getString(R.string.syco_tax_id)} : $vehicleSycotaxID\n" +
                        "${getString(R.string.vehicle_no)} : $vehicleNo\n"
        }
}
