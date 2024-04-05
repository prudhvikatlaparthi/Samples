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
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.model.VUCRMPropertyOwnership
import com.sgs.citytax.ui.adapter.PropertyOwnershipAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class PropertyOwnershipMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var isMultiple: Boolean = false
    private var mTaskCode: String? = ""

    private var propertyOwners: ArrayList<VUCRMPropertyOwnership> = arrayListOf()
    private var mTaxRuleBookCode: String? = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else
                context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implemet Listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
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
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_PROPERTY_OWNERSHIP_LIST) {
                bindData()
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    showPropertyOwnerScreen(obj as VUCRMPropertyOwnership?)
                }
                R.id.txtDelete -> {
                    deletePropertyOwnerShip(obj as VUCRMPropertyOwnership)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun showPropertyOwnerScreen(crmPropertyOwner: VUCRMPropertyOwnership?) {
        val propertyOwnerFragment = PropertyOwnerFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_PROPERTY_OWNER, crmPropertyOwner)
        if (crmPropertyOwner == null)
        {
            mListener?.screenMode = Constant.ScreenMode.ADD
        }
        else{
            bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
        }
        propertyOwnerFragment.arguments = bundle
        mListener?.showToolbarBackButton(R.string.property_ownership)
        propertyOwnerFragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_OWNERSHIP_LIST)
        mListener?.addFragment(propertyOwnerFragment, true)
    }

    fun bindData() {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
            && null != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
        ) {
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails("VU_CRM_PropertyOnwerships",
                ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                    ?: 0,
                ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId!!,
                mTaskCode,
                object : ConnectionCallBack<DataResponse> {
                    override fun onSuccess(response: DataResponse) {
                        if (response.propertyOwnership.isNotEmpty()) {
                            propertyOwners =
                                response.propertyOwnership as ArrayList<VUCRMPropertyOwnership>
                            mBinding.recyclerView.addItemDecoration(
                                DividerItemDecoration(
                                    context,
                                    LinearLayoutManager.VERTICAL
                                )
                            )
                            mBinding.recyclerView.adapter = PropertyOwnershipAdapter(
                                propertyOwners,
                                this@PropertyOwnershipMasterFragment,
                                mListener?.screenMode
                            )
                        } else {
                            propertyOwners = arrayListOf()
                            mBinding.recyclerView.adapter = null
                        }


//                        if (!isMultiple && propertyOwners.size > 0)
//                            mBinding.fabAdd.visibility = View.GONE
//                        else
//                            mBinding.fabAdd.visibility = View.VISIBLE
//
//
//                        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                            mBinding.fabAdd.visibility = View.GONE
//                        }
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

//                        mBinding.fabAdd.visibility = View.VISIBLE
//
//                        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                            mBinding.fabAdd.visibility = View.GONE
//                        }
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
if (ObjectHolder.registerBusiness.propertyOwnerships.isNotEmpty()) {
mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
mBinding.recyclerView.adapter = PropertyOwnershipAdapter(ObjectHolder.registerBusiness.propertyOwnerships, this)
}
}
else -> {

}
}
}*/
    }

    private fun deletePropertyOwnerShip(listItem: VUCRMPropertyOwnership) {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId) {
            APICall.deleteAccountMappingData(
                ObjectHolder.registerBusiness.vuCrmAccounts?.accountId!!,
                "CRM_PropertyOwnership",
                listItem.propertyOwnershipID?.toString(),
                object : ConnectionCallBack<Boolean> {
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

    fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showPropertyOwnerScreen(null)
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