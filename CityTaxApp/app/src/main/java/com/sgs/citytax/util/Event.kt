package com.sgs.citytax.util

import android.content.Intent

class Event private constructor() {
    var intent:Intent? = null
    fun hold(intent: Intent?) {
        this.intent = intent
    }

    fun clearData() {
        hold(null)
    }

    private object HOLDER {
        var INSTANCE = Event()
    }

    companion object {
        val instance: Event by lazy { HOLDER.INSTANCE }
    }
}