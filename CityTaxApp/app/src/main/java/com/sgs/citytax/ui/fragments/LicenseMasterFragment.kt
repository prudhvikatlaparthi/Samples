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
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.LicenseDetailsResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.LicenseDetails
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.ui.adapter.LicenseAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class LicenseMasterFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu? = null
    private var isMultiple = false
    private var mTaskCode: String? = ""
    private var mNoOfLicenses: Int = 0
    private var adapter: LicenseAdapter? = null
    private var mLicenseDetails: ArrayList<LicenseDetails> = arrayListOf()
    private var mTaxRuleBookCode: String? = ""

    var pageIndex: Int = 1
    val pageSize: Int = 50
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

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
            mTaxRuleBookCode = arguments?.getString(Constant.KEY_TAX_RULE_BOOK_CODE) ?: ""
        }

        setViews()
        bindData()
        setListeners()
    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
        adapter = LicenseAdapter(this, mListener?.screenMode)
        mBinding.recyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_LICENSES) {
                adapter?.clear()
                bindData()
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    private fun bindData() {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId) {
            mListener?.showProgressDialog()

            val details = LicenseDetails()
            details.isLoading = true
            adapter?.add(details)
            isLoading = true

            APICall.getLicenseDetails(ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
                ?: 0, pageSize, pageIndex, object : ConnectionCallBack<LicenseDetailsResponse> {
                override fun onSuccess(response: LicenseDetailsResponse) {
                    mListener?.dismissDialog()
                    if (response.details?.licenseDetails != null && response.details?.licenseDetails!!.isNotEmpty()) {
                        mLicenseDetails = response.details?.licenseDetails!!
                        val count: Int = mLicenseDetails.size
                        if (count < pageSize) {
                            hasMoreData = false
                        } else
                            pageIndex += 1
                        adapter?.remove(details)
                        adapter!!.addAll(mLicenseDetails)
                        isLoading = false

                        mNoOfLicenses = mLicenseDetails.size

                        /*if (!isMultiple && mLicenseDetails.size > 0)
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
                    } else {
                        adapter?.remove(details)
                        isLoading = false
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    adapter?.remove(details)
                    isLoading = false
                    mNoOfLicenses = mLicenseDetails.size
                    /*mBinding.fabAdd.visibility = View.VISIBLE

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
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showLicenseEntryScreen(null)

            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.txtEdit -> {
                    showLicenseEntryScreen(obj as LicenseDetails)
                }

                R.id.txtDelete -> {
                    val license = obj as LicenseDetails
                    deleteLicense(license.licenseId ?: 0)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }


    private fun showLicenseEntryScreen(licenseDetails: LicenseDetails?) {
        val fragment = LicenseEntryFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_LICENSE, licenseDetails)
        bundle.putInt(Constant.KEY_NUMBER_OF_LICENSES, mNoOfLicenses)
        bundle.putString(Constant.KEY_TASK_CODE, mTaskCode)
        if (licenseDetails == null) {
            mListener?.screenMode = Constant.ScreenMode.ADD
        } else {
            bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
        }
        fragment.arguments = bundle
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_LICENSES)
        mListener?.showToolbarBackButton(R.string.title_licenses)
        mListener?.addFragment(fragment, true)
    }

    private fun deleteLicense(licenseId: Int) {
        mListener?.showProgressDialog()
        APICall.deleteLicense(licenseId, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                adapter?.clear()
                bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showToolbarBackButton(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
    }


}
