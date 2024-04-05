package com.sgs.citytax.model

import com.sgs.citytax.util.Constant

data class QuickMenuItem(
        var name: String = "",
        var resourceID: Int = 0,
        var code: Constant.QuickMenu,
        var count:Int=0
)