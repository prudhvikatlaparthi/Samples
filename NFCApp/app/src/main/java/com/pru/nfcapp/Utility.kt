package com.pru.nfcapp

import android.os.Bundle
import android.text.TextUtils
import java.util.Collections
import java.util.Locale

object Utility {

    fun bundle2String(bundle: Bundle?, order: Int = 1): String {
        if (bundle == null || bundle.keySet().isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        val list: List<String> = ArrayList(bundle.keySet())
        if (order == 1) {
            Collections.sort(list) { obj: String, anotherString: String? ->
                obj.compareTo(
                    anotherString!!
                )
            }
        } else if (order == 2) {
            Collections.sort(list, Collections.reverseOrder())
        }
        for (key in list) {
            sb.append(key.uppercase())
            sb.append(": ")
            val value = bundle.get(key)
            sb.append(value)
            sb.append("\n")
        }
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }

    fun formatStr(format: String?, vararg params: Any?): String {
        return String.format(Locale.ENGLISH, format!!, *params)
    }

    fun hexStr2Bytes(hexStr: String): ByteArray {
        if (TextUtils.isEmpty(hexStr)) {
            return ByteArray(0)
        }
        val length = hexStr.length / 2
        val chars = hexStr.toCharArray()
        val b = ByteArray(length)
        for (i in 0 until length) {
            b[i] = (char2Byte(chars[i * 2]) shl 4 or char2Byte(
                chars[i * 2 + 1]
            )).toByte()
        }
        return b
    }

    fun bytes2HexStr(bytes: ByteArray?): String {
        return if (bytes == null || bytes.isEmpty()) {
            ""
        } else bytes2HexStr(bytes, 0, bytes.size)
    }

    private fun bytes2HexStr(src: ByteArray?, offset: Int, len: Int): String {
        val end = offset + len
        if (src == null || src.isEmpty() || offset < 0 || len < 0 || end > src.size) {
            return ""
        }
        val buffer = ByteArray(len * 2)
        var h = 0
        var l = 0
        var i = offset
        var j = 0
        while (i < end) {
            h = src[i].toInt() shr 4 and 0x0f
            l = src[i].toInt() and 0x0f
            buffer[j++] = (if (h > 9) h - 10 + 'A'.code else h + '0'.code).toByte()
            buffer[j++] = (if (l > 9) l - 10 + 'A'.code else l + '0'.code).toByte()
            i++
        }
        return String(buffer)
    }

    private fun char2Byte(c: Char): Int {
        if (c >= 'a') {
            return c.code - 'a'.code + 10 and 0x0f
        }
        return if (c >= 'A') {
            c.code - 'A'.code + 10 and 0x0f
        } else c.code - '0'.code and 0x0f
    }

    fun String.decodeHex(): String {
        require(length % 2 == 0) {"Must have an even length"}
        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
            .toString(Charsets.ISO_8859_1)  // Or whichever encoding your input uses
    }
}