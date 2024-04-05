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
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
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
import com.sgs.citytax.api.payload.StoreFitness
import com.sgs.citytax.api.response.AssetFitnessesData
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.GetFitnessTypes
import com.sgs.citytax.databinding.FragmentEntryFitnessBinding
import com.sgs.citytax.ui.fragments.AssetFitnessMasterFragment.Companion.primaryKey
import com.sgs.citytax.util.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class AssetFitnessEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentEntryFitnessBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING

    private var assetFitnessData: AssetFitnessesData? = null
    private var fitnessTypeList: MutableList<GetFitnessTypes> = arrayListOf()

    private var mImageFilePath = ""
    private val REQUEST_IMAGE = 100
    private var isDataSourceChanged = false
    var extension: String? = null
    var base64Data: String? = null

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            assetFitnessData = arguments?.getParcelable(Constant.KEY_DOCUMENT)
            if (assetFitnessData == null) assetFitnessData = AssetFitnessesData()
        }
        //endregion
        mBinding.edtFitnessDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtFromDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtExpiryDate.setDisplayDateFormat(displayDateFormat)
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry_fitness, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getCorporateOfficeLOVValues("AST_AssetFitnesses", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()

                fitnessTypeList = response.assetFitnessTypes

                if (fitnessTypeList.isNullOrEmpty())
                    mBinding.spnFitnessType.adapter = null
                else {
                    fitnessTypeList.add(0, GetFitnessTypes(-1, getString(R.string.select), 0, 0, ""))
                    val proofAdapter = ArrayAdapter<GetFitnessTypes>(requireContext(), android.R.layout.simple_spinner_dropdown_item, fitnessTypeList)
                    mBinding.spnFitnessType.adapter = proofAdapter
                }

                bindData()
            }

            override fun onFailure(message: String) {
                mBinding.spnFitnessType.adapter = null
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(this)
        mBinding.btnChoose.setOnClickListener(this)
        mBinding.btnClearImage.setOnClickListener(this)

        mBinding.edtFromDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.edtFromDate.text?.toString()?.let {
                    if (it.isNotEmpty())
                        mBinding.edtExpiryDate.setMinDate(parseDate(it, displayDateFormat).time)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
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
                //check here for number is larger than 10 or not
                var cost = mBinding.edtCost.text.toString();
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = mBinding.edtCost.text.toString().toDouble()
                    mBinding.edtCost.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6 + 15))
                    mBinding.edtCost.setText("${formatWithPrecision(enteredText)}")
                }
            }
        }

        assetFitnessData?.let {
            if (assetFitnessData?.awsPath != null && !assetFitnessData?.awsPath.isNullOrEmpty()) {
                mListener?.showProgressDialog(R.string.msg_please_wait)
                Glide.with(requireContext()).load(assetFitnessData?.awsPath).listener(object : RequestListener<Drawable> {
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

            for (fitnessTypeValues in fitnessTypeList)
                if (!TextUtils.isEmpty(fitnessTypeValues.fitnessType) && fitnessTypeValues.fitnessType == assetFitnessData?.fitnessType)
                    mBinding.spnFitnessType.setSelection(fitnessTypeList.indexOf(fitnessTypeValues))

            if (assetFitnessData?.fitnessDate != null && !TextUtils.isEmpty(assetFitnessData?.fitnessDate))
                mBinding.edtFitnessDate.setText(displayFormatDate(assetFitnessData?.fitnessDate))

            if (assetFitnessData?.fitnessNo != 0)
                mBinding.edtFitnessNo.setText(assetFitnessData?.fitnessNo.toString())

            if (assetFitnessData?.vendor != null && !TextUtils.isEmpty(assetFitnessData?.vendor))
                mBinding.edtFitnessInsured.setText(assetFitnessData?.vendor)

            if (assetFitnessData?.invoiceReference != null && !TextUtils.isEmpty(assetFitnessData?.invoiceReference))
                mBinding.edtInvoiceReference.setText(assetFitnessData?.invoiceReference)

            if (assetFitnessData?.cost!! > BigDecimal.ZERO) {
               // mBinding.edtCost.setText(formatWithPrecision(assetFitnessData?.cost.toString()))
                var cost = assetFitnessData?.cost.toString();
                if (!TextUtils.isEmpty(cost)) {
                    val enteredText: Double = assetFitnessData?.cost.toString().toDouble()
                    mBinding.edtCost.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6 + 15))
                    mBinding.edtCost.setText("${formatWithPrecision(enteredText)}")
                }
            }

            if (assetFitnessData?.fromDate != null && !TextUtils.isEmpty(assetFitnessData?.fromDate))
                mBinding.edtFromDate.setText(displayFormatDate(assetFitnessData?.fromDate))

            if (assetFitnessData?.expiryDate != null && !TextUtils.isEmpty(assetFitnessData?.expiryDate))
                mBinding.edtExpiryDate.setText(displayFormatDate(assetFitnessData?.expiryDate))

            if (assetFitnessData?.remarks != null && !TextUtils.isEmpty(assetFitnessData?.remarks))
                mBinding.edtRemarks.setText(assetFitnessData?.remarks)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                if (validateView())
                    saveFitnessDocument(prepareData())
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
//                assetFitnessData?.awsPath = null
                mBinding.btnClearImage.visibility = View.GONE
            }
        }
    }

    private fun prepareData(): StoreFitness {

        val storeFitness = StoreFitness()

        assetFitnessData.let {
            if (it?.fitnessID != 0) {
                storeFitness.fitnessID = it?.fitnessID
            }

            if (it?.documentID != 0)
                storeFitness.docid = it?.documentID
        }

        if (mBinding.edtRemarks.text.toString().trim().isNotEmpty())
            storeFitness.remarks = mBinding.edtRemarks.text.toString().trim()

        if (mBinding.edtCost.text.toString().trim().isNotEmpty())
            storeFitness.cost = currencyToDouble(mBinding.edtCost.text.toString().trim()).toString()

        if (mBinding.edtInvoiceReference.text.toString().trim().isNotEmpty())
            storeFitness.invoiceReference = mBinding.edtInvoiceReference.text.toString().trim()

        if (mBinding.edtExpiryDate.text.toString().trim().isNotEmpty())
            storeFitness.expiryDate = serverFormatDate(mBinding.edtExpiryDate.text.toString().trim())

        if (mBinding.edtFromDate.text.toString().trim().isNotEmpty())
            storeFitness.fromDate = serverFormatDate(mBinding.edtFromDate.text.toString().trim())

        if (mBinding.edtFitnessDate.text.toString().trim().isNotEmpty())
            storeFitness.fitnessDate = serverFormatDate(mBinding.edtFitnessDate.text.toString().trim())

        if (mBinding.edtFitnessNo.text.toString().trim().isNotEmpty())
            storeFitness.fitnessNo = mBinding.edtFitnessNo.text.toString().trim().toInt()

        if (mBinding.edtFitnessInsured.text.toString().trim().isNotEmpty())
            storeFitness.vendor = mBinding.edtFitnessInsured.text.toString().trim()


        if (mBinding.spnFitnessType.selectedItem != null) {
            val type = mBinding.spnFitnessType.selectedItem as GetFitnessTypes
            storeFitness.fitnessTypeID = type.fitnessTypeID
        }

        if (primaryKey > 0)
            storeFitness.assetID = primaryKey

        return storeFitness
    }

    private fun saveFitnessDocument(storeFitness: StoreFitness) {
        if (primaryKey != 0) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
            APICall.storeAssetFitness(storeFitness, extension, base64Data, object : ConnectionCallBack<Int> {
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
        if (mBinding.spnFitnessType.selectedItem == null || -1 == (mBinding.spnFitnessType.selectedItem as GetFitnessTypes).fitnessTypeID) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.fitness_type))
            return false
        }

        if (mBinding.edtFitnessDate.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.fitness_date))
            return false
        }

        if (mBinding.edtFromDate.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.start_date))
            return false
        }

        if (mBinding.edtExpiryDate.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.expiry_date))
            return false
        }

        if (!mBinding.btnClearImage.isVisible) {
            Toast.makeText(requireContext(), getString(R.string.msg_take_picture_to_upload), Toast.LENGTH_SHORT).show()
            return false
        }

        /*if (assetFitnessData?.fitnessID != 0) {
            if (assetFitnessData?.awsPath.isNullOrEmpty()  && mImageFilePath.isEmpty()) {
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