package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class MultipleImpoundmentTypes(
        @SerializedName("ImpoundmentTypeID")
        var impoundmentTypeID: Int? = 0,
        @SerializedName("ImpoundmentSubTypeID")
        var impoundmentSubTypeID: Int? = 0,
        @SerializedName("YardID")
        var yardID: Int? = 0,
        @SerializedName("TowingCraneTypeID")
        var towingCraneTypeID: Int? = 0,
        @SerializedName("TowingTripCount")
        var towingTripCount: Int? = 0,
        @SerializedName("ImpoundmentReason")
        var impoundmentReason: String? = "",
        @SerializedName("ImpoundmentCharge")
        var impoundmentCharge: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("FineAmount")
        var fineAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("qty")
        var quantity: Int? = null,
        @SerializedName("ViolationTypeID")
        var violationTypeID: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0,
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("rmks",alternate = ["Remarks"])
        var rmks: String? = "",
        @SerializedName("ViolationCharge")
        var violationCharge: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("extracharge")
        var extracharge: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("TowingCharge")
        var towingCharge: BigDecimal? = BigDecimal.ZERO,
        @Transient
        var impoundmentType: String? = "",
        @Transient
        var id: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readSerializable() as BigDecimal,
            parcel.readSerializable() as BigDecimal,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as BigDecimal,
            parcel.readSerializable() as BigDecimal,
            parcel.readSerializable() as BigDecimal,
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(impoundmentTypeID)
        parcel.writeValue(impoundmentSubTypeID)
        parcel.writeValue(yardID)
        parcel.writeValue(towingCraneTypeID)
        parcel.writeValue(towingTripCount)
        parcel.writeString(impoundmentReason)
        parcel.writeSerializable(impoundmentCharge)
        parcel.writeSerializable(fineAmount)
        parcel.writeValue(quantity)
        parcel.writeValue(violationTypeID)
        parcel.writeValue(pricingRuleID)
        parcel.writeString(violationDetails)
        parcel.writeString(rmks)
        parcel.writeSerializable(violationCharge)
        parcel.writeSerializable(extracharge)
        parcel.writeSerializable(towingCharge)
        parcel.writeString(impoundmentType)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MultipleImpoundmentTypes> {
        override fun createFromParcel(parcel: Parcel): MultipleImpoundmentTypes {
            return MultipleImpoundmentTypes(parcel)
        }

        override fun newArray(size: Int): Array<MultipleImpoundmentTypes?> {
            return arrayOfNulls(size)
        }
    }
}