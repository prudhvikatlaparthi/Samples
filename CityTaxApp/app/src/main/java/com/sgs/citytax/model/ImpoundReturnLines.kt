package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ImpoundReturnLines(
        @SerializedName("ImpoundmentID")
        var impoundmentID: Int? = 0,
        @SerializedName("qty")
        var quantity: BigDecimal? = BigDecimal.ZERO
) : Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(impoundmentID)
        parcel.writeValue(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImpoundReturnLines> {
        override fun createFromParcel(parcel: Parcel): ImpoundReturnLines {
            return ImpoundReturnLines(parcel)
        }

        override fun newArray(size: Int): Array<ImpoundReturnLines?> {
            return arrayOfNulls(size)
        }
    }

}
