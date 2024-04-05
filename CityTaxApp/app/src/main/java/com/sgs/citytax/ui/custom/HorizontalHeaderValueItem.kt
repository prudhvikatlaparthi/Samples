package com.sgs.citytax.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemHorizontalHeaderValueBinding

class HorizontalHeaderValueItem(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    private var binding: ItemHorizontalHeaderValueBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.item_horizontal_header_value,
        this,
        true
    )

    init {
        setupListeners()
    }

    private fun setupListeners() {

    }

    fun updateView(pair: Pair<String, Any>?) {
        pair?.let {
            binding.tvHeader.text = pair.first
            binding.tvValue.text = pair.second.toString().trim()
        }
    }
}