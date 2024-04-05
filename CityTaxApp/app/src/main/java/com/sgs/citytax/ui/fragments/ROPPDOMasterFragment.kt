package com.sgs.citytax.ui.fragments

import android.app.Activity
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
import com.sgs.citytax.model.ROPListItem
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.ui.adapter.AddressAdapter
import com.sgs.citytax.ui.adapter.ROPPDOAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class ROPPDOMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var isPDO: Boolean = false
    private var isMultiple = false
    private var mTaskCode: String? = ""
    private var mTaxRuleBookCode: String? = ""

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            arguments?.getParcelable<TaskCode>(Constant.KEY_TASK_CODE).apply {
                this?.IsMultiple?.let {
                    isMultiple = it == 'Y' || it == ' '
                }
                this?.taskCode?.let {
                    mTaskCode = it
                }
            }
            mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            isPDO = arguments?.getBoolean(Constant.KEY_IS_PUBLIC_DOMAIN_OCCUPANCY)!!
            mTaxRuleBookCode = arguments?.getString(Constant.KEY_TAX_RULE_BOOK_CODE)!!
        }
        //endregion
        setViews()
        bindData()
        setListeners()
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mBinding.recyclerView.adapter = ROPPDOAdapter(this, mListener?.screenMode)
    }

    private fun bindData() {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
                && null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && ObjectHolder.registerBusiness.vuCrmAccounts?.accountId != 0) {
            val tableName = if (isPDO) "VU_CRM_PublicDomainOccupancy" else "VU_CRM_RightOfPlaces"
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails(tableName, ObjectHolder.registerBusiness.vuCrmAccounts?.accountId!!, ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId!!, mTaskCode, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    val roppdoAdapter = mBinding.recyclerView.adapter as ROPPDOAdapter
                    roppdoAdapter.clear()

                    if (response.ropList.isNotEmpty())
                        roppdoAdapter.update(response.ropList)
                    if (response.podList.isNotEmpty())
                        roppdoAdapter.update(response.podList)

                    // todo commented for onsite requirement
//                    if (!isMultiple && response.ropList.isNotEmpty()) {
//                        mBinding.fabAdd.visibility = View.GONE
//                    } else {
//                        mBinding.fabAdd.visibility = View.VISIBLE
//                    }
//                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                        mBinding.fabAdd.visibility = View.GONE
//                    }
                    if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                    }
                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)
                    (mBinding.recyclerView.adapter as ROPPDOAdapter).clear()

                    // todo commented for onsite requirement
//                    mBinding.fabAdd.visibility = View.VISIBLE
//                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                        mBinding.fabAdd.visibility = View.GONE
//                    }
                    if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                    }
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            if (mCode == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
                if (!ObjectHolder.registerBusiness.ropPdos.isNullOrEmpty())
                    (mBinding.recyclerView.adapter as ROPPDOAdapter).update(ObjectHolder.registerBusiness.ropPdos)
            } else {
            }
        }*/
    }

    override fun onResume() {
        super.onResume()
        bindData()
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = ROPPDOEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                bundle.putString(Constant.KEY_TASK_CODE, mTaskCode)
                bundle.putBoolean(Constant.KEY_IS_PUBLIC_DOMAIN_OCCUPANCY, isPDO)
                fragment.arguments = bundle
                //endregion
                mListener?.screenMode = Constant.ScreenMode.ADD
                fragment.setTargetFragment(this@ROPPDOMasterFragment, Constant.REQUEST_CODE_PUBLIC_DOMAIN_OCCUPANCY_LIST)

                mListener?.addFragment(fragment, true)
            }

        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    val fragment = ROPPDOEntryFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                    bundle.putBoolean(Constant.KEY_IS_PUBLIC_DOMAIN_OCCUPANCY, isPDO)
                    bundle.putString(Constant.KEY_TASK_CODE, mTaskCode)
                    bundle.putParcelable(Constant.KEY_PDO_ROP, obj as ROPListItem)
                    bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
                    fragment.arguments = bundle
                    //endregion

                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_RIGHT_OF_PLACES_LIST)

                    mListener?.addFragment(fragment, true)
                }
                R.id.txtDelete -> {
                    deleteROPorPDO(obj as ROPListItem)
                }
                else -> {

                }
            }
        }

    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && (requestCode == Constant.REQUEST_CODE_PUBLIC_DOMAIN_OCCUPANCY_LIST || requestCode == Constant.REQUEST_CODE_RIGHT_OF_PLACES_LIST)) {
            (mBinding.recyclerView.adapter as ROPPDOAdapter).clear()
            bindData()
        }
    }

    private fun deleteROPorPDO(listItem: ROPListItem) {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId) {
            val tableName = if (isPDO) "CRM_PublicDomainOccupancy" else "CRM_RightOfPlaces"
            val primaryKey = if (isPDO) listItem.publicDomainOccupancyID else listItem.rightOfPlaceID
            APICall.deleteAccountMappingData(ObjectHolder.registerBusiness.vuCrmAccounts?.accountId!!, tableName, primaryKey?.toString(), object : ConnectionCallBack<Boolean> {
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

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        var screenMode: Constant.ScreenMode
    }
}