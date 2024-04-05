package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.*
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.GetChildTabCount
import com.sgs.citytax.api.payload.SearchFilter
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.COMHotelDesFinances
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentAgentEntryBinding
import com.sgs.citytax.databinding.FragmentRejectionRemarksBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.PrefHelper
import com.sgs.citytax.util.isValidEmail
import kotlinx.android.synthetic.main.activity_my_profile.*

class AgentEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentAgentEntryBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_UPDATE_AGENT
    private var mAgent: CRMAgents? = null

    private var prefHelper: PrefHelper = MyApplication.getPrefHelper()
    private var mAgentTypes: MutableList<CRMAgentTypes>? = null
    private var mStaticValues: MutableList<ComComboStaticValues>? = null
    private var mUserOrgBranches: MutableList<UMXUserOrgBranches>? = null
    private var notes: List<COMNotes>? = null
    private var documents: List<COMDocumentReference>? = null
    private var hotelDesFinancesList: MutableList<COMHotelDesFinances>? = arrayListOf()

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mAgent = arguments?.getParcelable(Constant.KEY_AGENT)
        }
        //endregion
        setViews()
        bindSpinner()
        setListeners()
        fetchChildEntriesCount()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_agent_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun fetchCount(filterColumns: List<FilterColumn>, tableCondition: String, tableOrViewName: String, primaryKeyColumnName: String) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = tableOrViewName
        tableDetails.primaryKeyColumnName = primaryKeyColumnName
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = tableCondition
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                bindCounts(tableOrViewName, 0)
            }

            override fun onSuccess(response: Int) {
                bindCounts(tableOrViewName, response)
            }
        })
    }

    private fun bindCounts(tableOrViewName: String, count: Int) {
        when (tableOrViewName) {
            "COM_DocumentReferences" -> {
                mBinding.txtNumberOfDocuments.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue = "${mAgent?.AgentID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "TableName"
                filterColumn.columnValue = "CRM_Agents"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
            }
            "COM_Notes" -> {
                mBinding.txtNumberOfNotes.text = "$count"
            }
        }
    }

    private fun setViews() {
        mBinding.spnAgentType.isEnabled = true
        mBinding.spnAgentAdminstrativOffice.isEnabled = true
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT) {
            mAgent.let {
                if (mAgent?.StatusCode == Constant.AgentStatus.ACTIVE.value || mAgent?.StatusCode == Constant.AgentStatus.VERIFIED.value) {
                    mBinding.btnSave.visibility = View.GONE
                    mBinding.viewNotes.visibility = View.GONE
                    mBinding.llRejectVerify.visibility = View.GONE
                } else {
                    mBinding.btnSave.visibility = View.GONE
                    mBinding.viewNotes.visibility = View.GONE
                    mBinding.llRejectVerify.visibility = View.VISIBLE
                }
            }
            setEnabled(false)

        }
        else if(fromScreen==Constant.QuickMenu.QUICK_MENU_UPDATE_AGENT){
            setEnabledForUpdate(false)
        }else if(fromScreen == Constant.QuickMenu.QUICK_MENU_VIEW_AGENT){
            //While Agent Live Tracking
            setEnabled(false)
            mBinding.btnSave.visibility = View.GONE
            mBinding.viewNotes.visibility = View.GONE
        }
    }

    private fun setEnabled(enabled: Boolean) {
        mBinding.spnAgentType.isEnabled = enabled
        mBinding.spnSalutation.isEnabled = enabled
        mBinding.edtFirstName.isEnabled = enabled
        mBinding.edtMiddleName.isEnabled = enabled
        mBinding.edtLastName.isEnabled = enabled
        mBinding.edtEmail.isEnabled = enabled
        mBinding.spnTelephoneCode.isEnabled = enabled
        mBinding.edtMobileNo.isEnabled = enabled
        mBinding.spnAgentType.isEnabled = enabled
        mBinding.spnAgentAdminstrativOffice.isEnabled = enabled
        mBinding.spnHotelDesFinance.isEnabled = enabled
    }

    private fun setEnabledForUpdate(enabled: Boolean) {
        mBinding.spnAgentType.isEnabled = enabled
        mBinding.spnAgentAdminstrativOffice.isEnabled = enabled
        mBinding.spnHotelDesFinance.isEnabled = enabled
    }

    private fun bindSpinner() {
        var tableName = "CRM_Agents"
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT)
            tableName = "CRM_AgentsVerification"

        mListener?.showProgressDialog(R.string.msg_authenticating)
        APICall.getCorporateOfficeLOVValues(tableName, object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()
                mAgentTypes = response.crmAgentTypes
                mStaticValues = response.comboStaticValues
                mUserOrgBranches = response.userOrgBranches
                hotelDesFinancesList = response.hotelDesFinances


                if (response.countryMaster.isNotEmpty()) {
                    val countryCode: String? = "BFA"
                    val countries: MutableList<COMCountryMaster> = arrayListOf()
                    var index = -1
                    val telephonicCodes: ArrayList<Int> = arrayListOf()
                    for (country in response.countryMaster) {
                        country.telephoneCode?.let {
                            if (it > 0) {
                                countries.add(country)
                                telephonicCodes.add(it)
                                if (index <= -1 && countryCode == country.countryCode)
                                    index = countries.indexOf(country)
                            }
                        }
                    }
                    if (index <= -1) index = 0
                    if (telephonicCodes.size > 0) {
                        val telephonicCodeArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, telephonicCodes)
                        mBinding.spnTelephoneCode.adapter = telephonicCodeArrayAdapter

                        if (mAgent?.telephoneCode != null) {
                            mBinding.spnTelephoneCode.setSelection(telephonicCodes.indexOf(mAgent?.telephoneCode as Int))
                        } else {
                            mBinding.spnTelephoneCode.setSelection(index)
                        }
                    } else mBinding.spnTelephoneCode.adapter = null

                }

                if (response.comboStaticValues.isNotEmpty() && response.comboStaticValues.isNotEmpty()) {
                    mStaticValues?.add(0, ComComboStaticValues("-1", getString(R.string.select)))
                    val salutationAdapter = ArrayAdapter<ComComboStaticValues>(activity!!.applicationContext, android.R.layout.simple_list_item_1, mStaticValues!!)
                    mBinding.spnSalutation.adapter = salutationAdapter
                } else mBinding.spnSalutation.adapter = null

                if (response.hotelDesFinances.isNotEmpty() && response.hotelDesFinances.isNotEmpty()) {
                    hotelDesFinancesList?.add(
                        0,
                        COMHotelDesFinances(0,null,getString(R.string.select))
                    )
                    val hotelDesFinanceAdapter = ArrayAdapter<COMHotelDesFinances>(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        hotelDesFinancesList!!
                    )
                    mBinding.spnHotelDesFinance.adapter = hotelDesFinanceAdapter
                    //region select In active status by default
                    var pos = 0
                    for ((index, obj) in hotelDesFinancesList!!.withIndex()) {
                        if (mAgent?.hotelDesFinanceID == obj.hotelDesFinanceID) {
                            pos = index
                            break
                        }
                    }
                    mBinding.spnHotelDesFinance.setSelection(pos, true)
                  } else mBinding.spnHotelDesFinance.adapter = null

                if (response.userOrgBranches.isNotEmpty() && response.userOrgBranches.isNotEmpty()) {
                    mUserOrgBranches?.add(0, UMXUserOrgBranches(getString(R.string.select), userOrgBranchID = -1))
                    val adminOfficeAdapter = ArrayAdapter<UMXUserOrgBranches>(activity!!.applicationContext, android.R.layout.simple_list_item_1, mUserOrgBranches!!)
                    mBinding.spnAgentAdminstrativOffice.adapter = adminOfficeAdapter
                } else mBinding.spnAgentAdminstrativOffice.adapter = null

                if (response.crmAgentTypes.isNotEmpty() && response.crmAgentTypes.size > 0) {
                    mAgentTypes?.add(0, CRMAgentTypes(-1, getString(R.string.select)))
                    val agentTypeAdapter = ArrayAdapter<CRMAgentTypes>(activity!!.applicationContext, android.R.layout.simple_list_item_1, mAgentTypes!!)
                    mBinding.spnAgentType.adapter = agentTypeAdapter
                    var position = 0
                    for ((index, agentType: CRMAgentTypes) in mAgentTypes!!.withIndex()) {
                        if (agentType.AgentType.equals(activity?.intent?.getStringExtra(Constant.KEY_AGENT_TYPE)?.replace(" Registration", ""), true)) {
                            position = index
                            break
                        }
                    }
                    mBinding.spnAgentType.setSelection(position)
                } else mBinding.spnAgentType.adapter = null

                bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)

            }
        })
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
        mBinding.btnVerify.setOnClickListener(this)
        mBinding.btnReject.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnSave -> {
                if (validateView()) {
                    if (mAgent?.AgentID ?: 0 > 0) {

                    }
                    saveAgent(view, null)
                }
            }
            R.id.llDocuments -> {
                when {
                    mAgent != null && mAgent?.AgentID ?: 0 > 0 -> {
                        val fragment = DocumentsMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, mAgent?.AgentID ?: 0)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)

                        mListener?.showToolbarBackButton(R.string.documents)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveAgent(view, null)
                    }
                }
            }
            R.id.llNotes -> {
                when {
                    mAgent != null && mAgent?.AgentID ?: 0 > 0 -> {
                        val fragment = NotesMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, mAgent?.AgentID ?: 0)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)

                        mListener?.showToolbarBackButton(R.string.notes)
                        mListener?.addFragment(fragment, true)
                    }

                    validateView() -> {
                        saveAgent(view, null)
                    }
                }
            }

            R.id.btnVerify -> {
                val crmAgents = getAgent()
                crmAgents.StatusCode = Constant.AgentStatus.VERIFIED.value
                crmAgents.verifiedByUserID = prefHelper.agentUserID
                saveAgent(view, crmAgents)
            }

            R.id.btnReject -> {
                val layoutInflater = LayoutInflater.from(context)
                val binding = DataBindingUtil.inflate<FragmentRejectionRemarksBinding>(layoutInflater, R.layout.fragment_rejection_remarks, null, false)
                mListener?.showAlertDialog(resources.getString(R.string.remarks), DialogInterface.OnClickListener { dialog, _ ->
                    if (binding.edtRemarks.text.toString().isNotEmpty()) {
                        val crmAgents = getAgent()
                        crmAgents.StatusCode = Constant.AgentStatus.REJECTED.value
                        crmAgents.remarks = binding.edtRemarks.text.toString()
                        crmAgents.verifiedByUserID = prefHelper.agentUserID
                        saveAgent(view, crmAgents)
                    } else {
                        mListener?.showSnackbarMsg(getString(R.string.msg_enter_remarks))
                    }
                }, null, binding.root)
            }
        }
    }

    private fun saveAgent(view: View?, crmAgents: CRMAgents?) {
        mListener?.showProgressDialog(R.string.msg_authenticating)
        var getAgentData = crmAgents
        getAgentData = if (getAgentData == null)
            getAgent()
        else
            crmAgents

        APICall.insertAgent(getAgentData, notes, documents, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mListener?.dismissDialog()

                if (mAgent == null)
                    mAgent = CRMAgents()

                //region Show message
                when (view?.id) {
                    R.id.btnVerify -> {
                        mListener?.showToast(R.string.verified_sucessfully)
                    }

                    R.id.btnReject -> {
                        mListener?.showToast(R.string.rejected_sucessfully)
                    }

                    R.id.btnSave -> {
                        if (mAgent?.AgentID ?: 0 > 0) {
                            mListener?.showToast(R.string.msg_record_update_success)
                        } else
                            mListener?.showToast(R.string.msg_record_save_success)
                    }

                    else -> {

                    }
                }
                //endregion

                //region Update AgentID
                if (response != 0.0)
                    mAgent?.AgentID = response.toInt()
                //endregion

                //region NavigateTo
                when (view?.id) {
                    R.id.llDocuments, R.id.llNotes -> {
                        onClick(view)
                    }
                    else -> {
                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_AGENT)
                            mListener?.finish()
                        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT) {
                            Handler().postDelayed({
                                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                                mListener?.popBackStack()
                            }, 500)
                        }
                    }
                }
                //endregion
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun getAgent(): CRMAgents {
        var crmAgent = CRMAgents()
        if (mAgent != null && mAgent!!.AgentID != 0) {
            crmAgent = mAgent!!
        }
        if (!TextUtils.isEmpty(mBinding.edtFirstName.text.toString().trim())) {
            crmAgent.FirstName = mBinding.edtFirstName.text.toString().trim()
        }
        if (!TextUtils.isEmpty(mBinding.edtMiddleName.text.toString().trim())) {
            crmAgent.MiddleName = mBinding.edtMiddleName.text.toString().trim()
        }
        else{
            crmAgent.MiddleName=""
        }
        if (!TextUtils.isEmpty(mBinding.edtLastName.text.toString().trim())) {
            crmAgent.LastName = mBinding.edtLastName.text.toString().trim()
        }
        else{
            crmAgent.LastName=""
        }
        if (!TextUtils.isEmpty(mBinding.edtMobileNo.text.toString().trim())) {
            crmAgent.mobileNo = mBinding.edtMobileNo.text.toString().trim()
        }
        if (!TextUtils.isEmpty(mBinding.edtEmail.text.toString().trim())) {
            crmAgent.email = mBinding.edtEmail.text.toString().trim()
        }

        if (mBinding.spnSalutation.selectedItem != null  && spnSalutation.selectedItemPosition > 0) {
            crmAgent.Salutation = (mBinding.spnSalutation.selectedItem as ComComboStaticValues).comboValue
        }else{
            crmAgent.Salutation=""
        }

        if (mBinding.spnAgentType.selectedItem != null) {
            crmAgent.AgentTypeID = (mBinding.spnAgentType.selectedItem as CRMAgentTypes).AgentTypeID
        }

        if (mBinding.spnAgentAdminstrativOffice.selectedItem != null) {
            crmAgent.OwnerOrgBranchID = (mBinding.spnAgentAdminstrativOffice.selectedItem as UMXUserOrgBranches).userOrgBranchID
        }

        if (mBinding.spnTelephoneCode.selectedItem != null) {
            crmAgent.telephoneCode = mBinding.spnTelephoneCode.selectedItem as Int?
        }

        if (mAgent == null || mAgent?.createdByAccountID == 0)
        //send accountID as createdByAccountID
            crmAgent.createdByAccountID = prefHelper.accountId

       /* if (mAgent == null || mAgent?.OwnerOrgBranchID == 0)
            crmAgent.OwnerOrgBranchID = prefHelper.agentOwnerOrgBranchID*/

        if (mAgent == null || mAgent?.ParentAgentID == 0) {
            crmAgent.ParentAgentID = if (prefHelper.superiorTo.isEmpty())
                prefHelper.parentAgentID
            else prefHelper.agentID
        }

        if (mAgent == null || mAgent?.AgentID ?: 0 == 0) {
            crmAgent.StatusCode = Constant.AgentStatus.INACTIVE.value
        }

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_AGENT && mAgent != null) {
            crmAgent.assignedZoneCode = mAgent!!.assignedZoneCode
        }

        if (mBinding.spnHotelDesFinance.selectedItem != null) {
            val hotelDesFianance: COMHotelDesFinances =
                mBinding.spnHotelDesFinance.selectedItem as COMHotelDesFinances
            for (obj in hotelDesFinancesList!!.iterator()) {
                if (hotelDesFianance.hotelDesFinanceID == obj.hotelDesFinanceID) {
                    crmAgent.hotelDesFinanceID = obj.hotelDesFinanceID
                    break
                }
            }
        }

        return crmAgent
    }

    private fun validateView(): Boolean {
        /*  if (mBinding.spnSalutation.selectedItem == null || "-1" == (mBinding.spnSalutation.selectedItem as ComComboStaticValues).comboCode) {
              mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.salutation))
              return false
          }*/
        if (TextUtils.isEmpty(mBinding.edtFirstName.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.first_name))
            mBinding.edtFirstName.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(mBinding.edtEmail.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.email_id))
            mBinding.edtEmail.requestFocus()
            return false
        }

        if (!isValidEmail(mBinding.edtEmail.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide_valid) + " " + getString(R.string.email_id))
            mBinding.edtEmail.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(mBinding.edtMobileNo.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.mobile))
            mBinding.edtMobileNo.requestFocus()
            return false
        }



        if (mBinding.spnTelephoneCode.selectedItem == null) {
            return false
        }


        if (mBinding.spnAgentType.selectedItem == null || -1 == (mBinding.spnAgentType.selectedItem as CRMAgentTypes).AgentTypeID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.agent_type))
            return false
        }

        if (mBinding.spnAgentAdminstrativOffice.selectedItem == null || -1 == (mBinding.spnAgentAdminstrativOffice.selectedItem as UMXUserOrgBranches).userOrgBranchID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.administration_office))
            return false
        }

        return true
    }

    fun bindData() {
        if (mAgent != null) {
            mBinding.edtFirstName.setText(mAgent?.FirstName ?: "")
            mBinding.edtMiddleName.setText(mAgent?.MiddleName ?: "")
            mBinding.edtLastName.setText(mAgent?.LastName ?: "")
            mBinding.edtEmail.setText(mAgent?.email ?: "")
            mBinding.edtMobileNo.setText(mAgent?.mobileNo ?: "")
            mBinding.tvParentAgent.text = (mAgent?.ParentAgentName ?: "")

            if (mStaticValues != null) {
                for ((index, obj) in mStaticValues?.withIndex()!!) {
                    if (!obj.comboValue.isNullOrEmpty())
                        if (mAgent?.Salutation == (obj.comboValue!!)) {
                            mBinding.spnSalutation.setSelection(index)
                        }
                }
            }

            if (mAgentTypes != null) {
                for ((index, obj) in mAgentTypes!!.withIndex()) {
                    if (mAgent!!.AgentTypeID == obj.AgentTypeID) {
                        mBinding.spnAgentType.setSelection(index)
                    }
                }
            }

            if (mUserOrgBranches != null) {
                for ((index, obj) in mUserOrgBranches!!.withIndex()) {
                    if (mAgent!!.OwnerOrgBranchID == obj.userOrgBranchID) {
                        mBinding.spnAgentAdminstrativOffice.setSelection(index)
                    }
                }
            }

        } else {
            mBinding.tvParentAgent.text = prefHelper.agentName
            // mBinding.tvOwner.text = prefHelper.agentOwnerOrgBranchID.toString()
        }
    }

    private fun fetchChildEntriesCount() {
        mAgent?.AgentID?.let {
            val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
            var filterColumn = FilterColumn()
            filterColumn.columnName = "PrimaryKeyValue"
            filterColumn.columnValue = "${mAgent?.AgentID}"
            filterColumn.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            filterColumn = FilterColumn()
            filterColumn.columnName = "TableName"
            filterColumn.columnValue = "CRM_Agents"
            filterColumn.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            fetchCount(listFilterColumn, "AND", "COM_DocumentReferences", "DocumentReferenceID")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fetchChildEntriesCount()
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun showProgressDialog(message: Int)
        fun showSnackbarMsg(message: String)
        fun dismissDialog()
        fun finish()
        fun popBackStack()
        fun showToast(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(msg: String, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener?, view: View)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener?, view: View?)
    }

}