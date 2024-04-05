package com.sgs.citytax.api.payload

import com.sgs.citytax.api.SecurityContext

data class GetMessageConnectionPayload(var context: SecurityContext? = SecurityContext())

