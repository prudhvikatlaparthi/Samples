package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName

class VuComProperties(
        @SerializedName("PropertyID")
        var propertyID: Int? = null,
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("PropertySycotaxID")
        var propertySycotaxID: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("ProductCode")
        var productCode: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(propertyID)
        parcel.writeString(propertyName)
        parcel.writeString(propertySycotaxID)
        parcel.writeString(taxRuleBookCode)
        parcel.writeString(productCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VuComProperties> {
        override fun createFromParcel(parcel: Parcel): VuComProperties {
            return VuComProperties(parcel)
        }

        override fun newArray(size: Int): Array<VuComProperties?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
//        return "\n${propertySycotaxID}\n\n${propertyName?:""}"
        return checkPropertyName() + propertySycotaxID
    }

    private fun checkPropertyName(): String {
        if (TextUtils.isEmpty(propertyName)) {
            return ""
        } else {
            return propertyName + "\n"
        }
    }

}