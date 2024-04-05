package com.sgs.citytax.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivitySignatureBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.ImageHelper

class SignatureActivity : BaseActivity(){
    private lateinit var mBinding:ActivitySignatureBinding
    private var documentList:ArrayList<COMDocumentReference> = arrayListOf()
    private var assetNo:Int?=0

    override fun onCreate(savedInstanceState    : Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signature)
        showToolbarBackButton(R.string.title_signature)
        processIntent()
        setListeners()

    }

    private fun processIntent(){
        intent.extras?.let {
            if (it.containsKey(Constant.KEY_ASSET_ID))
            assetNo = it.getInt(Constant.KEY_ASSET_ID)
        }
    }

    private fun setListeners(){
        mBinding.btnSave.setOnClickListener{
                    val bitmap = mBinding.signatureView.getTransparentSignatureBitmap(true)
                    if (bitmap != null){
                        documentList.clear()
                        val comDocumentReference = COMDocumentReference()
                        comDocumentReference.data = ImageHelper.getBase64String(bitmap)
                        comDocumentReference.extension = "jpeg"
                        comDocumentReference.documentName = assetNo.toString()
                        documentList.add(comDocumentReference)
                        val intent = Intent()
                        intent.putExtra(Constant.KEY_DOCUMENT,documentList)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }

        }
        mBinding.btnClear.setOnClickListener {
            mBinding.signatureView.clear()
        }
    }
}