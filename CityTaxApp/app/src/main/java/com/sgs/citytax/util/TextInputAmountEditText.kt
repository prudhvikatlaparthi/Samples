package com.sgs.citytax.util

import android.content.Context
import android.content.res.TypedArray
import android.text.InputFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import com.google.android.material.textfield.TextInputEditText
import com.sgs.citytax.R


class TextInputAmountEditText(context: Context, attrs: AttributeSet?) : TextInputEditText(context, attrs), OnFocusChangeListener {

    private var editText: TextInputEditText? = null
    var decimalCustomValue: Int = 0
    var showPrecision: Boolean = false

    init {
        if (attrs != null) {
            val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TextInputAmountEditText)
            this.decimalCustomValue = typedArray.getInt(R.styleable.TextInputAmountEditText_customDecimals, 0)
            this.showPrecision = typedArray.getBoolean(R.styleable.TextInputAmountEditText_showPrecision, false)
            typedArray.recycle()
        }
        init(decimalCustomValue)
    }


    private fun init(value: Int) {
        editText = this
        editText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9 + 15))
        editText!!.onFocusChangeListener = this

        this.decimalCustomValue = value;
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            val text: String = editText?.text.toString()
            editText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
            if (text.isNotEmpty()) {
                editText?.setText("${currencyToDouble(text)}")
            }
        } else {
            //this if condition is true when editText lost focus...
            //check here for number is larger than 10 or not
            val cost = editText?.text.toString()
            if (!TextUtils.isEmpty(cost)) {
                val enteredText: Double = getDecimalVal(cost)
                editText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9 + 15))
                if (decimalCustomValue == 0)
                    editText?.setText(formatWithPrecision(enteredText))
                else
                    editText?.setText(formatWithPrecisionCustomDecimals(enteredText.toString(), showPrecision, decimalCustomValue))
            }
        }
    }

}