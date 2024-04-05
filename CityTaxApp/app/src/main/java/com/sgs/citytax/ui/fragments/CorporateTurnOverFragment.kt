package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.CorporateTurnoverResponse
import com.sgs.citytax.databinding.FragmentCorporateTurnoverBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.*
import java.math.BigDecimal
import java.util.*

class CorporateTurnOverFragment : BaseFragment() {

    private var mListener: Listener? = null
    private lateinit var mBinding: FragmentCorporateTurnoverBinding
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mTaskCode: String? = ""
    private var mProductBilling: String? = ""
    private var mProduct: VuInvProducts? = null
    private var mShowProportionalDutyOnCP: Boolean = false

    private var mCorporateTurnover: VUCRMCorporateTurnover? = null
    private var mNoOfCorporateTurnOver: Int = 0
    private var mTaxRuleBookCode: String? = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_corporate_turnover, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mCorporateTurnover = arguments?.getParcelable(Constant.KEY_CORPORATE_TURNOVER)
            mProduct = arguments?.getParcelable(Constant.KEY_PRODUCT_DETAILS)
            mNoOfCorporateTurnOver = arguments?.getInt(Constant.KEY_NUMBER_OF_CORPORATE_TURNOVER)
                    ?: 0
            mTaskCode = it.getString(Constant.KEY_TASK_CODE)
            mProductBilling = it.getString(Constant.KEY_PRODUCT_BILLING_CYCLE)
            mShowProportionalDutyOnCP = it.getBoolean(Constant.KEY_SHOW_PROPORTIONAL_DUTY)
            mTaxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)

        }
        setViews()
        bindData()
        setListeners()
        if (mCorporateTurnover == null) {
            getLastBillingCycleActualAmountAPI()
        }

    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
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
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "CRM_CorporateTurnover"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${mCorporateTurnover?.turnoverID}"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        fetchCount(listFilterColumn)
    }

    fun onLayoutClick(v: View?) {
        when (v?.id) {
            R.id.llDocuments -> {
                when {
                    mCorporateTurnover != null && mCorporateTurnover?.turnoverID != 0 -> {
                        val fragment = DocumentsMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_CORPORATE_TURN_OVER)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, mCorporateTurnover?.turnoverID ?: 0)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                        mListener?.showToolbarBackButton(R.string.documents)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveCorporateTurnOver(getPayload(), v)
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
                                saveCorporateTurnOver(getPayload())
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
                var amount = mBinding.edtAmount.text.toString()
                if (!amount.isNullOrEmpty()) {
                    fetchTaxableMatter()
                }
                else{
                    mListener?.showSnackbarMsg(resources.getString(R.string.msg_enter_estimated_amount))
                }
            }
            R.id.btnCalculateForProp -> {
                if (!mBinding.edtRentPerRateCycle.text.toString().isNullOrEmpty() && !mBinding.edtAmount.text.toString().isNullOrEmpty()) {
                    fetchTaxableMatterProp()
                } else {
                    mListener?.showSnackbarMsg(resources.getString(R.string.msg_enter_rent_amount))
                }
            }
        }
    }

    private fun fetchTaxableMatterProp() {
        val getTaxableMatterColumnData = GetTaxableMatterColumnData()
        getTaxableMatterColumnData.taskCode = "CRM_ProportionalDutyOnRental"
        mListener?.showProgressDialog()
        APICall.getTaxableMatterColumnData(getTaxableMatterColumnData, object : ConnectionCallBack<List<DataTaxableMatter>> {
            override fun onSuccess(response: List<DataTaxableMatter>) {

                val list: ArrayList<DataTaxableMatter> = arrayListOf()
                for (it in response) {
                    val taxableMatter = DataTaxableMatter()
                    taxableMatter.taxableMatterColumnName = it.taxableMatterColumnName
                    if ("Amount" == it.taxableMatterColumnName) {
                        var amount = mBinding.edtAmount.text.toString()
                        taxableMatter.taxableMatter = currencyToDouble(amount).toString()
                    }
                    if ("RentPerRateCycle" == it.taxableMatterColumnName) {
                        var rentamount = mBinding.edtRentPerRateCycle.text.toString()
                        taxableMatter.taxableMatter = currencyToDouble(rentamount).toString()
                    }
                    list.add(taxableMatter)
                }
                fetchEstimatedAmountProp(list)

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
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
                    /*if ("Amount" == it.taxableMatterColumnName) {
                        taxableMatter.taxableMatter = "1"
                    }*/
                    if ("Amount" == it.taxableMatterColumnName) {
                        var amount = mBinding.edtAmount.text.toString()
                        taxableMatter.taxableMatter = currencyToDouble(amount).toString()
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

    private fun fetchEstimatedAmountProp(taxableMatter: ArrayList<DataTaxableMatter>) {
        val getEstimatedTaxForProduct = GetEstimatedTaxForProduct()
        getEstimatedTaxForProduct.dataTaxableMatter = taxableMatter
        getEstimatedTaxForProduct.taskCode = "CRM_ProportionalDutyOnRental"
        getEstimatedTaxForProduct.customerID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                ?: 0
        if (!TextUtils.isEmpty(mBinding.edtRentalStartDate.text?.toString()?.trim()))
            getEstimatedTaxForProduct.startDate = serverFormatDate(mBinding.edtRentalStartDate.text.toString().trim())
        mListener?.showProgressDialog()
        APICall.getEstimatedTaxForProduct(getEstimatedTaxForProduct, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mBinding.edtEstimatedAmountForProductProp.setText(formatWithPrecision(response))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.edtEstimatedAmountForProductProp.setText("")
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
        if (!TextUtils.isEmpty(mBinding.edtBusinessStartDate.text?.toString()?.trim()))
            getEstimatedTaxForProduct.startDate = serverFormatDate(mBinding.edtBusinessStartDate.text.toString().trim())
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

    private fun getLastBillingCycleActualAmountAPI() {
        val lastBillingCycleActualAmount = LastBillingCycleActualAmount()
        lastBillingCycleActualAmount.strtdt = serverFormatDate(mBinding.edtBusinessStartDate.text.toString().trim())
        lastBillingCycleActualAmount.taxPayerOrganizationID = ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
                ?: 0
        lastBillingCycleActualAmount.taxPayerAccountID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                ?: 0
        mListener?.showProgressDialog()
        APICall.getLastBillingCycleActualAmount(lastBillingCycleActualAmount, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mBinding.edtLastBillingCycleActualAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15 + 15))
                mBinding.edtLastBillingCycleActualAmount.setText(formatWithPrecision(response))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.edtEstimatedAmountForProduct.setText("")
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    fun setViews() {
        mBinding.edtBusinessStartDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtBusinessStartDate.setMaxDate(Calendar.getInstance().timeInMillis)
        /*if (mListener!!.isViewMode && MyApplication.getPrefHelper().superiorTo.isNotEmpty()) {
              mBinding.edtToDate.isEnabled = false
              mBinding.edtFromDate.isEnabled = false
              mBinding.edtAmount.isEnabled = false
              mBinding.btnSave.isEnabled = false
          }*/
        if (mNoOfCorporateTurnOver == 0)
            mBinding.tilBusinessStartDate.visibility = VISIBLE
        else
            mBinding.tilBusinessStartDate.visibility = GONE

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
            Constant.ScreenMode.ADD -> if(mCorporateTurnover!=null && mCorporateTurnover?.allowDelete=="N")setEditAction(false)
        }
        if (mShowProportionalDutyOnCP) mBinding.llProportionalDuty.visibility = VISIBLE else mBinding.llProportionalDuty.visibility = GONE
    }

    private fun setEditAction(action: Boolean) {
        mBinding.edtFromDate.isEnabled = action
        mBinding.edtToDate.isEnabled = action
        mBinding.edtAmount.isEnabled = action
        mBinding.edtBusinessStartDate.isEnabled = action
        mBinding.chkActive.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.btnCalculateForProp.isEnabled = action
        mBinding.edtRentalStartDate.isEnabled = action
        mBinding.edtRentPerRateCycle.isEnabled = action
        mBinding.edtLastBillingCycleActualAmount.isEnabled = action
//        mBinding.llDocuments.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }


    private fun setEditActionForSave(action: Boolean) {
        mBinding.edtFromDate.isEnabled = action
        mBinding.edtToDate.isEnabled = action
        mBinding.edtAmount.isEnabled = action
        mBinding.edtBusinessStartDate.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.btnCalculateForProp.isEnabled = action
        mBinding.edtRentalStartDate.isEnabled = action
        mBinding.edtRentPerRateCycle.isEnabled = action
        mBinding.edtLastBillingCycleActualAmount.isEnabled = action

        mBinding.chkActive.isEnabled = true
        mBinding.btnSave.visibility = View.VISIBLE

    }

    private fun bindData() {
        if (!mProductBilling.isNullOrEmpty())
            mBinding.tilRentPerRateCycle.hint = getString(R.string.rent_per_rate_cycle, mProductBilling)
        else
            mBinding.tilRentPerRateCycle.hint = getString(R.string.rent_per_rate_cycle, "")
        mBinding.txtNumberOfDocuments.text = "0"
        mBinding.edtFromDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtRentalStartDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtToDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtFromDate.setText(getDate(setCalendarText(Calendar.JANUARY, 1), displayDateFormat))
        mBinding.edtRentalStartDate.setText(getDate(setCalendarText(Calendar.JANUARY, 1), displayDateFormat))
        mBinding.edtToDate.setText(getDate(setCalendarText(Calendar.DECEMBER, 31), displayDateFormat))

        val timeInMillis = Calendar.getInstance().timeInMillis
        mBinding.edtFromDate.setMaxDate(timeInMillis)
        mBinding.edtToDate.setMinDate(timeInMillis)

        val calender = Calendar.getInstance(Locale.getDefault())
        calender.set(calender.get(Calendar.YEAR), Calendar.JANUARY, 1)
        mBinding.edtBusinessStartDate.setText(displayFormatDate(getCurrentYearStartDate()))
        mBinding.edtBusinessStartDate.setMinDate(calender.timeInMillis)
        mBinding.edtBusinessStartDate.setMaxDate(timeInMillis)
        mBinding.edtBusinessStartDate.setDisplayDateFormat(displayDateFormat)


        if (mCorporateTurnover != null) {
//            mBinding.chkActive.isChecked = mCorporateTurnover!!.active == "Y"
            mBinding.edtFromDate.setText(displayFormatDate(mCorporateTurnover!!.financialStartDate))
            mBinding.edtToDate.setText(displayFormatDate(mCorporateTurnover!!.financialEndDate))
            /*  mCorporateTurnover?.rentalStartDate?.let {
                  mBinding.edtRentalStartDate.setText(displayFormatDate(it))
              }*/
            if (!mCorporateTurnover?.rentalStartDate.isNullOrEmpty()) {
                mBinding.edtRentalStartDate.setText(displayFormatDate(mCorporateTurnover?.rentalStartDate))
            } else {
                mBinding.edtRentalStartDate.setText("")
            }


            //mBinding.edtAmount.setText("${mCorporateTurnover!!.amount}")
            //  mBinding.edtAmount.setText(formatWithPrecision(mCorporateTurnover!!.amount))

            mCorporateTurnover?.amount?.let {
                mBinding.edtAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9 + 15))
                mBinding.edtAmount.setText(formatWithPrecision(mCorporateTurnover!!.amount))
            }


            mCorporateTurnover?.lastBillingCycleActualAmount?.let {
                mBinding.edtLastBillingCycleActualAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9 + 15))
                mBinding.edtLastBillingCycleActualAmount.setText(formatWithPrecision(it))
            }
            mCorporateTurnover?.rentPerRateCycle?.let {
                mBinding.edtRentPerRateCycle.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9 + 15))
                mBinding.edtRentPerRateCycle.setText(formatWithPrecision(it))
            }
            mCorporateTurnover?.estimatedTax?.let {
                mBinding.edtEstimatedAmountForProduct.setText(formatWithPrecision(it))
            }
            mCorporateTurnover?.estimatedTaxProp?.let {
                mBinding.edtEstimatedAmountForProductProp.setText(formatWithPrecision(it))
            }
            mCorporateTurnover?.startDate?.let { it ->
                mCorporateTurnover?.financialStartDate?.let {
                    mBinding.edtBusinessStartDate.setMinDate(getTimeStamp(it, Constant.DateFormat.SERVER))
                }
                mBinding.edtBusinessStartDate.setText(displayFormatDate(it))
                mBinding.tilBusinessStartDate.visibility = VISIBLE
            }
            bindCounts()
            getInvoiceCount4Tax()
        }

        mBinding.edtAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtAmount.text.toString()
                if (text?.isNotEmpty()!!) {
                    mBinding.edtAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
                    mBinding.edtAmount.setText("${currencyToDouble(text)}");
                }
            } else {
                var cost = mBinding.edtAmount.text.toString();
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = currencyToDouble(cost)!!.toDouble()
                    mBinding.edtAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9 + 15))
                    mBinding.edtAmount.setText("${formatWithPrecisionCustomDecimals(cost,true,3)}")
                }
            }
        }

        mBinding.edtLastBillingCycleActualAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtLastBillingCycleActualAmount.text.toString()
                if (text?.isNotEmpty()!!) {
                    mBinding.edtLastBillingCycleActualAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15))
                    mBinding.edtLastBillingCycleActualAmount.setText("${currencyToDouble(text)}");
                }
            } else {
                var cost = mBinding.edtLastBillingCycleActualAmount.text.toString();
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = currencyToDouble(mBinding.edtLastBillingCycleActualAmount.text.toString())!!.toDouble()
                    mBinding.edtLastBillingCycleActualAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15 + 15))
                    mBinding.edtLastBillingCycleActualAmount.setText("${formatWithPrecision(enteredText)}")
                }
            }
        }

/*        mBinding.edtLastBillingCycleActualAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                val text: String = mBinding.edtLastBillingCycleActualAmount.text.toString()
                if (text?.isNotEmpty()!!) {
                    mBinding.edtLastBillingCycleActualAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                    mBinding.edtLastBillingCycleActualAmount.setText("${currencyToDouble(text)}");
                }
            } else {
                var cost = mBinding.edtLastBillingCycleActualAmount.text.toString();
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = mBinding.edtLastBillingCycleActualAmount.text.toString().toDouble()
                    mBinding.edtLastBillingCycleActualAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8 + 15))
                    mBinding.edtLastBillingCycleActualAmount.setText("${formatWithPrecision(enteredText)}")
                }
            }
        }*/

        mBinding.edtRentPerRateCycle.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtRentPerRateCycle.text.toString()
                if (text?.isNotEmpty()!!) {
                    mBinding.edtRentPerRateCycle.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15))
                    mBinding.edtRentPerRateCycle.setText("${currencyToDouble(text)}");
                }
            } else {
                var cost = mBinding.edtRentPerRateCycle.text.toString();
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = currencyToDouble(cost)!!.toDouble()
                    mBinding.edtRentPerRateCycle.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15 + 15))
                    mBinding.edtRentPerRateCycle.setText("${formatWithPrecisionCustomDecimals(cost,true,3)}")
                }
            }
        }
    }

    private fun setCalendarText(month: Int, date: Int): Date {
        val calender = Calendar.getInstance(Locale.getDefault())
        calender.set(calender.get(Calendar.YEAR), month, date)
        return calender.time
    }

    private fun setListeners() {
        mBinding.llDocuments.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
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
        mBinding.btnCalculateForProp.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.edtFromDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.edtFromDate.text?.toString()?.let {
                    if (it.isNotEmpty()) {
                        mBinding.edtToDate.setMinDate(parseDate(it, displayDateFormat).time)
                        mBinding.edtBusinessStartDate.setMinDate(parseDate(it, displayDateFormat).time)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun validateView(): Boolean {
        if (TextUtils.isEmpty(mBinding.edtAmount.text.toString().trim())) {
            mListener?.showSnackbarMsg(resources.getString(R.string.msg_enter_amount))
            mBinding.edtAmount.requestFocus()
            return false
        }
        var amountSave = mBinding.edtAmount.text.toString()
        if (currencyToDouble(amountSave).toString().toBigDecimal() == BigDecimal.ZERO) {
            mListener?.showSnackbarMsg(R.string.msg_amount_greater_than_zero)
            mBinding.edtAmount.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(mBinding.edtFromDate.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.financial_start_date))
            mBinding.edtFromDate.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(mBinding.edtToDate.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.financial_end_date))
            mBinding.edtToDate.requestFocus()
            return false
        }

        if (mBinding.tilBusinessStartDate.visibility == VISIBLE && TextUtils.isEmpty(mBinding.edtBusinessStartDate.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.business_start_date))
            mBinding.edtBusinessStartDate.requestFocus()
            return false
        }

        if(mShowProportionalDutyOnCP){
            if (TextUtils.isEmpty(mBinding.edtRentPerRateCycle.text.toString().trim())) {
                mListener?.showSnackbarMsg(resources.getString(R.string.msg_enter_amount))
                mBinding.edtRentPerRateCycle.requestFocus()
                return false
            }

            var amountSave = mBinding.edtRentPerRateCycle.text.toString()
            if (currencyToDouble(amountSave).toString().toBigDecimal() == BigDecimal.ZERO) {
                mListener?.showSnackbarMsg(R.string.msg_amount_greater_than_zero)
                mBinding.edtRentPerRateCycle.requestFocus()
                return false
            }
        }

        return true
    }

    private fun getPayload(): InsertCorporateTurnover {
        val crmCorporateTurnover = CRMCorporateTurnover()

        if (mCorporateTurnover != null && mCorporateTurnover!!.turnoverID != 0)
            crmCorporateTurnover.turnoverID = mCorporateTurnover!!.turnoverID

        if (mBinding.edtToDate.text != null && !TextUtils.isEmpty(mBinding.edtToDate.text.toString()))
            crmCorporateTurnover.financialEndDate = serverFormatDate(mBinding.edtToDate.text.toString().trim())

        if (mBinding.edtFromDate.text != null && !TextUtils.isEmpty(mBinding.edtFromDate.text.toString()))
            crmCorporateTurnover.financialStartDate = serverFormatDate(mBinding.edtFromDate.text.toString().trim())

        if (mBinding.edtBusinessStartDate.visibility == VISIBLE && mBinding.edtBusinessStartDate.text != null && !TextUtils.isEmpty(mBinding.edtBusinessStartDate.text.toString()))
            crmCorporateTurnover.startDate = serverFormatDate(mBinding.edtBusinessStartDate.text.toString().trim())
        else
            crmCorporateTurnover.startDate = null

        if (mBinding.edtAmount.text != null && !TextUtils.isEmpty(mBinding.edtAmount.text.toString())) {
            var amountSave = mBinding.edtAmount.text.toString().substringBefore(",").replace("\\s".toRegex(),"")
            crmCorporateTurnover.amount = getDoubleAmountFromValue(amountSave).toBigDecimal()
        }

        if (mBinding.chkActive.isChecked)
            crmCorporateTurnover.active = "Y"
        else
            crmCorporateTurnover.active = "N"

        crmCorporateTurnover.organizationId = ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
        if (mBinding.edtLastBillingCycleActualAmount.text != null && !TextUtils.isEmpty(mBinding.edtLastBillingCycleActualAmount.text.toString()))
            crmCorporateTurnover.lastBillingCycleActualAmount = currencyToDouble(mBinding.edtLastBillingCycleActualAmount.text.toString())?.toDouble()?.toBigDecimal()
        val proportionalDutyOnRental = ProportionalDutyOnRental()
        if (mBinding.edtRentPerRateCycle.text != null && !TextUtils.isEmpty(mBinding.edtRentPerRateCycle.text.toString()))
            proportionalDutyOnRental.rentPerRateCycle = currencyToDouble(mBinding.edtRentPerRateCycle.text.toString())?.toDouble()?.toBigDecimal()
        if (mBinding.edtRentalStartDate.text != null && !TextUtils.isEmpty(mBinding.edtRentalStartDate.text.toString()))
            proportionalDutyOnRental.rentalStartDate = serverFormatDate(mBinding.edtRentalStartDate.text.toString().trim())
        mCorporateTurnover?.rentalID?.let {
            if (it != 0)
                proportionalDutyOnRental.rentalID = it
        }

        val insertCorporateTurnover = InsertCorporateTurnover()
        insertCorporateTurnover.corporateTurnOver = crmCorporateTurnover
        insertCorporateTurnover.proportionalDutyOnRental = proportionalDutyOnRental

        return insertCorporateTurnover
    }

    private fun saveCorporateTurnOver(insertCorporateTurnover: InsertCorporateTurnover, view: View? = null) {
        if (0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId) {
            try {
                mListener?.showProgressDialog(getString(R.string.inserting) + " ${getString(R.string.corporate_turnover)}")

                APICall.insertCorporateTurnOver(insertCorporateTurnover, object : ConnectionCallBack<CorporateTurnoverResponse> {
                    override fun onSuccess(response: CorporateTurnoverResponse) {
                        mListener?.dismissDialog()
                        if (mCorporateTurnover == null) mCorporateTurnover = VUCRMCorporateTurnover()

                        response.corporateTurnoverID?.let {
                            mCorporateTurnover?.turnoverID = it
                        }

                        response.RentalID?.let {
                            mCorporateTurnover?.rentalID = it
                        }

                        if (view == null) {
                            if ((mCorporateTurnover != null) && mCorporateTurnover?.turnoverID != 0)
                                mListener?.showToast("${getString(R.string.corporate_turnover)} " + getString(R.string.updated_successfully))
                            else
                                mListener?.showToast("${getString(R.string.corporate_turnover)} " + getString(R.string.added_successfully))
                            Handler().postDelayed({
                                targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                                mListener!!.popBackStack()
                            }, 750)
                        } else onLayoutClick(view)
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        mListener?.showAlertDialog(message)
                    }
                })
            } catch (e: Exception) {
                LogHelper.writeLog(exception = e)
            }
        } /*else {
            mListener?.showAlertDialog("In complete flow")
               if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
                   ObjectHolder.registerBusiness.crmCorporateTurnovers.add(crmCorporateTurnover)
                   Handler().postDelayed({
                       targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                       mListener!!.popBackStack()
                   }, 750)
               } else {

               }
        }*/
    }
    private fun getInvoiceCount4Tax() {

        val currentDue = CheckCurrentDue()
        currentDue.accountId = mCorporateTurnover?.acctid
        currentDue.vchrno  = mCorporateTurnover?.turnoverID
        currentDue.taxRuleBookCode  = mTaxRuleBookCode
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response>0)
                {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }else{
                        setEditActionForSave(false)
                    }
                }
                else
                {
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mListener?.showToolbarBackButton(R.string.corporate_turnover)
        bindCounts()
    }

    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String)
        fun showProgressDialog()
        fun showProgressDialog(message: String)
        fun showSnackbarMsg(message: String)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showToast(message: String)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener)
        var screenMode: Constant.ScreenMode
    }

}
