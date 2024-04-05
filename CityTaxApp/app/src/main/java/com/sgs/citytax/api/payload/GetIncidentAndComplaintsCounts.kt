package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext

data class GetIncidentAndComplaintsCounts(
        var context: SecurityContext = SecurityContext()
)