package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName

data class GetAdministrationOffice(
    @SerializedName("acctid")
    val acctid: Int? = null,
    @SerializedName("acctname")
    val acctname: String? = null,
    @SerializedName("accttyp")
    val accttyp: String? = null,
    @SerializedName("accttypcode")
    val accttypcode: String? = null,
    @SerializedName("orgz")
    val orgz: String? = null,
    @SerializedName("usrorgbrid")
    val usrorgbrid: Int? = null
)