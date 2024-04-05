package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.InsertDocument
import com.sgs.citytax.api.response.COMDocumentType
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentDocumentEntryBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.ComComboStaticValues
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.REQUEST_CODE_CAMERA
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DocumentEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentDocumentEntryBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER

    private var mDocumentReference: COMDocumentReference? = null
    private var documentProof: ComComboStaticValues? = null

    private var mImageFilePath = ""
    private val REQUEST_IMAGE = 100
    private var isDataSourceChanged = false
    private var mResponseDocumentTypeList: MutableList<COMDocumentType> = arrayListOf()
    private var mResponseDocumentTypeListFilter: MutableList<COMDocumentType> = arrayListOf()
    private var mResponseAddressProofList: MutableList<ComComboStaticValues> = arrayListOf()
    var docProofDefaultIndex = -1
    var documentTypeDefaultIndex = -1
    private var isActionEnabled: Boolean = true

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mDocumentReference = arguments?.getParcelable(Constant.KEY_DOCUMENT)
            if (mDocumentReference == null) mDocumentReference = COMDocumentReference()
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_document_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {
        when (fromScreen) {
            Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS,
            Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
                mBinding.txtInpLayDocNo.hint = getString(R.string.business_doc_number)
            }
            else -> {
                mBinding.txtInpLayDocNo.hint = getString(R.string.id_number)
            }
        }
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {
        isActionEnabled = action
        mBinding.edtDocumentProofSpn.isEnabled = action
        mBinding.edtDocumentTypeSpn.isEnabled = action
        mBinding.edtDocumentNo.isEnabled = action
        mBinding.edtDocumentName.isEnabled = action
        mBinding.edtRemarks.isEnabled = action
        mBinding.btnChoose.isEnabled = action
        mBinding.btnClearImage.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
            mBinding.btnChoose.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
            mBinding.btnChoose.visibility = View.GONE
        }
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        var docProofSelected = ComComboStaticValues()
        APICall.getCorporateOfficeLOVValues("COM_Documents", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()

                mResponseAddressProofList = response.comboStaticValues

                if (mResponseAddressProofList.isNullOrEmpty())
                   // mBinding.spnDocumentProofType.adapter = null
                else {
                    // mResponseAddressProofList.add(0, ComComboStaticValues("-1", getString(R.string.select)))
                    if (mResponseAddressProofList.isNullOrEmpty())
                       // mBinding.spnDocumentProofType.adapter = null
                    else {
                        mResponseAddressProofList!!.forEachIndexed { index, docProof ->
                            if (docProof.defntn.equals("Y", ignoreCase = true)) {
                                docProofDefaultIndex = index
                                mBinding.edtDocumentProofSpn.setText(docProof.comboValue)
                                docProofSelected = docProof
                            }
                        }
                    }
                }


                mResponseDocumentTypeList = response.documentTypes

                if (mResponseDocumentTypeList.isNullOrEmpty())
                    mBinding.edtDocumentProofSpn.setText("")
                else {
                    mResponseDocumentTypeList!!.forEachIndexed { index, mResponseDocType ->
                        if (mResponseDocType.defntn.equals("Y", ignoreCase = true)) {
                            documentTypeDefaultIndex = index
                            mBinding.edtDocumentTypeSpn.setText(mResponseDocType.name)
                            if(docProofSelected != null)
                                bindDocTypeSpin(docProofSelected)
                        }
                    }

                }
                setEvents()

                bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun setEvents() {
        if(docProofDefaultIndex == -1)
            mBinding.edtDocumentProofSpn.setText("")
        if(documentTypeDefaultIndex == -1)
            mBinding.edtDocumentTypeSpn.setText("")

        mBinding.edtDocumentProofSpn.setOnClickListener {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mResponseAddressProofList!!)
            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setAdapter(adapter as ArrayAdapter<*>) { dialog, which ->
                dialog.dismiss()
                val any = adapter.getItem(which)

                    mBinding.edtDocumentProofSpn.setText(any?.comboValue)

                   bindDocTypeSpin(any)
            }
            builder.show()
        }

        mBinding.edtDocumentTypeSpn.setOnClickListener {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mResponseDocumentTypeListFilter!!)
            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setAdapter(adapter as ArrayAdapter<*>) { dialog, which ->
                dialog.dismiss()
                val any = adapter.getItem(which)
                mBinding.edtDocumentTypeSpn.setText(any?.name)
            }
            builder.show()
        }

    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (validateView())
                    saveDocument(prepareData())
            }
        })

        mBinding.btnChoose.setOnClickListener(this)
        mBinding.btnClearImage.setOnClickListener(this)
        /*mBinding.spnDocumentProofType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {



            }
        }*/

    }

    private fun bindData() {
        mDocumentReference?.let {
            if (mDocumentReference?.awsfile != null && !mDocumentReference?.awsfile.isNullOrEmpty()) {
                mListener?.showProgressDialog(R.string.msg_please_wait)
                Glide.with(this@DocumentEntryFragment).load(mDocumentReference?.awsfile).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        mListener?.dismissDialog()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        mBinding.btnClearImage.visibility = View.VISIBLE
                        mListener?.dismissDialog()
                        return false
                    }
                }).into(mBinding.imgDocument)
            }
            if (mDocumentReference?.documentNo != null && !TextUtils.isEmpty(mDocumentReference?.documentNo))
                mBinding.edtDocumentNo.setText(mDocumentReference?.documentNo)

            mBinding.edtDocumentName.setText(mDocumentReference?.documentName)
            mBinding.edtRemarks.setText(mDocumentReference?.remarks)

            for (addressProof in mResponseAddressProofList)
                if (!TextUtils.isEmpty(addressProof.comboValue) && addressProof.comboValue == mDocumentReference?.documentProofType) {
                    mBinding.edtDocumentProofSpn.setText(addressProof.comboValue)
                    bindDocTypeSpin(addressProof)
                   // mBinding.edtDocumentProofSpn.callOnClick()
                }
//            for (documentType in mResponseDocumentTypeList)
//                if (documentType.documentTypeID != null && documentType.documentTypeID != 0 && documentType.documentTypeID == mDocumentReference?.documentTypeID)
//                    mBinding.spnDocumentType.setSelection(mResponseDocumentTypeList.indexOf(documentType))
        }
    }

     private fun bindDocTypeSpin(addressProof: ComComboStaticValues?) {
         documentTypeDefaultIndex = -1
         documentProof = ComComboStaticValues()

         documentProof = addressProof as ComComboStaticValues
         Log.e("parent", ">>>" + documentProof?.comboValue)

         mResponseDocumentTypeListFilter.clear()
         for (Proof in mResponseDocumentTypeList) {
             if (documentProof?.comboValue.equals(Proof.docprftyp) /*|| Proof.docprftyp == null*/) {
                 mResponseDocumentTypeListFilter.add(Proof)
             }
         }

         if (mDocumentReference?.documentNo == null && TextUtils.isEmpty(mDocumentReference?.documentNo)) {
             if (mResponseDocumentTypeListFilter.isNullOrEmpty()){
                 mBinding.edtDocumentTypeSpn.setText("")
                 mBinding.edtDocumentTypeSpn.isEnabled = false
             }
             else {
                 if(isActionEnabled) mBinding.edtDocumentTypeSpn.isEnabled = true
                 mResponseDocumentTypeListFilter!!.forEachIndexed { index, mResponseDocType ->
                     if (mResponseDocType.defntn.equals("Y", ignoreCase = true)) {
                         documentTypeDefaultIndex = index
                         mBinding.edtDocumentTypeSpn.setText(mResponseDocType.name)
                     }
                 }
                 if(documentTypeDefaultIndex == -1)
                     mBinding.edtDocumentTypeSpn.setText("")
             }
         } else {

             mResponseDocumentTypeListFilter!!.forEachIndexed { index, mResponseDocType ->
                 if (mResponseDocType.defntn.equals("Y", ignoreCase = true)) {
                     documentTypeDefaultIndex = index
                     mBinding.edtDocumentTypeSpn.setText(mResponseDocType.name)
                 }
             }
             if(documentTypeDefaultIndex == -1)
                 mBinding.edtDocumentTypeSpn.setText("")

             for (documentType in mResponseDocumentTypeListFilter)
                 if (documentType.documentTypeID != null && documentType.documentTypeID != 0 && documentType.documentTypeID == mDocumentReference?.documentTypeID)
                     mBinding.edtDocumentTypeSpn.setText(documentType.name)
         }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
          /*  R.id.btnSave -> {
                if (validateView())
                    saveDocument(prepareData())
            }*/
            R.id.btnChoose -> {
                // region Storage Permission
                if (!hasPermission(requireContext(), Manifest.permission.CAMERA)) {
                    requestForPermission(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
                    return
                }
                // endregion
                openCameraIntent()
            }


            R.id.btnClearImage -> {
                mBinding.imgDocument.setImageBitmap(null)
                mDocumentReference?.data = null
                mDocumentReference?.awsfile = null
                mBinding.btnClearImage.visibility = View.GONE
            }
        }
    }

    private fun prepareData(): InsertDocument {

        val documentReference = COMDocumentReference()

        if (mBinding.edtDocumentNo.text != null) {
            val docNo = mBinding.edtDocumentNo.text.toString().trim()
            if (mDocumentReference?.documentReferenceID.isNullOrEmpty() && (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)) {
                documentReference.documentNo = "${ObjectHolder.registerBusiness.sycoTaxID}_$docNo"
            } else {
                documentReference.documentNo = docNo
            }
        }

        if (mBinding.edtRemarks.text != null)
            documentReference.remarks = mBinding.edtRemarks.text.toString().trim()
        if (mBinding.edtDocumentName.text != null)
            documentReference.documentName = mBinding.edtDocumentName.text.toString().trim()
        documentReference.verified = "Y"

        if (mBinding.edtDocumentProofSpn.text != null) {
            if (mBinding.edtDocumentProofSpn.text.toString() != getString(R.string.select)) {
                documentReference.documentProofType = mBinding.edtDocumentProofSpn.text.toString()
            }
        }

        /*if (mBinding.spnDocumentProofType.selectedItem != null) {
            val proofType = mBinding.spnDocumentProofType.selectedItem as ComComboStaticValues
            documentReference.documentProofType = proofType.comboValue
        }*/

        if (mBinding.edtDocumentTypeSpn.text != null) {
            if (mBinding.edtDocumentTypeSpn.text.toString() != getString(R.string.select)) {
                documentReference.documentTypeName = mBinding.edtDocumentTypeSpn.text.toString()
                documentReference.documentTypeID = getBusinessTypeID(mBinding.edtDocumentTypeSpn.text.toString())
            }
        }
       /* if (mBinding.spnDocumentType.selectedItem != null) {
            val documentType = mBinding.spnDocumentType.selectedItem as COMDocumentType
            documentReference.documentTypeID = documentType.documentTypeID
            documentReference.documentTypeName = documentType.name
        }*/

        documentReference.data = mDocumentReference?.data
        documentReference.extension = mDocumentReference?.extension
        if (mDocumentReference?.documentReferenceID == null || TextUtils.isEmpty(mDocumentReference?.documentReferenceID))
            mDocumentReference?.documentReferenceID = "${UUID.randomUUID()}"
        documentReference.documentReferenceID = mDocumentReference?.documentReferenceID
        if (!isDataSourceChanged && !TextUtils.isEmpty(mDocumentReference?.documentID) && "0" != mDocumentReference?.documentID)
            documentReference.documentID = mDocumentReference?.documentID
        else isDataSourceChanged = false

        val documentsList: ArrayList<COMDocumentReference> = arrayListOf()
        documentsList.add(documentReference)

        val insertDocument = InsertDocument()
        insertDocument.attachment = documentsList
        if (DocumentsMasterFragment.primaryKey != 0) {
            insertDocument.PrimaryKeyValue = DocumentsMasterFragment.primaryKey.toString()
        } else if(!TextUtils.isEmpty(DocumentsMasterFragment.mPrimaryKey)){
            insertDocument.PrimaryKeyValue = DocumentsMasterFragment.mPrimaryKey
        }
        insertDocument.TableName = DocumentsMasterFragment.getTableName(fromScreen)

        return insertDocument
    }

    private fun getBusinessTypeID(toString: String): Int? {
        for (obj in mResponseDocumentTypeListFilter!!) {
            if (obj.name == toString) {
                return obj.documentTypeID
            }
        }
        return null
    }


    private fun saveDocument(insertDocument: InsertDocument) {
        if (DocumentsMasterFragment.primaryKey != 0 || !TextUtils.isEmpty(DocumentsMasterFragment.mPrimaryKey)) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
            APICall.insertDocument(insertDocument, object : ConnectionCallBack<Boolean> {
                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }

                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 500)
                }

            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS || fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER || fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT) {
                val index: Int = indexOfDocument(documentReference.documentReferenceID, ObjectHolder.registerBusiness.documents)
                if (index == -1)
                    ObjectHolder.registerBusiness.documents.add(documentReference)
                else
                    ObjectHolder.registerBusiness.documents[index] = documentReference
                Handler().postDelayed({
                    targetFragment!!.onActivityResult(targetRequestCode, RESULT_OK, null)
                    mListener!!.popBackStack()
                }, 500)
            }
        }*/

    }

    private fun indexOfDocument(primaryKey: String?, documents: List<COMDocumentReference>): Int {
        for (document: COMDocumentReference in documents) {
            if (primaryKey != null && document.documentReferenceID == primaryKey)
                return documents.indexOf(document)
        }
        return -1
    }

    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile: File?
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                LogHelper.writeLog(exception = e)
                return
            }
            val photoUri: Uri = FileProvider.getUriForFile(requireContext(), context?.packageName.toString() + ".provider", photoFile)
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

    private fun validateView(): Boolean {
        if (mBinding.edtDocumentNo.text.toString().trim().isEmpty()) {
            when (fromScreen) {
                Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS, Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
                    mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.business_doc_number))
                }
                else -> {
                    mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.id_number))
                }
            }
            return false
        }

        if (mBinding.edtDocumentName.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.document_name))
            return false
        }

        if ((mDocumentReference?.awsfile == null || TextUtils.isEmpty(mDocumentReference?.awsfile)) && mDocumentReference?.data.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.image))
            return false
        }

        if (mBinding.edtDocumentProofSpn.text == null || "" == mBinding.edtDocumentProofSpn.text.toString()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.document_proof_type))
            return false
        }

        if (mBinding.edtDocumentTypeSpn.text == null || "" == mBinding.edtDocumentTypeSpn.text.toString()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.document_type))
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (isPermissionGranted(grantResults))
                openCameraIntent()
            else
                mListener!!.showAlertDialog(getString(R.string.msg_permission_storage_camera))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                isDataSourceChanged = true
                val file = File(mImageFilePath)
                if (file.exists()) {
                    Glide.with(mBinding.imgDocument).load(File(mImageFilePath).absolutePath).into(mBinding.imgDocument)
                } else {
                    mBinding.imgDocument.setImageURI(Uri.parse(mImageFilePath))
                }
                mBinding.btnClearImage.visibility = View.VISIBLE
                mDocumentReference?.data = ImageHelper.getBase64String(ImageHelper.decodeFile(File(mImageFilePath)))
                mDocumentReference?.extension = "jpg"
            } else if (resultCode == RESULT_CANCELED) {
                mListener!!.showSnackbarMsg(getString(R.string.cancelled))
            }
        }
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun showSnackbarMsg(message: String)
        fun showProgressDialog(message: Int)
        fun dismissDialog()
        fun popBackStack()
        var screenMode: Constant.ScreenMode

    }

}

