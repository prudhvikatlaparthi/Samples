package com.sgs.citytax.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.sgs.citytax.BuildConfig
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.NoticePrintFlag
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.model.ObjectHolder.documents
import com.sgs.citytax.ui.adapter.CustomDialogAdapter
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.*

fun getStringForPref(res: Int): String {
    return MyApplication.getContext().getString(res)
}

fun getString(res: Int): String {
    val context = MyApplication.getContext()
    val resources = context.resources
    val conf = resources.configuration
    val savedLocale = conf.locale
    if (MyApplication.getPrefHelper().language == "FR") {
        conf.locale = Locale.FRANCE
    } else {
        conf.locale = Locale.ENGLISH
    }
    resources.updateConfiguration(conf, null)
    return context.getString(res)
}

fun appGetString(res: Int): String {
    val context = MyApplication.getContext()
    val resources = context.resources
    val conf = resources.configuration
    val savedLocale = conf.locale
    if (MyApplication.getPrefHelper().language == "FR") {
        conf.locale = Locale.FRANCE
    } else {
        conf.locale = Locale.ENGLISH
    }
    conf.locale = Locale.ENGLISH
    resources.updateConfiguration(conf, null)
    return context.getString(res)
}

fun isValidEmail(target: CharSequence?): Boolean {
    return !TextUtils.isEmpty(target)
            && Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

private fun units(context: Context, index: Int): String {
    return arrayListOf(
            "", context.getString(R.string.txt_one), context.getString(R.string.txt_two), context.getString(R.string.txt_three), context.getString(R.string.txt_four),
            context.getString(R.string.txt_five), context.getString(R.string.txt_six), context.getString(R.string.txt_seven),
            context.getString(R.string.txt_eight), context.getString(R.string.txt_nine), context.getString(R.string.txt_ten), context.getString(R.string.txt_eleven),
            context.getString(R.string.txt_twelve), context.getString(R.string.txt_thirteen), context.getString(R.string.txt_fourteen),
            context.getString(R.string.txt_fifteen), context.getString(R.string.txt_sixteen), context.getString(R.string.txt_seventeen),
            context.getString(R.string.txt_eighteen), context.getString(R.string.txt_nineteen)
    )[index]
}


private fun tens(context: Context, index: Int): String {
    return arrayOf(
            "",
            "",
            context.getString(R.string.txt_twenty),
            context.getString(R.string.txt_thirty),
            context.getString(R.string.txt_forty),
            context.getString(R.string.txt_fifty),
            context.getString(R.string.txt_sixty),
            context.getString(R.string.txt_seventy),
            context.getString(R.string.txt_eighty),
            context.getString(R.string.txt_ninenty)
    )[index]
}

fun convertNumberToWord(n: Int, context: Context): String {
    if (n < 0) {
        return context.getString(R.string.txt_minus) + " " + convertNumberToWord(-n, context)
    }
    if (n < 20) {
        return units(context, n)
    }
    if (n < 100) {
        return tens(context, n / 10) + (if (n % 10 != 0) " " else "") + units(context, n % 10)
    }
    if (n < 1000) {
        if (MyApplication.getPrefHelper().language == "EN")
            return units(context, n / 100) + " " + context.getString(R.string.txt_hundred) + (if (n % 100 != 0) " " + context.getString(R.string.txt_and) + " " else "") + convertNumberToWord(n % 100, context)
        else
            return units(context, n / 100) + " " + context.getString(R.string.txt_hundred) + (if (n % 100 != 0) " " + /*context.getString(R.string.txt_and) +*/ " " else "") + convertNumberToWord(n % 100, context)
    }
    if (n < 1000000) {
        return convertNumberToWord(n / 1000, context) + " " + context.getString(R.string.txt_thousand) + (if (n % 1000 != 0) " " else "") + convertNumberToWord(n % 1000, context)
    }
    return if (n < 1000000000) {
        convertNumberToWord(n / 1000000, context) + " " + context.getString(R.string.txt_million) + (if (n % 1000000 != 0) " " else "") + convertNumberToWord(n % 1000000, context)
    } else convertNumberToWord(n / 1000000000, context) + " " + context.getString(R.string.txt_billion) + (if (n % 1000000000 != 0) " " else "") + convertNumberToWord(n % 1000000000, context)
}

fun getAmountInWordsWithCurrency(n: Double, textView: TextView) {
    // val activity = BaseActivity()
    // activity.showProgressDialog()
    APICall.convertAmountToText(n, object : ConnectionCallBack<String> {
        override fun onSuccess(response: String) {
            // activity.dismissDialog()
            textView.text = response
        }

        override fun onFailure(message: String) {
            //activity.dismissDialog()
        }
    })
}

fun getNoticePrintFlag(id: Int, btnPrint: Button, prodCode: String?= null) {
    val printFlag = NoticePrintFlag()
    printFlag.taxInvoiceId = if (id == 0) null else id
    printFlag.prodcode = prodCode
    APICall.getNoticePrintFlag(printFlag, object : ConnectionCallBack<Boolean> {
        override fun onSuccess(response: Boolean) {
            if (!response) {
                btnPrint.isClickable = false
                btnPrint.backgroundTintList =
                    ContextCompat.getColorStateList(btnPrint.context, R.color.colorGray)
                btnPrint.setTextColor(ContextCompat.getColor(btnPrint.context, R.color.white))
                MyApplication.getPrefHelper().isFromHistory = false
            }
        }

        override fun onFailure(message: String) {
        }
    })
}

fun getReceiptPrintFlag(id: Int, btnPrint: Button) {
    APICall.getReceiptPrintFlag(id, object : ConnectionCallBack<Boolean> {
        override fun onSuccess(response: Boolean) {
            if (!response)
            {
                btnPrint.isClickable = false
                btnPrint.backgroundTintList = ContextCompat.getColorStateList(btnPrint.context, R.color.colorGray)
                btnPrint.setTextColor(ContextCompat.getColor(btnPrint.context,R.color.white))
                MyApplication.getPrefHelper().isFromHistory = false
            }
        }
        override fun onFailure(message: String) {
        }
    })
}

fun formatNumber(value: Double?): String {
    val precision = if (MyApplication.getPrefHelper().currencyPrecision == 0) 2 else MyApplication.getPrefHelper().currencyPrecision
    return value?.let {
         String.format("%.${precision}f", it)
    } ?: String.format("%.${precision}f", 0.0)
}
fun getDecimalVal(text: String): Double {
    val regex: Pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)")
    val matcher: Matcher = regex.matcher(text)
    while (matcher.find()) {
        return matcher.group(1).toDouble()
    }
    return Double.MAX_VALUE
}

fun getTextWithPrecisionVal(it: String, doubleVal: Double): String {
    if (doubleVal == Double.MAX_VALUE){
        return it
    }
    return if (doubleVal >= 0.0) {
        val re = Regex("[^A-Za-z ]")
        var remarksText: String = re.replace(it, "")
        if (it.contains("F CFA"))
            remarksText = remarksText.replace("F CFA", "")
        remarksText.plus(" ") + formatWithPrecision(doubleVal.toString())
    } else
        it
}

fun formatWithPrecision(value: Double?, withCurrency: Boolean = true): String {
    val currency = if (withCurrency) MyApplication.getPrefHelper().currencySymbol else ""
    val locale = Locale("fr", "FR")
    val format = NumberFormat.getNumberInstance(locale) as DecimalFormat
    val pattern = if (MyApplication.getPrefHelper().currencyPrecision == 0) "###,##0.00" else {
        when (MyApplication.getPrefHelper().currencyPrecision) {
            1 -> "###,##0.00"
            2 -> "###,##0.00"
            3 -> "###,##0.000"
            4 -> "###,##0.0000"
            else -> "###,##0.00"
        }
    }
    format.applyPattern(pattern)
    value?.let {
        return if (MyApplication.getPrefHelper().isSymbolAtRight)
            "${format.format(value)} $currency"
        else
            "$currency ${format.format(value)}"
    }
    return "${format.format(0.0)} $currency"
}

fun formatWithPrecision(value: BigDecimal?, withCurrency: Boolean = true): String {
    val currency = if (withCurrency) MyApplication.getPrefHelper().currencySymbol else ""
    val locale = Locale("fr", "FR")
    val format = NumberFormat.getNumberInstance(locale) as DecimalFormat
    val pattern = if (MyApplication.getPrefHelper().currencyPrecision == 0) "###,##0.00" else {
        when (MyApplication.getPrefHelper().currencyPrecision) {
            1 -> "###,##0.00"
            2 -> "###,##0.00"
            3 -> "###,##0.000"
            4 -> "###,##0.0000"
            else -> "###,##0.00"
        }
    }
    format.applyPattern(pattern)
    value?.let {
        return if (MyApplication.getPrefHelper().isSymbolAtRight)
            "${format.format(value)} $currency"
        else
            "$currency ${format.format(value)}"
    }
    return "${format.format(0.0)} $currency"
}

fun currencyToDouble(textVal: String): Number? {
    val locale = Locale("fr", "FR")
    val format = NumberFormat.getNumberInstance(locale) as DecimalFormat
    if (textVal.isBlank()) {
        return format.parse("0")
    }
    return try {
        val removeCurrency: String = textVal.replace(MyApplication.getPrefHelper().currencySymbol, "")
        format.parse(removeCurrency.replace(" ", ""))
    }  catch (e: java.lang.Exception) {
        LogHelper.writeLog(exception = e)
        format.parse("0")
    }

}


/*fun formatWithPrecision(value: Double?, withCurrency: Boolean = true): String {
    //val currency = if (withCurrency) MyApplication.getPrefHelper().currencySymbol else ""
    val locale = Locale.getDefault()
    Currency.getInstance(locale)
    val numberFormat = NumberFormat.getCurrencyInstance(locale) as NumberFormat

    return numberFormat.format(value)
}*/

/*fun formatWithPrecision(value: BigDecimal?, withCurrency: Boolean = true): String {
    //val currency = if (withCurrency) MyApplication.getPrefHelper().currencySymbol else ""
    val locale = Locale.getDefault()
    Currency.getInstance(locale)
    val numberFormat = NumberFormat.getCurrencyInstance(locale) as NumberFormat

    return numberFormat.format(value)
}*/

fun formatWithPrecisionCustomDecimals(amount: String?, withCurrency: Boolean = true, customDecimalVal: Int = 0): String {
    return if (amount != null && amount != "null" && amount.isNotEmpty()) {
        if (getQuantity(amount).toIntOrNull() != null) {
            if(withCurrency){
                return formatWithPrecision(amount)
            }
            else {
                return getQuantity(amount)
            }
        } else {
            var value: Double? = amount.toDouble()
            val currency = if (withCurrency) MyApplication.getPrefHelper().currencySymbol else ""
            val locale = Locale("fr", "FR")
            val format = NumberFormat.getNumberInstance(locale) as DecimalFormat
            val pattern = if (customDecimalVal == 0) "###,##0.00" else {
                when (customDecimalVal) {
                    1 -> "###,##0.00"
                    2 -> "###,##0.00"
                    3 -> "###,##0.000"
                    4 -> "###,##0.0000"
                    else -> "###,##0.00"
                }
            }
            format.applyPattern(pattern)
            value?.let {
                return if (MyApplication.getPrefHelper().isSymbolAtRight)
                    "${format.format(value)} $currency"
                else
                    "$currency ${format.format(value)}"
            }
            "${format.format(0.0)} $currency"
        }
    } else formatWithPrecision(0.0, withCurrency)

}

fun formatWithPrecision(value: String?, withCurrency: Boolean = true): String {
    return if (value != null && value != "null" && value.isNotEmpty())
        formatWithPrecision(value.toDouble(), withCurrency)
    else formatWithPrecision(0.0, withCurrency)
}

fun removePrecisionMakeNumber(value: String, withCurrency: Boolean = true): String {
    val currency = if (withCurrency) MyApplication.getPrefHelper().currencySymbol else ""
    if (value.contains(currency)) {
        var tempValue = value.replace(currency, "").trim()
        tempValue = tempValue.split(",")[0]
        tempValue = tempValue.replace("[^0-9]".toRegex(), "")
        return tempValue
    }
    return value
}

fun getURL(receiptType: Constant.ReceiptType): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append(BuildConfig.RECEIPT_BASE_URL)
    stringBuilder.append("CustomerBill.aspx?")
    when (receiptType) {
        Constant.ReceiptType.TAX_RECEIPT -> {
            stringBuilder.append("AdvReceivedID=@documentNo@&SycID=@sycoTaxID@")
        }
        Constant.ReceiptType.TAX_NOTICE -> {
            stringBuilder.append("TaxInvoiceNo=@documentNo@&SycID=@sycoTaxID@&TaxNoticeNo=@TaxNoticeNo@&ViolationTicketId=@ViolationTicketId@")
        }
        Constant.ReceiptType.TAX_NOTICE_HISTORY -> {
            stringBuilder.append("TaxInvoiceNo=@documentNo@&SycID=@sycoTaxID@")
        }
        Constant.ReceiptType.BUSINESS_SUMMARY -> {
            stringBuilder.append("AccountID=@documentNo@&SycID=@sycoTaxID@")
        }
        Constant.ReceiptType.AGENT_RECHARGE, Constant.ReceiptType.LICENSE_RENEWAL  -> {
            stringBuilder.append("AdvReceivedID=@documentNo@")
        }
        Constant.ReceiptType.SALES  -> {
            stringBuilder.append("SalesOrderNo=@documentNo@")
        }
        Constant.ReceiptType.PENALTY_WAIVE_OFF -> {
            stringBuilder.append("WaiveOffID=@documentNo@")
        }
        Constant.ReceiptType.INITIAL_OUTSTANDING -> {
            stringBuilder.append("OutstandingWaveoffID=@documentNo@")
        }
        Constant.ReceiptType.ASSET_ASSIGNMENT -> {
            stringBuilder.append("AssetAssignmentID=@documentNo@&SycID=@sycoTaxID@")
        }
        Constant.ReceiptType.ASSET_RETURN -> {
            stringBuilder.append("AssetReturnID=@documentNo@&SycID=@sycoTaxID@")
        }
        Constant.ReceiptType.IMPOUND_RETURN -> {
            stringBuilder.append("ReturnTaxInvoiceNo=@documentNo@&ReturnLineID=@sycoTaxID@")
        }
        Constant.ReceiptType.BOOKING_REQUEST -> {
            stringBuilder.append("BookingRequestID=@documentNo@&SycID=@sycoTaxID@")
        }
        Constant.ReceiptType.SERVICE_BOOKING -> {
            stringBuilder.append("ServiceRequestNo=@documentNo@&SycID=@sycoTaxID@&BookingRequestID=@BookingRequestID@")
        }
        Constant.ReceiptType.BOOKING_ADVANCE -> {
            stringBuilder.append("AdvReceivedID=@documentNo@&SycID=@sycoTaxID@&BookingRequestID=@BookingRequestID@")
        }
        Constant.ReceiptType.INDIVIDUAL_TAX_SUMMARY -> {
            stringBuilder.append("TaxesID=@documentNo@&TaxSycotaxID=@sycoTaxID@&SycID=@taxSycoTaxID@")
        }
        Constant.ReceiptType.PROPERTY_SUMMARY -> {
            stringBuilder.append("PropertySycotaxID=@sycoTaxID@")
        }
        Constant.ReceiptType.PROPERTY_VERIFICATION -> {
            stringBuilder.append("ApprovedPropertyVerificationID=@documentNo@")
        }
        else -> {

        }
    }
    stringBuilder.append("&LngCode=@LanguageCode@")
    return stringBuilder.toString()
}


@Throws(WriterException::class)
fun createCode(str: String?, type: BarcodeFormat?): Bitmap? {
    val mHashtable = Hashtable<EncodeHintType, String?>()
    mHashtable[EncodeHintType.CHARACTER_SET] = "UTF-8"
    val matrix = MultiFormatWriter().encode(str, type, 256, 256, mHashtable)
    val width = matrix.width
    val height = matrix.height
    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        for (x in 0 until width) {
            if (matrix[x, y]) {
                pixels[y * width + x] = -0x1000000
            } else {
                pixels[y * width + x] = -0x1
            }
        }
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}

fun bindQRCode(receiptType: Constant.ReceiptType, documentID: String, sycoTaxID: String? = "", taxNoticeNo: String? = "", violationTicketId: String? = "", bookingRequestId: String? = ""): Bitmap? {
    var url: String? = ""
    url = getURL(receiptType)
    val qrCodeBitmap: Bitmap? = createCode(url.replace("@documentNo@", documentID).replace("@sycoTaxID@", sycoTaxID
            ?: "").replace("@TaxNoticeNo@", taxNoticeNo
            ?: "").replace("@ViolationTicketId@", violationTicketId ?: "")
            .replace("@BookingRequestID@", bookingRequestId ?: "")
            .replace("@LanguageCode@", MyApplication.getPrefHelper().language), BarcodeFormat.QR_CODE)
    val stream = ByteArrayOutputStream()
    qrCodeBitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)

    return qrCodeBitmap
}


fun getRoundValue(amount: BigDecimal, roundingPlace: Int): BigDecimal {
    var roundingPlace: Int = roundingPlace
    var roundValue = BigDecimal.ZERO
    try {
        val roundingValue: Double
        val powerValue: Double
        if (roundingPlace < 0) {
            roundingPlace = 0 - roundingPlace
            roundingValue = Math.round(amount.divide(BigDecimal(Math.pow(10.0, roundingPlace.toDouble())), 10, RoundingMode.HALF_EVEN).toDouble()).toDouble()
            powerValue = 10.0.pow(roundingPlace.toDouble())
            roundValue = BigDecimal(roundingValue * powerValue)
        } else {
            roundValue = amount.setScale(roundingPlace, BigDecimal.ROUND_HALF_UP)
        }
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return roundValue

}

fun String.appendCurrencyCode(): String = "$this ${MyApplication.getPrefHelper().currencySymbol}"

fun Double.appendCurrencyCode(): String = "$this ${MyApplication.getPrefHelper().currencySymbol}"

fun BigDecimal.appendCurrencyCode(): String = "$this ${MyApplication.getPrefHelper().currencySymbol}"

fun getNumberPattern(): Pattern = Pattern.compile("-?\\d+(\\.\\d+)?")

fun isNumber(strNumber: String?): Boolean {
    if (strNumber == null)
        return false
    return getNumberPattern().matcher(strNumber).matches()
}

fun truncateTo(amount: BigDecimal, scale: Int = MyApplication.getPrefHelper().currencyPrecision): BigDecimal {
    val str = amount.toString()
    return if (str.contains(".") && str.substring(str.indexOf(".")).length - 1 > scale)
        BigDecimal(str.substring(0, str.indexOf(".") + scale + 1))
    else amount
}

fun truncateTo(amount: Double, scale: Int = MyApplication.getPrefHelper().currencyPrecision): Double {
    val str = amount.toString()
    return if (str.contains(".") && str.substring(str.indexOf(".")).length - 1 > scale) {
        val tempAmount = str.substring(0, str.indexOf(".") + scale + 1)
        return tempAmount.toDouble()
    } else amount
}

fun getTariffWithCurrency(rate: String?): String {
    var result = ""
    if (rate != null) {
        if (rate.contains("/")) {
            val arr = rate.split("/")
            result = formatWithPrecision(arr[0])

            for ((i, value) in arr.withIndex()) {
                if (i != 0) {
                    result += "/"
                    result += value
                }
            }
        } else {
            result = formatWithPrecision(rate)
        }
        return result
    }
    return ""
}

fun getTariffWithPercentage(rate: String?): String {
    var result = ""
    try {
        if (rate != null) {
            if (rate.contains("%")) {
                rate.replace("%", "")
                val symbols = DecimalFormatSymbols(Locale.US)
                val df = DecimalFormat("#.##", symbols)
                // df = DecimalFormat("#.##")
                result = (df.format(df.parse(rate))).toString() + "%"
            } else {
                result = rate
            }
            return result
        }
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
        return ""
    }
    return ""
}
fun splitData(sss:String):String{
    val inputString0 = sss
    return inputString0?.replace(';', '\n') ?: ""
}
fun getQuantity(qty: String?): String {
    var result = ""
    try {
        if (qty != null) {
            if (qty.contains(".")) {
                qty.replace(".", "")
                val symbols = DecimalFormatSymbols(Locale.US)
                val df = DecimalFormat("#.#", symbols)
                //val df = DecimalFormat("#.#")
                result = (df.format(df.parse(qty))).toString()
            } else {
                result = qty
            }
            return result
        }
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
        return ""
    }
    return ""
}
fun getDoubleAmountFromValue(qty: String?): String {
    var result = "0.0"
    try {
        if (qty != null) {
            if (qty.contains(".")) {
                val symbols = DecimalFormatSymbols(Locale.US)
                val df = DecimalFormat("#.#", symbols)
                //val df = DecimalFormat("#.#")
                result = (df.format(df.parse(qty))).toString()
            } else if (qty.contains(",")) {
                val symbols = DecimalFormatSymbols(Locale.FRANCE)
                val df = DecimalFormat("#.#", symbols)
                val usSymbols = DecimalFormatSymbols(Locale.US)
                val usDf = DecimalFormat("#.#", usSymbols)
                result = (usDf.format(df.parse(qty))).toString()
            } else if (qty.contains(MyApplication.getPrefHelper().currencySymbol)) {
                result = qty.replace(MyApplication.getPrefHelper().currencySymbol, "").trim()
            } else {
                result = qty
            }
            return result
        }
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
        return result
    }
    return result
}
fun loadBitmapFromView(v: View): Bitmap? {
    val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.layout(v.left, v.top, v.right, v.bottom)
    v.draw(c)
    return b
}

fun resize(image: Bitmap?, maxWidth: Int = 384): Bitmap? {
    var image = image


    /*  val display: Display = windowManager.defaultDisplay

      val point = Point()
      display.getSize(point)
      maxHeight = point.y
*/
    if (image != null) {
        val maxHeight = image.height
        return if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            image
        } else {
            image
        }
    }
    return null
}


fun getStringTv(textview: TextView): String {
    if (!TextUtils.isEmpty(textview.text)) {
        return textview.text.toString().trim()
    }
    return ""
}

fun getIntTv(textview: TextView): Int? {
    if (!TextUtils.isEmpty(textview.text) && textview.text != null && textview.text != "null") {
        return (textview.text.toString().trim()).toInt()
    }
    return null
}

fun getDoubleTv(textview: TextView): Double {
    if (!TextUtils.isEmpty(textview.text)) {
        return (textview.text.toString().trim()).toDouble()
    }
    return 0.0
}

fun getStringSpn(spinner: AppCompatSpinner): String {
    return spinner.selectedItem.toString()
}

fun setCalendarText(month: Int, date: Int): Date {
    val calender = Calendar.getInstance(Locale.getDefault())
    calender.set(calender.get(Calendar.YEAR), month, date)
    return calender.time
}

fun showSearchAlertDialog(context: Context, mainArray: ArrayList<Any>?, onClickListener: CustomDialogAdapter.Listener) {
    val dialog = AlertDialog.Builder(context, R.style.AlertDialogTheme).create()
    val view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_with_search, null)
    val lv = view.findViewById(R.id.list_view) as RecyclerView
    val inputSearch = view.findViewById(R.id.inputSearch) as EditText
    dialog.setView(view)
    val adapter = CustomDialogAdapter(dialog, onClickListener)

    inputSearch.addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }

        override fun afterTextChanged(arg0: Editable) {
            val word = arg0.toString()
            if (!TextUtils.isEmpty(word) && word.length > 0) {
                val temp: ArrayList<Any>? = arrayListOf()
                try {
                    for ((index, obj) in mainArray!!.withIndex()) {
                        if (!TextUtils.isEmpty(obj.toString()) && obj.toString().contains(word)) {
                            temp?.add(obj)
                        }
                    }
                } catch (e: Exception) {
                    LogHelper.writeLog(exception = e)
                }
                adapter.doClearAndUpdateList(temp)
            } else {
                adapter.doClearAndUpdateList(mainArray as ArrayList<Any>)
            }
        }
    })
    lv.adapter = adapter
    adapter.doClearAndUpdateList(mainArray as ArrayList<Any>)
    dialog.show()
}

fun writeTextOnDrawable(drawableId: Int, text: String, textColor: Int): Bitmap {
    val resources = MyApplication.getContext().resources
    val bm: Bitmap = Bitmap.createBitmap(resources.getDrawable(drawableId, MyApplication.getContext()?.theme).intrinsicWidth,
            resources.getDrawable(drawableId, MyApplication.getContext()?.theme).intrinsicHeight, Bitmap.Config.ARGB_8888)
    val paint = Paint()
    paint.style = Paint.Style.FILL
    paint.color = textColor
    //paint.setTypeface(font)
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = convertToPixels(MyApplication.getContext(), 15)
    val textRect = Rect()
    paint.getTextBounds(text, 0, text.length, textRect)
    val canvas = Canvas(bm)
    // If the text is bigger than the canvas , reduce the font size
//        if (textRect.width() >= canvas.width - 4) // the padding on
//            paint.textSize = convertToPixels(requireContext(), 7) // Scaling needs to be
    val xPos = canvas.width / 2 - 3 // -2 is for regulating the x
    val yPos = (canvas.height / 2 - (paint.descent() + paint
            .ascent()) / 2)
    canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), paint)
    return bm
}

fun convertToPixels(context: Context, nDP: Int): Float {
    val conversionScale = context.resources
            .displayMetrics.density
    return (nDP * conversionScale + 0.5f).toFloat()
}


fun area(arr: ArrayList<LatLng>): Double {
    var area = 0.0
    val nPts: Int = arr.size
    var j = nPts - 1
    var p1: LatLng
    var p2: LatLng
    var i = 0
    while (i < nPts) {
        p1 = arr[i]
        p2 = arr[j]
        area += p1.latitude * p2.longitude
        area -= p1.longitude * p2.latitude
        j = i++
    }
    area /= 2.0
    return area
}

//fun Centroid(pts: ArrayList<LatLng>): LatLng? {
//    val nPts: Int = pts.size
//    var x = 0.0
//    var y = 0.0
//    var f: Double
//    var j = nPts - 1
//    var p1: LatLng
//    var p2: LatLng
//    var i = 0
//    while (i < nPts) {
//        p1 = pts[i]
//        p2 = pts[j]
//        f = p1.latitude * p2.longitude - p2.latitude * p1.longitude
//        x += (p1.latitude + p2.latitude) * f
//        y += (p1.longitude + p2.longitude) * f
//        j = i++
//    }
//    f = area(pts) * 6
//    return LatLng(x / f, y / f)
//}

fun Centroid(geoCoordinates: List<LatLng>): LatLng? {
    if (geoCoordinates.size === 1) {
        return geoCoordinates[0]
    }
    var x = 0.0
    var y = 0.0
    var z = 0.0
    for (geoCoordinate in geoCoordinates) {
        val latitude = geoCoordinate.latitude * Math.PI / 180
        val longitude = geoCoordinate.longitude * Math.PI / 180
        x += cos(latitude) * cos(longitude)
        y += cos(latitude) * sin(longitude)
        z += sin(latitude)
    }
    val total: Int = geoCoordinates.size
    x /= total
    y /= total
    z /= total
    val centralLongitude = atan2(y, x)
    val centralSquareRoot = sqrt(x * x + y * y)
    val centralLatitude = atan2(z, centralSquareRoot)
    return LatLng(centralLatitude * 180 / Math.PI, centralLongitude * 180 / Math.PI)
}

fun distanceCalculateInMeters(p: Location, q: Location): Double {
    /****
     * written this custom fun for calculate distance as to
     * match the meters accurately with BO
     */
    var c = Math.PI / 180
    // Google (gives randomly wrong results in Android!)
    //return google.maps.geometry.spherical.computeDistanceBetween(p,q);
    // Chord
    //return 9019995.5222 * Math.sqrt((1-Math.cos(c*(p.lat()-q.lat())))
    //   + (1-Math.cos(c*(p.lng()-q.lng()))) * Math.cos(c*p.lat()) * Math.cos(c*q.lat()));
    // Taylor for chord
    /*return 111318.845 * sqrt((p.latitude - q.latitude).pow(2.0)
            + (p.longitude - q.longitude).pow(2.0) * cos(c*p.latitude) * cos(c*q.longitude))*/

    //return 2 * asin(sqrt(sin((p - q) / 2).pow(2) + cos(p) * cos(q) * sin((d - f) / 2).pow(2)))

    return 9019995.5222 * Math.sqrt((1 - Math.cos(c * (p.latitude - q.latitude)))
            + (1 - Math.cos(c * (p.longitude - q.longitude))) * Math.cos(c * p.latitude) * Math.cos(c * q.latitude))
}

fun bitmapDescriptorFromVector(context: Context, color: String?): BitmapDescriptor? {
    var icon: Int = R.drawable.ic_map_marker_brown
    if (color != null && color.isNotEmpty()) {
        when (color.toUpperCase(Locale.getDefault())) {
            "#A52A2A" -> {
                icon = R.drawable.ic_map_marker_brown
            }
            "#FF8C00" -> {
                icon = R.drawable.ic_map_marker_orange
            }
            "#C8C8C8" -> {
                icon = R.drawable.ic_map_marker_grey
            }
            "#FF1A1A" -> {
                icon = R.drawable.ic_map_marker_red
            }
            "#00FF00" -> {
                icon = R.drawable.ic_map_marker_green
            }
            "#95B9C7" -> {
                icon = R.drawable.ic_map_marker_baby_blue
            }
            "#2B65EC" -> {
                icon = R.drawable.ic_map_marker_ocean_blue
            }
            "#F1C40F" -> {
                icon = R.drawable.ic_map_marker_violation
            }
            "#8E44AD" -> {
                icon = R.drawable.ic_map_marker_impound
            }
        }
    }
    val vectorDrawable = ContextCompat.getDrawable(context, icon)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/**
 * @param mContext      Context
 * @param setCancelable dialog cancelable or not
 * @param resource      custom layout resourceId
 *
 *
 * Example: mContext, R.layout.alert_custom_view, new Utils.CustomAlertInterface()
 */
fun showCustomAlertDialog(mContext: Context, resource: Int, setCancelable: Boolean, customAlertInterface: CustomAlertInterface) {
    val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(mContext, R.style.myDialog)
    val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val alertView: View = inflater.inflate(resource, null)
    builder.setView(alertView)
    val alertDialog: android.app.AlertDialog = builder.create()
    alertDialog.setCancelable(setCancelable)
    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    customAlertInterface.setListenerCustomAlert(alertView, alertDialog)
}
interface CustomAlertInterface {
    fun setListenerCustomAlert(alertView: View, alertDialog: android.app.AlertDialog)
}

fun Context.prepareCustomListDialog(resource: Int, setCancelable: Boolean): Dialog {
    val dialog = Dialog(this, R.style.myDialog)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(setCancelable)
    val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
    val width = (resources.displayMetrics.widthPixels * 0.96).toInt()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(resource)
    dialog.window?.setLayout(width, height)
    return dialog
}

fun Fragment.hideKeyboard() {
    requireView().let { requireActivity().hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.pxToDp(px: Int): Int {
    val metrics = resources.displayMetrics
    return (px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun Context.dpToPx(dp: Int): Int {
    val metrics = resources.displayMetrics
    return (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun EditText.setInputType(allowFraction: Boolean = false) {
    if (allowFraction) {
        this.keyListener = DigitsKeyListener.getInstance("0123456789.")
        this.filters =
            arrayOf(DecimalInputFilter(2), InputFilter.LengthFilter(12))
    } else {
        this.keyListener = DigitsKeyListener.getInstance("0123456789")
        this.filters =
            arrayOf(DecimalInputFilter(0), InputFilter.LengthFilter(8))
    }
}

fun checkVerified(){
    documents.forEach {
        it.verified= "N"
    }
}

fun checkRemarks(){
    documents.forEach {
        it.remarks = ""
    }
}

fun printPayload(src: Any?) {
    val payload = Gson().toJson(src)
    LogHelper.writeLog(exception = null, message = payload)
}