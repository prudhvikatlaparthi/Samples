package com.pru.printlib.data

import android.graphics.Bitmap

interface PrintingImagesHelper {
    fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray
}