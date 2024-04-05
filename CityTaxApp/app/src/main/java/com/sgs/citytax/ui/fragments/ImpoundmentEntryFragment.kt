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
import android.os.Handler
import android.provider.MediaStore
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentImpoundmentEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.LocalDocumentPreviewActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.adapter.BusinessAdapter
import com.sgs.citytax.ui.adapter.BusinessOwnersListAdapter
import com.sgs.citytax.ui.adapter.ImpoundDocumentsAdapter
import com.sgs.citytax.ui.adapter.VehicleSearchAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.KEY_DOCUMENT
import com.sgs.citytax.util.Constant.KEY_IMPOUNDMENT_VIOLATION_ID
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU
import kotlinx.android.synthetic.main.fragment_impoundment_entry.view.*
import kotlinx.android.synthetic.main.zone_sector_street_layout.view.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class ImpoundmentEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentImpoundmentEntryBinding
    private var mListener: Listener? = null
    private var mHelper: LocationHelper? = null
    private var mCountries: List<COMCountryMaster> = arrayListOf()
    private var mStates: List<COMStateMaster> = arrayListOf()
    private var mCities: List<VUCOMCityMaster> = arrayListOf()
    private var mZones: List<COMZoneMaster> = arrayListOf()
    private var mSectors: List<COMSectors> = arrayListOf()
    private var mImpoundmentSubTypes: ArrayList<LAWImpoundmentSubType> = arrayListOf()
    private var mImpoundmentTypes: ArrayList<LAWImpoundmentType> = arrayListOf()
    private var mPoliceStationYards: ArrayList<PoliceStationYards> = arrayListOf()
    private var mPoliceStations: List<VUCRMPoliceStation> = arrayListOf()
    private var mCraneTypes:ArrayList<VULAWTowingCraneTypes> = arrayListOf()
    private var mImpoundmentReasons: ArrayList<LAWImpoundmentReason> = arrayListOf()
    private var mViolationTypes: ArrayList<LAWViolationType> = arrayListOf()
    private var mDriver: BusinessOwnership? = null
    private var mAnimalViolator: BusinessOwnership? = null
    private var mImpoundFrom: BusinessOwnership? = null
    private var mVehicle: VehicleDetails? = null
    private var mBusiness: Business? = null
    private var mImpoundment: Impoundment? = null
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE
    private var mDocument: COMDocumentReference? = COMDocumentReference()
    private var mViolators: List<LAWViolatorTypes> = arrayListOf()
    private var telecode: Int? = null


//    private var mViolationClasses: java.util.ArrayList<LAWViolationType> = arrayListOf()
//    val childViolatons: java.util.ArrayList<LAWViolationType> = arrayListOf()


    val childViolatons: java.util.ArrayList<LAWViolationType> = arrayListOf()
    private var mViolationClasses: java.util.ArrayList<LAWViolationType> = arrayListOf()

    private var mSelectedViolationTypes: java.util.ArrayList<LAWViolationType> = arrayListOf()
    private var mSelectedViolationClasses: java.util.ArrayList<LAWViolationType> = arrayListOf()

    private var selectedViolatorType: LAWViolationType? = null
    var itemTypeSelector: AdapterView.OnItemSelectedListener? = null
    var itemClassSelector: AdapterView.OnItemSelectedListener? = null


    var event: Event? = null
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
    private var mImageFilePath : String? = null

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
                mVehicle = vehicleSearchList[position]
                mVehicle?.vehicleNumber?.let {
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
    private var adminOfficeAdress: AdminOfficeAdress? = null
    private lateinit var goodsOwnerTextListener: DebouncingTextListener
    private val mBusAdapter: BusinessAdapter by lazy {
        BusinessAdapter(object : IClickListener{
            override fun onClick(view: View, position: Int, obj: Any) {
                mBinding.rcGoodsOwner.isVisible = false
                mBusiness = obj as Business
                mBinding.edtGoodsOwner.removeTextChangedListener(goodsOwnerTextListener)
                mBinding.edtGoodsOwner.setText(mBusiness?.businessName)
                mBinding.edtGoodsOwner.setSelection(mBusiness?.businessName?.length ?:0)
                mBinding.edtGoodsOwner.addTextChangedListener(goodsOwnerTextListener)
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
                mBinding.edtDriverName.setText(mDriver?.phone)
//                showDriverInfo()
//                disableEnableDriverFields(true)
                mBinding.edtDriverName.setSelection(mDriver?.phone?.length ?:0)
                mBinding.edtDriverName.addTextChangedListener(driverTextListener)
                mBinding.driverFieldsLayout.visibility = GONE
                mBinding.driverFieldsWrapperFName.isVisible = true
                mBinding.layoutDriverFieldsWrapperFName.edtFirstName.setText(mDriver?.firstName)
                mBinding.layoutDriverFieldsWrapperFName.edtFirstName.isEnabled = false

            }

            override fun onLongClick(view: View, position: Int, obj: Any) {
            }
        },true)
    }

    //animalOwner
    private lateinit var animalOwnerTextListener: DebouncingTextListener
    private val animalOwnerAdapter: BusinessOwnersListAdapter by lazy {
        BusinessOwnersListAdapter(object : IClickListener{
            override fun onClick(view: View, position: Int, obj: Any) {
                mBinding.rcAnimalOwner.isVisible = false
                mAnimalViolator = obj as BusinessOwnership
                mBinding.edtAnimalViolatorOwner.removeTextChangedListener(animalOwnerTextListener)
                mBinding.edtAnimalViolatorOwner.setText(mAnimalViolator?.phone)
                mBinding.edtAnimalViolatorOwner.setSelection(mAnimalViolator?.phone?.length ?:0)
                mBinding.edtAnimalViolatorOwner.addTextChangedListener(animalOwnerTextListener)
                mBinding.animalOwnerWrapperFName.isVisible = true
                mBinding.layoutAnimalOwnerFName.edtFirstName.setText(mAnimalViolator?.firstName ?: mAnimalViolator?.accountName )
                mBinding.layoutAnimalOwnerFName.edtFirstName.isEnabled = false
                if(mAnimalViolator?.statusCode == Constant.OrganizationStatus.ACTIVE.value){
                    mBinding.layoutAnimalOwnerFName.lFirstName.hint = getString(R.string.business_name)
                }else{
                    mBinding.layoutAnimalOwnerFName.lFirstName.hint = getString(R.string.first_name)
                }

            }
            override fun onLongClick(view: View, position: Int, obj: Any) {
            }
        },true)
    }

    //Impound From Animal
    private lateinit var animalImpoundTextListener: DebouncingTextListener
    private val mImpoundFromAdapter: BusinessOwnersListAdapter by lazy {
        BusinessOwnersListAdapter(object : IClickListener{
            override fun onClick(view: View, position: Int, obj: Any) {
                mBinding.rcImpoundFrom.isVisible = false
                mImpoundFrom = obj as BusinessOwnership
                mBinding.edtAnimalImpondFrom.removeTextChangedListener(animalImpoundTextListener)
                mBinding.edtAnimalImpondFrom.setText(mImpoundFrom?.phone)
                mBinding.edtAnimalImpondFrom.setSelection(mImpoundFrom?.phone?.length ?:0)
                mBinding.edtAnimalImpondFrom.addTextChangedListener(animalImpoundTextListener)
                mBinding.impoundFromWrapperFName.isVisible = true
                mBinding.layoutImpoundFromWrapperFName.edtFirstName.setText(mImpoundFrom?.firstName?: mImpoundFrom?.accountName)
                mBinding.layoutImpoundFromWrapperFName.edtFirstName.isEnabled = false
                if(mImpoundFrom?.statusCode == Constant.OrganizationStatus.ACTIVE.value){
                    mBinding.layoutImpoundFromWrapperFName.lFirstName.hint = getString(R.string.business_name)
                }else{
                    mBinding.layoutImpoundFromWrapperFName.lFirstName.hint = getString(R.string.first_name)
                }

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

    private var animalOwnerDocList: ArrayList<COMDocumentReference> = arrayListOf()
    private val animalOwnerDocumentAdapter: ImpoundDocumentsAdapter by lazy {
        ImpoundDocumentsAdapter(animalOwnerDocList, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                when (view.id) {
                    R.id.imgDocument -> {
                        val comDocumentReference = obj as COMDocumentReference
                        animalOwnerDocList.remove(comDocumentReference)
                        animalOwnerDocList.add(0, comDocumentReference)
                        animalOwnerDocumentAdapter.notifyDataSetChanged()
                        startLocalPreviewActivity(animalOwnerDocList)
                    }
                    R.id.btnClearImage -> {
                        if (animalOwnerDocList.size > position){
                            animalOwnerDocList.removeAt(position)
                            animalOwnerDocumentAdapter.notifyDataSetChanged()
                            if (animalOwnerDocList.isEmpty()){
                                mBinding.layoutAnimalOwnerAddress.documentWrapper.txtNoDataFound.show()
                                mBinding.layoutAnimalOwnerAddress.documentWrapper.fabAddImage.enable()
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

    private var impoundFromDocList: ArrayList<COMDocumentReference> = arrayListOf()
    private val impoundFromDocumentAdapter: ImpoundDocumentsAdapter by lazy {
        ImpoundDocumentsAdapter(impoundFromDocList, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                when (view.id) {
                    R.id.imgDocument -> {
                        val comDocumentReference = obj as COMDocumentReference
                        impoundFromDocList.remove(comDocumentReference)
                        impoundFromDocList.add(0, comDocumentReference)
                        impoundFromDocumentAdapter.notifyDataSetChanged()
                        startLocalPreviewActivity(impoundFromDocList)
                    }
                    R.id.btnClearImage -> {
                        if (impoundFromDocList.size > position){
                            impoundFromDocList.removeAt(position)
                            impoundFromDocumentAdapter.notifyDataSetChanged()
                            if (impoundFromDocList.isEmpty()){
                                mBinding.layoutImpoundFromAddress.documentWrapper.txtNoDataFound.show()
                                mBinding.layoutImpoundFromAddress.documentWrapper.fabAddImage.enable()
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

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(KEY_QUICK_MENU))
                mCode = it.getSerializable(KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(KEY_DOCUMENT))
                mDocument = it.getParcelable(KEY_DOCUMENT)
        }
        setViews()
        bindSpinners()
        bindVehicleDetails()
        setEvents()
    }

    companion object {
        fun newInstance() = ImpoundmentEntryFragment()
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_impoundment_entry, container, false)
        return mBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setViews() {
        // Multiple documents
        mBinding.multipleDoc.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.multipleDoc.rcDocuments.adapter = documentListAdapter

        mBinding.vehDocWrapper.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.vehDocWrapper.rcDocuments.adapter = vehicleDocumentAdapter

        mBinding.layoutVehOwnAddress.documentWrapper.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.layoutVehOwnAddress.documentWrapper.rcDocuments.adapter = vehicleOwnerDocumentAdapter

        mBinding.layoutDriverAddress.documentWrapper.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.layoutDriverAddress.documentWrapper.rcDocuments.adapter = driverDocumentAdapter

        mBinding.layoutAnimalOwnerAddress.documentWrapper.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.layoutAnimalOwnerAddress.documentWrapper.rcDocuments.adapter = animalOwnerDocumentAdapter

        mBinding.layoutImpoundFromAddress.documentWrapper.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.layoutImpoundFromAddress.documentWrapper.rcDocuments.adapter = impoundFromDocumentAdapter

        // Goods Owner
        val goodsOwnerLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        mBinding.rcGoodsOwner.layoutManager = goodsOwnerLayoutManager
        mBinding.rcGoodsOwner.adapter = mBusAdapter
        mBinding.rcGoodsOwner.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL)
        )

        //Animal Owner
        val animalOwnerLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        mBinding.rcAnimalOwner.layoutManager = animalOwnerLayoutManager
        mBinding.rcAnimalOwner.adapter = animalOwnerAdapter
        mBinding.rcAnimalOwner.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL)
        )

        //ImpoundFrom
        val animalImpoundLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        mBinding.rcImpoundFrom.layoutManager = animalImpoundLayoutManager
        mBinding.rcImpoundFrom.adapter = mImpoundFromAdapter
        mBinding.rcImpoundFrom.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL)
        )

        // Driver
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        mBinding.rcvDriver.layoutManager = layoutManager
        mBinding.rcvDriver.adapter = driverAdapter
        mBinding.rcvDriver.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL)
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
            showHideVehicleOwnerDetails(false)
            showHideVehicleOwnerSearchPopup(false)
        }
    }

    private fun performVehicleSearch() {
        mVehicle = null
        mBinding.vehDocWrapper.txtNoDataFound.show()
        mBinding.vehDocWrapper.fabAddImage.enable()
        vehicleDocList.clear()
        vehicleDocumentAdapter.notifyDataSetChanged()
        if (mBinding.edtVehicleNo.text.toString().length > 2) {
            getVehicleDetailsWithOwner(
                searchData = mBinding.edtVehicleNo.text.toString(),
            )
        } else {
            showHideVehicleDetails(false)
            showHideVehicleSearchPopup(false)
        }
    }

    private fun getVehicleDetailsWithOwner(searchData: String) {
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

    private fun showHideVehicleDetails(isShow: Boolean) {
        if (isShow) {
            mBinding.vehDetailsWrapper.visibility = VISIBLE
        } else {
            mBinding.vehDetailsWrapper.visibility = GONE
        }
    }

    private fun showHideVehicleOwnerDetails(isShow: Boolean) {
        if (isShow) {
            mBinding.vehCtzOwnWrapper.visibility = VISIBLE
            mBinding.vehCtzOwnWrapperFName.isVisible = true
            mBinding.layoutvehCtzOwnWrapperFName.edtFirstName.setText("")
            mBinding.layoutVehOwnAddress.edtLastName.setText("")

        } else {
            mBinding.vehCtzOwnWrapper.visibility = GONE
            mBinding.vehCtzOwnWrapperFName.isVisible = false
            mBinding.layoutvehCtzOwnWrapperFName.edtFirstName.setText("")
            mBinding.layoutVehOwnAddress.edtLastName.setText("")

        }
    }

    private fun showHideVehicleSearchPopup(isShow : Boolean){
        mBinding.rcVehicleSearch.isVisible = isShow
    }

    private fun showHideVehicleOwnerSearchPopup(isShow : Boolean){
        mBinding.rcVehCtzOwn.isVisible = isShow
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

    private fun bindData() {
        mBinding.edtImpoundmentDate.setText(formatDisplayDateTimeInMillisecond(Date()))
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

    private fun changeVisibilityVehicleGoods(impoundmentType: LAWImpoundmentType?) {
        mBinding.crdVehicle.visibility = GONE
        mBinding.crdDriver.visibility = GONE
        mBinding.crdGoodsOwner.visibility = GONE
        //  mBinding.animalImpoundQuantityCard.visibility = GONE
        //this field should be visible for all impounds except animal impoundment
        mBinding.edtImpoundmentCharge.visibility = VISIBLE
        impoundmentType?.let { it ->
            it.applicableOnVehicle?.let {
                if (it == "Y") {
                    mBinding.crdVehicle.visibility = VISIBLE
                    mBinding.crdDriver.visibility = VISIBLE
                }
            }
            it.applicableOnGoods?.let {
                if (it == "Y") {
                    mBinding.crdGoodsOwner.visibility = VISIBLE
                }
            }
        }
        //this logic is for animal impound
        /*if(impoundmentType!!.applicableOnGoods == "N" && impoundmentType!!.applicableOnVehicle == "N") {
            mBinding.crdVehicle.visibility = GONE
            mBinding.crdDriver.visibility = GONE
            mBinding.crdGoodsOwner.visibility = GONE
            mBinding.edtImpoundmentCharge.visibility = GONE

            mBinding.animalImpoundQuantityCard.visibility = VISIBLE

        }*/
    }

    private fun changeVisibilityDriver(violationType: LAWViolationType?) {

        if (violationType?.applicableOnDriver == "Y") {
            mBinding.crdDriver.visibility = VISIBLE
            setTextInputLayoutHintColor(mBinding.edtDriverNameLayout, mBinding.edtDriverNameLayout.context, R.color.hint_color)
        } else {
            setTextInputLayoutHintColor(mBinding.edtDriverNameLayout, mBinding.edtDriverNameLayout.context, R.color.colorGray)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setEvents() {


        mBinding.llViolationTypes.setOnClickListener {
            val fragment = ImpoundmentTypeMasterFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_TYPES, mImpoundmentTypes)
            bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_SUB_TYPES, mImpoundmentSubTypes)
            bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_REASONS, mImpoundmentReasons)
            bundle.putParcelableArrayList(Constant.KEY_VIOLATION_TYPES, mViolationTypes)
            bundle.putParcelableArrayList(Constant.KEY_POLICE_STATION_YARDS, mPoliceStationYards)
            bundle.putParcelableArrayList(Constant.KEY_CRANE_TYPES,mCraneTypes)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATION_TYPE_LIST)
            mListener?.addFragment(fragment, true)
        }

        mBinding.spnImpoundmentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var impoundmentType: LAWImpoundmentType? = LAWImpoundmentType()
                if (p0 != null && p0.selectedItem != null)
                    impoundmentType = p0.selectedItem as LAWImpoundmentType

                impoundmentType?.impoundmentTypeID?.let {
                    filterImpoundmentSubTypes(it)
                }
                val violatorType: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
                if (violatorType!!.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.BUSINESS.code)
                        || violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.VEHICLE.code))
                    changeVisibilityVehicleGoods(impoundmentType)
            }
        }


        mBinding.spnViolator.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var violatorType = LAWViolatorTypes()
                mBinding.edtVehicleNo.setText("")
                mBinding.edtvehCtzOwnName.setText("")
                mBinding.edtAnimalViolatorOwner.setText("")
                mBinding.edtAnimalImpondFrom.setText("")
                mBinding.edtDriverName.setText("")
                mBinding.edtGoodsOwner.setText("")
                if (mBinding.spnZone.adapter?.count ?: 0 > 0 && mBinding.spnSector.adapter?.count ?: 0 > 0) {
                    mBinding.spnZone.setSelection(0)
                    mBinding.spnSector.setSelection(0)
                }

                if (p0 != null && p0.selectedItem != null)
                    violatorType = p0.selectedItem as LAWViolatorTypes

                if (violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()) == Constant.ViolationTypeCode.ANIMAL.code) {
                    mBinding.animalImpoundQuantityCard.visibility = VISIBLE
                    mBinding.animalImpoundFromCard.visibility = VISIBLE
                    mBinding.llViolationTypes.visibility = VISIBLE

                    mBinding.vehGoodImpound.visibility = GONE
                    mBinding.vehGoodImpoundBottom.visibility = GONE
                    mBinding.crdDriver.visibility = GONE
                    mBinding.crdVehicle.visibility = GONE
                    mBinding.crdGoodsOwner.visibility = GONE
                    mBinding.edtGoodsValuationTIL.visibility = GONE

                }

                if (violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.BUSINESS.code)
                        || violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.VEHICLE.code)) {
                    mBinding.vehGoodImpound.visibility = VISIBLE
                    mBinding.vehGoodImpoundBottom.visibility = VISIBLE
                    mBinding.crdDriver.visibility = VISIBLE
                    mBinding.crdVehicle.visibility = VISIBLE
                    mBinding.crdGoodsOwner.visibility = VISIBLE

                    mBinding.animalImpoundQuantityCard.visibility = GONE
                    mBinding.animalImpoundFromCard.visibility = GONE
                    mBinding.llViolationTypes.visibility = GONE

                    mBinding.edtGoodsValuationTIL.visibility = VISIBLE

                    val impoundmentType: LAWImpoundmentType? = mBinding.spnImpoundmentType.selectedItem as LAWImpoundmentType?
                    changeVisibilityVehicleGoods(impoundmentType)

                    lawImpoundmentTypesOptAnimalList(violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()))

                    childViolatons.clear()

                    for (violationType in mViolationTypes) {
                        if (violationType.parentViolationTypeID != null
                                && violationType.violatorTypeCode == violatorType.violatorTypeCode)
                            childViolatons.add(violationType)
                    }

                    childViolatons.add(0, LAWViolationType(violationType = getString(R.string.select),
                            violationTypeID = -1,
                            parentViolationTypeID = -1,
                            parentViolationType = getString(R.string.select)))


                    mViolationClasses.clear()

                    for (violationClass in mViolationTypes) {
                        if (violationClass.parentViolationTypeID == null
                                && violationClass.violatorTypeCode == violatorType.violatorTypeCode && isValidParent(violationClass)) {
                            mViolationClasses.add(violationClass)
                        }
                    }

                    mViolationClasses.add(0, LAWViolationType(violationType = getString(R.string.select),
                            violationTypeID = -1,
                            parentViolationTypeID = -1,
                            parentViolationType = getString(R.string.select)))


                    bindViolationClassTypes()
                }
                /* filterViolationClasses(violationType.parentViolationTypeID ?: 0)
                 fetchAmount(violationType.violationTypeID ?: 0)*/

            }
        }

//        mBinding.spnViolationType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                var violationType: LAWViolationType? = LAWViolationType()
//                if (p0 != null && p0.selectedItem != null)
//                    violationType = p0.selectedItem as LAWViolationType
//                filterViolationClasses(violationType?.parentViolationTypeID, violationType?.violationDetails)
//                violationType?.violationTypeID?.let {
//                    fetchFineAmount(it)
//                }
//
//                val violatorType: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
//                if (violatorType!!.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.BUSINESS.code)
//                        || violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.VEHICLE.code))
//                    changeVisibilityDriver(violationType)
//            }
//        }


        itemTypeSelector = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedViolatorType = LAWViolationType()
                if (parent != null && parent.selectedItem != null)
                    selectedViolatorType = parent.selectedItem as LAWViolationType

                setClassesTypes(2, selectedViolatorType)

                val violatorType: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
                if (violatorType!!.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.BUSINESS.code)
                        || violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.VEHICLE.code))
                    changeVisibilityDriver(selectedViolatorType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        itemClassSelector = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setClassesTypes(1, parent?.selectedItem as LAWViolationType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        mBinding.spnViolationType.onItemSelectedListener = itemTypeSelector
        mBinding.spnViolationClass.onItemSelectedListener = itemClassSelector


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
                filterZones(city?.cityID!!,mBinding.spnZone,mBinding.spnSector)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!,mBinding.spnSector)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.layoutVehOwnAddress.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!,mBinding.layoutVehOwnAddress.spnSector)
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

        mBinding.layoutAnimalOwnerAddress.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!,mBinding.layoutAnimalOwnerAddress.spnSector)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.layoutImpoundFromAddress.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!,mBinding.layoutImpoundFromAddress.spnSector)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.edtImpoundmentCharge.setOnFocusChangeListener { v, hasFocus ->
            val impoundmentCharge = mBinding.edtImpoundmentCharge.text?.toString()?.trim()
            impoundmentCharge?.let {
                if (hasFocus) {
                    if (!TextUtils.isEmpty(it))
                        mBinding.edtImpoundmentCharge.setText("${currencyToDouble(it)}")
                } else
                    mBinding.edtImpoundmentCharge.setText(formatWithPrecision(it))
            }
        }

        /*mBinding.edtAnimalViolatorOwner.setOnClickListener {
            val fragment = BusinessOwnerSearchFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_QUICK_MENU, mCode)
            bundle.putString(KEY_CITIZEN_BUSINESS, CITIZEN_BUSINESS)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
            mListener?.addFragment(fragment, true)

            *//*val fragment = BusinessOwnerSearchFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
            mListener?.showToolbarBackButton(R.string.citizen)
            mListener?.addFragment(fragment, true)*//*
        }*/

        /*mBinding.tvCreateCustomer.setOnClickListener {

            val fragment = BusinessOwnerEntryFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)
            mListener?.showToolbarBackButton(R.string.citizen)
            mListener?.addFragment(fragment, true)
        }*/

        /*mBinding.edtDriverName.setOnClickListener {
            val fragment = BusinessOwnerSearchFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_QUICK_MENU, mCode)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DRIVER_SEARCH)
            mListener?.addFragment(fragment, true)
        }

        mBinding.edtDrivingLicenseNumber.setOnClickListener {
            val fragment = BusinessOwnerSearchFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_QUICK_MENU, mCode)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DRIVER_SEARCH)
            mListener?.addFragment(fragment, true)
        }*/

        mBinding.tvCreateDriver.setOnClickListener {
            val fragment = BusinessOwnerEntryFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SALES_TAX)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)
            mListener?.addFragment(fragment, true)
        }

        /*mBinding.edtAnimalImpondFrom.setOnClickListener {
            val fragment = BusinessOwnerSearchFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_QUICK_MENU, mCode)
            bundle.putString(KEY_CITIZEN_BUSINESS, CITIZEN_BUSINESS)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_IMPOUND_FROM_SEARCH)
            mListener?.addFragment(fragment, true)
        }*/

        /*mBinding.tvCreateImpondfrom.setOnClickListener {
            val fragment = BusinessOwnerEntryFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SALES_TAX)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_IMPOUND_FROM)
            mListener?.addFragment(fragment, true)
        }*/

        mBinding.tvCreateOwner.setOnClickListener {
            event = Event.instance
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtra(KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE)
            startActivityForResult(intent, Constant.REQUEST_CODE_CREATE_VEHICLE)
        }

        /*mBinding.edtVehicleNo.setOnClickListener {
            val fragment = VehicleSearchFragment()
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_VEHICLE_SEARCH)
            mListener?.addFragment(fragment, true)
        }

        mBinding.edtOwner.setOnClickListener {
            val fragment = VehicleSearchFragment()
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_VEHICLE_SEARCH)
            mListener?.addFragment(fragment, true)
        }*/



        /*mBinding.edtGoodsOwner.setOnClickListener {
            val fragment = BusinessSearchFragment()
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_SEARCH)
            mListener?.addFragment(fragment, true)
        }*/

        mBinding.ivAddLocation.setOnClickListener {
            var latitude = 0.0
            var longitude = 0.0

            if (mBinding.edtLatitude.text.toString().trim().isNotEmpty())
                latitude = mBinding.edtLatitude.text.toString().trim().toDouble()
            if (mBinding.edtLongitude.text.toString().trim().isNotEmpty())
                longitude = mBinding.edtLongitude.text.toString().trim().toDouble()

            val dialog: LocateDialogFragment = LocateDialogFragment.newInstance(latitude, longitude)
            dialog.show(childFragmentManager, LocateDialogFragment::class.java.simpleName)
        }

//        mBinding.llDocuments.setOnClickListener(this)
        mBinding.btnSave.setOnClickListener(this)
        mBinding.tvSeeTicketHistoryVehicle.setOnClickListener(this)
        mBinding.tvSeeTicketHistoryDriver.setOnClickListener(this)

        mBinding.btnShowMoreOrLess.setOnClickListener {
            if (mBinding.btnShowMoreOrLess.text.toString().equals(getString(R.string.show_more), true)) {
                showMoreProductViews()
            } else {
                hideProductViews()
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

        goodsOwnerTextListener =
            DebouncingTextListener(viewLifecycleOwner.lifecycle) { _, newText ->
                newText?.let {
                    performGoodsOwnerSearch()
                }
            }
        mBinding.rcGoodsOwner.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        mBinding.edtGoodsOwner.addTextChangedListener(goodsOwnerTextListener)

        mBinding.edtGoodsOwner.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performGoodsOwnerSearch()
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

        //animalOwner Start
        animalOwnerTextListener =
            DebouncingTextListener(viewLifecycleOwner.lifecycle) { _, newText ->
                newText?.let {
                    performAnimalOwnerSearch()
                }
            }
        mBinding.rcAnimalOwner.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        mBinding.edtAnimalViolatorOwner.addTextChangedListener(animalOwnerTextListener)
        mBinding.edtAnimalViolatorOwner.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performAnimalOwnerSearch()
                return@OnEditorActionListener true
            }
            false
        }) //animalOwner End

        //animalImpound Start
        animalImpoundTextListener =
            DebouncingTextListener(viewLifecycleOwner.lifecycle) { _, newText ->
                newText?.let {
                    performAnimalImpoundSearch()
                }
            }
        mBinding.rcImpoundFrom.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        mBinding.edtAnimalImpondFrom.addTextChangedListener(animalImpoundTextListener)

        mBinding.edtAnimalImpondFrom.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performAnimalImpoundSearch()
                return@OnEditorActionListener true
            }
            false
        }) //animalImpound End

        mBinding.multipleDoc.fabAddImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ImpoundmentDocument.MultipleDocuments.value)
            }
        })

        mBinding.vehDocWrapper.fabAddImage.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ImpoundmentDocument.VehicleDocument.value)
            }
        })

        mBinding.layoutVehOwnAddress.documentWrapper.fabAddImage.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ImpoundmentDocument.VehicleOwnerDocument.value)
            }
        })

        mBinding.layoutDriverAddress.documentWrapper.fabAddImage.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ImpoundmentDocument.DriverDocument.value)
            }
        })

        mBinding.layoutAnimalOwnerAddress.documentWrapper.fabAddImage.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ImpoundmentDocument.AnimalOwnerDocument.value)
            }
        })

        mBinding.layoutImpoundFromAddress.documentWrapper.fabAddImage.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(Constant.ImpoundmentDocument.ImpoundFromDocument.value)
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

    private fun performAnimalImpoundSearch() {
        mImpoundFrom = null
        impoundFromDocList.clear()
        impoundFromDocumentAdapter.notifyDataSetChanged()
        mBinding.layoutImpoundFromAddress.documentWrapper.txtNoDataFound.show()
        mBinding.layoutImpoundFromAddress.documentWrapper.fabAddImage.enable()
        if (mBinding.edtAnimalImpondFrom.text.toString().length > 2) {
            getImpoundFromSearchResult(data = mBinding.edtAnimalImpondFrom.text.toString())
        } else {
            mBinding.rcImpoundFrom.isVisible = false
            mBinding.impoundFromWrapper.isVisible = false
            mBinding.impoundFromWrapperFName.isVisible = false
            mBinding.layoutImpoundFromWrapperFName.edtFirstName.setText("")
            mBinding.layoutImpoundFromAddress.edtLastName.setText("")
        }
    }

    private fun getImpoundFromSearchResult(data: String) {
        val searchFilter = getCitizenSearchFilterPayload(data)
        mBinding.impoundFromProgressView.isVisible = true
        APICall.getBusinessOwners(searchFilter, object : ConnectionCallBack<BusinessOwnerResponse> {
            override fun onSuccess(response: BusinessOwnerResponse) {
                mBinding.impoundFromProgressView.isVisible = false
                mBinding.impoundFromWrapper.visibility = GONE
                //mBinding.impoundFromWrapperFName.isVisible = true
                mBinding.rcImpoundFrom.isVisible = true
                val count: Int = response.results.businessOwner.size
                if (count > 0) {
                    mBinding.rcImpoundFrom.isVisible = true
                    mImpoundFromAdapter.clear()
                    response.results.businessOwner.let {
                        mImpoundFromAdapter.addAll(it)
                        mImpoundFromAdapter.notifyDataSetChanged()
                    }
                    mBinding.impoundFromWrapper.visibility = GONE
                    //mBinding.impoundFromWrapperFName.isVisible = true
                } else {
                    mBinding.rcImpoundFrom.isVisible = false
                    mBinding.impoundFromWrapper.visibility = VISIBLE
                    mBinding.impoundFromWrapperFName.isVisible = true
                    mBinding.layoutImpoundFromWrapperFName.edtFirstName.setText("")
                    mBinding.layoutImpoundFromAddress.edtLastName.setText("")
                }
            }
            override fun onFailure(message: String) {
                mBinding.rcImpoundFrom.isVisible = false
                mBinding.impoundFromProgressView.isVisible = false
                mBinding.impoundFromWrapper.visibility = VISIBLE
                mBinding.impoundFromWrapperFName.isVisible = true
                mBinding.layoutImpoundFromWrapperFName.edtFirstName.setText("")
                mBinding.layoutImpoundFromAddress.edtLastName.setText("")
                mImpoundFromAdapter.clear()
                mBinding.layoutImpoundFromWrapperFName.edtFirstName.isEnabled = true

            }
        })
    }

    private fun getCitizenSearchFilterPayload(data: String): OwnerSearchFilter {
        if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS || mCode == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
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
        if(mBinding.spnViolator.selectedItem.toString() == getString(R.string.animal)){
            tableDetails.initialTableCondition =
                "StatusCode = 'CRM_Organizations.Active' OR StatusCode = 'CRM_Contacts.Active'"
        }else{
            tableDetails.initialTableCondition =
                "AccountTypeCode = 'CUS' AND StatusCode = 'CRM_Contacts.Active'"
        }
        searchFilter.tableDetails = tableDetails
        return searchFilter
    }

    private fun performAnimalOwnerSearch() {
        mAnimalViolator = null
        mBinding.layoutAnimalOwnerAddress.documentWrapper.txtNoDataFound.show()
        mBinding.layoutAnimalOwnerAddress.documentWrapper.fabAddImage.enable()
        animalOwnerDocList.clear()
        animalOwnerDocumentAdapter.notifyDataSetChanged()
        if (mBinding.edtAnimalViolatorOwner.text.toString().length > 2) {
            getAnimalOwnerSearchResult(data = mBinding.edtAnimalViolatorOwner.text.toString())
        } else {
            mBinding.rcAnimalOwner.isVisible = false
            mBinding.animalOwnerWrapper.isVisible = false
            mBinding.animalOwnerWrapperFName.isVisible = false
            mBinding.layoutAnimalOwnerFName.edtFirstName.setText("")
            mBinding.layoutAnimalOwnerAddress.edtLastName.setText("")
        }
    }

    private fun getAnimalOwnerSearchResult(data: String) {
        /*if(mCode== Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS || mCode== Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS){
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
        tableDetails.initialTableCondition = "AccountTypeCode = 'CUS' AND StatusCode = 'CRM_Contacts.Active'"
        searchFilter.tableDetails = tableDetails*/
        val searchFilter = getCitizenSearchFilterPayload(data)
        mBinding.animalOwnerProgressView.isVisible = true
        APICall.getBusinessOwners(searchFilter, object : ConnectionCallBack<BusinessOwnerResponse> {
            override fun onSuccess(response: BusinessOwnerResponse) {
                mBinding.animalOwnerProgressView.isVisible = false
                val count: Int = response.results.businessOwner.size
                if (count > 0) {
                    mBinding.rcAnimalOwner.isVisible = true
                    animalOwnerAdapter.clear()
                    response.results.businessOwner.let {
                        animalOwnerAdapter.addAll(it)
                        animalOwnerAdapter.notifyDataSetChanged()
                    }
                    mBinding.animalOwnerWrapper.visibility = GONE
                    /*showHideVehicleOwnerSearchPopup(true)
                    showHideVehicleOwnerDetails(false)
                    vehicleOwnerSearchList.clear()
                    vehicleOwnerSearchAdapter.clear()
                    vehicleOwnerSearchList.addAll(response.results.businessOwner)
                    vehicleOwnerSearchAdapter.addAll(vehicleOwnerSearchList)*/
                } else {
                    mBinding.rcAnimalOwner.isVisible = false
                    mBinding.animalOwnerWrapper.visibility = VISIBLE
                    mBinding.animalOwnerWrapperFName.isVisible = true
                    mBinding.layoutAnimalOwnerFName.edtFirstName.setText("")
                    mBinding.layoutAnimalOwnerAddress.edtLastName.setText("")
                    /*showHideVehicleOwnerSearchPopup(false)
                    showHideVehicleOwnerDetails(true)*/
                }
            }
            override fun onFailure(message: String) {
                mBinding.rcAnimalOwner.isVisible = false
                mBinding.animalOwnerProgressView.isVisible = false
                mBinding.animalOwnerWrapper.visibility = VISIBLE
                mBinding.animalOwnerWrapperFName.isVisible = true
                mBinding.layoutAnimalOwnerFName.edtFirstName.setText("")
                mBinding.layoutAnimalOwnerAddress.edtLastName.setText("")
                animalOwnerAdapter.clear()
                mBinding.layoutAnimalOwnerFName.edtFirstName.isEnabled = true
                /*showHideVehicleOwnerSearchPopup(false)
                showHideVehicleOwnerDetails(true)*/
            }
        })
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
        }
    }

    private fun disableEnableDriverFields(enableView : Boolean) {
        if(enableView){
            mBinding.driverFieldsLayout.visibility = VISIBLE
            mBinding.driverFieldsWrapperFName.isVisible = true
            mBinding.layoutDriverFieldsWrapperFName.edtFirstName.setText("")
            mBinding.driverFieldsLayout.edtLastName.setText("")
        }else{
            mBinding.driverFieldsLayout.visibility = GONE
            mBinding.driverFieldsWrapperFName.isVisible = false
            mBinding.layoutDriverFieldsWrapperFName.edtFirstName.setText("")
            mBinding.driverFieldsLayout.edtLastName.setText("")
        }
        mBinding.edtDrivingLicenseNumber.isEnabled = enableView
        mBinding.layoutDriverAddress.spnZone.isEnabled = enableView
        mBinding.layoutDriverAddress.spnSector.isEnabled = enableView
        mBinding.layoutDriverAddress.edtStreet.isEnabled = enableView
    }

    private fun performGoodsOwnerSearch() {
        if (mBinding.edtGoodsOwner.text.toString().length > 2) {
            val data: String = mBinding.edtGoodsOwner.text.toString()
            getGoodsOwnerSearchResult(data)
        } else {
            mBusiness = null
            mBinding.rcGoodsOwner.isVisible = false
        }
    }

    private fun getGoodsOwnerSearchResult(data: String) {
        mBusiness = null
        mBinding.goodsOwnerProgressView.isVisible = true
        APICall.getBusiness(data, object : ConnectionCallBack<BusinessResponse> {
            override fun onSuccess(response: BusinessResponse) {
                mBinding.goodsOwnerProgressView.isVisible = false
                val count = response.businessOwner?.size
                if (count == null || count == 0) {
                    mBinding.rcGoodsOwner.isVisible = false
                    mBusAdapter.clear()
                } else {
                    mBinding.rcGoodsOwner.isVisible = true
                    mBusAdapter.clear()
                    mBusAdapter.addAll(response.businessOwner!!)
                }
//                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.goodsOwnerProgressView.isVisible = false
                mBinding.rcGoodsOwner.isVisible = false
//                mListener?.dismissDialog()
//                mListener?.showAlertDialog(message)
                mBusAdapter.clear()
            }
        })
    }

    override fun onDetach() {
        mListener = null
        mHelper?.disconnect()
        super.onDetach()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                /*R.id.llDocuments -> {
                    val fragment = LocalDocumentsMasterFragment()
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                    mListener?.showToolbarBackButton(R.string.documents)
                    mListener?.addFragment(fragment, true)
                }*/
                R.id.btnSave -> {
                    if (isValid()) {
                        val violatorType: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
                        if (violatorType!!.violatorTypeCode?.toUpperCase(Locale.getDefault()) == Constant.ViolationTypeCode.ANIMAL.code)
                            saveAnimalImpound()
                        else
                            save()
                    } else {
                    }
                }
                R.id.tvSeeTicketHistoryDriver -> {
                    val fragment = DriverTicketHistoryFragment()
                    val bundle = Bundle()
                    bundle.putString(Constant.KEY_DRIVING_LICENSE_NO, if (mDriver?.drivingLicenseNo != null) "${mDriver?.drivingLicenseNo}" else "")
                    bundle.putSerializable(KEY_QUICK_MENU, mCode)
                    fragment.arguments = bundle
                    mListener?.addFragment(fragment, true)
                }
                R.id.tvSeeTicketHistoryVehicle -> {
                    val fragment = VehicleTicketHistoryFragment()
                    val bundle = Bundle()
                    bundle.putString(Constant.KEY_VEHICLE_NO, mVehicle?.vehicleNumber ?: "")
                    bundle.putSerializable(KEY_QUICK_MENU, mCode)
                    fragment.arguments = bundle
                    mListener?.addFragment(fragment, true)
                }
                else -> {
                }
            }
        }
    }

    private fun setClassesTypes(isFrom: Int, violationParent: LAWViolationType? = null) {
        when (isFrom) {
            0 -> {
//                for (violationType in childViolatons) {
//                    if (violationType.violationTypeID == mViolation?.violationTypeId) {
//
//                        setChildViolationType(2, violationType)
//
//                        mBinding.spnViolationClass.onItemSelectedListener = null
//
//                        for (violation in mViolationClasses) {
//                            if (violation.violationTypeID == violationType.parentViolationTypeID) {
//                                mBinding.spnViolationClass.setSelection(mViolationClasses.indexOf(violation))
//                                break
//                            }
//                        }
//
//                        mViolation?.violationTypeId?.let { fetchFineAmount(it) }
//                        mBinding.edtViolationDetails.setText(mViolation?.violationDetails)
//
//                        Handler().postDelayed(Runnable {
//                            mBinding.spnViolationClass.onItemSelectedListener = itemClassSelector
//                        }, 100)
//
//                        break
//                    }
//                }
            }
            1 -> {
                //from CLASS_SELECTION
                setChildViolationType(isFrom, violationParent)
            }
            2 -> {
                //From TYPE_SELECTION
                fetchFineAmount(0)
                mBinding.edtViolationDetails.setText("")

                setChildViolationType(isFrom, violationParent)

                for (item in mViolationClasses) {
                    if (item.violationTypeID == violationParent?.parentViolationTypeID) {
                        mBinding.spnViolationClass.onItemSelectedListener = null
                        mBinding.spnViolationClass.setSelection(mViolationClasses.indexOf(item))

                        Handler().postDelayed(Runnable {
                            mBinding.spnViolationClass.onItemSelectedListener = itemClassSelector
                        }, 100)

                        break
                    }
                }
            }
        }
    }


    private fun setChildViolationType(isFrom: Int, violationParent: LAWViolationType? = null) {
        childViolatons.clear()
        if (mViolationTypes.isNotEmpty()) {
            val violatorType = mBinding.spnViolator.selectedItem as LAWViolatorTypes
            for (type in mViolationTypes) {
                if (type.parentViolationTypeID != null && type.violationTypeCode == violatorType.violatorTypeCode) {
                    childViolatons.add(type)
                }
            }

            //Filter according to the parentSelection
            if (violationParent != null) {
                childViolatons.clear()
                for (type in mViolationTypes) {
                    if (isFrom == 1) {
                        if (type.parentViolationTypeID == violationParent.violationTypeID) {
                            childViolatons.add(type)
                        }
                    } else {
                        if (type.parentViolationTypeID == violationParent.parentViolationTypeID) {
                            childViolatons.add(type)
                        }
                    }
                }

                if (childViolatons.isNullOrEmpty()) {
                    for (violationType in mViolationTypes) {
                        if (violationType.parentViolationTypeID != null
                                && violationType.violatorTypeCode == violatorType.violatorTypeCode)
                            childViolatons.add(violationType)
                    }
                }
            }

            childViolatons.add(0, LAWViolationType(violationType = getString(R.string.select),
                    violationTypeID = -1,
                    parentViolationTypeID = -1,
                    parentViolationType = getString(R.string.select)))
        }

        val violationTypesAdapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, childViolatons)
        mBinding.spnViolationType.adapter = violationTypesAdapter

        mBinding.spnViolationType.onItemSelectedListener = null
        if (violationParent != null) {
            mBinding.spnViolationType.setSelection(childViolatons.indexOf(violationParent))
            violationParent.violationTypeID?.let { fetchFineAmount(it) }
            mBinding.edtViolationDetails.setText(violationParent.violationDetails)
        } else {
            mBinding.spnViolationType.setSelection(0)
            fetchFineAmount(0)
            mBinding.edtViolationDetails.setText("")
        }
        Handler().postDelayed(Runnable {
            mBinding.spnViolationType.onItemSelectedListener = itemTypeSelector
        }, 100)
    }

    /*private fun fetchEstimatedQuantityAmount() {
        val getEstimatedImpoundAmount = GetEstimatedImpoundAmount()

        var impoundTypeID = 0
        val impoundmentType: LAWImpoundmentType? = mBinding.spnImpoundmentType.selectedItem as LAWImpoundmentType?
        impoundmentType?.impoundmentTypeID?.let {
            impoundTypeID = it
        }
        getEstimatedImpoundAmount.impoundmentTypeID = impoundTypeID
        if (!TextUtils.isEmpty(mBinding.edtQuantity.text?.toString()?.trim()))
            getEstimatedImpoundAmount.quantity = mBinding.edtQuantity.text?.toString()?.trim()

        mListener?.showProgressDialog()
        APICall.getEstimatedImpoundAmount(getEstimatedImpoundAmount, object : ConnectionCallBack<EstimatedImpoundAmountResponse> {
            override fun onSuccess(response: EstimatedImpoundAmountResponse) {
                mBinding.edtEstimatedImpoundCharge.setText(formatWithPrecision(response.impoundmentCharge))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.edtEstimatedImpoundCharge.setText("")
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        mHelper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
        mHelper?.fetchLocation()
        mHelper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                mListener?.dismissDialog()
                bindLatLongs(latitude, longitude)
                initComponents()
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

    private fun bindCounts() {
        if (ObjectHolder.documents.size > 0) {
            mBinding.multipleDoc.txtNoDataFound.visibility = View.GONE
        } else {
            mBinding.multipleDoc.txtNoDataFound.visibility = View.VISIBLE
        }
//        mBinding.txtNumberOfDocuments.text = "${ObjectHolder.documents.size}"
        mBinding.txtNoOfViolations.text = "${ObjectHolder.impoundments.size}"
    }

    private fun isValid(): Boolean {
        if (mBinding.edtImpoundmentDate.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.impoundment_date)}")
            return false
        }
        if (mBinding.vehGoodImpound.isVisible) {
            if (mBinding.spnImpoundmentType.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.impoundment_type)}")
                return false
            }
            if (mBinding.spnImpoundmentSubType.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.impoundment_sub_type)}")
                return false
            }

            if (mBinding.spnViolationType.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violation_type)}")
                return false
            }
        }


        if (mBinding.spnPoliceStation.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.police_station)}")
            return false
        }
        /*if (mBinding.spnCountry.selectedItem == null) {
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
        val impoundmentType: LAWImpoundmentType? = mBinding.spnImpoundmentType.selectedItem as LAWImpoundmentType?

        if (impoundmentType?.applicableOnVehicle == "Y" && mBinding.crdVehicle.isVisible ) {
            if (mBinding.edtVehicleNo.text.toString().trim()
                    .isEmpty() || (mVehicle == null && !mBinding.vehDetailsWrapper.isVisible)
            ) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.vehicle_registration_no)}")
                mBinding.edtVehicleNo.setText("")
                mBinding.edtVehicleNo.requestFocus()
                return false
            }
            if (mVehicle == null && mBinding.vehDetailsWrapper.isVisible) {
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


        if (impoundmentType?.applicableOnGoods == "Y" && (mBinding.crdGoodsOwner.isVisible && mBusiness == null)) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.goods_owner)}")
            mBinding.edtGoodsOwner.setText("")
            mBinding.edtGoodsOwner.requestFocus()
            return false

        }

        if (mBinding.animalImpoundQuantityCard.isVisible) {
            if (mBinding.edtAnimalViolatorOwner.text.toString().trim().isEmpty() || (mAnimalViolator == null && !mBinding.animalOwnerWrapper.isVisible)){
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.animal_owner_number)}")
                mBinding.edtAnimalViolatorOwner.requestFocus()
                mBinding.edtAnimalViolatorOwner.setText("")
                return false
            }
            if(mAnimalViolator == null && mBinding.animalOwnerWrapperFName.isVisible){
                if(mBinding.layoutAnimalOwnerFName.edtFirstName.text.toString().isEmpty()){
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.animal_owner)} ${getString(R.string.first_name)}")
                    return false
                }
            }
            if (mAnimalViolator == null && mBinding.animalOwnerWrapper.isVisible){
                if(mBinding.layoutAnimalOwnerAddress.edtLastName.text.toString().isEmpty()){
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.animal_owner)} ${getString(R.string.last_name)}")
                    return false
                }
                if (mBinding.layoutAnimalOwnerAddress.spnZone.selectedItem == null) {
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.animal_owner)} ${getString(R.string.zone)}")
                    return false
                }
                if (mBinding.layoutAnimalOwnerAddress.spnSector.selectedItem == null) {
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.animal_owner)} ${getString(R.string.sector)}")
                    return false
                }
            }

        }

        if (mBinding.animalImpoundFromCard.isVisible) {
            if (mBinding.edtAnimalImpondFrom.text.toString().trim().isEmpty()
                    || (mImpoundFrom == null && !mBinding.impoundFromWrapper.isVisible)) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.impond_from)}")
                mBinding.edtAnimalImpondFrom.requestFocus()
                mBinding.edtAnimalImpondFrom.setText("")
                return false
            }
            if(mImpoundFrom == null && mBinding.impoundFromWrapperFName.isVisible){
                if(mBinding.layoutImpoundFromWrapperFName.edtFirstName.text.toString().isEmpty()){
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.impond_from)} ${getString(R.string.first_name)}")
                    return false
                }
            }
            if (mImpoundFrom == null && mBinding.impoundFromWrapper.isVisible){
                if(mBinding.layoutImpoundFromAddress.edtLastName.text.toString().isEmpty()){
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.impond_from)} ${getString(R.string.last_name)}")
                    return false
                }
                if (mBinding.layoutImpoundFromAddress.spnZone.selectedItem == null) {
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.impond_from)} ${getString(R.string.zone)}")
                    return false
                }
                if (mBinding.layoutImpoundFromAddress.spnSector.selectedItem == null) {
                    mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.impond_from)} ${getString(R.string.sector)}")
                    return false
                }
            }
        }

        val violationType: LAWViolationType? = mBinding.spnViolationType.selectedItem as LAWViolationType?

        if (impoundmentType?.applicableOnVehicle == "Y" && violationType?.applicableOnDriver == "Y"
                && (mBinding.crdDriver.isVisible && mDriver == null)) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.driver)}")
//            mBinding.edtDriverNameLayout.TextAppearance(R.style.TextMandatoryInputLayout)
            mBinding.edtDriverName.requestFocus()
            return false

        }


        if (mBinding.llViolationTypes.isVisible && ObjectHolder.impoundments.isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide_impoundments))
            return false
        }

        if (mBinding.vehGoodImpoundBottom.isVisible ) {
            if ((mBinding.spnViolationType.selectedItem as LAWViolationType).violationTypeID  == -1) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.violation_type)}")
                return false
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


//        if (violationType?.applicableOnDriver == "Y" && mBinding.edtDriverName.text.toString().trim().isEmpty()) {
//            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.driver)}")
//            mBinding.edtDriverName.requestFocus()
//        }

        return true
    }

    private fun saveAnimalImpound() {
        val impoundment = Impoundment()
        val impoundmentType: LAWImpoundmentType? = mBinding.spnImpoundmentType.selectedItem as LAWImpoundmentType?
        /*impoundmentType?.impoundmentTypeID?.let {
            impoundment.impoundmentTypeID = it
        }
        val impoundmentSubType: LAWImpoundmentSubType? = mBinding.spnImpoundmentSubType.selectedItem as LAWImpoundmentSubType?
        impoundmentSubType?.impoundmentSubTypeID?.let {
            impoundment.impoundmentSubTypeID = it
        }
        val impoundmentReason: LAWImpoundmentReason? = mBinding.spnImpoundmentReason.selectedItem as LAWImpoundmentReason?
        impoundmentReason?.impoundmentReason?.let {
            impoundment.impoundmentReason = it
        }*/
        val policeStation: VUCRMPoliceStation? = mBinding.spnPoliceStation.selectedItem as VUCRMPoliceStation?
        policeStation?.userOrgBranchID?.let {
            impoundment.userOrgBranchID = it
        }
        /*val violationType: LAWViolationType? = mBinding.spnViolationType.selectedItem as LAWViolationType?
        violationType?.pricingRuleID?.let {
            impoundment.pricingRuleID = it
        }
        violationType?.violationTypeID?.let {
            impoundment.violationTypeID = it
        }
        mDriver?.accountID?.let {
            impoundment.driverAccountID = it
        }
        mVehicle?.accountId?.let {
            impoundment.vehicleOwnerAccountID = it
        }*/
        //animal impound related code in true condition
/*        if (impoundmentType?.applicableOnVehicle == "N" && violationType?.applicableOnDriver == "N") {
             mAnimalViolator?.accountID?.let {
                impoundment.goodsOwnerAccountID = it
            }
        } else {*/
        if (mAnimalViolator == null) {
            val animalModel = CitizenModel(
                phone = mBinding.edtAnimalViolatorOwner.text.toString().trim(),
                name = mBinding.layoutAnimalOwnerFName.edtFirstName.text.toString(),
                lastName = mBinding.animalOwnerWrapper.edtLastName.text.toString(),
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
                    zone = (mBinding.layoutAnimalOwnerAddress.spnZone.selectedItem as COMZoneMaster?)?.zone,
                    sectorID = (mBinding.layoutAnimalOwnerAddress.spnSector.selectedItem as COMSectors?)?.sectorId,
                    street = mBinding.layoutAnimalOwnerAddress.edtStreet.text.toString(),
                    description = null,
                    sector = (mBinding.layoutAnimalOwnerAddress.spnSector.selectedItem as COMSectors?)?.sector
                ), FileNameWithExtsn = if (animalOwnerDocList.isNotEmpty()) animalOwnerDocList[0].documentName.plus(".${kFileExtension}") else null,
                FileData = if (animalOwnerDocList.isNotEmpty()) animalOwnerDocList[0].data else null
            )
            impoundment.animalModel = animalModel
        } else {
            mAnimalViolator?.accountID?.let {
                impoundment.goodsOwnerAccountID = it
            }
        }

        if (mImpoundFrom == null) {
            val impoundFrom = CitizenModel(
                phone = mBinding.edtAnimalImpondFrom.text.toString().trim(),
                name = mBinding.layoutImpoundFromWrapperFName.edtFirstName.text.toString(),
                lastName = mBinding.layoutImpoundFromAddress.edtLastName.text.toString(),
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
                    zone = (mBinding.layoutImpoundFromAddress.spnZone.selectedItem as COMZoneMaster?)?.zone,
                    sectorID = (mBinding.layoutImpoundFromAddress.spnSector.selectedItem as COMSectors?)?.sectorId,
                    street = mBinding.layoutImpoundFromAddress.edtStreet.text.toString(),
                    description = null,
                    sector = (mBinding.layoutImpoundFromAddress.spnSector.selectedItem as COMSectors?)?.sector
                )
                , FileNameWithExtsn = if (impoundFromDocList.isNotEmpty()) impoundFromDocList[0].documentName.plus(".${kFileExtension}") else null,
                FileData = if (impoundFromDocList.isNotEmpty()) impoundFromDocList[0].data else null
            )
            impoundment.impoundFrom = impoundFrom
        } else {
            mImpoundFrom?.accountID?.let {
                impoundment.impoundFromAccountID = it
            }
        }
        val violatorType: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
        if (violatorType!!.violatorTypeCode?.toUpperCase(Locale.getDefault()) == Constant.ViolationTypeCode.ANIMAL.code)
            impoundment.violatorTypeCode = violatorType.violatorTypeCode
        //}
        if (mBinding.edtImpoundmentDate.text != null && !TextUtils.isEmpty(mBinding.edtImpoundmentDate.text.toString()))
            impoundment.impoundmentDate = formatDate(mBinding.edtImpoundmentDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyyHHmmss, Constant.DateFormat.DFyyyyMMddHHmmss)
        /*if (mBinding.edtFineAmount.text != null && !TextUtils.isEmpty(mBinding.edtFineAmount.text.toString()))
            impoundment.fineAmount = BigDecimal("${currencyToDouble(mBinding.edtFineAmount.text.toString().trim())}")*/

        //animal impound related code in true condition
/*        if (impoundmentType?.applicableOnVehicle == "N" && violationType?.applicableOnDriver == "N") {
            if (mBinding.edtEstimatedImpoundCharge.text != null && !TextUtils.isEmpty(mBinding.edtEstimatedImpoundCharge.text.toString()))
                impoundment.impoundmentCharge = BigDecimal("${currencyToDouble(mBinding.edtEstimatedImpoundCharge.text.toString().trim())}")
            if (mBinding.edtQuantity.text != null && !TextUtils.isEmpty(mBinding.edtQuantity.text.toString()))
                impoundment.quantity = mBinding.edtQuantity.text.toString().trim().toInt()
        }else {*/
        /*if (mBinding.edtImpoundmentCharge.text != null && !TextUtils.isEmpty(mBinding.edtImpoundmentCharge.text.toString()))
            impoundment.impoundmentCharge = BigDecimal("${currencyToDouble(mBinding.edtImpoundmentCharge.text.toString().trim())}")*/
        // }

        /*if (mBinding.edtRemarks.text != null && !TextUtils.isEmpty(mBinding.edtRemarks.text.toString()))
            impoundment.remarks = mBinding.edtRemarks.text.toString().trim()
        if (mBinding.edtViolationDetails.text != null && !TextUtils.isEmpty(mBinding.edtViolationDetails.text.toString()))
            impoundment.violationDetails = mBinding.edtViolationDetails.text.toString().trim()
        if (mBinding.edtVehicleNo.text != null && !TextUtils.isEmpty(mBinding.edtVehicleNo.text.toString()))
            impoundment.vehicleNo = mBinding.edtVehicleNo.text.toString().trim()
        if (mBinding.edtDrivingLicenseNumber.text != null && !TextUtils.isEmpty(mBinding.edtDrivingLicenseNumber.text.toString()))
            impoundment.drivingLicenseNo = mBinding.edtDrivingLicenseNumber.text.toString().trim()*/
        impoundment.geoAddress = GeoAddress()
        /*if (mBinding.spnCountry.selectedItem != null) {
            val country: COMCountryMaster? = mBinding.spnCountry.selectedItem as COMCountryMaster?
            country?.countryCode?.let {
                impoundment.geoAddress?.countryCode = it
            }
        }
        if (mBinding.spnState.selectedItem != null) {
            val state: COMStateMaster? = mBinding.spnState.selectedItem as COMStateMaster?
            state?.state?.let {
                impoundment.geoAddress?.state = it
            }
        }
        if (mBinding.spnCity.selectedItem != null) {
            val city: VUCOMCityMaster? = mBinding.spnCity.selectedItem as VUCOMCityMaster?
            city?.city?.let {
                impoundment.geoAddress?.city = it
            }
        }*/
        impoundment.geoAddress?.countryCode = adminOfficeAdress?.cntrycode
        impoundment.geoAddress?.state = adminOfficeAdress?.st
        impoundment.geoAddress?.city = adminOfficeAdress?.cty
        impoundment.geoAddress?.cityID = adminOfficeAdress?.ctyid
        impoundment.geoAddress?.stateID = adminOfficeAdress?.stid
        impoundment.geoAddress?.sector = adminOfficeAdress?.sec

        if (mBinding.spnZone.selectedItem != null) {
            val zone: COMZoneMaster? = mBinding.spnZone.selectedItem as COMZoneMaster?
            zone?.zone?.let {
                impoundment.geoAddress?.zone = it
            }
        }
        if (mBinding.spnSector.selectedItem != null) {
            val sector: COMSectors? = mBinding.spnSector.selectedItem as COMSectors?
            sector?.sectorId?.let {
                impoundment.geoAddress?.sectorID = it
            }
        }
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) {
            val street = mBinding.edtStreet.text.toString().trim()
            impoundment.geoAddress?.street = street
        }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) {
            val zipCode = mBinding.edtZipCode.text.toString().trim()
            impoundment.geoAddress?.zipCode = zipCode
        }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) {
            val plot = mBinding.edtPlot.text.toString().trim()
            impoundment.geoAddress?.plot = plot
        }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) {
            val block = mBinding.edtBlock.text.toString().trim()
            impoundment.geoAddress?.block = block
        }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(mBinding.edtDoorNo.text.toString())) {
            val doorNo = mBinding.edtDoorNo.text.toString().trim()
            impoundment.geoAddress?.doorNo = doorNo
        }
        if (mBinding.edtLatitude.text != null && !TextUtils.isEmpty(mBinding.edtLatitude.text.toString())) {
            val latitude = mBinding.edtLatitude.text.toString().trim()
            impoundment.geoAddress?.latitude = latitude
        }
        if (mBinding.edtLongitude.text != null && !TextUtils.isEmpty(mBinding.edtLongitude.text.toString())) {
            val longitude = mBinding.edtLongitude.text.toString().trim()
            impoundment.geoAddress?.longitude = longitude
        }

        val signature = ImpoundSignature()
        val bitmap = mBinding.signatureView.getTransparentSignatureBitmap(true)
        if (bitmap != null)
            signature.data = ImageHelper.getBase64String(mBinding.signatureView.signatureBitmap)

        val storeMultipleImpoundmentTicketPayload = StoreMultipleImpoundmentTicketPayload()
        storeMultipleImpoundmentTicketPayload.impoundment = impoundment
        storeMultipleImpoundmentTicketPayload.impoundmentTicketPayload = ObjectHolder.impoundments
        storeMultipleImpoundmentTicketPayload.fileExtension = mDocument?.extension
        checkVerified()
        checkRemarks()
        storeMultipleImpoundmentTicketPayload.documentsList = ObjectHolder.documents
        storeMultipleImpoundmentTicketPayload.signature = signature
        storeMultipleImpoundmentTicketPayload.fileData = mDocument?.data

        /*ArrayList<MultipleImpoundmentTicketDTO> multipleImpoundmentTicketDTOS,
                                                         Impoundment impoundment, String fileext, String filedata, String signatures,
                                                         ArrayList<COMDocumentReference> attachment, ConnectionCallBack<String> callBack*/

        mListener?.showProgressDialog()
        APICall.getStoreMultipleImpoundmentTicket(storeMultipleImpoundmentTicketPayload, object : ConnectionCallBack<ArrayList<ImpoundmentResponse>> {
            override fun onSuccess(response: ArrayList<ImpoundmentResponse>) {
                mListener?.dismissDialog()
                ObjectHolder.documents.clear()
                ObjectHolder.impoundments.clear()
                mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))
                Handler().postDelayed({
                    Log.e("this is response", "------------ $response")
                    navigateToReceiptScreen(response)
                }, 500)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })

    }

    private fun save() {
        val impoundment = Impoundment()
        val impoundmentType: LAWImpoundmentType? = mBinding.spnImpoundmentType.selectedItem as LAWImpoundmentType?
        impoundmentType?.impoundmentTypeID?.let {
            impoundment.impoundmentTypeID = it
        }
        val impoundmentSubType: LAWImpoundmentSubType? = mBinding.spnImpoundmentSubType.selectedItem as LAWImpoundmentSubType?
        impoundmentSubType?.impoundmentSubTypeID?.let {
            impoundment.impoundmentSubTypeID = it
        }
        val impoundmentReason: LAWImpoundmentReason? = mBinding.spnImpoundmentReason.selectedItem as LAWImpoundmentReason?
        impoundmentReason?.impoundmentReason?.let {
            impoundment.impoundmentReason = it
        }
        val policeStation: VUCRMPoliceStation? = mBinding.spnPoliceStation.selectedItem as VUCRMPoliceStation?
        policeStation?.userOrgBranchID?.let {
            impoundment.userOrgBranchID = it
        }
        val violationType: LAWViolationType? = mBinding.spnViolationType.selectedItem as LAWViolationType?
        violationType?.pricingRuleID?.let {
            impoundment.pricingRuleID = it
        }
        violationType?.violationTypeID?.let {
            impoundment.violationTypeID = it
        }

        if (impoundmentType?.applicableOnVehicle == "Y" && mBinding.crdDriver.isVisible && mDriver == null) {
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
                ), FileNameWithExtsn = if (driverDocList.isNotEmpty()) driverDocList[0].documentName.plus(".${kFileExtension}") else null,
                FileData = if (driverDocList.isNotEmpty()) driverDocList[0].data else null
            )
            impoundment.driverModel = driverModel
        } else {
            mDriver?.accountID?.let {
                impoundment.driverAccountID = it
            }
        }

        //animal impound related code in true condition
/*        if (impoundmentType?.applicableOnVehicle == "N" && violationType?.applicableOnDriver == "N") {
             mAnimalViolator?.accountID?.let {
                impoundment.goodsOwnerAccountID = it
            }
        } else {*/
        mBusiness?.accountID?.let {
            impoundment.goodsOwnerAccountID = it
        }

        val violatorType: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
        if (violatorType!!.violatorTypeCode?.toUpperCase(Locale.getDefault()) == Constant.ViolationTypeCode.VEHICLE.code
                || violatorType.violatorTypeCode?.toUpperCase(Locale.getDefault()) == Constant.ViolationTypeCode.BUSINESS.code)
            impoundment.violatorTypeCode = violatorType.violatorTypeCode

        if (mBinding.edtImpoundmentDate.text != null && !TextUtils.isEmpty(mBinding.edtImpoundmentDate.text.toString()))
            impoundment.impoundmentDate = formatDate(mBinding.edtImpoundmentDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyyHHmmss, Constant.DateFormat.DFyyyyMMddHHmmss)
        if (mBinding.edtFineAmount.text != null && !TextUtils.isEmpty(mBinding.edtFineAmount.text.toString()))
            impoundment.fineAmount = BigDecimal("${currencyToDouble(mBinding.edtFineAmount.text.toString().trim())}")

        //animal impound related code in true condition
/*        if (impoundmentType?.applicableOnVehicle == "N" && violationType?.applicableOnDriver == "N") {
            if (mBinding.edtEstimatedImpoundCharge.text != null && !TextUtils.isEmpty(mBinding.edtEstimatedImpoundCharge.text.toString()))
                impoundment.impoundmentCharge = BigDecimal("${currencyToDouble(mBinding.edtEstimatedImpoundCharge.text.toString().trim())}")
            if (mBinding.edtQuantity.text != null && !TextUtils.isEmpty(mBinding.edtQuantity.text.toString()))
                impoundment.quantity = mBinding.edtQuantity.text.toString().trim().toInt()
        }else {*/
        if (mBinding.edtImpoundmentCharge.text != null && !TextUtils.isEmpty(mBinding.edtImpoundmentCharge.text.toString()))
            impoundment.impoundmentCharge = BigDecimal("${currencyToDouble(mBinding.edtImpoundmentCharge.text.toString().trim())}")

        if (mBinding.edtGoodsValuation.text != null && !TextUtils.isEmpty(mBinding.edtGoodsValuation.text.toString()))
            impoundment.goodsValuation = BigDecimal("${currencyToDouble(mBinding.edtGoodsValuation.text.toString().trim())}")
        // }

        if (mBinding.edtRemarks.text != null && !TextUtils.isEmpty(mBinding.edtRemarks.text.toString()))
            impoundment.remarks = mBinding.edtRemarks.text.toString().trim()
        if (mBinding.edtViolationDetails.text != null && !TextUtils.isEmpty(mBinding.edtViolationDetails.text.toString()))
            impoundment.violationDetails = mBinding.edtViolationDetails.text.toString().trim()

        var vehicleModel : VehicleModel? =null
        if (violatorType.violatorTypeCode?.toUpperCase() == Constant.ViolationTypeCode.VEHICLE.code) {
            if (mVehicle == null) {
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
                    impoundment.vehicleNo = mBinding.edtVehicleNo.text.toString().trim()
            }
        }
        impoundment.vehicleModel = vehicleModel
        var ownerModel: OwnerModel? = null
        if (mVehicle != null && selectedVehicleOwner == null) {
            mVehicle?.accountId?.let {
                impoundment.vehicleOwnerAccountID = it
            }
        } else {
            ownerModel = OwnerModel(
                phone = if (mVehicle == null) mBinding.edtvehCtzOwnName.text.toString()
                    .trim() else null,
                name = mBinding.layoutvehCtzOwnWrapperFName.edtFirstName.text.toString(),
                lastName = mBinding.layoutVehOwnAddress.edtLastName.text.toString(),
                TelephoneCode = telecode,
                geoAddress = if (mVehicle == null) GeoAddress(
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
                acctid = mVehicle?.accountId,
                fromDate = if (mVehicle == null) serverFormatDate(mBinding.edtVehOwnerFromDate.text.toString()) else null
                , FileNameWithExtsn = if (vehicleOwnerDocList.isNotEmpty()) vehicleOwnerDocList[0].documentName.plus(".${kFileExtension}") else null,
                FileData = if (vehicleOwnerDocList.isNotEmpty()) vehicleOwnerDocList[0].data else null
            )
        }
        impoundment.ownerModel = ownerModel
        mDriver?.drivingLicenseNo?.let {
            impoundment.drivingLicenseNo = it
        }
        /*if (mBinding.edtDrivingLicenseNumber.text != null && !TextUtils.isEmpty(mBinding.edtDrivingLicenseNumber.text.toString()))
            impoundment.drivingLicenseNo = mBinding.edtDrivingLicenseNumber.text.toString().trim()*/
        impoundment.geoAddress = GeoAddress()
        /*if (mBinding.spnCountry.selectedItem != null) {
            val country: COMCountryMaster? = mBinding.spnCountry.selectedItem as COMCountryMaster?
            country?.countryCode?.let {
                impoundment.geoAddress?.countryCode = it
            }
        }*/
        impoundment.geoAddress?.countryCode = adminOfficeAdress?.cntrycode
        impoundment.geoAddress?.state = adminOfficeAdress?.st
        impoundment.geoAddress?.city = adminOfficeAdress?.cty
        impoundment.geoAddress?.cityID = adminOfficeAdress?.ctyid
        impoundment.geoAddress?.stateID = adminOfficeAdress?.stid
        impoundment.geoAddress?.sector = adminOfficeAdress?.sec
        /*if (mBinding.spnState.selectedItem != null) {
            val state: COMStateMaster? = mBinding.spnState.selectedItem as COMStateMaster?
            state?.state?.let {
                impoundment.geoAddress?.state = it
            }
        }*/
        /*if (mBinding.spnCity.selectedItem != null) {
            val city: VUCOMCityMaster? = mBinding.spnCity.selectedItem as VUCOMCityMaster?
            city?.city?.let {
                impoundment.geoAddress?.city = it
            }
        }*/
        if (mBinding.spnZone.selectedItem != null) {
            val zone: COMZoneMaster? = mBinding.spnZone.selectedItem as COMZoneMaster?
            zone?.zone?.let {
                impoundment.geoAddress?.zone = it
            }
        }
        if (mBinding.spnSector.selectedItem != null) {
            val sector: COMSectors? = mBinding.spnSector.selectedItem as COMSectors?
            sector?.sectorId?.let {
                impoundment.geoAddress?.sectorID = it
            }
        }
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) {
            val street = mBinding.edtStreet.text.toString().trim()
            impoundment.geoAddress?.street = street
        }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) {
            val zipCode = mBinding.edtZipCode.text.toString().trim()
            impoundment.geoAddress?.zipCode = zipCode
        }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) {
            val plot = mBinding.edtPlot.text.toString().trim()
            impoundment.geoAddress?.plot = plot
        }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) {
            val block = mBinding.edtBlock.text.toString().trim()
            impoundment.geoAddress?.block = block
        }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(mBinding.edtDoorNo.text.toString())) {
            val doorNo = mBinding.edtDoorNo.text.toString().trim()
            impoundment.geoAddress?.doorNo = doorNo
        }
        if (mBinding.edtLatitude.text != null && !TextUtils.isEmpty(mBinding.edtLatitude.text.toString())) {
            val latitude = mBinding.edtLatitude.text.toString().trim()
            impoundment.geoAddress?.latitude = latitude
        }
        if (mBinding.edtLongitude.text != null && !TextUtils.isEmpty(mBinding.edtLongitude.text.toString())) {
            val longitude = mBinding.edtLongitude.text.toString().trim()
            impoundment.geoAddress?.longitude = longitude
        }

        val signature = ImpoundSignature()
        val bitmap = mBinding.signatureView.getTransparentSignatureBitmap(true)
        if (bitmap != null)
            signature.data = ImageHelper.getBase64String(mBinding.signatureView.signatureBitmap)

        val insertImpoundment = InsertImpoundment()
        insertImpoundment.impoundment = impoundment
        insertImpoundment.fileData = mDocument?.data
        insertImpoundment.fileExtension = mDocument?.extension
        checkVerified()
        checkRemarks()
        insertImpoundment.documentsList = ObjectHolder.documents
        insertImpoundment.signature = signature

        mListener?.showProgressDialog()
        APICall.storeImpoundment(insertImpoundment, object : ConnectionCallBack<ImpoundmentResponse> {
            override fun onSuccess(response: ImpoundmentResponse) {
                mListener?.dismissDialog()
                ObjectHolder.documents.clear()
                mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))
                Handler().postDelayed({
                    navigateToReceiptScreen(arrayListOf(response))
                }, 500)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })

    }

    private fun navigateToReceiptScreen(response: ArrayList<ImpoundmentResponse>) {
        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
        val list: ArrayList<GenerateTaxNoticeResponse> = arrayListOf()
        for (item in response) {
            val generateTaxNoticeResponse = GenerateTaxNoticeResponse()
            generateTaxNoticeResponse.taxNoticeID = item.invoiceID
            generateTaxNoticeResponse.taxRuleBookCode = Constant.TaxRuleBook.IMP.Code
            list.add(generateTaxNoticeResponse)
        }
        intent.putExtra(KEY_QUICK_MENU, mCode)
        intent.putExtra(KEY_IMPOUNDMENT_VIOLATION_ID, response.getOrNull(0)?.impoundmentID)
        intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, list)
        /*  intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.IMP.Code)
          intent.putExtra(Constant.KEY_TAX_INVOICE_ID, invoiceId)*/
        startActivity(intent)
        activity?.finish()
    }


    private fun bindSpinners() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("LAW_Impoundments", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mCountries = response.countryMaster
                mStates = response.stateMaster
                mCities = response.cityMaster
                mZones = response.zoneMaster
                mSectors = response.sectors
                mViolators = response.violatorTypes
                /* mImpoundmentTypes = response.impoundmentTypes
                 mImpoundmentSubTypes = response.impoundmentSubTypes
                 mImpoundmentReasons = response.impoundmentReasons*/

                mImpoundmentTypes.addAll(response.impoundmentTypes)
                mImpoundmentSubTypes.addAll(response.impoundmentSubTypes)
                mPoliceStationYards.addAll(response.policeStationYards)
                mCraneTypes.addAll(response.craneTypes)
                mImpoundmentReasons.addAll(response.impoundmentReasons)
                mViolationTypes.addAll(response.violationTypes)
                mPoliceStations = response.policeStations


                /**
                 * Filtering animal related list from the spinner
                 */


                /*if (mImpoundmentTypes.isNullOrEmpty())
                    mBinding.spnImpoundmentType.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mImpoundmentTypes)
                    mBinding.spnImpoundmentType.adapter = adapter
                }*/

                if (mPoliceStations.isNullOrEmpty())
                    mBinding.spnPoliceStation.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mPoliceStations)
                    mBinding.spnPoliceStation.adapter = adapter
                    setStation()
                }

                if (mImpoundmentReasons.isNullOrEmpty())
                    mBinding.spnImpoundmentReason.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mImpoundmentReasons)
                    mBinding.spnImpoundmentReason.adapter = adapter
                }

//                if (mViolationTypes.isNullOrEmpty())
//                    mBinding.spnViolationType.adapter = null
//                else {
//                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mViolationTypes)
//                    mBinding.spnViolationType.adapter = adapter
//                }

                /***
                 *filtering impound violator types which are not necessary to
                 * shown in this screen
                 */
                lawViolatorTypes()


                bindData()

//                filterCountries()
                getAdminOfficeAddressData()
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
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
                    setZones(mZones.getOrNull(0)?.zoneID ?: 0, mBinding.spnZone)
                    setSector(mSectors.getOrNull(0)?.sectorId ?: 0, mBinding.spnSector)

                    //vehicle owner
                    setZones(mZones.getOrNull(0)?.zoneID ?: 0, mBinding.layoutVehOwnAddress.spnZone)
                    setSector(mSectors.getOrNull(0)?.sectorId ?: 0, mBinding.layoutVehOwnAddress.spnSector)

                    //driver
                    setZones(mZones.getOrNull(0)?.zoneID ?: 0, mBinding.layoutDriverAddress.spnZone)
                    setSector(mSectors.getOrNull(0)?.sectorId ?: 0, mBinding.layoutDriverAddress.spnSector)

                    //animal Owner
                    setZones(mZones.getOrNull(0)?.zoneID ?: 0, mBinding.layoutAnimalOwnerAddress.spnZone)
                    setSector(mSectors.getOrNull(0)?.sectorId ?: 0, mBinding.layoutAnimalOwnerAddress.spnSector)

                    //animal impound from
                    setZones(mZones.getOrNull(0)?.zoneID ?: 0, mBinding.layoutImpoundFromAddress.spnZone)
                    setSector(mSectors.getOrNull(0)?.sectorId ?: 0, mBinding.layoutImpoundFromAddress.spnSector)

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
                    mZones
                )
            spnZone.adapter = zoneArrayAdapter
        }
        mZones.forEachIndexed { index, comZoneMaster ->
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
                    mSectors
                )
            spnSector.adapter = sectorArrayAdapter
        }
        mSectors.forEachIndexed { index, comZoneMaster ->
            if (comZoneMaster.sectorId == sectorId) {
                spnSector.setSelection(index)
            }
        }
    }

    fun isAnimalImpoundPopUpVisible(): Boolean {
        return mBinding.rcImpoundFrom.isVisible
    }

    fun isAnimalOwnPopUpVisible(): Boolean {
        return mBinding.rcAnimalOwner.isVisible
    }

    fun isPopRcGoodsOwnerUpVisible(): Boolean {
        return mBinding.rcGoodsOwner.isVisible
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

    fun isImpoundTypeListAvailable(): Boolean{
        return ObjectHolder.impoundments.size > 0
    }

    fun onBackPressed() {
        when {
            isAnimalOwnPopUpVisible() -> {
                mBinding.rcAnimalOwner.isVisible = false
            }
            isAnimalImpoundPopUpVisible() -> {
                mBinding.rcImpoundFrom.isVisible = false
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
            isPopRcGoodsOwnerUpVisible() -> {
                mBinding.rcGoodsOwner.isVisible = false
            }
            isImpoundTypeListAvailable() -> {
                ObjectHolder.impoundments.clear()
            }
        }
    }

    private fun bindViolationClassTypes() {

        val adapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, mViolationClasses)
        mBinding.spnViolationClass.adapter = adapter

        val violationTypesAdapter = ArrayAdapter<LAWViolationType>(requireContext(), android.R.layout.simple_list_item_1, childViolatons)
        mBinding.spnViolationType.adapter = violationTypesAdapter

//        setClassesTypes(0)
    }

    private fun isValidParent(violationClass: LAWViolationType): Boolean {
        for (item in childViolatons) {
            if (item.parentViolationTypeID == violationClass.violationTypeID) {
                return true
            }
        }
        return false
    }

    private fun lawImpoundmentTypesOptAnimalList(violatorTypeCode: String?) {
        mBinding.spnImpoundmentType.adapter = null
        var lawImpoundmentTypes: ArrayList<LAWImpoundmentType> = arrayListOf()
        for (impoundmentType in mImpoundmentTypes) {

            if ((violatorTypeCode).equals(Constant.ViolationTypeCode.BUSINESS.code)) {
                if (impoundmentType.applicableOnGoods == "Y") {
                    lawImpoundmentTypes.add(impoundmentType)
                }
            } else if ((violatorTypeCode).equals(Constant.ViolationTypeCode.VEHICLE.code)) {
                if (impoundmentType.applicableOnVehicle == "Y") {
                    lawImpoundmentTypes.add(impoundmentType)

                }
            }

        }

        if (lawImpoundmentTypes.size > 0) {
            val adapter = ArrayAdapter<LAWImpoundmentType>(requireContext(), android.R.layout.simple_list_item_1, lawImpoundmentTypes)
            mBinding.spnImpoundmentType.adapter = adapter
        }
    }

    private fun lawViolatorTypes() {
        mBinding.spnViolator.adapter = null
        var lawViolatorTypes: ArrayList<LAWViolatorTypes> = arrayListOf()
        for (violationType in mViolators) {
            if (violationType.violatorTypeCode?.toUpperCase(Locale.getDefault()) != Constant.ViolationTypeCode.CITIZEN.code)
                lawViolatorTypes.add(violationType)
        }
        if (lawViolatorTypes.size > 0) {
            val adapter = ArrayAdapter<LAWViolatorTypes>(requireContext(), android.R.layout.simple_list_item_1, lawViolatorTypes)
            mBinding.spnViolator.adapter = adapter
        }
    }

    private fun setStation() {
        for ((index, obj) in mPoliceStations.withIndex()) {
            if (obj.userOrgBranchID == MyApplication.getPrefHelper().userOrgBranchID) {
                mBinding.spnPoliceStation.setSelection(index)
                mBinding.spnPoliceStation.isEnabled = false
                break
            }
        }
    }


    private fun filterImpoundmentSubTypes(impoundmentTypeID: Int) {
        val impoundmentSubTypes: ArrayList<LAWImpoundmentSubType> = arrayListOf()
        if (!mImpoundmentSubTypes.isNullOrEmpty()) {
            for (subType in mImpoundmentSubTypes) {
                if (subType.impoundmentTypeID == impoundmentTypeID)
                    impoundmentSubTypes.add(subType)
            }
        }

        if (impoundmentSubTypes.size > 0) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, impoundmentSubTypes)
            mBinding.spnImpoundmentSubType.adapter = adapter
        } else {
            mBinding.spnImpoundmentSubType.adapter = null
        }
    }

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = ArrayList()
        var index = -1
        val countryCode: String? = "BFA"
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
        val stateID = 100497
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
        val cityID = 100312093
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
            filterZones(cities[index].cityID!!, mBinding.spnZone, mBinding.spnSector)
        } else {
            mBinding.spnCity.adapter = null
            filterZones(cityID, mBinding.spnZone, mBinding.spnSector)
        }
    }

    private fun filterZones(cityID: Int, spnZone: AppCompatSpinner, spnSector: AppCompatSpinner) {
        var zones: MutableList<COMZoneMaster> = ArrayList()
        var index = 0
        val zoneName: String? = ""
        if (cityID <= 0) zones = ArrayList() else {
            for (zone in mZones) {
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
            filterSectors(index, spnSector)
        }
    }

    private fun filterSectors(zoneID: Int, spnSector: AppCompatSpinner) {
        var sectors: MutableList<COMSectors?> = ArrayList()
        var index = 0
        val sectorID = 0
        if (zoneID <= 0) sectors = ArrayList() else {
            for (sector in mSectors) {
                if (sector.zoneId != null && zoneID == sector.zoneId)
                    sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId)
                    index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            spnSector.adapter = sectorArrayAdapter
            spnSector.setSelection(index)
        } else{
            spnSector.adapter = null
            spnSector.isEnabled = false
        }
    }


    private fun filterViolationClasses(parentViolationTypeID: Int?, parentViolationDetail: String? = "") {
        mBinding.spnViolationClass.adapter = null
        var violationTypes: ArrayList<LAWViolationType> = arrayListOf()
        var violationDetail = ""
        parentViolationTypeID?.let { it ->
            parentViolationDetail?.let {
                violationDetail = it
            }
            if (it == 0)
                violationTypes = arrayListOf()
            else {
                for (violationType in mViolationTypes) {
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
        mBinding.edtViolationDetails.setText(violationDetail)
    }

    private fun fetchFineAmount(violationTypeID: Int) {
        mListener?.showProgressDialog()
        APICall.getEstimatedFineAmount(violationTypeID, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mListener?.dismissDialog()
                mBinding.edtFineAmount.setText(formatWithPrecision(response))
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }

        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mHelper?.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mHelper?.onActivityResult(requestCode, resultCode)
        mListener?.showToolbarBackButton(R.string.title_impondment)
        bindCounts()
        if (requestCode == Constant.ImpoundmentDocument.MultipleDocuments.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.multipleDoc.txtNoDataFound.hide()
                mDocumentsList.add(doc)
                documentListAdapter.notifyDataSetChanged()
                ObjectHolder.documents = mDocumentsList
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        } else if (requestCode == Constant.ImpoundmentDocument.VehicleDocument.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.vehDocWrapper.txtNoDataFound.hide()
                mBinding.vehDocWrapper.fabAddImage.disable()
                vehicleDocList.add(doc)
                vehicleDocumentAdapter.notifyDataSetChanged()
            } else {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        } else if (requestCode == Constant.ImpoundmentDocument.VehicleOwnerDocument.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.layoutVehOwnAddress.documentWrapper.txtNoDataFound.hide()
                mBinding.layoutVehOwnAddress.documentWrapper.fabAddImage.disable()
                vehicleOwnerDocList.add(doc)
                vehicleOwnerDocumentAdapter.notifyDataSetChanged()
            } else {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        } else if (requestCode == Constant.ImpoundmentDocument.DriverDocument.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.layoutDriverAddress.documentWrapper.txtNoDataFound.hide()
                mBinding.layoutDriverAddress.documentWrapper.fabAddImage.disable()
                driverDocList.add(doc)
                driverDocumentAdapter.notifyDataSetChanged()
            } else {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        } else if (requestCode == Constant.ImpoundmentDocument.AnimalOwnerDocument.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.layoutAnimalOwnerAddress.documentWrapper.txtNoDataFound.hide()
                mBinding.layoutAnimalOwnerAddress.documentWrapper.fabAddImage.disable()
                animalOwnerDocList.add(doc)
                animalOwnerDocumentAdapter.notifyDataSetChanged()
            } else {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        } else if (requestCode == Constant.ImpoundmentDocument.ImpoundFromDocument.value) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.layoutImpoundFromAddress.documentWrapper.txtNoDataFound.hide()
                mBinding.layoutImpoundFromAddress.documentWrapper.fabAddImage.disable()
                impoundFromDocList.add(doc)
                impoundFromDocumentAdapter.notifyDataSetChanged()
            } else {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        } else if (resultCode == Activity.RESULT_OK) {
            data?.let {
                if (requestCode == Constant.REQUEST_CODE_DRIVER_SEARCH && it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mDriver = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership?
//                    showDriverInfo()
                } else if (requestCode == Constant.REQUEST_CODE_VEHICLE_SEARCH && it.hasExtra(Constant.KEY_VEHICLE_OWNERSHIP)) {
                    mVehicle = data.getParcelableExtra(Constant.KEY_VEHICLE_OWNERSHIP) as VehicleDetails?
                    showVehicleInfo()
                } else if (requestCode == Constant.REQUEST_CODE_BUSINESS_SEARCH && it.hasExtra(Constant.KEY_BUSINESS)) {
                    mBusiness = data.getParcelableExtra(Constant.KEY_BUSINESS) as Business?
                    showGoodsOwnerInfo()
                } else if (requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER) {
                    val violatorType: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
                    if (violatorType!!.violatorTypeCode?.toUpperCase(Locale.getDefault()).equals(Constant.ViolationTypeCode.ANIMAL.code)) {
                        mAnimalViolator = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership?
                        setAnimalViolatorInfo()
                    } else {
                        mDriver = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership?
//                        showDriverInfo()
                    }
                } else if (requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH && it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    mAnimalViolator = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership?
                    setAnimalViolatorInfo()
                } else if (requestCode == Constant.REQUEST_CODE_IMPOUND_FROM) {
                    //TODO ADIL
//                    val violatorType: LAWViolatorTypes? = mBinding.spnViolator.selectedItem as LAWViolatorTypes?
                    mImpoundFrom = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership?
                    setAnimalImpoundFromInfo()
                } else if (requestCode == Constant.REQUEST_CODE_IMPOUND_FROM_SEARCH && it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    //TODO ADIL
                    mImpoundFrom = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership?
                    setAnimalImpoundFromInfo()
                } /*else if (requestCode == REQUEST_IMAGE) {
                    if (resultCode == Activity.RESULT_OK) {
                        val options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        val bitmap = BitmapFactory.decodeFile(mImageFilePath, options)
                        mDocumentReference = COMDocumentReference()
                        isDataSourceChanged = true
                        mDocumentReference?.data = ImageHelper.getBase64String(bitmap, 80)
                        mDocumentReference?.documentTypeID = 0
                        mDocumentReference?.documentProofType = null
                        mDocumentReference?.documentTypeName = null
                        mDocumentReference?.documentNo = "${UUID.randomUUID()}"
                        mDocumentReference?.documentName = mDocumentReference?.documentNo
                        mDocumentReference?.extension = "jpg"
                        mDocumnetsList.add(mDocumentReference)
                        val adapter = (mBinding.recyclerView.adapter as ImpoundDocumentsAdapter)
                        adapter.clear()
                        adapter.update(mDocumnetsList as List<COMDocumentReference>)
                        mBinding.recyclerView.visibility = VISIBLE
                        mBinding.txtNoDataFound.visibility = View.GONE
                        ObjectHolder.documents = mDocumnetsList as ArrayList<COMDocumentReference>
//                        Handler().postDelayed({
//                            saveDocument(prepareData())
//                        }, 500)
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        mListener?.showSnackbarMsg(getString(R.string.cancelled))
                    }
                }*/
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

    private fun getVehicleOwnership(key: String) {
        mListener?.showProgressDialog()
        APICall.getVehicleOwnershipDetails(key, object : ConnectionCallBack<VehicleOwnershipDetailsResult> {
            override fun onSuccess(response: VehicleOwnershipDetailsResult) {
                mListener?.dismissDialog()
                val list = response.vehicleDetails
                list?.let {
                    for (vehicle: VehicleDetails in it) {
                        if (vehicle.toDate == null) {
                            mVehicle = vehicle
                            mVehicle?.vehicleNumber = vehicle.vehicleNumber
                            mVehicle?.owner = vehicle.accountName
                            mVehicle?.accountId = vehicle.accountId
//                            showVehicleInfo()
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

    private fun setAnimalViolatorInfo() {
        mAnimalViolator?.let {
//            mBinding.edtAnimalViolatorOwner.setText(it.accountName)
        }
    }

    private fun setAnimalImpoundFromInfo() {
        mImpoundFrom?.let {
            mBinding.edtAnimalImpondFrom.setText(it.accountName)
        }
    }

    private fun showDriverInfo() {
        mDriver?.let {
//            mBinding.edtDriverName.setText(it.accountName)
//            mBinding.edtDrivingLicenseNumber.setText(it.drivingLicenseNo)
        }
    }

    private fun showVehicleInfo() {
        mVehicle?.let {
//            mBinding.edtVehicleNo.setText(it.vehicleNumber)
//            mBinding.edtOwner.setText(it.owner)
        }
    }

    private fun showGoodsOwnerInfo() {
        mBusiness?.let {
//            mBinding.edtGoodsOwner.setText(it.businessName)
        }
    }

    fun setTextInputLayoutHintColor(textInputLayout: TextInputLayout, context: Context, @ColorRes colorIdRes: Int) {
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
    }

    private fun startLocalPreviewActivity(list: ArrayList<COMDocumentReference>) {
        val localDocList = list.map {
            LocalDocument(localSrc = it.localPath)
        }
        val intent = Intent(context, LocalDocumentPreviewActivity::class.java)
        intent.putExtra(KEY_DOCUMENT, ArrayList(localDocList))
        startActivity(intent)
    }

    interface Listener {
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: String?)
        fun showProgressDialog(message: Int)
    }
}