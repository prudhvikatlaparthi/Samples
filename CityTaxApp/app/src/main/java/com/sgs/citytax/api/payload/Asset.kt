package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Asset(
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("AssetCategoryID")
        var assetCategoryID: Int? = 0,
        @SerializedName("AssetNo")
        var assetNo: String? = null,
        @SerializedName("LifeTimeStartDate")
        var lifeTimeStartDate: String? = null,
        @SerializedName("LifeTimeEndDate")
        var lifeTimeEndDate: String? = null,
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int? = 0,
        @SerializedName("BookingAdvance")
        var bookingAdvance: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("stscode")
        var statusCode: String? = null,
        @SerializedName("DurationPaymentCycleID")
        var durationPaymentCycleID: Int? = 0,
        @SerializedName("DurationPricingRuleID")
        var durationPricingRuleID: Int? = 0,
        @SerializedName("DistancePricingRuleID")
        var distancePricingRuleID: Int? = 0,
        @SerializedName("desc")
        var description: String? = null,
        @SerializedName("AllowMaintenance")
        var allowMaintenance: String? = "",
        @SerializedName("AllowInsurance")
        var allowInsurance: String? = "",
        @SerializedName("AllowFitness")
        var allowFitness: String? = "",
        @SerializedName("PaymentCycleCode")
        var paymentCycleCode: String? = "",
        @SerializedName("AssetSycotaxID")
        var assetSycotaxID: String? = "",
        @SerializedName("act")
        var status: String? = "N",
        @SerializedName("unit")
        var unit: String? = null,
        @SerializedName("AllowPeriodicInvoice")
        var allowPeriodicInvoice: String? =null,
        @SerializedName("SecurityDeposit")
        var securityDeposit: BigDecimal? = BigDecimal.ZERO


) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
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
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal) {
    }

    override fun toString(): String {
        return "$assetNo"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(assetID)
        parcel.writeValue(assetCategoryID)
        parcel.writeString(assetNo)
        parcel.writeString(lifeTimeStartDate)
        parcel.writeString(lifeTimeEndDate)
        parcel.writeValue(userOrgBranchID)
        parcel.writeValue(bookingAdvance)
        parcel.writeString(statusCode)
        parcel.writeValue(durationPaymentCycleID)
        parcel.writeValue(durationPricingRuleID)
        parcel.writeValue(distancePricingRuleID)
        parcel.writeString(description)
        parcel.writeString(allowMaintenance)
        parcel.writeString(allowInsurance)
        parcel.writeString(allowFitness)
        parcel.writeString(paymentCycleCode)
        parcel.writeString(assetSycotaxID)
        parcel.writeString(status)
        parcel.writeString(unit)
        parcel.writeString(allowPeriodicInvoice)
        parcel.writeValue(securityDeposit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Asset> {
        override fun createFromParcel(parcel: Parcel): Asset {
            return Asset(parcel)
        }

        override fun newArray(size: Int): Array<Asset?> {
            return arrayOfNulls(size)
        }
    }


}