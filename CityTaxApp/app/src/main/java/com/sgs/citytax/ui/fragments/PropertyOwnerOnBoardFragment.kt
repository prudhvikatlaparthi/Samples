package com.sgs.citytax.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.GetChildTabCount
import com.sgs.citytax.api.payload.SearchFilter
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.PropertyOwnerOnboardBinding
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.model.ComComboStaticValues
import com.sgs.citytax.model.PropertyOwnersData
import com.sgs.citytax.model.VehicleDetails
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.OnSingleClickListener
import java.util.*

class PropertyOwnerOnBoardFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: PropertyOwnerOnboardBinding
    private var mListener: Listener? = null

    private var mPropertyOwnersData: PropertyOwnersData? = null
    private var mCustomer: BusinessOwnership? = null
    private var mNominee: BusinessOwnership? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mSycoTaxID: String? = ""
    private var vehicleDetails: VehicleDetails? = null
    private var mPrimaryKey: String? = ""
    private var vehicleOwnershipID: String = ""
    private var fromDate: String = ""
    private var mComboStaticValues: List<ComComboStaticValues> = arrayListOf()
    private var comboStatVal: ComComboStaticValues? = null


    companion object {
        fun newInstance() = PropertyOwnerOnBoardFragment()
    }

    override fun initComponents() {

        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                mPrimaryKey = it.getString(Constant.KEY_PRIMARY_KEY)
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                mSycoTaxID = it.getString(Constant.KEY_SYCO_TAX_ID)
            if (it.containsKey(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS))
                vehicleDetails = it.getParcelable(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS)

            if (it.containsKey(Constant.KEY_PROPERTY_DETAILS))
                mPropertyOwnersData = it.getParcelable(Constant.KEY_PROPERTY_DETAILS)

            if (it.containsKey(Constant.KEY_PROPERTY_OWNER))
                mCustomer = it.getParcelable(Constant.KEY_PROPERTY_OWNER)

            if (it.containsKey(Constant.KEY_PROPERTY_NOMINEE))
                mNominee = it.getParcelable(Constant.KEY_PROPERTY_NOMINEE)

            if (it.containsKey(Constant.KEY_PROPERTY_RELATION))
                comboStatVal = it.getParcelable(Constant.KEY_PROPERTY_RELATION)

            if (vehicleDetails != null) {
                mCustomer = BusinessOwnership()
                mCustomer?.accountID = vehicleDetails?.accountId
                mCustomer?.accountName = vehicleDetails?.accountName
                vehicleOwnershipID = vehicleDetails?.vehicleOwnerShipID.toString()
                fromDate = vehicleDetails?.fromDate ?: ""
            }else if(mPropertyOwnersData != null){
                mCustomer = mPropertyOwnersData!!.owner
                mNominee = mPropertyOwnersData!!.nominee
                comboStatVal = mPropertyOwnersData!!.relation
            }
        }
        setViews()
        setEvents()
        bindSpinner()
        bindData()
    }

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

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_PropertyOwners", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mComboStaticValues = response.comboStaticValues
                if (mComboStaticValues.isNullOrEmpty())
                    mBinding.spnRelation.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mComboStaticValues)
                    mBinding.spnRelation.adapter = adapter
                    setRelation()
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnRelation.adapter = null
                mListener?.dismissDialog()
            }

        })
    }
    private fun setRelation() {
        for ((index, item) in mComboStaticValues.withIndex()){
            if (comboStatVal?.code == item.code) {
                mBinding.spnRelation.setSelection(index)
                break
            }
        }
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun finish()
        fun showSnackbarMsg(message: String?)
        fun popBackStack()
        var screenMode: Constant.ScreenMode
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.property_owner_onboard, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setViews() {
        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            mBinding.edtCustomerName.isEnabled = false
            mBinding.edtNomineeName.isEnabled = false
            mBinding.tvCreateCustomer.isEnabled = false
            mBinding.tvCreateNominee.isEnabled = false
            mBinding.spnRelation.isEnabled = false
        }
    }

    private fun setEvents() {
        //mBinding.btnSave.setOnClickListener(this)
        mBinding.btnSave.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (validateView()) {
                    saveValuesLocally()
                }
            }
        })

        mBinding.edtCustomerName.setOnClickListener(this)
        mBinding.tvCreateCustomer.setOnClickListener(this)
        mBinding.edtNomineeName.setOnClickListener(this)
        mBinding.tvCreateNominee.setOnClickListener(this)
        mBinding.blankLayout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edtCustomerName, R.id.edtPhoneNumber, R.id.edtEmailId -> {
                showCustomers(false)
            }
            R.id.edtNomineeName -> {
                showCustomers(true)
            }
            R.id.tvCreateCustomer, R.id.tvCreateNominee -> {
                val fragment = BusinessOwnerEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_CREATE_CITIZEN)
                fragment.arguments = bundle
                //endregion
                if (v?.id == R.id.tvCreateCustomer)
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_OWNER)
                else
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_NOMINEE)

                mListener?.showToolbarBackButton(R.string.property_owner)
                mListener?.addFragment(fragment, true)
            }
            R.id.llDocuments -> {
                if (!TextUtils.isEmpty(vehicleOwnershipID)) {
                    val fragment = DocumentsMasterFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_CITIZEN_DOCUMENT)
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, vehicleOwnershipID.toInt())
                    fragment.arguments = bundle
                    //endregion
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)

                    mListener?.showToolbarBackButton(R.string.documents)
                    mListener?.addFragment(fragment, true)

                } /*else if (validateView()) {
                    saveCitizenToVehicle(v)
                }*/
            }
           /* R.id.btnSave -> {
                if (validateView()) {
                    saveValuesLocally()
                }
            }*/
            R.id.blankLayout -> {
            }
        }
    }

    private fun saveValuesLocally() {
        Handler().postDelayed({
            var spinCode = ""
            var branch: ComComboStaticValues? = mBinding.spnRelation.selectedItem as ComComboStaticValues?
            branch?.code?.let {
                // bundle.putInt(KEY_BRANCH_ID, it)
                spinCode = it
            }


            val data = Intent()
            var propOwnerData = PropertyOwnersData()
            propOwnerData.owner = mCustomer
            if (mNominee!=null) {
                propOwnerData.nominee = mNominee
                propOwnerData.relation = branch
            }
            data.putExtra(Constant.KEY_PROPERTY_DETAILS, propOwnerData)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, data)
            mListener?.popBackStack()
        }, 500)
    }

    private fun validateView(): Boolean {
        if (mBinding.edtCustomerName.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.owner_name))
            return false
        }
       /* if (mBinding.edtNomineeName.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.nominee))
            return false
        }*/
        return true
    }


    private fun showCustomers(isNominee: Boolean) {
        val fragment = BusinessOwnerSearchFragment()
        if (isNominee) {
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PROPERTY_NOMINEE)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_NOMINEE_SEARCH)
        } else
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_OWNER_SEARCH)
        mListener?.showToolbarBackButton(R.string.property_owner)
        mListener?.addFragment(fragment, true)
    }

    private fun setCalendarText(month: Int, date: Int): Date {
        val calender = Calendar.getInstance(Locale.getDefault())
        calender.set(calender.get(Calendar.YEAR), month, date)
        return calender.time
    }

    fun bindData() {

        setCustomerInfo()
        if (!TextUtils.isEmpty(vehicleOwnershipID)) {
            fetchChildEntriesCount()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_OWNER_SEARCH) {
            mListener?.showToolbarBackButton(R.string.property_owner)
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                mCustomer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setCustomerInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_NOMINEE_SEARCH) {
            mListener?.showToolbarBackButton(R.string.property_owner)
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                mNominee = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setCustomerInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_OWNER) {
            mListener?.showToolbarBackButton(R.string.property_owner)
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mCustomer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_NOMINEE) {
            mListener?.showToolbarBackButton(R.string.property_owner)
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mNominee = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            }
        } else {
            if (vehicleOwnershipID != null) {
                mListener?.showToolbarBackButton(R.string.property_owner)
                fetchChildEntriesCount()
            }
        }
    }

    private fun setCustomerInfo() {
        mCustomer.let {
            mBinding.edtCustomerName.setText(it?.accountName)
        }
        mNominee.let {
            mBinding.edtNomineeName.setText(it?.accountName)
        }

    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    private fun fetchChildEntriesCount() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "ADM_VehicleOwnerShip"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = vehicleOwnershipID
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        fetchCount(listFilterColumn, "AND", "COM_DocumentReferences", "DocumentReferenceID")
    }

    private fun fetchCount(filterColumns: List<FilterColumn>, tableCondition: String, tableOrViewName: String, primaryKeyColumnName: String) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = tableOrViewName
        tableDetails.primaryKeyColumnName = primaryKeyColumnName
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = tableCondition
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                bindCounts(tableOrViewName, 0)
            }

            override fun onSuccess(response: Int) {
                bindCounts(tableOrViewName, response)
            }
        })
    }

    private fun bindCounts(tableOrViewName: String, count: Int) {
        when (tableOrViewName) {
            "COM_DocumentReferences" -> {
                //  mBinding.txtNumberOfDocuments.text = "$count"
            }
        }
    }

}