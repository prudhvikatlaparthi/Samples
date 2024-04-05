package com.pru.workdesigns

import android.content.Context
import android.widget.LinearLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlacesView(context: Context, val isThumbNails: Boolean = false) : LinearLayout(context) {
    init {
        initializeViews()
    }

    constructor(context: Context) : this(context, false)

    private fun initializeViews() {
        for (i in 0..10) {
            val placeItem = PlaceItem(context,isThumbNails)
                addView(placeItem)
        }
    }
}