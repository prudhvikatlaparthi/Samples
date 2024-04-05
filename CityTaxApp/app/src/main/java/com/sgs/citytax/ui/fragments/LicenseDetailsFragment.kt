package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.SubscriptionResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentLicenseDetailsBinding
import com.sgs.citytax.util.formatDateTimeInMillisecond
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond

class LicenseDetailsFragment : BaseFragment() {
    private lateinit var mBinding: FragmentLicenseDetailsBinding
    var pageIndex: Int = 1
    val pageSize: Int = 100
    var acctid: String = ""
    var usrid: String = ""
    private var listener: Listener? = null


    override fun initComponents() {
        bindData()
        setListeners()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_license_details, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    fun onBackPressed() {
        listener?.finish()
    }

    fun setListeners() {
        mBinding.btnRenew.setOnClickListener {
            val fragment = LicenseRenewFragment()
            listener?.addFragment(fragment, true)
        }
    }

    private fun bindData() {
        acctid = MyApplication.getPrefHelper().accountId.toString()
        usrid = MyApplication.getPrefHelper().loggedInUserID
        listener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getSubscriptionDetails(acctid, usrid, object : ConnectionCallBack<List<SubscriptionResponse>> {
            override fun onSuccess(response: List<SubscriptionResponse>) {
                mBinding.txtSubscriptionCode.text = response[0].LicenceKey
                mBinding.txtSubscriptionStartDate.text = formatDisplayDateTimeInMillisecond(response[0].ValidFromDate)
                mBinding.txtSubscriptionDays.text = response[0].RemainingDays + " " + getString(R.string.days)
                mBinding.txtSubscriptionExpiryDate.text = formatDisplayDateTimeInMillisecond(response[0].ValidUptoDate)
                listener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                listener?.dismissDialog()
            }
        })
    }

    fun updateData() {
        bindData()
    }

    interface Listener {
        fun popBackStack()
        fun showProgressDialog(message: Int)
        fun dismissDialog()
        fun showToolbarBackButton(title: Int)
        fun finish()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
    }

}