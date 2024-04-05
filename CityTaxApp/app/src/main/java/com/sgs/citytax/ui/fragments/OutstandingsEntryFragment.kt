package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.SaveOutstanding
import com.sgs.citytax.api.response.*
import com.sgs.citytax.databinding.FragmentOutstandingsEntryBinding
import com.sgs.citytax.model.OutstandingType
import com.sgs.citytax.util.*
import java.math.BigDecimal
import java.util.*


class OutstandingEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentOutstandingsEntryBinding
    private var mListener: Listener? = null
    private var mOutstandingTypes: MutableList<OutstandingType>? = arrayListOf()
    private var mTaxTypesTypes: ArrayList<CustomerProduct>? = arrayListOf()
    private var mProductTypes: ArrayList<ProductTypes>? = arrayListOf()
    private var mVoucherNumbers: ArrayList<OutstandingVoucherNo>? = arrayListOf()
    private var mYears = arrayListOf<String>()
    private var customerId: Int? = 0
    private var voucherNo: Int? = 0
    private var mtaxRuleBookCode: String? = null
    private var productCode: String? = null
    private var outstandingID: Int? = 0
    private var editableStatus: Boolean = true
    private var mOutstandings: GetOutstanding? = null
    private var fromScreen: Constant.QuickMenu? = null

    override fun initComponents() {
        arguments?.let {
            customerId = arguments?.getInt(Constant.KEY_CUSTOMER_ID) ?: 0
            mOutstandings = arguments?.getParcelable(Constant.KEY_OUT_STANDING)
            if (it.containsKey(Constant.KEY_VOUCHER_NO))
                voucherNo = arguments?.getInt(Constant.KEY_VOUCHER_NO)
            if (it.containsKey(Constant.KEY_PRODUCT_CODE))
                productCode = arguments?.getString(Constant.KEY_PRODUCT_CODE)
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?
        }
        initViews()
        getYears()
        setListeners()
        fetchYears()
        showViewsEnabled()
    }

    private fun initViews() {
        if (fromScreen != null)
            mBinding.llTaxType.visibility = View.GONE
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND)
        {
            mBinding.llProductType.visibility = View.VISIBLE
        }
    }

    private fun showViewsEnabled() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {
        mBinding.edtNetReceivable.isEnabled = action
        mBinding.spnOutstandingType.isEnabled = action
        mBinding.spnYear.isEnabled = action
        mBinding.spnTax.isEnabled = action
        mBinding.spnVoucherNo.isEnabled = action
        mBinding.spnProductType.isEnabled = action
        if (action)
            mBinding.btnSave.visibility = View.VISIBLE
        else
            mBinding.btnSave.visibility = View.GONE
    }

    private fun fetchOutStandings() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("ACC_InitialOutstandings", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mOutstandingTypes = response.outstandingTypes
                if (mOutstandingTypes != null && mOutstandingTypes!!.isNotEmpty()) {
                    mOutstandingTypes?.add(0, OutstandingType(getString(R.string.select), "-1"))
                    mBinding.spnOutstandingType.adapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, mOutstandingTypes!!)
                    if (fromScreen == null)
                        fetchTaxes()
                    else
                        bindData()
                } else
                    mBinding.spnOutstandingType.adapter = null
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnOutstandingType.adapter = null
                mListener?.dismissDialog()
                mListener?.showSnackbarMsg(message)
            }
        })

    }

    private fun fetchTaxes() {
        mListener?.showProgressDialog()
        APICall.getTaxTypes(customerId?.toString(), object : ConnectionCallBack<List<CustomerProduct>> {
            override fun onSuccess(response: List<CustomerProduct>) {
                mTaxTypesTypes = response as ArrayList<CustomerProduct>

                if (mTaxTypesTypes != null && mTaxTypesTypes!!.isNotEmpty()) {
                    mTaxTypesTypes?.add(0, CustomerProduct(0, "-1", getString(R.string.select), ""))
                    mBinding.spnTax.adapter = ArrayAdapter<CustomerProduct>(activity!!, android.R.layout.simple_list_item_1, mTaxTypesTypes!!)
                    bindData()
                } else
                    mBinding.spnTax.adapter = null
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnTax.adapter = null
                mListener?.dismissDialog()
            }
        })

    }

    private fun fetchYears() {
        mYears.add(0, getString(R.string.select))
        mBinding.spnYear.adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, mYears)
        fetchOutStandings()
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND) {
            fetchProductTypes()
        }
    }

    private fun fetchProductTypes() {
        mListener?.showProgressDialog()
        APICall.getTaxes4InitialOutstanding(voucherNo, object : ConnectionCallBack<List<ProductTypes>> {
            override fun onSuccess(response: List<ProductTypes>) {
                mProductTypes = response as ArrayList<ProductTypes>

                if (mProductTypes != null && mProductTypes!!.isNotEmpty()) {
                    mProductTypes?.add(0, ProductTypes("-1", "-1", getString(R.string.select), "", ""))
                    mBinding.spnProductType.adapter = ArrayAdapter<ProductTypes>(activity!!, android.R.layout.simple_list_item_1, mProductTypes!!)
                    bindData()
                } else
                    mBinding.spnTax.adapter = null
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnTax.adapter = null
                mListener?.dismissDialog()
            }
        })

    }

    private fun fetchVoucherNumbers() {
        mListener?.showProgressDialog()
        APICall.getVoucherNumber(customerId?.toInt(), mtaxRuleBookCode, object : ConnectionCallBack<List<OutstandingVoucherNo>> {
            override fun onSuccess(response: List<OutstandingVoucherNo>) {
                mVoucherNumbers = response as ArrayList<OutstandingVoucherNo>
//                mVoucherNumbers?.add(0, OutstandingVoucherNo("-1", getString(R.string.select), "", 0))
                mVoucherNumbers?.add(0, OutstandingVoucherNo("-1", 0, getString(R.string.select), 0, getString(R.string.select)))
                mVoucherNumbers?.let {

                    mBinding.spnVoucherNo.adapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, mVoucherNumbers!!)

                    if (mOutstandings != null) {
                        mVoucherNumbers?.let { it ->
                            for ((index, obj) in it.withIndex()) {
                                if (mOutstandings!!.name == obj.name) {
                                    mBinding.spnVoucherNo.setSelection(index)
                                }
                            }
                        }
                    }
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnVoucherNo.adapter = null

                mListener?.dismissDialog()
                mListener?.showSnackbarMsg(message)
            }

        })
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_outstandings_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun getYears() {
        val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val year = calendar[Calendar.YEAR]
        for (i in 0..19) {
            val currentYear = year - i
            mYears.add(i, currentYear.toString())
        }

    }


    private fun bindData() {
        //edit booking focus listener
        /*mBinding.edtNetReceivable.onFocusChangeListener = View.OnFocusChangeListener() { v, hasFocus ->
            mBinding.edtNetReceivable.isFocusable = true
            if (hasFocus) {
                val text: String = mBinding.edtNetReceivable.text.toString()
                if (text?.isNotEmpty()!!)
                    mBinding.edtNetReceivable.setText("${currencyToDouble(text)}");
            } else {
                //this if condition is true when edittext lost focus...
                //check here for number is larger than 10 or not
                val enteredText: Double = mBinding.edtNetReceivable.text.toString().toDouble()
                mBinding.edtNetReceivable.setText("${formatWithPrecision(enteredText)}")
            }
        }*/
        mOutstandings?.let {

            outstandingID = mOutstandings?.initialOutstandingID

            if (mOutstandingTypes != null)
                for ((index, obj) in mOutstandingTypes!!.withIndex()) {
                    if (mOutstandings!!.outstandingTypeCode == obj.outstandingTypeCode) {
                        mBinding.spnOutstandingType.setSelection(index)

                    }
                }
            if (mTaxTypesTypes != null)
                for ((index, obj) in mTaxTypesTypes!!.withIndex()) {
                    if (mOutstandings!!.productCode == obj.productCode) {
                        mBinding.spnTax.setSelection(index)
                    }
                }
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_LAND
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND) {
                if (mProductTypes != null)
                    for ((index, obj) in mProductTypes!!.withIndex()) {
                        if (mOutstandings!!.productCode == obj.productCode) {
                            mBinding.spnProductType.setSelection(index)
                        }
                    }
            }
            for ((index, obj) in mYears.withIndex()) {
                if (obj != getString(R.string.select) && mOutstandings!!.year == obj.toInt()) {
                    mBinding.spnYear.setSelection(index)
                }
            }
            mBinding.edtNetReceivable.setText("${formatWithPrecision(mOutstandings!!.netReceivable)}")

            if(mOutstandings?.receivedAmount!!>BigDecimal.ZERO){
                setEditAction(false)
            }


        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.spnTax.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var product = CustomerProduct()
                if (parent != null && parent.selectedItem != null)
                    product = parent.selectedItem as CustomerProduct

                mtaxRuleBookCode = product.taxRuleBookCode

                if (product.multiInvoice.equals("Y")) {
                    editableStatus = true
                    mBinding.llVoucherno.visibility = View.VISIBLE
                    if (fromScreen == null)
                        fetchVoucherNumbers()
                } else {
                    editableStatus = false
                    mBinding.llVoucherno.visibility = View.GONE
                }
            }

        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                //todo: needtoremove this once proper edittext currency handler is written
                if (validateViews()) {
                   // editTextCurrencyAdd()
                    StoreInitialOutstandings()
                }
            }
        }
    }

    private fun editTextCurrencyAdd() {
        //this is to remove any currency added
        try {
            val text: String = mBinding.edtNetReceivable.text.toString().trim()
            if (text.isNotEmpty()) {
                mBinding.edtNetReceivable.setText("${currencyToDouble(text)}");
                //this is to re-append the currency
                val enteredText: Double = mBinding.edtNetReceivable.text.toString().toDouble()
                mBinding.edtNetReceivable.setText("${formatWithPrecision(enteredText)}")
            }
        } catch (e: Exception) {
            LogHelper.writeLog(e,e.message)
            mListener?.showAlertDialog(e.toString())
        }
    }


    private fun edtRemoveFocus(focus: Boolean = false) {
        mBinding.edtNetReceivable.isFocusable = focus
        mBinding.edtNetReceivable.isFocusableInTouchMode = true
    }

    private fun StoreInitialOutstandings() {
        mBinding.btnSave.isEnabled = false
        val saveOutstanding = SaveOutstanding()
        saveOutstanding.initialOutstandingID = outstandingID
        if (mBinding.spnOutstandingType.selectedItem != null) {
            val outstandingType = mBinding.spnOutstandingType.selectedItem as OutstandingType
            saveOutstanding.outstandingTypeCode = outstandingType.outstandingTypeCode
        }

        if (mBinding.spnYear.selectedItem != null) {
            val outstandingYear = mBinding.spnYear.selectedItem as String
            saveOutstanding.year = outstandingYear.toInt()
        }

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND) {
            if (mBinding.spnProductType.selectedItem != null) {
                val productType = mBinding.spnProductType.selectedItem as ProductTypes
                saveOutstanding.productCode = productType.productCode
            }
        }else{
            saveOutstanding.productCode = productCode
        }

        if (fromScreen != null) {

            saveOutstanding.voucherNo = voucherNo
        } else {
            if (mBinding.spnTax.selectedItem != null) {
                val outstandingTax = mBinding.spnTax.selectedItem as CustomerProduct
                saveOutstanding.productCode = outstandingTax.productCode
            }

            if (editableStatus)
                if (mBinding.spnVoucherNo.selectedItem != null) {
                    val voucherNo = mBinding.spnVoucherNo.selectedItem as OutstandingVoucherNo
                    saveOutstanding.voucherNo = voucherNo.voucherNo?.toInt()
                } else
                    saveOutstanding.voucherNo = 0
        }
saveOutstanding.netReceivable= currencyToDouble(mBinding.edtNetReceivable.text.toString())?.toDouble()?.toBigDecimal()
        //saveOutstanding.netReceivable = currencyToDouble(mBinding.edtNetReceivable.text.toString().trim())?.toLong()?.let { BigDecimal(it) }

        saveOutstanding.accountID = customerId?.toInt()

        APICall.saveOutStanding(saveOutstanding, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                Handler().postDelayed({
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    mListener?.popBackStack()
                }, 500)

            }

            override fun onFailure(message: String) {
                mBinding.btnSave.isEnabled = true
                mListener?.dismissDialog()
                mListener?.showSnackbarMsg(message)

            }

        })

    }


    private fun validateViews(): Boolean {
        edtRemoveFocus()
        val outstandingType = mBinding.spnOutstandingType.selectedItem as OutstandingType?
        if (outstandingType?.outstandingTypeCode == null || outstandingType.outstandingTypeCode == "-1") {
            mListener!!.showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.outstandings_type))
            mBinding.spnOutstandingType.requestFocus()
            return false
        }

        val outstandingYear = mBinding.spnYear.selectedItem as String
        if (outstandingYear == getString(R.string.select)) {
            mListener!!.showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.tax_year))
            mBinding.spnYear.requestFocus()
            return false
        }

        if (fromScreen == null) {
            val outstandingTax = mBinding.spnTax.selectedItem as CustomerProduct?
            if (outstandingTax?.productCode == null || outstandingTax.productCode == "-1") {
                mListener!!.showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.tax))
                mBinding.spnTax.requestFocus()
                return false
            }

            if (editableStatus) {
                val voucherNo = mBinding.spnVoucherNo.selectedItem as OutstandingVoucherNo?
                if (voucherNo?.taxSubType == null || voucherNo.taxSubType == getString(R.string.select)) {
                    mListener!!.showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.tax_sub_type))
                    mBinding.spnVoucherNo.requestFocus()
                    return false
                }
            }
        }


        if (mBinding.llProductType.isVisible) {
            val productType = mBinding.spnProductType.selectedItem as ProductTypes?
            if (productType?.product == getString(R.string.select)) {
                mListener!!.showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.product_type))
                mBinding.spnProductType.requestFocus()
                return false
            }
        }

        if (mBinding.edtNetReceivable.text != null && TextUtils.isEmpty(mBinding.edtNetReceivable.text.toString().trim { it <= ' ' })) {
            mListener!!.showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.net_receivable))
            return false
        }

        if (currencyToDouble(mBinding.edtNetReceivable.text.toString())?.toDouble()!! <= 0) {
            var doubleVal = getDecimalVal(resources.getString(R.string.msg_zero_amount))
            mListener!!.showAlertDialog(getTextWithPrecisionVal(resources.getString(R.string.msg_zero_amount), doubleVal))

            return false
        }

        return true
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun popBackStack()
        fun showSnackbarMsg(message: String)
        fun showAlertDialog(message: String)
        fun showProgressDialog(message: Int)
        fun showProgressDialog()
        fun dismissDialog()
        var screenMode: Constant.ScreenMode
    }
}