package com.pru.docviewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow


object PathUtils {
    @JvmStatic
    fun getPathFromUri(uri: Uri): String? {
        // DocumentProvider
        when {
            DocumentsContract.isDocumentUri(MyApp.instance, uri) -> {
                // ExternalStorageProvider
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":".toRegex()).toTypedArray()
                        val type = split[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                        }

                    }
                    isDownloadsDocument(uri) -> {
                        return downloadDocument(uri)
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":".toRegex()).toTypedArray()
                        val type = split[0]
                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }

                            "video" -> {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            }

                            "audio" -> {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }

                            "document" -> {
                                contentUri = MediaStore.Files.getContentUri("external")
                            }
                        }
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(
                            split[1]
                        )
                        return getDataColumn(MyApp.instance, contentUri, selection, selectionArgs)
                    }
                    "content".equals(uri.scheme, ignoreCase = true) && "com.google.android.apps.docs.storage" == uri.authority -> {
                        return downloadFile(MyApp.instance, uri)
                    }
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                // Return the remote address
                if (isGooglePhotosUri(uri)) {
                    return uri.lastPathSegment
                } else if ("com.google.android.apps.docs.storage.legacy" == uri.authority) {
                    return downloadFile(MyApp.instance, uri)
                }
                return getDataColumn(MyApp.instance, uri, null, null)
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                return uri.path
            }
        }
        return null
    }

    private fun downloadDocument(uri: Uri): String? {
        val id = DocumentsContract.getDocumentId(uri)
        if (id != null && id.contains("/storage/emulated/0")) {
            return id
        } else if (!TextUtils.isEmpty(id)) {
            val fileName = getFileName(MyApp.instance, uri)
            return if (TextUtils.isEmpty(fileName)) {
                ""
            } else "/storage/emulated/0/Download/$fileName"
        }
        return ""
    }

    private fun downloadFile(context: Context, uri: Uri): String? {
        val fileName = getFileName(context, uri) ?: return ""
//        val activity = context as Activity
        val file: File = getDocumentsFileDir(context)
        file.mkdirs()
        val fileDir = file.absolutePath + "/" + fileName
        val filePath = File(fileDir)
        val destinationPath: String? = filePath.path
        saveFileFromUri(context, uri, destinationPath)
        return destinationPath
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null) {
            filename = getName(uri.toString())
        } else {
            val returnCursor = context.contentResolver.query(uri, null, null, null, null)
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.getColumnIndex(OpenableColumns.SIZE)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            if (`is` == null) {
                return
            }
            `is`.read(buf)
            do {
                bos.write(buf)
            } while (`is`.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        if (uri == null) return null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            ).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getDocumentsFileDir(activity: Context): File {
        val fileDir = String.format("/Android/data/%s/Documents/", activity.packageName)
        return File(Environment.getExternalStorageDirectory().path + fileDir)
    }

    fun decodeFile(f: File?): Bitmap? {
        var b: Bitmap? = null
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(f)
            BitmapFactory.decodeStream(fis, null, o)
            val finalPic = ExifInterface(f!!.absolutePath)
            var rotation = 0
            val orientation = finalPic.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    rotation = 90
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    rotation = 270
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    rotation = 180
                }
            }
            fis.close()
            val imageMaxSize = 1024
            var scale = 1
            if (o.outHeight > imageMaxSize || o.outWidth > imageMaxSize) {
                scale = 2.0.pow(
                    ceil(
                        ln(
                            imageMaxSize /
                                    o.outHeight.coerceAtLeast(o.outWidth).toDouble()
                        ) / ln(0.5)
                    )
                ).toInt()
            }

            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            fis = FileInputStream(f)
            b = BitmapFactory.decodeStream(fis, null, o2)
            val finalPicUsingMatrix = Matrix()
            finalPicUsingMatrix.postRotate(rotation.toFloat())
            val finalBitmapImage =
                Bitmap.createBitmap(b!!, 0, 0, b.width, b.height, finalPicUsingMatrix, true)
            b = finalBitmapImage
            fis.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            fis?.close()
            //return b
        }
        return b
    }
}