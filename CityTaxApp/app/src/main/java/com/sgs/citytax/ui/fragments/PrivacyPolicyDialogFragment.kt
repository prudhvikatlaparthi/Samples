package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.Agent
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPrivacyPolicyBinding
import com.sgs.citytax.model.UMXUserPolicyDetails
import com.sgs.citytax.ui.ChangePasswordActivity
import com.sgs.citytax.ui.DashboardActivity
import com.sgs.citytax.ui.LoginAuthActivity
import com.sgs.citytax.util.Constant
import java.util.*

class PrivacyPolicyDialogFragment : DialogFragment() {

    private var listener: Listener? = null
    private lateinit var binding: FragmentPrivacyPolicyBinding
    private var content: String? = ""
    private lateinit var mContext: Context
    var userName = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
            this.mContext = context
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(content: String,mUserName: String) = PrivacyPolicyDialogFragment().apply {
            this.content = content
            this.userName = mUserName
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_policy, container, false)
        initComponents()
        return binding.root
    }

    private fun initComponents() {
        setButtonsText()
        bindData()
        setListeners()
    }

    private fun bindData() {
        binding.privacyWebView.loadData(content, "text/html", "UTF-8")
    }

    private fun setButtonsText(){
        setLocale(mContext, MyApplication.getPrefHelper().language)
        binding.btnAccept.text = mContext.getString(R.string.accept)
        binding.btnReject.text = mContext.getString(R.string.reject)
    }

    private fun setListeners() {
        binding.btnAccept.setOnClickListener {
            listener?.showProgressDialog()
            val userPolicyDetails = UMXUserPolicyDetails()
            userPolicyDetails.accepted = 'Y'
            userPolicyDetails.userId = MyApplication.getPrefHelper().loggedInUserID

            APICall.insertUserPolicyDetails(userPolicyDetails, object : ConnectionCallBack<String?> {
                override fun onSuccess(response: String?) {
                    listener?.dismissDialog()
                    if (response != null && response.isNotEmpty()) {
                        dismiss()
                        if (MyApplication.getPrefHelper().loginCount == 1)
                            changePasswordAlert()
                        else
                            navigateNext()
                    }
                }

                override fun onFailure(message: String) {
                    listener?.dismissDialog()
                    listener?.showAlertDialog(message)
                }
            })
        }

        binding.btnReject.setOnClickListener {
            dismiss()
        }
    }

    private fun setLocale(context: Context, locale: String) {
        val locale1 = Locale(locale)
        Locale.setDefault(locale1)
        val res = context.resources
        val configuration = res.configuration
        configuration.setLocale(locale1)
        res.updateConfiguration(configuration,res.displayMetrics)
    }

    private fun changePasswordAlert() {
        listener?.showAlertDialog(getString(R.string.security_reasons), DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
            navigateToChangePasswordScreen()
        })
    }

    private fun navigateToChangePasswordScreen() {
        val intent = Intent(mContext, ChangePasswordActivity::class.java)
        intent.putExtra(Constant.KEY_AGENT, getAgent())
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        mContext.startActivity(intent)
        activity?.finish()
    }

    private fun navigateNext() {
        val intent=Intent(requireContext(), LoginAuthActivity::class.java)
        val bundle = Bundle()
        bundle.putString(Constant.USERNAME, userName)
        intent.putExtras(bundle)
        startActivity(intent)
        activity?.finish()
//        startActivity(Intent(context, DashboardActivity::class.java))
    }

    private fun getAgent(): Agent {
        val agent = Agent()
        agent.frstname = MyApplication.getPrefHelper().agentFName
        agent.mddlename = MyApplication.getPrefHelper().agentMName
        agent.lastname = MyApplication.getPrefHelper().agentLName
        agent.email = MyApplication.getPrefHelper().agentEmail
        agent.mobile = MyApplication.getPrefHelper().agentMobile
        agent.password = MyApplication.getPrefHelper().agentPassword
        agent.newPassword = ""
        agent.salutation = MyApplication.getPrefHelper().agentSalutation
        agent.agentid = MyApplication.getPrefHelper().agentID
        agent.agenttypeid = MyApplication.getPrefHelper().agentTypeID
        agent.ownrorgbrid = MyApplication.getPrefHelper().agentOwnerOrgBranchID
        agent.agentUserID = MyApplication.getPrefHelper().agentUserID
        return agent
    }

    interface Listener {
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun showProgressDialog()
        fun dismissDialog()
    }
}