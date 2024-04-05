package com.sgs.citytax.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.CRMPropertyRent
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.ui.adapter.RentalDetailsAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.REQUEST_CODE_RENTAL_DETAILS
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class RentalMasterFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var crmPropertyRents: List<CRMPropertyRent> = arrayListOf()
    private var isMultiple = false
    private var mTaskCode: String? = ""
    private var mTaxRuleBookCode: String? = ""
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            arguments?.getParcelable<TaskCode>(Constant.KEY_TASK_CODE).apply {
                this?.IsMultiple?.let {
                    isMultiple = it == 'Y' || it == ' '
                }
                this?.taskCode?.let {
                    mTaskCode = it
                }
            }
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mTaxRuleBookCode = arguments?.getString(Constant.KEY_TAX_RULE_BOOK_CODE)!!
        }
        bindData()
        initialiseListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_RENTAL_DETAILS) {
                bindData()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.txtEdit -> {
                showRentalEntryScreen(obj as CRMPropertyRent?)
            }
            R.id.txtDelete -> {
                deletePropertyRents(obj as CRMPropertyRent)
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun deletePropertyRents(crmPropertyRent: CRMPropertyRent?) {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0) {
            mListener?.showProgressDialog()
            APICall.deleteAccountMappingData(ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                    ?: 0, "CRM_PropertyRents", crmPropertyRent?.propertyRentID.toString()
                    , object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    bindData()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }

    private fun showRentalEntryScreen(crmPropertyRent: CRMPropertyRent?) {
        val rentalEntryFragment = RentalEntryFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_RENTAL_DETAILS, crmPropertyRent)
        if (crmPropertyRent == null)
        {
            mListener?.screenMode = Constant.ScreenMode.ADD
        }
        else{
            bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
        }
        rentalEntryFragment.arguments = bundle
        mListener?.showToolbarBackButton(R.string.title_rental_details)
        rentalEntryFragment.setTargetFragment(this, Constant.REQUEST_CODE_RENTAL_DETAILS)
        mListener?.addFragment(rentalEntryFragment, true)
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    fun bindData() {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && ObjectHolder.registerBusiness.vuCrmAccounts?.accountId != 0
                && null != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId) {
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails("VU_CRM_PropertyRents", ObjectHolder.registerBusiness.vuCrmAccounts?.accountId!!, ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId!!, mTaskCode, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    if (response.propertyRents.isNotEmpty()) {
                        crmPropertyRents = response.propertyRents
                        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        mBinding.recyclerView.adapter = RentalDetailsAdapter(crmPropertyRents as ArrayList<CRMPropertyRent>, this@RentalMasterFragment, mListener?.screenMode)
                    } else {
                        crmPropertyRents = listOf()
                        mBinding.recyclerView.adapter = null
                    }
                    /*if (!isMultiple && crmPropertyRents.isNotEmpty())
                        mBinding.fabAdd.visibility = View.GONE
                    else
                        mBinding.fabAdd.visibility = View.VISIBLE

                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
                        mBinding.fabAdd.visibility = View.GONE
                    }*/
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                    }
                    mListener?.dismissDialog()

                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mBinding.recyclerView.adapter = null
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)
                    /*mBinding.fabAdd.visibility = View.VISIBLE

                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
                        mBinding.fabAdd.visibility = View.GONE
                    }*/
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                    }
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            when (fromScreen) {
                Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
                    if (ObjectHolder.registerBusiness.crmPropertyRents.isNotEmpty()) {
                        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        mBinding.recyclerView.adapter = RentalDetailsAdapter(ObjectHolder.registerBusiness.crmPropertyRents, this)
                    }
                    mListener?.dismissDialog()
                }
                else -> {

                }
            }
        }*/
    }

    private fun initialiseListeners() {
        mBinding.fabAdd.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                showRentalEntryScreen(null)
            }

        })
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, null)
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode

    }
}
