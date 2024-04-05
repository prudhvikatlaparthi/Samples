package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class VuTax(
        @SerializedName("custid")
        var customerId: Int? = 0,
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("crtd")
        var createdBy: String? = null,
        @SerializedName("crtddt")
        var createdDate: String? = null,
        @SerializedName("mdfd")
        var modifiedBy: String? = null,
        @SerializedName("mdfddt")
        var modifiedDate: String? = null,
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("sts")
        var sts: String? = "",
        @SerializedName("ActivityDomainID")
        var activityDomainID: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("custprodintid")
        var customerProductInterestID: Int? = 0,
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("Customer")
        var customer: String? = "",
        @SerializedName("photo")
        var photo: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("TaskCodeList")
        var taskCodes: List<TaskCode>? = arrayListOf(),
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal? = BigDecimal.ZERO,
        @Expose(serialize = false, deserialize = false)
        var noOfROP: Int? = 0,
        @Expose(serialize = false, deserialize = false)
        var noOfPDO: Int? = 0,
        @Expose(serialize = false, deserialize = false)
        var noOfCorporateTurnOver: Int? = 0,
        @Expose(serialize = false, deserialize = false)
        var noOfVehicleOwnership: Int? = 0,
        @Expose(serialize = false, deserialize = false)
        var noOfAdvertisements: Int? = 0,
        @Expose(serialize = false, deserialize = false)
        var noOfShows: Int? = 0,
        @Transient
        var noOfHotel: Int? = 0,
        @Transient
        var noOfLicenses: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
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
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(TaskCode),
            parcel.readSerializable() as BigDecimal?,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(customerId)
        parcel.writeString(productCode)
        parcel.writeString(createdBy)
        parcel.writeString(createdDate)
        parcel.writeString(modifiedBy)
        parcel.writeString(modifiedDate)
        parcel.writeString(statusCode)
        parcel.writeString(remarks)
        parcel.writeString(sts)
        parcel.writeString(activityDomainID)
        parcel.writeString(product)
        parcel.writeValue(customerProductInterestID)
        parcel.writeString(active)
        parcel.writeString(customer)
        parcel.writeString(photo)
        parcel.writeString(billingCycle)
        parcel.writeString(taxRuleBookCode)
        parcel.writeTypedList(taskCodes)
        parcel.writeValue(noOfROP)
        parcel.writeValue(noOfPDO)
        parcel.writeValue(noOfCorporateTurnOver)
        parcel.writeValue(noOfVehicleOwnership)
        parcel.writeValue(noOfAdvertisements)
        parcel.writeValue(noOfShows)
        parcel.writeValue(noOfHotel)
        parcel.writeValue(noOfLicenses)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VuTax> {
        override fun createFromParcel(parcel: Parcel): VuTax {
            return VuTax(parcel)
        }

        override fun newArray(size: Int): Array<VuTax?> {
            return arrayOfNulls(size)
        }
    }
}