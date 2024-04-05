package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GenerateTaxNoticeResponse(
        @SerializedName("TaxNoticeID")
        var taxNoticeID: Int? = 0,
        @SerializedName("ReturnLineID")
        var returnLineID: Int? = 0,
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(taxNoticeID)
        parcel.writeValue(returnLineID)
        parcel.writeString(taxRuleBookCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GenerateTaxNoticeResponse> {
        override fun createFromParcel(parcel: Parcel): GenerateTaxNoticeResponse {
            return GenerateTaxNoticeResponse(parcel)
        }

        override fun newArray(size: Int): Array<GenerateTaxNoticeResponse?> {
            return arrayOfNulls(size)
        }
    }
}