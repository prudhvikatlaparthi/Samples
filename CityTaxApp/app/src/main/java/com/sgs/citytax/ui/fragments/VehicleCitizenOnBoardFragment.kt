package com.sgs.citytax.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.databinding.ActivityVehicleCitizenOnboardBinding
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.model.VehicleDetails
import com.sgs.citytax.model.VehicleOwnership
import com.sgs.citytax.ui.fragments.BaseFragment
import com.sgs.citytax.ui.fragments.BusinessOwnerEntryFragment
import com.sgs.citytax.ui.fragments.BusinessOwnerSearchFragment
import com.sgs.citytax.ui.fragments.DocumentsMasterFragment
import com.sgs.citytax.util.*
import java.util.*

class VehicleCitizenOnBoardFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: ActivityVehicleCitizenOnboardBinding
    private var mListener: FragmentCommunicator? = null

    private var mCustomer: BusinessOwnership? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mSycoTaxID: String? = ""
    private var vehicleDetails: VehicleDetails? = null
    private var mPrimaryKey: String? = ""
    private var vehicleOwnershipID: String = ""
    private var fromDate: String = ""


    companion object {
        fun newInstance() = VehicleCitizenOnBoardFragment()
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

            if (vehicleDetails != null) {
                mCustomer = BusinessOwnership()
                mCustomer?.accountID = vehicleDetails?.accountId
                mCustomer?.accountName = vehicleDetails?.accountName
                vehicleOwnershipID = vehicleDetails?.vehicleOwnerShipID.toString()
                fromDate = vehicleDetails?.fromDate ?: ""
            }
        }
        setEvents()
        bindData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as FragmentCommunicator
            else context as FragmentCommunicator
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_vehicle_citizen_onboard, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setEvents() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.edtCustomerName.setOnClickListener(this)
        mBinding.tvCreateCustomer.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edtCustomerName, R.id.edtPhoneNumber, R.id.edtEmailId -> {
                showCustomers()
            }
            R.id.tvCreateCustomer -> {
                val fragment = BusinessOwnerEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_CREATE_CITIZEN)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)

                mListener?.showToolbarBackButton(R.string.title_vehicle_ownership)
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

                } else if (validateView()){
                    saveCitizenToVehicle(v)
                }
            }
            R.id.btnSave -> {
                if (validateView()) {
                    saveCitizenToVehicle(v)
                }
            }
        }
    }

    private fun validateView(): Boolean {
        if (mBinding.edtCustomerName.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.owner_name))
            return false
        }
        if (mBinding.edtFromDate.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.from_date))
            return false
        }
        return true
    }

    private fun saveCitizenToVehicle(v: View) {
        mListener?.showProgressDialog()
        val vehicleOwnership = VehicleOwnership()
        if (TextUtils.isEmpty(vehicleOwnershipID)) {
            vehicleOwnership.vehicleOwnershipID = "0"
        } else {
            vehicleOwnership.vehicleOwnershipID = vehicleOwnershipID
        }
        vehicleOwnership.accountID = mCustomer!!.accountID.toString()
        vehicleOwnership.fromDate = serverFormatDate(mBinding.edtFromDate.text.toString())
        vehicleOwnership.vehicleNo = mPrimaryKey
        vehicleOwnership.toDate = ""
        val insertVehicleOwnership = GetInsertVehicleOwnership(SecurityContext(), vehicleOwnership)
        APICall.onBoardVehicleOwenership(insertVehicleOwnership, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mListener?.dismissDialog()
                vehicleOwnershipID = (response.toInt()).toString()
                if(v.id ==  R.id.llDocuments){
                    onClick(v)
                }else{
                    Handler().postDelayed({
                        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener?.popBackStack()
                    }, 500)
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun showCustomers() {
        val fragment = BusinessOwnerSearchFragment()
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
        mListener?.showToolbarBackButton(R.string.citizen)
        mListener?.addFragment(fragment, true)
    }

    private fun setCalendarText(month: Int, date: Int): Date {
        val calender = Calendar.getInstance(Locale.getDefault())
        calender.set(calender.get(Calendar.YEAR), month, date)
        return calender.time
    }

    fun bindData() {
        mBinding.edtFromDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtFromDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtFromDate.setText(getDate(setCalendarText(Calendar.JANUARY, 1), displayDateFormat))

        setCustomerInfo()
        if (!TextUtils.isEmpty(vehicleOwnershipID)) {
            fetchChildEntriesCount()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
            mListener?.showToolbarBackButton(R.string.title_vehicle_ownership)
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                mCustomer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setCustomerInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER) {
            mListener?.showToolbarBackButton(R.string.title_vehicle_ownership)
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mCustomer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            }
        } else {
            if (vehicleOwnershipID != null) {
                mListener?.showToolbarBackButton(R.string.citizen)
                fetchChildEntriesCount()
            }
        }
    }

    private fun setCustomerInfo() {
        mCustomer.let {
            mBinding.edtCustomerName.setText(it?.accountName)
        }
        if (!TextUtils.isEmpty(fromDate)) {
            mBinding.edtFromDate.setText(displayFormatDate(fromDate))
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
                mBinding.txtNumberOfDocuments.text = "$count"
            }
        }
    }

}