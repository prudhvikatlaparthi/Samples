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
import com.sgs.citytax.databinding.FragmentPropertySearchInfoBinding
import com.sgs.citytax.model.PropertyDetailLocation
import com.sgs.citytax.util.serverFormatDate

class PropertyDialogFragment : DialogFragment() {
    private lateinit var mBinding: FragmentPropertySearchInfoBinding
    private var mListener: Listener? = null
    private var locations: List<PropertyDetailLocation>? = arrayListOf()
    private var location: PropertyDetailLocation? = null

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
        fun newInstance(locations: List<PropertyDetailLocation>, location: PropertyDetailLocation?) = PropertyDialogFragment().apply {
            this.locations = locations
            this.location = location
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_search_info, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {
        filterData()
        setListeners()
    }

    private fun filterData() {
        val sycoTaxList: ArrayList<String>? = arrayListOf()
        val registrationNoList: ArrayList<String>? = arrayListOf()

        val propertyType: ArrayList<String>? = arrayListOf()
        val zones: ArrayList<String>? = arrayListOf()
        val sectors: ArrayList<String>? = arrayListOf()
        val monthOnBoard: ArrayList<String>? = arrayListOf()
        val yearOnBoard: ArrayList<String>? = arrayListOf()

        locations?.let {
            for ((index, value) in it.withIndex()) {
                sycoTaxList?.let { it1 ->
                    if ((it[index].PropertySycotaxID != null) && !it1.contains(it[index].PropertySycotaxID)) {
                        sycoTaxList.add(it[index].PropertySycotaxID.toString())
                    }
                }

                registrationNoList?.let { it1 ->
                    if ((it[index].RegistrationNo != null) && !it1.contains(it[index].RegistrationNo)) {
                        registrationNoList.add(it[index].RegistrationNo.toString())
                    }
                }

                propertyType?.let { it1 ->
                    if ((it[index].PropertyType != null) && !it1.contains(it[index].PropertyType)) {
                        propertyType.add(it[index].PropertyType.toString())
                    }
                }

                monthOnBoard?.let { it1 ->
                    if ((it[index].OnboardingMonth != null) && !it1.contains(it[index].OnboardingMonth.toString())) {
                        monthOnBoard.add(it[index].OnboardingMonth.toString())
                    }
                }

                yearOnBoard?.let { it1 ->
                    if ((it[index].OnboardingYear != null) && !it1.contains(it[index].OnboardingYear.toString())) {
                        yearOnBoard.add(it[index].OnboardingYear.toString())
                    }
                }

                zones?.let { it1 ->
                    if ((it[index].Zone != null) && !it1.contains(it[index].Zone)) {
                        zones.add(it[index].Zone.toString())
                    }
                }

                sectors?.let { it1 ->
                    if ((it[index].Sector != null) && !it1.contains(it[index].Sector)) {
                        sectors.add(it[index].Sector.toString())
                    }
                }
            }
        }

        sycoTaxList?.let {
            val incidentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sycoTaxList)
            mBinding.edtSycoTaxID.setAdapter(incidentAdapter)
            mBinding.edtSycoTaxID.threshold = 2
        }

        registrationNoList?.let {
            val incidentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, registrationNoList)
            mBinding.edtRegistrationNo.setAdapter(incidentAdapter)
            mBinding.edtRegistrationNo.threshold = 2
        }

        propertyType?.let {
            it.add(0, getString(R.string.select))
            val incidentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, propertyType)
            mBinding.spnPropertyType.adapter = incidentAdapter
        }

        monthOnBoard?.let {
            it.add(0, getString(R.string.select))
            val incidentSubTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, monthOnBoard)
            mBinding.spnMonthOnBoard.adapter = incidentSubTypeAdapter
        }

        yearOnBoard?.let {
            it.add(0, getString(R.string.select))
            val incidentSubTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, yearOnBoard)
            mBinding.spnYearOnBoard.adapter = incidentSubTypeAdapter
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

        if (location != null) {

            location?.PropertySycotaxID?.let {
                mBinding.edtSycoTaxID.setText(it)
            }
            location?.RegistrationNo?.let {
                mBinding.edtRegistrationNo.setText(it)
            }

            propertyType?.let {
                for ((index, obj) in it.withIndex()) {
                    if (location?.PropertyType?.contentEquals(obj) == true) {
                        mBinding.spnPropertyType.setSelection(index)
                        break
                    }
                }
            }

            monthOnBoard?.let {
                for ((index, obj) in it.withIndex()) {
                    if ((location?.OnboardingMonth.toString()).contentEquals(obj) == true) {
                        mBinding.spnMonthOnBoard.setSelection(index)
                        break
                    }
                }
            }

            yearOnBoard?.let {
                for ((index, obj) in it.withIndex()) {
                    if ((location?.OnboardingYear.toString()).contentEquals(obj) == true) {
                        mBinding.spnYearOnBoard.setSelection(index)
                        break
                    }
                }
            }

            zones?.let {
                for ((index, obj) in it.withIndex()) {
                    if (location?.Zone?.contentEquals(obj) == true) {
                        mBinding.spnZone.setSelection(index)
                        break
                    }
                }
            }

            sectors?.let {
                for ((index, obj) in it.withIndex()) {
                    if (location?.Sector?.contentEquals(obj) == true) {
                        mBinding.spnSector.setSelection(index)
                        break
                    }
                }
            }
        }
    }

    private fun setListeners() {

        mBinding.btnCancel.setOnClickListener {
            resetValues()
        }

        mBinding.btnApply.setOnClickListener {
            location = PropertyDetailLocation()

            if (mBinding.edtSycoTaxID.text.toString().trim().isNotEmpty())
                location?.PropertySycotaxID = mBinding.edtSycoTaxID.text.toString()

            if (mBinding.edtRegistrationNo.text.toString().trim().isNotEmpty())
                location?.RegistrationNo = mBinding.edtRegistrationNo.text.toString()

            if (mBinding.spnPropertyType.selectedItem != null)
                location?.PropertyType = mBinding.spnPropertyType.selectedItem.toString()

            if (mBinding.spnMonthOnBoard.selectedItem != null) {
                if (mBinding.spnMonthOnBoard.selectedItem.toString() == requireContext().getString(R.string.select)) {
                    location?.OnboardingMonth = -1
                } else {
                    location?.OnboardingMonth = (mBinding.spnMonthOnBoard.selectedItem.toString()).toInt()
                }
            }

            if (mBinding.spnZone.selectedItem != null)
                location?.Zone = mBinding.spnZone.selectedItem.toString()

            if (mBinding.spnSector.selectedItem != null)
                location?.Sector = mBinding.spnSector.selectedItem.toString()

            if (mBinding.spnYearOnBoard.selectedItem != null) {
                if (mBinding.spnYearOnBoard.selectedItem.toString() == requireContext().getString(R.string.select)) {
                    location?.OnboardingYear = -1
                } else {
                    location?.OnboardingYear = (mBinding.spnYearOnBoard.selectedItem.toString()).toInt()
                }
            }

            mListener?.onApplyPropertyClick(location)
            dismiss()
        }
    }

    private fun resetValues() {
        location = null
        mListener?.onClearPropertyClick()

        mBinding.edtSycoTaxID.setText("")
        mBinding.edtRegistrationNo.setText("")

        mBinding.spnPropertyType.setSelection(0)
        mBinding.spnMonthOnBoard.setSelection(0)
        mBinding.spnZone.setSelection(0)
        mBinding.spnSector.setSelection(0)
        mBinding.spnYearOnBoard.setSelection(0)
    }

    interface Listener {
        fun finish()
        fun showToast(message: String)
        fun showSnackbarMsg(message: String)
        fun onApplyPropertyClick(location: PropertyDetailLocation?)
        fun onClearPropertyClick()
    }
}
