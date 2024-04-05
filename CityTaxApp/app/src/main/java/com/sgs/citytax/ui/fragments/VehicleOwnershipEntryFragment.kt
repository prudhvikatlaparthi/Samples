package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentVehicleOwnershipEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.*
import java.math.BigInteger
import java.util.*

class VehicleOwnershipEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentVehicleOwnershipEntryBinding
    private var mListener: Listener? = null
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mTaskCode: String? = ""
    private var mSycoTaxId: String? = ""
    private var mVuAdmVehicleOwnership: VUADMVehicleOwnership? = null
    private var mVehicleTypes: MutableList<ADMVehicleTypes>? = null
    private var mStatusCodes: MutableList<COMStatusCode>? = null
    private var mFuelTypes: MutableList<ComComboStaticValues>? = null
    private var mTransmissionTypes: MutableList<ComComboStaticValues>? = null
    private var mScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP

    private var mPrimaryKey: String? = null

    private var mVehicleSycotaxList: ArrayList<UnusedVehicleSycotaxID> = arrayListOf()

    private var vehicleOwnershipId: String? = null
    private var mTaxRuleBookCode: String? = ""

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mVuAdmVehicleOwnership = arguments?.getParcelable(Constant.KEY_VEHICLE_OWNERSHIP) as VUADMVehicleOwnership?
            mTaskCode = it.getString(Constant.KEY_TASK_CODE)
            mSycoTaxId = it.getString(Constant.KEY_SYCO_TAX_ID)
            mTaxRuleBookCode = arguments?.getString(Constant.KEY_TAX_RULE_BOOK_CODE)!!
        }
        //endregion
        setViews()
        bindSpinner()
        setListeners()
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicle_ownership_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {
        mBinding.edtStartDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtStartDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtEndDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtRegistrationDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtRegistrationDate.setDisplayDateFormat(displayDateFormat)

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
        mBinding.edtStartDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtEndDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtRegistrationDate.setDisplayDateFormat(displayDateFormat)

        if (!TextUtils.isEmpty(mSycoTaxId)) {
            mBinding.edtSycoTaxID.setText(mSycoTaxId)
            mBinding.edtSycoTaxID.isEnabled = false
        }
        if (mCode == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS ||
            mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS ||
            mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD) {
            mBinding.llVehicleOwnership.visibility = View.GONE
            mBinding.llVehicleOwnershipView.visibility = View.GONE
        } else {
            mBinding.llVehicleOwnership.visibility = View.VISIBLE
            mBinding.llVehicleOwnershipView.visibility = View.VISIBLE
        }

    }

    private fun setEditAction(action: Boolean) {

        mBinding.edtSycoTaxID.isEnabled = action
        mBinding.edtVehicleNo.isEnabled = action
        mBinding.edtRegistrationNo.isEnabled = action
        mBinding.spnTransmissionType.isEnabled = action
        mBinding.edtCO2Emission.isEnabled = action
        mBinding.edtPower.isEnabled = action
        mBinding.edtSeatNumber.isEnabled = action
        mBinding.edtColor.isEnabled = action
        mBinding.edtStartDate.isEnabled = action
        mBinding.edtEndDate.isEnabled = action
        mBinding.edtChassisNo.isEnabled = action
        mBinding.edtRegistrationDate.isEnabled = action
        mBinding.edtHorsePower.isEnabled = action
        mBinding.edtValue.isEnabled = action
        mBinding.edtLoadCapacity.isEnabled = action
        mBinding.edtManufacturer.isEnabled = action
        mBinding.edtMake.isEnabled = action
        mBinding.edtModel.isEnabled = action
        mBinding.edtVariant.isEnabled = action
        mBinding.edtManufacturingYear.isEnabled = action
        mBinding.edtEngineNo.isEnabled = action
        mBinding.edtCubicCapacity.isEnabled = action
        mBinding.edtRemarks.isEnabled = action
        mBinding.spnVehicleType.isEnabled = action
        mBinding.spnStatus.isEnabled = action
        mBinding.spnFuelType.isEnabled = action
        mBinding.btnGet.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }


    private fun setEditActionForSave(action: Boolean) {

        mBinding.edtSycoTaxID.isEnabled = action
        mBinding.edtVehicleNo.isEnabled = action
        mBinding.edtRegistrationNo.isEnabled = action
        mBinding.spnTransmissionType.isEnabled = action
        mBinding.edtCO2Emission.isEnabled = action
        mBinding.edtPower.isEnabled = action
        mBinding.edtSeatNumber.isEnabled = action
        mBinding.edtColor.isEnabled = action
        mBinding.edtStartDate.isEnabled = action
        mBinding.edtEndDate.isEnabled = action
        mBinding.edtChassisNo.isEnabled = action
        mBinding.edtRegistrationDate.isEnabled = action
        mBinding.edtHorsePower.isEnabled = action
        mBinding.edtValue.isEnabled = action
        mBinding.edtLoadCapacity.isEnabled = action
        mBinding.edtManufacturer.isEnabled = action
        mBinding.edtMake.isEnabled = action
        mBinding.edtModel.isEnabled = action
        mBinding.edtVariant.isEnabled = action
        mBinding.edtManufacturingYear.isEnabled = action
        mBinding.edtEngineNo.isEnabled = action
        mBinding.edtCubicCapacity.isEnabled = action
        mBinding.edtRemarks.isEnabled = action
        mBinding.spnVehicleType.isEnabled = action
        mBinding.spnFuelType.isEnabled = action
        mBinding.btnGet.isEnabled = action

        mBinding.spnStatus.isEnabled = true
        mBinding.btnSave.visibility = View.VISIBLE

    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("ADM_Vehicles", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mVehicleTypes = response.vehicleTypes
                if (mVehicleTypes != null && mVehicleTypes!!.isNotEmpty()) {
                    mVehicleTypes?.add(0, ADMVehicleTypes(getString(R.string.select), "-1", ""))
                    mBinding.spnVehicleType.adapter = ArrayAdapter<ADMVehicleTypes>(activity!!, android.R.layout.simple_spinner_dropdown_item, mVehicleTypes!!)
                } else
                    mBinding.spnVehicleType.adapter = null

                mStatusCodes = response.statusCodes
                if (mStatusCodes != null && mStatusCodes!!.isNotEmpty()) {
                    mStatusCodes?.add(0, COMStatusCode(getString(R.string.select), "-1"))
                    mBinding.spnStatus.adapter = ArrayAdapter<COMStatusCode>(activity!!, android.R.layout.simple_spinner_dropdown_item, mStatusCodes!!)
                } else
                    mBinding.spnStatus.adapter = null

                mFuelTypes = response.comboStaticValues
                if (mFuelTypes != null && mFuelTypes!!.isNotEmpty()) {
                    mFuelTypes?.add(0, ComComboStaticValues("-1", getString(R.string.select)))
                    mBinding.spnFuelType.adapter = ArrayAdapter<ComComboStaticValues>(activity!!, android.R.layout.simple_spinner_dropdown_item, mFuelTypes!!)
                } else
                    mBinding.spnFuelType.adapter = null

                if (mFuelTypes != null && mFuelTypes!!.isNotEmpty()) {
                    mTransmissionTypes = arrayListOf()
                    for ((index, value) in mFuelTypes!!.withIndex()) {
                        if (value.comboCode == "VehicleTransmission") {
                            mTransmissionTypes!!.add(value)
                        }
                    }
                }
                if (mTransmissionTypes != null && mTransmissionTypes!!.isNotEmpty()) {
                    mTransmissionTypes?.add(0, ComComboStaticValues("-1", getString(R.string.select)))
                    mBinding.spnTransmissionType.adapter = ArrayAdapter<ComComboStaticValues>(activity!!, android.R.layout.simple_spinner_dropdown_item, mTransmissionTypes!!)
                } else
                    mBinding.spnTransmissionType.adapter = null




                bindData()
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnVehicleType.adapter = null
                mBinding.spnStatus.adapter = null
                mBinding.spnFuelType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindData() {
        val timeInMillis = Calendar.getInstance().timeInMillis
        mBinding.edtStartDate.setMaxDate(timeInMillis)
        mBinding.edtEndDate.setMinDate(timeInMillis)
        if (mVuAdmVehicleOwnership != null) {
            mBinding.edtSycoTaxID.isEnabled = false
            mBinding.edtSycoTaxID.setText(mVuAdmVehicleOwnership!!.vehicleSycotaxID)
            mBinding.edtVehicleNo.isEnabled = false
            mBinding.edtRegistrationNo.isEnabled = false
            mBinding.edtVehicleNo.setText(mVuAdmVehicleOwnership!!.vehicleNo)
            mBinding.edtRegistrationNo.setText(mVuAdmVehicleOwnership!!.vehicleNo)
            mBinding.edtCO2Emission.setText(mVuAdmVehicleOwnership!!.co2Emission)
            mBinding.edtPower.setText(mVuAdmVehicleOwnership!!.power)
            if(mVuAdmVehicleOwnership!!.seatNumber.toString()=="null"){
                mBinding.edtSeatNumber.setText("")
            } else{
                mBinding.edtSeatNumber.setText("${mVuAdmVehicleOwnership!!.seatNumber}")
            }
            mBinding.edtColor.setText(mVuAdmVehicleOwnership!!.color)
            mBinding.edtStartDate.setText(displayFormatDate(mVuAdmVehicleOwnership!!.fromDate))
            mBinding.edtEndDate.setText(displayFormatDate(mVuAdmVehicleOwnership!!.toDate))
            mBinding.edtChassisNo.setText(mVuAdmVehicleOwnership!!.chassisNo)
            mBinding.edtRegistrationDate.setText(displayFormatDate(mVuAdmVehicleOwnership!!.registrationDate))
            mBinding.edtHorsePower.setText(mVuAdmVehicleOwnership!!.horsepower)
            mBinding.edtValue.setText("${mVuAdmVehicleOwnership!!.value}")
            if(mVuAdmVehicleOwnership!!.loadCapacity.toString()=="null"){
                mBinding.edtLoadCapacity.setText("")
            } else{
                mBinding.edtLoadCapacity.setText("${mVuAdmVehicleOwnership!!.loadCapacity}")
            }


            vehicleOwnershipId = mVuAdmVehicleOwnership!!.vehicleOwnershipID.toString()

            mVuAdmVehicleOwnership?.mfg?.let {
                mBinding.edtManufacturer.setText("${mVuAdmVehicleOwnership!!.mfg}")
            }
            mVuAdmVehicleOwnership?.make?.let {
                mBinding.edtMake.setText("${mVuAdmVehicleOwnership!!.make}")
            }
            mVuAdmVehicleOwnership?.model?.let {
                mBinding.edtModel.setText("${mVuAdmVehicleOwnership!!.model}")
            }
            mVuAdmVehicleOwnership?.variant?.let {
                mBinding.edtVariant.setText("${mVuAdmVehicleOwnership!!.variant}")
            }
            mVuAdmVehicleOwnership?.manufacturingYear?.let {
                mBinding.edtManufacturingYear.setText("${mVuAdmVehicleOwnership!!.manufacturingYear}")
            }
            mVuAdmVehicleOwnership?.engineNo?.let {
                mBinding.edtEngineNo.setText("${mVuAdmVehicleOwnership!!.engineNo}")
            }
            mVuAdmVehicleOwnership?.cubicCapacity?.let {
                mBinding.edtCubicCapacity.setText("${mVuAdmVehicleOwnership!!.cubicCapacity}")
            }
            mBinding.edtRemarks.setText(mVuAdmVehicleOwnership!!.remarks)
            mVuAdmVehicleOwnership?.estimatedTax?.let {
                mBinding.edtEstimatedAmountForProduct.setText(formatWithPrecision(it))
            }
            mBinding.edtStartDate.setText(displayFormatDate(mVuAdmVehicleOwnership!!.fromDate))
            mBinding.edtEndDate.setText(displayFormatDate(mVuAdmVehicleOwnership!!.toDate))
            mBinding.edtRegistrationDate.setText(displayFormatDate(mVuAdmVehicleOwnership!!.registrationDate))
            if (mVehicleTypes != null)
                for ((index, obj) in mVehicleTypes!!.withIndex()) {
                    if (mVuAdmVehicleOwnership!!.vehicleTypeCode == obj.vehicleTypeCode) {
                        mBinding.spnVehicleType.setSelection(index)
                        break
                    }
                }
            if (mStatusCodes != null)
                for ((index, obj) in mStatusCodes!!.withIndex()) {
                    if (mVuAdmVehicleOwnership!!.statusCode == obj.statusCode) {
                        mBinding.spnStatus.setSelection(index)
                        break
                    }
                }

            if (mFuelTypes != null)
                for ((index, obj) in mFuelTypes!!.withIndex()) {
                    if (mVuAdmVehicleOwnership!!.fuelType == obj.comboValue) {
                        mBinding.spnFuelType.setSelection(index)
                        break
                    }
                }

            if (mTransmissionTypes != null)
                for ((index, obj) in mTransmissionTypes!!.withIndex()) {
                    if (mVuAdmVehicleOwnership!!.transmission == obj.comboValue) {
                        mBinding.spnTransmissionType.setSelection(index)
                        break
                    }
                }

            fetchChildEntriesCount()
            if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
            {
                getInvoiceCount4Tax()

            }
            if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
            {
                setEditAction(false)
            }

        } else {
            /**
             * if new vehicle ownership is added edtEndDate should be disabled
             */
            mBinding.edtEndDate.isEnabled = false
        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.btnGet.setOnClickListener(this)
        mBinding.edtStartDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.edtStartDate.text?.toString()?.let {
                    if (it.isNotEmpty())
                    //  mBinding.edtEndDate.setMinDate(parseDate(it, DateTimeTimeSecondFormat).time)
                        mBinding.edtEndDate.setMinDate(parseDate(it, displayDateFormat).time)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        mBinding.edtSycoTaxID.setOnClickListener {
            getUnusedParkingSycoTaxId()
        }
        mBinding.llVehicleOwnership.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
    }

    private fun getUnusedParkingSycoTaxId() {
        mListener?.showProgressDialog()
        APICall.getRandomVehicleSycotaxIDList(object : ConnectionCallBack<VehicleSycotaxListResponse> {
            override fun onSuccess(response: VehicleSycotaxListResponse) {
                mListener?.dismissDialog()
                if(response.vehicleSycotaxList != null){
                    mVehicleSycotaxList.addAll(response.vehicleSycotaxList!!)
                }
//                displayScannerListSelection()
                displaySycoTaxList(mVehicleSycotaxList)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener!!.showAlertDialog(message)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llVehicleOwnership -> {
                if (!TextUtils.isEmpty(mPrimaryKey)) {
                    val fragment = VehicleOnBoardingMasterFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                    bundle.putString(Constant.KEY_PRIMARY_KEY, mPrimaryKey)
                    bundle.putString(Constant.KEY_SYCO_TAX_ID, mSycoTaxId)
                    fragment.arguments = bundle
                    //endregion

                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_VEHICLE_OWNERSHIP_LIST)

                    mListener?.showToolbarBackButton(R.string.title_vehicle_ownership_details)
                    mListener?.addFragment(fragment, true)

                } else if (validateView()) {
                    prepareData(v)
                }
            }
            R.id.llDocuments -> {
                if (!TextUtils.isEmpty(mPrimaryKey)) {
                    val fragment = DocumentsMasterFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, mScreen)
                    bundle.putString(Constant.KEY_PRIMARY_KEY, mPrimaryKey)
                    fragment.arguments = bundle
                    //endregion

                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)

                    mListener?.showToolbarBackButton(R.string.documents)
                    mListener?.addFragment(fragment, true)

                } else if (validateView()) {
                    prepareData(v)
                }

            }
            R.id.llNotes -> {
                if (!TextUtils.isEmpty(mPrimaryKey)) {
                    val fragment = NotesMasterFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, mScreen)
                    bundle.putString(Constant.KEY_PRIMARY_KEY, mPrimaryKey)
                    fragment.arguments = bundle
                    //endregion
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)

                    mListener?.showToolbarBackButton(R.string.notes)
                    mListener?.addFragment(fragment, true)
                } else if (validateView()) {
                    prepareData(v)
                }
            }
            R.id.btnSave -> {
                if (validateView()) {
                    mListener?.showAlertDialog(R.string.are_you_sure_you_have_entered_all_valid_information,
                            R.string.yes,
                            View.OnClickListener {
                                val dialog = (it as Button).tag as AlertDialog
                                dialog.dismiss()
                                prepareData(v)
                            },
                            R.string.no,
                            View.OnClickListener
                            {
                                val dialog = (it as Button).tag as AlertDialog
                                dialog.dismiss()
                            })
                }
            }
            R.id.btnGet -> {
                fetchTaxableMatter()
            }
        }
    }

    private fun fetchTaxableMatter() {
        val getTaxableMatterColumnData = GetTaxableMatterColumnData()
        getTaxableMatterColumnData.taskCode = mTaskCode
        mListener?.showProgressDialog()
        APICall.getTaxableMatterColumnData(getTaxableMatterColumnData, object : ConnectionCallBack<List<DataTaxableMatter>> {
            override fun onSuccess(response: List<DataTaxableMatter>) {

                val list: ArrayList<DataTaxableMatter> = arrayListOf()
                for (it in response) {
                    val taxableMatter = DataTaxableMatter()
                    taxableMatter.taxableMatterColumnName = it.taxableMatterColumnName
                    if ("LoadCapacity" == it.taxableMatterColumnName) {
                        if (!TextUtils.isEmpty(mBinding.edtLoadCapacity.text?.toString()?.trim()))
                            taxableMatter.taxableMatter = mBinding.edtLoadCapacity.text.toString().trim()
                    }
                    if ("NoOfVehicle" == it.taxableMatterColumnName) {
                        taxableMatter.taxableMatter = "1"
                    }
                    if ("SeatsNumber" == it.taxableMatterColumnName) {
                        if (!TextUtils.isEmpty(mBinding.edtSeatNumber.text?.toString()?.trim()))
                            taxableMatter.taxableMatter = mBinding.edtSeatNumber.text.toString().trim()
                    }
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
        getEstimatedTaxForProduct.taskCode = mTaskCode
        getEstimatedTaxForProduct.customerID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                ?: 0
        if (!TextUtils.isEmpty(mBinding.edtStartDate.text?.toString()?.trim()))
            getEstimatedTaxForProduct.startDate = serverFormatDate(mBinding.edtStartDate.text.toString().trim())
        if (mBinding.spnVehicleType.selectedItem != null) {
            val vehicleType = mBinding.spnVehicleType.selectedItem as ADMVehicleTypes?
            vehicleType?.vehicleTypeCode?.let {
                getEstimatedTaxForProduct.entityPricingVoucherNo = it
            }
        }
        mListener?.showProgressDialog()
        APICall.getEstimatedTaxForProduct(getEstimatedTaxForProduct, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mBinding.edtEstimatedAmountForProduct.setText(formatWithPrecision(response))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.edtEstimatedAmountForProduct.setText("")
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun prepareData(v: View) {
        val vehicleOwnership = VehicleOwnership()
        if (mVuAdmVehicleOwnership != null && mVuAdmVehicleOwnership?.vehicleOwnershipID != null)
            vehicleOwnership.vehicleOwnershipID = "${mVuAdmVehicleOwnership?.vehicleOwnershipID}"
        vehicleOwnership.fromDate = serverFormatDateTimeInMilliSecond(formatDate(mBinding.edtStartDate.text.toString().trim(), displayDateFormat, displayDateTimeTimeSecondFormat))
        vehicleOwnership.toDate = serverFormatDateTimeInMilliSecond(formatDate(mBinding.edtEndDate.text.toString().trim(), displayDateFormat, displayDateTimeTimeSecondFormat))
        vehicleOwnership.vehicleNo = mBinding.edtRegistrationNo.text.toString().trim()
        val vehicleMaster = VehicleMaster()
        vehicleMaster.cO2Emissions = mBinding.edtCO2Emission.text.toString().trim()
        vehicleMaster.chassisNo = mBinding.edtChassisNo.text.toString().trim()
        vehicleMaster.color = mBinding.edtColor.text.toString().trim()
        vehicleMaster.horsepower = mBinding.edtHorsePower.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtLoadCapacity.text.toString().trim()))
            vehicleMaster.loadCapacity = mBinding.edtLoadCapacity.text.toString().trim().toInt()

        if (!TextUtils.isEmpty(mBinding.edtManufacturer.text.toString().trim()))
            vehicleMaster.mfg = mBinding.edtManufacturer.text.toString().trim()

        if (!TextUtils.isEmpty(mBinding.edtMake.text.toString().trim()))
            vehicleMaster.make = mBinding.edtMake.text.toString().trim()

        if (!TextUtils.isEmpty(mBinding.edtModel.text.toString().trim()))
            vehicleMaster.model = mBinding.edtModel.text.toString().trim()

        if (!TextUtils.isEmpty(mBinding.edtVariant.text.toString().trim()))
            vehicleMaster.variant = mBinding.edtVariant.text.toString().trim()

        if (!TextUtils.isEmpty(mBinding.edtManufacturingYear.text.toString().trim()))
            vehicleMaster.manufacturingYear = mBinding.edtManufacturingYear.text.toString().toInt()

        if (!TextUtils.isEmpty(mBinding.edtEngineNo.text.toString().trim()))
            vehicleMaster.engineNo = mBinding.edtEngineNo.text.toString().trim()

        if (!TextUtils.isEmpty(mBinding.edtCubicCapacity.text.toString().trim()))
            vehicleMaster.cubicCapacity = currencyToDouble(mBinding.edtCubicCapacity.text.toString())!!.toDouble()

        vehicleMaster.power = mBinding.edtPower.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtRegistrationDate.text.toString().trim()))
            vehicleMaster.registrationDate = serverFormatDateTimeInMilliSecond(formatDate(mBinding.edtRegistrationDate.text.toString().trim(), displayDateFormat, displayDateTimeTimeSecondFormat))

        vehicleMaster.registrationNo = mBinding.edtRegistrationNo.text.toString().trim()
        vehicleMaster.remarks = mBinding.edtRemarks.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtSeatNumber.text.toString().trim()))
            vehicleMaster.seatsNumber = mBinding.edtSeatNumber.text.toString().trim().toInt()
        if (!TextUtils.isEmpty(mBinding.edtValue.text.toString().trim()))
            vehicleMaster.value = mBinding.edtValue.text.toString().trim().toInt()
        vehicleMaster.vehicleNo = mBinding.edtRegistrationNo.text.toString().trim()
        if (mBinding.spnStatus.selectedItem != null) {
            val statusCode: COMStatusCode = mBinding.spnStatus.selectedItem as COMStatusCode
            vehicleMaster.statusCode = statusCode.statusCode
        }
        if (mBinding.spnVehicleType.selectedItem != null) {
            val vehicleType: ADMVehicleTypes = mBinding.spnVehicleType.selectedItem as ADMVehicleTypes
            vehicleMaster.vehicleTypeCode = vehicleType.vehicleTypeCode
        }
        if (mBinding.spnFuelType.selectedItem != null) {
            val comComboStaticValues: ComComboStaticValues = mBinding.spnFuelType.selectedItem as ComComboStaticValues
            vehicleMaster.fuelType = comComboStaticValues.comboValue
        }
        if (mBinding.spnTransmissionType.selectedItem != null) {
            val comComboStaticValues: ComComboStaticValues = mBinding.spnTransmissionType.selectedItem as ComComboStaticValues
            vehicleMaster.transmission = comComboStaticValues.comboValue
        }

        vehicleMaster.vehicleSycotaxID = mBinding.edtSycoTaxID.text.toString().trim()
        if (!TextUtils.isEmpty(vehicleMaster.vehicleSycotaxID)) {
            saveVehicleDetails(v, GetInsertVehicleOwnershipDetails(SecurityContext(), vehicleOwnership, vehicleMaster))
        } else {
            //TODO in validation
            mListener?.showAlertDialog("Please select Sycotax Id")
        }

    }

    private fun saveVehicleDetails(v: View, insertVehicleOwnership: GetInsertVehicleOwnershipDetails) {
        if (0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId) {
            insertVehicleOwnership.VehicleOwnership.accountID = "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId!!}"

            if (vehicleOwnershipId != null) {
                insertVehicleOwnership.VehicleOwnership.vehicleOwnershipID = vehicleOwnershipId
            }

            mListener?.showProgressDialog()
            APICall.insertVehicleOwnership(insertVehicleOwnership, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    if (v.id == R.id.btnSave) {
                        if ((mVuAdmVehicleOwnership != null) && mVuAdmVehicleOwnership?.vehicleOwnershipID != 0)
                            mListener?.showSnackbarMsg(getString(R.string.vehicle_details_updated_successfully))
                        else
                            mListener?.showSnackbarMsg(getString(R.string.vehicle_details_added_successfully))
                        Handler().postDelayed({
                            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                            mListener!!.popBackStack()
                        }, 500)
                    } else {
                        if (response) {
                            if (vehicleOwnershipId == null) {
                                getVehicleOwnershipId(insertVehicleOwnership, v)
                            } else {
                                insertVehicleOwnership.VehicleMaster.vehicleNo.let {
                                    mPrimaryKey = insertVehicleOwnership.VehicleMaster.vehicleNo
                                    onClick(v)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }

            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            *//*   if (mCode == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
     val vuAdmVehicleOwnership = VUADMVehicleOwnership()
     vuAdmVehicleOwnership.chassisNo = insertVehicleOwnership.VehicleMaster.chassisNo
     vuAdmVehicleOwnership.co2Emission = insertVehicleOwnership.VehicleMaster.cO2Emissions
     vuAdmVehicleOwnership.color = insertVehicleOwnership.VehicleMaster.color
     vuAdmVehicleOwnership.fromDate = insertVehicleOwnership.VehicleOwnership.fromDate
     vuAdmVehicleOwnership.toDate = insertVehicleOwnership.VehicleOwnership.toDate
     vuAdmVehicleOwnership.fuelType = insertVehicleOwnership.VehicleMaster.fuelType
     vuAdmVehicleOwnership.horsepower = insertVehicleOwnership.VehicleMaster.horsepower
     vuAdmVehicleOwnership.power = insertVehicleOwnership.VehicleMaster.power
     vuAdmVehicleOwnership.vehicleNo = insertVehicleOwnership.VehicleOwnership.vehicleNo
     vuAdmVehicleOwnership.registrationNo = insertVehicleOwnership.VehicleMaster.registrationNo
     vuAdmVehicleOwnership.transmission = insertVehicleOwnership.VehicleMaster.transmission
     vuAdmVehicleOwnership.seatNumber = insertVehicleOwnership.VehicleMaster.seatsNumber
     vuAdmVehicleOwnership.value = insertVehicleOwnership.VehicleMaster.value
     vuAdmVehicleOwnership.loadCapacity = insertVehicleOwnership.VehicleMaster.loadCapacity
     vuAdmVehicleOwnership.vehicleTypeCode = insertVehicleOwnership.VehicleMaster.vehicleTypeCode
     vuAdmVehicleOwnership.statusCode = insertVehicleOwnership.VehicleMaster.statusCode
     vuAdmVehicleOwnership.remarks = insertVehicleOwnership.VehicleMaster.remarks
     vuAdmVehicleOwnership.registrationDate = insertVehicleOwnership.VehicleMaster.registrationDate

     ObjectHolder.registerBusiness.insertVehicleOwnershipDetails.add(insertVehicleOwnership)
     ObjectHolder.registerBusiness.vehicleOwnerships.add(vuAdmVehicleOwnership)

     Handler().postDelayed({
         targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
         mListener!!.popBackStack()
     }, 500)
 } else {*//*
        }*/

    }

    private fun getVehicleOwnershipId(insertVehicleOwnership: GetInsertVehicleOwnershipDetails, v: View) {
        if (0 != ObjectHolder.registerBusiness.vuCrmAccounts?.accountId) {
            insertVehicleOwnership.VehicleOwnership.accountID = "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId!!}"
        }
        APICall.getVehicleOwnershipIDByVehicleNo(insertVehicleOwnership.VehicleOwnership.accountID, insertVehicleOwnership.VehicleMaster.vehicleNo, object : ConnectionCallBack<BigInteger> {
            override fun onSuccess(response: BigInteger) {
                if (response.compareTo(BigInteger("0")) == 1) {
                    vehicleOwnershipId = response.toString()
                    insertVehicleOwnership.VehicleMaster.vehicleNo.let {
                        mPrimaryKey = insertVehicleOwnership.VehicleMaster.vehicleNo
                        onClick(v)
                    }
                }
            }

            override fun onFailure(message: String) {
                mListener?.showSnackbarMsg(message)
            }

        })

    }

    private fun validateView(): Boolean {
        if (mBinding.edtSycoTaxID.text != null && TextUtils.isEmpty(mBinding.edtSycoTaxID.text.toString().trim { it <= ' ' })) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.syco_tax_id))
            return false
        }
        if (mBinding.edtRegistrationNo.text != null && TextUtils.isEmpty(mBinding.edtRegistrationNo.text.toString().trim { it <= ' ' })) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.registration_no))
            return false
        }
        val transmissionComboStatic = mBinding.spnTransmissionType.selectedItem as ComComboStaticValues?
        if (transmissionComboStatic?.comboCode == null || transmissionComboStatic.comboCode == "-1") {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.transmission))
            mBinding.spnTransmissionType.requestFocus()
            return false
        }

        val comComboStaticValues = mBinding.spnFuelType.selectedItem as ComComboStaticValues?
        if (comComboStaticValues?.comboCode == null || comComboStaticValues.comboCode == "-1") {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.fuel_type))
            mBinding.spnFuelType.requestFocus()
            return false
        }
        val comStatusCode = mBinding.spnStatus.selectedItem as COMStatusCode?
        if (comStatusCode?.statusCode == null || comStatusCode.statusCode == "-1") {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.status))
            mBinding.spnStatus.requestFocus()
            return false
        }
        val vehicleTypes = mBinding.spnVehicleType.selectedItem as ADMVehicleTypes?
        if (vehicleTypes?.vehicleTypeCode == null || vehicleTypes.vehicleTypeCode == "-1") {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.vehicle_type))
            mBinding.spnVehicleType.requestFocus()
            return false
        }
        if (!TextUtils.isEmpty(mBinding.edtRegistrationDate.text.toString().trim())) {
            val date = mBinding.edtRegistrationDate.text.toString().trim()
            val inMillis = getTimeStampFromDate(date)
            val currentMillis = System.currentTimeMillis()
            if (inMillis > currentMillis) {
                mListener?.showSnackbarMsg(getString(R.string.registration_date_should_not_be_future_date))
                return false
            }
        }
        if (TextUtils.isEmpty(mBinding.edtStartDate.text.toString().trim())) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.start_date))
            return false
        }
        if (!TextUtils.isEmpty(mBinding.edtStartDate.text.toString().trim()) && !TextUtils.isEmpty(mBinding.edtEndDate.text.toString().trim())) {
            val startDate = serverFormatDate(mBinding.edtStartDate.text.toString().trim())
            val startDateInMillis = getTimeStampFromDate(startDate)
            val endDate = serverFormatDate(mBinding.edtEndDate.text.toString().trim())
            val endDateInMillis = getTimeStampFromDate(endDate)
            if (endDateInMillis < startDateInMillis) {
                mListener?.showSnackbarMsg(getString(R.string.msg_start_date_is_greater_than_end_date))
                return false
            }
        }
        return true
    }

    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun popBackStack()
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener)
        var screenMode: Constant.ScreenMode
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)

    }


//    private fun displayScannerListSelection() {
//        mListener?.showAlertDialog(R.string.parking_vehicle_scan_list,
//                R.string.yes, View.OnClickListener {
//            val dialog = (it as Button).tag as androidx.appcompat.app.AlertDialog
//            dialog.dismiss()
//        }, R.string.no, View.OnClickListener {
//            val dialog = (it as Button).tag as androidx.appcompat.app.AlertDialog
//            dialog.dismiss()
//            displaySycoTaxList(mVehicleSycotaxList)
//        })
//    }

    private fun displaySycoTaxList(response: List<UnusedVehicleSycotaxID>) {
        val vehicleSycotaxIDList: ArrayList<String> = arrayListOf()
        for (value in response) {
            vehicleSycotaxIDList.add(value.vehicleSycotaxID.toString())
        }
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle(R.string.title_select_sycotax_id)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, vehicleSycotaxIDList)
        builder.setAdapter(adapter) { dialog, which ->
            dialog.dismiss()
            response[which].vehicleSycotaxID?.let {
                mBinding.edtSycoTaxID.setText(it)
            }
        }
        val dialog = builder.create()
        dialog.show()

    }


    private fun fetchChildEntriesCount() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "ADM_Vehicles"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        if (mVuAdmVehicleOwnership != null) {
            filterColumn.columnValue = mVuAdmVehicleOwnership?.registrationNo
        } else if (mPrimaryKey != null) {
            filterColumn.columnValue = mPrimaryKey
        }
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
                filterColumn.columnValue = "ADM_Vehicles"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                if (mVuAdmVehicleOwnership != null) {
                    filterColumn.columnValue = mVuAdmVehicleOwnership?.registrationNo
                } else if (mPrimaryKey != null) {
                    filterColumn.columnValue = mPrimaryKey
                }
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
            }
            "COM_Notes" -> {
                mBinding.txtNumberOfNotes.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "VehicleNo"
                if (mVuAdmVehicleOwnership != null) {
                    filterColumn.columnValue = mVuAdmVehicleOwnership?.registrationNo
                } else if (mPrimaryKey != null) {
                    filterColumn.columnValue = mPrimaryKey
                }
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "ADM_VehicleOwnership", "VehicleOwnershipID")
            }
            "ADM_VehicleOwnership" -> {
                mBinding.txtNumberOfInitialOutstanding.text = "$count"
            }
        }
    }

    private fun getInvoiceCount4Tax() {

        val currentDue = CheckCurrentDue()
        currentDue.accountId = mVuAdmVehicleOwnership?.accountId
        currentDue.vchrno  = mVuAdmVehicleOwnership?.vehicleOwnershipID
        currentDue.taxRuleBookCode  = mTaxRuleBookCode
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response>0)
                {
                    setEditActionForSave(false)
                }
                else
                {
                    setEditAction(true)
                }
            }
            override fun onFailure(message: String) {
                if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                {
                    setEditAction(false)
                }

            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mListener?.showToolbarBackButton(R.string.title_register_vehicle)
        if (mVuAdmVehicleOwnership?.registrationNo != null || mPrimaryKey != null) {
            fetchChildEntriesCount()
        }
    }

}
