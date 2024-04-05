package com.pru.hiltarchi.ui.fragments

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.pru.hiltarchi.R
import com.pru.hiltarchi.databinding.LayoutCustomAmoutBinding

@RequiresApi(Build.VERSION_CODES.O)
class CustomViewAmount(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {
    private var amountTextSize: Int = R.dimen.normalText
    private var amountTextColor: Int = ContextCompat.getColor(context, R.color.purple_200)
    private var applyStrike: Boolean = false
    private lateinit var binding: LayoutCustomAmoutBinding

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomViewAmount)
        applyStrike = typedArray.getBoolean(R.styleable.CustomViewAmount_applyStrike, false)
        amountTextColor =
            typedArray.getColor(
                R.styleable.CustomViewAmount_textColor,
                ContextCompat.getColor(context, R.color.purple_200)
            )
        amountTextSize =
            typedArray.getColor(R.styleable.CustomViewAmount_textSize, R.dimen.normalText)

        typedArray.recycle()
        initializeView()
    }
/*
    constructor(context: Context) : super(context) {
        initializeView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initializeView(context, attrs)
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeView() {
        binding = LayoutCustomAmoutBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
//        binding.strikeThrough.isVisible = applyStrike
        val list = listOf("$", " 120", ".00")
        list.forEachIndexed { index, data ->
            val customTextView = CustomTextView(context, amountTextColor, amountTextSize)
            customTextView.update(index, data)
            binding.mainView.addView(customTextView)
        }
        if (applyStrike) {
            binding.mainView.alpha = 0.5f
            binding.strikeThrough.alpha = 0.7f
            binding.strikeThrough.isVisible = true
        }
//        binding.strikeThrough.w
    }
}