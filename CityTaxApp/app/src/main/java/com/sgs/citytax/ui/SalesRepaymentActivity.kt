package com.sgs.citytax.ui

import android.app.Activity
import android.content.Intent
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
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.Data
import com.sgs.citytax.api.payload.GenerateSalesTaxAndPaymentPayload
import com.sgs.citytax.api.payload.SalesListDetails
import com.sgs.citytax.api.response.SalesRepaymentItem
import com.sgs.citytax.api.response.SalesRepaymentResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivitySalesRepaymentBinding
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.ui.adapter.SalesRepaymentAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class SalesRepaymentActivity : BaseActivity() {
    private var selectedSalesRepaymentItem: SalesRepaymentItem? = null
    private lateinit var binding: ActivitySalesRepaymentBinding
    private lateinit var pagination: Pagination
    private var fromDate: String? = null
    private var toDate: String? = null
    private val resultList = mutableListOf<SalesRepaymentItem>()
    private val salesRepaymentAdapter: SalesRepaymentAdapter by lazy {
        SalesRepaymentAdapter { item: SalesRepaymentItem ->
            makePayment(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sales_repayment)
        showToolbarBackButton(R.string.title_cheque_repayments)
        initComponents()
        val map = getFromAndToDate()
        fromDate = map["from_date"]
        toDate = map["to_date"]
        setEvents()
        getData()
    }

    private fun setEvents() {
        binding.btnSearch.setOnClickListener {
            getData()
        }
    }

    private fun initComponents() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.recyclerView.adapter = salesRepaymentAdapter
        pagination = Pagination(1, 10, binding.recyclerView) { pageNumber, PageSize ->
            getData(pageNumber, pageSize = PageSize)
        }
    }

    private fun getData(pageIndex: Int = 1, pageSize: Int = 10) {
        if (pageIndex == 1) {
            binding.recyclerView.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            resetRecyclerAdapter()
            showProgressDialog()
        } else {
            binding.ProgressBar.isVisible = true
        }
        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
            val calender = Calendar.getInstance(Locale.getDefault())
            calender.add(Calendar.DAY_OF_YEAR, -1)
            fromDate = getDate(calender.time, DateFormat)
            toDate = getDate(Date(), DateFormat)
        }
        val salesListDetails = SalesListDetails(
            pageIndex = pageIndex,
            pageSize = pageSize,
            fromDate = fromDate,
            toDate = toDate,
            filter = binding.edtSalesItemSearch.text.toString()
        )
        APICall.getSalesRepaymentDetails(
            salesListDetails,
            object : ConnectionCallBack<SalesRepaymentResponse> {
                override fun onSuccess(response: SalesRepaymentResponse) {
                    dismissDialog()
                    binding.ProgressBar.hide()
                    if (pageIndex == 1) {
                        response.totalRecordsFound?.let {
                            pagination.totalRecords = it
                        }
                    }
                    if (response.salesListResults?.size ?: 0 > 0) {
                        pagination.stopPagination(response.salesListResults?.size!!)
                        resultList.addAll(response.salesListResults!!)
                        salesRepaymentAdapter.submitList(resultList)
                        pagination.setIsScrolled(false)
                    } else {
                        pagination.stopPagination(0)
                        if (pageIndex == 1) {
                            resetRecyclerAdapter()
                            showAlertDialog(getString(R.string.msg_no_data))
                        }
                    }
                }

                override fun onFailure(message: String) {
                    if (pageIndex == 1) {
                        resetRecyclerAdapter()
                        showAlertDialog(message)
                    }
                    dismissDialog()
                    binding.ProgressBar.hide()
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

    private fun resetRecyclerAdapter() {
        resultList.clear()
        salesRepaymentAdapter.submitList(resultList)
    }

    private fun makePayment(item: SalesRepaymentItem) {
        var finalPrice = item.net_receivable ?: BigDecimal.ZERO
        finalPrice = finalPrice.stripTrailingZeros().toPlainString().toBigDecimal()
        if (finalPrice <= BigDecimal.ZERO) {
            showAlertDialog(getString(R.string.net_receivable_should_be_greater_than_zero))
            return
        }
        selectedSalesRepaymentItem = item
        val data = Data(
            acctid = item.accountID_,
            finalPrice = finalPrice,
            sono = item.sales_order_no
        )
        val generateSalesTaxAndPaymentPayload =
            GenerateSalesTaxAndPaymentPayload(context = SecurityContext(), data = data)

        val payment = MyApplication.resetPayment()
        payment.amountDue = finalPrice //finalPrice
        payment.amountTotal = finalPrice //finalPrice
        payment.minimumPayAmount = finalPrice //finalPrice
        payment.customerID = item.accountID_ ?: 0
        payment.paymentType = Constant.PaymentType.SALES_TAX
        payment.generateSalesTaxAndPayment = generateSalesTaxAndPaymentPayload

        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(
            Constant.KEY_QUICK_MENU,
            Constant.QuickMenu.QUICK_MENU_SECURITY_TAX
        )
        intent.putExtra(
            Constant.KEY_IGNORE_CHEQUE_PAYMENT,
            true
        )
        startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.let {
                if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID) && it.getIntExtra(
                        Constant.KEY_ADVANCE_RECEIVED_ID,
                        0
                    ) > 0
                ) {
                    getData()
                    val intent = Intent(this, AllTaxNoticesActivity::class.java)
                    // sending the SalesOrderNo
                    if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(
                            Constant.KEY_ADVANCE_RECEIVED_ID,
                            it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0)
                        )
                    intent.putExtra(
                        Constant.KEY_TAX_RULE_BOOK_CODE,
                        Constant.TaxRuleBook.SALES.Code
                    )
                    if (selectedSalesRepaymentItem?.taxRuleBookCode_ == Constant.TaxRuleBook.SECURITY_SALES.Code) {
                        intent.putExtra(
                            Constant.KEY_QUICK_MENU,
                            Constant.QuickMenu.QUICK_MENU_SECURITY_TAX
                        )
                    } else {
                        intent.putExtra(
                            Constant.KEY_QUICK_MENU,
                            Constant.QuickMenu.QUICK_MENU_SALES_TAX
                        )
                    }
                    startActivity(intent)
                }
            }
        }
    }
}