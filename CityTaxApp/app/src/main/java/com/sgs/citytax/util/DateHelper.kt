package com.sgs.citytax.util

import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val DateTimeMillisecondFormat = "yyyy-MM-dd HH:mm:ss.SSS"
const val DateTimeTimeZoneMillisecondFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
const val DateTimeTimeSecondFormat = "yyyy-MM-dd HH:mm:ss"
const val displayDateTimeTimeSecondFormat = "dd-MM-yyyy HH:mm:ss"
const val parkingdisplayDateTimeTimeSecondFormat = "dd-MM-yyyy hh:mm a"
const val displayDateTimeTimeFormat = "dd-MM-yyyy HH:mm"
const val DateFormat = "yyyy-MM-dd"
const val displayDateFormat = "dd-MM-yyyy"
const val DateTimeSecondFormat = "yyyyMMdd_HHmmss"
const val DisplayDateMonthFormat = "dd-MMM"

fun formatDateTimeSecondFormat(date: Date): String {
    return SimpleDateFormat(DateTimeSecondFormat, Locale.getDefault()).format(date)
}

fun formatDateTimeInMillisecond(date: Date): String {
    return SimpleDateFormat(DateTimeMillisecondFormat, Locale.getDefault()).format(date)
}

fun formatCurrentDateTime(date: Date): String {
    return SimpleDateFormat(DateFormat, Locale.getDefault()).format(date)
}

fun formatDateTimeInMillisecond(dateString: String?): String {
    var str  = ""
    try {
        val date: Date? = SimpleDateFormat(DateTimeTimeZoneMillisecondFormat, Locale.getDefault()).parse(dateString)
        str = SimpleDateFormat(DateTimeTimeSecondFormat, Locale.getDefault()).format(date!!).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun formatDisplayDateTimeInMillisecond(dateString: String?): String {
    var str = ""
     try {
        val date: Date? = SimpleDateFormat(DateTimeTimeZoneMillisecondFormat, Locale.getDefault()).parse(dateString)
         str = SimpleDateFormat(displayDateTimeTimeSecondFormat, Locale.getDefault()).format(date!!).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}
fun formatDisplayDateTimeInMinutes(dateString: String?): String {
    var str = ""
    try {
        val date: Date? = SimpleDateFormat(DateTimeTimeZoneMillisecondFormat, Locale.getDefault()).parse(dateString)
        str = SimpleDateFormat(displayDateTimeTimeFormat, Locale.getDefault()).format(date!!).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun formatDateTime(date: Date): String {
    val formatter = SimpleDateFormat(DateTimeTimeSecondFormat, Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(date)
}

fun formatDisplayDateTime(date: Date): String {
    val formatter = SimpleDateFormat(displayDateTimeTimeSecondFormat, Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(date)
}

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat(DateFormat, Locale.getDefault())
    formatter.timeZone = TimeZone.getDefault()
    return formatter.format(date)
}

fun formatDate(dateString: String, from: String, to: String): String {
    var str = ""
    try {
        val date: Date? = SimpleDateFormat(from, Locale.getDefault()).parse(dateString)
        date?.let {
            str = formatDate(it, to)
        }
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun formatDate(date: Date, to: String): String {
    var str = ""
    try {
        str = SimpleDateFormat(to, Locale.getDefault()).format(date)
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}
// endregion

fun getDate(date: String, fromFormat: String, toFormat: String): String {
    var str = ""
    try {
        val formattedDate: Date? = SimpleDateFormat(fromFormat, Locale.getDefault()).parse(date)
        if (formattedDate == null)
            return str
        else {
            str = SimpleDateFormat(toFormat, Locale.getDefault()).format(formattedDate).toString()
        }
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun getDate(date: Date, toFormat: String): String {
    var str = ""
    try {
        str = SimpleDateFormat(toFormat, Locale.getDefault()).format(date).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun formatDate(dateString: String?): String {
    var str = ""
    try {
        val date: Date? = SimpleDateFormat(DateTimeTimeZoneMillisecondFormat, Locale.getDefault()).parse(dateString)
        str = SimpleDateFormat(DateFormat, Locale.getDefault()).format(date!!).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun displayFormatDate(date: Date): String {
    var str = ""
    try {
        str = SimpleDateFormat(displayDateFormat, Locale.getDefault()).format(date).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun displayFormatDate(dateString: String?): String {
    var str = ""
    try {
        val date: Date? = SimpleDateFormat(DateTimeTimeZoneMillisecondFormat, Locale.getDefault()).parse(dateString)
        str = SimpleDateFormat(displayDateFormat, Locale.getDefault()).format(date!!).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str

}

fun serverFormatDate(dateString: String?): String {
    var str = ""
    try {
        val date: Date? = SimpleDateFormat(displayDateFormat, Locale.getDefault()).parse(dateString)
        str = SimpleDateFormat(DateFormat, Locale.getDefault()).format(date!!).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun serverFormatDatewithTime(dateString: String?): String {
    var str = ""
    try {
        val date: Date? = SimpleDateFormat(DateTimeTimeZoneMillisecondFormat, Locale.getDefault()).parse(dateString)
        str = SimpleDateFormat(parkingdisplayDateTimeTimeSecondFormat, Locale.getDefault()).format(date!!).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun formatDates(dateString: String): Date {
    return try {
        val date: Date? = SimpleDateFormat(DateFormat, Locale.getDefault()).parse(dateString)
        date!!
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
        Date()
    }
}

fun parseDate(date: String, toFormat: String): Date {
    return SimpleDateFormat(toFormat, Locale.getDefault()).parse(date)!!
}

fun getTimeStampFromDate(dateString: String): Long {
    return try {
        val date: Date? = SimpleDateFormat(DateFormat, Locale.getDefault()).parse(dateString)
        date?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
        System.currentTimeMillis()
    }
}

fun addMoths(date: Date, months: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.MONTH, months)
    return cal.time
}

fun addDays(date: Date, days: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DATE, days)
    return cal.time
}

fun getDateDifference(dateString: String, startDateString: String, endDateString: String): Boolean {
    val date = parseDate(dateString, DateFormat)
    val startDate = parseDate(startDateString, DateFormat)
    val endDate = parseDate(endDateString, DateFormat)
    return ((date == startDate && date == endDate) || (date.after(startDate) && date.before(endDate))
            || (date == startDate && date.before(endDate)) || (date.after(startDate) && date == endDate))
}

fun getCurrentYearStartDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_YEAR, 1)
    return calendar.time
}

fun checkDatesInTodayRange(startDate: String, endDate: String): Boolean {
    val calendar = Calendar.getInstance()
    val todayDate = formatCurrentDateTime(calendar.time)
    val todayDateObject = parseDate(todayDate, DateFormat)
    val startDateObject = formatDates(startDate)
    val endDateObject = formatDates(endDate)

    val calendarToday = Calendar.getInstance()
    calendarToday.time = todayDateObject

    val calendarStartDate = Calendar.getInstance()
    calendarStartDate.time = startDateObject

    val calendarEndDate = Calendar.getInstance()
    calendarEndDate.time = endDateObject

    return (calendarToday.equals(calendarStartDate) || calendarToday.equals(calendarEndDate)
            || (calendarToday.after(calendarStartDate) && calendarToday.before(calendarEndDate)))
}

fun formatDisplayDateTimeInMillisecond(date: Date?): String {
    return SimpleDateFormat(displayDateTimeTimeSecondFormat, Locale.getDefault()).format(date!!).toString()
}

fun serverFormatDateTimeInMilliSecond(dateString: String?): String {
    return try {
        val date: Date? = SimpleDateFormat(displayDateTimeTimeSecondFormat, Locale.getDefault()).parse(dateString)
        SimpleDateFormat(DateTimeTimeSecondFormat, Locale.getDefault()).format(date).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
        ""
    }
}

fun monthsBetween(a: Date, b: Date?): Int {
    var b = b
    val cal = Calendar.getInstance()
    if (a.before(b)) {
        cal.time = a
    } else {
        cal.time = b
        b = a
    }
    var c = 0
    while (cal.time.before(b)) {
        cal.add(Calendar.MONTH, 1)
        c++
    }
    return c - 1
}

fun checkDatesLatest(startDate: String?, endDate: String?): String {
    //formatDisplayDateTimeInMillisecond
    if (TextUtils.isEmpty(startDate)) {
        if (TextUtils.isEmpty(endDate)) {
            return ""
        } else {
            return endDate.toString()
        }
    } else {
        if (TextUtils.isEmpty(endDate)) {
            return startDate.toString()
        }
    }

    val startDateObject = formatDates(formatDate(startDate))
    val endDateObject = formatDates(formatDate(endDate))

    val calendarStartDate = Calendar.getInstance()
    calendarStartDate.time = startDateObject

    val calendarEndDate = Calendar.getInstance()
    calendarEndDate.time = endDateObject

    if (calendarStartDate.after(calendarEndDate)) {
        return startDate.toString()
    } else {
        return endDate.toString()
    }

}

fun getTimeStamp(dateString: String, format: Constant.DateFormat): Long {
    var date : Long? = null
    val formatter = SimpleDateFormat(format.value, Locale.getDefault())
    try {
        val datef = formatter.parse(dateString)
        datef?.let {
            date = getTimeStamp(it)
        }
    } catch (e: ParseException) {
        LogHelper.writeLog(exception = e)
    }
    return date?: System.currentTimeMillis()
}

fun getTimeStamp(date: Date): Long {
    return date.time
}

fun formatDate(dateString: String, from: Constant.DateFormat, to: Constant.DateFormat): String {
    var str = ""
    try {
        val date: Date? = SimpleDateFormat(from.value, Locale.getDefault()).parse(dateString)
        date?.let {
            str = formatDate(it, to)
        }
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun formatDate(date: Date, to: Constant.DateFormat): String {
    var str = ""
    try {
        str = SimpleDateFormat(to.value, Locale.getDefault()).format(date)
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}

fun convertToDate(dateString: String, format: Constant.DateFormat): Date {
    var date : Date? = null
    try {
         date = SimpleDateFormat(format.value, Locale.getDefault()).parse(dateString)
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return date?: Calendar.getInstance().time
}

fun getDateTimeInMillisecond(date: String?): Date? {
    var returnDate: Date? = null
    try {
        date?.let {
            if (it.trim().isNotEmpty()) {
                returnDate =
                    SimpleDateFormat(DateTimeMillisecondFormat, Locale.getDefault()).parse(it)
            }
        }
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
        returnDate = null
    }
    return returnDate
}

fun isDayDifference(inputDate: Date, days: Int): Boolean {
    val currentDate = Date()
    val difference = abs(currentDate.time - inputDate.time)
    val dayDifference = difference / (24 * 60 * 60 * 1000)
    return dayDifference >= days
}

fun formatDisplayDateMonth(date: Date): String {
    return SimpleDateFormat(DisplayDateMonthFormat, Locale.getDefault()).format(date)
}

fun formatDisplayDateMonth(dateString: String?): String {
    var str = ""
    try {
        val date: Date? = SimpleDateFormat(DateTimeTimeZoneMillisecondFormat, Locale.getDefault()).parse(dateString)
        str = SimpleDateFormat(DisplayDateMonthFormat, Locale.getDefault()).format(date!!).toString()
    } catch (e: Exception) {
        LogHelper.writeLog(exception = e)
    }
    return str
}



