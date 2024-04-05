package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.SearchTaxPayerFltrData
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.*
import com.sgs.citytax.databinding.FragmentBusinessSearchInfoBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.custom.customDropDown.CustomDropdownMenu
import com.sgs.citytax.ui.custom.customDropDown.CustomTextInputEditText
import java.util.*
import kotlin.collections.ArrayList

class BusinessSearchDialogFragment : DialogFragment() {
    private lateinit var mBinding: FragmentBusinessSearchInfoBinding
    private var mListener: Listener? = null
    private var filtersData: GetDropdownFiltersForBusinessSearchResponse? = null
    private var location: BusinessLocations? = null
    private var mProducts: ArrayList<VuInvProducts>? = arrayListOf()
    private var mTaxSubType: ArrayList<TaxSubType>? = arrayListOf()
    private var mYears = arrayListOf<String>()
    lateinit var sycCustomDropdownMenu: CustomDropdownMenu<SearchForTaxPayerForMapItem>
    lateinit var businessCustomDropdownMenu: CustomDropdownMenu<SearchForTaxPayerForMapItem>
    var monthlist=arrayListOf<String>()


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
        fun newInstance(locations: GetDropdownFiltersForBusinessSearchResponse, location: BusinessLocations?) = BusinessSearchDialogFragment().apply {
           this.filtersData=locations
            this.location = location
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_search_info, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {
        filterData()
        setView()
        setListeners()
    }

    private fun setView() {

        sycCustomDropdownMenu = CustomDropdownMenu(context, object : CustomDropdownMenu.DropDownClickListener {
            override fun onItemClick(position: Int, item: Any?) {
                mBinding.edtSycoTaxID.setText(item.toString())
                sycCustomDropdownMenu.clearList()
                sycCustomDropdownMenu.dismiss()
            }
        })
        mBinding.edtSycoTaxID.setCustomDropdown(sycCustomDropdownMenu)
        mBinding.edtSycoTaxID.setOnCustomListener(object: CustomTextInputEditText.OnCustomListener{
            override fun onPaginationScroll(pageIndex: Int, text: String) {
                getSearchFilterData(sId = text, index = pageIndex)
            }
        })

        businessCustomDropdownMenu = CustomDropdownMenu(context, object : CustomDropdownMenu.DropDownClickListener {
            override fun onItemClick(position: Int, item: Any?) {
                mBinding.edtBusinessName.setText(item.toString())
                businessCustomDropdownMenu.clearList()
                businessCustomDropdownMenu.dismiss()
            }
        })
        mBinding.edtBusinessName.setCustomDropdown(businessCustomDropdownMenu)
        mBinding.edtBusinessName.setOnCustomListener(object: CustomTextInputEditText.OnCustomListener{
            override fun onPaginationScroll(pageIndex: Int, text: String) {
                getSearchFilterData(bName = text, index = pageIndex)
            }
        })
    }

    private fun getSearchFilterData(sId: String? = null, bName: String? = null, index: Int) {
        val filter = SearchTaxPayerFltrData(SycoTaxID = sId, BusinessName = bName)
        APICall.getSearchMapFilterData(filter, index, object : ConnectionCallBack<SearchForTaxPayerForMapResponse> {
            override fun onSuccess(response: SearchForTaxPayerForMapResponse) {
                if (sId != null) {
                    if (index == 1) {
                        mBinding.edtSycoTaxID.pagination.totalRecords = response.TotalRecordsFound!!
                        mBinding.edtSycoTaxID.customDropdownMenu.clearList()
                    }
                    if (response.SearchResults.isNotEmpty()) {
                        mBinding.edtSycoTaxID.setDataList(response.SearchResults as ArrayList<SearchForTaxPayerForMapItem>)
                        sycCustomDropdownMenu.updateList(response.SearchResults as ArrayList<SearchForTaxPayerForMapItem>)
                        mBinding.edtSycoTaxID.showPopup()
                    }
                } else if (bName != null) {
                    if (index == 1) {
                        mBinding.edtBusinessName.pagination.totalRecords = response.TotalRecordsFound!!
                        mBinding.edtBusinessName.customDropdownMenu.clearList()
                    }
                    if (response.SearchResults.isNotEmpty()) {
                        mBinding.edtBusinessName.setDataList(response.SearchResults as ArrayList<SearchForTaxPayerForMapItem>)
                        businessCustomDropdownMenu.updateList(response.SearchResults as ArrayList<SearchForTaxPayerForMapItem>)
                        mBinding.edtBusinessName.showPopup()
                    }
                }
            }

            override fun onFailure(message: String) {
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun filterData() {
        val sycoTaxIDs: ArrayList<String>? = arrayListOf()
        val businessNames: ArrayList<String>? = arrayListOf()
        val emails: ArrayList<String>? = arrayListOf()
        val phones: ArrayList<String>? = arrayListOf()

        val yearOnBoard: ArrayList<String>? = arrayListOf()
        val monthOnBoard: ArrayList<String>? = arrayListOf()
        val zones: ArrayList<COMZoneMasterS>? = arrayListOf()
        val sectors: ArrayList<COMSector>? = arrayListOf()
        val activityDomains: ArrayList<CRMActivityDomainS>? = arrayListOf()
        val activityClasses: ArrayList<CRMActivityClassS>? = arrayListOf()
        val taxType: ArrayList<VUINVProducts>? = arrayListOf()
        val taxSubtype: ArrayList<VUCRMTaxSubType>? = arrayListOf()

        filtersData?.let { data ->

            data.cOMZoneMaster?.let {
                zones?.addAll(it)
            }

            data.cOMSectors?.let {
                sectors?.addAll(it)
            }

            data.cRMActivityDomains?.let {
                activityDomains?.addAll(it)
            }

            data.cRMActivityClasses?.let {
                activityClasses?.addAll(it)
            }

            data.vUINVProducts?.let {
                taxType?.addAll(it)
            }

            data.vUCRMTaxSubTypes?.let {
                taxSubtype?.addAll(it)
            }


        }

        yearOnBoard?.let {
            yearOnBoard.add(0, getString(R.string.select))
            yearOnBoard.addAll(getYears())
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, yearOnBoard)
            mBinding.spnYearOnBoard.adapter = domainAdapter
        }




        monthOnBoard?.let {
            monthOnBoard.add(0, getString(R.string.select))
            monthOnBoard.addAll(getMonth())
            val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, monthOnBoard)
            mBinding.spnMonthOnBoard.adapter = monthAdapter
        }

      /*  sycoTaxIDs?.let {
            val emailAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sycoTaxIDs)
            mBinding.edtSycoTaxID.setAdapter(emailAdapter)
            mBinding.edtSycoTaxID.threshold = 2
        }

        businessNames?.let {
            val emailAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, businessNames)
            mBinding.edtBusinessName.setAdapter(emailAdapter)
            mBinding.edtBusinessName.threshold = 2
        }

        emails?.let {
            val emailAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, emails)
            mBinding.edtEmail.setAdapter(emailAdapter)
            mBinding.edtEmail.threshold = 2
        }

        phones?.let {
            val phoneAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, phones)
            mBinding.edtMobile.setAdapter(phoneAdapter)
            mBinding.edtMobile.threshold = 2
        }

        yearOnBoard?.let {
            yearOnBoard.add(0, getString(R.string.select))
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, yearOnBoard)
            mBinding.spnYearOnBoard.adapter = domainAdapter
        }

        monthOnBoard?.let {
            monthOnBoard.add(0, getString(R.string.select))
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, monthOnBoard)
            mBinding.spnMonthOnBoard.adapter = domainAdapter
        }*/



        activityDomains?.let {
           var cRMActivityDomains =CRMActivityDomainS()
            cRMActivityDomains.activityDomain=getString(R.string.select)
            activityDomains.add(0, cRMActivityDomains)
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activityDomains)
            mBinding.spnActivityDomain.adapter = domainAdapter
        }

        sectors?.let {
            var crmSector = COMSector()
            crmSector.sector=getString(R.string.select)
            sectors.add(0, crmSector)
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnSector.adapter = domainAdapter
        }

        zones?.let {
            var crmZone = COMZoneMasterS()
            crmZone.zone=getString(R.string.select)
            zones.add(0, crmZone)
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnZone.adapter = domainAdapter
        }

        activityClasses?.let {
            var crmActivity = CRMActivityClassS()
            crmActivity.activityClass=getString(R.string.select)
            activityClasses.add(0, crmActivity)
            val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, activityClasses)
            mBinding.spnActivityClass.adapter = domainAdapter
        }
        taxType?.let {
            var mtaxType=VUINVProducts()
            mtaxType.TaxType=getString(R.string.select)
            taxType.add(0,mtaxType)
            val taxTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, taxType)
            mBinding.spnTaxType.adapter = taxTypeAdapter
        }

        taxSubtype?.let {
            var mtaxSubType=VUCRMTaxSubType()
            mtaxSubType.TaxSubType=getString(R.string.select)
            taxSubtype.add(0,mtaxSubType)
            val taxSubTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, taxSubtype)
            mBinding.spnTaxSubType.adapter = taxSubTypeAdapter
        }

        if (location != null) {
            location?.sycotaxID?.let {
                mBinding.edtSycoTaxID.setText(it)
            }

            location?.business?.let {
                mBinding.edtBusinessName.setText(it)
            }

            location?.email?.let {
                mBinding.edtEmail.setText(it)
            }

            location?.phone?.let {
                mBinding.edtMobile.setText(it)
            }

            zones?.let {
                for ((index, obj) in zones.withIndex()) {
                    if (location?.zone?.contentEquals(obj.toString()) == true) {
                        mBinding.spnZone.setSelection(index)
                        break
                    }
                }
            }

            sectors?.let {
                for ((index, obj) in sectors.withIndex()) {
                    if (location?.sector?.contentEquals(obj.toString()) == true) {
                        mBinding.spnSector.setSelection(index)
                        break
                    }
                }
            }

            activityDomains?.let {
                for ((index, obj) in activityDomains.withIndex()) {
                    if (location?.activityDomain?.contentEquals(obj.toString()) == true) {
                        mBinding.spnActivityDomain.setSelection(index)
                        break
                    }
                }
            }

            activityClasses?.let {
                for ((index, obj) in activityClasses.withIndex()) {
                    if (location?.activityClass?.contentEquals(obj.toString()) == true) {
                        mBinding.spnActivityClass.setSelection(index)
                        break
                    }
                }
            }

            yearOnBoard?.let {
                for ((index, obj) in yearOnBoard.withIndex()) {
                    if (location?.onboardingYear?.contentEquals(obj) == true) {
                        mBinding.spnYearOnBoard.setSelection(index)
                        break
                    }
                }
            }

            monthOnBoard?.let {
                for ((index, obj) in monthOnBoard.withIndex()) {
                    if (location?.onboardingMonth?.contentEquals(obj) == true) {
                        mBinding.spnMonthOnBoard.setSelection(index)
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
            location = BusinessLocations()
            if (mBinding.edtSycoTaxID.text.toString().trim().isNotEmpty())
                location?.sycotaxID = mBinding.edtSycoTaxID.text.toString()

            if (mBinding.edtBusinessName.text.toString().trim().isNotEmpty())
                location?.business = mBinding.edtBusinessName.text.toString()

            if (mBinding.edtEmail.text.toString().trim().isNotEmpty())
                location?.email = mBinding.edtEmail.text.toString()

            if (mBinding.edtMobile.text.toString().trim().isNotEmpty())
                location?.phone = mBinding.edtMobile.text.toString()

            if (mBinding.spnZone.selectedItem != null && mBinding.spnZone.selectedItemPosition!=0)
                location?.zone = mBinding.spnZone.selectedItem.toString()

            if (mBinding.spnSector.selectedItem != null && mBinding.spnSector.selectedItemPosition!=0)
                location?.sector = mBinding.spnSector.selectedItem.toString()

            if (mBinding.spnYearOnBoard.selectedItem != null && mBinding.spnYearOnBoard.selectedItemPosition!=0)
                location?.onboardingYear = mBinding.spnYearOnBoard.selectedItem.toString()

            if (mBinding.spnMonthOnBoard.selectedItem != null && mBinding.spnMonthOnBoard.selectedItemPosition!=0)
                location?.onboardingMonth = mBinding.spnMonthOnBoard.selectedItem.toString()

            if (mBinding.spnActivityClass.selectedItem != null && mBinding.spnActivityClass.selectedItemPosition!=0)
                location?.activityClass = mBinding.spnActivityClass.selectedItem.toString()

            if (mBinding.spnActivityDomain.selectedItem != null && mBinding.spnActivityDomain.selectedItemPosition!=0)
                location?.activityDomain = mBinding.spnActivityDomain.selectedItem.toString()

            if (mBinding.spnTaxSubType.selectedItem != null && mBinding.spnTaxSubType.selectedItemPosition!=0)
                location?.taxSubType = mBinding.spnTaxSubType.selectedItem.toString()

            if (mBinding.spnTaxType.selectedItem != null && mBinding.spnTaxType.selectedItemPosition!=0) {
                val selectedCode = (mBinding.spnTaxType.selectedItem as VUINVProducts).TaxTypeCode
                location?.taxType = selectedCode.toString() //mBinding.spnTaxType.selectedItem.toString()
            }

            mListener?.onApplyClick(location)
            dismiss()
        }
    }

    private fun resetValues() {
        location = null
        mListener?.onClearClick()

        mBinding.edtSycoTaxID.setText("")
        mBinding.edtBusinessName.setText("")
        mBinding.edtMobile.setText("")
        mBinding.edtEmail.setText("")

        mBinding.spnYearOnBoard.setSelection(0)
        mBinding.spnMonthOnBoard.setSelection(0)
        mBinding.spnZone.setSelection(0)
        mBinding.spnSector.setSelection(0)
        mBinding.spnTaxSubType.setSelection(0)
        mBinding.spnTaxType.setSelection(0)
        mBinding.spnActivityClass.setSelection(0)
        mBinding.spnActivityDomain.setSelection(0)
    }

    interface Listener {
        fun finish()
        fun onApplyClick(location: BusinessLocations?)
        fun onClearClick()
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialogFailure(message: String, noRecordsFound: Int, onClickListener: DialogInterface.OnClickListener)
    }

    private fun getYears():ArrayList<String> {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val year = calendar[Calendar.YEAR]
        for (i in (year-5)..(year+5)) {
            mYears.add(i.toString())
        }
        return mYears

    }

    private fun getMonth():ArrayList<String>{
        for(i in 1..12){
            monthlist.add(i.toString())
        }
        return monthlist
    }
}