package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetEstimatedImpoundAmount
import com.sgs.citytax.api.response.EstimatedImpoundAmountResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentImpoundmentTypeEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.currencyToDouble
import com.sgs.citytax.util.formatWithPrecision
import java.math.BigDecimal
import java.util.*

class ImpoundmentTypeEntryFragment : BaseFragment() {
    private var isCalculateDone: Boolean = false
    private lateinit var mBinding: FragmentImpoundmentTypeEntryBinding
    private var mListener: Listener? = null
    private var mImpoundment: MultipleImpoundmentTypes? = null
    private var mSelectedCraneType: VULAWTowingCraneTypes? = null


    private var mImpoundmentTypes: ArrayList<LAWImpoundmentType>? = arrayListOf()
    private var mImpoundmentSubTypes: ArrayList<LAWImpoundmentSubType>? = arrayListOf()
    private var mImpoundmentReasons: ArrayList<LAWImpoundmentReason>? = arrayListOf()
    var mViolationTypes: ArrayList<LAWViolationType> = arrayListOf()
    private var mPoliceStationYards: ArrayList<PoliceStationYards> = arrayListOf()
    private var mCraneTypes: ArrayList<VULAWTowingCraneTypes> = arrayListOf()

    private var selectedFineAmount: String? = null
    private var selectedViolationDetails: String? = null


    val childViolatons: java.util.ArrayList<LAWViolationType> = arrayListOf()
    private var mViolationClasses: java.util.ArrayList<LAWViolationType> = arrayListOf()

    private var mSelectedViolationTypes: java.util.ArrayList<LAWViolationType> = arrayListOf()
    private var mSelectedViolationClasses: java.util.ArrayList<LAWViolationType> = arrayListOf()

    private var selectedViolatorType: LAWViolationType? = null
    var itemTypeSelector: AdapterView.OnItemSelectedListener? = null
    var itemClassSelector: AdapterView.OnItemSelectedListener? = null

    var impoundViolationCode = Constant.ViolationTypeCode.ANIMAL.code

    var fromEdit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_impoundment_type_entry,
            container,
            false
        )
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            mImpoundment = it.getParcelable(Constant.KEY_IMPOUNDMENTS)
            if (it.containsKey(Constant.KEY_IMPOUNDMENT_TYPES))
                mImpoundmentTypes =
                    it.getParcelableArrayList<LAWImpoundmentType>(Constant.KEY_IMPOUNDMENT_TYPES) as ArrayList<LAWImpoundmentType>
            if (it.containsKey(Constant.KEY_IMPOUNDMENT_SUB_TYPES))
                mImpoundmentSubTypes =
                    it.getParcelableArrayList<LAWImpoundmentSubType>(Constant.KEY_IMPOUNDMENT_SUB_TYPES) as ArrayList<LAWImpoundmentSubType>
            if (it.containsKey(Constant.KEY_IMPOUNDMENT_REASONS))
                mImpoundmentReasons =
                    it.getParcelableArrayList<LAWImpoundmentReason>(Constant.KEY_IMPOUNDMENT_REASONS) as ArrayList<LAWImpoundmentReason>
            if (it.containsKey(Constant.KEY_VIOLATION_TYPES))
                mViolationTypes =
                    it.getParcelableArrayList<LAWViolationType>(Constant.KEY_VIOLATION_TYPES) as ArrayList<LAWViolationType>
            if (it.containsKey(Constant.KEY_POLICE_STATION_YARDS))
                mPoliceStationYards =
                    it.getParcelableArrayList<PoliceStationYards>(Constant.KEY_POLICE_STATION_YARDS) as ArrayList<PoliceStationYards>
            if (it.containsKey(Constant.KEY_CRANE_TYPES))
                mCraneTypes =
                    it.getParcelableArrayList<VULAWTowingCraneTypes>(Constant.KEY_CRANE_TYPES) as ArrayList<VULAWTowingCraneTypes>
        }

        setTypeClass()
        mImpoundmentTypes?.let { it ->
            var impoundmentTypes: ArrayList<LAWImpoundmentType> = arrayListOf()
            for (impoundment in it) {
                impoundment.applicableOnAnimal?.let {
                    if (it == "Y")
                        impoundmentTypes.add(impoundment)
                }
            }
            mImpoundmentTypes = impoundmentTypes
        }
        if (mImpoundment == null) mImpoundment = MultipleImpoundmentTypes()
        //endregion
        setListeners()

    }

    private fun setTypeClass() {
        childViolatons.clear()

        for (violationType in mViolationTypes) {
            if (violationType.parentViolationTypeID != null
                && violationType.violatorTypeCode == impoundViolationCode
            )
                childViolatons.add(violationType)
        }

        childViolatons.add(
            0, LAWViolationType(
                violationType = getString(R.string.select),
                violationTypeID = -1,
                parentViolationTypeID = -1,
                parentViolationType = getString(R.string.select)
            )
        )


        mViolationClasses.clear()

        for (violationClass in mViolationTypes) {
            if (violationClass.parentViolationTypeID == null
                && violationClass.violatorTypeCode == impoundViolationCode && isValidParent(
                    violationClass
                )
            ) {
                mViolationClasses.add(violationClass)
            }
        }

        mViolationClasses.add(
            0, LAWViolationType(
                violationType = getString(R.string.select),
                violationTypeID = -1,
                parentViolationTypeID = -1,
                parentViolationType = getString(R.string.select)
            )
        )


        bindViolationClassTypes()
    }

    private fun bindViolationClassTypes() {

        val adapter = ArrayAdapter<LAWViolationType>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mViolationClasses
        )
        mBinding.spnViolationClass.adapter = adapter

        val violationTypesAdapter = ArrayAdapter<LAWViolationType>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            childViolatons
        )
        mBinding.spnViolationType.adapter = violationTypesAdapter

        setClassesTypes(0)
    }

    private fun isValidParent(violationClass: LAWViolationType): Boolean {
        for (item in childViolatons) {
            if (item.parentViolationTypeID == violationClass.violationTypeID) {
                return true
            }
        }
        return false
    }


    private fun setClassesTypes(isFrom: Int, violationParent: LAWViolationType? = null) {
        when (isFrom) {
            0 -> {
                for (violationType in childViolatons) {
                    if (violationType.violationTypeID == mImpoundment?.violationTypeID) {

                        setChildViolationType(2, violationType)

                        mBinding.spnViolationClass.onItemSelectedListener = null

                        for (violation in mViolationClasses) {
                            if (violation.violationTypeID == violationType.parentViolationTypeID) {
                                mBinding.spnViolationClass.setSelection(
                                    mViolationClasses.indexOf(
                                        violation
                                    )
                                )
                                break
                            }
                        }

                        mImpoundment?.fineAmount?.let {
                            mBinding.edtFineAmount.filters =
                                arrayOf<InputFilter>(InputFilter.LengthFilter(8 + 15))
                            mBinding.edtFineAmount.setText("${formatWithPrecision(it)}")
                            selectedFineAmount = it.toString()
                        }

                        mBinding.edtViolationDetails.setText(mImpoundment?.violationDetails)

                        Handler().postDelayed(Runnable {
                            mBinding.spnViolationClass.onItemSelectedListener = itemClassSelector
                        }, 100)

                        break
                    }
                }
            }
            1 -> {
                //from CLASS_SELECTION
                setChildViolationType(isFrom, violationParent)
            }
            2 -> {
                //From TYPE_SELECTION
                fetchFineAmount(0)
                mBinding.edtViolationDetails.setText("")

                setChildViolationType(isFrom, violationParent)

                for (item in mViolationClasses) {
                    if (item.violationTypeID == violationParent?.parentViolationTypeID) {
                        mBinding.spnViolationClass.onItemSelectedListener = null
                        mBinding.spnViolationClass.setSelection(mViolationClasses.indexOf(item))

                        Handler().postDelayed(Runnable {
                            mBinding.spnViolationClass.onItemSelectedListener = itemClassSelector
                        }, 100)

                        break
                    }
                }
            }
        }
    }


    private fun setChildViolationType(isFrom: Int, violationParent: LAWViolationType? = null) {
        childViolatons.clear()
        if (mViolationTypes.isNotEmpty()) {
            for (type in mViolationTypes) {
                if (type.parentViolationTypeID != null && type.violationTypeCode == impoundViolationCode) {
                    childViolatons.add(type)
                }
            }

            //Filter according to the parentSelection
            if (violationParent != null) {
                childViolatons.clear()
                for (type in mViolationTypes) {
                    if (isFrom == 1) {
                        if (type.parentViolationTypeID == violationParent.violationTypeID) {
                            childViolatons.add(type)
                        }
                    } else {
                        if (type.parentViolationTypeID == violationParent.parentViolationTypeID) {
                            childViolatons.add(type)
                        }
                    }
                }

                if (childViolatons.isNullOrEmpty()) {
                    for (violationType in mViolationTypes) {
                        if (violationType.parentViolationTypeID != null
                            && violationType.violatorTypeCode == impoundViolationCode
                        )
                            childViolatons.add(violationType)
                    }
                }
            }

            childViolatons.add(
                0, LAWViolationType(
                    violationType = getString(R.string.select),
                    violationTypeID = -1,
                    parentViolationTypeID = -1,
                    parentViolationType = getString(R.string.select)
                )
            )
        }

        val violationTypesAdapter = ArrayAdapter<LAWViolationType>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            childViolatons
        )
        mBinding.spnViolationType.adapter = violationTypesAdapter

        mBinding.spnViolationType.onItemSelectedListener = null
        if (violationParent != null) {
            mBinding.spnViolationType.setSelection(childViolatons.indexOf(violationParent))
//            violationParent.violationTypeID?.let { fetchFineAmount(it) }

            violationParent?.violationTypeID?.let {
                if (mImpoundment?.violationTypeID != violationParent.violationTypeID && selectedFineAmount != null) {
                    selectedFineAmount = null
                    fetchFineAmount(it)
                } else if (selectedFineAmount == null) {
                    fetchFineAmount(it)
                }
            }
            mBinding.edtViolationDetails.setText(violationParent.violationDetails)
        } else {
            mBinding.spnViolationType.setSelection(0)
            fetchFineAmount(0)
            mBinding.edtViolationDetails.setText("")
        }
        Handler().postDelayed(Runnable {
            mBinding.spnViolationType.onItemSelectedListener = itemTypeSelector
        }, 100)
    }


    private fun bindSpinners() {
        mImpoundmentTypes?.let {
            if (it.isNullOrEmpty())
                mBinding.spnImpoundmentType.adapter = null
            else {
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it)
                mBinding.spnImpoundmentType.adapter = adapter
            }
        }
        mImpoundmentReasons?.let {
            if (it.isNullOrEmpty())
                mBinding.spnImpoundmentReason.adapter = null
            else {
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it)
                mBinding.spnImpoundmentReason.adapter = adapter
            }
        }

        mPoliceStationYards?.let {
            if (it.isNullOrEmpty())
                mBinding.spnYard.adapter = null
            else {
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it)
                mBinding.spnYard.adapter = adapter
            }
        }

        mCraneTypes?.let {
            if (it.isNullOrEmpty())
                mBinding.spnCraneTypes.adapter = null
            else {
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it)
                mBinding.spnCraneTypes.adapter = adapter
            }
        }
//        mViolationTypes?.let {
//            if (it.isNullOrEmpty())
//                mBinding.spnViolationType.adapter = null
//            else {
//                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it)
//                mBinding.spnViolationType.adapter = adapter
//            }
//        }
        bindViolationClassTypes()
        bindData()
    }

    private fun filterImpoundmentSubTypes(impoundmentTypeID: Int) {
        val impoundmentSubTypes: ArrayList<LAWImpoundmentSubType> = arrayListOf()
        mImpoundmentSubTypes?.let {
            if (!it.isNullOrEmpty()) {
                for (subType in it) {
                    if (subType.impoundmentTypeID == impoundmentTypeID)
                        impoundmentSubTypes.add(subType)
                }
            }

            if (impoundmentSubTypes.size > 0) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    impoundmentSubTypes
                )
                mBinding.spnImpoundmentSubType.adapter = adapter
            } else {
                mBinding.spnImpoundmentSubType.adapter = null
            }
        }
    }

    private fun setListeners() {
        mBinding.spnImpoundmentType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var impoundmentType: LAWImpoundmentType? = LAWImpoundmentType()
                    if (p0 != null && p0.selectedItem != null)
                        impoundmentType = p0.selectedItem as LAWImpoundmentType
                    mBinding.edtEstimatedImpoundCharge.setText(formatWithPrecision("0"))
                    impoundmentType?.impoundmentTypeID?.let {
                        filterImpoundmentSubTypes(it)
                    }
                    fetchEstimatedQuantityAmount()
                }
            }

        mBinding.spnCraneTypes.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p0 != null && p0.selectedItem != null)
                        mSelectedCraneType = p0.selectedItem as VULAWTowingCraneTypes
                    if(!fromEdit){
                        mBinding.edtTowingTripCount.setText("")
                        mBinding.edtTowingCharge.setText(formatWithPrecision("0"))
                    }
                }
            }

        mBinding.edtTowingTripCount.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val towingTripCount = p0.toString().toIntOrNull() ?: 0
                mBinding.edtTowingCharge.setText(formatWithPrecision(mSelectedCraneType?.towingCranePrice?.toDouble()?.times(towingTripCount)))
                /*if (p0.toString().isNotEmpty() && p0.toString().trim() != "0"){
                }else
                    mBinding.edtTowingCharge.setText(formatWithPrecision("0"))*/
            }

        })

//        mBinding.spnViolationType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                var violationType: LAWViolationType? = LAWViolationType()
//                if (p0 != null && p0.selectedItem != null)
//                    violationType = p0.selectedItem as LAWViolationType
//
//                filterViolationClasses(violationType?.parentViolationTypeID, violationType?.violationDetails)
//
//                violationType?.violationTypeID?.let {
//                    if (mImpoundment?.violationTypeID != violationType.violationTypeID && selectedFineAmount != null) {
//                        selectedFineAmount = null
//                        fetchFineAmount(it)
//                    } else if (selectedFineAmount == null) {
//                        fetchFineAmount(it)
//                    }
//                }
//            }
//        }


        itemTypeSelector = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedViolatorType = LAWViolationType()
                if (parent != null && parent.selectedItem != null)
                    selectedViolatorType = parent.selectedItem as LAWViolationType

                setClassesTypes(2, selectedViolatorType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        itemClassSelector = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setClassesTypes(1, parent?.selectedItem as LAWViolationType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        mBinding.spnViolationType.onItemSelectedListener = itemTypeSelector
        mBinding.spnViolationClass.onItemSelectedListener = itemClassSelector
        mBinding.edtQuantity.addTextChangedListener {
            isCalculateDone = false
        }

        mBinding.btnGet.setOnClickListener {

            if (!TextUtils.isEmpty(mBinding.edtQuantity.text?.toString()?.trim())) {
                val qty: Int = mBinding.edtQuantity.text?.toString()?.toInt()!!
                if (qty > 0) {
                    isCalculateDone = true
                    fetchEstimatedQuantityAmount()
                } else {
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.quantity)}")
                }
            } else {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.quantity)}")
            }
        }

        mBinding.btnSave.setOnClickListener {
            if (validateView()) {
                if (isCalculateDone) {
                    save()
                }else{
                    mListener?.showSnackbarMsg(getString(R.string.pls_cal_charge_b4_saving))
                }
            }
        }

        bindSpinners()
    }

    private fun fetchEstimatedQuantityAmount() {
        val getEstimatedImpoundAmount = GetEstimatedImpoundAmount()

        var impoundTypeID = 0
        val impoundmentType: LAWImpoundmentType? =
            mBinding.spnImpoundmentType.selectedItem as LAWImpoundmentType?
        impoundmentType?.impoundmentTypeID?.let {
            impoundTypeID = it
        }
        getEstimatedImpoundAmount.impoundmentTypeID = impoundTypeID
        if (!TextUtils.isEmpty(mBinding.edtQuantity.text?.toString()?.trim()))
            getEstimatedImpoundAmount.quantity = mBinding.edtQuantity.text?.toString()?.trim()
        else
            getEstimatedImpoundAmount.quantity = "0"

        mListener?.showProgressDialog()
        APICall.getEstimatedImpoundAmount(
            getEstimatedImpoundAmount,
            object : ConnectionCallBack<EstimatedImpoundAmountResponse> {
                override fun onSuccess(response: EstimatedImpoundAmountResponse) {
                    mBinding.edtEstimatedImpoundCharge.setText(formatWithPrecision("0"))
                    mBinding.edtViolationCharge.setText(formatWithPrecision(response.violationCharge))
                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mBinding.edtEstimatedImpoundCharge.setText("")
                    mBinding.edtViolationCharge.setText("")
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
    }

    private fun fetchFineAmount(violationTypeID: Int) {
        mListener?.showProgressDialog()
        APICall.getEstimatedFineAmount(violationTypeID, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mListener?.dismissDialog()
                if (selectedFineAmount != null) {
                    // mBinding.edtFineAmount.setText(formatWithPrecision(selectedFineAmount))

                    mBinding.edtFineAmount.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(8 + 15))
                    mBinding.edtFineAmount.setText("${formatWithPrecision(selectedFineAmount)}")
                    selectedFineAmount = null
                } else {
                    // mBinding.edtFineAmount.setText(formatWithPrecision(response))
                    mBinding.edtFineAmount.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(8 + 15))
                    mBinding.edtFineAmount.setText("${formatWithPrecision(response)}")
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }

        })
    }


    private fun filterViolationClasses(
        parentViolationTypeID: Int?,
        parentViolationDetail: String? = ""
    ) {
        mBinding.spnViolationClass.adapter = null
        var violationTypes: ArrayList<LAWViolationType> = arrayListOf()
        var violationDetail = ""
        parentViolationTypeID?.let { it ->
            parentViolationDetail?.let {
                violationDetail = it
            }
            if (it == 0)
                violationTypes = arrayListOf()
            else {
                mViolationTypes?.let {
                    for (violationType in it) {
                        if (parentViolationTypeID == violationType.violationTypeID)
                            violationTypes.add(violationType)
                        if (TextUtils.isEmpty(violationDetail) && violationType.violationDetails != null && !TextUtils.isEmpty(
                                violationType.violationDetails
                            )
                        )
                            violationType.violationDetails?.let {
                                violationDetail = it
                            }
                    }
                }
            }
            if (violationTypes.size > 0) {
                val adapter = ArrayAdapter<LAWViolationType>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    violationTypes
                )
                mBinding.spnViolationClass.adapter = adapter
            }
        }

        mBinding.edtViolationDetails.setText(violationDetail)
    }

    private fun bindData() {
        mImpoundment?.let { it ->
            it.quantity?.let {
                mBinding.edtQuantity.setText("$it")
            }
            /* it.impoundmentCharge?.let {
                 mBinding.edtEstimatedImpoundCharge.setText(formatWithPrecision(it))
             }*/
            mBinding.edtEstimatedImpoundCharge.setText(formatWithPrecision("0"))

            isCalculateDone = mImpoundment != null

            if (mImpoundmentTypes != null && mImpoundmentTypes!!.isNotEmpty()) {
                for (impoundmentType in mImpoundmentTypes!!) {
                    if (impoundmentType.impoundmentTypeID == it.impoundmentTypeID) {
                        mBinding.spnImpoundmentType.setSelection(
                            mImpoundmentTypes!!.indexOf(
                                impoundmentType
                            )
                        )
                        break
                    }
                }
            }
            if (mImpoundmentSubTypes != null && mImpoundmentSubTypes!!.isNotEmpty()) {
                for (impoundmentSubType in mImpoundmentSubTypes!!) {
                    if (impoundmentSubType.impoundmentSubTypeID == it.impoundmentSubTypeID) {
                        mBinding.spnImpoundmentSubType.setSelection(
                            mImpoundmentSubTypes!!.indexOf(
                                impoundmentSubType
                            )
                        )
                        break
                    }
                }
            }
            if (mImpoundmentReasons != null && mImpoundmentReasons!!.isNotEmpty()) {
                for (impoundmentReason in mImpoundmentReasons!!) {
                    if (impoundmentReason.impoundmentReason == it.impoundmentReason) {
                        mBinding.spnImpoundmentReason.setSelection(
                            mImpoundmentReasons!!.indexOf(
                                impoundmentReason
                            )
                        )
                        break
                    }
                }
            }
            if (mPoliceStationYards != null && mPoliceStationYards.isNotEmpty()) {
                for (yard in mPoliceStationYards) {
                    if (yard.yardID == it.yardID) {
                        mBinding.spnYard.setSelection(mPoliceStationYards.indexOf(yard))
                        break
                    }
                }
            }
            if (mCraneTypes != null && mCraneTypes.isNotEmpty()) {
                for (craneType in mCraneTypes) {
                    if (craneType.towingCraneTypeID!!.equals(it.towingCraneTypeID)) {
                        fromEdit = true
                        mBinding.spnCraneTypes.setSelection(mCraneTypes.indexOf(craneType))
                        break
                    }
                }
            }
//            if (mViolationTypes != null && mViolationTypes!!.isNotEmpty()) {
//                for (violationType in mViolationTypes!!) {
//                    if (violationType.violationTypeID == it.violationTypeID) {
//                        mBinding.spnViolationType.setSelection(mViolationTypes!!.indexOf(violationType))
//                        break
//                    }
//                }
//            }
//
//            it.fineAmount?.let {
//                mBinding.edtFineAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8 + 15))
//                mBinding.edtFineAmount.setText("${formatWithPrecision(it)}")
//                selectedFineAmount = it.toString()
//            }

            it.violationCharge?.let {
                mBinding.edtViolationCharge.setText("${formatWithPrecision(it)}")
            }
            it.towingTripCount?.let {
                mBinding.edtTowingTripCount.setText("$it")
            }
            it.towingCharge?.let {
                mBinding.edtTowingCharge.setText(formatWithPrecision(it))
            }
            it.extracharge?.let {
                mBinding.edtExtraCharges.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(8 + 15))
                mBinding.edtExtraCharges.setText(formatWithPrecision(it))
            }

            it.rmks?.let {
                mBinding.edtRemarks.setText(it)
            }
            val handler = Handler()
            var runnable: Runnable? = null
            runnable = Runnable {
                it.violationDetails?.let {
                    mBinding.edtViolationDetails.setText(it)
                    selectedViolationDetails = it.toString()
                }
                handler.removeCallbacks(runnable)
            }
            handler.postDelayed(runnable, 500)


        }

        mBinding.edtFineAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtFineAmount.text.toString()
                if (text.isNotEmpty()) {
                    mBinding.edtFineAmount.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                    mBinding.edtFineAmount.setText("${currencyToDouble(text)}")
                }
            } else {
                //this if condition is true when edittext lost focus...
                //check here for number is larger than 10 or not
                val cost = mBinding.edtFineAmount.text.toString()
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = mBinding.edtFineAmount.text.toString().toDoubleOrNull() ?: 0.0
                    mBinding.edtFineAmount.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(8 + 15))
                    mBinding.edtFineAmount.setText("${formatWithPrecision(enteredText)}")
                }
            }
        }

        mBinding.edtExtraCharges.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtExtraCharges.text.toString()
                if (text.isNotEmpty()) {
                    mBinding.edtExtraCharges.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                    mBinding.edtExtraCharges.setText("${currencyToDouble(text)}")
                }
            } else {
                //this if condition is true when edittext lost focus...
                //check here for number is larger than 10 or not
                val cost = mBinding.edtExtraCharges.text.toString()
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = mBinding.edtExtraCharges.text.toString().toDoubleOrNull() ?: 0.0
                    mBinding.edtExtraCharges.filters =
                        arrayOf<InputFilter>(InputFilter.LengthFilter(8 + 15))
                    mBinding.edtExtraCharges.setText("${formatWithPrecision(enteredText)}")
                }
            }
        }
    }

    private fun save() {
        val multipleImpoundmentTypes = MultipleImpoundmentTypes()
        if (mBinding.spnImpoundmentType.selectedItem != null) {
            val impoundmentType = mBinding.spnImpoundmentType.selectedItem as LAWImpoundmentType?
            impoundmentType?.let {
                multipleImpoundmentTypes.impoundmentTypeID = it.impoundmentTypeID
                multipleImpoundmentTypes.impoundmentType = it.impoundmentType
            }
        }
        if (mBinding.spnImpoundmentSubType.selectedItem != null) {
            val impoundmentSubType =
                mBinding.spnImpoundmentSubType.selectedItem as LAWImpoundmentSubType?
            impoundmentSubType?.let {
                multipleImpoundmentTypes.impoundmentSubTypeID = it.impoundmentSubTypeID
            }
        }
        if (mBinding.spnImpoundmentReason.selectedItem != null) {
            val impoundmentReason =
                mBinding.spnImpoundmentReason.selectedItem as LAWImpoundmentReason?
            impoundmentReason?.let {
                multipleImpoundmentTypes.impoundmentReason = it.impoundmentReason
            }
        }
        if (mBinding.spnYard.selectedItem != null) {
            val yards = mBinding.spnYard.selectedItem as PoliceStationYards?
            yards?.let {
                multipleImpoundmentTypes.yardID = it.yardID
            }
        }
        if (mBinding.spnCraneTypes.selectedItem != null) {
            mSelectedCraneType?.let {
                multipleImpoundmentTypes.towingCraneTypeID = it.towingCraneTypeID
            }
        }
        if (mBinding.spnViolationType.selectedItem != null) {
            val violationType = mBinding.spnViolationType.selectedItem as LAWViolationType?
            violationType?.let {
                multipleImpoundmentTypes.violationTypeID = it.violationTypeID
            }
        }
        if (mBinding.edtQuantity.text != null && mBinding.edtQuantity.text.toString().isNotEmpty())
            multipleImpoundmentTypes.quantity = mBinding.edtQuantity.text.toString().trim().toInt()
        if (mBinding.edtTowingTripCount.text != null && mBinding.edtTowingTripCount.text.toString().isNotEmpty())
            multipleImpoundmentTypes.towingTripCount = mBinding.edtTowingTripCount.text.toString().trim().toInt()
        if (mBinding.edtViolationDetails.text != null && mBinding.edtViolationDetails.text.toString()
                .isNotEmpty()
        )
            multipleImpoundmentTypes.violationDetails =
                mBinding.edtViolationDetails.text.toString().trim()
        if (mBinding.edtRemarks.text != null && mBinding.edtRemarks.text.toString().isNotEmpty())
            multipleImpoundmentTypes.rmks = mBinding.edtRemarks.text.toString().trim()
        if (mBinding.edtEstimatedImpoundCharge.text != null && !TextUtils.isEmpty(mBinding.edtEstimatedImpoundCharge.text.toString()))
            multipleImpoundmentTypes.impoundmentCharge = BigDecimal("${currencyToDouble(mBinding.edtEstimatedImpoundCharge.text.toString().trim())}")
        if (mBinding.edtTowingCharge.text != null && !TextUtils.isEmpty(mBinding.edtTowingCharge.text.toString()))
            multipleImpoundmentTypes.towingCharge = BigDecimal("${currencyToDouble(mBinding.edtTowingCharge.text.toString().trim())}")
        if (mBinding.edtFineAmount.text != null && !TextUtils.isEmpty(mBinding.edtFineAmount.text.toString()))
            multipleImpoundmentTypes.fineAmount =
                BigDecimal("${currencyToDouble(mBinding.edtFineAmount.text.toString().trim())}")

        if (mBinding.edtViolationCharge.text != null && !TextUtils.isEmpty(mBinding.edtViolationCharge.text.toString()))
            multipleImpoundmentTypes.violationCharge = BigDecimal(
                "${
                    currencyToDouble(
                        mBinding.edtViolationCharge.text.toString().trim()
                    )
                }"
            )

        if (mBinding.edtExtraCharges.text != null && !TextUtils.isEmpty(mBinding.edtExtraCharges.text.toString()))
            multipleImpoundmentTypes.extracharge =
                BigDecimal("${currencyToDouble(mBinding.edtExtraCharges.text.toString().trim())}")


        if (mImpoundment != null && mImpoundment!!.id != null && mImpoundment!!.id!!.isNotEmpty())
            multipleImpoundmentTypes.id = mImpoundment!!.id
        else
            multipleImpoundmentTypes.id = "${UUID.randomUUID()}"

        val list = ObjectHolder.impoundments
        var index = -1
        if (list.isNotEmpty()) {
            for (item in list) {
                if (item.id == multipleImpoundmentTypes.id && index == -1) {
                    index = list.indexOf(item)
                    break
                }
            }
        }
        if (index == -1)
            list.add(multipleImpoundmentTypes)
        else
            list[index] = multipleImpoundmentTypes
        ObjectHolder.impoundments = arrayListOf()
        ObjectHolder.impoundments.addAll(list)

        Handler().postDelayed({
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
            mListener?.popBackStack()
        }, 500)
    }

    private fun validateView(): Boolean {
        if (mBinding.edtQuantity.text == null || mBinding.edtQuantity.text.toString()
                .isEmpty() || mBinding.edtQuantity.text.toString() == "0"
        ) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.quantity)}")
            return false
        }
        if (mBinding.edtTowingTripCount.text == null || mBinding.edtTowingTripCount.text.toString()
                .isEmpty() || mBinding.edtTowingTripCount.text.toString() == "0"
        ) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.towing_trip_count)}")
            return false
        }
      /*  if (mBinding.spnImpoundmentType.selectedItem != null) {
            val impoundmentType = mBinding.spnImpoundmentType.selectedItem as LAWImpoundmentType
            for (item in ObjectHolder.impoundments) {
                if (mImpoundment != null) {
                    if (mImpoundment!!.impoundmentTypeID == impoundmentType.impoundmentTypeID) {
                        return true
                    } else {
                        if (item.impoundmentTypeID == impoundmentType.impoundmentTypeID) {
                            mListener?.showSnackbarMsg(getString(R.string.msg_impoundment_type_selected))
                            return false
                        }
                    }
                } else {
                    if (item.impoundmentTypeID == impoundmentType.impoundmentTypeID) {
                        mListener?.showSnackbarMsg(getString(R.string.msg_impoundment_type_selected))
                        return false
                    }
                }
            }
        }*/

        if ((mBinding.spnViolationType.selectedItem as LAWViolationType).violationTypeID == -1) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violation_type)}")
            return false
        }

        return true
    }


    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showToast(message: String)
        fun showSnackbarMsg(message: String?)
        fun showProgressDialog(message: Int)
        fun popBackStack()
    }

}