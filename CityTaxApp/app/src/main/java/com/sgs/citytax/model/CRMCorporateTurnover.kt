package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CRMCorporateTurnover(
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
        @SerializedName("orgzid")
        var organizationId: Int? = 0,
        @SerializedName("CorporateTurnoverID")
        var turnoverID: Int? = 0,
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("LastBillingCycleActualAmount")
        var lastBillingCycleActualAmount: BigDecimal? = BigDecimal.ZERO
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(BigDecimal::class.java.classLoader) as BigDecimal?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            source.readValue(BigDecimal::class.java.classLoader) as? BigDecimal
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(financialStartDate)
        writeString(financialEndDate)
        writeString(businessName)
        writeString(ifu)
        writeValue(amount)
        writeValue(organizationId)
        writeValue(turnoverID)
        writeValue(startDate)
        writeValue(active)
        writeValue(estimatedTax)
        writeValue(lastBillingCycleActualAmount)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CRMCorporateTurnover> = object : Parcelable.Creator<CRMCorporateTurnover> {
            override fun createFromParcel(source: Parcel): CRMCorporateTurnover = CRMCorporateTurnover(source)
            override fun newArray(size: Int): Array<CRMCorporateTurnover?> = arrayOfNulls(size)
        }
    }
}