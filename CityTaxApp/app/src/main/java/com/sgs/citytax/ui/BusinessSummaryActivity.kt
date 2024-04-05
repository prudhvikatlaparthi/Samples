package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.payload.StoreCustomerB2B
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityBusinessSummaryBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.VUCRMAccounts
import com.sgs.citytax.model.VehicleDetails
import com.sgs.citytax.model.VehicleMaster
import com.sgs.citytax.ui.fragments.BusinessSummaryApprovalFragment
import com.sgs.citytax.ui.fragments.VehicleSummaryFragment
import com.sgs.citytax.util.Constant

class BusinessSummaryActivity : BaseActivity(), BusinessSummaryApprovalFragment.Listener, VehicleSummaryFragment.Listener {
    private lateinit var binding: ActivityBusinessSummaryBinding
    private var sycoTaxId = ""
    private var vuCrmAccount: VUCRMAccounts? = null
    private var fromScreen: Constant.QuickMenu? = null
    private var vehicleMaster: VehicleMaster? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_summary)
        processIntent()
        attachFragment()
        if(fromScreen == Constant.QuickMenu.QUICK_MENU_TRACKON_BUSINESS_SUMMARY)
            showToolbarBackButton(R.string.vehicle_summary)
        else
            showToolbarBackButton(R.string.title_business_summary)
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_SYCO_TAX_ID))
                sycoTaxId = intent.getStringExtra(Constant.KEY_SYCO_TAX_ID) ?: ""

            if (intent.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?

            if (intent.hasExtra(Constant.KEY_VEHICLE_DETAILS))
                vehicleMaster = intent.getParcelableExtra(Constant.KEY_VEHICLE_DETAILS) as VehicleMaster?

            vuCrmAccount = ObjectHolder.registerBusiness.vuCrmAccounts
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getData(): StoreCustomerB2B {
        val storeCustomerB2B = StoreCustomerB2B()
        storeCustomerB2B.organization?.organization = vuCrmAccount?.accountName
        storeCustomerB2B.organization?.accountID = vuCrmAccount?.accountId!!
        storeCustomerB2B.organization?.organizationID = vuCrmAccount?.organizationId!!
        storeCustomerB2B.organization?.sycotaxID = vuCrmAccount?.sycoTaxID
        storeCustomerB2B.organization?.statusCode = vuCrmAccount?.statusCode
        storeCustomerB2B.organization?.status = vuCrmAccount?.status
        storeCustomerB2B.organization?.segmentId = vuCrmAccount?.segmentId
        storeCustomerB2B.organization?.parentOrganizationID = vuCrmAccount?.parentOrganizationID
        storeCustomerB2B.organization?.phone = vuCrmAccount?.phone
        storeCustomerB2B.organization?.email = vuCrmAccount?.email
        storeCustomerB2B.organization?.status = vuCrmAccount?.status
        storeCustomerB2B.organization?.statusCode = vuCrmAccount?.statusCode
        storeCustomerB2B.organization?.activityDomainID = vuCrmAccount?.activityDomainID
        storeCustomerB2B.organization?.webSite = vuCrmAccount?.website
        storeCustomerB2B.organization?.ifu = vuCrmAccount?.ifu
        storeCustomerB2B.organization?.remarks = vuCrmAccount?.remarks
        vuCrmAccount?.accountId?.let {
            storeCustomerB2B.organization?.accountID = it
        }
        vuCrmAccount?.organizationId?.let {
            storeCustomerB2B.organization?.organizationID = it
        }
        storeCustomerB2B.organization?.sycotaxID = vuCrmAccount?.sycoTaxID
        storeCustomerB2B.organization?.email = vuCrmAccount?.email
        storeCustomerB2B.organization?.activityDomainName = vuCrmAccount?.activityDomain
        storeCustomerB2B.organization?.latitude = vuCrmAccount?.latitude?.toDouble()
        storeCustomerB2B.organization?.longitude = vuCrmAccount?.longitude?.toDouble()
        storeCustomerB2B.organization?.geoAddressID = vuCrmAccount?.geoAddressID
        storeCustomerB2B.organization?.activityClassID = vuCrmAccount?.activityClassID
        storeCustomerB2B.organization?.activityClassName = vuCrmAccount?.activityClassName
        storeCustomerB2B.organization?.tradeNo = vuCrmAccount?.tradeNo
        /*
        createdByAccountId
        */
        return storeCustomerB2B
    }

    private fun attachFragment() {
        if(fromScreen == Constant.QuickMenu.QUICK_MENU_TRACKON_BUSINESS_SUMMARY){
            val fragment = VehicleSummaryFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            bundle.putParcelable(Constant.KEY_VEHICLE_DETAILS, vehicleMaster)
            bundle.putSerializable(Constant.KEY_SYCO_TAX_ID, sycoTaxId)
            fragment.arguments = bundle
            addFragment(fragment, true)
        }else {
            val fragment = BusinessSummaryApprovalFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            bundle.putParcelable(Constant.KEY_STORE_CUSTOMER_B2B, getData())
            fragment.arguments = bundle
            addFragment(fragment, true)
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.businessSummaryContainer)
    }
}