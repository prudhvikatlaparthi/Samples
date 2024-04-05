package com.sgs.citytax.ui.custom

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ImageOrientationViewBinding
import com.sgs.citytax.util.ImageHelper
import java.io.File
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.View
import com.sgs.citytax.util.OnSingleClickListener


class ImageChangeOrientation : BaseActivity() {
     lateinit var ico:ImageOrientationViewBinding
        var data: Bitmap? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ico = DataBindingUtil.setContentView(this, R.layout.image_orientation_view)
        val intent = intent
        var mImageFilePath: String?= null;
        if (intent!=null) {
            mImageFilePath = intent.getStringExtra("url")
        }
        mImageFilePath?.let {
            data = ImageHelper.decodeFile(File(it))
            ico.imageView.setImageBitmap(data)

            ico.rotateLeft.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    rotateRightImage()
                }
            })
            ico.rotateRight.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    rotateRightImage(true)
                }
            })
            ico.btnOk.setOnClickListener(
                object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        val baseImageData = ImageHelper.getBase64String(data)
                        val exit = Intent()
                        exit.putExtra("image", baseImageData)
                        setResult(150, exit)
                        finish()
                    }
                }
            )
        }

    }

    private fun rotateRightImage(isRight:Boolean = false){
        var bMapRotate: Bitmap? = null
        val mat = Matrix()
        if (isRight) {
            mat.postRotate(90f)
        }else{
            mat.postRotate(-90f)
        }
        bMapRotate = Bitmap.createBitmap(data!!, 0, 0, data!!.getWidth(), data!!.getHeight(), mat, true)
        data!!.recycle()
        data = bMapRotate
        ico.imageView.setImageBitmap(bMapRotate)
    }

    override fun onBackPressed() {
    }
}