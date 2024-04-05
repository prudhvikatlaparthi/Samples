package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AdminOfficeAddressResponse(
    @SerializedName("Table" ) val Table: List<AdminOfficeAdress> = listOf()
)