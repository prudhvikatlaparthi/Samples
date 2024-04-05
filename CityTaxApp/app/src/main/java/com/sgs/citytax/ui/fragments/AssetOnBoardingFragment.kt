package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.*
import com.sgs.citytax.databinding.FragmentAssetOnboardingBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.custom.DatePickerEditText
import com.sgs.citytax.util.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList


class AssetOnBoardingFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentAssetOnboardingBinding
    private var listener: Listener? = null

    private val editTextList = arrayListOf<EditText>()
    private val spinnerList = arrayListOf<Spinner>()
    private val checkBoxList = arrayListOf<CheckBox>()

    private var mandatoryEditList: ArrayList<Int> = arrayListOf()
    private var mandatorySpinnerList: ArrayList<Int> = arrayListOf()
    private var mandatoryCheckBoxList: ArrayList<Int> = arrayListOf()

    private var statusCodesList: MutableList<COMStatusCode>? = null
    private var assetCategoriesList: MutableList<AssetCategory>? = null
    private var mUserOrgBranches: MutableList<UMXUserOrgBranches>? = null

    private var mResponseCountriesList: List<COMCountryMaster> = ArrayList()
    private var mResponseStatesList: List<COMStateMaster> = ArrayList()
    private var mResponseCitiesList: List<VUCOMCityMaster> = ArrayList()
    private var mResponseZonesList: List<COMZoneMaster> = ArrayList()
    private var mResponseSectorsList: List<COMSectors> = ArrayList()

    private var assetValues: Asset? = null
    private var assetSpecifications: MutableList<GetUpdateAssetSpecifications>? = null
    private var geoAddress: GeoAddress? = null
    private var updateAsset: GetUpdateAsset? = null

    private var fromScreen: Constant.QuickMenu? = null
    private var mHelper: LocationHelper? = null
    private var mSycoTaxID: String? = ""

    companion object {
        var assetID = 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = if (parentFragment != null)
                parentFragment as Listener
            else context as Listener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun initComponents() {
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            updateAsset = arguments?.getParcelable(Constant.KEY_UPDATE_ASSET)
            mSycoTaxID = arguments?.getString(Constant.KEY_SYCO_TAX_ID)
        }
        bindSpinner()
        setListeners()
        fetchChildEntriesCount()
    }
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        getCurrentLocation(view)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_asset_onboarding, container, false)
        initComponents()
//        container?.rootView?.let { getCurrentLocation(it) }
        return mBinding.root
    }


    fun bindSpinner() {
        listener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("AST_Assets", object : ConnectionCallBack<DataResponse> {
            override fun onFailure(message: String) {
                listener?.dismissDialog()
                listener?.showAlertDialog(message)
            }

            override fun onSuccess(response: DataResponse) {
                assetCategoriesList = response.assetCategories
                statusCodesList = response.statusCodes
                mUserOrgBranches = response.userOrgBranches

                mResponseCountriesList = response.countryMaster
                mResponseStatesList = response.stateMaster
                mResponseCitiesList = response.cityMaster
                mResponseZonesList = response.zoneMaster
                mResponseSectorsList = response.sectors

                filterCountries()

                if (assetCategoriesList.isNullOrEmpty())
                    mBinding.spnAssetCategory.adapter = null
                else {
                    assetCategoriesList?.add(0, AssetCategory(assetCategory = getString(R.string.select), assetCategoryID = -1))
                    val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, assetCategoriesList!!)
                    mBinding.spnAssetCategory.adapter = domainAdapter
                    mBinding.spnAssetCategory.post {
                        mBinding.spnAssetCategory.measure(0, 0)
                        val width = mBinding.spnAssetCategory.measuredWidth
                        mBinding.spnAssetCategory.dropDownWidth = requireContext().dpToPx((width * 1.5).toInt())
                    }
                }

                if (statusCodesList.isNullOrEmpty())
                    mBinding.spnStatus.adapter = null
                else {
                    statusCodesList?.add(0, COMStatusCode(getString(R.string.select), "-1"))
                    val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, statusCodesList!!)
                    mBinding.spnStatus.adapter = domainAdapter
                    mBinding.spnStatus.setSelection(1)
                }
                if (response.userOrgBranches.isNotEmpty() && response.userOrgBranches.isNotEmpty()) {
                    mUserOrgBranches?.add(0, UMXUserOrgBranches(getString(R.string.select), userOrgBranchID = -1))
                    val adminOfficeAdapter = ArrayAdapter<UMXUserOrgBranches>(activity!!.applicationContext, android.R.layout.simple_list_item_1, mUserOrgBranches!!)
                    mBinding.spnAgentAdminstrativOffice.adapter = adminOfficeAdapter
                } else mBinding.spnAgentAdminstrativOffice.adapter = null

                bindData()

                listener?.dismissDialog()
            }
        })
    }

    fun bindData() {
        mBinding.edtLifeTimeStartDate.setDisplayDateFormat(displayDateTimeTimeSecondFormat)
        mBinding.edtLifeTimeEndDate.setDisplayDateFormat(displayDateTimeTimeSecondFormat)

        //edit booking focus listener
        mBinding.edtBookingAdvance.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtBookingAdvance.text.toString()
                if(text?.isNotEmpty()!!)
                    mBinding.edtBookingAdvance.setText("${currencyToDouble(text)}");
            }else{
                //this if condition is true when edittext lost focus...
                //check here for number is larger than 10 or not
                if(!TextUtils.isEmpty(mBinding.edtBookingAdvance.text.toString())) {
                    val enteredText: Double = mBinding.edtBookingAdvance.text.toString().toDouble()
                    mBinding.edtBookingAdvance.setText("${formatWithPrecision(enteredText)}")
                }
            }
        }
        // security deposit focus listener
        mBinding.edtSecurityDeposit.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtSecurityDeposit.text.toString()
                if(text?.isNotEmpty()!!)
                    mBinding.edtSecurityDeposit.setText("${currencyToDouble(text)}");
            }else{
                //this if condition is true when edittext lost focus...
                //check here for number is larger than 10 or not
                if(!TextUtils.isEmpty(mBinding.edtSecurityDeposit.text.toString())) {
                    val enteredText: Double = mBinding.edtSecurityDeposit.text.toString().toDouble()
                    mBinding.edtSecurityDeposit.setText("${formatWithPrecision(enteredText)}")
                }
            }
        }


        if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET) {
            assetValues = updateAsset?.asset
            geoAddress = updateAsset?.geoAddress
            assetSpecifications = updateAsset?.assetSpecifications

            assetValues?.assetID?.let {
                assetID = it
            }

            assetValues?.let {
                mBinding.edtSycoTaxID.setText(it.assetSycotaxID)
                mBinding.edtSecurityDeposit.setText(formatWithPrecision(it.securityDeposit.toString()))
                mBinding.edtAssetNo.setText(it.assetNo)
                mBinding.edtBookingAdvance.setText(formatWithPrecision(it.bookingAdvance.toString()))

                mBinding.edtLifeTimeStartDate.setText(formatDisplayDateTimeInMillisecond(it.lifeTimeStartDate))
                mBinding.edtLifeTimeEndDate.setText(formatDisplayDateTimeInMillisecond(it.lifeTimeEndDate))
                mBinding.edtDescription.setText(it.description)
                if (it.allowInsurance == "Y") {
                    mBinding.llInsuranceLicense.visibility = View.VISIBLE
                    mBinding.viewInsurance.visibility = View.VISIBLE
                }

                if (it.allowFitness == "Y") {
                    mBinding.llFitness.visibility = View.VISIBLE
                    mBinding.viewFitness.visibility = View.VISIBLE
                }

                if (it.allowMaintenance == "Y") {
                    mBinding.llVehicleMaintenance.visibility = View.VISIBLE
                    mBinding.viewMaintenance.visibility = View.VISIBLE
                }

                assetCategoriesList?.let { it1 ->
                    for ((index, obj) in it1.withIndex()) {
                        if (assetValues?.assetCategoryID == obj.assetCategoryID) {
                            mBinding.spnAssetCategory.setSelection(index)
                            break
                        }
                    }
                }

                statusCodesList?.let { it2 ->
                    for ((index, obj) in it2.withIndex()) {
                        if (assetValues?.statusCode?.contentEquals(obj.statusCode.toString()) == true) {
                            mBinding.spnStatus.setSelection(index)
                            break
                        }
                    }
                }

                mUserOrgBranches?.let { mUserOrgBranches ->
                    for ((index, obj) in mUserOrgBranches.withIndex()) {
                        if (it.userOrgBranchID == obj.userOrgBranchID) {
                            mBinding.spnAgentAdminstrativOffice.setSelection(index)
                        }
                    }
                }

                mBinding.chkActive.isChecked = it.status == "Y"
            }

            geoAddress?.let {
                mBinding.llAddress.visibility = View.VISIBLE
                mBinding.llbtnNewAddAddress.visibility = View.GONE
                mBinding.edtStreet.setText(it.street)
                mBinding.edtZipCode.setText(it.zipCode)
                mBinding.edtPlot.setText(it.plot)
                mBinding.edtBlock.setText(it.block)
                mBinding.edtDoorNo.setText(it.doorNo)
                mBinding.edtAddDescription.setText(it.description)
                filterCountries()
                it.latitude?.let {
                    mBinding.edtLatitude.setText("${it}")
                }
                it.longitude?.let {
                    mBinding.edtLongitude.setText("${it}")
                }

            }

            assetSpecifications?.let {
                val assetSpecificationsList: ArrayList<AssetSpecs> = arrayListOf()
                for ((index, value) in it.withIndex()) {
                    val assetSpecs = AssetSpecs()
                    assetSpecs.dataType = it[index].dataType
                    assetSpecs.mandatory = it[index].dynamicForm?.mandatory
                    assetSpecs.specification = it[index].specification
                    assetSpecs.specificationID = it[index].specificationID
                    if (it[index].value != null)
                        assetSpecs.listValues = it[index].value

                    if (it[index].dateValue != null)
                        assetSpecs.dateValue = it[index].dateValue

                    if (it[index].specificationValueID != 0)
                        assetSpecs.specificationValueID = it[index].specificationValueID

                    assetSpecificationsList.add(assetSpecs)
                }
                bindDynamicData(assetSpecificationsList)
            }
        } else {
            val timeInMillis = Calendar.getInstance().timeInMillis
            mBinding.edtLifeTimeStartDate.setMaxDate(timeInMillis)
            mBinding.edtLifeTimeEndDate.setMinDate(timeInMillis)
            mBinding.edtSycoTaxID.setText(mSycoTaxID)
        }

    }

    fun getDynamicFormSpecifications4Asset(categoryID: Int) {
        APICall.getDynamicFormSpecs4Asset(categoryID, object : ConnectionCallBack<List<AssetSpecs>> {
            override fun onFailure(message: String) {
                listener?.dismissDialog()
                clearAll()
            }

            override fun onSuccess(response: List<AssetSpecs>) {
                listener?.dismissDialog()
                clearAll()
                if (response.isNotEmpty())
                    bindDynamicData(response)
            }
        })
    }

    fun bindDynamicData(assetSpecificationsList: List<AssetSpecs>) {
        for (i in assetSpecificationsList.indices) {
            if (assetSpecificationsList[i].dataType == Constant.DynamicFormDataTypes.Checkbox.value) {
                val checkBox = CheckBox(requireContext())
                checkBox.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )

                assetSpecificationsList[i].specificationID?.let {
                    checkBox.id = it
                }
                checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                checkBox.text = assetSpecificationsList[i].specification
                checkBox.compoundDrawablePadding = resources.getDimension(R.dimen.vertical_spacing).toInt()
                assetSpecificationsList[i].listValues.let {
                    if (it == "Y")
                        checkBox.isChecked = true
                }
                checkBox.tag = assetSpecificationsList[i].specification
                if (assetSpecificationsList[i].mandatory == "Y") {
                    assetSpecificationsList[i].specificationID?.let {
                        mandatoryCheckBoxList.add(it)
                    }
                }

                checkBoxList.add(checkBox)

                mBinding.llDynamic.addView(checkBox)

            }
            if (assetSpecificationsList[i].dataType == Constant.DynamicFormDataTypes.Date.value) {
                val textInputLayout = TextInputLayout(requireContext())
                textInputLayout.hint = assetSpecificationsList[i].specification
                textInputLayout.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textInputLayout.hint = assetSpecificationsList[i].specification

                val datepickerText = DatePickerEditText(requireContext())
                datepickerText.showIcons(true)
                assetSpecificationsList[i].dateValue.let {
                    datepickerText.setText(displayFormatDate(it))
                }
                datepickerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                datepickerText.compoundDrawablePadding = resources.getDimension(R.dimen.vertical_spacing).toInt()
                datepickerText.setDisplayDateFormat(displayDateFormat)
                datepickerText.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                datepickerText.tag = assetSpecificationsList[i].dataType
                assetSpecificationsList[i].specificationID?.let {
                    datepickerText.id = it
                }
                if (assetSpecificationsList[i].mandatory == "Y") {
                    assetSpecificationsList[i].specificationID?.let {
                        mandatoryEditList.add(it)
                    }
                }

                val timeInMillis = Calendar.getInstance().timeInMillis
                if (assetSpecificationsList[i].specification?.toLowerCase()!!.contains("startdate") || assetSpecificationsList[i].specification?.toLowerCase()!!.contains("fromdate"))
                    datepickerText.setMaxDate(timeInMillis)
                else if (assetSpecificationsList[i].specification?.toLowerCase()!!.contains("enddate") || assetSpecificationsList[i].specification?.toLowerCase()!!.contains("todate") || assetSpecificationsList[i].specification?.toLowerCase()!!.contains("expiry"))
                    datepickerText.setMinDate(timeInMillis)

                editTextList.add(datepickerText)

                textInputLayout.addView(datepickerText)
                mBinding.llDynamic.addView(textInputLayout)
            }

            if (assetSpecificationsList[i].dataType == Constant.DynamicFormDataTypes.DateTime.value) {
                val textInputLayout = TextInputLayout(requireContext())
                textInputLayout.hint = assetSpecificationsList[i].specification
                textInputLayout.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )

                val dateTimepickerText = DatePickerEditText(requireContext())
                dateTimepickerText.showIcons(true)
                dateTimepickerText.setDisplayDateFormat(displayDateTimeTimeSecondFormat)
                dateTimepickerText.compoundDrawablePadding = resources.getDimension(R.dimen.vertical_spacing).toInt()
                assetSpecificationsList[i].dateValue.let {
                    dateTimepickerText.setText(formatDisplayDateTimeInMillisecond(it))
                }
                dateTimepickerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                dateTimepickerText.tag = assetSpecificationsList[i].dataType
                assetSpecificationsList[i].specificationID?.let {
                    dateTimepickerText.id = it
                }

                if (assetSpecificationsList[i].mandatory == "Y") {
                    assetSpecificationsList[i].specificationID?.let {
                        mandatoryEditList.add(it)
                    }
                }

                dateTimepickerText.setDateTime(true)
                val timeInMillis = Calendar.getInstance().timeInMillis
                if (assetSpecificationsList[i].specification?.toLowerCase()!!.contains("startdate") || assetSpecificationsList[i].specification?.toLowerCase()!!.contains("fromdate"))
                    dateTimepickerText.setMaxDate(timeInMillis)
                else if (assetSpecificationsList[i].specification?.toLowerCase()!!.contains("enddate") || assetSpecificationsList[i].specification?.toLowerCase()!!.contains("todate") || assetSpecificationsList[i].specification?.toLowerCase()!!.contains("expiry"))
                    dateTimepickerText.setMinDate(timeInMillis)

                editTextList.add(dateTimepickerText)

                textInputLayout.addView(dateTimepickerText)
                mBinding.llDynamic.addView(textInputLayout)
            }


            if (assetSpecificationsList[i].dataType == Constant.DynamicFormDataTypes.Spinner.value) {
                val llayout = LinearLayout(requireContext())
                llayout.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                llayout.orientation = LinearLayout.HORIZONTAL

                val tvDynamic = TextView(requireContext())
                tvDynamic.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5.0f)
                tvDynamic.setPadding(2, 0, 0, 0)
                tvDynamic.text = assetSpecificationsList[i].specification

                if (assetSpecificationsList[i].mandatory == "Y") {
                    assetSpecificationsList[i].specificationID?.let {
                        mandatorySpinnerList.add(it)
                    }
                }

                val searchFilter = AdvanceSearchFilter()
                searchFilter.pageSize = 100
                searchFilter.pageIndex = 1
                searchFilter.query = null
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "SpecificationID"
                filterColumn.columnValue = assetSpecificationsList[i].specificationID.toString()
                filterColumn.srchType = "equal"

                listFilterColumn.add(listFilterColumn.size, filterColumn)

                searchFilter.filterColumns = listFilterColumn

                val tableDetails = TableDetails()
                tableDetails.tableOrViewName = "INV_SpecificationValueSets"
                tableDetails.primaryKeyColumnName = "SpecificationValueID"
                tableDetails.TableCondition = "AND"
                tableDetails.selectColoumns = "SpecificationValueID,Value,Active,[Default]"

                searchFilter.tableDetails = tableDetails

                APICall.getDynamicValuesDropdown(searchFilter, object : ConnectionCallBack<GetSpecificationValueSetResult> {
                    override fun onFailure(message: String) {
                        listener?.dismissDialog()
                    }

                    override fun onSuccess(response: GetSpecificationValueSetResult) {
                        listener?.dismissDialog()

                        val specificationValueSets = response.results?.invSpecificationValueSets
                        val spinner = Spinner(requireContext())
                        spinner.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5.0f)
                        assetSpecificationsList[i].specificationID?.let {
                            spinner.id = it
                        }

                        if (specificationValueSets.isNullOrEmpty())
                            spinner.adapter = null
                        else {
                            if (assetSpecificationsList[i].mandatory == "N") {
                                specificationValueSets.add(0, AssetSpecsValueSets(value = getString(R.string.select), specificationValueID = -1))
                            }
                            val specsValueSetAdapter = ArrayAdapter<AssetSpecsValueSets>(requireContext(), android.R.layout.simple_list_item_1, specificationValueSets)
                            spinner.adapter = specsValueSetAdapter
                        }


                        specificationValueSets?.let {
                            for ((index, value) in it.withIndex()) {
                                if (value.default == "Y")
                                    spinner.setSelection(index)
                            }
                        }


                        specificationValueSets?.let {
                            for ((index, obj) in it.withIndex()) {
                                if (assetSpecificationsList[i].specificationValueID == obj.specificationValueID) {
                                    spinner.setSelection(index)
                                    break
                                }
                            }
                        }

                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                spinner.tag = specificationValueSets?.get(position)?.specificationValueID
                            }
                        }

                        spinnerList.add(spinner)

                        llayout.addView(tvDynamic)
                        llayout.addView(spinner)
                        mBinding.llDynamic.addView(llayout)
                    }
                })
            }

            if (assetSpecificationsList[i].dataType == Constant.DynamicFormDataTypes.Decimal.value || assetSpecificationsList[i].dataType == Constant.DynamicFormDataTypes.Integer.value || assetSpecificationsList[i].dataType == Constant.DynamicFormDataTypes.Memo.value || assetSpecificationsList[i].dataType == Constant.DynamicFormDataTypes.Text.value) {
                val textInputLayout = TextInputLayout(requireContext())
                textInputLayout.hint = assetSpecificationsList[i].specification
                textInputLayout.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textInputLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

                val editText = TextInputEditText(requireContext())
                assetSpecificationsList[i].specificationID?.let {
                    editText.id = it
                }
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                assetSpecificationsList[i].listValues.let {
                    editText.setText(assetSpecificationsList[i].listValues)
                }
                editText.tag = assetSpecificationsList[i].dataType
                editText.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                if (assetSpecificationsList[i].mandatory == "Y") {
                    assetSpecificationsList[i].specificationID?.let {
                        mandatoryEditList.add(it)
                    }
                }


                when (assetSpecificationsList[i].dataType) {
                    Constant.DynamicFormDataTypes.Decimal.value -> {
                        editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL + InputType.TYPE_CLASS_NUMBER
                    }
                    Constant.DynamicFormDataTypes.Integer.value -> {
                        editText.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                    Constant.DynamicFormDataTypes.Memo.value -> {
                        editText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE + InputType.TYPE_CLASS_TEXT
                    }
                    Constant.DynamicFormDataTypes.Text.value -> {
                        editText.inputType = InputType.TYPE_CLASS_TEXT
                    }
                }

                editTextList.add(editText)

                textInputLayout.addView(editText)
                mBinding.llDynamic.addView(textInputLayout)
            }
        }
    }

    fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.llInsuranceLicense.setOnClickListener(this)
        mBinding.llFitness.setOnClickListener(this)
        mBinding.llVehicleMaintenance.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
        mBinding.ivAddLocation.setOnClickListener(this)
        mBinding.btnExistingAddress.setOnClickListener(this)
        mBinding.btnNewAddress.setOnClickListener(this)

        mBinding.spnAssetCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val assetCategory: AssetCategory = mBinding.spnAssetCategory.selectedItem as AssetCategory
                    resetValues()
                    assetCategoriesList?.let {
                        if (it[position].allowFitness == "Y") {
                            mBinding.llFitness.visibility = View.VISIBLE
                            mBinding.viewFitness.visibility = View.VISIBLE
                        }
                        if (it[position].allowInsurance == "Y") {
                            mBinding.llInsuranceLicense.visibility = View.VISIBLE
                            mBinding.viewInsurance.visibility = View.VISIBLE
                        }
                        if (it[position].allowMaintenance == "Y") {
                            mBinding.llVehicleMaintenance.visibility = View.VISIBLE
                            mBinding.viewMaintenance.visibility = View.VISIBLE
                        }
                    }

                    if (assetCategory.assetCategoryID != -1) {
                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET && assetValues?.assetCategoryID != assetCategory.assetCategoryID) {
                            assetCategory.assetCategoryID?.let {
                                getDynamicFormSpecifications4Asset(it)
                            }
                        }
                    }
                }
            }
        }

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

    private fun saveAssetTax(view: View) {
        if (validateView()) {
            mBinding.btnSave.isEnabled = false
            val asset = Asset()
            asset.assetSycotaxID = mBinding.edtSycoTaxID.text.toString().trim()
            if (mBinding.spnAssetCategory.selectedItem != null) {
                val assetCategory: AssetCategory = mBinding.spnAssetCategory.selectedItem as AssetCategory
                if (assetCategory.assetCategoryID != -1)
                    asset.assetCategoryID = assetCategory.assetCategoryID
            }

            if (mBinding.edtAssetNo.text.toString().trim().isNotEmpty())
                asset.assetNo = mBinding.edtAssetNo.text.toString().trim()

            if (mBinding.edtLifeTimeStartDate.text.toString().trim().isNotEmpty())
                asset.lifeTimeStartDate = serverFormatDateTimeInMilliSecond(mBinding.edtLifeTimeStartDate.text.toString().trim())

            if (mBinding.edtLifeTimeEndDate.text.toString().trim().isNotEmpty())
                asset.lifeTimeEndDate = serverFormatDateTimeInMilliSecond(mBinding.edtLifeTimeEndDate.text.toString().trim())

            if (mBinding.edtSecurityDeposit.text.toString().trim().isNotEmpty())
                asset.securityDeposit = BigDecimal(currencyToDouble(mBinding.edtSecurityDeposit.text.toString()) as Long)

            if (mBinding.edtDescription.text.toString().trim().isNotEmpty())
                asset.description = mBinding.edtDescription.text.toString().trim()

            if (mBinding.edtDescription.text.toString().trim().isNotEmpty())
                asset.description = mBinding.edtDescription.text.toString().trim()

//            if (MyApplication.getPrefHelper().userOrgBranchID > 0)
//                asset.userOrgBranchID = MyApplication.getPrefHelper().userOrgBranchID

            if (mBinding.spnAgentAdminstrativOffice.selectedItem != null) {
                asset.userOrgBranchID = (mBinding.spnAgentAdminstrativOffice.selectedItem as UMXUserOrgBranches).userOrgBranchID
            }

            if (mBinding.spnStatus.selectedItem != null) {
                asset.statusCode = (mBinding.spnStatus.selectedItem as COMStatusCode?)?.statusCode
            }

            if (mBinding.edtBookingAdvance.text.toString().trim().isNotEmpty())
                asset.bookingAdvance = BigDecimal(currencyToDouble(mBinding.edtBookingAdvance.text.toString()) as Long)

            asset.status = if (mBinding.chkActive.isChecked) "Y" else "N"

            assetValues.let {
                if (it?.allowMaintenance != null)
                    asset.allowMaintenance = "Y"

                if (it?.allowFitness != null)
                    asset.allowFitness = "Y"

                if (it?.allowInsurance != null)
                    asset.allowInsurance = "Y"
            }

            val assetSpecificationsList: ArrayList<AssetSpecifications> = arrayListOf()
            if (editTextList.size > 0) {
                for (listValues in editTextList) {
                    if (listValues.tag == Constant.DynamicFormDataTypes.Decimal.value || listValues.tag == Constant.DynamicFormDataTypes.Integer.value || listValues.tag == Constant.DynamicFormDataTypes.Memo.value || listValues.tag == Constant.DynamicFormDataTypes.Text.value) {
                        if (listValues.text.toString().isNotEmpty()) {
                            val assetSpecifications = AssetSpecifications()
                            assetSpecifications.specificationID = listValues.id
                            assetSpecifications.value = listValues.text.toString()
                            assetID.let {
                                assetSpecifications.assetID = assetID
                            }
                            assetSpecificationsList.add(assetSpecifications)
                        }
                    } else if (listValues.tag == Constant.DynamicFormDataTypes.Date.value || listValues.tag == Constant.DynamicFormDataTypes.DateTime.value) {
                        if (listValues.text.toString().isNotEmpty()) {
                            val assetSpecifications = AssetSpecifications()
                            assetSpecifications.specificationID = listValues.id
                            assetSpecifications.dateValue = serverFormatDate(listValues.text.toString())
                            assetID.let {
                                assetSpecifications.assetID = assetID
                            }
                            assetSpecificationsList.add(assetSpecifications)
                        }
                    }
                }
            }

            if (spinnerList.size > 0) {
                for (spinnerListValues in spinnerList) {
                    if (spinnerListValues.selectedItem.toString().isNotEmpty() && spinnerListValues.selectedItem.toString() != getString(R.string.select)) {
                        val assetSpecifications = AssetSpecifications()
                        assetSpecifications.specificationID = spinnerListValues.id
                        assetSpecifications.specificationValueID = spinnerListValues.tag.toString().toInt()
                        assetID.let {
                            assetSpecifications.assetID = assetID
                        }
                        assetSpecificationsList.add(assetSpecifications)
                    }
                }
            }

            if (checkBoxList.size > 0) {
                for (checkBoxValues in checkBoxList) {
                    if (checkBoxValues.isChecked) {
                        val assetSpecifications = AssetSpecifications()
                        assetSpecifications.specificationID = checkBoxValues.id
                        assetSpecifications.value = "Y"
                        assetID.let {
                            assetSpecifications.assetID = assetID
                        }
                        assetSpecificationsList.add(assetSpecifications)
                    }
                }
            }

            if (assetID > 0) {
                asset.assetID = assetID
            }

            val storeAsset = StoreAsset()
            storeAsset.asset = asset
            storeAsset.assetSpecs = assetSpecificationsList
            storeAsset.geoAddress = prepareAddressData()

            APICall.storeAsset(storeAsset, object : ConnectionCallBack<Int> {
                override fun onSuccess(response: Int) {
                    listener?.dismissDialog()
                    if (assetID <= 0)
                        assetID = response
                    mBinding.btnSave.isEnabled = true
                    if (view.id == R.id.btnSave) {
                        clearAll()
                        assetID = 0
                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET)
                            listener?.showToast(getString(R.string.msg_asset_update_success))
                        else
                            listener?.showToast(getString(R.string.msg_asset_onboard_success))
                        listener?.finish()
                    } else {
                        onClick(view)
                    }
                }

                override fun onFailure(message: String) {
                    listener?.dismissDialog()
                    if (message.isNotEmpty())
                        listener?.showAlertDialog(message)
                    mBinding.btnSave.isEnabled = true
                }
            })
        }
    }

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = java.util.ArrayList()
        var index = -1
        var countryCode: String? = "BFA"
        geoAddress?.countryCode?.let {
            countryCode = it
        }
        for (country in mResponseCountriesList) {
            countries.add(country)
            if (index <= -1 && countryCode != null && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode) {
                index = countries.indexOf(country)
                country.countryCode?.let {
                    countryCode = it
                }
            }
        }
        if (index <= -1) index = 0
        if (countries.size > 0) {
            val countryMasterArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, countries)
            mBinding.spnCountry.adapter = countryMasterArrayAdapter
            mBinding.spnCountry.setSelection(index)
        } else
            mBinding.spnCountry.adapter = null
        filterStates(countryCode)
    }

    private fun filterStates(countryCode: String?) {
        var states: MutableList<COMStateMaster> = java.util.ArrayList()
        var index = -1
        var stateID = 100497
        var stateName = "Kadiogo"
        geoAddress?.state?.let {
            stateName = it
        }
        if (TextUtils.isEmpty(countryCode)) states = java.util.ArrayList() else {
            for (state in mResponseStatesList) {
                if (countryCode == state.countryCode) states.add(state)
                if (index <= -1 && !TextUtils.isEmpty(stateName) && state.state != null && stateName == state.state) {
                    index = states.indexOf(state)
                    state.stateID?.let {
                        stateID = it
                    }
                }
            }
        }
        if (index <= -1) index = 0
        if (states.size > 0) {
            val stateArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, states)
            mBinding.spnState.adapter = stateArrayAdapter
            mBinding.spnState.setSelection(index)
        } else
            mBinding.spnState.adapter = null
        filterCities(stateID)
    }

    private fun filterCities(stateID: Int) {
        var cities: MutableList<VUCOMCityMaster> = java.util.ArrayList()
        var index = -1
        var cityID = 100312090
        var cityName = "Ouagadougou"
        geoAddress?.city?.let {
            cityName = it
        }
        if (stateID <= 0) cities = java.util.ArrayList() else {
            for (city in mResponseCitiesList) {
                if (city.stateID != null && stateID == city.stateID) cities.add(city)
                if (index <= 0 && !TextUtils.isEmpty(cityName) && city.city != null && cityName == city.city) {
                    index = cities.indexOf(city)
                    city.cityID?.let {
                        cityID = it
                    }
                }
            }
        }
        if (index <= -1) index = 0
        if (cities.size > 0) {
            val cityArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)
            mBinding.spnCity.adapter = cityArrayAdapter
            mBinding.spnCity.setSelection(index)
        } else
            mBinding.spnCity.adapter = null
        filterZones(cityID)
    }

    private fun filterZones(cityID: Int) {
        var zones: MutableList<COMZoneMaster> = java.util.ArrayList()
        var index = 0
        var zoneName: String? = ""
        var zoneID = 0
        geoAddress?.zone?.let {
            zoneName = it
        }
        if (cityID <= 0) zones = java.util.ArrayList() else {
            for (zone in mResponseZonesList) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone) {
                    index = zones.indexOf(zone)
                    zone.zoneID?.let {
                        zoneID = it
                    }
                }
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnZone.adapter = zoneArrayAdapter
            mBinding.spnZone.setSelection(index)
        } else
            mBinding.spnZone.adapter = null
        filterSectors(zoneID)
    }

    private fun filterSectors(zoneID: Int) {
        var sectors: MutableList<COMSectors?> = java.util.ArrayList()
        var index = -1
        var sectorID = 0
        geoAddress?.sectorID?.let {
            sectorID = it
        }
        if (zoneID <= 0) sectors = java.util.ArrayList() else {
            for (sector in mResponseSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId)
                    index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnSector.adapter = sectorArrayAdapter
            mBinding.spnSector.setSelection(index)
        } else mBinding.spnSector.adapter = null
    }

    private fun prepareAddressData(): GeoAddress {
        val geoAddress = GeoAddress()

        updateAsset?.geoAddress?.geoAddressID?.let {
            geoAddress.geoAddressID = it
        }
        updateAsset?.geoAddress?.accountId?.let {
            geoAddress.accountId = it
        }
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
        if (comSectors?.sectorId != null) geoAddress.sectorID = comSectors.sectorId
        // endregion
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) geoAddress.street = mBinding.edtStreet.text.toString().trim { it <= ' ' }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) geoAddress.zipCode = mBinding.edtZipCode.text.toString().trim { it <= ' ' }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) geoAddress.plot = mBinding.edtPlot.text.toString().trim { it <= ' ' }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) geoAddress.block = mBinding.edtBlock.text.toString().trim { it <= ' ' }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(mBinding.edtDoorNo.text.toString().trim { it <= ' ' })) geoAddress.doorNo = mBinding.edtDoorNo.text.toString().trim { it <= ' ' }
        if (mBinding.edtAddDescription.text != null && !TextUtils.isEmpty(mBinding.edtAddDescription.text.toString())) geoAddress.description = mBinding.edtAddDescription.text.toString().trim { it <= ' ' }
        if (mBinding.edtLatitude.text.toString().trim().isNotEmpty()) {
            val latitude = mBinding.edtLatitude.text.toString().trim().toDouble()
            geoAddress.latitude = "$latitude"
        }

        if (mBinding.edtLongitude.text.toString().trim().isNotEmpty()) {
            val longitude = mBinding.edtLongitude.text.toString().trim().toDouble()
            geoAddress.longitude = "$longitude"
        }

        return geoAddress
    }

    private fun resetValues() {
        mBinding.llInsuranceLicense.visibility = View.GONE
        mBinding.llFitness.visibility = View.GONE
        mBinding.llVehicleMaintenance.visibility = View.GONE
        mBinding.viewInsurance.visibility = View.GONE
        mBinding.viewFitness.visibility = View.GONE
        mBinding.viewMaintenance.visibility = View.GONE
    }

    private fun clearAll() {
        mBinding.llDynamic.removeAllViews()

        editTextList.clear()
        spinnerList.clear()
        checkBoxList.clear()

        mandatoryEditList.clear()
        mandatorySpinnerList.clear()
        mandatoryCheckBoxList.clear()
    }

    fun bindLatLongs(latitude: Double?, longitude: Double?) {
        mBinding.edtLatitude.setText(latitude.toString())
        mBinding.edtLongitude.setText(longitude.toString())
    }

    fun bindAddress(latitude: Double?, longitude: Double?) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
        mBinding.edtStreet.setText(addresses[0].subLocality)
        if (addresses[0].getAddressLine(0).toLowerCase().contains("plot") || addresses[0].getAddressLine(0).toLowerCase().contains("block") || addresses[0].getAddressLine(0).toLowerCase().contains("door")) {
            for (address in addresses[0].getAddressLine(0).split(",")) {
                if (address.toLowerCase().contains("plot"))
                    mBinding.edtPlot.setText(address)
                if (address.toLowerCase().contains("block"))
                    mBinding.edtBlock.setText(address)
                if (address.toLowerCase().contains("door"))
                    mBinding.edtDoorNo.setText(address)

            }
        } else {
            mBinding.edtPlot.setText(addresses[0].premises)
            mBinding.edtBlock.setText(addresses[0].premises)
            mBinding.edtDoorNo.setText(addresses[0].premises)
        }
        mBinding.edtZipCode.setText(addresses[0].postalCode)
    }

    private fun getCurrentLocation(view: View) {
        mHelper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
        mHelper?.fetchLocation()
        mHelper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                bindAddress(latitude, longitude)
                bindLatLongs(latitude, longitude)
//                initComponents()
                listener?.dismissDialog()
            }

            override fun start() {
                listener?.showProgressDialog(R.string.msg_location_fetching)
            }
        })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mHelper?.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mHelper?.onActivityResult(requestCode, resultCode)
        fetchChildEntriesCount()
    }

    override fun onDetach() {
        listener = null
        mHelper?.disconnect()
        super.onDetach()
    }

    fun onBackPressed() {
        clearAll()
        assetID = 0
    }

    override fun onClick(v: View?) {
        v.let {
            when (v?.id) {
                R.id.btnExistingAddress -> {
                    mBinding.llAddress.visibility = View.VISIBLE
                    getCurrentLocation(v)
                }

                R.id.btnNewAddress -> {
                    mBinding.llAddress.visibility = View.VISIBLE
                    mBinding.edtStreet.setText("")
                    mBinding.edtDoorNo.setText("")
                    mBinding.edtBlock.setText("")
                    mBinding.edtPlot.setText("")
                    mBinding.edtZipCode.setText("")
                    mBinding.edtAddDescription.setText("")
                }

                R.id.ivAddLocation -> {
                    var mLatitude = 0.0
                    var mLongitude = 0.0
                    if (mBinding.edtLatitude.text.toString().trim().isNotEmpty()) {
                        mLatitude = mBinding.edtLatitude.text.toString().trim().toDouble()
                    }

                    if (mBinding.edtLongitude.text.toString().trim().isNotEmpty()) {
                        mLongitude = mBinding.edtLongitude.text.toString().trim().toDouble()
                    }
                    val dialog: LocateDialogFragment = LocateDialogFragment.newInstance(mLatitude, mLongitude)
                    dialog.show(childFragmentManager, LocateDialogFragment::class.java.simpleName)
                }


                R.id.btnSave -> {
                    saveAssetTax(v)
                }

                R.id.llInsuranceLicense -> {
                    if (assetID > 0) {
                        val fragment = AssetInsuranceMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, assetID)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_INSURANCE_LIST)
                        listener?.showToolbarBackButton(R.string.insurance_details)
                        listener?.addFragment(fragment, true)
                    } else {
                        saveAssetTax(v)
                    }
                }

                R.id.llFitness -> {
                    if (assetID > 0) {
                        val fragment = AssetFitnessMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, assetID)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_FITNESS_LIST)
                        listener?.showToolbarBackButton(R.string.fitness_details)
                        listener?.addFragment(fragment, true)
                    } else {
                        saveAssetTax(v)
                    }
                }

                R.id.llVehicleMaintenance -> {
                    if (assetID > 0) {
                        val fragment = AssetMaintenanceMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, assetID)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_MAINTENANCE_LIST)
                        listener?.showToolbarBackButton(R.string.maintenance_details)
                        listener?.addFragment(fragment, true)
                    } else {
                        saveAssetTax(v)
                    }
                }

                R.id.llDocuments -> {
                    if (assetID > 0) {
                        val fragment = DocumentsMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, assetID)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_DOCUMENT_LIST)
                        listener?.showToolbarBackButton(R.string.documents)
                        listener?.addFragment(fragment, true)
                    } else {
                        saveAssetTax(v)
                    }
                }

                R.id.llNotes -> {
                    if (assetID > 0) {
                        val fragment = NotesMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, assetID)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_NOTES_LIST)
                        listener?.showToolbarBackButton(R.string.notes)
                        listener?.addFragment(fragment, true)
                    } else {
                        saveAssetTax(v)
                    }
                }


                else -> {

                }
            }
        }
    }

    fun validateView(): Boolean {
        if (mBinding.spnAssetCategory.selectedItem == null || mBinding.spnAssetCategory.selectedItemPosition == 0) {
            listener?.showToast(getString(R.string.msg_provide) + " " + (getString(R.string.asset_category)))
            return false
        }

        if (mBinding.edtAssetNo.text != null && TextUtils.isEmpty(mBinding.edtAssetNo.text.toString())) {
            listener?.showToast(getString(R.string.msg_provide) + " " + (getString(R.string.asset_name)))
            return false
        }

        for (editText in editTextList) {
            for (mandatory in mandatoryEditList) {
                if (editText.id == mandatory) {
                    if (editText.text.toString().isEmpty()) {
                        listener?.showToast(getString(R.string.msg_provide) + " " + editText.hint)
                        return false
                    }
                }
            }
        }

        for (spinnerValue in spinnerList) {
            for (mandatory in mandatorySpinnerList) {
                if (spinnerValue.id == mandatory) {
                    if (spinnerValue.selectedItem.toString().isEmpty()) {
                        listener?.showToast(getString(R.string.msg_provide) + " " + spinnerValue.selectedItem.toString())
                        return false
                    }
                }
            }
        }

        for (checkBoxValue in checkBoxList) {
            for (mandatory in mandatoryCheckBoxList) {
                if (checkBoxValue.id == mandatory && !checkBoxValue.isChecked) {
                    listener?.showToast(getString(R.string.msg_check) + " " + checkBoxValue.tag.toString())
                    return false
                }
            }
        }

        if (mBinding.spnStatus.selectedItem == null || mBinding.spnStatus.selectedItemPosition == 0) {
            listener?.showToast(getString(R.string.msg_provide) + " " + (getString(R.string.status)))
            return false
        }
        if (mBinding.spnZone.selectedItem == null) {
            listener?.showToast(getString(R.string.msg_provide) + " " + (getString(R.string.zone)))
            return false
        }
        if (mBinding.spnSector.selectedItem == null) {
            listener?.showToast(getString(R.string.msg_provide) + " " + (getString(R.string.sector)))
            return false
        }
        if (mBinding.spnAgentAdminstrativOffice.selectedItem == null || mBinding.spnAgentAdminstrativOffice.selectedItemPosition == 0) {
            listener?.showToast(getString(R.string.msg_provide) + " " + (getString(R.string.admin_office)))
            return false
        }

        return true
    }

    private fun fetchChildEntriesCount() {
        assetID.let {
            val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
            val filterColumn = FilterColumn()
            filterColumn.columnName = "AssetID"
            if (updateAsset != null) {
                filterColumn.columnValue = updateAsset?.asset?.assetID.toString()
            } else {
                filterColumn.columnValue = assetID.toString()
            }
            filterColumn.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            fetchCount(listFilterColumn, "OR", "VU_AST_AssetInsurances", "InsuranceID")

        }
    }

    private fun fetchCount(filterColumns: List<FilterColumn>, tableCondition: String, tableOrViewName: String, primaryKeyColumnName: String) {
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
                bindCounts(filterColumns, tableOrViewName, 0)
            }

            override fun onSuccess(response: Int) {
                bindCounts(filterColumns, tableOrViewName, response)
            }
        })
    }

    private fun bindCounts(filterColumns: List<FilterColumn>, tableOrViewName: String, count: Int) {
        when (tableOrViewName) {
            "VU_AST_AssetInsurances" -> {
                mBinding.txtNumberOfInsuranceLicense.text = "$count"
                fetchCount(filterColumns, "AND", "VU_AST_AssetFitnesses", "FitnessID")
            }
            "VU_AST_AssetFitnesses" -> {
                mBinding.txtNumberOfFitness.text = "$count"
                fetchCount(filterColumns, "OR", "VU_AST_AssetMaintenance", "MaintenanceID")
            }
            "VU_AST_AssetMaintenance" -> {
                mBinding.txtNumberOfVehicleMaintenance.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "TableName"
                filterColumn.columnValue = "AST_Assets"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                if (updateAsset != null) {
                    filterColumn.columnValue = updateAsset?.asset?.assetID.toString()
                } else {
                    filterColumn.columnValue = assetID.toString()
                }
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_DocumentReferences", "DocumentReferenceID")
            }
            "COM_DocumentReferences" -> {
                mBinding.txtNumberOfDocuments.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "TableName"
                filterColumn.columnValue = "AST_Assets"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                if (updateAsset != null) {
                    filterColumn.columnValue = updateAsset?.asset?.assetID.toString()
                } else {
                    filterColumn.columnValue = assetID.toString()
                }
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
            }
            "COM_Notes" -> {
                mBinding.txtNumberOfNotes.text = "$count"
            }
        }
    }

    interface Listener {
        fun popBackStack()
        fun showProgressDialog()
        fun dismissDialog()
        fun showToast(message: String)
        fun showToolbarBackButton(title: Int)
        fun finish()
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog(message: Int)
    }
}