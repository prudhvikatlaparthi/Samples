package com.sgs.citytax.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LicenseRenewalModel(
        @SerializedName("LicenseID")
        var licenseId: Int? = 0,
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? ="",
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? ="",
        @SerializedName("ValidFromDate")
        var validFromDate: String? ="",
        @SerializedName("ValidToDate")
        var validToDate: String? ="",
        @SerializedName("amt")
        var amt: Int? = 0,
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false


)