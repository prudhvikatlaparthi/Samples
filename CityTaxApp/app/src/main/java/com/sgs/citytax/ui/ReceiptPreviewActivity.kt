package com.sgs.citytax.ui

import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityReceiptPreviewBinding
import com.sgs.citytax.util.Constant


class ReceiptPreviewActivity : BaseActivity() {

    private lateinit var binding: ActivityReceiptPreviewBinding
    val MAX_PROGRESS = 100
    private var url: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_preview)
        showToolbarBackButton(R.string.receipt_preview)
        initWebView()
        setWebClient()
        processIntent()

    }

    private fun initWebView() {

        binding.webViewReceiptPreview.settings.builtInZoomControls = true;
        binding.webViewReceiptPreview.settings.displayZoomControls = false;
        binding.webViewReceiptPreview.settings.javaScriptEnabled = true
        binding.webViewReceiptPreview.settings.loadWithOverviewMode = true
        binding.webViewReceiptPreview.settings.useWideViewPort = true
        binding.webViewReceiptPreview.settings.domStorageEnabled = true

        binding.webViewReceiptPreview.webViewClient = object : WebViewClient() {
            override
            fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }
    }

    private fun setWebClient() {
        binding.webViewReceiptPreview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progressBar.progress = newProgress
                if (binding.progressBar.visibility == View.GONE && newProgress < MAX_PROGRESS) {
                    binding.progressBar.visibility = View.VISIBLE
                }

                if (newProgress == MAX_PROGRESS) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_RECEIPT_PREVIEW))
                url = intent.getStringExtra(Constant.KEY_RECEIPT_PREVIEW)
            binding.webViewReceiptPreview.loadUrl(url)
        }
    }
}