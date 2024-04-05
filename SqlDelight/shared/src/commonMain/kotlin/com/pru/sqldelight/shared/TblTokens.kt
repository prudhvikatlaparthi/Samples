package com.pru.sqldelight.shared

data class TblTokens(
    var staticToken: String? = null,
    var dynamicToken: String? = null,
    var secretKey: String? = null,
    var domainAccountID: Int? = null,
    var userOrgBRID: Int? = null
){
    override fun toString(): String {
        return staticToken+domainAccountID
    }
}