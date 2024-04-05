package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.APICall.getInvoiceCount4Tax
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.BusinessDueSummaryResults
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentCartTaxBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.FragmentCommunicator
import com.sgs.citytax.ui.IndividualTaxSummaryActivity
import com.sgs.citytax.util.*
import java.util.*

class CartTaxFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentCartTaxBinding
    private var mListener: FragmentCommunicator? = null
    private var mCartTypes: MutableList<CRMCartType> = arrayListOf()
    private var mCustomer: BusinessOwnership? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mSycoTaxID: String? = ""
    private var mCartTax: CartTax? = null

    private var primaryKey = 0
    private var acctID = 0 //cartID

    companion object {
        fun newInstance() = CartTaxFragment()
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_CART_TAX))
                mCartTax = it.getParcelable(Constant.KEY_CART_TAX)
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                mSycoTaxID = it.getString(Constant.KEY_SYCO_TAX_ID)

            if(it.containsKey(Constant.KEY_PRIMARY_KEY))
                primaryKey = it.getInt(Constant.KEY_PRIMARY_KEY, 0)
            if(it.containsKey(Constant.KEY_ACCOUNT_ID))
                acctID = it.getInt(Constant.KEY_ACCOUNT_ID, 0)
        }
        //endregion
        bindDataFromAPI()
        //bindSpinner()
        setEvents()
    }

    private fun bindDataFromAPI() {
        APICall.getCartList(primaryKey, "VU_CRM_Carts", acctID, object : ConnectionCallBack<List<CartTax>> {
            override fun onSuccess(response: List<CartTax>) {
                mListener?.dismissDialog()

                mCartTax = response[0]
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
            mListener = if (parentFragment != null)
                parentFragment as FragmentCommunicator
            else context as FragmentCommunicator
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart_tax, container, false)
        initComponents()
        return mBinding.root
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_Carts", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mCartTypes = response.cartTypes
                if (mCartTypes.isNullOrEmpty())
                    mBinding.spnCartType.adapter = null
                else {
                    mCartTypes.add(0, CRMCartType(getString(R.string.select), "", "", 0, ""))
                    mBinding.spnCartType.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, mCartTypes)
                }

                bindData()

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnCartType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun fetchTaxableMatter() {
        val getTaxableMatterColumnData = GetTaxableMatterColumnData()
        getTaxableMatterColumnData.taskCode = "CRM_Carts"
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
        getEstimatedTaxForProduct.taskCode = "CRM_Carts"
        //getEstimatedTaxForProduct.customerID = mCustomer?.accountID ?: 0
        getEstimatedTaxForProduct.customerID = MyApplication.getPrefHelper().accountId
        if (mBinding.spnCartType.selectedItem == null || 0 != (mBinding.spnCartType.selectedItem as CRMCartType).cartTypeID)
            getEstimatedTaxForProduct.entityPricingVoucherNo = "${(mBinding.spnCartType.selectedItem as CRMCartType).cartTypeID}"
        if (mBinding.edtRegistrationDate.text != null && !TextUtils.isEmpty(mBinding.edtRegistrationDate.text.toString().trim()))
            getEstimatedTaxForProduct.startDate = serverFormatDate(mBinding.edtRegistrationDate.text.toString().trim())
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

    private fun bindData() {
        mBinding.edtRegistrationDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtRegistrationDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtRegistrationDate.setText(getDate(setCalendarText(Calendar.JANUARY, 1), displayDateFormat))
        mSycoTaxID?.let {
            mBinding.edtSycoTaxID.setText(it)
        }

        mCartTax?.let {
            mBinding.edtRegistrationDate.setText(displayFormatDate(it.registrationDate))
            mBinding.edtCartNo.setText(it.cartNo)
            mBinding.cbActive.isChecked = it.active == "Y"
            mBinding.edtCustomerName.setText(it.accountName)
            it.accountPhone?.let { accountPhone ->
                mBinding.edtPhoneNumber.setText(accountPhone)
            }
            it.email?.let { email ->
                mBinding.edtEmailId.setText(email)
            }
            it.estimatedTax?.let { estimatedTax ->
                mBinding.edtEstimatedAmount.setText(formatWithPrecision(estimatedTax))
            }

            for ((index, cartType) in mCartTypes.withIndex()) {
                if (cartType.cartTypeID == it.cartTypeID) {
                    mBinding.spnCartType.setSelection(index)
                    break
                }
            }

            if (mCustomer == null) mCustomer = BusinessOwnership()
            mCustomer?.accountID = it.accountID
            mCustomer?.contactName = it.accountName
            mCustomer?.phone = it.accountPhone

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

            if (mListener?.screenMode == Constant.ScreenMode.VIEW)
                disableEdit()


            //todo Commented for this release(13/01/2022)
//            if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX) {
//                getInvoiceCount4Tax()
//            }
        }
        getIndividualDueSummary()
    }

    private fun setCalendarText(month: Int, date: Int): Date {
        val calender = Calendar.getInstance(Locale.getDefault())
        calender.set(calender.get(Calendar.YEAR), month, date)
        return calender.time
    }


    private fun getIndividualDueSummary() {
        if (mCartTax != null && mCartTax?.cartID != null && mCartTax?.cartID != 0) {
            mListener?.showProgressDialog()
            val getIndividualDueSummary = GetIndividualDueSummary()
            getIndividualDueSummary.productCode = "${(mBinding.spnCartType.selectedItem as CRMCartType).productCode}"
            getIndividualDueSummary.voucherNo = mCartTax?.cartID ?: 0

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

    private fun getInvoiceCount4Tax() {

        val currentDue = CheckCurrentDue()
        currentDue.accountId = mCartTax?.accountID
        currentDue.vchrno  = mCartTax?.cartID
        currentDue.taxRuleBookCode  = Constant.TaxRuleBook.CART.Code
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response>0)
                {
                    setEditActionForSave()
                }
                else
                {
                    setEditAction(true)
                }

            }
            override fun onFailure(message: String) {
            }
        })
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

    private fun setEvents() {
        mBinding.edtCustomerName.setOnClickListener(this)
        mBinding.edtPhoneNumber.setOnClickListener(this)
        mBinding.edtEmailId.setOnClickListener(this)
        mBinding.tvCreateCustomer.setOnClickListener(this)
        mBinding.btnGet.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
        mBinding.llInitialOutstanding.setOnClickListener(this)
        mBinding.btnSave.setOnClickListener(this)
        mBinding.tvFixedExpenses.setOnClickListener(this)

        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX) {
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

    private fun getPayload(): CartTax {
        val cartTax = CartTax()

        mCartTax?.cartID?.let {
            cartTax.cartID = it
        }

        cartTax.cartSycoTaxID = mBinding.edtSycoTaxID.text.toString().trim()
        cartTax.cartNo = mBinding.edtCartNo.text.toString().trim()
        cartTax.cartTypeID = (mBinding.spnCartType.selectedItem as CRMCartType).cartTypeID
        cartTax.registrationDate = serverFormatDate(mBinding.edtRegistrationDate.text.toString().trim())
        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX)
            cartTax.accountID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
        else
            cartTax.accountID = mCustomer?.accountID
        cartTax.active = if (mBinding.cbActive.isChecked) "Y" else "N"
        cartTax.estimatedTax = null

        return cartTax
    }

    private fun showCustomers() {
        val fragment = BusinessOwnerSearchFragment()
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
        mListener?.showToolbarBackButton(R.string.citizen)
        mListener?.addFragment(fragment, true)
    }

    private fun isValid(): Boolean {
        if (mBinding.spnCartType.selectedItem == null || 0 == (mBinding.spnCartType.selectedItem as CRMCartType).cartTypeID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.cart_type))
            return false
        }

        if (mBinding.edtRegistrationDate.text != null && TextUtils.isEmpty(mBinding.edtRegistrationDate.text.toString().trim())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.registration_date))
            return false
        }

        if (mBinding.edtCartNo.text != null && TextUtils.isEmpty(mBinding.edtCartNo.text.toString().trim())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.cart_no))
            return false
        }

        if (mBinding.edtCustomerName.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(R.string.msg_provide_citizen_info)
            return false
        }

        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnGet -> {
                if (mBinding.spnCartType.selectedItem != null && 0 != (mBinding.spnCartType.selectedItem as CRMCartType).cartTypeID) {
                    fetchTaxableMatter()
                } else {
                    mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.cart_type))
                }
            }
            R.id.llDocuments -> {
                when {
                    mCartTax != null && mCartTax?.cartID != null && mCartTax?.cartID != 0 -> {
                        val fragment = DocumentsMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, mCartTax?.cartID ?: 0)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)

                        mListener?.showToolbarBackButton(R.string.documents)
                        mListener?.addFragment(fragment, true)
                    }
                    isValid() -> {
                        save(getPayload(), v)
                    }
                }
            }
            R.id.llNotes -> {
                when {
                    mCartTax != null && mCartTax?.cartID != null && mCartTax?.cartID != 0 -> {
                        val fragment = NotesMasterFragment()

                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                        bundle.putInt(Constant.KEY_PRIMARY_KEY, mCartTax?.cartID
                                ?: 0)
                        fragment.arguments = bundle
                        //endregion

                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)

                        mListener?.showToolbarBackButton(R.string.notes)
                        mListener?.addFragment(fragment, true)
                    }

                    isValid() -> {
                        save(getPayload(), v)
                    }
                }
            }
            R.id.btnSave -> {
                if (isValid())
                    save(getPayload(), null)
            }
            R.id.tvCreateCustomer -> {

                val fragment = BusinessOwnerEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)

                mListener?.showToolbarBackButton(R.string.citizen)
                mListener?.addFragment(fragment, true)
            }

            R.id.llInitialOutstanding -> {
                when {
                    mCartTax != null && mCartTax?.cartID != null && mCartTax?.cartID != 0 -> {
                        val fragment = OutstandingsMasterFragment()
                        val bundle = Bundle()
                        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX)
                        bundle.putInt(Constant.KEY_CUSTOMER_ID, ObjectHolder.registerBusiness.vuCrmAccounts?.accountId ?: 0)
                        else
                        bundle.putInt(Constant.KEY_CUSTOMER_ID, mCustomer?.accountID ?: 0)
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                        bundle.putInt(Constant.KEY_VOUCHER_NO, mCartTax?.cartID ?: 0)
                        if (mBinding.spnCartType.selectedItem != null) {
                            if (-1 != (mBinding.spnCartType.selectedItem as CRMCartType).cartTypeID)
                                (mBinding.spnCartType.selectedItem as CRMCartType).productCode?.let {
                                    bundle.putString(Constant.KEY_PRODUCT_CODE, it)
                                }
                        }
                        fragment.arguments = bundle
                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_OUT_STANDING_ENTRY)

                        mListener?.showToolbarBackButton(R.string.title_initial_outstandings)
                        mListener?.addFragment(fragment, true)
                    }
                    isValid() -> {
                        save(getPayload(), v)
                    }
                    else -> {

                    }
                }
            }

            R.id.edtCustomerName, R.id.edtPhoneNumber, R.id.edtEmailId -> {
                showCustomers()
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

    private fun save(cartTax: CartTax, view: View?) {
        mListener?.showProgressDialog()

        val storeCartTax = StoreCartTax()
        storeCartTax.cart = cartTax

        APICall.storeCartTax(storeCartTax, object : ConnectionCallBack<Int> {

            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()

                if (mCartTax == null) mCartTax = CartTax()
                if (response != 0) mCartTax?.cartID = response

                when (view?.id) {
                    R.id.llDocuments -> onClick(view)
                    R.id.llNotes -> onClick(view)
                    R.id.llInitialOutstanding -> onClick(view)
                    else -> {
                        if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_CART_TAX)
                            mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))
                        else if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_OUT_STANDING_ENTRY) {
            getIndividualDueSummary()
        } else {
            if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
                mListener?.showToolbarBackButton(R.string.title_cart_tax)
                if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mCustomer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER) {
                mListener?.showToolbarBackButton(R.string.title_cart_tax)
                data?.let {
                    if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                        mCustomer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                        setCustomerInfo()
                    }
                }
            }
        }

        if ((mCartTax != null) && mCartTax?.cartID != 0)
            fetchChildEntriesCount()
    }


    private fun setCustomerInfo() {
        mCustomer?.let {
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

    override fun onDestroy() {
        super.onDestroy()
        mCustomer = null
    }

    private fun disableEdit() {
        mBinding.spnCartType.isEnabled = false
        mBinding.edtRegistrationDate.isEnabled = false
        mBinding.edtCartNo.isEnabled = false
        mBinding.btnGet.isEnabled = false
        mBinding.cbActive.isEnabled = false
        mBinding.edtCustomerName.isEnabled = false
        mBinding.edtPhoneNumber.isEnabled = false
        mBinding.edtEmailId.isEnabled = false
        mBinding.tvCreateCustomer.isEnabled = false
        mBinding.btnSave.visibility = GONE
    }

    private fun  setEditAction(action : Boolean) {
        mBinding.spnCartType.isEnabled = action
        mBinding.edtRegistrationDate.isEnabled = action
        mBinding.edtCartNo.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.cbActive.isEnabled = action
        mBinding.edtCustomerName.isEnabled = action
        mBinding.edtPhoneNumber.isEnabled = action
        mBinding.edtEmailId.isEnabled = action
        mBinding.tvCreateCustomer.isEnabled = action
        mBinding.btnSave.visibility = View.VISIBLE
    }

    private fun setEditActionForSave() {
        mBinding.spnCartType.isEnabled = false
        mBinding.edtRegistrationDate.isEnabled = false
        mBinding.edtCartNo.isEnabled = false
        mBinding.btnGet.isEnabled = false
        mBinding.edtCustomerName.isEnabled = false
        mBinding.edtPhoneNumber.isEnabled = false
        mBinding.edtEmailId.isEnabled = false
        mBinding.tvCreateCustomer.isEnabled = false
        mBinding.cbActive.isEnabled = true
        mBinding.btnSave.visibility = View.VISIBLE
    }

    private fun fetchChildEntriesCount() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "CRM_Carts"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${mCartTax?.cartID}"
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
                filterColumn.columnValue = "CRM_Carts"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue = "${mCartTax?.cartID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
            }
            "COM_Notes" -> {
                mBinding.txtNumberOfNotes.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX)
                    filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId}"
                else
                filterColumn.columnValue = "${mCustomer?.accountID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "ProductCode"
                if (mBinding.spnCartType.selectedItem != null && -1 != (mBinding.spnCartType.selectedItem as CRMCartType).cartTypeID) {
                    (mBinding.spnCartType.selectedItem as CRMCartType).productCode?.let {
                        filterColumn.columnValue = it
                    }
                } else filterColumn.columnValue = "0"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "VoucherNo"
                if ((mCartTax != null) && mCartTax?.cartID != 0) {
                    filterColumn.columnValue = mCartTax?.cartID.toString()
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

}
