package com.sgs.citytax.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.InsertDocument
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityTaxNoticeCaptureBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.KEY_DOCUMENT_NAME
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class TaxNoticeCaptureActivity : BaseActivity() {

    private var documentName: String? = null
    private lateinit var binding: ActivityTaxNoticeCaptureBinding
    private lateinit var helper: LocationHelper
    private var documentsList: ArrayList<COMDocumentReference> = arrayListOf()
    private var taxNoticeId: String? = null
    private var mImageFilePath = ""
    private val REQUEST_IMAGE = 100
    private var mQuickMenuCode = Constant.QuickMenu.QUICK_MENU_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tax_notice_capture)
        showToolbarBackButton(R.string.title_upload_ref_image)
        processIntent()
        initComponents()
    }

    fun initComponents() {
        binding.btnCamera.setOnClickListener {
            showImagePickerOptions()
        }
        binding.btnSave.setOnClickListener {
            saveDocument(documentsList)
        }

        binding.btnClose.setOnClickListener {
            val homeIntent = Intent(this, DashboardActivity::class.java)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
        }
        binding.btnClear.setOnClickListener {
            binding.imgDocument.setImageBitmap(null)
            binding.btnCamera.visibility = VISIBLE
            binding.imgDocument.visibility = GONE
            binding.btnClear.visibility = GONE
        }
    }

    private fun processIntent() {
        val intent = intent
        if (intent != null) {
            if (intent.hasExtra("KEY_TAX_NOTICE_ID")) taxNoticeId = intent.getStringExtra("KEY_TAX_NOTICE_ID")
            if (intent.hasExtra(KEY_DOCUMENT_NAME)) documentName = intent.getStringExtra(KEY_DOCUMENT_NAME)
            if (intent.hasExtra(Constant.KEY_QUICK_MENU)){
                mQuickMenuCode = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.QuickMenu ?: Constant.QuickMenu.QUICK_MENU_NONE
            }
        }
    }

    private fun showImagePickerOptions() {
        takeCameraImage()
    }

    private fun takeCameraImage() {

//        if (!hasPermission(this, Manifest.permission.CAMERA)) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CODE_CAMERA)
//        } else {
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(cameraIntent, Constant.REQUEST_IMAGE_CAPTURE)
//        }

        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(this.packageManager) != null) {
            val photoFile: File?
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                LogHelper.writeLog(exception = e)
                return
            }
            val photoUri: Uri = FileProvider.getUriForFile(this, this?.packageName.toString() + ".provider", photoFile)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(pictureIntent, REQUEST_IMAGE)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mImageFilePath = image.absolutePath
        return image
    }

    override fun onResume() {
        super.onResume()
        helper = LocationHelper(this, binding.imgDocument, activity = this)
    }

    override fun onDestroy() {
        super.onDestroy()
        helper.disconnect()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_CODE_CAMERA) {
            if (isPermissionGranted(grantResults))
                showImagePickerOptions()
            else
                showAlertDialog(getString(R.string.msg_permission_storage_camera))
        }
        helper.onRequestPermissionsResult(requestCode, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        helper.onActivityResult(requestCode, resultCode)
        when (requestCode) {
            REQUEST_IMAGE ->
                if (resultCode == RESULT_OK) {
//                val photo = data?.extras?.get("data") as Bitmap
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    val photo = BitmapFactory.decodeFile(mImageFilePath, options)
                binding.imgDocument.setImageBitmap(photo)
                binding.imgDocument.visibility = VISIBLE
                binding.btnClear.visibility = VISIBLE
                documentsList.clear()
                val comDocumentReference = COMDocumentReference()
                comDocumentReference.documentName = documentName ?: System.currentTimeMillis().toString()
                comDocumentReference.extension = "jpeg"
                comDocumentReference.data = ImageHelper.getBase64String(photo,80)
                comDocumentReference.documentNo = ""
                comDocumentReference.documentProofType = ""
                comDocumentReference.verified = ""
                documentsList.add(comDocumentReference)
            }
        }

    }

    private fun saveDocument(documentReference: ArrayList<COMDocumentReference>) {
        showProgressDialog(R.string.msg_please_wait)

        val insertDocument = InsertDocument()
        insertDocument.PrimaryKeyValue = taxNoticeId
        insertDocument.TableName = getTableName()
        insertDocument.attachment = documentReference

        APICall.insertDocument(insertDocument, object : ConnectionCallBack<Boolean> {
            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }

            override fun onSuccess(response: Boolean) {
                dismissDialog()
                /*if (mCode == Constant.QuickMenu.QUICK_MENU_TAX_NOTICE) {
                    val intent = Intent(this@TaxNoticeCaptureActivity, TaxDetailsActivity::class.java)
                    intent.putExtra(Constant.KEY_CUSTOMER_ID, ObjectHolder.registerBusiness.vuCrmAccounts?.accountId)
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TAX_NOTICE)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }*/
                finish()
            }
        })
    }

    private fun getTableName() =
        when (mQuickMenuCode) {
            Constant.QuickMenu.QUICK_MENU_IMPONDMENT -> "LAW_Impoundments"
            Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE -> "LAW_ViolationTickets"
            else -> "SAL_TaxInvoices"
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}