package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.VehicleOwnershipDeletePayload
import com.sgs.citytax.api.response.VehicleOwnershipDetailsResult
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.VehicleDetails
import com.sgs.citytax.ui.VehicleCitizenOnBoardFragment
import com.sgs.citytax.ui.adapter.VehicleOnBoardingDetailsAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener


class VehicleOnBoardingMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP

    companion object {
        fun getTableName(screen: Constant.QuickMenu) =
                if (screen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP || screen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE || screen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP)
                    "ADM_VehicleOwnership"
                else ""

        var primaryKey = 0
        var mPrimaryKey = ""
        var mSycoTaxId = ""
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP || fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS) {
                mPrimaryKey = arguments?.getString(Constant.KEY_PRIMARY_KEY) ?: ""
            } else {
                primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
            }
            mSycoTaxId = arguments?.getString(Constant.KEY_SYCO_TAX_ID) ?: ""
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


    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setViews() {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT)
            mBinding.fabAdd.visibility = View.GONE

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> mBinding.fabAdd.visibility = View.VISIBLE
            Constant.ScreenMode.VIEW -> mBinding.fabAdd.visibility = View.GONE
            else -> {

            }
        }

        val itemDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)
        mBinding.recyclerView.addItemDecoration(itemDecor)
        mBinding.recyclerView.adapter = VehicleOnBoardingDetailsAdapter(this, fromScreen, mListener?.screenMode)
    }

    private fun bindData() {
        if (primaryKey != 0 || !TextUtils.isEmpty(mPrimaryKey)) {
            var key = ""
            if (primaryKey != 0) {
                key = primaryKey.toString()
            } else if (!TextUtils.isEmpty(mPrimaryKey)) {
                key = mPrimaryKey
            }
            mListener?.showProgressDialog()
            APICall.getVehicleOwnershipDetails(key, object : ConnectionCallBack<VehicleOwnershipDetailsResult> {
                override fun onSuccess(response: VehicleOwnershipDetailsResult) {
                    mListener?.dismissDialog()
                    clearAdapter().update(response.vehicleDetails ?: listOf())
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mBinding.recyclerView.adapter = VehicleOnBoardingDetailsAdapter(this@VehicleOnBoardingMasterFragment, fromScreen, mListener?.screenMode)
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)
                }
            })
        }
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = VehicleCitizenOnBoardFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putString(Constant.KEY_PRIMARY_KEY, mPrimaryKey)
                bundle.putString(Constant.KEY_SYCO_TAX_ID, mSycoTaxId)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this@VehicleOnBoardingMasterFragment, Constant.REQUEST_CODE_VEHICLE_DETAILS)

                mListener?.showToolbarBackButton(R.string.citizen)
                mListener?.addFragment(fragment, true)
            }
        })
    }

    private fun clearAdapter(): VehicleOnBoardingDetailsAdapter {
        var adapter = mBinding.recyclerView.adapter

        if (adapter == null) {
            adapter = VehicleOnBoardingDetailsAdapter(this, fromScreen, mListener?.screenMode)
        }

        (adapter as VehicleOnBoardingDetailsAdapter).clear()
        return adapter
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.txtEdit -> {
                val fragment = VehicleCitizenOnBoardFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putString(Constant.KEY_PRIMARY_KEY, mPrimaryKey)
                bundle.putParcelable(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS, obj as VehicleDetails)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_VEHICLE_DETAILS)

                mListener?.showToolbarBackButton(R.string.citizen)
                mListener?.addFragment(fragment, true)
            }
            R.id.txtDelete -> {
                deleteOwnership(obj as VehicleDetails)
            }
        }
    }

    private fun deleteOwnership(vehicleDetails: VehicleDetails) {
        if (null != vehicleDetails.vehicleOwnerShipID) {
            mListener?.showProgressDialog()
            val deleteVehicleOwnership = VehicleOwnershipDeletePayload(SecurityContext(), vehicleDetails.vehicleOwnerShipID.toString())
            APICall.deleteOnBoardVehicleOwnership(deleteVehicleOwnership, object : ConnectionCallBack<Boolean> {
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

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mListener?.showToolbarBackButton(R.string.title_vehicle_ownership_details)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_VEHICLE_DETAILS)
            bindData()
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