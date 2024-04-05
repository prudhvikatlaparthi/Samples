package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.databinding.FragmentViolationTypeEntryBinding
import com.sgs.citytax.model.LAWViolationType
import com.sgs.citytax.model.MultipleViolationTypes
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.currencyToDouble
import com.sgs.citytax.util.formatWithPrecision
import java.math.BigDecimal
import java.util.*

class ViolationTypeEntryFragment : BaseFragment() {
    private val TAG = "ViolationTypeEntryFragm"
    private lateinit var mBinding: FragmentViolationTypeEntryBinding
    private var mListener: Listener? = null
    private var mViolation: MultipleViolationTypes? = null
    private var selectedViolatorType: LAWViolationType? = null
    private var mViolationTypes: ArrayList<LAWViolationType> = arrayListOf()
    private var mViolationClasses: ArrayList<LAWViolationType> = arrayListOf()
    val childViolatons: ArrayList<LAWViolationType> = arrayListOf()

    var itemTypeSelector: AdapterView.OnItemSelectedListener? = null
    var itemClassSelector: AdapterView.OnItemSelectedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_violation_type_entry, container, false)
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
            mViolation = it.getParcelable(Constant.KEY_VIOLATIONS)
            if (it.containsKey(Constant.KEY_VIOLATION_TYPES))
                mViolationTypes = it.getParcelableArrayList<LAWViolationType>(Constant.KEY_VIOLATION_TYPES) as ArrayList<LAWViolationType>

            if (it.containsKey(Constant.KEY_VIOLATION_CLASSES)) {
                mViolationClasses.clear()
                mViolationClasses = it.getParcelableArrayList<LAWViolationType>(Constant.KEY_VIOLATION_CLASSES) as ArrayList<LAWViolationType>
            }

            if (mViolationClasses.isNotEmpty() && mViolationClasses[0].violationTypeID != -1) {
                mViolationClasses.add(0, LAWViolationType(violationType = getString(R.string.select),
                        violationTypeID = -1,
                        parentViolationTypeID = -1,
                        parentViolationType = getString(R.string.select)))
            }

        }
        if (mViolation == null) mViolation = MultipleViolationTypes()
        //endregion
        setListeners()
        bindSpinners()
    }

    private fun bindSpinners() {
        val adapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, mViolationClasses)
        mBinding.spnViolationClass.adapter = adapter

        childViolatons.clear()
        if (mViolationTypes.isNotEmpty()) {
            for (type in mViolationTypes) {
                if (type.parentViolationTypeID != null) {
                    childViolatons.add(type)
                }
            }
        }
        childViolatons.add(0, LAWViolationType(violationType = getString(R.string.select),
                violationTypeID = -1,
                parentViolationTypeID = -1,
                parentViolationType = getString(R.string.select)))

        val violationTypesAdapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, childViolatons)
        mBinding.spnViolationType.adapter = violationTypesAdapter

        setClassesTypes(0)
//        bindData()
    }

    private fun setChildViolationType(isFrom: Int, violationParent: LAWViolationType? = null) {
        childViolatons.clear()
        if (mViolationTypes.isNotEmpty()) {
            for (type in mViolationTypes) {
                if (type.parentViolationTypeID != null) {
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
                    childViolatons.addAll(mViolationTypes)
                }
            }

            childViolatons.add(0, LAWViolationType(violationType = getString(R.string.select),
                    violationTypeID = -1,
                    parentViolationTypeID = -1,
                    parentViolationType = getString(R.string.select)))
        }

        val violationTypesAdapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, childViolatons)
        mBinding.spnViolationType.adapter = violationTypesAdapter

        mBinding.spnViolationType.onItemSelectedListener = null
        if (violationParent != null) {
            mBinding.spnViolationType.setSelection(childViolatons.indexOf(violationParent))
            violationParent.violationTypeID?.let { fetchAmount(it) }
            mBinding.edtViolationDetails.setText(violationParent.violationDetails)
        } else {
            mBinding.spnViolationType.setSelection(0)
            fetchAmount(0)
            mBinding.edtViolationDetails.setText("")
        }
        Handler().postDelayed(Runnable {
            mBinding.spnViolationType.onItemSelectedListener = itemTypeSelector
        }, 100)
    }

    private fun setListeners() {
        itemTypeSelector = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedViolatorType = LAWViolationType()
                if (parent != null && parent.selectedItem != null)
                    selectedViolatorType = parent.selectedItem as LAWViolationType

                setClassesTypes(2, selectedViolatorType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        itemClassSelector = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setClassesTypes(1, parent?.selectedItem as LAWViolationType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        mBinding.spnViolationType.onItemSelectedListener = itemTypeSelector
        mBinding.spnViolationClass.onItemSelectedListener = itemClassSelector

        mBinding.btnSave.setOnClickListener {
            if (validateView())
                saveViolation()
        }
    }

    private fun setClassesTypes(isFrom: Int, violationParent: LAWViolationType? = null) {
        when (isFrom) {
            0 -> {
                for (violationType in childViolatons) {
                    if (violationType.violationTypeID == mViolation?.violationTypeId) {

                        setChildViolationType(2, violationType)

                        mBinding.spnViolationClass.onItemSelectedListener = null

                        for (violation in mViolationClasses) {
                            if (violation.violationTypeID == violationType.parentViolationTypeID) {
                                mBinding.spnViolationClass.setSelection(mViolationClasses.indexOf(violation))
                                break
                            }
                        }

                        mViolation?.violationTypeId?.let { fetchAmount(it) }
                        mBinding.edtViolationDetails.setText(mViolation?.violationDetails)

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
                fetchAmount(0)
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

    private fun fetchAmount(violationTypeId: Int) {
        APICall.getEstimatedFineAmount(violationTypeId, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                response.let {
                    mBinding.edtAmount.setText(formatWithPrecision(it))
                }
            }

            override fun onFailure(message: String) {
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun saveViolation() {
        val multipleViolationTypes = MultipleViolationTypes()
        if (mBinding.edtAmount.text.toString().isNotEmpty())
            multipleViolationTypes.fineAmount = BigDecimal("${currencyToDouble(mBinding.edtAmount.text.toString().trim())}")
        if (mBinding.edtViolationDetails.text.toString().isNotEmpty())
            multipleViolationTypes.violationDetails = mBinding.edtViolationDetails.text.toString().trim()
        if (mBinding.spnViolationType.selectedItem != null) {
            val violationType = mBinding.spnViolationType.selectedItem as LAWViolationType
            multipleViolationTypes.violationTypeId = violationType.violationTypeID
            multipleViolationTypes.pricingRuleId = violationType.pricingRuleID
            multipleViolationTypes.violationType = violationType.violationType
            multipleViolationTypes.applicableOnDriver = violationType.applicableOnDriver
        }
        if (mViolation?.violationID == null || TextUtils.isEmpty(mViolation?.violationID))
            multipleViolationTypes.violationID = "${UUID.randomUUID()}"
        else
            multipleViolationTypes.violationID = mViolation?.violationID

        val list = ObjectHolder.violations
        var index = -1
        if (list.isNotEmpty()) {
            for (item in list) {
                if (item.violationID == multipleViolationTypes.violationID && index == -1) {
                    index = list.indexOf(item)
                    break
                }
            }
        }
        if (index == -1)
            list.add(multipleViolationTypes)
        else
            list[index] = multipleViolationTypes
        ObjectHolder.violations = arrayListOf()
        ObjectHolder.violations.addAll(list)

        Handler().postDelayed({
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
            mListener?.popBackStack()
        }, 500)
    }

    private fun validateView(): Boolean {
        if (mBinding.spnViolationType.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violation_type)}")
            return false
        }
        if ((mBinding.spnViolationType.selectedItem as LAWViolationType).violationTypeID  == -1) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violation_type)}")
            return false
        }
        if (mBinding.edtViolationDetails.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violation_details)}")
            return false
        }

        if (mBinding.spnViolationType.selectedItem != null) {
            val violationType = mBinding.spnViolationType.selectedItem as LAWViolationType
            for (item in ObjectHolder.violations) {
                if (mViolation != null) {
                    if (mViolation!!.violationTypeId == violationType.violationTypeID) {
                        return true
                    } else {
                        if (item.violationTypeId == violationType.violationTypeID) {
                            mListener?.showSnackbarMsg(getString(R.string.msg_violation_type_selected))
                            return false
                        }
                    }
                } else {
                    if (item.violationTypeId == violationType.violationTypeID) {
                        mListener?.showSnackbarMsg(getString(R.string.msg_violation_type_selected))
                        return false
                    }
                }

                /*if (item.violationTypeId == violationType.violationTypeID) {
                   mListener?.showSnackbarMsg(getString(R.string.msg_violation_type_selected))
                   return false
               }*/
            }
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