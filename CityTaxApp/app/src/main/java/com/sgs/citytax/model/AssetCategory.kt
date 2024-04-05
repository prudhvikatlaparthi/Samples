package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AssetCategory(
        @SerializedName("AssetCategory")
        var assetCategory: String? = "",
        @SerializedName("AssetCategoryCode")
        var assetCategoryCode: String? = "",
        @SerializedName("AssetTypeCode")
        var assetTypeCode: String? = "",
        @SerializedName("AssetSpecificationSetID")
        var assetSpecificationSetID: String? = null,
        @SerializedName("AllowMaintenance")
        var allowMaintenance: String? = "",
        @SerializedName("AllowInsurance")
        var allowInsurance: String? = "",
        @SerializedName("AllowFitness")
        var allowFitness: String? = "",
        @SerializedName("AllowRentBooking")
        var allowRentBooking: String? = "",
        @SerializedName("prodcode")
        var prodcode: String? = "",
        @SerializedName("BookingAdvance")
        var bookingAdvance: String? = "",
        @SerializedName("AssetCategoryID")
        var assetCategoryID: Int? = 0,
        @SerializedName("act")
        var isActive: String? = "",
        @SerializedName("PaymentCycleCode")
        var paymentCycleCode: String? = "",
        @SerializedName("AllowPeriodicInvoice")
        var allowPeriodicInvoice: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun toString(): String {
        return "$assetCategory"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(assetCategory)
        parcel.writeString(assetCategoryCode)
        parcel.writeString(assetTypeCode)
        parcel.writeString(assetSpecificationSetID)
        parcel.writeString(allowMaintenance)
        parcel.writeString(allowInsurance)
        parcel.writeString(allowFitness)
        parcel.writeString(allowRentBooking)
        parcel.writeString(prodcode)
        parcel.writeString(bookingAdvance)
        parcel.writeValue(assetCategoryID)
        parcel.writeString(isActive)
        parcel.writeString(paymentCycleCode)
        parcel.writeString(allowPeriodicInvoice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssetCategory> {
        override fun createFromParcel(parcel: Parcel): AssetCategory {
            return AssetCategory(parcel)
        }

        override fun newArray(size: Int): Array<AssetCategory?> {
            return arrayOfNulls(size)
        }
    }
}