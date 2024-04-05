package com.pru.dynamicapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.pru.dynamicapp.databinding.LayoutCTextViewBinding
import com.pru.dynamicapp.models.ViewItem

class CTextView(context: Context, attrs: AttributeSet? = null) :
    BaseView(context, attrs) {
    private lateinit var llParent: LinearLayout
    private lateinit var viewItem: ViewItem

    constructor(context: Context, viewItem: ViewItem, llParent: LinearLayout) : this(context) {
        this.viewItem = viewItem
        this.llParent = llParent
        setupView()
    }

    private val binding: LayoutCTextViewBinding = LayoutCTextViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {
        this.orientation = VERTICAL
        setupListeners()
    }

    private fun setupListeners() {

    }

    private fun setupView() {
        binding.tvView.text = viewItem.label
    }

    fun isMandatory(): Boolean {
        return viewItem.required ?: false
    }

    override fun getViewName(): String? {
        return viewItem.name
    }

    override fun triggerEvent(any: Any) {
        if (any is String) {
            binding.tvView.text = any.takeIf { it.isNotEmpty() } ?: viewItem.label
        }
    }
}