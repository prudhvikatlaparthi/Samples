package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentRopPdoEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.*
import java.util.*

class ROPPDOEntryFragment : BaseFragment(), TextWatcher {

    private var mItem: ROPListItem? = null
    private lateinit var mBinding: FragmentRopPdoEntryBinding
    private var mListener: Listener? = null
    private var mCode: Constant.QuickMenu? = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mPublicDomainOccupancy: Boolean? = false
    private var occupancyTypes: MutableList<VUCRMNatureOfOccupancy>? = null
    private var mMarkets: MutableList<VUCRMMarkets>? = null
    private var isOccupancyChanged: Boolean = false
    private var mTaskCode: String? = ""
    private var mTaxRuleBookCode: String? = ""

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPublicDomainOccupancy = arguments?.getBoolean(Constant.KEY_IS_PUBLIC_DOMAIN_OCCUPANCY)
            mItem = arguments?.getParcelable(Constant.KEY_PDO_ROP)
            mTaskCode = it.getString(Constant.KEY_TASK_CODE)
            mTaxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)
        }
        //endregion

        bindSpinner()
        setListeners()
        setViews()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rop_pdo_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {
        mBinding.edtStartDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtStartDate.setDisplayDateFormat(displayDateFormat)
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
            Constant.ScreenMode.ADD -> if(mItem!=null && mItem?.allowDelete=="N")setEditAction(false)
        }
        if (mPublicDomainOccupancy == false)
        {
            mBinding.llMarket.visibility = View.VISIBLE
        }
    }

    private fun setEditAction(action: Boolean) {
        mBinding.edtLength.isEnabled = action
        mBinding.edtWidth.isEnabled = action
        mBinding.edtTaxableMatter.isEnabled = action
        mBinding.edtTaxPeriod.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.edtStartDate.isEnabled = action
        mBinding.spnOccupancy.isEnabled = action
        mBinding.spnMarkets.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.cbActive.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }

    private fun setEditActionForSave(action: Boolean) {
        mBinding.edtLength.isEnabled = action
        mBinding.edtWidth.isEnabled = action
        mBinding.edtTaxableMatter.isEnabled = action
        mBinding.edtTaxPeriod.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.edtStartDate.isEnabled = action
        mBinding.spnOccupancy.isEnabled = action
        mBinding.spnMarkets.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.cbActive.isEnabled = true
        mBinding.btnSave.visibility = View.VISIBLE
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getCorporateOfficeLOVValues(if (mPublicDomainOccupancy!!) "CRM_PublicDomainOccupancy" else "CRM_RightOfPlaces", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                occupancyTypes = response.occupancyTypes
                if (occupancyTypes.isNullOrEmpty())
                    mBinding.spnOccupancy.adapter = null
                else {
                    occupancyTypes?.add(0, VUCRMNatureOfOccupancy(getString(R.string.select), -1, 0, ""))
                    mBinding.spnOccupancy.adapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, occupancyTypes!!)
                }
                mMarkets = response.mMarkets
                if (mMarkets.isNullOrEmpty())
                    mBinding.spnMarkets.adapter = null
                else {
                    mMarkets?.add(0, VUCRMMarkets(getString(R.string.select), 0, "", ""))
                    mBinding.spnMarkets.adapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, mMarkets!!)
                }

                bindData()

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnOccupancy.adapter = null
                mBinding.spnMarkets.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindData() {
        mBinding.txtNumberOfDocuments.text = "0"
        mBinding.edtStartDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtStartDate.setText(displayFormatDate(getCurrentYearStartDate()))
        if (mItem != null) {
            isOccupancyChanged = false
            mBinding.edtLength.setText(if (mItem?.length != null) formatWithPrecisionCustomDecimals(mItem?.length.toString(), false, 3) else "")
            mBinding.edtWidth.setText(if (mItem?.width != null) formatWithPrecisionCustomDecimals(mItem?.width.toString(), false, 3) else "")
            mBinding.edtTaxableMatter.setText(formatWithPrecisionCustomDecimals(mItem?.taxableMatter.toString(), false, 3))
            mBinding.edtTaxPeriod.setText(mItem?.taxPeriod.toString())
            mBinding.edtDescription.setText(mItem?.description)
            mBinding.edtStartDate.setText(displayFormatDate(mItem?.startDate))
            /*mItem?.estimatedTax?.let {
                mBinding.edtEstimatedAmountForProduct.setText(formatWithPrecision(it))
            }*/
            if (mItem?.estimatedTax != null) {
                mBinding.edtEstimatedAmountForProduct.setText(formatWithPrecision(mItem?.estimatedTax))
            } else {
                mBinding.edtEstimatedAmountForProduct.setText(formatWithPrecision("0"))
            }

            mBinding.cbActive.isChecked = "Y" == mItem?.act

            mBinding.llIdentificationNo.visibility = View.VISIBLE

            if (mPublicDomainOccupancy != null && mPublicDomainOccupancy!!)
                mBinding.tvIDNo.text = ("${mItem?.publicDomainOccupancyID}")
            else
                mBinding.tvIDNo.text = ("${mItem?.rightOfPlaceID}")

            if (occupancyTypes != null)
                for ((index, obj) in occupancyTypes?.withIndex()!!) {
                    if (mItem?.occupancyID == obj.occupancyID) {
                        mBinding.spnOccupancy.setSelection(index)
                        break
                    }
                }
            if (mMarkets != null)
                for ((index, obj) in mMarkets?.withIndex()!!) {
                    if (mItem?.marketID == obj.marketID) {
                        mBinding.spnMarkets.setSelection(index)
                        break
                    }
                }

            bindCounts()
            getInvoiceCount4Tax()
        } else {
            mBinding.tvIDNo.text = ""
            mBinding.llIdentificationNo.visibility = View.GONE
        }
    }

    private fun fetchCount(filterColumns: List<FilterColumn>) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "COM_DocumentReferences"
        tableDetails.primaryKeyColumnName = "DocumentReferenceID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "AND"
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                mBinding.txtNumberOfDocuments.text = "0"
            }

            override fun onSuccess(response: Int) {
                mBinding.txtNumberOfDocuments.text = "$response"
            }
        })
    }

    private fun bindCounts() {
        mPublicDomainOccupancy?.let {
            val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
            var filterColumn = FilterColumn()
            filterColumn.columnName = "TableName"
            filterColumn.columnValue = if (it) "CRM_PublicDomainOccupancy" else "CRM_RightOfPlaces"
            filterColumn.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            filterColumn = FilterColumn()
            filterColumn.columnName = "PrimaryKeyValue"
            filterColumn.columnValue = "${if (it) mItem?.publicDomainOccupancyID else mItem?.rightOfPlaceID}"
            filterColumn.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            fetchCount(listFilterColumn)
        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.llDocuments.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.btnGet.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.edtLength.addTextChangedListener(this)
        mBinding.edtWidth.addTextChangedListener(this)
        mBinding.spnOccupancy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(view: AdapterView<*>?) {

            }

            override fun onItemSelected(view: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                if (isOccupancyChanged) {
                    // mBinding.edtStartDate.setText("")
                    mBinding.edtLength.setText("")
                    mBinding.edtWidth.setText("")
                    mBinding.edtTaxableMatter.setText("")
                }

                val vucrmNatureOfOccupancy: VUCRMNatureOfOccupancy = mBinding.spnOccupancy.selectedItem as VUCRMNatureOfOccupancy
                val unitCode = vucrmNatureOfOccupancy.unitCode

                mBinding.edtTaxableMatter.isEnabled = false
                mBinding.edtLength.isEnabled = false
                mBinding.edtWidth.isEnabled = false

                val square = arrayListOf("SCM", "SDM", "SFT", "SM", "SMM", "SQI", "SQM", "SQY")
                val cube = arrayListOf("CUF", "CUI", "CUM", "CUY")

                if (cube.contains(unitCode) || square.contains(unitCode)) {
                    mBinding.edtLength.isEnabled = true
                    mBinding.edtWidth.isEnabled = true
                } else if (unitCode == "M") {
                    mBinding.edtLength.isEnabled = true
                } else mBinding.edtTaxableMatter.isEnabled = mListener?.screenMode != Constant.ScreenMode.VIEW

                /*if (unitCode.isNullOrEmpty())
                    mBinding.edtTaxableMatter.isEnabled = true
                else {
                    if (square.contains(unitCode)) {
                        mBinding.edtLength.isEnabled = true
                        mBinding.edtWidth.isEnabled = true
                    } else if (cube.contains(unitCode)) {
                        mBinding.edtLength.isEnabled = true
                        mBinding.edtWidth.isEnabled = true
                    } else {
                        mBinding.edtLength.isEnabled = true
                    }
                }*/

                mBinding.edtTaxPeriod.setText(vucrmNatureOfOccupancy.taxPeriod.toString())
                isOccupancyChanged = true
                if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
                    mBinding.edtLength.isEnabled = false
                    mBinding.edtWidth.isEnabled = false
                }
                setViews()
            }

        }
    }

    fun onLayoutClick(v: View?) {
        when (v?.id) {
            R.id.llDocuments -> {
                when {
                    mPublicDomainOccupancy != null && ((mItem?.publicDomainOccupancyID != null && mItem?.publicDomainOccupancyID != 0) || (mItem?.rightOfPlaceID != null && mItem?.rightOfPlaceID != 0)) -> {
                        val fragment = DocumentsMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        mPublicDomainOccupancy?.let {
                            if (it) {
                                bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PUBLIC_DOMAIN_OCCUPANCY)
                                bundle.putInt(Constant.KEY_PRIMARY_KEY, mItem?.publicDomainOccupancyID
                                    ?: 0)
                            } else {
                                bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_RIGHTS_OF_MARKET_PLACES)
                                bundle.putInt(Constant.KEY_PRIMARY_KEY, mItem?.rightOfPlaceID ?: 0)
                            }
                        }
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                        mListener?.showToolbarBackButton(R.string.documents)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        prepareData(v)
                    }
                    else -> {

                    }
                }
            }
            R.id.btnSave -> {
                if (validateView()){
                    mListener?.showAlertDialog(R.string.are_you_sure_you_have_entered_all_valid_information,
                        R.string.yes,
                        View.OnClickListener {
                            val dialog = (it as Button).tag as AlertDialog
                            dialog.dismiss()
                            prepareData()
                        },
                        R.string.no,
                        View.OnClickListener
                        {
                            val dialog = (it as Button).tag as AlertDialog
                            dialog.dismiss()
                        })
                }
            }
            R.id.btnGet -> {
                fetchTaxableMatter()
            }
        }
    }

    private fun fetchTaxableMatter() {
        val getTaxableMatterColumnData = GetTaxableMatterColumnData()
        getTaxableMatterColumnData.taskCode = mTaskCode
        mListener?.showProgressDialog()
        APICall.getTaxableMatterColumnData(getTaxableMatterColumnData, object : ConnectionCallBack<List<DataTaxableMatter>> {
            override fun onSuccess(response: List<DataTaxableMatter>) {

                val list: ArrayList<DataTaxableMatter> = arrayListOf()
                for (it in response) {
                    val taxableMatter = DataTaxableMatter()
                    taxableMatter.taxableMatterColumnName = it.taxableMatterColumnName
                    if ("TaxableMatter" == it.taxableMatterColumnName) {
                        if (!TextUtils.isEmpty(mBinding.edtTaxableMatter.text?.toString()?.trim()))
                            taxableMatter.taxableMatter = currencyToDouble(mBinding.edtTaxableMatter.text.toString().trim())!!.toDouble().toBigDecimal().toString()
                    }
                    list.add(taxableMatter)
                }
                fetchEstimatedAmount(list)

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun fetchEstimatedAmount(taxableMatter: ArrayList<DataTaxableMatter>) {
        val getEstimatedTaxForProduct = GetEstimatedTaxForProduct()
        getEstimatedTaxForProduct.dataTaxableMatter = taxableMatter
        getEstimatedTaxForProduct.taskCode = mTaskCode
        getEstimatedTaxForProduct.customerID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
            ?: 0
        if (!TextUtils.isEmpty(mBinding.edtStartDate.text?.toString()?.trim()))
            getEstimatedTaxForProduct.startDate = serverFormatDate(mBinding.edtStartDate.text.toString().trim())
        getEstimatedTaxForProduct.entityPricingVoucherNo = "${(mBinding.spnOccupancy.selectedItem as VUCRMNatureOfOccupancy).occupancyID}"
        mListener?.showProgressDialog()
        APICall.getEstimatedTaxForProduct(getEstimatedTaxForProduct, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mBinding.edtEstimatedAmountForProduct.setText(formatWithPrecision(response))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.edtEstimatedAmountForProduct.setText("")
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun edtRemoveFocus(focus: Boolean = false) {

        mBinding.edtLength.isFocusable = focus
        mBinding.edtWidth.isFocusable = focus
        mBinding.edtTaxableMatter.isFocusable = focus

        mBinding.edtLength.isFocusableInTouchMode = true
        mBinding.edtWidth.isFocusableInTouchMode = true
        mBinding.edtTaxableMatter.isFocusableInTouchMode = true

    }

    private fun prepareData(view: View? = null) {
        edtRemoveFocus()
        if (mPublicDomainOccupancy!!) {
            val insertPODDetails = InsertPODDetails()
            if (mItem != null && mItem?.publicDomainOccupancyID != 0)
                insertPODDetails.publicDomainOccupancyID = mItem!!.publicDomainOccupancyID.toString()
            insertPODDetails.active = if (mBinding.cbActive.isChecked) "Y" else "N"
            insertPODDetails.occupancyID="${(mBinding.spnOccupancy.selectedItem as VUCRMNatureOfOccupancy).occupancyID}"
            insertPODDetails.description = mBinding.edtDescription.text.toString().trim()
            if (!mBinding.edtLength.text.toString().trim().isNullOrEmpty())
                insertPODDetails.length = currencyToDouble(mBinding.edtLength.text.toString().trim())!!.toDouble().toBigDecimal().toString()
            insertPODDetails.organizationID = "${
                ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
                    ?: 0
            }"
            insertPODDetails.taxPeriod = mBinding.edtTaxPeriod.text.toString().trim()
            if (!mBinding.edtTaxableMatter.text.toString().trim().isNullOrEmpty())
                insertPODDetails.taxableMatter = currencyToDouble(mBinding.edtTaxableMatter.text.toString().trim())!!.toDouble().toBigDecimal().toString()
            if (!mBinding.edtWidth.text.toString().trim().isNullOrEmpty())
                insertPODDetails.width = currencyToDouble(mBinding.edtWidth.text.toString().trim())!!.toDouble().toBigDecimal().toString()
            insertPODDetails.startDate = serverFormatDate(mBinding.edtStartDate.text.toString().trim())
            save(view, insertPODDetails = GetInsertPODDetails(SecurityContext(), insertPODDetails))
        } else {
            val insertROPDetails = InsertROPDetails()
            if (mItem != null && mItem?.rightOfPlaceID != 0)
                insertROPDetails.rightOfPlaceID = mItem!!.rightOfPlaceID.toString()
            insertROPDetails.active = if (mBinding.cbActive.isChecked) "Y" else "N"
            insertROPDetails.description = mBinding.edtDescription.text.toString().trim()
            if (!mBinding.edtLength.text.toString().trim().isNullOrEmpty())
                insertROPDetails.length = currencyToDouble(mBinding.edtLength.text.toString().trim())!!.toDouble().toString()
            insertROPDetails.occupancyID = "${(mBinding.spnOccupancy.selectedItem as VUCRMNatureOfOccupancy).occupancyID}"

//            if ((mBinding.spnMarkets.selectedItem as VUCRMMarkets).marketID != -1)
            insertROPDetails.marketID = "${(mBinding.spnMarkets.selectedItem as VUCRMMarkets).marketID}"

            insertROPDetails.organizationID = "${
                ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
                    ?: 0
            }"
            insertROPDetails.taxPeriod = "1"
            if (!mBinding.edtTaxableMatter.text.toString().trim().isNullOrEmpty())
                insertROPDetails.taxableMatter = currencyToDouble(mBinding.edtTaxableMatter.text.toString().trim())!!.toDouble().toBigDecimal().toString()
            if (!mBinding.edtWidth.text.toString().trim().isNullOrEmpty())
                insertROPDetails.width = currencyToDouble(mBinding.edtWidth.text.toString().trim())!!.toDouble().toBigDecimal().toString()
            insertROPDetails.startDate = serverFormatDate(mBinding.edtStartDate.text.toString().trim())
            save(view, insertROPDetails = GetInsertROPDetails(SecurityContext(), insertROPDetails))
        }
    }

    private fun save(view: View? = null, insertPODDetails: GetInsertPODDetails? = null, insertROPDetails: GetInsertROPDetails? = null) {
        if (0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId) {
            mListener?.showProgressDialog()
            APICall.insertPDOAndROPDetails(if (mPublicDomainOccupancy!!) insertPODDetails else insertROPDetails, object : ConnectionCallBack<Int> {
                override fun onSuccess(response: Int) {
                    mListener?.dismissDialog()

                    mPublicDomainOccupancy?.let {
                        if (mItem == null) mItem = ROPListItem()
                        if (it) {
                            if (response != 0)
                                mItem?.publicDomainOccupancyID = response
                        } else {
                            if (response != 0)
                                mItem?.rightOfPlaceID = response
                        }
                    }

                    if (view == null)
                        Handler().postDelayed({
                            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                            mListener?.popBackStack()
                        }, 750)
                    else
                        onLayoutClick(view)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mPublicDomainOccupancy != null && mPublicDomainOccupancy!!) {
            mListener?.showToolbarBackButton(R.string.public_domain_occupancy)
        } else {
            mListener?.showToolbarBackButton(R.string.rights_of_places_in_markets)
        }
        bindCounts()
    }

    private fun validateView(): Boolean {
        edtRemoveFocus()
        if (mBinding.spnOccupancy.selectedItem == null || -1 == (mBinding.spnOccupancy.selectedItem as VUCRMNatureOfOccupancy).occupancyID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.occupancy))
            return false
        }

        if ((mBinding.edtTaxableMatter.text == null && TextUtils.isEmpty(mBinding.edtTaxableMatter.text.toString().trim { it <= ' ' })) ||   currencyToDouble(mBinding.edtTaxableMatter.text.toString().trim())!!.toDouble() <= 0) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.taxable_matter))
            return false
        }

        return true
    }

    override fun afterTextChanged(p0: Editable?) {
        var length = 1.0
        var width = 1.0

        /*if (mBinding.edtLength.text.toString().trim().isNotEmpty() && mBinding.edtLength.text.toString().trim().toDouble() != 0.0)
            length = mBinding.edtLength.text.toString().trim().toDouble()
        if (mBinding.edtWidth.text.toString().trim().isNotEmpty() && mBinding.edtWidth.text.toString().trim().toDouble() != 0.0)
            width = mBinding.edtWidth.text.toString().trim().toDouble()

        if (mBinding.edtLength.text.isNullOrEmpty() && mBinding.edtWidth.text.isNullOrEmpty()) {
            edtTaxableMatter.setText("")
        } else {
            edtTaxableMatter.setText(truncateTo((length*width)).toString())
        }*/


        if (mBinding.edtLength.text.toString().trim().isNotEmpty()) {
            length = if (mBinding.edtLength.text.toString().trim().contains(","))
                (currencyToDouble(mBinding.edtLength.text.toString().trim())!!.toDouble())!!.toDouble()
            else
                getDecimalVal( mBinding.edtLength.text.toString().trim())
        }
        if (mBinding.edtWidth.text.toString().trim().isNotEmpty())
            width = if (mBinding.edtWidth.text.toString().trim().contains(","))
                (currencyToDouble(mBinding.edtWidth.text.toString().trim())!!.toDouble())!!.toDouble()
            else
                getDecimalVal(mBinding.edtWidth.text.toString().trim())

        if (mBinding.edtLength.text.isNullOrEmpty() && mBinding.edtWidth.text.isNullOrEmpty()) {
            mBinding.edtTaxableMatter.setText("")
        } else {
            mBinding.edtTaxableMatter.setText(formatWithPrecisionCustomDecimals((length * width).toString(), false, 3))
        }

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    private fun getInvoiceCount4Tax() {

        val currentDue = CheckCurrentDue()
        currentDue.accountId = mItem?.acctid
        if (mPublicDomainOccupancy == true)
            currentDue.vchrno  = mItem?.publicDomainOccupancyID
        else
            currentDue.vchrno  = mItem?.rightOfPlaceID

        currentDue.taxRuleBookCode  = mTaxRuleBookCode
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response>0)
                {
                    if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }
                    else {
                        setEditActionForSave(false)
                    }
                }
                else
                {
                    if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }
                    else {
                        setEditAction(true)
                    }
                }
            }
            override fun onFailure(message: String) {
                if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                {
                    setEditAction(false)
                }
            }
        })
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog(message: Int)
        fun showProgressDialog(message: String)
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        fun showToast(message: String)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener)
        var screenMode: Constant.ScreenMode

    }

}
