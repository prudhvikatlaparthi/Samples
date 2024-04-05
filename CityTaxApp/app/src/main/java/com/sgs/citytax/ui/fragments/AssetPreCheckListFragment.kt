package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.AssetBookingRequestLine
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.AssetSpecs
import com.sgs.citytax.api.response.AssetSpecsValueSets
import com.sgs.citytax.api.response.GetSpecificationValueSetResult
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentAssetPreCheckListBinding
import com.sgs.citytax.model.ASTAssetRentPreCheckLists
import com.sgs.citytax.model.ASTAssetRents
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.custom.DatePickerEditText
import com.sgs.citytax.util.*
import java.util.*

class AssetPreCheckListFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentAssetPreCheckListBinding
    private var assetCategoryId: Int? = 0
    private var fromScreen: Constant.QuickMenu? = null
    private var mListener: Listener? = null

    private var assetBookingRequestLine: AssetBookingRequestLine? = null
    private var mHelper: LocationHelper? = null
    private var assetRentId: Int? = 0
    private var assetId: Int? = 0

    private val editTextList = arrayListOf<EditText>()
    private val spinnerList = arrayListOf<Spinner>()
    private val checkBoxList = arrayListOf<CheckBox>()

    private var mandatoryEditList: ArrayList<Int> = arrayListOf()
    private var mandatorySpinnerList: ArrayList<Int> = arrayListOf()
    private var mandatoryCheckBoxList: ArrayList<Int> = arrayListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_asset_pre_check_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mHelper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
    }

    override fun initComponents() {
        processIntent()
        getDynamicForm()
        setViewsVisibility()
        setListeners()
    }

    override fun onDetach() {
        mListener = null
        mHelper?.disconnect()
        super.onDetach()
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
            assetBookingRequestLine = arguments?.getParcelable(Constant.KEY_ASSET_BOOKING_LINE)
            assetId = it.getInt(Constant.KEY_ASSET_ID)
        }
    }

    private fun setViewsVisibility() {
        if (assetBookingRequestLine != null && assetBookingRequestLine?.trackOdometer.equals("Y")) {
            mBinding.edtOdometerStartLayout.visibility = View.VISIBLE
            mBinding.edtOdometerStart.visibility = View.VISIBLE
        } else {
            mBinding.edtOdometerStartLayout.visibility = View.GONE
            mBinding.edtOdometerStart.visibility = View.GONE
        }
    }

    private fun getDynamicForm() {
        mListener?.showProgressDialog()
        clearAll()
        assetCategoryId = assetBookingRequestLine?.assetCategoryID

        APICall.getDynamicFormSpecs4Asset(assetCategoryId
                ?: 0, object : ConnectionCallBack<List<AssetSpecs>> {
            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })
                clearAll()
            }

            override fun onSuccess(response: List<AssetSpecs>) {
                mListener?.dismissDialog()
                clearAll()
                if (response != null && response.isNotEmpty()){
                    bindDynamicData(response)
                }else
                {
                    mListener?.showAlertDialog(getString(R.string.msg_no_data), DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                }

            }
        })
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

    private fun bindDynamicData(assetSpecs: List<AssetSpecs>) {
        if (assetId != null)
            mBinding.txtAssetNo.text = assetId.toString()
        for (assetSpec in assetSpecs) {
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.bottomMargin = resources.getDimension(R.dimen.vertical_spacing).toInt()
            if (assetSpec.dataType == Constant.DynamicFormDataTypes.Checkbox.value) {
                val checkBox = CheckBox(requireContext())
                checkBox.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
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

                mBinding.llDynamic.addView(checkBox)

            }

            if (assetSpec.dataType == Constant.DynamicFormDataTypes.Date.value) {
                val textInputLayout = TextInputLayout(requireContext())
                textInputLayout.hint = assetSpec.specification
                textInputLayout.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textInputLayout.hint = assetSpec.specification

                val datepickerText = DatePickerEditText(requireContext())
                datepickerText.showIcons(true)
                datepickerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                datepickerText.setDisplayDateFormat(displayDateFormat)
                datepickerText.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                datepickerText.tag = assetSpec.dataType
                datepickerText.id = assetSpec.specificationID!!
                if (assetSpec.mandatory == "Y") {
                    mandatoryEditList.add(assetSpec.specificationID!!)
                }

                val timeInMillis = Calendar.getInstance().timeInMillis
                if (assetSpec.specification?.toLowerCase()!!.contains("startdate") || assetSpec.specification?.toLowerCase()!!.contains("fromdate"))
                    datepickerText.setMaxDate(timeInMillis)
                else if (assetSpec.specification?.toLowerCase()!!.contains("enddate") || assetSpec.specification?.toLowerCase()!!.contains("todate") || assetSpec.specification?.toLowerCase()!!.contains("expiry"))
                    datepickerText.setMinDate(timeInMillis)

                editTextList.add(datepickerText)

                textInputLayout.addView(datepickerText)
                mBinding.llDynamic.addView(textInputLayout)
            }

            if (assetSpec.dataType == Constant.DynamicFormDataTypes.DateTime.value) {
                val textInputLayout = TextInputLayout(requireContext())
                textInputLayout.hint = assetSpec.specification
                textInputLayout.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
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
                if (assetSpec.specification?.toLowerCase()!!.contains("startdate") || assetSpec.specification?.toLowerCase()!!.contains("fromdate"))
                    dateTimepickerText.setMaxDate(timeInMillis)
                else if (assetSpec.specification?.toLowerCase()!!.contains("enddate") || assetSpec.specification?.toLowerCase()!!.contains("todate") || assetSpec.specification?.toLowerCase()!!.contains("expiry"))
                    dateTimepickerText.setMinDate(timeInMillis)

                editTextList.add(dateTimepickerText)

                textInputLayout.addView(dateTimepickerText)
                mBinding.llDynamic.addView(textInputLayout)
            }

            if (assetSpec.dataType == Constant.DynamicFormDataTypes.Spinner.value) {
                val llayout = LinearLayout(requireContext())
                llayout.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                llayout.orientation = LinearLayout.HORIZONTAL

                val tvDynamic = TextView(requireContext())
                tvDynamic.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5.0f)
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
                        spinner.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5.0f)
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
                            mBinding.llDynamic.addView(llayout)
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
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textInputLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

                val editText = TextInputEditText(requireContext())
                editText.id = assetSpec.specificationID!!
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                editText.tag = assetSpec.dataType
                editText.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
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
                mBinding.llDynamic.addView(textInputLayout)
            }


        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
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
                                savePreCheckListData(latitude, longitude)
                            }

                            override fun start() {
                                mListener?.showProgressDialog(R.string.msg_location_fetching)
                            }
                        })
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun getAssetRentPreCheckListData(): ArrayList<ASTAssetRentPreCheckLists>? {

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

    private fun getAssetRents(latitude: Double, longitude: Double): ASTAssetRents {
        val assetRents = ASTAssetRents()
        if (assetBookingRequestLine != null) {
            assetRents.bookingRequestLineId = assetBookingRequestLine?.bookingRequestLineID
            assetRents.assetId = assetId
            assetRents.tenurePeriod = assetBookingRequestLine?.tenurePeriod
            assetRents.distance = assetBookingRequestLine?.distance
            assetRents.assignByAccountId = MyApplication.getPrefHelper().accountId
            assetRents.assetRentTypeID = assetBookingRequestLine?.rentTypeID
        }
        assetRents.assignDate = formatDateTimeInMillisecond(Date())
        if (!TextUtils.isEmpty(mBinding.edtOdometerStart.text.toString()))
            assetRents.odometerStart = mBinding.edtOdometerStart.text.toString().toDouble()
        assetRents.assignLatitude = latitude
        assetRents.assignLongitude = longitude
        if (assetRentId != 0)
            assetRents.assetRentId = assetRentId

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

    private fun savePreCheckListData(latitude: Double, longitude: Double) {
        mListener?.showProgressDialog()
        APICall.assignAsset(assetBookingRequestLine?.rentTypeID,getAssetRents(latitude, longitude), getAssetRentPreCheckListData(), getSignatureView(), object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                assetRentId = response
                /* Handler().postDelayed({
                     targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                     mListener?.popBackStack()
                 }, 500)*/
                navigateToSummaryScreen(assetRentId ?: 0)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun navigateToSummaryScreen(assetRentId: Int) {
        val fragment = AssetPreCheckListSummaryFragment()
        val bundle = Bundle()
        bundle.putInt(Constant.KEY_ASSET_RENT_ID, assetRentId)
        bundle.putBoolean(Constant.KEY_IS_MOVABLE, assetBookingRequestLine?.isMovable =="Y")
        fragment.arguments = bundle
        mListener?.addFragment(fragment,false)
    }


    private fun validateView(): Boolean {
        if (mBinding.edtOdometerStart.isVisible && mBinding.edtOdometerStart.text.toString().isEmpty()) {
            mListener?.showSnackbarMsg(R.string.msg_odometer_empty)
            mBinding.edtOdometerStart.requestFocus()
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