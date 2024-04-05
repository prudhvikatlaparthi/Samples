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
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.ServiceRequestTable
import com.sgs.citytax.api.response.VUCRMServiceRequest
import com.sgs.citytax.databinding.FragmentTaskEntryBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.COMStatusCode
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.adapter.DocumentPreviewAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.REQUEST_CODE_CAMERA
import com.sgs.citytax.util.Constant.REQUEST_CODE_STORAGE
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class TaskEntryFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentTaskEntryBinding
    private var mServiceRequest: VUCRMServiceRequest? = null
    private var mStatusCodes: MutableList<COMStatusCode>? = null
    private var mListener: Listener? = null
    private lateinit var helper: LocationHelper
    private var documentsList: ArrayList<COMDocumentReference> = arrayListOf()
    private val mDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()
    private var mDocumentReference : COMDocumentReference? = null

    private val REQUEST_IMAGE = 100
    private var mImageFilePath = ""

    companion object {
        @JvmStatic
        fun newInstance(serviceRequest: VUCRMServiceRequest) = TaskEntryFragment().apply {
            mServiceRequest = serviceRequest
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onResume() {
        super.onResume()
        helper = LocationHelper(requireActivity(), mBinding.btnSave, fragment = this)
    }

    override fun onDestroy() {
        super.onDestroy()
        helper.disconnect()
    }

    override fun initComponents() {
        initEvents()
        bindSpinner()
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_ServiceRequests", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()
                mStatusCodes = response.statusCodes
                context?.let {
                    val adapter = ArrayAdapter<COMStatusCode>(it, android.R.layout.simple_spinner_dropdown_item, response.statusCodes)
                    mBinding.spnStatus.adapter = adapter
                }
               // bindData()
                fetchComments()
            }

            override fun onFailure(message: String) {
                mBinding.spnStatus.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun bindData() {
        if (mServiceRequest != null) {
            if (mStatusCodes != null) {
                for ((index, obj) in mStatusCodes!!.withIndex()) {
                    if (mServiceRequest!!.statusCode!!.contentEquals(obj.statusCode!!)) {
                        mBinding.spnStatus.setSelection(index)
                    }
                }
            }
            if (mServiceRequest!!.status == "Closed") {
                mBinding.spnStatus.isEnabled = false
                mBinding.taskCommentsLayout.visibility = View.GONE
                mBinding.btnCamera.visibility = View.GONE
                mBinding.btnSave.visibility = View.GONE
                mBinding.ivAddLocation.visibility = View.GONE
            }
            mBinding.incidentTaskLabel.text = if (mServiceRequest?.taskType?.toLowerCase(Locale.ROOT) == "incident")
                getString(R.string.label_incident_no)
            else if(mServiceRequest?.taskType?.toLowerCase(Locale.ROOT) == "service") getString(R.string.label_service_request_no)
            else getString(R.string.label_complaint_no)

            mBinding.startTime.text = mServiceRequest?.startTime?.let { formatDisplayDateTimeInMillisecond(it) }
            mBinding.targetEndTime.text = mServiceRequest?.targetEndTime?.let { formatDisplayDateTimeInMillisecond(it) }
            mBinding.assignTime.text = mServiceRequest?.assignTime?.let { formatDisplayDateTimeInMillisecond(it) }
            mBinding.closedTime.text = mServiceRequest?.closedTime?.let { formatDisplayDateTimeInMillisecond(it) }
            mBinding.dateText.text = mServiceRequest?.complaintDate?.let { formatDisplayDateTimeInMillisecond(it) }
            mBinding.parentServiceRequestNo.text = mServiceRequest?.parentServiceRequestNo ?: ""
            mBinding.taskNo.text =mServiceRequest?.serviceRequestNo.toString()
            mBinding.taskAssignText.text = mServiceRequest?.assignedTo ?: ""
            mBinding.taskDescText.text = mServiceRequest?.issueDescription ?: ""
            mBinding.taskCategoryText.text = mServiceRequest?.taskType ?: ""
            mServiceRequest?.taskCategory?.let {
                mBinding.llTaskType.visibility = View.VISIBLE
                mBinding.taskTypeText.text = it
            }

            mServiceRequest?.taskSubCategory?.let {
                mBinding.llTaskSubType.visibility = View.VISIBLE
                mBinding.txtTaskSubType.text = it
            }
            mServiceRequest?.priority?.let {
                mBinding.llPriority.visibility = View.VISIBLE
                mBinding.txtPriority.text = it
            }
            bindLocation()
            mBinding.imgDocument.visibility = View.VISIBLE
        }
    }

    private fun bindLocation() {
        if (mServiceRequest?.latitude != 0.0 && mServiceRequest?.longitude != 0.0 && mServiceRequest?.latitude != null && mServiceRequest?.longitude != null) {
            mBinding.edtLongitude.setText(mServiceRequest?.longitude.toString())
            mBinding.edtLatitude.setText(mServiceRequest?.latitude.toString())
            mBinding.ivAddLocation.isEnabled = false
           // fetchComments()
        } else {
            helper.fetchLocation()
            helper.setListener(object : LocationHelper.Location {
                override fun found(latitude: Double, longitude: Double) {
                    mListener?.dismissDialog()
                    mBinding.edtLongitude.setText(longitude.toString())
                    mBinding.edtLatitude.setText(latitude.toString())
                    //fetchComments()
                }

                override fun start() {
                    mListener?.showProgressDialog(R.string.msg_location_fetching)
                }
            })
        }
    }

    private fun fetchComments() {
        mListener?.showProgressDialog()
        APICall.getCommentsAndDocuments(mServiceRequest?.serviceRequestNo.toString(), false, object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                bindComments(response.serviceRequestTable)
                bindService(response.vUCRMServiceRequests)
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }
    fun bindService(serviceData:List<VUCRMServiceRequest>){
        mServiceRequest=serviceData[0]
        bindData()
    }

    fun bindComments(serviceRequests: List<ServiceRequestTable>) {
        if (serviceRequests.isNotEmpty()) {
            var view: View
            for (item in serviceRequests) {
                view = layoutInflater.inflate(R.layout.view_comments, mBinding.parentLayout, false)
                val commentedByText = view.findViewById<View>(R.id.commented_by_text) as TextView
                val commentsText = view.findViewById<View>(R.id.comments_text) as TextView
                val commentedDateTex = view.findViewById<View>(R.id.commented_date_tex) as TextView
                commentedByText.text = item.modifiedByName
                commentsText.text = item.comments
                commentedDateTex.text = formatDisplayDateTimeInMillisecond(item.commentDate)
                mBinding.taskCommentsListLayout.addView(view)
                val comDocumentReference = COMDocumentReference()
                comDocumentReference.documentNo = item.documentID
                comDocumentReference.awsfile = item.aWSPath
                mDocumentReferences.add(comDocumentReference)
            }
            mBinding.rcvDocumentPreviews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            mBinding.rcvDocumentPreviews.adapter = DocumentPreviewAdapter(mDocumentReferences, this)
        } else {
            mBinding.commentsTxtview.visibility = View.GONE
        }
    }

    private fun initEvents() {
        mServiceRequest?.statusCode?.let {
            if (mServiceRequest?.statusCode == "CRM_ServiceRequests.Closed") {
                mBinding.btnSave.visibility = View.GONE
                mBinding.ivAddLocation.setOnClickListener(null)
                mBinding.spnStatus.isEnabled = false
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
            mBinding.btnClearImage.visibility = View.GONE
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
    }

    fun validateView(): Boolean {
        if (mBinding.etComments.text.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.label_comments))
            return false
        }
        return true
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
            serviceRequest.description = mServiceRequest?.issueDescription ?: ""
            serviceRequest.serviceRequestDate = mServiceRequest?.serviceRequestDate ?: ""
            serviceRequest.statusCode = (mBinding.spnStatus.selectedItem as COMStatusCode?)?.statusCode
            if (mBinding.edtLatitude.text != null && !TextUtils.isEmpty(mBinding.edtLatitude.text.toString()))
                serviceRequest.latitude = mBinding.edtLatitude.text.toString().toDouble()
            if (mBinding.edtLongitude.text != null && !TextUtils.isEmpty(mBinding.edtLongitude.text.toString()))
                serviceRequest.longitude = mBinding.edtLongitude.text.toString().toDouble()

          /*  mServiceRequest?.serviceRequestNo?.let {
                serviceRequest.serviceRequestNo = it.toString()
                mBinding.etComments.text?.toString()?.let { comment ->
                    srUpdate.comments = comment
                    srUpdate.serviceRequestNo = it.toString()
                }
            }*/
            serviceRequest.serviceRequestNo=mServiceRequest?.serviceRequestNo.toString()
            mBinding.etComments.text?.toString()?.let { comment ->
                srUpdate.comments = comment
                srUpdate.serviceRequestNo = mServiceRequest?.serviceRequestNo.toString()
            }

            val addServiceRequest = NewAddServiceRequest()
            addServiceRequest.context = SecurityContext()
            addServiceRequest.srUpdate = srUpdate
            addServiceRequest.serviceRequest = serviceRequest
            if (documentsList.size > 0)
                addServiceRequest.attachment = documentsList

            mListener?.showProgressDialog(R.string.msg_please_wait)

            APICall.newAddServiceRequest(addServiceRequest, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    if (response) {
                        mListener?.hideKeyBoard()
                        if (mBinding.spnStatus.selectedItem.toString() == "Closed") {
                            mListener?.showAlertDialog(getString(R.string.close_task), DialogInterface.OnClickListener { dialog, _ ->
                                dialog?.dismiss()
                                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent())
                                mListener?.popBackStack()
                            })
                        } else if ("0" != addServiceRequest.serviceRequest?.serviceRequestNo) {
                            mListener?.showSnackbarMsg(R.string.msg_record_update_success)
                            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent())
                            mListener?.popBackStack()
                        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        helper.onRequestPermissionsResult(requestCode, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA && isPermissionGranted(grantResults))
            navigateToCamera()
        else if (requestCode == REQUEST_CODE_STORAGE && isPermissionGranted(grantResults))
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
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
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
            val photoUri: Uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName.toString() + ".provider", photoFile)
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
            documentsList.clear()
            when (requestCode) {
                REQUEST_IMAGE -> {
//                    val photo = data?.extras?.get("data") as Bitmap
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val photo = BitmapFactory.decodeFile(mImageFilePath, options)

                    mBinding.imgDocument.setImageBitmap(photo)
                    mBinding.imgDocument.visibility = View.VISIBLE
                    mBinding.btnClearImage.visibility = View.VISIBLE
                    val cOMDocumentReference = COMDocumentReference()
                    cOMDocumentReference.documentName = System.currentTimeMillis().toString()
                    cOMDocumentReference.extension = "jpeg"
                    cOMDocumentReference.data = ImageHelper.getBase64String(photo,80)
                    documentsList.add(cOMDocumentReference)
                }
//                Constant.REQUEST_GALLERY_IMAGE -> {
//                    val imageUri = data?.data
//                    if (imageUri != null) {
//                        val imageStream: InputStream
//                        try {
//                            imageStream = activity?.contentResolver?.openInputStream(imageUri)!!
//                            val bitmap = BitmapFactory.decodeStream(imageStream)
//                            mBinding.imgDocument.setImageBitmap(bitmap)
//                            mBinding.imgDocument.visibility = View.VISIBLE
//                            val cOMDocumentReference = COMDocumentReference()
//                            cOMDocumentReference.documentName = System.currentTimeMillis().toString()
//                            cOMDocumentReference.extension = ImageHelper.getFileExtension(requireActivity(), imageUri)
//                            cOMDocumentReference.data = ImageHelper.getImageBytes(bitmap)
//                            documentsList.add(cOMDocumentReference)
//                        } catch (e: FileNotFoundException) {
//                            mBinding.imgDocument.setImageBitmap(null)
//                            LogHelper.writeLog(exception = e)
//                        }
//                    }
//                }
            }
        }
        helper.onActivityResult(requestCode, resultCode)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.itemImageDocumentPreview -> {
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    mDocumentReferences.remove(comDocumentReference)
                    mDocumentReferences.add(0, comDocumentReference)
                    intent.putExtra(Constant.KEY_DOCUMENT, mDocumentReferences)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    fun updateText(latitude: Double, longitude: Double) {
        mBinding.edtLatitude.setText(latitude.toString())
        mBinding.edtLongitude.setText(longitude.toString())
    }

    fun onBackPressed() {
        mListener?.popBackStack()
    }

    interface Listener {
        fun popBackStack()
        fun finish()
        fun hideKeyBoard()
        fun dismissDialog()
        fun showProgressDialog(message: Int)
        fun showProgressDialog()
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: String, okListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener)
    }

}