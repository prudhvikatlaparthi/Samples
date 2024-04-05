package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetEstimatedAmount4ServiceTax
import com.sgs.citytax.api.payload.NewServiceRequest
import com.sgs.citytax.api.payload.SRUpdate
import com.sgs.citytax.api.payload.SaveServiceTaxRequest
import com.sgs.citytax.api.response.Business
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.ServiceRequestTable
import com.sgs.citytax.api.response.VUCRMServiceTaxRequest
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentServiceEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.adapter.DocumentPreviewAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.REQUEST_CODE_SERVICE_BOOKING_DETAIL
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class ServiceEntryFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentServiceEntryBinding
    private var mListener: Listener? = null
    private var helper: LocationHelper? = null
    private var mCountries: List<COMCountryMaster> = arrayListOf()
    private var mSectors: List<COMSectors> = arrayListOf()
    private var mStates: List<COMStateMaster> = arrayListOf()
    private var mStatus: List<COMStatusCode> = arrayListOf()
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mCities: List<VUCOMCityMaster> = arrayListOf()
    private var mZones: List<COMZoneMaster> = arrayListOf()
    private var mServiceTypes: ArrayList<CRMServiceType> = arrayListOf()
    private var mUnits: ArrayList<VUINVMeasurementUnits> = arrayListOf()
    private var mServiceSubTypes: ArrayList<CRMServiceSubType> = arrayListOf()
    private var mCustomer: BusinessOwnership? = null
    private var mBusiness: Business? = Business()
    private var mServiceRequest: NewServiceRequest? = null
    private var mDocumentReference = COMDocumentReference()
    private var mAdapter: DocumentPreviewAdapter? = null
    private var mImageFilePath = ""
    private var selectedVUCRMServiceTaxRequest: VUCRMServiceTaxRequest? = null
    private var isEstimationCalculated: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance() = ServiceEntryFragment().apply {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_service_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_SERVICE_TAX))
                mServiceRequest = it.getParcelable(Constant.KEY_SERVICE_TAX)
        }
        setEvents()
        bindSpinner()
    }

    override fun onDestroy() {
        super.onDestroy()
        helper?.disconnect()
    }

    private fun updateUnitCodeAndArea(unitCode: String?) {
        if (unitCode != null && !TextUtils.isEmpty(unitCode)) {
            mBinding.llUnit.visibility = VISIBLE
            mBinding.tilArea.visibility = VISIBLE
            var index = -1
            if (!mUnits.isNullOrEmpty()) {
                for (vuInvMeasurementUnit in mUnits) {
                    if (vuInvMeasurementUnit.unitCode != null && vuInvMeasurementUnit.unitCode == unitCode) {
                        index = mUnits.indexOf(vuInvMeasurementUnit)
                        break
                    }
                }
            }
            if (index == -1) index = 0
            mBinding.spnUnit.setSelection(index)
        } else {
            mBinding.llUnit.visibility = GONE
            mBinding.tilArea.visibility = GONE
        }
    }

    private fun preparePayloadAndSave() {
        val serviceRequest = NewServiceRequest()
        if (mServiceRequest == null)
            mServiceRequest = NewServiceRequest()
        if(mBinding.llUnit.isVisible && mBinding.tilArea.isVisible) {
            if (mBinding.spnUnit.selectedItem != null) {
                val serviceUnit = mBinding.spnUnit.selectedItem as VUINVMeasurementUnits?
                serviceUnit?.unitCode?.let {
                    serviceRequest.unitcode = it
                    mServiceRequest?.unitcode = it
                }
            }
        }

    if (mBinding.spnServiceType.selectedItem != null) {
        val serviceType = mBinding.spnServiceType.selectedItem as CRMServiceType?
        serviceType?.serviceTypeID?.let {
            serviceRequest.serviceTypeID = it
            mServiceRequest?.serviceTypeID = it
        }
    }


        if (mBinding.spnServiceSubType.selectedItem != null) {
            val serviceSubType = mBinding.spnServiceSubType.selectedItem as CRMServiceSubType?
            serviceSubType?.serviceSubTypeID?.let {
                serviceRequest.serviceSubTypeID = it
                mServiceRequest?.serviceSubTypeID = it
            }
            serviceSubType?.pricingRuleID?.let {
                serviceRequest.pricingRuleID = it
                mServiceRequest?.pricingRuleID = it
            }
        }
        if (mBinding.spnCountry.selectedItem != null) {
            val country = mBinding.spnCountry.selectedItem as COMCountryMaster?
            country?.countryCode?.let {
                serviceRequest.countryCode = it
                mServiceRequest?.countryCode = it
            }
        }
        if (mBinding.spnState.selectedItem != null) {
            val state = mBinding.spnState.selectedItem as COMStateMaster?
            state?.stateID?.let {
                serviceRequest.stateID = it
                mServiceRequest?.stateID = it
            }
        }
        if (mBinding.spnCity.selectedItem != null) {
            val city = mBinding.spnCity.selectedItem as VUCOMCityMaster?
            city?.cityID?.let {
                serviceRequest.cityID = it
                mServiceRequest?.cityID = it
            }
        }
        if (mBinding.spnSector.selectedItem != null) {
            val sector = mBinding.spnSector.selectedItem as COMSectors?
            sector?.sectorId?.let {
                serviceRequest.sectorID = it
                mServiceRequest?.sectorID = it
            }
        }
        if (mBinding.spnZone.selectedItem != null) {
            val zone = mBinding.spnZone.selectedItem as COMZoneMaster?
            zone?.zoneID?.let {
                serviceRequest.zoneID = it
                mServiceRequest?.zoneID = it
            }
        }
        if (mBinding.spnStatus.selectedItem != null) {
            val status = mBinding.spnStatus.selectedItem as COMStatusCode?
            status?.statusCode?.let {
                serviceRequest.statusCode = it
                mServiceRequest?.statusCode = it
            }
        }
        if(mBinding.checkbox.isChecked){
            serviceRequest.assignTo3rdParty = "Y"
            mServiceRequest?.assignTo3rdParty = "Y"
        }else{
            serviceRequest.assignTo3rdParty = "N"
            mServiceRequest?.assignTo3rdParty = "N"
        }
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) {
            serviceRequest.street = mBinding.edtStreet.text.toString().trim()
            mServiceRequest?.street = serviceRequest.street
        }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) {
            serviceRequest.zip = mBinding.edtZipCode.text.toString().trim()
            mServiceRequest?.zip = serviceRequest.zip
        }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) {
            serviceRequest.plot = mBinding.edtPlot.text.toString().trim()
            mServiceRequest?.plot = serviceRequest.plot
        }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) {
            serviceRequest.block = mBinding.edtBlock.text.toString().trim()
            mServiceRequest?.block = serviceRequest.block
        }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(mBinding.edtDoorNo.text.toString())) {
            serviceRequest.doorNo = mBinding.edtDoorNo.text.toString().trim()
            mServiceRequest?.doorNo = serviceRequest.doorNo
        }
        if (mBinding.edtArea.text != null && !TextUtils.isEmpty(mBinding.edtArea.text.toString())) {
            serviceRequest.area = mBinding.edtArea.text.toString().trim()
            mServiceRequest?.area = serviceRequest.area
        }
        if (mBinding.edtAdvanceAmount.text != null && !TextUtils.isEmpty(mBinding.edtAdvanceAmount.text.toString())) {
            serviceRequest.advanceAmount = (currencyToDouble(mBinding.edtAdvanceAmount.text.toString())?.toDouble() ?: 0.0).toBigDecimal()
            mServiceRequest?.advanceAmount = serviceRequest.advanceAmount
        }
        if (mBinding.edtEstimatedAmount.text != null && !TextUtils.isEmpty(mBinding.edtEstimatedAmount.text.toString())) {
            serviceRequest.estimatedAmount = (currencyToDouble(mBinding.edtEstimatedAmount.text.toString())?.toDouble() ?: 0.0).toBigDecimal()
            mServiceRequest?.estimatedAmount = serviceRequest.estimatedAmount
        }
        serviceRequest.description = mBinding.etDescription.text.toString()
        mServiceRequest?.description = serviceRequest.description
        serviceRequest.serviceRequestDate = formatDate(mBinding.edtServiceRequestDate.text.toString(), Constant.DateFormat.DFddMMyyyyHHmmss, Constant.DateFormat.SERVER)
        mServiceRequest?.serviceRequestDate = serviceRequest.serviceRequestDate
        if (mBinding.edtServiceDate.text != null && !TextUtils.isEmpty(mBinding.edtServiceDate.text.toString())) {
            serviceRequest.serviceDate = formatDate(mBinding.edtServiceDate.text.toString(), Constant.DateFormat.DFddMMyyyyHHmmss, Constant.DateFormat.SERVER)
            mServiceRequest?.serviceDate = serviceRequest.serviceDate
        }
        if (mBinding.edtLatitude.text != null && !TextUtils.isEmpty(mBinding.edtLatitude.text.toString())) {
            serviceRequest.latitude = mBinding.edtLatitude.text.toString().toDouble()
            mServiceRequest?.latitude = serviceRequest.latitude
        }
        if (mBinding.edtLongitude.text != null && !TextUtils.isEmpty(mBinding.edtLongitude.text.toString())) {
            serviceRequest.longitude = mBinding.edtLongitude.text.toString().toDouble()
            mServiceRequest?.longitude = serviceRequest.longitude
        }
        var accountID = 0
        mBusiness?.accountID?.let {
            if (it != 0)
                accountID = it
        }
        mCustomer?.accountID?.let {
            if (it != 0)
                accountID = it
        }
        serviceRequest.accountID = "$accountID"
        mServiceRequest?.accountID = serviceRequest.accountID
        val saveServiceTaxRequest = SaveServiceTaxRequest()
        mServiceRequest?.serviceRequestNo?.let {
            if (!TextUtils.isEmpty(it)) {
                serviceRequest.serviceRequestNo = it
                mServiceRequest?.serviceRequestNo = serviceRequest.serviceRequestNo
                mBinding.etComments.text?.toString()?.let { comment ->
                    if (!TextUtils.isEmpty(comment)) {
                        val srUpdate = SRUpdate()
                        srUpdate.comments = comment
                        srUpdate.serviceRequestNo = it
                        saveServiceTaxRequest.srUpdate = srUpdate
                    }
                }
            }
        }
        saveServiceTaxRequest.serviceRequest = serviceRequest
        val documents: ArrayList<COMDocumentReference> = arrayListOf()
        if (!mDocumentReference.data.isNullOrEmpty())
            documents.add(mDocumentReference)
        saveServiceTaxRequest.attachment = documents
        save(saveServiceTaxRequest, false)
    }

    private fun navigateToPayment() {
        val payment = MyApplication.resetPayment()
        var netReceivable = BigDecimal.ZERO
        mServiceRequest?.advanceAmount?.let {
            netReceivable = it
        }
        payment.amountDue = netReceivable
        payment.amountTotal = netReceivable
        payment.minimumPayAmount = netReceivable
        mServiceRequest?.accountID?.let {
            payment.customerID = it.toInt()
        }
        payment.paymentType = Constant.PaymentType.SERVICE_REQUEST
        mServiceRequest?.serviceRequestNo?.let {
            payment.serviceRequestNo = it.toInt()
        }
        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
        startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
    }

    private fun setEvents() {
        mBinding.btnClearImage.setOnClickListener {
            mBinding.imgDocument.setImageBitmap(null)
            mDocumentReference = COMDocumentReference()
            mBinding.btnClearImage.visibility = GONE
        }
        mBinding.ivAddLocation.setOnClickListener {
            var mLatitude = 0.0
            var mLongitude = 0.0
            if (mBinding.edtLatitude.text.toString().trim().isNotEmpty()) {
                mLatitude = mBinding.edtLatitude.text.toString().trim().toDouble()
            }

            if (mBinding.edtLongitude.text.toString().trim().isNotEmpty()) {
                mLongitude = mBinding.edtLongitude.text.toString().trim().toDouble()
            }
            val dialog: LocateDialogFragment = LocateDialogFragment.newInstance(mLatitude, mLongitude)
            dialog.show(childFragmentManager, null)
        }
        mBinding.btnGet.setOnClickListener {
            fetchEstimatedAmount()
        }
        mBinding.btnSave.setOnClickListener {
            if (mBinding.btnSave.text == getString(R.string.proceed)) {
                if (mServiceRequest?.advanceAmount != null && mServiceRequest?.advanceAmount.toString().toBigDecimal() > BigDecimal.ZERO) {
                    mListener?.showAlertDialog(
                            getString(R.string.confirm_req_with_amount)
                                    + " ${formatWithPrecision(mServiceRequest?.advanceAmount.toString())}?", DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                        navigateToPayment()
                    }, DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                    })

                } else {
                    mListener?.showAlertDialog(getString(R.string.confirm_req), DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                        val saveServiceTaxRequest = SaveServiceTaxRequest()
                        mServiceRequest?.statusCode = "CRM_ServiceRequests.Confirmed"
                        saveServiceTaxRequest.serviceRequest = mServiceRequest
                        save(saveServiceTaxRequest, true)
                    }, DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                    })
                }
            } else {
                if (isValid()) {
                    if (isEstimationCalculated) {
                        preparePayloadAndSave()
                    } else {
                        mListener?.showSnackbarMsg(getString(R.string.pls_cal_estimation_b4_saving))
                    }
                }
            }
        }
        mBinding.btnCamera.setOnClickListener {
            showImagePickerOptions()
        }

        mBinding.spnServiceType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var serviceType: CRMServiceType? = CRMServiceType()
                if (p0 != null && p0.selectedItem != null)
                    serviceType = p0.selectedItem as CRMServiceType?
                serviceType?.serviceTypeID?.let {
                    filterSubType(it)
                }
            }
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

        mBinding.edtCustomerName.setOnClickListener {
            val fragment = BusinessOwnerSearchFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OR_CITIZEN_SEARCH)
            mListener?.showToolbarBackButton(R.string.citizen)
            mListener?.addFragment(fragment, true)
        }

        mBinding.spnCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var country: COMCountryMaster? = COMCountryMaster()
                if (p0 != null && p0.selectedItem != null)
                    country = p0.selectedItem as COMCountryMaster
                filterStates(country?.countryCode)
            }
        }

        mBinding.spnState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var state: COMStateMaster? = COMStateMaster()
                if (p0 != null && p0.selectedItem != null)
                    state = p0.selectedItem as COMStateMaster
                filterCities(state?.stateID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnServiceSubType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var serviceSubType: CRMServiceSubType? = CRMServiceSubType()
                if (p0 != null && p0.selectedItem != null)
                    serviceSubType = p0.selectedItem as CRMServiceSubType?
                updateUnitCodeAndArea(serviceSubType?.unitCode)
                serviceSubType?.advanceAmount?.let {
                    mBinding.edtAdvanceAmount.setText(formatWithPrecision(it))
                }
                if(mServiceRequest == null){
                    fetchEstimatedAmount()
                }
            }
        }

        mBinding.spnCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var city: VUCOMCityMaster? = VUCOMCityMaster()
                if (p0 != null && p0.selectedItem != null)
                    city = p0.selectedItem as VUCOMCityMaster
                filterZones(city?.cityID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.edtArea.addTextChangedListener (object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (mBinding.tilArea.isVisible) {
                    isEstimationCalculated = false
                }
                s?.toString()?.let { enteredText ->
                    if(enteredText.equals(".") && enteredText.length == 1 ) {
                        mBinding.edtArea.setText("0.")
                        mBinding.edtArea.setSelection(mBinding.edtArea.text.toString().length)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    private fun bindLocation() {
        if (mServiceRequest?.latitude != 0.0 && mServiceRequest?.longitude != 0.0 && mServiceRequest?.latitude != null && mServiceRequest?.longitude != null) {
            mBinding.edtLongitude.setText(mServiceRequest?.longitude.toString())
            mBinding.edtLatitude.setText(mServiceRequest?.latitude.toString())
            mBinding.ivAddLocation.isEnabled = false
            fetchComments()
        } else {
            helper?.fetchLocation()
            helper?.setListener(object : LocationHelper.Location {
                override fun found(latitude: Double, longitude: Double) {
                    mListener?.dismissDialog()
                    mBinding.edtLongitude.setText(longitude.toString())
                    mBinding.edtLatitude.setText(latitude.toString())
                    fetchComments()
                }

                override fun start() {
                    mListener?.showProgressDialog(R.string.msg_location_fetching)
                }
            })
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_ServiceTax", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()
                mCountries = response.countryMaster
                mStates = response.stateMaster
                mCities = response.cityMaster
                mSectors = response.sectors
                mZones = response.zoneMaster
                mStatus = response.statusCodes
                if (response.serviceTypes.isNullOrEmpty())
                    mBinding.spnServiceType.adapter = null
                else {
                    var index = -1
                    mServiceTypes.add(CRMServiceType(-1, getString(R.string.select), "", ""))
                    mServiceTypes.addAll(response.serviceTypes)
                    if (mServiceRequest!=null) {
                        for ((i, status) in mServiceTypes.withIndex()) {
                            if (index == -1 && status.serviceType == mServiceRequest?.serviceType) {
                                index = i
                                break
                            }
                        }
                        if (index < 0) index = 0
                    }
                    mBinding.spnServiceType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mServiceTypes)
                      mBinding.spnServiceType.setSelection(index)
                }
                mServiceSubTypes.addAll(response.serviceSubTypes)
                if (mStatus.isNullOrEmpty())
                    mBinding.spnStatus.adapter = null
                else {
                    var index = -1
                    for ((i, status) in mStatus.withIndex()) {
                        if (index == -1 && status.statusCode == "CRM_ServiceRequests.New") {
                            index = i
                            break
                        }
                    }
                    if (index < 0) index = 0
                    mBinding.spnStatus.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mStatus)
                    mBinding.spnStatus.setSelection(index)
                }
                mUnits.addAll(response.measurementUnits)
                if (mUnits.isNullOrEmpty())
                    mBinding.spnUnit.adapter = null
                else
                    mBinding.spnUnit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mUnits)
                filterCountries()
                bindDefaultData()
            }

            override fun onFailure(message: String) {
                mBinding.spnServiceType.adapter = null
                mBinding.spnServiceSubType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun bindDefaultData() {
        mBinding.tilServiceNo.visibility = GONE
        mBinding.llComments.visibility = GONE
//        mBinding.edtArea.setText("1")
        mBinding.edtServiceDate.setDisplayDateFormat(Constant.DateFormat.DFddMMyyyyHHmmss.value)
        val timeInMillis = Calendar.getInstance().timeInMillis
        mBinding.edtServiceDate.setMinDate(timeInMillis)
        mBinding.edtServiceRequestDate.setText(formatDisplayDateTimeInMillisecond(Date()))
        bindData()
    }

    private fun filterSubType(serviceTypeID: Int) {
        val subTypes: MutableList<CRMServiceSubType> = ArrayList()
        var index = -1
        var serviceSubTypeID = 0
        mServiceRequest?.serviceTypeID?.let {
            serviceSubTypeID = it
        }
        subTypes.add(CRMServiceSubType(getString(R.string.select), -1, -1, 0))
        for (subType in mServiceSubTypes) {
            if (serviceTypeID == subType.serviceTypeID) {
                subTypes.add(subType)
            }
           /* if (index <= -1 && serviceSubTypeID != 0 && subType.serviceSubTypeID != null && serviceSubTypeID == subType.serviceSubTypeID)*/
            if (index < 0 && subType.serviceTypeID == serviceTypeID) {
                index = subTypes.indexOf(subType)
            }
        }
        if (index < 0) index = 0
        if (subTypes.size > 0) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, subTypes)
            mBinding.spnServiceSubType.adapter = adapter
            mBinding.spnServiceSubType.setSelection(index)
        } else
            mBinding.spnServiceSubType.adapter = null
    }

    private fun bindData() {
        mServiceRequest?.let { it ->
            mBinding.edtServiceNo.setText(it.serviceRequestNo)
            mBinding.tilServiceNo.visibility = VISIBLE
            it.serviceRequestDate?.let {
                mBinding.edtServiceRequestDate.setText(formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyHHmmss))
            }
            it.serviceDate?.let {
                mBinding.edtServiceDate.setText(formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyHHmmss))
            }
            it.serviceTypeID?.let {
                for ((index, serviceType) in mServiceTypes.withIndex()) {
                    if (it == serviceType.serviceTypeID) {
                        mBinding.spnServiceType.setSelection(index)
                        break
                    }
                }
            }
            it.statusCode?.let {
                for ((index, status) in mStatus.withIndex()) {
                    if (it == status.statusCode) {
                        mBinding.spnStatus.setSelection(index)
                        break
                    }
                }
            }
//            it.countryCode?.let {
//                for ((index, country) in mCountries.withIndex()) {
//                    if (it == country.countryCode) {
//                        mBinding.spnCountry.setSelection(index)
//                        break
//                    }
//                }
//            }
//            it.stateID?.let {
//                for ((index, state) in mStates.withIndex()) {
//                    if (it == state.stateID) {
//                        mBinding.spnState.setSelection(index)
//                        break
//                    }
//                }
//            }
//            it.cityID?.let {
//                for ((index, city) in mCities.withIndex()) {
//                    if (it == city.stateID) {
//                        mBinding.spnCity.setSelection(index)
//                        break
//                    }
//                }
//            }
//            it.zoneID?.let {
//                for ((index, zone) in mZones.withIndex()) {
//                    if (it == zone.zoneID) {
//                        mBinding.spnZone.setSelection(index)
//                        break
//                    }
//                }
//            }
//            it.sectorID?.let {
//                for ((index, sector) in mSectors.withIndex()) {
//                    if (it == sector.sectorId) {
//                        mBinding.spnZone.setSelection(index)
//                        break
//                    }
//                }
//            }
            it.advanceAmount?.let {
                mBinding.edtAdvanceAmount.setText(formatWithPrecision(it))
            }
            it.estimatedAmount?.let {
                mBinding.edtEstimatedAmount.setText(formatWithPrecision(it))
            }
            it.customer?.let {
                mBinding.edtCustomerName.setText(it)
            }
            it.accountID?.let {
                if (mCustomer == null)
                    mCustomer = BusinessOwnership()
                mCustomer?.accountID = it.toInt()
            }
            it.street?.let {
                mBinding.edtStreet.setText(it)
            }
            it.zip?.let {
                mBinding.edtZipCode.setText(it)
            }
            it.plot?.let {
                mBinding.edtPlot.setText(it)
            }
            it.block?.let {
                mBinding.edtBlock.setText(it)
            }
            it.doorNo?.let {
                mBinding.edtDoorNo.setText(it)
            }
            it.area?.let {
                if (!TextUtils.isEmpty(it))
                    mBinding.edtArea.setText(it)
            }
            mBinding.llComments.visibility = VISIBLE
            it.description?.let {
                mBinding.etDescription.setText(it)
            }
            mBinding.checkbox.isChecked = mServiceRequest!!.is3rdParty == "Y"
            disableFields()
        }
        bindLocation()
    }

    private fun disableFields() {

        mBinding.etDescription.isEnabled = false
        mBinding.edtCustomerName.isEnabled = false
        mBinding.tvCreateCustomer.isEnabled = false
        mBinding.edtArea.isEnabled = false
        mBinding.spnServiceType.isClickable = false
        mBinding.spnServiceSubType.isClickable = false

        val action =  mServiceRequest?.statusCode=="CRM_ServiceRequests.New"
        mBinding.edtServiceDate.isEnabled = action
        mBinding.spnCountry.isClickable = action
        mBinding.spnState.isClickable = action
        mBinding.spnCity.isClickable = action
        mBinding.spnSector.isClickable = action
        mBinding.spnZone.isClickable = action
        mBinding.edtStreet.isEnabled = action
        mBinding.edtZipCode.isEnabled = action
        mBinding.edtPlot.isEnabled = action
        mBinding.edtBlock.isEnabled = action
        mBinding.edtDoorNo.isEnabled = action
        mBinding.checkbox.isEnabled = action
    }

    private fun save(saveServiceTaxRequest: SaveServiceTaxRequest, isFromReceipt: Boolean) {
        mListener?.showProgressDialog()
        APICall.saveServiceTaxRequest(saveServiceTaxRequest, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                if (response != 0) {
                    mServiceRequest?.serviceRequestNo = "$response"
                    mBinding.edtServiceNo.setText("$response")
                    mBinding.tilServiceNo.visibility = VISIBLE
                }
                mDocumentReference = COMDocumentReference()
                mBinding.btnClearImage.visibility = GONE
                mBinding.imgDocument.setImageBitmap(null)
                mBinding.etComments.setText("")
                disableFields()
                /**
                 * isFromReceipt boolean is to check whether the flow is from
                 * receipt(proceed button) or from save button
                 */
                if (!isFromReceipt)
                    callReceiptPage(mServiceRequest?.serviceRequestNo)
                else {
                    mListener?.showToast(getString(R.string.service_req_saved_successfully))
                    activity?.finish()
                }

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun callReceiptPage(mServiceRequest: String?) {
        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, mServiceRequest?.toInt())
        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.SERVICE_REQUEST_BOOKING_DETAIL.Code)
        startActivityForResult(intent, REQUEST_CODE_SERVICE_BOOKING_DETAIL)
        //activity?.finish()
    }

    private fun isValid(): Boolean {
        if (mBinding.spnServiceType.selectedItem == null || -1 == (mBinding.spnServiceType.selectedItem as CRMServiceType).serviceTypeID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.service_type))
            return false
        }
        if (mBinding.spnServiceSubType.selectedItem == null || -1 == (mBinding.spnServiceSubType.selectedItem as CRMServiceSubType).serviceSubTypeID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.service_sub_type))
            return false
        }
        if (mBinding.spnCountry.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.country)}")
            return false
        }
        if (mBinding.spnState.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.state)}")
            return false
        }
        if (mBinding.spnCity.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.city)}")
            return false
        }
        if (mBinding.spnZone.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.zone)}")
            return false
        }
        if (mBinding.spnSector.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.sector)}")
            return false
        }
        if ( mBinding.edtCustomerName.isEnabled &&  mBinding.edtCustomerName.text.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.citizen_name))
            return false
        }

        if (mServiceRequest?.serviceRequestNo != null && !TextUtils.isEmpty(mServiceRequest?.serviceRequestNo) && mBinding.etComments.text.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.label_comments))
            return false
        }
        if (mBinding.etDescription.isEnabled && mBinding.etDescription.text.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.label_description))
            return false
        }
        if ((mBinding.edtArea.text == null || mBinding.edtArea.text?.toString() == null || mBinding.edtArea.text.toString().isEmpty() || mBinding.edtArea.text.toString() == ".") && mBinding.llUnit.isVisible) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.taxable_matter))
            return false
        }
        if (!mBinding.edtAdvanceAmount.text.isNullOrEmpty()) {
            val advAmount =
                currencyToDouble(mBinding.edtAdvanceAmount.text.toString())?.toDouble() ?: 0.0
            val estAmount =
                currencyToDouble(mBinding.edtEstimatedAmount.text.toString())?.toDouble() ?: 0.0
            if (advAmount > 0.0 && estAmount > 0.0 && advAmount > estAmount) {
                mListener?.showSnackbarMsg(getString(R.string.advance_amount_greater_than_estimated_amount))
                return false
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        helper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = ArrayList()
        var index = -1
        var countryCode: String? = "BFA"
        mServiceRequest?.countryCode?.let {
            countryCode = it
        }
        for (country in mCountries) {
            countries.add(country)
            if (index <= -1 && countryCode != null && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode) index = countries.indexOf(country)
        }
        if (index <= -1) index = 0
        if (countries.size > 0) {
            val countryMasterArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, countries)
            mBinding.spnCountry.adapter = countryMasterArrayAdapter
            mBinding.spnCountry.setSelection(index)
            filterStates(countries[index].countryCode)
        } else {
            mBinding.spnCountry.adapter = null
            filterStates(countryCode)
        }
    }

    private fun filterStates(countryCode: String?) {
        var states: MutableList<COMStateMaster> = ArrayList()
        var index = -1
        var stateID = 100497
        mServiceRequest?.stateID?.let {
            stateID = it
        }
        if (TextUtils.isEmpty(countryCode)) states = ArrayList() else {
            for (state in mStates) {
                if (countryCode == state.countryCode) states.add(state)
                if (index <= -1 && stateID != 0 && state.stateID != null && stateID == state.stateID) index = states.indexOf(state)
            }
        }
        if (index <= -1) index = 0
        if (states.size > 0) {
            val stateArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, states)
            mBinding.spnState.adapter = stateArrayAdapter
            mBinding.spnState.setSelection(index)
            filterCities(states[index].stateID!!)
        } else {
            mBinding.spnState.adapter = null
            filterCities(stateID)
        }
    }

    private fun filterCities(stateID: Int) {
        var cities: MutableList<VUCOMCityMaster> = ArrayList()
        var index = -1
        var cityID = 100312093
        mServiceRequest?.cityID?.let {
            cityID = it
        }
        if (stateID <= 0) cities = ArrayList() else {
            for (city in mCities) {
                if (city.stateID != null && stateID == city.stateID) cities.add(city)
                if (index <= 0 && cityID != 0 && city.cityID != null && cityID == city.cityID) index = cities.indexOf(city)
            }
        }
        if (index <= -1) index = 0
        if (cities.size > 0) {
            val cityArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)
            mBinding.spnCity.adapter = cityArrayAdapter
            mBinding.spnCity.setSelection(index)
            filterZones(cities[index].cityID!!)
        } else {
            mBinding.spnCity.adapter = null
            filterZones(cityID)
        }
    }

    private fun filterZones(cityID: Int) {
        var zones: MutableList<COMZoneMaster> = ArrayList()
        var index = 0
        var zoneID: Int? = 0
        mServiceRequest?.zoneID?.let {
            zoneID = it
        }
        if (cityID <= 0) zones = ArrayList() else {
            for (zone in mZones) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && zoneID != 0 && zone.zoneID != null && zoneID == zone.zoneID) index = zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnZone.adapter = zoneArrayAdapter
            mBinding.spnZone.setSelection(index)
            filterSectors(zones[index].zoneID!!)
            setSelectedZone()
        } else {
            mBinding.spnZone.adapter = null
            filterSectors(index)
        }
    }

    private fun filterSectors(zoneID: Int) {
        var sectors: MutableList<COMSectors?> = ArrayList()
        var index = 0
        var sectorID = 0
        mServiceRequest?.sectorID?.let {
            sectorID = it
        }
        if (zoneID <= 0) sectors = ArrayList() else {
            for (sector in mSectors) {
                if (sector.zoneId != null && zoneID == sector.zoneId)
                    sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId)
                    index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            mBinding.spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnSector.adapter = sectorArrayAdapter
            if (selectedVUCRMServiceTaxRequest?.sectorID != null){
                for ((index, sector) in sectors.withIndex()) {
                    if (selectedVUCRMServiceTaxRequest?.sectorID == sector?.sectorId) {
                        mBinding.spnSector.setSelection(index)
                        break
                    }
                }
                selectedVUCRMServiceTaxRequest = null
            }else{
                mBinding.spnSector.setSelection(index)
            }
        } else {
            mBinding.spnSector.adapter = null
            mBinding.spnSector.isEnabled = false
        }
    }

    private fun showImagePickerOptions() {
        mListener?.showAlertDialog(R.string.select_a_file_to_upload,
                R.string.label_take_camera_pictire,
                View.OnClickListener {
                    if (hasPermission(requireActivity(), Manifest.permission.CAMERA))
                        navigateToCamera()
                    else
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CODE_CAMERA)
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                R.string.cancel,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                }
        )
    }

    private fun navigateToCamera() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File?
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                LogHelper.writeLog(exception = e)
                return
            }
            val photoUri: Uri = FileProvider.getUriForFile(requireActivity(), requireActivity()?.packageName.toString() + ".provider", photoFile)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(pictureIntent, Constant.REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mImageFilePath = image.absolutePath
        return image
    }


    private fun fetchEstimatedAmount() {
        val getEstimatedAmount4ServiceTax = GetEstimatedAmount4ServiceTax()
        if (mBinding.spnServiceSubType.selectedItem != null) {
            val serviceSubType = mBinding.spnServiceSubType.selectedItem as CRMServiceSubType?
            serviceSubType?.serviceSubTypeID?.let {
                if (it != -1)
                    getEstimatedAmount4ServiceTax.subTypeID = it
            }
        }
        if (mBinding.edtArea.text != null && !TextUtils.isEmpty(mBinding.edtArea.text.toString()))
            getEstimatedAmount4ServiceTax.area = mBinding.edtArea.text.toString().trim().toDouble()
        mListener?.showProgressDialog()
        APICall.getEstimatedAmount4ServiceTax(getEstimatedAmount4ServiceTax, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                updateEstimatedAmount(response)
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                updateEstimatedAmount()
            }
        })
    }
    fun bindAddress(serviceData:List<VUCRMServiceTaxRequest>){

        serviceData[0]?.let { it ->
            selectedVUCRMServiceTaxRequest = it
            it.street?.let {
                mBinding.edtStreet.setText(it)
            }
            it.zip?.let {
                mBinding.edtZipCode.setText(it)
            }
            it.plot?.let {
                mBinding.edtPlot.setText(it)
            }
            it.block?.let {
                mBinding.edtBlock.setText(it)
            }
            it.doorNo?.let {
                mBinding.edtDoorNo.setText(it)
            }
            it.area?.let {
                if (!TextUtils.isEmpty(it))
                    mBinding.edtArea.setText(BigDecimal(it).toDouble().toString())
            }
            it.issueDescription?.let {
                if (!TextUtils.isEmpty(it))
                    mBinding.etDescription.setText(it)
            }
            it.tentativeRequestedDate?.let {
                mBinding.edtServiceDate.setText(formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyHHmmss))
            }
            setSelectedZone()
            fetchEstimatedAmount()
        }
    }

    private fun setSelectedZone() {
        selectedVUCRMServiceTaxRequest?.znid?.let {
            for ((index, zone) in mZones.withIndex()) {
                if (it == zone.zoneID) {
                    mBinding.spnZone.setSelection(index)
                    break
                }
            }
        }
    }

    private fun fetchComments() {
        mServiceRequest?.serviceRequestNo?.let {
            mListener?.showProgressDialog()
            APICall.getCommentsAndDocuments(it, true, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    bindComments(response.serviceRequestTable)
                    bindAddress(response.vUCRMServiceTaxRequests)
                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }

    fun updateText(latitude: Double, longitude: Double) {
        mBinding.edtLatitude.setText(latitude.toString())
        mBinding.edtLongitude.setText(longitude.toString())
    }

    fun bindComments(serviceRequestTables: List<ServiceRequestTable>) {
        if (serviceRequestTables.isNotEmpty()) {
            mBinding.llCommentsContainer.removeAllViews()
            var view: View
            val documents: ArrayList<COMDocumentReference> = arrayListOf()
            for (item in serviceRequestTables) {
                view = layoutInflater.inflate(R.layout.view_comments, mBinding.llCommentsContainer, false)
                val commentedByText = view.findViewById<View>(R.id.commented_by_text) as TextView
                val commentsText = view.findViewById<View>(R.id.comments_text) as TextView
                val commentedDateTex = view.findViewById<View>(R.id.commented_date_tex) as TextView
                commentedByText.text = item.modifiedByName
                commentsText.text = item.comments
                commentedDateTex.text = formatDisplayDateTimeInMillisecond(item.commentDate)
                mBinding.llCommentsContainer.addView(view)
                val comDocumentReference = COMDocumentReference()
                comDocumentReference.documentNo = item.documentID
                comDocumentReference.awsfile = item.aWSPath
                documents.add(comDocumentReference)
            }
            mBinding.rcvDocumentPreviews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            mAdapter = DocumentPreviewAdapter(documents, this)
            mBinding.rcvDocumentPreviews.adapter = mAdapter
        } else {
            mBinding.tvComments.visibility = GONE
        }
    }

    private fun updateEstimatedAmount(amount: Double? = 0.0) {
        isEstimationCalculated = true
        mBinding.edtEstimatedAmount.setText(formatWithPrecision(amount))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        helper?.onActivityResult(requestCode, resultCode)
        data?.let {
            if (it.getBooleanExtra(Constant.KEY_STOP_TITLE_RESET,true)) {
                mListener?.showToolbarBackButton(R.string.title_service_tax)
            }
        } ?: mListener?.showToolbarBackButton(R.string.title_service_tax)
        mBinding.btnCamera.show()
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.REQUEST_IMAGE_CAPTURE -> {
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val photo = BitmapFactory.decodeFile(mImageFilePath, options)
                    mBinding.imgDocument.setImageBitmap(photo)
                    mBinding.imgDocument.visibility = VISIBLE
                    mBinding.btnClearImage.visibility = VISIBLE
                    mDocumentReference = COMDocumentReference()
                    mDocumentReference.documentName = System.currentTimeMillis().toString()
                    mDocumentReference.extension = "jpeg"
                    mDocumentReference.data = ImageHelper.getBase64String(photo,80)
                    mDocumentReference.documentProofType = ""

                }
                Constant.REQUEST_CODE_BUSINESS_OWNER -> {
                    data?.let {
                        if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                            mCustomer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                            showCustomerInfo()
                        }
                    }
                }
                Constant.REQUEST_CODE_BUSINESS_OR_CITIZEN_SEARCH -> {
                    data?.let {
                        if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                            mCustomer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership?
                            mBusiness = Business()
                            showCustomerInfo()
                        } else if (it.hasExtra(Constant.KEY_BUSINESS)) {
                            mBusiness = data.getParcelableExtra(Constant.KEY_BUSINESS) as Business?
                            mCustomer = BusinessOwnership()
                            showCustomerInfo()
                        }
                    }
                }
                REQUEST_CODE_SERVICE_BOOKING_DETAIL -> {
                    data?.let {
                        /***
                         * changing the button name to proceed as to call the payment api or save api
                         * depending on the advAmount type
                         */
                        mBinding.btnCamera.hide()
                        mBinding.btnSave.text = getString(R.string.proceed)
                        fetchComments()
                    }
                }
                Constant.REQUEST_CODE_PAYMENT_SUCCESS -> {
                    data?.let {
                        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                        if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                            intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0))
                        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.SERVICE_BOOKING_ADVANCE.Code)
                        startActivity(intent)
                        activity?.finish()
                    }
                }

            }
        }
        else {
            mBinding.llComments.visibility = VISIBLE
            fetchComments()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        helper?.onRequestPermissionsResult(requestCode, grantResults)
        if (requestCode == Constant.REQUEST_CODE_CAMERA && isPermissionGranted(grantResults))
            navigateToCamera()
        else
            mListener?.showAlertDialog(getString(R.string.msg_permission_storage_camera))
    }

    private fun showCustomerInfo() {
        mCustomer?.let {
            if (!TextUtils.isEmpty(it.accountName))
                mBinding.edtCustomerName.setText(it.accountName)
        }
        mBusiness?.let {
            if (!TextUtils.isEmpty(it.businessName))
                mBinding.edtCustomerName.setText(it.businessName)
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.itemImageDocumentPreview -> {
                    val documentReferences = mAdapter?.get()
                    documentReferences?.let {
                        val comDocumentReference = obj as COMDocumentReference
                        val intent = Intent(context, DocumentPreviewActivity::class.java)
                        documentReferences.remove(comDocumentReference)
                        documentReferences.add(0, comDocumentReference)
                        intent.putExtra(Constant.KEY_DOCUMENT, documentReferences)
                        startActivity(intent)
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
        mListener?.popBackStack()
    }

    interface Listener {
        fun hideKeyBoard()
        fun popBackStack()
        fun dismissDialog()
        fun showToast(message: String)
        fun showProgressDialog(message: Int)
        fun showProgressDialog()
        fun showSnackbarMsg(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showToolbarBackButton(title: Int)
        fun showSnackbarMsg(message: Int)
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener)
    }

}