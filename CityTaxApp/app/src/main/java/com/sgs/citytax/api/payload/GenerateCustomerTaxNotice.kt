package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.TaxableMatterList
import java.math.BigDecimal

data class GenerateCustomerTaxNotice(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerId: Int? = 0,
        @SerializedName("prodcode")
        var productCode: String = "",
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("taxablematter")
        var taxableMatter: BigDecimal? = null,
        @SerializedName("taxablematterlst")
        var taxableMatterList: TaxableMatterList? = null
)