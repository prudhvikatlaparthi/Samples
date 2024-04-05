package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputLayout
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.ChequeDetails
import com.sgs.citytax.api.payload.MobiCashTransaction
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.MobiCashPayment
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPaymentEditBinding
import com.sgs.citytax.model.COMCountryMaster
import com.sgs.citytax.model.Payment
import com.sgs.citytax.model.PaymentBreakup
import com.sgs.citytax.ui.custom.CustomKeyBoard
import com.sgs.citytax.util.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class PaymentEditFragment : BaseFragment(), View.OnClickListener {

    private var customKeyboard: CustomKeyBoard? = null
    private lateinit var binding: FragmentPaymentEditBinding
    private lateinit var mCode: Constant.QuickMenu
    private lateinit var listener: Listener
    private lateinit var mContext: Context
    private lateinit var helper: LocationHelper
    private lateinit var payment: Payment

    private var mImageFilePath = ""
    private var extension: String? = null
    private var base64Data: String? = null

    private var textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            var paidAmount = BigDecimal.ZERO
            if (payment.paymentMode == Constant.PaymentMode.CHEQUE) {
                if (!TextUtils.isEmpty(binding.edtChequeAmount.text.toString()))
                    paidAmount = currencyToDouble(binding.edtChequeAmount.text.toString().trim())?.toDouble()?.toBigDecimal() ?: BigDecimal.ZERO
            } else {
                if (!TextUtils.isEmpty(binding.etAmount.text.toString()))
                    paidAmount = binding.etAmount.text.toString().trim().toBigDecimal()
            }
            val balanceAmount: BigDecimal = payment.amountDue.subtract(paidAmount)
            binding.txtBalanceAmount.text = formatWithPrecision(balanceAmount)
            if (mCode != Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE && paidAmount > payment.amountDue)
                listener.showSnackbarMsg(getString(R.string.msg_payment_cannot_be_more))
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun initComponents() {
        initPayment()
        initViews()
        initEvents()
        bindData()
        bindSpinner()
    }

    companion object {
        @JvmStatic
        fun newInstance(code: Constant.QuickMenu) = PaymentEditFragment().apply {
            mCode = code
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        listener = try {
            context as Listener
        } catch (e: Exception) {
            throw ClassCastException(context.toString() + "must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_edit, container, false)
        initComponents()
        return binding.root
    }

    private fun bindData() {

        if(mCode == Constant.QuickMenu.QUICK_MENU_SALES_TAX){
            binding.clAmountLayout.visibility = GONE
        }

        if (payment.paymentMode == Constant.PaymentMode.CHEQUE) {
            if (payment.paymentType == Constant.PaymentType.SALES_TAX) {
                binding.edtChequeAmount.setText(formatWithPrecision(payment.minimumPayAmount))
                binding.edtChequeAmount.isEnabled = false
                binding.edtChequeAmount.isFocusable = false
                binding.edtChequeAmount.isFocusableInTouchMode = false
                binding.txtLytChequeAmount.endIconMode = TextInputLayout.END_ICON_NONE
            } else {
                binding.edtChequeAmount.inputType = InputType.TYPE_CLASS_NUMBER
                binding.edtChequeAmount.setText(
                    payment.minimumPayAmount.stripTrailingZeros().toPlainString()
                )
            }
        } else {
            binding.etAmount.setText(formatWithPrecision(payment.amountDue))
        }

        if (mCode == Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE)
            binding.etAmount.setText(formatWithPrecision(0.0))
        else {
            binding.txtDue.text = formatWithPrecision(payment.amountDue)
            binding.txtBalanceAmount.text = formatWithPrecision(0.0)
            binding.txtMinimumAmount.text = formatWithPrecision(payment.minimumPayAmount)
            binding.etAmount.setText(payment.minimumPayAmount.toPlainString())
            if (mCode == Constant.QuickMenu.QUICK_MENU_SALES_TAX) {
                binding.etAmount.isEnabled = false
                binding.etAmount.isFocusable = false
                binding.etAmount.isFocusableInTouchMode = false
                binding.txtInpLayAmount.endIconMode = TextInputLayout.END_ICON_NONE


            }
        }
        if (payment.paymentMode == Constant.PaymentMode.CHEQUE) {
            if (mCode == Constant.QuickMenu.QUICK_MENU_SALES_TAX) {
                binding.edtPercentage.setText(
                    payment.penaltyPercentage?.stripTrailingZeros()?.toPlainString() ?: "0"
                )
                binding.edtProsecutionFees.setText(formatWithPrecision(payment.prosecutionFees?.stripTrailingZeros()?.toPlainString() ?: "0"))
            }
            bindBankNameSpinner()
        }
    }

    private fun bindBankNameSpinner() {
        listener.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("COM_BankMaster", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                listener.dismissDialog()
                if (response.comBankMasters.isNotEmpty()) {
                    val bankAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, response.comBankMasters)
                    binding.spnBankName.adapter = bankAdapter
                }
            }

            override fun onFailure(message: String) {
                listener.dismissDialog()
                listener.showAlertDialog(message)
            }
        })

    }

    private fun bindSpinner() {
        listener.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("COM_CountryMaster", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                if (response.countryMaster.isNotEmpty()) {
                    val countryCode: String? = "BFA"
                    val countries: MutableList<COMCountryMaster> = arrayListOf()
                    var index = -1
                    val telephonicCodes: ArrayList<Int> = arrayListOf()
                    for (country in response.countryMaster) {
                        country.telephoneCode?.let {
                            if (it > 0) {
                                countries.add(country)
                                telephonicCodes.add(it)
                                if (index <= -1 && countryCode == country.countryCode)
                                    index = countries.indexOf(country)
                            }
                        }
                    }
                    if (index <= -1) index = 0
                    if (telephonicCodes.size > 0) {
                        val telephonicCodeArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, telephonicCodes)
                        binding.spnTelephoneCode.adapter = telephonicCodeArrayAdapter
                        binding.spnTelephoneCode.setSelection(index)
                    } else binding.spnTelephoneCode.adapter = null
                }

                bindData()
                listener.dismissDialog()
            }

            override fun onFailure(message: String) {
                binding.spnTelephoneCode.adapter = null
                listener.dismissDialog()
                listener.showAlertDialog(message)
            }
        })
    }

    private fun initViews() {
        binding.btnApply.isEnabled = true
        when {
            mCode == Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE -> {
                if (payment.paymentMode == Constant.PaymentMode.MOBICASH)
                    binding.txtInpLayOTP.visibility = GONE
                else
                    binding.txtInpLayOTP.visibility = VISIBLE
                binding.txtInpLayMobileNumber.visibility = VISIBLE
                binding.spnTelephoneCode.visibility = VISIBLE
                binding.crdPaymentInfo.visibility = GONE
                binding.txtInpLytChequeNo.visibility = GONE
                binding.llChequeBankName.visibility = GONE
                binding.rlDocs.visibility = GONE
                binding.crdAmountInfo.visibility = VISIBLE
                binding.chequeAmountInfo.visibility = GONE

            }
            payment.paymentMode == Constant.PaymentMode.WALLET -> {
                binding.txtInpLayMobileNumber.visibility = VISIBLE
                binding.spnTelephoneCode.visibility = VISIBLE
                binding.txtInpLayOTP.visibility = VISIBLE
                binding.crdPaymentInfo.visibility = VISIBLE
                binding.txtInpLytChequeNo.visibility = GONE
                binding.llChequeBankName.visibility = GONE
                binding.rlDocs.visibility = GONE
                binding.crdAmountInfo.visibility = VISIBLE
                binding.chequeAmountInfo.visibility = GONE
            }
            payment.paymentMode == Constant.PaymentMode.MOBICASH -> {
                binding.txtInpLayMobileNumber.visibility = VISIBLE
                binding.spnTelephoneCode.visibility = VISIBLE
                binding.txtInpLayOTP.visibility = GONE
                binding.crdPaymentInfo.visibility = VISIBLE
                binding.txtInpLytChequeNo.visibility = GONE
                binding.llChequeBankName.visibility = GONE
                binding.rlDocs.visibility = GONE
                binding.crdAmountInfo.visibility = VISIBLE
                binding.chequeAmountInfo.visibility = GONE
            }
            payment.paymentMode == Constant.PaymentMode.CHEQUE -> {
                binding.txtInpLayMobileNumber.visibility = GONE
                binding.spnTelephoneCode.visibility = GONE
                binding.txtInpLytChequeNo.visibility = VISIBLE
                binding.llChequeBankName.visibility = VISIBLE
                binding.rlDocs.visibility = VISIBLE
                binding.crdPaymentInfo.visibility = VISIBLE
                binding.txtInpLayOTP.visibility = GONE
                binding.crdAmountInfo.visibility = GONE
                binding.chequeAmountInfo.visibility = VISIBLE
                binding.llPenalty.isVisible = mCode == Constant.QuickMenu.QUICK_MENU_SALES_TAX
            }

            else -> {
                binding.txtInpLayMobileNumber.visibility = GONE
                binding.spnTelephoneCode.visibility = GONE
                binding.txtInpLayOTP.visibility = GONE
                binding.crdPaymentInfo.visibility = VISIBLE
                binding.txtInpLytChequeNo.visibility = GONE
                binding.llChequeBankName.visibility = GONE
                binding.rlDocs.visibility = GONE
            }
        }
        customKeyboard = CustomKeyBoard(activity, binding.keyPad, R.layout.keyboard, null)
        customKeyboard?.registerEditText(binding.edtChequeNo)
        customKeyboard?.registerEditText(binding.etAmount)
        customKeyboard?.registerEditText(binding.edtChequeAmount)
        customKeyboard?.registerEditText(binding.etMobileNumber)
        customKeyboard?.registerEditText(binding.etOTP)
        customKeyboard?.registerEditText(binding.edtProsecutionFees)
        customKeyboard?.registerEditText(binding.edtPercentage,true)

        binding.edtChequeDate.setDisplayDateFormat(displayDateFormat)
        // As per QA SR commented this on 15 Mar
        // binding.edtChequeDate.setMaxDate(System.currentTimeMillis())
    }

    private fun initEvents() {
        binding.etAmount.addTextChangedListener(textWatcher)
        binding.edtChequeAmount.addTextChangedListener(textWatcher)
        binding.edtPercentage.addTextChangedListener {
            val percent = it?.toString()?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val value = payment.amountDue.multiply(percent).divide(BigDecimal(100))
            binding.txtPenaltyAmount.text = formatWithPrecision(value)
        }
        binding.btnCancel.setOnClickListener(this)
        binding.btnApply.setOnClickListener(this)
        binding.imgCheque.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)
        /*binding.edtChequeAmount.filters = arrayOf(InputFilter label@{ source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int ->
            if (source == "") return@label null
            val pattern = Pattern.compile("^[0-9]+([.][0-9]{0,2})?$")
            val builder = StringBuilder(dest!!)
            builder.replace(dstart, dend, source.subSequence(start, end).toString())
            val matcher = pattern.matcher(builder)
            if (!matcher.matches()) return@label ""
            null
        })*/
        binding.etAmount.filters = arrayOf(InputFilter label@{ source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int ->
            if (source == "") return@label null
            val pattern = Pattern.compile("^[0-9]+([.][0-9]{0,2})?$")
            val builder = StringBuilder(dest!!)
            builder.replace(dstart, dend, source.subSequence(start, end).toString())
            val matcher = pattern.matcher(builder)
            if (!matcher.matches()) return@label ""
            null
        })

        binding.btnApply.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (binding.llPenalty.isVisible) {
                    binding.edtProsecutionFees.clearFocus()
                }
               paymentProceed()
            }
        })

        binding.edtProsecutionFees.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val text: String = binding.edtProsecutionFees.text.toString()
                    if (text.isNotEmpty())
                        binding.edtProsecutionFees.setText("${currencyToDouble(text)}");
                    binding.edtProsecutionFees.setSelection(
                        binding.edtProsecutionFees.text?.length ?: 0
                    )
                    customKeyboard?.showCustomKeyboard(binding.edtProsecutionFees)
                } else {
                    //this if condition is true when edittext lost focus...
                    //check here for number is larger than 10 or not
                    if (!TextUtils.isEmpty(binding.edtProsecutionFees.text.toString())) {
                        val enteredText: Double =
                            binding.edtProsecutionFees.text.toString().toDouble()
                        binding.edtProsecutionFees.setText("${formatWithPrecision(enteredText)}")
                        binding.edtProsecutionFees.setSelection(
                            binding.edtProsecutionFees.text?.length ?: 0
                        )
                    }
                }
            }
    }

    private fun initPayment() {
        payment = MyApplication.getPayment()
        payment.minimumPayAmount =
            BigDecimal(payment.minimumPayAmount.toDouble()).setScale(2, RoundingMode.DOWN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_IMAGE_CAPTURE) {
            binding.imgCheque.setImageURI(Uri.parse(mImageFilePath))
            binding.btnDelete.visibility = VISIBLE
            base64Data = ImageHelper.getBase64String(ImageHelper.decodeFile(File(mImageFilePath)))
            extension = "${formatDateTimeSecondFormat(Date())}.jpg"
            payment.filenameWithExt = extension
            payment.fileData = base64Data
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCancel -> {
                listener.popBackStack()
            }
           /* R.id.btnApply -> {
                //savePayment()
                paymentProceed()
            }*/
            R.id.imgCheque -> {
                if (hasPermission(requireContext(), Manifest.permission.CAMERA)) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (intent.resolveActivity(requireContext().packageManager) != null) {
                        val photoFile: File?
                        try {
                            photoFile = createImageFile()
                        } catch (e: IOException) {
                            LogHelper.writeLog(exception = e)
                            return
                        }
                        val photoUri: Uri? = photoFile.let { FileProvider.getUriForFile(requireContext(), context?.packageName.toString() + ".provider", it) }
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        startActivityForResult(intent, Constant.REQUEST_IMAGE_CAPTURE)
                    }
                } else {
                    requestForPermission(Manifest.permission.CAMERA, Constant.REQUEST_CODE_CAMERA)
                }
            }

            R.id.btnDelete -> {
                binding.btnDelete.visibility = GONE
                binding.imgCheque.setImageResource(R.drawable.ic_place_holder)
                base64Data = null
                extension = null
                payment.filenameWithExt = ""
                payment.fileData = ""
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mImageFilePath = image.absolutePath
        return image
    }

    private fun savePayment(): Boolean {
        //binding.btnApply.isEnabled = false
        val paymentBreakupList: ArrayList<PaymentBreakup> = arrayListOf()
        if (payment.paymentMode == Constant.PaymentMode.CHEQUE) {
            if (binding.edtChequeAmount.text != null && TextUtils.isEmpty(binding.edtChequeAmount.text)) {
                listener.showSnackbarMsg(getString(R.string.msg_enter_amount))
                binding.btnApply.isEnabled = true
                return false
            }
            if (binding.llPenalty.isVisible) {
                if (TextUtils.isEmpty(binding.edtPercentage.text) || binding.edtPercentage.text.toString() == ".") {
                    listener.showSnackbarMsg(getString(R.string.msg_percentage_empty))
                    binding.btnApply.isEnabled = true
                    return false
                }
                val percent = binding.edtPercentage.text?.toString()?.toBigDecimalOrNull() ?: BigDecimal.ZERO
                if (percent > BigDecimal(100)) {
                    listener.showSnackbarMsg(getString(R.string.percent_value_cannt_greater_100))
                    binding.btnApply.isEnabled = true
                    return false
                }
                val prosFee = currencyToDouble(binding.edtProsecutionFees.text.toString().trim())?.toDouble()?.toBigDecimal() ?: BigDecimal.ZERO
                if (prosFee <= BigDecimal.ZERO) {
                    listener.showSnackbarMsg(getString(R.string.pls_msg_enter_prosecution_Fees))
                    binding.btnApply.isEnabled = true
                    return false
                }
            }

            val amountChequePaid = currencyToDouble(binding.edtChequeAmount.text.toString().trim())?.toDouble()?.toBigDecimal() ?: BigDecimal.ZERO

            if (amountChequePaid == BigDecimal.ZERO) {
                var doubleVal = getDecimalVal(resources.getString(R.string.payment_cannot_be_done))
                listener.showSnackbarMsg(getTextWithPrecisionVal(resources.getString(R.string.payment_cannot_be_done), doubleVal))
                binding.btnApply.isEnabled = true
                return false
            }

            if (amountChequePaid < payment.minimumPayAmount) {
                listener.showSnackbarMsg(getString(R.string.msg_minimum_pay_amount))
                binding.btnApply.isEnabled = true
                return false
            }

            if (TextUtils.isEmpty(binding.edtChequeNo.text)) {
                listener.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.cheque_number))
                binding.btnApply.isEnabled = true
                return false
            }

            if (TextUtils.isEmpty(binding.edtChequeDate.text)) {
                listener.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.cheque_date))
                binding.btnApply.isEnabled = true
                return false
            }

            if (binding.spnBankName.selectedItem == null) {
                listener.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.bank_name))
                binding.btnApply.isEnabled = true
                return false
            }

            if (mImageFilePath.isEmpty()) {
                listener.showSnackbarMsg(getString(R.string.msg_take_picture_to_upload))
                binding.btnApply.isEnabled = true
                return false
            } else if (payment.fileData.isNullOrEmpty()) {
                listener.showSnackbarMsg(getString(R.string.msg_take_picture_to_upload))
                binding.btnApply.isEnabled = true
                return false
            }

            if (mCode == Constant.QuickMenu.QUICK_MENU_SALES_TAX && payment.amountDue > amountChequePaid) {
                listener.showSnackbarMsg((getString(R.string.msg_partial_payment_not_allowed)))
                binding.btnApply.isEnabled = true
                return false
            }

            if (amountChequePaid > payment.amountDue) {
                listener.showSnackbarMsg(getString(R.string.msg_payment_cannot_be_more))
                binding.btnApply.isEnabled = true
                return false
            }

            val breakup = PaymentBreakup(payment.paymentMode.name, amountChequePaid)
            paymentBreakupList.add(breakup)
            payment.amountPaid = amountChequePaid
            val chequeDetails = ChequeDetails()
            chequeDetails.amount = amountChequePaid
            chequeDetails.bankName = binding.spnBankName.selectedItem.toString()
            //  chequeDetails.chequeDate = formatDisplayDateTimeInMillisecond(parseDate(binding.edtChequeDate.text.toString(), displayDateFormat))
            chequeDetails.chequeDate = getDate(binding.edtChequeDate.text.toString(), displayDateFormat, DateTimeTimeSecondFormat)
            chequeDetails.chequeNo = binding.edtChequeNo.text.toString()
            chequeDetails.statusCode = Constant.PaymentStatus.NEW.value
            if (mCode == Constant.QuickMenu.QUICK_MENU_SALES_TAX) {
                chequeDetails.penaltyPercentage =
                    binding.edtPercentage.text.toString().trim().toBigDecimalOrNull()
                chequeDetails.penaltyAmount =
                    currencyToDouble(binding.txtPenaltyAmount.text.toString().trim())?.toDouble()?.toBigDecimal()
                chequeDetails.prosecutionFees = currencyToDouble(binding.edtProsecutionFees.text.toString().trim())?.toDouble()?.toBigDecimal()
            }
            payment.chequeDetails = chequeDetails
        } else {
            if (binding.etAmount.text != null && TextUtils.isEmpty(binding.etAmount.text)) {
                listener.showSnackbarMsg(getString(R.string.msg_enter_amount))
                binding.btnApply.isEnabled = true
                return false
            }

            val amountPaid = binding.etAmount.text.toString().trim().toBigDecimal()

            if (amountPaid == BigDecimal.ZERO) {
                var doubleVal = getDecimalVal(resources.getString(R.string.payment_cannot_be_done))
                listener.showSnackbarMsg(getTextWithPrecisionVal(resources.getString(R.string.payment_cannot_be_done), doubleVal))
                binding.btnApply.isEnabled = true
                return false
            }

            if (amountPaid < payment.minimumPayAmount) {
                listener.showSnackbarMsg(getString(R.string.msg_minimum_pay_amount))
                binding.btnApply.isEnabled = true
                return false
            }

            if (mCode != Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE && payment.amountDue < amountPaid) {
                listener.showAlertDialog(getString(R.string.msg_payment_cannot_be_more))
                binding.btnApply.isEnabled = true
                return false
            }

            if ((mCode == Constant.QuickMenu.QUICK_MENU_SALES_TAX || mCode == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEW || mCode == Constant.QuickMenu.QUICK_MENU_CREATE_ASSET_BOOKING || mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING) && payment.amountDue > amountPaid) {
                listener.showAlertDialog((getString(R.string.msg_partial_payment_not_allowed)))
                binding.btnApply.isEnabled = true
                return false
            }

            if ((payment.paymentMode == Constant.PaymentMode.WALLET || payment.paymentMode == Constant.PaymentMode.MOBICASH) && binding.etMobileNumber.text != null && binding.spnTelephoneCode.selectedItem != null) {
                val mobileNumber = binding.etMobileNumber.text.toString().trim()
                if (TextUtils.isEmpty(mobileNumber)) {
                    listener.showSnackbarMsg(getString(R.string.msg_enter_valid_mobile))
                    binding.btnApply.isEnabled = true
                    return false
                }
                payment.customerMobileNo = mobileNumber
                payment.telcode = binding.spnTelephoneCode.selectedItem.toString()
            }

            if (payment.paymentMode == Constant.PaymentMode.WALLET && binding.etOTP.text != null) {
                val otp = binding.etOTP.text.toString().trim()
                if (TextUtils.isEmpty(otp)) {
                    listener.showSnackbarMsg(getString(R.string.msg_enter_otp))
                    binding.btnApply.isEnabled = true
                    return false
                } else if (otp.length != 6) {
                    listener.showSnackbarMsg(getString(R.string.msg_enter_valid_otp))
                    binding.btnApply.isEnabled = true
                    return false
                }
                payment.otp = otp
            }

            val breakup = PaymentBreakup(payment.paymentMode.name, amountPaid)
            paymentBreakupList.add(breakup)
            payment.amountPaid = amountPaid
        }

        payment.paymentBreakUps = paymentBreakupList
        if (mCode == Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE)
            payment.amountDue = BigDecimal.ZERO
        else
            payment.amountDue = payment.amountTotal - payment.amountPaid

        return true

    }

    private fun paymentProceed() {
        if (payment.paymentMode == Constant.PaymentMode.WALLET || payment.paymentMode == Constant.PaymentMode.CASH || payment.paymentMode == Constant.PaymentMode.CHEQUE) {
            if (savePayment()) {
                Handler().postDelayed({
                    targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    binding.btnApply.isEnabled = true
                    listener.popBackStack()
                }, 750)
            }
        } else {
            if ((payment.paymentMode == Constant.PaymentMode.WALLET || payment.paymentMode == Constant.PaymentMode.MOBICASH) && binding.etMobileNumber.text != null && binding.spnTelephoneCode.selectedItem != null) {

                if (isValidMobicash()) {
                    getTransactionID()
                }


            }
        }
    }

    private fun isValidMobicash(): Boolean {
        if (binding.etAmount.text != null && TextUtils.isEmpty(binding.etAmount.text)) {
            listener.showSnackbarMsg(getString(R.string.msg_enter_amount))
            binding.btnApply.isEnabled = true
            return false

        }
        if (!TextUtils.isEmpty(binding.etAmount.text.toString())) {
            val amountPaid = binding.etAmount.text.toString().trim().toBigDecimal()

            if (amountPaid == BigDecimal.ZERO) {
                var doubleVal = getDecimalVal(resources.getString(R.string.payment_cannot_be_done))
                listener.showSnackbarMsg(getTextWithPrecisionVal(resources.getString(R.string.payment_cannot_be_done), doubleVal))
                binding.btnApply.isEnabled = true
                return false
            }

            if (amountPaid < payment.minimumPayAmount) {
                listener.showSnackbarMsg(getString(R.string.msg_minimum_pay_amount))
                binding.btnApply.isEnabled = true
                return false
            }
        } else {
            listener.showSnackbarMsg(getString(R.string.msg_enter_amount))
            binding.btnApply.isEnabled = true
            return false
        }

        val mobileNumber = binding.etMobileNumber.text.toString().trim()
        if (TextUtils.isEmpty(mobileNumber)) {
            listener.showSnackbarMsg(getString(R.string.msg_enter_valid_mobile))
            binding.btnApply.isEnabled = true
            return false
        }
        return true
    }

    private fun getTransactionID() {

        // region EditText
        val view = EditText(context)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 0, 16, 0)
        view.layoutParams = params
        view.hint = getString(R.string.hint_enter_remarks)
        // endregion
        listener.showAlertDialog(R.string.remarks,
                R.string.save,
                View.OnClickListener {
                    val remarks = view.text?.toString()?.trim()
                    if (TextUtils.isEmpty(remarks)) {
                        view.error = getString(R.string.msg_enter_remarks)
                    } else {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        showConfirmationDialog(remarks)
                    }
                },
                R.string.cancel,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                /*R.string.skip_and_save*/0,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    showConfirmationDialog()
                },
                view)
    }

    private fun showConfirmationDialog(remarks: String? = "") {
        listener.showAlertDialog(R.string.are_you_sure_you_want_to_continue,
                R.string.yes,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    if (remarks == null || TextUtils.isEmpty(remarks))
                        doMobiCashTransaction()
                    else
                        doMobiCashTransaction(remarks)
                },
                R.string.no,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                })
    }

    private fun doMobiCashTransaction(remarks: String? = "") {
        if (savePayment()) {
            if (payment.amountPaid <= BigDecimal.ZERO) {
                var doubleVal = getDecimalVal(resources.getString(R.string.payment_cannot_be_done))
                listener.showAlertDialog(getTextWithPrecisionVal(resources.getString(R.string.payment_cannot_be_done), doubleVal))

                return
            }
            helper.fetchLocation()
            helper.setListener(object : LocationHelper.Location {
                override fun found(latitude: Double, longitude: Double) {
                    val context = SecurityContext()
                    context.latitude = "$latitude"
                    context.longitude = "$longitude"
                    val mobiCashTransaction = MobiCashTransaction()
                    /*if ((MyApplication.getPrefHelper().agentTypeCode != Constant.AgentTypeCode.ISP.name && MyApplication.getPrefHelper().agentTypeCode != Constant.AgentTypeCode.SPR.name))
                        mobiCashTransaction.agentacctid = MyApplication.getPrefHelper().accountId*/
                    if (MyApplication.getPrefHelper().agentTypeCode === Constant.AgentTypeCode.TPA.name || MyApplication.getPrefHelper().agentTypeCode === Constant.AgentTypeCode.PPS.name || MyApplication.getPrefHelper().agentTypeCode === Constant.AgentTypeCode.ASA.name)
                    mobiCashTransaction.agentacctid = MyApplication.getPrefHelper().accountId

                    mobiCashTransaction.custacctid = payment.customerID
                    mobiCashTransaction.billamt = payment.amountPaid
                    mobiCashTransaction.remarks = remarks
                    mobiCashTransaction.mobile = payment.customerMobileNo
                    mobiCashTransaction.walletcode = Constant.PaymentMode.MOBICASH.toString()

                    APICall.makeMobicashPaymentRequest(mobiCashTransaction, context, object : ConnectionCallBack<MobiCashPayment> {
                        override fun onSuccess(response: MobiCashPayment) {
                            if (response.status == "0") {
                                listener.dismissDialog()
                                Handler().postDelayed({
                                    val intent = Intent()
                                    intent.putExtra(Constant.KEY_MOBICASH_PAYMENT, response)
                                    targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                                    binding.btnApply.isEnabled = true
                                    listener.popBackStack()
                                }, 750)
                            } else {
                                listener.dismissDialog()
                                listener.showAlertDialog(response.message)
                            }
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                            listener.showAlertDialog(message)
                            listener.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, _ ->
                                dialogInterface.dismiss()
                                listener.paymentFail()
                            })
                            /*  Handler().postDelayed({
                            val intent = Intent()
                            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                            binding.btnApply.isEnabled = true
                            listener.popBackStack()
                        }, 750)*/
                        }
                    })
                }

                override fun start() {
                    listener.showProgressDialog(R.string.msg_processing_payment)
                }
            })
        }
    }


    interface Listener {
        fun popBackStack()
        fun showSnackbarMsg(message: String?)
        fun showAlertDialog(message: String?)
        fun dismissDialog()
        fun hideKeyBoard()
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun showAlertDialog(message: String?, listener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener?, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
        fun paymentFail()
    }

    override fun onResume() {
        super.onResume()
        helper = LocationHelper(requireContext(), binding.btnApply, fragment = this)
    }

    override fun onDetach() {
        super.onDetach()
        helper.disconnect()
    }

}