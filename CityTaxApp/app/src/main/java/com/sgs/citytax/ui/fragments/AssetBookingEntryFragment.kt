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
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.Asset
import com.sgs.citytax.api.payload.AssetBookingRequestHeader
import com.sgs.citytax.api.payload.AssetBookingRequestLine
import com.sgs.citytax.api.payload.InsertAssetBookingRequest
import com.sgs.citytax.api.response.AssetBooking
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.ValidateAssetForAssignAndReturnResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentAssetBookingEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.adapter.AssetBookingAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.KEY_ASSETS
import com.sgs.citytax.util.Constant.KEY_ASSET_CATEGORIES
import com.sgs.citytax.util.Constant.KEY_ASSET_TYPES
import com.sgs.citytax.util.Constant.KEY_BRANCH_ID
import com.sgs.citytax.util.Constant.KEY_DESTINATION_CITIES
import com.sgs.citytax.util.Constant.KEY_DESTINATION_COUNTRIES
import com.sgs.citytax.util.Constant.KEY_DESTINATION_SECTORS
import com.sgs.citytax.util.Constant.KEY_DESTINATION_STATES
import com.sgs.citytax.util.Constant.KEY_DESTINATION_ZONES
import com.sgs.citytax.util.Constant.KEY_PICK_UP_CITIES
import com.sgs.citytax.util.Constant.KEY_PICK_UP_COUNTRIES
import com.sgs.citytax.util.Constant.KEY_PICK_UP_SECTORS
import com.sgs.citytax.util.Constant.KEY_PICK_UP_STATES
import com.sgs.citytax.util.Constant.KEY_PICK_UP_ZONES
import com.sgs.citytax.util.Constant.KEY_SELECTED_ASSETS
import com.sgs.citytax.util.Constant.KEY_SELECTED_ASSET_CATEGORIES
import com.sgs.citytax.util.Constant.REQUEST_CODE_ASSET_ASSIGNMENT
import com.sgs.citytax.util.Constant.REQUEST_CODE_PAYMENT_SUCCESS
import com.sgs.citytax.util.Constant.REQUEST_CODE_SCAN_ASSET
import java.math.BigDecimal
import java.util.*

class AssetBookingEntryFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentAssetBookingEntryBinding
    private var mListener: Listener? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mCustomer: BusinessOwnership? = null
    private var mAdapter: AssetBookingAdapter? = null
    private var mAssetBooking: AssetBooking? = null
    private var mVoucherNo: Int = 0

    private var selectedPosition = -1
    private var mSelectedAssetCategories: ArrayList<Int>? = arrayListOf()
    private var mSelectedAssets: ArrayList<Int>? = arrayListOf()

    private var destinationCountriesList: ArrayList<COMCountryMaster> = arrayListOf()
    private var destinationStatesList: ArrayList<COMStateMaster> = arrayListOf()
    private var destinationCitiesList: ArrayList<VUCOMCityMaster> = arrayListOf()
    private var destinationZonesList: ArrayList<COMZoneMaster> = arrayListOf()
    private var destinationSectorsList: ArrayList<COMSectors> = arrayListOf()
    private var pickUpCountriesList: ArrayList<COMCountryMaster> = arrayListOf()
    private var pickUpStatesList: ArrayList<COMStateMaster> = arrayListOf()
    private var pickUpCitiesList: ArrayList<VUCOMCityMaster> = arrayListOf()
    private var pickUpZonesList: ArrayList<COMZoneMaster> = arrayListOf()
    private var pickUpSectorsList: ArrayList<COMSectors> = arrayListOf()
    private var mAssetTypes: ArrayList<AssetType> = arrayListOf()
    private var mAssetCategories: ArrayList<AssetCategory> = arrayListOf()
    private var mAssets: ArrayList<Asset> = arrayListOf()
    private var mAdministrativeOffices: List<UMXUserOrgBranches> = arrayListOf()

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_ASSET_BOOKING))
                mAssetBooking = it.getParcelable(Constant.KEY_ASSET_BOOKING)
        }
        //endregion
        setViews()
        setEvents()
        bindSpinner()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_asset_booking_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCustomer = null
    }

    private fun setViews() {
        mAdapter = AssetBookingAdapter(this, mCode)
        mBinding.recyclerView.adapter = mAdapter
        val itemDecor = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        mBinding.recyclerView.addItemDecoration(itemDecor)
        if (mCode == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT) {
            mBinding.edtCustomerName.isEnabled = false
            mBinding.edtPhoneNumber.isEnabled = false
            mBinding.tvNewAsset.visibility = GONE
            mBinding.edtEmail.isEnabled = false
            mBinding.llCreateCustomer.visibility = GONE
            mBinding.cardViewAmounts.visibility = GONE
            mBinding.btnSave.visibility = GONE
            mBinding.btnCollectDeposit.visibility = GONE
        }
    }

    private fun setEvents() {
        mBinding.edtCustomerName.setOnClickListener {
            showCustomers()
        }

        mBinding.edtPhoneNumber.setOnClickListener {
            showCustomers()
        }

        mBinding.edtEmail.setOnClickListener {
            showCustomers()
        }

        mBinding.tvCreateCustomer.setOnClickListener {

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

        mBinding.tvNewAsset.setOnClickListener {
            if (mAdapter!=null && isAllowPeriodicInvoice()) {
                mListener?.showAlertDialog(getString(R.string.periodic_item_validation))
                return@setOnClickListener
            }
            val fragment = AssetBookingLineEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            updateSelectedCategoriesAndAssets()
            bundle.putIntegerArrayList(KEY_SELECTED_ASSET_CATEGORIES, mSelectedAssetCategories)
            bundle.putIntegerArrayList(KEY_SELECTED_ASSETS, mSelectedAssets)

            if (mAdapter != null && mAdapter!!.get().size > 0) {
                val mTempAssetCategories: ArrayList<AssetCategory> = arrayListOf()
                for (item in mAssetCategories) {
                    if (item.allowPeriodicInvoice == "N") {
                        mTempAssetCategories.add(item)
                    }
                }
                bundle.putParcelableArrayList(KEY_ASSET_CATEGORIES, mTempAssetCategories)
            } else {
                bundle.putParcelableArrayList(KEY_ASSET_CATEGORIES, mAssetCategories)
            }


            bundle.putParcelableArrayList(KEY_ASSETS, mAssets)
            bundle.putParcelableArrayList(KEY_ASSET_TYPES, mAssetTypes)
            bundle.putParcelableArrayList(KEY_PICK_UP_COUNTRIES, pickUpCountriesList)
            bundle.putParcelableArrayList(KEY_DESTINATION_COUNTRIES, destinationCountriesList)
            bundle.putParcelableArrayList(KEY_PICK_UP_STATES, pickUpStatesList)
            bundle.putParcelableArrayList(KEY_DESTINATION_STATES, destinationStatesList)
            bundle.putParcelableArrayList(KEY_PICK_UP_CITIES, pickUpCitiesList)
            bundle.putParcelableArrayList(KEY_DESTINATION_CITIES, destinationCitiesList)
            bundle.putParcelableArrayList(KEY_PICK_UP_ZONES, pickUpZonesList)
            bundle.putParcelableArrayList(KEY_DESTINATION_ZONES, destinationZonesList)
            bundle.putParcelableArrayList(KEY_PICK_UP_SECTORS, pickUpSectorsList)
            bundle.putParcelableArrayList(KEY_DESTINATION_SECTORS, destinationSectorsList)
            if (mBinding.spnAdministrationOffice.selectedItem != null) {
                val branch = mBinding.spnAdministrationOffice.selectedItem as UMXUserOrgBranches?
                branch?.userOrgBranchID?.let {
                    bundle.putInt(KEY_BRANCH_ID, it)
                }
            }
            fragment.arguments = bundle
            //endregion
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_BOOKING_LINE)

            mListener?.showToolbarBackButton(R.string.asset_booking)
            mListener?.addFragment(fragment, true)
        }

        mBinding.btnSave.setOnClickListener {
            if (isValid())
                save()
        }

        mBinding.btnCollectDeposit.setOnClickListener {
            navigateToPaymentScreen()
        }
    }

    private fun isAllowPeriodicInvoice(): Boolean {
        for (item in mAdapter!!.get()) {
            if (item.allowPeriodicInvoice == "Y") {
                return true
            }
        }
        return false
    }

    private fun navigateToPaymentScreen() {
        //region Payment object preparation for navigating to payment screen
        val payment = MyApplication.resetPayment()
        var netReceivable = BigDecimal.ZERO
        if (mBinding.edtNetReceivable.text != null && !TextUtils.isEmpty(mBinding.edtNetReceivable.text.toString()))
            netReceivable = BigDecimal(currencyToDouble(mBinding.edtNetReceivable.text.toString()) as Long)
        payment.amountDue = netReceivable
        payment.amountTotal = netReceivable
        payment.minimumPayAmount = netReceivable
        mCustomer?.accountID?.let {
            payment.customerID = it
        }
        payment.paymentType = Constant.PaymentType.ASSET_BOOKING
        payment.voucherNo = mVoucherNo
        mAssetBooking?.assetBookingRequestHeader?.bookingRequestID?.let {
            if (it != 0)
                payment.voucherNo = it
        }
        //endregion

        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
        startActivityForResult(intent, REQUEST_CODE_PAYMENT_SUCCESS)
    }

    private fun isValid(): Boolean {
        if (mCustomer == null) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide_customer_info))
            return false
        }
        if (mAdapter == null || mAdapter?.get().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.provide_asset_details)}")
            return false
        }
        return true
    }

    private fun save() {
        mListener?.showProgressDialog()

        val assetBookingRequest = AssetBookingRequestHeader()
        if (mBinding.spnAdministrationOffice.selectedItem != null) {
            val branch = mBinding.spnAdministrationOffice.selectedItem as UMXUserOrgBranches?
            branch?.userOrgBranchID?.let {
                assetBookingRequest.userOrgId = it
            }
        }
        mAssetBooking?.assetBookingRequestHeader?.bookingRequestID?.let {
            assetBookingRequest.bookingRequestID = it
        }
        assetBookingRequest.statusCode = "AST_AssetBookingRequests.New"
        mAssetBooking?.assetBookingRequestHeader?.statusCode?.let {
            if (it.isNotEmpty())
                assetBookingRequest.statusCode = it
        }
        var netReceivable = BigDecimal.ZERO
        if (mBinding.edtNetReceivable.text != null && !TextUtils.isEmpty(mBinding.edtNetReceivable.text.toString()))
            netReceivable = BigDecimal(currencyToDouble(mBinding.edtNetReceivable.text.toString().trim()) as Long)
        if (mBinding.edtEstimatedAmount.text != null && !TextUtils.isEmpty(mBinding.edtEstimatedAmount.text.toString()))
            assetBookingRequest.estimatedAmount = BigDecimal(currencyToDouble(mBinding.edtEstimatedAmount.text.toString()) as Long)
        assetBookingRequest.netReceivable = netReceivable
        assetBookingRequest.bookingRequestDate = formatDateTime(Date())
        mCustomer?.accountID?.let {
            assetBookingRequest.accountID = it
        }

        val insertAssetBookingRequest = InsertAssetBookingRequest()
        insertAssetBookingRequest.assetBookingRequestHeader = assetBookingRequest
        mAdapter?.get()?.let {
            insertAssetBookingRequest.assetBookingRequestLines?.addAll(it)
        }

        APICall.insertAssetBooking(insertAssetBookingRequest, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                mBinding.btnSave.visibility = GONE
                mBinding.btnCollectDeposit.visibility = VISIBLE

                if ((mAssetBooking != null) && mAssetBooking?.assetBookingRequestHeader?.bookingRequestID != 0)
                    mListener?.showSnackbarMsg(getString(R.string.asset_booking_update))
                else
                    mListener?.showSnackbarMsg(getString(R.string.asset_booking_success))

                Handler().postDelayed({
                    mVoucherNo = response

                    val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.BOOKING_REQUEST.Code)
                    intent.putExtra(Constant.KEY_BOOKING_REQUEST_ID, response)
                    startActivity(intent)

                }, 500)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun showCustomers() {
        val fragment = BusinessOwnerSearchFragment()
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
        mListener?.addFragment(fragment, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                mCustomer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setCustomerInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER) {
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mCustomer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_ASSET_BOOKING_LINE) {
            data?.let { it ->
                if (it.hasExtra(Constant.KEY_ASSET_BOOKING_LINE)) {
                    val requestLine = it.getParcelableExtra<AssetBookingRequestLine>(Constant.KEY_ASSET_BOOKING_LINE)
                    if (requestLine != null) {
                        val requestLines: ArrayList<AssetBookingRequestLine> = arrayListOf()
                        mAdapter?.get()?.let {
                            requestLines.addAll(it)
                        }
                        var index = -1
                        for ((i, line) in requestLines.withIndex()) {
                            if (line.bookingRequestLineID != null && line.bookingRequestLineID != 0) {
                                if (line.bookingRequestLineID == requestLine.bookingRequestLineID) {
                                    index = i
                                    break
                                }
                            } else if (line.uniqueID == requestLine.uniqueID) {
                                index = i
                                break
                            }
                        }
                        if (index == -1)
                            requestLines.add(requestLine)
                        else
                            requestLines[index] = requestLine
                        mAdapter?.clear()
                        requestLines.let {
                            mAdapter?.addAll(it)
                        }
                        calculateEstimatedAmountAndBookingAdvance()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.extras?.let {

                val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                if (it.containsKey(Constant.KEY_ADVANCE_RECEIVED_ID))
                    intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getInt(Constant.KEY_ADVANCE_RECEIVED_ID))
                intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.BOOKING_ADVANCE.Code)
                startActivity(intent)
                activity?.finish()
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SCAN_ASSET) {
            data?.let { it ->
                if (it.hasExtra(Constant.KEY_ASSET_ID)) {
                    val assetNo = it.getStringExtra(Constant.KEY_ASSET_ID)
                    if (selectedPosition == -1)
                        return

                    mAssetBooking?.assetBookingRequestLines?.get(selectedPosition).let {
                        validateAsset4AssignAsset(assetNo
                                ?: "", it?.bookingQuantity
                                ?: 0, it?.bookingRequestLineID ?: 0)
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ASSET_ASSIGNMENT) {
            if (selectedPosition == -1)
                return

            //mAdapter?.clear()
            mAssetBooking?.assetBookingRequestLines?.get(selectedPosition)?.let {
                it.assignQuantity = it.assignQuantity?.plus(1)
                //bindData()
                mAdapter?.update(it)
            }
        }
    }

    private fun updateSelectedCategoriesAndAssets() {
        mAdapter?.get()?.let { it ->
            mSelectedAssetCategories = arrayListOf()
            mSelectedAssets = arrayListOf()
            for (line in it) {
                if (line.assetID != null && line.assetID != 0) {
                    line.assetID?.let { assetID ->
                        mSelectedAssets?.add(assetID)
                    }
                } else if (line.assetCategoryID != null && line.assetCategoryID != 0) {
                    line.assetCategoryID?.let { assetCategoryID ->
                        mSelectedAssetCategories?.add(assetCategoryID)
                    }
                }
            }
        }
    }

    private fun calculateEstimatedAmountAndBookingAdvance() {
        mAdapter?.get()?.let { it ->
            var estimatedAmount = BigDecimal.ZERO
            var netReceivable = BigDecimal.ZERO
            for (line in it) {
                line.estimatedRentAmount?.let {
                    estimatedAmount = estimatedAmount.plus(it)
                }
                line.bookingAdvance?.let {
                    netReceivable = netReceivable.plus(it)
                }
                line.securityDeposit?.let {
                    netReceivable = netReceivable.plus(it)
                }
            }
            updateEstimatedAmount(estimatedAmount)
            updateNetReceivable(netReceivable)
        }
    }



    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("AST_AssetBookingRequests", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mAssets = arrayListOf()
                mAssets.addAll(response.assets)
                mAssetCategories = arrayListOf()
                mAssetCategories.addAll(response.assetCategories)
                //mAssetCategories[0].allowPeriodicInvoice = "Y"
                mAssetTypes = arrayListOf()
                mAssetTypes.addAll(response.assetTypes)
                pickUpCountriesList = arrayListOf()
                pickUpCountriesList.addAll(response.countryMaster)
                pickUpStatesList = arrayListOf()
                pickUpStatesList.addAll(response.stateMaster)
                pickUpCitiesList = arrayListOf()
                pickUpCitiesList.addAll(response.cityMaster)
                pickUpZonesList = arrayListOf()
                pickUpZonesList.addAll(response.zoneMaster)
                pickUpSectorsList = arrayListOf()
                pickUpSectorsList.addAll(response.sectors)
                destinationCountriesList = arrayListOf()
                destinationCountriesList.addAll(response.countryMaster)
                destinationStatesList = arrayListOf()
                destinationStatesList.addAll(response.stateMaster)
                destinationCitiesList = arrayListOf()
                destinationCitiesList.addAll(response.cityMaster)
                destinationZonesList = arrayListOf()
                destinationZonesList.addAll(response.zoneMaster)
                destinationSectorsList = arrayListOf()
                destinationSectorsList.addAll(response.sectors)
                mAdministrativeOffices = response.userOrgBranches

                if (mAdministrativeOffices.isNullOrEmpty())
                    mBinding.spnAdministrationOffice.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mAdministrativeOffices)
                    mBinding.spnAdministrationOffice.adapter = adapter
                    var index = 0
                    val branchID = MyApplication.getPrefHelper().userOrgBranchID
                    for (b in mAdministrativeOffices) {
                        if (b.userOrgBranchID == branchID) {
                            index = mAdministrativeOffices.indexOf(b)
                            break
                        }
                    }
                    mBinding.spnAdministrationOffice.setSelection(index)
                }

                bindData()

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnAdministrationOffice.adapter = null
                mListener?.dismissDialog()
            }

        })
    }

    private fun bindData() {
        updateNetReceivable()
        updateEstimatedAmount()
        mAdapter?.addAll(arrayListOf())
        mAssetBooking?.let { assetBooking ->

            val header = assetBooking.assetBookingRequestHeader
            val lines = assetBooking.assetBookingRequestLines

            header?.let {

                header.customerName?.let {
                    mBinding.edtCustomerName.setText(it)
                }

                header.phoneNumber?.let {
                    mBinding.edtPhoneNumber.setText(it)
                }

                header.email?.let {
                    mBinding.edtEmail.setText(it)
                }

                header.estimatedAmount?.let {
                    updateEstimatedAmount(it)
                }

                header.netReceivable?.let {
                    updateNetReceivable(it)
                }

                header.accountID?.let {
                    mCustomer = BusinessOwnership()
                    mCustomer?.accountID = it
                    mCustomer?.contactName = mBinding.edtCustomerName.text.toString()
                    mCustomer?.phone = mBinding.edtPhoneNumber.text.toString()
                }

            }

            lines?.let {
                if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING && it.size == 1) {
                    it[0].allowPeriodicInvoice = header?.allowPeriodicInvoice
                }
                mAdapter?.addAll(it)

            }

        }
    }

    private fun updateNetReceivable(amount: BigDecimal? = BigDecimal.ZERO) {
        mBinding.edtNetReceivable.setText(formatWithPrecision(amount.toString()))
    }

    private fun updateEstimatedAmount(amount: BigDecimal? = BigDecimal.ZERO) {
        mBinding.edtEstimatedAmount.setText(formatWithPrecision(amount.toString()))
    }

    private fun setCustomerInfo() {
        mCustomer?.let {
            mBinding.edtCustomerName.setText(it.accountName)
            mBinding.edtPhoneNumber.setText(it.phone ?: "")
            mBinding.edtEmail.setText(it.email ?: "")
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        selectedPosition = position
        when (view.id) {
            R.id.txtEdit -> {
                val fragment = AssetBookingLineEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                updateSelectedCategoriesAndAssets()
                bundle.putIntegerArrayList(KEY_SELECTED_ASSET_CATEGORIES, mSelectedAssetCategories)
                bundle.putIntegerArrayList(KEY_SELECTED_ASSETS, mSelectedAssets)
                bundle.putParcelable(Constant.KEY_ASSET_BOOKING_LINE, obj as AssetBookingRequestLine)
                bundle.putParcelableArrayList(KEY_ASSET_CATEGORIES, mAssetCategories)
                bundle.putParcelableArrayList(KEY_ASSETS, mAssets)
                bundle.putParcelableArrayList(KEY_ASSET_TYPES, mAssetTypes)
                bundle.putParcelableArrayList(KEY_PICK_UP_COUNTRIES, pickUpCountriesList)
                bundle.putParcelableArrayList(KEY_DESTINATION_COUNTRIES, destinationCountriesList)
                bundle.putParcelableArrayList(KEY_PICK_UP_STATES, pickUpStatesList)
                bundle.putParcelableArrayList(KEY_DESTINATION_STATES, destinationStatesList)
                bundle.putParcelableArrayList(KEY_PICK_UP_CITIES, pickUpCitiesList)
                bundle.putParcelableArrayList(KEY_DESTINATION_CITIES, destinationCitiesList)
                bundle.putParcelableArrayList(KEY_PICK_UP_ZONES, pickUpZonesList)
                bundle.putParcelableArrayList(KEY_DESTINATION_ZONES, destinationZonesList)
                bundle.putParcelableArrayList(KEY_PICK_UP_SECTORS, pickUpSectorsList)
                bundle.putParcelableArrayList(KEY_DESTINATION_SECTORS, destinationSectorsList)
                if (mBinding.spnAdministrationOffice.selectedItem != null) {
                    val branch = mBinding.spnAdministrationOffice.selectedItem as UMXUserOrgBranches?
                    branch?.userOrgBranchID?.let {
                        bundle.putInt(KEY_BRANCH_ID, it)
                    }
                }
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_BOOKING_LINE)

                mListener?.showToolbarBackButton(R.string.asset_booking)
                mListener?.addFragment(fragment, true)
            }
            R.id.txtDelete -> {
                val bookingRequestId = mAssetBooking?.assetBookingRequestHeader?.bookingRequestID
                if (mAdapter == null || mAdapter?.get().isNullOrEmpty() || (mAdapter?.get()?.size!! == 1 && (mVoucherNo > 0 || bookingRequestId != null)))
                    mListener?.showSnackbarMsg(getString(R.string.delete_message))
                else {
                    val assetBooking = obj as AssetBookingRequestLine
                    mAdapter?.remove(assetBooking)
                    calculateEstimatedAmountAndBookingAdvance()
                }
            }
            R.id.btnAssignAsset -> {
                /*     val assetBookingRequestLine = obj as AssetBookingRequestLine
                     if (assetBookingRequestLine.assetID != 0) {
                         validateAsset4AssignAsset(assetBookingRequestLine.assetID.toString(), MyApplication.getPrefHelper().accountId, assetBookingRequestLine.bookingQuantity
                                 ?: 0)
                     } else {*/

                val assetBookingLine = obj as AssetBookingRequestLine
                if (!assetBookingLine.assetNo.isNullOrEmpty() && assetBookingLine.isMovable.equals("N")) {
                    validateAsset4AssignAsset(assetBookingLine.assetNo ?: "",
                            assetBookingLine.bookingQuantity
                                    ?: 0, assetBookingLine.bookingRequestLineID ?: 0)
                } else {
                    val intent = Intent(context, ScanActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_ASSIGN_ASSET)
                    startActivityForResult(intent, REQUEST_CODE_SCAN_ASSET)
                }
                //}
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    private fun validateAsset4AssignAsset(assetNo: String, bookingQuantity: Int, bookingLineId: Int) {
        mListener?.showProgressDialog()
        APICall.validateAsset4AssignRent(assetNo, bookingQuantity, bookingLineId, object : ConnectionCallBack<ValidateAssetForAssignAndReturnResponse> {
            override fun onSuccess(response: ValidateAssetForAssignAndReturnResponse) {
                mListener?.dismissDialog()
                navigateToCheckListScreen(response.assetId ?: 0)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    fun navigateToCheckListScreen(assetNo: Int) {
        if (selectedPosition == -1)
            return

        mAssetBooking?.assetBookingRequestLines?.get(selectedPosition).let {
            val assetPreCheckListFragment = AssetPreCheckListFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            bundle.putInt(Constant.KEY_ASSET_ID, assetNo)
            bundle.putParcelable(Constant.KEY_ASSET_BOOKING_LINE, it)
            assetPreCheckListFragment.arguments = bundle
            assetPreCheckListFragment.setTargetFragment(this@AssetBookingEntryFragment, REQUEST_CODE_ASSET_ASSIGNMENT)
            mListener?.showToolbarBackButton(R.string.asset_assignment)
            mListener?.addFragment(assetPreCheckListFragment, true)
        }

    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun finish()
        fun showSnackbarMsg(message: String?)
    }

}