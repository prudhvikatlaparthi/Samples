package com.sgs.citytax.ui.fragments

import android.content.Context
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
import com.sgs.citytax.api.response.InventoryStatusResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentInventoryStatusBinding
import com.sgs.citytax.ui.adapter.InventoryStatusAdapter

class InventoryStatusFragment : BaseFragment() {
    private lateinit var mBinding: FragmentInventoryStatusBinding
    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun initComponents() {
        setViews()
        bindData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory_status, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    private fun setViews() {
        mBinding.rcvInventoryStatus.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    fun bindData() {
        listener?.showProgressDialog()
        APICall.getInventoryStatus(MyApplication.getPrefHelper().accountId, object : ConnectionCallBack<InventoryStatusResponse> {
            override fun onSuccess(response: InventoryStatusResponse) {
                listener?.dismissDialog()
                response.inventoryStatus.let {
                    mBinding.rcvInventoryStatus.adapter = InventoryStatusAdapter(it)
                }
            }

            override fun onFailure(message: String) {
                listener?.dismissDialog()
                if (message.isNotEmpty())
                    listener?.showAlertDialog(message)
            }
        })
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String)
    }
}