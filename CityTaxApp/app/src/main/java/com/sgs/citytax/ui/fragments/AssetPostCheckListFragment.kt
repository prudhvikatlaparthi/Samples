package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentAssetPostCheckListBinding
import com.sgs.citytax.model.ASTAssetRentPreCheckLists
import com.sgs.citytax.model.ASTAssetRents
import com.sgs.citytax.model.AssetRentalSpecificationsList
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.custom.DatePickerEditText
import com.sgs.citytax.util.*
import java.math.BigDecimal
import java.util.*

class AssetPostCheckListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentAssetPostCheckListBinding

    private var fromScreen: Constant.QuickMenu? = null
    private var mListener: Listener? = null

    private var assetBookingRequestLine: AssetBookingRequestLine? = null
    private var mHelper: LocationHelper? = null
    private var assetId: Int? = 0

    private var mAssetBooking: AssetBooking? = null
    private var validateAssetForAssignAndReturnResponse: ValidateAssetForAssignAndReturnResponse? = null
    private var assetRentalDetails: AssetRentalDetailsResponse? = null

    //region Dynamic Form
    private val editTextList = arrayListOf<EditText>()
    private val spinnerList = arrayListOf<Spinner>()
    private val checkBoxList = arrayListOf<CheckBox>()

    private var mandatoryEditList: ArrayList<Int> = arrayListOf()
    private var mandatorySpinnerList: ArrayList<Int> = arrayListOf()
    private var mandatoryCheckBoxList: ArrayList<Int> = arrayListOf()
    //endregion


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_asset_post_check_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: Exception) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        mHelper?.disconnect()
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        mHelper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
    }

    override fun initComponents() {
        processIntent()
        setViews()
        getBookingsForReturn()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mHelper?.onActivityResult(requestCode, resultCode)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mHelper?.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun processIntent() {
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            assetId = it.getInt(Constant.KEY_ASSET_ID)
            validateAssetForAssignAndReturnResponse = it.getParcelable(Constant.KEY_VALIDATE_ASSET)
        }
    }

    private fun setViews() {
        if (validateAssetForAssignAndReturnResponse?.allowInvoiceAfterReturn == "N") {
            mBinding.edtFineAmountLayout.visibility = View.GONE
            mBinding.edtFineAmount.visibility = View.GONE
        } else {
            mBinding.edtFineAmountLayout.visibility = View.VISIBLE
            mBinding.edtFineAmount.visibility = View.VISIBLE

        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)

        mBinding.edtFineAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtFineAmount.text.toString()
                if (text?.isNotEmpty()!!) {
                    mBinding.edtFineAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
                    mBinding.edtFineAmount.setText("${currencyToDouble(text)}");
                }
            } else {
                //this if condition is true when edittext lost focus...
                //check here for number is larger than 10 or not
                if (!TextUtils.isEmpty(mBinding.edtFineAmount.text.toString())) {
                    val enteredText: Double = mBinding.edtFineAmount.text.toString().toDouble()
                    mBinding.edtFineAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6 + 15))
                    mBinding.edtFineAmount.setText("${formatWithPrecision(enteredText.toString())}")
                }
            }
        }
    }

    private fun getBookingsForReturn() {
        mListener?.showProgressDialog()

        val getBookingsList = GetBookingsList()
        getBookingsList.bookingRequestID = validateAssetForAssignAndReturnResponse?.bookingRequestId
        getBookingsList.bookingRequestLineId = validateAssetForAssignAndReturnResponse?.bookingRequestLineId
        getBookingsList.isAssetBookingUpdate = false

        APICall.getBookings(getBookingsList, object : ConnectionCallBack<List<AssetBooking>> {
            override fun onSuccess(response: List<AssetBooking>) {
                mListener?.dismissDialog()
                mAssetBooking = response[0]
                if (assetBookingRequestLine == null) {
                    if (mAssetBooking?.assetBookingRequestLine != null) {
                        assetBookingRequestLine = mAssetBooking?.assetBookingRequestLine
                    }

                    if (assetBookingRequestLine != null && assetBookingRequestLine?.trackOdometer.equals("Y")) {
                        mBinding.edtOdometerStartLayout.visibility = View.VISIBLE
                        mBinding.edtOdometerStart.visibility = View.VISIBLE
                        mBinding.edtOdometerEndLayout.visibility = View.VISIBLE
                        mBinding.edtOdometerEnd.visibility = View.VISIBLE
                    } else {
                        mBinding.edtOdometerStartLayout.visibility = View.GONE
                        mBinding.edtOdometerStart.visibility = View.GONE
                        mBinding.edtOdometerEndLayout.visibility = View.GONE
                        mBinding.edtOdometerEnd.visibility = View.GONE
                    }

                    getAssetRentals()
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })
            }
        })
    }

    private fun getAssetRentals() {
        val assetRentId = validateAssetForAssignAndReturnResponse?.assetRentId

        APICall.getAssetRentalDetails(assetRentId, object : ConnectionCallBack<AssetRentalDetailsResponse> {
            override fun onSuccess(response: AssetRentalDetailsResponse) {
                assetRentalDetails = response
                assetRentalDetails?.let {
                    it.odometerStart?.let { odometerStart ->
                        mBinding.edtOdometerStart.setText(odometerStart.toString())
                    }
                }

                getPrePostCheckListData()
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun clearAll() {
        mBinding.llDynamicPreCheckListData.removeAllViews()
        mBinding.llDynamicPostCheckListData.removeAllViews()

        editTextList.clear()
        spinnerList.clear()
        checkBoxList.clear()

        mandatoryEditList.clear()
        mandatorySpinnerList.clear()
        mandatoryCheckBoxList.clear()
    }

    private fun getPrePostCheckListData() {
        clearAll()
        val assetRentId = validateAssetForAssignAndReturnResponse?.assetRentId
        val assetCategoryId = validateAssetForAssignAndReturnResponse?.assetCategoryId
        APICall.getPrePostCheckListData(assetRentId, assetCategoryId, object : ConnectionCallBack<AssetPrePostResponse> {
            override fun onSuccess(response: AssetPrePostResponse) {
                clearAll()
                if (response.assetRentalSpecifications != null && response.assetRentalSpecifications.isNotEmpty()) {
                    bindPreCheckListData(response.assetRentalSpecifications)
                } else {
                    mListener?.showAlertDialog(getString(R.string.msg_no_data), DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                }

                if (response.assetSpecifications != null && response.assetSpecifications.isNotEmpty()) {
                    bindPostCheckListForm(response.assetSpecifications)
                } else {
                    mListener?.showAlertDialog(getString(R.string.msg_no_data), DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                }
            }

            override fun onFailure(message: String) {
                clearAll()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindPreCheckListData(assetRentalSpecifications: ArrayList<AssetRentalSpecificationsList>) {
        for (assetRentalSpecification in assetRentalSpecifications) {
            val linearLayout = LinearLayout(requireContext())
            linearLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            val textView = TextView(requireContext())
            textView.layoutParams = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
            textView.text = assetRentalSpecification.specification
            textView.setPadding(4, 0, 0, 0)
            linearLayout.addView(textView)


            val valueTextView = TextView(requireContext())
            valueTextView.layoutParams = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f)
            valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
            if (assetRentalSpecification.dataType == Constant.DynamicFormDataTypes.Date.value
                    || assetRentalSpecification.dataType == Constant.DynamicFormDataTypes.DateTime.value) {
                if (!assetRentalSpecification.dateValue.isNullOrEmpty() && assetRentalSpecification.dateValue!!.contains(" ")) {
                    val value = assetRentalSpecification.dateValue!!.split(" ")
                    valueTextView.text = displayFormatDate(value[0])
                } else
                    valueTextView.text = displayFormatDate(assetRentalSpecification.dateValue)
            } else if (assetRentalSpecification.dataType == Constant.DynamicFormDataTypes.Spinner.value) {
                valueTextView.text = assetRentalSpecification.specificationValue
            } else {
                valueTextView.text = assetRentalSpecification.value
            }
            valueTextView.setPadding(4, 0, 0, 0)
            linearLayout.addView(valueTextView)

            mBinding.llDynamicPreCheckListData.addView(linearLayout)

        }
    }

    private fun bindPostCheckListForm(assetSpecs: List<AssetSpecs>) {
        assetId?.let {
            mBinding.txtAssetNo.text = it.toString()
        }

        for (assetSpec in assetSpecs) {
            val layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT)
            layoutParams.bottomMargin = resources.getDimension(R.dimen.vertical_spacing).toInt()
            if (assetSpec.dataType == Constant.DynamicFormDataTypes.Checkbox.value) {
                val checkBox = CheckBox(requireContext())
                checkBox.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                )
                checkBox.id = assetSpec.specificationID!!
                checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                checkBox.text = assetSpec.specification
                checkBox.compoundDrawablePadding = resources.getDimension(R.dimen.vertical_spacing).toInt()
                checkBox.tag = assetSpec.specification
                if (assetSpec.mandatory == "Y") {
                    mandatoryCheckBoxList.add(assetSpec.specificationID!!)
                }

                checkBoxList.add(checkBox)

                mBinding.llDynamicPostCheckListData.addView(checkBox)

            }

            if (assetSpec.dataType == Constant.DynamicFormDataTypes.Date.value) {
                val textInputLayout = TextInputLayout(requireContext())
                textInputLayout.hint = assetSpec.specification
                textInputLayout.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                )
                textInputLayout.hint = assetSpec.specification

                val datepickerText = DatePickerEditText(requireContext())
                datepickerText.showIcons(true)
                datepickerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                datepickerText.setDisplayDateFormat(displayDateFormat)
                datepickerText.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                )
                datepickerText.tag = assetSpec.dataType
                datepickerText.id = assetSpec.specificationID!!
                if (assetSpec.mandatory == "Y") {
                    mandatoryEditList.add(assetSpec.specificationID!!)
                }

                val timeInMillis = Calendar.getInstance().timeInMillis
                if (assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("startdate")
                        || assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("fromdate"))
                    datepickerText.setMaxDate(timeInMillis)
                else if (assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("enddate")
                        || assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("todate")
                        || assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("expiry"))
                    datepickerText.setMinDate(timeInMillis)

                editTextList.add(datepickerText)

                textInputLayout.addView(datepickerText)
                mBinding.llDynamicPostCheckListData.addView(textInputLayout)
            }

            if (assetSpec.dataType == Constant.DynamicFormDataTypes.DateTime.value) {
                val textInputLayout = TextInputLayout(requireContext())
                textInputLayout.hint = assetSpec.specification
                textInputLayout.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                )

                val dateTimepickerText = DatePickerEditText(requireContext())
                dateTimepickerText.showIcons(true)
                dateTimepickerText.setDisplayDateFormat(displayDateTimeTimeSecondFormat)
                dateTimepickerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                dateTimepickerText.tag = assetSpec.dataType
                dateTimepickerText.id = assetSpec.specificationID!!

                if (assetSpec.mandatory == "Y") {
                    mandatoryEditList.add(assetSpec.specificationID!!)
                }

                dateTimepickerText.setDateTime(true)
                val timeInMillis = Calendar.getInstance().timeInMillis
                if (assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("startdate") || assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("fromdate"))
                    dateTimepickerText.setMaxDate(timeInMillis)
                else if (assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("enddate")
                        || assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("todate")
                        || assetSpec.specification?.toLowerCase(Locale.getDefault())!!.contains("expiry"))
                    dateTimepickerText.setMinDate(timeInMillis)

                editTextList.add(dateTimepickerText)

                textInputLayout.addView(dateTimepickerText)
                mBinding.llDynamicPostCheckListData.addView(textInputLayout)
            }

            if (assetSpec.dataType == Constant.DynamicFormDataTypes.Spinner.value) {
                val llayout = LinearLayout(requireContext())
                llayout.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                )
                llayout.orientation = LinearLayout.HORIZONTAL

                val tvDynamic = TextView(requireContext())
                tvDynamic.layoutParams = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 5.0f)
                tvDynamic.setPadding(2, 0, 0, 0)
                tvDynamic.text = assetSpec.specification

                if (assetSpec.mandatory == "Y") {
                    mandatorySpinnerList.add(assetSpec.specificationID!!)
                }

                val searchFilter = AdvanceSearchFilter()
                searchFilter.pageSize = 100
                searchFilter.pageIndex = 1
                searchFilter.query = null
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "SpecificationID"
                filterColumn.columnValue = assetSpec.specificationID!!.toString()
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
                        mListener?.dismissDialog()
                    }

                    override fun onSuccess(response: GetSpecificationValueSetResult) {
                        mListener?.dismissDialog()

                        val specificationValueSets = response.results?.invSpecificationValueSets
                        val spinner = Spinner(requireContext())
                        spinner.layoutParams = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 5.0f)
                        spinner.id = assetSpec.specificationID!!

                        if (specificationValueSets.isNullOrEmpty())
                            spinner.adapter = null
                        else {
                            if (assetSpec.mandatory == "N") {
                                specificationValueSets.add(0, AssetSpecsValueSets(value = getString(R.string.select), specificationValueID = -1))
                            }
                            val specsValueSetAdapter = ArrayAdapter<AssetSpecsValueSets>(requireContext(), android.R.layout.simple_list_item_1, specificationValueSets)
                            spinner.adapter = specsValueSetAdapter

                            for ((index, value) in specificationValueSets.withIndex()) {
                                if (value.default == "Y")
                                    spinner.setSelection(index)
                            }


                            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    spinner.tag = specificationValueSets[position].specificationValueID
                                }
                            }
                            spinnerList.add(spinner)

                            llayout.addView(tvDynamic)
                            llayout.addView(spinner)
                            mBinding.llDynamicPostCheckListData.addView(llayout)
                        }


                    }
                })
            }

            if (assetSpec.dataType == Constant.DynamicFormDataTypes.Decimal.value
                    || assetSpec.dataType == Constant.DynamicFormDataTypes.Integer.value
                    || assetSpec.dataType == Constant.DynamicFormDataTypes.Memo.value
                    || assetSpec.dataType == Constant.DynamicFormDataTypes.Text.value) {
                val textInputLayout = TextInputLayout(requireContext())
                textInputLayout.hint = assetSpec.specification
                textInputLayout.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                )
                textInputLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

                val editText = TextInputEditText(requireContext())
                editText.id = assetSpec.specificationID!!
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                editText.tag = assetSpec.dataType
                editText.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                )
                if (assetSpec.mandatory == "Y") {
                    mandatoryEditList.add(assetSpec.specificationID!!)
                }


                when (assetSpec.dataType) {
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
                mBinding.llDynamicPostCheckListData.addView(textInputLayout)
            }

        }

    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                R.id.btnSave -> {
                    if (validateView()) {
                        mHelper?.fetchLocation()
                        mHelper?.setListener(object : LocationHelper.Location {
                            override fun found(latitude: Double, longitude: Double) {
                                mListener?.dismissDialog()
                                saveReturnCheckListData(latitude, longitude)
                            }

                            override fun start() {
                                mListener?.showProgressDialog(R.string.msg_location_fetching)
                            }
                        })
                    }
                }
            }
        }
    }

    private fun validateView(): Boolean {

        if (mBinding.edtOdometerEnd.isVisible && mBinding.edtOdometerEnd.text.toString().isEmpty()) {
            mListener?.showSnackbarMsg(R.string.msg_odometer_end_empty)
            mBinding.edtOdometerEnd.requestFocus()
            return false
        }

        if (mBinding.edtOdometerEnd.isVisible && mBinding.edtOdometerEnd.text.toString().toDouble() < assetRentalDetails?.odometerStart ?: 0.0) {
            mListener?.showSnackbarMsg(R.string.msg_odometer_end_not_matching)
            mBinding.edtOdometerEnd.requestFocus()
            return false
        }


        for (editText in editTextList) {
            for (mandatory in mandatoryEditList) {
                if (editText.id == mandatory) {
                    if (editText.text.toString().isEmpty()) {
                        mListener?.showToast(getString(R.string.msg_provide) + " " + editText.hint)
                        return false
                    }
                }
            }
        }

        for (spinnerValue in spinnerList) {
            for (mandatory in mandatorySpinnerList) {
                if (spinnerValue.isVisible && spinnerValue.id == mandatory) {
                    if (spinnerValue.selectedItem.toString().isEmpty()) {
                        mListener?.showToast(getString(R.string.msg_provide) + " " + spinnerValue.selectedItem.toString())
                        return false
                    }
                }
            }
        }

        for (checkBoxValue in checkBoxList) {
            for (mandatory in mandatoryCheckBoxList) {
                if (checkBoxValue.id == mandatory && !checkBoxValue.isChecked) {
                    mListener?.showToast(getString(R.string.msg_check) + " " + checkBoxValue.tag.toString())
                    return false
                }
            }
        }

        return true
    }

    private fun getAssetRentPostCheckListData(): ArrayList<ASTAssetRentPreCheckLists> {

        val assetRentPreCheckLists: ArrayList<ASTAssetRentPreCheckLists> = arrayListOf()

        //mBinding.btnSave.isEnabled = false

        if (editTextList.size > 0) {
            for (editText in editTextList) {
                if (editText.tag == Constant.DynamicFormDataTypes.Decimal.value ||
                        editText.tag == Constant.DynamicFormDataTypes.Integer.value ||
                        editText.tag == Constant.DynamicFormDataTypes.Memo.value ||
                        editText.tag == Constant.DynamicFormDataTypes.Text.value) {
                    if (editText.text.toString().isNotEmpty()) {
                        val assetRentPreCheckList = ASTAssetRentPreCheckLists()
                        assetRentPreCheckList.specificationId = editText.id
                        assetRentPreCheckList.value = editText.text.toString()
                        assetRentPreCheckLists.add(assetRentPreCheckList)
                    }
                } else if (editText.tag == Constant.DynamicFormDataTypes.Date.value || editText.tag == Constant.DynamicFormDataTypes.DateTime.value) {
                    if (editText.text.toString().isNotEmpty()) {
                        val assetRentPreCheckList = ASTAssetRentPreCheckLists()
                        assetRentPreCheckList.specificationId = editText.id
                        assetRentPreCheckList.dateValue = serverFormatDate(editText.text.toString())
                        assetRentPreCheckLists.add(assetRentPreCheckList)
                    }
                }
            }
        }

        if (spinnerList.size > 0) {
            for (spinner in spinnerList) {
                if (spinner.selectedItem.toString().isNotEmpty() && spinner.selectedItem.toString() != getString(R.string.select)) {
                    val assetRentPreCheckList = ASTAssetRentPreCheckLists()
                    assetRentPreCheckList.specificationId = spinner.id
                    assetRentPreCheckList.specificationValueId = spinner.tag.toString().toInt()
                    assetRentPreCheckLists.add(assetRentPreCheckList)
                }
            }
        }

        if (checkBoxList.size > 0) {
            for (checkBox in checkBoxList) {
                if (checkBox.isChecked) {
                    val assetRentPreCheckList = ASTAssetRentPreCheckLists()
                    assetRentPreCheckList.specificationId = checkBox.id
                    assetRentPreCheckList.value = "Y"
                    assetRentPreCheckLists.add(assetRentPreCheckList)
                }
            }
        }
        return assetRentPreCheckLists
    }

    private fun navigateToSummaryScreen() {
        val fragment = AssetPostCheckListSummaryFragment()
        val bundle = Bundle()
        bundle.putInt(Constant.KEY_ASSET_RENT_ID, validateAssetForAssignAndReturnResponse?.assetRentId
                ?: 0)
        bundle.putBoolean(Constant.KEY_IS_MOVABLE, assetBookingRequestLine?.isMovable == "Y")
        fragment.arguments = bundle
        mListener?.addFragment(fragment, true)
    }

    private fun saveReturnCheckListData(latitude: Double, longitude: Double) {
        mListener?.showProgressDialog()
        APICall.returnAsset(getAssetReturnsData(latitude, longitude), getAssetRentPostCheckListData(), getSignatureView(), object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                navigateToSummaryScreen()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun getAssetReturnsData(latitude: Double, longitude: Double): ASTAssetRents {
        val assetRents = ASTAssetRents()
        assetRents.receiveDate = formatDateTimeInMillisecond(Date())
        if (!TextUtils.isEmpty(mBinding.edtOdometerEnd.text.toString()))
            assetRents.odometerEnd = mBinding.edtOdometerEnd.text.toString().toDouble()
        assetRents.remarks = mBinding.edtRemarks.text.toString()
        if (mBinding.edtFineAmountLayout.isVisible && mBinding.edtFineAmount.isVisible && !TextUtils.isEmpty(mBinding.edtFineAmount.text.toString()))
            assetRents.fineAmount = BigDecimal(currencyToDouble(mBinding.edtFineAmount.text.toString()) as Long)
           // assetRents.fineAmount = mBinding.edtFineAmount.text.toString().toDouble()


        assetRents.receiveLatitude = latitude
        assetRents.receiveLongitude = longitude
        assetRents.receivedByAccountID = MyApplication.getPrefHelper().accountId
        assetRents.assignDate = formatDateTimeInMillisecond(validateAssetForAssignAndReturnResponse?.assignDate)
        assetRents.assetRentId = validateAssetForAssignAndReturnResponse?.assetRentId
        assetRents.assetRentTypeID = assetBookingRequestLine?.rentTypeID


        if (assetBookingRequestLine != null) {
            assetRents.bookingRequestLineId = assetBookingRequestLine?.bookingRequestLineID
            assetRents.assetId = assetId
            assetRents.tenurePeriod = assetBookingRequestLine?.tenurePeriod
            assetRents.distance = assetBookingRequestLine?.distance
        }

        if (assetRentalDetails != null) {
            assetRents.assignLongitude = assetRentalDetails?.assignLongitude ?: 0.0
            assetRents.assignLatitude = assetRentalDetails?.assignLatitude ?: 0.0
            assetRents.odometerStart = assetRentalDetails?.odometerStart ?: 0.0
            assetRents.assignByAccountId = assetRentalDetails?.assignByAccountId
        }

        return assetRents
    }

    private fun getSignatureView(): ArrayList<COMDocumentReference> {
        val documentList: ArrayList<COMDocumentReference> = arrayListOf()
        val comDocumentReference = COMDocumentReference()
        val bitmap = mBinding.signatureView.signatureBitmap
        documentList.clear()
        if (bitmap != null)
            comDocumentReference.data = ImageHelper.getBase64String(bitmap)
        comDocumentReference.extension = "jpeg"
        comDocumentReference.documentName = getString(R.string.title_signature) + "_" + "$assetId"

        documentList.add(comDocumentReference)

        return documentList
    }


    interface Listener {
        fun popBackStack()
        fun showProgressDialog()
        fun dismissDialog()
        fun showToast(message: String)
        fun showToolbarBackButton(title: Int)
        fun finish()
        fun showSnackbarMsg(message: Int)
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog(message: Int)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
    }

}