package com.pru.nfcapp

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import androidx.core.content.ContextCompat

class SwingCardHintDialog(
    context: Context,
    /** 0-NFC,1-IC,2-NFC IC  */
) : Dialog(context, R.style.DefaultDialogStyle) {
    init {
        init()
    }

    private fun init() {
        setContentView(R.layout.dialog_swing_card_hint)
        val imgView = findViewById<ImageView>(R.id.src_img)
        imgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.card_nfc))
        if (window != null) {
            window!!.attributes.gravity = Gravity.CENTER
        }

        setCanceledOnTouchOutside(false)
        setCancelable(true)
    }
}