package com.sgs.citytax.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.VerifyBusinessContacts
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentOtpValidationBinding
import com.sgs.citytax.ui.DashboardActivity
import java.util.*
import java.util.concurrent.TimeUnit


class OTPValidationFragment : BaseFragment() {

    var prefix = ""
    var smsOTP = 0
    var OTP = ""
    val TAG = "OTPValidationFragment>>"
    var timer: CountDownTimer? = null
    var v:View?=null
    lateinit var mBinding:FragmentOtpValidationBinding

    override fun initComponents() {
        //nothing to do
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_otp_validation,container,false)
        this.v=mBinding.root

       generateOtp()

        val mblNumber=MyApplication.getPrefHelper().agentContryCode+""+MyApplication.getPrefHelper().agentMobile

        mBinding.otpTextId.text = getString(R.string.otp_text, mblNumber.subSequence(mblNumber.length-4,mblNumber.length))

        mBinding.requestOtpBtnId.visibility = View.VISIBLE
        mBinding.otpLayoutId.visibility = View.GONE

        mBinding.requestOtpBtnId.setOnClickListener {
            sendSmsOTP(OTP, mblNumber)
        }

        mBinding.resendTextId.setOnClickListener {
            generateOtp()
            mBinding.resendTextId.isEnabled = false
            mBinding.resendTextId.setTextColor(resources.getColor(android.R.color.darker_gray))
            sendSmsOTP(OTP, mblNumber)
        }

        mBinding.smsOTPLoginBtnId.setOnClickListener {
            doLogin()
        }
        return mBinding.root
    }

    private fun generateOtp() {
        prefix = getRandomAlphabet()
        smsOTP = getRandomNumber()
        OTP = "$prefix - $smsOTP"
        mBinding.prefixTVId.text = "$prefix -"
    }

    private fun doLogin() {
        if (mBinding.smsOTPFieldId.text.toString() == smsOTP.toString()) {
            timer?.cancel()
            val intent= Intent(context, DashboardActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else {
            showDialog(getString(R.string.incorrect_otp_text))
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

    private fun sendSmsOTP(otp: String, mob: String) {

        val verifyBusinessContacts = VerifyBusinessContacts()
        verifyBusinessContacts.mobile = mob
        verifyBusinessContacts.mobileOTP = otp

        APICall.verifyBusinessContacts(mob, otp, "", "", "LOGIN_OTP",MyApplication.getPrefHelper().accountId, object :
            ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                if (response) {
                    mBinding.requestOtpBtnId.visibility = View.GONE
                    mBinding.otpLayoutId.visibility = View.VISIBLE
                    countDown()
                } else {
                    Log.d(TAG, "API Calling Failed")
                }
            }

            override fun onFailure(message: String) {
                showDialog(message)
                Log.d(TAG, "Error :  $message")
            }
        })
    }

    private fun countDown() {
        mBinding.resendTextId.isEnabled = false
        mBinding.resendTextId.setTextColor(resources.getColor(android.R.color.darker_gray))
        timer = object : CountDownTimer(1000 * 60, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val hms = String.format(
                    Locale.getDefault(), "%02d",
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                ) + "sec"
                mBinding.resendTextId.text = v?.context?.getString(R.string.resend_text, hms)

            }

            override fun onFinish() {
                mBinding.resendTextId.text = v?.context?.getString(R.string.resend_text, "")
                mBinding.resendTextId.setTextColor(ContextCompat.getColor(mBinding.resendTextId.context, R.color.black))
                mBinding.resendTextId.isEnabled = true
            }
        }

        timer?.start()

    }

//    override fun onStop() {
//        timer?.cancel()
//        super.onStop()
//    }

    private fun getRandomNumber(): Int {
        val min = 100000
        val max = 999999
        return Random().nextInt(max - min + 1) + min
    }

    private fun getRandomAlphabet(): String {
        val alphabets = arrayListOf<Char>(
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'U',
            'V',
            'W',
            'X',
            'Y',
            'Z'
        )
        val min = 0
        val max = 25
        return "${alphabets[Random().nextInt(max - min + 1) + min]}${alphabets[Random().nextInt(max - min + 1) + min]}${
            alphabets[Random().nextInt(
                max - min + 1
            ) + min]
        }"
    }

}
