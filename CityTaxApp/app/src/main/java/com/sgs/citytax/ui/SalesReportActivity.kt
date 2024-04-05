package com.sgs.citytax.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetGroupingSalesReportPayload
import com.sgs.citytax.api.response.GetGroupingSalesReportResponse
import com.sgs.citytax.api.response.HeaderT
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivitySalesReportBinding
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.text.SimpleDateFormat
import java.util.*

class SalesReportActivity : BaseActivity() {
    private lateinit var binding: ActivitySalesReportBinding
    private var fromDate: String? = null
    private var toDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sales_report)
        showToolbarBackButton(R.string.title_sales_report)
        initComponents()
        val map = getFromAndToDate()
        fromDate = map["from_date"]
        toDate = map["to_date"]
        setEvents()
        getData()
    }

    private fun setEvents() {

    }

    private fun initComponents() {

    }

    private fun getData() {
        showProgressDialog()
        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
            val calender = Calendar.getInstance(Locale.getDefault())
            calender.add(Calendar.DAY_OF_YEAR, -1)
            fromDate = getDate(calender.time, DateFormat)
            toDate = getDate(Date(), DateFormat)
        }
        val payload = GetGroupingSalesReportPayload(
            fromDate = fromDate, toDate = toDate, payLoadString = null
        )
        APICall.getGroupingSalesReport(
            payload,
            object : ConnectionCallBack<GetGroupingSalesReportResponse> {
                override fun onSuccess(response: GetGroupingSalesReportResponse) {
                    binding.salesReportView.clearView()
                    dismissDialog()
                    updateSalesData(response)
                }

                override fun onFailure(message: String) {
                    dismissDialog()
                    binding.salesReportView.clearView()
                    showAlertDialog(message)
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_filter) {
            showDateRangeSelection()
        } else if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    private fun getFromAndToDate(): Map<String, String> {
        val dateFormat = SimpleDateFormat(DateFormat, Locale.getDefault())
        val fromCal = Calendar.getInstance()
        val toCal = Calendar.getInstance()
        fromCal.add(Calendar.DATE, -1)
        val map = HashMap<String, String>()
        map["from_date"] = dateFormat.format(fromCal.time)
        map["to_date"] = dateFormat.format(toCal.time)
        return map
    }

    private fun showDateRangeSelection() {
        val layoutInflater = LayoutInflater.from(this)
        val binding = DataBindingUtil.inflate<FilterDateRangeBinding>(
            layoutInflater,
            R.layout.filter_date_range,
            date_dilog_linear_layout,
            false
        )
        val edtFromDate = binding.editTextFromDate
        val edtToDate = binding.editTextToDate
        edtToDate.isEnabled = false
        edtToDate.setDisplayDateFormat(displayDateFormat)
        edtToDate.setMaxDate(Calendar.getInstance().timeInMillis)
        edtFromDate.setDisplayDateFormat(displayDateFormat)
        edtFromDate.setMaxDate(Calendar.getInstance().timeInMillis)
        if (fromDate.isNullOrBlank() && fromDate.isNullOrBlank()) {
            val map = getFromAndToDate()
            fromDate = map["from_date"]
            toDate = map["to_date"]
        }
        edtFromDate.setText(fromDate?.let {
            displayFormatDate(formatDates(it))
        })
        edtToDate.setText(toDate?.let {
            displayFormatDate(formatDates(it))
        })

        binding.editTextFromDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                edtFromDate.text?.toString()?.let {
                    if (it.isNotEmpty()) {
                        edtToDate.isEnabled = true
                        edtToDate.setText("")
                        edtToDate.setMinDate(parseDate(it, displayDateFormat).time)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.txtInpLayFromDate.error = null
                }
            }
        })

        binding.editTextToDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.txtInpLayToDate.error = null
                }
            }
        })
        showAlertDialog(R.string.title_select_date, R.string.ok, {
            val dialog = (it as Button).tag as AlertDialog
            var isValid = true
            fromDate = serverFormatDate(edtFromDate.text.toString())
            toDate = serverFormatDate(edtToDate.text.toString())
            if (TextUtils.isEmpty(fromDate)) {
                isValid = false
                binding.txtInpLayFromDate.error = getString(R.string.msg_from_date)
                edtFromDate.requestFocus()
            } else if (TextUtils.isEmpty(toDate)) {
                isValid = false
                binding.txtInpLayToDate.error = getString(R.string.msg_to_date)
                edtToDate.requestFocus()
            }
            if (isValid) {
                getData()
                dialog.dismiss()
            }
        }, 0, null, R.string.cancel, {
            val dialog = (it as Button).tag as AlertDialog
            dialog.dismiss()
        }, binding.root)
    }


    private fun updateSalesData(response: GetGroupingSalesReportResponse) {
        response.headerT?.let { list: List<HeaderT> ->
            list.forEach {
                binding.salesReportView.updateView(it, response)
            }
        }
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun finish()
        fun showAlertDialog(
            message: Int,
            positiveButton: Int,
            positiveListener: View.OnClickListener,
            neutralButton: Int,
            neutralListener: View.OnClickListener?,
            negativeButton: Int,
            negativeListener: View.OnClickListener,
            view: View
        )
    }
}