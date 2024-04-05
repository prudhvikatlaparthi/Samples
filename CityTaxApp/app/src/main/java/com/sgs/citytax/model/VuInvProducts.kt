package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class VuInvProducts(
        @SerializedName("prodcode", alternate = ["ProductCode"])
        var productCode: String? = "",
        @SerializedName("prod", alternate = ["Product"])
        var product: String? = "",
        @SerializedName("unit")
        var unit: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("catid")
        var categoryID: Int = -1,
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("BillingCycleID")
        var billingCycleID: Int = 0,
        @SerializedName("paymentCycleCode")
        var paymentCycleCode: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun toString(): String {
        return product.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productCode)
        parcel.writeString(product)
        parcel.writeString(unit)
        parcel.writeString(taxRuleBookCode)
        parcel.writeInt(categoryID)
        parcel.writeString(billingCycle)
        parcel.writeInt(billingCycleID)
        parcel.writeString(paymentCycleCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VuInvProducts> {
        override fun createFromParcel(parcel: Parcel): VuInvProducts {
            return VuInvProducts(parcel)
        }

        override fun newArray(size: Int): Array<VuInvProducts?> {
            return arrayOfNulls(size)
        }
    }
}