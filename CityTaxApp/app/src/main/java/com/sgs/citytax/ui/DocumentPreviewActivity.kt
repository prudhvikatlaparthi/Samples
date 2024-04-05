package com.sgs.citytax.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityDocumentPeviewBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.adapter.DocumentPreviewAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.LogHelper


class DocumentPreviewActivity : BaseActivity(), IClickListener {

    private lateinit var mBinding: ActivityDocumentPeviewBinding
    private var comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()
    private var scaleGestureDetector: ScaleGestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_document_peview)
        showToolbarBackButton(R.string.title_documents_preview)
        processIntent()
        bindData()
    }

    private fun processIntent() {
        intent.let {
            if (it.hasExtra(Constant.KEY_DOCUMENT))
                comDocumentReferences = intent.getParcelableArrayListExtra<COMDocumentReference>(Constant.KEY_DOCUMENT) as ArrayList<COMDocumentReference>
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    fun bindData() {
        if (comDocumentReferences.isNotEmpty()) {
            showProgressDialog()
            mBinding.rcvDocumentPreviews.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            mBinding.rcvDocumentPreviews.adapter = DocumentPreviewAdapter(comDocumentReferences, this)
            if (comDocumentReferences[0].awsfile != null && comDocumentReferences[0].awsfile!!.isNotEmpty()) {
                mBinding.txtDocumentNo.text = comDocumentReferences[0].documentNo
                getBitmapFromURL(comDocumentReferences[0].awsfile)
            } else if (comDocumentReferences[0].data != null && comDocumentReferences[0].data!!.isNotEmpty()){
                loadBitmapFromBase64String(comDocumentReferences[0])

            } else {
                mBinding.imgDocumentPreview.visibility = View.GONE
                mBinding.txtDocumentNo.visibility = View.GONE
            }
            dismissDialog()
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector?.onTouchEvent(event)
        return true
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.itemImageDocumentPreview -> {
//                    TouchImageView(this).resetZoom()
//                    mBinding.imgDocumentPreview.resetZoom()
                    val comDocumentReference = obj as COMDocumentReference
                    if (comDocumentReference.awsfile != null && comDocumentReference.awsfile!!.isNotEmpty()) {
                        mBinding.txtDocumentNo.text = comDocumentReference.documentNo
                        getBitmapFromURL(comDocumentReference.awsfile)
                    } else if (comDocumentReference.data != null && comDocumentReference.data!!.isNotEmpty()){
                        loadBitmapFromBase64String(comDocumentReference)
                    } else {
                        mBinding.imgDocumentPreview.visibility = View.GONE
                        mBinding.txtDocumentNo.visibility = View.GONE
                    }
                }
                else -> {

                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    private fun loadBitmapFromBase64String(comDocumentReference: COMDocumentReference){
        if (comDocumentReference.data != null && comDocumentReference.data!!.isNotEmpty()) {
            val imageBytes: String = comDocumentReference.data!!
            val imageByteArray: ByteArray = Base64.decode(imageBytes, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            mBinding.imgDocumentPreview.setImageBitmap(bitmap)
        }
    }

    private fun getBitmapFromURL(src: String?) {
        try {
            showProgressDialog()
            Glide.with(this)
                .asBitmap()
                .load(src)
                .override(1600, 1600)
                .placeholder(R.drawable.ic_place_holder)
                .into(object : BitmapImageViewTarget(mBinding.imgDocumentPreview) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        super.onResourceReady(resource, transition)
                        dismissDialog()
                    }
                })

            /*showProgressDialog()
            Glide.with(this)
                    .asBitmap().load(src)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>?, isFirstResource: Boolean): Boolean {
                            dismissDialog()
                            return false
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            dismissDialog()
                            mBinding.imgDocumentPreview.setImageBitmap(resource)
                            return false
                        }
                    }
                    ).submit()*/
            /*val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)*/
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
    }
}