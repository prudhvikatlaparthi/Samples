package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.NewAddServiceRequest
import com.sgs.citytax.api.payload.NewServiceRequest
import com.sgs.citytax.api.payload.SRUpdate
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentIncidentEntryBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.adapter.DocumentPreviewAdapter
import com.sgs.citytax.util.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class IncidentEntryFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentIncidentEntryBinding
    private var mListener: Listener? = null
    private var mServiceRequest: VUCRMServiceRequest? = null
    var mIncidentTypes: MutableList<CRMIncidentMaster>? = null
    var mIncidentSubTypes: MutableList<CRMIncidentSubtype>? = arrayListOf()
    private var helper: LocationHelper? = null
    private var documentsList: ArrayList<COMDocumentReference> = arrayListOf()
    private var mDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()
    private var fromScreen: Any? = null
    private val REQUEST_IMAGE = 100
    private var mImageFilePath = ""
    private var mDocumentReference : COMDocumentReference? = null
    companion object {
        @JvmStatic
        fun newInstance(serviceRequest: VUCRMServiceRequest? = null, fromScreen: Any?) = IncidentEntryFragment().apply {
            this.fromScreen = fromScreen
            mServiceRequest = serviceRequest
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_incident_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        checkIsAddAppending()
        initEvents()
        bindSpinner()
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        helper?.disconnect()
    }

    private fun initEvents() {


        mServiceRequest?.statusCode?.let {
            if (mServiceRequest?.statusCode == "CRM_ServiceRequests.Closed") {
                mBinding.btnSave.visibility = View.GONE
                mBinding.ivAddLocation.setOnClickListener(null)
                mBinding.etComments.isEnabled = false
                mBinding.btnCamera.isEnabled = false
            }
        }
        mBinding.btnSave.setOnClickListener {
            actionSave()
        }
        mBinding.btnCamera.setOnClickListener {
            showImagePickerOptions()
        }
        mBinding.btnClearImage.setOnClickListener {
            mBinding.imgDocument.setImageBitmap(null)
            mDocumentReference?.data = null
            mDocumentReference?.awsfile = null
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_ServiceRequests", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()
                mIncidentTypes = response.incidentMgmtType
                mIncidentSubTypes = response.incidentSubType

                context?.let {
                    val adapter = ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, response.incidentMgmtType)
                    mBinding.spnIncidentType.adapter = adapter
                }

                mBinding.dateText.text = formatDisplayDateTimeInMillisecond(Date())
                mBinding.statusText.text = getString(R.string.status_new)

                mListener?.dismissDialog()

                bindData()
            }

            override fun onFailure(message: String) {
                mBinding.spnIncidentType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun actionSave() {
        if (validateView()) {
            val serviceRequest = NewServiceRequest()
            val srUpdate = SRUpdate()

            serviceRequest.closedTime = mServiceRequest?.closedTime ?: ""
            serviceRequest.assignTime = mServiceRequest?.assignTime ?: ""
            serviceRequest.targetEndTime = mServiceRequest?.targetEndTime ?: ""
            serviceRequest.startTime = mServiceRequest?.startTime ?: ""
            serviceRequest.parentServiceRequestNo = mServiceRequest?.parentServiceRequestNo ?: ""
            serviceRequest.assignToUserID = mServiceRequest?.assignedToUserID ?: ""
            serviceRequest.description = mServiceRequest?.issueDescription
                    ?: mBinding.etDescription.text.toString()
            serviceRequest.serviceRequestDate = mServiceRequest?.serviceRequestDate
                    ?: serverFormatDateTimeInMilliSecond(mBinding.dateText.text.toString())
            serviceRequest.statusCode = mServiceRequest?.statusCode
            serviceRequest.accountID = MyApplication.getPrefHelper().accountId.toString()
            serviceRequest.incidentID = (mBinding.spnIncidentType.selectedItem as CRMIncidentMaster).incidentID
            if (mBinding.spnIncidentSubType.selectedItem != null)
                serviceRequest.incidentSubtypeID = (mBinding.spnIncidentSubType.selectedItem as CRMIncidentSubtype).incidentSubTypeID
            if (mBinding.edtLatitude.text != null && !TextUtils.isEmpty(mBinding.edtLatitude.text.toString()))
                serviceRequest.latitude = mBinding.edtLatitude.text.toString().toDouble()
            if (mBinding.edtLongitude.text != null && !TextUtils.isEmpty(mBinding.edtLongitude.text.toString()))
                serviceRequest.longitude = mBinding.edtLongitude.text.toString().toDouble()

            mServiceRequest?.serviceRequestNo?.let {
                serviceRequest.serviceRequestNo = it.toString()
                mBinding.etComments.text?.toString()?.let { comment ->
                    srUpdate.comments = comment
                    srUpdate.serviceRequestNo = it.toString()
                }
            }

            val addServiceRequest = NewAddServiceRequest()
            addServiceRequest.context = SecurityContext()
            addServiceRequest.srUpdate = srUpdate
            addServiceRequest.serviceRequest = serviceRequest
            val documents: ArrayList<COMDocumentReference> = arrayListOf()
            if (!mDocumentReference?.data.isNullOrEmpty()){
                mDocumentReference?.let { documents.add(it) }
                addServiceRequest.attachment = documents
            } else if (documentsList.size > 0){
                addServiceRequest.attachment = documentsList
            }else{

            }

            mListener?.showProgressDialog(R.string.msg_please_wait)

            APICall.newAddServiceRequest(addServiceRequest, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    if (response) {
                        mListener?.hideKeyBoard()
                        if ((mServiceRequest?.serviceRequestNo != null) && 0 != mServiceRequest?.serviceRequestNo)
                            mListener?.showSnackbarMsg(R.string.msg_record_update_success)
                        else
                            mListener?.showSnackbarMsg(R.string.msg_record_save_success)
                        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent())
                        mListener?.popBackStack()
                    }
                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mListener?.showSnackbarMsg(message)
                    mListener?.dismissDialog()
                }
            })
        }
    }

    private fun setListeners() {
        mBinding.spnIncidentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val incidentSubTypes: ArrayList<CRMIncidentSubtype> = arrayListOf()
                incidentSubTypes.add(0, CRMIncidentSubtype(getString(R.string.select), incidentSubTypeID = -1))
                mIncidentSubTypes?.let {
                    for ((index, obj) in it.withIndex()) {
                        if (obj.incidentID == mIncidentTypes?.get(position)?.incidentID)
                            incidentSubTypes.add(obj)
                    }
                }

                if (incidentSubTypes.isNullOrEmpty())
                    mBinding.spnIncidentSubType.adapter = null
                else {
                    val domainAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, incidentSubTypes)
                    mBinding.spnIncidentSubType.adapter = domainAdapter
                }

                mServiceRequest?.let {
                    incidentSubTypes.let { it2 ->
                        var pos = 0
                        for ((index, obj) in it2.withIndex()) {
                            if (obj.incidentSubTypeID == it.incidentSubtypeID) {
                                pos = index
                                break
                            }
                        }
                        mBinding.spnIncidentSubType.setSelection(pos)
                    }
                }

            }
        }
    }

    private fun checkIsAddAppending() {
        if (mServiceRequest?.serviceRequestNo != null && mServiceRequest?.serviceRequestNo != 0) {
            mBinding.commentsLayout.visibility = VISIBLE
            mBinding.incidentNumberLayout.visibility = VISIBLE
            mBinding.spnIncidentType.isEnabled = false
            mBinding.spnIncidentSubType.isEnabled = false
            mBinding.etDescription.isEnabled = false
        } else {
            mBinding.commentsLayout.visibility = GONE
            mBinding.incidentNumberLayout.visibility = GONE
        }
    }

    fun updateText(latitude: Double, longitude: Double) {
        mBinding.edtLatitude.setText(latitude.toString())
        mBinding.edtLongitude.setText(longitude.toString())
    }

    override fun onResume() {
        super.onResume()
        helper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        helper?.onRequestPermissionsResult(requestCode, grantResults)
        if (requestCode == Constant.REQUEST_CODE_CAMERA && isPermissionGranted(grantResults))
            navigateToCamera()
        else if (requestCode == Constant.REQUEST_CODE_STORAGE && isPermissionGranted(grantResults))
            navigateToGallery()
        else
            mListener?.showAlertDialog(getString(R.string.msg_permission_storage_camera))
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
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(cameraIntent, Constant.REQUEST_IMAGE_CAPTURE)

        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile: File?
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                LogHelper.writeLog(exception = e)
                return
            }
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName.toString() + ".provider",
                photoFile
            )
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(pictureIntent, REQUEST_IMAGE)
        }
    }

        private fun createImageFile(): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "IMG_" + timeStamp + "_"
            val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File.createTempFile(imageFileName, ".jpg", storageDir)
            mImageFilePath = image.absolutePath
            return image
        }

        private fun navigateToGallery() {
        val galleryIntent = Intent(Intent.ACTION_VIEW)
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.select_picture)), Constant.REQUEST_GALLERY_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE -> {
//                    val photo = data?.extras?.get("data") as Bitmap
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val photo = BitmapFactory.decodeFile(mImageFilePath, options)

                    mBinding.imgDocument.setImageBitmap(photo)
                    mBinding.imgDocument.visibility = VISIBLE
                    mBinding.btnClearImage.visibility = VISIBLE
                    documentsList.clear()
                    val cOMDocumentReference = COMDocumentReference()
                    cOMDocumentReference.documentName = System.currentTimeMillis().toString()
                    cOMDocumentReference.extension = "jpeg"
                    cOMDocumentReference.data =  ImageHelper.getBase64String(photo,80)
                    cOMDocumentReference.documentProofType = ""
                    documentsList.add(cOMDocumentReference)
                }
                Constant.REQUEST_GALLERY_IMAGE -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        val imageStream: InputStream
                        try {
                            imageStream = activity?.contentResolver?.openInputStream(imageUri)!!
                            val bitmap = BitmapFactory.decodeStream(imageStream)
                            mBinding.imgDocument.setImageBitmap(bitmap)
                            mBinding.imgDocument.visibility = VISIBLE
                            documentsList.clear()
                            val cOMDocumentReference = COMDocumentReference()
                            cOMDocumentReference.documentName = System.currentTimeMillis().toString()
                            cOMDocumentReference.extension = ImageHelper.getFileExtension(requireActivity(), imageUri)
                            cOMDocumentReference.data = ImageHelper.getImageBytes(bitmap)
                            documentsList.add(cOMDocumentReference)
                        } catch (e: FileNotFoundException) {
                            mBinding.imgDocument.setImageBitmap(null)
                            LogHelper.writeLog(exception = e)
                        }
                    }
                }
            }
        }
        helper?.onActivityResult(requestCode, resultCode)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun bindData() {
        mServiceRequest?.let { it ->
            mBinding.statusText.text = it.status
            mBinding.incidentNo.text = "${it.serviceRequestNo}"
            mBinding.etDescription.setText(it.issueDescription ?: "-")
            mBinding.edtLatitude.setText("${it.latitude}")
            mBinding.edtLongitude.setText("${it.longitude}")
            mBinding.dateText.text = it.serviceRequestDate?.let { date -> formatDisplayDateTimeInMillisecond(date) }
            mBinding.imgDocument.visibility = VISIBLE

            mServiceRequest?.priority?.let {
                mBinding.viewPriority.visibility = VISIBLE
                mBinding.llPriority.visibility = VISIBLE
                mBinding.txtPriority.text = it
            }

            mIncidentTypes?.let { it1 ->
                var pos = 0
                for ((index, obj) in it1.withIndex()) {
                    if (obj.incidentID == it.incidentID) {
                        pos = index
                        break
                    }
                }
                mBinding.spnIncidentType.setSelection(pos)
            }

//            mIncidentSubTypes?.let { it1 ->
//                var pos = 0
//                for ((index, obj) in it1.withIndex()) {
//                    if (obj.incidentSubTypeID == it.incidentSubtypeID) {
//                        pos = index
//                        break
//                    }
//                }
//                mBinding.spnIncidentSubType.setSelection(pos)
//            }

            if (it.accountID != MyApplication.getPrefHelper().accountId || it.status!!.toLowerCase() == "closed") {
                mBinding.incidentCommentsLayout.visibility = GONE
                mBinding.btnCamera.visibility = GONE
                mBinding.btnSave.visibility = GONE
            }
            mBinding.ivAddLocation.isEnabled = fromScreen != Constant.QuickMenu.QUICK_MENU_AGENT_SUMMARY_DETAILS
        }
        bindLocation()
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

    private fun fetchComments() {
        mServiceRequest?.serviceRequestNo?.let {
            mListener?.showProgressDialog()
            APICall.getCommentsAndDocuments(it.toString(), false, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    bindComments(response.serviceRequestTable)
                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }

    fun bindComments(serviceRequestTables: List<ServiceRequestTable>) {
        if (serviceRequestTables.isNotEmpty()) {
            var view: View
            for (item in serviceRequestTables) {
                view = layoutInflater.inflate(R.layout.view_comments, mBinding.parentLayout, false)
                val commentedByText = view.findViewById<View>(R.id.commented_by_text) as TextView
                val commentsText = view.findViewById<View>(R.id.comments_text) as TextView
                val commentedDateTex = view.findViewById<View>(R.id.commented_date_tex) as TextView
                commentedByText.text = item.modifiedByName
                commentsText.text = item.comments
                commentedDateTex.text = formatDisplayDateTimeInMillisecond(item.commentDate)
                mBinding.incidentCommentsListLayout.addView(view)
                val comDocumentReference = COMDocumentReference()
                comDocumentReference.documentNo = item.documentID
                comDocumentReference.awsfile = item.aWSPath
                mDocumentReferences.add(comDocumentReference)
            }
            mBinding.rcvDocumentPreviews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            mBinding.rcvDocumentPreviews.adapter = DocumentPreviewAdapter(mDocumentReferences, this)
        } else {
            mBinding.commentsTxtview.visibility = GONE
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.itemImageDocumentPreview -> {
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    mDocumentReferences.remove(comDocumentReference)
                    mDocumentReferences.add(0, comDocumentReference)
                    //intent.putExtra(Constant.KEY_DOCUMENT_URL, comDocumentReference.awsfile)
                    intent.putExtra(Constant.KEY_DOCUMENT, mDocumentReferences)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    fun validateView(): Boolean {
        if (mServiceRequest?.serviceRequestNo != null && mServiceRequest?.serviceRequestNo != 0 && mBinding.etComments.text.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.label_comments))
            return false
        } else if (mBinding.etDescription.text.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.label_description))
            return false
        }
        return true
    }

    fun onBackPressed() {
        mListener?.popBackStack()
    }

    interface Listener {
        fun hideKeyBoard()
        fun popBackStack()
        fun dismissDialog()
        fun showProgressDialog(message: Int)
        fun showProgressDialog()
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener)
    }

}