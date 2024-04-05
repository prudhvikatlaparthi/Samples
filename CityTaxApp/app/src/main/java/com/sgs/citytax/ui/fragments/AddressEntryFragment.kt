package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentAddressEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper
import java.util.*

class AddressEntryFragment : BaseFragment() {

    private lateinit var mBinding: FragmentAddressEntryBinding
    private var mListener: Listener? = null
    private lateinit var mHelper: LocationHelper
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mPrimaryKey: Int? = 0
private var mString:String?=null
    private var mGeoAddress: GeoAddress? = null
    private var mResponseCountriesList: List<COMCountryMaster> = ArrayList()
    private var mResponseStatesList: List<COMStateMaster> = ArrayList()
    private var mResponseCitiesList: List<VUCOMCityMaster> = ArrayList()
    private var mResponseZonesList: List<COMZoneMaster> = ArrayList()
    private var mResponseSectorsList: List<COMSectors> = ArrayList()
    private var isActionEnabled: Boolean = true

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPrimaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY)
            mGeoAddress = arguments?.getParcelable(Constant.KEY_ADDRESS)
        }
        setViews()
        bindSpinners()
        setListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_address_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        mHelper.disconnect()
        super.onDetach()
    }

    private fun setViews() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {
        isActionEnabled = action
        mBinding.spnCountry.isEnabled = action
        mBinding.spnState.isEnabled = action
        mBinding.spnCity.isEnabled = action
        mBinding.spnZone.isEnabled = action
        mBinding.spnSector.isEnabled = action
        mBinding.edtStreet.isEnabled = action
        mBinding.edtPlot.isEnabled = action
        mBinding.edtBlock.isEnabled = action
        mBinding.edtDoorNo.isEnabled = action
        mBinding.edtZipCode.isEnabled = action
        mBinding.edtDescription.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }

    private fun bindData() {
        if (mGeoAddress != null) {
            mBinding.edtStreet.setText(mGeoAddress!!.street)
            mBinding.edtZipCode.setText(mGeoAddress!!.zipCode)
            mBinding.edtPlot.setText(mGeoAddress!!.plot)
            mBinding.edtBlock.setText(mGeoAddress!!.block)
            mBinding.edtDoorNo.setText(mGeoAddress!!.doorNo)
            mBinding.edtDescription.setText(mGeoAddress!!.description)
            filterCountries()
        } else {
            mBinding.edtStreet.setText("")
            mBinding.edtZipCode.setText("")
            mBinding.edtPlot.setText("")
            mBinding.edtBlock.setText("")
            mBinding.edtDoorNo.setText("")
            mBinding.edtDescription.setText("")
        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener {
            if (validateFields()) {
                mHelper.fetchLocation()
                mHelper.setListener(object : LocationHelper.Location {
                    override fun found(latitude: Double, longitude: Double) {
                        mListener?.dismissDialog()
                        saveAddress(prepareData(latitude, longitude))

                    }

                    override fun start() {
                        mListener?.showProgressDialog(R.string.msg_location_fetching)
                    }
                })
            }
        }

        mBinding.spnCountry.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var country: COMCountryMaster? = COMCountryMaster()
                if (p0 != null && p0.selectedItem != null)
                    country = p0.selectedItem as COMCountryMaster
                filterStates(country?.countryCode)
            }
        }

        mBinding.spnState.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var state: COMStateMaster? = COMStateMaster()
                if (p0 != null && p0.selectedItem != null)
                    state = p0.selectedItem as COMStateMaster
                filterCities(state?.stateID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnCity.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var city: VUCOMCityMaster? = VUCOMCityMaster()
                if (p0 != null && p0.selectedItem != null)
                    city = p0.selectedItem as VUCOMCityMaster
                filterZones(city?.cityID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnZone.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        mHelper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
    }

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = ArrayList()
        var index = -1
        var countryCode: String? = ""
        if (mGeoAddress != null) countryCode = mGeoAddress!!.countryCode
        for (country in mResponseCountriesList) {
            countries.add(country)
            if (index <= -1 && countryCode != null && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode) index = countries.indexOf(country)
        }
        if (index <= -1) index = 0
        if (countries.size > 0) {
            val countryMasterArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, countries)
            mBinding.spnCountry.adapter = countryMasterArrayAdapter
            mBinding.spnCountry.setSelection(index)
            filterStates(countries[index].countryCode)
        } else {
            mBinding.spnCountry.adapter = null
            filterStates(countryCode)
        }
    }

    private fun filterStates(countryCode: String?) {
        var states: MutableList<COMStateMaster> = ArrayList()
        var index = -1
        var stateID = 100497
        if (mGeoAddress != null && mGeoAddress!!.stateID != null) stateID = mGeoAddress!!.stateID!!
        if (TextUtils.isEmpty(countryCode)) states = ArrayList() else {
            for (state in mResponseStatesList) {
                if (countryCode == state.countryCode) states.add(state)
                if (index <= -1 && stateID != 0 && state.stateID != null && stateID == state.stateID) index = states.indexOf(state)
            }
        }
        if (index <= -1) index = 0
        if (states.size > 0) {
            val stateArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, states)
            mBinding.spnState.adapter = stateArrayAdapter
            mBinding.spnState.setSelection(index)
            filterCities(states[index].stateID!!)
        } else {
            mBinding.spnState.adapter = null
            filterCities(stateID)
        }
    }

    private fun filterCities(stateID: Int) {
        var cities: MutableList<VUCOMCityMaster> = ArrayList()
        var index = -1
        var cityID = 100312093
        if (mGeoAddress != null && mGeoAddress!!.cityID != null) cityID = mGeoAddress!!.cityID!!
        if (stateID <= 0) cities = ArrayList() else {
            for (city in mResponseCitiesList) {
                if (city.stateID != null && stateID == city.stateID) cities.add(city)
                if (index <= 0 && cityID != 0 && city.cityID != null && cityID == city.cityID) index = cities.indexOf(city)
            }
        }
        if (index <= -1) index = 0
        if (cities.size > 0) {
            val cityArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)
            mBinding.spnCity.adapter = cityArrayAdapter
            mBinding.spnCity.setSelection(index)
            filterZones(cities[index].cityID!!)
        } else {
            mBinding.spnCity.adapter = null
            filterZones(cityID)
        }
    }

    private fun filterZones(cityID: Int) {
        var zones: MutableList<COMZoneMaster> = ArrayList()
        var index = 0
        var zoneName: String? = ""
        if (mGeoAddress != null && mGeoAddress!!.zone != null) zoneName = mGeoAddress!!.zone
        if (cityID <= 0) zones = ArrayList() else {
            for (zone in mResponseZonesList) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone) index = zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnZone.adapter = zoneArrayAdapter
            mBinding.spnZone.setSelection(index)
            filterSectors(zones[index].zoneID!!)
        } else {
            mBinding.spnZone.adapter = null
            filterSectors(0)
        }
    }

    private fun filterSectors(zoneID: Int) {
        var sectors: MutableList<COMSectors?> = ArrayList()
        var index = 0
        var sectorID = 0
        if (mGeoAddress != null && mGeoAddress!!.sectorID != null) sectorID = mGeoAddress!!.sectorID!!
        if (zoneID <= 0) sectors = ArrayList() else {
            for (sector in mResponseSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId) index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            if (isActionEnabled) mBinding.spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnSector.adapter = sectorArrayAdapter
            mBinding.spnSector.setSelection(index)
        } else {
            mBinding.spnSector.adapter = null
            mBinding.spnSector.isEnabled = false
        }
    }

    private fun bindSpinners() {
        mListener!!.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_AccountAddresses", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mResponseCountriesList = response.countryMaster
                mResponseStatesList = response.stateMaster
                mResponseCitiesList = response.cityMaster
                mResponseZonesList = response.zoneMaster
                mResponseSectorsList = response.sectors
                filterCountries()
                bindData()
                mListener!!.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnCountry.adapter = null
                mBinding.spnState.adapter = null
                mBinding.spnCity.adapter = null
                mBinding.spnZone.adapter = null
                mBinding.spnSector.adapter = null
                mListener!!.dismissDialog()
                mListener!!.showAlertDialog(message)
            }

        })
    }

    private fun prepareData(latitude: Double, longitude: Double): GeoAddress {
        val geoAddress = GeoAddress()
        if (mGeoAddress != null && mGeoAddress!!.addressID != null)
            geoAddress.addressID = mGeoAddress!!.addressID
        if (mGeoAddress != null && mGeoAddress!!.geoAddressID != null)
            geoAddress.geoAddressID = mGeoAddress!!.geoAddressID

        geoAddress.accountId = mPrimaryKey

        // region Spinner Data
        val countryMaster = mBinding.spnCountry.selectedItem as COMCountryMaster
        if (countryMaster.countryCode != null) {
            geoAddress.countryCode = countryMaster.countryCode
            geoAddress.country = countryMaster.country
        }
        val comStateMaster = mBinding.spnState.selectedItem as COMStateMaster
        if (comStateMaster.state != null) {
            geoAddress.state = comStateMaster.state
            geoAddress.stateID = comStateMaster.stateID
        }
        val comCityMaster = mBinding.spnCity.selectedItem as VUCOMCityMaster
        if (comCityMaster.city != null) geoAddress.city = comCityMaster.city
        val comZoneMaster = mBinding.spnZone.selectedItem as COMZoneMaster
        if (comZoneMaster.zone != null) geoAddress.zone = comZoneMaster.zone
        val comSectors = mBinding.spnSector.selectedItem as COMSectors
        if (comSectors.sectorId != null) geoAddress.sectorID = comSectors.sectorId
        // endregion
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) geoAddress.street = mBinding.edtStreet.text.toString().trim { it <= ' ' }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) geoAddress.zipCode = mBinding.edtZipCode.text.toString().trim { it <= ' ' }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) geoAddress.plot = mBinding.edtPlot.text.toString().trim { it <= ' ' }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) geoAddress.block = mBinding.edtBlock.text.toString().trim { it <= ' ' }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(mBinding.edtDoorNo.text.toString().trim { it <= ' ' })) geoAddress.doorNo = mBinding.edtDoorNo.text.toString().trim { it <= ' ' }
        if (mBinding.edtDescription.text != null && !TextUtils.isEmpty(mBinding.edtDescription.text.toString())) geoAddress.description = mBinding.edtDescription.text.toString().trim { it <= ' ' }
        geoAddress.latitude = "$latitude"
        geoAddress.longitude = "$longitude"

        return geoAddress
    }

    private fun saveAddress(address: GeoAddress) {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.saveGeoAddress(address, object : ConnectionCallBack<Int> {
                override fun onSuccess(response: Int) {
                    mListener!!.dismissDialog()
                    if (address.addressID != 0)
                        mListener!!.showSnackbarMsg(R.string.msg_record_update_success)
                    else
                        mListener!!.showSnackbarMsg(R.string.msg_record_save_success)
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 500)
                }

                override fun onFailure(message: String) {
                    mListener!!.dismissDialog()
                    mListener!!.showAlertDialog(message)
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            *//*if (mCode == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
                ObjectHolder.registerBusiness.addresses.add(address)

                Handler().postDelayed({
                    targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    mListener!!.popBackStack()
                }, 500)
            }*//*
        }*/
    }

    private fun validateFields(): Boolean {
        val comCityMaster = mBinding.spnCity.selectedItem as VUCOMCityMaster?
        if (comCityMaster?.city == null) {
            mListener?.showSnackbarMsg(resources.getString(R.string.validation_choose_city))
            mBinding.spnCity.requestFocus()
            return false
        }
        val comZoneMaster = mBinding.spnZone.selectedItem as COMZoneMaster?
        if (comZoneMaster?.zone == null) {
            mListener?.showSnackbarMsg(resources.getString(R.string.validation_choose_zone))
            mBinding.spnZone.requestFocus()
            return false
        }
        val comSectors = mBinding.spnSector.selectedItem as COMSectors?
        if (comSectors?.sectorId == null) {
            mListener?.showSnackbarMsg(resources.getString(R.string.validation_choose_sector))
            mBinding.spnSector.requestFocus()
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mHelper.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mHelper.onActivityResult(requestCode, resultCode)
    }

    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        var screenMode: Constant.ScreenMode

    }
}