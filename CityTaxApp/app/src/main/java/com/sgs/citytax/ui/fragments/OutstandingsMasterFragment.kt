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
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.GetOutstanding
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.ui.adapter.OutstandingAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener


class OutstandingsMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var customerId: Int? = 0

    //    private var isEdit: Boolean = false
    private var voucherNo: Int? = 0
    private var fromScreen: Constant.QuickMenu? = null
    private var productCode: String? = null

    companion object {

    }

    override fun initComponents() {
        arguments?.let {
            customerId = arguments?.getInt(Constant.KEY_CUSTOMER_ID) ?: 0
//            isEdit = arguments?.getBoolean(Constant.KEY_EDIT) ?: false
            if (it.containsKey(Constant.KEY_VOUCHER_NO))
                voucherNo = arguments?.getInt(Constant.KEY_VOUCHER_NO)
            if (it.containsKey(Constant.KEY_PRODUCT_CODE))
                productCode = arguments?.getString(Constant.KEY_PRODUCT_CODE)
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen =
                    arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?
        }
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

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    private fun setViews() {
        val itemDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> mBinding.fabAdd.visibility = View.VISIBLE
            Constant.ScreenMode.VIEW -> mBinding.fabAdd.visibility = View.GONE
        }

        mBinding.recyclerView.addItemDecoration(itemDecor)
        mBinding.recyclerView.adapter = OutstandingAdapter(this, mListener?.screenMode)
    }

    private fun bindData() {
        mListener?.showProgressDialog()
        APICall.getOutStandingsList(
            customerId,
            productCode,
            voucherNo,
            object : ConnectionCallBack<ArrayList<GetOutstanding>> {
                override fun onSuccess(response: ArrayList<GetOutstanding>) {
                    mListener?.dismissDialog()
                    response.sortBy { selector(it) }
                    response.reverse()
                    clearAdapter().update(response)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mBinding.recyclerView.adapter =
                        OutstandingAdapter(this@OutstandingsMasterFragment, mListener?.screenMode)
                }
            })
    }

    fun selector(outstanding: GetOutstanding): Int = outstanding.year

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val fragment = OutstandingEntryFragment()
                val bundle = Bundle()
                bundle.putInt(Constant.KEY_CUSTOMER_ID, customerId ?: 0)
                bundle.putString(Constant.KEY_PRODUCT_CODE, productCode)
                voucherNo?.let {
                    bundle.putInt(Constant.KEY_VOUCHER_NO, it)
                }
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                fragment.arguments = bundle
                fragment.setTargetFragment(
                    this@OutstandingsMasterFragment,
                    Constant.REQUEST_CODE_OUT_STANDING_ENTRY
                )
                mListener?.showToolbarBackButton(R.string.title_outstandings)
                mListener?.addFragment(fragment, true)
            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.txtEdit -> {
                val fragment = OutstandingEntryFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_OUT_STANDING, obj as GetOutstanding?)
                bundle.putInt(Constant.KEY_CUSTOMER_ID, customerId ?: 0)
                bundle.putString(Constant.KEY_PRODUCT_CODE, productCode)
                voucherNo?.let {
                    bundle.putInt(Constant.KEY_VOUCHER_NO, it)
                }
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_OUT_STANDING_ENTRY)

                mListener?.showToolbarBackButton(R.string.title_outstandings)
                mListener?.addFragment(fragment, true)
            }
            R.id.txtDelete -> {
                val getOutstandings = obj as GetOutstanding
                if (getOutstandings.allowDelete == "Y")
                    deleteOutstanding(obj as GetOutstanding?)
                else
                    mListener?.showAlertDialog(getString(R.string.delete_message))
            }
        }
    }

    private fun deleteOutstanding(getOutstandings: GetOutstanding?) {
        mListener?.showProgressDialog()
        APICall.deleteOutStanding(
            getOutstandings?.initialOutstandingID,
            object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    bindData()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showSnackbarMsg(message)
                }
            })

    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun clearAdapter(): OutstandingAdapter {
        var adapter = mBinding.recyclerView.adapter

        if (adapter == null) {
            adapter = OutstandingAdapter(this, mListener?.screenMode)
        }

        (adapter as OutstandingAdapter).clear()
        return adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_OUT_STANDING_ENTRY)
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
        fun showSnackbarMsg(message: String)
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode
    }
}