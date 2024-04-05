package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.CheckCurrentDue
import com.sgs.citytax.api.payload.GetEstimatedTaxForProduct
import com.sgs.citytax.api.payload.GetTaxableMatterColumnData
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentRentalEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.*
import java.util.*

class RentalEntryFragment : BaseFragment(), View.OnClickListener {
    private var mListener: Listener? = null
    private lateinit var mBinding: FragmentRentalEntryBinding
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER

    private var mCrmPropertyRent: CRMPropertyRent? = null
    private var documents: List<COMDocumentReference>? = null
    private var crmRentTypes: MutableList<CRMPropertyRentTypes>? = null
    private var mTaskCode: String? = ""
    private var mTaxRuleBookCode: String? = ""


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rental_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {

        arguments?.let {
            mCrmPropertyRent = arguments?.getParcelable(Constant.KEY_RENTAL_DETAILS)
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mTaskCode = it.getString(Constant.KEY_TASK_CODE)
            mTaxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)

        }

        setViews()
        bindSpinner()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.REQUEST_CODE_DOCUMENTS_LIST -> {
                    documents = data?.getParcelableArrayListExtra<COMDocumentReference>("arrayList") as ArrayList<COMDocumentReference>
                }
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                R.id.btnSave -> {
                    mListener?.hideKeyBoard()
                    if (validateView()) {
                        mListener?.showAlertDialog(R.string.are_you_sure_you_have_entered_all_valid_information,
                                R.string.yes,
                                View.OnClickListener {
                                    val dialog = (it as Button).tag as AlertDialog
                                    dialog.dismiss()
                                    saveRentalDetails(getPayload(), null)
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

                R.id.txtDocuments -> {
                    val fragment = DocumentsMasterFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_RENTAL_TAX)
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, mCrmPropertyRent?.propertyRentID
                            ?: 0)
                    fragment.arguments = bundle
                    //endregion

                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)

                    mListener?.showToolbarBackButton(R.string.documents)
                    mListener?.addFragment(fragment, true)
                }
                R.id.btnGet -> {
                    fetchTaxableMatter()
                }
                else -> {

                }
            }
        }
    }

    private fun fetchTaxableMatter() {
        val getTaxableMatterColumnData = GetTaxableMatterColumnData()
        getTaxableMatterColumnData.taskCode = mTaskCode
        mListener?.showProgressDialog()
        APICall.getTaxableMatterColumnData(getTaxableMatterColumnData, object : ConnectionCallBack<List<DataTaxableMatter>> {
            override fun onSuccess(response: List<DataTaxableMatter>) {

                val list: java.util.ArrayList<DataTaxableMatter> = arrayListOf()
                for (it in response) {
                    val taxableMatter = DataTaxableMatter()
                    taxableMatter.taxableMatterColumnName = it.taxableMatterColumnName
                    /*if ("LoadCapacity" == it.taxableMatterColumnName) {
                        if (!TextUtils.isEmpty(mBinding.edtLoadCapacity.text?.toString()?.trim()))
                            taxableMatter.taxableMatter = mBinding.edtLoadCapacity.text.toString().trim()
                    }
                    if ("NoOfVehicle" == it.taxableMatterColumnName) {
                        taxableMatter.taxableMatter = "1"
                    }
                    if ("SeatsNumber" == it.taxableMatterColumnName) {
                        if (!TextUtils.isEmpty(mBinding.edtSeatNumber.text?.toString()?.trim()))
                            taxableMatter.taxableMatter = mBinding.edtSeatNumber.text.toString().trim()
                    }*/
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
        if (!TextUtils.isEmpty(mBinding.edtAgreementDate.text?.toString()?.trim()))
            getEstimatedTaxForProduct.startDate = serverFormatDate(mBinding.edtAgreementDate.text.toString().trim())
        mListener?.showProgressDialog()
        APICall.getEstimatedTaxForProduct(getEstimatedTaxForProduct, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mBinding.edtEstimatedAmountForProduct.setText(formatNumber(response))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.edtEstimatedAmountForProduct.setText("")
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    fun setViews() {
        mBinding.edtAgreementDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtAgreementDate.setDisplayDateFormat(displayDateFormat)

        mBinding.txtDocuments.visibility = GONE
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {
        mBinding.spnRentType.isEnabled = action
        mBinding.edtRentalAmount.isEnabled = action
        mBinding.edtAgreementNo.isEnabled = action
        mBinding.edtNoOfMonths.isEnabled = action
        mBinding.edtAgreementDate.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.edtTaxableRate.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.txtDocuments.isEnabled = action
        mBinding.chkActive.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }

    private fun setEditActionForSave(action: Boolean) {
        mBinding.spnRentType.isEnabled = action
        mBinding.edtRentalAmount.isEnabled = action
        mBinding.edtAgreementNo.isEnabled = action
        mBinding.edtNoOfMonths.isEnabled = action
        mBinding.edtAgreementDate.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.edtTaxableRate.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.txtDocuments.isEnabled = action
        mBinding.chkActive.isEnabled = true
        mBinding.btnSave.visibility = View.VISIBLE
    }


    fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_PropertyRents", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()
                crmRentTypes = response.rentTypes
                if (response.rentTypes.isNotEmpty() && response.rentTypes.size > 0) {
                    crmRentTypes?.add(0, CRMPropertyRentTypes(getString(R.string.select), "", -1))
                    val agentTypeAdapter = ArrayAdapter<CRMPropertyRentTypes>(activity!!.applicationContext, android.R.layout.simple_list_item_1, crmRentTypes!!)
                    mBinding.spnRentType.adapter = agentTypeAdapter
                } else mBinding.spnRentType.adapter = null

                bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)

            }
        })
    }

    fun bindData() {
        if (mCrmPropertyRent != null) {

            if (crmRentTypes != null) {
                for ((index, obj) in crmRentTypes!!.withIndex()) {
                    if (mCrmPropertyRent!!.rentType!!.contentEquals(obj.rentType!!)) {
                        mBinding.spnRentType.setSelection(index)
                    }
                }
            }
            if (mCrmPropertyRent!!.rentAmount != null)
                mBinding.edtRentalAmount.setText(mCrmPropertyRent!!.rentAmount.toString())
            else mBinding.edtRentalAmount.setText("0.0")

            mCrmPropertyRent?.estimatedTax?.let {
                mBinding.edtEstimatedAmountForProduct.setText("$it")
            }

            if (!mCrmPropertyRent!!.agreementNo.isNullOrEmpty())
                mBinding.edtAgreementNo.setText(mCrmPropertyRent!!.agreementNo.toString())
            else mBinding.edtAgreementNo.setText("")

            if (mCrmPropertyRent!!.noOfMonths != null)
                mBinding.edtNoOfMonths.setText(mCrmPropertyRent!!.noOfMonths.toString())
            else
                mBinding.edtNoOfMonths.setText("0")

            if (!mCrmPropertyRent!!.agreementDate.isNullOrEmpty())
                mBinding.edtAgreementDate.setText(displayFormatDate(mCrmPropertyRent!!.agreementDate))
            else
                mBinding.edtAgreementDate.setText("")

            if (!mCrmPropertyRent!!.description.isNullOrEmpty())
                mBinding.edtDescription.setText(mCrmPropertyRent!!.description)
            else
                mBinding.edtDescription.setText("")

            if (mCrmPropertyRent!!.taxableRate != null)
                mBinding.edtTaxableRate.setText(mCrmPropertyRent!!.taxableRate.toString())
            else
                mBinding.edtTaxableRate.setText("0")

            mBinding.chkActive.isChecked = mCrmPropertyRent!!.active.equals("Y")

            mBinding.txtDocuments.visibility = VISIBLE
        }
        getInvoiceCount4Tax()
    }


    fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.btnGet.setOnClickListener(this)
        mBinding.txtDocuments.setOnClickListener(this)
    }

    fun validateView(): Boolean {
        if (TextUtils.isEmpty(mBinding.edtRentalAmount.text.toString().trim())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + getString(R.string.rent_amount))
            mBinding.edtRentalAmount.requestFocus()
            return false
        }
        if (mBinding.edtRentalAmount.text.toString().trim().toDouble() <= 0.0) {
            mListener?.showSnackbarMsg(getString(R.string.msg_amount_greater_than_zero))
            mBinding.edtRentalAmount.requestFocus()
            return false
        }

        if (mBinding.spnRentType.selectedItem == null || -1 == (mBinding.spnRentType.selectedItem as CRMPropertyRentTypes).rentTypeId) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.rent_type))
            return false
        }

        return true
    }

    private fun getPayload(): CRMPropertyRent {
        mBinding.btnSave.isEnabled = false

        var propertyRents = CRMPropertyRent()
        if (mCrmPropertyRent != null && mCrmPropertyRent!!.propertyRentID != 0) {
            propertyRents = mCrmPropertyRent!!
        }

        propertyRents.accountID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId

        if (mBinding.spnRentType.selectedItem != null) {
            propertyRents.rentTypeID = (mBinding.spnRentType.selectedItem as CRMPropertyRentTypes).rentTypeId
            propertyRents.rentType = (mBinding.spnRentType.selectedItem as CRMPropertyRentTypes).rentType
        }

        if (!TextUtils.isEmpty(mBinding.edtRentalAmount.text.toString().trim())) {
            propertyRents.rentAmount = mBinding.edtRentalAmount.text.toString().trim().toDouble()
        }

        if (!TextUtils.isEmpty(mBinding.edtAgreementNo.text.toString().trim())) {
            propertyRents.agreementNo = mBinding.edtAgreementNo.text.toString().trim()
        } else
            propertyRents.agreementNo = ""
        if (!TextUtils.isEmpty(mBinding.edtNoOfMonths.text.toString().trim())) {
            propertyRents.noOfMonths = mBinding.edtNoOfMonths.text.toString().trim().toInt()
        }
        if (!TextUtils.isEmpty(mBinding.edtAgreementDate.text.toString().trim())) {
            propertyRents.agreementDate = serverFormatDate(mBinding.edtAgreementDate.text.toString().trim())
        } else
            propertyRents.agreementDate = ""
        if (!TextUtils.isEmpty(mBinding.edtDescription.text.toString().trim())) {
            propertyRents.description = mBinding.edtDescription.text.toString().trim()
        } else
            propertyRents.description = ""
        if (!TextUtils.isEmpty(mBinding.edtTaxableRate.text.toString().trim())) {
            propertyRents.taxableRate = mBinding.edtTaxableRate.text.toString().trim().toInt()
        } else
            propertyRents.taxableRate = 0

        if (mBinding.chkActive.isChecked) {
            propertyRents.active = "Y"
        } else propertyRents.active = "N"

        return propertyRents
    }

    private fun saveRentalDetails(propertyRent: CRMPropertyRent, view: View?) {
        if (0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId) {
            mListener?.showProgressDialog()
            APICall.insertPropertyRents(propertyRent, documents, object : ConnectionCallBack<Double> {
                override fun onSuccess(response: Double) {
                    mListener?.dismissDialog()
                    if (propertyRent.propertyRentID != 0)
                        mListener?.showToast(R.string.msg_record_update_success)
                    else
                        mListener?.showToast(R.string.msg_record_save_success)

                    mBinding.txtDocuments.visibility = VISIBLE


                    if (mCrmPropertyRent == null)
                        mCrmPropertyRent = propertyRent
                    mCrmPropertyRent?.propertyRentID = response.toInt()

                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    }, 750)
                    mBinding.btnSave.isEnabled = true
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    mBinding.txtDocuments.visibility = GONE
                    mBinding.btnSave.isEnabled = true
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            *//*if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {

                ObjectHolder.registerBusiness.crmPropertyRents.add(propertyRents)
                mListener?.dismissDialog()
                mListener?.showToast(R.string.msg_record_save_success)
                targetFragment?.onActivityResult(Constant.REQUEST_CODE_RENTAL_DETAILS, Activity.RESULT_OK, Intent())
                Handler().postDelayed(Runnable {
                    mListener?.popBackStack()
                }, 300)
            }*//*
        }*/
    }
    private fun getInvoiceCount4Tax() {

        val currentDue = CheckCurrentDue()
        currentDue.accountId = mCrmPropertyRent?.accountID
        currentDue.vchrno  = mCrmPropertyRent?.propertyID
        currentDue.taxRuleBookCode  = mTaxRuleBookCode
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response>0)
                {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }
                    else {
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


    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun showProgressDialog()
        fun showSnackbarMsg(message: String)
        fun dismissDialog()
        fun popBackStack()
        fun showToast(message: Int)
        fun hideKeyBoard()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener)
        var screenMode: Constant.ScreenMode

    }

}