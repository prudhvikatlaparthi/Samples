package com.pru.touchnote.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pru.touchnote.R
import com.pru.touchnote.data.model.Data
import com.pru.touchnote.utils.showToast
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.android.synthetic.main.appbar_layout.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class UserDetailActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var mLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        toolbar_title.text = "User Details"
        toolbar_left.visibility = View.VISIBLE
        toolbar_left.setOnClickListener {
            onBackPressed()
        }
        val details: Data = intent.getSerializableExtra("DETAILS") as Data
        setData(details)

        bt_select_photo.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            } else {
                pickImages()
            }
        }
        bt_continue.setOnClickListener {
            bt_continue.visibility = GONE
            val bitmap = getBitmapFromView(root_layout)
            bt_continue.visibility = VISIBLE
            if (bitmap != null) {
                val file = getFile(bitmap)
                if (file != null) {
                    scanImages(file)
                    showToast("Success! saved the image in downloads.")
                    onBackPressed()
                } else {
                    showToast("Error! some thing went wrong.")
                }
            }
        }

        mLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                bt_continue.isEnabled = true
                bt_continue.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                bt_select_photo.visibility = GONE
                val data: Intent? = it.data
                img.setImageURI(data?.data)
            }
        }
    }

    private fun pickImages() {
        if (this::mLauncher.isInitialized) {
            mLauncher.launch(
                Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )
        }
    }

    private fun setData(details: Data) {
        tv_id.text = details.id.toString()
        tv_name.text = details.name
        tv_email.text = details.email
        tv_gender.text = details.gender

    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun getFile(bitmap: Bitmap): String? {
        var filePath: String? = null
        try {
            val bs = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.PNG, 100, bs)
            filePath = saveBitmap(bs.toByteArray())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return filePath
    }

    fun saveBitmap(bytes: ByteArray?): String? {
        if (bytes == null) {
            return null
        }
        var filePath: File? = null
        try {
            var fos: FileOutputStream? = null
            filePath = getDocumentsFileDir()
            fos = FileOutputStream(filePath)
            fos.write(bytes)
            fos.flush()
            fos.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return if (filePath != null) filePath.getPath() else null
    }

    private fun getDocumentsFileDir(): File? {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "touchnote_android_task_" + System.currentTimeMillis() + ".png"
        );
    }

    private fun scanImages(path: String) {
        try {
            MediaScannerConnection.scanFile(
                this, arrayOf(path), null
            ) { _, _ -> }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        pickImages()
    }

    private fun requestPermissions() {
        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            pickImages()
            return
        }
        EasyPermissions.requestPermissions(
            this,
            "You need to accept storage permission in-order to view the Gallery",
            1000,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setThemeResId(R.style.AlertDialogCustom).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}