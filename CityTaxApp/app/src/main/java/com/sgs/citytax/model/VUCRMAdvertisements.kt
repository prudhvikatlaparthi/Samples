package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class VUCRMAdvertisements(
        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("AdvertisementID")
        var advertisementId: Int? = 0,
        @SerializedName("orgzid")
        var organizationId: Int? = 0,
        @SerializedName("AdvertisementTypeID")
        var advertisementTypeId: Int? = 0,
        @SerializedName("qty")
        var quantity: Int? = 0,
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("desc")
        var description: String? = "",
        @SerializedName("AdvertisementTypeName")
        var advertisementTypeName: String? = "",
        @SerializedName("AllowDelete")
        var allowDelete: String? = "",
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("Length")
        var Length: String? = null,
        @SerializedName("wdth")
        var wdth: String? = null,
        @SerializedName("unitcode")
        var unitcode: String? = null,
        @SerializedName("TaxableMatter")
        var TaxableMatter: String? = null,
        @SerializedName("unit")
        var unit: String? = null,
        @Transient
        @Expose(serialize = false, deserialize = false)
        var documentList: ArrayList<COMDocumentReference>? = arrayListOf()
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                TODO("documentList")) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(accountID)
                parcel.writeValue(advertisementId)
                parcel.writeValue(organizationId)
                parcel.writeValue(advertisementTypeId)
                parcel.writeValue(quantity)
                parcel.writeString(active)
                parcel.writeString(startDate)
                parcel.writeString(description)
                parcel.writeString(advertisementTypeName)
                parcel.writeString(allowDelete)
                parcel.writeValue(estimatedTax)
                parcel.writeString(Length)
                parcel.writeString(wdth)
                parcel.writeString(unitcode)
                parcel.writeString(TaxableMatter)
                parcel.writeString(unit)

        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<VUCRMAdvertisements> {
                override fun createFromParcel(parcel: Parcel): VUCRMAdvertisements {
                        return VUCRMAdvertisements(parcel)
                }

                override fun newArray(size: Int): Array<VUCRMAdvertisements?> {
                        return arrayOfNulls(size)
                }
        }

}