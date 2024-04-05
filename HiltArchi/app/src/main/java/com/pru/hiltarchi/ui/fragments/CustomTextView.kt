package com.pru.hiltarchi.ui.fragments

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.pru.hiltarchi.R


class CustomTextView :
    androidx.appcompat.widget.AppCompatTextView {

    private var amountTextSize: Int = R.dimen.normalText
    private var amountTextColor: Int = ContextCompat.getColor(context, R.color.purple_200)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        amountTextColor: Int,
        amountTextSize: Int
    ) : super(context) {
        this.amountTextSize = amountTextSize
        this.amountTextColor = amountTextColor
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun update(index: Int, data: String) {
        this.setTextColor(amountTextColor)
        when (index) {
            0 -> {
                this.text = data
                val typeface = context.resources.getFont(R.font.open_sans)
                this.typeface = typeface
                this.textSize = 8f
            }
            1 -> {
                this.text = data
                val typeface = context.resources.getFont(R.font.oswald_bold)
                this.typeface = typeface
                this.textSize = 15f
            }
            else -> {
                this.text = data
                val typeface = context.resources.getFont(R.font.open_sans)
                this.typeface = typeface
                this.textSize = 8f
            }
        }
    }
}