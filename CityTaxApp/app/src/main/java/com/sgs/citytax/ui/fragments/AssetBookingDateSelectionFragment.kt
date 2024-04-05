package com.sgs.citytax.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.BuildConfig
import com.sgs.citytax.R
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentAssetBookingDateSelectionBinding
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_FROM_DATE
import com.sgs.citytax.util.Constant.KEY_TO_DATE

class AssetBookingDateSelectionFragment : BaseFragment() {

    private lateinit var mBinding: FragmentAssetBookingDateSelectionBinding
    private var mListener: Listener? = null
    private var mCategoryID: Int? = 0
    private var mTennureID: Int? = 0
    private var mAssetID: Int? = 0
    private var mBranchID: Int? = 0
    private var mQuantity: String? = ""
    private var mBookingRequestLineID: Int? = 0

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_ASSET_CATEGORY_ID))
                mCategoryID = it.getInt(Constant.KEY_ASSET_CATEGORY_ID)
            if (it.containsKey(Constant.KEY_ASSET_ID))
                mAssetID = it.getInt(Constant.KEY_ASSET_ID)
            if (it.containsKey(Constant.KEY_BRANCH_ID))
                mBranchID = it.getInt(Constant.KEY_BRANCH_ID)
            if (it.containsKey(Constant.KEY_QUANTITY))
                mQuantity = it.getString(Constant.KEY_QUANTITY)
            if (it.containsKey(Constant.KEY_BOOKING_REQUEST_LINE_ID))
                mBookingRequestLineID = it.getInt(Constant.KEY_BOOKING_REQUEST_LINE_ID)
            if (it.containsKey(Constant.KEY_TENURE_REQUEST_LINE_ID))
                mTennureID = it.getInt(Constant.KEY_TENURE_REQUEST_LINE_ID)

        }
        // endregion
        setViews()
        setEvents()
        bindData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_asset_booking_date_selection, container, false)
        initComponents()
        return mBinding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setViews() {
        mBinding.webView.settings.loadWithOverviewMode = true
        mBinding.webView.settings.useWideViewPort = true
        mBinding.webView.settings.javaScriptEnabled = true
    }

    private fun setEvents() {
        mBinding.webView.webChromeClient = chromeClient
        mBinding.webView.webViewClient = webClient
    }

    private fun bindData() {
        mBinding.webView.clearCache(true)
        val url = "${BuildConfig.RECEIPT_BASE_URL}Modules/AST/AST_AssetCalender.aspx?catID=${mCategoryID ?: 0}&tenr=${mTennureID ?: 0}&astID=${mAssetID ?: 0}&qty=${mQuantity ?: 1}&usrbrchID=${mBranchID ?: 0}&beqrlnID=${mBookingRequestLineID ?: -1}&lngCd=${if (MyApplication.getPrefHelper().language.isEmpty()) "FR" else MyApplication.getPrefHelper().language}&isandroid=true"
        mBinding.webView.loadUrl(url)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    private val chromeClient = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            consoleMessage?.message()?.let {
                if (it.contains("BookinDate"))
                    setResult(it.replace("BookinDate:", ""))
            }
            return true
        }
    }

    private fun setResult(data: String) {
        val intent = Intent()
        val list = data.split(",")
        if (list.size == 2) {
            intent.putExtra(KEY_FROM_DATE, list[0])
            intent.putExtra(KEY_TO_DATE, list[1])
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        mListener?.popBackStack()
    }

    private val webClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mListener?.dismissDialog()
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mListener?.showProgressDialog()
        }
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun popBackStack()
    }

}