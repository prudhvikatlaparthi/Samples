package com.sgs.citytax.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.GenerateCustomerTaxNotice
import com.sgs.citytax.api.response.GenerateTaxNoticeResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityTaxDetailsBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.ui.fragments.IndividualDetailTaxNoticeFragment
import com.sgs.citytax.ui.fragments.IndividualTaxNoticeFragment
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper

class IndividualTaxNoticeActivity : BaseActivity(),IndividualDetailTaxNoticeFragment.Listener, IndividualTaxNoticeFragment.Listener{

    private lateinit var binding: ActivityTaxDetailsBinding
    private var individualTaxFragment : IndividualDetailTaxNoticeFragment? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE

    private var sycoTaxID : String ? =""
    private var mTaxRuleBookCode: String? = ""
    private lateinit var helper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tax_details)
        processIntent()
        showTitle()
        attachFragment()
    }

    private fun showTitle(){
        if(mCode  == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE)
            showToolbarBackButton(R.string.title_individual_tax_notice)
        else if(mCode == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION)
            showToolbarBackButton(R.string.title_individual_tax_collection)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
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
                sycoTaxID = it.get(Constant.KEY_SYCO_TAX_ID).toString()
        }
    }

    private fun attachFragment() {
        individualTaxFragment = IndividualDetailTaxNoticeFragment.newInstance(mCode,sycoTaxID)
        individualTaxFragment?.let {
            addFragment(it, true)
        }
    }

    override fun onPaymentHistoryClick(taxDetails: SAL_TaxDetails) {
        val payment = MyApplication.resetPayment()
        payment.customerID = taxDetails.AccountID
        payment.productCode = taxDetails.productCode
        payment.voucherNo = taxDetails.VoucherNo
        startActivity(Intent(this, PaymentHistoryActivity::class.java))
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
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
        mTaxRuleBookCode = taxDetails.taxRuleBookCode
        startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
    }

    override fun onGenTaxInvoiceClick(taxNotices: List<SAL_TaxDetails>) {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                generateTaxNotice(latitude, longitude, taxNotices[taxNotices.size - 1])
            }

            override fun start() {
                showProgressDialog(R.string.msg_please_wait)
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
                if (currentFragment is IndividualTaxNoticeFragment)
                    (currentFragment as IndividualTaxNoticeFragment).onBackPressed()
                val intent = Intent(this@IndividualTaxNoticeActivity, AllTaxNoticesActivity::class.java)
                intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, arrayListOf(response))
                intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, taxDetails.AccountID)
                startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS && resultCode == Activity.RESULT_OK) {
            data?.extras?.let {

                var taxInvoiceID = 0
                if (it.containsKey(Constant.KEY_TAX_INVOICE_ID))
                    taxInvoiceID = it.getInt(Constant.KEY_TAX_INVOICE_ID)

                if (prefHelper.showTaxNotice) {
                    val intent = Intent(this, AllTaxNoticesActivity::class.java)
                    if (it.containsKey(Constant.KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getInt(Constant.KEY_ADVANCE_RECEIVED_ID))
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
                    intent.putExtra(Constant.KEY_TAX_INVOICE_ID, taxInvoiceID)
                    startActivity(intent)
                } else
                    showSnackbarMsg(getString(R.string.msg_payment_success_tax_notice, taxInvoiceID))

            }
        }
        individualTaxFragment?.getIndividualTaxDetails(sycoTaxID)
        helper.onActivityResult(requestCode, resultCode)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onBackPressed() {
        when (currentFragment) {
            is IndividualDetailTaxNoticeFragment -> {
                (currentFragment as IndividualDetailTaxNoticeFragment).onBackPressed()
                MyApplication.resetPayment()
            }
            else -> {
                MyApplication.resetPayment()
                super.onBackPressed()
            }
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.container)
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, addToBackStack, R.id.container)
    }
}