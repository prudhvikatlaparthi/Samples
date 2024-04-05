package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ParkingTicketDetails(
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("Product")
        var productName: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: String? = "",
        @SerializedName("TicketDate")
        var ticketDate: String? = "",
        @SerializedName("TicketNo")
        var ticketNo: String? = "",
        @SerializedName("ParkingStartDate")
        var parkingStartDate: String? = "",
        @SerializedName("ParkingEndDate")
        var parkingEndDate: String? = "",
        @SerializedName("vehno", alternate = ["VehicleNo"])
        var vehicleNumber: String? = "",
        @SerializedName("VehicleSycotaxID")
        var vehicleSycoTaxId: String? = "",
        @SerializedName("VehicleOwner")
        var vehicleOwner: String? = "",
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("sec")
        var sector: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("lat")
        var lat: String? = "",
        @SerializedName("long")
        var longitude: String? = "",
        @SerializedName("ParkingPlace")
        var parkingPlace: String? = "",
        @SerializedName("ParkingType")
        var parkingType: String? = "",
        @SerializedName("ParkingRate")
        var parkingRate: String? = "",
        @SerializedName("ParkingAmount")
        var parkingAmount: Double? = 0.0,
        @SerializedName("PendingAmount")
        var pendingAmount: Double? = 0.0,
        @SerializedName("Note")
        var note: String? = ": null",
        @SerializedName("GeneratedBy")
        var generatedBy: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        var currentDue: Double? = 0.0,
        @SerializedName("CitizenSycotaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("SycoTaxID")
        var sycoTaxID: String?="",
        var netReceivable: BigDecimal? = BigDecimal.ZERO,
        var receivedAmount: BigDecimal? = BigDecimal.ZERO,
        var isPass: String? = "",
        var vehicleOwnerAccountId: Int? = 0,
        var parkingTicketId: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
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
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(taxRuleBookCode)
        parcel.writeString(productName)
        parcel.writeValue(taxInvoiceId)
        parcel.writeString(taxationYear)
        parcel.writeString(ticketDate)
        parcel.writeString(ticketNo)
        parcel.writeString(parkingStartDate)
        parcel.writeString(parkingEndDate)
        parcel.writeString(vehicleNumber)
        parcel.writeString(vehicleSycoTaxId)
        parcel.writeString(vehicleOwner)
        parcel.writeString(zone)
        parcel.writeString(sector)
        parcel.writeString(plot)
        parcel.writeString(block)
        parcel.writeString(doorNo)
        parcel.writeString(street)
        parcel.writeString(zipCode)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(lat)
        parcel.writeString(longitude)
        parcel.writeString(parkingPlace)
        parcel.writeString(parkingType)
        parcel.writeString(parkingRate)
        parcel.writeValue(parkingAmount)
        parcel.writeValue(pendingAmount)
        parcel.writeString(note)
        parcel.writeString(generatedBy)
        parcel.writeValue(printCounts)
        parcel.writeValue(currentDue)
        parcel.writeString(citizenSycoTaxId)
        parcel.writeString(citizenCardNumber)
        parcel.writeString(sycoTaxID)
        parcel.writeValue(netReceivable)
        parcel.writeValue(receivedAmount)
        parcel.writeString(isPass)
        parcel.writeValue(vehicleOwnerAccountId)
        parcel.writeValue(parkingTicketId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParkingTicketDetails> {
        override fun createFromParcel(parcel: Parcel): ParkingTicketDetails {
            return ParkingTicketDetails(parcel)
        }

        override fun newArray(size: Int): Array<ParkingTicketDetails?> {
            return arrayOfNulls(size)
        }
    }
}