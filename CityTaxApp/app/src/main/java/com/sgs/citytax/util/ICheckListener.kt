package com.sgs.citytax.util

import android.view.View

interface ICheckListener {
    fun onCheckedChange(view: View, position: Int, obj: Any, sts: String)
}