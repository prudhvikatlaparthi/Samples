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
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.adapter.CorporateTurnOverAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_NUMBER_OF_CORPORATE_TURNOVER
import com.sgs.citytax.util.Constant.REQUEST_CODE_CORPORATE_TURNOVER
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class CorporateTurnOverMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var isMultiple = false
    private var corporateTurnOvers: ArrayList<VUCRMCorporateTurnover> = arrayListOf()
    private var mTaskCode: String? = ""
    private var mProductBilling: String? = ""
    private var mProduct: VuInvProducts? = null
    private var mShowProportionalDutyOnCP: Boolean = false
    private var mTaxRuleBookCode: String? = ""

    private var mNoOfTurnOvers: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

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
            arguments?.getParcelable<VuInvProducts>(Constant.KEY_PRODUCT_DETAILS)?.let {
                mProduct = it
            }
            arguments?.getBoolean(Constant.KEY_SHOW_PROPORTIONAL_DUTY)?.let {
                mShowProportionalDutyOnCP = it
            }
            arguments?.getString(Constant.KEY_PRODUCT_BILLING_CYCLE)?.let {
                mProductBilling = it
            }
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mTaxRuleBookCode = arguments?.getString(Constant.KEY_TAX_RULE_BOOK_CODE)!!

        }

        bindData()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CORPORATE_TURNOVER) {
                bindData()
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    showCorporateEntryScreen(obj as VUCRMCorporateTurnover?)
                }
                R.id.txtDelete -> {
                    deleteCorporateTurnOver(obj as VUCRMCorporateTurnover)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    private fun deleteCorporateTurnOver(crmCorporateTurnover: VUCRMCorporateTurnover?) {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0) {
            mListener?.showProgressDialog()
            APICall.deleteAccountMappingData(ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                    ?: 0, "CRM_CorporateTurnover",
                    crmCorporateTurnover?.turnoverID.toString(), object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    bindData()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }

    private fun showCorporateEntryScreen(crmCorporateTurnover: VUCRMCorporateTurnover?) {
        val corporateTurnOverFragment = CorporateTurnOverFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_CORPORATE_TURNOVER, crmCorporateTurnover)
        bundle.putParcelable(Constant.KEY_PRODUCT_DETAILS, mProduct)
        bundle.putInt(KEY_NUMBER_OF_CORPORATE_TURNOVER, mNoOfTurnOvers)
        bundle.putString(Constant.KEY_TASK_CODE, mTaskCode)
        bundle.putString(Constant.KEY_PRODUCT_BILLING_CYCLE, mProductBilling)
        bundle.putBoolean(Constant.KEY_SHOW_PROPORTIONAL_DUTY, mShowProportionalDutyOnCP)
        if (crmCorporateTurnover == null)
        {
            mListener?.screenMode = Constant.ScreenMode.ADD
        }
        else
        {
            bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
        }

        corporateTurnOverFragment.arguments = bundle
        corporateTurnOverFragment.setTargetFragment(this, REQUEST_CODE_CORPORATE_TURNOVER)
        mListener?.showToolbarBackButton(R.string.corporate_turnover)
        mListener?.addFragment(corporateTurnOverFragment, true)
    }

    fun bindData() {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
                && null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && ObjectHolder.registerBusiness.vuCrmAccounts?.accountId != 0) {
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails("VU_CRM_CorporateTurnover", ObjectHolder.registerBusiness.vuCrmAccounts?.accountId!!, ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId!!, mTaskCode, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    if (response.vuCorporateTurnOver.isNotEmpty()) {
                        corporateTurnOvers = response.vuCorporateTurnOver as ArrayList<VUCRMCorporateTurnover>
                        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        mBinding.recyclerView.adapter = CorporateTurnOverAdapter(mShowProportionalDutyOnCP, corporateTurnOvers, this@CorporateTurnOverMasterFragment, mListener?.screenMode, mProductBilling)
                    } else {
                        corporateTurnOvers = arrayListOf()
                        mBinding.recyclerView.adapter = null
                    }
                    mNoOfTurnOvers = corporateTurnOvers.size

//                    if (!isMultiple && corporateTurnOvers.size > 0)
//                        mBinding.fabAdd.visibility = View.GONE
//                    else
//                        mBinding.fabAdd.visibility = View.VISIBLE
//
//                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                        mBinding.fabAdd.visibility = View.GONE
//                    }
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

                    mNoOfTurnOvers = corporateTurnOvers.size
                    // todo commented for onsite requirement
//                    mBinding.fabAdd.visibility = View.VISIBLE
//                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                        mBinding.fabAdd.visibility = View.GONE
//                    }
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                    }
                }
            })
        }/* else {
mListener?.showAlertDialog("In complete flow")
when (fromScreen) {
Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
if (ObjectHolder.registerBusiness.crmCorporateTurnovers.isNotEmpty()) {
mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
mBinding.recyclerView.adapter = CorporateTurnOverAdapter(ObjectHolder.registerBusiness.crmCorporateTurnovers, this)
}
}
else -> {

}¬
}
}*/

    }

    fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                showCorporateEntryScreen(null)
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
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode

    }

}