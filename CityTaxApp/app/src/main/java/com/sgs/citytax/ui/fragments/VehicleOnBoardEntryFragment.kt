package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
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
import com.sgs.citytax.api.response.VehicleOwnershipDetailsResult
import com.sgs.citytax.databinding.FragmentVehicleOnboardingEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.ParkingTicketEntryActivity
import com.sgs.citytax.util.*
import java.util.*

class VehicleOnBoardEntryFragment : BaseFragment(), View.OnClickListener {

    private var mListener: Listener? = null
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP
    private var mSycoTaxId: String? = ""
    private var mVehicleMaster: VehicleMaster? = null

    private var mVehicleTypes: MutableList<ADMVehicleTypes>? = null
    private var mStatusCodes: MutableList<COMStatusCode>? = null
    private var mFuelTypes: MutableList<ComComboStaticValues>? = null
    private var mTransmissionTypes: MutableList<ComComboStaticValues>? = null
    lateinit var mBinding: FragmentVehicleOnboardingEntryBinding

    private var mPrimaryKey: String? = ""

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicle_onboarding_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    override fun initComponents() {
        //region getArguments
        arguments?.let {
            mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS)) {
                mVehicleMaster = arguments?.getParcelable(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS) as VehicleMaster?
                mPrimaryKey = mVehicleMaster?.vehicleNo
            }
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID)) {
                mSycoTaxId = it.getString(Constant.KEY_SYCO_TAX_ID)
            }
        }
        //endregion
        setViews()
        bindSpinner()
        setListeners()
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun popBackStack()
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener)
        var screenMode: Constant.ScreenMode
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun finish()
    }

    private fun setViews() {
        mBinding.edtRegistrationNo.isEnabled = TextUtils.isEmpty(mPrimaryKey)
//        mBinding.edtStartDate.setMaxDate(Calendar.getInstance().timeInMillis)
//        mBinding.edtStartDate.setDisplayDateFormat(displayDateFormat)
//        mBinding.edtEndDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtRegistrationDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtRegistrationDate.setDisplayDateFormat(displayDateFormat)

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }

        if (mCode == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP
                || mCode == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE
                || mCode == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP
                || mCode == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE) {
            mBinding.tilSycoId.visibility = View.VISIBLE
            mBinding.edtSycoTaxID.setText(mSycoTaxId)
            mBinding.frDocs.visibility = View.VISIBLE
        } else {
            mBinding.tilSycoId.visibility = View.GONE
        }
    }

    private fun setEditAction(action: Boolean) {

//        mBinding.edtRegistrationNo.isEnabled = action
        mBinding.spnTransmissionType.isEnabled = action
        mBinding.edtCO2Emission.isEnabled = action
        mBinding.edtPower.isEnabled = action
        mBinding.edtSeatNumber.isEnabled = action
        mBinding.edtColor.isEnabled = action
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

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
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

                if (response.comboStaticValues.isNotEmpty()) {
                    mFuelTypes = arrayListOf()
                    mTransmissionTypes = arrayListOf()
                    for (value in response.comboStaticValues) {
                        if (value.comboCode == "VehicleFuel") {
                            mFuelTypes!!.add(value)
                        } else if (value.comboCode == "VehicleTransmission") {
                            mTransmissionTypes!!.add(value)
                        }
                    }

                    if (mFuelTypes != null && mFuelTypes!!.isNotEmpty()) {
                        mFuelTypes?.add(0, ComComboStaticValues("-1", getString(R.string.select)))
                        mBinding.spnFuelType.adapter = ArrayAdapter<ComComboStaticValues>(activity!!, android.R.layout.simple_spinner_dropdown_item, mFuelTypes!!)
                    } else
                        mBinding.spnFuelType.adapter = null


                    if (mTransmissionTypes != null && mTransmissionTypes!!.isNotEmpty()) {
                        mTransmissionTypes?.add(0, ComComboStaticValues("-1", getString(R.string.select)))
                        mBinding.spnTransmissionType.adapter = ArrayAdapter<ComComboStaticValues>(activity!!, android.R.layout.simple_spinner_dropdown_item, mTransmissionTypes!!)
                    } else
                        mBinding.spnTransmissionType.adapter = null
                }

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
        if (mVehicleMaster != null) {
            if(mVehicleMaster!!.registrationNo!=null)
            mBinding.edtRegistrationNo.setText(mVehicleMaster!!.registrationNo)
            else
                mBinding.edtRegistrationNo.setText(mVehicleMaster!!.vehicleNo)

            mVehicleMaster?.cO2Emissions?.let {
                mBinding.edtCO2Emission.setText(mVehicleMaster!!.cO2Emissions)
            }
            mVehicleMaster?.power?.let {
                mBinding.edtPower.setText(mVehicleMaster!!.power)
            }
            mVehicleMaster?.seatsNumber?.let {
                mBinding.edtSeatNumber.setText("${mVehicleMaster!!.seatsNumber}")
            }
            mVehicleMaster?.color?.let {
                mBinding.edtColor.setText(mVehicleMaster!!.color)
            }

            mVehicleMaster?.chassisNo?.let {
                mBinding.edtChassisNo.setText(mVehicleMaster!!.chassisNo)
            }
            mBinding.edtRegistrationDate.setText(displayFormatDate(mVehicleMaster!!.registrationDate))
            mVehicleMaster?.horsepower?.let {
                mBinding.edtHorsePower.setText(mVehicleMaster!!.horsepower)
            }
            mVehicleMaster?.value?.let {
                mBinding.edtValue.setText("${mVehicleMaster!!.value}")
            }

            mVehicleMaster?.loadCapacity?.let {
                mBinding.edtLoadCapacity.setText("${mVehicleMaster!!.loadCapacity}")
            }

            mVehicleMaster?.mfg?.let {
                mBinding.edtManufacturer.setText("${mVehicleMaster!!.mfg}")
            }
            mVehicleMaster?.make?.let {
                mBinding.edtMake.setText("${mVehicleMaster!!.make}")
            }
            mVehicleMaster?.model?.let {
                mBinding.edtModel.setText("${mVehicleMaster!!.model}")
            }
            mVehicleMaster?.variant?.let {
                mBinding.edtVariant.setText("${mVehicleMaster!!.variant}")
            }
            mVehicleMaster?.manufacturingYear?.let {
                mBinding.edtManufacturingYear.setText("${mVehicleMaster!!.manufacturingYear}")
            }
            mVehicleMaster?.engineNo?.let {
                mBinding.edtEngineNo.setText("${mVehicleMaster!!.engineNo}")
            }
            mVehicleMaster?.cubicCapacity?.let {
                mBinding.edtCubicCapacity.setText("${mVehicleMaster!!.cubicCapacity}")
            }
            mVehicleMaster?.cubicCapacity?.let {
                mBinding.edtCubicCapacity.setText("${mVehicleMaster!!.cubicCapacity}")
            }
            mVehicleMaster?.remarks?.let {
                mBinding.edtRemarks.setText(mVehicleMaster!!.remarks)
            }




            if (mVehicleTypes != null)
                for ((index, obj) in mVehicleTypes!!.withIndex()) {
                    if (mVehicleMaster!!.vehicleTypeCode == obj.vehicleTypeCode) {
                        mBinding.spnVehicleType.setSelection(index)
                        break
                    }
                }
            if (mStatusCodes != null)
                for ((index, obj) in mStatusCodes!!.withIndex()) {
                    if (mVehicleMaster!!.statusCode == obj.statusCode) {
                        mBinding.spnStatus.setSelection(index)
                        break
                    }
                }

            if (mTransmissionTypes != null)
                for ((index, obj) in mTransmissionTypes!!.withIndex()) {
                    if (mVehicleMaster!!.transmission == obj.comboValue) {
                        mBinding.spnTransmissionType.setSelection(index)
                        break
                    }
                }
            if (mFuelTypes != null)
                for ((index, obj) in mFuelTypes!!.withIndex()) {
                    if (mVehicleMaster!!.fuelType == obj.comboValue) {
                        mBinding.spnFuelType.setSelection(index)
                        break
                    }
                }

            fetchChildEntriesCount()
        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.llVehicleOwnership.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                val count = mBinding.txtNumberOfInitialOutstanding.text
                if (!TextUtils.isEmpty(count) && (count.toString()).toInt() > 0) {
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
                } else {
                    mListener?.showAlertDialog(getString(R.string.please_add_vehicle_ownership))
                }
            }
            R.id.llVehicleOwnership -> {
                if (!TextUtils.isEmpty(mPrimaryKey)) {
                    val fragment = VehicleOnBoardingMasterFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    if(mCode == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE)
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP)
                    else
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
                    if(mCode == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE)
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP)
                    else
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
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
                    if(mCode == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE)
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP)
                    else
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
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
        }
    }

    private fun validateView(): Boolean {
        if (mBinding.edtRegistrationNo.text != null && TextUtils.isEmpty(mBinding.edtRegistrationNo.text.toString().trim())) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.vehicle_registration_no))
            return false
        }

        if (mBinding.edtRegistrationDate.text != null && TextUtils.isEmpty(mBinding.edtRegistrationDate.text.toString().trim())) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.registration_date))
            return false
        }
        val transmissionTypes = mBinding.spnTransmissionType.selectedItem as ComComboStaticValues?
        if (transmissionTypes?.comboCode == null || transmissionTypes.comboCode == "-1") {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.transmission))
            return false
        }

        val fuelTypes = mBinding.spnFuelType.selectedItem as ComComboStaticValues?
        if (fuelTypes?.comboCode == null || fuelTypes.comboCode == "-1") {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.fuel_type))
            mBinding.spnVehicleType.requestFocus()
            return false
        }

        val vehicleTypes = mBinding.spnVehicleType.selectedItem as ADMVehicleTypes?
        if (vehicleTypes?.vehicleTypeCode == null || vehicleTypes.vehicleTypeCode == "-1") {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.vehicle_type))
            mBinding.spnVehicleType.requestFocus()
            return false
        }
        val statusTypes = mBinding.spnStatus.selectedItem as COMStatusCode?
        if (statusTypes?.statusCode == null || statusTypes.statusCode == "-1") {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.status))
            mBinding.spnStatus.requestFocus()
            return false
        }

        return true
    }


    private fun prepareData(v: View) {
//        val vehicleOwnership = VehicleOwnership()
//        if (mVuAdmVehicleOwnership != null && mVuAdmVehicleOwnership?.vehicleOwnershipID != null)
//            vehicleOwnership.vehicleOwnershipID = "${mVuAdmVehicleOwnership?.vehicleOwnershipID}"
//        vehicleOwnership.fromDate = formatDateTimeInMillisecond(parseDate(mBinding.edtStartDate.text.toString().trim(), displayDateFormat))
//        vehicleOwnership.toDate = formatDateTimeInMillisecond(parseDate(mBinding.edtEndDate.text.toString().trim(), displayDateFormat))
        mVehicleMaster = VehicleMaster()
        mVehicleMaster?.cO2Emissions = mBinding.edtCO2Emission.text.toString().trim()
        mVehicleMaster?.chassisNo = mBinding.edtChassisNo.text.toString().trim()
        mVehicleMaster?.color = mBinding.edtColor.text.toString().trim()
        mVehicleMaster?.horsepower = mBinding.edtHorsePower.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtLoadCapacity.text.toString().trim()))
            mVehicleMaster?.loadCapacity = mBinding.edtLoadCapacity.text.toString().trim().toInt()

        mVehicleMaster?.mfg = mBinding.edtManufacturer.text.toString().trim()
        mVehicleMaster?.make = mBinding.edtMake.text.toString().trim()
        mVehicleMaster?.model = mBinding.edtModel.text.toString().trim()
        mVehicleMaster?.variant = mBinding.edtVariant.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtManufacturingYear.text.toString().trim())) {
            mVehicleMaster?.manufacturingYear = mBinding.edtManufacturingYear.text.toString().trim().toInt()
        }
        mVehicleMaster?.engineNo = mBinding.edtEngineNo.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtCubicCapacity.text.toString().trim())) {
            mVehicleMaster?.cubicCapacity = currencyToDouble(mBinding.edtCubicCapacity.text.toString())!!.toDouble()
        }

        mVehicleMaster?.power = mBinding.edtPower.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtRegistrationDate.text.toString().trim()))
            mVehicleMaster?.registrationDate = formatDateTimeInMillisecond(parseDate(mBinding.edtRegistrationDate.text.toString().trim(), displayDateFormat))
        mVehicleMaster?.registrationNo = mBinding.edtRegistrationNo.text.toString().trim()
        mVehicleMaster?.vehicleNo = mBinding.edtRegistrationNo.text.toString().trim()
        mVehicleMaster?.remarks = mBinding.edtRemarks.text.toString().trim()
        if (!TextUtils.isEmpty(mBinding.edtSeatNumber.text.toString().trim()))
            mVehicleMaster?.seatsNumber = mBinding.edtSeatNumber.text.toString().trim().toInt()
        if (!TextUtils.isEmpty(mBinding.edtValue.text.toString().trim()))
            mVehicleMaster?.value = mBinding.edtValue.text.toString().trim().toInt()
        if (mBinding.spnStatus.selectedItem != null) {
            val statusCode: COMStatusCode = mBinding.spnStatus.selectedItem as COMStatusCode
            mVehicleMaster?.statusCode = statusCode.statusCode
        }
        if (mBinding.spnVehicleType.selectedItem != null) {
            val vehicleType: ADMVehicleTypes = mBinding.spnVehicleType.selectedItem as ADMVehicleTypes
            mVehicleMaster?.vehicleTypeCode = vehicleType.vehicleTypeCode
        }
        if (mBinding.spnFuelType.selectedItem != null) {
            val comComboStaticValues: ComComboStaticValues = mBinding.spnFuelType.selectedItem as ComComboStaticValues
            mVehicleMaster?.fuelType = comComboStaticValues.comboValue
        }
        if (mBinding.spnTransmissionType.selectedItem != null) {
            val comComboStaticValues: ComComboStaticValues = mBinding.spnTransmissionType.selectedItem as ComComboStaticValues
            mVehicleMaster?.transmission = comComboStaticValues.comboValue
        }

        mVehicleMaster?.vehicleSycotaxID = mSycoTaxId
        if (!TextUtils.isEmpty(mPrimaryKey)) {
            updateVehicleDetails(GetInsertVehicleOnBoarding(SecurityContext(), mVehicleMaster!!), v)
        } else saveVehicleDetails(GetInsertVehicleOnBoarding(SecurityContext(), mVehicleMaster!!), v)
    }

    private fun saveVehicleDetails(getInsertVehicleOwnershipDetails: GetInsertVehicleOnBoarding, v: View) {
        mListener?.showProgressDialog()
        APICall.onBoardVehicle(getInsertVehicleOwnershipDetails, object : ConnectionCallBack<String> {
            override fun onSuccess(response: String) {
                mListener?.dismissDialog()
                mPrimaryKey = response

                when (v.id) {
                    R.id.llVehicleOwnership, R.id.llDocuments, R.id.llNotes -> {
                        onClick(v)
                    }

                    else -> {
                        Handler().postDelayed({
                            mListener?.popBackStack()
                            if (mCode == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE
                                    || mCode == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE) {
                                sendVehicleMasterToDetails()
                            } else if (mCode == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP) {
                                getVehicleOwnership(mVehicleMaster)
                            } else {
                                mListener?.finish()
                            }
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

    private fun updateVehicleDetails(getInsertVehicleOwnershipDetails: GetInsertVehicleOnBoarding, v: View) {
        mListener?.showProgressDialog()
        APICall.onUpdateVehicleonBoard(getInsertVehicleOwnershipDetails, object : ConnectionCallBack<String> {
            override fun onSuccess(response: String) {
                mListener?.dismissDialog()
                mPrimaryKey = response

                when (v.id) {
                    R.id.llVehicleOwnership, R.id.llDocuments, R.id.llNotes -> {
                        onClick(v)
                    }

                    else -> {
                        Handler().postDelayed({
                            mListener?.popBackStack()
                            if (mCode == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE
                                    || mCode == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE) {
                                sendVehicleMasterToDetails()
                            } else if (mCode == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP) {
                                getVehicleOwnership(mVehicleMaster)
                            } else {
                                mListener?.finish()
                            }
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

    private fun sendVehicleMasterToDetails() {
        if (mVehicleMaster != null) {
            val vehicleDetails = VehicleDetails()
            vehicleDetails.vehicleNumber = mVehicleMaster?.vehicleNo
            vehicleDetails.vehicleSycoTaxID = mVehicleMaster?.vehicleSycotaxID
            val intent = Intent()
            intent.putExtra(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS, vehicleDetails)
            Event.instance.hold(intent)
            mListener?.finish()
        }
    }

    private fun startParkingTicketEntry(vehicleDetails: VehicleDetails) {
        val intent = Intent(context, ParkingTicketEntryActivity::class.java)
        intent.putExtra(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS, vehicleDetails)
        intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
        startActivity(intent)
        activity?.finish()
    }

    private fun getVehicleOwnership(vehicleMaster: VehicleMaster?) {
        mListener?.showProgressDialog()
        APICall.getVehicleOwnershipDetails(vehicleMaster?.vehicleNo, object : ConnectionCallBack<VehicleOwnershipDetailsResult> {
            override fun onSuccess(response: VehicleOwnershipDetailsResult) {
                mListener?.dismissDialog()
                val list = response.vehicleDetails
                list?.let {
                    for (vehicle: VehicleDetails in it) {
                        if (vehicle.toDate == null) {
                            startParkingTicketEntry(vehicle)
                            break
                        }else{
                            startParkingTicketEntry(vehicle)
                            break
                        }
                    }
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (message.isNotEmpty())
                    mListener?.showAlertDialog(message)
            }
        })
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
        if(mVehicleMaster!!.registrationNo!=null){
            filterColumn.columnValue = mVehicleMaster?.registrationNo
        }else{
            filterColumn.columnValue = mVehicleMaster?.vehicleNo
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
                if(mVehicleMaster!!.registrationNo!=null){
                    filterColumn.columnValue = mVehicleMaster?.registrationNo
                }else{
                    filterColumn.columnValue = mVehicleMaster?.vehicleNo
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
                if(mVehicleMaster!!.registrationNo!=null){
                    filterColumn.columnValue = mVehicleMaster?.registrationNo
                }else{
                    filterColumn.columnValue = mVehicleMaster?.vehicleNo
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mListener?.showToolbarBackButton(R.string.title_register_vehicle)
        if (mVehicleMaster?.registrationNo != null) {
            fetchChildEntriesCount()
        }
    }

}