package com.sgs.citytax.ui

import android.graphics.Bitmap
import android.os.Bundle
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
import com.sgs.citytax.model.COMPropertyPlanImage
import com.sgs.citytax.ui.adapter.PropertyPlanImagePreviewAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.LogHelper

class PropertyPlansPreviewActivity : BaseActivity(), IClickListener{
    private lateinit var mBinding: ActivityDocumentPeviewBinding
    private var propertyPlans: ArrayList<COMPropertyPlanImage> = arrayListOf()
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_document_peview)
        processIntent()
        bindData()
        if(mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND || mCode == Constant.QuickMenu.QUICK_MENU_CREATE_LAND) {
            showToolbarBackButton(R.string.land_images)
        }else
            showToolbarBackButton(R.string.property_plan_documents)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun processIntent() {
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_PROPERTY_PLAN_IMAGE))
                propertyPlans = it.getParcelableArrayList<COMPropertyPlanImage>(Constant.KEY_PROPERTY_PLAN_IMAGE) as ArrayList<COMPropertyPlanImage>
            if (it.containsKey(Constant.KEY_QUICK_MENU)) {
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            }
        }
    }
    
    private fun bindData(){
        if (propertyPlans.isNotEmpty()) {
            showProgressDialog()
            mBinding.rcvDocumentPreviews.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            mBinding.rcvDocumentPreviews.adapter = PropertyPlanImagePreviewAdapter(propertyPlans, this)

            propertyPlans[0].awsPath?.let {
                getBitmapFromURL(it)
                mBinding.txtDocumentNo.text = propertyPlans[0].propertyPlanId.toString()
            }
        } else {
            mBinding.imgDocumentPreview.visibility = View.GONE
            mBinding.txtDocumentNo.visibility = View.GONE
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector?.onTouchEvent(event)
        return true
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
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.itemPropertyPlanImagePreview -> {
//                    TouchImageView(this).resetZoom()
//                    mBinding.imgDocumentPreview.resetZoom()
                    val propertyPlan = obj as COMPropertyPlanImage
                    if (propertyPlan.awsPath != null && propertyPlan.awsPath!!.isNotEmpty()) {
                        mBinding.txtDocumentNo.text = propertyPlan.propertyPlanId.toString()
                        getBitmapFromURL(propertyPlan.awsPath)
                    } else {
                        mBinding.imgDocumentPreview.visibility = View.GONE
                        mBinding.txtDocumentNo.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }
}