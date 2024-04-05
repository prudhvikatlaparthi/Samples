package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class TaxNoticeDetail(
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: String? = "",
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("subtot")
        var subTotal: BigDecimal? = BigDecimal.ZERO
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as BigDecimal,
            parcel.readSerializable() as BigDecimal
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(taxInvoiceDate)
        parcel.writeString(noticeReferenceNo)
        parcel.writeString(statusCode)
        parcel.writeString(taxInvoiceID)
        parcel.writeSerializable(currentDue)
        parcel.writeSerializable(subTotal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaxNoticeDetail> {
        override fun createFromParcel(parcel: Parcel): TaxNoticeDetail {
            return TaxNoticeDetail(parcel)
        }

        override fun newArray(size: Int): Array<TaxNoticeDetail?> {
            return arrayOfNulls(size)
        }
    }
}