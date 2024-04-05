package com.pru.singleactivity.ui.address

import com.pru.singleactivity.base.Result
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressResult(
    var isRefresh: Boolean
) : Result()
