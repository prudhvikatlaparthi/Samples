package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.google.maps.android.SphericalUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.PropertyComfortLevels
import com.sgs.citytax.api.response.PropertyDueResponse
import com.sgs.citytax.api.response.PropertyOwners
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPropertyEntryTaxBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.GeoMapSearchActivity
import com.sgs.citytax.ui.ParentPropertyPlanImagesActivity
import com.sgs.citytax.ui.TreeViewActivity
import com.sgs.citytax.ui.adapter.CustomDialogAdapter
import com.sgs.citytax.util.*
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*

class PropertyTaxEntryFragment : BaseFragment(), View.OnClickListener {
    val TAG = "PropertyTaxEntryFragmen"
    private lateinit var mBinding: FragmentPropertyEntryTaxBinding
    private var mListener: Listener? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mSycoTaxID: String? = ""
    private var mCountries: List<COMCountryMaster> = arrayListOf()
    private var mStates: List<COMStateMaster> = arrayListOf()
    private var mSectors: List<COMSectors> = arrayListOf()
    private var mZones: List<COMZoneMaster> = arrayListOf()
    private var mCities: List<VUCOMCityMaster> = arrayListOf()
    private var mAdministrativeOffices: List<UMXUserOrgBranches> = arrayListOf()
    private var mPropertyTypes: List<COMPropertyTypes> = arrayListOf()
    private var mElectricityConsumptions: List<COMElectricityConsumption> = arrayListOf()
    private var mPhasesOfElectricity: List<COMPhaseOfElectricity> = arrayListOf()
    private var mWaterConsumptions: List<COMWaterConsumption> = arrayListOf()
    private var mProperties: List<VUCOMProperty> = arrayListOf()
    private var mLands: List<VUCOMLand> = arrayListOf()
    private var registrationTypes: List<COMPropertyRegistrationTypes> = arrayListOf()
    private var measurementUnits: List<VUINVMeasurementUnits> = arrayListOf()
    private var landUseTypes: List<COMLandUseTypes> = arrayListOf()
    private var mStatusCodes: List<COMStatusCode> = arrayListOf()
    private var mGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mParentGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mLandGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mComboStaticValues: List<ComComboStaticValues> = arrayListOf()
    private var mPropertyExemptionReasons: MutableList<COMPropertyExemptionReasons> = arrayListOf()

    private var mPropertySplitList: MutableList<ComComboStaticValues> = arrayListOf()

    private var mStorePropertyData: StorePropertyData? = null
    private var mGeoAddress: GeoAddress? = null
    private var geoAddressList: ArrayList<GeoAddress>? = null
    private var pendingList: PendingRequestList? = null
    private var mNominee: BusinessOwnership? = null

    var propertyOwners: PropertyOwners? = null
    private var mPropertyOwnership: PropertyOwnerNomineePayload? = null

    private var latLong = LatLng(12.36566, -1.53388)
    var googleMap: GoogleMap? = null
    val POLYGON_PADDING_PREFERENCE: Int = 100

    var locationHelper: LocationHelper? = null
    private var editMode: Boolean? = true

    var splitObjSelected: ComComboStaticValues = ComComboStaticValues()

    private var mArrivedRequestCount = 0

    private var mTaxRuleBookCode: String? = ""

    private var mAccountId: Int = 0
    private var setViewForGeoSpatial: Boolean? = false  //todo New key to Hide views for geo spacial- Busianess Record - 15/3/2022, not used fromScreen, to not to disturb th flow
    private var isActionEnabled: Boolean = true

    companion object {
        fun newInstance() = PropertyTaxEntryFragment()
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                mSycoTaxID = it.getString(Constant.KEY_SYCO_TAX_ID)
            if (it.containsKey(Constant.KEY_EDIT))
                editMode = it.getBoolean(Constant.KEY_EDIT)
            if (it.containsKey(Constant.KEY_PROPERTY_DETAILS))
                mStorePropertyData = it.getParcelable(Constant.KEY_PROPERTY_DETAILS)
            if (it.containsKey(Constant.KEY_ADDRESS))
                geoAddressList = it.getParcelableArrayList(Constant.KEY_ADDRESS)

            if (it.containsKey(Constant.KEY_PENDING_PROPERTY_LIST))
                pendingList = it.getParcelable(Constant.KEY_PENDING_PROPERTY_LIST)
            if (it.containsKey(Constant.KEY_TAX_RULE_BOOK_CODE))
                mTaxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)
            if (it.containsKey(Constant.KEY_ACCOUNT_ID))
                mAccountId = it.getInt(Constant.KEY_ACCOUNT_ID)
            if (it.containsKey(Constant.KEY_GEO_SPATIAL_VIEW))
                setViewForGeoSpatial = it.getBoolean(Constant.KEY_GEO_SPATIAL_VIEW)
        }
        //endregion
        if (geoAddressList != null && geoAddressList?.size!! > 0) {
            mGeoAddress = geoAddressList?.get(0) as GeoAddress
        }
        setViews()
        showViewsEnabled()
        bindSpinner()
        setEvents()

        if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY)
            mBinding.spnStatus.isClickable = false

        if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY) {
            mBinding.cardViewParentDocs.visibility = View.GONE
        }

        if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY) {
//            mListener?.showProgressDialog()
            APICall.getChildPropertyCount4Property(mStorePropertyData?.propertyID, object : ConnectionCallBack<Int> {
                override fun onSuccess(response: Int) {
//                    mListener?.dismissDialog()
                    if (response > 0) {
                        mBinding.spnPropertySplit.isClickable = false
                        mBinding.spnPropertySplit.isEnabled = false
                    }
                }

                override fun onFailure(message: String) {
//                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }

            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_entry_tax, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setViews() {
        if (mCode == Constant.QuickMenu.QUICK_MENU_VERIFY_PROPERTY) {
            mBinding.llApproveReject.visibility = View.VISIBLE
            mBinding.llSave.visibility = View.GONE
            mListener?.screenMode = Constant.ScreenMode.VIEW
        } else {
            mBinding.llApproveReject.visibility = View.GONE
            mBinding.llSave.visibility = View.VISIBLE
        }

        if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY) {
            mBinding.spnStatus.isClickable = false
            mBinding.spnStatus.isEnabled = false
            mBinding.spnComfortLevel.isClickable = false
            mBinding.spnComfortLevel.isEnabled = false
        }

        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY) {
            mBinding.cardViewOwnerChild.visibility = View.GONE
            mBinding.cardViewOwner.visibility = View.VISIBLE
            mBinding.cardViewTreeChild.visibility = View.GONE
            if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY) {
                mBinding.edtToDate.isEnabled = false
            }
            if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY) {
                mBinding.edtFromDate.isEnabled = false
                mBinding.edtPropertyRegistrationNo.isEnabled = false
                mBinding.edtNomineeName.isEnabled = false
                mBinding.spnExemptionReason.isClickable = false
                mBinding.spnRelation.isClickable = false
                mBinding.llCreateNominee.visibility = View.GONE

            }
        } else {
            mBinding.cardViewOwnerChild.visibility = View.VISIBLE
            mBinding.cardViewOwner.visibility = View.GONE
            mBinding.cardViewTreeChild.visibility = View.VISIBLE
        }

        if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY) {
            mBinding.edtParentProperty.isEnabled = false
            mBinding.edtLandProperty.isEnabled = false
        }

    }

    private fun setEditAction(action: Boolean) {
        isActionEnabled = action
        mBinding.edtPropertyName.isEnabled = action
        mBinding.edtPropertyCode.isEnabled = action
        mBinding.spnPropertyType.isClickable = action
        mBinding.edtParentProperty.isEnabled = action
        mBinding.edtLandProperty.isEnabled = action
        mBinding.spnLandUseType.isClickable = action
        mBinding.spnRegistrationType.isClickable = action
        //mBinding.spnStatus.isClickable = action
        mBinding.edtRegistrationNo.isEnabled = action
        mBinding.edtRegistrationDate.isEnabled = action
        mBinding.edtSurveyNo.isEnabled = action
        mBinding.spnAdministrationOffice.isClickable = action
        mBinding.edtConstructedDate.isEnabled = action
        mBinding.edtMonthlyRentAmount.isEnabled = action
        mBinding.edtEstimatedRentAmount.isEnabled = action
        mBinding.spnElectricityConsumption.isEnabled = action
        mBinding.spnPhaseOfElectricity.isEnabled = action
        mBinding.spnWaterConsumption.isEnabled = action
        mBinding.spnComfortLevel.isEnabled = action
        mBinding.edtBuiltUpAreaPerFloor.isEnabled = action
        mBinding.edtTotalBuiltUpArea.isEnabled = action
        mBinding.edtOpenSpace.isEnabled = action
        mBinding.edtNoOfFloor.isEnabled = action
        mBinding.edtNoOfHousesPerFloor.isEnabled = action
        mBinding.edtNoOfEstablishmentsPerFloor.isEnabled = action
        mBinding.edtNoOfParkingPlaces.isEnabled = action
        mBinding.edtFloorNo.isEnabled = action
        //mBinding.spnMeasureUnit.isClickable = action
        mBinding.edtLength.isEnabled = action
        mBinding.edtWidth.isEnabled = action
        mBinding.edtArea.isEnabled = action
        mBinding.spnCountry.isClickable = action
        mBinding.spnState.isClickable = action
        mBinding.spnCity.isClickable = action
        mBinding.spnZone.isClickable = action
        mBinding.spnSector.isClickable = action
        mBinding.edtStreet.isEnabled = action
        mBinding.edtZipCode.isEnabled = action
        mBinding.edtPlot.isEnabled = action
        mBinding.edtBlock.isEnabled = action
        mBinding.edtDoorNo.isEnabled = action
        mBinding.btnCalculate.isEnabled = action
        mBinding.btnCalculate.isClickable = action
        mBinding.btnAddGeo.isEnabled = action
        mBinding.btnAddGeo.isClickable = action
        mBinding.spnPropertySplit.isEnabled = action
        mBinding.spnPropertySplit.isClickable = action
        if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY && editMode == false) {
            mBinding.btnSave.visibility = View.GONE
        } else if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            mBinding.btnSave.visibility = View.GONE
        }

    }

    private fun setEditActionForSave(action: Boolean) {
        isActionEnabled = action
        mBinding.edtPropertyName.isEnabled = action
        mBinding.edtPropertyCode.isEnabled = action
        mBinding.spnPropertyType.isClickable = action
        mBinding.edtParentProperty.isEnabled = action
        mBinding.edtLandProperty.isEnabled = action
        mBinding.spnLandUseType.isClickable = action
        mBinding.spnRegistrationType.isClickable = action
        mBinding.edtRegistrationNo.isEnabled = action
        mBinding.edtRegistrationDate.isEnabled = action
        mBinding.edtSurveyNo.isEnabled = action
        mBinding.spnAdministrationOffice.isClickable = action
        mBinding.edtConstructedDate.isEnabled = action
        mBinding.edtMonthlyRentAmount.isEnabled = action
        mBinding.edtEstimatedRentAmount.isEnabled = action
        mBinding.spnElectricityConsumption.isEnabled = action
        mBinding.spnPhaseOfElectricity.isEnabled = action
        mBinding.spnWaterConsumption.isEnabled = action
        mBinding.spnComfortLevel.isEnabled = action
        mBinding.edtBuiltUpAreaPerFloor.isEnabled = action
        mBinding.edtTotalBuiltUpArea.isEnabled = action
        mBinding.edtOpenSpace.isEnabled = action
        mBinding.edtNoOfFloor.isEnabled = action
        mBinding.edtNoOfHousesPerFloor.isEnabled = action
        mBinding.edtNoOfEstablishmentsPerFloor.isEnabled = action
        mBinding.edtNoOfParkingPlaces.isEnabled = action
        mBinding.edtFloorNo.isEnabled = action
        //mBinding.spnMeasureUnit.isClickable = action
        mBinding.edtLength.isEnabled = action
        mBinding.edtWidth.isEnabled = action
        mBinding.edtArea.isEnabled = action
        mBinding.spnCountry.isClickable = action
        mBinding.spnState.isClickable = action
        mBinding.spnCity.isClickable = action
        mBinding.spnZone.isClickable = action
        mBinding.spnSector.isClickable = action
        mBinding.edtStreet.isEnabled = action
        mBinding.edtZipCode.isEnabled = action
        mBinding.edtPlot.isEnabled = action
        mBinding.edtBlock.isEnabled = action
        mBinding.edtDoorNo.isEnabled = action
        mBinding.btnCalculate.isEnabled = action
        mBinding.btnCalculate.isClickable = action
        mBinding.btnAddGeo.isEnabled = action
        mBinding.btnAddGeo.isClickable = action
        mBinding.btnSave.visibility = VISIBLE
        mBinding.spnStatus.isClickable = true
    }

    private fun showViewsEnabled() {
        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            setEditAction(false)
        } else
            setEditAction(true)
    }

    private fun bindSpinner() {


//        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY) {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_PropertyOwners", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mComboStaticValues = response.comboStaticValues
                if (mComboStaticValues.isNullOrEmpty())
                    mBinding.spnRelation.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mComboStaticValues)
                    mBinding.spnRelation.adapter = adapter
                }
//                mListener?.dismissDialog()
                updatemArrivedRequestCount()
            }

            override fun onFailure(message: String) {
                mBinding.spnRelation.adapter = null
//                mListener?.dismissDialog()
                updatemArrivedRequestCount()
            }

        })

//        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_PropertyOwnership", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mPropertyExemptionReasons = response.propertyExemption
                if (mPropertyExemptionReasons.isNullOrEmpty())
                    mBinding.spnExemptionReason.adapter = null
                else {
                    mPropertyExemptionReasons.add(0, COMPropertyExemptionReasons(getString(R.string.select), -1))
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mPropertyExemptionReasons)
                    mBinding.spnExemptionReason.adapter = adapter
                }

//                    bindData()

//                mListener?.dismissDialog()
                updatemArrivedRequestCount()
            }

            override fun onFailure(message: String) {
                mBinding.spnExemptionReason.adapter = null
//                mListener?.dismissDialog()
                updatemArrivedRequestCount()
            }

        })
//        }


//        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("COM_Property", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mCountries = response.countryMaster
                mStates = response.stateMaster
                mCities = response.cityMaster
                mSectors = response.sectors
                mZones = response.zoneMaster

                filterCountries()

                mAdministrativeOffices = response.userOrgBranches
                mPropertyTypes = response.propertyTypesVU
                mElectricityConsumptions = response.electricityConsumptions
                mPhasesOfElectricity = response.phasesOfElectricity
                mWaterConsumptions = response.waterConsumptions
                mProperties = response.properties
                mLands = response.lands
                registrationTypes = response.propertyRegistrationTypes
                measurementUnits = response.measurementUnits
                //landUseTypes = response.landUseTypes
                mStatusCodes = response.statusCodes
                mPropertySplitList = response.comboStaticValues

                if (mAdministrativeOffices.isNullOrEmpty())
                    mBinding.spnAdministrationOffice.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mAdministrativeOffices)
                    mBinding.spnAdministrationOffice.adapter = adapter
                }
                if (mPropertyTypes.isNullOrEmpty())
                    mBinding.spnPropertyType.adapter = null
                else {
                    (mPropertyTypes as MutableList<COMPropertyTypes>).add(0, COMPropertyTypes(getString(R.string.select), "", "", -1))
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mPropertyTypes)
                    mBinding.spnPropertyType.adapter = adapter
                }
                if (mPropertySplitList.isNullOrEmpty())
                    mBinding.spnPropertySplit.adapter = null
                else {
                    mPropertySplitList.add(0, ComComboStaticValues("", getString(R.string.select), "-1"))
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mPropertySplitList)
                    mBinding.spnPropertySplit.adapter = adapter
                }
                if (mElectricityConsumptions.isNullOrEmpty())
                    mBinding.spnElectricityConsumption.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mElectricityConsumptions)
                    mBinding.spnElectricityConsumption.adapter = adapter
                }
                if (mPhasesOfElectricity.isNullOrEmpty())
                    mBinding.spnPhaseOfElectricity.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mPhasesOfElectricity)
                    mBinding.spnPhaseOfElectricity.adapter = adapter
                }
                if (mWaterConsumptions.isNullOrEmpty())
                    mBinding.spnWaterConsumption.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mWaterConsumptions)
                    mBinding.spnWaterConsumption.adapter = adapter
                }
                if (mProperties.isNullOrEmpty())
                    mBinding.edtParentProperty.setText(getString(R.string.select))
                else {
                    (mProperties as MutableList<VUCOMProperty>).add(0, VUCOMProperty(-1, "", getString(R.string.select)))
                }
                if (mLands.isNullOrEmpty())
                    mBinding.edtLandProperty.setText(getString(R.string.select))
                else {
                    (mLands as MutableList<VUCOMLand>).add(0, VUCOMLand(-1, "", getString(R.string.select)))
                }

                if (registrationTypes.isNullOrEmpty())
                    mBinding.spnRegistrationType.adapter = null
                else {
                    (registrationTypes as MutableList<COMPropertyRegistrationTypes>).add(0, COMPropertyRegistrationTypes(getString(R.string.select), "-1"))
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, registrationTypes)
                    mBinding.spnRegistrationType.adapter = adapter
                }

//                if (measurementUnits.isNullOrEmpty())
//                    mBinding.spnMeasureUnit.adapter = null
//                else {
//                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, measurementUnits)
//                    mBinding.spnMeasureUnit.adapter = adapter
//                }
                /* if (landUseTypes.isNullOrEmpty())
                     mBinding.spnLandUseType.adapter = null
                 else {
                     val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, landUseTypes)
                     mBinding.spnLandUseType.adapter = adapter
                 }*/
                if (mStatusCodes.isNullOrEmpty())
                    mBinding.spnStatus.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mStatusCodes)
                    mBinding.spnStatus.adapter = adapter
                    if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY) {
                        var position = 0
                        mStatusCodes.forEachIndexed { index, comStatusCode ->
                            if (Constant.COM_PropertyMaster_Inactive == comStatusCode.statusCode) {
                                position = index
                                return@forEachIndexed
                            }
                        }
                        if (position != -1)
                            mBinding.spnStatus.setSelection(position)
                    }
                }

                setSpinner()
                updatemArrivedRequestCount()
//                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
//                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                updatemArrivedRequestCount()
            }
        })

    }

    @Synchronized
    fun updatemArrivedRequestCount() {
        mArrivedRequestCount++
        if (mArrivedRequestCount >= 3) {
            mListener?.dismissDialog()
        }
    }

    private fun setSpinner() {
        if (mStorePropertyData != null) {
            mAdministrativeOffices.let {
                for ((index, obj) in it.withIndex()) {
                    if (mStorePropertyData?.userOrgBranchID == obj.userOrgBranchID!!) {
                        mBinding.spnAdministrationOffice.setSelection(index)
                        break
                    }
                }
            }

            mPropertyTypes.let {
                for ((index, obj) in it.withIndex()) {
                    if (mStorePropertyData?.propertyTypeID == obj.propertyTypeID!!) {
                        mBinding.spnPropertyType.setSelection(index)
                        break
                    }
                }
            }
            mPropertySplitList.let {
                for ((index, obj) in it.withIndex()) {
                    if (mStorePropertyData?.propertySplitCode == obj.code!!) {
                        mBinding.spnPropertySplit.setSelection(index)
                        break
                    }
                }
            }
            mElectricityConsumptions.let {
                for ((index, obj) in it.withIndex()) {
                    if (mStorePropertyData?.electricityConsumptionID == obj.electricityConsumptionID!!) {
                        mBinding.spnElectricityConsumption.setSelection(index)
                        break
                    }
                }
            }
            mPhasesOfElectricity.let {
                for ((index, obj) in it.withIndex()) {
                    if (mStorePropertyData?.phaseOfElectricityID == obj.phaseOfElectricityID!!) {
                        mBinding.spnPhaseOfElectricity.setSelection(index)
                        break
                    }
                }
            }
            mWaterConsumptions.let {
                for ((index, obj) in it.withIndex()) {
                    if (mStorePropertyData?.waterConsumptionID == obj.waterConsumptionID!!) {
                        mBinding.spnWaterConsumption.setSelection(index)
                        break
                    }
                }
            }
            setLandProperty()

            setParentProperty()

            registrationTypes.let {
                for ((index, obj) in it.withIndex()) {
                    if (mStorePropertyData?.propertyRegistrationTypeID != null && obj.propertyRegistrationTypeID != null && mStorePropertyData?.propertyRegistrationTypeID!! == obj.propertyRegistrationTypeID!!) {
                        mBinding.spnRegistrationType.setSelection(index)
                        break
                    }
                }
            }
//            measurementUnits.let {
//                for ((index, obj) in it.withIndex()) {
//                    if (mStorePropertyData?.unitCode == obj.unitCode!!) {
//                        mBinding.spnMeasureUnit.setSelection(index)
//                        break
//                    }
//                }
//            }
            /*  landUseTypes.let {
                  for ((index, obj) in it.withIndex()) {
                      if (mStorePropertyData?.landUseTypeID == obj.LandUseTypeID!!) {
                          mBinding.spnLandUseType.setSelection(index)
                          break
                      }
                  }
              }*/
            mStatusCodes.let {
                for ((index, obj) in it.withIndex()) {
                    if (mStorePropertyData?.statusCode == obj.statusCode!!) {
                        mBinding.spnStatus.setSelection(index)
                        break
                    }
                }
            }
        }
        bindData()
    }

    private fun setParentProperty() {
        mProperties.let {
            for ((index, obj) in it.withIndex()) {
                if (mStorePropertyData?.parentPropertyID == obj.proprtyid!!) {
                    mBinding.edtParentProperty.setText(obj.propertySycotaxID)
                    mBinding.edtSurveyNo.isEnabled = false
                    mBinding.edtBlock.isEnabled = false
                    mBinding.edtDoorNo.isEnabled = false

                    mBinding.spnPropertySplit.isClickable = true
                    setParentGeoLocationMarkers(obj.geoLocationArea)
                    if (obj.landPropertyID != null) {
                        mBinding.edtLandProperty.isEnabled = false
                        setLandProperty(obj.landPropertyID)
                    } else {
                        mBinding.edtLandProperty.setText("")
                    }
                    break
                }
            }
        }
    }

    private fun setParentGeoLocationMarkers(mGeoLocationArea: String?) {
        val arrayListTutorialType = object : TypeToken<ArrayList<GeoFenceLatLong>>() {}.type
        if (mGeoLocationArea != null && !TextUtils.isEmpty(mGeoLocationArea))
            mParentGeoFenceLatLong = Gson().fromJson(mGeoLocationArea, arrayListTutorialType)
        bindMarkerData()
    }

    private fun setLandGeoLocationMarkers(mGeoLocationArea: String?) {
        val arrayListTutorialType = object : TypeToken<ArrayList<GeoFenceLatLong>>() {}.type
        if (mGeoLocationArea != null && !TextUtils.isEmpty(mGeoLocationArea))
            mLandGeoFenceLatLong = Gson().fromJson(mGeoLocationArea, arrayListTutorialType)
        bindMarkerData()
    }

    private fun bindData() {
        mBinding.edtSycoTaxID.setText(mSycoTaxID ?: "")

        /* mBinding.edtMonthlyRentAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
             if (hasFocus) {
                 val text: String = mBinding.edtMonthlyRentAmount.text.toString()
                 if (text.isNotEmpty())
                     mBinding.edtMonthlyRentAmount.setText("${currencyToDouble(text)}")
             } else {
                 //this if condition is true when edittext lost focus...
                 //check here for number is larger than 10 or not
                 var cost = mBinding.edtMonthlyRentAmount.text.toString()
                 if (!TextUtils.isEmpty(cost)) {
                     val enteredText: Double = mBinding.edtMonthlyRentAmount.text.toString().toDouble()
                     mBinding.edtMonthlyRentAmount.setText("${formatWithPrecision(enteredText)}")
                 }
             }
         }*/

        mBinding.edtMonthlyRentAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtMonthlyRentAmount.text.toString()
                if (text?.isNotEmpty()!!) {
                    mBinding.edtMonthlyRentAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                    mBinding.edtMonthlyRentAmount.setText("${currencyToDouble(text)}");
                }
            } else {
                //this if condition is true when edittext lost focus...
                //check here for number is larger than 10 or not
                var cost = mBinding.edtMonthlyRentAmount.text.toString();
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = mBinding.edtMonthlyRentAmount.text.toString().toDouble()
                    mBinding.edtMonthlyRentAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8 + 15))
                    mBinding.edtMonthlyRentAmount.setText("${formatWithPrecision(enteredText)}")
                }
            }
        }

        if (mStorePropertyData != null) {
            mBinding.vmStoreProperty = mStorePropertyData
            mBinding.vmGeoAddress = mGeoAddress
            setGeoData(mStorePropertyData?.geoLocationArea)
            Handler(Looper.getMainLooper()).postDelayed({
                if (mStorePropertyData?.estimatedTax == null || mStorePropertyData?.estimatedTax == 0.0) {
                    calculate(showLoader = false)
                }

            }, 2 * 1000)
        }

        Handler().postDelayed({
            mBinding.edtLength.addTextChangedListener {
                calculateArea()
            }

            mBinding.edtWidth.addTextChangedListener {
                calculateArea()
            }


            mBinding.edtMonthlyRentAmount.addTextChangedListener {
                if (!(it.toString()).contains(MyApplication.getPrefHelper().currencySymbol)) {
                    mBinding.edtEstimatedRentAmount.setText("")
                }
            }

        }, 500)

        getPropertyDueSummary()


        if (/*mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY ||*/ mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY) {
//            mListener?.showProgressDialog()
            APICall.getPropertyOwnersDetails(mStorePropertyData?.propertyID
                    ?: 0, object : ConnectionCallBack<PropertyOwners> {
                override fun onSuccess(response: PropertyOwners) {
//                    mListener?.dismissDialog()
                    propertyOwners = response
                    mBinding.edtFromDate.setText(displayFormatDate(response.propertyOwners.get(0).fromDate))
                    mBinding.edtToDate.setText(displayFormatDate(response.propertyOwners.get(0).toDate))
                    mBinding.edtPropertyRegistrationNo.setText(response.propertyOwners.get(0).registrationNo)
                    mBinding.edtNomineeName.setText(response.propertyOwners.get(0).propertyowners.get(0).nomineeAccountName)

                    mPropertyExemptionReasons.let {
                        for ((index, obj) in it.withIndex()) {
                            if (response.propertyOwners.get(0).propertyExemptionReasonID == obj.propertyExemptionReasonID) {
                                mBinding.spnExemptionReason.setSelection(index)
                                break
                            }
                        }
                    }

                    mComboStaticValues.let {
                        for ((index, obj) in it.withIndex()) {
                            if (response.propertyOwners.get(0).propertyowners.get(0).cmbval == obj.comboValue) {
                                mBinding.spnRelation.setSelection(index)
                                break
                            }
                        }
                    }
                }

                override fun onFailure(message: String) {
//                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }

        //todo Commented for this release(13/01/2022)
//        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND) {
//            getInvoiceCount4Tax()
//        }
    }

    private fun getInvoiceCount4Tax() {
        val currentDue = CheckCurrentDue()
        currentDue.accountId = mAccountId
        currentDue.vchrno = mStorePropertyData?.propertyID
        currentDue.taxRuleBookCode = mTaxRuleBookCode
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response > 0) {
                    setEditActionForSave(false)
                } else {
                    setEditAction(true)
                }
            }

            override fun onFailure(message: String) {
            }
        })
    }

    fun calculateArea() {
        var length = 1.0
        var width = 1.0

        if (mBinding.edtLength.text.toString().trim().isNotEmpty()) {
            length = if (mBinding.edtLength.text.toString().trim().contains(","))
                (currencyToDouble(mBinding.edtLength.text.toString().trim())!!.toDouble())!!.toDouble()
            else
                getDecimalVal(mBinding.edtLength.text.toString().trim())
        }
        if (mBinding.edtWidth.text.toString().trim().isNotEmpty())
            width = if (mBinding.edtWidth.text.toString().trim().contains(","))
                (currencyToDouble(mBinding.edtWidth.text.toString().trim())!!.toDouble())!!.toDouble()
            else
                getDecimalVal(mBinding.edtWidth.text.toString().trim())

        if (mBinding.edtLength.text.isNullOrEmpty() && mBinding.edtWidth.text.isNullOrEmpty()) {
            mBinding.edtArea.setText("")
        } else {
            mBinding.edtArea.setText(formatWithPrecisionCustomDecimals((length * width).toString(), false, 3))
        }
    }

    private fun calculateArea(mGeoFenceLatLong: ArrayList<GeoFenceLatLong>): String {
        val polygonList: MutableList<LatLng> = ArrayList()
        for (latLng in mGeoFenceLatLong)
            polygonList.add(LatLng(latLng.latitude, latLng.longitude))
        Log.e(TAG, "computeArea " + SphericalUtil.computeArea(polygonList))
        var text = ""
        if (SphericalUtil.computeArea(polygonList) > 0) {
            text = Constant.df.format(SphericalUtil.computeArea(polygonList)) + " " + getString(R.string.meter_square)
//            mBinding.tvArea.text = text
        } else {
//            mBinding.tvArea.text = "---"
        }
        return text
    }

    private fun setGeoData(geoLocationArea: String?) {
        if (!TextUtils.isEmpty(geoLocationArea)) {
            val arrayListTutorialType = object : TypeToken<ArrayList<GeoFenceLatLong>>() {}.type
            mGeoFenceLatLong = Gson().fromJson(geoLocationArea, arrayListTutorialType)
            bindMarkerData()
        }
    }

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = java.util.ArrayList()
        var index = -1
        var countryCode: String? = ""
        if (mGeoAddress != null) countryCode = mGeoAddress!!.countryCode
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
        var states: MutableList<COMStateMaster> = java.util.ArrayList()
        var index = -1
        var stateID = 100497
        if (mGeoAddress != null && mGeoAddress!!.stateID != null) stateID = mGeoAddress!!.stateID!!
        if (TextUtils.isEmpty(countryCode)) states = java.util.ArrayList() else {
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
        var cities: MutableList<VUCOMCityMaster> = java.util.ArrayList()
        var index = -1
        var cityID = 100312093
        if (mGeoAddress != null && mGeoAddress!!.cityID != null) cityID = mGeoAddress!!.cityID!!
        if (stateID <= 0) cities = java.util.ArrayList() else {
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
        var zones: MutableList<COMZoneMaster> = java.util.ArrayList()
        var index = 0
        var zoneName: String? = ""
        if (mGeoAddress != null && mGeoAddress!!.zone != null) zoneName = mGeoAddress!!.zone
        if (cityID <= 0) zones = java.util.ArrayList() else {
            for (zone in mZones) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone) index = zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnZone.adapter = zoneArrayAdapter
            mBinding.spnZone.setSelection(index)
            filterSectors(zones[index].zoneID!!)
        } else {
            mBinding.spnZone.adapter = null
            filterSectors(0)
        }
    }

    private fun filterSectors(zoneID: Int) {
        var sectors: MutableList<COMSectors?> = java.util.ArrayList()
        var index = 0
        var sectorID = 0
        if (mGeoAddress != null && mGeoAddress!!.sectorID != null) sectorID = mGeoAddress!!.sectorID!!
        if (zoneID <= 0) sectors = java.util.ArrayList() else {
            for (sector in mSectors) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId) index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            if(isActionEnabled) mBinding.spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnSector.adapter = sectorArrayAdapter
            mBinding.spnSector.setSelection(index)
        } else {
            mBinding.spnSector.adapter = null
            mBinding.spnSector.isEnabled = false
        }


    }

    private fun fetchComfortLevels() {
        mListener?.showProgressDialog()
        val getPropertyComfortLevels = GetPropertyComfortLevels()
        var electricityConsumptionID: Int? = null
        if (mBinding.spnElectricityConsumption.selectedItem != null) {
            val electricityConsumption = mBinding.spnElectricityConsumption.selectedItem as COMElectricityConsumption?
            electricityConsumption?.electricityConsumptionID?.let {
                if (it != -1)
                    electricityConsumptionID = it
            }
        }
        var phaseOfElectricityID: Int? = null
        if (mBinding.spnPhaseOfElectricity.selectedItem != null) {
            val phaseOfElectricity = mBinding.spnPhaseOfElectricity.selectedItem as COMPhaseOfElectricity?
            phaseOfElectricity?.phaseOfElectricityID?.let {
                if (it != -1)
                    phaseOfElectricityID = it
            }
        }
        var waterConsumptionID: Int? = null
        if (mBinding.spnElectricityConsumption.selectedItem != null) {
            val waterConsumption = mBinding.spnWaterConsumption.selectedItem as COMWaterConsumption?
            waterConsumption?.waterConsumptionID?.let {
                if (it != -1)
                    waterConsumptionID = it
            }
        }
        getPropertyComfortLevels.electricityConsumptionID = electricityConsumptionID
        getPropertyComfortLevels.phaseOfElectricityID = phaseOfElectricityID
        getPropertyComfortLevels.waterConsumptionID = waterConsumptionID
        APICall.getPropertyComfortLevels(getPropertyComfortLevels, object : ConnectionCallBack<PropertyComfortLevels> {
            override fun onSuccess(response: PropertyComfortLevels) {
                val list: ArrayList<PropertyComfortLevel> = arrayListOf()
                response.propertyComfortLevels?.let {
                    list.add(it)
                }
                if (list.isNullOrEmpty()) {
                    mBinding.spnComfortLevel.adapter = null
                    //ToDo : Uncomment this line for Onsite team issue
                    mListener?.showToast(getString(R.string.not_proper_combination))
                } else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
                    mBinding.spnComfortLevel.adapter = adapter

//                    if (mStorePropertyData != null && mStorePropertyData!!.comfortLevelID != null) {
//                        calculate()
//                    }
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun setEvents() {
        mBinding.edtParentProperty.setOnClickListener {
            showSearchAlertDialog(requireContext(), mProperties as ArrayList<Any>?, object : CustomDialogAdapter.Listener {
                override fun onAdapterItemClick(dialog: AlertDialog, any: Any?) {
                    mGeoFenceLatLong.clear()
                    mParentGeoFenceLatLong.clear()
                    mLandGeoFenceLatLong.clear()
                    val data = any as VUCOMProperty
                    if (data.propertyName != getString(R.string.select)) {
                        mBinding.edtParentProperty.setText(data.propertySycotaxID)
                        /*  mBinding.edtSurveyNo.setText("")
                          mBinding.edtSurveyNo.isEnabled = false
                          mBinding.edtBlock.setText("")
                          mBinding.edtBlock.isEnabled = false
                          mBinding.edtDoorNo.setText("")
                          mBinding.edtDoorNo.isEnabled = false

                          mBinding.spnPropertySplit.isClickable = true*/
                        if (data.propertySplitCode != null) {
                            when (data.propertySplitCode) {
                                Constant.LOT -> {
                                    mBinding.edtBlock.isEnabled = false

                                    mBinding.edtDoorNo.isEnabled = true
                                    mBinding.edtSurveyNo.isEnabled = true
                                }
                                Constant.SURVEYNO -> {
                                    mBinding.edtSurveyNo.isEnabled = false

                                    mBinding.edtBlock.isEnabled = true
                                    mBinding.edtDoorNo.isEnabled = true
                                }
                                Constant.PARCEL -> {
                                    mBinding.edtDoorNo.isEnabled = false

                                    mBinding.edtSurveyNo.isEnabled = true
                                    mBinding.edtBlock.isEnabled = true
                                }
                            }
                        }

                        if (data.landPropertyID != null) {
                            setLandProperty(data.landPropertyID)
                        } else {
                            mBinding.edtLandProperty.setText("")
                        }
                        mBinding.edtLandProperty.isEnabled = false
                        setParentGeoLocationMarkers(data.geoLocationArea)
                    } else {
                        mBinding.edtParentProperty.setText("")
                        mBinding.edtLandProperty.setText("")

                        mBinding.edtSurveyNo.isEnabled = true
                        mBinding.edtBlock.isEnabled = true
                        mBinding.edtDoorNo.isEnabled = true
                        /*mBinding.edtSurveyNo.isEnabled = true
                        mBinding.edtBlock.isEnabled = true
                        mBinding.edtDoorNo.isEnabled = true
                        mBinding.edtLandProperty.setText("")
                        mBinding.edtLandProperty.isEnabled = true

                        mBinding.spnPropertySplit.setSelection(0)
                        mBinding.spnPropertySplit.isClickable = false*/

                        mBinding.edtLandProperty.isEnabled = true
                        if (mStorePropertyData != null) {
                            setLandProperty()
                            setParentProperty()
                            setGeoData(mStorePropertyData?.geoLocationArea)
                        } else {
                            bindMarkerData()
                        }
                    }
                    dialog.dismiss()
                }
            })

        }
        mBinding.edtLandProperty.setOnClickListener {
            showSearchAlertDialog(requireContext(), mLands as ArrayList<Any>?, object : CustomDialogAdapter.Listener {
                override fun onAdapterItemClick(dialog: AlertDialog, any: Any?) {
                    mGeoFenceLatLong.clear()
                    mParentGeoFenceLatLong.clear()
                    mLandGeoFenceLatLong.clear()
                    val data = any as VUCOMLand
                    if (data.propertyName != getString(R.string.select)) {
                        mBinding.edtLandProperty.setText(data.propertySycotaxID)
                        data.proprtyid.let {
                            setLandProperty(it.toString())
                        }
                    } else {
                        mBinding.edtLandProperty.setText("")
                        if (mStorePropertyData != null) {
                            setLandProperty()
                            setParentProperty()
                            setGeoData(mStorePropertyData?.geoLocationArea)
                        } else {
                            bindMarkerData()
                        }
                    }
                    dialog.dismiss()
                }
            })
        }

//        mBinding.spnParentProperty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                if (p2 != 0) {
//                    mBinding.edtSurveyNo.setText("")
//                    mBinding.edtSurveyNo.isEnabled = false
//                    mBinding.edtBlock.setText("")
//                    mBinding.edtBlock.isEnabled = false
//                    mBinding.edtDoorNo.setText("")
//                    mBinding.edtDoorNo.isEnabled = false
//                } else {
//                    mBinding.edtSurveyNo.isEnabled = true
//                    mBinding.edtBlock.isEnabled = true
//                    mBinding.edtDoorNo.isEnabled = true
//                }
//
//            }
//        }

        mBinding.spnWaterConsumption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                fetchComfortLevels()
            }
        }

        mBinding.spnPhaseOfElectricity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                fetchComfortLevels()
            }
        }

        mBinding.spnElectricityConsumption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                fetchComfortLevels()
            }
        }

        mBinding.spnPropertySplit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val obj = p0?.selectedItem as ComComboStaticValues

                splitObjSelected = obj
                /*if (obj.code == Constant.LOT) {


                } else if (obj.code == Constant.SURVEYNO) {
                    mBinding.edtBlock.isEnabled = true
                    mBinding.edtSurveyNo.isEnabled = false
                    mBinding.edtDoorNo.isEnabled = true

                    mBinding.edtSurveyNo.setText("")
                } else if (obj.code == Constant.PARCEL) {
                    mBinding.edtBlock.isEnabled = true
                    mBinding.edtSurveyNo.isEnabled = true
                    mBinding.edtDoorNo.isEnabled = false
                    mBinding.edtDoorNo.setText("")
                }*/
            }
        }

        mBinding.spnPropertyType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val obj = p0?.selectedItem as COMPropertyTypes
                if (obj.taxRuleBookCode == Constant.KEY_RES_PROP) {
                    mBinding.comPropertyTypeLL.visibility = View.GONE
//                    mBinding.resPropertyTypeLL.visibility = View.VISIBLE
                    mBinding.llIsApartment.visibility = View.VISIBLE
                    if (obj.isApartment == Constant.ACTIVE_Y) {
                        mBinding.flTaxCollection.visibility = View.GONE
                        mBinding.resPropertyTypeLL.visibility = View.GONE
                        mBinding.edtFloorNumber.visibility = View.GONE
                        mBinding.chkIsApartment.isEnabled = false
                        mBinding.chkIsApartment.isChecked = true
                    } else {
                        mBinding.flTaxCollection.visibility = View.VISIBLE
                        mBinding.resPropertyTypeLL.visibility = View.VISIBLE
                        mBinding.edtFloorNumber.visibility = View.VISIBLE
                        mBinding.chkIsApartment.isEnabled = false
                        mBinding.chkIsApartment.isChecked = false
                    }

                } else if (obj.taxRuleBookCode == Constant.KEY_COM_PROP) {
                    mBinding.comPropertyTypeLL.visibility = View.VISIBLE
                    mBinding.resPropertyTypeLL.visibility = View.GONE
                    mBinding.edtFloorNumber.visibility = View.GONE
                    mBinding.llIsApartment.visibility = View.GONE

                    obj.mRateCycle.let {
                        if (!TextUtils.isEmpty(it) || it != "null") {
                            mBinding.monthlyRentAmount.hint = getString(R.string.estimated_rent_amount) + "/" + it
                        } else {
                            mBinding.monthlyRentAmount.hint = getString(R.string.estimated_rent_amount)
                        }
                    }
                    obj.mPaymentCycle.let {
                        if (!TextUtils.isEmpty(it) || it != "null") {
                            mBinding.yearlyRentAmount.hint = getString(R.string.estimated_rent_amount) + "/" + it
                        } else {
                            mBinding.yearlyRentAmount.hint = getString(R.string.estimated_rent_amount)
                        }
                    }
                } else {
                    mBinding.comPropertyTypeLL.visibility = View.VISIBLE
                    mBinding.resPropertyTypeLL.visibility = View.VISIBLE
                    mBinding.llIsApartment.visibility = View.GONE
                    obj.mRateCycle.let {
                        mBinding.monthlyRentAmount.hint = getString(R.string.estimated_rent_amount) + "/" + it
                    }
                    obj.mPaymentCycle.let {
                        mBinding.yearlyRentAmount.hint = getString(R.string.estimated_rent_amount) + "/" + it
                    }
                }
                obj.unit.let {
                    mBinding.edtMeasureUnit.setText(it)
                }

//                if (obj.taxRuleBookCode == Constant.KEY_RES_PROP && obj.isApartment == Constant.ACTIVE_Y) {
//                    mBinding.flTaxCollection.visibility = View.GONE
//                    mBinding.resPropertyTypeLL.visibility = View.GONE
//                    mBinding.edtFloorNumber.visibility = View.GONE
//
//                }
//                else if (obj.taxRuleBookCode == Constant.KEY_RES_PROP && obj.isApartment == Constant.ACTIVE_N) {
//                    mBinding.flTaxCollection.visibility = View.VISIBLE
//                    mBinding.resPropertyTypeLL.visibility = View.VISIBLE
//                    mBinding.edtFloorNumber.visibility = View.VISIBLE
//
//                }
//                else {
//                    mBinding.flTaxCollection.visibility = View.VISIBLE
//                    mBinding.resPropertyTypeLL.visibility = View.VISIBLE
//                    mBinding.edtFloorNumber.visibility = View.GONE
//                    mBinding.edtFloorNo.setText("")
//                }

            }
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

        mBinding.llPropertyImages.setOnClickListener(this)
        mBinding.llPropertyOwner.setOnClickListener(this)
        mBinding.llPropertyPlanImages.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
        mBinding.llInitialOutstanding.setOnClickListener(this)
        mBinding.tvFixedExpenses.setOnClickListener(this)
        mBinding.btnApprove.setOnClickListener(this)
        mBinding.btnReject.setOnClickListener(this)
        mBinding.edtNomineeName.setOnClickListener(this)
        mBinding.tvCreateNominee.setOnClickListener(this)
        mBinding.llTreeView.setOnClickListener(this)
        mBinding.btnRentCalculate.setOnClickListener(this)
        mBinding.llParentPlanImage.setOnClickListener(this)
        mBinding.llParentImage.setOnClickListener(this)
        mBinding.llParentDocuments.setOnClickListener(this)

        locationHelper = LocationHelper(requireContext(), mBinding.view, activity = requireActivity())
        locationHelper?.fetchLocation()

        val supportMapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment

        supportMapFragment.getMapAsync { map ->
            googleMap = map
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                val success = googleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))
                if (!success) {
                    Log.e("LocateDialogFragment", "Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                LogHelper.writeLog(exception = e)
//                Log.e("LocateDialogFragment", "Can't find style. Error: ", e)
            }
            if (hasPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@getMapAsync
                }
                map?.isMyLocationEnabled = true
            }
            googleMap?.uiSettings?.isZoomControlsEnabled = true

            locationHelper?.setListener(object : LocationHelper.Location {
                override fun found(latitude: Double, longitude: Double) {
//                    mListener?.dismissDialog()
                    latLong = LatLng(latitude, longitude)
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 20.0f))
                    if (mStorePropertyData != null) {
                        setGeoData(mStorePropertyData?.geoLocationArea)
                    }
                }

                override fun start() {
//                    mListener?.showProgressDialog()
                }
            })
        }

        mBinding.btnAddGeo.setOnClickListener(this)
        mBinding.btnSave.setOnClickListener(this)
        mBinding.btnCalculate.setOnClickListener(this)

        mBinding.edtRegistrationDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtRegistrationDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtRegistrationDate.setText(getDate(setCalendarText(Calendar.JANUARY, 1), displayDateFormat))

        mBinding.edtConstructedDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtConstructedDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtConstructedDate.setText(getDate(setCalendarText(Calendar.JANUARY, 1), displayDateFormat))

        if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY) {
            mBinding.llSpnLandUseType.visibility = View.GONE
            mBinding.viewLandUseType.visibility = View.GONE
        }

        mBinding.edtFromDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtFromDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtToDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtToDate.setMaxDate(Calendar.getInstance().timeInMillis)

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
    }

    private fun setLandProperty(landPropertyID: String?) {
        mLands.let {
            for (obj in it) {
                if (landPropertyID?.toInt() == obj.proprtyid!!) {
                    mBinding.edtLandProperty.setText(obj.propertySycotaxID)
                    setLandGeoLocationMarkers(obj.geoLocationArea)
                    break
                }
            }
        }
    }

    private fun setLandProperty() {
        mLands.let {
            for ((index, obj) in it.withIndex()) {
                if (mStorePropertyData?.landPropertyID == obj.proprtyid!!) {
                    mBinding.edtLandProperty.setText(obj.propertySycotaxID)
                    setLandGeoLocationMarkers(obj.geoLocationArea)
                    break
                }
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                R.id.llPropertyImages -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 -> {
                            val fragment = PropertyImageMasterFragment()
                            val bundle = Bundle()
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                            bundle.putInt(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID
                                    ?: 0)
                            fragment.arguments = bundle
                            fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_IMAGE)
                            mListener?.addFragment(fragment, true)
                        }
                        validateView() -> {
                            storePropertyData(prepareData(view), view)
                        }
                        else -> {
                        }
                    }

                }
                R.id.llPropertyPlanImages -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 -> {
                            if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY && !TextUtils.isEmpty(mBinding.txtNoOfPlanImages.text) && mBinding.txtNoOfPlanImages.text.toString().toInt() == 0) {
                                if (mStorePropertyData != null && mStorePropertyData?.isInvoiceGenerated!!) {
                                    mListener?.screenMode = Constant.ScreenMode.VIEW
                                } else {
                                    mListener?.screenMode = Constant.ScreenMode.ADD
                                }
                            }
                            val fragment = PropertyPlanImageMasterFragment()
                            val bundle = Bundle()
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                            bundle.putInt(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID
                                    ?: 0)
                            fragment.arguments = bundle
                            fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_PLAN_IMAGE)
                            mListener?.addFragment(fragment, true)
                        }
                        validateView() -> {
                            storePropertyData(prepareData(view), view)
                        }
                        else -> {
                        }
                    }

                }
                R.id.btnCalculate -> {
                    calculate()

                }
                R.id.llPropertyOwner -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 -> {
                            val fragment = PropertyOwnerMasterFragment()
                            val bundle = Bundle()
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                            bundle.putSerializable(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID)
                            fragment.arguments = bundle
                            fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_OWNER)
                            mListener?.addFragment(fragment, true)
                        }
                        validateView() -> {
                            storePropertyData(prepareData(view), view)
                        }
                        else -> {

                        }
                    }

                }
                R.id.llTreeView -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 -> {
                            val intent = Intent(context, TreeViewActivity::class.java)
                            intent.putExtra(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID
                                    ?: 0)
                            intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                            startActivity(intent)
                        }
                        /*validateView() -> {
                            storePropertyData(prepareData(view), view)
                        }*/
                        else -> {

                        }
                    }
                }
                R.id.llParentPlanImage -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 -> {
                            /*if (mBinding.txtParentPlanImageCount.text.isEmpty() || mBinding.txtParentPlanImageCount.text.trim() == "0"){
                                return@let
                            }*/
                            val intent = Intent(context, ParentPropertyPlanImagesActivity::class.java)
                            intent.putExtra(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID
                                    ?: 0)
                            intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                            intent.putExtra(Constant.KEY_PARENT_TYPE, getString(R.string.parent_property_plan_documents))
                            startActivity(intent)
                        }
                        validateView() -> {
                            storePropertyData(prepareData(view), view)
                        }
                        else -> {

                        }
                    }
                }
                R.id.llParentImage -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 -> {
                            /*if (mBinding.txtParentImageCOunt.text.isEmpty() || mBinding.txtParentImageCOunt.text.trim() == "0"){
                                return@let
                            }*/
                            val intent = Intent(context, ParentPropertyPlanImagesActivity::class.java)
                            intent.putExtra(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID
                                    ?: 0)
                            intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                            intent.putExtra(Constant.KEY_PARENT_TYPE, getString(R.string.parent_property_images))
                            startActivity(intent)
                        }
                        validateView() -> {
                            storePropertyData(prepareData(view), view)
                        }
                        else -> {

                        }
                    }
                }
                R.id.llParentDocuments -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 -> {
                            /*if (mBinding.txtParentDocumentCount.text.isEmpty() || mBinding.txtParentDocumentCount.text.trim() == "0"){
                                return@let
                            }*/
                            val intent = Intent(context, ParentPropertyPlanImagesActivity::class.java)
                            intent.putExtra(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID
                                    ?: 0)
                            intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                            intent.putExtra(Constant.KEY_PARENT_TYPE, getString(R.string.parent_documents_property))
                            startActivity(intent)
                        }
                        validateView() -> {
                            storePropertyData(prepareData(view), view)
                        }
                        else -> {

                        }
                    }
                }
                R.id.btnSave -> {
                    /*when {
                        validateView() -> {
                            storePropertyData(prepareData())
                        }
                        else -> {

                        }
                    }*/
                    when {
                        /* mBinding.txtPropertyOwnerCount.text.toString() == "0" -> {
                             mListener?.showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.property_ownership))
                             return
                         }*/
                        mBinding.txtNoOfPlanImages.text.toString() == "0" -> {
                            mListener?.showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.property_plan_documents))
                            return
                        }
                        else -> {
                            when {
                                validateView(view) -> {
                                    var areaVal = 0.0
                                    var mapVal = 0.0
                                    if (!TextUtils.isEmpty(mBinding.edtArea.text)) {
                                        try {
                                            areaVal = if (mBinding.edtArea.text.toString().trim().contains(","))
                                                (currencyToDouble(mBinding.edtArea.text.toString().trim())!!.toDouble())
                                            else
                                                mBinding.edtArea.text.toString().trim().toDouble()
                                        } catch (e: Exception) {
                                            LogHelper.writeLog(exception = e)
                                        }
                                    }
                                    if (!TextUtils.isEmpty(mBinding.edtMapArea.text)) {
                                        val value = mBinding.edtMapArea.text.toString()
                                        val mapAreaWithoutM2 = value.substring(0, value.length - 2)
                                        mapVal = if (value.contains(",")) {
                                            currencyToDouble(mapAreaWithoutM2)?.toDouble()!!
                                        } else {
                                            mapAreaWithoutM2.toDouble()
                                        }
                                    }
                                    if (mapVal > areaVal)
                                        showAlertForSave(view)
                                    else
                                        storePropertyData(prepareData(view))
                                }
                                else -> {

                                }
                            }
                        }
                    }

                }
                R.id.btnAddGeo -> {
                    if (TextUtils.isEmpty(mBinding.spnPropertyType.selectedItem?.toString()) || mBinding.spnPropertyType.selectedItemPosition == 0) {
                        mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.property_type))
                        mBinding.spnPropertyType.requestFocus()
                    }else {
                        val intent = Intent(context, GeoMapSearchActivity::class.java)
                        intent.putExtra(Constant.KEY_GEO_AREA_LATLONG, mGeoFenceLatLong)
                        intent.putExtra(Constant.KEY_PARENT_GEO_AREA_LATLONG, mParentGeoFenceLatLong)
                        intent.putExtra(Constant.KEY_LAND_GEO_AREA_LATLONG, mLandGeoFenceLatLong)
                        intent.putExtra(Constant.KEY_FROM_SCREEN, "0")
                        intent.putExtra(Constant.KEY_NAVIGATION_MENU, Constant.NavigationMenu.NAVIGATION_ONBOARDING)
                        startActivityForResult(intent, Constant.REQUEST_CODE_PROPERTY_GEO)
                    }
                }
                R.id.llDocuments -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 -> {
                            val fragment = DocumentsMasterFragment()

                            //region SetArguments
                            val bundle = Bundle()
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                            bundle.putInt(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID
                                    ?: 0)
                            fragment.arguments = bundle
                            //endregion

                            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                            mListener?.showToolbarBackButton(R.string.documents)
                            mListener?.addFragment(fragment, true)
                        }
                        validateView() -> {
                            storePropertyData(prepareData(view), view)
                        }
                        else -> {
                        }
                    }
                }
                R.id.llNotes -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 -> {
                            val fragment = NotesMasterFragment()
                            val bundle = Bundle()
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                            bundle.putInt(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID
                                    ?: 0)
                            fragment.arguments = bundle
                            fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)

                            mListener?.showToolbarBackButton(R.string.notes)
                            mListener?.addFragment(fragment, true)
                        }
                        validateView() -> {
                            storePropertyData(prepareData(view), view)
                        }
                        else -> {
                        }
                    }
                }
                R.id.llInitialOutstanding -> {
                    when {
                        mStorePropertyData != null && mStorePropertyData?.propertyID != 0 && isPropertyOwnerAdded() -> {
                            val fragment = OutstandingsMasterFragment()
                            val bundle = Bundle()


                            bundle.putInt(Constant.KEY_VOUCHER_NO, mStorePropertyData?.propertyID
                                    ?: 0)
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                            if (mBinding.spnPropertyType.selectedItem != null) {
                                if (-1 != (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).propertyTypeID)
                                    (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).productCode?.let {
                                        bundle.putString(Constant.KEY_PRODUCT_CODE, it)
                                    }
                            }
                            fragment.arguments = bundle
                            fragment.setTargetFragment(this, Constant.REQUEST_CODE_OUT_STANDING_ENTRY)

                            mListener?.showToolbarBackButton(R.string.title_initial_outstandings)
                            mListener?.addFragment(fragment, true)
                        }
                        validateView(view) -> {
                            storePropertyData(prepareData(view), view)
                        }
                        else -> {
                        }
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
                R.id.btnApprove -> {
                    showRemarksPopUp(true)
                }

                R.id.btnReject -> {
                    showRemarksPopUp(false)
                }
                R.id.tvCreateNominee -> {
                    val fragment = BusinessOwnerEntryFragment()
                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_CREATE_CITIZEN)
                    fragment.arguments = bundle
                    //endregion
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_NOMINEE)

                    mListener?.showToolbarBackButton(R.string.property_owner)
                    mListener?.addFragment(fragment, true)
                }
                R.id.edtNomineeName -> {
                    showCustomers()
                }
                R.id.btnRentCalculate -> {
                    val estimatedRentAmount: String = mBinding.edtMonthlyRentAmount.text.toString()
                    if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode == Constant.KEY_COM_PROP) {
                        if (TextUtils.isEmpty(estimatedRentAmount)) {
                            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.estimated_rent_amount))
                            mBinding.edtMonthlyRentAmount.requestFocus()
                            return
                        } else {
                        }
                    } else {
                        if (TextUtils.isEmpty((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode)) {
                            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.property_type))
                            return
                        }
                    }

                    getEstimatedRent(currencyToDouble(estimatedRentAmount).toString())
                }

                else -> {
                }
            }
        }
    }

    private fun showAlertForSave(mView: View) {

        mListener?.showAlertDialog(R.string.map_area_greater_than_area,
                R.string.save,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    storePropertyData(prepareData(mView))
                }, R.string.no,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                })
    }

    private fun setMode() {

    }

    private fun getEstimatedRent(estimatedRentAmount: String?) {
        mListener?.showProgressDialog()
        val gerap = GetEstimatedRentAmountPayload()
        gerap.proptypeid = (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).propertyTypeID
        gerap.rentamt = estimatedRentAmount?.toDouble()
        if (!TextUtils.isEmpty(mBinding.edtConstructedDate.text.toString())) {
            gerap.strtdt = serverFormatDate(mBinding.edtConstructedDate.text.toString())
        }
        APICall.getEstimatedRentAmount(gerap, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mListener?.dismissDialog()
                if (response != null) {
                    mBinding.edtEstimatedRentAmount.setText(formatWithPrecision(response.toString()))
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun showCustomers() {
        val fragment = BusinessOwnerSearchFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PROPERTY_NOMINEE)
        fragment.arguments = bundle
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_NOMINEE_SEARCH)
        mListener?.showToolbarBackButton(R.string.property_owner)
        mListener?.addFragment(fragment, true)
    }

    private fun showRemarksPopUp(isApprove: Boolean) {
        // region EditText
        val view = EditText(context)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 0, 16, 0)
        view.layoutParams = params
        view.hint = getString(R.string.hint_enter_remarks)
        // endregion
        mListener?.showAlertDialog(R.string.remarks,
                R.string.save,
                View.OnClickListener {
                    val remarks = view.text?.toString()?.trim()
                    if (TextUtils.isEmpty(remarks)) {
                        view.error = getString(R.string.msg_enter_remarks)
                    } else {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        if (isApprove)
                            approveVerification(remarks ?: "")
                        else
                            rejectVerification(remarks ?: "")
                    }
                },
                0, null, R.string.cancel,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                view)
    }

    private fun approveVerification(remarks: String) {
        mListener?.showProgressDialog()
        val data = PropertyVerificationRequestData()
        data.physicalRemarks = remarks
        data.allowPhysicalVerification = pendingList?.allowPhysicalVerification
        data.propertyId = pendingList?.propertyId
        data.verificationRequestId = pendingList?.propertyVerificationRequestId

        APICall.approvePropertyVerification(data, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                pendingList?.isPhysicalVerified = true
                if (!pendingList?.isDocumentVerified!!)
                    showDocumentVerificationPopUp()
                else {
                    val intent = Intent()
                    intent.putExtra(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    mListener?.popBackStack()
                }


            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun rejectVerification(remarks: String) {
        mListener?.showProgressDialog()
        val data = PropertyVerificationRequestData()
        data.physicalRemarks = remarks
        data.allowPhysicalVerification = pendingList?.allowPhysicalVerification
        data.propertyId = pendingList?.propertyId
        data.verificationRequestId = pendingList?.propertyVerificationRequestId

        APICall.rejectPropertyVerification(data, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                pendingList?.isPhysicalVerified = true
                if (!pendingList?.isDocumentVerified!!)
                    showDocumentVerificationPopUp()
                else {
                    val intent = Intent()
                    intent.putExtra(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    mListener?.popBackStack()
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun showDocumentVerificationPopUp() {
        mListener?.showAlertDialog(R.string.msg_proceed_doc_verification,
                R.string.yes,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    val intent = Intent()
                    intent.putExtra(Constant.KEY_IS_DOC_VERIFICATION_PENDING, true)
                    intent.putExtra(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    mListener?.popBackStack()
                }, R.string.no,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    mListener?.popBackStack()
                })
    }

    private fun calculate(showLoader: Boolean = true) {
        if (showLoader && !isCalculateValid()) {
            return
        }
        try {
            if (showLoader) mListener?.showProgressDialog()
            val acctid: Int? = MyApplication.getPrefHelper().accountId
            val propertyTypeID: Int? = (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).propertyTypeID
            // val landUseTypeID: Int? = (mBinding.spnLandUseType.selectedItem as COMLandUseTypes).LandUseTypeID
            val sectorID: Int? = (mBinding.spnSector.selectedItem as COMSectors).sectorId
            val ageOfProperty: Int? = calculateAgeOfProperty() //TODO calculate the age of property
            // val estimatedRentAmount: String = mBinding.edtEstimatedRentAmount.text.toString()
            var estimatedRentAmount: BigDecimal = BigDecimal.ZERO
            if (!TextUtils.isEmpty(mBinding.edtEstimatedRentAmount.text.toString())) {
                estimatedRentAmount = BigDecimal(currencyToDouble(mBinding.edtMonthlyRentAmount.text.toString()) as Long)
            }
            val strtdt: String = serverFormatDateTimeInMilliSecond(getDate(mBinding.edtConstructedDate.text.toString(), displayDateFormat, displayDateTimeTimeSecondFormat))
            val ctyid: Int? = (mBinding.spnCity.selectedItem as VUCOMCityMaster).cityID
            var landArea:BigDecimal = BigDecimal.ZERO

            if (mBinding.edtArea.text.toString().trim().isNotEmpty()) {
                landArea = if (mBinding.edtArea.text.toString().trim().contains(","))
                    (currencyToDouble(mBinding.edtArea.text.toString().trim())!!.toDouble().toBigDecimal())
                else
                    BigDecimal(mBinding.edtArea.text.toString().trim().toDouble())
            }

            val proEstimatedTax = ProEstimatedTax()
            proEstimatedTax.acctid = acctid
            proEstimatedTax.propertyTypeID = propertyTypeID
            proEstimatedTax.sectorID = sectorID
            proEstimatedTax.ageOfProperty = ageOfProperty
            proEstimatedTax.strtdt = strtdt
            proEstimatedTax.ctyid = ctyid

            if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode == Constant.KEY_RES_PROP) {
                val comfortLevelID: Int? = (mBinding.spnComfortLevel.selectedItem as PropertyComfortLevel).comfortLevelID
                        ?: 0
                proEstimatedTax.comfortLevelID = comfortLevelID
            } else if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode == Constant.KEY_COM_PROP) {
                proEstimatedTax.estimatedRentAmount = estimatedRentAmount
            }

            proEstimatedTax.estimatedLandArea = landArea

            APICall.getEstimatedTax4Property(proEstimatedTax, object : ConnectionCallBack<Double> {
                override fun onSuccess(response: Double) {
                    if (showLoader) mListener?.dismissDialog()
                    response.let {
                        mBinding.edtEstimatedTaxAmount.setText(formatWithPrecision(response))
                    }
                }

                override fun onFailure(message: String) {
                    if (showLoader) mListener?.dismissDialog()
                    if (showLoader) mListener?.showAlertDialog(message)
                }
            })
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
    }

    private fun calculateAgeOfProperty(): Int? {
        val constructedDate = mBinding.edtConstructedDate.text.toString()
        if (!TextUtils.isEmpty(constructedDate)) {
            val selectedDate = formatDates(serverFormatDate(constructedDate))
            val todayDate = Calendar.getInstance().time
            val diff = monthsBetween(todayDate, selectedDate)
            if (diff < 12) {
                return 0
            } else {
                val year = diff / 12
                return year
            }
        }
        return 0
    }

    private fun isCalculateValid(): Boolean {
        if (TextUtils.isEmpty((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode)) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.property_type))
            return false
        }

        /*  if (TextUtils.isEmpty(mBinding.edtRegistrationDate.text.toString())) {
              mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.registration_date))
              mBinding.edtRegistrationDate.requestFocus()
              return false
          }*/

        val estimatedRentAmount: String = mBinding.edtEstimatedRentAmount.text.toString()
        if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode == Constant.KEY_COM_PROP) {
            if (TextUtils.isEmpty(estimatedRentAmount)) {
                mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.estimated_rent_amount))
                mBinding.edtEstimatedRentAmount.requestFocus()
                return false
            }
        }

        if (TextUtils.isEmpty(mBinding.spnSector.selectedItem?.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.sector))
            mBinding.spnSector.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(mBinding.edtArea.text.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.area))
            mBinding.edtArea.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(mBinding.spnZone.selectedItem?.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.zone))
            mBinding.spnZone.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(mBinding.spnCity.selectedItem?.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.city))
            mBinding.spnCity.requestFocus()
            return false
        }

        if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode == Constant.KEY_RES_PROP) {
            if (mBinding.spnComfortLevel.selectedItem == null) {
                mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.comfort_level))
                return false
            } else if ((mBinding.spnComfortLevel.selectedItem as PropertyComfortLevel).comfortLevelID == null) {
                mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.comfort_level))
                return false
            }
        }
        return true
    }

    private fun edtRemoveFocus(focus: Boolean = false) {

        mBinding.edtLength.isFocusable = focus
        mBinding.edtWidth.isFocusable = focus
        mBinding.edtArea.isFocusable = focus

        mBinding.edtLength.isFocusableInTouchMode = true
        mBinding.edtWidth.isFocusableInTouchMode = true
        mBinding.edtArea.isFocusableInTouchMode = true

    }

    private fun prepareData(view: View? = null): StorePropertyPayload {
        edtRemoveFocus()

        val storePropertyPayload = StorePropertyPayload()

        val storePropertyData = StorePropertyData()
        mStorePropertyData?.propertyID?.let {
            storePropertyData.propertyID = it
        }
        mStorePropertyData?.geoAddressID?.let {
            storePropertyData.geoAddressID = it
        }
        storePropertyData.propertySycotaxID = getStringTv(mBinding.edtSycoTaxID)
        storePropertyData.propertyName = getStringTv(mBinding.edtPropertyName)
        storePropertyData.propertyCode = getStringTv(mBinding.edtPropertyCode)
        storePropertyData.propertyTypeID = (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).propertyTypeID
        storePropertyData.propertySplitCode = (mBinding.spnPropertySplit.selectedItem as ComComboStaticValues).code
        if (mBinding.edtParentProperty.text.toString() != getString(R.string.select)) {
            storePropertyData.parentPropertyID = getParentPropertyID(mBinding.edtParentProperty.text.toString())
        }
        if (mBinding.edtLandProperty.text.toString() != getString(R.string.select)) {
            storePropertyData.landPropertyID = getLandPropertyID(mBinding.edtLandProperty.text.toString())
        }

        if (view?.id == mBinding.btnSave.id && mCode == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY) {
            storePropertyData.statusCode = Constant.COM_PropertyMaster_Unverified
        } else {
            storePropertyData.statusCode = (mBinding.spnStatus.selectedItem as COMStatusCode).statusCode
        }
        storePropertyData.registrationNo = getStringTv(mBinding.edtRegistrationNo)
        storePropertyData.registrationDate = serverFormatDate(mBinding.edtRegistrationDate.text.toString())
        storePropertyData.surveyNo = getStringTv(mBinding.edtSurveyNo)
        storePropertyData.userOrgBranchID = (mBinding.spnAdministrationOffice.selectedItem as UMXUserOrgBranches).userOrgBranchID
        if (!mBinding.edtConstructedDate.text.toString().isEmpty())
            storePropertyData.constructedDate = serverFormatDate(mBinding.edtConstructedDate.text.toString())
        else
            storePropertyData.constructedDate = null

        if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode == Constant.KEY_RES_PROP && (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).isApartment == Constant.ACTIVE_N) {
            storePropertyData.electricityConsumptionID = (mBinding.spnElectricityConsumption.selectedItem as COMElectricityConsumption).electricityConsumptionID
            storePropertyData.phaseOfElectricityID = (mBinding.spnPhaseOfElectricity.selectedItem as COMPhaseOfElectricity).phaseOfElectricityID
            storePropertyData.waterConsumptionID = (mBinding.spnWaterConsumption.selectedItem as COMWaterConsumption).waterConsumptionID
            storePropertyData.comfortLevelID = (mBinding.spnComfortLevel.selectedItem as PropertyComfortLevel).comfortLevelID

        } else if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode == Constant.KEY_COM_PROP) {
            storePropertyData.monthlyRentAmount = currencyToDouble(mBinding.edtMonthlyRentAmount.text.toString())!!.toDouble().toBigDecimal()
            storePropertyData.estimatedRentAmount = currencyToDouble(mBinding.edtEstimatedRentAmount.text.toString())!!.toDouble().toBigDecimal()
        }

        storePropertyData.builtUpAreaPerFloor = getDoubleTv(mBinding.edtBuiltUpAreaPerFloor)
        storePropertyData.totalBuiltUpArea = getDoubleTv(mBinding.edtTotalBuiltUpArea)
        storePropertyData.openSpace = getDoubleTv(mBinding.edtOpenSpace)
        storePropertyData.noOfHousesPerFloor = getIntTv(mBinding.edtNoOfHousesPerFloor)
        storePropertyData.noOfFloors = getIntTv(mBinding.edtNoOfFloor)
        storePropertyData.noOfEstablishmentsPerFloor = getIntTv(mBinding.edtNoOfEstablishmentsPerFloor)
        storePropertyData.noOfParkingPlaces = getIntTv(mBinding.edtNoOfParkingPlaces)
        storePropertyData.unitCode = (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).unitcode
        storePropertyData.propertyRegistrationTypeID = (mBinding.spnRegistrationType.selectedItem as COMPropertyRegistrationTypes).propertyRegistrationTypeID
        storePropertyData.length = currencyToDouble(mBinding.edtLength.text.toString().trim())!!.toDouble().toBigDecimal()
        storePropertyData.width = currencyToDouble(mBinding.edtWidth.text.toString().trim())!!.toDouble().toBigDecimal()
        storePropertyData.area = currencyToDouble(mBinding.edtArea.text.toString().trim())!!.toDouble().toBigDecimal()
        if (!TextUtils.isEmpty(mBinding.edtMapArea.text.toString())) {
            if (mBinding.edtMapArea.text.toString().contains(",")) {
                var mapAreaWithoutM2 = (mBinding.edtMapArea.text.toString().substring(0, mBinding.edtMapArea.text.toString().length - 2))
                storePropertyData.mapArea = currencyToDouble(mapAreaWithoutM2)?.toDouble()!!.toBigDecimal()
            } else {
                storePropertyData.mapArea = (mBinding.edtMapArea.text.toString().substring(0, mBinding.edtMapArea.text.toString().length - 2)).toDouble().toBigDecimal()
            }
        }
        /* if (mBinding.llSpnLandUseType.visibility == View.VISIBLE) {
             storePropertyData.landUseTypeID = (mBinding.spnLandUseType.selectedItem as COMLandUseTypes).LandUseTypeID
         }*/
        storePropertyData.active = Constant.ACTIVE_Y
        storePropertyData.geoLocationArea = getJsonObj()
        storePropertyData.isApartment = (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).isApartment
        if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).isApartment == Constant.ACTIVE_N) {
            storePropertyData.floorNo = getIntTv(mBinding.edtFloorNo)
        }

        val geoAddress = GeoAddress()
        geoAddress.country = getStringSpn(mBinding.spnCountry)
        geoAddress.countryCode = (mBinding.spnCountry.selectedItem as COMCountryMaster).countryCode
        geoAddress.state = getStringSpn(mBinding.spnState)
        geoAddress.stateID = (mBinding.spnState.selectedItem as COMStateMaster).stateID
        geoAddress.city = getStringSpn(mBinding.spnCity)
        geoAddress.cityID = (mBinding.spnCity.selectedItem as VUCOMCityMaster).cityID
        geoAddress.zone = (mBinding.spnZone.selectedItem as COMZoneMaster).zone
        geoAddress.sector = (mBinding.spnSector.selectedItem as COMSectors).sector
        geoAddress.sectorID = (mBinding.spnSector.selectedItem as COMSectors).sectorId
        geoAddress.street = getStringTv(mBinding.edtStreet)
        geoAddress.zipCode = getStringTv(mBinding.edtZipCode)
        geoAddress.block = getStringTv(mBinding.edtBlock)
        geoAddress.plot = getStringTv(mBinding.edtPlot)
        geoAddress.doorNo = getStringTv(mBinding.edtDoorNo)

        storePropertyData.geoAddress = geoAddress

        storePropertyPayload.data = storePropertyData

        var countIntial: Int = 0
        if (!TextUtils.isEmpty(mBinding.txtNumberOfInitialOutstanding.text.toString().trim())) {
            countIntial = (mBinding.txtNumberOfInitialOutstanding.text.toString()).toInt()
        }
        if ((mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY)
                && ((view?.id == mBinding.btnSave.id || view?.id == mBinding.llInitialOutstanding.id) && (mPropertyOwnership == null && countIntial <= 0))) {
            var propertyOwnership = PropertyOwnerNomineePayload()
            propertyOwnership.fromDate = serverFormatDate(mBinding.edtFromDate.text.toString())
            if (!TextUtils.isEmpty(mBinding.edtToDate.text.toString())) {
                propertyOwnership.toDate = serverFormatDate(mBinding.edtToDate.text.toString())
            }
            propertyOwnership.registrationNo = mBinding.edtPropertyRegistrationNo.text.toString()
            val selectedExemption = mBinding.spnExemptionReason.selectedItem
            if (selectedExemption != null) {
                if ((selectedExemption as COMPropertyExemptionReasons).propertyExemptionReasonID != -1) {
                    propertyOwnership.propertyExemptionReasonID = selectedExemption.propertyExemptionReasonID
                }
            }
            //propertyOwnership.propertyExemptionReasonID = (mBinding.spnExemptionReason.selectedItem as COMPropertyExemptionReasons).propertyExemptionReasonID
            propertyOwnership.ownerAccountID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId

            if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY) {
                propertyOwnership.nomineeAccountID = propertyOwners?.propertyOwners?.get(0)?.propertyowners?.get(0)?.nomineeAccountID
            } else {
                propertyOwnership.nomineeAccountID = mNominee?.accountID
            }
            if (propertyOwners?.propertyOwners?.get(0)?.propertyowners?.get(0)?.propertyOwnershipID != 0) {
                propertyOwnership.propertyOwnershipID = propertyOwners?.propertyOwners?.get(0)?.propertyowners?.get(0)?.propertyOwnershipID
            }

            var branch: ComComboStaticValues? = mBinding.spnRelation.selectedItem as ComComboStaticValues?
            propertyOwnership.relationshipType = branch?.comboValue

            if ((mBinding.spnStatus.selectedItem as COMStatusCode).statusCode != Constant.COM_PropertyMaster_Unverified) {
                storePropertyData.statusCode = Constant.COM_PropertyMaster_Unverified
            } else {
                storePropertyData.statusCode = (mBinding.spnStatus.selectedItem as COMStatusCode).statusCode
            }
            mBinding.spnStatus.setSelection(getStatusCode(Constant.COM_PropertyMaster_Unverified))
            mBinding.spnStatus.isEnabled = false

            mPropertyOwnership = propertyOwnership
            storePropertyPayload.ownershipdata = propertyOwnership
        } else {
            storePropertyPayload.ownershipdata = null
        }


        return storePropertyPayload

    }

    private fun validateView(view: View? = getView()): Boolean {

        if (TextUtils.isEmpty(mBinding.edtSycoTaxID.text.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.syco_tax_id))
            mBinding.edtSycoTaxID.requestFocus()
            return false
        }
        /*   if (TextUtils.isEmpty(mBinding.edtPropertyName.text.toString())) {
               mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.property_name))
               mBinding.edtPropertyName.requestFocus()
               return false
           }*/
        if (TextUtils.isEmpty(mBinding.spnPropertyType.selectedItem?.toString()) || mBinding.spnPropertyType.selectedItemPosition == 0) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.property_type))
            mBinding.spnPropertyType.requestFocus()
            return false
        }

        /*if (mBinding.edtConstructedDate.text.toString() == null || mBinding.edtConstructedDate.text.toString().isEmpty()) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.constructed_date))
            mBinding.edtConstructedDate.requestFocus()
            return false
        }*/

        if ((mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY)
                && (view?.id == mBinding.btnSave.id || view?.id == mBinding.llInitialOutstanding.id)) {
            if (mBinding.edtFromDate.text.toString() == null || mBinding.edtFromDate.text.toString().isEmpty()) {
                mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.from_date))
                mBinding.edtFromDate.requestFocus()
                return false
            }
        }

        //TODO: Need to test it
        /* if (TextUtils.isEmpty(mBinding.spnParentProperty.selectedItem.toString()) || mBinding.spnParentProperty.selectedItem.toString() == getString(R.string.select)) {
             mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.parent_property))
             mBinding.spnParentProperty.requestFocus()
             return false
         }
         if (TextUtils.isEmpty(mBinding.spnLandProperty.selectedItem.toString()) || mBinding.spnLandProperty.selectedItem.toString() == getString(R.string.select)) {
             mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.land_property))
             mBinding.spnLandProperty.requestFocus()
             return false
         }*/

        if (TextUtils.isEmpty(mBinding.edtLandProperty.text.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.land_property))
            return false
        }
        if (TextUtils.isEmpty(mBinding.edtMeasureUnit.text.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.measurement_units))
            return false
        }
        if (TextUtils.isEmpty(mBinding.edtRegistrationNo.text.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.registration_no))
            mBinding.edtRegistrationNo.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(mBinding.edtRegistrationDate.text.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.registration_date))
            mBinding.edtRegistrationDate.requestFocus()
            return false
        }
//        if (mBinding.edtParentProperty.text.toString() == getString(R.string.select)) {
        if (mBinding.edtSurveyNo.isEnabled && TextUtils.isEmpty(mBinding.edtSurveyNo.text)) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.survey_number))
            mBinding.edtSurveyNo.requestFocus()
            return false
        }
//        }
        if (TextUtils.isEmpty(mBinding.spnAdministrationOffice.selectedItem?.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.administration_office))
            mBinding.spnAdministrationOffice.requestFocus()
            return false
        }

        if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode == Constant.KEY_RES_PROP) {
            if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).isApartment == Constant.ACTIVE_N) {
                if (TextUtils.isEmpty(mBinding.spnElectricityConsumption.selectedItem?.toString())) {
                    mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.electricity_consumption))
                    mBinding.spnElectricityConsumption.requestFocus()
                    return false
                }
                if (TextUtils.isEmpty(mBinding.spnPhaseOfElectricity.selectedItem?.toString())) {
                    mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.phase_of_electricity))
                    mBinding.spnPhaseOfElectricity.requestFocus()
                    return false
                }
                if (TextUtils.isEmpty(mBinding.spnWaterConsumption.selectedItem?.toString())) {
                    mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.water_consumption))
                    mBinding.spnWaterConsumption.requestFocus()
                    return false
                }
                if (TextUtils.isEmpty(mBinding.spnComfortLevel.selectedItem?.toString())) {
                    mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.comfort_level))
                    mBinding.spnComfortLevel.requestFocus()
                    return false
                }
            } else {
//                if (TextUtils.isEmpty(mBinding.edtFloorNo.text.toString())) {
//                    mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.floors_no))
//                    mBinding.edtFloorNo.requestFocus()
//                    return false
//                }
            }
        } else if ((mBinding.spnPropertyType.selectedItem as COMPropertyTypes).taxRuleBookCode == Constant.KEY_COM_PROP) {
            if (TextUtils.isEmpty(mBinding.edtMonthlyRentAmount.text.toString())) {
                mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.monthly_rent_amount))
                mBinding.edtMonthlyRentAmount.requestFocus()
                return false
            }
            if (TextUtils.isEmpty(mBinding.edtEstimatedRentAmount.text.toString())) {
                mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.estimated_rent_amount))
                mBinding.edtEstimatedRentAmount.requestFocus()
                return false
            }
        }

        if (TextUtils.isEmpty(mBinding.spnCountry.selectedItem?.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.country))
            mBinding.spnCountry.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(mBinding.spnState.selectedItem?.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.state))
            mBinding.spnState.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(mBinding.spnCity.selectedItem?.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.city))
            mBinding.spnCity.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(mBinding.spnZone.selectedItem?.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.zone))
            mBinding.spnZone.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(mBinding.spnSector.selectedItem?.toString())) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.sector))
            mBinding.spnSector.requestFocus()
            return false
        }
        if (mGeoFenceLatLong.size <= 2) {
            mListener?.showAlertDialog("${getString(R.string.msg_provide)} ${getString(R.string.geo_fence)} ")
            return false
        }

        if (TextUtils.isEmpty(mBinding.spnPropertySplit.selectedItem?.toString()) || mBinding.spnPropertySplit.selectedItemPosition == 0) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.property_split))
            mBinding.spnPropertySplit.requestFocus()
            return false
        }

        if (splitObjSelected != null) {
            when (splitObjSelected.code) {
                Constant.LOT -> {
                    if (mBinding.edtBlock.isEnabled && TextUtils.isEmpty(mBinding.edtBlock.text)) {
                        mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.block))
                        mBinding.edtBlock.requestFocus()
                        return false
                    }
                }
                Constant.SURVEYNO -> {
                    if (mBinding.edtSurveyNo.isEnabled && TextUtils.isEmpty(mBinding.edtSurveyNo.text)) {
                        mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.survey_number))
                        mBinding.edtSurveyNo.requestFocus()
                        return false
                    }
                }
                Constant.PARCEL -> {
                    if (mBinding.edtDoorNo.isEnabled && TextUtils.isEmpty(mBinding.edtDoorNo.text)) {
                        mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.door_no))
                        mBinding.edtDoorNo.requestFocus()
                        return false
                    }
                }
            }
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mListener?.showToolbarBackButton(R.string.title_property_txt)
        if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY || (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY && editMode == true)) {
            if (mStorePropertyData != null && mStorePropertyData?.isInvoiceGenerated!!) {
                mListener?.screenMode = Constant.ScreenMode.VIEW
            } else {
                mListener?.screenMode = Constant.ScreenMode.ADD
            }
        }
        fetchChildEntriesCount()
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_OUT_STANDING_ENTRY) {
            getPropertyDueSummary()
        } else if (requestCode == Constant.REQUEST_CODE_PROPERTY_GEO && resultCode == Activity.RESULT_OK) {
            data?.extras?.let {
                if (it.containsKey(Constant.KEY_GEO_AREA_LATLONG)) {
                    mGeoFenceLatLong = it.getParcelableArrayList<GeoFenceLatLong>(Constant.KEY_GEO_AREA_LATLONG)!!
                    bindMarkerData()
                }
                if (it.containsKey(Constant.KEY_MAP_AREA)) {
                    mBinding.edtMapArea.setText(it.getString(Constant.KEY_MAP_AREA))
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_NOMINEE_SEARCH) {
//            mListener?.showToolbarBackButton(R.string.property_owner)
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                mNominee = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setCustomerInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_NOMINEE) {
//            mListener?.showToolbarBackButton(R.string.property_owner)
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mNominee = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            }
        }
    }

    private fun setCustomerInfo() {
        mNominee.let {
            mBinding.edtNomineeName.setText(it?.accountName)
        }
    }

    private fun bindMarkerData() {
        googleMap?.clear()

        setLandPropertyMark()
        setParentPropertyMark()
        setChildPropertyMark()
    }

    private fun setChildPropertyMark() {
        val latLngArray: ArrayList<LatLng> = arrayListOf()
        if (mGeoFenceLatLong.size > 0) {
            for ((position: Int, location: GeoFenceLatLong?) in this.mGeoFenceLatLong.withIndex()) {
                location.let {
                    val markerOptions = MarkerOptions()
                    location.latitude.let {
                        it.toDouble().let { latitude ->
                            location.longitude.let { it ->
                                it.toDouble().let { longitude ->
                                    markerOptions.position(LatLng(latitude, longitude))
                                    markerOptions.icon(bitmapDescriptorFromVector(MyApplication.getContext(), "${position + 1}"))
                                    val marker = googleMap?.addMarker(markerOptions)
                                    marker?.tag = location
                                }
                            }
                        }
                    }
                }
            }
            for (geo in mGeoFenceLatLong) {
                latLngArray.add(LatLng(geo.latitude, geo.longitude))
            }
            if (latLngArray.size > 0) {
                googleMap!!.addPolygon(PolygonOptions().addAll(latLngArray).fillColor(0xffff6600.toInt()).strokeWidth(2F).strokeColor(R.color.colorAccent))
                val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(latLngArray)
                googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))
            }

            calculateDistance(mGeoFenceLatLong, 2F)

            setAreaMarkerFromLatLng("${mBinding.root.context.getString(R.string.area)}: ${calculateArea(mGeoFenceLatLong)}", Centroid(latLngArray))

        }
    }

    private fun setAreaMarkerFromLatLng(text: String, latLng: LatLng?) {
        val obm = writeTextOnDrawable(R.drawable.map_area_bg, text, Color.WHITE)
        val markerOptions = latLng?.let {
            MarkerOptions().icon(
                    BitmapDescriptorFactory.fromBitmap(obm))
                    .position(it)
        }
        googleMap!!.addMarker(markerOptions)
    }

    private fun setParentPropertyMark() {
        val parentLatLngArray: ArrayList<LatLng> = arrayListOf()

        if (mParentGeoFenceLatLong.size > 0) {
            for (geo in mParentGeoFenceLatLong) {
                parentLatLngArray.add(LatLng(geo.latitude, geo.longitude))
            }

            googleMap!!.addPolygon(PolygonOptions().addAll(parentLatLngArray).fillColor(0xffffa500.toInt()).strokeWidth(2F).strokeColor(R.color.colorliteAccent))
            val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(parentLatLngArray)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))

            //  calculateDistance(mParentGeoFenceLatLong, 3F)

        }
    }

    private fun setLandPropertyMark() {
        val landLatLngArray: ArrayList<LatLng> = arrayListOf()

        if (mLandGeoFenceLatLong.size > 0) {

            for (geo in mLandGeoFenceLatLong) {
                landLatLngArray.add(LatLng(geo.latitude, geo.longitude))
            }

            googleMap!!.addPolygon(PolygonOptions().addAll(landLatLngArray).fillColor(0x99999999.toInt()).strokeWidth(2F).strokeColor(R.color.colorRed))
            val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(landLatLngArray)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))

            // calculateDistance(mLandGeoFenceLatLong, 8F)

        }
    }

    private fun calculateDistance(mGeoFenceLatLong: ArrayList<GeoFenceLatLong>, distance: Float) {
        var prevLatLng = LatLng(0.0, 0.0)
        mGeoFenceLatLong.forEachIndexed { index, latLng ->
            prevLatLng = if (index == 0)
                LatLng(latLng.latitude, latLng.longitude)
            else {
                // Log.e("message val", "($i, $index)")
                val loc1 = Location("")
                loc1.latitude = prevLatLng.latitude
                loc1.longitude = prevLatLng.longitude

                val loc2 = Location("")
                loc2.latitude = latLng.latitude
                loc2.longitude = latLng.longitude
                var distanceInMeters = distanceCalculateInMeters(loc1, loc2)
                Log.e("message val", "($distanceInMeters)")
                setMarkerFromLat(Constant.df.format(distanceInMeters) + " m", loc1, loc2, distance)
                LatLng(latLng.latitude, latLng.longitude)
            }
        }
        if (mGeoFenceLatLong.size > 2) {
            val loc1 = Location("")
            loc1.latitude = mGeoFenceLatLong[mGeoFenceLatLong.size - 1].latitude
            loc1.longitude = mGeoFenceLatLong[mGeoFenceLatLong.size - 1].longitude

            val loc2 = Location("")
            loc2.latitude = mGeoFenceLatLong[0].latitude
            loc2.longitude = mGeoFenceLatLong[0].longitude
            var distanceInMeters = distanceCalculateInMeters(loc1, loc2)
            Log.e("message last val", "($distanceInMeters)")
            setMarkerFromLat(Constant.df.format(distanceInMeters) + " m", loc1, loc2, distance)


            val latLngArray: ArrayList<LatLng> = arrayListOf()

            for (geo in mGeoFenceLatLong) {
                latLngArray.add(LatLng(geo.latitude, geo.longitude))
            }
//            setAreaMarkerFromLatLng("Area: ${calculateArea(mGeoFenceLatLong)}", Centroid(latLngArray))

            setMarkerFromLat(Constant.df.format(distanceInMeters.toDouble()) + " m", loc1, loc2, distance)
            return
        }
    }

    private fun setMarkerFromLat(distanceInMeters: String, prevLatLng: Location, latLng: Location, distance: Float) {
        val obm = writeTextOnDrawable(R.drawable.map_distance_bg, distanceInMeters, Color.BLACK)
        var point = LatLng((prevLatLng.latitude + latLng.latitude) / 2, (prevLatLng.longitude + latLng.longitude) / 2)
        if (distance == 3F) {
            point = LatLng((prevLatLng.latitude + point.latitude) / 2, (prevLatLng.longitude + point.longitude) / 2)
        } else if (distance == 8F) {
            point = LatLng((point.latitude + latLng.latitude) / 2, (point.longitude + latLng.longitude) / 2)
        }
        val markerOptions = MarkerOptions().icon(
                BitmapDescriptorFactory.fromBitmap(obm))
                .position(point)
        googleMap!!.addMarker(markerOptions)
    }

    private fun bitmapDescriptorFromVector(context: Context, color: String): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_location_pin)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = writeTextOnDrawable(R.drawable.ic_location_pin, color, Color.BLACK) //Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showToolbarBackButton(title: Int)
        fun showAlertDialogFailure(message: String, noRecordsFound: Int, onClickListener: DialogInterface.OnClickListener)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showToast(message: String)
        fun finish()
        fun popBackStack()
        var screenMode: Constant.ScreenMode
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener?, view: View?)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener?)
    }

    private fun getPolygonLatLngBounds(polygon: List<LatLng>): LatLngBounds? {
        val centerBuilder = LatLngBounds.builder()
        for (point in polygon) {
            centerBuilder.include(point)
        }
        return centerBuilder.build()
    }

    fun getJsonObj(): String {
        return Gson().toJson(mGeoFenceLatLong).toString()
    }

    fun storePropertyData(payload: StorePropertyPayload, view: View? = null) {
        mListener?.showProgressDialog()
        APICall.storePropertyDetails(payload, object : ConnectionCallBack<String> {
            override fun onSuccess(response: String) {
                mListener?.dismissDialog()
                if (mStorePropertyData == null)
                    mStorePropertyData = StorePropertyPayload().data
                mStorePropertyData?.propertyID = response.toInt()

                if (view == null) {
                    if (mStorePropertyData != null && mStorePropertyData?.propertyID != 0)
                        mListener?.showToast(getString(R.string.msg_record_save_success))

                    Handler().postDelayed({
                        navigateToSummary()
                    }, 500)
                } else
                    onClick(view)
                //mListener?.showAlertDialog(response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            }
        })
    }

    private fun navigateToSummary() {
        val fragment = PropertyTaxSummaryFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
        bundle.putInt(Constant.KEY_PRIMARY_KEY, mStorePropertyData?.propertyID ?: 0)
        fragment.arguments = bundle
        mListener?.addFragment(fragment, true)
    }

    private fun getPropertyDueSummary() {
        if (mStorePropertyData != null && mStorePropertyData?.propertyID != 0) {
//            mListener?.showProgressDialog()
            APICall.getPropertyDueSummary(mStorePropertyData?.propertyID
                    ?: 0, object : ConnectionCallBack<PropertyDueResponse> {
                override fun onSuccess(response: PropertyDueResponse) {
//                    mListener?.dismissDialog()
                    resetDueSummary()
                    response.dueSummaries[0].let {
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
//                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    resetDueSummary()
                    fetchChildEntriesCount()
                }
            })
        }

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

    private fun fetchChildEntriesCount() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "COM_PropertyMaster"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${mStorePropertyData?.propertyID}"
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
                filterColumn.columnValue = "COM_PropertyMaster"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue = "${mStorePropertyData?.propertyID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
            }
            "COM_Notes" -> {
                mBinding.txtNumberOfNotes.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "ProductCode"
                if (mBinding.spnPropertyType.selectedItem != null && -1 != (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).propertyTypeID) {
                    (mBinding.spnPropertyType.selectedItem as COMPropertyTypes).productCode?.let {
                        filterColumn.columnValue = it
                    }
                } else filterColumn.columnValue = "0"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "VoucherNo"
                filterColumn.columnValue = "${mStorePropertyData?.propertyID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "VU_ACC_InitialOutstandings", "InitialOutstandingID")
            }
            "VU_ACC_InitialOutstandings" -> {
                mBinding.txtNumberOfInitialOutstanding.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "PropertyID"
                filterColumn.columnValue = "${mStorePropertyData?.propertyID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_PropertyImages", "PropertyImageID")
            }
            "COM_PropertyImages" -> {
                mBinding.txtNoOfImages.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "PropertyID"
                filterColumn.columnValue = "${mStorePropertyData?.propertyID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_PropertyPlans", "PropertyPlanID")
            }
            "COM_PropertyPlans" -> {
                mBinding.txtNoOfPlanImages.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "PropertyID"
                filterColumn.columnValue = "${mStorePropertyData?.propertyID}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "CRM_PropertyOwnership", "PropertyOwnershipID")
            }
            "CRM_PropertyOwnership" -> {
                mBinding.txtPropertyOwnerCount.text = "$count"
                if (count > 0) {
                    if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY) {
                        mBinding.spnStatus.setSelection(getStatusCode(Constant.COM_PropertyMaster_Unverified))
                    }
                }
                mBinding.txtParentPlanImageCount.text = "0"
                mBinding.txtParentImageCOunt.text = "0"
                mBinding.txtParentDocumentCount.text = "0"
                getParentPlanImagesCount("ParentPlanImages")
            }
            "ParentPlanImages" -> {
                mBinding.txtParentPlanImageCount.text = "$count"
                getParentImagesCount("ParentImagesCount")
            }
            "ParentImagesCount" -> {
                mBinding.txtParentImageCOunt.text = "$count"
                getParentDocumentsCount("ParentDocumentsCount")
            }
            "ParentDocumentsCount" -> {
                mBinding.txtParentDocumentCount.text = "$count"
            }
        }
    }

    private fun getParentsCountMethod(response: LinkedTreeMap<String, ArrayList<String>>, tableOrViewName: String) {
        for ((key, list) in response) {
            var count = 0
            val gson = Gson()
            val jsonObj: JSONObject = JSONArray(gson.toJson(list))[0] as JSONObject
            if (jsonObj.has("RecordCounts"))
                count = jsonObj.getInt("RecordCounts")
            Log.e("thi is json list", ">>>>>>>>>>>$count")
            bindCounts(tableOrViewName, count)
        }
    }

    private fun getParentPlanImagesCount(tableOrViewName: String) {
//        mListener?.showProgressDialog()
        APICall.getParentPropertyPlanImages(mStorePropertyData?.propertyID ?: 0
        ?: 0, true, object : ConnectionCallBack<LinkedTreeMap<String, ArrayList<String>>> {
            override fun onSuccess(response: LinkedTreeMap<String, ArrayList<String>>) {
                try {

//                    mListener?.dismissDialog()
                    getParentsCountMethod(response, tableOrViewName)

                } catch (e: java.lang.Exception) {
                    LogHelper.writeLog(exception = e)
//                    Log.e("exp in e", ">>>>>>>>>>${e.localizedMessage}")
                }
            }

            override fun onFailure(message: String) {
//                mListener?.dismissDialog()
            }
        })
    }

    private fun getParentImagesCount(tableOrViewName: String) {
//        mListener?.showProgressDialog()
        APICall.getParentPropertyImages(mStorePropertyData?.propertyID ?: 0
        ?: 0, true, object : ConnectionCallBack<LinkedTreeMap<String, ArrayList<String>>> {
            override fun onSuccess(response: LinkedTreeMap<String, ArrayList<String>>) {
                try {

//                    mListener?.dismissDialog()
                    getParentsCountMethod(response, tableOrViewName)
                } catch (e: java.lang.Exception) {
                    LogHelper.writeLog(exception = e)
//                    Log.e("exp in e", ">>>>>>>>>>${e.localizedMessage}")
                }
            }

            override fun onFailure(message: String) {
//                mListener?.dismissDialog()
            }
        })
    }

    private fun getParentDocumentsCount(tableOrViewName: String) {
//        mListener?.showProgressDialog()
        APICall.getParentPropertyDocuments(mStorePropertyData?.propertyID ?: 0
        ?: 0, true, object : ConnectionCallBack<LinkedTreeMap<String, ArrayList<String>>> {
            override fun onSuccess(response: LinkedTreeMap<String, ArrayList<String>>) {
                try {

//                    mListener?.dismissDialog()
                    getParentsCountMethod(response, tableOrViewName)
                } catch (e: java.lang.Exception) {
                    LogHelper.writeLog(exception = e)
//                    Log.e("exp in e", ">>>>>>>>>>${e.localizedMessage}")
                }
            }

            override fun onFailure(message: String) {
//                mListener?.dismissDialog()
            }
        })
    }

    private fun getStatusCode(statusCode: String): Int {
        for ((posi, code) in mStatusCodes.withIndex()) {
            if (statusCode == code.statusCode) {
                return posi
            }
        }
        return 0
    }

    private fun getParentPropertyID(toString: String): Int? {
        for (property in mProperties) {
            if (property.propertySycotaxID == toString) {
                return property.proprtyid
            }
        }
        return null
    }

    private fun getLandPropertyID(toString: String): Int? {
        for (land in mLands) {
            if (land.propertySycotaxID == toString) {
                return land.proprtyid
            }
        }
        return null
    }

    private fun isPropertyOwnerAdded(): Boolean {
        var isPropertyOwnershipAdded = true
        var countIntial: Int? = null
        if (!TextUtils.isEmpty(mBinding.txtNumberOfInitialOutstanding.text.toString().trim())) {
            countIntial = (mBinding.txtNumberOfInitialOutstanding.text.toString()).toInt()
        }

        if ((mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY && mPropertyOwnership == null) || (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY && propertyOwners == null)) {
            isPropertyOwnershipAdded = false
        }
        return isPropertyOwnershipAdded
    }


}
