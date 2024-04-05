package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.bumptech.glide.annotation.Excludes
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class MultipleViolationTypes(
        @SerializedName("violationTypeID")
        var violationTypeId: Int? = 0,
        @SerializedName("pricingRuleID")
        var pricingRuleId: Int? = 0,
        @SerializedName("fineAmount")
        var fineAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("violationDetails")
        var violationDetails: String? = "",
        @SerializedName("ApplicableOnDriver")
        var applicableOnDriver: String? = "",
        @Transient
        var violationID: String? = null,
        @Transient
        var violationType: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(violationTypeId)
        parcel.writeValue(pricingRuleId)
        parcel.writeValue(fineAmount)
        parcel.writeString(violationDetails)
        parcel.writeString(applicableOnDriver)
        parcel.writeString(violationID)
        parcel.writeString(violationType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MultipleViolationTypes> {
        override fun createFromParcel(parcel: Parcel): MultipleViolationTypes {
            return MultipleViolationTypes(parcel)
        }

        override fun newArray(size: Int): Array<MultipleViolationTypes?> {
            return arrayOfNulls(size)
        }
    }
}