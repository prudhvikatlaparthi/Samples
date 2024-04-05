package com.sgs.citytax.ui.fragments

import android.app.Activity.RESULT_OK
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
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.CRMAccountEmails
import com.sgs.citytax.ui.adapter.EmailsAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.REQUEST_CODE_ACCOUNT_EMAIL
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class EmailMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS
    private var mPrimaryKey: Int = 0

    private var crmAccountEmails: List<CRMAccountEmails> = arrayListOf()
    private var email: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else
                context as Listener
        } catch (e: Exception) {
            throw ClassCastException("$context must implemet Listener")
        }
    }

    override fun initComponents() {
        //region Get arguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPrimaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
        }
        //endregion
        setViews()
        bindData()
        setListeners()
    }

    private fun setViews() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> mBinding.fabAdd.visibility = View.VISIBLE
            Constant.ScreenMode.VIEW -> mBinding.fabAdd.visibility = View.GONE
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

    fun bindData() {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails(
                "CRM_AccountEmails",
                mPrimaryKey,
                0,
                null,
                object : ConnectionCallBack<DataResponse> {
                    override fun onSuccess(response: DataResponse) {
                        if (response.emailAccounts.isNotEmpty()) {
                            crmAccountEmails = response.emailAccounts
                            mBinding.recyclerView.addItemDecoration(
                                DividerItemDecoration(
                                    context,
                                    LinearLayoutManager.VERTICAL
                                )
                            )
                            mBinding.recyclerView.adapter = EmailsAdapter(
                                crmAccountEmails,
                                this@EmailMasterFragment,
                                mListener?.screenMode
                            )
                            for (i in response.emailAccounts.indices) {
                                if (response.emailAccounts[i].default == "Y") {
                                    email = response.emailAccounts[i].email
                                    break
                                } else {
                                    email = ""
                                }
                            }

                        } else {
                            email = ""
                            crmAccountEmails = arrayListOf()
                            mBinding.recyclerView.adapter = null
                        }

                        mListener?.dismissDialog()
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        mBinding.recyclerView.adapter = null
                        if (message.isNotEmpty())
                            mListener?.showAlertDialog(message)
                    }
                })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            when (fromScreen) {
                Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
                    if (ObjectHolder.registerBusiness.accountEmails.isNotEmpty()) {
                        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        mBinding.recyclerView.adapter = EmailsAdapter(ObjectHolder.registerBusiness.accountEmails, this)
                    }

                }
                else -> {

                }
            }
        }*/
    }

    fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showEmailEntryScreen(null)

            }
        })
    }

    fun onBackPressed() {
        val intent = Intent()
        if (!email.isNullOrEmpty())
            intent.putExtra("EMAIL", email)
        targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, intent)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    showEmailEntryScreen(obj as CRMAccountEmails?)
                }
                R.id.txtDelete -> {
                    deleteEmails(obj as CRMAccountEmails)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    private fun deleteEmails(crmAccountEmails: CRMAccountEmails?) {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.deleteAccountMappingData(mPrimaryKey
                ?: 0,
                "CRM_AccountEmails",
                crmAccountEmails?.accountEmailID.toString(),
                object : ConnectionCallBack<Boolean> {
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

    private fun showEmailEntryScreen(crmAccountEmails: CRMAccountEmails?) {
        val emailEntryFragment = EmailEntryFragment()
        //region Set Arguments
        val bundle = Bundle()
        bundle.putParcelable(Constant.KEY_ACCOUNT_EMAILS, crmAccountEmails)
        bundle.putInt(Constant.KEY_PRIMARY_KEY, mPrimaryKey)
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        emailEntryFragment.arguments = bundle
        //endregion

        emailEntryFragment.setTargetFragment(this, Constant.REQUEST_CODE_ACCOUNT_EMAIL)
        emailEntryFragment.show(
            activity?.supportFragmentManager!!,
            emailEntryFragment.javaClass.simpleName
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ACCOUNT_EMAIL) {
                bindData()
                /*  if (data?.hasExtra("EMAIL") == true)
                      email = data.getStringExtra("EMAIL")*/
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode

    }
}