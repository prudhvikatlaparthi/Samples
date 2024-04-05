package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentPhoneEntryBinding
import com.sgs.citytax.model.COMCountryMaster
import com.sgs.citytax.model.ComComboStaticValues
import com.sgs.citytax.util.Constant

class PhoneEntryFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentPhoneEntryBinding
    private var mListener: Listener? = null
    private var mPrimaryKey: Int = 0
    private var fromScreen: Constant.QuickMenu? = null

    private var comComboStaticValues: MutableList<ComComboStaticValues>? = arrayListOf()
    private var mAccountPhone: AccountPhone? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else
                context as Listener
        } catch (e: Exception) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_phone_entry, container, true)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {

        //region getArguments
        arguments?.let {
            mAccountPhone = arguments?.getParcelable(Constant.KEY_PHONE)
            mPrimaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        //endregion

        setViews()
        bindSpinner()
        setListeners()
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                R.id.btnSave -> {
                    if (validateView()) {
                        val number = mBinding.etNumber.text.toString().trim()
                        //Number changed
                        if (mAccountPhone?.number != number && (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
                                        || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER) || fromScreen == Constant.QuickMenu.QUICK_MENU_SALES_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_CITIZEN) {
                            checkPhoneNumberExist()
                        } else {
                            saveAccountPhone(getPayload())
                        }
                    }
                }
            }
        }
    }

    private fun setViews() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {
        mBinding.spnPhoneType.isEnabled = action
        mBinding.spnTelephoneCode.isEnabled = action
        mBinding.etNumber.isEnabled = action
        mBinding.chkDefault.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_AccountPhones", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                comComboStaticValues = response.comboStaticValues
                if (comComboStaticValues != null && comComboStaticValues!!.isNotEmpty()) {
                    comComboStaticValues?.add(0, ComComboStaticValues("-1", getString(R.string.select)))
                    mBinding.spnPhoneType.adapter = ArrayAdapter<ComComboStaticValues>(activity!!, android.R.layout.simple_spinner_dropdown_item, comComboStaticValues!!)
                } else
                    mBinding.spnPhoneType.adapter = null

                if (response.countryMaster.isNotEmpty()) {
                    val countryCode: String? = "BFA"
                    val countries: MutableList<COMCountryMaster> = arrayListOf()
                    var index = -1
                    val telephonicCodes: ArrayList<Int> = arrayListOf()
                    for (country in response.countryMaster) {
                        country.telephoneCode?.let {
                            if (it > 0) {
                                countries.add(country)
                                telephonicCodes.add(it)
                                if (index <= -1 && countryCode == country.countryCode)
                                    index = countries.indexOf(country)
                            }
                        }
                    }
                    if (index <= -1) index = 0
                    if (telephonicCodes.size > 0) {
                        val telephonicCodeArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, telephonicCodes)
                        mBinding.spnTelephoneCode.adapter = telephonicCodeArrayAdapter

                        if (mAccountPhone?.telCode != null) {
                            mBinding.spnTelephoneCode.setSelection(telephonicCodes.indexOf(mAccountPhone?.telCode as Int))
                        } else {
                            mBinding.spnTelephoneCode.setSelection(index)
                        }
                    } else mBinding.spnTelephoneCode.adapter = null

                }

                bindData()
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnPhoneType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindData() {
        if (mAccountPhone != null) {
            mBinding.etNumber.setText(mAccountPhone!!.number)
            mBinding.chkDefault.isChecked = "Y" == mAccountPhone!!.default

            if (comComboStaticValues != null)
                for ((index, obj) in comComboStaticValues!!.withIndex()) {
                    if (mAccountPhone!!.phoneType == obj.comboValue) {
                        mBinding.spnPhoneType.setSelection(index)
                        break
                    }
                }
        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
    }

    private fun validateView(): Boolean {
        if (mBinding.etNumber.text.toString().trim().isEmpty()) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.number))
            return false
        }

        if (mBinding.spnPhoneType.selectedItem == null || "-1" == (mBinding.spnPhoneType.selectedItem as ComComboStaticValues).comboCode) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.phone_type))
            return false
        }
        return true
    }

    private fun getPayload(): AccountPhone {
        val accountPhone = AccountPhone()

        accountPhone.accountPhoneID = mAccountPhone?.accountPhoneID ?: 0
        accountPhone.default = if (mBinding.chkDefault.isChecked) "Y" else "N"
        accountPhone.phoneType = (mBinding.spnPhoneType.selectedItem as ComComboStaticValues).comboValue
        accountPhone.accountID = mAccountPhone?.accountID
        accountPhone.number = mBinding.etNumber.text.toString().trim()
        accountPhone.accountID = mPrimaryKey
        if (mBinding.spnTelephoneCode.selectedItem != null)
            accountPhone.telCode = mBinding.spnTelephoneCode.selectedItem.toString().toInt()

        return accountPhone
    }

    private fun checkPhoneNumberExist() {
        mListener?.showProgressDialog()

        val number = mBinding.etNumber.text.toString().trim()

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        filterColumn.columnName = "AccountTypeCode"
        filterColumn.columnValue = Constant.AccountTypeCode.CUS.name
        filterColumn.srchType = "equal"

        listFilterColumn.add(listFilterColumn.size, filterColumn)

        filterColumn = FilterColumn()
        filterColumn.columnName = "Number"
        filterColumn.columnValue = number
        filterColumn.srchType = "equal"

        listFilterColumn.add(listFilterColumn.size, filterColumn)

        searchFilter.filterColumns = listFilterColumn

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_AccountPhones"
        tableDetails.primaryKeyColumnName = "AccountPhoneID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "AND"
        tableDetails.sendCount = true

        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                if (response == 0)
                    saveAccountPhone(getPayload())
                else mListener?.showAlertDialog(getString(R.string.msg_nuber_exists))
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun saveAccountPhone(phone: AccountPhone) {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.insertAccountPhone(phone, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    val intent = Intent()
                    if (phone.default == "Y")
                        intent.putExtra("PHONE_NUMBER", phone.number)
                    else
                        intent.putExtra("PHONE_NUMBER", "")
                    targetFragment?.onActivityResult(Constant.REQUEST_CODE_ACCOUNT_PHONE, Activity.RESULT_OK, intent)
                    dismiss()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS && ObjectHolder.registerBusiness.accountID == 0 && ObjectHolder.registerBusiness.organizationID == 0) {
                mListener?.dismissDialog()
                mListener?.showToast(R.string.msg_record_save_success)
                ObjectHolder.registerBusiness.accountPhones.add(phone)
                targetFragment?.onActivityResult(Constant.REQUEST_CODE_ACCOUNT_PHONE, Activity.RESULT_OK, Intent())
                dismiss()
            }
        }*/
    }

    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun showToast(message: Int)
        fun showToast(message: String)
        var screenMode: Constant.ScreenMode

    }

}