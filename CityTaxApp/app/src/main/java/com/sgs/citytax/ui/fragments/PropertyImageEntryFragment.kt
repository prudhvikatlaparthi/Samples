package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.databinding.FragmentPropertyImageEntryBinding
import com.sgs.citytax.model.COMPropertyImage
import com.sgs.citytax.util.*

class PropertyImageEntryFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentPropertyImageEntryBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var mListener: Listener? = null
    private var mComPropertyImage: COMPropertyImage? = null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_image_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        processIntent()
        setViews()
        bindData()
        setListeners()
    }

    private fun processIntent() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu

            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                propertyID = it.getInt(Constant.KEY_PRIMARY_KEY)

            if (it.containsKey(Constant.KEY_PROPERTY_IMAGE))
                mComPropertyImage = it.getParcelable(Constant.KEY_PROPERTY_IMAGE)

            if (mComPropertyImage == null) mComPropertyImage = COMPropertyImage()
        }
    }

    private fun setViews() {
        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            mBinding.edtDescription.isEnabled = false
            mBinding.checkbox.isEnabled = false
            mBinding.btnChoose.isEnabled = false
            mBinding.imgProperty.isEnabled = false
            mBinding.btnClearImage.isEnabled = false
            mBinding.btnSave.visibility = View.GONE
        }

    }

    private fun setListeners() {
        mBinding.btnChoose.setOnClickListener(this)
        mBinding.btnClearImage.setOnClickListener(this)
        mBinding.btnSave.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (isValid())
                    saveImage()
            }
        })
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
            data?.let {
                val photo = it.extras?.get("data") as Bitmap
                mBinding.imgProperty.setImageBitmap(photo)
                mBinding.btnClearImage.visibility = View.VISIBLE
                mComPropertyImage?.fileNameWithExtension = "Property.jpeg"
                mComPropertyImage?.data = ImageHelper.getBase64String(photo)
            }
        }
    }

    fun openCamera() {
        if (!hasPermission(requireActivity(), Manifest.permission.CAMERA)) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CODE_CAMERA)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, Constant.REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnChoose -> {
                    openCamera()
                }

                R.id.btnClearImage -> {
                    mBinding.imgProperty.setImageBitmap(null)
                    mComPropertyImage?.data = null
                    mComPropertyImage?.awsPath = null
                    mBinding.btnClearImage.visibility = View.GONE
                }

            }
        }
    }

    private fun bindData() {
        if (mComPropertyImage != null) {

            if (mComPropertyImage?.awsPath != null && !mComPropertyImage?.awsPath.isNullOrEmpty()) {
                mListener?.showProgressDialog(R.string.msg_please_wait)
                Glide.with(this).load(mComPropertyImage?.awsPath).listener(object : RequestListener<Drawable> {
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

            mComPropertyImage?.description?.let {
                mBinding.edtDescription.setText(it)
            }

            mComPropertyImage?.default?.let {
                mBinding.checkbox.isChecked = it == "Y"
            }
        }

    }

    private fun prepareData(): COMPropertyImage {
        val propertyImage = COMPropertyImage()

        if (!mBinding.edtDescription.text.toString().isEmpty())
            propertyImage.description = mBinding.edtDescription.text.toString().trim()

        if (mComPropertyImage != null && mComPropertyImage?.propertyImageID != 0)
            propertyImage.propertyImageID = mComPropertyImage?.propertyImageID

        if (mBinding.checkbox.isChecked)
            propertyImage.default = "Y"
        else
            propertyImage.default = "N"

        propertyID?.let {
            propertyImage.propertyID = it
        }
        if (0 != mComPropertyImage?.photo)
            propertyImage.photo = mComPropertyImage?.photo

        propertyImage.data = mComPropertyImage?.data
        propertyImage.fileNameWithExtension = mComPropertyImage?.fileNameWithExtension

        return propertyImage
    }

    private fun saveImage() {
        mListener?.showProgressDialog()
        APICall.storePropertyImage(prepareData(), object : ConnectionCallBack<Int> {
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
        if ((mComPropertyImage?.awsPath == null || TextUtils.isEmpty(mComPropertyImage?.awsPath)) && mComPropertyImage?.data.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.image))
            return false
        }

        if (mBinding.edtDescription.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.enter_description))
            return false
        }
        return true
    }


    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun showSnackbarMsg(message: String)
        fun showProgressDialog(message: Int)
        fun showProgressDialog()
        fun dismissDialog()
        fun popBackStack()
        fun showToast(message: String)
        var screenMode: Constant.ScreenMode
    }
}