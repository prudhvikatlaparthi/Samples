package com.sgs.citytax.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityPropertyVerificationBinding
import com.sgs.citytax.model.COMPropertyTypes
import com.sgs.citytax.model.PropertyDetailLocation
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper


class PropertyVerificationActivity : BaseActivity(), PropertyPendingListFragment.Listener,
        ParentPropertyPlanImageFragment.Listener,
        PropertyHistoryMasterFragment.Listener,
        PropertyDocumentVerificationFragment.Listener,
        PropertyTaxEntryFragment.Listener,
        LandTaxEntryFragment.Listener,
        LocationHelper.Location,
        PropertyImageMasterFragment.Listener,
        PropertyImageEntryFragment.Listener,
        PropertyPlanImageMasterFragment.Listener,
        PropertyPlanImageEntryFragment.Listener,
        LocalDocumentsMasterFragment.Listener,
        LocalDocumentEntryFragment.Listener,
        LocalNotesMasterFragment.Listener,
        LocalNotesEntryFragment.Listener,
        PropertyOwnerMasterFragment.Listener,
        PropertyOwnerEntryFragment.Listener,
        PropertyOwnerOnBoardFragment.Listener,
        BusinessOwnerEntryFragment.Listener,
        BusinessOwnerSearchFragment.Listener,
        DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener,
        NotesMasterFragment.Listener,
        NotesEntryFragment.Listener,
        OutstandingsMasterFragment.Listener,
        OutstandingEntryFragment.Listener,
        PropertyVerificationReceiptFragment.Listener,
        PropertyFilterDialogFragment.Listener {

    private lateinit var mBinding: ActivityPropertyVerificationBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD

    private var propertyLocations: MutableList<COMPropertyTypes> = arrayListOf()
    private var propertyLocation: PropertyDetailLocation? = null
    val fragment = PropertyPendingListFragment()

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_property_verification)
        showToolbarBackButton(R.string.title_property_verification)

        //region Arguments
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        //endregion

        attachFragment()
        getPropertyLocations()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_search_business, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            showToolbarBackButton(R.string.title_property_verification)
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
            showSearchOption(currentFragment is PropertyPendingListFragment)
        }
        if (item.itemId == R.id.action_search) {
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_VERIFY_PROPERTY) {
                val dialogFragment: PropertyFilterDialogFragment = PropertyFilterDialogFragment.newInstance(propertyLocations, propertyLocation)
                this.supportFragmentManager.let {
                    dialogFragment.show(it, PropertyFilterDialogFragment::class.java.simpleName)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        when(currentFragment){
            is LandTaxEntryFragment ->{
                showToolbarBackButton(R.string.title_property_verification)
            }
            is PropertyTaxEntryFragment->{
                showToolbarBackButton(R.string.title_property_verification)
            }
        }
        showSearchOption(currentFragment is PropertyPendingListFragment)
    }

    private fun attachFragment() {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        fragment.arguments = bundle
        addFragment(fragment, false)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, addToBackStack, R.id.container)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

    override fun start() {
        showProgressDialog()
    }

    override fun found(latitude: Double, longitude: Double) {
        val latLong = LatLng(latitude, longitude)
        addFragmentWithOutAnimation(GeoFenceMapFragment(), true, R.id.map)
        dismissDialog()
    }

    private fun getPropertyLocations() {
        showProgressDialog()
        /* APICall.getPropertyLocations(object : ConnectionCallBack<PropertyLocations> {
             override fun onSuccess(response: PropertyLocations) {
                 dismissDialog()
                 propertyLocations = response.propertyDetailLocations as ArrayList<PropertyDetailLocation>
               *//*  if (binding.rcvMapList.adapter == null)
                    binding.rcvMapList.adapter = PropertyMapAdapter(propertyLocations) { propertyDetailsLocation: PropertyDetailLocation -> itemPropertyClicked(propertyDetailsLocation) }*//*
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })*/

        APICall.getCorporateOfficeLOVValues("COM_PropertyVerificationRequests", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                propertyLocations = response.propertyTypesVU
                dismissDialog()
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })

    }

    override fun onApplyPropertyClick(location: PropertyDetailLocation?) {
        propertyLocation = location
        var isSelected = false

        if (propertyLocation != null && propertyLocations.isNotEmpty()) {
            fragment.filterData(location)
        }
    }

    override fun onClearPropertyClick() {
        propertyLocation = null
    }

    override fun showSearchOption(status: Boolean) {
        if (this.menu != null) {
            this.menu!!.findItem(R.id.action_search)?.isVisible = status
        }
    }

}