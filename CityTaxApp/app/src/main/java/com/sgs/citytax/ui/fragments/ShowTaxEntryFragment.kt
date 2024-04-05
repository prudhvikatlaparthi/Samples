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
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentShowTaxEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.displayDateFormat
import java.util.*

class ShowTaxEntryFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentShowTaxEntryBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mTaskCode: String? = ""
    private var taxData: ShowsDetailsTable? = null

    private var mResponseCountriesList: List<COMCountryMaster> = arrayListOf()
    private var mResponseStatesList: List<COMStateMaster> = arrayListOf()
    private var mResponseCitiesList: List<VUCOMCityMaster> = arrayListOf()
    private var mResponseZonesList: List<COMZoneMaster> = arrayListOf()
    private var mResponseSectorsList: List<COMSectors> = arrayListOf()
    private var operatorTypes: List<VUCRMTypeOfOperators> = arrayListOf()
    private var geoAddress: GeoAddress? = null
    private var mTaxRuleBookCode: String? = ""
    private var isActionEnabled: Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_tax_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            taxData = arguments?.getParcelable(Constant.KEY_SHOW)
            mTaskCode = it.getString(Constant.KEY_TASK_CODE)
            mTaxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)
        }
        taxData?.geoAddress?.let {
            geoAddress = it[0]

        }
        setViews()
        bindSpinners()
        setListeners()

    }

    private fun setViews() {
        mBinding.edtStartDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtStartDate.setDisplayDateFormat(displayDateFormat)

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
            Constant.ScreenMode.ADD -> if(taxData!=null && taxData?.allowDelete=="N")setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {
        isActionEnabled = action
        mBinding.spnOperatorType.isEnabled = action
        mBinding.edtShowName.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.chkActive.isEnabled = action
        mBinding.edtStartDate.isEnabled = action
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

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }

    private fun setEditActionForSave(action: Boolean) {
        isActionEnabled = action
        mBinding.spnOperatorType.isEnabled = action
        mBinding.edtShowName.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.edtStartDate.isEnabled = action
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
        mBinding.chkActive.isEnabled = true
        mBinding.btnSave.visibility = View.VISIBLE
    }

    private fun bindSpinners() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_Shows", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {

                mResponseCountriesList = response.countryMaster
                mResponseStatesList = response.stateMaster
                mResponseCitiesList = response.cityMaster
                mResponseZonesList = response.zoneMaster
                mResponseSectorsList = response.sectors
                operatorTypes = response.operatorTypes

                filterCountries()

                if (operatorTypes.isNotEmpty()) {
                    val operatorTypesAdapter = ArrayAdapter<VUCRMTypeOfOperators>(requireContext(), android.R.layout.simple_list_item_1, operatorTypes)
                    mBinding.spnOperatorType.adapter = operatorTypesAdapter
                }

                bindData()

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindData() {
        taxData?.let { data ->
            for ((index, operatorType) in operatorTypes.withIndex()) {
                if (data.operatorTypeID == operatorType.operatorTypeId) {
                    mBinding.spnOperatorType.setSelection(index)
                    break
                }
            }
            data.showName?.let {
                mBinding.edtShowName.setText(it)
            }
            data.description?.let {
                mBinding.edtDescription.setText(it)
            }
            mBinding.chkActive.isChecked = data.active == "Y"

            geoAddress?.let {
                it.street?.let {
                    mBinding.edtStreet.setText(it)
                }
                it.plot?.let {
                    mBinding.edtPlot.setText(it)
                }
                it.block?.let {
                    mBinding.edtBlock.setText(it)
                }
                it.doorNo?.let {
                    mBinding.edtDoorNo.setText(it)
                }
                it.zipCode?.let {
                    mBinding.edtZipCode.setText(it)
                }
            }
            bindCounts()

            //todo Commented for this release(13/01/2022)
//            getInvoiceCount4Tax()
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
            {
                setEditAction(false)
            }
        }
    }

    private fun setListeners() {

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

        mBinding.llDocuments.setOnClickListener(this)
        mBinding.btnSave.setOnClickListener(this)
    }

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = arrayListOf()
        var index = -1
        var countryCode: String? = "BFA"
        geoAddress?.countryCode?.let {
            countryCode = it
        }
        for (country in mResponseCountriesList) {
            countries.add(country)
            if (index <= -1 && countryCode != null && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode) index = countries.indexOf(country)
        }
        if (index <= -1)
            index = 0
        if (countries.size > 0) {
            val countriesAdapter = ArrayAdapter<COMCountryMaster>(requireContext(), android.R.layout.simple_list_item_1, countries)
            mBinding.spnCountry.adapter = countriesAdapter
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
        geoAddress?.stateID?.let {
            stateID = it
        }
        if (TextUtils.isEmpty(countryCode)) states = java.util.ArrayList() else {
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
        var cities: MutableList<VUCOMCityMaster> = java.util.ArrayList()
        var index = -1
        var cityID = 100312093
        geoAddress?.cityID?.let {
            cityID = it
        }
        if (stateID <= 0) cities = java.util.ArrayList() else {
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
        var zones: MutableList<COMZoneMaster> = java.util.ArrayList()
        var index = 0
        var zoneName: String? = ""
        geoAddress?.zone?.let {
            zoneName = it
        }
        if (cityID <= 0) zones = java.util.ArrayList() else {
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
        var sectors: MutableList<COMSectors?> = java.util.ArrayList()
        var index = 0
        var sectorID = 0
        geoAddress?.sectorID?.let {
            sectorID = it
        }
        if (zoneID <= 0) sectors = java.util.ArrayList() else {
            for (sector in mResponseSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId) index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            if(isActionEnabled) mBinding.spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnSector.adapter = sectorArrayAdapter
            mBinding.spnSector.setSelection(index)
        } else {
            mBinding.spnSector.adapter = null
            mBinding.spnSector.isEnabled = false
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.llDocuments -> {
                    when {
                        taxData != null && taxData?.showID != 0 -> {
                            val fragment = DocumentsMasterFragment()

                            //region SetArguments
                            val bundle = Bundle()
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SHOW_TAX)
                            bundle.putInt(Constant.KEY_PRIMARY_KEY, taxData?.showID ?: 0)
                            fragment.arguments = bundle
                            //endregion

                            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                            mListener?.showToolbarBackButton(R.string.documents)
                            mListener?.addFragment(fragment, true)
                        }
                        validateView() -> {
                            save(view)
                        }
                        else -> {

                        }
                    }
                }

                R.id.btnSave -> {
                    if (validateView()) {
                        mListener?.showAlertDialog(R.string.are_you_sure_you_have_entered_all_valid_information,
                                R.string.yes,
                                View.OnClickListener {
                                    val dialog = (it as Button).tag as AlertDialog
                                    dialog.dismiss()
                                    save()
                                },
                                R.string.no,
                                View.OnClickListener
                                {
                                    val dialog = (it as Button).tag as AlertDialog
                                    dialog.dismiss()
                                })
                    } else {
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun getShowData(): ShowTaxData {
        val data = ShowTaxData()

        if (taxData != null && taxData!!.showID != 0)
            data.showID = taxData?.showID

        if (mBinding.edtShowName.text.toString().isNotEmpty())
            data.showName = mBinding.edtShowName.text.toString().trim()
        else
            data.showName = ""

        if (mBinding.edtDescription.text.toString().isNotEmpty())
            data.description = mBinding.edtDescription.text.toString().trim()
        else
            data.description = ""

        if (mBinding.spnOperatorType.selectedItem != null) {
            val operatorType = mBinding.spnOperatorType.selectedItem as VUCRMTypeOfOperators
            data.operatorTypeId = operatorType.operatorTypeId
        }

        if (mBinding.chkActive.isChecked)
            data.active = "Y"
        else
            data.active = "N"

        data.organizationId = ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId


        //data.startDate = serverFormatDate(mBinding.edtStartDate.text.toString())


        return data
    }

    private fun prepareAddressData(): GeoAddress {
        val geoAddress = GeoAddress()
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
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) geoAddress.street = mBinding.edtStreet.text.toString().trim { it <= ' ' }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) geoAddress.zipCode = mBinding.edtZipCode.text.toString().trim { it <= ' ' }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) geoAddress.plot = mBinding.edtPlot.text.toString().trim { it <= ' ' }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) geoAddress.block = mBinding.edtBlock.text.toString().trim { it <= ' ' }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(mBinding.edtDoorNo.text.toString().trim { it <= ' ' })) geoAddress.doorNo = mBinding.edtDoorNo.text.toString().trim { it <= ' ' }
        return geoAddress
    }

    private fun save(view: View? = null) {
        mListener?.showProgressDialog()
        APICall.storeShows(getShowData(), prepareAddressData(), object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                if (taxData == null) taxData = ShowsDetailsTable()
                taxData?.showID = response
                if (view == null) {
                    mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 780)
                } else
                    onClick(view)

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun validateView(): Boolean {
        if (mBinding.edtShowName.text.toString().isEmpty() || TextUtils.isEmpty(mBinding.edtShowName.text.toString())) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.show_name)}")
            return false
        }

        val operatorType = mBinding.spnOperatorType.selectedItem as VUCRMTypeOfOperators?
        if (operatorType?.operatorType == null || operatorType.operatorTypeId == -1) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.operator_type))
            mBinding.spnOperatorType.requestFocus()
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

    private fun fetchCount(filterColumns: List<FilterColumn>) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "COM_DocumentReferences"
        tableDetails.primaryKeyColumnName = "DocumentReferenceID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "AND"
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                mBinding.txtNumberOfDocuments.text = "0"
            }

            override fun onSuccess(response: Int) {
                mBinding.txtNumberOfDocuments.text = "$response"
            }
        })
    }

    private fun bindCounts() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "CRM_Shows"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${taxData?.showID}"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        fetchCount(listFilterColumn)
    }

    private fun getInvoiceCount4Tax() {
        val currentDue = CheckCurrentDue()
        currentDue.accountId = taxData?.acctid
        currentDue.vchrno  = taxData?.showID
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bindCounts()
    }


    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun popBackStack()
        fun showSnackbarMsg(message: String)
        fun showAlertDialog(message: String)
        fun showToolbarBackButton(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener)

    }
}