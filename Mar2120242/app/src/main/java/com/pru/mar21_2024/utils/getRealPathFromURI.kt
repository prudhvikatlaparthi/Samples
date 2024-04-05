package com.pru.mar21_2024.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore

fun getRealPathFromURI(context: Context, uri: Uri): String? {
    // Check if the Uri authority is ExternalStorageProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
        if ("com.android.externalstorage.documents" == uri.authority) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return context.getExternalFilesDir(null).toString() + "/" + split[1]
            }
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        // Return the remote address
        if (isGooglePhotosUri(uri)) return uri.lastPathSegment
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(columnIndex)
        }
    } finally {
        cursor?.close()
    }
    return null
}

private fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}
