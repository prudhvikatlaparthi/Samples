package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ROPListItem(
        @SerializedName("PublicDomainOccupancyID")
        var publicDomainOccupancyID: Int? = 0,
        @SerializedName("RightOfPlaceID")
        var rightOfPlaceID: Int? = 0,
        @SerializedName("orgzid")
        var orgzid: String? = "",
        @SerializedName("OccupancyID")
        var occupancyID: Int? = 0,
        @SerializedName("MarketID")
        var marketID: Int? = 0,
        @SerializedName("Market")
        var market: String? = null,
        @SerializedName("OccupancyName")
        var occupancyName: String? = "",
        @SerializedName("TaxPeriod")
        var taxPeriod: Int? = 0,
        @SerializedName("act")
        var act: String? = "",
        @SerializedName("desc")
        var description: String? = "",
        @SerializedName("IFU")
        var IFU: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("Length")
        var length: Double? = 0.00,
        @SerializedName("wdth")
        var width: Double? = 0.00,
        @SerializedName("Height")
        var height: Double? = 0.00,
        @SerializedName("TaxableMatter")
        var taxableMatter: Double? = 0.00,
        @SerializedName("amt")
        var Amount: Double? = 0.00,
        @SerializedName("Due")
        var due: Double? = 0.00,
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("AllowDelete")
        var allowDelete: String? = "",
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("acctid")
        var acctid: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(publicDomainOccupancyID)
        parcel.writeValue(rightOfPlaceID)
        parcel.writeString(orgzid)
        parcel.writeValue(occupancyID)
        parcel.writeValue(marketID)
        parcel.writeValue(market)
        parcel.writeString(occupancyName)
        parcel.writeValue(taxPeriod)
        parcel.writeString(act)
        parcel.writeString(description)
        parcel.writeString(IFU)
        parcel.writeString(businessName)
        parcel.writeValue(length)
        parcel.writeValue(width)
        parcel.writeValue(height)
        parcel.writeValue(taxableMatter)
        parcel.writeValue(Amount)
        parcel.writeValue(due)
        parcel.writeString(startDate)
        parcel.writeString(allowDelete)
        parcel.writeValue(acctid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ROPListItem> {
        override fun createFromParcel(parcel: Parcel): ROPListItem {
            return ROPListItem(parcel)
        }

        override fun newArray(size: Int): Array<ROPListItem?> {
            return arrayOfNulls(size)
        }
    }

}