package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext

data class AcceptPrivacyPolicy(
        var context: SecurityContext = SecurityContext()
)