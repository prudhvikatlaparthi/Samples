package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AdjustmentsListResults(
    @SerializedName("AccountID")
    val AccountID: Int? = 0,
    @SerializedName("AccountName")
    val AccountName: String? = null,
    @SerializedName("AdjustmentDate")
    val AdjustmentDate: String? = null,
    @SerializedName("AdjustmentID")
    val AdjustmentID: Int? = 0,
    @SerializedName("AdjustmentType")
    val AdjustmentType: String? = null,
    @SerializedName("AdjustmentTypeCode")
    val AdjustmentTypeCode: String? = null,
    @SerializedName("AdjustmentTypeID")
    val AdjustmentTypeID: Int? = 0,
    @SerializedName("AllowFractionalQuantity")
    val AllowFractionalQuantity: String? = null,
    @SerializedName("Item")
    val Item: String? = null,
    @SerializedName("ItemCode")
    val ItemCode: String? = null,
    @SerializedName("ParentProductCode")
    val ParentProductCode: String? = null,
    @SerializedName("Product")
    val Product: String? = null,
    @SerializedName("ProductCode")
    val ProductCode: String? = null,
    @SerializedName("Quantity")
    val Quantity: Double? = null,
    @SerializedName("Remarks")
    val Remarks: String? = null,
    @SerializedName("StockInOut")
    val StockInOut: String? = null,
    @SerializedName("Unit")
    val Unit: String? = null,
    @SerializedName("TaxCode")
    val TaxCode: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),

        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(AccountID)
        parcel.writeString(AccountName)
        parcel.writeString(AdjustmentDate)
        parcel.writeValue(AdjustmentID)
        parcel.writeString(AdjustmentType)
        parcel.writeString(AdjustmentTypeCode)
        parcel.writeValue(AdjustmentTypeID)
        parcel.writeString(AllowFractionalQuantity)
        parcel.writeString(Item)
        parcel.writeString(ItemCode)
        parcel.writeString(ParentProductCode)
        parcel.writeString(Product)
        parcel.writeString(ProductCode)
        parcel.writeValue(Quantity)
        parcel.writeString(Remarks)
        parcel.writeString(StockInOut)
        parcel.writeString(Unit)
        parcel.writeString(TaxCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdjustmentsListResults> {
        override fun createFromParcel(parcel: Parcel): AdjustmentsListResults {
            return AdjustmentsListResults(parcel)
        }

        override fun newArray(size: Int): Array<AdjustmentsListResults?> {
            return arrayOfNulls(size)
        }
    }


}
