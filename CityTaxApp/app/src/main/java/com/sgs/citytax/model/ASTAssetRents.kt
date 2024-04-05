package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ASTAssetRents(
        @SerializedName("AssetRentID")
        var assetRentId: Int? = 0,
        @SerializedName("BookingRequestLineID")
        var bookingRequestLineId: Int? = 0,
        @SerializedName("AssetID")
        var assetId: Int? = 0,
        @SerializedName("AssignDate")
        var assignDate: String? = "",
        @SerializedName("OdometerStart")
        var odometerStart: Double? = 0.0,
        @SerializedName("AssignLatitude")
        var assignLatitude: Double? = 0.0,
        @SerializedName("AssignLongitude")
        var assignLongitude: Double? = 0.0,
        @SerializedName("AssignByAccountID")
        var assignByAccountId: Int? = 0,
        @SerializedName("TenurePeriod")
        var tenurePeriod: Int? = 0,
        @SerializedName("ReceiveDate")
        var receiveDate: String? = "",
        @SerializedName("OdometerEnd")
        var odometerEnd: Double? = 0.0,
        @SerializedName("ReceiveLatitude")
        var receiveLatitude: Double? = 0.0,
        @SerializedName("ReceiveLongitude")
        var receiveLongitude: Double? = 0.0,
        @SerializedName("ReceiveByAccountID")
        var receivedByAccountID: Int? = 0,
        @SerializedName("Distance")
        var distance: Int? = 0,
        @SerializedName("DistanceAmount")
        var distanceAmount: Double? = 0.0,
        @SerializedName("DurationAmount")
        var durationAmount: Double? = 0.0,
        @SerializedName("FineAmount")
        var fineAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("AssignSignature")
        var assignSignature: String? = "",
        @SerializedName("ReturnSignature")
        var returnSignature: String? = "",
        @SerializedName("AssetRentTypeID")
        var assetRentTypeID: Int? = 0
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(Double::class.java.classLoader) as? Double,
                parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),parcel.readValue(Int::class.java.classLoader) as? Int) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(assetRentId)
                parcel.writeValue(bookingRequestLineId)
                parcel.writeValue(assetId)
                parcel.writeString(assignDate)
                parcel.writeValue(odometerStart)
                parcel.writeValue(assignLatitude)
                parcel.writeValue(assignLongitude)
                parcel.writeValue(assignByAccountId)
                parcel.writeValue(tenurePeriod)
                parcel.writeString(receiveDate)
                parcel.writeValue(odometerEnd)
                parcel.writeValue(receiveLatitude)
                parcel.writeValue(receiveLongitude)
                parcel.writeValue(receivedByAccountID)
                parcel.writeValue(distance)
                parcel.writeValue(distanceAmount)
                parcel.writeValue(durationAmount)
                parcel.writeValue(fineAmount)
                parcel.writeString(remarks)
                parcel.writeString(assignSignature)
                parcel.writeString(returnSignature)
                parcel.writeValue(assetRentTypeID)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<ASTAssetRents> {
                override fun createFromParcel(parcel: Parcel): ASTAssetRents {
                        return ASTAssetRents(parcel)
                }

                override fun newArray(size: Int): Array<ASTAssetRents?> {
                        return arrayOfNulls(size)
                }
        }
}