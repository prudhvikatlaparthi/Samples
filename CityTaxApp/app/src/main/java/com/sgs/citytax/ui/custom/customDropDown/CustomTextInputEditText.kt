package com.sgs.citytax.ui.custom.customDropDown

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.WindowManager
import com.google.android.material.textfield.TextInputEditText
import com.sgs.citytax.util.Pagination

class CustomTextInputEditText : TextInputEditText {

    private lateinit var _context: Context
    lateinit var pagination: Pagination
    lateinit var customDropdownMenu: CustomDropdownMenu<*>
    lateinit var listener: OnCustomListener

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, null)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        _context = context
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnClickListener() {
        pagination = Pagination(1, 20, customDropdownMenu.recyclerView) { pageNumber, _ ->
            listener.let {
                listener.onPaginationScroll(pageNumber, text.toString())
            }
        }
        setOnTouchListener { _, event ->
            val mDRAWABLEmRIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (right - compoundDrawables[mDRAWABLEmRIGHT].bounds.width())) {
                    pagination.setDefaultValues()
                    true
                }
            }
            false
        }
    }

    fun setCustomDropdown(mCustomDropdownMenu: CustomDropdownMenu<*>) {
        customDropdownMenu = mCustomDropdownMenu
        setOnClickListener()
        customDropdownMenu.height = WindowManager.LayoutParams.WRAP_CONTENT
        customDropdownMenu.width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        customDropdownMenu.isOutsideTouchable = true
        customDropdownMenu.isFocusable = true
    }


    fun setOnCustomListener(listener: OnCustomListener) {
        this.listener = listener
    }

    fun setDataList(result: ArrayList<*>) {
        pagination.setIsScrolled(false)
        if (result.isNotEmpty()) {
            pagination.stopPagination(result.size)
        } else {
            pagination.stopPagination(0)
        }
    }

    fun showPopup() {
        customDropdownMenu.showAsDropDown(this)
    }

    interface OnCustomListener {
        fun onPaginationScroll(pageIndex: Int, text: String)
    }
}