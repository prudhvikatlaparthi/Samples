package com.pru.touchnote.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.pru.farmersapp.listeners.ToggleListener
import com.pru.touchnote.R
import java.io.Serializable


class ToggleTypeView : LinearLayout, Serializable {
    private var mContext: Context
    private lateinit var mOpenView: TextView
    private lateinit var mCloseView: TextView
    private var mIsHighSelected = false
    private var toggleListener: ToggleListener? = null
    private lateinit var mOpenViewWrapper: RelativeLayout
    private lateinit var mCloseViewWrapper: RelativeLayout

    constructor(context: Context) : super(context) {
        mContext = context
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        mContext = context
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        mContext = context
        init()
    }

    private fun init() {
        val mRootView =
            View.inflate(mContext, R.layout.toggle_view, this) as LinearLayout
        mOpenView = mRootView.findViewById(R.id.poll_selector_open)
        mCloseView = mRootView.findViewById(R.id.poll_selector_close)
        mOpenViewWrapper = mRootView.findViewById(R.id.selector_wrapper_1)
        mCloseViewWrapper = mRootView.findViewById(R.id.selector_wrapper_2)
        mOpenView.setOnClickListener(OnClickListener {
            setToggleType(mOpenView.getText().toString())
            toggleListener?.toggleChanged(mOpenView.getText().toString())
        })
        mCloseView.setOnClickListener(OnClickListener {
            setToggleType(mCloseView.getText().toString())
            toggleListener?.toggleChanged(mCloseView.getText().toString())
        })
    }

    val selectedToggleType: String
        get() = if (mIsHighSelected) mCloseView.text.toString() else mOpenView.text.toString()

    fun setToggleType(priorityValue: String) {
        if (mCloseView.text.toString() == priorityValue) {
            mIsHighSelected = true
            mOpenViewWrapper.background = ContextCompat.getDrawable(
                mContext,
                R.drawable.toggle_default_normal
            )
            mOpenView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.priority_default_text_color
                )
            )
            mCloseViewWrapper.background = ContextCompat.getDrawable(
                mContext,
                R.drawable.toggle_selected_high
            )
            mCloseView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.priority_selected_text_color
                )
            )
        } else { // First wrapper
            mIsHighSelected = false
            mOpenViewWrapper.background = ContextCompat.getDrawable(
                mContext,
                R.drawable.toggle_selected_normal
            )
            mOpenView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.priority_selected_text_color
                )
            )

            mCloseViewWrapper.background = ContextCompat.getDrawable(
                mContext,
                R.drawable.toggle_default_high
            )
            mCloseView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.priority_default_text_color
                )
            )
        }
    }

    fun setToggleNames(n1: String?, n2: String?) {
        mOpenView.text = n1
        mCloseView.text = n2
    }


    fun setToggleListener(toggleListener: ToggleListener?) {
        this.toggleListener = toggleListener
    }

    fun setToggleViewEnd() {
        setToggleType(mCloseView.text.toString())
        toggleListener?.toggleChanged(mCloseView.text.toString())
    }

    fun setToggleDisable(from: Int) {
        if (from == 1) {
            mOpenView.isEnabled = false
            mCloseView.isEnabled = false
            mOpenView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.separator
                )
            )
        } else {
            mOpenView.isEnabled = false
            mCloseView.isEnabled = false
            mCloseView.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.separator
                )
            )
        }
    }
}