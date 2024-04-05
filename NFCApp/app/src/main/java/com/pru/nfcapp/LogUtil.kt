package com.pru.nfcapp

import android.text.TextUtils
import android.util.Log
import java.util.Locale

object LogUtil {
    private const val TAG = "NFC App"
    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5
    private var LEVEL = VERBOSE

    fun v(msg: String?) {
        if (LEVEL <= VERBOSE && !TextUtils.isEmpty(msg)) {
            myLog(VERBOSE, msg)
        }
    }

    fun d(msg: String?) {
        if (LEVEL <= DEBUG && !TextUtils.isEmpty(msg)) {
            myLog(DEBUG, msg)
        }
    }

    fun i(msg: String?) {
        if (LEVEL <= INFO && !TextUtils.isEmpty(msg)) {
            myLog(INFO, msg)
        }
    }

    fun w(msg: String?) {
        if (LEVEL <= WARN && !TextUtils.isEmpty(msg)) {
            myLog(WARN, msg)
        }
    }

    fun e(msg: String?) {
        if (LEVEL <= ERROR && !TextUtils.isEmpty(msg)) {
            myLog(ERROR, msg)
        }
    }

    private fun myLog(type: Int, msg: String?) {
        val stackTrace = Thread.currentThread().stackTrace
        val index = 4
        val className = stackTrace[index].fileName
        var methodName = stackTrace[index].methodName
        val lineNumber = stackTrace[index].lineNumber
        methodName =
            methodName.substring(0, 1).uppercase(Locale.getDefault()) + methodName.substring(1)
        val stringBuilder = StringBuilder()
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#")
            .append(methodName).append(" ] ")
        stringBuilder.append(msg)
        val logStr = stringBuilder.toString()
        when (type) {
            VERBOSE -> Log.v(TAG, logStr)
            DEBUG -> Log.d(TAG, logStr)
            INFO -> Log.i(TAG, logStr)
            WARN -> Log.w(TAG, logStr)
            ERROR -> Log.e(TAG, logStr)
            else -> {}
        }
    }
}