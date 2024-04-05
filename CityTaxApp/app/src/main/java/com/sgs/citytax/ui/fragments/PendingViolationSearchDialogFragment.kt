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
import com.sgs.citytax.R
import com.sgs.citytax.databinding.FragmentPendingViolationSearchBinding
import com.sgs.citytax.model.LAWViolationType
import com.sgs.citytax.model.TicketHistory
import com.sgs.citytax.util.Constant

class PendingViolationSearchDialogFragment : DialogFragment() {
    private lateinit var mBinding: FragmentPendingViolationSearchBinding
    private var mListener: Listener? = null
    private var mTicketHistory: TicketHistory? = null
    private var mViolationTypes: ArrayList<LAWViolationType> = arrayListOf()
    private var mViolationClasses: ArrayList<LAWViolationType> = arrayListOf()

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

    companion object {
        @JvmStatic
        fun newInstance() = PendingViolationSearchDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending_violation_search, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {
        arguments?.let { it ->
            mViolationTypes.add(LAWViolationType(getString(R.string.select), "", -1, -1, -1, "", "", ""))
            if (it.containsKey(Constant.KEY_VIOLATION_TYPES)) {
                val list = it.getParcelableArrayList<LAWViolationType>(Constant.KEY_VIOLATION_TYPES)
                list?.let {
                    mViolationTypes.addAll(it)
                }
            }
            if (it.containsKey(Constant.KEY_TICKET_HISTORY)) {
                mTicketHistory = it.getParcelable(Constant.KEY_TICKET_HISTORY)
            }
        }
        bindSpinner()
        setEvents()
    }

    private fun setEvents() {
        mBinding.spnViolationType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var violationType = LAWViolationType()

                if (p0 != null && p0.selectedItem != null)
                    violationType = p0.selectedItem as LAWViolationType

                filterViolationClasses(violationType.parentViolationTypeID ?: 0)
            }
        }
        mBinding.btnCancel.setOnClickListener {
            clear()
        }
        mBinding.btnApply.setOnClickListener {
            prepareData()
        }
    }

    private fun prepareData() {
        val ticketHistory = TicketHistory()
        if (!mBinding.edtVehicleNo.text?.toString()?.trim().isNullOrEmpty())
            ticketHistory.vehicleNo = mBinding.edtVehicleNo.text.toString().trim()
        if (!mBinding.edtMobileNo.text?.toString()?.trim().isNullOrEmpty())
            ticketHistory.mobileNo = mBinding.edtMobileNo.text.toString().trim()
        if (mBinding.spnViolationType.selectedItem != null) {
            val violationType = mBinding.spnViolationType.selectedItem as LAWViolationType
            if (violationType.violationTypeID != -1)
                ticketHistory.violationTypeID = violationType.violationTypeID
        }
        if (mBinding.spnViolationSubType.selectedItem != null) {
            val violationSubType = mBinding.spnViolationSubType.selectedItem as LAWViolationType
            if (violationSubType.violationTypeID != -1)
                ticketHistory.violationSubTypeID = violationSubType.violationTypeID
        }
        mListener?.onApply(ticketHistory)
        dismiss()
    }

    private fun clear() {
        mBinding.edtMobileNo.setText("")
        mBinding.edtVehicleNo.setText("")
        mBinding.spnViolationSubType.setSelection(0)
        mBinding.spnViolationType.setSelection(0)
        mListener?.clear()
    }

    private fun filterViolationClasses(parentViolationTypeID: Int?) {
        mBinding.spnViolationSubType.adapter = null
        mViolationClasses = arrayListOf()
        mViolationClasses.add(LAWViolationType(getString(R.string.select), "", -1, -1, -1, "", "", ""))
        parentViolationTypeID?.let { it ->
            if (it == 0)
                mViolationClasses = arrayListOf()
            else {
                for (violationType in mViolationTypes) {
                    if (parentViolationTypeID == violationType.violationTypeID)
                        mViolationClasses.add(violationType)
                }
            }
        }
        mViolationClasses.let {
            val adapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, it)
            mBinding.spnViolationSubType.adapter = adapter

            mTicketHistory?.violationSubTypeID?.let {
                for ((index, violation) in mViolationClasses.withIndex()) {
                    if (violation.violationTypeID == it) {
                        mBinding.spnViolationSubType.setSelection(index)
                        break
                    }
                }
            }
        }
    }

    private fun bindSpinner() {
        if (mViolationTypes.isNotEmpty()) {
            val adapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, mViolationTypes)
            mBinding.spnViolationType.adapter = adapter
        } else
            mBinding.spnViolationType.adapter = null

        bindData()
    }

    private fun bindData() {
        mTicketHistory?.let { it ->
            it.mobileNo?.let {
                if (it.isNotEmpty())
                    mBinding.edtMobileNo.setText(it)
            }
            it.vehicleNo?.let {
                if (it.isNotEmpty())
                    mBinding.edtVehicleNo.setText(it)
            }
            it.violationTypeID?.let {
                for ((index, violation) in mViolationTypes.withIndex()) {
                    if (violation.violationTypeID == it) {
                        mBinding.spnViolationType.setSelection(index)
                        break
                    }
                }
            }
        }
    }

    interface Listener {
        fun finish()
        fun onApply(ticketHistory: TicketHistory)
        fun clear()
    }
}