package com.sgs.citytax.util

import android.view.View

interface IClickListener {

    fun onClick(view: View, position: Int, obj: Any)

    fun onLongClick(view: View, position: Int, obj: Any)

}
