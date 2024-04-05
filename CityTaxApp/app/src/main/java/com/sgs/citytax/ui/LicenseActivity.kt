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
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.SubscriptionRenewal
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.GenericServiceResponse
import com.sgs.citytax.api.response.RoundingMethod
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityLicenseDetailsBinding
import com.sgs.citytax.ui.fragments.LicenseDetailsFragment
import com.sgs.citytax.ui.fragments.LicenseRenewFragment
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.getRoundValue
import java.math.BigDecimal
import java.util.*

class LicenseActivity : BaseActivity(), LicenseDetailsFragment.Listener, LicenseRenewFragment.Listener {

    private lateinit var binding: ActivityLicenseDetailsBinding
    private var roundingMethod: RoundingMethod? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_license_details)
        showToolbarBackButton(R.string.title_license_details)
        attachFragment()
        getDefaultRoundingMethod()
    }

    private fun getDefaultRoundingMethod() {
        showProgressDialog()
        APICall.getTableOrViewData(getDefaultRoundingMethodPayload(), object : ConnectionCallBack<GenericServiceResponse> {
            override fun onSuccess(response: GenericServiceResponse) {
                dismissDialog()
                if (response.result?.roundingMethods?.size ?: 0 > 0)
                    roundingMethod = response.result!!.roundingMethods?.get(0)
            }

            override fun onFailure(message: String) {
                dismissDialog()
                if (message.isNotEmpty())
                    showAlertDialog(message)
            }
        })
    }

    private fun getDefaultRoundingMethodPayload(): AdvanceSearchFilter {
        val searchFilter = AdvanceSearchFilter()

        //region Apply Filters
        val filterList: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "[Default]"
        filterColumn.columnValue = "Y"
        filterColumn.srchType = "equal"

        filterList.add(filterColumn)
        searchFilter.filterColumns = filterList
        //endregion

        //region Apply Table/View details
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "COM_RoundingMethods"
        tableDetails.primaryKeyColumnName = "RoundingMethodID"
        tableDetails.selectColoumns = "RoundingMethodID, RoundingMethod, RoundingPlace, [Default]"
        tableDetails.TableCondition = "OR"

        searchFilter.tableDetails = tableDetails
        //endregion

        return searchFilter
    }

    private fun attachFragment() {
        addFragmentWithOutAnimation(LicenseDetailsFragment(), true, R.id.licenseContainer)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.licenseContainer)

    override fun onBackPressed() {
        when (currentFragment) {
            is LicenseDetailsFragment -> {
                (currentFragment as LicenseDetailsFragment).onBackPressed()
            }
            is LicenseRenewFragment -> {
                (currentFragment as LicenseRenewFragment).onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
        when (currentFragment) {
            is LicenseDetailsFragment -> {
                showToolbarBackButton(R.string.title_license_details)
            }
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.licenseContainer)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_LICENCE_RENEWAL && resultCode == Activity.RESULT_OK) {
            if (currentFragment is LicenseRenewFragment)
                (currentFragment as LicenseRenewFragment).onBackPressed()
            if (currentFragment is LicenseDetailsFragment)
                (currentFragment as LicenseDetailsFragment).updateData()
            data?.extras?.let {
                val intent = Intent(this@LicenseActivity, AllTaxNoticesActivity::class.java)
                if (it.containsKey(Constant.KEY_ADVANCE_RECEIVED_ID))
                    intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getInt(Constant.KEY_ADVANCE_RECEIVED_ID))
                intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.LICENSE_RENEWAL.Code)
                startActivity(intent)
            }
        }
    }

    override fun onRenewClick(userID: String, subscriptionRenewal: SubscriptionRenewal) {
        val payment = MyApplication.resetPayment()
        payment.amountDue = getRoundValue(subscriptionRenewal.amount
                ?: BigDecimal.ZERO, roundingMethod?.roundingPlace ?: 0)
        payment.amountTotal = getRoundValue(subscriptionRenewal.amount
                ?: BigDecimal.ZERO, roundingMethod?.roundingPlace ?: 0)
        payment.userID = userID
        payment.customerID = MyApplication.getPrefHelper().accountId
        payment.subscriptionRenewal = subscriptionRenewal
        val intent = Intent(this@LicenseActivity, PaymentActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_LICENSE_RENEW)
        startActivityForResult(intent, Constant.REQUEST_CODE_LICENCE_RENEWAL)
    }

    override fun getRoundingPlace(): Int {
        return roundingMethod?.roundingPlace ?: 0
    }
}
