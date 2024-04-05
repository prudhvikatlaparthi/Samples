package com.pru.customspinner.searchSpinner

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.SpinnerAdapter
import androidx.appcompat.widget.AppCompatSpinner

class SearchSpinner(context: Context,attrs : AttributeSet) :
    AppCompatSpinner(context,attrs,0), View.OnTouchListener {

    override fun setAdapter(adapter: SpinnerAdapter?) {
        super.setAdapter(adapter)
    }

    override fun getSelectedItemPosition(): Int {
        return super.getSelectedItemPosition()
    }

    override fun getSelectedItem(): Any {
        return super.getSelectedItem()
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        return true
    }

}