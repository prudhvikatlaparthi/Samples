package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext

data class GetProducts(
        var context: SecurityContext = SecurityContext()
)