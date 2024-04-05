package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class HeadList(
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("acctid")
        var acctid: Int? = 0,
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("usrorgbrid")
        var usrorgbrid: Int? = 0,
        @SerializedName("duedt")
        var duedt: String? = "",
        @SerializedName("stscode")
        var stscode: String? = "",
        @SerializedName("subtot")
        var subtot: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("rndngmthdid")
        var rndngmthdid: Int? = 0,
        @SerializedName("rndng")
        var rndng: Double? = 0.0,
        @SerializedName("netrec")
        var netrec: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("refno")
        var refno: String? = null,
        @SerializedName("desc")
        var desc: String? = null,
        @SerializedName("custprodintid")
        var custprodintid: Int? = 0,
        @SerializedName("crtd")
        var crtd: String? = null,
        @SerializedName("crtddt")
        var crtddt: String? = null,
        @SerializedName("mdfd")
        var mdfd: String? = null,
        @SerializedName("mdfddt")
        var mdfddt: String? = null,
        @SerializedName("lat")
        var lat: String? = null,
        @SerializedName("long")
        var long: String? = null,
        @SerializedName("DesignFilePath")
        var designFilePath: Int? = 0,
        @SerializedName("IsUpdateable")
        var isUpdateable: Boolean? = null,
        @SerializedName("CustomProperties")
        var customProperties: String? = null,
        @SerializedName("DesignSource")
        var designSource: String? = null
)