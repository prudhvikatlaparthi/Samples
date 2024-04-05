package com.pru.workdesigns

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.pru.workdesigns.databinding.LayoutCityItemBinding

class PlaceItem(context: Context, val isThumbNails: Boolean = false) : ConstraintLayout(context) {
    private lateinit var binding: LayoutCityItemBinding

    init {
        initializeViews()
    }

    constructor(context: Context) : this(context, false)

    private fun initializeViews() {
        binding = LayoutCityItemBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
        if (isThumbNails) {
            binding.root.layoutParams = LayoutParams(100.dp, ViewGroup.LayoutParams.WRAP_CONTENT)
            binding.group2.isVisible = false
        }
    }

    val Int.dp: Int
        get() = (this * context.resources.displayMetrics.density).toInt()
}

