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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.sgs.citytax.databinding.FragmentPropertyPlanImageEntryBinding
import com.sgs.citytax.model.COMPropertyPlanImage
import com.sgs.citytax.util.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PropertyPlanImageEntryFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentPropertyPlanImageEntryBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu? = null
    private var mPropertyPlanImage: COMPropertyPlanImage? = null
    private var mImageFilePath = ""

    private var propertyID: Int? = 0

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
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_plan_image_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        processIntent()
        setViews()
        bindData()
        setListeners()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_CODE_CAMERA) {
            if (isPermissionGranted(grantResults))
                openCamera()
            else
                mListener?.showToast(getString(R.string.msg_permission_storage_camera))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val photo = BitmapFactory.decodeFile(mImageFilePath, options)
            mBinding.imgProperty.setImageBitmap(photo)
            mBinding.imgProperty.visibility = View.VISIBLE
            mBinding.btnClearImage.visibility = View.VISIBLE
            mPropertyPlanImage?.fileNameWithExtension = "PropertyPlan.jpeg"
            mPropertyPlanImage?.data = ImageHelper.getBase64String(photo,80)
        }
    }

    private fun processIntent() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_PROPERTY_PLAN_IMAGE))
                mPropertyPlanImage = it.getParcelable(Constant.KEY_PROPERTY_PLAN_IMAGE)

            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu

            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                propertyID = it.getInt(Constant.KEY_PRIMARY_KEY)

            if (mPropertyPlanImage == null) mPropertyPlanImage = COMPropertyPlanImage()
        }
    }

    private fun setViews() {
        if (mListener?.screenMode == Constant.ScreenMode.VIEW || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY) {
            mBinding.imgProperty.isEnabled = false
            mBinding.btnClearImage.isEnabled = false
            mBinding.btnSave.visibility = View.GONE
            mBinding.btnChoose.isEnabled = false
        }
    }

    private fun bindData() {
        mPropertyPlanImage?.let { propertyImage ->
            propertyImage.awsPath?.let {
                if (it.isNotEmpty()) {
                    mListener?.showProgressDialog(R.string.msg_please_wait)
                    Glide.with(this).load(it).listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            mListener?.dismissDialog()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            mBinding.btnClearImage.visibility = View.VISIBLE
                            mListener?.dismissDialog()
                            return false
                        }
                    }).into(mBinding.imgProperty)
                }
            }

            propertyImage.description?.let {
                mBinding.edtDescription.setText(it)
            }

            propertyImage.default?.let {
                mBinding.checkbox.isChecked = it == "Y"
            }
        }
    }

    private fun setListeners() {
        mBinding.btnChoose.setOnClickListener(this)
        mBinding.btnClearImage.setOnClickListener(this)
        mBinding.btnSave.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (isValid())
                    save()
            }
        })
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnChoose -> {
                    openCamera()
                }
                R.id.btnClearImage -> {
                    mBinding.imgProperty.setImageBitmap(null)
                    mPropertyPlanImage?.data = null
                    mBinding.btnClearImage.visibility = View.GONE
                }
            }
        }
    }

    private fun openCamera() {
        if (!hasPermission(requireActivity(), Manifest.permission.CAMERA)) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CODE_CAMERA)
        } else {
            val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                val photoFile: File?
                try {
                    photoFile = createImageFile()
                } catch (e: IOException) {
                    LogHelper.writeLog(exception = e)
                    return
                }
                val photoUri: Uri = FileProvider.getUriForFile(requireActivity(), requireActivity()?.packageName.toString() + ".provider", photoFile)
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(pictureIntent, Constant.REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mImageFilePath = image.absolutePath
        return image
    }

    private fun prepareData(): COMPropertyPlanImage {
        val propertyPlanImage = COMPropertyPlanImage()


        if (mPropertyPlanImage != null && mPropertyPlanImage?.propertyPlanId != 0)
            propertyPlanImage.propertyPlanId = mPropertyPlanImage?.propertyPlanId

        propertyID?.let {
            propertyPlanImage.propertyID = it
        }
        if (0 != mPropertyPlanImage?.plan)
            propertyPlanImage.plan = mPropertyPlanImage?.plan

        propertyPlanImage.data = mPropertyPlanImage?.data
        propertyPlanImage.fileNameWithExtension = mPropertyPlanImage?.fileNameWithExtension

        return propertyPlanImage
    }

    private fun save() {
        mListener?.showProgressDialog()
        APICall.storePropertyPlanImage(prepareData(), object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                Handler().postDelayed({
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    mListener?.popBackStack()
                }, 500)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun isValid(): Boolean {
        if (mPropertyPlanImage?.data.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.image))
            return false
        }

        return true
    }

    interface Listener {
        fun showSnackbarMsg(message: String)
        fun showProgressDialog(message: Int)
        fun showProgressDialog()
        fun showAlertDialog(message: String)
        fun dismissDialog()
        fun popBackStack()
        fun showToast(message: String)
        var screenMode: Constant.ScreenMode
    }
}