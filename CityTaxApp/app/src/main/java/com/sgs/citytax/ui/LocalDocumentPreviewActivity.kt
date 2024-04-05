package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityDocumentPeviewBinding
import com.sgs.citytax.model.LocalDocument
import com.sgs.citytax.ui.adapter.LocalDocumentPreviewAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener


class LocalDocumentPreviewActivity : BaseActivity() {

    private lateinit var mBinding: ActivityDocumentPeviewBinding
    private var localDocuments: ArrayList<LocalDocument> = arrayListOf()
    private val localDocumentPreviewAdapter: LocalDocumentPreviewAdapter by lazy {
        LocalDocumentPreviewAdapter(localDocuments, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                loadMainImage(localDocuments[position])
            }

            override fun onLongClick(view: View, position: Int, obj: Any) {

            }
        })
    }

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
                localDocuments =
                    intent.getParcelableArrayListExtra<LocalDocument>(Constant.KEY_DOCUMENT) as ArrayList<LocalDocument>? ?: arrayListOf()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    fun bindData() {
        mBinding.txtDocumentNo.visibility = View.GONE
        if (localDocuments.isNotEmpty()) {
            mBinding.rcvDocumentPreviews.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            mBinding.rcvDocumentPreviews.adapter = localDocumentPreviewAdapter
            mBinding.imgDocumentPreview.visibility = View.VISIBLE
            loadMainImage(localDocuments[0])
        } else {
            mBinding.imgDocumentPreview.visibility = View.GONE
        }
    }

    private fun loadMainImage(localImage: LocalDocument) {
        Glide.with(this)
            .asBitmap()
            .load(localImage.localSrc)
            .override(1600, 1600)
            .placeholder(R.drawable.ic_place_holder)
            .into(mBinding.imgDocumentPreview)
    }
}