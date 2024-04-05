package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class GetOutstanding(
        @SerializedName("InitialOutstandingID")
        var initialOutstandingID: Int? = null,
        @SerializedName("OutstandingTypeCode")
        var outstandingTypeCode: String? = null,
        @SerializedName("Year")
        var year: Int = -1,
        @SerializedName("AccountID")
        var accountID: Int? = null,
        @SerializedName("ProductCode")
        var productCode: String? = null,
        @SerializedName("VoucherNo")
        var voucherNo: Int? = null,
        @SerializedName("NetReceivable")
        var netReceivable: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("AccountName")
        var accountName: String? = null,
        @SerializedName("ReceivedAmount")
        var receivedAmount:  BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("Product")
        var product: String? = null,
        @SerializedName("TaxSubType")
        var taxSubType: String? = null,
        @SerializedName("AllowDelete")
        var allowDelete: String? = null,
        @SerializedName("OutstandingType")
        var outstandingType: String? = null,
        @SerializedName("Name")
        var name: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readInt(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readSerializable() as BigDecimal,
            parcel.readString(),
            parcel.readSerializable() as BigDecimal,
            parcel.readSerializable() as BigDecimal,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(initialOutstandingID)
        parcel.writeString(outstandingTypeCode)
        parcel.writeInt(year)
        parcel.writeValue(accountID)
        parcel.writeString(productCode)
        parcel.writeValue(voucherNo)
        parcel.writeValue(netReceivable)
        parcel.writeString(accountName)
        parcel.writeValue(receivedAmount)
        parcel.writeValue(currentDue)
        parcel.writeString(product)
        parcel.writeString(taxSubType)
        parcel.writeString(allowDelete)
        parcel.writeString(outstandingType)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetOutstanding> {
        override fun createFromParcel(parcel: Parcel): GetOutstanding {
            return GetOutstanding(parcel)
        }

        override fun newArray(size: Int): Array<GetOutstanding?> {
            return arrayOfNulls(size)
        }
    }

}