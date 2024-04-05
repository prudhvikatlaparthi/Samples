package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.response.EstimateTax

data class VUCRMCustomerProductInterests(
        @SerializedName("custid")
        var customerId: Int? = 0,
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("crtd")
        var crtd: String? = null,
        @SerializedName("crtddt")
        var crtddt: String? = null,
        @SerializedName("mdfd")
        var mdfd: String? = null,
        @SerializedName("mdfddt")
        var mdfddt: String? = null,
        @SerializedName("stscode")
        var stscode: String? = "",
        @SerializedName("rmks")
        var rmks: String? = "",
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
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode:String?="",
        @SerializedName("TaskCodeList")
        var taskCodes: List<TaskCode>? = arrayListOf(),
        @SerializedName("EstimatedTax")
        var estimatedTax: EstimateTax? = null
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
            parcel.createTypedArrayList(TaskCode),
            TODO("estimatedTax")) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(customerId)
        parcel.writeString(productCode)
        parcel.writeString(crtd)
        parcel.writeString(crtddt)
        parcel.writeString(mdfd)
        parcel.writeString(mdfddt)
        parcel.writeString(stscode)
        parcel.writeString(rmks)
        parcel.writeString(sts)
        parcel.writeString(activityDomainID)
        parcel.writeString(product)
        parcel.writeValue(customerProductInterestID)
        parcel.writeString(active)
        parcel.writeString(customer)
        parcel.writeString(photo)
        parcel.writeString(taxRuleBookCode)
        parcel.writeTypedList(taskCodes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VUCRMCustomerProductInterests> {
        override fun createFromParcel(parcel: Parcel): VUCRMCustomerProductInterests {
            return VUCRMCustomerProductInterests(parcel)
        }

        override fun newArray(size: Int): Array<VUCRMCustomerProductInterests?> {
            return arrayOfNulls(size)
        }
    }

}