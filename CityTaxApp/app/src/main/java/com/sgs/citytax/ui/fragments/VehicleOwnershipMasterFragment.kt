package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.SingleLineTransformationMethod
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
import com.sgs.citytax.model.VUADMVehicleOwnership
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.adapter.VehicleOwnershipAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Event
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class VehicleOwnershipMasterFragment : BaseFragment(), IClickListener{

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var isMultiple = false
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mTaskCode: String? = ""
    private var mResultCode: Int = 1876
    var event: Event? = null
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
        mBinding.fabSearch.visibility=View.VISIBLE
        mBinding.recyclerView.adapter = VehicleOwnershipAdapter(this, mListener?.screenMode)
    }

    private fun bindData() {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                && null != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId) {
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails("VU_ADM_VehicleOwnership", ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                    ?: 0, ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId!!, mTaskCode, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    (mBinding.recyclerView.adapter as VehicleOwnershipAdapter).clear()
                    if (response.vehicleOwnerships.isNotEmpty()) {
                        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        (mBinding.recyclerView.adapter as VehicleOwnershipAdapter).update(response.vehicleOwnerships)
                    }
                    mListener?.dismissDialog()
//                    if (!isMultiple && response.vehicleOwnerships.isNotEmpty()) {
//                        mBinding.fabAdd.visibility = View.GONE
//                        mBinding.fabSearch.visibility = View.GONE
//                    } else {
//                        mBinding.fabAdd.visibility = View.VISIBLE
//                        mBinding.fabSearch.visibility = View.VISIBLE
//                    }
//
//                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                        mBinding.fabAdd.visibility = View.GONE
//                        mBinding.fabSearch.visibility = View.GONE
//                    }
                    if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                        mBinding.fabSearch.visibility = View.GONE
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    (mBinding.recyclerView.adapter as VehicleOwnershipAdapter).clear()
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)

//                    mBinding.fabAdd.visibility = View.VISIBLE
//                    mBinding.fabSearch.visibility = View.VISIBLE
//
//                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                        mBinding.fabAdd.visibility = View.GONE
//                        mBinding.fabSearch.visibility = View.GONE
//                    }
                    if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                    }
                }
            })
        }

    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                mListener?.screenMode = Constant.ScreenMode.ADD
                showVehicleOwnerEntryFragment(null)
            }

        })
        mBinding.fabSearch.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                mListener?.screenMode = Constant.ScreenMode.ADD
                searchExistingVehicleOwnerEntryFragment(null)
            }
        })
    }

    private fun searchExistingVehicleOwnerEntryFragment(vuadmVehicleOwnership: VUADMVehicleOwnership?) {
        if (vuadmVehicleOwnership == null) {
            event = Event.instance
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP_SEARCH)
            startActivityForResult(intent, Constant.REQUEST_CODE_VEHICLE_DETAILS)
        } else {
            vehicleOwnershipEntryFragment(vuadmVehicleOwnership, vuadmVehicleOwnership.vehicleSycotaxID)
        }
    }
    private fun showVehicleOwnerEntryFragment(vuadmVehicleOwnership: VUADMVehicleOwnership?) {
        if (vuadmVehicleOwnership == null) {
            event = Event.instance
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP)
            startActivityForResult(intent, mResultCode)
        } else {
            vehicleOwnershipEntryFragment(vuadmVehicleOwnership, vuadmVehicleOwnership.vehicleSycotaxID)
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    showVehicleOwnerEntryFragment(obj as VUADMVehicleOwnership?)
                }
                R.id.txtDelete -> {
                    deleteVehicleOwner(obj as VUADMVehicleOwnership?)
                }
            }
        }
    }


    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun deleteVehicleOwner(vuadmVehicleOwnership: VUADMVehicleOwnership?) {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0 && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0)
            mListener?.showProgressDialog()
        APICall.deleteAccountMappingData(ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                ?: 0, "ADM_VehicleOwnership"
                , vuadmVehicleOwnership?.vehicleOwnershipID.toString()
                , object : ConnectionCallBack<Boolean> {
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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_VEHICLE_DETAILS) {
            val mData = event?.intent
            if (mData != null) {
                mData.let {
                    if (it.hasExtra(Constant.KEY_SYCO_TAX_ID) && it.hasExtra(Constant.KEY_QUICK_MENU)
                            && it.hasExtra(Constant.KEY_VEHICLE_OWNERSHIP)) {
                        val sycotaxID = it.getStringExtra(Constant.KEY_SYCO_TAX_ID)
                        val vuAdmVehicleOwnership = it.getParcelableExtra<VUADMVehicleOwnership>(Constant.KEY_VEHICLE_OWNERSHIP)
                        vehicleOwnershipEntryFragment(vuAdmVehicleOwnership, sycotaxID)
                        event?.clearData()
                    }
                }
            } else {
                bindData()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == mResultCode) {
            val mData = event?.intent
            mData?.let {
                if (it.hasExtra(Constant.KEY_SYCO_TAX_ID)) {
                    val sycotaxID = it.getStringExtra(Constant.KEY_SYCO_TAX_ID)
                    vehicleOwnershipEntryFragment(null, sycotaxID)
                    event?.clearData()
                }
            }
        }
    }

    private fun vehicleOwnershipEntryFragment(vuAdmVehicleOwnership: VUADMVehicleOwnership?, sycoTaxID: String?) {
        val fragment = VehicleOwnershipEntryFragment()

        //region SetArguments
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
        bundle.putString(Constant.KEY_TASK_CODE, mTaskCode)
        bundle.putString(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        bundle.putParcelable(Constant.KEY_VEHICLE_OWNERSHIP, vuAdmVehicleOwnership)
        bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
        fragment.arguments = bundle
        //endregion

        fragment.setTargetFragment(this, Constant.REQUEST_CODE_VEHICLE_DETAILS)

        mListener?.showToolbarBackButton(R.string.vehicle_ownership)
        mListener?.addFragment(fragment, true)
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
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode

    }

}