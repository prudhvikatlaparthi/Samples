package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class StockTransferListResults(
    @SerializedName("StockAllocationID")
    var allocationID: Int? = 0,
    @SerializedName("AllocationDate")
    var allocationDate: String? = "",
    @SerializedName("ItemCode")
    var itemCode: String? = "",
    @SerializedName("Item")
    var item: String? = "",
    @SerializedName("Product")
    var product: String? = "",
    @SerializedName("Unit")
    var unit: String? = "",
    @SerializedName("FromAccountName")
    var fromAccountName: String? = "",
    @SerializedName("ToAccountName")
    var toAccountName: String? = "",
    @SerializedName("Quantity")
    var quantity: Double? = 0.0,
    @SerializedName("Remarks")
    var remarks: String? = "",
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString()
    ) {
    }
 
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(allocationID)
        parcel.writeString(allocationDate)
        parcel.writeString(itemCode)
        parcel.writeString(item)
        parcel.writeString(product)
        parcel.writeString(unit)
        parcel.writeString(fromAccountName)
        parcel.writeString(toAccountName)
        parcel.writeValue(quantity)
        parcel.writeString(remarks)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StockTransferListResults> {
        override fun createFromParcel(parcel: Parcel): StockTransferListResults {
            return StockTransferListResults(parcel)
        }

        override fun newArray(size: Int): Array<StockTransferListResults?> {
            return arrayOfNulls(size)
        }
    }
}
