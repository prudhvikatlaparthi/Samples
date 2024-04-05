package com.sgs.citytax.util

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val key = "EffiaSoft@2012JY"

fun doEncrypt(data: String, key: String): String {
    var encryptedData = ""
    try {
        if (key.isEmpty()) {
            return data
        }
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val spec = SecretKeySpec(key.toByteArray(), "AES")
        val iv = byteArrayOf(74, 53, 53, 88, 74, 59, 120, 23, 33, 29, 95, 25, 60, 116, 93, 62)
        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, spec, ivSpec)
        val encryptedText = cipher.doFinal(data.toByteArray(charset("UTF-8")))
        encryptedData = Base64.encodeToString(encryptedText, Base64.DEFAULT)
    } catch (e: NoSuchPaddingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: IllegalBlockSizeException) {
        LogHelper.writeLog(exception = e)
    } catch (e: InvalidKeyException) {
        LogHelper.writeLog(exception = e)
    } catch (e: BadPaddingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: UnsupportedEncodingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: InvalidAlgorithmParameterException) {
        LogHelper.writeLog(exception = e)
    }
    return encryptedData.trim()
}

fun doDecrypt(data: String, key: String): String {
    var decryptedData = ""
    try {
        if (key.isEmpty()) {
            return data
        }
        val bytStr = Base64.decode(data, Base64.DEFAULT)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val iv = byteArrayOf(74, 53, 53, 88, 74, 59, 120, 23, 33, 29, 95, 25, 60, 116, 93, 62)
        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        decryptedData = String(cipher.doFinal(bytStr), charset("UTF-8"))
    } catch (e: NoSuchAlgorithmException) {
        LogHelper.writeLog(exception = e)
    } catch (e: NoSuchPaddingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: IllegalBlockSizeException) {
        LogHelper.writeLog(exception = e)
    } catch (e: InvalidKeyException) {
        LogHelper.writeLog(exception = e)
    } catch (e: UnsupportedEncodingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: BadPaddingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: InvalidAlgorithmParameterException) {
        LogHelper.writeLog(exception = e)
    }
    return decryptedData.trim()
}

fun doEncrypt(data: String): String {
    var encryptedData = ""
    try {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedText = cipher.doFinal(data.toByteArray(charset("UTF-8")))
        encryptedData = Base64.encodeToString(encryptedText, Base64.DEFAULT)
    } catch (e: NoSuchAlgorithmException) {
        LogHelper.writeLog(exception = e)
    } catch (e: NoSuchPaddingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: IllegalBlockSizeException) {
        LogHelper.writeLog(exception = e)
    } catch (e: InvalidKeyException) {
        LogHelper.writeLog(exception = e)
    } catch (e: BadPaddingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: UnsupportedEncodingException) {
        LogHelper.writeLog(exception = e)
    }
    return encryptedData.trim { it <= ' ' }
}

fun doDecrypt(data: String?): String {
    var decryptedData = ""
    try {
        val bytStr = Base64.decode(data, Base64.DEFAULT)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        decryptedData = String(cipher.doFinal(bytStr), charset("UTF-8"))
    } catch (e: NoSuchAlgorithmException) {
        LogHelper.writeLog(exception = e)
    } catch (e: NoSuchPaddingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: IllegalBlockSizeException) {
        LogHelper.writeLog(exception = e)
    } catch (e: InvalidKeyException) {
        LogHelper.writeLog(exception = e)
    } catch (e: UnsupportedEncodingException) {
        LogHelper.writeLog(exception = e)
    } catch (e: BadPaddingException) {
        LogHelper.writeLog(exception = e)
    }
    return decryptedData.trim { it <= ' ' }
}
