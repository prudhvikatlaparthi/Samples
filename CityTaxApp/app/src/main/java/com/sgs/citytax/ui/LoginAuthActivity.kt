package com.sgs.citytax.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.APICall.getMessageConnectionString
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetMessageConnectionPayload
import com.sgs.citytax.api.payload.VerifyBusinessContacts
import com.sgs.citytax.api.response.GetMessageConnectionStringResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityLoginAuthBinding
import com.sgs.citytax.databinding.OtpValidationLayoutBinding
import com.sgs.citytax.util.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor


class LoginAuthActivity : BaseActivity() {


    lateinit var mBinding: ActivityLoginAuthBinding

    var userName: String? = null
    var verificationCode: String? = null
    private var codes: ArrayList<String> = arrayListOf()
    var uniqueId = ""
    private var secretKey = ""
    var smsPrefix = ""
    private var smsOTP = 0
    var googleSmsPrefix = ""
    private var googleSmsOTP = 0
    var OTP = ""
    val TAG = "OTPValidationFragment>>"
    var timer: CountDownTimer? = null
    var mblNumber = ""
    var isFirstTime: Boolean = false
    var isSMSVerificationDone: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.authenticator_text)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_auth)

        userName = intent.extras?.getString(Constant.USERNAME)

        mBinding.googleAuthCodeOpt.isChecked = true

        mblNumber =
            MyApplication.getPrefHelper().agentContryCode + "" + MyApplication.getPrefHelper().agentMobile

        setListeners()
        initForSMSAndGoogleOTPValidation(mBinding.smsOTPLayoutInclude)
        initForSMSAndGoogleOTPValidation(mBinding.smsOTPLayoutIncludeGoogle)


        if (prefHelper.authUniqueKey.isEmpty()) {
            isFirstTime = true
            setVisibility(View.VISIBLE)
            loadQRCode()
        } else {
            isFirstTime = false
            uniqueId = prefHelper.authUniqueKey
            setVisibility(View.GONE)
        }

    }

    private fun initForSMSAndGoogleOTPValidation(includeSMSOTPLayout: OtpValidationLayoutBinding) {
        if (includeSMSOTPLayout == mBinding.smsOTPLayoutInclude)
            generateOtp(true)
        else
            generateOtp()

        includeSMSOTPLayout.otpTextId.text = getString(
            R.string.otp_text,
            mblNumber.subSequence(mblNumber.length - 4, mblNumber.length)
        )

        mBinding.codeTextId.transformationMethod = AsteriskPasswordTransformationMethod()
        includeSMSOTPLayout.smsOTPFieldId.transformationMethod =
            AsteriskPasswordTransformationMethod()


        includeSMSOTPLayout.requestOtpBtnId.visibility = View.VISIBLE
        includeSMSOTPLayout.otpLayoutId.visibility = View.GONE
    }

    fun reset() {
        mBinding.smsOTPLayoutInclude.smsOTPFieldId.setText("")
        mBinding.smsOTPLayoutIncludeGoogle.smsOTPFieldId.setText("")
        mBinding.codeTextId.setText("")
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setListeners() {

        mBinding.smsOTPLayoutInclude.eyeId.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (mBinding.smsOTPLayoutInclude.eyeId.tag.toString() == "show") {
                    mBinding.smsOTPLayoutInclude.smsOTPFieldId.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    mBinding.smsOTPLayoutInclude.smsOTPFieldId.setSelection(mBinding.smsOTPLayoutInclude.smsOTPFieldId.length())
                    mBinding.smsOTPLayoutInclude.eyeId.setImageResource(R.drawable.eye_hide)
                    mBinding.smsOTPLayoutInclude.eyeId.tag = "hide"
                } else {
                    mBinding.smsOTPLayoutInclude.smsOTPFieldId.transformationMethod =
                        AsteriskPasswordTransformationMethod()
                    mBinding.smsOTPLayoutInclude.smsOTPFieldId.setSelection(mBinding.smsOTPLayoutInclude.smsOTPFieldId.length())
                    mBinding.smsOTPLayoutInclude.eyeId.setImageResource(R.drawable.eye_show)
                    mBinding.smsOTPLayoutInclude.eyeId.tag = "show"
                }
            }
        })

        mBinding.smsOTPLayoutIncludeGoogle.eyeId.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (mBinding.smsOTPLayoutIncludeGoogle.eyeId.tag.toString() == "show") {
                    mBinding.smsOTPLayoutIncludeGoogle.smsOTPFieldId.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    mBinding.smsOTPLayoutIncludeGoogle.smsOTPFieldId.setSelection(mBinding.smsOTPLayoutIncludeGoogle.smsOTPFieldId.length())
                    mBinding.smsOTPLayoutIncludeGoogle.eyeId.setImageResource(R.drawable.eye_hide)
                    mBinding.smsOTPLayoutIncludeGoogle.eyeId.tag = "hide"
                } else {
                    mBinding.smsOTPLayoutIncludeGoogle.smsOTPFieldId.transformationMethod =
                        AsteriskPasswordTransformationMethod()
                    mBinding.smsOTPLayoutIncludeGoogle.smsOTPFieldId.setSelection(mBinding.smsOTPLayoutIncludeGoogle.smsOTPFieldId.length())
                    mBinding.smsOTPLayoutIncludeGoogle.eyeId.setImageResource(R.drawable.eye_show)
                    mBinding.smsOTPLayoutIncludeGoogle.eyeId.tag = "show"
                }
            }
        })

        mBinding.eyeId.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (mBinding.eyeId.tag.toString() == "show") {
                    mBinding.codeTextId.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    mBinding.codeTextId.setSelection(mBinding.codeTextId.length())
                    mBinding.eyeId.setImageResource(R.drawable.eye_hide)
                    mBinding.eyeId.tag = "hide"
                } else {
                    mBinding.codeTextId.transformationMethod = AsteriskPasswordTransformationMethod()
                    mBinding.codeTextId.setSelection(mBinding.codeTextId.length())
                    mBinding.eyeId.setImageResource(R.drawable.eye_show)
                    mBinding.eyeId.tag = "show"
                }
            }
        })

        mBinding.radioGrp.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(radioGroup: RadioGroup?, selectedId: Int) {
                reset()
                when (selectedId) {
                    R.id.googleAuthCodeOpt -> {
                        mBinding.smsOTPLayoutInclude.smsOTPLayout.visibility = View.GONE
                        if (isFirstTime) {
                            setVisibility(View.VISIBLE)
                        } else {
                            setVisibility(View.GONE)
                        }
                    }
                    R.id.smsOTPOpt -> {
                        mBinding.googleAuthLayout.visibility = View.GONE
                        mBinding.smsOTPLayoutIncludeGoogle.smsOTPLayout.visibility = View.GONE
                        /*if(mBinding.smsOTPLayoutInclude.resendTextId.text.toString()=="Resend SMS OTP") {
                            generateOtp()
                        }*/
                        mBinding.smsOTPLayoutInclude.smsOTPLayout.visibility = View.VISIBLE
                        if (mBinding.smsOTPLayoutInclude.requestOtpBtnId.visibility == View.VISIBLE) {
                            mBinding.loginBtnId.visibility = View.GONE
                        }
                    }
                }
            }

        })

        mBinding.loginBtnId.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                when (mBinding.radioGrp.checkedRadioButtonId) {
                    R.id.googleAuthCodeOpt -> if (isFirstTime && !isSMSVerificationDone) {
                        doSMSLogin()
                    } else {
                        doLogin()
                    }
                    R.id.smsOTPOpt -> doSMSLogin()
                }
            }
        })

        mBinding.copyBtnId.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipBoardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        Constant.KEY_LABEL,
                        mBinding.secretKeyTextId.text
                    )
                )
            }
        })

        mBinding.smsOTPLayoutInclude.requestOtpBtnId.setOnClickListener(object :
            OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mBinding.loginBtnId.visibility = View.VISIBLE
                sendSmsOTP(mblNumber)
            }
        })

        mBinding.smsOTPLayoutIncludeGoogle.requestOtpBtnId.setOnClickListener(object :
            OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mBinding.loginBtnId.visibility = View.VISIBLE
                sendSmsOTP(mblNumber)
            }
        })

        mBinding.smsOTPLayoutInclude.resendTextId.setOnClickListener(object :
            OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                generateOtp()
                mBinding.smsOTPLayoutInclude.smsOTPFieldId.setText("")
                mBinding.smsOTPLayoutInclude.resendTextId.isEnabled = false
                mBinding.smsOTPLayoutInclude.resendTextId.setTextColor(ContextCompat.getColor(this@LoginAuthActivity,android.R.color.darker_gray))
                sendSmsOTP(mblNumber)
            }
        })

        mBinding.smsOTPLayoutIncludeGoogle.resendTextId.setOnClickListener(object :
            OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                generateOtp()
                mBinding.smsOTPLayoutIncludeGoogle.smsOTPFieldId.setText("")
                mBinding.smsOTPLayoutIncludeGoogle.resendTextId.isEnabled = false
                mBinding.smsOTPLayoutIncludeGoogle.resendTextId.setTextColor(ContextCompat.getColor(this@LoginAuthActivity,android.R.color.darker_gray))
                sendSmsOTP(mblNumber)
            }
        })

    }

    private fun generateOtp(smsOtp: Boolean = false) {
        if (smsOtp) {
            generateSmsOtp()
            return
        }
        when (mBinding.radioGrp.checkedRadioButtonId) {
            R.id.googleAuthCodeOpt -> {
                generategoogleSmsOtp()
            }
            R.id.smsOTPOpt -> {
                generateSmsOtp()
            }
        }
    }

    private fun generategoogleSmsOtp() {
        googleSmsPrefix = getRandomAlphabet()
        googleSmsOTP = getRandomNumber()

        mBinding.smsOTPLayoutIncludeGoogle.prefixTVId.text =
            "$googleSmsPrefix -"
    }

    private fun generateSmsOtp() {
        smsPrefix = getRandomAlphabet()
        smsOTP = getRandomNumber()
        OTP = "$smsPrefix - $smsOTP"

        mBinding.smsOTPLayoutInclude.prefixTVId.text = "$smsPrefix -"
    }

    private fun doSMSLogin() {
       if (checkIfAuthGoogleForFirstTime()) {
            val otp=mBinding.smsOTPLayoutIncludeGoogle.smsOTPFieldId.text.toString()
            if (otp.isEmpty()) {
                showDialog(getString(R.string.enter_otp_text))
            } else {
                if (otp == googleSmsOTP.toString()) {
                    timer?.cancel()
                    isSMSVerificationDone = true
                    setVisibility(View.VISIBLE)
                } else {
                    showDialog(getString(R.string.incorrect_otp_text))
                }
            }
        } else {
            val otp = mBinding.smsOTPLayoutInclude.smsOTPFieldId.text.toString()
            if (otp.isEmpty()) {
                showDialog(getString(R.string.enter_otp_text))
            } else {
                if (otp == smsOTP.toString()) {
                    timer?.cancel()
                    navigateDashBoardActivity()
                } else {
                    showDialog(getString(R.string.incorrect_otp_text))
                }
            }
        }
//        navigateDashBoardActivity()
    }


    private fun sendSmsOTP(mob: String) {
        showProgressDialog(getString(R.string.msg_please_wait))
        when (mBinding.radioGrp.checkedRadioButtonId) {
            R.id.googleAuthCodeOpt -> {
                OTP = "$googleSmsPrefix-$googleSmsOTP"
            }
            R.id.smsOTPOpt -> {
                OTP = "$smsPrefix-$smsOTP"
            }
        }

        APICall.verifyBusinessContacts(
            mob,
            OTP,
            "",
            "",
            "LOGIN_OTP",
            MyApplication.getPrefHelper().accountId,
            object :
                ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    dismissDialog()
                    if (response) {
                        if (checkIfAuthGoogleForFirstTime()) {
                            mBinding.smsOTPLayoutIncludeGoogle.requestOtpBtnId.visibility =
                                View.GONE
                            mBinding.smsOTPLayoutIncludeGoogle.otpLayoutId.visibility = View.VISIBLE
                            countDown()
                        } else {
                            mBinding.smsOTPLayoutInclude.requestOtpBtnId.visibility = View.GONE
                            mBinding.smsOTPLayoutInclude.otpLayoutId.visibility = View.VISIBLE
                            countDown()
                        }
                    }
                }

                override fun onFailure(message: String) {
                    dismissDialog()
                    showDialog(message)
                }
            })
    }

    private fun countDown() {
        if (checkIfAuthGoogleForFirstTime()) {
            mBinding.smsOTPLayoutIncludeGoogle.resendTextId.isEnabled = false
            mBinding.smsOTPLayoutIncludeGoogle.resendTextId.setTextColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.darker_gray
                )
            )
            mBinding.smsOTPLayoutIncludeGoogle.resendTextId.paintFlags = mBinding.smsOTPLayoutIncludeGoogle.resendTextId.paintFlags and Paint.ANTI_ALIAS_FLAG

            timer = object : CountDownTimer(1000 * 180, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    val hms = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                        )
                    ) + Constant.KEY_SEC
                    mBinding.smsOTPLayoutIncludeGoogle.resendTextId.text =
                        getString(R.string.sms_resend_text, hms)

                }

                override fun onFinish() {
                    googleSmsPrefix = ""
                    googleSmsOTP = 0

                    mBinding.smsOTPLayoutIncludeGoogle.resendTextId.paintFlags = mBinding.smsOTPLayoutIncludeGoogle.resendTextId.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                    mBinding.smsOTPLayoutIncludeGoogle.resendTextId.text =
                        getString(R.string.sms_resend_text_finish)
                    mBinding.smsOTPLayoutIncludeGoogle.resendTextId.setTextColor(
                        ContextCompat.getColor(
                            mBinding.smsOTPLayoutIncludeGoogle.resendTextId.context,
                            R.color.hyperlink_blue
                        )
                    )
                    mBinding.smsOTPLayoutIncludeGoogle.resendTextId.isEnabled = true
                }
            }

            timer?.start()
        } else {
            mBinding.smsOTPLayoutInclude.resendTextId.isEnabled = false
            mBinding.smsOTPLayoutInclude.resendTextId.setTextColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.darker_gray
                )
            )

            mBinding.smsOTPLayoutInclude.resendTextId.paintFlags = mBinding.smsOTPLayoutInclude.resendTextId.paintFlags and Paint.ANTI_ALIAS_FLAG

            timer = object : CountDownTimer(1000 * 180, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    val hms = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                        )
                    ) + Constant.KEY_SEC
                    mBinding.smsOTPLayoutInclude.resendTextId.text =
                        getString(R.string.sms_resend_text, hms)

                }

                override fun onFinish() {
                    smsPrefix = ""
                    smsOTP = 0

                    mBinding.smsOTPLayoutInclude.resendTextId.paintFlags = mBinding.smsOTPLayoutInclude.resendTextId.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                    mBinding.smsOTPLayoutInclude.resendTextId.text =
                        getString(R.string.sms_resend_text_finish)
                    mBinding.smsOTPLayoutInclude.resendTextId.setTextColor(
                        ContextCompat.getColor(
                            mBinding.smsOTPLayoutInclude.resendTextId.context,
                            R.color.hyperlink_blue
                        )
                    )
                    mBinding.smsOTPLayoutInclude.resendTextId.isEnabled = true
                }
            }

            timer?.start()
        }


    }


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

    private fun setVisibility(visibility: Int) {
        mBinding.copyBtnId.visibility = View.GONE
        if (checkIfAuthGoogleForFirstTime() && !isSMSVerificationDone) {
            mBinding.smsOTPLayoutIncludeGoogle.smsOTPLayout.visibility = View.VISIBLE
            mBinding.googleAuthLayout.visibility = View.GONE
            if (mBinding.smsOTPLayoutIncludeGoogle.requestOtpBtnId.visibility == View.VISIBLE) {
                mBinding.loginBtnId.visibility = View.GONE
            }else{
                mBinding.loginBtnId.visibility = View.VISIBLE
            }
        } else {
            mBinding.smsOTPLayoutIncludeGoogle.smsOTPLayout.visibility = View.GONE
            mBinding.loginBtnId.visibility = View.VISIBLE
            mBinding.googleAuthLayout.visibility = View.VISIBLE
            mBinding.QRimageViewId.visibility = visibility
            mBinding.googleTextMsg.visibility = visibility
            mBinding.secretKeyTextViewId.visibility = visibility
            mBinding.secretKeyTextId.visibility = visibility
        }
    }

    private fun checkIfAuthGoogleForFirstTime(): Boolean =
        isFirstTime && mBinding.radioGrp.checkedRadioButtonId == R.id.googleAuthCodeOpt

    private fun doLogin() {
        //Must Do Login
        val userEnteredCode = mBinding.codeTextId.text.toString()
        if (userEnteredCode.isEmpty()) {
            showDialog(getString(R.string.enter_verification_code_text))
        } else {
            if (uniqueId.isNotEmpty()) {
                codes = getCodes(uniqueId)
                if (codes.contains(userEnteredCode)) {
                    showProgressDialog(getString(R.string.msg_please_wait))
                    if (prefHelper.authUniqueKey.isEmpty()) {
                        APICall.updateAuth2FA(
                            secretKey,
                            uniqueId,
                            object : ConnectionCallBack<Boolean> {
                                override fun onSuccess(response: Boolean) {
                                    dismissDialog()
                                    if (response) {
                                        timer?.cancel()
                                        navigateDashBoardActivity()
                                    } else {
                                        showAlertDialog(getString(R.string.failed_message))
                                    }
                                }

                                override fun onFailure(message: String) {
                                    dismissDialog()
                                    showAlertDialog(message)
                                }

                            })
                    } else {
                        navigateDashBoardActivity()
                    }
                } else {
                    dismissDialog()
                    showDialog(getString(R.string.incorrect_verification_code_text))
                }
            }
        }

    }

    private fun navigateDashBoardActivity() {
        // To retrieve host, port and password credentials for, jedis connection
        getMessageConnectionString(
            GetMessageConnectionPayload(),
            object : ConnectionCallBack<GetMessageConnectionStringResponse> {
                override fun onSuccess(response: GetMessageConnectionStringResponse) {

//                    val connection = response.connection?.split(":")
                    val connection = response.publicConnection?.split(":") // connection replaced at backend, to publicConnection

                    if (connection?.size ?: 0 > 1) {
                        MyApplication.getPrefHelper().jedisConnectionHost = connection?.get(0) ?: ""
                        MyApplication.getPrefHelper().jedisConnectionPort =
                            connection?.get(1)?.toInt() ?: 0
                    }
                    MyApplication.getPrefHelper().jedisConnectionPassword = response.password ?: ""

                    JedisUtil.startJedis()
                }

                override fun onFailure(message: String) {
                }
            })

        val intent = Intent(this@LoginAuthActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showDialog(msg: String) {
        val mdialog = AlertDialog.Builder(this@LoginAuthActivity)
        mdialog.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }.setTitle(R.string.alert_dialog_title)
            .setMessage(msg)
            .create()
        mdialog.show()
    }

    private fun loadQRCode() {
        uniqueId = ""
        uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 10)
        val bytes = uniqueId.toByteArray(Charsets.UTF_8)
        secretKey = Base32.byteArrayToBase32(bytes)
        //Setting Up the secret Key
        mBinding.secretKeyTextId.text = secretKey
        val provisionUrl =
            (String.format("otpauth://totp/${userName}?secret=${secretKey.trim('=')}"))
        //creating QR Code and setting it up
        val qrCode: Bitmap = createQRCode(provisionUrl)
        Glide.with(this@LoginAuthActivity).load(qrCode).into(mBinding.QRimageViewId)
    }

    private fun getCodes(uniqueId: String): ArrayList<String> {
        //Getting the codes
        val codes: ArrayList<String> = arrayListOf()
        val epoch = System.currentTimeMillis() / 1000
        val counter = floor(epoch / 30.0).toLong()
        codes.add(Base32.GenerateHashedCode(uniqueId, counter - 1, 6))
        codes.add(Base32.GenerateHashedCode(uniqueId, counter, 6))
        codes.add(Base32.GenerateHashedCode(uniqueId, counter + 1, 6))
        return codes
    }

    private fun createQRCode(str: String): Bitmap {
        val mHashtable = Hashtable<EncodeHintType, String?>()
        mHashtable[EncodeHintType.CHARACTER_SET] = "UTF-8"
        mHashtable[EncodeHintType.MARGIN] = "0"
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