package com.sgs.citytax.ui.fragments

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.sgs.citytax.*
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.MethodReturn
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentTwoFactorAuthBinding
import com.sgs.citytax.ui.DashboardActivity
import com.sgs.citytax.util.Base32
import com.sgs.citytax.util.Base32.GenerateHashedCode
import com.sgs.citytax.util.Constant
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor

class TwoFactorAuthFragment : BaseFragment() {

    companion object {
        const val APP_PACKAGE_NAME = "com.google.android.apps.authenticator2"
        const val TAG = "TwoFactorAuthFragment>>"
    }

    var userName: String? = null
    var verificationCode: String? = null
    private var codes:ArrayList<String> = arrayListOf()
    private var prefHelper=MyApplication.getPrefHelper()
    var uniqueId=""
    private var secretKey=""
    lateinit var mBinding:FragmentTwoFactorAuthBinding

    override fun initComponents() {
       //Nothing to do
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        userName = arguments?.getString(Constant.USERNAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_two_factor_auth, container,false)

        mBinding.loginBtnId.setOnClickListener {
            doLogin()
        }

        mBinding.copyBtnId.setOnClickListener {
            val clipBoardManager =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoardManager.setPrimaryClip(ClipData.newPlainText("label", mBinding.secretKeyTextId.text))
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            true
        }

        if(prefHelper.authUniqueKey.isEmpty()){
            setVisibility(View.VISIBLE)
            loadQRCode()
        }else{
            uniqueId=prefHelper.authUniqueKey
            setVisibility(View.GONE)
        }
        return mBinding.root
    }

    private fun setVisibility(visibility: Int) {
        mBinding.QRimageViewId.visibility=visibility
        mBinding.secretKeyTextViewId.visibility=visibility
        mBinding.secretKeyTextId.visibility=visibility
        mBinding.copyBtnId.visibility=View.GONE
    }

    private fun doLogin() {
        //Must Do Login
        val userEnteredCode=mBinding.codeTextId.text.toString()
        if (uniqueId.isNotEmpty()) {
            codes = getCodes(uniqueId)
            if (codes.contains(userEnteredCode)) {

                if(prefHelper.authUniqueKey.isEmpty()){
                    APICall.updateAuth2FA(secretKey,uniqueId,object:ConnectionCallBack<Boolean>{
                        override fun onSuccess(response: Boolean) {
                            if(response){
                                val intent=Intent(context, DashboardActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            }else{
                                Toast.makeText(context,"Failed to store Key", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(message: String) {
                            Toast.makeText(context,"Error $message", Toast.LENGTH_LONG).show()
                        }

                    })
                }else{
                    val intent=Intent(context, DashboardActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            } else {
                showDialog(getString(R.string.incorrect_verification_code_text))
            }
        }

    }

    private fun showDialog(msg:String) {
        val mdialog = AlertDialog.Builder(context)
        mdialog.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }.setTitle(R.string.alert_dialog_title)
            .setMessage(msg)
            .create()
        mdialog.show()
    }


    private fun loadQRCode(){
        uniqueId=""
        uniqueId= UUID.randomUUID().toString().replace("-","").substring(0,10)
        val bytes = uniqueId.toByteArray(Charsets.UTF_8)
        secretKey= Base32.byteArrayToBase32(bytes)
        //Setting Up the secret Key
        mBinding.secretKeyTextId.text = secretKey
        val provisionUrl = (String.format("otpauth://totp/${userName}?secret=${secretKey.trim('=')}"))
        //creating QR Code and setting it up
        val qrCode:Bitmap=createQRCode(provisionUrl)
        Glide.with(this@TwoFactorAuthFragment).load(qrCode).into(mBinding.QRimageViewId)
        Log.d(TAG,"BASE 64 Encoding : >> $secretKey")
    }

    private fun getCodes(uniqueId:String):ArrayList<String> {
        //Getting the codes
        val codes:ArrayList<String> = arrayListOf()
        val epoch=System.currentTimeMillis()/1000
        val counter = floor(epoch/30.0).toLong()
        codes.add(GenerateHashedCode(uniqueId, counter - 1,6))
        codes.add(GenerateHashedCode(uniqueId, counter,6))
        codes.add(GenerateHashedCode(uniqueId, counter + 1,6))
        return codes
    }

    private fun createQRCode(str:String): Bitmap {
        val mHashtable = Hashtable<EncodeHintType, String?>()
        mHashtable[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val matrix = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 256, 256, mHashtable)
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (matrix[x, y]) {
                    pixels[y * width + x] = -0x1000000
                } else {
                    pixels[y * width + x] = -0x1
                }
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

}
