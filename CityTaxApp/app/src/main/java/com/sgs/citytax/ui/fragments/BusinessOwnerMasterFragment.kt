package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.StoreCustomerB2C
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.adapter.BusinessOwnershipAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class BusinessOwnerMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
    private var mGeoAddress: GeoAddress? = null
    private var primaryKey = 0
    var owners: List<BusinessOwnership> = emptyList()
    private var keyShow: String? = null
    private var setViewForGeoSpatial: Boolean? = false  //todo New key to Hide views for geo spacial - 15/3/2022, not used fromScreen, to not to disturb th flow

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
            keyShow = arguments?.getString(Constant.KEY_SHOW)
            if (arguments?.containsKey(Constant.KEY_ADDRESS)!!){
                mGeoAddress = arguments?.getParcelable (Constant.KEY_ADDRESS)
            }
            if (it.containsKey(Constant.KEY_GEO_SPATIAL_VIEW))
                setViewForGeoSpatial = it.getBoolean(Constant.KEY_GEO_SPATIAL_VIEW, false)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    private fun setViews() {
        mBinding.fabSearch.visibility = VISIBLE

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> {
                mBinding.fabAdd.visibility = View.VISIBLE
                mBinding.fabSearch.visibility = VISIBLE
            }

            Constant.ScreenMode.VIEW -> {
                mBinding.fabAdd.visibility =  GONE
                mBinding.fabSearch.visibility= GONE
            }
        }




        val itemDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)
        mBinding.recyclerView.addItemDecoration(itemDecor)
        mBinding.recyclerView.adapter = BusinessOwnershipAdapter(this, mListener?.screenMode)
    }

    private fun bindData() {
        if (primaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails("VU_CRM_TaxPayerAccountContacts", primaryKey,
                    0, null, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    mListener?.dismissDialog()
                    response.businessOwnerships.let {
                        owners = it
                        (mBinding.recyclerView.adapter as BusinessOwnershipAdapter).reset(ArrayList(it))
                    }
                }

                override fun onFailure(message: String) {
                    owners = emptyList()
                    mListener?.dismissDialog()
                    mBinding.recyclerView.adapter = BusinessOwnershipAdapter(this@BusinessOwnerMasterFragment, mListener?.screenMode)
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            when (fromScreen) {

                Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER,
                Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER -> {
                    val businessOwners: ArrayList<BusinessOwnership> = arrayListOf()
                    for (obj: ObjBusinessOwner in ObjectHolder.registerBusiness.insertBusinessOwnership) {
                        businessOwners.add(obj.businessOwnership)
                    }
                    (mBinding.recyclerView.adapter as BusinessOwnershipAdapter).reset(businessOwners)
                }

                else -> {

                }
            }
        }*/
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = BusinessOwnerEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER) //as we have to show the 'copyAddress' and hide 'licence' fields
                bundle.putParcelable(Constant.KEY_ADDRESS, mGeoAddress)
                bundle.putString(Constant.KEY_SHOW, keyShow)

                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this@BusinessOwnerMasterFragment, Constant.REQUEST_CODE_BUSINESS_OWNER)

                mListener?.showToolbarBackButton(R.string.title_business_owner)
                mListener?.addFragment(fragment, true)
            }

        })
        mBinding.fabSearch.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = BusinessOwnerSearchFragment()

                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)

                fragment.arguments = bundle
                fragment.setTargetFragment(this@BusinessOwnerMasterFragment, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
                mListener?.showToolbarBackButton(R.string.citizen)
                mListener?.addFragment(fragment, true)
            }

        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.txtEdit -> {
                val fragment = BusinessOwnerEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putParcelable(Constant.KEY_BUSINESS_OWNER, obj as BusinessOwnership)
                bundle.putBoolean(Constant.KEY_EDIT, true)
                setViewForGeoSpatial?.let { bundle.putBoolean(Constant.KEY_GEO_SPATIAL_VIEW, it) }
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)

                mListener?.showToolbarBackButton(R.string.title_business_owner)
                mListener?.addFragment(fragment, true)
            }
            R.id.txtDelete -> {
                deleteBusinessOwner(obj as BusinessOwnership?)
            }
        }
    }

    private fun deleteBusinessOwner(businessOwner: BusinessOwnership?) {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0 && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0)
            mListener?.showProgressDialog()
        APICall.deleteAccountMappingData(ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                ?: 0, "CRM_AccountContacts"
                , businessOwner?.accountContactID.toString()
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

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER)
            bindData()
        else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                val businessOwner: BusinessOwnership = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                if (!isExist(businessOwner)) {
                    businessOwner.taxPayerAccountID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                    val storeCustomerB2C = StoreCustomerB2C()
                    //  To generate business owner id for a citizen, who has citizen id; when a citizen mapped with a business
                    // we have to send business owner id as empty
                    businessOwner.businessOwnerID = businessOwner.businessOwnerID ?: ""
                    if (businessOwner.firstName == null && TextUtils.isEmpty(businessOwner.firstName)){
                        businessOwner.firstName = businessOwner.accountName
                    }
                    storeCustomerB2C.businessOwnership = businessOwner
                    saveBusinessOwnership(storeCustomerB2C)
                }
            }
        }
    }

    private fun isExist(businessOwner: BusinessOwnership): Boolean {
        owners.let {
            for (owner in it) {
                if (owner.businessOwnerID == businessOwner.businessOwnerID)
                    return true
            }
        }
        return false
    }

    private fun saveBusinessOwnership(customerB2C: StoreCustomerB2C) {
        if (0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId) {
            mListener?.showProgressDialog()
            APICall.storeCustomerB2C(customerB2C, object : ConnectionCallBack<BusinessOwnership> {
                override fun onSuccess(response: BusinessOwnership) {
                    mListener?.dismissDialog()
                    mListener?.showSnackbarMsg(R.string.msg_record_save_success)
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
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode
    }

}