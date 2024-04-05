package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.response.BusinessDues
import java.math.BigDecimal

data class TaxPayerDetails(
        @SerializedName("Customer")
        var customer: String? = "",
        @SerializedName("custid", alternate = ["CustomerID"])
        var CustomerID: Int,
        var IsSycoTaxIDPresent: Boolean,
        @SerializedName("Number")
        var number: String? = "",
        @SerializedName("Email", alternate = ["email"])
        var email: String? = "",
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = "",
        @SerializedName("dtls", alternate = ["Details"])
        var vuCrmAccounts: VUCRMAccounts?,
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal = BigDecimal.ZERO,
        @SerializedName("BusinessDues")
        var businessDues: BusinessDues?,
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(VUCRMAccounts::class.java.classLoader),
            TODO("estimatedTax"),
            TODO("businessDues"),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(customer)
        parcel.writeInt(CustomerID)
        parcel.writeByte(if (IsSycoTaxIDPresent) 1 else 0)
        parcel.writeString(number)
        parcel.writeString(email)
        parcel.writeString(sycoTaxID)
        parcel.writeParcelable(vuCrmAccounts, flags)
        parcel.writeByte(if (isLoading) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaxPayerDetails> {
        override fun createFromParcel(parcel: Parcel): TaxPayerDetails {
            return TaxPayerDetails(parcel)
        }

        override fun newArray(size: Int): Array<TaxPayerDetails?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return customer + "\n" + sycoTaxID + "\n"
    }
}