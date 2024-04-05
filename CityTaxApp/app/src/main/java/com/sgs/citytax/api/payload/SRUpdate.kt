package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class SRUpdate(@SerializedName("cmts")
                    var comments: String? = null, @SerializedName("svcreqno")
                    var serviceRequestNo: String? = null)