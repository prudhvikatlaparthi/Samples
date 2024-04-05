package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.BusinessDueSummaryResults
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.GetGamingMachineTypes
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentGamingMachineEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.model.GamingMachineTax
import com.sgs.citytax.ui.IndividualTaxSummaryActivity
import com.sgs.citytax.util.*
import java.util.*

class GamingMachineTaxFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentGamingMachineEntryBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu? = null

    private var gamingMachineList: ArrayList<GetGamingMachineTypes> = arrayListOf()
    private var customer: BusinessOwnership? = null
    private var sycoTaxID: String? = null
    private var helper: LocationHelper? = null
    private var gamingMachineTax: GamingMachineTax? = null

    private var primaryKey = 0
    private var acctID = 0 //gameMachineID


    override fun initComponents() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                sycoTaxID = arguments?.getString(Constant.KEY_SYCO_TAX_ID)
            if (it.containsKey(Constant.KEY_GAMING_MACHINE))
                gamingMachineTax = arguments?.getParcelable(Constant.KEY_GAMING_MACHINE)

            if(it.containsKey(Constant.KEY_PRIMARY_KEY))
                primaryKey = it.getInt(Constant.KEY_PRIMARY_KEY, 0)
            if(it.containsKey(Constant.KEY_ACCOUNT_ID))
                acctID = it.getInt(Constant.KEY_ACCOUNT_ID, 0)
        }
        bindDataFromAPI()
        //bindSpinner()
        setViews()
        setListeners()
    }

    private fun bindDataFromAPI() {
        APICall.getGamingMachineList(primaryKey, "VU_CRM_GamingMachines", acctID, object : ConnectionCallBack<List<GamingMachineTax>> {
            override fun onSuccess(response: List<GamingMachineTax>) {
                mListener?.dismissDialog()

                gamingMachineTax = response[0]
                bindSpinner()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                bindSpinner()
            }
        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_gaming_machine_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        helper?.disconnect()
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getCorporateOfficeLOVValues("CRM_GamingMachines", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()

                gamingMachineList = response.gamingMachineTypes

                if (gamingMachineList.isNullOrEmpty())
                    mBinding.spnGamingType.adapter = null
                else {
                    gamingMachineList.add(0, GetGamingMachineTypes(getString(R.string.select), gamingMachineTypeID = -1))
                    val proofAdapter = ArrayAdapter<GetGamingMachineTypes>(requireContext(), android.R.layout.simple_spinner_dropdown_item, gamingMachineList)
                    mBinding.spnGamingType.adapter = proofAdapter
                }

                bindData()
            }

            override fun onFailure(message: String) {
                mBinding.spnGamingType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun bindData() {
        gamingMachineTax?.let {
            for (gamingTypeValues in gamingMachineList)
                if ((gamingTypeValues.gamingMachineTypeID != 0) && gamingTypeValues.gamingMachineTypeID == it.gamingMachineTypeID)
                    mBinding.spnGamingType.setSelection(gamingMachineList.indexOf(gamingTypeValues))

            mBinding.chkStatus.isChecked = it.active == "Y"
            it.registrationDate?.let { registrationDate ->
                mBinding.edtRegistrationDate.setText(displayFormatDate(registrationDate))
            }

            it.gamingMachineSycotaxID?.let { gamingMachineSycotaxID ->
                mBinding.edtSycoTaxID.setText(gamingMachineSycotaxID)
            }

            it.serialNo?.let { serialNo ->
                mBinding.edtSerialNo.setText(serialNo)
            }

            it.latitude?.let { latitude ->
                mBinding.edtLatitude.setText(latitude)
            }

            it.longitude?.let { longitude ->
                mBinding.edtLongitude.setText(longitude)
            }

            it.estimatedTax?.let { estimatedTax ->
                mBinding.edtEstimatedAmount.setText(formatWithPrecision(estimatedTax.toDouble()))
            }

            if (customer == null)
                customer = BusinessOwnership()
            customer?.accountID = it.accountID
            if (it.accountName != null && !TextUtils.isEmpty(it.accountName)) {
                mBinding.edtCustomerName.setText(it.accountName)
                customer?.contactName = it.accountName
            }
            if (it.accountPhone != null && !TextUtils.isEmpty(it.accountPhone)) {
                mBinding.edtPhoneNumber.setText(it.accountPhone)
                customer?.phone = it.accountPhone
            }
            if (it.email != null && !TextUtils.isEmpty(it.email)) {
                mBinding.edtEmailId.setText(it.email)
                customer?.email = it.email
            }

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
//        if(fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX) {
//            getInvoiceCount4Tax()
//        }
    }

    private fun getIndividualDueSummary() {
        if (gamingMachineTax != null && gamingMachineTax?.gamingMachineID != null && gamingMachineTax?.gamingMachineID != 0) {
            mListener?.showProgressDialog()
            val getIndividualDueSummary = GetIndividualDueSummary()
            getIndividualDueSummary.productCode = "${(mBinding.spnGamingType.selectedItem as GetGamingMachineTypes).productCode}"
            getIndividualDueSummary.voucherNo = gamingMachineTax?.gamingMachineID ?: 0

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

    private fun setViews() {
        sycoTaxID?.let {
            mBinding.edtSycoTaxID.isEnabled = false
            mBinding.edtSycoTaxID.setText(it)
        }

        mBinding.edtRegistrationDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtRegistrationDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtRegistrationDate.setText(getDate(setCalendarText(Calendar.JANUARY, 1), displayDateFormat))
        mBinding.chkStatus.isChecked = true

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX) {
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

    private fun setCalendarText(month: Int, date: Int): Date {
        val calender = Calendar.getInstance(Locale.getDefault())
        calender.set(calender.get(Calendar.YEAR), month, date)
        return calender.time
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        helper?.onActivityResult(requestCode, resultCode)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_OUT_STANDING_ENTRY) {
            getIndividualDueSummary()
        } else {
            if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
                mListener?.showToolbarBackButton(R.string.title_gaming_machine)
                if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    customer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER) {
                mListener?.showToolbarBackButton(R.string.title_gaming_machine)
                data?.let {
                    if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                        customer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                        setCustomerInfo()
                    }
                }
            }
        }

        if ((gamingMachineTax != null) && gamingMachineTax?.gamingMachineID != 0)
            fetchChildEntriesCount()
    }

    private fun fetchTaxableMatter() {
        val getTaxableMatterColumnData = GetTaxableMatterColumnData()
        getTaxableMatterColumnData.taskCode = "CRM_GamingMachines"
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
        getEstimatedTaxForProduct.taskCode = "CRM_GamingMachines"
        getEstimatedTaxForProduct.customerID = MyApplication.getPrefHelper().accountId
        if (!TextUtils.isEmpty(mBinding.edtRegistrationDate.text?.toString()?.trim()))
            getEstimatedTaxForProduct.startDate = serverFormatDate(mBinding.edtRegistrationDate.text.toString().trim())
        getEstimatedTaxForProduct.isIndividualTax = true
        if (mBinding.spnGamingType.selectedItem != null || -1 == (mBinding.spnGamingType.selectedItem as GetGamingMachineTypes).gamingMachineTypeID)
            getEstimatedTaxForProduct.entityPricingVoucherNo = (mBinding.spnGamingType.selectedItem as GetGamingMachineTypes).gamingMachineTypeID.toString()
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


    private fun setCustomerInfo() {
        customer?.let {
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                if (validateView())
                    saveGamingMachine(prepareData(), v)
            }
            R.id.edtCustomerName, R.id.edtPhoneNumber, R.id.edtEmailId -> {
                showCustomers()
            }

            R.id.tvCreateCustomer -> {
                val fragment = BusinessOwnerEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)

                mListener?.showToolbarBackButton(R.string.citizen)
                mListener?.addFragment(fragment, true)
            }

            R.id.ivAddLocation -> {
                var mLatitude = 0.0
                var mLongitude = 0.0
                if (mBinding.edtLatitude.text.toString().trim().isNotEmpty()) {
                    mLatitude = mBinding.edtLatitude.text.toString().trim().toDouble()
                }

                if (mBinding.edtLongitude.text.toString().trim().isNotEmpty()) {
                    mLongitude = mBinding.edtLongitude.text.toString().trim().toDouble()
                }
                val dialog: LocateDialogFragment = LocateDialogFragment.newInstance(mLatitude, mLongitude)
                dialog.show(childFragmentManager, LocateDialogFragment::class.java.simpleName)
            }

            R.id.llDocuments -> {
                when {
                    gamingMachineTax != null && gamingMachineTax?.gamingMachineID != null && gamingMachineTax?.gamingMachineID != 0 -> {
                        val fragment = DocumentsMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, gamingMachineTax?.gamingMachineID
                                ?: 0)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                        mListener?.showToolbarBackButton(R.string.documents)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveGamingMachine(prepareData(), v)
                    }
                    else -> {

                    }
                }
            }

            R.id.llNotes -> {
                when {
                    gamingMachineTax != null && gamingMachineTax?.gamingMachineID != null && gamingMachineTax?.gamingMachineID != 0 -> {
                        val fragment = NotesMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, gamingMachineTax?.gamingMachineID
                                ?: 0)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)

                        mListener?.showToolbarBackButton(R.string.notes)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveGamingMachine(prepareData(), v)
                    }
                    else -> {

                    }
                }
            }

            R.id.llInitialOutstanding -> {
                when {
                    gamingMachineTax != null && gamingMachineTax?.gamingMachineID != null && gamingMachineTax?.gamingMachineID != 0 -> {
                        val fragment = OutstandingsMasterFragment()
                        val bundle = Bundle()
                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX)
                            bundle.putInt(Constant.KEY_CUSTOMER_ID, ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0)
                        else
                        bundle.putInt(Constant.KEY_CUSTOMER_ID, customer?.accountID ?: 0)
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putInt(Constant.KEY_VOUCHER_NO, gamingMachineTax?.gamingMachineID
                                ?: 0)
                        if (mBinding.spnGamingType.selectedItem != null) {
                            (mBinding.spnGamingType.selectedItem as GetGamingMachineTypes).productCode?.let {
                                bundle.putString(Constant.KEY_PRODUCT_CODE, it)
                            }
                        }
                        fragment.arguments = bundle
                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_OUT_STANDING_ENTRY)

                        mListener?.showToolbarBackButton(R.string.title_initial_outstandings)
                        mListener?.addFragment(fragment, true)
                    }
                    validateView() -> {
                        saveGamingMachine(prepareData(), v)
                    }
                    else -> {

                    }
                }
            }

            R.id.btnGet -> {
                if (mBinding.spnGamingType.selectedItem != null && -1 != (mBinding.spnGamingType.selectedItem as GetGamingMachineTypes).gamingMachineTypeID) {
                    fetchTaxableMatter()
                }else{
                    mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.gaming_machine_type))
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

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.tvCreateCustomer.setOnClickListener(this)
        mBinding.edtCustomerName.setOnClickListener(this)
        mBinding.edtPhoneNumber.setOnClickListener(this)
        mBinding.edtEmailId.setOnClickListener(this)
        mBinding.ivAddLocation.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
        mBinding.llInitialOutstanding.setOnClickListener(this)
        mBinding.btnGet.setOnClickListener(this)
        mBinding.tvFixedExpenses.setOnClickListener(this)
    }

    private fun saveGamingMachine(storeGamingMachinesTax: StoreGamingMachinesTax, view: View?) {
        mListener?.showProgressDialog()
        mBinding.btnSave.isEnabled = false

        APICall.storeGamingMachine(storeGamingMachinesTax, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                mBinding.btnSave.isEnabled = true
                if (gamingMachineTax == null)
                    gamingMachineTax = GamingMachineTax()
                if (response != 0) gamingMachineTax?.gamingMachineID = response

                when (view?.id) {
                    R.id.llDocuments -> onClick(view)
                    R.id.llNotes -> onClick(view)
                    R.id.llInitialOutstanding -> onClick(view)
                    else -> {
                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE)
                            mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))
                        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE)
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
                mBinding.btnSave.isEnabled = true
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun navigateToSummary() {
        val intent = Intent(context, IndividualTaxSummaryActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, mBinding.edtSycoTaxID.text.toString())
        startActivity(intent)
    }

    private fun prepareData(): StoreGamingMachinesTax {
        val mGamingMachineTax = GamingMachineTax()
        gamingMachineTax?.gamingMachineID?.let {
            mGamingMachineTax.gamingMachineID = it
        }
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX)
            mGamingMachineTax.accountID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
        else
        customer?.let {
            mGamingMachineTax.accountID = it.accountID
        }

        if (!TextUtils.isEmpty(mBinding.edtSycoTaxID.text.toString().trim()))
            mGamingMachineTax.gamingMachineSycotaxID = mBinding.edtSycoTaxID.text.toString().trim()

        if (!TextUtils.isEmpty(mBinding.edtRegistrationDate.text.toString().trim()))
            mGamingMachineTax.registrationDate = serverFormatDate(mBinding.edtRegistrationDate.text.toString().trim())

        if (mBinding.spnGamingType.selectedItem != null)
            mGamingMachineTax.gamingMachineTypeID = (mBinding.spnGamingType.selectedItem as GetGamingMachineTypes).gamingMachineTypeID

        if (!TextUtils.isEmpty(mBinding.edtSerialNo.text.toString().trim()))
            mGamingMachineTax.serialNo = mBinding.edtSerialNo.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtLatitude.text.toString().trim()))
            mGamingMachineTax.latitude = mBinding.edtLatitude.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtLongitude.text.toString().trim()))
            mGamingMachineTax.longitude = mBinding.edtLongitude.text.toString().trim()

        mGamingMachineTax.active = if (mBinding.chkStatus.isChecked) "Y" else "N"
        mGamingMachineTax.estimatedTax = null

        val storeGamingMachinesTax = StoreGamingMachinesTax()
        storeGamingMachinesTax.gamingmachines = mGamingMachineTax
        storeGamingMachinesTax.attachment = null
        storeGamingMachinesTax.note = null

        return storeGamingMachinesTax
    }

    private fun showCustomers() {
        val fragment = BusinessOwnerSearchFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE)
        fragment.arguments = bundle
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
        mListener?.showToolbarBackButton(R.string.citizen)
        mListener?.addFragment(fragment, true)
    }

    private fun getCurrentLocation() {
        helper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
        helper?.fetchLocation()
        helper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                bindLatLongs(latitude, longitude)
                mListener?.dismissDialog()
            }

            override fun start() {
                mListener?.showProgressDialog(R.string.msg_location_fetching)
            }
        })
    }

    fun bindLatLongs(latitude: Double?, longitude: Double?) {
        mBinding.edtLatitude.setText(latitude.toString())
        mBinding.edtLongitude.setText(longitude.toString())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        helper?.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun validateView(): Boolean {
        if (mBinding.spnGamingType.selectedItem == null || -1 == (mBinding.spnGamingType.selectedItem as GetGamingMachineTypes).gamingMachineTypeID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.gaming_machine_type))
            return false
        }

        if (mBinding.edtSerialNo.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.serial_no))
            return false
        }

        if (mBinding.edtRegistrationDate.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.registration_date))
            return false
        }

        if (mBinding.edtCustomerName.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.citizen_name))
            return false
        }

        return true
    }

    private fun disableEdit(action: Boolean) {
        mBinding.edtSerialNo.isEnabled = action
        mBinding.spnGamingType.isEnabled = action
        mBinding.edtRegistrationDate.isEnabled = action
        mBinding.ivAddLocation.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.chkStatus.isEnabled = action
        mBinding.edtCustomerName.isEnabled = action
        mBinding.edtPhoneNumber.isEnabled = action
        mBinding.edtEmailId.isEnabled = action
        mBinding.tvCreateCustomer.isEnabled = action
        mBinding.btnSave.isVisible = action
    }

    private fun disableEditForSave(action: Boolean) {
        mBinding.edtSerialNo.isEnabled = action
        mBinding.spnGamingType.isEnabled = action
        mBinding.edtRegistrationDate.isEnabled = action
        mBinding.ivAddLocation.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.chkStatus.isEnabled = true
        mBinding.edtCustomerName.isEnabled = action
        mBinding.edtPhoneNumber.isEnabled = action
        mBinding.edtEmailId.isEnabled = action
        mBinding.tvCreateCustomer.isEnabled = action
        mBinding.btnSave.isVisible = true
    }


    private fun fetchChildEntriesCount() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "CRM_GamingMachines"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${gamingMachineTax?.gamingMachineID}"
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
                filterColumn.columnValue = "CRM_GamingMachines"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue = "${gamingMachineTax?.gamingMachineID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
            }
            "COM_Notes" -> {
                mBinding.txtNumberOfNotes.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX)
                    filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId}"
                else
                filterColumn.columnValue = "${customer?.accountID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "ProductCode"
                if (mBinding.spnGamingType.selectedItem != null && -1 != (mBinding.spnGamingType.selectedItem as GetGamingMachineTypes).gamingMachineTypeID) {
                    (mBinding.spnGamingType.selectedItem as GetGamingMachineTypes).productCode?.let {
                        filterColumn.columnValue = it
                    }
                } else filterColumn.columnValue = "0"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "VoucherNo"
                if ((gamingMachineTax != null) && gamingMachineTax?.gamingMachineID != 0) {
                    filterColumn.columnValue = gamingMachineTax?.gamingMachineID.toString()
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
    private fun getInvoiceCount4Tax() {
        val currentDue = CheckCurrentDue()
        currentDue.accountId = gamingMachineTax?.accountID
        currentDue.vchrno  = gamingMachineTax?.gamingMachineID
        currentDue.taxRuleBookCode  = Constant.TaxRuleBook.GAME.toString()
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response > 0) {
                    disableEditForSave(false)
                } else {
                    disableEdit(true)

                }
            }
            override fun onFailure(message: String) {
            }
        })
    }

    interface Listener {
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun showSnackbarMsg(message: String?)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun finish()
        fun showToast(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
    }

}