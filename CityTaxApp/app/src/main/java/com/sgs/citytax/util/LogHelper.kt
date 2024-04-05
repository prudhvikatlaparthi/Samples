package com.sgs.citytax.util

import android.text.TextUtils
import com.google.api.client.util.Base64
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.base.MyApplication
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


object LogHelper {
    private val kLogFileName =
        "Android-SGS-${MyApplication.getPrefHelper().serialNumber}.log"

    private val kLogPath =
        MyApplication.getContext().getExternalFilesDir(null)?.path.plus("/SGS/Log/")

    private val kFinalLogPath = kLogPath.plus(kLogFileName)

    @Throws(IOException::class)
    private fun getLogFile(): File? {
        var logFile: File? = null
        val logDirectory = File(kLogPath)
        if (logDirectory.exists() || logDirectory.mkdirs()) {
            logFile = File(kFinalLogPath)
            if (!logFile.exists()) logFile.createNewFile()
        }
        return logFile
    }

    @JvmStatic
    fun writeLog(exception: Exception?, message: String? = null) {
        try {
            if ((exception == null && message == null) || TextUtils.isEmpty(MyApplication.getPrefHelper().domain)) {
                return
            }
            val logFile = getLogFile() ?: return
            val bufferWriter = BufferedWriter(FileWriter(logFile, true))
            bufferWriter.append(
                String.format(
                    "%s [%s] [%s] [%s] [%s] - ",
                    getCurrentTimeStamp(),
                    MyApplication.getPrefHelper().domain,
                    MyApplication.getPrefHelper().agentBranch,
                    MyApplication.getPrefHelper().loggedInUserID,
                    MyApplication.getPrefHelper().serialNumber
                )
            )
            if (exception == null) {
                bufferWriter.append(message).append("\n")
                bufferWriter.newLine()
            } else {
                bufferWriter.append(exception.message).append("\n")
                bufferWriter.append(getPrintStackTrace(exception))
                if (!TextUtils.isEmpty(message))
                    bufferWriter.append("--Exception Message--").append(message)
                bufferWriter.append("\n")
            }
            bufferWriter.close()
            val oneMB: Long = 1048576
            if (logFile.length() > oneMB) {
                if (createLogFileCopyWithTimeStamp()) {
                    File(kFinalLogPath).delete()
                }
            }
        } catch (e: Exception) {
            //Don't change this
            e.printStackTrace()
        }
    }

    private fun getLogFilesToUpload(): HashMap<String, String>? {
        val logFolder = File(kLogPath)
        val logFilesToUpload = HashMap<String, String>()
        if (!logFolder.exists()) return null
        logFolder.listFiles { dir, name ->
            if (name != kLogFileName && name.startsWith(
                    kLogFileName.replace(".log", "")
                )
            ) {
                val value =
                    getEncodedFileToBase64Binary(File(dir.path + "/" + name))
                value?.let {
                    logFilesToUpload[name] = it
                }
                true
            } else false
        }
        return logFilesToUpload
    }

    private fun getEncodedFileToBase64Binary(file: File): String? {
        var encodedString: String? = null
        try {
            val bytes: ByteArray? = getFileInBytes(file)
            val encoded: ByteArray = Base64.encodeBase64(bytes)
            encodedString = String(encoded)
        } catch (e: IOException) {
            //Don't change this
            e.printStackTrace()
        }
        return encodedString
    }

    @Throws(IOException::class)
    private fun getFileInBytes(file: File): ByteArray? {
        val inputStream: InputStream = FileInputStream(file)
        var bytes: ByteArray? = null
        try {
            val length = file.length()
            bytes = ByteArray(length.toInt())
            var offset = 0
            var numRead = 0
            while (offset < bytes.size && inputStream.read(bytes, offset, bytes.size - offset)
                    .also {
                        numRead = it
                    } >= 0
            ) {
                offset += numRead
            }
            if (offset < bytes.size) {
                throw IOException("Could not completely read file " + file.name)
            }
            inputStream.close()
        } catch (e: java.lang.Exception) {
            //Don't change this
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: java.lang.Exception) {
                //Don't change this
                e.printStackTrace()
            }
        }
        return bytes
    }

    private fun createLogFileCopyWithTimeStamp(): Boolean {
        var isFileCopied = false
        val copyFile = File(
            String.format(
                "%s-%s.log",
                kFinalLogPath
                    .replace(".log", ""),
                getCurrentTimeStampForLogFileCopy()
            )
        )
        try {
            FileWriter(copyFile).use { fileWriter ->
                readLog()?.let {
                    fileWriter.write(it)
                }
                fileWriter.close()
                isFileCopied = true
            }
        } catch (e: java.lang.Exception) {
            //Don't change this
            e.printStackTrace()
        }
        return isFileCopied
    }

    //endregion
    // region READ
    private fun readLog(): String? {
        var data: String? = null
        var br: BufferedReader? = null
        try {
            br = BufferedReader(
                FileReader(
                    kFinalLogPath
                )
            )
            val sb = StringBuilder()
            var line = br.readLine()
            while (line != null) {
                sb.append(line)
                sb.append("\n")
                line = br.readLine()
            }
            data = sb.toString()
        } catch (e: java.lang.Exception) {
            //Don't change this
            e.printStackTrace()
        } finally {
            try {
                br?.close()
            } catch (e: IOException) {
                //Don't change this
                e.printStackTrace()
            }
        }
        return data
    }

    //region GET
    private fun getPrintStackTrace(e: java.lang.Exception): String {
        val exceptionStackTrace: String
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        exceptionStackTrace = sw.toString()
        return exceptionStackTrace
    }

    private fun getCurrentTimeStamp(): String? {
        val formatter =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun getCurrentTimeStampForLogFileCopy(): String? {
        val formatter = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        return formatter.format(Date())
    }

    /*@JvmStatic
    fun deleteLog() {
        try {
            val logDirectory = File(kLogPath)
            if (logDirectory.exists()) logDirectory.delete()
        } catch (e: java.lang.Exception) {
            LogHelper.writeLog(exception = e)
        }
    }*/

    @JvmStatic
    fun deleteFileLog(name: String) {
        try {
            File(kLogPath.plus(name)).delete()
        } catch (e: java.lang.Exception) {
            //Don't change this
            e.printStackTrace()
        }
    }

    fun isLogFileExist(): Boolean {
        return File(kFinalLogPath).exists()
    }

    @JvmStatic
    fun sendLogFiles(listenerCallback: ((isRunning: Boolean) -> Unit)? = null) {
        resetUploadCounter()
        listenerCallback?.invoke(true)
        if (!isLogFileExist()) {
            listenerCallback?.invoke(false)
            return
        }
        if (createLogFileCopyWithTimeStamp()) {
            File(kFinalLogPath).delete()
            val filesToUpload: HashMap<String, String>? = getLogFilesToUpload()
            if (filesToUpload != null) {
                uploadLogFiles(filesToUpload, listenerCallback)
            } else {
                listenerCallback?.invoke(false)
            }
        } else {
            listenerCallback?.invoke(false)
        }
    }

    private var uploadCounter: Int = -1

    private var isUploadFailed: Boolean = false

    private fun resetUploadCounter() {
        isUploadFailed = false
        uploadCounter = -1
    }

    private fun uploadLogFiles(
        filesToUpload: HashMap<String, String>,
        listenerCallback: ((isRunning: Boolean) -> Unit)?
    ) {
        if (uploadCounter >= filesToUpload.size - 1) {
            if (!isUploadFailed) {
                MyApplication.getPrefHelper().logUploadTime =
                    formatDateTimeInMillisecond(Date())
            }
            listenerCallback?.invoke(false)
            return //loop is finished;
        }

        uploadCounter++
        val keySet: MutableSet<String> = filesToUpload.keys
        val listOfKeys = ArrayList(keySet)
        val key = listOfKeys[uploadCounter]
        val value = filesToUpload[key]
        APICall.uploadLogFileAPI(key, value, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                deleteFileLog(key)
                uploadLogFiles(filesToUpload, listenerCallback)
            }

            override fun onFailure(message: String) {
                isUploadFailed = true
                uploadLogFiles(filesToUpload, listenerCallback)
            }
        })
    }
}