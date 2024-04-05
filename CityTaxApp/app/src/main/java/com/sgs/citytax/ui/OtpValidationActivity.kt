package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.Organization
import com.sgs.citytax.api.payload.StoreCustomerB2B
import com.sgs.citytax.api.response.TaxPayerResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityOtpValidationBinding
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.util.Constant
import kotlinx.android.synthetic.main.activity_otp_validation.*
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class OtpValidationActivity : BaseActivity() {
    private lateinit var binding: ActivityOtpValidationBinding
    private var timer: CountDownTimer? = null
    var smsOTP = ""
    var mailOTP = ""
    var organization: Organization? = null
    private var businessOwnerShip: ArrayList<BusinessOwnership>? = null
    private var estimatedTax: BigDecimal = BigDecimal.ZERO
    private var storeCustomerB2B: StoreCustomerB2B? = null
    private var mCode: Constant.QuickMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_validation)
        hideToolbar()
        processIntent()
        bindData()
        setListener()
        smsOTP = getRandomNumber().toString()
        mailOTP = getRandomNumber().toString()

        sendOtpToSMSandEmail()
    }

    private fun bindData() {
        if (validateEmail())
            binding.llMailOTP.visibility = VISIBLE
        else
            binding.llMailOTP.visibility = GONE

        if (validatePhone())
            binding.llSMSOTP.visibility = VISIBLE
        else
            binding.llSMSOTP.visibility = GONE
    }

    private fun processIntent() {
        intent.let {
            if (it.hasExtra(Constant.KEY_ORGANISATION))
                organization = it.getParcelableExtra(Constant.KEY_ORGANISATION)
            if (it.hasExtra(Constant.KEY_BUSINESS_OWNER))
                businessOwnerShip = it.getParcelableArrayListExtra(Constant.KEY_BUSINESS_OWNER)
            if (it.hasExtra(Constant.KEY_ESTIMATED_TAX))
                estimatedTax = it.getSerializableExtra(Constant.KEY_ESTIMATED_TAX) as BigDecimal
            if (it.hasExtra(Constant.KEY_STORE_CUSTOMER_B2B))
                storeCustomerB2B = it.getParcelableExtra(Constant.KEY_STORE_CUSTOMER_B2B)
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun sendOtpToSMSandEmail() {
        showProgressDialog(R.string.msg_please_wait)
        val mob = "${organization?.telCode ?: ""}${organization?.phone ?: ""}"
        val email: String = organization?.email ?: ""
        val accountid: Int = organization?.accountID ?: 0

        APICall.verifyBusinessContacts(mob, smsOTP, mailOTP, email,"SMS_BusinessOnBoarding",accountid, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                Log.e("response", "" + response)
                dismissDialog()
                countDown()
            }

            override fun onFailure(message: String) {
                dismissDialog()
            }
        })
    }

    private fun setListener() {

        binding.btnResend.setOnClickListener {
            binding.etsmsotp.setText("")
            binding.etemailotp.setText("")
            binding.etsmsotp.requestFocus()
            smsOTP = getRandomNumber().toString()
            mailOTP = getRandomNumber().toString()
            sendOtpToSMSandEmail()
            btnResend.visibility = GONE
        }

        binding.btnProceed.setOnClickListener {
            if (validateOTP()) {
                timer?.cancel()
                updatePHoneEmailVerification()
            }
        }
        binding.btnProceedWithout.setOnClickListener {
            if (validateWithout()) {
                timer?.cancel()
                navigateNextScreen()
            }
        }
    }

    private fun updatePHoneEmailVerification() {
        showProgressDialog()
        val inputSMSOTP = binding.etsmsotp.text?.toString()
        val inputMailOTP = binding.etemailotp.text?.toString()

        if (!inputSMSOTP.isNullOrEmpty())
            storeCustomerB2B?.organization?.phoneVerified = "Y"
        if (!inputMailOTP.isNullOrEmpty())
            storeCustomerB2B?.organization?.emailVerified = "Y"

        APICall.storeCustomerB2B(storeCustomerB2B, object : ConnectionCallBack<TaxPayerResponse> {
            override fun onSuccess(response: TaxPayerResponse) {
                dismissDialog()
                mCode?.let {
                    if (it == Constant.QuickMenu.QUICK_MENU_VERIFICATION_BUSINESS) {
                        val intent = Intent()
                        intent.putExtra(Constant.KEY_REFRESH_VERIFICATION, true)
                        setResult(RESULT_OK, intent)
                        finish()
                        return
                    }
                }
                navigateNextScreen(true)
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }

    private fun navigateNextScreen(isOTPVerified: Boolean? = false) {
        mCode?.let {
            if (it == Constant.QuickMenu.QUICK_MENU_VERIFICATION_BUSINESS) {
                val intent = Intent()
                intent.putExtra(Constant.KEY_REFRESH_VERIFICATION, false)
                setResult(RESULT_OK, intent)
                finish()
                return
            }
        }
        val intent = Intent(this, BusinessSummaryPreviewActivity::class.java)
        intent.putExtra(Constant.KEY_ORGANISATION, organization)
        intent.putParcelableArrayListExtra(Constant.KEY_BUSINESS_OWNER, businessOwnerShip)
        intent.putExtra(Constant.KEY_OTP_VALIDATION, isOTPVerified)
        intent.putExtra(Constant.KEY_ESTIMATED_TAX, estimatedTax)
        startActivity(intent)
        finish()
    }

    private fun validateWithout(): Boolean {

        if (binding.etDesc.text.toString().trim().isEmpty()) {
            showToast(R.string.msg_enter_remarks)
            binding.etDesc.requestFocus()
            return false
        }
        return true

    }

    private fun validateOTP(): Boolean {
        val inputSMSOTP = binding.etsmsotp.text?.toString()
        val inputMailOTP = binding.etemailotp.text?.toString()

        if (binding.llSMSOTP.visibility == VISIBLE && binding.llMailOTP.visibility == VISIBLE) {
            if (!inputSMSOTP.isNullOrEmpty() && !inputMailOTP.isNullOrEmpty()) {
                if (validatePhone() && (inputSMSOTP.length != 4 || inputSMSOTP != smsOTP)) {
                    showToast(R.string.msg_enter_correct_sms_otp)
                    return false
                } else if (validateEmail() && (inputMailOTP.length != 4 || inputMailOTP != mailOTP)) {
                    showToast(R.string.msg_enter_correct_mail_otp)
                    return false
                }
            } else if (!inputSMSOTP.isNullOrEmpty()) {
                if (validatePhone() && (inputSMSOTP.length != 4 || inputSMSOTP != smsOTP)) {
                    showToast(R.string.msg_enter_correct_sms_otp)
                    return false
                }
            } else if (!inputMailOTP.isNullOrEmpty()) {
                if (validateEmail() && (inputMailOTP.length != 4 || inputMailOTP != mailOTP)) {
                    showToast(R.string.msg_enter_correct_mail_otp)
                    return false
                }
            } else if (validatePhone() && (inputSMSOTP == null || inputSMSOTP.isEmpty())) {
                showToast(R.string.msg_enter_sms_or_email_otp)
                return false
            }
        } else if (binding.llSMSOTP.visibility == VISIBLE) {
            if (validatePhone() && (inputSMSOTP == null || inputSMSOTP.isEmpty())) {
                showToast(R.string.msg_enter_sms_otp)
                return false
            } else if (validatePhone() && !inputSMSOTP.isNullOrEmpty() && (inputSMSOTP.length != 4 || inputSMSOTP != smsOTP)) {
                showToast(R.string.msg_enter_correct_sms_otp)
                return false
            }
        } else if (binding.llMailOTP.visibility == VISIBLE) {
            if (!validatePhone() && validateEmail() && (inputMailOTP == null || inputMailOTP.isEmpty())) {
                showToast(R.string.msg_enter_mail_otp)
                return false
            } else if (validateEmail() && !inputMailOTP.isNullOrEmpty() && (inputMailOTP.length != 4 || inputMailOTP != mailOTP)) {
                showToast(R.string.msg_enter_correct_mail_otp)
                return false
            }
        }

        return true
    }


    private fun countDown() {
        timer = object : CountDownTimer(1000 * 60 * 15, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val hms = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                binding.tvTime.text = hms

            }

            override fun onFinish() {
                binding.tvTime.text = ""
                binding.btnResend.visibility = VISIBLE
                smsOTP = ""
                mailOTP = ""
                binding.etsmsotp.setText("")
                binding.etemailotp.setText("")
            }
        }
        timer?.start()

    }

    private fun getRandomNumber(): Any {
        val min = 1000
        val max = 9999
        return Random().nextInt(max - min + 1) + min
    }

    private fun validateEmail() = storeCustomerB2B?.organization?.email != null && !TextUtils.isEmpty(storeCustomerB2B?.organization?.email) && storeCustomerB2B?.organization?.emailVerified == "N"
    private fun validatePhone() = storeCustomerB2B?.organization?.phone != null && !TextUtils.isEmpty(storeCustomerB2B?.organization?.phone) && storeCustomerB2B?.organization?.phoneVerified == "N"

}
