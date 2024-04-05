package com.sgs.citytax.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GenerateTaxNoticeResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityPaymentNewBinding
import com.sgs.citytax.ui.fragments.PaymentEditFragment
import com.sgs.citytax.ui.fragments.PaymentFragment
import com.sgs.citytax.ui.fragments.PaymentRechargeFragment
import com.sgs.citytax.util.Constant

class PaymentActivity : BaseActivity(),
        PaymentRechargeFragment.Listener,
        PaymentFragment.Listener,
        PaymentEditFragment.Listener {


    private lateinit var binding: ActivityPaymentNewBinding
    private lateinit var code: Constant.QuickMenu
    private var ignoreCheque : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_new)
        showToolbarBackButton(R.string.title_payment)
        processIntent()
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun processIntent() {
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                code = it.get(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_IGNORE_CHEQUE_PAYMENT))
                ignoreCheque = it.getBoolean(Constant.KEY_IGNORE_CHEQUE_PAYMENT,false)
        }
    }

    private fun attachFragment() {
        when (code) {
            Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE -> {
                val fragment = PaymentRechargeFragment.newInstance(code)
                addFragmentWithOutAnimation(fragment, true, R.id.container)
            }
            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_COLLECTION,
            Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION,
            Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION,
            Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION,
            Constant.QuickMenu.QUICK_MENU_LICENSE_RENEW,
            Constant.QuickMenu.QUICK_MENU_SALES_TAX,
            Constant.QuickMenu.QUICK_MENU_SECURITY_TAX,
            Constant.QuickMenu.QUICK_MENU_CREATE_ASSET_BOOKING,
            Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING,
            Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT,
            Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT,
            Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL,
            Constant.QuickMenu.QUICK_MENU_SERVICE_REQUEST_MASTER,
            Constant.QuickMenu.QUICK_MENU_SERVICE,
            Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT,
            Constant.QuickMenu.QUICK_MENU_PENDING_SERVICE_REQUESTS-> {
                val fragment = PaymentFragment.newInstance(code,ignoreCheque)
                addFragmentWithOutAnimation(fragment, true, R.id.container)
            }
            else -> {

            }
        }
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
            is PaymentRechargeFragment ->
                (currentFragment as PaymentRechargeFragment).onBackPressed()
            is PaymentFragment ->
                (currentFragment as PaymentFragment).onBackPressed()
            else ->
                super.onBackPressed()
        }
    }

    override fun paymentSuccess(advanceReceivedID: Int, taxInvoiceID: Int) {
        val intent = Intent()
        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, advanceReceivedID)
        intent.putExtra(Constant.KEY_TAX_INVOICE_ID, taxInvoiceID)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun paymentSuccess(advanceReceivedID: Int) {
        val intent = Intent()
        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, advanceReceivedID)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun paymentSuccessForLicense(advanceReceivedID: Int) {
        val intent = Intent(this@PaymentActivity, LicenseActivity::class.java)
        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, advanceReceivedID)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onServiceGenerationSuccess(response: GenerateTaxNoticeResponse) {
        val intent = Intent()
        intent.putExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, response)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun navigateToReceipt(invoiceID: Int,advanceReceivedID: Int) {
        val intent = Intent()
        intent.putExtra(Constant.KEY_TAX_INVOICE_ID, invoiceID)
        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, advanceReceivedID)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun paymentFail()
    {
        finish()
    }
}