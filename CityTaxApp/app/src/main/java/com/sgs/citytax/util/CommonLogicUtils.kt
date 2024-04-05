package com.sgs.citytax.util

import android.content.Context
import androidx.core.view.isVisible
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetQrNoteAndLogoPayload
import com.sgs.citytax.api.response.OrgData
import com.sgs.citytax.databinding.LayoutQrCodeLabelBinding
import java.math.BigDecimal
import java.util.*

object CommonLogicUtils {
    private fun List<OrgData>?.getQRCodeNotes(): Pair<String?, String?> {
        val orgData = this?.getOrNull(0)
        val qr1 = orgData?.qRCodeNote?.trim()
        val qr2 = orgData?.qRCodeNote2?.trim()
        return Pair(qr1, qr2)
    }

    fun checkNUpdateQRCodeNotes(
        qrCodeWrapper: LayoutQrCodeLabelBinding,
        orgDataList: List<OrgData>? = null
    ) {
        if (orgDataList != null) {
            updateQRCodeNotes(qrCodeWrapper, orgDataList)
        } else {
            val payload = GetQrNoteAndLogoPayload()
            APICall.getQrNoteAndLogo(payload, object : ConnectionCallBack<List<OrgData>> {
                override fun onSuccess(response: List<OrgData>) {
                    updateQRCodeNotes(qrCodeWrapper, response)
                }

                override fun onFailure(message: String) {

                }
            })
        }
    }

    private fun updateQRCodeNotes(
        qrCodeWrapper: LayoutQrCodeLabelBinding,
        orgDataList: List<OrgData>?
    ) {
        val qrPair = orgDataList.getQRCodeNotes()
        qrCodeWrapper.tvQRLabel.isVisible = qrPair.first?.let {
            qrCodeWrapper.tvQRLabel.text = it
            true
        } ?: false
        qrCodeWrapper.tvQRLabel2.isVisible = qrPair.second?.let {
            qrCodeWrapper.tvQRLabel2.text = it
            true
        } ?: false
    }

    private fun Context.retrieveString(name: String): String {
        return try {
            getString(resources.getIdentifier(name, "string", packageName))
        } catch (e: Exception) {
            name.replace("_", "")
        }
    }

    fun Context.convertObjectToMap(obj: Any, ignoreZeroItems :Boolean = false): TreeMap<Int, Pair<String, Any>> {
        val myObjectAsDict = TreeMap<Int, Pair<String, Any>>()
        try {
            val allFields = obj.javaClass.declaredFields
            for (field in allFields) {
                field.isAccessible = true
                val value = field.get(obj)
                if (value != null && !field.name.endsWith("_")) {
                    val seq = field.getAnnotation(OrderSequence::class.java)?.value
                        ?: (Random().nextInt(Int.MAX_VALUE - 1000) + 1000)
                    var name = this.retrieveString(field.name).trim()
                    name = if (name.contains(":")) name.replace(":", "") else name
                    when (field.type.canonicalName) {
                        "java.util.Date" -> {
                            myObjectAsDict[seq] = Pair(name, formatDisplayDateTime(value as Date))
                        }
                        "java.lang.Double" -> {
                            val dbl = value as Double
                            if (dbl > 0) {
                                myObjectAsDict[seq] = Pair(name, formatWithPrecision(dbl))
                            }
                        }
                        "java.math.BigDecimal" -> {
                            val dbl = value as BigDecimal
                            if (ignoreZeroItems) {
                                if (dbl.compareTo(BigDecimal.ZERO) != 0) {
                                    myObjectAsDict[seq] = Pair(name, formatWithPrecision(dbl))
                                }
                            } else {
                                myObjectAsDict[seq] = Pair(name, formatWithPrecision(dbl))
                            }
                        }
                        else -> {
                            if (field.name.contains("date")) {
                                val dateString = value as String
                                if (dateString.contains("T00:00:00.000")){
                                    myObjectAsDict[seq] =
                                        Pair(name, displayFormatDate(dateString))
                                }else{
                                    myObjectAsDict[seq] =
                                        Pair(name, formatDisplayDateTimeInMinutes(dateString))
                                }
                            } else {
                                myObjectAsDict[seq] = Pair(name, value)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
        return myObjectAsDict
    }
}