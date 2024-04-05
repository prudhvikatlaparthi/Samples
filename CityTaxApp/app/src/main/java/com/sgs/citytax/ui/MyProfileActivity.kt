package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.Agent
import com.sgs.citytax.api.payload.StoreAgentDetails
import com.sgs.citytax.api.response.COMHotelDesFinances
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityMyProfileBinding
import com.sgs.citytax.model.ComComboStaticValues
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.doDecrypt
import com.sgs.citytax.util.doEncrypt
import com.sgs.citytax.util.formatWithPrecision
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityMyProfileBinding
    var agentId: Int = 0
    var agentTypeId: Int = 0
    var ownerOrgBranchId: Int = 0
    var agentUserID: String? = null
    var salutationList:ArrayList<ComComboStaticValues> = arrayListOf()
    private var hotelDesFinancesList: MutableList<COMHotelDesFinances>? = arrayListOf()
    var zoneCode:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)
        showToolbarBackButton(R.string.title_my_profile)
        showViewsFirstTime()
        getSalutationData()
        setListeners()
    }

    private fun getSalutationData() {
        APICall.getCorporateOfficeLOVValues("UMX_Users", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                if (response!=null) {
                    salutationList = response.comboStaticValues as ArrayList<ComComboStaticValues>
                    hotelDesFinancesList = response.hotelDesFinances as ArrayList<COMHotelDesFinances>
                    salutationList.add(0, ComComboStaticValues("",getString(R.string.select)))
                    binding.spnSalutation.adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, salutationList)
                    binding.spnSalutation.setSelection(0)
                    hotelDesFinancesList?.add(
                        0,
                        COMHotelDesFinances(0,null,getString(R.string.select))
                    )
                    binding.spnHotelDesFinance.adapter = ArrayAdapter<COMHotelDesFinances>(applicationContext, android.R.layout.simple_list_item_1, hotelDesFinancesList!!)
                    binding.spnHotelDesFinance.setSelection(0)
                    bindData()
                }
            }

            override fun onFailure(message: String) {
                showAlertDialog(message)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_change_password, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_CHANGE_PASSWORD) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra("Password")) {
                    val password = data.getStringExtra("Password")
                    binding.edtPassword.setText(password)
                }
            }
        }
    }*/

    private fun showViewsFirstTime() {
        when(prefHelper.agentTypeCode){
            Constant.AgentTypeCode.LEA.name,
            Constant.AgentTypeCode.LEI.name,
            Constant.AgentTypeCode.LES.name->{
                binding.txtOwnerType.hint=resources.getString(R.string.police_station)
            }
            else->{
                binding.txtOwnerType.hint=resources.getString(R.string.admin_office)
            }
        }
        if (prefHelper.agentTypeCode.equals(Constant.AgentTypeCode.ASO.name)) {
            binding.layoutSalutationSpinner.visibility = View.GONE
        } else {
            binding.layoutSalutationSpinner.visibility = View.VISIBLE
            binding.edtCollectionAmount.visibility = View.VISIBLE
        }

        if(prefHelper.agentTypeCode.equals(Constant.AgentTypeCode.ASA.name)||prefHelper.agentTypeCode.equals(Constant.AgentTypeCode.ASO.name)){
            binding.txtInputCommissionEarned.visibility = View.VISIBLE
            binding.txtInputCommissionDisb.visibility = View.VISIBLE
            binding.txtInputCommissionbalance.visibility = View.VISIBLE
        }

        binding.spnSalutation.isEnabled = false
        binding.spnHotelDesFinance.isEnabled = false
    }

    private fun bindData() {
        showProgressDialog()
        APICall.getAgentDetailsValues(prefHelper.accountId, object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                if (response.agentDetails.isNotEmpty()) {
                    val agentDetail = response.agentDetails[0]
                    binding.edtAgentType.setText(agentDetail.agenttype)
                    binding.edtOwner.setText(agentDetail.branchname)
                    binding.edtParentAgent.setText(agentDetail.parentagentname)
                    binding.edtMobileNo.setText(agentDetail.mobile)
                    binding.edtEmail.setText(agentDetail.email)
                    binding.edtLastName.setText(agentDetail.lastname)
                    binding.edtMiddleName.setText(agentDetail.middlename)
                    binding.edtFirstName.setText(agentDetail.firstname)
                    //  zoneCode=agentDetail.assignedZoneCode
                    if (agentDetail.telephonicCode != null)
                        binding.txtTelephoneCode.text = agentDetail.telephonicCode
                    else
                        binding.txtTelephoneCode.visibility = View.GONE
                    binding.edtPassword.setText(doDecrypt(agentDetail.password!!, prefHelper.getStaticToken()))
                    if (agentDetail.fromDate != null)
                        binding.edtStartDate.setText(agentDetail.fromDate)
                    else binding.edtStartDate.setText("")
                    if (agentDetail.toDate != null)
                        binding.edtEndDate.setText(agentDetail.toDate)
                    else binding.edtEndDate.setText("")
                    binding.edtTargetAmount.setText(agentDetail.targetAmount)

                    if (hotelDesFinancesList.isNullOrEmpty())
                        binding.spnHotelDesFinance.adapter = null
                    else {
                        hotelDesFinancesList?.add(
                            0,
                            COMHotelDesFinances(0,null,getString(R.string.select))
                        )
                        val hotelDesFinanceAdapter = ArrayAdapter<COMHotelDesFinances>(
                            applicationContext,
                            android.R.layout.simple_list_item_1,
                            hotelDesFinancesList!!
                        )
                        binding.spnHotelDesFinance.adapter = hotelDesFinanceAdapter
                        //region select In active status by default
                        var pos = 0
                        for ((index, obj) in hotelDesFinancesList!!.withIndex()) {
                            if (agentDetail?.hotelDesFinanceID == obj.hotelDesFinanceID) {
                                pos = index
                                break
                            }
                        }
                        binding.spnHotelDesFinance.setSelection(pos, true)
                        //endregion
                    }


                    if (prefHelper.agentTypeCode == Constant.AgentTypeCode.ASO.name) {
                        binding.edtCollectionAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15 + 15))
                        binding.edtCollectionAmount.setText(formatWithPrecision(agentDetail.AssociationCollection))
                        // binding.edtCollectionAmount.setText(agentDetail.AssociationCollection)
                    }
                    else {
                        binding.edtCollectionAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15 + 15))
                        binding.edtCollectionAmount.setText(formatWithPrecision(agentDetail.CollectionAmount))
                        //binding.edtCollectionAmount.setText(agentDetail.CollectionAmount)
                    }

                    binding.edtCommissionEarned.setText(formatWithPrecision(agentDetail.CommissionEarned))
                    binding.edtCommissionBalance.setText(formatWithPrecision(agentDetail.CommissionBalance))
                    binding.edtCommissionDisbursed.setText(formatWithPrecision(agentDetail.CommissionDisbursed))

                    agentId = agentDetail.agentid
                    agentTypeId = agentDetail.agenttypeid
                    ownerOrgBranchId = agentDetail.ownerorgbranchid
                    agentUserID = agentDetail.agentUserid

                    for ((index, value) in salutationList.withIndex()) {
                        if (value.comboValue == agentDetail.salutation) {
                            binding.spnSalutation.setSelection(index)
                            break
                        }
                    }
                } else {
                    binding.spnSalutation.adapter = null
                }
                dismissDialog()
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })

    }

    private fun setListeners() {
        binding.btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                val storeAgentDetails = StoreAgentDetails()
                storeAgentDetails.agent = getAgent()
                if (validateView()) {
                    showProgressDialog(R.string.msg_please_wait)
                    APICall.storeAgentDetails(storeAgentDetails, object : ConnectionCallBack<Boolean> {
                        override fun onSuccess(response: Boolean) {
                            hideKeyBoard()
                            dismissDialog()
                            finish()
                        }

                        override fun onFailure(message: String) {
                            dismissDialog()
                            showAlertDialog(message)
                        }
                    })
                }
            }
        }
    }

    private fun getAgent(): Agent {
        val agent = Agent()

        if (edtFirstName.text != null && !TextUtils.isEmpty(edtFirstName.text.toString()))
            agent.frstname = edtFirstName.text.toString().trim()
        if (edtMiddleName.text != null && !TextUtils.isEmpty(edtMiddleName.text.toString()))
            agent.mddlename = edtMiddleName.text.toString().trim()
        if (edtLastName.text != null && !TextUtils.isEmpty(edtLastName.text.toString()))
            agent.lastname = edtLastName.text.toString().trim()
        if (edtEmail.text != null && !TextUtils.isEmpty(edtEmail.text.toString()))
            agent.email = edtEmail.text.toString().trim()
        if (edtMobileNo.text != null && !TextUtils.isEmpty(edtMobileNo.text.toString()))
            agent.mobile = edtMobileNo.text.toString().trim()
        if (binding.edtPassword.text != null && !TextUtils.isEmpty(binding.edtPassword.text.toString()))
            agent.password = doEncrypt(binding.edtPassword.text.toString(), prefHelper.getStaticToken())

        agent.newPassword = ""
        if (spnSalutation?.selectedItem != null && spnSalutation.selectedItemPosition > 0)
            agent.salutation = spnSalutation?.selectedItem.toString().trim()
        agent.agentid = agentId
        agent.agenttypeid = agentTypeId
        agent.ownrorgbrid = ownerOrgBranchId
        agent.agentUserID = agentUserID
        //agent.assignedZoneCode=zoneCode

        return agent
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()

        if (item.itemId == R.id.action_chnage_password) {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.putExtra(Constant.KEY_AGENT, getAgent())
            intent.putExtra(Constant.KEY_FROM_PROFILE,true)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun validateView(): Boolean {
        /*  if (binding.edtPassword.text != null && TextUtils.isEmpty(binding.edtPassword.text.toString())) {
              showSnackbarMsg(R.string.msg_password_empty)
              binding.edtPassword.requestFocus()
              return false
          }*/

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString().trim()).matches()) {
            showToast(getString(R.string.msg_provide_valid) + " " + getString(R.string.email))
            binding.edtEmail.requestFocus()
            return false
        }

        /*  if (binding.spnSalutation.selectedItem == null || binding.spnSalutation.selectedItemPosition == 0) {
              showToast(getString(R.string.msg_provide) + " " + (getString(R.string.salutation)))
              return false
          }*/

        return true
    }
}
