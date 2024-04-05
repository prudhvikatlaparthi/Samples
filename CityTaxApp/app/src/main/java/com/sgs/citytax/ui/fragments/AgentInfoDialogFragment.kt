package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.AgentLocations
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentAgentInfoDialogBinding
import com.sgs.citytax.model.CRMAgentDetails
import com.sgs.citytax.model.CRMAgents
import com.sgs.citytax.ui.AgentOnboardingActivity
import com.sgs.citytax.util.*

class AgentInfoDialogFragment : DialogFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentAgentInfoDialogBinding
    private var mListener: Listener? = null
    private var fromScreenCode: Any? = null
    private var agentInfo: AgentLocations? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(agentInfo: AgentLocations?, fromScreen: Any?) =
            AgentInfoDialogFragment().apply {
                this.agentInfo = agentInfo
                this.fromScreenCode = fromScreen
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_agent_info_dialog, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AlertDialogTheme)
    }

    fun initComponents() {
        bindData()
        setListeners()
    }

    private fun bindData() {
        val accountId: Int = MyApplication.getPrefHelper().accountId

        agentInfo?.agentAccountID?.let {
            if(it != accountId) {
                mBinding.llAgentButton.visibility = View.VISIBLE
            }
        }

        if (agentInfo != null) {
            mBinding.txtTitle.text = agentInfo?.agent
            mBinding.txtAgentType.text = agentInfo?.agentType
            mBinding.txtAgentCode.text = agentInfo?.agentCode
            mBinding.txtMobile.text = agentInfo?.mobile
            mBinding.txtEmail.text = agentInfo?.email
            //zone
            val zone: String =
                if (agentInfo?.assignedZoneCode == Constant.AssignedZoneSectorCode.AZ.name) getString(
                    R.string.all_zone
                ) else if (agentInfo?.assignedZoneCode == Constant.AssignedZoneSectorCode.NZ.name) getString(
                    R.string.no_zone
                ) else agentInfo?.zone ?: ""
            //sector
            val sector: String =
                if (agentInfo?.assignedZoneCode == Constant.AssignedZoneSectorCode.AZ.name) getString(
                    R.string.all_sector
                )
                else if (agentInfo?.assignedZoneCode == Constant.AssignedZoneSectorCode.NZ.name) getString(
                    R.string.no_sector
                ) else agentInfo?.sector ?: ""
            //

            mBinding.txtZone.text = zone
            mBinding.txtSector.text = sector
            mBinding.txtReportingManager.text = agentInfo?.reportingManager

            if (agentInfo?.agentTypeCode == Constant.AgentTypeCode.ISP.name ||
                agentInfo?.agentTypeCode == Constant.AgentTypeCode.SPR.name ||
                agentInfo?.agentTypeCode == Constant.AgentTypeCode.LEI.name ||
                agentInfo?.agentTypeCode == Constant.AgentTypeCode.LES.name ||
                agentInfo?.agentTypeCode == Constant.AgentTypeCode.MCA.name ||
                agentInfo?.agentTypeCode == Constant.AgentTypeCode.LEA.name ||
                MyApplication.getPrefHelper().isParkingMunicipalAgent()||
                MyApplication.getPrefHelper().isParkingThirdPartyAgent()) {

                    mBinding.llCashInHand.visibility = View.VISIBLE
                    mBinding.txtCashInHand.text = formatWithPrecision(agentInfo?.cashInHand)

            } else if (agentInfo?.agentTypeCode == Constant.AgentTypeCode.TPA.name ||
                agentInfo?.agentTypeCode == Constant.AgentTypeCode.PPS.name) {

                    mBinding.llMWalletBalance.visibility = View.VISIBLE
                    mBinding.llCommissionEarned.visibility = View.VISIBLE
                    mBinding.llCommissionDisbursed.visibility = View.VISIBLE
                    mBinding.llCommissionBalance.visibility = View.VISIBLE

                    mBinding.txtMWalletBalance.text = formatWithPrecision(agentInfo?.municipalWalletBalance)
                    mBinding.txtCommissionEarned.text = formatWithPrecision(agentInfo?.commissionEarned)
                    mBinding.txtCommissionDisbursed.text = formatWithPrecision(agentInfo?.commissionsDisbursed)
                    mBinding.txtCommissionBalance.text = formatWithPrecision(agentInfo?.commissionsBalance)
            }
        }
    }

    private fun setListeners() {
        mBinding.btnAgent.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mBinding.btnAgent.id -> {
                mListener?.onClick()

                if (fromScreenCode == Constant.NavigationMenu.NAVIGATION_LOCATION) {
                    agentInfo?.agentAccountID?.let {
                        getCRMAgentDetails(it)
                    }
                }
            }
        }
    }

    //fetch selected agent details and navigate to update Screen
    private fun getCRMAgentDetails(agentAccountID: Int) {
        mListener?.showProgressDialog()
        APICall.getAgentDetailsValues(agentAccountID, object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                if (response.agentDetails.isNotEmpty()) {
                    val agentDetail = response.agentDetails[0]

                    //acquired CRMAgentDetails from api
                    //pass data to CRMAgents
                    val crmAgents: CRMAgents = agentDetail.toCRMAgent()

                    mListener?.dismissDialog()

                    //Navigate to update agent screen
                    val intent = Intent(context, AgentOnboardingActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_VIEW_AGENT)
                    intent.putExtra(Constant.KEY_AGENT, crmAgents)
                    startActivity(intent)

                    dismiss()
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    //Fetch data to CRMAgents from CRMAgentDetails
    fun CRMAgentDetails.toCRMAgent() = CRMAgents(
        AgentID = agentid,
        AgentTypeID = agenttypeid,
        AgentType = agenttype,
        Salutation = salutation,
        FirstName = firstname,
        MiddleName = middlename,
        LastName = lastname,
        ParentAgentID = parentagentid,
        ParentAgentName = parentagentname,
        AgentUserID = agentUserid,
        OwnerOrgBranchID = ownerorgbranchid,
        email = email,
        mobileNo = mobile,
        StatusCode = statusCode,
        Status = status,
        agentName = agentname,
        agentCode = agentCode,
        isLoading = false,
        remarks = remarks,
        verifiedByUserID = null,
        createdByAccountID = createdByAccountID,
        telephoneCode = telephonicCode?.toInt(),
        assignedZoneCode = assignedZoneCode,
        hotelDesFinanceID = hotelDesFinanceID,
    )

    interface Listener {
        fun showAlertDialog(message: String)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun onClick()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
    }
}