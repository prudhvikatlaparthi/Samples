package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext

class SearchForTaxPayerForMapPayload (
        var context:SecurityContext? = null,
        var fltrdata:SearchTaxPayerFltrData? = null,
        var pageindex:Int? = 1,
        var pagesize:Int? = 20
)