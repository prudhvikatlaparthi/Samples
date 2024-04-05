package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.GetChildTabCount
import com.sgs.citytax.api.payload.SearchFilter
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.Business
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.VehicleOwnershipDetailsResult
import com.sgs.citytax.api.response.ViolationTicketResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentVehicleTicketEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.LocalDocumentPreviewActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.adapter.ImpoundDocumentsAdapter
import com.sgs.citytax.util.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class VehicleTicketEntryFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentVehicleTicketEntryBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var mListener: Listener? = null
    private var mCustomer: BusinessOwnership? = null
    private var mDriver: BusinessOwnership? = null
    private var mViolator: BusinessOwnership? = null
    private var mVehicleDetails: VehicleDetails? = null
    private var mBusiness: Business? = null
    private var mDocument: COMDocumentReference? = COMDocumentReference()
    private var mViolationDetail: ViolationDetail? = ViolationDetail()
    private var mHelper: LocationHelper? = null

    private var mResponseCountriesList: List<COMCountryMaster> = arrayListOf()
    private var mResponseStatesList: List<COMStateMaster> = arrayListOf()
    private var mResponseCitiesList: List<VUCOMCityMaster> = arrayListOf()
    private var mResponseZonesList: List<COMZoneMaster> = arrayListOf()
    private var mResponseSectorsList: List<COMSectors> = arrayListOf()
    private var mViolationTypesList: List<LAWViolationType> = arrayListOf()
    private var mPoliceStations: List<VUCRMPoliceStation> = arrayListOf()
    private var mImageFilePath : String? = null
    var event: Event? = null
    private val kFileExtension = "jpg"

    private val TAG = "VehicleTicketEntryFragm"
    // Multiple docs
    private val mDocumentsList: ArrayList<COMDocumentReference> = arrayListOf()
    private val documentListAdapter: ImpoundDocumentsAdapter by lazy {
        ImpoundDocumentsAdapter(mDocumentsList, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                view.let {
                    when (view.id) {
                        R.id.imgDocument -> {
                            val comDocumentReference = obj as COMDocumentReference
                            mDocumentsList.remove(comDocumentReference)
                            mDocumentsList.add(0, comDocumentReference)
                            documentListAdapter.notifyDataSetChanged()
                            startLocalPreviewActivity(mDocumentsList)
                        }
                        R.id.btnClearImage -> {
                            if (mDocumentsList.size == 1)
                                mBinding.multipleDoc.txtNoDataFound.visibility = GONE
                            mDocumentsList.removeAt(position)
                            documentListAdapter.notifyDataSetChanged()
                            if (mDocumentsList.isEmpty()){
                                mBinding.multipleDoc.txtNoDataFound.show()
                            }
                            ObjectHolder.documents = mDocumentsList
                        }
                        else -> {
                        }
                    }
                }
            }

            override fun onLongClick(view: View, position: Int, obj: Any) {

            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicle_ticket_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentLocation()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        mHelper?.disconnect()
        super.onDetach()
    }

    override fun initComponents() {
        processIntent()
        setViews()
        bindSpinner()
        bindSignature()
        setListeners()
        getDocumentData()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: kotlin.Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mHelper?.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun getCurrentLocation() {
        mHelper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
        mHelper?.fetchLocation()
        mHelper?.setListener(object : LocationHelper.Location {
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

    private fun processIntent() {
        arguments?.let {
            fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_VIOLATION_DETAIL))
                mViolationDetail = it.getParcelable(Constant.KEY_VIOLATION_DETAIL)
        }
    }

    private fun setViews() {
        // Multiple documents
        mBinding.multipleDoc.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.multipleDoc.rcDocuments.adapter = documentListAdapter

        mBinding.crdVehicleAndOwnerSelection.visibility = GONE
        mBinding.crdDriverSelection.visibility = GONE
        mBinding.crdViolatorSelection.visibility = GONE
        mBinding.edtTicketDate.setText(formatDisplayDateTimeInMillisecond(Date()))
        /*mBinding.edtTicketDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtTicketDate.setText(getDate(Calendar.getInstance().time, displayDateTimeTimeSecondFormat))
        mBinding.edtTicketDate.setDisplayDateFormat(displayDateTimeTimeSecondFormat)*/
    }

    private fun bindSignature() {
        mListener?.showProgressDialog()
        mViolationDetail?.violationOwnerSignatureID?.let {
            APICall.downloadAWSPath(it, object : ConnectionCallBack<String> {
                override fun onSuccess(response: String) {
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(response)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {
                                    mListener?.dismissDialog()
                                }

                                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                                    mBinding.signatureView.signatureBitmap = resource
                                    mListener?.dismissDialog()
                                }
                            })
                }

                override fun onFailure(message: String) {

                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mHelper?.onActivityResult(requestCode, resultCode)
        bindCounts()
        if (requestCode == Constant.ContraventionDocument.MultipleDocuments.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.multipleDoc.txtNoDataFound.hide()
                mDocumentsList.add(doc)
                documentListAdapter.notifyDataSetChanged()
                ObjectHolder.documents = mDocumentsList
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        }else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                mCustomer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setCustomerInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_DRIVER_SEARCH) {
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                mDriver = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setDriverInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER) {
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mCustomer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_DRIVER) {
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mDriver = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setDriverInfo()
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_VIOLATOR_SEARCH) {
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mViolator = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setViolatorInfo()
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_VIOLATOR) {
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mViolator = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setViolatorInfo()
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_SEARCH) {
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS))
                    mBusiness = it.getParcelableExtra(Constant.KEY_BUSINESS) as Business
                setBusinessInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_VEHICLE_SEARCH) {
            data?.let {
                if (it.hasExtra(Constant.KEY_VEHICLE_OWNERSHIP)) {
                    mVehicleDetails = it.getParcelableExtra(Constant.KEY_VEHICLE_OWNERSHIP)
                    setVehicleInfo()
                }
            }
        } else if (requestCode == Constant.REQUEST_CODE_CREATE_VEHICLE) {
            val mData = event?.intent
            mData?.let {
                if (it.hasExtra(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS)) {
                    if (event != null) {
                        val vehicleDetail = it.getParcelableExtra<VehicleDetails>(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS)
                        vehicleDetail?.vehicleNumber?.let { it1 -> getVehicleOwnership(it1) }
                        Event.instance.clearData()
                        event = null
                    }
                }
            }
        }
    }

    private fun getVehicleOwnership(key: String) {
        mListener?.showProgressDialog()
        APICall.getVehicleOwnershipDetails(key, object : ConnectionCallBack<VehicleOwnershipDetailsResult> {
            override fun onSuccess(response: VehicleOwnershipDetailsResult) {
                mListener?.dismissDialog()
                val list = response.vehicleDetails
                list?.let {
                    for (vehicle: VehicleDetails in it) {
                        if (vehicle.toDate == null) {
                            mVehicleDetails = vehicle
                            mVehicleDetails?.vehicleNumber = vehicle.vehicleNumber
                            mVehicleDetails?.owner = vehicle.accountName
                            setVehicleInfo()
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

    private fun setListeners() {
        mBinding.btnShowMoreOrLess.setOnClickListener {
            if (mBinding.btnShowMoreOrLess.text.toString().equals(getString(R.string.show_more), true)) {
                showMoreProductViews()
            } else {
                hideProductViews()
            }
        }
        mBinding.btnSave.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        /*mBinding.edtOwnerName.setOnClickListener {
            val fragment = BusinessOwnerSearchFragment()
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
            mListener?.addFragment(fragment, true)
        }*/

        mBinding.edtDriverName.setOnClickListener {
            val fragment = BusinessOwnerSearchFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DRIVER_SEARCH)
            mListener?.addFragment(fragment, true)
        }

        mBinding.edtDriverNumber.setOnClickListener {
            val fragment = BusinessOwnerSearchFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DRIVER_SEARCH)
            mListener?.addFragment(fragment, true)
        }

        mBinding.edtDrivingLicenseNumber.setOnClickListener {
            val fragment = BusinessOwnerSearchFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DRIVER_SEARCH)
            mListener?.addFragment(fragment, true)
        }

        mBinding.edtViolatorName.setOnClickListener {
            val violationType = mBinding.spnViolationType.selectedItem as LAWViolationType
            if (violationType.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.code)
                showCitizenViolators()
            else if (violationType.violatorTypeCode == Constant.ViolationTypeCode.BUSINESS.code)
                showBusinessViolators()
        }

        mBinding.edtViolatorNumber.setOnClickListener {
            val violationType = mBinding.spnViolationType.selectedItem as LAWViolationType
            if (violationType.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.code)
                showCitizenViolators()
            else if (violationType.violatorTypeCode == Constant.ViolationTypeCode.BUSINESS.code)
                showBusinessViolators()
        }


        mBinding.tvCreateDriver.setOnClickListener {
            val fragment = BusinessOwnerEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            //endregion

            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DRIVER)

            mListener?.showToolbarBackButton(R.string.driver)
            mListener?.addFragment(fragment, true)
        }

        mBinding.tvCreateViolator.setOnClickListener {
            val fragment = BusinessOwnerEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            //endregion

            fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATOR)

            //mListener?.showToolbarBackButton(R.string.driver)
            mListener?.addFragment(fragment, true)
        }

        mBinding.edtVehicleNo.setOnClickListener {
            val fragment = VehicleSearchFragment()
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_VEHICLE_SEARCH)
            mListener?.addFragment(fragment, true)
        }

        mBinding.tvCreateVehicle.setOnClickListener {
            event = Event.instance
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE)
            startActivityForResult(intent, Constant.REQUEST_CODE_CREATE_VEHICLE)
        }

        mBinding.txtTicketHistory.setOnClickListener {
            val fragment = VehicleTicketHistoryFragment()
            val bundle = Bundle()
            bundle.putString(Constant.KEY_VEHICLE_NO, mVehicleDetails?.vehicleNumber ?: "")
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            mListener?.addFragment(fragment, true)
        }

        mBinding.tvSeeTicketHistoryDriver.setOnClickListener {
            val fragment = DriverTicketHistoryFragment()
            val bundle = Bundle()
            bundle.putString(Constant.KEY_DRIVING_LICENSE_NO, if (mDriver?.drivingLicenseNo != null) "${mDriver?.drivingLicenseNo}" else "")
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            mListener?.addFragment(fragment, true)
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
            dialog.show(childFragmentManager, LocateDialogFragment::class.java.simpleName)
        }

        mBinding.spnViolationType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var violationType = LAWViolationType()

                if (p0 != null && p0.selectedItem != null)
                    violationType = p0.selectedItem as LAWViolationType

                if (violationType.applicableOnDriver == "Y" && violationType.violatorTypeCode?.toUpperCase(Locale.getDefault()) == Constant.ViolationTypeCode.VEHICLE.code) {
                    mBinding.crdVehicleAndOwnerSelection.visibility = VISIBLE
                    mBinding.crdDriverSelection.visibility = VISIBLE
                } else {
                    mBinding.crdVehicleAndOwnerSelection.visibility = GONE
                    mBinding.crdDriverSelection.visibility = GONE
                }

                if (violationType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.BUSINESS.code)
                        || violationType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.CITIZEN.code)) {
                    mBinding.crdViolatorSelection.visibility = VISIBLE
                    if (violationType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.CITIZEN.code)) {
                        mBinding.llCreateViolator.visibility = VISIBLE
                    } else {
                        mBinding.llCreateViolator.visibility = GONE
                    }
                } else
                    mBinding.crdViolatorSelection.visibility = GONE

                if(fromScreen==Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT) {
                    mBinding.crdViolatorSelection.visibility = GONE
                    mBinding.crdVehicleAndOwnerSelection.visibility = GONE
                    mBinding.crdDriverSelection.visibility = GONE
                }

                filterViolationClasses(violationType.parentViolationTypeID ?: 0)
                fetchAmount(violationType.violationTypeID ?: 0)

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
        mBinding.multipleDoc.fabAddImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ContraventionDocument.MultipleDocuments.value)
            }
        })

    }
    private fun checkAndStartCameraIntent(resultCode: Int) {
        // region Storage Permission
        if (!hasPermission(requireContext(), Manifest.permission.CAMERA)) {
            requestForPermission(
                arrayOf(Manifest.permission.CAMERA),
                Constant.REQUEST_CODE_CAMERA
            )
            return
        }
        // endregion
        openCameraIntent(resultCode)
    }
    private fun openCameraIntent(requestCode: Int) {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile: File?
            try {
                mImageFilePath = null
                photoFile = createImageFile()
            } catch (e: IOException) {
                LogHelper.writeLog(exception = e)
                return
            }
            val photoUri: Uri = FileProvider.getUriForFile(requireContext(), context?.packageName.toString() + ".provider", photoFile)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(pictureIntent, requestCode)
        }
    }
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".${kFileExtension}", storageDir)
        mImageFilePath = image.absolutePath
        return image
    }

    private fun fetchAmount(violationTypeId: Int) {
        APICall.getEstimatedFineAmount(violationTypeId, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                response.let {
                    mBinding.edtAmount.setText(formatWithPrecision(it))
                }
            }

            override fun onFailure(message: String) {
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun filterViolationClasses(parentViolationTypeID: Int?, parentViolationDetail: String? = "") {
        mBinding.spnViolationClass.adapter = null
        var violationTypes: ArrayList<LAWViolationType> = arrayListOf()
        var violationDetail = ""
        parentViolationTypeID?.let {
            parentViolationDetail?.let {
                violationDetail = it
            }
            if (it == 0)
                violationTypes = arrayListOf()
            else {
                for (violationType in mViolationTypesList) {
                    if (parentViolationTypeID == violationType.violationTypeID)
                        violationTypes.add(violationType)
                    if (TextUtils.isEmpty(violationDetail) && violationType.violationDetails != null && !TextUtils.isEmpty(violationType.violationDetails))
                        violationType.violationDetails?.let {
                            violationDetail = it
                        }
                }
            }
            if (violationTypes.size > 0) {
                val adapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, violationTypes)
                mBinding.spnViolationClass.adapter = adapter
            }
        }
       // mBinding.edtViolationDetails.setText(violationDetail)
    }

    private fun setCustomerInfo() {
        mCustomer?.let {
            mBinding.edtOwnerName.setText(it.accountName)
        }
    }

    private fun setDriverInfo() {
        mDriver?.let {
            mBinding.edtDriverName.setText(it.accountName)
            mBinding.edtDriverNumber.setText(it.phone)
            mBinding.edtDrivingLicenseNumber.setText(it.drivingLicenseNo)
        }
    }

    private fun setViolatorInfo() {
        mViolator?.let {
            mBinding.edtViolatorName.setText(it.accountName)
            mBinding.edtViolatorNumber.setText(it.phone)
        }
    }

    private fun setBusinessInfo() {
        mBusiness?.let {
            mBinding.edtViolatorName.setText(it.businessName)
            mBinding.edtViolatorNumber.setText(it.number)
        }
    }

    private fun setVehicleInfo() {
        mVehicleDetails?.let {
            mBinding.edtVehicleNo.setText(it.vehicleNumber)
            mBinding.edtOwnerName.setText(it.owner ?: "")
        }
    }

    private fun showCitizenViolators() {
        val fragment = BusinessOwnerSearchFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        fragment.arguments = bundle
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATOR_SEARCH)
        mListener?.addFragment(fragment, true)
    }

    private fun showBusinessViolators() {
        val fragment = BusinessSearchFragment()
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_SEARCH)
        mListener?.addFragment(fragment, true)
    }


    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("LAW_ViolationTickets", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mResponseCountriesList = response.countryMaster
                mResponseStatesList = response.stateMaster
                mResponseCitiesList = response.cityMaster
                mResponseZonesList = response.zoneMaster
                mResponseSectorsList = response.sectors
                mViolationTypesList = response.violationTypes
                mPoliceStations = response.policeStations

                filterCountries()

                if (mViolationTypesList.isNotEmpty()) {
                    val violationTypesAdapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, mViolationTypesList)
                    mBinding.spnViolationType.adapter = violationTypesAdapter
                }

                if (mPoliceStations.isNotEmpty()) {
                    val policeStationAdapter = ArrayAdapter<VUCRMPoliceStation>(requireContext(), android.R.layout.simple_list_item_1, mPoliceStations)
                    mBinding.spnPoliceStation.adapter = policeStationAdapter
                    setStation()
                }

                bindData()

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun showMoreProductViews() {
//        mBinding.edtCountryLayout.visibility = View.VISIBLE
//        mBinding.edtStateLayout.visibility = View.VISIBLE
//        mBinding.edtCityLayout.visibility = View.VISIBLE
        mBinding.edtStreetLayout.visibility = View.VISIBLE
        mBinding.edtSectionLayout.visibility = View.VISIBLE
        mBinding.edtLotLayout.visibility = View.VISIBLE
        mBinding.edtParcelLayout.visibility = View.VISIBLE
        mBinding.edtZipLayout.visibility = View.VISIBLE
        showUnderLineText(mBinding.btnShowMoreOrLess, getString(R.string.show_less))
    }

    private fun hideProductViews() {
        //mBinding.edtCountryLayout.visibility = View.GONE
        //mBinding.edtStateLayout.visibility = View.GONE
        //mBinding.edtCityLayout.visibility = View.GONE
        mBinding.edtStreetLayout.visibility = View.GONE
        mBinding.edtSectionLayout.visibility = View.GONE
        mBinding.edtLotLayout.visibility = View.GONE
        mBinding.edtParcelLayout.visibility = View.GONE
        mBinding.edtZipLayout.visibility = View.GONE
        showUnderLineText(mBinding.btnShowMoreOrLess, getString(R.string.show_more))
    }

    private fun setStation() {
        for ((index, obj) in mPoliceStations.withIndex()){
            if (obj.userOrgBranchID == MyApplication.getPrefHelper().userOrgBranchID) {
                mBinding.spnPoliceStation.setSelection(index)
                mBinding.spnPoliceStation.setEnabled(false)
                break
            }
        }
    }


    private fun bindData() {
        mViolationDetail?.let { it ->
            for ((index, violationType) in mViolationTypesList.withIndex()) {
                if (it.violationTypeID == violationType.violationTypeID) {
                    mBinding.spnViolationType.setSelection(index)
                    break
                }
            }
            for ((index, policeStation) in mPoliceStations.withIndex()) {
                if (it.userOrgBranchID == policeStation.userOrgBranchID) {
                    mBinding.spnPoliceStation.setSelection(index)
                    break
                }
            }
            var currentDue = BigDecimal.ZERO
            var netReceivable = BigDecimal.ZERO
            it.currentDue?.let {
                currentDue = it
            }
            it.netReceivable?.let {
                netReceivable = it
            }
            if (currentDue == netReceivable)
                mBinding.btnSave.visibility = VISIBLE
            else
                mBinding.btnSave.visibility = GONE
            it.latitude?.let {
                mBinding.edtLatitude.setText(it)
            }
            it.longitude?.let {
                mBinding.edtLongitude.setText(it)
            }
            it.street?.let {
                mBinding.edtStreet.setText(it)
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
            it.zipCode?.let {
                mBinding.edtZipCode.setText(it)
            }
            it.street?.let {
                mBinding.edtStreet.setText(it)
            }
            it.street?.let {
                mBinding.edtStreet.setText(it)
            }
            it.street?.let {
                mBinding.edtStreet.setText(it)
            }
            it.violationTicketDate?.let {
                mBinding.edtTicketDate.setText(formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyHHmmss))
            }
            it.remarks?.let {
                mBinding.edtRemarks.setText(it)
            }
            it.vehicleNo?.let {
                mBinding.edtVehicleNo.setText(it)
            }
            it.vehicleOwner?.let {
                mBinding.edtOwnerName.setText(it)
            }
            it.driver?.let {
                mBinding.edtDriverName.setText(it)
            }
            it.mobile?.let {
                mBinding.edtDriverNumber.setText(it)
            }
            it.drivingLicenseNo?.let {
                mBinding.edtDrivingLicenseNumber.setText(it)
            }
            it.violator?.let {
                mBinding.edtViolatorName.setText(it)
            }
            it.mobile?.let {
                mBinding.edtViolatorNumber.setText(it)
            }
            it.violationDetails?.let {
                mBinding.edtViolationDetails.setText(it)
            }
        }
        bindCounts()
    }

    private fun getDocumentData() {
        if (mViolationDetail?.violationTicketID != 0) {
            APICall.getDocumentDetails(
                mViolationDetail?.violationTicketID.toString(),
                "LAW_ViolationTickets",
                object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        mDocumentsList.addAll(response as ArrayList<COMDocumentReference>)
                        documentListAdapter.notifyDataSetChanged()
                        ObjectHolder.documents = mDocumentsList
                        if (mDocumentsList.isNotEmpty()) {
                            mBinding.multipleDoc.txtNoDataFound.hide()
                        } else {
                            mBinding.multipleDoc.txtNoDataFound.show()
                        }
                    }

                    override fun onFailure(message: String) {
                        if (message.isNotEmpty()) {
                            mListener?.showAlertDialog(message)
                        }
                    }
                })
        }
    }

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = arrayListOf()
        var index = -1
        var countryCode: String? = "BFA"
        mViolationDetail?.countryCode?.let {
            countryCode = it
        }
        for (country in mResponseCountriesList) {
            countries.add(country)
            if (index <= -1 && countryCode != null && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode) index = countries.indexOf(country)
        }
        if (index <= -1)
            index = 0
        if (countries.size > 0) {
            val countriesAdapter = ArrayAdapter<COMCountryMaster>(requireContext(), android.R.layout.simple_list_item_1, countries)
            mBinding.spnCountry.adapter = countriesAdapter
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
        mViolationDetail?.stateID?.let {
            stateID = it
        }
        if (TextUtils.isEmpty(countryCode)) states = java.util.ArrayList() else {
            for (state in mResponseStatesList) {
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
        mViolationDetail?.cityID?.let {
            cityID = it
        }
        if (stateID <= 0) cities = java.util.ArrayList() else {
            for (city in mResponseCitiesList) {
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
        mViolationDetail?.zone?.let {
            zoneName = it
        }
        if (cityID <= 0) zones = java.util.ArrayList() else {
            for (zone in mResponseZonesList) {
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
        mViolationDetail?.sectorID?.let {
            sectorID = it
        }
        if (zoneID <= 0) sectors = java.util.ArrayList() else {
            for (sector in mResponseSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId) index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            mBinding.spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnSector.adapter = sectorArrayAdapter
            mBinding.spnSector.setSelection(index)
        } else {
            mBinding.spnSector.adapter = null
            mBinding.spnSector.isEnabled = false
        }
    }

    private fun prepareVehicleData(): VehicleTicketData {
        val vehicleTicketData = VehicleTicketData()
        mViolationDetail?.violationTicketID?.let {
            vehicleTicketData.violationTicketId = it
        }
        if (mBinding.edtRemarks.text.toString().isNotEmpty())
            vehicleTicketData.remarks = mBinding.edtRemarks.text.toString().trim()
        if (mViolator != null)
            vehicleTicketData.violatorAccountId = mViolator?.accountID
        else if (mBusiness != null)
            vehicleTicketData.violatorAccountId = mBusiness?.accountID
        else
            vehicleTicketData.violatorAccountId = mViolationDetail?.violatorAccountID
        if (mDriver != null) {
            vehicleTicketData.drivingLicenseNumber = mDriver?.drivingLicenseNo
            vehicleTicketData.driverAccountId = mDriver?.accountID
        }
        if (mVehicleDetails != null) {
            vehicleTicketData.vehcleOwnerAccountId = mVehicleDetails?.accountId
            vehicleTicketData.vehicleNumber = mVehicleDetails?.vehicleNumber
        }
        if (mBinding.edtAmount.text.toString().isNotEmpty())
            vehicleTicketData.fineAmount = BigDecimal("${currencyToDouble(mBinding.edtAmount.text.toString().trim())}")
        if (mBinding.edtViolationDetails.text.toString().isNotEmpty())
            vehicleTicketData.violationDetails = mBinding.edtViolationDetails.text.toString().trim()
        if (mBinding.spnViolationType.selectedItem != null) {
            val violationType = mBinding.spnViolationType.selectedItem as LAWViolationType
            vehicleTicketData.violationTypeId = violationType.violationTypeID
        }
        if (mBinding.spnPoliceStation.selectedItem != null) {
            val policeStation = mBinding.spnPoliceStation.selectedItem as VUCRMPoliceStation
            vehicleTicketData.userOrgBranchId = policeStation.userOrgBranchID
        }
        if (mBinding.edtTicketDate.text.toString().isNotEmpty())
            vehicleTicketData.violationTicketDate = serverFormatDateTimeInMilliSecond(mBinding.edtTicketDate.text.toString())

        return vehicleTicketData
    }

    private fun prepareAddressData(): GeoAddress {
        val geoAddress = GeoAddress()

        /*  geoAddress.geoAddressID = vuCrmAccounts?.geoAddressID
          geoAddress.accountId = vuCrmAccounts?.accountId*/

        // region Spinner Data
        val countryMaster = mBinding.spnCountry.selectedItem as COMCountryMaster
        if (countryMaster.countryCode != null) {
            geoAddress.countryCode = countryMaster.countryCode
            geoAddress.country = countryMaster.country
        }
        val comStateMaster = mBinding.spnState.selectedItem as COMStateMaster?
        if (comStateMaster?.state != null) {
            geoAddress.state = comStateMaster.state
            geoAddress.stateID = comStateMaster.stateID
        }
        val comCityMaster = mBinding.spnCity.selectedItem as VUCOMCityMaster?
        if (comCityMaster?.city != null) geoAddress.city = comCityMaster.city
        val comZoneMaster = mBinding.spnZone.selectedItem as COMZoneMaster?
        if (comZoneMaster?.zone != null) geoAddress.zone = comZoneMaster.zone
        val comSectors = mBinding.spnSector.selectedItem as COMSectors?
        if (comSectors?.sectorId != null) {
            geoAddress.sectorID = comSectors.sectorId
            geoAddress.sector = comSectors.sector
        }
        // endregion
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) geoAddress.street = mBinding.edtStreet.text.toString().trim { it <= ' ' }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) geoAddress.zipCode = mBinding.edtZipCode.text.toString().trim { it <= ' ' }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) geoAddress.plot = mBinding.edtPlot.text.toString().trim { it <= ' ' }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) geoAddress.block = mBinding.edtBlock.text.toString().trim { it <= ' ' }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(mBinding.edtDoorNo.text.toString().trim { it <= ' ' })) geoAddress.doorNo = mBinding.edtDoorNo.text.toString().trim { it <= ' ' }
        geoAddress.latitude = mBinding.edtLatitude.text.toString().trim()
        geoAddress.longitude = mBinding.edtLongitude.text.toString().trim()

        return geoAddress
    }

    private fun saveVehicleViolationTicket() {
        mListener?.showProgressDialog()
        val fileExtension = mDocument?.extension
        val fileData = mDocument?.data

        val signature = ViolationSignature()
        val bitmap = mBinding.signatureView.getTransparentSignatureBitmap(true)
        if (bitmap != null)
            signature.data = ImageHelper.getBase64String(mBinding.signatureView.signatureBitmap)

        APICall.insertViolationTicket(prepareVehicleData(), prepareAddressData(), fileExtension, fileData, signature, object : ConnectionCallBack<ViolationTicketResponse> {
            override fun onSuccess(response: ViolationTicketResponse) {
                mListener?.dismissDialog()
                ObjectHolder.documents.clear()
                if ((response.invoiceId == null || response.invoiceId == 0) && mViolationDetail?.taxInvoiceID != null && mViolationDetail?.taxInvoiceID != 0)
                    response.invoiceId = mViolationDetail?.taxInvoiceID
                mListener?.showToast(getString(R.string.msg_record_save_success))
                Handler().postDelayed({
                    navigateToReceiptScreen(response)
                }, 500)

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun navigateToReceiptScreen(response: ViolationTicketResponse) {
        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
        intent.putParcelableArrayListExtra(Constant.KEY_VIOLATION_TICKET_RESPONSE, arrayListOf(response))
        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.VT)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
        startActivity(intent)
        activity?.finish()
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                /*R.id.llDocuments -> {
                    val fragment = DocumentsMasterFragment()
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, mViolationDetail?.violationTicketID
                            ?: 0)
                    fragment.arguments = bundle
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                    mListener?.showToolbarBackButton(R.string.documents)
                    mListener?.addFragment(fragment, true)

                }*/

                R.id.btnSave -> {
                    if (validateView())
                        saveVehicleViolationTicket()
                    else {
                    }
                }
                else -> {
                }
            }
        }
    }

   /* private fun bindCounts() {
        mBinding.txtNumberOfDocuments.text = "${ObjectHolder.documents.size}"
    }*/

    private fun fetchCount(filterColumns: List<FilterColumn>) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "COM_DocumentReferences"
        tableDetails.primaryKeyColumnName = "DocumentReferenceID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "AND"
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                //mBinding.txtNumberOfDocuments.text = "0"
            }

            override fun onSuccess(response: Int) {
                //mBinding.txtNumberOfDocuments.text = "$response"
            }
        })
    }

    private fun bindCounts() {
        if (ObjectHolder.documents.size > 0) {
            mBinding.multipleDoc.txtNoDataFound.visibility = View.GONE
        } else {
            mBinding.multipleDoc.txtNoDataFound.visibility = View.VISIBLE
        }

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "LAW_ViolationTickets"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${mViolationDetail?.violationTicketID}"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        fetchCount(listFilterColumn)
    }

    private fun validateView(): Boolean {
        if (mBinding.edtTicketDate.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.ticket_date)}")
            return false
        }

        if (mBinding.crdViolatorSelection.isVisible && mBinding.edtViolatorName.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violator_name)}")
            return false
        }

        if (mBinding.edtViolationDetails.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violation_details)}")
            return false
        }

        if (mBinding.crdVehicleAndOwnerSelection.isVisible && mBinding.edtVehicleNo.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.vehicle_no)}")
            return false
        }

        if (mBinding.spnViolationType.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violation_type)}")
            return false
        }

        if (mBinding.spnPoliceStation.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.police_station)}")
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

        return true
    }
    private fun afterDocumentResult(): COMDocumentReference {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(mImageFilePath, options)
        val mDocumentReference = COMDocumentReference()
        mDocumentReference.localPath = mImageFilePath
        mDocumentReference.data = ImageHelper.getBase64String(bitmap, 70)
        mDocumentReference.documentTypeID = 0
        mDocumentReference.documentProofType = null
        mDocumentReference.documentTypeName = null
        mDocumentReference.documentNo = "${UUID.randomUUID()}"
        mDocumentReference.documentName = mDocumentReference.documentNo
        mDocumentReference.extension = kFileExtension
        return mDocumentReference
    }
    private fun startLocalPreviewActivity(list: ArrayList<COMDocumentReference>) {
        val localDocList = list.map {
            LocalDocument(localSrc = it.localPath ?: it.awsfile)
        }
        val intent = Intent(context, LocalDocumentPreviewActivity::class.java)
        intent.putExtra(Constant.KEY_DOCUMENT, ArrayList(localDocList))
        startActivity(intent)
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun finish()
        fun showToast(message: String)
        fun showSnackbarMsg(message: String?)
        fun showProgressDialog(message: Int)
    }
}