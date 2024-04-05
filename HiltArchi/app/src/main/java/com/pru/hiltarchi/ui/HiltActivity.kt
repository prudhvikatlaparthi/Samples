package com.pru.hiltarchi.ui

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.pru.hiltarchi.R
import com.pru.hiltarchi.databinding.ActivityDummyBinding
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HiltActivity : AppCompatActivity() {
    private lateinit var activityHiltBinding: ActivityDummyBinding
    private lateinit var smsVerifyCatcher: SmsVerifyCatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHiltBinding = ActivityDummyBinding.inflate(layoutInflater)
        setContentView(activityHiltBinding.root)

        smsVerifyCatcher = SmsVerifyCatcher(
            this
        ) { message ->
            println(message.filter { it.isDigit() })
            supportActionBar?.title = message.filter { it.isDigit() }
        }


    }

    override fun onStart() {
        super.onStart()
        smsVerifyCatcher.onStart()
    }

    override fun onStop() {
        super.onStop()
        smsVerifyCatcher.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}

fun roundOffDecimal2Points(number: Double?): String {
    if (number == null) {
        return String.format("%.2f", 0.toDouble())
    }
    return try {
        String.format("%.2f", number.toDouble())
    } catch (e: Exception) {
        e.printStackTrace()
        String.format("%.2f", 0.toDouble())
    }
}

fun Context.makeTextFormattedAmount(data: String): SpannableString {
    val prefix = "₹ "
//    val prefix = myPreferences.getStringValue(MyPreferences.PREF_CURRENCY_SYMBOL, "")
    val value = prefix + data
    val mSpannableString = SpannableString(value)
    return try {
        // initial string
        mSpannableString.setSpan(
            RelativeSizeSpan(0.8f),
            0, prefix!!.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        mSpannableString.setSpan(
            StyleSpan(ResourcesCompat.getFont(this, R.font.oswald)?.style ?: 0),
            0, prefix.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        // last string .00
        mSpannableString.setSpan(
            RelativeSizeSpan(0.8f),
            value.length - 3,
            value.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        mSpannableString.setSpan(
            StyleSpan(ResourcesCompat.getFont(this, R.font.open_sans)?.style ?: 0),
            value.length - 3,
            value.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        // middle string
        if (value.length - 4 > prefix.length + 1) {
            mSpannableString.setSpan(
                StyleSpan(ResourcesCompat.getFont(this, R.font.oswald_bold)?.style ?: 0),
                prefix.length + 1,
                value.length - 4,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        } else {
            mSpannableString.setSpan(
                StyleSpan(ResourcesCompat.getFont(this, R.font.oswald_bold)?.style ?: 0),
                prefix.length + 1,
                prefix.length + 1,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        mSpannableString
    } catch (e: Exception) {
        e.printStackTrace()
        mSpannableString
    }
}








