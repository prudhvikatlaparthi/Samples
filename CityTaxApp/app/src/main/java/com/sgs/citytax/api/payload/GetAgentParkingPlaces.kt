package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext

data class GetAgentParkingPlaces(
        var context: SecurityContext = SecurityContext()
)