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
import com.sgs.citytax.api.payload.CheckCurrentDue
import com.sgs.citytax.api.payload.InsertPropertyOwnership
import com.sgs.citytax.api.payload.SavePropertyOwnership
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentPropertyOwnerBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.serverFormatDateTimeInMilliSecond

class PropertyOwnerFragment : BaseFragment(), View.OnClickListener {

    private var mListener: Listener? = null
    private lateinit var mBinding: FragmentPropertyOwnerBinding
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS

    private var responseExistingPropertiesList: MutableList<VUCOMExistingProperties> = arrayListOf()
    private var responsePropertyTypesList: List<COMPropertyTypes> = arrayListOf()
    private var responseOrgBranchesList: List<UMXUserOrgBranches> = arrayListOf()
    private var responseParentPropertiesList: ArrayList<ParentProperty> = arrayListOf()
    private var responseMeasurementUnitsList: List<VUINVMeasurementUnits> = arrayListOf()
    private var responseGeoAddressList: MutableList<GeoAddress> = arrayListOf()
    private var responseCountriesList: List<COMCountryMaster> = arrayListOf()
    private var responseStatesList: List<COMStateMaster> = arrayListOf()
    private var responseCitiesList: List<VUCOMCityMaster> = arrayListOf()
    private var responseZonesList: List<COMZoneMaster> = arrayListOf()
    private var responseSectorsList: List<COMSectors> = arrayListOf()

    private var mPropertyOwnership: VUCRMPropertyOwnership? = null

    private lateinit var helper: LocationHelper
    private var mTaxRuleBookCode: String? = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else
                context as Listener
        } catch (e: Exception) {
            throw ClassCastException("$context must implement listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_owner, container, false)
        /*showViewsFirstTime()
        bindSpinners()*/
        initComponents()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        helper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
        helper.disconnect()
    }


    override fun initComponents() {

        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPropertyOwnership = arguments?.getParcelable(Constant.KEY_PROPERTY_OWNER)
            mTaxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)
        }

        setViews()
        bindSpinners()
        setListeners()
    }


    fun setViews() {
        /* if (mListener!!.isViewMode && MyApplication.getPrefHelper().superiorTo.isNotEmpty()) {
             mBinding.spnExistingPropertyCode.isEnabled = false
             mBinding.edtPropertyName.isEnabled = false
             mBinding.spnPropertyType.isEnabled = false
             mBinding.spnAdministrationOffice.isEnabled = false
             mBinding.spnParentProperty.isEnabled = false
             mBinding.spnUnit.isEnabled = false
             mBinding.edtArea.isEnabled = false
             mBinding.edtGeoLocationArea.isEnabled = false
             mBinding.edtPropertyDescription.isEnabled = false
             mBinding.edtPropertyCode.isEnabled = false
             mBinding.spnExistingAddress.isEnabled = false
             mBinding.spnCountry.isEnabled = false
             mBinding.spnState.isEnabled = false
             mBinding.spnCity.isEnabled = false
             mBinding.spnZone.isEnabled = false
             mBinding.spnSector.isEnabled = false
             mBinding.edtStreet.isEnabled = false
             mBinding.edtZipCode.isEnabled = false
             mBinding.edtPlot.isEnabled = false
             mBinding.edtBlock.isEnabled = false
             mBinding.edtDoorNo.isEnabled = false
             mBinding.edtDescription.isEnabled = false
             mBinding.edtRegistrationNo.isEnabled = false
             mBinding.edtFromDate.isEnabled = false
             mBinding.edtToDate.isEnabled = false

             mBinding.btnSave.isEnabled = false
         }*/
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {

        mBinding.spnExistingPropertyCode.isEnabled = action
        mBinding.edtPropertyName.isEnabled = action
        mBinding.spnPropertyType.isEnabled = action
        mBinding.spnAdministrationOffice.isEnabled = action
        mBinding.spnParentProperty.isEnabled = action
        mBinding.spnUnit.isEnabled = action
        mBinding.edtArea.isEnabled = action
        mBinding.edtGeoLocationArea.isEnabled = action
        mBinding.edtPropertyDescription.isEnabled = action
        mBinding.edtPropertyCode.isEnabled = action
        mBinding.spnExistingAddress.isEnabled = action
        mBinding.spnCountry.isEnabled = action
        mBinding.spnState.isEnabled = action
        mBinding.spnCity.isEnabled = action
        mBinding.spnZone.isEnabled = action
        mBinding.spnSector.isEnabled = action
        mBinding.edtStreet.isEnabled = action
        mBinding.edtZipCode.isEnabled = action
        mBinding.edtPlot.isEnabled = action
        mBinding.edtBlock.isEnabled = action
        mBinding.edtDoorNo.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.edtRegistrationNo.isEnabled = action
        mBinding.edtFromDate.isEnabled = action
        mBinding.edtToDate.isEnabled = action
        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }

 private fun setEditActionForSave(action: Boolean) {

        mBinding.spnExistingPropertyCode.isEnabled = action
        mBinding.edtPropertyName.isEnabled = action
        mBinding.spnPropertyType.isEnabled = action
        mBinding.spnAdministrationOffice.isEnabled = action
        mBinding.spnParentProperty.isEnabled = action
        mBinding.spnUnit.isEnabled = action
        mBinding.edtArea.isEnabled = action
        mBinding.edtGeoLocationArea.isEnabled = action
        mBinding.edtPropertyDescription.isEnabled = action
        mBinding.edtPropertyCode.isEnabled = action
        mBinding.spnExistingAddress.isEnabled = action
        mBinding.spnCountry.isEnabled = action
        mBinding.spnState.isEnabled = action
        mBinding.spnCity.isEnabled = action
        mBinding.spnZone.isEnabled = action
        mBinding.spnSector.isEnabled = action
        mBinding.edtStreet.isEnabled = action
        mBinding.edtZipCode.isEnabled = action
        mBinding.edtPlot.isEnabled = action
        mBinding.edtBlock.isEnabled = action
        mBinding.edtDoorNo.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.edtRegistrationNo.isEnabled = action
        mBinding.edtFromDate.isEnabled = action
        mBinding.edtToDate.isEnabled = action
        mBinding.btnSave.visibility = View.VISIBLE
    }

    private fun bindSpinners() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("COM_PropertyMaster", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                responseExistingPropertiesList = response.existingProperties
                responsePropertyTypesList = response.propertyTypes
                responseOrgBranchesList = response.userOrgBranches
                responseMeasurementUnitsList = response.measurementUnits
                responseGeoAddressList = response.geoAddress
                responseCountriesList = response.countryMaster
                responseStatesList = response.stateMaster
                responseCitiesList = response.cityMaster
                responseZonesList = response.zoneMaster
                responseSectorsList = response.sectors

                for (existingProperty in responseExistingPropertiesList) {
                    val parentProperty = ParentProperty()
                    parentProperty.parentPropertyID = existingProperty.parentPropertyID
                    parentProperty.parentPropertyName = existingProperty.parentPropertyName
                    responseParentPropertiesList.add(parentProperty)
                }

                filterExistingPropertyCode()
                filterExistingAddress()
                bindData()
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnExistingPropertyCode.adapter = null
                mBinding.spnPropertyType.adapter = null
                mBinding.spnAdministrationOffice.adapter = null
                mBinding.spnParentProperty.adapter = null
                mBinding.spnUnit.adapter = null
                mBinding.spnExistingAddress.adapter = null
                mBinding.spnCountry.adapter = null
                mBinding.spnState.adapter = null
                mBinding.spnCity.adapter = null
                mBinding.spnZone.adapter = null
                mBinding.spnSector.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindData() {
        if (mPropertyOwnership != null) {
            mBinding.edtArea.setText(mPropertyOwnership!!.area.toString())
            mBinding.edtPropertyCode.setText(mPropertyOwnership!!.propertyCode)
            mBinding.edtPropertyName.setText(mPropertyOwnership!!.propertyName)
            mBinding.edtPropertyDescription.setText(mPropertyOwnership!!.propertyDescription)
            mBinding.edtGeoLocationArea.setText(mPropertyOwnership!!.geoLocationArea)
            mBinding.edtStreet.setText(mPropertyOwnership!!.street)
            mBinding.edtZipCode.setText(mPropertyOwnership!!.zipCode)
            mBinding.edtPlot.setText(mPropertyOwnership!!.plot)
            mBinding.edtBlock.setText(mPropertyOwnership!!.block)
            mBinding.edtDoorNo.setText(mPropertyOwnership!!.doorNo)
            mBinding.edtDescription.setText(mPropertyOwnership!!.description)
            mBinding.edtRegistrationNo.setText(mPropertyOwnership!!.registrationNo)
            mBinding.edtFromDate.setText(formatDisplayDateTimeInMillisecond(mPropertyOwnership!!.fromDate))
            mBinding.edtToDate.setText(formatDisplayDateTimeInMillisecond(mPropertyOwnership!!.toDate))

            getInvoiceCount4Tax()
        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)

        mBinding.spnExistingPropertyCode.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var existingProperty: VUCOMExistingProperties? = VUCOMExistingProperties()
                if (p0 != null && p0.selectedItem != null)
                    existingProperty = p0.selectedItem as VUCOMExistingProperties
                if (existingProperty?.propertyID != 0)
                    filterPropertyType(existingProperty)
            }
        }

        mBinding.spnExistingAddress.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var geoAddress: GeoAddress? = GeoAddress()
                if (p0 != null && p0.selectedItem != null)
                    geoAddress = p0.selectedItem as GeoAddress
                if (geoAddress?.geoAddressID != 0)
                    filterCountries(geoAddress)
            }
        }

        mBinding.spnCountry.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var country: COMCountryMaster? = COMCountryMaster()
                if (p0 != null && p0.selectedItem != null)
                    country = p0.selectedItem as COMCountryMaster
                val geoAddress = mBinding.spnExistingAddress.selectedItem as GeoAddress
                if (geoAddress.geoAddressID != 0)
                    filterStates(country?.countryCode, geoAddress)
            }
        }

        mBinding.spnState.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var state: COMStateMaster? = COMStateMaster()
                if (p0 != null && p0.selectedItem != null)
                    state = p0.selectedItem as COMStateMaster
                val geoAddress = mBinding.spnExistingAddress.selectedItem as GeoAddress
                filterCities(state?.stateID!!, geoAddress)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnCity.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var city: VUCOMCityMaster? = VUCOMCityMaster()
                if (p0 != null && p0.selectedItem != null)
                    city = p0.selectedItem as VUCOMCityMaster
                val geoAddress = mBinding.spnExistingAddress.selectedItem as GeoAddress
                filterZones(city?.cityID!!, geoAddress)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnZone.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                val geoAddress = mBinding.spnExistingAddress.selectedItem as GeoAddress
                filterSectors(zone?.zoneID!!, geoAddress)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }

    // region Existing Property
    private fun filterExistingPropertyCode() {
        if (responseExistingPropertiesList.isNullOrEmpty()) {
            mBinding.spnExistingPropertyCode.adapter = null
            filterPropertyType()
        } else {
            var index = -1
            val existingProperty = VUCOMExistingProperties()
            existingProperty.propertyID = 0
            existingProperty.propertyName = getString(R.string.select)
            responseExistingPropertiesList.add(0, existingProperty)

            if (mPropertyOwnership != null && mPropertyOwnership!!.propertyID != 0)
                for (existingProperties in responseExistingPropertiesList)
                    if (index <= -1 && mPropertyOwnership!!.propertyID != 0 && existingProperties.propertyID == mPropertyOwnership!!.propertyID)
                        index = responseExistingPropertiesList.indexOf(existingProperties)

            if (index <= -1) index = 0
            val existingPropertyAdapter = ArrayAdapter<VUCOMExistingProperties>(activity!!, android.R.layout.simple_spinner_dropdown_item, responseExistingPropertiesList)
            mBinding.spnExistingPropertyCode.adapter = existingPropertyAdapter
            mBinding.spnExistingPropertyCode.setSelection(index)
            filterPropertyType(responseExistingPropertiesList[index])
        }
    }

    private fun filterPropertyType(existingProperty: VUCOMExistingProperties? = null) {
        if (existingProperty != null && existingProperty.propertyID != 0) {
            mBinding.edtPropertyName.setText(existingProperty.propertyName)
            mBinding.edtArea.setText("${existingProperty.area}")
            mBinding.edtGeoLocationArea.setText(existingProperty.geoLocationArea)
            mBinding.edtPropertyDescription.setText(existingProperty.description)
            mBinding.edtPropertyCode.setText(existingProperty.propertyCode)
        }

        var index = -1
        var propertyTypeID: Int? = 0
        if (mPropertyOwnership != null)
            propertyTypeID = mPropertyOwnership!!.propertyTypeID
        if (propertyTypeID != null && propertyTypeID <= 0 && existingProperty != null && existingProperty.propertyID != 0)
            propertyTypeID = existingProperty.propertyTypeID

        for (propertyType in responsePropertyTypesList)
            if (index <= -1 && propertyType.propertyTypeID == propertyTypeID)
                index = responsePropertyTypesList.indexOf(propertyType)

        if (index <= -1) index = 0
        if (responsePropertyTypesList.isEmpty()) {
            mBinding.spnPropertyType.adapter = null
            filterAdministrationOffice(existingProperty)
        } else {
            val propertyTypeAdapter = ArrayAdapter<COMPropertyTypes>(activity!!, android.R.layout.simple_spinner_dropdown_item, responsePropertyTypesList)
            mBinding.spnPropertyType.adapter = propertyTypeAdapter
            mBinding.spnPropertyType.setSelection(index)
            filterAdministrationOffice(existingProperty)
        }
    }

    private fun filterAdministrationOffice(existingProperty: VUCOMExistingProperties? = null) {
        var index = -1
        var userOrgBranchID: Int? = 0
        if (mPropertyOwnership != null)
            userOrgBranchID = mPropertyOwnership!!.userOrgBranchID
        if (userOrgBranchID != null && userOrgBranchID <= 0 && existingProperty != null && existingProperty.userOrgBranchID != 0)
            userOrgBranchID = existingProperty.userOrgBranchID

        for (userOrgBranch in responseOrgBranchesList)
            if (index <= -1 && userOrgBranch.userOrgBranchID == userOrgBranchID)
                index = responseOrgBranchesList.indexOf(userOrgBranch)

        if (index <= -1) index = 0

        if (responsePropertyTypesList.isEmpty()) {
            mBinding.spnAdministrationOffice.adapter = null
            filterParentProperty(existingProperty)
        } else {
            val orgBranchAdapter = ArrayAdapter<UMXUserOrgBranches>(activity!!, android.R.layout.simple_spinner_dropdown_item, responseOrgBranchesList)
            mBinding.spnAdministrationOffice.adapter = orgBranchAdapter
            mBinding.spnAdministrationOffice.setSelection(index)
            filterParentProperty(existingProperty)
        }
    }

    private fun filterParentProperty(existingProperty: VUCOMExistingProperties? = null) {
        var index = -1
        var parentPropertyID: Int? = 0

        if (mPropertyOwnership != null)
            parentPropertyID = mPropertyOwnership!!.parentPropertyID
        if (parentPropertyID != null && parentPropertyID <= 0 && existingProperty != null && existingProperty.parentPropertyID != 0)
            parentPropertyID = existingProperty.parentPropertyID

        for (parentProperty in responseParentPropertiesList)
            if (index <= -1 && parentProperty.parentPropertyID == parentPropertyID)
                index = responseParentPropertiesList.indexOf(parentProperty)

        if (index <= -1) index = 0

        if (responseParentPropertiesList.isEmpty()) {
            mBinding.spnParentProperty.adapter = null
            filterUnit(existingProperty)
        } else {
            val parentPropertyAdapter = ArrayAdapter<ParentProperty>(activity!!, android.R.layout.simple_spinner_dropdown_item, responseParentPropertiesList)
            mBinding.spnParentProperty.adapter = parentPropertyAdapter
            mBinding.spnParentProperty.setSelection(index)
            filterUnit(existingProperty)
        }
    }

    private fun filterUnit(existingProperty: VUCOMExistingProperties? = null) {
        var index = -1
        var unitCode: String? = ""

        if (mPropertyOwnership != null)
            unitCode = mPropertyOwnership!!.unitCode
        if (unitCode != null && TextUtils.isEmpty(unitCode) && existingProperty != null && existingProperty.unitCode != null && !TextUtils.isEmpty(existingProperty.unitCode))
            unitCode = existingProperty.unitCode

        for (unit in responseMeasurementUnitsList)
            if (index <= -1 && unit.unitCode == unitCode)
                index = responseMeasurementUnitsList.indexOf(unit)

        if (index <= -1) index = 0

        if (responseMeasurementUnitsList.isEmpty())
            mBinding.spnUnit.adapter = null
        else {
            val unitsAdapter = ArrayAdapter<VUINVMeasurementUnits>(activity!!, android.R.layout.simple_spinner_dropdown_item, responseMeasurementUnitsList)
            mBinding.spnUnit.adapter = unitsAdapter
            mBinding.spnUnit.setSelection(index)
        }
    }
    // endregion

    // region Existing Address
    private fun filterExistingAddress() {
        if (responseGeoAddressList.isNullOrEmpty()) {
            mBinding.spnExistingAddress.adapter = null
            filterCountries()
        } else {
            var index = -1
            val geoAddress = GeoAddress()
            geoAddress.geoAddressID = 0
            geoAddress.geoAddressType = getString(R.string.select)
            responseGeoAddressList.add(0, geoAddress)

            if (mPropertyOwnership != null && mPropertyOwnership!!.geoAddressID != 0)
                for (geoAddresses in responseGeoAddressList)
                    if (index <= -1 && mPropertyOwnership!!.geoAddressID != 0 && geoAddresses.geoAddressID == mPropertyOwnership!!.geoAddressID)
                        index = responseGeoAddressList.indexOf(geoAddresses)

            if (index <= -1) index = 0
            val geoAddressAdapter = ArrayAdapter<GeoAddress>(activity!!, android.R.layout.simple_spinner_dropdown_item, responseGeoAddressList)
            mBinding.spnExistingAddress.adapter = geoAddressAdapter
            mBinding.spnExistingAddress.setSelection(index)
            filterCountries(responseGeoAddressList[index])
        }
    }

    private fun filterCountries(geoAddress: GeoAddress? = null) {
        if (geoAddress != null && geoAddress.geoAddressID != 0) {
            mBinding.edtStreet.setText(geoAddress.street)
            mBinding.edtZipCode.setText(geoAddress.zipCode)
            mBinding.edtPlot.setText(geoAddress.plot)
            mBinding.edtBlock.setText(geoAddress.block)
            mBinding.edtDoorNo.setText(geoAddress.doorNo)
            mBinding.edtDescription.setText(geoAddress.description)
        }

        val countries: MutableList<COMCountryMaster> = ArrayList()
        var index = -1
        var countryCode = ""
        if (mPropertyOwnership != null)
            countryCode = mPropertyOwnership!!.countryCode.toString()
        if (TextUtils.isEmpty(countryCode) && geoAddress != null && geoAddress.geoAddressID != 0)
            countryCode = geoAddress.countryCode.toString()
        for (country in responseCountriesList) {
            countries.add(country)
            if (index <= -1 && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode)
                index = countries.indexOf(country)
        }
        if (index <= -1) index = 0
        if (countries.size > 0) {
            val countryMasterArrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, countries)
            mBinding.spnCountry.adapter = countryMasterArrayAdapter
            mBinding.spnCountry.setSelection(index)
            filterStates(countries[index].countryCode, geoAddress)
        } else {
            mBinding.spnCountry.adapter = null
            filterStates(countryCode)
        }
    }

    private fun filterStates(countryCode: String?, geoAddress: GeoAddress? = null) {
        var states: MutableList<COMStateMaster> = ArrayList()
        var index = -1
        var stateID: Int? = 0
        if (mPropertyOwnership != null)
            stateID = mPropertyOwnership!!.stateID
        if (stateID == 0 && geoAddress != null && geoAddress.geoAddressID != 0 && geoAddress.stateID != 0)
            stateID = geoAddress.stateID
        if (TextUtils.isEmpty(countryCode))
            states = ArrayList()
        else {
            for (state in responseStatesList) {
                if (countryCode == state.countryCode)
                    states.add(state)
                if (index <= -1 && stateID != 0 && state.stateID != null && stateID == state.stateID)
                    index = states.indexOf(state)
            }
        }
        if (index <= -1) index = 0
        if (states.size > 0) {
            val stateArrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, states)
            mBinding.spnState.adapter = stateArrayAdapter
            mBinding.spnState.setSelection(index)
            filterCities(states[index].stateID!!, geoAddress)
        } else {
            mBinding.spnState.adapter = null
            filterCities(stateID)
        }
    }

    private fun filterCities(stateID: Int?, geoAddress: GeoAddress? = null) {
        var cities: MutableList<VUCOMCityMaster> = ArrayList()
        var index = -1
        var cityID: Int? = 0
        if (mPropertyOwnership != null)
            cityID = mPropertyOwnership!!.cityID
        if (cityID == 0 && geoAddress != null && geoAddress.geoAddressID != 0 && geoAddress.cityID != 0)
            cityID = geoAddress.cityID
        if (stateID != null && stateID <= 0)
            cities = ArrayList()
        else {
            for (city in responseCitiesList) {
                if (city.stateID != null && stateID == city.stateID)
                    cities.add(city)
                if (index <= 0 && cityID != 0 && city.cityID != null && cityID == city.cityID)
                    index = cities.indexOf(city)
            }
        }
        if (index <= -1) index = 0
        if (cities.size > 0) {
            val cityArrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, cities)
            mBinding.spnCity.adapter = cityArrayAdapter
            mBinding.spnCity.setSelection(index)
            filterZones(cities[index].cityID!!, geoAddress)
        } else {
            mBinding.spnCity.adapter = null
            filterZones(cityID, geoAddress)
        }
    }

    private fun filterZones(cityID: Int?, geoAddress: GeoAddress? = null) {
        var zones: MutableList<COMZoneMaster> = ArrayList()
        var index = 0
        var zoneName: String? = ""
        if (mPropertyOwnership != null)
            zoneName = mPropertyOwnership!!.zone
        if (TextUtils.isEmpty(zoneName) && geoAddress != null && geoAddress.geoAddressID != 0 && !TextUtils.isEmpty(geoAddress.zone))
            zoneName = geoAddress.zone
        if (cityID != null && cityID <= 0)
            zones = ArrayList()
        else {
            for (zone in responseZonesList) {
                if (zone.cityID != null && cityID == zone.cityID)
                    zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone)
                    index = zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnZone.adapter = zoneArrayAdapter
            mBinding.spnZone.setSelection(index)
            filterSectors(zones[index].zoneID!!, geoAddress)
        } else {
            mBinding.spnZone.adapter = null
            filterSectors(0)
        }
    }

    private fun filterSectors(zoneID: Int, geoAddress: GeoAddress? = null) {
        var sectors: MutableList<COMSectors?> = ArrayList()
        var index = 0
        var sectorID: Int? = 0
        if (mPropertyOwnership != null)
            sectorID = mPropertyOwnership!!.sectorID
        if (sectorID == 0 && geoAddress != null && geoAddress.geoAddressID != 0 && geoAddress.sectorID != 0)
            sectorID = geoAddress.sectorID
        if (zoneID <= 0) sectors = ArrayList() else {
            for (sector in responseSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId) index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            val sectorArrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnSector.adapter = sectorArrayAdapter
            mBinding.spnSector.setSelection(index)
        } else {
            mBinding.spnSector.adapter = null
        }
    }
    // endregion

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                insertDataWithValidation()
            }
        }
    }

    private fun insertDataWithValidation() {
        if (validateView()) {
            helper.fetchLocation()
            helper.setListener(object : LocationHelper.Location {
                override fun found(latitude: Double, longitude: Double) {
                    mListener?.dismissDialog()
                    prepareDate(latitude, longitude)
                }

                override fun start() {
                    mListener?.showProgressDialog(R.string.msg_location_fetching)
                }
            })
        }
    }

    private fun prepareDate(latitude: Double, longitude: Double) {
        if (validateView()) {
            // region Registration Details
            val savePropertyOwnership = SavePropertyOwnership()
            if (mPropertyOwnership != null && mPropertyOwnership!!.propertyOwnershipID != 0)
                savePropertyOwnership.propertyOwnershipID = mPropertyOwnership!!.propertyOwnershipID
            savePropertyOwnership.accountID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
            if (mBinding.edtRegistrationNo.text != null && !TextUtils.isEmpty(mBinding.edtRegistrationNo.text.toString().trim()))
                savePropertyOwnership.registrationNo = mBinding.edtRegistrationNo.text.toString().trim()
            if (mBinding.edtFromDate.text != null && !TextUtils.isEmpty(mBinding.edtFromDate.text.toString().trim()))
                savePropertyOwnership.fromDate = serverFormatDateTimeInMilliSecond(mBinding.edtFromDate.text.toString().trim())
            if (mBinding.edtToDate.text != null && !TextUtils.isEmpty(mBinding.edtToDate.text.toString().trim()))
                savePropertyOwnership.toDate = serverFormatDateTimeInMilliSecond(mBinding.edtToDate.text.toString().trim())
            // endregion

            // region Geo Address
            val geoAddress = GeoAddress()

            // region Spinner Data
            val existingAddress = mBinding.spnExistingAddress.selectedItem as GeoAddress
            geoAddress.geoAddressID = existingAddress.geoAddressID
            val country = mBinding.spnCountry.selectedItem as COMCountryMaster
            geoAddress.countryCode = country.countryCode
            val state = mBinding.spnState.selectedItem as COMStateMaster
            geoAddress.state = state.state
            val city = mBinding.spnCity.selectedItem as VUCOMCityMaster
            geoAddress.city = city.city
            val zone = mBinding.spnZone.selectedItem as COMZoneMaster?
            if (zone != null)
                geoAddress.zone = zone.zone
            val sector = mBinding.spnSector.selectedItem as COMSectors?
            if (sector != null)
                geoAddress.sectorID = sector.sectorId
            // endregion

            if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString()))
                geoAddress.street = mBinding.edtStreet.text.toString().trim()
            if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString()))
                geoAddress.zipCode = mBinding.edtZipCode.text.toString().trim()
            if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString()))
                geoAddress.plot = mBinding.edtPlot.text.toString().trim()
            if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString()))
                geoAddress.block = mBinding.edtBlock.text.toString().trim()
            if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(mBinding.edtDoorNo.text.toString().trim()))
                geoAddress.doorNo = mBinding.edtDoorNo.text.toString().trim()
            if (mBinding.edtDescription.text != null && !TextUtils.isEmpty(mBinding.edtDescription.text.toString()))
                geoAddress.description = mBinding.edtDescription.text.toString().trim()
            geoAddress.latitude = "$latitude"
            geoAddress.longitude = "$longitude"
            // endregion

            // region Property Details
            val propertyMaster = CRMPropertyMaster()

            // region Spinner Data
            val existingPropertyCode = mBinding.spnExistingPropertyCode.selectedItem as VUCOMExistingProperties
            propertyMaster.propertyID = existingPropertyCode.propertyID
            val propertyType = mBinding.spnPropertyType.selectedItem as COMPropertyTypes
            propertyMaster.propertyTypeID = propertyType.propertyTypeID
            val orgBranch = mBinding.spnAdministrationOffice.selectedItem as UMXUserOrgBranches
            propertyMaster.userOrgBranchID = orgBranch.userOrgBranchID
            val parentProperty = mBinding.spnParentProperty.selectedItem as ParentProperty
            propertyMaster.parentPropertyID = parentProperty.parentPropertyID
            val unit = mBinding.spnUnit.selectedItem as VUINVMeasurementUnits
            propertyMaster.unitCode = unit.unitCode
            propertyMaster.geoAddressID = existingAddress.geoAddressID
            // endregion

            if (mBinding.edtPropertyName.text != null && !TextUtils.isEmpty(mBinding.edtPropertyName.text.toString().trim()))
                propertyMaster.propertyName = mBinding.edtPropertyName.text.toString().trim()
            if (mBinding.edtArea.text != null && !TextUtils.isEmpty(mBinding.edtArea.text.toString().trim()))
                propertyMaster.area = mBinding.edtArea.text.toString().trim().toInt()
            if (mBinding.edtGeoLocationArea.text != null && !TextUtils.isEmpty(mBinding.edtGeoLocationArea.text.toString().trim()))
                propertyMaster.geoLocationArea = mBinding.edtGeoLocationArea.text.toString().trim()
            if (mBinding.edtPropertyDescription.text != null && !TextUtils.isEmpty(mBinding.edtPropertyDescription.text.toString().trim()))
                propertyMaster.description = mBinding.edtPropertyDescription.text.toString().trim()
            if (mBinding.edtPropertyCode.text != null && !TextUtils.isEmpty(mBinding.edtPropertyCode.text.toString().trim()))
                propertyMaster.propertyCode = mBinding.edtPropertyCode.text.toString().trim()
            // endregion

            val insertPropertyOwnership = InsertPropertyOwnership()
            insertPropertyOwnership.geoAddress = geoAddress
            insertPropertyOwnership.propertyMaster = propertyMaster
            insertPropertyOwnership.savePropertyOwnership = savePropertyOwnership


            savePropertyOwnerShipDetails(insertPropertyOwnership)
        }
    }

    private fun savePropertyOwnerShipDetails(insertPropertyOwnership: InsertPropertyOwnership) {
        if (0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId) {
            insertPropertyOwnership.savePropertyOwnership?.accountID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId

            mListener?.showProgressDialog()
            APICall.insertPropertyOwnershipDetails(insertPropertyOwnership, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    if ((mPropertyOwnership != null) && mPropertyOwnership?.propertyID != 0)
                        mListener?.showSnackbarMsg("${getString(R.string.property_ownership)} " + getString(R.string.updated_successfully))
                    else
                        mListener?.showSnackbarMsg("${getString(R.string.property_ownership)} " + getString(R.string.added_successfully))
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 750)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            *//*    when (fromScreen) {
          Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
                  val vucrmPropertyOwnership = VUCRMPropertyOwnership()
                  vucrmPropertyOwnership.propertyOwnershipID = insertPropertyOwnership.savePropertyOwnership?.propertyOwnershipID
                  vucrmPropertyOwnership.registrationNo = insertPropertyOwnership.savePropertyOwnership?.registrationNo
                  vucrmPropertyOwnership.fromDate = insertPropertyOwnership.savePropertyOwnership?.fromDate
                  vucrmPropertyOwnership.toDate = insertPropertyOwnership.savePropertyOwnership?.toDate

                  vucrmPropertyOwnership.geoAddressID = insertPropertyOwnership.geoAddress?.addressID
                  vucrmPropertyOwnership.countryCode = insertPropertyOwnership.geoAddress?.countryCode
                  vucrmPropertyOwnership.stateID = insertPropertyOwnership.geoAddress?.stateID!!
                  vucrmPropertyOwnership.cityID = insertPropertyOwnership.geoAddress?.cityID!!
                  vucrmPropertyOwnership.zone = insertPropertyOwnership.geoAddress?.zone
                  vucrmPropertyOwnership.sectorID = insertPropertyOwnership.geoAddress?.sectorID!!
                  vucrmPropertyOwnership.street = insertPropertyOwnership.geoAddress?.street
                  vucrmPropertyOwnership.zipCode = insertPropertyOwnership.geoAddress?.zipCode
                  vucrmPropertyOwnership.plot = insertPropertyOwnership.geoAddress?.plot
                  vucrmPropertyOwnership.block = insertPropertyOwnership.geoAddress?.block
                  vucrmPropertyOwnership.doorNo = insertPropertyOwnership.geoAddress?.doorNo
                  vucrmPropertyOwnership.description = insertPropertyOwnership.geoAddress?.description
                  vucrmPropertyOwnership.latitude = insertPropertyOwnership.geoAddress?.latitude
                  vucrmPropertyOwnership.longitude = insertPropertyOwnership.geoAddress?.longitude

                  vucrmPropertyOwnership.propertyName = insertPropertyOwnership.propertyMaster?.propertyName
                  vucrmPropertyOwnership.area = insertPropertyOwnership.propertyMaster?.area
                  vucrmPropertyOwnership.geoLocationArea = insertPropertyOwnership.propertyMaster?.geoLocationArea
                  vucrmPropertyOwnership.description = insertPropertyOwnership.propertyMaster?.description
                  vucrmPropertyOwnership.propertyID = insertPropertyOwnership.propertyMaster?.propertyID
                  vucrmPropertyOwnership.propertyCode = insertPropertyOwnership.propertyMaster?.propertyCode

                  ObjectHolder.registerBusiness.insertPropertyOwnership.add(insertPropertyOwnership)
                  ObjectHolder.registerBusiness.propertyOwnerships.add(vucrmPropertyOwnership)
                  Handler().postDelayed({
                      targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                      mListener!!.popBackStack()
                  }, 750)

          }
          else -> {*//*
        }*/
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        helper.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        helper.onActivityResult(requestCode, resultCode)
    }

    private fun validateView(): Boolean {
        if (mBinding.edtPropertyName.text != null && TextUtils.isEmpty(mBinding.edtPropertyName.text.toString().trim { it <= ' ' })) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.property_name))
            return false
        }
        val orgBranch = mBinding.spnAdministrationOffice.selectedItem as UMXUserOrgBranches?
        if (orgBranch?.userOrgBranchID == null) {
            mListener!!.showSnackbarMsg(resources.getString(R.string.validation_choose_administration_office))
            mBinding.spnCity.requestFocus()
            return false
        }
        val unit = mBinding.spnUnit.selectedItem as VUINVMeasurementUnits?
        if (unit?.unitCode == null) {
            mListener!!.showSnackbarMsg(resources.getString(R.string.validation_choose_administration_office))
            mBinding.spnCity.requestFocus()
            return false
        }
        if (mBinding.edtArea.text != null && TextUtils.isEmpty(mBinding.edtArea.text.toString().trim { it <= ' ' })) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.area))
            return false
        }
        if (mBinding.edtPropertyCode.text != null && TextUtils.isEmpty(mBinding.edtPropertyCode.text.toString().trim { it <= ' ' })) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.property_code))
            return false
        }
        val comCityMaster = mBinding.spnCity.selectedItem as VUCOMCityMaster?
        if (comCityMaster?.city == null) {
            mListener!!.showSnackbarMsg(resources.getString(R.string.validation_choose_city))
            mBinding.spnCity.requestFocus()
            return false
        }
        val comZoneMaster = mBinding.spnZone.selectedItem as COMZoneMaster?
        if (comZoneMaster?.zone == null) {
            mListener!!.showSnackbarMsg(resources.getString(R.string.validation_choose_zone))
            mBinding.spnZone.requestFocus()
            return false
        }
        val comSectors = mBinding.spnSector.selectedItem as COMSectors?
        if (comSectors?.sectorId == null) {
            mListener!!.showSnackbarMsg(resources.getString(R.string.validation_choose_sector))
            mBinding.spnSector.requestFocus()
            return false
        }
        if (mBinding.edtRegistrationNo.text != null && TextUtils.isEmpty(mBinding.edtRegistrationNo.text.toString().trim { it <= ' ' })) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.registration_no))
            return false
        }
        if (mBinding.edtFromDate.text != null && TextUtils.isEmpty(mBinding.edtFromDate.text.toString().trim { it <= ' ' })) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.from_date))
            return false
        }
        if (mBinding.edtToDate.text != null && TextUtils.isEmpty(mBinding.edtToDate.text.toString().trim { it <= ' ' })) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.to_date))
            return false
        }

        return true
    }

    private fun getInvoiceCount4Tax() {

        val currentDue = CheckCurrentDue()
        currentDue.accountId = mPropertyOwnership?.accountID
        currentDue.vchrno  = mPropertyOwnership?.propertyID
        currentDue.taxRuleBookCode  = mTaxRuleBookCode
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response>0)
                {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }
                    else {
                        setEditActionForSave(false)
                    }
                }
                else
                {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }
                    else {
                        setEditAction(true)
                    }
                }
            }
            override fun onFailure(message: String) {
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                {
                    setEditAction(false)
                }
            }
        })
    }

    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun showProgressDialog(message: String)
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        fun showToast(message: String)
        var screenMode: Constant.ScreenMode

    }

}
