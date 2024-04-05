package com.sgs.citytax.ui.custom

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemHorizontalReportBinding
import com.sgs.citytax.util.formatWithPrecision
import java.math.BigDecimal

class HorizontalReportItem(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    var binding: ItemHorizontalReportBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.item_horizontal_report,
        this,
        true
    )

    init {
        this.orientation = VERTICAL
        setupListeners()
    }

    private fun setupListeners() {

    }

    fun updateView(
        value: String?,
        qty: BigDecimal?,
        price: BigDecimal?,
        removeIcon: Boolean = false,
        margin: Int? = null,
        @DrawableRes bgColor: Int? = null
    ) {
        binding.tvHeaderValue.text = value ?: ""
        binding.tvQty.text = qty?.stripTrailingZeros()?.toPlainString()
        binding.tvSalesPrice.text = formatWithPrecision(price, withCurrency = false)
        /*val lp = binding.headerReportItem.layoutParams as LayoutParams
        lp.setMargins(margin ?: lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
        binding.headerReportItem.layoutParams = lp*/

        bgColor?.let {
            this.background = ContextCompat.getDrawable(context, it)
        }
        if (removeIcon) {
            binding.tvHeaderValue.typeface = Typeface.DEFAULT
            binding.tvHeaderValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            binding.imgIcon.isVisible = false
        } else {
            binding.tvHeaderValue.typeface = Typeface.DEFAULT_BOLD
            binding.tvQty.typeface = Typeface.DEFAULT_BOLD
            binding.tvSalesPrice.typeface = Typeface.DEFAULT_BOLD
            binding.imgIcon.isVisible = true
        }
    }

    fun toggleItem(visible: Boolean) {
        if (visible) {
            binding.imgIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_expand_less
                )
            )
        } else {
            binding.imgIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_expand_more
                )
            )
        }
    }
}