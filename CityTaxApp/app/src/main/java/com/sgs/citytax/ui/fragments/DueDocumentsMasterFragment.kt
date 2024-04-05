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
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.InsertDocument
import com.sgs.citytax.databinding.FragmentDueAndAgreementMasterListBinding
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.adapter.DueandAgreementDocumentAdapter
import com.sgs.citytax.ui.custom.ImageChangeOrientation
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.activity_collection_histroy.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DueDocumentsMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentDueAndAgreementMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()
    private var isDataSourceChanged = false
    private var mImageFilePath = ""
    private val REQUEST_IMAGE = 100
    private val REQUEST_ROTATE_IMAGE = 150
    private var mDocumentReference: COMDocumentReference? = null

    companion object {
        fun getTableName(screen: Constant.QuickMenu) =
            if (screen == Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_NOTICES)
                "ACC_DueNotices"
            else if (screen == Constant.QuickMenu.QUICK_MENU_AGREEMENT)
                "ACC_DueAgreements"
            else ""

        var mPrimaryKey = ""
        var mAgreementNo = ""
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPrimaryKey = arguments?.getString(Constant.KEY_PRIMARY_KEY) ?: ""
            mAgreementNo = arguments?.getString(Constant.KEY_AGREEMENTNO) ?: ""

        }
        //endregion
        setViews()
        bindData()
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

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_due_and_agreement_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setViews() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> mBinding.fabAdd.visibility = View.VISIBLE
            Constant.ScreenMode.VIEW -> mBinding.fabAdd.visibility = View.GONE
        }
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_AGREEMENT)
        {
            mBinding.btnSave.visibility = View.GONE
        }
        mBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.recyclerView.adapter = DueandAgreementDocumentAdapter(this, fromScreen, mListener?.screenMode)
    }

    private fun bindData() {
      if (!TextUtils.isEmpty(mPrimaryKey)) {
            mListener?.showProgressDialog()
          ObjectHolder.comDocumentReferences.clear()
            APICall.getDueandAgreementDocumentDetails(
                mPrimaryKey,
                getTableName(fromScreen),
                object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        mListener?.dismissDialog()
                        comDocumentReferences = response as ArrayList<COMDocumentReference>
                        val adapter = (mBinding.recyclerView.adapter as DueandAgreementDocumentAdapter)
                        adapter.clear()
                        adapter.update(response)
                        ObjectHolder.dueDocumentCount = adapter.itemCount
                        ObjectHolder.comDocumentReferences = comDocumentReferences
                        if (comDocumentReferences.size==0)
                        {
                            mBinding.recyclerView.visibility = View.GONE
                            mBinding.txtNoDataFound.visibility = View.VISIBLE
                        }
                        else
                        {
                            mBinding.recyclerView.visibility = View.VISIBLE
                            mBinding.txtNoDataFound.visibility = View.GONE
                        }
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        (mBinding.recyclerView.adapter as DueandAgreementDocumentAdapter).clear()
                        mBinding.recyclerView.visibility = View.GONE
                        mBinding.txtNoDataFound.visibility = View.VISIBLE
                        if (message.isNotEmpty()) {
                        }
                        mListener?.showAlertDialog(message)
                    }
                })
        }
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                // region Storage Permission
                if (!hasPermission(requireContext(), Manifest.permission.CAMERA)) {
                    requestForPermission(arrayOf(Manifest.permission.CAMERA),
                        Constant.REQUEST_CODE_CAMERA
                    )
                    return
                }
                // endregion
                openCameraIntent()
            }
        })
        mBinding.btnSave.setOnClickListener (object: OnSingleClickListener() {
            override fun onSingleClick(v: View)
            {
                if (ObjectHolder.dueDocumentCount!! > 0) {
                    UpdateDueNotices()
                }
                else
                {
                    mListener?.showAlertDialog(getString(R.string.txt_message_capture_image))
                }
            }
        })
    }

    private fun UpdateDueNotices() {
        mListener?.showProgressDialog()
        APICall.UpdateDueNotices(ObjectHolder.dueNoticeID,"","", "","","",object :
            ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                ObjectHolder.clearDueNoticeID()
                OnpopBackStack()
                mListener?.showToolbarBackButton(R.string.title_handover_due_notices)
                Handler().postDelayed({
                    targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                }, 500)
            }
            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }
    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {

                R.id.imgDocument -> {
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    comDocumentReferences.remove(comDocumentReference)
                    comDocumentReferences.add(0, comDocumentReference)
                    //intent.putExtra(Constant.KEY_DOCUMENT_URL, comDocumentReference.awsfile)
                    intent.putExtra(Constant.KEY_DOCUMENT, comDocumentReferences)
                    startActivity(intent)
                }

                R.id.btnClearImage -> {
                    deleteDocument(obj as COMDocumentReference)
                }
                else -> {

                }
            }
        }
    }

    private fun deleteDocument(comDocumentReference: COMDocumentReference?) {
        if (null != comDocumentReference?.documentID) {
            mListener?.showProgressDialog()
            APICall.deleteDocument(
                comDocumentReference.documentReferenceID!!.toInt(),
                object : ConnectionCallBack<Boolean> {
                    override fun onSuccess(response: Boolean) {
                        mListener?.dismissDialog()
                        bindData()
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        mListener?.showAlertDialog(message)
                    }
                })
        }
    }


    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun openCameraIntent() {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(mImageFilePath, options)
                mDocumentReference = COMDocumentReference()
                isDataSourceChanged = true
                mDocumentReference?.data = ImageHelper.getBase64String(bitmap, 80)
                mDocumentReference?.extension = "jpg"
                Handler().postDelayed({
                    saveDocument(prepareData())
                }, 500)
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                mListener?.showSnackbarMsg(R.string.cancelled)
            }
        }

    }


    private fun saveDocument(insertDocument: InsertDocument) {
        mListener?.showProgressDialog()
        APICall.insertDocument(insertDocument, object : ConnectionCallBack<Boolean> {
            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                Handler().postDelayed({
                    bindData()
                }, 500)
            }

        })
    }

    private fun prepareData(): InsertDocument {
        val documentReference = COMDocumentReference()
        documentReference.documentName = "pic"
        documentReference.documentNo = ""
        documentReference.documentProofType = ""
        documentReference.data = mDocumentReference?.data
        documentReference.extension = mDocumentReference?.extension
        if (mDocumentReference?.documentReferenceID == null || TextUtils.isEmpty(mDocumentReference?.documentReferenceID))
            mDocumentReference?.documentReferenceID = "${UUID.randomUUID()}"
        documentReference.documentReferenceID = mDocumentReference?.documentReferenceID
        if (!isDataSourceChanged && !TextUtils.isEmpty(mDocumentReference?.documentID) && "0" != mDocumentReference?.documentID)
            documentReference.documentID = mDocumentReference?.documentID
        else isDataSourceChanged = false

        val documentsList: java.util.ArrayList<COMDocumentReference> = arrayListOf()
        documentsList.add(documentReference)

        val insertDocument = InsertDocument()
        insertDocument.attachment = documentsList
        if(!TextUtils.isEmpty(mPrimaryKey)){
            insertDocument.PrimaryKeyValue = mPrimaryKey
            insertDocument.docnoinitial = mAgreementNo
        }
        insertDocument.TableName = getTableName(fromScreen)

        return insertDocument
    }


    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)

    }
    fun OnpopBackStack()
    {
        mListener?.popBackStack()
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun popBackStack()
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode

    }
}