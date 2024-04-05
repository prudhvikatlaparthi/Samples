package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentEmailEntryBinding
import com.sgs.citytax.model.CRMAccountEmails
import com.sgs.citytax.model.ComComboStaticValues
import com.sgs.citytax.util.Constant

class EmailEntryFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var mListener: Listener? = null
    private lateinit var mBinding: FragmentEmailEntryBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var mPrimaryKey: Int = 0

    private var accountEmail: CRMAccountEmails? = null
    private var emailTypes: MutableList<ComComboStaticValues>? = arrayListOf()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else
                context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_email_entry, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {

        //region GetArguments
        arguments?.let {
            accountEmail = arguments?.getParcelable(Constant.KEY_ACCOUNT_EMAILS)
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
                    if (validateViews())
                        saveEmail(getPayload())
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
        mBinding.spnEmailType.isEnabled = action
        mBinding.edtEmailAddress.isEnabled = action
        mBinding.chkDefault.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_AccountEmails", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                emailTypes = response.comboStaticValues
                if (emailTypes != null && emailTypes!!.isNotEmpty()) {
                    emailTypes?.add(0, ComComboStaticValues("-1", getString(R.string.select)))
                    val emailTypeAdapter = ArrayAdapter<ComComboStaticValues>(activity!!, android.R.layout.simple_spinner_dropdown_item, emailTypes!!)
                    mBinding.spnEmailType.adapter = emailTypeAdapter
                } else
                    mBinding.spnEmailType.adapter = null

                bindData()

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnEmailType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindData() {
        if (accountEmail != null) {
            mBinding.edtEmailAddress.setText(accountEmail!!.email)
            mBinding.chkDefault.isChecked = "Y" == accountEmail!!.default
            if (emailTypes != null)
                for ((index, obj) in emailTypes!!.withIndex()) {
                    if (accountEmail!!.EmailType == obj.comboValue) {
                        mBinding.spnEmailType.setSelection(index)
                        break
                    }
                }
        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
    }

    private fun validateViews(): Boolean {
        mListener?.hideKeyBoard()

        if (mBinding.edtEmailAddress.text.toString().trim().isEmpty()) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.email))
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mBinding.edtEmailAddress.text.toString().trim()).matches()) {
            mListener?.showToast(getString(R.string.msg_provide_valid) + " " + getString(R.string.email))
            return false
        }

        if (mBinding.spnEmailType.selectedItem == null || "-1" == (mBinding.spnEmailType.selectedItem as ComComboStaticValues).comboCode) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.email_type))
            return false
        }

        return true
    }

    private fun getPayload(): CRMAccountEmails {
        val email = CRMAccountEmails()
        accountEmail?.let {
            email.accountEmailID = it.accountEmailID
            email.default = it.default
        }
        email.default = if (mBinding.chkDefault.isChecked) "Y" else "N"
        email.email = mBinding.edtEmailAddress.text.toString().trim()
        email.EmailType = (mBinding.spnEmailType.selectedItem as ComComboStaticValues).comboValue
        email.accountId = mPrimaryKey

        return email
    }

    private fun saveEmail(email: CRMAccountEmails) {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.insertAccountEmail(email, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    val intent = Intent()
                    if (email.default == "Y")
                        intent.putExtra("EMAIL", email.email)
                    else
                        intent.putExtra("EMAIL", "")
                    targetFragment?.onActivityResult(Constant.REQUEST_CODE_ACCOUNT_EMAIL, Activity.RESULT_OK, intent)
                    dismiss()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {

                ObjectHolder.registerBusiness.accountEmails.add(email)
                mListener?.dismissDialog()
                mListener?.showToast(R.string.msg_record_save_success)
                targetFragment?.onActivityResult(Constant.REQUEST_CODE_ACCOUNT_EMAIL, Activity.RESULT_OK, Intent())
                dismiss()
            }
        }*/
    }


    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String)
        fun showProgressDialog()
        fun showToast(message: Int)
        fun showToast(message: String)
        var screenMode: Constant.ScreenMode

    }
}