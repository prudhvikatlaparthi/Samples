package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class VUCRMCorporateTurnover(
        @SerializedName("CorporateTurnoverID")
        var turnoverID: Int? = 0,
        @SerializedName("orgzid")
        var organizationId: Int? = 0,
        @SerializedName("RentalID")
        var rentalID: Int? = 0,
        @SerializedName("FinancialStartDate")
        var financialStartDate: String? = null,
        @SerializedName("FinancialEndDate")
        var financialEndDate: String? = null,
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("IFU")
        var ifu: String? = "",
        @SerializedName("amt")
        var amount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("RentalStartDate")
        var rentalStartDate: String? = "",
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("RentPerRateCycle")
        var rentPerRateCycle: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("LastBillingCycleActualAmount")
        var lastBillingCycleActualAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ActualAmount")
        var actualAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("Esttx4DutyOnRental")
        var estimatedTaxProp: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("AllowDelete")
        var allowDelete: String? = "",
        @SerializedName("acctid")
        var acctid: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal?,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal?,
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal?,
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal?,
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal?,
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal?,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(turnoverID)
        parcel.writeValue(organizationId)
        parcel.writeValue(rentalID)
        parcel.writeString(financialStartDate)
        parcel.writeString(financialEndDate)
        parcel.writeString(businessName)
        parcel.writeValue(amount)
        parcel.writeString(ifu)
        parcel.writeString(startDate)
        parcel.writeString(rentalStartDate)
        parcel.writeValue(estimatedTax)
        parcel.writeValue(rentPerRateCycle)
        parcel.writeValue(lastBillingCycleActualAmount)
        parcel.writeValue(actualAmount)
        parcel.writeValue(estimatedTaxProp)
        parcel.writeString(allowDelete)
        parcel.writeValue(acctid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VUCRMCorporateTurnover> {
        override fun createFromParcel(parcel: Parcel): VUCRMCorporateTurnover {
            return VUCRMCorporateTurnover(parcel)
        }

        override fun newArray(size: Int): Array<VUCRMCorporateTurnover?> {
            return arrayOfNulls(size)
        }
    }
}