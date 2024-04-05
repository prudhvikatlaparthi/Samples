package com.pru.dynamicapp

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

abstract class BaseView(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    abstract fun getViewName(): String?
    abstract fun triggerEvent(any: Any)
}