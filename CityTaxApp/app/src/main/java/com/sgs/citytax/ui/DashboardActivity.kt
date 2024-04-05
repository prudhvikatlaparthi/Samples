package com.sgs.citytax.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import com.sgs.citytax.BuildConfig
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityDashboardBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_BUSINESS_MODE
import com.sgs.citytax.util.Constant.KEY_NAVIGATION_MENU
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU
import com.sgs.citytax.util.JedisUtil
import com.sgs.citytax.util.LocationHelper

class DashboardActivity : BaseActivity(),
        LocationHelper.Location,
        QuickMenuFragment.Listener,
        DashboardFragment.Listener,
        NavigationView.OnNavigationItemSelectedListener,
        MapFragment.Listener,
        BusinessInfoDialogFragment.Listener,
        LawPendingTransactionInfoDialogFragment.Listener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private var locationHelper: LocationHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.updateLanguage(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        if(BuildConfig.BUILD_VARIANT=="UAT") {
            showToolbarBackButton(getString(R.string.app_name) + "-Test")
        }else{
            showToolbarBackButton((R.string.app_name))
        }
        initViews()
        setEventListeners()
        setNavigationMenu()
        showDashboard()
    }

    override fun start() {
        showProgressDialog()
    }

    override fun found(latitude: Double, longitude: Double) {
        val latLong = LatLng(latitude, longitude)
        addFragmentWithOutAnimation(MapFragment.newInstance(latLong), true, R.id.map)
        dismissDialog()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationHelper?.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationHelper?.onActivityResult(requestCode, resultCode)
    }

    override fun addFragmentWithOutAnimation(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, true, R.id.container)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.container)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when {
            toggle.onOptionsItemSelected(item) -> return true
            id == R.id.action_profile -> {
                intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
                return true
            }
            id == R.id.action_settings -> {
                intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            id == R.id.action_logout -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //region Clear ObjectHolder before using new feature
        ObjectHolder.clearAll()
        //endregion
        binding.drawerLayout.closeDrawers()

        when (item.itemId) {
            R.id.agent_collection_histroy -> {
                startActivity(Intent(this, CollectionHistoryActivity::class.java))
            }
            R.id.incident_management -> {
                startActivity(Intent(this, IncidentActivity::class.java))
            }
            R.id.credit_balance_statement -> {
                startActivity(Intent(this, CreditBalanceActivity::class.java))
            }
            R.id.app_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.logout -> {
                showLogoutPopUp()
            }
            R.id.my_profile -> {
                startActivity(Intent(this, MyProfileActivity::class.java))
            }
            R.id.products -> {
                startActivity(Intent(this, MunicipalTaxesActivity::class.java))
            }
            R.id.tax_payer_registration -> {
                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
                startActivity(intent)
            }
            R.id.agent_summary_details -> {
                val intent = Intent(this, AgentSummaryDetailsActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_AGENT_SUMMARY_DETAILS)
                startActivity(intent)
            }
            R.id.agent_collection_summary -> {
                val intent = Intent(this, AgentCollectionSummaryActivity::class.java)
                startActivity(intent)
            }
            R.id.cash_deposit_approval -> {
                val intent = Intent(this, CashDepositApprovalActivity::class.java)
                startActivity(intent)
            }
            R.id.agent_commission_report -> {
                startActivity(Intent(this, AgentCommissionReportActivity::class.java))
            }
            R.id.pending_violations -> {
                startActivity(Intent(this, PendingViolationsActivity::class.java))
            }
            R.id.tax_notice_history -> {
                if (prefHelper.isLawEnforceAgent() || prefHelper.isLawEnforeInspector() || prefHelper.isLawEnforeSupervisor()) {
                    val intent = Intent(this, ScanActivity::class.java)
                    intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY)
                    startActivity(intent)
                } else {
                    val dialogFragment: DashboardDialogFragment = DashboardDialogFragment.newInstance(Constant.QuickMenu.QUICK_MENU_TAX_NOTICE_HISTORY)
                    this.supportFragmentManager.let {
                        dialogFragment.show(it, DashboardDialogFragment::class.java.simpleName)
                    }
                }
            }
            R.id.business_transaction_history -> {
                if (prefHelper.agentTypeCode == Constant.AgentTypeCode.LEA.name || prefHelper.agentTypeCode == Constant.AgentTypeCode.LEI.name
                        || prefHelper.agentTypeCode == Constant.AgentTypeCode.LES.name) {

                    val intent = Intent(this, TrackOnTransactionHistoryActivity::class.java)
                    startActivity(intent)

                } else if (MyApplication.getPrefHelper().allowParking == "Y") {
                    if (MyApplication.getPrefHelper().parkingPlaceID > 0) {
                        val intent = Intent(this, ScanActivity::class.java)
                        intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_TRANSACTION_HISTORY)
                        startActivity(intent)
                    } else {
                        showAlertDialog(getString(R.string.please_select_parking_place))
                    }

                } else {
                    val dialogFragment: DashboardDialogFragment = DashboardDialogFragment.newInstance(Constant.QuickMenu.QUICK_MENU_BUSINESS_TRANSACTION_HISTORY)
                    this.supportFragmentManager.let {
                        dialogFragment.show(it, DashboardDialogFragment::class.java.simpleName)
                    }
                }
            }
            R.id.parking_ticket_history -> {
                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_TICKET_HISTORY)
                startActivity(intent)
            }
            R.id.my_agents -> {
                val myAgents = Intent(this, MyAgentsActivity::class.java)
                startActivity(myAgents)
            }
            R.id.tasks -> {
                val myAgents = Intent(this, TaskActivity::class.java)
                startActivity(myAgents)
            }
            R.id.business_activation -> {
                val intent = Intent(this, BusinessMasterActivity::class.java)
                intent.putExtra(KEY_BUSINESS_MODE, Constant.BusinessMode.BusinessActivate)
                startActivity(intent)
            }
            R.id.business_summary -> {
                val intent = Intent(this, ScanActivity::class.java)
                if (prefHelper.agentTypeCode == Constant.AgentTypeCode.LEA.name || prefHelper.agentTypeCode == Constant.AgentTypeCode.LEI.name
                        || prefHelper.agentTypeCode == Constant.AgentTypeCode.LES.name) {
                    intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TRACKON_BUSINESS_SUMMARY)
                } else {
                    intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSINESS_SUMMARY)
                }
                startActivity(intent)
            }
            R.id.individual_tax_summary -> {
                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_SUMMARY)
                startActivity(intent)
            }
            R.id.property_tax_summary -> {
                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_SUMMARY)
                startActivity(intent)
            }
            R.id.land_tax_summary -> {
                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_LAND_TAX_SUMMARY)
                startActivity(intent)
            }
            R.id.location_tracker -> {
                val intent = Intent(this, LocationTrackerActivity::class.java)
                intent.putExtra(KEY_NAVIGATION_MENU, Constant.NavigationMenu.NAVIGATION_LOCATION)
                startActivity(intent)
            }

            R.id.business_location -> {
                val intent = Intent(this, BusinessSearchActivity::class.java)
                intent.putExtra(KEY_NAVIGATION_MENU, Constant.NavigationMenu.NAVIGATION_ONBOARDING)
                startActivity(intent)
            }

            R.id.location_complaints -> {
                val intent = Intent(this, ComplaintIncidentSearchActivity::class.java)
                intent.putExtra(KEY_NAVIGATION_MENU, Constant.NavigationMenu.NAVIGATION_COMPLAINT)
                startActivity(intent)
            }

            R.id.location_incidents -> {
                val intent = Intent(this, ComplaintIncidentSearchActivity::class.java)
                intent.putExtra(KEY_NAVIGATION_MENU, Constant.NavigationMenu.NAVIGATION_INCIDENT)
                startActivity(intent)
            }

            R.id.location_property -> {
                val intent = Intent(this, ComplaintIncidentSearchActivity::class.java)
                intent.putExtra(KEY_NAVIGATION_MENU, Constant.NavigationMenu.NAVIGATION_PROPERTY)
                startActivity(intent)
            }

            R.id.business_penalty_waive_off -> {
                if (prefHelper.agentTypeCode == Constant.AgentTypeCode.LEA.name || prefHelper.agentTypeCode == Constant.AgentTypeCode.LEI.name || prefHelper.agentTypeCode == Constant.AgentTypeCode.LES.name) {
                    val intent = Intent(this, ScanActivity::class.java)
                    intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_LAW_PENALTY_WAIVE_OFF)
                    startActivity(intent)
                } else if (MyApplication.getPrefHelper().allowParking == "Y") {
                    if (MyApplication.getPrefHelper().parkingPlaceID > 0) {
                        val intent = Intent(this, ScanActivity::class.java)
                        intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_PENALTY_WAIVE_OFF)
                        startActivity(intent)
                    } else {
                        showAlertDialog(getString(R.string.please_select_parking_place))
                    }

                } else {
                    val dialogFragment: DashboardDialogFragment = DashboardDialogFragment.newInstance(Constant.QuickMenu.QUICK_MENU_PENALTY_WAIVE_OFF)
                    this.supportFragmentManager.let {
                        dialogFragment.show(it, DashboardDialogFragment::class.java.simpleName)
                    }
                }
            }
            R.id.business_impoundment_list -> {
                if (prefHelper.agentTypeCode == Constant.AgentTypeCode.LEA.name || prefHelper.agentTypeCode == Constant.AgentTypeCode.LEI.name
                        || prefHelper.agentTypeCode == Constant.AgentTypeCode.LES.name) {

                    val intent = Intent(this, ImpoundmentListActivity::class.java)
                    startActivity(intent)

                }
            }

            R.id.my_subscriptions -> {
                val intent = Intent(this, LicenseActivity::class.java)
                startActivity(intent)
            }

            R.id.agents -> {
                val intent = Intent(this, AgentSearchActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT)
                startActivity(intent)
            }

            R.id.business -> {
                val intent = Intent(this, BusinessMasterActivity::class.java)
                startActivity(intent)
            }

            R.id.stock_in_report -> {
                val intent = Intent(this, StockInReportActivity::class.java)
                startActivity(intent)
            }

            R.id.inventory_status -> {
                val intent = Intent(this, InventoryStatusActivity::class.java)
                startActivity(intent)
            }
            R.id.outstanding_waive_off -> {
                val dialogFragment: DashboardDialogFragment = DashboardDialogFragment.newInstance(Constant.QuickMenu.QUICK_MENU_OUTSTANDING_WAIVE_OFF)
                this.supportFragmentManager.let {
                    dialogFragment.show(it, DashboardDialogFragment::class.java.simpleName)
                }
            }

            R.id.licenseRenewal -> {
                val intent = Intent(this, PendingLicenseRenewalActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL)
                startActivity(intent)
            }

            R.id.asset_onboarding -> {
                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING)
                startActivity(intent)
            }

            R.id.new_asset_booking -> {
                val intent = Intent(this, AssetBookingActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_CREATE_ASSET_BOOKING)
                startActivity(intent)
            }

            R.id.update_asset_booking -> {
                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING)
                startActivity(intent)
            }

            R.id.asset_assignment -> {
                val intent = Intent(this, AssetBookingAndReturnListActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT)
                startActivity(intent)
            }
            R.id.asset_return -> {
                val intent = Intent(this, AssetBookingAndReturnListActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_ASSET_RETURN)
                startActivity(intent)
            }
            R.id.service_request_master -> {
                val intent = Intent(this, ServiceActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SERVICE_REQUEST_MASTER)
                startActivity(intent)
            }

            R.id.update_asset -> {
                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET)
                startActivity(intent)
            }

            R.id.property_verification ->{
                val intent = Intent(this,PropertyVerificationActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_VERIFY_PROPERTY)
                startActivity(intent)
            }
            R.id.pending_services ->{
                val intent = Intent(this,PendingServiceInvoicesActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PENDING_SERVICE_REQUESTS)
                startActivity(intent)
            }
            R.id.sales_history ->{
                val intent = Intent(this,SalesHistoryActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SALES_TAX)
                startActivity(intent)
            }
            R.id.salesReport -> {
                val intent = Intent(this, SalesReportActivity::class.java)
                startActivity(intent)
            }
            R.id.cheque_repayments -> {
                val intent = Intent(this, SalesRepaymentActivity::class.java)
                startActivity(intent)
            }
            R.id.security_history ->{
                val intent = Intent(this,SalesHistoryActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX)
                startActivity(intent)
            }
            R.id.adjustments -> {
                val intent = Intent(this, AdjustmentsActivity::class.java)
                startActivity(intent)
            }
            R.id.stockTransfer ->{
                val intent = Intent(this, StockTransferActivity::class.java)
                startActivity(intent)
            }
            else -> {
                if (item.title.toString().endsWith(" Registration") && prefHelper.superiorTo.contains(item.title.toString().replace(" Registration", ""))) {
                    val intent = Intent(this, AgentOnboardingActivity::class.java)
                    intent.putExtra(Constant.KEY_AGENT_TYPE, item.title)
                    intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_AGENT)
                    startActivity(intent)
                }
            }

        }
        return false
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            showLogoutPopUp()
        }
    }

    private fun showLogoutPopUp() {
        showAlertDialog(getString(R.string.msg_logout), DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
            showProgressDialog()
            APICall.updateLogoutUser(object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    JedisUtil.cancelJedis();
                    dismissDialog()
                    dialog.dismiss()
                    finishAffinity()
                    startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                }

                override fun onFailure(message: String) {
                    dismissDialog()
                    showAlertDialog(message)
                }
            })
        }, null)
    }

    private fun setEventListeners() {
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navigation.setNavigationItemSelectedListener(this)

        locationHelper?.setListener(this)
        locationHelper?.fetchLocation()
    }

    private fun initViews() {
        locationHelper = LocationHelper(this, binding.container, activity = this)
    }

    private fun setNavigationMenu() {
        try {
            val menu: Menu = binding.navigation.menu

            if (prefHelper.agentTypeCode == Constant.AgentTypeCode.ISP.name) {
                //Reports
                menu.setGroupVisible(R.id.group_reports, true)
                menu.findItem(R.id.agent_collection_histroy).isVisible = true
                menu.findItem(R.id.tax_notice_history).isVisible = true
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = true
                menu.findItem(R.id.credit_balance_statement).isVisible = false
                menu.findItem(R.id.agent_commission_report).isVisible = true
                menu.findItem(R.id.cash_deposit_approval).isVisible = true
                menu.findItem(R.id.agent_summary_details).isVisible = true
                menu.findItem(R.id.agent_collection_summary).isVisible = true
                menu.findItem(R.id.business_penalty_waive_off).isVisible = true
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.outstanding_waive_off).isVisible = true
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = false

                //Master
                menu.setGroupVisible(R.id.group_master, true)
                menu.findItem(R.id.business_summary).isVisible = true
                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.my_agents).isVisible = true
                menu.findItem(R.id.tax_payer_registration).isVisible = true
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false
                menu.findItem(R.id.business_activation).isVisible = prefHelper.IsApprover.equals("Y")

                //GeoSpatialData
                menu.setGroupVisible(R.id.group_geo_spatial_data, true)
                menu.findItem(R.id.location_tracker).isVisible = true
                menu.findItem(R.id.business_location).isVisible = true
                menu.findItem(R.id.location_complaints).isVisible = true
                menu.findItem(R.id.location_incidents).isVisible = true

                menu.findItem(R.id.my_subscriptions).isVisible = false
                menu.findItem(R.id.verification).isVisible = true
                menu.findItem(R.id.agents).isVisible = true

            } else if (prefHelper.agentTypeCode == Constant.AgentTypeCode.SPR.name) {
                //Reports
                menu.setGroupVisible(R.id.group_reports, true)
                menu.findItem(R.id.agent_collection_histroy).isVisible = true
                menu.findItem(R.id.tax_notice_history).isVisible = true
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = true
                menu.findItem(R.id.credit_balance_statement).isVisible = false
                menu.findItem(R.id.agent_commission_report).isVisible = true
                menu.findItem(R.id.cash_deposit_approval).isVisible = true
                menu.findItem(R.id.agent_summary_details).isVisible = true
                menu.findItem(R.id.agent_collection_summary).isVisible = true
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = false
                //Commented line - 12 Sep 21, as Supervisour not able to see the parking ticket
                //menu.findItem(R.id.parking_ticket_history).isVisible = true
                //Master
                menu.setGroupVisible(R.id.group_master, true)
                menu.findItem(R.id.business_summary).isVisible = true
                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.my_agents).isVisible = true
                menu.findItem(R.id.tax_payer_registration).isVisible = true
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false
                menu.findItem(R.id.business_activation).isVisible = prefHelper.IsApprover.equals("Y")

                //GeoSpatialData
                menu.setGroupVisible(R.id.group_geo_spatial_data, true)
                menu.findItem(R.id.location_tracker).isVisible = true
                menu.findItem(R.id.business_location).isVisible = true
                menu.findItem(R.id.location_complaints).isVisible = true
                menu.findItem(R.id.location_incidents).isVisible = true

                menu.findItem(R.id.my_subscriptions).isVisible = false
                menu.findItem(R.id.verification).isVisible = true
                menu.findItem(R.id.agents).isVisible = true

            } else if (prefHelper.agentTypeCode == Constant.AgentTypeCode.MCA.name) {
                //Reports
                menu.setGroupVisible(R.id.group_reports, true)
                menu.findItem(R.id.agent_collection_histroy).isVisible = true
                menu.findItem(R.id.tax_notice_history).isVisible = true
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = true
                menu.findItem(R.id.credit_balance_statement).isVisible = false
                menu.findItem(R.id.agent_commission_report).isVisible = false
                menu.findItem(R.id.cash_deposit_approval).isVisible = true
                menu.findItem(R.id.agent_summary_details).isVisible = false
                menu.findItem(R.id.agent_collection_summary).isVisible = false
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = false

                //Master
                menu.setGroupVisible(R.id.group_master, true)
                menu.findItem(R.id.business_summary).isVisible = true
                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.my_agents).isVisible = false
                menu.findItem(R.id.tax_payer_registration).isVisible = true
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false

                //GeoSpatialData
                menu.setGroupVisible(R.id.group_geo_spatial_data, true)
                menu.findItem(R.id.location_tracker).isVisible = true
                menu.findItem(R.id.business_location).isVisible = true
                menu.findItem(R.id.location_complaints).isVisible = true
                menu.findItem(R.id.location_incidents).isVisible = true

                menu.findItem(R.id.my_subscriptions).isVisible = false
                menu.findItem(R.id.verification).isVisible = true

            } else if (prefHelper.isThirdPartyAgent() || prefHelper.agentTypeCode == Constant.AgentTypeCode.ASA.name) {
                //Reports
                menu.setGroupVisible(R.id.group_reports, true)
                menu.findItem(R.id.agent_collection_histroy).isVisible = true
                menu.findItem(R.id.tax_notice_history).isVisible = true
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = true
                menu.findItem(R.id.credit_balance_statement).isVisible = true
                menu.findItem(R.id.agent_commission_report).isVisible = true
                menu.findItem(R.id.cash_deposit_approval).isVisible = false
                menu.findItem(R.id.agent_summary_details).isVisible = false
                menu.findItem(R.id.agent_collection_summary).isVisible = false
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = false

                //Master
                menu.setGroupVisible(R.id.group_master, true)
                menu.findItem(R.id.business_summary).isVisible = true
                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.my_agents).isVisible = false
                menu.findItem(R.id.tax_payer_registration).isVisible = true
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false

                //GeoSpatialData
                menu.setGroupVisible(R.id.group_geo_spatial_data, true)
                menu.findItem(R.id.location_tracker).isVisible = true
                menu.findItem(R.id.business_location).isVisible = true
                menu.findItem(R.id.location_complaints).isVisible = true
                menu.findItem(R.id.location_incidents).isVisible = true

                menu.findItem(R.id.my_subscriptions).isVisible = false
                menu.findItem(R.id.verification).isVisible = true

            }
            // true -> collection Owner
            else if (prefHelper.agentTypeCode == Constant.AgentTypeCode.PPS.name && prefHelper.isAdminUser) {
                menu.findItem(R.id.credit_balance_statement).isVisible = true
                menu.findItem(R.id.agent_collection_histroy).isVisible = true
                menu.findItem(R.id.agent_summary_details).isVisible = false
                menu.findItem(R.id.agent_collection_summary).isVisible = false
                menu.findItem(R.id.cash_deposit_approval).isVisible = false
                menu.findItem(R.id.agent_commission_report).isVisible = true
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.tax_notice_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = false
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = false

                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.tax_payer_registration).isVisible = false
                menu.findItem(R.id.business_summary).isVisible = false
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false
                menu.findItem(R.id.my_agents).isVisible = false

                menu.findItem(R.id.location_tracker).isVisible = false
                menu.findItem(R.id.business_location).isVisible = false
                menu.findItem(R.id.location_complaints).isVisible = false
                menu.findItem(R.id.location_incidents).isVisible = false

                menu.findItem(R.id.my_subscriptions).isVisible = true


                menu.setGroupVisible(R.id.group_reports, true)
                menu.setGroupVisible(R.id.group_master, true)

                menu.setGroupVisible(R.id.group_geo_spatial_data, false)
                menu.findItem(R.id.verification).isVisible = false

            } else if (prefHelper.agentTypeCode == Constant.AgentTypeCode.PPS.name && !prefHelper.isAdminUser) {
                menu.findItem(R.id.credit_balance_statement).isVisible = false
                menu.findItem(R.id.agent_collection_histroy).isVisible = true
                menu.findItem(R.id.agent_summary_details).isVisible = false
                menu.findItem(R.id.agent_collection_summary).isVisible = false
                menu.findItem(R.id.cash_deposit_approval).isVisible = false
                menu.findItem(R.id.agent_commission_report).isVisible = false
                menu.findItem(R.id.tax_notice_history).isVisible = false
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = false
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = false

                menu.findItem(R.id.business_summary).isVisible = false
                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.my_agents).isVisible = false
                menu.findItem(R.id.tax_payer_registration).isVisible = false
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false

                menu.findItem(R.id.location_tracker).isVisible = false
                menu.findItem(R.id.business_location).isVisible = false
                menu.findItem(R.id.location_complaints).isVisible = false
                menu.findItem(R.id.location_incidents).isVisible = false

                menu.findItem(R.id.my_subscriptions).isVisible = true

                menu.setGroupVisible(R.id.group_reports, true)
                menu.setGroupVisible(R.id.group_master, true)
                menu.setGroupVisible(R.id.group_geo_spatial_data, false)
                menu.findItem(R.id.verification).isVisible = false


            } else if (prefHelper.agentTypeCode == Constant.AgentTypeCode.ASO.name) {

                menu.findItem(R.id.agent_collection_histroy).isVisible = false
                menu.findItem(R.id.tax_notice_history).isVisible = false
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = false
                menu.findItem(R.id.credit_balance_statement).isVisible = false
                menu.findItem(R.id.agent_commission_report).isVisible = true
                menu.findItem(R.id.cash_deposit_approval).isVisible = false
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = false

                menu.findItem(R.id.agent_summary_details).isVisible = false
                menu.findItem(R.id.agent_collection_summary).isVisible = false


                menu.findItem(R.id.business_summary).isVisible = false
                menu.findItem(R.id.products).isVisible = false
                menu.findItem(R.id.my_agents).isVisible = true
                menu.findItem(R.id.tax_payer_registration).isVisible = false
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false

                menu.findItem(R.id.location_tracker).isVisible = false
                menu.findItem(R.id.business_location).isVisible = false
                menu.findItem(R.id.location_complaints).isVisible = false
                menu.findItem(R.id.location_incidents).isVisible = false

                menu.findItem(R.id.my_subscriptions).isVisible = false

                menu.setGroupVisible(R.id.group_reports, true)
                menu.setGroupVisible(R.id.group_master, true)
                menu.setGroupVisible(R.id.group_geo_spatial_data, false)
                menu.findItem(R.id.verification).isVisible = false

            }
            //Commentted this code on 31-05-21 as the ASA & TPA is having same option hence additional condition written in TPA
            /*else if (prefHelper.agentTypeCode == Constant.AgentTypeCode.ASA.name) {

                menu.findItem(R.id.agent_collection_histroy).isVisible = true
                menu.findItem(R.id.tax_notice_history).isVisible = true
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = true
                menu.findItem(R.id.credit_balance_statement).isVisible = true
                menu.findItem(R.id.agent_commission_report).isVisible = true
                menu.findItem(R.id.cash_deposit_approval).isVisible = false
                menu.findItem(R.id.agent_summary_details).isVisible = false
                menu.findItem(R.id.agent_collection_summary).isVisible = false
                menu.findItem(R.id.stock_in_report).isVisible = false

                menu.findItem(R.id.business_summary).isVisible = false
                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.my_agents).isVisible = false
                menu.findItem(R.id.tax_payer_registration).isVisible = true
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false

                menu.setGroupVisible(R.id.group_geo_spatial_data, true)
                menu.findItem(R.id.business_location).isVisible = true
                menu.findItem(R.id.location_tracker).isVisible = false
                menu.findItem(R.id.location_complaints).isVisible = false
                menu.findItem(R.id.location_incidents).isVisible = false

                menu.findItem(R.id.my_subscriptions).isVisible = false

                menu.setGroupVisible(R.id.group_reports, true)
                menu.setGroupVisible(R.id.group_master, true)

                menu.findItem(R.id.verification).isVisible = false

            }*/ else if (prefHelper.agentTypeCode == Constant.AgentTypeCode.LEA.name
                    || prefHelper.agentTypeCode == Constant.AgentTypeCode.LEI.name
                    || prefHelper.agentTypeCode == Constant.AgentTypeCode.LES.name) {


                //Reports
                menu.setGroupVisible(R.id.group_reports, true)
                menu.findItem(R.id.agent_collection_histroy).isVisible = true
                menu.findItem(R.id.tax_notice_history).title = getString(R.string.label_ticket_history)
                menu.findItem(R.id.tax_notice_history).isVisible = true
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = true
                menu.findItem(R.id.pending_violations).isVisible = true
                menu.findItem(R.id.credit_balance_statement).isVisible = true
                menu.findItem(R.id.agent_commission_report).isVisible = true
                menu.findItem(R.id.cash_deposit_approval).isVisible = true
                menu.findItem(R.id.agent_summary_details).isVisible = true
                menu.findItem(R.id.agent_collection_summary).isVisible = true
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.licenseRenewal).isVisible = false
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = true


                if (prefHelper.agentTypeCode == Constant.AgentTypeCode.LEA.name){
                    menu.findItem(R.id.agent_summary_details).isVisible = false
                    menu.findItem(R.id.agent_collection_summary).isVisible = false
                }

                if (!prefHelper.agentIsPrepaid)
                {
                    menu.findItem(R.id.agent_commission_report).isVisible = false
                    menu.findItem(R.id.credit_balance_statement).isVisible = false
                }

                /*
                Penalty waive
                LE Inspector it is required
                LE Agent not required
                LE Supervisor not required
                */
                if (prefHelper.agentTypeCode == Constant.AgentTypeCode.LEI.name) {
                    menu.findItem(R.id.business_penalty_waive_off).isVisible = true
                } else {
                    menu.findItem(R.id.business_penalty_waive_off).isVisible = false
                }

                menu.findItem(R.id.business_impoundment_list).isVisible = true



                //Master
                menu.setGroupVisible(R.id.group_master, true)
                menu.findItem(R.id.business_summary).title = getString(R.string.vehicle_summary)
                menu.findItem(R.id.business_summary).isVisible = true
                menu.findItem(R.id.products).title = getString(R.string.law_enforcement_taxes)
                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.my_agents).isVisible = false
                menu.findItem(R.id.tax_payer_registration).isVisible = false
                menu.findItem(R.id.individual_tax_summary).isVisible = false
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false

                menu.findItem(R.id.property_tax_summary).isVisible=false
                menu.findItem(R.id.land_tax_summary).isVisible=false

                menu.findItem(R.id.cheque_repayments).isVisible = true

                //GeoSpatialData
                menu.setGroupVisible(R.id.group_geo_spatial_data, true)
                menu.findItem(R.id.location_tracker).isVisible = true
                menu.findItem(R.id.business_location).isVisible = false
                menu.findItem(R.id.location_property).isVisible = false
                menu.findItem(R.id.location_complaints).isVisible = true
                menu.findItem(R.id.location_incidents).isVisible = true

                menu.findItem(R.id.my_subscriptions).isVisible = false
                menu.findItem(R.id.verification).isVisible = false
                menu.findItem(R.id.agents).isVisible = false
            }

            if (prefHelper.agentAllowSales == "Y") {
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.inventory_status).isVisible = true
                menu.findItem(R.id.asset_onboarding).isVisible = true
                menu.findItem(R.id.service_request_master).isVisible = true
                menu.findItem(R.id.pending_services).isVisible = true
                menu.findItem(R.id.adjustments).isVisible = true
                menu.findItem(R.id.stockTransfer).isVisible = true
                menu.findItem(R.id.sales_history).isVisible = true
                menu.findItem(R.id.salesReport).isVisible = true
                menu.findItem(R.id.cheque_repayments).isVisible = true
                if (prefHelper.agentTypeCode != Constant.AgentTypeCode.PPS.name)
                    menu.findItem(R.id.security_history).isVisible = true
            }
            if (prefHelper.agentTypeCode == Constant.AgentTypeCode.SLA.name) {
                //Reports
                menu.setGroupVisible(R.id.group_reports, true)
                menu.findItem(R.id.agent_collection_histroy).isVisible = true
                menu.findItem(R.id.tax_notice_history).isVisible = false
                menu.findItem(R.id.parking_ticket_history).isVisible = false
                menu.findItem(R.id.business_transaction_history).isVisible = false
                menu.findItem(R.id.credit_balance_statement).isVisible = false
                menu.findItem(R.id.agent_commission_report).isVisible = false
                menu.findItem(R.id.cash_deposit_approval).isVisible = true
                menu.findItem(R.id.agent_summary_details).isVisible = false
                menu.findItem(R.id.agent_collection_summary).isVisible = false
                menu.findItem(R.id.business_penalty_waive_off).isVisible = false
                menu.findItem(R.id.stock_in_report).isVisible = false
                menu.findItem(R.id.outstanding_waive_off).isVisible = false
                menu.findItem(R.id.licenseRenewal).isVisible = false

                //Master
                menu.setGroupVisible(R.id.group_master, true)
                menu.findItem(R.id.business_summary).isVisible = false
                menu.findItem(R.id.individual_tax_summary).isVisible = false
                menu.findItem(R.id.property_tax_summary).isVisible = false
                menu.findItem(R.id.land_tax_summary).isVisible = false
                menu.findItem(R.id.products).isVisible = false
                menu.findItem(R.id.my_agents).isVisible = false
                menu.findItem(R.id.asset_onboarding).isVisible = false
                menu.findItem(R.id.service_request_master).isVisible = true
                menu.findItem(R.id.pending_services).isVisible = true
                menu.findItem(R.id.tax_payer_registration).isVisible = false
                menu.findItem(R.id.incident_management).isVisible = true
                menu.findItem(R.id.tasks).isVisible = true
                menu.findItem(R.id.tax_payer_verification).isVisible = false

                //GeoSpatialData
                menu.setGroupVisible(R.id.group_geo_spatial_data, true)
                menu.findItem(R.id.location_tracker).isVisible = false
                menu.findItem(R.id.business_location).isVisible = false
                menu.findItem(R.id.location_complaints).isVisible = true
                menu.findItem(R.id.location_incidents).isVisible = true
                menu.findItem(R.id.location_property).isVisible = false

                menu.findItem(R.id.my_subscriptions).isVisible = false
                menu.findItem(R.id.verification).isVisible = false
                menu.findItem(R.id.agents).isVisible = false

            }

         /*   Asset condition*/
            //if((prefHelper.agentTypeCode == Constant.AgentTypeCode.MCA.name && prefHelper.agentIsPrepaid)||

                    if(prefHelper.agentTypeCode == Constant.AgentTypeCode.MCA.name||
                    prefHelper.agentTypeCode == Constant.AgentTypeCode.ISP.name ||
                    prefHelper.agentTypeCode == Constant.AgentTypeCode.SPR.name||
                    prefHelper.isThirdPartyAgent()||prefHelper.agentTypeCode == Constant.AgentTypeCode.ASA.name){
                menu.findItem(R.id.update_asset).isVisible = true
                menu.findItem(R.id.new_asset_booking).isVisible = true
                menu.findItem(R.id.update_asset_booking).isVisible = true
                menu.findItem(R.id.asset_assignment).isVisible = true
                menu.findItem(R.id.asset_return).isVisible = true
            }
            //if (prefHelper.allowParking == "Y" || prefHelper.isParkingThirdPartyAgent()) {
            if (prefHelper.isParkingThirdPartyAgent()) {
                menu.findItem(R.id.products).title = getString(R.string.title_parking_taxes)
                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.business_penalty_waive_off).isVisible = true
                menu.findItem(R.id.parking_ticket_history).isVisible = true
                menu.findItem(R.id.my_agents).isVisible = false
                menu.findItem(R.id.tax_payer_registration).isVisible = false
                menu.findItem(R.id.individual_tax_summary).isVisible = false
                menu.findItem(R.id.business_location).isVisible = false
                menu.findItem(R.id.location_property).isVisible = false
                menu.findItem(R.id.licenseRenewal).isVisible = false


                menu.findItem(R.id.my_subscriptions).isVisible = false
                menu.findItem(R.id.verification).isVisible = false
                menu.findItem(R.id.agents).isVisible = false
                menu.findItem(R.id.cash_deposit_approval).isVisible = false
                menu.findItem(R.id.tax_notice_history).isVisible=false
                menu.findItem(R.id.business_summary).isVisible=false
                menu.findItem(R.id.property_tax_summary).isVisible=false
                menu.findItem(R.id.land_tax_summary).isVisible=false
                menu.findItem(R.id.agent_summary_details).isVisible=false
                menu.findItem(R.id.agent_collection_summary).isVisible=false
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = false


                if (!prefHelper.agentIsPrepaid) {
                    menu.findItem(R.id.agent_commission_report).isVisible = false
                    menu.findItem(R.id.credit_balance_statement).isVisible = false
                    //  menu.findItem(R.id.agent_collection_summary).isVisible=false
                   // menu.findItem(R.id.agent_summary_details).isVisible = false
                }else {
                    menu.findItem(R.id.agent_commission_report).isVisible = true
                    menu.findItem(R.id.credit_balance_statement).isVisible = true
                }
            }
            if (prefHelper.isParkingMunicipalAgent()) {
                menu.findItem(R.id.products).title = getString(R.string.title_parking_taxes)
                menu.findItem(R.id.products).isVisible = true
                menu.findItem(R.id.business_penalty_waive_off).isVisible = true
                menu.findItem(R.id.parking_ticket_history).isVisible = true
                menu.findItem(R.id.my_agents).isVisible = false
                menu.findItem(R.id.tax_payer_registration).isVisible = false
                menu.findItem(R.id.individual_tax_summary).isVisible = false
                menu.findItem(R.id.business_location).isVisible = false
                menu.findItem(R.id.my_subscriptions).isVisible = false
                menu.findItem(R.id.verification).isVisible = false
                menu.findItem(R.id.agents).isVisible = false
                menu.findItem(R.id.agent_commission_report).isVisible = false
                menu.findItem(R.id.agent_summary_details).isVisible = false
                menu.findItem(R.id.credit_balance_statement).isVisible = false
                menu.findItem(R.id.agent_collection_summary).isVisible = false
                menu.findItem(R.id.tax_notice_history).isVisible=false
                menu.findItem(R.id.business_summary).isVisible=false
                menu.findItem(R.id.property_tax_summary).isVisible=false
                menu.findItem(R.id.land_tax_summary).isVisible=false
                menu.findItem(R.id.agent_summary_details).isVisible=false
                menu.findItem(R.id.agent_collection_summary).isVisible=false
                menu.findItem(R.id.location_property).isVisible = false
                menu.findItem(R.id.licenseRenewal).isVisible = false
                menu.findItem(R.id.sales_history).isVisible = false
                menu.findItem(R.id.salesReport).isVisible = false
                menu.findItem(R.id.security_history).isVisible = false

                if (!prefHelper.agentIsPrepaid) {
                    menu.findItem(R.id.agent_collection_summary).isVisible = false
                    menu.findItem(R.id.agent_commission_report).isVisible = false
                    menu.findItem(R.id.credit_balance_statement).isVisible = false
                    menu.findItem(R.id.agent_summary_details).isVisible = false
                }

            }

        } catch (e: java.lang.Exception) {
            showSnackbarMsg(e.message)
        }
    }


    private fun showDashboard() {
        addFragmentWithOutAnimation(DashboardFragment.newInstance(), true, R.id.container)
    }

    override fun onClick() {
    }

    override fun onReloadClick() {
        val list = supportFragmentManager.fragments
        for (f in list) {
            if (f is MapFragment) {
                f.getBusinessLocations(true)
                break
            }
        }
    }
}