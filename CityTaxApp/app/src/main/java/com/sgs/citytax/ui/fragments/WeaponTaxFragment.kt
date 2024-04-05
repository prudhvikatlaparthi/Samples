package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.BusinessDueSummaryResults
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentWeaponTaxBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.IndividualTaxSummaryActivity
import com.sgs.citytax.util.*
import java.util.*

class WeaponTaxFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentWeaponTaxBinding
    private var mListener: Listener? = null
    private var mWeaponTypes: MutableList<CRMWeaponTypes>? = arrayListOf()
    private var mWeaponExemptionReasons: MutableList<CRMWeaponExemptionReasons>? = arrayListOf()
    private var sycoTaxID = ""
    private var mCustomer: BusinessOwnership? = null
    private var mDocumentsList: ArrayList<COMDocumentReference>? = arrayListOf()
    private var mComNotesList: ArrayList<COMNotes> = arrayListOf()
    private var mWeapon: Weapon? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE

    private var primaryKey = 0
    private var acctID = 0 //weaponID

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_weapon_tax, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {

        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_WEAPON_TAX))
                mWeapon = it.getParcelable(Constant.KEY_WEAPON_TAX)
            sycoTaxID = arguments?.getString(Constant.KEY_SYCO_TAX_ID) ?: ""
            if(it.containsKey(Constant.KEY_PRIMARY_KEY))
                primaryKey = it.getInt(Constant.KEY_PRIMARY_KEY, 0)
            if(it.containsKey(Constant.KEY_ACCOUNT_ID))
                acctID = it.getInt(Constant.KEY_ACCOUNT_ID, 0)

        }
        bindDataFromAPI()
        //bindSpinner()
        initListeners()
    }

    private fun bindDataFromAPI() {
        APICall.getWeaponList(primaryKey, "VU_CRM_Weapons", acctID, object : ConnectionCallBack<List<Weapon>> {
            override fun onSuccess(response: List<Weapon>) {
                mListener?.dismissDialog()

                mWeapon = response[0]
                bindSpinner()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                bindSpinner()
            }
        })
    }

    private fun initListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.tvFixedExpenses.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
        mBinding.llInitialOutstanding.setOnClickListener(this)
        mBinding.btnGet.setOnClickListener(this)
        mBinding.edtCustomerName.setOnClickListener {
            showCustomers()
        }
        mBinding.edtPhoneNumber.setOnClickListener {
            showCustomers()
        }
        mBinding.edtEmailId.setOnClickListener {
            showCustomers()
        }
        mBinding.tvCreateCustomer.setOnClickListener {

            val fragment = BusinessOwnerEntryFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)
            mListener?.showToolbarBackButton(R.string.citizen)
            mListener?.addFragment(fragment, true)
        }
        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX) {
            mBinding.llCreateCustomer.visibility = View.GONE
            mBinding.edtCustomerName.isEnabled = false
            mBinding.edtPhoneNumber.isEnabled = false
            mBinding.edtEmailId.isEnabled = false
            mBinding.edtCustomerName.setText(ObjectHolder.registerBusiness.vuCrmAccounts?.accountName)
            mBinding.edtPhoneNumber.setText(ObjectHolder.registerBusiness.vuCrmAccounts?.phone)
            mBinding.edtEmailId.setText(ObjectHolder.registerBusiness.vuCrmAccounts?.email)
            mBinding.edtCountry.setText(ObjectHolder.registerBusiness.geoAddress.country)
            mBinding.edtState.setText(ObjectHolder.registerBusiness.geoAddress.state)
            mBinding.edtCity.setText(ObjectHolder.registerBusiness.geoAddress.city)
            mBinding.edtZone.setText(ObjectHolder.registerBusiness.geoAddress.zone)
            mBinding.edtSector.setText(ObjectHolder.registerBusiness.geoAddress.sector)
            mBinding.edtStreet.setText(ObjectHolder.registerBusiness.geoAddress.street)
            mBinding.edtPlot.setText(ObjectHolder.registerBusiness.geoAddress.plot)
            mBinding.edtBlock.setText(ObjectHolder.registerBusiness.geoAddress.block)
            mBinding.edtDoorNo.setText(ObjectHolder.registerBusiness.geoAddress.doorNo)
            mBinding.edtZipCode.setText(ObjectHolder.registerBusiness.geoAddress.zipCode)

        }
    }

    private fun saveWeapon(payload: StoreWeapon, view: View?) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.storeWeapons(payload, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                if (mWeapon == null)
                    mWeapon = Weapon()
                if (response != 0) mWeapon?.weaponID = response

                when (view?.id) {
                    R.id.llDocuments -> onClick(view)
                    R.id.llNotes -> onClick(view)
                    R.id.llInitialOutstanding -> onClick(view)
                    else -> {
                        if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_WEAPON_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX)
                            mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))
                        else if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX)
                            mListener?.showSnackbarMsg(getString(R.string.msg_record_update_success))

                        Handler().postDelayed({
                            navigateToSummary()
                            mListener?.finish()
                        }, 500)

                    }
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })

    }

    private fun navigateToSummary() {
        val intent = Intent(context, IndividualTaxSummaryActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, mBinding.edtSycoTaxID.text.toString())
        startActivity(intent)
    }

    private fun getPayload(): StoreWeapon {
        val weapon = Weapon()
        mWeapon?.let {
            weapon.weaponID = it.weaponID
        }

        if (mBinding.spnWeaponType.selectedItem != null) {
            val weaponType: CRMWeaponTypes = mBinding.spnWeaponType.selectedItem as CRMWeaponTypes
            if (weaponType.weaponTypeID != 0) {
                weapon.weaponTypeID = weaponType.weaponTypeID
            }
        }
        if (mBinding.spnExemptionReason.selectedItem != null) {
            val exemptionReason: CRMWeaponExemptionReasons = mBinding.spnExemptionReason.selectedItem as CRMWeaponExemptionReasons
            if (exemptionReason.weaponExemptionReasonID != 0) {
                weapon.weaponExemptionReasonID = exemptionReason.weaponExemptionReasonID
            }
        }
        weapon.weaponSycotaxID = mBinding.edtSycoTaxID.text.toString().trim()
        weapon.serialNo = mBinding.edtSerialNumber.text.toString().trim()
        weapon.make = mBinding.edtMake.text.toString().trim()
        weapon.model = mBinding.edtModel.text.toString().trim()
        weapon.registrationDate = serverFormatDate(mBinding.edtRegistrationDate.text.toString().trim())
        weapon.purposeOfPossession = mBinding.edtPurposeOfPossession.text.toString().trim()
        weapon.description = mBinding.edtDescription.text.toString().trim()

        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX) {
            ObjectHolder.registerBusiness.vuCrmAccounts?.accountId?.let {
                weapon.accountID = it
            }
        } else {
            mCustomer?.accountID?.let {
                weapon.accountID = it
            }
        }


        if (mBinding.chkActive.isChecked)
            weapon.active = "Y"
        else
            weapon.active = "N"

        val storeWeapons = StoreWeapon()
        storeWeapons.weapons = weapon
        storeWeapons.attachment = mDocumentsList
        storeWeapons.note = mComNotesList

        return storeWeapons
    }

    private fun validateView(): Boolean {
        if (mBinding.spnWeaponType.selectedItem == null || mBinding.spnWeaponType.selectedItemPosition == 0) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + (getString(R.string.weapon_type)))
            return false
        }
        if (mBinding.edtSerialNumber.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.serial_number))
            return false
        }
        if (mBinding.edtRegistrationDate.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.registration_date))
            return false
        }
        if (mBinding.edtCustomerName.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.owner_name))
            return false
        }
        if (mBinding.edtPurposeOfPossession.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.purpose_of_possession))
            return false
        }
        return true

    }

    private fun showCustomers() {
        val fragment = BusinessOwnerSearchFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
        fragment.arguments = bundle
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
        mListener?.showToolbarBackButton(R.string.citizen)
        mListener?.addFragment(fragment, true)
    }


    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_Weapons", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mWeaponTypes = response.weaponTypes
                mWeaponExemptionReasons = response.weaponExemptionReasons

                if (mWeaponTypes != null && mWeaponTypes!!.isNotEmpty()) {
                    mWeaponTypes?.add(0, CRMWeaponTypes(0, getString(R.string.select), "-1", "-1"))
                    mWeaponTypes?.let {
                        mBinding.spnWeaponType.adapter = ArrayAdapter<CRMWeaponTypes>(activity!!, android.R.layout.simple_spinner_dropdown_item, it)
                    }

                    mWeaponExemptionReasons?.add(0, CRMWeaponExemptionReasons(0, getString(R.string.select), "-1"))
                    mWeaponExemptionReasons?.let {
                        mBinding.spnExemptionReason.adapter = ArrayAdapter<CRMWeaponExemptionReasons>(activity!!, android.R.layout.simple_list_item_1, it)
                    }
                    bindData()

                } else
                    mBinding.spnWeaponType.adapter = null

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnWeaponType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindData() {

        mBinding.edtSycoTaxID.isEnabled = false
        mBinding.edtSycoTaxID.setText(sycoTaxID)
        mBinding.edtRegistrationDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtRegistrationDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtRegistrationDate.setText(getDate(setCalendarText(Calendar.JANUARY, 1), displayDateFormat))

        mWeapon?.let {

            for ((index, weaponType) in mWeaponTypes?.withIndex()!!) {
                if (weaponType.weaponTypeID == it.weaponTypeID) {
                    mBinding.spnWeaponType.setSelection(index)
                    break
                }
            }

            for ((index, examptionReason) in mWeaponExemptionReasons?.withIndex()!!) {
                if (examptionReason.weaponExemptionReasonID == it.weaponExemptionReasonID) {
                    mBinding.spnExemptionReason.setSelection(index)
                    break
                }
            }

            mBinding.edtSerialNumber.setText(it.serialNo)
            mBinding.edtMake.setText(it.make)
            mBinding.edtModel.setText(it.model)
            mBinding.edtRegistrationDate.setText(displayFormatDate(it.registrationDate))
            mBinding.edtPurposeOfPossession.setText(it.purposeOfPossession)
            mBinding.edtDescription.setText(it.description)
            it.estimatedTax?.let { estimatedTax ->
                mBinding.edtEstimatedAmount.setText(formatWithPrecision(estimatedTax))
            }
            mBinding.edtCustomerName.setText(it.accountName)
            it.accountPhone?.let { accountPhone ->
                mBinding.edtPhoneNumber.setText(accountPhone)
            }
            mBinding.chkActive.isChecked = it.active == "Y"

            if (mCustomer == null) mCustomer = BusinessOwnership()
            mCustomer?.accountID = it.accountID
            mCustomer?.contactName = it.accountName
            mCustomer?.phone = it.accountPhone

            mBinding.edtEmailId.setText(it.email)
            mBinding.edtCountry.setText(it.country)
            mBinding.edtState.setText(it.state)
            mBinding.edtCity.setText(it.city)
            mBinding.edtZone.setText(it.zone)
            mBinding.edtSector.setText(it.sector)
            mBinding.edtStreet.setText(it.street)
            mBinding.edtPlot.setText(it.section)
            mBinding.edtBlock.setText(it.lot)
            mBinding.edtDoorNo.setText(it.parcelRes)
            mBinding.edtZipCode.setText(it.zipCode)


        }
        getIndividualDueSummary()

        if (mListener?.screenMode == Constant.ScreenMode.VIEW)
            disableEdit(false)

        //todo Commented for this release(13/01/2022)
//        if(mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX){
//            getInvoiceCount4Tax()
//        }
    }

    private fun setCalendarText(month: Int, date: Int): Date {
        val calender = Calendar.getInstance(Locale.getDefault())
        calender.set(calender.get(Calendar.YEAR), month, date)
        return calender.time
    }

    private fun getIndividualDueSummary() {
        if (mWeapon != null && mWeapon?.weaponID != null && mWeapon?.weaponID != 0) {
            mListener?.showProgressDialog()
            val getIndividualDueSummary = GetIndividualDueSummary()
            getIndividualDueSummary.productCode = "${(mBinding.spnWeaponType.selectedItem as CRMWeaponTypes).productCode}"
            getIndividualDueSummary.voucherNo = mWeapon?.weaponID ?: 0

            APICall.getIndividualDueSummary(getIndividualDueSummary, object : ConnectionCallBack<BusinessDueSummaryResults> {
                override fun onSuccess(response: BusinessDueSummaryResults) {
                    mListener?.dismissDialog()

                    resetDueSummary()
                    response.businessDueSummary[0].let {
                        mBinding.txtInitialOutstandingCurrentYearDue.text = formatWithPrecision(it.initialOutstandingCurrentYearDue)
                        mBinding.txtCurrentYearDue.text = formatWithPrecision(it.currentYearDue)
                        mBinding.txtCurrentYearPenaltyDue.text = formatWithPrecision(it.currentYearPenaltyDue)
                        mBinding.txtAnteriorYearDue.text = formatWithPrecision(it.anteriorYearDue)
                        mBinding.txtAnteriorYearPenaltyDue.text = formatWithPrecision(it.anteriorYearPenaltyDue)
                        mBinding.txtPreviousYearDue.text = formatWithPrecision(it.previousYearDue)
                        mBinding.txtPreviousYearPenaltyDue.text = formatWithPrecision(it.previousYearPenaltyDue)
                    }
                    fetchChildEntriesCount()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    resetDueSummary()
                    fetchChildEntriesCount()
                }

            })
        } else resetDueSummary()
    }

    private fun resetDueSummary() {
        mBinding.txtInitialOutstandingCurrentYearDue.text = formatWithPrecision(0.0)
        mBinding.txtCurrentYearDue.text = formatWithPrecision(0.0)
        mBinding.txtCurrentYearPenaltyDue.text = formatWithPrecision(0.0)
        mBinding.txtAnteriorYearDue.text = formatWithPrecision(0.0)
        mBinding.txtAnteriorYearPenaltyDue.text = formatWithPrecision(0.0)
        mBinding.txtPreviousYearDue.text = formatWithPrecision(0.0)
        mBinding.txtPreviousYearPenaltyDue.text = formatWithPrecision(0.0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_OUT_STANDING_ENTRY) {
            getIndividualDueSummary()
        } else {
            if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
                mListener?.showToolbarBackButton(R.string.title_weapon_tax)
                if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mCustomer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER) {
                mListener?.showToolbarBackButton(R.string.title_weapon_tax)
                data?.let {
                    if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                        mCustomer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                        setCustomerInfo()
                    }
                }
            }
        }

        if ((mWeapon != null) && mWeapon?.weaponID != 0)
            fetchChildEntriesCount()
    }

    private fun setCustomerInfo() {
        mCustomer?.let {it
            mBinding.edtCustomerName.setText(it.accountName)
            mBinding.edtPhoneNumber.setText(it.phone ?: "")
            mBinding.edtEmailId.setText(it.email ?: "")
            mBinding.edtCountry.setText(it.country ?: "")
            mBinding.edtState.setText(it.state ?: "")
            mBinding.edtCity.setText(it.city ?: "")
            mBinding.edtZone.setText(it.zone ?: "")
            mBinding.edtSector.setText(it.sector ?: "")
            mBinding.edtStreet.setText(it.street ?: "")
            mBinding.edtPlot.setText(it.section ?: "")
            mBinding.edtBlock.setText(it.lot ?: "")
            mBinding.edtDoorNo.setText(it.pacel ?: "")
            mBinding.edtZipCode.setText(it.zipCode ?: "")

        }
    }

    private fun fetchChildEntriesCount() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "CRM_Weapons"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${mWeapon?.weaponID}"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        fetchCount(listFilterColumn, "AND", "COM_DocumentReferences", "DocumentReferenceID")
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
                filterColumn.columnName = "TableName"
                filterColumn.columnValue = "CRM_Weapons"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue = "${mWeapon?.weaponID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
            }
            "COM_Notes" -> {
                mBinding.txtNumberOfNotes.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX)
                filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId}"
                else
                filterColumn.columnValue = "${mCustomer?.accountID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "ProductCode"
                if (mBinding.spnWeaponType.selectedItem != null && -1 != (mBinding.spnWeaponType.selectedItem as CRMWeaponTypes).weaponTypeID) {
                    (mBinding.spnWeaponType.selectedItem as CRMWeaponTypes).productCode?.let {
                        filterColumn.columnValue = it
                    }
                } else filterColumn.columnValue = "0"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "VoucherNo"
                if ((mWeapon != null) && mWeapon?.weaponID != 0) {
                    filterColumn.columnValue = mWeapon?.weaponID.toString()
                } else filterColumn.columnValue = "0"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "VU_ACC_InitialOutstandings", "InitialOutstandingID")
            }
            "VU_ACC_InitialOutstandings" -> {
                mBinding.txtNumberOfInitialOutstanding.text = "$count"
            }
        }
    }


    interface Listener {
        var screenMode: Constant.ScreenMode
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun showProgressDialog(message: Int)
        fun showSnackbarMsg(message: String?)
        fun showProgressDialog()
        fun dismissDialog()
        fun finish()
        fun popBackStack()
        fun showToast(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(msg: String, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener?, view: View)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener?, view: View?)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                if (validateView()) {
                    saveWeapon(getPayload(), v)
                }
            }
            R.id.llDocuments ->

                when {
                    mWeapon != null && mWeapon?.weaponID != null && mWeapon?.weaponID != 0 -> {
                        val fragment = DocumentsMasterFragment()
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, mWeapon?.weaponID ?: 0)
                        fragment.arguments = bundle
                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                        mListener?.showToolbarBackButton(R.string.documents)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveWeapon(getPayload(), v)
                    }
                    else -> {

                    }
                }


            R.id.llNotes -> {
                when {
                    mWeapon != null && mWeapon?.weaponID != null && mWeapon?.weaponID != 0 -> {
                        val fragment = NotesMasterFragment()
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, mWeapon?.weaponID ?: 0)
                        fragment.arguments = bundle
                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)

                        mListener?.showToolbarBackButton(R.string.notes)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveWeapon(getPayload(), v)
                    }
                    else -> {

                    }
                }

            }

            R.id.llInitialOutstanding -> {
                when {
                    mWeapon != null && mWeapon?.weaponID != null && mWeapon?.weaponID != 0 -> {
                        val fragment = OutstandingsMasterFragment()
                        val bundle = Bundle()
                        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX)
                            bundle.putInt(Constant.KEY_CUSTOMER_ID, ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0)
                        else
                            bundle.putInt(Constant.KEY_CUSTOMER_ID, mCustomer?.accountID ?: 0)
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                        bundle.putInt(Constant.KEY_VOUCHER_NO, mWeapon?.weaponID ?: 0)
                        if (mBinding.spnWeaponType.selectedItem != null) {
                            (mBinding.spnWeaponType.selectedItem as CRMWeaponTypes).productCode?.let {
                                bundle.putString(Constant.KEY_PRODUCT_CODE, it)
                            }
                        }
                        fragment.arguments = bundle
                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_OUT_STANDING_ENTRY)

                        mListener?.showToolbarBackButton(R.string.title_initial_outstandings)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveWeapon(getPayload(), v)
                    }
                    else -> {

                    }
                }
            }


            R.id.btnGet -> {
                if (mBinding.spnWeaponType.selectedItem != null && mBinding.spnWeaponType.selectedItemPosition != 0) {
                    fetchEstimatedAmount()
                }
                else{
                    mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + (getString(R.string.weapon_type)))
                }
            }

            R.id.tvFixedExpenses -> {
                if (mBinding.llFixedExpenses.visibility == View.VISIBLE) {
                    mBinding.llFixedExpenses.visibility = View.GONE
                    mBinding.tvFixedExpenses.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
                    mBinding.cardView.requestFocus()
                    mBinding.view.clearFocus()
                } else {
                    mBinding.llFixedExpenses.visibility = View.VISIBLE
                    mBinding.tvFixedExpenses.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
                    mBinding.view.requestFocus()
                    mBinding.cardView.clearFocus()
                }
            }

        }
    }

    private fun fetchEstimatedAmount() {
        val getTaxableMatterColumnData = GetTaxableMatterColumnData()
        getTaxableMatterColumnData.taskCode = "CRM_Weapons"
        mListener?.showProgressDialog()
        APICall.getTaxableMatterColumnData(getTaxableMatterColumnData, object : ConnectionCallBack<List<DataTaxableMatter>> {
            override fun onSuccess(response: List<DataTaxableMatter>) {

                val list: ArrayList<DataTaxableMatter> = arrayListOf()
                for (it in response) {
                    val taxableMatter = DataTaxableMatter()
                    taxableMatter.taxableMatterColumnName = it.taxableMatterColumnName
                    if ("NoOfUnits" == it.taxableMatterColumnName)
                        taxableMatter.taxableMatter = "1"
                    list.add(taxableMatter)
                }
                fetchEstimatedAmount(list)

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun fetchEstimatedAmount(taxableMatter: ArrayList<DataTaxableMatter>) {
        val getEstimatedTaxForProduct = GetEstimatedTaxForProduct()
        getEstimatedTaxForProduct.dataTaxableMatter = taxableMatter
        getEstimatedTaxForProduct.taskCode = "CRM_Weapons"
        getEstimatedTaxForProduct.customerID = MyApplication.getPrefHelper().accountId
        getEstimatedTaxForProduct.isIndividualTax = true
        if (!TextUtils.isEmpty(mBinding.edtRegistrationDate.text?.toString()?.trim()))
            getEstimatedTaxForProduct.startDate = serverFormatDate(mBinding.edtRegistrationDate.text.toString().trim())
        if (mBinding.spnWeaponType.selectedItem == null || 0 != (mBinding.spnWeaponType.selectedItem as CRMWeaponTypes).weaponTypeID)
            getEstimatedTaxForProduct.entityPricingVoucherNo = "${(mBinding.spnWeaponType.selectedItem as CRMWeaponTypes).weaponTypeID}"
        getEstimatedTaxForProduct.isIndividualTax = true
        mListener?.showProgressDialog()
        APICall.getEstimatedTaxForProduct(getEstimatedTaxForProduct, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mBinding.edtEstimatedAmount.setText(formatWithPrecision(response))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.edtEstimatedAmount.setText("")
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun disableEdit(action : Boolean) {
        mBinding.spnWeaponType.isEnabled = action
        mBinding.spnExemptionReason.isEnabled = action
        mBinding.edtSerialNumber.isEnabled = action
        mBinding.edtMake.isEnabled = action
        mBinding.edtModel.isEnabled = action
        mBinding.edtRegistrationDate.isEnabled = action
        mBinding.edtPurposeOfPossession.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.chkActive.isEnabled = action
        mBinding.edtCustomerName.isEnabled = action
        mBinding.edtPhoneNumber.isEnabled = action
        mBinding.edtEmailId.isEnabled = action
        mBinding.tvCreateCustomer.isEnabled = action

        if(action){
            mBinding.btnSave.visibility = View.VISIBLE
        }else{
            mBinding.btnSave.visibility = View.GONE
        }
    }

    private fun disableEditActionForSave() {
        mBinding.spnWeaponType.isEnabled = false
        mBinding.spnExemptionReason.isEnabled = false
        mBinding.edtSerialNumber.isEnabled = false
        mBinding.edtMake.isEnabled = false
        mBinding.edtModel.isEnabled = false
        mBinding.edtRegistrationDate.isEnabled = false
        mBinding.edtPurposeOfPossession.isEnabled = false
        mBinding.edtDescription.isEnabled = false
        mBinding.btnGet.isEnabled = false
        mBinding.edtCustomerName.isEnabled = false
        mBinding.edtPhoneNumber.isEnabled = false
        mBinding.edtEmailId.isEnabled = false
        mBinding.tvCreateCustomer.isEnabled = false

        mBinding.chkActive.isEnabled = true
        mBinding.btnSave.visibility = View.VISIBLE
    }

    private fun getInvoiceCount4Tax() {
        val currentDue = CheckCurrentDue()
        currentDue.accountId = mWeapon?.accountID
        currentDue.vchrno  = mWeapon?.weaponID
        currentDue.taxRuleBookCode  = Constant.TaxRuleBook.WEAPON.Code
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response > 0) {
                    disableEditActionForSave()
                } else {
                    disableEdit(true)
                }
            }
            override fun onFailure(message: String) {
            }
        })
    }

}