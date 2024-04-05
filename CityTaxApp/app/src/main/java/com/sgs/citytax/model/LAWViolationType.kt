package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LAWViolationType(
        @SerializedName("ViolationType")
        var violationType: String? = "",
        @SerializedName("ViolationTypeCode")
        var violationTypeCode: String? = "",
        @SerializedName("ParentViolationTypeID")
        var parentViolationTypeID: Int? = 0,
        @SerializedName("ViolationTypeID")
        var violationTypeID: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0,
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = "",
        @SerializedName("ApplicableOnDriver")
        var applicableOnDriver: String? = "",
        @SerializedName("ParentViolationType")
        var parentViolationType: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun toString(): String {
        return "$violationType"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(violationType)
        parcel.writeString(violationTypeCode)
        parcel.writeValue(parentViolationTypeID)
        parcel.writeValue(violationTypeID)
        parcel.writeValue(pricingRuleID)
        parcel.writeString(violationDetails)
        parcel.writeString(violatorTypeCode)
        parcel.writeString(applicableOnDriver)
        parcel.writeString(parentViolationType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LAWViolationType> {
        override fun createFromParcel(parcel: Parcel): LAWViolationType {
            return LAWViolationType(parcel)
        }

        override fun newArray(size: Int): Array<LAWViolationType?> {
            return arrayOfNulls(size)
        }
    }
}