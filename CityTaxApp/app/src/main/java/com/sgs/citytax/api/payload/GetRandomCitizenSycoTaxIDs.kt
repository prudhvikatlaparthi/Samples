package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext

data class GetRandomCitizenSycoTaxIDs(
        val context: SecurityContext = SecurityContext()
)