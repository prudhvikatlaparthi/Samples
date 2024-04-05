package com.sgs.citytax.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityScanBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.TaxPayerDetails
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Event
import java.math.BigDecimal

class ScanActivity : BaseActivity(),
        ScannerFragment.Listener,
        ImpoundmentsComboStaticsDialogFragment.Listener,
        ImpoundmentReturnHistoryfragment.Listener,
        TicketPaymentFragment.Listener,
        VehicleOwnershipEntryFragment.Listener{
    private var fromScreen: Any? = null
    private var businessMode: Constant.BusinessMode = Constant.BusinessMode.None
    private lateinit var binding: ActivityScanBinding
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        hideToolbar()
        processIntent()
        showScanner()
    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU)
            businessMode =
                it.getSerializableExtra(Constant.KEY_BUSINESS_MODE) as? Constant.BusinessMode?
                    ?: Constant.BusinessMode.None
        }
    }

    private fun showScanner() {
        addFragmentWithOutAnimation(ScannerFragment.newInstance(fromScreen,businessMode), false, R.id.container)
    }

    override fun scanResult(response: TaxPayerDetails) {
        when (fromScreen) {
            null -> {
            }

            Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
                showAlertDialog(getString(R.string.msg_business_already_registered)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    finish()
                }
            }

            Constant.QuickMenu.QUICK_MENU_BUSINESS_SUMMARY -> {
                val intent = Intent(this, BusinessSummaryActivity::class.java)
                //region Set Arguments
                ObjectHolder.registerBusiness.sycoTaxID = response.sycoTaxID ?: ""
                ObjectHolder.registerBusiness.vuCrmAccounts = response.vuCrmAccounts
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSINESS_SUMMARY)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, response.sycoTaxID)
                //endregion
                startActivity(intent)
                finish()
            }

            Constant.QuickMenu.QUICK_MENU_TRACKON_BUSINESS_SUMMARY -> {
                val intent = Intent(this, BusinessSummaryActivity::class.java)
                //region Set Arguments
                ObjectHolder.registerBusiness.sycoTaxID = response.sycoTaxID ?: ""
                ObjectHolder.registerBusiness.vuCrmAccounts = response.vuCrmAccounts
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TRACKON_BUSINESS_SUMMARY)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, response.sycoTaxID)
                //endregion
                startActivity(intent)
                finish()
            }

            Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS -> {
                if (businessMode == Constant.BusinessMode.BusinessActivateVerifyScan) {
                    val intent = Intent()
                    intent.putExtra(Constant.KEY_SYCO_TAX_ID, response.sycoTaxID)
                    Event.instance.hold(intent)
                    setResult(Activity.RESULT_OK)
                } else {
                    /*  val currentInvoiceDue = response.businessDues?.businessDueSummary?.get(0)?.currentYearDue
                        ?: BigDecimal.ZERO*/
                    var currentInvoiceDue = BigDecimal.ZERO
                    //Initial Current year outstanding > 0 && CurrentInvoiceDue == 0


                    if ((response.businessDues?.businessDueSummary?.get(0)?.initialOutstandingCurrentYearDue!! > BigDecimal.ZERO
                                && response.businessDues?.businessDueSummary?.get(0)?.currentInvoiceDue?.compareTo(
                            BigDecimal(0)
                        ) == 0) ||
                        response.businessDues?.businessDueSummary?.get(0)?.initialOutstandingCurrentYearDue!!.compareTo(
                            BigDecimal(0)
                        ) == 0
                        && response.businessDues?.businessDueSummary?.get(0)?.currentInvoiceDue?.compareTo(
                            BigDecimal(0)
                        ) == 0
                    ) {
                        currentInvoiceDue = BigDecimal.ZERO
                    } else {
                        currentInvoiceDue = BigDecimal.ONE
                    }

                    val intent = Intent(this, RegisterBusinessActivity::class.java)
                    //region Set Arguments
                    ObjectHolder.registerBusiness.sycoTaxID = response.sycoTaxID ?: ""
                    ObjectHolder.registerBusiness.vuCrmAccounts = response.vuCrmAccounts
                    ObjectHolder.registerBusiness.vuCrmAccounts?.email = response.email
                    ObjectHolder.registerBusiness.vuCrmAccounts?.phone = response.number
                    ObjectHolder.registerBusiness.vuCrmAccounts?.estimatedTax =
                        response.estimatedTax
                    intent.putExtra(
                        Constant.KEY_QUICK_MENU,
                        Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS
                    )
                    intent.putExtra(Constant.KEY_CUSTOMER_ID, response.sycoTaxID)
                    if (currentInvoiceDue > BigDecimal.ZERO) {
                        intent.putExtra(Constant.KEY_EDIT, true)
                    } else {
                        intent.putExtra(Constant.KEY_EDIT, false) //disable
                    }
                    //endregion
                    startActivity(intent)
                }
                finish()
            }

            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_COLLECTION,
            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE -> {
                val intent = Intent(this, TaxDetailsActivity::class.java)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, response.CustomerID)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                startActivity(intent)
                finish()
            }

            Constant.QuickMenu.QUICK_MENU_CORPORATE_BUSINESS_TRANSACTION_HISTORY -> {
                val intent = Intent(this, BusinessTransactionHistoryActivity::class.java)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, response.sycoTaxID)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                startActivity(intent)
                finish()
            }

            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE_HISTORY -> {
                val intent = Intent(this, TaxNoticeHistoryActivity::class.java)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, response.sycoTaxID)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                startActivity(intent)
                finish()
            }

            Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER -> {
                ObjectHolder.registerBusiness.vuCrmAccounts = response.vuCrmAccounts
                val intent = Intent(this, RegisterOwnerActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                startActivity(intent)
                finish()
            }

            Constant.QuickMenu.QUICK_MENU_VERIFICATION_BUSINESS -> {
                val intent = Intent(this, BusinessSummaryActivity::class.java)
                //region Set Arguments
                ObjectHolder.registerBusiness.sycoTaxID = response.sycoTaxID ?: ""
                ObjectHolder.registerBusiness.vuCrmAccounts = response.vuCrmAccounts
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_VERIFICATION_BUSINESS)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, response.sycoTaxID)
                //endregion
                startActivity(intent)
                finish()
            }
            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_PENALTY_WAIVE_OFF -> {
                val intent = Intent(this, PenaltyWaiveOffActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                intent.putExtra(Constant.KEY_ACCOUNT_ID, response.CustomerID)
                startActivity(intent)
                finish()
            }

            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_OUTSTANDING_WAIVE_OFF -> {
                val intent = Intent(this, OutstandingWaiveOffActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                intent.putExtra(Constant.KEY_ACCOUNT_ID, response.CustomerID)
                startActivity(intent)
                finish()
            }

            Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP -> {
                val intent = Intent(this, OutstandingWaiveOffActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                intent.putExtra(Constant.KEY_ACCOUNT_ID, response.CustomerID)
                startActivity(intent)
                finish()
            }
            Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_NOTICES -> {
                val intent = Intent(this, HandoverDueNoticesActivity::class.java)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, response.sycoTaxID)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                startActivity(intent)
                finish()
            }

            else -> {
            }
        }
    }

    override fun addFragmentWithFrameLayoutID(fragment: Fragment, addToBackStack: Boolean, frameLayoutID: Int) {
        addFragmentWithOutAnimation(fragment, false, frameLayoutID)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }
    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }

    override fun onBackPressed() {
        finish()
    }
}