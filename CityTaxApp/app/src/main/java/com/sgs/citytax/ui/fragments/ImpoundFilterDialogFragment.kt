package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentImpoundFilterDialogBinding
import com.sgs.citytax.model.Impoundment
import com.sgs.citytax.model.LAWImpoundmentSubType
import com.sgs.citytax.model.LAWImpoundmentType

class ImpoundFilterDialogFragment : DialogFragment() {
    private lateinit var mBinding: FragmentImpoundFilterDialogBinding
    private var mListener: Listener? = null


    private var mImpoundmentSubTypes: ArrayList<LAWImpoundmentSubType> = arrayListOf()
    private var mImpoundmentTypes: ArrayList<LAWImpoundmentType> = arrayListOf()

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
        fun newInstance() = ImpoundFilterDialogFragment().apply {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_impound_filter_dialog, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {
        setListeners()
        bindSpinners()
        setEvents()
    }

    private fun bindSpinners() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("LAW_Impoundments", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mImpoundmentTypes = response.impoundmentTypes as ArrayList<LAWImpoundmentType>
                mImpoundmentSubTypes = response.impoundmentSubTypes as ArrayList<LAWImpoundmentSubType>

                mImpoundmentTypes?.let {
                    it.add(0, LAWImpoundmentType(activity?.getString(R.string.select), "", -1, "", ""))
                }

                mImpoundmentSubTypes?.let {
                    it.add(0, LAWImpoundmentSubType(activity?.getString(R.string.select), "", -1, -1))
                }

                if (mImpoundmentTypes.isNullOrEmpty())
                    mBinding.spnImpoundmentType.adapter = null
                else {
                    val adapter = ArrayAdapter(mBinding.spnImpoundmentType.context, android.R.layout.simple_list_item_1, mImpoundmentTypes)
                    mBinding.spnImpoundmentType.adapter = adapter
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }

        })
    }

    private fun setEvents() {
        mBinding.spnImpoundmentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var impoundmentType: LAWImpoundmentType? = LAWImpoundmentType()
                if (p0 != null && p0.selectedItem != null) {

                    if (p0.selectedItem is LAWImpoundmentType) {
                        impoundmentType = p0.selectedItem as LAWImpoundmentType
                        impoundmentType?.impoundmentTypeID?.let {
                            filterImpoundmentSubTypes(it)
                        }
                    }
                }

            }
        }
    }

    private fun filterImpoundmentSubTypes(impoundmentTypeID: Int) {

        val impoundmentSubTypes: java.util.ArrayList<LAWImpoundmentSubType> = arrayListOf()

        impoundmentSubTypes?.let {
            it.add(0, LAWImpoundmentSubType(activity?.getString(R.string.select), "", -1, -1))
        }
        if (!mImpoundmentSubTypes.isNullOrEmpty()) {
            for (subType in mImpoundmentSubTypes) {
                if (subType.impoundmentTypeID == impoundmentTypeID)
                    impoundmentSubTypes.add(subType)
            }
        }

        if (impoundmentSubTypes.size > 0) {
            val adapter = ArrayAdapter(mBinding.spnImpoundmentType.context, android.R.layout.simple_spinner_dropdown_item, impoundmentSubTypes)
            mBinding.spnImpoundmentSubType.adapter = adapter
        } else {
            mBinding.spnImpoundmentSubType.adapter = null
        }
    }

    private fun setListeners() {

        mBinding.btnCancel.setOnClickListener {
            resetValues()
        }

        mBinding.btnApply.setOnClickListener {

            val impoundment = Impoundment()
            val impoundmentType: LAWImpoundmentType? = mBinding.spnImpoundmentType.selectedItem as LAWImpoundmentType?
            impoundmentType?.impoundmentTypeID?.let {
                if (it.toString().intOrString() != null)
                    impoundment.impoundmentTypeID = it
                else
                    impoundment.impoundmentTypeID = 0
            }
            val impoundmentSubType: LAWImpoundmentSubType? = mBinding.spnImpoundmentSubType.selectedItem as LAWImpoundmentSubType?
            impoundmentSubType?.impoundmentTypeID?.let {
                if (it.toString().intOrString() != null)
                    impoundment.impoundmentSubTypeID = it
                else
                    impoundment.impoundmentSubTypeID = 0
            }

            var vehNo: String = mBinding.edtVehicleNo.text.toString()
            var mobile: String = mBinding.edtMobile.text.toString()
            var typeId = 0
            var subTypeId = 0
            if(impoundment.impoundmentTypeID!! > 0)
                typeId = impoundment.impoundmentTypeID!!
            if(impoundment.impoundmentSubTypeID!! > 0)
                subTypeId = impoundment.impoundmentSubTypeID!!


            mListener?.onApplyClick(typeId, subTypeId, vehNo, mobile)
            dismiss()
        }
    }

    private fun String.intOrString(): Any {
        return when (val v = toIntOrNull()) {
            null -> this
            else -> v
        }
    }

    private fun resetValues() {

        mListener?.onClearClick()

        mBinding.edtVehicleNo.setText("")
        mBinding.edtMobile.setText("")

        mBinding.spnImpoundmentType.setSelection(0)
        mBinding.spnImpoundmentSubType.setSelection(0)

        mListener?.onClearClick()
    }

    interface Listener {
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: String?)
        fun showProgressDialog(message: Int)
        fun onApplyClick(impoundType: Int?, impoundSubType: Int?, vehNo: String, mobile: String)
        fun onClearClick()
    }
}
