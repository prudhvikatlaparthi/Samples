package com.sgs.citytax.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.GenerateCustomerAllTaxes
import com.sgs.citytax.api.payload.GenerateCustomerTaxNotice
import com.sgs.citytax.api.payload.GetShowOrHotelBillingAndPricing
import com.sgs.citytax.api.response.GenerateTaxNoticeResponse
import com.sgs.citytax.api.response.ShowAndHotelBillingAndPricingResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityTaxDetailsBinding
import com.sgs.citytax.databinding.ShowBillingDialogBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.model.TaxableMatterList
import com.sgs.citytax.ui.fragments.TaxDetailsFragment
import com.sgs.citytax.ui.fragments.TaxNoticeFragment
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_ADVANCE_RECEIVED_ID
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU
import com.sgs.citytax.util.Constant.KEY_TAX_INVOICE_ID
import com.sgs.citytax.util.Constant.KEY_TAX_RULE_BOOK_CODE
import com.sgs.citytax.util.Constant.REQUEST_CODE_GENERATE_TAX_NOTICE
import com.sgs.citytax.util.Constant.REQUEST_CODE_PAYMENT_SUCCESS
import com.sgs.citytax.util.LocationHelper
import com.sgs.citytax.util.displayFormatDate
import com.sgs.citytax.util.formatWithPrecision
import kotlinx.android.synthetic.main.show_billing_dialog.*
import java.math.BigDecimal

class TaxDetailsActivity : BaseActivity(),
        TaxDetailsFragment.Listener,
        TaxNoticeFragment.Listener {

    private lateinit var binding: ActivityTaxDetailsBinding
    private var taxDetailsFragment: TaxDetailsFragment? = null

    private var customerID = 0
    private var licenseNumber = ""
    private var mCode = Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_COLLECTION

    private var mTaxRuleBookCode: String? = ""
    var dialog: Dialog? = null

    private lateinit var helper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tax_details)
        showToolbarBackButton(R.string.title_tax_details)
        processIntent()
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT_SUCCESS && resultCode == Activity.RESULT_OK) {
            data?.extras?.let {

                var taxInvoiceID = 0
                if (it.containsKey(KEY_TAX_INVOICE_ID))
                    taxInvoiceID = it.getInt(KEY_TAX_INVOICE_ID)

                if (prefHelper.showTaxNotice) {
                    val intent = Intent(this@TaxDetailsActivity, AllTaxNoticesActivity::class.java)
                    if (it.containsKey(KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(KEY_ADVANCE_RECEIVED_ID, it.getInt(KEY_ADVANCE_RECEIVED_ID))
                    intent.putExtra(KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
                    intent.putExtra(KEY_TAX_INVOICE_ID, taxInvoiceID)
                    startActivity(intent)
                    MyApplication.getPrefHelper().isFromHistory = false
                } else
                    showSnackbarMsg(getString(R.string.msg_payment_success_tax_notice, taxInvoiceID))

            }
        }
        taxDetailsFragment?.getTaxDetails()
        helper.onActivityResult(requestCode, resultCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        helper.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onResume() {
        super.onResume()
        helper = LocationHelper(this, binding.container, activity = this)
    }

    override fun onDestroy() {
        super.onDestroy()
        helper.disconnect()
    }

    private fun processIntent() {
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.get(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_CUSTOMER_ID))
                customerID = it.getInt(Constant.KEY_CUSTOMER_ID)
            if (it.containsKey(Constant.KEY_LICENSE_NUMBER))
                licenseNumber = it.getString(Constant.KEY_LICENSE_NUMBER).toString()
        }
    }

    private fun attachFragment() {
        taxDetailsFragment = TaxDetailsFragment.newInstance(mCode, customerID, licenseNumber)
        addFragment(taxDetailsFragment!!, true)
    }

    override fun onPaymentHistoryClick(taxDetails: SAL_TaxDetails) {
        val payment = MyApplication.resetPayment()
        payment.customerID = customerID
        payment.productCode = taxDetails.productCode
        payment.voucherNo = taxDetails.VoucherNo
        payment.taxRuleBookCode = taxDetails.taxRuleBookCode
        startActivity(Intent(this@TaxDetailsActivity, PaymentHistoryActivity::class.java))
    }

    override fun onCollectPaymentClick(taxDetails: SAL_TaxDetails) {
        val payment = MyApplication.resetPayment()
        payment.amountDue = taxDetails.TotalDue
        payment.amountTotal = taxDetails.TotalDue
        payment.customerID = customerID
        payment.paymentType = Constant.PaymentType.TAX
        payment.productCode = taxDetails.productCode
        payment.voucherNo = taxDetails.VoucherNo
        payment.minimumPayAmount = taxDetails.minimumPayAmount
        // To show TaxNotice ID after payment is successful
        payment.currentTaxInvoiceNo = taxDetails.currentTaxInvoiceNo
        val intent = Intent(this@TaxDetailsActivity, PaymentActivity::class.java)
        intent.putExtra(KEY_QUICK_MENU, mCode)
        mTaxRuleBookCode = taxDetails.taxRuleBookCode
        startActivityForResult(intent, REQUEST_CODE_PAYMENT_SUCCESS)
    }

    override fun onGenTaxInvoiceClick(taxNotices: List<SAL_TaxDetails>) {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                if (taxNotices.size > 1)
                    generateAllTaxNotices(latitude, longitude)
                else {
                    if (taxNotices[taxNotices.size - 1].taxRuleBookCode == Constant.TaxRuleBook.HOTEL.Code
                            || taxNotices[taxNotices.size - 1].taxRuleBookCode == Constant.TaxRuleBook.SHOW.Code)
                        getBillingDetailsForShowAndHotel(taxNotices[taxNotices.size - 1], latitude, longitude)
                    else
                        generateTaxNotice(latitude, longitude, taxNotices[taxNotices.size - 1])
                }
            }

            override fun start() {
                showProgressDialog(R.string.msg_please_wait)
            }
        })
    }

    private fun generateAllTaxNotices(latitude: Double, longitude: Double) {
        val context = SecurityContext()
        context.latitude = "$latitude"
        context.longitude = "$longitude"
        val generateCustomerAllTaxes = GenerateCustomerAllTaxes()
        generateCustomerAllTaxes.customerID = customerID
        generateCustomerAllTaxes.context = context
        APICall.generateCustomerAllTaxes(generateCustomerAllTaxes, object : ConnectionCallBack<List<GenerateTaxNoticeResponse>> {
            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }

            override fun onSuccess(response: List<GenerateTaxNoticeResponse>) {
                dismissDialog()
                if (currentFragment is TaxNoticeFragment)
                    (currentFragment as TaxNoticeFragment).onBackPressed()
                val intent = Intent(this@TaxDetailsActivity, AllTaxNoticesActivity::class.java)
                intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, response as ArrayList<out Parcelable>)
                intent.putExtra(KEY_QUICK_MENU, mCode)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, customerID)
                startActivityForResult(intent, REQUEST_CODE_GENERATE_TAX_NOTICE)
                MyApplication.getPrefHelper().isFromHistory = false
            }
        })
    }

    private fun generateTaxNotice(latitude: Double, longitude: Double, taxDetails: SAL_TaxDetails, taxableMatterList: TaxableMatterList? = null) {
        val context = SecurityContext()
        context.latitude = "$latitude"
        context.longitude = "$longitude"
        val generateCustomerTaxNotice = GenerateCustomerTaxNotice()
        generateCustomerTaxNotice.context = context
        generateCustomerTaxNotice.customerId = customerID
        generateCustomerTaxNotice.productCode = taxDetails.productCode ?: ""
        generateCustomerTaxNotice.voucherNo = taxDetails.VoucherNo
        //generateCustomerTaxNotice.taxableMatter = taxableMatter
        generateCustomerTaxNotice.taxableMatterList = taxableMatterList
        APICall.generateCustomerTaxNotice(generateCustomerTaxNotice, object : ConnectionCallBack<GenerateTaxNoticeResponse> {
            override fun onSuccess(response: GenerateTaxNoticeResponse) {
                dismissDialog()
                if (currentFragment is TaxNoticeFragment)
                    (currentFragment as TaxNoticeFragment).onBackPressed()
                val intent = Intent(this@TaxDetailsActivity, AllTaxNoticesActivity::class.java)
                intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, arrayListOf(response))
                intent.putExtra(KEY_QUICK_MENU, mCode)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, customerID)
                startActivityForResult(intent, REQUEST_CODE_PAYMENT_SUCCESS)
                MyApplication.getPrefHelper().isFromHistory = false
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.container)
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, addToBackStack, R.id.container)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onBackPressed() {
        when (currentFragment) {
            is TaxDetailsFragment -> {
                (currentFragment as TaxDetailsFragment).onBackPressed()
                MyApplication.resetPayment()
            }
            else -> {
                MyApplication.resetPayment()
                super.onBackPressed()
            }
        }
    }

    private fun getBillingDetailsForShowAndHotel(taxDetails: SAL_TaxDetails, latitude: Double, longitude: Double) {
        showProgressDialog()
        val billingDetails = GetShowOrHotelBillingAndPricing()
        billingDetails.accountId = taxDetails.AccountID
        billingDetails.voucherNo = taxDetails.VoucherNo
        billingDetails.taxRuleBookCode = taxDetails.taxRuleBookCode

        APICall.getBillingDetailsForShowAndHotel(billingDetails, object : ConnectionCallBack<ShowAndHotelBillingAndPricingResponse> {
            override fun onSuccess(response: ShowAndHotelBillingAndPricingResponse) {
                dismissDialog()
                showBillingDialog(response, taxDetails, latitude, longitude)
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })

    }

    private fun showBillingDialog(billingDetails: ShowAndHotelBillingAndPricingResponse, salTaxdetails: SAL_TaxDetails, latitude: Double, longitude: Double) {
        val layoutInflater = LayoutInflater.from(this)
        val mBinding: ShowBillingDialogBinding
        dialog = Dialog(this, R.style.AlertDialogTheme)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.setCancelable(true)
        mBinding = DataBindingUtil.inflate(layoutInflater, R.layout.show_billing_dialog, ll_root_view, false)
        dialog?.setContentView(mBinding.root)
        var estimatedAmount = BigDecimal.ZERO
        if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.SHOW.Code) {
            mBinding.llOperatorType.visibility = View.VISIBLE
            mBinding.txtRevenueText.text = mBinding.txtRevenueText.context.getString(R.string.revenue)
            mBinding.llShowRate.visibility = View.VISIBLE
        } else if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.HOTEL.Code) {
            mBinding.llStar.visibility = View.VISIBLE
            mBinding.llShowRate.visibility = View.GONE
            mBinding.edtRevenue.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
            mBinding.txtRevenueText.text = mBinding.txtRevenueText.context.getString(R.string.room_nights)
            mBinding.edtRevenue.inputType = InputType.TYPE_CLASS_NUMBER
        } else {
            mBinding.llOperatorType.visibility = View.GONE
            mBinding.llStar.visibility = View.GONE
            mBinding.llShowRate.visibility = View.GONE
        }

        if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.SHOW.Code && billingDetails.allowShowCount == "Y") {
            mBinding.llNoOfShows.visibility = View.VISIBLE
        } else {
            mBinding.llNoOfShows.visibility = View.GONE
        }

        billingDetails.billingCycle?.let {
            mBinding.txtBillingCycle.text = billingDetails.billingCycle
        }
        billingDetails.showRate?.let {
            mBinding.txtShowRate.text = formatWithPrecision(it)
        }
        salTaxdetails.occupancy?.let {
            mBinding.txtOperatorType.text = it
            mBinding.txtStar.text = it
        }
        billingDetails.startDate?.let {
            mBinding.txtFromDate.text = displayFormatDate(it)
        }
        billingDetails.endDate?.let {
            mBinding.txtToDate.text = displayFormatDate(it)
        }
        billingDetails.rate?.let {
            if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.HOTEL.Code)
                mBinding.txtRate.text = formatWithPrecision(it)
            else
                mBinding.txtRate.text = "$it%"
        }

        mBinding.edtRevenue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isNotEmpty()) {
                        if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.HOTEL.Code) {
                            estimatedAmount = s.toString().toBigDecimal() * (billingDetails.rate?.toBigDecimal()
                                    ?: BigDecimal.ZERO)
                        } else if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.SHOW.Code) {
                            estimatedAmount = ((s.toString().toBigDecimal()) * (billingDetails.rate?.toBigDecimal()
                                    ?: BigDecimal.ZERO).divide(BigDecimal.valueOf(100)))
                        }
                        mBinding.txtEstimatedAmount.text = formatWithPrecision(estimatedAmount)
                    } else
                        mBinding.txtEstimatedAmount.text = formatWithPrecision(0.0)
                }
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
///////Exisiting function need to check the current condition
       /* if (mBinding.llNoOfShows.isVisible) {
            mBinding.edtNoOfShows.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        if (s.isNotEmpty()) {
                            estimatedAmount = estimatedAmount.plus((s.toString().toBigDecimal()) * (billingDetails.showRate?.toBigDecimal()
                                    ?: BigDecimal.ZERO))

                            mBinding.txtEstimatedAmount.text = formatWithPrecision(estimatedAmount)
                        } else
                            mBinding.txtEstimatedAmount.text = formatWithPrecision(0.0)
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
            })
        }*/

        ///Below in Aparna's code , QA issue
        if (mBinding.llNoOfShows.isVisible) {
            mBinding.edtNoOfShows.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        if (s.isNotEmpty()) {
                            var mestimatedAmount= BigDecimal.ZERO
                            mestimatedAmount = mestimatedAmount.plus((s.toString().toBigDecimal()) * (billingDetails.showRate?.toBigDecimal()
                                    ?: BigDecimal.ZERO))

                            mBinding.txtEstimatedAmount.text = formatWithPrecision(mestimatedAmount)
                        } else
                            mBinding.txtEstimatedAmount.text = formatWithPrecision(0.0)
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
            })
        }

        mBinding.btnProceed.setOnClickListener {
            if (validateView(mBinding, salTaxdetails)) {
                dialog?.dismiss()
                //val taxableMatter = mBinding.edtRevenue.text.toString().toDouble()
                val taxableMatterList = TaxableMatterList()
                if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.SHOW.Code) {
                    taxableMatterList.revenue = mBinding.edtRevenue.text.toString().toBigDecimal()
                    if (mBinding.llNoOfShows.isVisible)
                        taxableMatterList.showCount = mBinding.edtNoOfShows.text.toString().toInt()
                } else if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.HOTEL.Code) {
                    mBinding.edtRevenue.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
                    taxableMatterList.roomNights = mBinding.edtRevenue.text.toString().toInt()
                }
                generateTaxNotice(latitude, longitude, salTaxdetails, taxableMatterList)
            }
        }

        dialog?.show()
    }

    private fun validateView(mBinding: ShowBillingDialogBinding, salTaxdetails: SAL_TaxDetails): Boolean {
        if (mBinding.edtRevenue.text.toString().isEmpty()) {
            if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.SHOW.Code)
                showAlertDialog(getString(R.string.msg_revenue))
            else
                showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.room_nights))

            mBinding.edtRevenue.requestFocus()
            return false
        }

        if (mBinding.edtRevenue.text.toString().isNotEmpty()
                && mBinding.edtRevenue.text.toString().toBigDecimal() <= BigDecimal.ZERO) {
            if (salTaxdetails.taxRuleBookCode == Constant.TaxRuleBook.SHOW.Code)
                showAlertDialog(getString(R.string.msg_revenue))
            else
                showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.room_nights))

            mBinding.edtRevenue.requestFocus()
            return false
        }

        if (mBinding.llNoOfShows.isVisible && mBinding.edtNoOfShows.text.toString().isEmpty()) {
            showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.no_of_shows))
            mBinding.edtNoOfShows.requestFocus()
            return false
        }

        return true
    }

}