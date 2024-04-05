package com.sgs.citytax.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.GenerateCustomerAllTaxes
import com.sgs.citytax.api.payload.GenerateCustomerTaxNotice
import com.sgs.citytax.api.response.GenerateTaxNoticeResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityTaxDetailsBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.ui.fragments.PropertyLandTaxDetailsFragment
import com.sgs.citytax.ui.fragments.PropertyLandTaxNoticeFragment
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_ADVANCE_RECEIVED_ID
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU
import com.sgs.citytax.util.Constant.KEY_TAX_INVOICE_ID
import com.sgs.citytax.util.Constant.KEY_TAX_RULE_BOOK_CODE
import com.sgs.citytax.util.Constant.REQUEST_CODE_GENERATE_TAX_NOTICE
import com.sgs.citytax.util.Constant.REQUEST_CODE_PAYMENT_SUCCESS
import com.sgs.citytax.util.LocationHelper

class PropertyLandTaxDetailsActivity : BaseActivity(),
        PropertyLandTaxDetailsFragment.Listener,
        PropertyLandTaxNoticeFragment.Listener {

    private lateinit var binding: ActivityTaxDetailsBinding
    private var taxDetailsFragment: PropertyLandTaxDetailsFragment? = null

    private var sycoTaxId: String? = ""
    private var propertyID: Int? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION

    private var mTaxRuleBookCode: String? = ""

    private lateinit var helper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tax_details)

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
                    val intent = Intent(this@PropertyLandTaxDetailsActivity, AllTaxNoticesActivity::class.java)
                    if (it.containsKey(KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(KEY_ADVANCE_RECEIVED_ID, it.getInt(KEY_ADVANCE_RECEIVED_ID))
                    intent.putExtra(KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
                    intent.putExtra(KEY_TAX_INVOICE_ID, taxInvoiceID)
                    startActivity(intent)
                } else
                    showSnackbarMsg(getString(R.string.msg_payment_success_tax_notice, taxInvoiceID))

            }
        }
        taxDetailsFragment?.getTaxDetails(sycoTaxId)
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
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                sycoTaxId = it.getString(Constant.KEY_SYCO_TAX_ID).toString()
            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                propertyID = it.getInt(Constant.KEY_PRIMARY_KEY)
        }
        if (mCode == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE
                || mCode == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION) {
            showToolbarBackButton(R.string.property_tax_details)
        } else {
            showToolbarBackButton(R.string.land_tax_details)
        }
    }

    private fun attachFragment() {
        taxDetailsFragment = PropertyLandTaxDetailsFragment.newInstance(mCode, sycoTaxId)
        addFragment(taxDetailsFragment!!, true)
    }

    override fun onPaymentHistoryClick(taxDetails: SAL_TaxDetails) {
        val payment = MyApplication.resetPayment()
        payment.customerID = taxDetails.AccountID
        payment.productCode = taxDetails.productCode
        payment.voucherNo = propertyID
        startActivity(Intent(this@PropertyLandTaxDetailsActivity, PaymentHistoryActivity::class.java))
    }

    override fun onCollectPaymentClick(taxDetails: SAL_TaxDetails) {
        val payment = MyApplication.resetPayment()
        payment.amountDue = taxDetails.TotalDue
        payment.amountTotal = taxDetails.TotalDue
        payment.customerID = taxDetails.AccountID
        payment.paymentType = Constant.PaymentType.TAX
        payment.productCode = taxDetails.productCode
        payment.voucherNo = taxDetails.VoucherNo
        payment.minimumPayAmount = taxDetails.minimumPayAmount
        // To show TaxNotice ID after payment is successful
        payment.currentTaxInvoiceNo = taxDetails.currentTaxInvoiceNo
        val intent = Intent(this@PropertyLandTaxDetailsActivity, PaymentActivity::class.java)
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
                else
                    generateTaxNotice(latitude, longitude, taxNotices[taxNotices.size - 1])
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
//        generateCustomerAllTaxes.customerID = customerID
        generateCustomerAllTaxes.context = context
        APICall.generateCustomerAllTaxes(generateCustomerAllTaxes, object : ConnectionCallBack<List<GenerateTaxNoticeResponse>> {
            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }

            override fun onSuccess(response: List<GenerateTaxNoticeResponse>) {
                dismissDialog()
                if (currentFragment is PropertyLandTaxNoticeFragment)
                    (currentFragment as PropertyLandTaxNoticeFragment).onBackPressed()
                val intent = Intent(this@PropertyLandTaxDetailsActivity, AllTaxNoticesActivity::class.java)
                intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, response as ArrayList<out Parcelable>)
                intent.putExtra(KEY_QUICK_MENU, mCode)
//                intent.putExtra(Constant.KEY_CUSTOMER_ID, customerID)
                startActivityForResult(intent, REQUEST_CODE_GENERATE_TAX_NOTICE)
            }
        })
    }

    private fun generateTaxNotice(latitude: Double, longitude: Double, taxDetails: SAL_TaxDetails) {
        val context = SecurityContext()
        context.latitude = "$latitude"
        context.longitude = "$longitude"
        val generateCustomerTaxNotice = GenerateCustomerTaxNotice()
        generateCustomerTaxNotice.context = context
        generateCustomerTaxNotice.customerId = taxDetails.AccountID
        generateCustomerTaxNotice.productCode = taxDetails.productCode ?: ""
        generateCustomerTaxNotice.voucherNo = taxDetails.VoucherNo
        APICall.generateCustomerTaxNotice(generateCustomerTaxNotice, object : ConnectionCallBack<GenerateTaxNoticeResponse> {
            override fun onSuccess(response: GenerateTaxNoticeResponse) {
                dismissDialog()
                if (currentFragment is PropertyLandTaxNoticeFragment)
                    (currentFragment as PropertyLandTaxNoticeFragment).onBackPressed()
                val intent = Intent(this@PropertyLandTaxDetailsActivity, AllTaxNoticesActivity::class.java)
                intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, arrayListOf(response))
                intent.putExtra(KEY_QUICK_MENU, mCode)
                startActivityForResult(intent, REQUEST_CODE_PAYMENT_SUCCESS)
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
            is PropertyLandTaxDetailsFragment -> {
                (currentFragment as PropertyLandTaxDetailsFragment).onBackPressed()
                MyApplication.resetPayment()
            }
            else -> {
                MyApplication.resetPayment()
                super.onBackPressed()
            }
        }
    }

}