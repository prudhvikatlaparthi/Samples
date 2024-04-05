package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ValidateAssetForAssignAndReturnResponse(
        @SerializedName("AssetID")
        var assetId: Int? = 0,
        @SerializedName("AssetCategoryID")
        var assetCategoryId: Int? = 0,
        @SerializedName("BookingRequestID")
        var bookingRequestId: Int? = 0,
        @SerializedName("AssetRentID")
        var assetRentId: Int? = 0,
        @SerializedName("AssignDate")
        var assignDate: String? = "",
        @SerializedName("BookingReqLineID")
        var bookingRequestLineId: Int? = 0,
        @SerializedName("AllowInvoiceOnlyAfterReturn")
        var allowInvoiceAfterReturn: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(assetId)
        parcel.writeValue(assetCategoryId)
        parcel.writeValue(bookingRequestId)
        parcel.writeValue(assetRentId)
        parcel.writeString(assignDate)
        parcel.writeValue(bookingRequestLineId)
        parcel.writeString(allowInvoiceAfterReturn)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ValidateAssetForAssignAndReturnResponse> {
        override fun createFromParcel(parcel: Parcel): ValidateAssetForAssignAndReturnResponse {
            return ValidateAssetForAssignAndReturnResponse(parcel)
        }

        override fun newArray(size: Int): Array<ValidateAssetForAssignAndReturnResponse?> {
            return arrayOfNulls(size)
        }
    }
}