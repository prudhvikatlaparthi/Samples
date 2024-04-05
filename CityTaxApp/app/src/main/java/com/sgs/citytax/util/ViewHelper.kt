package com.sgs.citytax.util

import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.URLSpan
import android.view.View
import android.view.View.*
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import java.math.BigDecimal


class URLSpanNoUnderline(url: String?) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}

fun stripUnderlines(textView: TextView) {
    val s: Spannable = SpannableString(textView.text)
    val spans = s.getSpans(0, s.length, URLSpan::class.java)
    for (span in spans) {
        val start = s.getSpanStart(span)
        val end = s.getSpanEnd(span)
        s.removeSpan(span)
        s.setSpan(URLSpanNoUnderline(span.url), start, end, 0)
    }
    textView.text = s
}

fun showUnderLineText(textView: TextView, string: String){
    textView.setPaintFlags(textView.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
    textView.setText(string)
}

@BindingAdapter("formatWithPrecision")
fun TextView.setFormatWithPrecision(value: Double?) {
    this.text = formatWithPrecision(value)
}
@BindingAdapter("formatWithPrecision")
fun TextView.setFormatWithPrecision(value: Int?) {
    this.text = formatWithPrecision(value?.toDouble())
}
@BindingAdapter("formatWithPrecision")
fun TextView.setFormatWithPrecision(value: BigDecimal?) {
    this.text = formatWithPrecision(value?.toDouble())
}

fun View?.show() {
    this?.visibility = VISIBLE
}

fun View?.hide() {
    this?.visibility = GONE
}

fun View?.invisible() {
    this?.visibility = INVISIBLE
}

fun View?.enable() {
    this?.isEnabled = true
}

fun View?.disable() {
    this?.isEnabled = false
}

@BindingAdapter("show_hide")
fun View.showHide(value: String?) {
    this.isVisible = value?.let {
        true
    }  ?: false
}