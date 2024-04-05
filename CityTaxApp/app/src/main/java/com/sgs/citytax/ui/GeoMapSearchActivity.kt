package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityGeoMapSearchBinding
import com.sgs.citytax.model.GeoFenceLatLong
import com.sgs.citytax.ui.fragments.GeoFenceMapFragment
import com.sgs.citytax.ui.fragments.MapFragment
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper


class GeoMapSearchActivity : BaseActivity(), MapFragment.Listener,FragmentCommunicator {

    private lateinit var binding: ActivityGeoMapSearchBinding
    private var fromScreen: Constant.NavigationMenu? = null
    private var mGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mParentGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mLandGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var locationHelper: LocationHelper? = null
    var mScreenMode = Constant.ScreenMode.ADD
    var mFromScreen=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_geo_map_search)
        showToolbarBackButton(R.string.geo_location_area)
        processIntent()
        initViews()
        setViews()
        getLocation()
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.NavigationMenu

            if (intent.hasExtra(Constant.KEY_GEO_AREA_LATLONG))
                mGeoFenceLatLong = intent.getParcelableArrayListExtra<GeoFenceLatLong>(Constant.KEY_GEO_AREA_LATLONG)

            if (intent.hasExtra(Constant.KEY_PARENT_GEO_AREA_LATLONG))
                mParentGeoFenceLatLong = intent.getParcelableArrayListExtra<GeoFenceLatLong>(Constant.KEY_PARENT_GEO_AREA_LATLONG)

            if (intent.hasExtra(Constant.KEY_LAND_GEO_AREA_LATLONG))
                mLandGeoFenceLatLong = intent.getParcelableArrayListExtra<GeoFenceLatLong>(Constant.KEY_LAND_GEO_AREA_LATLONG)

            if (intent.hasExtra(Constant.KEY_FROM_SCREEN))
                mFromScreen = intent.getStringExtra(Constant.KEY_FROM_SCREEN)


        }
    }

    private fun initViews() {
        binding.rcvMapList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    private fun setViews() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        if (item.itemId == R.id.action_search) {
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateMarker() {
        val fragment = supportFragmentManager.findFragmentById(R.id.GeoMapSearchContainer) as GeoFenceMapFragment
        fragment.updateGeoMarkersList(mGeoFenceLatLong)

    }

    private fun getLocation() {
        showProgressDialog(R.string.msg_location_fetching)
        locationHelper = LocationHelper(this, binding.rcvMapList, this)
        locationHelper?.fetchLocation()
        locationHelper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                dismissDialog()
                val latLong = LatLng(latitude, longitude)
                addFragment(GeoFenceMapFragment.newInstance(mGeoFenceLatLong, mParentGeoFenceLatLong,mLandGeoFenceLatLong,mFromScreen), false, R.id.GeoMapSearchContainer)
            }

            override fun start() {
            }
        })
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {

    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {mScreenMode = value}

    override fun onReloadClick() {
    }
}