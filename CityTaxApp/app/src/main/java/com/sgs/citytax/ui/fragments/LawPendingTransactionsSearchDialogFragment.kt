package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.GetDropdownFiltersForBusinessSearchResponse
import com.sgs.citytax.databinding.FragmentLawPendingTransactionSearchInfoBinding
import com.sgs.citytax.model.LawPendingTransactionLocations

class LawPendingTransactionsSearchDialogFragment : DialogFragment() {
    private lateinit var mBinding: FragmentLawPendingTransactionSearchInfoBinding
    private var mListener: Listener? = null
    private var location: LawPendingTransactionLocations? = null
    private var filtersData: GetDropdownFiltersForLAWSearchResponse? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(filterData:GetDropdownFiltersForLAWSearchResponse?, location: LawPendingTransactionLocations?) = LawPendingTransactionsSearchDialogFragment().apply {
            this.filtersData = filterData
            this.location = location
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_law_pending_transaction_search_info, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {
        filterData()
        setListeners()
    }

    private fun filterData() {
        val violationType: ArrayList<LAWViolationTypeS>? = arrayListOf()
        val violationSubType: ArrayList<VULAWViolationSubType>? = arrayListOf()
        val impoundType: ArrayList<VULAWImpoundmentType>? = arrayListOf()
        val impoundSubType: ArrayList<VULAWImpoundmentSubType>? = arrayListOf()


        filtersData.let {data->

            data?.lAWViolationTypes?.let { it -> violationType?.addAll(it) }
            data?.vULAWViolationSubTypes?.let { it1-> violationSubType?.addAll(it1) }
            data?.vULAWImpoundmentTypes?.let { it2->impoundType?.addAll(it2) }
            data?.vULAWImpoundmentSubTypes?.let { it3->impoundSubType?.addAll(it3) }


        }

        violationType?.let {
            var vType=LAWViolationTypeS()
            vType.violationType=getString(R.string.select)
            violationType.add(0, vType)
            val violationTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, it)
            mBinding.spnViolationType.adapter = violationTypeAdapter
        }

        violationSubType?.let {
            var vSubType=VULAWViolationSubType()
            vSubType.violationSubType=getString(R.string.select)
            violationSubType.add(0, vSubType)
            val violationSubTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, it)
            mBinding.spnViolationSubType.adapter = violationSubTypeAdapter
        }

        impoundType?.let {
            var impType=VULAWImpoundmentType()
            impType.impoundmentType=getString(R.string.select)
            impoundType.add(0, impType)
            val impoundTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, it)
            mBinding.spnImpoundType.adapter = impoundTypeAdapter
        }

        impoundSubType?.let {
            var impSubType=VULAWImpoundmentSubType()
            impSubType.impoundmentSubType=getString(R.string.select)
            impoundSubType.add(0,impSubType)
            val impoundSubTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, it)
            mBinding.spnImpoundSubType.adapter = impoundSubTypeAdapter
        }

        if (location != null) {
            location?.VehicleNo?.let {
                mBinding.edtVehicleNo.setText(it)
            }
            location?.VehicleOwnerMobile?.let {
                mBinding.edtMobile.setText(it)
            }
        }


        violationType?.let {
            for ((index, obj) in violationType.withIndex()) {
                if (location?.ViolationType?.contentEquals(obj.toString()) == true) {
                    mBinding.spnViolationType.setSelection(index)
                    break
                }
            }
        }
        violationSubType?.let {
            for ((index, obj) in violationSubType.withIndex()) {
                if (location?.ViolationClass?.contentEquals(obj.toString()) == true) {
                    mBinding.spnViolationSubType.setSelection(index)
                    break
                }
            }
        }
        impoundType?.let {
            for ((index, obj) in impoundType.withIndex()) {
                if (location?.ImpoundmentType?.contentEquals(obj.toString()) == true) {
                    mBinding.spnImpoundType.setSelection(index)
                    break
                }
            }
        }
        impoundSubType?.let {
            for ((index, obj) in impoundSubType.withIndex()) {
                if (location?.ImpoundmentSubType?.contentEquals(obj.toString()) == true) {
                    mBinding.spnImpoundSubType.setSelection(index)
                    break
                }
            }
        }

    }

    private fun setListeners() {
        mBinding.btnCancel.setOnClickListener {
            resetValues()
        }

        mBinding.btnApply.setOnClickListener {
            location = LawPendingTransactionLocations()
            if (mBinding.edtVehicleNo.text.toString().trim().isNotEmpty())
                location?.VehicleNo = mBinding.edtVehicleNo.text.toString()

            if (mBinding.edtMobile.text.toString().trim().isNotEmpty()) {
                location?.VehicleOwnerMobile = mBinding.edtMobile.text.toString()
                location?.DriverMobile = mBinding.edtMobile.text.toString()
                location?.GoodsOwnerMobile = mBinding.edtMobile.text.toString()
            }

            if (mBinding.spnViolationType.selectedItem != null && mBinding.spnViolationType.selectedItemPosition!=0)
                location?.ViolationType = mBinding.spnViolationType.selectedItem.toString()

            if (mBinding.spnViolationSubType.selectedItem != null && mBinding.spnViolationSubType.selectedItemPosition!=0)
                location?.ViolationClass = mBinding.spnViolationSubType.selectedItem.toString()

            if (mBinding.spnImpoundType.selectedItem != null  && mBinding.spnImpoundType.selectedItemPosition!=0)
                location?.ImpoundmentType = mBinding.spnImpoundType.selectedItem.toString()

            if (mBinding.spnImpoundSubType.selectedItem != null && mBinding.spnImpoundSubType.selectedItemPosition!=0)
                location?.ImpoundmentSubType = mBinding.spnImpoundSubType.selectedItem.toString()

            mListener?.onLawApplyClick(location)
            dismiss()
        }
    }

    private fun resetValues() {
        location = null
        mListener?.onClearClick()

        mBinding.edtVehicleNo.setText("")
        mBinding.edtMobile.setText("")

        mBinding.spnViolationType.setSelection(0)
        mBinding.spnViolationSubType.setSelection(0)
        mBinding.spnImpoundType.setSelection(0)
        mBinding.spnImpoundSubType.setSelection(0)
    }

    interface Listener {
        fun finish()
        fun onLawApplyClick(location: LawPendingTransactionLocations?)
        fun onClearClick()
    }
}