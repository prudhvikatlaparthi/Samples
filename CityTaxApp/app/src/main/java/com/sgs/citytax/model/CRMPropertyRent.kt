package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CRMPropertyRent(
        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("Active")
        var active: String? = "",
        @SerializedName("AgreementNo")
        var agreementNo: String? = "",
        @SerializedName("AgreementDate")
        var agreementDate: String? = "",
        @SerializedName("Description")
        var description: String? = "",
        @SerializedName("NoofMonths")
        var noOfMonths: Int? = 0,
        @SerializedName("PaymentCycleID")
        var paymentCycleID: Int? = 0,
        @SerializedName("PropertyID")
        var propertyID: Int? = 0,
        @SerializedName("PropertyRentID")
        var propertyRentID: Int? = 0,
        @SerializedName("RentAmount")
        var rentAmount: Double? = 0.0,
        @SerializedName("RentTypeID")
        var rentTypeID: Int? = 0,
        @SerializedName("TaxableRate")
        var taxableRate: Int? = 0,
        @SerializedName("RentType")
        var rentType: String? = "",
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal? = BigDecimal.ZERO
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(accountID)
        parcel.writeString(active)
        parcel.writeString(agreementNo)
        parcel.writeString(agreementDate)
        parcel.writeString(description)
        parcel.writeValue(noOfMonths)
        parcel.writeValue(paymentCycleID)
        parcel.writeValue(propertyID)
        parcel.writeValue(propertyRentID)
        parcel.writeValue(rentAmount)
        parcel.writeValue(rentTypeID)
        parcel.writeValue(taxableRate)
        parcel.writeString(rentType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CRMPropertyRent> {
        override fun createFromParcel(parcel: Parcel): CRMPropertyRent {
            return CRMPropertyRent(parcel)
        }

        override fun newArray(size: Int): Array<CRMPropertyRent?> {
            return arrayOfNulls(size)
        }
    }

}