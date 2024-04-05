package com.pru.printlib.data

import android.graphics.Bitmap
import com.pru.printlib.utilities.ImageUtils

class DefaultPrintingImagesHelper: PrintingImagesHelper {
    override fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        return ImageUtils.decodeBitmap(bitmap)!!
    }

}