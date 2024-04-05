package com.sgs.citytax.ui.custom

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Context
import android.text.TextUtils
import android.text.format.DateFormat.is24HourFormat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.sgs.citytax.R
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.util.DateTimeTimeSecondFormat
import com.sgs.citytax.util.LogHelper
import com.sgs.citytax.util.parkingdisplayDateTimeTimeSecondFormat
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DatePickerEditText : TextInputEditText, View.OnClickListener, OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var _context: Context
    private lateinit var calendar: Calendar
    private var stringFormat: String = DateTimeTimeSecondFormat
    private var needToShowClear = false
    private var needToShowCalenderIcon = false
    private var needToShowTimePicker = false
    private var mMaxDate: Long = 0L
    private var mMinDate: Long = 0L
    private var needDateTime = false
    private var selectedTime: Long = 0L
    private var hour: Int = 0
    private var minute: Int = 0
    private var myHour: Int = 0
    private var myMinute: Int = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        _context = context
        this.isFocusable = false
        setOnClickListener(this)
        calendar = Calendar.getInstance(TimeZone.getDefault())
        updateAttributes(context, attrs)
    }

    private fun updateAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DatePickerEditText)
        val needIcons = typedArray.getInteger(R.styleable.DatePickerEditText_needIcons, 0)
        if (needIcons == 0) {
            this.needToShowClear = false
            this.needToShowCalenderIcon = false
        } else if (needIcons == 1) {
            this.needToShowClear = true
            this.needToShowCalenderIcon = true
        }
        val timeTypedArray = context.obtainStyledAttributes(attrs, R.styleable.TimePickerEditText)
        val needTimePicker =
            timeTypedArray.getInteger(R.styleable.TimePickerEditText_needTimePicker, 0)
        if (needTimePicker == 0) {
            this.needToShowTimePicker = false
        } else if (needTimePicker == 1) {
            this.needToShowTimePicker = true
        }
        updateDrawablesIcons()
        setText("")
        isLongClickable=false
        setTextIsSelectable(false)
        typedArray.recycle()
        timeTypedArray.recycle()
    }

    fun showIcons(show: Boolean) {
        this.needToShowClear = show
        this.needToShowCalenderIcon = show
        updateDrawablesIcons()
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (needToShowClear) {
            needToShowClear = !TextUtils.isEmpty(text)
            updateDrawablesIcons()
            needToShowClear = true
        }
    }

    private fun updateDrawablesIcons() {
        setCompoundDrawablesWithIntrinsicBounds(
            if (needToShowCalenderIcon) ContextCompat.getDrawable(
                context,
                R.drawable.ic_calender
            ) else null,
            null,
            if (needToShowClear) ContextCompat.getDrawable(context, R.drawable.ic_clear) else null,
            null
        )
        if (needToShowClear)
            setOnTouchListener(object : OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    val DRAWABLE_RIGHT = 2
                    if (event != null && compoundDrawables[DRAWABLE_RIGHT] != null && event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= (right - compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                            setText("")
                            return true
                        }
                    }
                    return false
                }
            })
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DATE, dayOfMonth)
        selectedTime = calendar.timeInMillis
        hour = calendar[Calendar.HOUR]
        minute = calendar[Calendar.MINUTE]
        updateDisplay()
    }

    fun setDisplayDateFormat(format: String) {
        this.stringFormat = format
    }


    override fun onClick(v: View) {
        setLocale(Locale(MyApplication.getPrefHelper().language))
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        if (selectedTime != 0L)
            calendar.timeInMillis = selectedTime
        if (text != null && text?.toString() != null && text.toString() != "")
            calendar.timeInMillis = getTimeStampFromDate(text.toString())
        val dialog = DatePickerDialog(
            _context, this,
            calendar[Calendar.YEAR], calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        if (mMaxDate != 0L)
            dialog.datePicker.maxDate = mMaxDate
        if (mMinDate != 0L)
            dialog.datePicker.minDate = mMinDate
        dialog.show()
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).text =
            resources.getString(R.string.cancel)
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).text = resources.getString(R.string.ok)
    }

    private fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        context.createConfigurationContext(configuration)
    }

    private fun updateDisplay() {
        try {
            if (!needToShowTimePicker) {
                setText(formatDate(stringFormat, calendar.time))
            }
            if (needDateTime && !needToShowTimePicker) {
                val timeDialog = TimePickerDialog(
                    _context, this,
                    calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE],
                    true
                )
                timeDialog.show()
                updateDateTimeDisplay()
            } else if (needToShowTimePicker) {
                val calendar = Calendar.getInstance(TimeZone.getDefault())
                val timePickerDialog = TimePickerDialog(
                    _context, this,
                    calendar[Calendar.HOUR_OF_DAY],
                    calendar[Calendar.MINUTE],
                    false
                )
                timePickerDialog.show()
            }
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
    }

    private fun updateDateTimeDisplay() {
        try {
            setText(formatDateTime(stringFormat, calendar.time))
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
        setLocale(Locale.getDefault())
    }

    private fun formatDate(format: String, date: Date): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(date).toString()
    }

    private fun formatDateTime(format: String, date: Date): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(date).toString()
    }

    fun setMaxDate(unixTimeStamp: Long) {
        mMaxDate = unixTimeStamp
    }

    fun setMinDate(unixTimeStamp: Long) {
        mMinDate = unixTimeStamp
    }

    fun setDateTime(dateTime: Boolean) {
        needDateTime = dateTime
    }

    private fun getTimeStampFromDate(dateString: String): Long {
        return try {
            val date: Date? = SimpleDateFormat(stringFormat, Locale.getDefault()).parse(dateString)
            date?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (needToShowTimePicker) {
            myHour = hourOfDay;
            myMinute = minute;
            calendar.set(Calendar.HOUR_OF_DAY, myHour)
            calendar.set(Calendar.MINUTE, myMinute)
//            val datetime = Calendar.getInstance()
            val c = Calendar.getInstance()
//            datetime[Calendar.HOUR_OF_DAY] = hourOfDay
//            datetime[Calendar.MINUTE] = minute

            val sSDF = SimpleDateFormat(parkingdisplayDateTimeTimeSecondFormat)
            val sCurrentDate = calendar.time
            val selectedDateTimeStr = sSDF.format(sCurrentDate)
            val selectedDateTime = sSDF.parse(selectedDateTimeStr)
            val currentDate = c.time
            val currentDateTimeStr = sSDF.format(currentDate)
            val currentDateTime = sSDF.parse(currentDateTimeStr)

            if (selectedDateTime.equals(currentDateTime) || selectedDateTime.after(currentDateTime)) {
                updateDateTimeDisplay()
            }else{
                //it's before current'
                Toast.makeText(
                    context,
                    context.getString(R.string.date_time_error),
                    Toast.LENGTH_LONG
                ).show()
                setText("")

            }


//            if (datetime.timeInMillis >= c.timeInMillis) {
//                updateDateTimeDisplay()
//            } else {
//                //it's before current'
//                Toast.makeText(
//                    context,
//                    context.getString(R.string.date_time_error),
//                    Toast.LENGTH_LONG
//                ).show()
//                setText("")
//            }

            /* textView.setText("Year: " + myYear + "\n" +
                     "Month: " + myMonth + "\n" +
                     "Day: " + myday + "\n" +
                     "Hour: " + myHour + "\n" +
                     "Minute: " + myMinute);*/
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateDateTimeDisplay()
        }

    }

    fun setDateToCalender(date: Date){
        calendar.time = date
        selectedTime = date.time
    }
    fun getDateToCalender():Date{
        return calendar.time
    }

}