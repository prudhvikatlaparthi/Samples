package com.pru.responsiveapp.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.pru.responsiveapp.FirebaseAnalyticsLogger
import com.pru.responsiveapp.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyButton(private val myContext: Context, private val attributeSet: AttributeSet? = null) :
    AppCompatButton(myContext, attributeSet) {
    @Inject
    lateinit var firebaseAnalyticsLogger: FirebaseAnalyticsLogger

    init {
        prepareButton()
    }

    private fun prepareButton() {
        val typedArray = myContext.obtainStyledAttributes(attributeSet, R.styleable.MyButton)

        val sendEvent = typedArray.getBoolean(R.styleable.MyButton_sendEvent, false)
        val eventName = typedArray.getString(R.styleable.MyButton_eventName)

        setOnClickListener {
            if (sendEvent && eventName.orEmpty().trim().isNotEmpty()) {
                firebaseAnalyticsLogger.sendClickEvent(eventName=eventName!!)
            }
        }

        typedArray.recycle()
    }
}