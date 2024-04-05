package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext

data class GetUnusedSycoTaxId(
        var context: SecurityContext = SecurityContext()
)