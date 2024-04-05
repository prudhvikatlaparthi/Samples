package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext

data class GetQrNoteAndLogoPayload(
    var context: SecurityContext = SecurityContext()
)