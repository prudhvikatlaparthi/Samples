package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.Business
import com.sgs.citytax.api.response.BusinessResponse
import com.sgs.citytax.databinding.FragmentBusinessSearchBinding
import com.sgs.citytax.ui.adapter.BusinessAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class BusinessSearchFragment : BaseFragment(), IClickListener, View.OnClickListener {
    private lateinit var mBinding: FragmentBusinessSearchBinding
    private var mListener: Listener? = null

    private lateinit var mAdapter: BusinessAdapter
    private var mCode: Constant.QuickMenu? = null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_search, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        setViews()
    }

    private fun setViews() {
        mBinding.btnSearch.setOnClickListener(this)
        mBinding.rcvOwnerSearchResult.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mAdapter = BusinessAdapter(this)
        mBinding.rcvOwnerSearchResult.adapter = mAdapter
        mBinding.llInner.setOnClickListener{
            Log.d("TAG", "setViews: ")
        }
    }


    override fun onClick(v: View?) {
        if (v?.id == R.id.btnSearch) {
            val data: String = mBinding.edtownersearch.text.toString()
            mListener?.hideKeyBoard()
            if (validateView()) {
                getBusinessOwnerSearchResult(data)
            }
        }

    }

    private fun getBusinessOwnerSearchResult(data: String) {

        APICall.getBusiness(data, object : ConnectionCallBack<BusinessResponse> {
            override fun onSuccess(response: BusinessResponse) {
                val count = response.businessOwner?.size
                if (count == null || count == 0)
                    Toast.makeText(context, R.string.msg_no_data, Toast.LENGTH_SHORT).show()
                mAdapter.clear()
                response.businessOwner?.let {
                    mAdapter.update(it)
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                mAdapter.clear()
                mBinding.rcvOwnerSearchResult.adapter = null

            }
        })
    }

    private fun validateView(): Boolean {
        if (TextUtils.isEmpty(mBinding.edtownersearch.text.toString().trim())) {
            mListener?.showSnackbarMsg(getString(R.string.error_message))
            return false
        }
        return true
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    interface Listener {
        fun finish()
        fun popBackStack()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val business = obj as Business
        val intent = Intent()
        intent.putExtra(Constant.KEY_BUSINESS, business)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        mListener?.popBackStack()
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }
}