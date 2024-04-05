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
import com.sgs.citytax.api.payload.AccountPhone
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.ui.adapter.PhonesAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class PhoneMasterFragment : BaseFragment(), View.OnClickListener, IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu? = null
    private var mPrimaryKey: Int = 0

    private var accountPhones: ArrayList<AccountPhone> = arrayListOf()
    private var phoneNumber: String? = null

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        mListener?.showToolbarBackButton(R.string.title_phones)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPrimaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
        }

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

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                R.id.fabAdd -> {

                }
            }
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    showPhoneEntryScreen(obj as AccountPhone?)
                }
                R.id.txtDelete -> {
                    deletePhone(obj as AccountPhone)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    private fun deletePhone(accountPhone: AccountPhone?) {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.deleteAccountMappingData(mPrimaryKey
                    ?: 0, "CRM_AccountPhones", accountPhone?.accountPhoneID.toString()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_ACCOUNT_PHONE) {
                bindData()
                /*if (data?.hasExtra("PHONE_NUMBER") == true)
                    phoneNumber = data.getStringExtra("PHONE_NUMBER")*/
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun showPhoneEntryScreen(accountPhone: AccountPhone?) {
        val phoneEntryFragment = PhoneEntryFragment()

        //region Set Arguments
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_PHONE, accountPhone)
        bundle.putInt(Constant.KEY_PRIMARY_KEY, mPrimaryKey)
        phoneEntryFragment.arguments = bundle
        //endregion

        phoneEntryFragment.setTargetFragment(this, Constant.REQUEST_CODE_ACCOUNT_PHONE)
        phoneEntryFragment.show(activity?.supportFragmentManager!!, phoneEntryFragment.javaClass.simpleName)
    }

    private fun bindData() {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.getCorporateOfficeChildTabDetails("CRM_AccountPhones", mPrimaryKey, 0, null, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    if (response.accountPhones.isNotEmpty()) {
                        accountPhones = response.accountPhones as ArrayList<AccountPhone>
                        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        mBinding.recyclerView.adapter = PhonesAdapter(accountPhones, this@PhoneMasterFragment, mListener?.screenMode)
                        for (i in response.accountPhones.indices) {
                            if(response.accountPhones[i].default=="Y"){
                                phoneNumber=response.accountPhones[i].number
                                break
                            }
                            else{
                                phoneNumber=""
                            }
                        }

                    } else {
                        accountPhones = arrayListOf()
                        phoneNumber=""
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
                    if (ObjectHolder.registerBusiness.accountPhones.isNotEmpty()) {
                        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        mBinding.recyclerView.adapter = PhonesAdapter(ObjectHolder.registerBusiness.accountPhones, this)
                    }
                }
                else -> {

                }
            }
        }*/
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                showPhoneEntryScreen(null)
            }

        })
    }

    fun onBackPressed() {
        val intent = Intent()
        if (!phoneNumber.isNullOrEmpty())
            intent.putExtra("PHONE_NUMBER", phoneNumber)
        targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, intent)
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
    }
}