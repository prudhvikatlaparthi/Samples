package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ComplaintIncidentDetailLocation
import com.sgs.citytax.databinding.FragmentComplaintsSearchInfoBinding
import com.sgs.citytax.util.*

class ComplaintsDialogFragment : DialogFragment() {
    private lateinit var mBinding: FragmentComplaintsSearchInfoBinding
    private var mListener: Listener? = null
    private var locations: List<ComplaintIncidentDetailLocation>? = arrayListOf()
    private var location: ComplaintIncidentDetailLocation? = null

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
        fun newInstance(locations: List<ComplaintIncidentDetailLocation>, location: ComplaintIncidentDetailLocation?) = ComplaintsDialogFragment().apply {
            this.locations = locations
            this.location = location
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_complaints_search_info, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {
        filterData()
        setListeners()
    }

    private fun filterData() {
        val complaintTypes: ArrayList<String>? = arrayListOf()
        val complaintSubTypes: ArrayList<String>? = arrayListOf()
        val zones: ArrayList<String>? = arrayListOf()
        val sectors: ArrayList<String>? = arrayListOf()
        val status: ArrayList<String>? = arrayListOf()

        locations?.let {
            for ((index, value) in it.withIndex()) {
                complaintTypes?.let { it1 ->
                    if ((it[index].complaint != null) && !it1.contains(it[index].complaint)) {
                        complaintTypes.add(it[index].complaint.toString())
                    }
                }

                complaintSubTypes?.let { it1 ->
                    if ((it[index].complaintSubtype != null) && !it1.contains(it[index].complaintSubtype)) {
                        complaintSubTypes.add(it[index].complaintSubtype.toString())
                    }
                }

                status?.let { it1 ->
                    if ((it[index].status != null) && !it1.contains(it[index].status)) {
                        status.add(it[index].status.toString())
                    }
                }

                zones?.let { it1 ->
                    if ((it[index].zone != null) && !it1.contains(it[index].zone)) {
                        zones.add(it[index].zone.toString())
                    }
                }

                sectors?.let { it1 ->
                    if ((it[index].sector != null) && !it1.contains(it[index].sector)) {
                        sectors.add(it[index].sector.toString())
                    }
                }
            }
        }

        complaintTypes?.let {
            it.add(0, getString(R.string.select))
            val incidentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, complaintTypes)
            mBinding.spnComplaintCategory.adapter = incidentAdapter
        }

        complaintSubTypes?.let {
            it.add(0, getString(R.string.select))
            val incidentSubTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, complaintSubTypes)
            mBinding.spnComplaintSubCategory.adapter = incidentSubTypeAdapter
        }

        sectors?.let {
            sectors.add(0, getString(R.string.select))
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnSector.adapter = domainAdapter
        }

        zones?.let {
            zones.add(0, getString(R.string.select))
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnZone.adapter = domainAdapter
        }

        status?.let {
            status.add(0, getString(R.string.select))
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, status)
            mBinding.spnStatus.adapter = domainAdapter
        }


        if (location != null) {

            location?.fromDate?.let {
                mBinding.edtFromDate.setText(displayFormatDate(getDate(it, DateFormat, DateTimeTimeZoneMillisecondFormat)))
            }

            location?.toDate?.let {
                mBinding.edtToDate.isEnabled = true
                mBinding.edtToDate.setText(displayFormatDate(getDate(it, DateFormat, DateTimeTimeZoneMillisecondFormat)))
            }

            complaintTypes?.let {
                for ((index, obj) in it.withIndex()) {
                    if (location?.complaint?.contentEquals(obj) == true) {
                        mBinding.spnComplaintCategory.setSelection(index)
                        break
                    }
                }
            }

            complaintSubTypes?.let {
                for ((index, obj) in it.withIndex()) {
                    if (location?.complaintSubtype?.contentEquals(obj) == true) {
                        mBinding.spnComplaintSubCategory.setSelection(index)
                        break
                    }
                }
            }

            zones?.let {
                for ((index, obj) in it.withIndex()) {
                    if (location?.zone?.contentEquals(obj) == true) {
                        mBinding.spnZone.setSelection(index)
                        break
                    }
                }
            }

            sectors?.let {
                for ((index, obj) in it.withIndex()) {
                    if (location?.sector?.contentEquals(obj) == true) {
                        mBinding.spnSector.setSelection(index)
                        break
                    }
                }
            }

            status?.let {
                for ((index, obj) in it.withIndex()) {
                    if (location?.status?.contentEquals(obj) == true) {
                        mBinding.spnStatus.setSelection(index)
                        break
                    }
                }
            }
        }
    }

    private fun setListeners() {
        mBinding.edtFromDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtToDate.setDisplayDateFormat(displayDateFormat)

        mBinding.edtFromDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.edtFromDate.text?.toString()?.let {
                    if (it.isNotEmpty()) {
                        mBinding.edtToDate.isEnabled = true
                        mBinding.edtToDate.setText("")
                        mBinding.edtToDate.setMinDate(parseDate(it, displayDateFormat).time)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        mBinding.btnCancel.setOnClickListener {
            resetValues()
        }

        mBinding.btnApply.setOnClickListener {
            location = ComplaintIncidentDetailLocation()

            if (mBinding.edtFromDate.text.toString().trim().isNotEmpty() && mBinding.edtToDate.text.toString().trim().isEmpty()) {
                mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.to_date))
                return@setOnClickListener
            }

            if (mBinding.edtFromDate.text.toString().trim().isNotEmpty())
                location?.fromDate = serverFormatDate(mBinding.edtFromDate.text.toString())

            if (mBinding.edtToDate.text.toString().trim().isNotEmpty())
                location?.toDate = serverFormatDate(mBinding.edtToDate.text.toString())

            if (mBinding.spnComplaintCategory.selectedItem != null)
                location?.complaint = mBinding.spnComplaintCategory.selectedItem.toString()

            if (mBinding.spnComplaintSubCategory.selectedItem != null)
                location?.complaintSubtype = mBinding.spnComplaintSubCategory.selectedItem.toString()

            if (mBinding.spnZone.selectedItem != null)
                location?.zone = mBinding.spnZone.selectedItem.toString()

            if (mBinding.spnSector.selectedItem != null)
                location?.sector = mBinding.spnSector.selectedItem.toString()

            if (mBinding.spnStatus.selectedItem != null)
                location?.status = mBinding.spnStatus.selectedItem.toString()

            mListener?.onApplyComplaintClick(location)
            dismiss()
        }
    }

    private fun resetValues() {
        location = null
        mListener?.onClearComplaintClick()

        mBinding.edtFromDate.setText("")
        mBinding.edtToDate.setText("")

        mBinding.spnComplaintCategory.setSelection(0)
        mBinding.spnComplaintSubCategory.setSelection(0)
        mBinding.spnZone.setSelection(0)
        mBinding.spnSector.setSelection(0)
        mBinding.spnStatus.setSelection(0)
    }

    interface Listener {
        fun finish()
        fun showToast(message: String)
        fun showSnackbarMsg(message: String)
        fun onApplyComplaintClick(location: ComplaintIncidentDetailLocation?)
        fun onClearComplaintClick()
    }
}
