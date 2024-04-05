package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.Account

data class AccountsResponse(
    @SerializedName("Table")
    var table : List<Account> = arrayListOf()
)