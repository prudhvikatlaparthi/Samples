package com.sgs.citytax.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.GetAdminOfficeAddressPayload
import com.sgs.citytax.api.payload.OwnerSearchFilter
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentViolationTicketEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.LocalDocumentPreviewActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.adapter.BusinessAdapter
import com.sgs.citytax.ui.adapter.BusinessOwnersListAdapter
import com.sgs.citytax.ui.adapter.ImpoundDocumentsAdapter
import com.sgs.citytax.ui.adapter.VehicleSearchAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.first_name.view.*
import kotlinx.android.synthetic.main.zone_sector_street_layout.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ViolationTicketEntryFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentViolationTicketEntryBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var mListener: Listener? = null
    private var mCustomer: BusinessOwnership? = null
    private var mDriver: BusinessOwnership? = null
    private var mViolator: BusinessOwnership? = null
    private var mVehicleDetails: VehicleDetails? = null
    private var mBusiness: Business? = null
    private var mDocument: COMDocumentReference? = COMDocumentReference()
    private var mHelper: LocationHelper? = null

    private var mResponseCountriesList: List<COMCountryMaster> = arrayListOf()
    private var mResponseStatesList: List<COMStateMaster> = arrayListOf()
    private var mResponseCitiesList: List<VUCOMCityMaster> = arrayListOf()
    private var mResponseZonesList: List<COMZoneMaster> = arrayListOf()
    private var mResponseSectorsList: List<COMSectors> = arrayListOf()
    private var mViolators: List<LAWViolatorTypes> = arrayListOf()
    private var mPoliceStations: List<VUCRMPoliceStation> = arrayListOf()
    private var mViolationTypes: List<LAWViolationType> = arrayListOf()
    private var mViolationClasses: ArrayList<LAWViolationType> = arrayListOf()
    private var mSelectedViolationTypes: ArrayList<LAWViolationType> = arrayListOf()
    private var mSelectedViolationClasses: ArrayList<LAWViolationType> = arrayListOf()
    var isApplicableOnDriver: Boolean = false
    var event: Event? = null

    private var mImageFilePath : String? = null

    //Admin Office Address
    private var adminOfficeAdress: AdminOfficeAdress? = null
    private var telecode: Int? = null

    //citizen Contrevenant
    private lateinit var citizenContrevenantTextListener: DebouncingTextListener
    private val mCitizenContrevenantAdapter: BusinessOwnersListAdapter by lazy {
        BusinessOwnersListAdapter(object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                citizenContravenantDocList.clear()
                citizenContravenantDocumentAdapter.notifyDataSetChanged()
                mBinding.rcCitizenContrevenant.isVisible = false
                mViolator = obj as BusinessOwnership
                mBinding.edtViolatorName.removeTextChangedListener(citizenContrevenantTextListener)
                mBinding.edtViolatorName.setText(mViolator?.phone)
                mBinding.edtViolatorName.setSelection(mViolator?.phone?.length ?: 0)
                mBinding.edtViolatorName.addTextChangedListener(citizenContrevenantTextListener)
                mBinding.citizenContrevenantWrapperFName.isVisible = true
                mBinding.layoutCitizenContrevenantWrapperFName.lFirstName.hint = getString(R.string.first_name)
                mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.setText(mViolator?.firstName)
                mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.isEnabled = false



            }

            override fun onLongClick(view: View, position: Int, obj: Any) {
            }
        },true)
    }

    //business Contrevenant
    //private lateinit var citizenContrevenantTextListener: DebouncingTextListener
    private val mbusinessContrevenantAdapter: BusinessAdapter by lazy {
        BusinessAdapter(object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                mBinding.rcBusinessContrevenant.isVisible = false
                mBusiness = obj as Business
                mBinding.edtViolatorName.removeTextChangedListener(citizenContrevenantTextListener)
                mBinding.edtViolatorName.setText(mBusiness?.number)
                mBinding.edtViolatorName.setSelection(mBusiness?.number?.length ?: 0)
                mBinding.edtViolatorName.addTextChangedListener(citizenContrevenantTextListener)
                mBinding.layoutCitizenContrevenantWrapperFName.lFirstName.hint = getString(R.string.business_name)
                mBinding.citizenContrevenantWrapperFName.isVisible = true
                mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.setText(mBusiness?.businessName)
                mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.isEnabled = false

            }

            override fun onLongClick(view: View, position: Int, obj: Any) {
            }
        },true)
    }

    //Vehicle & VehOwner
    private lateinit var vehicleSearchTextListener: DebouncingTextListener
    private val vehicleSearchList: ArrayList<VehicleDetails> = arrayListOf()
    private val vehicleSearchAdapter: VehicleSearchAdapter by lazy {
        VehicleSearchAdapter(vehicleSearchList, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                vehicleDocList.clear()
                vehicleDocumentAdapter.notifyDataSetChanged()
                showHideVehicleSearchPopup(false)
                showHideVehicleDetails(false)
                mVehicleDetails = vehicleSearchList[position]
                mVehicleDetails?.vehicleNumber?.let {
                    getVehicleOwnership(it)
                    mBinding.edtVehicleNo.removeTextChangedListener(vehicleSearchTextListener)
                    mBinding.edtVehicleNo.setText(it)
                    mBinding.edtVehicleNo.setSelection(it.length)
                    mBinding.edtVehicleNo.addTextChangedListener(vehicleSearchTextListener)
                }
            }

            override fun onLongClick(view: View, position: Int, obj: Any) {

            }
        })
    }
    private lateinit var vehicleOwnerSearchTextListener: DebouncingTextListener
    private val vehicleOwnerSearchList: ArrayList<BusinessOwnership> = arrayListOf()
    private var selectedVehicleOwner: BusinessOwnership? = null
    private val vehicleOwnerSearchAdapter: BusinessOwnersListAdapter by lazy {
        BusinessOwnersListAdapter(object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                vehicleOwnerDocList.clear()
                vehicleOwnerDocumentAdapter.notifyDataSetChanged()
                showHideVehicleOwnerDetails(false)
                showHideVehicleOwnerSearchPopup(false)
                mBinding.vehCtzOwnWrapperFName.isVisible = true
                selectedVehicleOwner = vehicleOwnerSearchList[position]
                mBinding.edtvehCtzOwnName.removeTextChangedListener(vehicleOwnerSearchTextListener)
                mBinding.edtvehCtzOwnName.setText(selectedVehicleOwner?.phone)
                mBinding.edtvehCtzOwnName.setSelection(selectedVehicleOwner?.phone?.length ?: 0)
                mBinding.edtvehCtzOwnName.addTextChangedListener(vehicleOwnerSearchTextListener)
                mBinding.layoutvehCtzOwnWrapperFName.edtFirstName.setText(selectedVehicleOwner?.firstName)
                mBinding.layoutvehCtzOwnWrapperFName.edtFirstName.isEnabled = false

            }

            override fun onLongClick(view: View, position: Int, obj: Any) {

            }
        },true)
    }

    private lateinit var driverTextListener: DebouncingTextListener
    private val driverAdapter: BusinessOwnersListAdapter by lazy {
        BusinessOwnersListAdapter(object : IClickListener{
            override fun onClick(view: View, position: Int, obj: Any) {
                driverDocList.clear()
                driverDocumentAdapter.notifyDataSetChanged()
                mBinding.rcvDriver.isVisible = false
                mDriver = obj as BusinessOwnership
                mBinding.edtDriverName.removeTextChangedListener(driverTextListener)
                mBinding.edtDriverName.setText(mDriver?.accountName)
//                showDriverInfo()
//                disableEnableDriverFields(true)
                mBinding.edtDriverName.setSelection(mDriver?.accountName?.length ?:0)
                mBinding.edtDriverName.addTextChangedListener(driverTextListener)
                mBinding.driverFieldsLayout.visibility = View.GONE
                mBinding.driverFieldsWrapperFName.isVisible = true
                mBinding.layoutDriverFieldsWrapperFName.edtFirstName.setText(mDriver?.firstName)
                mBinding.layoutDriverFieldsWrapperFName.edtFirstName.isEnabled = false


            }

            override fun onLongClick(view: View, position: Int, obj: Any) {
            }
        },true)
    }

    private val kFileExtension = "jpg"

    private var vehicleDocList: ArrayList<COMDocumentReference> = arrayListOf()
    private val vehicleDocumentAdapter: ImpoundDocumentsAdapter by lazy {
        ImpoundDocumentsAdapter(vehicleDocList, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                when (view.id) {
                    R.id.imgDocument -> {
                        val comDocumentReference = obj as COMDocumentReference
                        vehicleDocList.remove(comDocumentReference)
                        vehicleDocList.add(0, comDocumentReference)
                        vehicleDocumentAdapter.notifyDataSetChanged()
                        startLocalPreviewActivity(vehicleDocList)
                    }
                    R.id.btnClearImage -> {
                        if (vehicleDocList.size > position){
                            vehicleDocList.removeAt(position)
                            vehicleDocumentAdapter.notifyDataSetChanged()
                            if (vehicleDocList.isEmpty()){
                                mBinding.vehDocWrapper.txtNoDataFound.show()
                                mBinding.vehDocWrapper.fabAddImage.enable()
                            }
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onLongClick(view: View, position: Int, obj: Any) {

            }
        })
    }

    private var vehicleOwnerDocList: ArrayList<COMDocumentReference> = arrayListOf()
    private val vehicleOwnerDocumentAdapter: ImpoundDocumentsAdapter by lazy {
        ImpoundDocumentsAdapter(vehicleOwnerDocList, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                when (view.id) {
                    R.id.imgDocument -> {
                        val comDocumentReference = obj as COMDocumentReference
                        vehicleOwnerDocList.remove(comDocumentReference)
                        vehicleOwnerDocList.add(0, comDocumentReference)
                        vehicleOwnerDocumentAdapter.notifyDataSetChanged()
                        startLocalPreviewActivity(vehicleOwnerDocList)
                    }
                    R.id.btnClearImage -> {
                        if (vehicleOwnerDocList.size > position){
                            vehicleOwnerDocList.removeAt(position)
                            vehicleOwnerDocumentAdapter.notifyDataSetChanged()
                            if (vehicleOwnerDocList.isEmpty()){
                                mBinding.layoutVehOwnAddress.documentWrapper.txtNoDataFound.show()
                                mBinding.layoutVehOwnAddress.documentWrapper.fabAddImage.enable()
                            }
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onLongClick(view: View, position: Int, obj: Any) {

            }
        })
    }

    private var driverDocList: ArrayList<COMDocumentReference> = arrayListOf()
    private val driverDocumentAdapter: ImpoundDocumentsAdapter by lazy {
        ImpoundDocumentsAdapter(driverDocList, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                when (view.id) {
                    R.id.imgDocument -> {
                        val comDocumentReference = obj as COMDocumentReference
                        driverDocList.remove(comDocumentReference)
                        driverDocList.add(0, comDocumentReference)
                        driverDocumentAdapter.notifyDataSetChanged()
                        startLocalPreviewActivity(driverDocList)
                    }
                    R.id.btnClearImage -> {
                        if (driverDocList.size > position){
                            driverDocList.removeAt(position)
                            driverDocumentAdapter.notifyDataSetChanged()
                            if (driverDocList.isEmpty()){
                                mBinding.layoutDriverAddress.documentWrapper.txtNoDataFound.show()
                                mBinding.layoutDriverAddress.documentWrapper.fabAddImage.enable()
                            }
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onLongClick(view: View, position: Int, obj: Any) {

            }
        })
    }

    private var citizenContravenantDocList: ArrayList<COMDocumentReference> = arrayListOf()
    private val citizenContravenantDocumentAdapter: ImpoundDocumentsAdapter by lazy {
        ImpoundDocumentsAdapter(citizenContravenantDocList, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                when (view.id) {
                    R.id.imgDocument -> {
                        val comDocumentReference = obj as COMDocumentReference
                        citizenContravenantDocList.remove(comDocumentReference)
                        citizenContravenantDocList.add(0, comDocumentReference)
                        citizenContravenantDocumentAdapter.notifyDataSetChanged()
                        startLocalPreviewActivity(citizenContravenantDocList)
                    }
                    R.id.btnClearImage -> {
                        if (citizenContravenantDocList.size > position){
                            citizenContravenantDocList.removeAt(position)
                            citizenContravenantDocumentAdapter.notifyDataSetChanged()
                            if (citizenContravenantDocList.isEmpty()){
                                mBinding.layoutCitizenContrevenantAddress.documentWrapper.txtNoDataFound.show()
                                mBinding.layoutCitizenContrevenantAddress.documentWrapper.fabAddImage.enable()
                            }
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onLongClick(view: View, position: Int, obj: Any) {

            }
        })
    }
    // Multiple docs
    private var mDocumentsList: ArrayList<COMDocumentReference> = arrayListOf()
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
                                mBinding.multipleDoc.txtNoDataFound.visibility = View.GONE
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_violation_ticket_entry, container, false)
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
        bindVehicleDetails()
        setListeners()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: kotlin.Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mHelper?.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mHelper?.onActivityResult(requestCode, resultCode)
        bindCounts()

        if (requestCode == Constant.ContraventionDocument.VehicleDocument.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.vehDocWrapper.txtNoDataFound.hide()
                mBinding.vehDocWrapper.fabAddImage.disable()
                vehicleDocList.add(doc)
                vehicleDocumentAdapter.notifyDataSetChanged()
            } else {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        } else if (requestCode == Constant.ContraventionDocument.VehicleOwnerDocument.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.layoutVehOwnAddress.documentWrapper.txtNoDataFound.hide()
                mBinding.layoutVehOwnAddress.documentWrapper.fabAddImage.disable()
                vehicleOwnerDocList.add(doc)
                vehicleOwnerDocumentAdapter.notifyDataSetChanged()
            } else {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        } else if (requestCode == Constant.ContraventionDocument.DriverDocument.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.layoutDriverAddress.documentWrapper.txtNoDataFound.hide()
                mBinding.layoutDriverAddress.documentWrapper.fabAddImage.disable()
                driverDocList.add(doc)
                driverDocumentAdapter.notifyDataSetChanged()
            } else {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        }else if (requestCode == Constant.ContraventionDocument.CitizenDocument.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.layoutCitizenContrevenantAddress.documentWrapper.txtNoDataFound.hide()
                mBinding.layoutCitizenContrevenantAddress.documentWrapper.fabAddImage.disable()
                citizenContravenantDocList.add(doc)
                citizenContravenantDocumentAdapter.notifyDataSetChanged()
            } else {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        }else if (requestCode == Constant.ContraventionDocument.MultipleDocuments.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.multipleDoc.txtNoDataFound.hide()
                mDocumentsList.add(doc)
                documentListAdapter.notifyDataSetChanged()
                ObjectHolder.documents = mDocumentsList
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                mCustomer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setCustomerInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_DRIVER_SEARCH) {
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                mDriver = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
//                setDriverInfo()
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
//                    setDriverInfo()
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

    private fun bindCounts() {
//        mBinding.txtNumberOfDocuments.text = "${ObjectHolder.documents.size}"
        mBinding.txtNoOfViolations.text = "${ObjectHolder.violations.size}"
        for ((index,obj) in ObjectHolder.violations.withIndex())
        {
            if (obj.applicableOnDriver == "Y")
            {
                isApplicableOnDriver = true
            }
            else
            {
                isApplicableOnDriver = false
            }
        }
        //TODO: Remobed this condition as per the update by Rakesh : 03-02-21
        /*if (isApplicableOnDriver)
        {
            setTextInputLayoutHintColor(mBinding.edtDriverNameLayout, mBinding.edtDriverNameLayout.context, R.color.hint_color)
        }
        else
        {
            setTextInputLayoutHintColor(mBinding.edtDriverNameLayout, mBinding.edtDriverNameLayout.context, R.color.colorGray)
        }*/
    }

    fun setTextInputLayoutHintColor(textInputLayout: TextInputLayout, context: Context, @ColorRes colorIdRes: Int) {
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
    }

    private fun showHideVehicleSearchPopup(isShow : Boolean){
        mBinding.rcVehicleSearch.isVisible = isShow
    }

    private fun showHideVehicleOwnerSearchPopup(isShow : Boolean){
        mBinding.rcVehCtzOwn.isVisible = isShow
    }

    private fun showHideVehicleDetails(isShow: Boolean) {
        if (isShow) {
            mBinding.vehDetailsWrapper.visibility = View.VISIBLE
        } else {
            mBinding.vehDetailsWrapper.visibility = View.GONE
        }
    }

    private fun showHideVehicleOwnerDetails(isShow: Boolean) {
        if (isShow) {
            mBinding.vehCtzOwnWrapper.visibility = View.VISIBLE
            mBinding.vehCtzOwnWrapperFName.isVisible = true
            mBinding.layoutvehCtzOwnWrapperFName.edtFirstName.setText("")
            mBinding.layoutVehOwnAddress.edtLastName.setText("")

        } else {
            mBinding.vehCtzOwnWrapper.visibility = View.GONE
        }
    }

    private fun performVehicleSearch() {
        mVehicleDetails = null
        mBinding.vehDocWrapper.txtNoDataFound.show()
        mBinding.vehDocWrapper.fabAddImage.enable()
        vehicleDocList.clear()
        vehicleDocumentAdapter.notifyDataSetChanged()
        if (mBinding.edtVehicleNo.text.toString().length > 2) {
            getVehicleDetailsWithOwner(
                searchData = mBinding.edtVehicleNo.text.toString(),
            )
        } else {
            mVehicleDetails = null
            showHideVehicleDetails(false)
            showHideVehicleSearchPopup(false)
        }
    }

    private fun performVehicleOwnerSearch() {
        selectedVehicleOwner = null
        vehicleOwnerDocList.clear()
        mBinding.layoutVehOwnAddress.documentWrapper.txtNoDataFound.show()
        mBinding.layoutVehOwnAddress.documentWrapper.fabAddImage.enable()
        vehicleOwnerDocumentAdapter.notifyDataSetChanged()
        if (mBinding.edtvehCtzOwnName.text.toString().length > 2) {
            getVehicleOwnerSearchResult(
                data = mBinding.edtvehCtzOwnName.text.toString(),
            )
        } else {
            selectedVehicleOwner = null
            showHideVehicleOwnerDetails(false)
            showHideVehicleOwnerSearchPopup(false)
            mBinding.vehCtzOwnWrapperFName.isVisible = false
            mBinding.layoutvehCtzOwnWrapperFName.edtFirstName.setText("")
            mBinding.layoutVehOwnAddress.edtLastName.setText("")

        }
    }

    private fun getVehicleDetailsWithOwner(searchData: String) {
        mVehicleDetails = null
        vehicleSearchList.clear()
        mBinding.vehicleNoProgressView.isVisible = true
        APICall.getVehicleDetailsWithOwner(
            searchData,
            object : ConnectionCallBack<VehicleDetailsWithOwnerResponse> {
                override fun onSuccess(response: VehicleDetailsWithOwnerResponse) {
                    mBinding.vehicleNoProgressView.isVisible = false
                    val data = response.vehicleDetails
                    if (data?.isNotEmpty() == true) {
                        showHideVehicleSearchPopup(true)
                        showHideVehicleDetails(false)
                        vehicleSearchList.addAll(data)
                        vehicleSearchAdapter.notifyDataSetChanged()
                    } else{
                        showHideVehicleDetails(true)
                        showHideVehicleSearchPopup(false)
                    }
                }

                override fun onFailure(message: String) {
                    mBinding.vehicleNoProgressView.isVisible = false
                    showHideVehicleDetails(true)
                    showHideVehicleSearchPopup(false)
                }
            })
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

    private fun getBusinessOwnerSearchResult(data: String) {
        mBinding.driverProgressView.isVisible = true
        /*if(mCode== Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS || mCode== Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS){
            //Need to handle Inactive condition
        }
        val searchFilter = OwnerSearchFilter()
        *//*searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex*//*
        searchFilter.query = null
        *//* "Advsrchfilter": {
             "FilterColumns": [
             {
                 "colname": "AccountName",
                 "ColumnValue": "sa",
                 "SrchType": "Like"
             },
             {
                 "colname": "BusinessOwnerID",
                 "ColumnValue": "sa",
                 "SrchType": "Equal"
             },
             {
                 "colname": "CitizenID",
                 "ColumnValue": "sa",
                 "SrchType": "Equal"
             },
             {
                 "colname": "PhoneNumbers",
                 "ColumnValue": "sa",
                 "SrchType": "Like"
             },
             {
                 "colname": "Emails",
                 "ColumnValue": "sa",
                 "SrchType": "Like"
             }
             ],*//*
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "AccountName"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
//        filterColumn = FilterColumn()
//        filterColumn.columnName = "Status"
//        filterColumn.columnValue = "Active"
//        filterColumn.srchType = "Equal"
//        listFilterColumn.add(listFilterColumn.size, filterColumn)
        *//*if (mCode != Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
            && mCode != Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER
            && mCode != Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE
            && mCode != Constant.QuickMenu.QUICK_MENU_IMPONDMENT) {
            filterColumn = FilterColumn()
            filterColumn.columnName = "BusinessOwnerID"
            filterColumn.columnValue = data
            filterColumn.srchType = "Equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
        }*//*
        filterColumn = FilterColumn()
        filterColumn.columnName = "BusinessOwnerID"
        filterColumn.columnValue = data
        filterColumn.srchType = "Equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "CitizenID"
        filterColumn.columnValue = data
        filterColumn.srchType = "Equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PhoneNumbers"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "Emails"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        searchFilter.filterColumns = listFilterColumn
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_OwnerDetails"
        tableDetails.primaryKeyColumnName = "AccountID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "OR"
        *//*if (mCode == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
            || mCode == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE || mCode == Constant.QuickMenu.QUICK_MENU_IMPONDMENT
            || mCode == Constant.QuickMenu.QUICK_MENU_PROPERTY_NOMINEE|| mCode==Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
            tableDetails.initialTableCondition = "AccountTypeCode = 'CUS' AND StatusCode = 'CRM_Contacts.Active'"
        else if(mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER){
            tableDetails.initialTableCondition = "AccountTypeCode = 'CUS'"
        }*//*
        tableDetails.initialTableCondition = "AccountTypeCode = 'CUS' AND StatusCode = 'CRM_Contacts.Active'"
        *//****
         * this logic is to return both business & citizen ids irrespective of mCode
         *//*
        *//*if(showCitizenBusiness.equals(CITIZEN_BUSINESS, ignoreCase = true)){
            filterColumn = FilterColumn()
            filterColumn.columnName = "BusinessOwnerID"
            filterColumn.columnValue = data
            filterColumn.srchType = "Equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            tableDetails.initialTableCondition = ""
        }*//*
        searchFilter.tableDetails = tableDetails
//        val businessOwners = BusinessOwnership()
//        businessOwners.isLoading = true
//        (mBinding.rcvDriver.adapter as BusinessOwnersListAdapter).add(businessOwners)
//        isLoading = true*/
        val searchFilter = getCitizenSearchFilterPayload(data)
        APICall.getBusinessOwners(searchFilter, object : ConnectionCallBack<BusinessOwnerResponse> {
            override fun onSuccess(response: BusinessOwnerResponse) {
                mBinding.driverProgressView.isVisible = false
                val count: Int = response.results.businessOwner.size
                /* if (count < pageSize) {
                     hasMoreData = false
                 } else
                     pageIndex += 1*/
                /*if (pageIndex == 1 && response.results.businessOwner.isEmpty())
                    mAdapter.clear()*/
                driverAdapter.clear()
                driverAdapter.addAll(response.results.businessOwner)
                mBinding.rcvDriver.isVisible = true
                if (count == 0){
                    mBinding.rcvDriver.isVisible = false
                    disableEnableDriverFields(true)
                }
//                isLoading = false
            }
            override fun onFailure(message: String) {
                mBinding.driverProgressView.isVisible = false
//                mListener?.showAlertDialog(message)
                mBinding.rcvDriver.isVisible = false
                disableEnableDriverFields(true)
                mBinding.layoutDriverFieldsWrapperFName.edtFirstName.isEnabled = true
//                isLoading = false
            }
        })
    }

    private fun getVehicleOwnerSearchResult(data: String) {
        selectedVehicleOwner = null
        mBinding.vehicleOwnerProgressView.isVisible = true
        /*if(mCode== Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS || mCode== Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS){
            //Need to handle Inactive condition
        }
        val searchFilter = OwnerSearchFilter()
        *//*searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex*//*
        searchFilter.query = null
        *//* "Advsrchfilter": {
             "FilterColumns": [
             {
                 "colname": "AccountName",
                 "ColumnValue": "sa",
                 "SrchType": "Like"
             },
             {
                 "colname": "BusinessOwnerID",
                 "ColumnValue": "sa",
                 "SrchType": "Equal"
             },
             {
                 "colname": "CitizenID",
                 "ColumnValue": "sa",
                 "SrchType": "Equal"
             },
             {
                 "colname": "PhoneNumbers",
                 "ColumnValue": "sa",
                 "SrchType": "Like"
             },
             {
                 "colname": "Emails",
                 "ColumnValue": "sa",
                 "SrchType": "Like"
             }
             ],*//*
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "AccountName"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
//        filterColumn = FilterColumn()
//        filterColumn.columnName = "Status"
//        filterColumn.columnValue = "Active"
//        filterColumn.srchType = "Equal"
//        listFilterColumn.add(listFilterColumn.size, filterColumn)
        *//*if (mCode != Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
            && mCode != Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER
            && mCode != Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE
            && mCode != Constant.QuickMenu.QUICK_MENU_IMPONDMENT) {
            filterColumn = FilterColumn()
            filterColumn.columnName = "BusinessOwnerID"
            filterColumn.columnValue = data
            filterColumn.srchType = "Equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
        }*//*
        filterColumn = FilterColumn()
        filterColumn.columnName = "BusinessOwnerID"
        filterColumn.columnValue = data
        filterColumn.srchType = "Equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "CitizenID"
        filterColumn.columnValue = data
        filterColumn.srchType = "Equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PhoneNumbers"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "Emails"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        searchFilter.filterColumns = listFilterColumn
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_OwnerDetails"
        tableDetails.primaryKeyColumnName = "AccountID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "OR"
        *//*if (mCode == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
            || mCode == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE || mCode == Constant.QuickMenu.QUICK_MENU_IMPONDMENT
            || mCode == Constant.QuickMenu.QUICK_MENU_PROPERTY_NOMINEE|| mCode==Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
            tableDetails.initialTableCondition = "AccountTypeCode = 'CUS' AND StatusCode = 'CRM_Contacts.Active'"
        else if(mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER){
            tableDetails.initialTableCondition = "AccountTypeCode = 'CUS'"
        }*//*
        tableDetails.initialTableCondition = "AccountTypeCode = 'CUS' AND StatusCode = 'CRM_Contacts.Active'"
        *//****
         * this logic is to return both business & citizen ids irrespective of mCode
         *//*
        *//*if(showCitizenBusiness.equals(CITIZEN_BUSINESS, ignoreCase = true)){
            filterColumn = FilterColumn()
            filterColumn.columnName = "BusinessOwnerID"
            filterColumn.columnValue = data
            filterColumn.srchType = "Equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            tableDetails.initialTableCondition = ""
        }*//*
        searchFilter.tableDetails = tableDetails*/
        val searchFilter = getCitizenSearchFilterPayload(data)
        APICall.getBusinessOwners(searchFilter, object : ConnectionCallBack<BusinessOwnerResponse> {
            override fun onSuccess(response: BusinessOwnerResponse) {
                mBinding.vehicleOwnerProgressView.isVisible = false
                val count: Int = response.results.businessOwner.size
                if (count > 0) {
                    showHideVehicleOwnerSearchPopup(true)
                    showHideVehicleOwnerDetails(false)
                    //mBinding.vehCtzOwnWrapperFName.isVisible = true
                    vehicleOwnerSearchList.clear()
                    vehicleOwnerSearchAdapter.clear()
                    vehicleOwnerSearchList.addAll(response.results.businessOwner)
                    vehicleOwnerSearchAdapter.addAll(vehicleOwnerSearchList)
                } else {
                    showHideVehicleOwnerSearchPopup(false)
                    showHideVehicleOwnerDetails(true)
                }

            }
            override fun onFailure(message: String) {
                mBinding.vehicleOwnerProgressView.isVisible = false
                showHideVehicleOwnerSearchPopup(false)
                showHideVehicleOwnerDetails(true)
                mBinding.layoutvehCtzOwnWrapperFName.edtFirstName.isEnabled = true
            }
        })
    }

    private fun getCitizenSearchFilterPayload(data: String): OwnerSearchFilter {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS || fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
            //Need to handle Inactive condition
        }
        val searchFilter = OwnerSearchFilter()
        searchFilter.query = null
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "AccountName"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "BusinessOwnerID"
        filterColumn.columnValue = data
        filterColumn.srchType = "Equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "CitizenID"
        filterColumn.columnValue = data
        filterColumn.srchType = "Equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PhoneNumbers"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "Emails"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        searchFilter.filterColumns = listFilterColumn
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_OwnerDetails"
        tableDetails.primaryKeyColumnName = "AccountID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "OR"
        tableDetails.initialTableCondition =
            "AccountTypeCode = 'CUS' AND StatusCode = 'CRM_Contacts.Active'"
        searchFilter.tableDetails = tableDetails
        return searchFilter
    }

    private fun performDriverSearch() {
        mBinding.layoutDriverAddress.documentWrapper.txtNoDataFound.show()
        mBinding.layoutDriverAddress.documentWrapper.fabAddImage.enable()
        mDriver = null
        driverDocList.clear()
        driverDocumentAdapter.notifyDataSetChanged()
        if (mBinding.edtDriverName.text.toString().length > 2) {
            getBusinessOwnerSearchResult(data = mBinding.edtDriverName.text.toString())
        } else {
            mBinding.rcvDriver.isVisible = false
            mBinding.edtDrivingLicenseNumber.setText("")
            disableEnableDriverFields(false)
            mBinding.driverFieldsWrapperFName.isVisible = false
            mBinding.layoutDriverFieldsWrapperFName.edtFirstName.setText("")
            mBinding.layoutDriverAddress.edtLastName.setText("")


        }
    }

    private fun disableEnableDriverFields(enableView: Boolean) {
        if (enableView) {
            mBinding.driverFieldsLayout.visibility = View.VISIBLE
            mBinding.driverFieldsWrapperFName.isVisible = true
            mBinding.layoutDriverFieldsWrapperFName.edtFirstName.setText("")
            mBinding.layoutDriverAddress.edtLastName.setText("")
        } else {
            mBinding.driverFieldsLayout.visibility = View.GONE
            mBinding.layoutDriverFieldsWrapperFName.edtFirstName.setText("")
            mBinding.layoutDriverAddress.edtLastName.setText("")
        }
        mBinding.edtDrivingLicenseNumber.isEnabled = enableView
        mBinding.layoutDriverAddress.spnZone.isEnabled = enableView
        mBinding.layoutDriverAddress.spnSector.isEnabled = enableView
        mBinding.layoutDriverAddress.edtStreet.isEnabled = enableView
    }

    private fun getCitizenContrevenantSearchResult(data: String) {
        mViolator = null
        val searchFilter = getCitizenSearchFilterPayload(data)
        mBinding.citizenContrevenantProgressView.isVisible = true
        APICall.getBusinessOwners(searchFilter, object : ConnectionCallBack<BusinessOwnerResponse> {
            override fun onSuccess(response: BusinessOwnerResponse) {
                mBinding.citizenContrevenantProgressView.isVisible = false
                mBinding.citizenContrevenantWrapper.visibility = View.GONE
                mBinding.rcCitizenContrevenant.isVisible = true
                val count: Int = response.results.businessOwner.size
                if (count > 0) {
                    mBinding.rcCitizenContrevenant.isVisible = true
                    mCitizenContrevenantAdapter.clear()
                    response.results.businessOwner.let {
                        mCitizenContrevenantAdapter.addAll(it)
                        mCitizenContrevenantAdapter.notifyDataSetChanged()
                    }
                    mBinding.citizenContrevenantWrapper.visibility = View.GONE
                } else {
                    mBinding.rcCitizenContrevenant.isVisible = false
                    mBinding.citizenContrevenantWrapper.visibility = View.VISIBLE
                    mBinding.layoutCitizenContrevenantWrapperFName.lFirstName.hint = getString(R.string.first_name)
                    mBinding.citizenContrevenantWrapperFName.isVisible = true
                    mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.setText("")
                    mBinding.citizenContrevenantWrapper.edtFirstName.setText("")


                }
            }
            override fun onFailure(message: String) {
                mBinding.rcCitizenContrevenant.isVisible = false
                mBinding.citizenContrevenantProgressView.isVisible = false
                mBinding.citizenContrevenantWrapper.visibility = View.VISIBLE
                mBinding.layoutCitizenContrevenantWrapperFName.lFirstName.hint = getString(R.string.first_name)
                mBinding.citizenContrevenantWrapperFName.isVisible = true
                mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.setText("")
                mBinding.layoutCitizenContrevenantAddress.edtLastName.setText("")
                mCitizenContrevenantAdapter.clear()
                mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.isEnabled = true

            }
        })
    }


    private fun performCitizenContravenantSearch() {
        mViolator = null
        citizenContravenantDocList.clear()
        citizenContravenantDocumentAdapter.notifyDataSetChanged()
        if (mBinding.edtViolatorName.text.toString().length > 2) {
            getCitizenContrevenantSearchResult(data = mBinding.edtViolatorName.text.toString())
        } else {
            mBinding.rcCitizenContrevenant.isVisible = false
            mBinding.citizenContrevenantWrapper.isVisible = false
            mBinding.citizenContrevenantWrapperFName.isVisible = false
            mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.setText("")
            mBinding.layoutCitizenContrevenantAddress.edtLastName.setText("")

        }
    }

    private fun getBusinessContravenantSearchResult(data: String) {
        mBusiness = null
        mBinding.citizenContrevenantProgressView.isVisible = true
        APICall.getBusiness(data, object : ConnectionCallBack<BusinessResponse> {
            override fun onSuccess(response: BusinessResponse) {
                mBinding.citizenContrevenantProgressView.isVisible = false
                val count = response.businessOwner?.size
                if (count == null || count == 0) {
                    mBinding.rcBusinessContrevenant.isVisible = false
                    mbusinessContrevenantAdapter.clear()
                } else {
                    mBinding.rcBusinessContrevenant.isVisible = true
                    mbusinessContrevenantAdapter.clear()
                    mbusinessContrevenantAdapter.addAll(response.businessOwner!!)
                }
//                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.citizenContrevenantProgressView.isVisible = false
                mBinding.rcBusinessContrevenant.isVisible = false
//                mListener?.dismissDialog()
//                mListener?.showAlertDialog(message)
                mbusinessContrevenantAdapter.clear()
            }
        })
    }

    private fun performBusinessContrevenantSearch() {
        mBusiness = null
        if (mBinding.edtViolatorName.text.toString().length > 2) {
            getBusinessContravenantSearchResult(data = mBinding.edtViolatorName.text.toString())
        } else {
            mBinding.rcBusinessContrevenant.isVisible = false
            mBinding.citizenContrevenantWrapper.isVisible = false
            mBinding.citizenContrevenantWrapperFName.isVisible = false
            mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.setText("")
            mBinding.layoutCitizenContrevenantAddress.edtLastName.setText("")
        }
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
            if (it.containsKey(Constant.KEY_DOCUMENT))
                mDocument = it.getParcelable(Constant.KEY_DOCUMENT)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setViews() {
        // Multiple documents
        mBinding.multipleDoc.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.multipleDoc.rcDocuments.adapter = documentListAdapter

        mBinding.layoutCitizenContrevenantAddress.documentWrapper.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.layoutCitizenContrevenantAddress.documentWrapper.rcDocuments.adapter = citizenContravenantDocumentAdapter

        mBinding.vehDocWrapper.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.vehDocWrapper.rcDocuments.adapter = vehicleDocumentAdapter

        mBinding.layoutVehOwnAddress.documentWrapper.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.layoutVehOwnAddress.documentWrapper.rcDocuments.adapter = vehicleOwnerDocumentAdapter

        mBinding.layoutDriverAddress.documentWrapper.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.layoutDriverAddress.documentWrapper.rcDocuments.adapter = driverDocumentAdapter

//        mBinding.crdVehicleAndOwnerSelection.visibility = View.GONE
        mBinding.crdDriverSelection.visibility = View.GONE
        mBinding.crdViolatorSelection.visibility = View.GONE
        /*mBinding.edtStartDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtStartDate.setText(getDate(Calendar.getInstance().time, displayDateTimeTimeSecondFormat))
        mBinding.edtStartDate.setDisplayDateFormat(displayDateTimeTimeSecondFormat)*/

        mBinding.edtStartDate.setText(formatDisplayDateTimeInMillisecond(Date()))

        // Citizen Contrevenant
        val citizenContrevenantLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(requireContext())
        mBinding.rcCitizenContrevenant.layoutManager = citizenContrevenantLayoutManager
        mBinding.rcCitizenContrevenant.adapter = mCitizenContrevenantAdapter
        mBinding.rcCitizenContrevenant.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )

        // Business Contrevenant
        val businessContrevenantLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(requireContext())
        mBinding.rcBusinessContrevenant.layoutManager = businessContrevenantLayoutManager
        mBinding.rcBusinessContrevenant.adapter = mbusinessContrevenantAdapter
        mBinding.rcBusinessContrevenant.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )

        // Vehicle
        val vehicleSearchLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        mBinding.rcVehicleSearch.layoutManager = vehicleSearchLayoutManager
        mBinding.rcVehicleSearch.adapter = vehicleSearchAdapter
        mBinding.rcVehicleSearch.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL)
        )


        // Vehicle Owner
        mBinding.edtVehRegistrationDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtVehRegistrationDate.setDisplayDateFormat(displayDateFormat)
        val vehicleOwnerSearchLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        mBinding.rcVehCtzOwn.layoutManager = vehicleOwnerSearchLayoutManager
        mBinding.rcVehCtzOwn.adapter = vehicleOwnerSearchAdapter
        mBinding.rcVehCtzOwn.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL)
        )

        mBinding.edtVehOwnerFromDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtVehOwnerFromDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtVehOwnerFromDate.setText(getDate(setCalendarText(Calendar.JANUARY, 1), displayDateFormat))

        // Driver
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        mBinding.rcvDriver.layoutManager = layoutManager
        mBinding.rcvDriver.adapter = driverAdapter
        mBinding.rcvDriver.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL)
        )

    }

    private fun bindVehicleDetails() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("ADM_Vehicles", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                if (response.vehicleTypes?.isNotEmpty() == true) {
                    mBinding.spnVehicleType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, response.vehicleTypes!!)
                } else
                    mBinding.spnVehicleType.adapter = null

                if (response.statusCodes.isNotEmpty()) {
                    mBinding.spnVehicleStatus.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, response.statusCodes!!)
                } else
                    mBinding.spnVehicleStatus.adapter = null

                if (response.comboStaticValues.isNotEmpty()) {
                    val mFuelTypes = arrayListOf<ComComboStaticValues>()
                    val mTransmissionTypes = arrayListOf<ComComboStaticValues>()
                    for (value in response.comboStaticValues) {
                        if (value.comboCode == "VehicleFuel") {
                            mFuelTypes.add(value)
                        } else if (value.comboCode == "VehicleTransmission") {
                            mTransmissionTypes.add(value)
                        }
                    }

                    if (mFuelTypes.isNotEmpty()) {
                        mBinding.spnFuelType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, mFuelTypes)
                    } else
                        mBinding.spnFuelType.adapter = null


                    if (mTransmissionTypes.isNotEmpty()) {
                        mBinding.spnTransmissionType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, mTransmissionTypes)
                    } else
                        mBinding.spnTransmissionType.adapter = null
                }

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnTransmissionType.adapter = null
                mBinding.spnVehicleType.adapter = null
                mBinding.spnVehicleStatus.adapter = null
                mBinding.spnFuelType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
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
                mViolators = response.violatorTypes
                mPoliceStations = response.policeStations
                mViolationTypes = response.violationTypes
                mViolationClasses = arrayListOf()
                mViolationClasses.addAll(response.violationTypes)

//                filterCountries()

                if (mViolators.isNotEmpty()) {
                    lawViolatorTypes()
                }

                if (mPoliceStations.isNotEmpty()) {
                    val policeStationAdapter = ArrayAdapter<VUCRMPoliceStation>(requireContext(), android.R.layout.simple_list_item_1, mPoliceStations)
                    mBinding.spnPoliceStation.adapter = policeStationAdapter
                    setStation()
                }
                getAdminOfficeAddressData()
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun getAdminOfficeAddressData() {
        val adminAdressPayload = GetAdminOfficeAddressPayload()
        adminAdressPayload.accountID = MyApplication.getPrefHelper().accountId
        APICall.getAdminOfficeAddress(
            adminAdressPayload,
            object : ConnectionCallBack<AdminOfficeAdress> {
                override fun onSuccess(response: AdminOfficeAdress) {
                    adminOfficeAdress = response
//                    val cityID = adminOfficeAdress?.ctyid ?: 0
//                    filterZones(cityID)
                    telecode = response.telephoneCode
                    mBinding.edtCountry.setText(adminOfficeAdress?.cntry)
                    mBinding.edtState.setText(adminOfficeAdress?.st)
                    mBinding.edtCity.setText(adminOfficeAdress?.cty)
                    setZones(mResponseZonesList.getOrNull(0)?.zoneID ?: 0, mBinding.spnZone)
                    setSector(mResponseSectorsList.getOrNull(0)?.sectorId ?: 0, mBinding.spnSector)

                    //Contrevention citizen
                    setZones(
                        mResponseZonesList.getOrNull(0)?.zoneID ?: 0,
                        mBinding.layoutCitizenContrevenantAddress.spnZone
                    )
                    setSector(
                        mResponseSectorsList.getOrNull(0)?.sectorId ?: 0,
                        mBinding.layoutCitizenContrevenantAddress.spnSector
                    )

                    //vehicle owner
                    setZones(mResponseZonesList.getOrNull(0)?.zoneID ?: 0, mBinding.layoutVehOwnAddress.spnZone)
                    setSector(mResponseSectorsList.getOrNull(0)?.sectorId?: 0, mBinding.layoutVehOwnAddress.spnSector)

                    //driver name
                    setZones(mResponseZonesList.getOrNull(0)?.zoneID ?: 0, mBinding.layoutDriverAddress.spnZone)
                    setSector(mResponseSectorsList.getOrNull(0)?.sectorId?: 0, mBinding.layoutDriverAddress.spnSector)

                }

                override fun onFailure(message: String) {
                }
            })
    }

    private fun setZones(zoneID: Int, spnZone: AppCompatSpinner) {
        if (zoneID == 0) {
            spnZone.adapter = null
        } else {
            val zoneArrayAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    mResponseZonesList
                )
            spnZone.adapter = zoneArrayAdapter
        }
        mResponseZonesList.forEachIndexed { index, comZoneMaster ->
            if (comZoneMaster.zoneID == zoneID) {
                spnZone.setSelection(index)
            }
        }
    }
    private fun setSector(sectorId: Int, spnSector: AppCompatSpinner) {
        if (sectorId == 0) {
            spnSector.adapter = null
        } else {
            val sectorArrayAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    mResponseSectorsList
                )
            spnSector.adapter = sectorArrayAdapter
        }
        mResponseSectorsList.forEachIndexed { index, comZoneMaster ->
            if (comZoneMaster.sectorId == sectorId) {
                spnSector.setSelection(index)
            }
        }
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

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = arrayListOf()
        var index = -1
        val countryCode: String? = "BFA"
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
        val stateID = 100497
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
        val cityID = 100312093
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
            filterZones(cities[index].cityID!!, mBinding.spnZone, mBinding.spnSector)
        } else {
            mBinding.spnCity.adapter = null
            filterZones(cityID, mBinding.spnZone, mBinding.spnSector)
        }
    }

    private fun filterZones(cityID: Int, spnZone: AppCompatSpinner, spnSector: AppCompatSpinner) {
        var zones: MutableList<COMZoneMaster> = java.util.ArrayList()
        var index = 0
        var zoneName: String? = ""
        if (cityID <= 0) zones = java.util.ArrayList() else {
            for (zone in mResponseZonesList) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone) index = zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            spnZone.adapter = zoneArrayAdapter
            spnZone.setSelection(index)
            filterSectors(zones[index].zoneID!!, spnSector)
        } else {
            spnZone.adapter = null
            filterSectors(0, spnSector)
        }
    }

    private fun filterSectors(zoneID: Int, spnSector: AppCompatSpinner) {
        var sectors: MutableList<COMSectors?> = java.util.ArrayList()
        var index = 0
        var sectorID = 0
        if (zoneID <= 0) sectors = java.util.ArrayList() else {
            for (sector in mResponseSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId) index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            spnSector.adapter = sectorArrayAdapter
            spnSector.setSelection(index)
        } else {
            spnSector.adapter = null
            spnSector.isEnabled = false
        }
    }

    private fun showMoreAddressDetails() {
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

    private fun hideAddressDetails() {
        mBinding.edtCountryLayout.visibility = View.GONE
        mBinding.edtStateLayout.visibility = View.GONE
        mBinding.edtCityLayout.visibility = View.GONE
        mBinding.edtStreetLayout.visibility = View.GONE
        mBinding.edtSectionLayout.visibility = View.GONE
        mBinding.edtLotLayout.visibility = View.GONE
        mBinding.edtParcelLayout.visibility = View.GONE
        mBinding.edtZipLayout.visibility = View.GONE
        showUnderLineText(mBinding.btnShowMoreOrLess, getString(R.string.show_more))
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
//        mBinding.llDocuments.setOnClickListener(this)
        mBinding.llViolationTypes.setOnClickListener(this)

        mBinding.btnShowMoreOrLess.setOnClickListener {
            if (mBinding.btnShowMoreOrLess.text.toString()
                    .equals(getString(R.string.show_more), true)
            ) {
                showMoreAddressDetails()
            } else {
                hideAddressDetails()
            }
        }
        //Citizen Contrevenant
        citizenContrevenantTextListener =
            DebouncingTextListener(viewLifecycleOwner.lifecycle) { _, newText ->
                newText?.let {
                    checkContrevenantType()
                }
            }
        mBinding.rcCitizenContrevenant.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        mBinding.rcBusinessContrevenant.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        mBinding.edtViolatorName.addTextChangedListener(citizenContrevenantTextListener)


        mBinding.edtViolatorName.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                checkContrevenantType()
                return@OnEditorActionListener true
            }
            false
        })
        //Citizen Contrevenant End

       /* mBinding.edtDriverName.setOnClickListener {
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
        }*/

       /* mBinding.edtViolatorName.setOnClickListener {
            val violatorType = mBinding.spnViolator.selectedItem as LAWViolatorTypes
            if (violatorType.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.code)
                showCitizenViolators()
            else if (violatorType.violatorTypeCode == Constant.ViolationTypeCode.BUSINESS.code)
                showBusinessViolators()
        }

        mBinding.edtViolatorNumber.setOnClickListener {
            val violationType = mBinding.spnViolator.selectedItem as LAWViolatorTypes
            if (violationType.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.code)
                showCitizenViolators()
            else if (violationType.violatorTypeCode == Constant.ViolationTypeCode.BUSINESS.code)
                showBusinessViolators()
        }*/


        /*mBinding.tvCreateDriver.setOnClickListener {
            val fragment = BusinessOwnerEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            //endregion

            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DRIVER)

            mListener?.showToolbarBackButton(R.string.driver)
            mListener?.addFragment(fragment, true)
        }*/

        /*mBinding.tvCreateViolator.setOnClickListener {
            val fragment = BusinessOwnerEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            //endregion

            fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATOR)

            //mListener?.showToolbarBackButton(R.string.driver)
            mListener?.addFragment(fragment, true)
        }*/

/*
        mBinding.edtVehicleNo.setOnClickListener {
            val fragment = VehicleSearchFragment()
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_VEHICLE_SEARCH)
            mListener?.addFragment(fragment, true)
        }
*/

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

        mBinding.spnViolator.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mBinding.edtViolatorName.setText("")
                mBinding.edtVehicleNo.setText("")
                mBinding.edtvehCtzOwnName.setText("")
                mBinding.edtDriverName.setText("")
                if (mBinding.spnZone.adapter?.count ?: 0 > 0 && mBinding.spnSector.adapter?.count ?: 0 > 0) {
                    mBinding.spnZone.setSelection(0)
                    mBinding.spnSector.setSelection(0)
                }
                if(mBinding.spnViolator.selectedItem.toString() == getString(R.string.citizen)){
                    mBinding.edtViolatorName.inputType = InputType.TYPE_CLASS_NUMBER
                }else{
                    mBinding.edtViolatorName.inputType = InputType.TYPE_CLASS_TEXT
                }

                var violatorType = LAWViolatorTypes()

                if (p0 != null && p0.selectedItem != null)
                    violatorType = p0.selectedItem as LAWViolatorTypes

                if (violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()) == Constant.ViolationTypeCode.VEHICLE.code) {
                    mBinding.crdVehicleAndOwnerSelection.visibility = View.VISIBLE
                    mBinding.crdDriverSelection.visibility = View.VISIBLE
                } else {
                    mBinding.crdVehicleAndOwnerSelection.visibility = View.GONE
                    mBinding.crdDriverSelection.visibility = View.GONE
                    /*if (violatorType.violatorTypeCode == Constant.ViolationTypeCode.BUSINESS.code){
                        mBinding.edtViolatorName.addTextChangedListener(getBusinessContrevenantSearchResult())
                    }else{

                    }*/
                }

                if (violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.BUSINESS.code)
                        || violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.CITIZEN.code)) {
                    mBinding.crdViolatorSelection.visibility = View.VISIBLE
                   /* if (violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.CITIZEN.code)) {
                        mBinding.llCreateViolator.visibility = View.VISIBLE
                    } else {
                        mBinding.llCreateViolator.visibility = View.GONE
                    }*/
                } else
                    mBinding.crdViolatorSelection.visibility = View.GONE

                mSelectedViolationTypes.clear()

                for (violationType in mViolationTypes) {
                    if (violationType.parentViolationTypeID != null
                            && violationType.violatorTypeCode == violatorType.violatorTypeCode)
                        mSelectedViolationTypes.add(violationType)
                }

                mSelectedViolationClasses.clear()

                for (violationClass in mViolationClasses) {
                    if (violationClass.parentViolationTypeID == null
                            && violationClass.violatorTypeCode == violatorType.violatorTypeCode && isValidParent(violationClass)) {
                        mSelectedViolationClasses.add(violationClass)
                    }
                }

                /* filterViolationClasses(violationType.parentViolationTypeID ?: 0)
                 fetchAmount(violationType.violationTypeID ?: 0)*/

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
                filterZones(city?.cityID!!, mBinding.spnZone, mBinding.spnSector)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!, mBinding.spnSector)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.layoutVehOwnAddress.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!, mBinding.layoutVehOwnAddress.spnSector)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.layoutCitizenContrevenantAddress.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!, mBinding.layoutCitizenContrevenantAddress.spnSector)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        mBinding.layoutDriverAddress.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!,mBinding.layoutDriverAddress.spnSector)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        vehicleSearchTextListener =
            DebouncingTextListener(viewLifecycleOwner.lifecycle) { _, newText ->
                newText?.let {
                    performVehicleSearch()
                }
            }

        mBinding.rcVehicleSearch.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        mBinding.edtVehicleNo.addTextChangedListener(vehicleSearchTextListener)

        mBinding.edtVehicleNo.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performVehicleSearch()
                return@OnEditorActionListener true
            }
            false
        })

        vehicleOwnerSearchTextListener =
            DebouncingTextListener(viewLifecycleOwner.lifecycle) { _, newText ->
                newText?.let {
                    performVehicleOwnerSearch()
                }
            }

        mBinding.rcVehCtzOwn.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        mBinding.edtvehCtzOwnName.addTextChangedListener(vehicleOwnerSearchTextListener)

        mBinding.edtvehCtzOwnName.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performVehicleOwnerSearch()
                return@OnEditorActionListener true
            }
            false
        })

        driverTextListener =
            DebouncingTextListener(viewLifecycleOwner.lifecycle) { _, newText ->
                newText?.let {
                    performDriverSearch()
                }
            }
        mBinding.rcvDriver.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        mBinding.edtDriverName.addTextChangedListener(driverTextListener)
        mBinding.edtDriverName.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performDriverSearch()
                return@OnEditorActionListener true
            }
            false
        })

        mBinding.vehDocWrapper.fabAddImage.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ContraventionDocument.VehicleDocument.value)
            }
        })

        mBinding.layoutVehOwnAddress.documentWrapper.fabAddImage.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ContraventionDocument.VehicleOwnerDocument.value)
            }
        })

        mBinding.layoutDriverAddress.documentWrapper.fabAddImage.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ContraventionDocument.DriverDocument.value)
            }
        })

        mBinding.multipleDoc.fabAddImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ContraventionDocument.MultipleDocuments.value)
            }
        })

        mBinding.layoutCitizenContrevenantAddress.documentWrapper.fabAddImage.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ContraventionDocument.CitizenDocument.value)
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

    //this method is to check whether the Contrevene type is Business or Citizen
    private fun checkContrevenantType(){
        val violatorType = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
        if (violatorType?.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.code)
            performCitizenContravenantSearch()
        else if (violatorType?.violatorTypeCode == Constant.ViolationTypeCode.BUSINESS.code)
            performBusinessContrevenantSearch()
    }

    private fun isValidParent(violationClass: LAWViolationType): Boolean {
        for (item in mSelectedViolationTypes) {
            if (item.parentViolationTypeID == violationClass.violationTypeID) {
                return true
            }
        }
        return false
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

    private fun setCustomerInfo() {
        mCustomer?.let {
//            mBinding.edtOwnerName.setText(it.accountName)
        }
    }

    private fun setDriverInfo() {
        mDriver?.let {
            mBinding.edtDriverName.setText(it.accountName)
            //mBinding.edtDriverNumber.setText(it.phone)
            mBinding.edtDrivingLicenseNumber.setText(it.drivingLicenseNo)
        }
    }

    private fun setViolatorInfo() {
        mViolator?.let {
            mBinding.edtViolatorName.setText(it.accountName)
//            mBinding.edtViolatorNumber.setText(it.phone)
        }
    }

    private fun setBusinessInfo() {
        mBusiness?.let {
            mBinding.edtViolatorName.setText(it.businessName)
//            mBinding.edtViolatorNumber.setText(it.number)
        }
    }

    private fun setVehicleInfo() {
        mVehicleDetails?.let {
//            mBinding.edtVehicleNo.setText(it.vehicleNumber)
//            mBinding.edtOwnerName.setText(it.owner ?: "")
        }
    }

    private fun prepareVehicleData(): VehicleTicketData {
        val vehicleTicketData = VehicleTicketData()
        if (mBusiness != null)
            vehicleTicketData.violatorAccountId = mBusiness?.accountID

        if (mViolator == null) {
            val violatorModel = CitizenModel(
                phone = mBinding.edtViolatorName.text.toString().trim(),
                name = mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.text.toString().trim(),
                lastName = mBinding.layoutCitizenContrevenantAddress.edtLastName.text.toString(),
                TelephoneCode = telecode,
                geoAddress = GeoAddress(
                    addressID = null,
                    geoAddressID = null,
                    accountId = 0,
                    geoAddressType = null,
                    countryCode = adminOfficeAdress?.cntrycode,
                    country = adminOfficeAdress?.cntry,
                    stateID = adminOfficeAdress?.stid,
                    state = adminOfficeAdress?.st,
                    cityID = adminOfficeAdress?.ctyid,
                    city = adminOfficeAdress?.cty,
                    zone = (mBinding.layoutCitizenContrevenantAddress.spnZone.selectedItem as COMZoneMaster?)?.zone,
                    sectorID = (mBinding.layoutCitizenContrevenantAddress.spnSector.selectedItem as COMSectors?)?.sectorId,
                    street = mBinding.layoutCitizenContrevenantAddress.edtStreet.text.toString(),
                    description = null,
                    sector = (mBinding.layoutCitizenContrevenantAddress.spnSector.selectedItem as COMSectors?)?.sector
                ), FileNameWithExtsn = if (citizenContravenantDocList.isNotEmpty()) citizenContravenantDocList[0].documentName.plus(".${kFileExtension}") else null,
                FileData = if (citizenContravenantDocList.isNotEmpty()) citizenContravenantDocList[0].data else null
            )
            vehicleTicketData.violatorModel = violatorModel
        } else {
            mViolator?.accountID?.let {
                vehicleTicketData.violatorAccountId = it
            }
        }
        /*if (mDriver != null) {
            vehicleTicketData.drivingLicenseNumber = mDriver?.drivingLicenseNo
            vehicleTicketData.driverAccountId = mDriver?.accountID
        }*/
        if(mBinding.edtDriverName.text.toString().isNotEmpty()) {
            if (mBinding.crdDriverSelection.isVisible && mDriver == null) {
                val driverModel = CitizenModel(
                    phone = mBinding.edtDriverName.text.toString().trim(),
                    name = mBinding.layoutDriverFieldsWrapperFName.edtFirstName.text.toString(),
                    lastName = mBinding.layoutDriverAddress.edtLastName.text.toString(),
                    TelephoneCode = telecode,
                    geoAddress = GeoAddress(
                        addressID = null,
                        geoAddressID = null,
                        accountId = 0,
                        geoAddressType = null,
                        countryCode = adminOfficeAdress?.cntrycode,
                        country = adminOfficeAdress?.cntry,
                        stateID = adminOfficeAdress?.stid,
                        state = adminOfficeAdress?.st,
                        cityID = adminOfficeAdress?.ctyid,
                        city = adminOfficeAdress?.cty,
                        zone = (mBinding.layoutDriverAddress.spnZone.selectedItem as COMZoneMaster?)?.zone,
                        sectorID = (mBinding.layoutDriverAddress.spnSector.selectedItem as COMSectors?)?.sectorId,
                        street = mBinding.layoutDriverAddress.edtStreet.text.toString(),
                        description = null,
                        sector = (mBinding.layoutDriverAddress.spnSector.selectedItem as COMSectors?)?.sector
                    ), FileNameWithExtsn = if (driverDocList.isNotEmpty()) driverDocList[0].documentName.plus(
                        ".${kFileExtension}"
                    ) else null,
                    FileData = if (driverDocList.isNotEmpty()) driverDocList[0].data else null
                )
                vehicleTicketData.driverModel = driverModel
            } else {
                mDriver?.accountID?.let {
                    vehicleTicketData.driverAccountId = it
                }
                mDriver?.drivingLicenseNo?.let {
                    vehicleTicketData.drivingLicenseNumber = it
                }
            }
        }

       /* if (mVehicleDetails != null) {
            vehicleTicketData.vehcleOwnerAccountId = mVehicleDetails?.accountId
            vehicleTicketData.vehicleNumber = mVehicleDetails?.vehicleNumber
        }*/

        val violatorType: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
        var vehicleModel : VehicleModel? =null
        if (violatorType?.violatorTypeCode?.toUpperCase() == Constant.ViolationTypeCode.VEHICLE.code) {
            if (mVehicleDetails == null) {
                vehicleModel = VehicleModel(
                    VehicleNo = mBinding.edtVehicleNo.text.toString().trim(),
                    RegistrationNo = mBinding.edtVehicleNo.text.toString().trim(),
                    transmission = (mBinding.spnTransmissionType.selectedItem as ComComboStaticValues?)?.comboValue,
                    fuelType = (mBinding.spnFuelType.selectedItem as ComComboStaticValues?)?.comboValue,
                    registrationDate = formatDateTimeInMillisecond(
                        parseDate(
                            mBinding.edtVehRegistrationDate.text.toString().trim(), displayDateFormat
                        )
                    ),
                    vehicleTypeCode = (mBinding.spnVehicleType.selectedItem as ADMVehicleTypes?)?.vehicleTypeCode,
                    statusCode = (mBinding.spnVehicleStatus.selectedItem as COMStatusCode?)?.statusCode,
                    FileNameWithExtsn = if (vehicleDocList.isNotEmpty()) vehicleDocList[0].documentName.plus(".${kFileExtension}") else null,
                    FileData = if (vehicleDocList.isNotEmpty()) vehicleDocList[0].data else null
                )
            } else {
                if (mBinding.edtVehicleNo.text != null && !TextUtils.isEmpty(mBinding.edtVehicleNo.text.toString()))
                    vehicleTicketData.vehicleNumber = mBinding.edtVehicleNo.text.toString().trim()
            }
        }
        vehicleTicketData.vehicleModel = vehicleModel
        var ownerModel: OwnerModel? = null
        if (mVehicleDetails != null && selectedVehicleOwner == null) {
            mVehicleDetails?.accountId?.let {
                vehicleTicketData.vehcleOwnerAccountId = it
            }
        } else {
            ownerModel = OwnerModel(
                phone = if (mVehicleDetails == null) mBinding.edtvehCtzOwnName.text.toString()
                    .trim() else null,
                name = mBinding.vehCtzOwnWrapperFName.edtFirstName.text.toString(),
                lastName = mBinding.layoutVehOwnAddress.edtLastName.text.toString(),
                TelephoneCode = telecode,
                geoAddress = if (mVehicleDetails == null) GeoAddress(
                    addressID = null,
                    geoAddressID = null,
                    accountId = 0,
                    geoAddressType = null,
                    countryCode = adminOfficeAdress?.cntrycode,
                    country = adminOfficeAdress?.cntry,
                    stateID = adminOfficeAdress?.stid,
                    state = adminOfficeAdress?.st,
                    cityID = adminOfficeAdress?.ctyid,
                    city = adminOfficeAdress?.cty,
                    zone = (mBinding.layoutVehOwnAddress.spnZone.selectedItem as COMZoneMaster?)?.zone,
                    sectorID = (mBinding.layoutVehOwnAddress.spnSector.selectedItem as COMSectors?)?.sectorId,
                    street = mBinding.layoutVehOwnAddress.edtStreet.text.toString(),
                    description = null,
                    sector = (mBinding.layoutVehOwnAddress.spnSector.selectedItem as COMSectors?)?.sector
                ) else null,
                acctid = mVehicleDetails?.accountId,
                fromDate = if (mVehicleDetails == null) serverFormatDate(mBinding.edtVehOwnerFromDate.text.toString()) else null
                , FileNameWithExtsn = if (vehicleOwnerDocList.isNotEmpty()) vehicleOwnerDocList[0].documentName.plus(".${kFileExtension}") else null,
                FileData = if (vehicleOwnerDocList.isNotEmpty()) vehicleOwnerDocList[0].data else null
            )
        }
        vehicleTicketData.ownerModel = ownerModel

        if (mBinding.spnPoliceStation.selectedItem != null) {
            val policeStation = mBinding.spnPoliceStation.selectedItem as VUCRMPoliceStation
            vehicleTicketData.userOrgBranchId = policeStation.userOrgBranchID
        }
        if (mBinding.edtStartDate.text.toString().isNotEmpty())
            vehicleTicketData.violationTicketDate = serverFormatDateTimeInMilliSecond(mBinding.edtStartDate.text.toString())
        if (mBinding.edtHeaderRemarks.text.toString().isNotEmpty())
            vehicleTicketData.remarks = mBinding.edtHeaderRemarks.text.toString()

        return vehicleTicketData
    }

    private fun prepareAddressData(): GeoAddress {
        val geoAddress = GeoAddress()

        /*  geoAddress.geoAddressID = vuCrmAccounts?.geoAddressID
          geoAddress.accountId = vuCrmAccounts?.accountId*/
        // region Spinner Data
        /*val countryMaster = mBinding.spnCountry.selectedItem as COMCountryMaster
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
        if (comCityMaster?.city != null) geoAddress.city = comCityMaster.city*/
        geoAddress.countryCode = adminOfficeAdress?.cntrycode
        geoAddress.state = adminOfficeAdress?.st
        geoAddress.city = adminOfficeAdress?.cty
        geoAddress.cityID = adminOfficeAdress?.ctyid
        geoAddress.stateID = adminOfficeAdress?.stid
        geoAddress.sector = adminOfficeAdress?.sec
        val comZoneMaster = mBinding.spnZone.selectedItem as COMZoneMaster?
        if (comZoneMaster?.zone != null) geoAddress.zone = comZoneMaster.zone
        val comSectors = mBinding.spnSector.selectedItem as COMSectors?
        if (comSectors?.sectorId != null) {
            geoAddress.sectorID = comSectors.sectorId
            geoAddress.sector = comSectors.sector
        }
        // endregion
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) geoAddress.street =
            mBinding.edtStreet.text.toString().trim { it <= ' ' }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) geoAddress.zipCode =
            mBinding.edtZipCode.text.toString().trim { it <= ' ' }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) geoAddress.plot =
            mBinding.edtPlot.text.toString().trim { it <= ' ' }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) geoAddress.block =
            mBinding.edtBlock.text.toString().trim { it <= ' ' }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(
                mBinding.edtDoorNo.text.toString().trim { it <= ' ' })
        ) geoAddress.doorNo = mBinding.edtDoorNo.text.toString().trim { it <= ' ' }
        geoAddress.latitude = mBinding.edtLatitude.text.toString().trim()
        geoAddress.longitude = mBinding.edtLongitude.text.toString().trim()

        return geoAddress
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
               /* R.id.llDocuments -> {
                    val fragment = LocalDocumentsMasterFragment()
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                    mListener?.showToolbarBackButton(R.string.documents)
                    mListener?.addFragment(fragment, true)
                }*/
                R.id.llViolationTypes -> {
                    val fragment = ViolationTypeMasterFragment()
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(Constant.KEY_VIOLATION_TYPES, mSelectedViolationTypes)
                    bundle.putParcelableArrayList(Constant.KEY_VIOLATION_CLASSES, mSelectedViolationClasses)
                    fragment.arguments = bundle
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATION_TYPE_LIST)
                    mListener?.addFragment(fragment, true)
                }
                R.id.btnSave -> {
                    if(validateView())
                    {
                        saveViolations()
                    }
                    else{}


                }
                else -> {
                }
            }
        }
    }

    private fun saveViolations() {
        mListener?.showProgressDialog()
        val fileExtension = mDocument?.extension
        val fileData = mDocument?.data

        val signature = ViolationSignature()
        val bitmap = mBinding.signatureView.getTransparentSignatureBitmap(true)
        if (bitmap != null)
            signature.data = ImageHelper.getBase64String(mBinding.signatureView.signatureBitmap)

        APICall.insertMultipleViolations(prepareVehicleData(), prepareAddressData(), fileExtension, fileData, signature, object : ConnectionCallBack<List<ViolationTicketResponse>> {
            override fun onSuccess(response: List<ViolationTicketResponse>) {
                mListener?.dismissDialog()
                ObjectHolder.documents.clear()
                ObjectHolder.violations.clear()
                val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                intent.putParcelableArrayListExtra(Constant.KEY_VIOLATION_TICKET_RESPONSE, response as ArrayList<out Parcelable>)
                intent.putExtra(Constant.KEY_IMPOUNDMENT_VIOLATION_ID, response.getOrNull(0)?.ticketId)
                intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.VT)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
                startActivity(intent)
                activity?.finish()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun validateView(): Boolean {
        if (mBinding.edtStartDate.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.ticket_date)}")
            return false
        }
        val violator: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?

        if (mBinding.crdViolatorSelection.isVisible && violator?.violatorTypeCode == Constant.ViolationTypeCode.BUSINESS.code && mBusiness == null) {
            mBinding.edtViolatorName.requestFocus()
            mBinding.edtViolatorName.setText("")
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.contravenant_number)}")
            return false
        }

        if (mBinding.crdViolatorSelection.isVisible && violator?.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.code) {
            if (mViolator == null && !mBinding.citizenContrevenantWrapper.isVisible) {
                mBinding.edtViolatorName.requestFocus()
                mBinding.edtViolatorName.setText("")
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.contravenant_number)}")
                return false
            }
            if(mViolator == null && mBinding.citizenContrevenantWrapperFName.isVisible){
                if(mBinding.layoutCitizenContrevenantWrapperFName.edtFirstName.text.toString().isEmpty()){
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violator)} ${getString(R.string.first_name)}")
                    return false
                }
            }
            if (mViolator == null && mBinding.citizenContrevenantWrapper.isVisible){
                if(mBinding.layoutCitizenContrevenantAddress.edtLastName.text.toString().isEmpty()){
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violator)} ${getString(R.string.last_name)}")
                    return false
                }
                if (mBinding.layoutCitizenContrevenantAddress.spnZone.selectedItem == null) {
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violator)} ${getString(R.string.zone)}")
                    return false
                }
                if (mBinding.layoutCitizenContrevenantAddress.spnSector.selectedItem == null) {
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violator)} ${getString(R.string.sector)}")
                    return false
                }
            }

        }

        /*if (mBinding.crdVehicleAndOwnerSelection.isVisible && mBinding.edtVehicleNo.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.vehicle_no)}")
            return false
        }*/

        if (mBinding.spnPoliceStation.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.police_station)}")
            return false
        }
       /* if (mBinding.spnCountry.selectedItem == null) {
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
        }*/
        if (mBinding.spnZone.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.zone)}")
            return false
        }
        if (mBinding.spnSector.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.sector)}")
            return false
        }


        if (violator?.violatorTypeCode == "VEHICLE" && mBinding.crdVehicleAndOwnerSelection.isVisible)
        {
           /* mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.vehicle_number)}")
            mBinding.edtVehicleNo.requestFocus()
            return false*/

            if (mBinding.edtVehicleNo.text.toString().trim()
                    .isEmpty() || (mVehicleDetails == null && !mBinding.vehDetailsWrapper.isVisible)
            ) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.vehicle_registration_no)}")
                mBinding.edtVehicleNo.setText("")
                mBinding.edtVehicleNo.requestFocus()
                return false
            }
            if (mVehicleDetails == null && mBinding.vehDetailsWrapper.isVisible) {
                if (mBinding.edtVehRegistrationDate.text != null && TextUtils.isEmpty(
                        mBinding.edtVehRegistrationDate.text.toString().trim()
                    )
                ) {
                    mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.registration_date))
                    return false
                }
                val transmissionTypes =
                    mBinding.spnTransmissionType.selectedItem as ComComboStaticValues?
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
                if (mBinding.edtvehCtzOwnName.text.toString().isEmpty() || (selectedVehicleOwner == null && !mBinding.vehCtzOwnWrapper.isVisible)){
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.owner_number)}")
                    mBinding.edtvehCtzOwnName.setText("")
                    mBinding.edtvehCtzOwnName.requestFocus()
                    return false
                }
                if (mBinding.edtVehOwnerFromDate.text.toString().trim().isEmpty()) {
                    mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.from_date))
                    return false
                }
                if(mBinding.vehCtzOwnWrapperFName.isVisible){
                    if(mBinding.layoutvehCtzOwnWrapperFName.edtFirstName.text.toString().isEmpty()){
                        mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.vehicle_owner)} ${getString(R.string.first_name)}")
                        return false
                    }
                }
                if (mBinding.vehCtzOwnWrapper.isVisible){
                    if(mBinding.layoutVehOwnAddress.edtLastName.text.toString().isEmpty()){
                        mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.vehicle_owner)} ${getString(R.string.last_name)}")
                        return false
                    }
                    if (mBinding.layoutVehOwnAddress.spnZone.selectedItem == null) {
                        mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.vehicle_owner)} ${getString(R.string.zone)}")
                        return false
                    }
                    if (mBinding.layoutVehOwnAddress.spnSector.selectedItem == null) {
                        mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.vehicle_owner)} ${getString(R.string.sector)}")
                        return false
                    }
                }
            }

        }

        if(mBinding.driverFieldsWrapperFName.isVisible && mBinding.layoutDriverFieldsWrapperFName.edtFirstName.text.toString().isEmpty()){
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.driver)} ${getString(R.string.first_name)}")
            return false
        }
        if (mBinding.driverFieldsLayout.isVisible && mBinding.layoutDriverAddress.edtLastName.text.toString().isEmpty()){
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.driver)} ${getString(R.string.last_name)}")
            return false
        }
        if (ObjectHolder.violations.isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide_violations))
            return false
        }
        //TODO: Remobed this condition as per the update by Rakesh : 03-02-21
        /* if (isApplicableOnDriver)
         {
             mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.driver)}")
             mBinding.edtDriverName.requestFocus()
             return false
         }*/

        return true
    }


    private fun lawViolatorTypes() {
        mBinding.spnViolator.adapter = null
        var lawViolatorTypes: ArrayList<LAWViolatorTypes> = arrayListOf()
        for (violationType in mViolators) {
            if (violationType.violatorTypeCode?.toUpperCase(Locale.getDefault()) != Constant.ViolationTypeCode.ANIMAL.code)
                lawViolatorTypes.add(violationType)
        }
        if (lawViolatorTypes.size > 0) {
            val adapter = ArrayAdapter<LAWViolatorTypes>(requireContext(), android.R.layout.simple_list_item_1, lawViolatorTypes)
            mBinding.spnViolator.adapter = adapter
        }
    }

    fun isBusinessContrevenantPopUpVisible(): Boolean {
        return mBinding.rcBusinessContrevenant.isVisible
    }
    fun isCitizenContrevenantPopUpVisible(): Boolean {
        return mBinding.rcCitizenContrevenant.isVisible
    }

    fun isPoprcVehCtzOwnUpVisible(): Boolean {
        return mBinding.rcVehCtzOwn.isVisible
    }

    fun isPoprcVehicleSearchUpVisible(): Boolean {
        return mBinding.rcVehicleSearch.isVisible
    }

    fun isPoprcvDriverUpVisible(): Boolean {
        return mBinding.rcvDriver.isVisible
    }

    fun onBackPressed() {
        when {
            isBusinessContrevenantPopUpVisible() -> {
                mBinding.rcBusinessContrevenant.isVisible = false
            }
            isCitizenContrevenantPopUpVisible() -> {
                mBinding.rcCitizenContrevenant.isVisible = false
            }
            isPoprcVehCtzOwnUpVisible() -> {
                mBinding.rcVehCtzOwn.isVisible = false
            }
            isPoprcVehicleSearchUpVisible() -> {
                mBinding.rcVehicleSearch.isVisible = false
            }
            isPoprcvDriverUpVisible() -> {
                mBinding.rcvDriver.isVisible = false
            }
        }
    }

    private fun startLocalPreviewActivity(list: ArrayList<COMDocumentReference>) {
        val localDocList = list.map {
            LocalDocument(localSrc = it.localPath)
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