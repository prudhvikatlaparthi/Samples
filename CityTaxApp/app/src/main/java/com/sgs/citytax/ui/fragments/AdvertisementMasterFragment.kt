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
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.model.VUCRMAdvertisements
import com.sgs.citytax.model.VuTax
import com.sgs.citytax.ui.adapter.AdvertisementAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class AdvertisementMasterFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null

    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var isMultiple = false
    private var advertisements: ArrayList<VUCRMAdvertisements> = arrayListOf()
    private var mTaskCode: String? = ""
    private var mTaxRuleBookCode:String?=""

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
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mTaxRuleBookCode = arguments?.getString(Constant.KEY_TAX_RULE_BOOK_CODE)!!
        }
        bindData()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_ADVERTISEMENTS) {
                bindData()
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun bindData() {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
                && null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && ObjectHolder.registerBusiness.vuCrmAccounts?.accountId != 0) {
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails("VU_CRM_Advertisements", ObjectHolder.registerBusiness.vuCrmAccounts?.accountId!!, ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId!!, mTaskCode, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    mListener?.dismissDialog()
                    if (response.vuCrmAdvertisements.isNotEmpty()) {
                        advertisements = response.vuCrmAdvertisements as ArrayList<VUCRMAdvertisements>
                        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        mBinding.recyclerView.adapter = AdvertisementAdapter(advertisements, this@AdvertisementMasterFragment, mListener?.screenMode)
                    } else {
                        advertisements = arrayListOf()
                        mBinding.recyclerView.adapter = null
                    }

                    /*if (!isMultiple && advertisements.size > 0)
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
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mBinding.recyclerView.adapter = null
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)
                   /* mBinding.fabAdd.visibility = View.VISIBLE

                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
                        mBinding.fabAdd.visibility = View.GONE
                    }*/
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                showAdvertisementEntryScreen(null)
            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.txtEdit -> {
                    showAdvertisementEntryScreen(obj as VUCRMAdvertisements)
                }
                R.id.txtDelete -> {
                    deleteAdvertisement(obj as VUCRMAdvertisements)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }


    private fun showAdvertisementEntryScreen(advertisement: VUCRMAdvertisements?) {
        val advertisementEntryFragment = AdvertisementEntryFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_ADVERTISEMENTS, advertisement)
        bundle.putString(Constant.KEY_TASK_CODE, mTaskCode)
        if(advertisement==null){
            mListener?.screenMode=Constant.ScreenMode.ADD
        }else
        {
            bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
        }
        advertisementEntryFragment.arguments = bundle
        advertisementEntryFragment.setTargetFragment(this, Constant.REQUEST_CODE_ADVERTISEMENTS)
        mListener?.showToolbarBackButton(R.string.title_advertisements)
        mListener?.addFragment(advertisementEntryFragment, true)
    }

    private fun deleteAdvertisement(vuCrmAdvertisements: VUCRMAdvertisements?) {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0) {
            if (vuCrmAdvertisements?.allowDelete == "Y") {
                mListener?.showProgressDialog()
                APICall.deleteAccountMappingData(ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                        ?: 0, "CRM_Advertisements",
                        vuCrmAdvertisements.advertisementId.toString(), object : ConnectionCallBack<Boolean> {
                    override fun onSuccess(response: Boolean) {
                        mListener?.dismissDialog()
                        bindData()
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        mListener?.showAlertDialog(message)
                    }
                })
            } else mListener?.showAlertDialog("You cannot delete this record")
        }
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
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