package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityLocationTrackerBinding
import com.sgs.citytax.ui.fragments.AgentInfoDialogFragment
import com.sgs.citytax.ui.fragments.BusinessInfoDialogFragment
import com.sgs.citytax.ui.fragments.LocateAgentFragment
import com.sgs.citytax.ui.fragments.MapFragment
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper
import java.util.ArrayList


class LocationTrackerActivity : BaseActivity(), LocateAgentFragment.Listener, MapFragment.Listener,
    LocationHelper.Location, BusinessInfoDialogFragment.Listener, AgentInfoDialogFragment.Listener {
    private lateinit var binding: ActivityLocationTrackerBinding
    private var locationHelper: LocationHelper? = null
    private var fromScreen: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_tracker)
        processIntent()
        setToolBarTitle()
        initViews()
        locationHelper?.setListener(this)
        locationHelper?.fetchLocation()
    }

    private fun setToolBarTitle() {
        when (fromScreen) {
            Constant.NavigationMenu.NAVIGATION_LOCATION -> {
                showToolbarBackButton(R.string.title_location_tracker)
            }
            Constant.NavigationMenu.NAVIGATION_COMPLAINT -> {
                showToolbarBackButton(R.string.title_complaints_location)
            }
            Constant.NavigationMenu.NAVIGATION_ONBOARDING -> {
                showToolbarBackButton(R.string.title_business_location)
            }
            else -> {
                showToolbarBackButton(R.string.title_incident_location)
            }
        }
    }

    private fun initViews() {
        locationHelper = LocationHelper(this, binding.locationTrackerContainer, activity = this)
    }

    private fun processIntent() {
        intent.let {
            if (it.hasExtra(Constant.KEY_NAVIGATION_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_NAVIGATION_MENU)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun start() {
        showProgressDialog()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun found(latitude: Double, longitude: Double) {
        val latLong = LatLng(latitude, longitude)
        if (fromScreen == Constant.NavigationMenu.NAVIGATION_ONBOARDING) {
            addFragmentWithOutAnimation(MapFragment.newInstance(latLong), true, R.id.locationTrackerContainer)
        } else {
            addFragmentWithOutAnimation(LocateAgentFragment.newInstance(latLong, fromScreen!!), true, R.id.locationTrackerContainer)
        }
        dismissDialog()
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, true, R.id.locationTrackerContainer)
    }

    override fun updateData(any: ArrayList<*>, navigation: Constant.NavigationMenu) {

    }

    override fun onClick() {
    }

    override fun onReloadClick() {
    }
}