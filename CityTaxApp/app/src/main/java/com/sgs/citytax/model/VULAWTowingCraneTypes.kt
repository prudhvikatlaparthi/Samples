package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class VULAWTowingCraneTypes(
    @SerializedName("TowingCraneTypeCode")
    var towingCraneTypeCode: String? = "",
    @SerializedName("TowingCraneType")
    var towingCraneType: String? = "",
    @SerializedName("TowingCraneTypeID")
    var towingCraneTypeID: Int? = 0,
    @SerializedName("PricingRuleID")
    var pricingRuleID: Int? = 0,
    @SerializedName("TowingCranePrice")
    var towingCranePrice: BigDecimal? = BigDecimal.TEN
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readSerializable() as? BigDecimal,
    ) {
    }

    override fun toString(): String {
        return towingCraneType.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(towingCraneTypeCode)
        parcel.writeString(towingCraneType)
        parcel.writeValue(towingCraneTypeID)
        parcel.writeValue(pricingRuleID)
        parcel.writeValue(towingCranePrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VULAWTowingCraneTypes> {
        override fun createFromParcel(parcel: Parcel): VULAWTowingCraneTypes {
            return VULAWTowingCraneTypes(parcel)
        }

        override fun newArray(size: Int): Array<VULAWTowingCraneTypes?> {
            return arrayOfNulls(size)
        }
    }
}