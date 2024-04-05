package com.pru.dynamicapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import com.pru.dynamicapp.databinding.LayoutCEditTextBinding
import com.pru.dynamicapp.models.ViewItem

class CEditText(context: Context, attrs: AttributeSet? = null) :
    BaseView(context, attrs) {
    private lateinit var llParent: LinearLayout
    private lateinit var viewItem: ViewItem

    constructor(context: Context, viewItem: ViewItem, llParent: LinearLayout) : this(context) {
        this.viewItem = viewItem
        this.llParent = llParent
        setupView()
    }

    private val binding: LayoutCEditTextBinding = LayoutCEditTextBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {
        this.orientation = VERTICAL
        setupListeners()
    }

    private fun setupListeners() {
        binding.etView.addTextChangedListener {
            if (viewItem.child != null) {
                llParent.children.forEach {
                    val child = it as BaseView
                    if (viewItem.child?.contains(child.getViewName() ?: "") == true) {
                        child.triggerEvent(binding.etView.text.toString())
                    }
                }
            }
        }
    }

    private fun setupView() {
        binding.tvLabel.text = viewItem.label
    }

    fun isMandatory(): Boolean {
        return viewItem.required ?: false
    }

    override fun getViewName(): String? {
        return viewItem.name
    }

    override fun triggerEvent(any: Any) {

    }
}