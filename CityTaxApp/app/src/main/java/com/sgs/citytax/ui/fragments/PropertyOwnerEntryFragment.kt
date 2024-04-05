package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
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
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.StorePropertyOwnershipWithPropertyOwnerResponse
import com.sgs.citytax.api.response.ValidateAssetForAssignAndReturnResponse
import com.sgs.citytax.databinding.FragmentPropertyOwnerEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.PropertyOwnerOnBoardFragment
import com.sgs.citytax.ui.adapter.PropertyOwnerBookingAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.KEY_ASSETS
import com.sgs.citytax.util.Constant.KEY_ASSET_CATEGORIES
import com.sgs.citytax.util.Constant.KEY_ASSET_TYPES
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
import kotlin.collections.ArrayList

class PropertyOwnerEntryFragment : BaseFragment(), IClickMultiListener {

    private lateinit var mBinding: FragmentPropertyOwnerEntryBinding
    private var mListener: Listener? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mCustomer: StorePropertyOwnershipWithPropertyOwnerResponse? = null
    private var mAdapter: PropertyOwnerBookingAdapter? = null
    private var mPropertyId: Int? = null
    private var mPropertyOwnerShip: Int = 0
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
    private var mPropertyExemptionReasons: MutableList<COMPropertyExemptionReasons> = arrayListOf()
    private var swipeLock: Boolean = false

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                mPropertyId = it.getInt(Constant.KEY_PRIMARY_KEY)
            if (it.containsKey(Constant.KEY_PROPERTY_OWNERSHIP))
                mPropertyOwnerShip = it.getInt(Constant.KEY_PROPERTY_OWNERSHIP)
            if (it.containsKey(Constant.KEY_PROPERTY_OWNER_DETAILS))
                mCustomer = it.getParcelable(Constant.KEY_PROPERTY_OWNER_DETAILS)
        }
        //endregion
        if (mCustomer != null) {
            swipeLock = true
            setDisable()
            if (TextUtils.isEmpty(mCustomer!!.toDate)) {
                mBinding.edtToDate.isEnabled = true
                mBinding.btnSave.visibility = VISIBLE
            }
        }

        setViews()
        setEvents()
        bindSpinner()
        CallExistingDocsInfo()
        CallExistingNotesInfo()


    }

    private fun CallExistingDocsInfo() {
        if (mPropertyOwnerShip != 0 && mPropertyOwnerShip != null) {
            mListener?.showProgressDialog()
            APICall.getDocumentDetails(mPropertyOwnerShip.toString(), "CRM_PropertyOwnership", object : ConnectionCallBack<List<COMDocumentReference>> {
                override fun onSuccess(response: List<COMDocumentReference>) {
                    mListener?.dismissDialog()
                    ObjectHolder.documents = response as ArrayList<COMDocumentReference>
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    if (message.isNotEmpty()) {
                    }
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }

    private fun CallExistingNotesInfo() {
        if (mPropertyOwnerShip != 0 && mPropertyOwnerShip != null) {
            mListener?.showProgressDialog()
            APICall.getNotesDetails(mPropertyOwnerShip.toString(), "CRM_PropertyOwnership", object : ConnectionCallBack<List<COMNotes>> {
                override fun onSuccess(response: List<COMNotes>) {
                    mListener?.dismissDialog()
                    ObjectHolder.notes = response as ArrayList<COMNotes>
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)
                }
            })
        }
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_owner_entry, container, false)
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

        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            mBinding.edtFromDate.isEnabled = false
            mBinding.edtToDate.isEnabled = false
            mBinding.edtRegistrationNo.isEnabled = false
            mBinding.tvNewOwner.visibility = GONE
            mBinding.spnExemptionReason.isClickable = false
            mBinding.btnSave.visibility = GONE
        }

        mBinding.edtFromDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtFromDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtToDate.setDisplayDateFormat(displayDateFormat)

        mAdapter = PropertyOwnerBookingAdapter(this, mCode, swipeLock)
        mBinding.recyclerView.adapter = mAdapter
        val itemDecor = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        mBinding.recyclerView.addItemDecoration(itemDecor)


        if (mCustomer == null) {
            mBinding.edtToDate.isEnabled = false
        }

    }

    private fun setDisable() {
        mBinding.edtFromDate.isEnabled = false
        mBinding.edtToDate.isEnabled = false
        mBinding.edtRegistrationNo.isEnabled = false
        mBinding.llDocuments.setOnClickListener(null)
        mBinding.llNotes.setOnClickListener(null)
        mBinding.tvNewOwner.visibility = GONE
        mBinding.spnExemptionReason.isClickable = false
        mBinding.btnSave.visibility = GONE

    }

    private fun setEvents() {

        mBinding.edtFromDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.edtFromDate.text?.toString()?.let {
                    if (it.isNotEmpty())
                    //  mBinding.edtEndDate.setMinDate(parseDate(it, DateTimeTimeSecondFormat).time)
                        mBinding.edtToDate.setMinDate(parseDate(it, displayDateFormat).time)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })



        mBinding.edtToDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.edtToDate.text?.toString()?.let {


                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        mBinding.tvCreateCustomer.setOnClickListener {

            val fragment = BusinessOwnerEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            fragment.arguments = bundle
            //endregion

            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)

            mListener?.showToolbarBackButton(R.string.property_owner)
            mListener?.addFragment(fragment, true)
        }

        mBinding.llDocuments.setOnClickListener {
            val fragment = LocalDocumentsMasterFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            bundle.putInt(Constant.KEY_PROPERTY_OWNERSHIP, mPropertyOwnerShip ?: 0)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
            mListener?.showToolbarBackButton(R.string.documents)
            mListener?.addFragment(fragment, true)
        }
        mBinding.llNotes.setOnClickListener {
            val fragment = LocalNotesMasterFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            bundle.putInt(Constant.KEY_PROPERTY_OWNERSHIP, mPropertyOwnerShip ?: 0)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)
            mListener?.showToolbarBackButton(R.string.notes)
            mListener?.addFragment(fragment, true)
        }


        mBinding.tvNewOwner.setOnClickListener {
            selectedPosition = -1
            val fragment = PropertyOwnerOnBoardFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            //updateSelectedCategoriesAndAssets()
            bundle.putIntegerArrayList(KEY_SELECTED_ASSET_CATEGORIES, mSelectedAssetCategories)
            bundle.putIntegerArrayList(KEY_SELECTED_ASSETS, mSelectedAssets)
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
            if (mBinding.spnExemptionReason.selectedItem != null) {
                val branch = mBinding.spnExemptionReason.selectedItem as COMPropertyExemptionReasons?
                branch?.propertyExemptionReason?.let {
                    //  bundle.putInt(KEY_BRANCH_ID, it)
                }
            }
            fragment.arguments = bundle
            //endregion
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_OWNER_LIST)

            mListener?.showToolbarBackButton(R.string.property_owner)
            mListener?.addFragment(fragment, true)
        }

        mBinding.btnSave.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (isValid())
                    save()
            }
        })
    }

    private fun isValid(): Boolean {
        if (mBinding.edtFromDate.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.from_date)}")
            return false
        }
     /*   if (mBinding.edtRegistrationNo.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.registration_no)}")
            return false
        }*/
        if (mAdapter == null || mAdapter?.getPropertyOwnersList().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.property_owner)}")
            return false
        }
        if (TextUtils.isEmpty(mBinding.txtNumberOfDocuments.text.toString()) || (mBinding.txtNumberOfDocuments.text.toString()).toInt() < 1) {
            mListener?.showAlertDialog("${getString(R.string.msg_provide)} ${getString(R.string.documents)} ")
            return false
        }
        return true
    }

    private fun save() {
        mListener?.showProgressDialog()
        val propertyOwnership = PropertyOwnershipPayload()
        propertyOwnership.propertyID = mPropertyId
        propertyOwnership.fromDate = serverFormatDate(mBinding.edtFromDate.text.toString())
        if (!TextUtils.isEmpty(mBinding.edtToDate.text.toString())) {
            propertyOwnership.toDate = serverFormatDate(mBinding.edtToDate.text.toString())
        }
        propertyOwnership.registrationNo = mBinding.edtRegistrationNo.text.toString()

        val selectedExemption = mBinding.spnExemptionReason.selectedItem
        if (selectedExemption != null) {
            if ((selectedExemption as COMPropertyExemptionReasons).propertyExemptionReasonID != -1) {
                propertyOwnership.propertyExemptionReasonID = selectedExemption.propertyExemptionReasonID
            }
        }
        if (mCustomer != null) {
            propertyOwnership.propertyOwnershipID = mCustomer!!.propertyOwnershipID
        }

        val propertyOwnersPayload: ArrayList<PropertyOwnersPayload>? = mAdapter?.getPropertyOwnersList()

        APICall.storePropertyOwnershipWithPropertyOwner(propertyOwnership, propertyOwnersPayload, ObjectHolder.documents, ObjectHolder.notes, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                Log.d("TAG", "onSuccess: ")
                Handler().postDelayed({
                    ObjectHolder.notes.clear()
                    ObjectHolder.documents.clear()
                    mVoucherNo = response
                    mListener?.popBackStack()
                    onBackPressed()
                }, 500)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                })
                Log.d("TAG", "onFailure: ")
            }
        })


    }

    private fun showCustomers() {
        val fragment = BusinessOwnerSearchFragment()
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
        mListener?.addFragment(fragment, true)
    }


    private fun fetchChildEntriesCount() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "CRM_PropertyOwnership"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = mPropertyOwnerShip.toString()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mListener?.showToolbarBackButton(R.string.title_property_txt)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                //  mCustomer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setCustomerInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER) {
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    //  mCustomer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_OWNER_LIST) {
            data?.let { it ->
                if (it.hasExtra(Constant.KEY_ASSET_BOOKING_LINE) || it.hasExtra(Constant.KEY_PROPERTY_DETAILS)) {
                    val propertyOwnersData = it.getParcelableExtra<PropertyOwnersData>(Constant.KEY_PROPERTY_DETAILS)
                    if (selectedPosition < 0) {
                        propertyOwnersData?.let {
                            if (!mAdapter?.checkOwnerExist(it)!!) {
                                mAdapter?.add(it)
                            }
                            else{mListener?.showSnackbarMsg(getString(R.string.owner_exist))}

                        }
                    } else {
                        mAdapter?.replace(selectedPosition, propertyOwnersData)
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
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ASSET_ASSIGNMENT) {
            if (selectedPosition == -1)
                return
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_NOTES_LIST) {
            ObjectHolder.notesCount = ObjectHolder.notes.size
            bindNoteCounts()

        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_DOCUMENTS_LIST) {
            ObjectHolder.docCount = ObjectHolder.documents.size
            bindDocCounts()
        }
    }

    private fun bindCounts(tableOrViewName: String, count: Int) {
        when (tableOrViewName) {
            "COM_DocumentReferences" -> {
//                mBinding.txtNumberOfDocuments.text = "$count"
                ObjectHolder.docCount = count
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "TableName"
                filterColumn.columnValue = "CRM_PropertyOwnership"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue = mPropertyOwnerShip.toString()
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
            }
            "COM_Notes" -> {
//                mBinding.txtNumberOfNotes.text = "$count"
                ObjectHolder.notesCount = count
            }

        }
        bindNoteCounts()
        bindDocCounts()
    }

    private fun bindNoteCounts() {
        mBinding.txtNumberOfNotes.text = "${ObjectHolder.notesCount}"

    }

    private fun bindDocCounts() {
        mBinding.txtNumberOfDocuments.text = "${ObjectHolder.docCount}"

    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_PropertyOwnership", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mAssets = arrayListOf()
                mAssets.addAll(response.assets)
                mAssetCategories = arrayListOf()
                mAssetCategories.addAll(response.assetCategories)
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
                //mComboStaticValues = response.comboStaticValues
                mPropertyExemptionReasons = response.propertyExemption


                if (mPropertyExemptionReasons.isNullOrEmpty())
                    mBinding.spnExemptionReason.adapter = null
                else {
                    mPropertyExemptionReasons?.add(0, COMPropertyExemptionReasons(getString(R.string.select), -1))
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mPropertyExemptionReasons)
                    mBinding.spnExemptionReason.adapter = adapter
                }

                bindData()

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnExemptionReason.adapter = null
                mListener?.dismissDialog()
            }

        })
    }

    private fun bindData() {
        fetchChildEntriesCount()
        updateNetReceivable()
        updateEstimatedAmount()

        if (mCustomer != null) {
            mBinding.edtFromDate.setText(displayFormatDate(mCustomer!!.fromDate))
            if (!TextUtils.isEmpty(mCustomer!!.toDate)) {
                mBinding.edtToDate.setText(displayFormatDate(mCustomer!!.toDate))
            }
            mBinding.edtRegistrationNo.setText(mCustomer!!.registrationNo)

            if (mCustomer!!.propertyExemptionReasonID != null) {
                for ((index, obj) in mPropertyExemptionReasons.withIndex()) {
                    if (obj.propertyExemptionReasonID == mCustomer!!.propertyExemptionReasonID!!.toInt()) {
                        mBinding.spnExemptionReason.setSelection(index)
                    }
                }
            }

            setPropertyOwnerDetails()
        }
    }

    private fun setPropertyOwnerDetails() {
        val propertyOwner: ArrayList<BusinessOwnership> = arrayListOf()
        val propertyNominee: ArrayList<BusinessOwnership> = arrayListOf()
        val spinRelationValue: ArrayList<ComComboStaticValues> = arrayListOf()
        var propertyOwnersDataList: ArrayList<PropertyOwnersData> = arrayListOf()
        for (obj in mCustomer!!.propertyowners) {
            var propertyOwnersData = PropertyOwnersData()
            val propOwner = BusinessOwnership()
            propOwner.accountName = obj.ownerAccountName
            propOwner.accountID = obj.ownerAccountID

            val propNominee = BusinessOwnership()
            propNominee.accountName = obj.nomineeAccountName
            propNominee.accountID = obj.nomineeAccountID

            val relationValue = ComComboStaticValues()
            relationValue.code = obj.relationshipType
            relationValue.comboValue = obj.cmbval

            propertyOwnersData.owner = propOwner
            propertyOwnersData.nominee = propNominee
            propertyOwnersData.relation = relationValue

            propertyOwnersDataList.add(propertyOwnersData)
        }
        mAdapter!!.addAll(propertyOwnersDataList)
    }

    private fun updateNetReceivable(amount: BigDecimal? = BigDecimal.ZERO) {
        mBinding.edtNetReceivable.setText(formatWithPrecision(amount.toString()))
    }

    private fun updateEstimatedAmount(amount: BigDecimal? = BigDecimal.ZERO) {
        mBinding.edtEstimatedAmount.setText(formatWithPrecision(amount.toString()))
    }

    private fun setCustomerInfo() {
        mCustomer?.let {
            mBinding.edtFromDate.setText(it.fromDate)
            mBinding.edtToDate.setText(it.toDate ?: "")
            mBinding.edtRegistrationNo.setText(it.registrationNo ?: "")
        }
    }

    override fun onClick(view: View, position: Int, propOwner: Any) {
        selectedPosition = position
        when (view.id) {
            R.id.txtEdit -> {
                val fragment = PropertyOwnerOnBoardFragment()

                //region SetArguments
                val bundle = Bundle()
                val propOwnerData = propOwner as PropertyOwnersData
                bundle.putParcelable(Constant.KEY_PROPERTY_DETAILS, propOwnerData)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)

                if (mBinding.spnExemptionReason.selectedItem != null) {
                    val branch = mBinding.spnExemptionReason.selectedItem as COMPropertyExemptionReasons?
                    branch?.propertyExemptionReason?.let {
                        // bundle.putInt(KEY_BRANCH_ID, it)
                    }
                }
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_OWNER_LIST)

                mListener?.showToolbarBackButton(R.string.property_owner)
                mListener?.addFragment(fragment, true)
            }
            R.id.txtDelete -> {
                //val bookingRequestId = mAssetBooking?.assetBookingRequestHeader?.bookingRequestID
                if (mAdapter == null || mAdapter?.getPropertyOwnersList().isNullOrEmpty() || (mAdapter?.getPropertyOwnersList()?.size!! == 1))
                    mListener?.showSnackbarMsg(getString(R.string.delete_message))
                else {
                    mAdapter?.remove(propOwner as PropertyOwnersData)
                }
            }
            R.id.btnAssignAsset -> {

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
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun finish()
        fun showSnackbarMsg(message: String?)
        fun popBackStack()
        var screenMode: Constant.ScreenMode
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
    }

}