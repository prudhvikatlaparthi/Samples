package com.pru.singleactivity.ui.address

import com.pru.singleactivity.base.Args
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressArgs(
    var userId: Int
) : Args()
