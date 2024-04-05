package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.BusinessAddressResponse
import com.sgs.citytax.api.response.CitizenSycoTaxResponse
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentBusinessOwnerEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.RegisterOwnerActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU
import com.sgs.citytax.util.displayDateFormat
import com.sgs.citytax.util.displayFormatDate
import com.sgs.citytax.util.serverFormatDate
import java.util.*

class BusinessOwnerEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentBusinessOwnerEntryBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
    private var isEdit = false

    private var mDocumentsList: List<COMDocumentReference>? = null
    private var mComNotesList: ArrayList<COMNotes> = arrayListOf()
    private var mBusinessOwnership: BusinessOwnership? = null
    private var mStatusCodes: MutableList<COMStatusCode>? = null
    private var mProfessions: MutableList<CRMProfessions>? = null

    private var mGeoAddress: GeoAddress? = null
    private var mBusinessGeoAddress: GeoAddress? = null
    private var mResponseCountriesList: List<COMCountryMaster> = java.util.ArrayList()
    private var mResponseStatesList: List<COMStateMaster> = java.util.ArrayList()
    private var mResponseCitiesList: List<VUCOMCityMaster> = java.util.ArrayList()
    private var mResponseZonesList: List<COMZoneMaster> = java.util.ArrayList()
    private var mResponseSectorsList: List<COMSectors> = java.util.ArrayList()

    private var keyShow: String? = null

    private var isActionEnabled: Boolean = true
    var pageIndex: Int = 1
    val pageSize: Int = 100
    private var setViewForGeoSpatial: Boolean? = false  //todo New key to Hide views for geo spacial - 15/3/2022, not used fromScreen, to not to disturb th flow

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (arguments?.containsKey(KEY_QUICK_MENU)!!)
                fromScreen = arguments?.getSerializable(KEY_QUICK_MENU) as Constant.QuickMenu
            if (arguments?.containsKey(Constant.KEY_BUSINESS_OWNER)!!)
                mBusinessOwnership = arguments?.getParcelable(Constant.KEY_BUSINESS_OWNER)
            if (arguments?.containsKey(Constant.KEY_EDIT)!!)
                isEdit = arguments?.getBoolean(Constant.KEY_EDIT) as Boolean
            if (arguments?.containsKey(Constant.KEY_ADDRESS)!!) {
                mBusinessGeoAddress = arguments?.getParcelable(Constant.KEY_ADDRESS)
            }
            if (arguments?.containsKey(Constant.KEY_SHOW)!!) {
                keyShow = arguments?.getString(Constant.KEY_SHOW)
            }
            if (it.containsKey(Constant.KEY_GEO_SPATIAL_VIEW))
                setViewForGeoSpatial = it.getBoolean(Constant.KEY_GEO_SPATIAL_VIEW, false)
        }

        //endregion
        setViews()
        bindSpinner()
        setListeners()
        fetchChildEntriesCount()
        showViewsEnabled()
    }

    private fun showViewsEnabled() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
        if (!mBusinessOwnership?.citizenSycoTaxID.isNullOrEmpty())
        // setEditAction(false)
            mBinding.edtSycoTaxID.isEnabled = false
        mBinding.btnScan.isEnabled = false
    }

    private fun setEditAction(action: Boolean) {
        isActionEnabled = action

        mBinding.edtFirstName.isEnabled = action
        mBinding.edtLastName.isEnabled = action
        mBinding.spnTelephoneCode.isEnabled = action
        mBinding.edtPhoneNumber.isEnabled = action
        mBinding.edtEmail.isEnabled = action
        mBinding.spnProfession.isEnabled = action
        mBinding.spnStatus.isEnabled = action
        mBinding.edtIfuNo.isEnabled = action
//        mBinding.edtDescription.isEnabled = action
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
        mBinding.edtDob.isEnabled = action
        mBinding.edtSycoTaxID.isEnabled = action
        mBinding.btnScan.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = VISIBLE
        } else {
            mBinding.btnSave.visibility = GONE
        }

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_business_owner_entry,
            container,
            false
        )
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {

        mBinding.edtDob.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtDob.setDisplayDateFormat(displayDateFormat)

        mBinding.llAccountAddressChildTab.visibility = GONE
        mBinding.llAccountEmailsChildTab.visibility = GONE
        mBinding.llAccountPhoneChildTab.visibility = GONE
        mBinding.llCards.visibility = GONE

        if (keyShow == Constant.KEY_BUSINESS) {
            mBinding.tilDrivingLicenseNumber.visibility = GONE
            mBinding.copyAddressBtn.visibility = VISIBLE
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS) {
            mBinding.tilDrivingLicenseNumber.visibility = GONE
            mBinding.copyAddressBtn.visibility = GONE
        } else {
            mBinding.tilDrivingLicenseNumber.visibility = VISIBLE
            mBinding.copyAddressBtn.visibility = GONE
        }
        if(mBinding.tilDrivingLicenseNumber.isVisible && setViewForGeoSpatial == true)
            mBinding.edtDrivingLicenseNumber.isEnabled = false

    }

    private fun bindData() {
        if (mBusinessOwnership != null) {
            if (!mBusinessOwnership?.firstName.isNullOrEmpty())
                mBinding.edtFirstName.setText(mBusinessOwnership?.firstName ?: "")
            else
                mBinding.edtFirstName.setText(mBusinessOwnership?.accountName ?: "")
            mBinding.edtLastName.setText(mBusinessOwnership?.lastName ?: "")
            mBinding.edtIfuNo.setText(mBusinessOwnership?.ifu ?: "")
            mBinding.edtDescription.setText(mBusinessOwnership?.remarks ?: "")
            mBinding.edtPhoneNumber.setText(mBusinessOwnership?.phone ?: "")
            mBinding.edtEmail.setText(mBusinessOwnership?.email ?: "")
            mBinding.edtDrivingLicenseNumber.setText(mBusinessOwnership?.drivingLicenseNo ?: "")
            mBinding.edtDob.setText(displayFormatDate(mBusinessOwnership!!.dob))
            mBinding.edtSycoTaxID.setText(mBusinessOwnership?.citizenSycoTaxID ?: "")

            if (mStatusCodes != null)
                for ((index, obj) in mStatusCodes!!.withIndex()) {
                    if (mBusinessOwnership!!.statusCode == obj.statusCode) {
                        mBinding.spnStatus.setSelection(index)
                        break
                    }
                }

            if (mProfessions != null) {
                for ((index, obj) in mProfessions!!.withIndex()) {
                    if (mBusinessOwnership?.professionID == obj.professionID) {
                        mBinding.spnProfession.setSelection(index)
                        break
                    }
                }
            }

            mBinding.llAccountAddressChildTab.visibility = GONE
            mBinding.llAccountEmailsChildTab.visibility = VISIBLE
            mBinding.llAccountPhoneChildTab.visibility = VISIBLE
            mBinding.llCards.visibility = VISIBLE

            mBinding.llBusinessOwnerID.visibility = GONE
            mBusinessOwnership?.businessOwnerID?.let {
                if (!TextUtils.isEmpty(it)) {
                    mBinding.tvBusinessOwnerID.text = it
                    mBinding.llBusinessOwnerID.visibility = VISIBLE
                }
            }

            //mBinding.llCitizenID.visibility = GONE
            mBusinessOwnership?.citizenID?.let {
                if (!TextUtils.isEmpty(it)) {
                    mBinding.tvCitizenID.text = it
                    mBinding.llCitizenID.visibility = VISIBLE
                }
            }

            mBinding.btnSave.visibility = VISIBLE
            mBinding.btnNext.visibility = GONE

            mBinding.btnSave.tag = getString(R.string.save)
            mBinding.btnNext.tag = getString(R.string.next)

            if (mBusinessOwnership?.ownerGeoAddressID != null) {
                getExistingAddress(mBusinessOwnership?.ownerGeoAddressID)
            } else {
                getExistingAddress(mBusinessOwnership?.addressId)
            }

        } else {
            mBinding.llBusinessOwnerID.visibility = GONE
            //  mBinding.llCitizenID.visibility = GONE

            mBinding.btnSave.visibility = GONE
            mBinding.btnNext.visibility = VISIBLE
        }
        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            mBinding.btnSave.visibility = GONE
            mBinding.btnNext.visibility = GONE
        }

//        if (MyApplication.getPrefHelper().getAsPerAgentType() &&  activity is RegisterBusinessActivity && mListener!!.screenMode == Constant.ScreenMode.VIEW) {
//            if (mBusinessOwnership == null) {
//                mBinding.btnSave.visibility = GONE
//                mBinding.btnNext.visibility = VISIBLE
//            } else {
//                mBinding.btnSave.visibility = VISIBLE
//                mBinding.btnNext.visibility = GONE
//            }
//        }
    }

    private fun getExistingAddress(geoAddressID: Int?) {
        val searchFilter = OwnerSearchFilter()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex
        searchFilter.query = null

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "GeoAddressID"
        filterColumn.columnValue = geoAddressID.toString()
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        searchFilter.filterColumns = listFilterColumn
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_COM_GeoAddresses"
        tableDetails.primaryKeyColumnName = "GeoAddressID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "OR"

        searchFilter.tableDetails = tableDetails

        APICall.getBusinessAddress(
            searchFilter,
            object : ConnectionCallBack<BusinessAddressResponse> {
                override fun onSuccess(response: BusinessAddressResponse) {
                    response.results.businessOwner.get(0).let {
                        mGeoAddress = it

                        if (mGeoAddress != null) {
                            mBinding.edtStreet.setText(mGeoAddress!!.street)
                            mBinding.edtZipCode.setText(mGeoAddress!!.zipCode)
                            mBinding.edtPlot.setText(mGeoAddress!!.plot)
                            mBinding.edtBlock.setText(mGeoAddress!!.block)
                            mBinding.edtDoorNo.setText(mGeoAddress!!.doorNo)
                            mBinding.edtDescription.setText(mGeoAddress!!.description)
                            filterCountries()
                        } /*else
                    {
                        mBinding.spnCountry.adapter = null
                        mBinding.spnState.adapter = null
                        mBinding.spnCity.adapter = null
                        mBinding.spnZone.adapter = null
                        mBinding.spnSector.adapter = null

                        mBinding.edtStreet.setText("")
                        mBinding.edtZipCode.setText("")
                        mBinding.edtPlot.setText("")
                        mBinding.edtBlock.setText("")
                        mBinding.edtDoorNo.setText("")
                        mBinding.edtDescription.setText("")
                    }*/

                    }

                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    // mListener?.showAlertDialog(message)
                }
            })

    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.btnNext.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
        mBinding.llAccountPhoneChildTab.setOnClickListener(this)
        mBinding.llCards.setOnClickListener(this)
        mBinding.llAccountEmailsChildTab.setOnClickListener(this)
        mBinding.llAccountAddressChildTab.setOnClickListener(this)
        mBinding.copyAddressBtn.setOnClickListener(this)
        // mBinding.edtSycoTaxID.setOnClickListener(this)
        mBinding.btnScan.setOnClickListener(this)

        mBinding.spnCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var country: COMCountryMaster? = COMCountryMaster()
                if (p0 != null && p0.selectedItem != null)
                    country = p0.selectedItem as COMCountryMaster
                filterStates(country?.countryCode)
            }
        }

        mBinding.spnState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var state: COMStateMaster? = COMStateMaster()
                if (p0 != null && p0.selectedItem != null)
                    state = p0.selectedItem as COMStateMaster
                filterCities(state?.stateID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var city: VUCOMCityMaster? = VUCOMCityMaster()
                if (p0 != null && p0.selectedItem != null)
                    city = p0.selectedItem as VUCOMCityMaster
                filterZones(city?.cityID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    private fun getCitizenSycoTaxIDs() {
        mListener?.showProgressDialog()
        val getRandomCitizenSycoTaxIDs = GetRandomCitizenSycoTaxIDs()
        APICall.getRandomCitizenSycoTaxIDs(
            getRandomCitizenSycoTaxIDs,
            object : ConnectionCallBack<CitizenSycoTaxResponse> {
                override fun onSuccess(response: CitizenSycoTaxResponse) {
                    mListener?.dismissDialog()
                    response.sycoTaxIDs?.let {
                        showSycoTaxIdsList(it)
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
    }

    private fun showSycoTaxIdsList(citizenSycoTaxIDList: ArrayList<CitizenSycoTaxID>) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle(R.string.title_select_sycotax_id)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            citizenSycoTaxIDList
        )
        builder.setAdapter(adapter) { dialog, which ->
            dialog.dismiss()
            citizenSycoTaxIDList[which].sycoTaxID?.let {
                mBinding.edtSycoTaxID.setText(it)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getCorporateOfficeLOVValues(
            "CRM_AccountContacts",
            object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    mStatusCodes = response.statusCodes
                    mResponseCountriesList = response.countryMaster
                    mResponseStatesList = response.stateMaster
                    mResponseCitiesList = response.cityMaster
                    mResponseZonesList = response.zoneMaster
                    mResponseSectorsList = response.sectors

                    filterCountries()

                    if (mStatusCodes != null && mStatusCodes!!.isNotEmpty()) {
                        mStatusCodes?.add(0, COMStatusCode(getString(R.string.select), "-1"))
                        mBinding.spnStatus.adapter = ArrayAdapter<COMStatusCode>(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            mStatusCodes!!
                        )
                        //region select In active status by default
                        var pos = 0
                        for ((index, obj) in mStatusCodes!!.withIndex()) {
                            if ("CRM_Contacts.Active" == obj.statusCode) {
                                pos = index
                                break
                            }
                        }
                        mBinding.spnStatus.setSelection(pos, true)
                        //endregion
                    } else
                        mBinding.spnStatus.adapter = null

                    if (response.countryMaster.isNotEmpty()) {
                        val countryCode: String? = "BFA"
                        val countries: MutableList<COMCountryMaster> = arrayListOf()
                        var index = -1
                        val telephonicCodes: ArrayList<Int> = arrayListOf()
                        for (country in response.countryMaster) {
                            country.telephoneCode?.let {
                                if (it > 0) {
                                    countries.add(country)
                                    telephonicCodes.add(it)
                                    if (index <= -1 && countryCode == country.countryCode)
                                        index = countries.indexOf(country)
                                }
                            }
                        }
                        if (index <= -1) index = 0
                        if (telephonicCodes.size > 0) {
                            val telephonicCodeArrayAdapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                telephonicCodes
                            )
                            mBinding.spnTelephoneCode.adapter = telephonicCodeArrayAdapter

                            if (mBusinessOwnership?.telCode != null) {
                                mBinding.spnTelephoneCode.setSelection(
                                    telephonicCodes.indexOf(
                                        mBusinessOwnership?.telCode as Int
                                    )
                                )
                            } else {
                                mBinding.spnTelephoneCode.setSelection(index)
                            }
                        } else mBinding.spnTelephoneCode.adapter = null
                    }

                    mProfessions = response.professions

                    if (mProfessions != null && mProfessions!!.isNotEmpty()) {
                        mProfessions?.add(0, CRMProfessions(getString(R.string.select), "", -1))
                        mBinding.spnProfession.adapter = ArrayAdapter<CRMProfessions>(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            mProfessions!!
                        )
                    }

                    bindData()

                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mBinding.spnStatus.adapter = null
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)

                    mBinding.spnCountry.adapter = null
                    mBinding.spnState.adapter = null
                    mBinding.spnCity.adapter = null
                    mBinding.spnZone.adapter = null
                    mBinding.spnSector.adapter = null

                }
            })
    }

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = java.util.ArrayList()
        var index = -1
        var countryCode: String? = "BFA"
        if (mGeoAddress != null) countryCode = mGeoAddress!!.countryCode
        for (country in mResponseCountriesList) {
            countries.add(country)
            if (index <= -1 && countryCode != null && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode) index =
                countries.indexOf(country)
        }
        if (index <= -1) index = 0
        if (countries.size > 0) {
            val countryMasterArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                countries
            )
            mBinding.spnCountry.adapter = countryMasterArrayAdapter
            mBinding.spnCountry.setSelection(index)
            filterStates(countries[index].countryCode)
        } else {
            mBinding.spnCountry.adapter = null
            filterStates(countryCode)
        }
    }

    private fun filterStates(countryCode: String?) {
        var states: MutableList<COMStateMaster> = java.util.ArrayList()
        var index = -1
        var stateID = 100497
        if (mGeoAddress != null && mGeoAddress!!.stateID != null) stateID = mGeoAddress!!.stateID!!
        if (TextUtils.isEmpty(countryCode)) states = java.util.ArrayList() else {
            for (state in mResponseStatesList) {
                if (countryCode == state.countryCode) states.add(state)
                if (index <= -1 && stateID != 0 && state.stateID != null && stateID == state.stateID) index =
                    states.indexOf(state)
            }
        }
        if (index <= -1) index = 0
        if (states.size > 0) {
            val stateArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                states
            )
            mBinding.spnState.adapter = stateArrayAdapter
            mBinding.spnState.setSelection(index)
            filterCities(states[index].stateID!!)
        } else {
            mBinding.spnState.adapter = null
            filterCities(stateID)
        }
    }

    private fun filterCities(stateID: Int) {
        var cities: MutableList<VUCOMCityMaster> = java.util.ArrayList()
        var index = -1
        var cityID = 100312093
        if (mGeoAddress != null && mGeoAddress!!.cityID != null) cityID = mGeoAddress!!.cityID!!
        if (stateID <= 0) cities = java.util.ArrayList() else {
            for (city in mResponseCitiesList) {
                if (city.stateID != null && stateID == city.stateID) cities.add(city)
                if (index <= 0 && cityID != 0 && city.cityID != null && cityID == city.cityID) index =
                    cities.indexOf(city)
            }
        }
        if (index <= -1) index = 0
        if (cities.size > 0) {
            val cityArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                cities
            )
            mBinding.spnCity.adapter = cityArrayAdapter
            mBinding.spnCity.setSelection(index)
            filterZones(cities[index].cityID!!)
        } else {
            mBinding.spnCity.adapter = null
            filterZones(cityID)
        }
    }

    private fun filterZones(cityID: Int) {
        var zones: MutableList<COMZoneMaster> = java.util.ArrayList()
        var index = 0
        var zoneName: String? = ""
        if (mGeoAddress != null && mGeoAddress!!.zone != null) zoneName = mGeoAddress!!.zone
        if (cityID <= 0) zones = java.util.ArrayList() else {
            for (zone in mResponseZonesList) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone) index =
                    zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnZone.adapter = zoneArrayAdapter
            mBinding.spnZone.setSelection(index)
            filterSectors(zones[index].zoneID!!)
        } else {
            mBinding.spnZone.adapter = null
            filterSectors(0)
        }
    }

    private fun filterSectors(zoneID: Int) {
        var sectors: MutableList<COMSectors?> = java.util.ArrayList()
        var index = 0
        var sectorID = 0
        if (mGeoAddress != null && mGeoAddress!!.sectorID != null) sectorID =
            mGeoAddress!!.sectorID!!
        if (zoneID <= 0) sectors = java.util.ArrayList() else {
            for (sector in mResponseSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId) index =
                    sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            if(isActionEnabled) mBinding.spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                sectors
            )
            mBinding.spnSector.adapter = sectorArrayAdapter
            mBinding.spnSector.setSelection(index)
        } else {
            mBinding.spnSector.adapter = null
            mBinding.spnSector.isEnabled = false
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave, R.id.btnNext -> {
                mListener?.hideKeyBoard()
                if (validateView())
                    saveBusinessOwnership(getPayload(), null)

                if (v.id == R.id.btnSave) {
                    mBinding.btnSave.tag = getString(R.string.save)
                } else if (v.id == R.id.btnNext) {
                    mBinding.btnNext.tag = getString(R.string.next)
                }
            }

            R.id.llDocuments -> {
                when {
                    //null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId -> {
                    mBusinessOwnership != null && mBusinessOwnership?.contactID != null && mBusinessOwnership?.contactID != 0 -> {
                        val fragment = DocumentsMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(
                            KEY_QUICK_MENU,
                            Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
                        )
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, mBusinessOwnership?.contactID ?: 0)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)

                        mListener?.showToolbarBackButton(R.string.documents)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveBusinessOwnership(getPayload(), v)
                    }
                }
            }

            R.id.llNotes -> {
                when {
                    //null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId -> {
                    mBusinessOwnership != null && mBusinessOwnership?.contactID != null && mBusinessOwnership?.contactID != 0 -> {
                        val fragment = NotesMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(
                            KEY_QUICK_MENU,
                            Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
                        )
                        bundle.putInt(
                            Constant.KEY_PRIMARY_KEY, mBusinessOwnership?.contactID
                                ?: 0
                        )
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)

                        mListener?.showToolbarBackButton(R.string.notes)
                        mListener?.addFragment(fragment, true)
                    }

                    validateView() -> {
                        saveBusinessOwnership(getPayload(), v)
                    }
                }
            }

            R.id.llCards -> {
                when {
                    mBusinessOwnership != null && mBusinessOwnership?.contactID != null && mBusinessOwnership?.contactID != 0
                            && mBusinessOwnership?.zone != null && mBusinessOwnership?.sector != null -> {

                        val fragment = CardMasterFragment()
                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, mBusinessOwnership?.contactID ?: 0)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_IDENTITY_CARD)
                        mListener?.showToolbarBackButton(R.string.citizen_id_cards)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveBusinessOwnership(getPayload(), v)
                    }
                }
            }

            R.id.llAccountPhoneChildTab -> {
                val fragment = PhoneMasterFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(KEY_QUICK_MENU, fromScreen)
                if (mBusinessOwnership?.ownerAccountID != 0) {
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, mBusinessOwnership?.ownerAccountID ?: 0)
                } else {
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, mBusinessOwnership?.accountID ?: 0)
                }
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_ACCOUNT_PHONE)
                mListener?.showToolbarBackButton(R.string.title_phones)
                mListener?.addFragment(fragment, true)
            }

            R.id.llAccountEmailsChildTab -> {
                val fragment = EmailMasterFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(KEY_QUICK_MENU, fromScreen)
                /*  bundle.putInt(Constant.KEY_PRIMARY_KEY, mBusinessOwnership?.ownerAccountID ?: 0)*/
                if (mBusinessOwnership?.ownerAccountID != 0) {
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, mBusinessOwnership?.ownerAccountID ?: 0)
                } else {
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, mBusinessOwnership?.accountID ?: 0)
                }
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_ACCOUNT_EMAIL)
                mListener?.showToolbarBackButton(R.string.title_emails)
                mListener?.addFragment(fragment, true)
            }

            R.id.llAccountAddressChildTab -> {
                val fragment = AddressMasterFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(KEY_QUICK_MENU, fromScreen)
                bundle.putInt(Constant.KEY_PRIMARY_KEY, mBusinessOwnership?.accountID ?: 0)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_ACCOUNT_ADDRESSES_LIST)

                mListener?.showToolbarBackButton(R.string.title_address)
                mListener?.addFragment(fragment, true)
            }
            R.id.copy_address_btn -> {
                if (mBusinessGeoAddress != null) {
                    mGeoAddress = mBusinessGeoAddress
                    if (mGeoAddress != null) {
                        mBinding.edtStreet.setText(mGeoAddress!!.street)
                        mBinding.edtZipCode.setText(mGeoAddress!!.zipCode)
                        mBinding.edtPlot.setText(mGeoAddress!!.plot)
                        mBinding.edtBlock.setText(mGeoAddress!!.block)
                        mBinding.edtDoorNo.setText(mGeoAddress!!.doorNo)
                        mBinding.edtDescription.setText(mGeoAddress!!.description)
                        filterCountries()
                    }
                }
            }
            /*  R.id.edtSycoTaxID -> {
                  getCitizenSycoTaxIDs()
              }*/
            R.id.btnScan -> {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(
                    Constant.KEY_QUICK_MENU,
                    Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
                )
                startActivityForResult(intent, Constant.REQUEST_CODE_SCAN_BUSINESS_OWNER)
            }
        }
    }

    private fun getPayload(): StoreCustomerB2C {
        mBinding.btnSave.isEnabled = false

        val businessOwnership = BusinessOwnership()
        businessOwnership.accountContactID = mBusinessOwnership?.accountContactID ?: 0
        businessOwnership.businessOwnerID = mBusinessOwnership?.businessOwnerID
        businessOwnership.citizenID = mBusinessOwnership?.citizenID
        businessOwnership.contactID = mBusinessOwnership?.contactID
        businessOwnership.taxPayerAccountID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
        if (!TextUtils.isEmpty(mBinding.edtSycoTaxID.text.toString().trim())) {
            businessOwnership.citizenSycoTaxID = mBinding.edtSycoTaxID.text.toString().trim()
        }
        if (!TextUtils.isEmpty(mBinding.edtFirstName.text.toString().trim()))
            businessOwnership.firstName = mBinding.edtFirstName.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtLastName.text.toString().trim()))
            businessOwnership.lastName = mBinding.edtLastName.text.toString().trim()
        if (mBinding.spnStatus.selectedItem != null)
            businessOwnership.statusCode =
                (mBinding.spnStatus.selectedItem as COMStatusCode).statusCode

        if (mBinding.spnProfession.selectedItem != null && (mBinding.spnProfession.selectedItem as CRMProfessions).professionID != -1) {
            businessOwnership.professionID =
                (mBinding.spnProfession.selectedItem as CRMProfessions).professionID
            businessOwnership.profession =
                (mBinding.spnProfession.selectedItem as CRMProfessions).profession
        } else {
            businessOwnership.professionID = 0
            businessOwnership.profession = ""
        }
        if (!TextUtils.isEmpty(mBinding.edtIfuNo.text.toString().trim()))
            businessOwnership.ifu = mBinding.edtIfuNo.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtDescription.text.toString().trim()))
            businessOwnership.remarks = mBinding.edtDescription.text.toString().trim()
        if (mBinding.edtPhoneNumber.text != null && !TextUtils.isEmpty(mBinding.edtPhoneNumber.text.toString()))
            businessOwnership.phone = mBinding.edtPhoneNumber.text.toString().trim()
        else
            businessOwnership.phone = ""
        if (mBinding.edtEmail.text != null && !TextUtils.isEmpty(mBinding.edtEmail.text.toString()))
            businessOwnership.email = mBinding.edtEmail.text.toString().trim()
        else
            businessOwnership.email = ""
        if (mBinding.edtDob.text != null && !TextUtils.isEmpty(mBinding.edtDob.text.toString()))
            businessOwnership.dob = serverFormatDate(mBinding.edtDob.text.toString().trim())
        if (mBinding.spnTelephoneCode.selectedItem != null)
            businessOwnership.telCode = mBinding.spnTelephoneCode.selectedItem as Int?


        val comCountryMaster = mBinding.spnCountry.selectedItem as COMCountryMaster?
        if (comCountryMaster?.countryCode != null) {
            businessOwnership.countryCode = comCountryMaster.countryCode
            businessOwnership.country = comCountryMaster.country
        }

        val comStateMaster = mBinding.spnState.selectedItem as COMStateMaster?
        if (comStateMaster?.state != null)
            businessOwnership.state = comStateMaster.state

        val vucomCityMaster = mBinding.spnCity.selectedItem as VUCOMCityMaster?
        if (vucomCityMaster?.city != null)
            businessOwnership.city = vucomCityMaster.city


        val comZoneMaster = mBinding.spnZone.selectedItem as COMZoneMaster?
        if (comZoneMaster?.zone != null)
            businessOwnership.zone = comZoneMaster.zone

        val comSectors = mBinding.spnSector.selectedItem as COMSectors?
        if (comSectors?.sectorId != null) {
            businessOwnership.sectorId = comSectors.sectorId
            businessOwnership.sector = comSectors.sector
        }

        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) businessOwnership.street =
            mBinding.edtStreet.text.toString().trim { it <= ' ' }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) businessOwnership.zipCode =
            mBinding.edtZipCode.text.toString().trim { it <= ' ' }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) businessOwnership.section =
            mBinding.edtPlot.text.toString().trim { it <= ' ' }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) businessOwnership.lot =
            mBinding.edtBlock.text.toString().trim { it <= ' ' }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(
                mBinding.edtDoorNo.text.toString().trim { it <= ' ' })
        ) businessOwnership.pacel = mBinding.edtDoorNo.text.toString().trim { it <= ' ' }
        if (mBinding.edtDescription.text != null && !TextUtils.isEmpty(mBinding.edtDescription.text.toString())) businessOwnership.description =
            mBinding.edtDescription.text.toString().trim { it <= ' ' }


        businessOwnership.drivingLicenseNo = mBinding.edtDrivingLicenseNumber.text.toString().trim()
        val customerB2C = StoreCustomerB2C()
        customerB2C.businessOwnership = businessOwnership
        customerB2C.geoAddress = prepareData()
        customerB2C.attachment = mDocumentsList
        customerB2C.note = mComNotesList

        return customerB2C
    }

    private fun prepareData(): GeoAddress {
        val geoAddress = GeoAddress()

        geoAddress.geoAddressID = mBusinessOwnership?.addressId
        geoAddress.accountId = mBusinessOwnership?.accountID

        // region Spinner Data
        val countryMaster = mBinding.spnCountry.selectedItem as COMCountryMaster
        if (countryMaster.countryCode != null) {
            geoAddress.countryCode = countryMaster.countryCode
            geoAddress.country = countryMaster.country
        }
        val comStateMaster = mBinding.spnState.selectedItem as COMStateMaster?
        if (comStateMaster?.state != null) {
            geoAddress.state = comStateMaster.state
            geoAddress.stateID = comStateMaster.stateID
        }
        val comCityMaster = mBinding.spnCity.selectedItem as VUCOMCityMaster?
        if (comCityMaster?.city != null) geoAddress.city = comCityMaster.city
        val comZoneMaster = mBinding.spnZone.selectedItem as COMZoneMaster?
        if (comZoneMaster?.zone != null) geoAddress.zone = comZoneMaster.zone
        val comSectors = mBinding.spnSector.selectedItem as COMSectors?
        if (comSectors?.sectorId != null) {
            geoAddress.sectorID = comSectors.sectorId
            geoAddress.sector = comSectors.sector
        }
        // endregion
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) geoAddress.street =
            mBinding.edtStreet.text.toString().trim { it <= ' ' }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) geoAddress.zipCode =
            mBinding.edtZipCode.text.toString().trim { it <= ' ' }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) geoAddress.plot =
            mBinding.edtPlot.text.toString().trim { it <= ' ' }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) geoAddress.block =
            mBinding.edtBlock.text.toString().trim { it <= ' ' }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(
                mBinding.edtDoorNo.text.toString().trim { it <= ' ' })
        ) geoAddress.doorNo = mBinding.edtDoorNo.text.toString().trim { it <= ' ' }
        if (mBinding.edtDescription.text != null && !TextUtils.isEmpty(mBinding.edtDescription.text.toString())) geoAddress.description =
            mBinding.edtDescription.text.toString().trim { it <= ' ' }
        return geoAddress
    }

    private fun saveBusinessOwnership(customerB2C: StoreCustomerB2C, view: View?) {
        if (0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
            APICall.storeCustomerB2C(customerB2C, object : ConnectionCallBack<BusinessOwnership> {

                override fun onSuccess(response: BusinessOwnership) {
                    mListener?.dismissDialog()
                    //mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))

                    mBinding.llAccountAddressChildTab.visibility = GONE
                    mBinding.llAccountEmailsChildTab.visibility = VISIBLE
                    mBinding.llAccountPhoneChildTab.visibility = VISIBLE
                    mBinding.llCards.visibility = VISIBLE

                    /*  if (mBusinessOwnership != null && "0" != mBusinessOwnership?.businessOwnerID)
                          mListener?.showToast(R.string.msg_record_update_success)
                      else
                          mListener?.showToast(R.string.msg_record_save_success)*/

                    response.businessOwnerID?.let {
                        if (!TextUtils.isEmpty(it)) {
                            mBinding.tvBusinessOwnerID.text = it
                            mBinding.llBusinessOwnerID.visibility = VISIBLE
                        }
                    }

                    response.citizenID?.let {
                        if (!TextUtils.isEmpty(it)) {
                            mBinding.tvCitizenID.text = it
                            mBinding.llCitizenID.visibility = VISIBLE
                        }
                    }

                    if (mBusinessOwnership == null)
                        mBusinessOwnership = customerB2C.businessOwnership
                    mBusinessOwnership?.businessOwnerID = response.businessOwnerID
                    mBusinessOwnership?.citizenID = response.citizenID
                    mBusinessOwnership?.contactID = response.contactID
                    mBusinessOwnership?.accountID = response.accountID

                    if (mBinding.btnSave.visibility == GONE) {
                        mBinding.btnSave.visibility = VISIBLE
                        mBinding.btnNext.visibility = GONE
                    }

                    Handler().postDelayed({
                        val intent = Intent()
                        if (mBusinessOwnership?.contactName.isNullOrBlank())
                            mBusinessOwnership?.contactName =
                                "${mBusinessOwnership?.firstName ?: ""} ${mBusinessOwnership?.lastName ?: ""}"
                        if (mBusinessOwnership?.accountName.isNullOrEmpty())
                            mBusinessOwnership?.accountName =
                                "${mBusinessOwnership?.firstName ?: ""} ${mBusinessOwnership?.lastName ?: ""}"
                        /*  if (mBusinessOwnership?.citizenSycoTaxID.isNullOrEmpty())
                              mBusinessOwnership?.citizenSycoTaxID = mBinding.edtSycoTaxID.text.toString().trim()*/
                        mBusinessOwnership?.email = mBinding.edtEmail.text.toString()
                        mBusinessOwnership?.emails = mBinding.edtEmail.text.toString()
                        mBusinessOwnership?.ifu = mBinding.edtIfuNo.text.toString()

                        intent.putExtra(Constant.KEY_BUSINESS_OWNER, mBusinessOwnership)
                        intent.putExtra(Constant.KEY_STOP_TITLE_RESET, mBinding.btnSave.tag == mBinding.btnSave.context.getString(R.string.save))

                        targetFragment?.onActivityResult(
                            targetRequestCode,
                            Activity.RESULT_OK,
                            intent
                        )
                        if (view == null && mBinding.llAccountPhoneChildTab.visibility == VISIBLE) {
                            if (mBinding.btnSave.tag == mBinding.btnSave.context.getString(R.string.save)) {
                                if (isEdit)
                                    mListener?.showToast(R.string.msg_record_update_success)
                                else
                                    mListener?.showToast(R.string.msg_record_save_success)
                                if (context is RegisterOwnerActivity)
                                    mListener?.finish()
                                else mListener?.popBackStack()
                            } else if (mBinding.btnNext.tag == mBinding.btnNext.context.getString(R.string.next)) {
                                fetchChildEntriesCount()
                            }
                        } else if (view == mBinding.llCards) {
                            mBinding.llCards.callOnClick()
                        } else if (mBinding.btnNext.tag == mBinding.btnNext.context.getString(R.string.next)) {
                            fetchChildEntriesCount()
                        }
                    }, 750)

                    mBinding.btnSave.isEnabled = true

                    onClick(view)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)

                    /*  mBinding.llAccountAddressChildTab.visibility = GONE
                      mBinding.llAccountEmailsChildTab.visibility = GONE
                      mBinding.llAccountPhoneChildTab.visibility = GONE
                      mBinding.llCards.visibility = GONE*/
                    mBinding.btnSave.isEnabled = true
                }
            })
        }
        /*else {
            mListener?.showAlertDialog("In complete flow")

            when (fromScreen) {

                Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER,
                Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER -> {

                    ObjectHolder.registerBusiness.customerB2Cs.add(customerB2C)
                    ObjectHolder.registerBusiness.businessOwners.add(businessOwnership)

                    var isExist = false
                    for (obj: ObjBusinessOwner in ObjectHolder.registerBusiness.insertBusinessOwnership) {
                        if (obj.businessOwnership.accountContactID == businessOwnership.accountContactID) {
                            obj.businessOwnership = businessOwnership
                            isExist = true
                            break
                        }
                    }
                    if (!isExist) {
                        val objBusinessOwner = ObjBusinessOwner()
                        businessOwnership.accountContactID = java.util.UUID.randomUUID().hashCode()
                        objBusinessOwner.businessOwnership = businessOwnership
                        ObjectHolder.registerBusiness.insertBusinessOwnership.add(objBusinessOwner)
                    }

                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 500)
                }

                else -> {

                }
            }
        }*/

    }

    private fun validateView(): Boolean {
        /* if (mBinding.edtSycoTaxID.text.toString().isEmpty()) {
             mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.syco_tax_id))
             return false
         }*/
        if (mBinding.edtFirstName.text != null && TextUtils.isEmpty(
                mBinding.edtFirstName.text.toString().trim()
            )
        ) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.first_name))
            return false
        }

        /*if (mBinding.edtPhoneNumber.text != null && TextUtils.isEmpty(mBinding.edtPhoneNumber.text.toString().trim())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.phone_number))
            return false
        }

        if (mBinding.edtEmail.text != null && TextUtils.isEmpty(mBinding.edtEmail.text.toString().trim())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.email))
            return false
        }*/

        /*if (mBinding.spnProfession.selectedItem == null || -1 == (mBinding.spnProfession.selectedItem as CRMProfessions).professionID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.profession))
            return false
        }*/

        if (mBinding.spnStatus.selectedItem == null || "-1" == (mBinding.spnStatus.selectedItem as COMStatusCode).statusCode) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.status))
            return false
        }
        if (mBinding.spnCountry.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.country)}")
            return false
        }
        if (mBinding.spnState.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.state)}")
            return false
        }
        if (mBinding.spnCity.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.city)}")
            return false
        }
        if (mBinding.spnZone.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.zone)}")
            return false
        }
        if (mBinding.spnSector.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.sector)}")
            return false
        }
        return true
    }

    private fun fetchCount(
        filterColumns: List<FilterColumn>,
        tableCondition: String,
        tableOrViewName: String,
        primaryKeyColumnName: String
    ) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = tableOrViewName
        tableDetails.primaryKeyColumnName = primaryKeyColumnName
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = tableCondition
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                bindCounts(tableOrViewName, 0)
            }

            override fun onSuccess(response: Int) {
                bindCounts(tableOrViewName, response)
            }
        })
    }

    private fun fetchChildEntriesCount() {
        mBusinessOwnership?.accountID?.let {
            val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
            var filterColumn = FilterColumn()
            filterColumn.columnName = "PrimaryKeyValue"
            filterColumn.columnValue = "${mBusinessOwnership?.contactID}"
            filterColumn.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            filterColumn = FilterColumn()
            filterColumn.columnName = "TableName"
            /*  if (fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_CART_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX) {
                  filterColumn.columnValue = "CRM_Carts"
              } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_WEAPON_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX) {
                  filterColumn.columnValue = "CRM_Weapons"
              } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE) {
                  filterColumn.columnValue = "CRM_GamingMachines"
              } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE)
                  filterColumn.columnValue = "LAW_ViolationTickets"
              else {
                  filterColumn.columnValue = "CRM_Contacts"
              }*/
            filterColumn.columnValue = "CRM_Contacts"
            filterColumn.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            fetchCount(listFilterColumn, "AND", "COM_DocumentReferences", "DocumentReferenceID")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fetchChildEntriesCount()
        if (data != null) {
            val intent = Intent()
            if (requestCode == Constant.REQUEST_CODE_ACCOUNT_PHONE) {
                if (!data.getStringExtra("PHONE_NUMBER").isNullOrEmpty())
                    mBinding.edtPhoneNumber.setText(data.getStringExtra("PHONE_NUMBER"))
                else {
                    mBinding.edtPhoneNumber.setText("")
                }
            } else if (requestCode == Constant.REQUEST_CODE_ACCOUNT_EMAIL) {
                if (!data.getStringExtra("EMAIL").isNullOrEmpty())
                    mBinding.edtEmail.setText(data.getStringExtra("EMAIL"))
                else {
                    mBinding.edtEmail.setText("")
                }
            } else if (requestCode == Constant.REQUEST_CODE_SCAN_BUSINESS_OWNER) {
                var sycoTaxID: String? = ""
                if (data.hasExtra(Constant.KEY_SYCO_TAX_ID))
                    sycoTaxID = data.getStringExtra(Constant.KEY_SYCO_TAX_ID)
                mBinding.edtSycoTaxID.setText(sycoTaxID)
            }
            if (data.hasExtra(KEY_QUICK_MENU) && (data.getSerializableExtra(KEY_QUICK_MENU) as Constant.QuickMenu) == fromScreen) {
                data.let {
                    if (it.hasExtra(Constant.KEY_ADDRESS)) {
                        val mCustomer =
                            it.getParcelableExtra(Constant.KEY_ADDRESS) as BusinessOwnership
                        setAddress(mCustomer)
                    }
                }
            } else {
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
            }
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_CITIZEN) {
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_CITIZEN) {
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
        }
    }

    private fun setAddress(mCustomer: BusinessOwnership) {
        if (mBusinessOwnership != null) {
            mBusinessOwnership!!.country = mCustomer.country
            mBusinessOwnership!!.state = mCustomer.state
            mBusinessOwnership!!.city = mCustomer.city
            mBusinessOwnership!!.zone = mCustomer.zone
            mBusinessOwnership!!.sector = mCustomer.sector
            mBusinessOwnership!!.street = mCustomer.street
            mBusinessOwnership!!.section = mCustomer.section
            mBusinessOwnership!!.lot = mCustomer.lot
            mBusinessOwnership!!.pacel = mCustomer.pacel
            mBusinessOwnership!!.zipCode = mCustomer.zipCode
            mBusinessOwnership!!.description = mCustomer.description
        }
    }

    private fun bindCounts(tableOrViewName: String, count: Int) {
        when (tableOrViewName) {
            "COM_DocumentReferences" -> {
                mBinding.txtNumberOfDocuments.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue = "${mBusinessOwnership?.contactID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "TableName"
                /* if (fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_CART_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX) {
                      filterColumn.columnValue = "CRM_Carts"
                  } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_WEAPON_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX) {
                      filterColumn.columnValue = "CRM_Weapons"
                  } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE) {
                      filterColumn.columnValue = "CRM_GamingMachines"
                  } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE)
                      filterColumn.columnValue = "LAW_ViolationTickets"
                  else {
                      filterColumn.columnValue = "CRM_Contacts"
                 }*/
                filterColumn.columnValue = "CRM_Contacts"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
                mBinding.llDocuments.isEnabled =
                    !(mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfDocuments.text.toString()
                        .toInt() == 0)
            }
            "COM_Notes" -> {
                mBinding.txtNumberOfNotes.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                if (mBusinessOwnership?.ownerAccountID != 0) {
                    filterColumn.columnValue = "${mBusinessOwnership?.ownerAccountID}"
                } else {
                    filterColumn.columnValue = "${mBusinessOwnership?.accountID}"
                }
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "CRM_AccountPhones", "AccountPhoneID")
                mBinding.llNotes.isEnabled =
                    !(mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfNotes.text.toString()
                        .toInt() == 0)
            }
            "CRM_AccountPhones" -> {
                mBinding.txtNumberOfPhones.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                if (mBusinessOwnership?.ownerAccountID != 0) {
                    filterColumn.columnValue = "${mBusinessOwnership?.ownerAccountID}"
                } else {
                    filterColumn.columnValue = "${mBusinessOwnership?.accountID}"
                }
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "CRM_AccountEmails", "AccountEmailID")
                mBinding.llAccountPhoneChildTab.isEnabled =
                    !(mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfPhones.text.toString()
                        .toInt() == 0)
            }
            "CRM_AccountEmails" -> {
                mBinding.txtNumberOfEmails.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                if (mBusinessOwnership?.ownerAccountID != 0) {
                    filterColumn.columnValue = "${mBusinessOwnership?.ownerAccountID}"
                } else {
                    filterColumn.columnValue = "${mBusinessOwnership?.accountID}"
                }
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "VU_CRM_AccountAddresses", "AccountAddressID")
                mBinding.llAccountEmailsChildTab.isEnabled =
                    !(mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfEmails.text.toString()
                        .toInt() == 0)
            }
            "VU_CRM_AccountAddresses" -> {
                mBinding.txtNumberOfAddresses.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "ContactID"
                filterColumn.columnValue = "${mBusinessOwnership?.contactID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "CRM_CitizenIdentityCards", "CitizenCardID")
                mBinding.llAccountAddressChildTab.isEnabled =
                    !(mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfAddresses.text.toString()
                        .toInt() == 0)
            }
            "CRM_CitizenIdentityCards" -> {
                mBinding.txtNumberOfCards.text = "$count"
                mBinding.llCards.isEnabled =
                    !(mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfCards.text.toString()
                        .toInt() == 0)
            }
        }
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
        fun finish()
        fun showToast(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode

    }

}
