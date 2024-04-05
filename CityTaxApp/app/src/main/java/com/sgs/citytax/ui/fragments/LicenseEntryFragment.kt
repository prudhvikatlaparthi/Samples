package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentLicenseEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.LicenseRenewalHistoryActivity
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.fragment_license_entry.*
import java.util.*

class LicenseEntryFragment : BaseFragment() {
    private lateinit var mBinding: FragmentLicenseEntryBinding
    private var mListener: Listener? = null

    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mTaskCode: String? = ""
    private var mLicenseCategories: List<VUCRMCategoryOfLicenses> = arrayListOf()
    private var mStatusCodes: ArrayList<COMStatusCode> = arrayListOf()
    private var licenseDetails: LicenseDetails? = null
    private var mTaxRuleBookCode: String? = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_license_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {

        //region Arguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mTaskCode = it.getString(Constant.KEY_TASK_CODE)
            licenseDetails = it.getParcelable(Constant.KEY_LICENSE)
            mTaxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)
        }
        //endregion

        setViews()
        bindSpinner()
        setListeners()
    }

    private fun setViews() {
        mBinding.edtIssuanceDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtIssuanceDate.setText(getDate(Calendar.getInstance().time, displayDateFormat))
        mBinding.edtIssuanceDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtValidTillDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtValidTillDate.setMinDate(Calendar.getInstance().timeInMillis)

        if (licenseDetails != null) {
            mBinding.llRenewalHistory.visibility = View.VISIBLE
            mBinding.renewalView.visibility = View.VISIBLE
            mBinding.edtLicenseNumber.isEnabled=false
            setEditFalse(false)
        } else {
            mBinding.llRenewalHistory.visibility = View.GONE
            mBinding.renewalView.visibility = View.GONE
            setEditFalse(true)

        }

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
            Constant.ScreenMode.ADD -> if(licenseDetails!=null && licenseDetails?.renewPending=="N")setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {
       /* mBinding.spnLicenseCategory.isEnabled = action
        mBinding.edtLicenseNumber.isEnabled = action
        mBinding.edtIssuanceDate.isEnabled = action
        mBinding.edtCancellationDate.isEnabled = action
        mBinding.edtCancellationRemarks.isEnabled = action*/
        setEditFalse(action)
        mBinding.spnStatus.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }


    }

    private fun setEditActionForSave(action: Boolean) {
        /* mBinding.spnLicenseCategory.isEnabled = action
         mBinding.edtLicenseNumber.isEnabled = action
         mBinding.edtIssuanceDate.isEnabled = action
         mBinding.edtCancellationDate.isEnabled = action
         mBinding.edtCancellationRemarks.isEnabled = action*/
        setEditFalse(action)
        mBinding.spnStatus.isEnabled = true
        mBinding.btnSave.visibility = View.VISIBLE

    }

    private fun setEditFalse(action: Boolean) {
        mBinding.spnLicenseCategory.isEnabled = action
        mBinding.edtLicenseNumber.isEnabled = action
        mBinding.edtIssuanceDate.isEnabled = action
        mBinding.edtValidTillDate.isEnabled=action
        mBinding.edtCancellationDate.isEnabled = action
        mBinding.edtCancellationRemarks.isEnabled = action
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_Licenses", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mLicenseCategories = response.licenseCategories
                mStatusCodes = response.statusCodes as ArrayList<COMStatusCode>

                if (mLicenseCategories.isNotEmpty()) {
                    val licenseAdapter = ArrayAdapter<VUCRMCategoryOfLicenses>(requireContext(), android.R.layout.simple_list_item_1, mLicenseCategories)
                    mBinding.spnLicenseCategory.adapter = licenseAdapter
                }
                if (mStatusCodes.isNotEmpty()) {
                    if (licenseDetails == null) {
                        mStatusCodes.removeIf { comStatusCode ->
                            comStatusCode.statusCode.equals("CRM_Licenses.Cancelled")
                        }
                    }
                    val statusAdapter = ArrayAdapter<COMStatusCode>(requireContext(), android.R.layout.simple_list_item_1, mStatusCodes)
                    mBinding.spnStatus.adapter = statusAdapter
                }

                bindData()

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.btnGet.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.llDocuments.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.llNotes.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.llRenewalHistory.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })

        mBinding.spnLicenseCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val category = mBinding.spnLicenseCategory.selectedItem as VUCRMCategoryOfLicenses
                if (category.authorisedBevarages != null && !category.authorisedBevarages!!.isEmpty()) {
                    mBinding.edtAuthorisedBevarages.setText(category.authorisedBevarages)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val statusCode = mBinding.spnStatus.selectedItem as COMStatusCode
                if (licenseDetails != null && licenseDetails?.currentDue == 0.0 && statusCode.statusCode == "CRM_Licenses.Cancelled") {
                    mBinding.layoutCancellationDate.visibility = View.VISIBLE
                    mBinding.layoutCancellationRemarks.visibility = View.VISIBLE
                } else {
                    mBinding.layoutCancellationDate.visibility = View.GONE
                    mBinding.layoutCancellationRemarks.visibility = View.GONE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.edtIssuanceDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let {
                    if (it.isNotEmpty()) {
                        mBinding.edtValidTillDate.setMinDate(parseDate(it, displayDateFormat).time)
                        mBinding.edtValidTillDate.setText("")
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun bindData() {
        licenseDetails?.let { data ->
            for ((index, obj) in mLicenseCategories.withIndex()) {
                if (data.licenseCategoryId == obj.licenseCategoryID) {
                    mBinding.spnLicenseCategory.setSelection(index)
                    break
                }
            }

            for ((index, obj) in mStatusCodes.withIndex()) {
                if (data.statusCode == obj.statusCode) {
                    mBinding.spnStatus.setSelection(index)
                    break
                }
            }

            data.licenseNo?.let {
                mBinding.edtLicenseNumber.setText(it)
            }
            data.issueanceDate?.let {
                mBinding.edtIssuanceDate.setText(displayFormatDate(it))
            }
            data.cancellationDate?.let {
                mBinding.edtCancellationDate.setText(displayFormatDate(it))
            }
            data.cancellationRemarks?.let {
                mBinding.edtCancellationRemarks.setText(displayFormatDate(it))
            }
            if (!data.validFromDate.isNullOrEmpty() && !data.validupToDate.isNullOrEmpty()) {
                mBinding.layoutValidFromDate.visibility = View.VISIBLE
                mBinding.layoutValidToDate.visibility = View.VISIBLE
                mBinding.layouValidTillDate.visibility = View.GONE
                data.validupToDate?.let {
                    mBinding.edtValidUpto.setText(displayFormatDate(it))
                }
                data.validFromDate?.let {
                    mBinding.edtValidFrom.setText(displayFormatDate(it))
                }
            } else {
                mBinding.layoutValidFromDate.visibility = View.GONE
                mBinding.layoutValidToDate.visibility = View.GONE
                mBinding.layouValidTillDate.visibility = View.VISIBLE
                data.validTillDate?.let {
                    mBinding.edtValidTillDate.setText(displayFormatDate(it))
                }
            }

            data.estimatedTaxAmount?.let {
                mBinding.edtEstimatedAmount.setText(formatWithPrecision(it))
            }
            fetchChildEntriesCount()

            //todo Commented for this release(13/01/2022)
//            getInvoiceCount4Tax()

            if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
            {
                setEditAction(false)
            }
        }
    }

    private fun getInvoiceCount4Tax() {
        val currentDue = CheckCurrentDue()
        currentDue.accountId = licenseDetails?.accountId
        currentDue.vchrno = licenseDetails?.licenseId
        currentDue.taxRuleBookCode = mTaxRuleBookCode
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response > 0) {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }
                    else {
                        setEditActionForSave(false)
                    }
                } else {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }
                    else {
                        setEditAction(true)
                    }
                }
            }

            override fun onFailure(message: String) {
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                {
                    setEditAction(false)
                }
            }
        })
    }

    fun onLayoutClick(view: View?) {
        view?.let {
            when (view.id) {
                R.id.llDocuments -> {
                    when {
                        licenseDetails != null && licenseDetails?.licenseId != 0 -> {
                            val fragment = DocumentsMasterFragment()

                            //region SetArguments
                            val bundle = Bundle()
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_LICENSE_TAX)
                            bundle.putInt(Constant.KEY_PRIMARY_KEY, licenseDetails?.licenseId ?: 0)
                            fragment.arguments = bundle
                            //endregion

                            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                            mListener?.showToolbarBackButton(R.string.documents)
                            mListener?.addFragment(fragment, true)
                        }
                        validateView() -> {
                            storeLicense(view)
                        }
                        else -> {
                        }
                    }
                }

                R.id.llNotes -> {
                    when {
                        licenseDetails != null && licenseDetails?.licenseId != 0 -> {
                            val fragment = NotesMasterFragment()
                            val bundle = Bundle()
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_LICENSE_TAX)
                            bundle.putInt(Constant.KEY_PRIMARY_KEY, licenseDetails?.licenseId
                                    ?: 0)
                            fragment.arguments = bundle
                            fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)

                            mListener?.showToolbarBackButton(R.string.notes)
                            mListener?.addFragment(fragment, true)
                        }
                        validateView() -> {
                            storeLicense(view)
                        }
                        else -> {
                        }
                    }
                }

                R.id.btnGet -> {
                    fetchTaxableMatter()
                }

                R.id.llRenewalHistory -> {
                    when {
                        licenseDetails != null && licenseDetails?.licenseId != 0 -> {
                            val intent = Intent(context, LicenseRenewalHistoryActivity::class.java)
                            intent.putExtra(Constant.KEY_PRIMARY_KEY, licenseDetails?.licenseId
                                    ?: 0)
                            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_LICENSE_TAX)
                            startActivity(intent)
                        }
                        else -> {
                        }
                    }
                }

                R.id.btnSave -> {
                    if (validateView()) {
                        mListener?.showAlertDialog(R.string.are_you_sure_you_have_entered_all_valid_information,
                                R.string.yes,
                                View.OnClickListener {
                                    val dialog = (it as Button).tag as AlertDialog
                                    dialog.dismiss()
                                    storeLicense()
                                },
                                R.string.no,
                                View.OnClickListener
                                {
                                    val dialog = (it as Button).tag as AlertDialog
                                    dialog.dismiss()
                                })
                    } else {
                    }
                }
                else -> {
                }
            }

        }
    }

    private fun fetchTaxableMatter() {
        mListener?.showProgressDialog()
        val licenseEstimatedTax = LicenseEstimatedTax()
        licenseEstimatedTax.accountId = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
        if (mBinding.spnLicenseCategory.selectedItem != null) {
            val licenseCategory = mBinding.spnLicenseCategory.selectedItem as VUCRMCategoryOfLicenses
            licenseEstimatedTax.licenseCategoryId = licenseCategory.licenseCategoryID
        }
        if (mBinding.edtIssuanceDate.text.toString().isNotEmpty())
            licenseEstimatedTax.startDate = serverFormatDate(mBinding.edtIssuanceDate.text.toString())

        licenseEstimatedTax.cityId = ObjectHolder.registerBusiness.geoAddress.cityID

        APICall.getEstimatedTaxForLicense(licenseEstimatedTax, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mBinding.edtEstimatedAmount.setText(formatWithPrecision(response))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun prepareData(): LicensePayloadData {
        val licenseData = LicensePayloadData()
        licenseDetails?.licenseId?.let {
            licenseData.licenseId = it
        }
        licenseDetails?.licenseRequestId?.let {
            licenseData.licenseRequestId = it
        }

        if (mBinding.spnLicenseCategory.selectedItem != null) {
            val licenseCategory = mBinding.spnLicenseCategory.selectedItem as VUCRMCategoryOfLicenses
            licenseData.licenseCategoryId = licenseCategory.licenseCategoryID
        }

        if (mBinding.spnStatus.selectedItem != null) {
            val statusCode = mBinding.spnStatus.selectedItem as COMStatusCode
            licenseData.statusCode = statusCode.statusCode
        }

        if (!mBinding.edtLicenseNumber.text.toString().isEmpty())
            licenseData.licenseNo = mBinding.edtLicenseNumber.text.toString().trim()

        if (!mBinding.edtIssuanceDate.text.toString().isEmpty())
            licenseData.issuanceDate = serverFormatDate(mBinding.edtIssuanceDate.text.toString().trim())

        if (mBinding.edtValidTillDate.isVisible && mBinding.edtValidTillDate.text.toString().isNotEmpty())
            licenseData.validTillDate = serverFormatDate(mBinding.edtValidTillDate.text.toString().trim())

        if (!mBinding.edtAuthorisedBevarages.text.toString().isEmpty())
            licenseData.authorisedBevarages = mBinding.edtAuthorisedBevarages.text.toString().trim()

        if (mBinding.edtCancellationRemarks.isVisible && !mBinding.edtCancellationRemarks.text.toString().isEmpty())
            licenseData.cancellationRemarks = mBinding.edtCancellationRemarks.text.toString().trim()

        if (mBinding.edtCancellationDate.isVisible && !mBinding.edtCancellationDate.text.toString().isEmpty())
            licenseData.cancellationDate = serverFormatDate(mBinding.edtCancellationDate.text.toString().trim())

        licenseData.cancelledByUserId = MyApplication.getPrefHelper().loggedInUserID

        licenseData.organisationId = ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId


        return licenseData
    }

    private fun storeLicense(view: View? = null) {
        mListener?.showProgressDialog()
        APICall.storeLicenses(prepareData(), object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.showProgressDialog()
                if (licenseDetails == null) licenseDetails = LicenseDetails()
                licenseDetails?.licenseId = response

                if (view == null) {
                    mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 780)
                } else
                    onLayoutClick(view)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun validateView(): Boolean {
        val licenseCategory = mBinding.spnLicenseCategory.selectedItem as VUCRMCategoryOfLicenses
        if (licenseCategory.licenseCategory == null && licenseCategory.licenseCategoryID == -1) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.license_category))
            mBinding.spnLicenseCategory.requestFocus()
            return false
        }

        val statusCode = mBinding.spnStatus.selectedItem as COMStatusCode
        if (statusCode.status == null && statusCode.statusCode == "-1") {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.status))
            mBinding.spnStatus.requestFocus()
            return false
        }

        if (mBinding.edtLicenseNumber.text.toString().isEmpty()) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.license_no))
            return false
        }

        if (mBinding.edtIssuanceDate.text.toString().isEmpty()) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.issuance_date))
            return false
        }

        if (mBinding.layoutCancellationDate.isVisible && mBinding.edtCancellationDate.text.toString().isEmpty()) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.cancellation_date))
            return false
        }

        if (mBinding.layoutCancellationRemarks.isVisible && mBinding.edtCancellationRemarks.text.toString().isEmpty()) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.cancellation_remarks))
            return false
        }

        /*   if(mBinding.edtCancellationRemarks.isVisible && mBinding..text.toString().isEmpty()){
               mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.cancellation_remarks))
               return false
           }*/
        return true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fetchChildEntriesCount()
    }

    private fun fetchChildEntriesCount() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "CRM_Licenses"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${licenseDetails?.licenseId}"
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
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "TableName"
                filterColumn.columnValue = "CRM_Licenses"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue = "${licenseDetails?.licenseId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
            }
            "COM_Notes" -> {
                mBinding.txtNoOfNotes.text = "$count"
            }
        }
    }


    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun popBackStack()
        fun showSnackbarMsg(message: String)
        fun showAlertDialog(message: String)
        fun showToolbarBackButton(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener)

    }
}