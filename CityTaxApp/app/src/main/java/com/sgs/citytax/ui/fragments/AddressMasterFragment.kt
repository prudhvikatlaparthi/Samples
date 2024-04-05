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
import com.sgs.citytax.api.response.BusinessOwner
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.ui.adapter.AddressAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class AddressMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mPrimaryKey: Int = 0
    private var geoAddress: GeoAddress? = null

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPrimaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY)!!
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
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> mBinding.fabAdd.visibility = View.VISIBLE
            Constant.ScreenMode.VIEW -> mBinding.fabAdd.visibility = View.GONE
        }
        mBinding.recyclerView.adapter = AddressAdapter(this, mListener?.screenMode)
    }

    private fun bindData() {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails("VU_CRM_AccountAddresses", mPrimaryKey,
                    0, null, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    mListener?.dismissDialog()
                    (mBinding.recyclerView.adapter as AddressAdapter).clear()
                    if (response.accountAddresses.isNotEmpty()) {
                        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        if (response.accountAddresses.size >= 1) {
                            geoAddress = response.accountAddresses.get(0)
                            mBinding.fabAdd.visibility = View.GONE
                        } else {
                            mBinding.fabAdd.visibility = View.VISIBLE
                        }
                        (mBinding.recyclerView.adapter as AddressAdapter).update(response.accountAddresses)
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    (mBinding.recyclerView.adapter as AddressAdapter).clear()
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            if (mCode == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
                if (!ObjectHolder.registerBusiness.addresses.isNullOrEmpty()) {
                    (mBinding.recyclerView.adapter as AddressAdapter).update(ObjectHolder.registerBusiness.addresses)
                    mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                }
            }
        }*/
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                showAddressEntryScreen(null)

            }
        })
    }


    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    showAddressEntryScreen(obj as GeoAddress)
                }
                R.id.txtDelete -> {
                    deleteAddress(obj as GeoAddress)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun deleteAddress(geoAddress: GeoAddress?) {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.deleteAccountMappingData(mPrimaryKey
                    ?: 0, "CRM_AccountAddresses", geoAddress?.addressID.toString()
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
    }

    private fun showAddressEntryScreen(geoAddress: GeoAddress?) {
        val fragment = AddressEntryFragment()

        //region SetArguments
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
        bundle.putInt(Constant.KEY_PRIMARY_KEY, mPrimaryKey)
        bundle.putParcelable(Constant.KEY_ADDRESS, geoAddress)

        fragment.arguments = bundle
        //endregion

        fragment.setTargetFragment(this, Constant.REQUEST_CODE_ACCOUNT_ADDRESSES)

        mListener?.showToolbarBackButton(R.string.title_address)
        mListener?.addFragment(fragment, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_ACCOUNT_ADDRESSES) {
            (mBinding.recyclerView.adapter as AddressAdapter).clear()
            bindData()
        }
    }

    fun onBackPressed() {
        val intent = Intent()
        if (geoAddress!=null) {
            val businessOwnership = BusinessOwnership()
            businessOwnership.country = geoAddress!!.country
            businessOwnership.state = geoAddress!!.state
            businessOwnership.city = geoAddress!!.city
            businessOwnership.zone = geoAddress!!.zone
            businessOwnership.sector = geoAddress!!.sector
            businessOwnership.street = geoAddress!!.street
            businessOwnership.section = geoAddress!!.plot
            businessOwnership.lot = geoAddress!!.block
            businessOwnership.pacel = geoAddress!!.doorNo
            businessOwnership.zipCode = geoAddress!!.zipCode
            businessOwnership.description = geoAddress!!.description
            intent.putExtra(Constant.KEY_ADDRESS, businessOwnership)
            intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
        }

        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
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