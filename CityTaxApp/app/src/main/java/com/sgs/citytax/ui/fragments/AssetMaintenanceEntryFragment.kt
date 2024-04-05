package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.InputFilter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.StoreMaintenance
import com.sgs.citytax.api.response.AssetMaintenanceData
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.GetMaintenanceTypes
import com.sgs.citytax.databinding.FragmentEntryMaintenanceBinding
import com.sgs.citytax.ui.fragments.AssetMaintenanceMasterFragment.Companion.primaryKey
import com.sgs.citytax.util.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class AssetMaintenanceEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentEntryMaintenanceBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING

    private var assetMaintenanceData: AssetMaintenanceData? = null
    private var maintenanceTypeList: MutableList<GetMaintenanceTypes> = arrayListOf()

    private var mImageFilePath = ""
    private val REQUEST_IMAGE = 100
    private var isDataSourceChanged = false
    var extension: String? = null
    var base64Data: String? = null

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            assetMaintenanceData = arguments?.getParcelable(Constant.KEY_DOCUMENT)
            if (assetMaintenanceData == null) assetMaintenanceData = AssetMaintenanceData()
        }
        //endregion
        mBinding.edtMaintenanceDate.setDisplayDateFormat(displayDateFormat)
        bindSpinner()
        setListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry_maintenance, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getCorporateOfficeLOVValues("AST_AssetMaintenance", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()

                maintenanceTypeList = response.assetMaintenanceeTypes

                if (maintenanceTypeList.isNullOrEmpty())
                    mBinding.spnMaintenanceType.adapter = null
                else {
                    maintenanceTypeList.add(0, GetMaintenanceTypes(-1, getString(R.string.select)))
                    val proofAdapter = ArrayAdapter<GetMaintenanceTypes>(requireContext(), android.R.layout.simple_spinner_dropdown_item, maintenanceTypeList)
                    mBinding.spnMaintenanceType.adapter = proofAdapter
                }

                bindData()
            }

            override fun onFailure(message: String) {
                mBinding.spnMaintenanceType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.btnChoose.setOnClickListener(this)
        mBinding.btnClearImage.setOnClickListener(this)
    }

    private fun bindData() {

        mBinding.edtCost.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtCost.text.toString()
                if (text?.isNotEmpty()!!) {
                    mBinding.edtCost.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
                    mBinding.edtCost.setText("${currencyToDouble(text)}");
                }
            } else {
                //this if condition is true when edittext lost focus...
                //check here for number is larger than 10 or
                var cost = mBinding.edtCost.text.toString();
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = mBinding.edtCost.text.toString().toDouble()
                    mBinding.edtCost.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6 + 15))
                    mBinding.edtCost.setText("${formatWithPrecision(enteredText)}")
                }
            }
        }


        assetMaintenanceData?.let {
            if (assetMaintenanceData?.awsPath != null && !assetMaintenanceData?.awsPath.isNullOrEmpty()) {
                mListener?.showProgressDialog(R.string.msg_please_wait)
                Glide.with(requireContext()).load(assetMaintenanceData?.awsPath).listener(object : RequestListener<Drawable> {
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

            for (maintenanceTypeValues in maintenanceTypeList)
                if (!TextUtils.isEmpty(maintenanceTypeValues.maintenanceType) && maintenanceTypeValues.maintenanceType == assetMaintenanceData?.maintenanceType)
                    mBinding.spnMaintenanceType.setSelection(maintenanceTypeList.indexOf(maintenanceTypeValues))

            if (assetMaintenanceData?.maintenanceDate != null && !TextUtils.isEmpty(assetMaintenanceData?.maintenanceDate))
                mBinding.edtMaintenanceDate.setText(displayFormatDate(assetMaintenanceData?.maintenanceDate))

            if (assetMaintenanceData?.vendor != null && !TextUtils.isEmpty(assetMaintenanceData?.vendor))
                mBinding.edtVendor.setText(assetMaintenanceData?.vendor)

            if (assetMaintenanceData?.invoiceReference != null && !TextUtils.isEmpty(assetMaintenanceData?.invoiceReference))
                mBinding.edtInvoiceReference.setText(assetMaintenanceData?.invoiceReference)

            if (assetMaintenanceData?.distanceTravelled != 0)
                mBinding.edtDistanceTravelled.setText(assetMaintenanceData?.distanceTravelled.toString())

            if (assetMaintenanceData?.maintenanceDetails != null && !TextUtils.isEmpty(assetMaintenanceData?.maintenanceDetails))
                mBinding.edtMaintenanceDetails.setText(assetMaintenanceData?.maintenanceDetails)

            if (assetMaintenanceData?.totalCost!! > BigDecimal.ZERO) {
                //mBinding.edtCost.setText(formatWithPrecision(assetMaintenanceData?.totalCost.toString()))
                var cost = assetMaintenanceData?.totalCost.toString();
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = assetMaintenanceData?.totalCost.toString().toDouble()
                    mBinding.edtCost.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6 + 15))
                    mBinding.edtCost.setText("${formatWithPrecision(enteredText)}")
                }
            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                if (validateView())
                    saveMaintenanceDocument(prepareData())
            }
            R.id.btnChoose -> {
                // region Storage Permission
                if (!hasPermission(requireContext(), Manifest.permission.CAMERA)) {
                    requestForPermission(arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CODE_CAMERA)
                    return
                }
                // endregion
                openCameraIntent()
            }


            R.id.btnClearImage -> {
                mBinding.imgDocument.setImageBitmap(null)
//                assetMaintenanceData?.awsPath = null
                mBinding.btnClearImage.visibility = View.GONE
            }
        }
    }

    private fun prepareData(): StoreMaintenance {

        val storeMaintenance = StoreMaintenance()

        assetMaintenanceData.let {
            if (it?.maintenanceID != 0) {
                storeMaintenance.maintenanceID = it?.maintenanceID
            }

            if (it?.documentID != 0)
                storeMaintenance.docid = it?.documentID
        }

        if (mBinding.edtCost.text.toString().trim().isNotEmpty())
            storeMaintenance.totalCost = BigDecimal(currencyToDouble(mBinding.edtCost.text.toString()) as Long)

        if (mBinding.edtInvoiceReference.text.toString().trim().isNotEmpty())
            storeMaintenance.invoiceReference = mBinding.edtInvoiceReference.text.toString().trim()

        if (mBinding.edtMaintenanceDate.text.toString().trim().isNotEmpty())
            storeMaintenance.maintenanceDate = serverFormatDate(mBinding.edtMaintenanceDate.text.toString().trim())

        if (mBinding.edtDistanceTravelled.text.toString().trim().isNotEmpty())
            storeMaintenance.distanceTravelled = mBinding.edtDistanceTravelled.text.toString().trim().toInt()

        if (mBinding.edtMaintenanceDetails.text.toString().trim().isNotEmpty())
            storeMaintenance.maintenanceDetails = mBinding.edtMaintenanceDetails.text.toString().trim()

        if (mBinding.edtVendor.text.toString().trim().isNotEmpty())
            storeMaintenance.vendor = mBinding.edtVendor.text.toString().trim()


        if (mBinding.spnMaintenanceType.selectedItem != null) {
            val type = mBinding.spnMaintenanceType.selectedItem as GetMaintenanceTypes
            storeMaintenance.maintenanceTypeID = type.maintenancetypeID
        }

        if (primaryKey > 0)
            storeMaintenance.assetID = primaryKey

        return storeMaintenance
    }

    private fun saveMaintenanceDocument(storeMaintenance: StoreMaintenance) {
        if (primaryKey != 0) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
            APICall.storeAssetMaintenance(storeMaintenance, extension, base64Data, object : ConnectionCallBack<Int> {
                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }

                override fun onSuccess(response: Int) {
                    mListener?.dismissDialog()
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 500)
                }
            })
        }
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
        if (mBinding.spnMaintenanceType.selectedItem == null || -1 == (mBinding.spnMaintenanceType.selectedItem as GetMaintenanceTypes).maintenancetypeID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.maintanence_type))
            return false
        }

        if (mBinding.edtMaintenanceDate.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.maintenance_date))
            return false
        }

        if (!mBinding.btnClearImage.isVisible) {
            Toast.makeText(requireContext(), getString(R.string.msg_take_picture_to_upload), Toast.LENGTH_SHORT).show()
            return false
        }

        /*if (assetMaintenanceData?.maintenanceID != 0) {
            if (assetMaintenanceData?.awsPath.isNullOrEmpty() && mImageFilePath.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.msg_take_picture_to_upload), Toast.LENGTH_SHORT).show()
                return false
            }
        } else if (mImageFilePath.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.msg_take_picture_to_upload), Toast.LENGTH_SHORT).show()
            return false
        }*/

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_CODE_CAMERA) {
            if (isPermissionGranted(grantResults))
                openCameraIntent()
            else
                mListener!!.showAlertDialog(getString(R.string.msg_permission_storage_camera))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                isDataSourceChanged = true
                mBinding.imgDocument.setImageURI(Uri.parse(mImageFilePath))
                mBinding.btnClearImage.visibility = View.VISIBLE
                base64Data = ImageHelper.getBase64String(ImageHelper.decodeFile(File(mImageFilePath)))
                extension = "${formatDateTimeSecondFormat(Date())}.jpg"
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mListener!!.showSnackbarMsg(getString(R.string.cancelled))
            }
        }
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun showSnackbarMsg(message: String)
        fun showProgressDialog(message: Int)
        fun dismissDialog()
        fun popBackStack()
    }

}