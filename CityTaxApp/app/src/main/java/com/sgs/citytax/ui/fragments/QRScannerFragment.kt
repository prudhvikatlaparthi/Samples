package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.Result
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.util.Constant.KEY_QR_CODE_DATA
import com.sgs.citytax.util.Constant.REQUEST_CODE_CAMERA
import com.sgs.citytax.util.hasPermission
import com.sgs.citytax.util.isPermissionGranted
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QRScannerFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    private lateinit var mScannerView: ZXingScannerView
    private lateinit var mContext: Context

    companion object {
        @JvmStatic
        fun newInstance() = QRScannerFragment().apply { }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initComponents()
        mScannerView = ZXingScannerView(activity)
        if (!MyApplication.getPrefHelper().isScanEnabled)
        {
            mScannerView.visibility = View.INVISIBLE
        }
        return mScannerView
    }

    override fun initComponents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasPermission(mContext, Manifest.permission.CAMERA)) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
            return
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun startCamera() {
        mScannerView.setResultHandler(this)
        mScannerView.startCamera()
    }

    override fun handleResult(rawResult: Result?) {
        val intent = Intent()
        intent.putExtra(KEY_QR_CODE_DATA, rawResult?.text)
        mScannerView?.stopCamera()
        val handler = Handler()
        handler.postDelayed({
            targetFragment?.onActivityResult(
                    targetRequestCode,
                    RESULT_OK,
                    intent)
        }, 500)
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (isPermissionGranted(grantResults))
                startCamera()
        }
    }

}