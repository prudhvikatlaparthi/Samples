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
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.VehicleDetailsWithOwnerResponse
import com.sgs.citytax.databinding.FragmentVehicleSearchBinding
import com.sgs.citytax.model.VehicleDetails
import com.sgs.citytax.ui.adapter.VehicleSearchAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class VehicleSearchFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentVehicleSearchBinding
    private var mListener: Listener? = null
    private var vehicleDetails: ArrayList<VehicleDetails> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicle_search, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun initComponents() {
        setListeners()
    }

    private fun setListeners() {
        mBinding.mainLayout.setOnClickListener{
            //to handle the background click
        }
        mBinding.btnSearch.setOnClickListener {
            if (validateView())
                bindData()
        }
    }

    private fun validateView(): Boolean {
        if (TextUtils.isEmpty(mBinding.edtVehicleSearch.text.toString().trim())) {
            mListener?.showSnackbarMsg(getString(R.string.error_message))
            return false
        }
        return true
    }

    private fun bindData() {
        mListener?.showProgressDialog()
        val filterData = mBinding.edtVehicleSearch.text.toString().trim()
        APICall.getVehicleDetailsWithOwner(filterData, object : ConnectionCallBack<VehicleDetailsWithOwnerResponse> {
            override fun onSuccess(response: VehicleDetailsWithOwnerResponse) {
                if(response.vehicleDetails!=null){
                    if (response.vehicleDetails!!.isNotEmpty()) {
                        vehicleDetails = response.vehicleDetails!!
                        mBinding.rcvVehicles.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
                        mBinding.rcvVehicles.adapter = VehicleSearchAdapter(vehicleDetails, this@VehicleSearchFragment)
                    } else {
                        mListener?.showAlertDialog(getString(R.string.msg_no_data))
                    }
                }
                else{
                    mListener?.showAlertDialog(getString(R.string.msg_no_data))
                }

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val vehicleDetails = obj as VehicleDetails
        val intent = Intent()
        intent.putExtra(Constant.KEY_VEHICLE_OWNERSHIP, vehicleDetails)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        mListener?.popBackStack()
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun finish()
        fun showSnackbarMsg(message: String?)
        fun showProgressDialog(message: Int)
        fun popBackStack()
    }
}