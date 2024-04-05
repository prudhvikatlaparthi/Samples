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
import android.view.View.INVISIBLE
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.CreditBalance
import com.sgs.citytax.api.response.CreditBalanceResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityCreditBalanceBinding
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.ui.adapter.CreditBalanceAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.REQUEST_CODE_RECHARGE
import kotlinx.android.synthetic.main.filter_date_range.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreditBalanceActivity : BaseActivity() {
    private lateinit var binding: ActivityCreditBalanceBinding
    private var creditBalanceList: ArrayList<CreditBalance> = arrayListOf()
    private var fromDate: String = ""
    private var toDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credit_balance)
        showToolbarBackButton(R.string.title_credit_balance_statement)
        bindData()
        setViewEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recharge, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_recharge -> {
                MyApplication.resetPayment()
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE)
                startActivityForResult(intent, REQUEST_CODE_RECHARGE)
                /*val paymentIntent = Intent(this, PaymentActivity::class.java)
                paymentIntent.putExtra("IS_FOR_RECHARGE", true)
                startActivityForResult(paymentIntent, Constant.REQUEST_CODE_RECHARGE)*/
            }
            R.id.action_date_filter -> showDateRangeSelection()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getFromAndToDate(): Map<String, String> {
        val dateFormat = SimpleDateFormat(displayDateFormat, Locale.getDefault())
        val fromCal = Calendar.getInstance()
        val toCal = Calendar.getInstance()
        fromCal.add(Calendar.DATE, -60)
        val map: MutableMap<String, String> = HashMap()
        map["from_date"] = dateFormat.format(fromCal.time)
        map["to_date"] = dateFormat.format(toCal.time)
        return map
    }

    private fun showDateRangeSelection() {
        val layoutInflater = LayoutInflater.from(this)
        val binding = DataBindingUtil.inflate<FilterDateRangeBinding>(layoutInflater, R.layout.filter_date_range, date_dilog_linear_layout, false)
        binding.editTextFromDate.setText(fromDate)
        binding.editTextToDate.setText(toDate)
        binding.editTextFromDate.setDisplayDateFormat(displayDateFormat)
        binding.editTextToDate.setDisplayDateFormat(displayDateFormat)
        if (prefHelper.creditBalanceFromDate.isEmpty() && prefHelper.creditBalanceToDate.isEmpty()) {
            val map = getFromAndToDate()
            fromDate = map["from_date"] ?: ""
            toDate = map["to_date"] ?: ""
        } else {
            fromDate = prefHelper.creditBalanceFromDate
            toDate = prefHelper.creditBalanceToDate
        }

        binding.editTextFromDate.setText(fromDate)
        binding.editTextToDate.setText(toDate)

        binding.editTextFromDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let {
                    if (it.isNotEmpty()) {
                        binding.editTextToDate.setMinDate(parseDate(it, displayDateFormat).time)
                        binding.editTextToDate.setText("")
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
            fromDate = binding.editTextFromDate.text.toString()
            toDate = binding.editTextToDate.text.toString()
            if (TextUtils.isEmpty(fromDate)) {
                isValid = false
                binding.txtInpLayFromDate.error = getString(R.string.msg_from_date)
            } else if (TextUtils.isEmpty(toDate)) {
                isValid = false
                binding.txtInpLayToDate.error = getString(R.string.msg_to_date)
            }
            if (isValid) {
                bindData()
                dialog.dismiss()
            }
        }, 0, null, R.string.cancel, {
            val dialog = (it as Button).tag as AlertDialog
            dialog.dismiss()
        }, binding.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RECHARGE && resultCode == Activity.RESULT_OK) {
            bindData()
            data?.extras?.let { it ->
                if (it.containsKey(Constant.KEY_ADVANCE_RECEIVED_ID)) {
                    it.getInt(Constant.KEY_ADVANCE_RECEIVED_ID).let {
                        val intent = Intent(this@CreditBalanceActivity, AllTaxNoticesActivity::class.java)
                        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it)
                        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.RECHARGE.Code)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun setViewEvents() {
       /* binding.swpRefreshLayout.setOnRefreshListener {
            bindData()
            binding.swpRefreshLayout.isRefreshing = false
        }*/
    }

    fun bindData() {
        binding.tvAgentName.text = prefHelper.accountName
        showProgressDialog(getString(R.string.msg_please_wait))

        APICall.getAgentStatement(serverFormatDate(fromDate), serverFormatDate(toDate), object : ConnectionCallBack<CreditBalanceResponse> {
            override fun onSuccess(response: CreditBalanceResponse) {

                creditBalanceList.clear()

                if (prefHelper.agentIsPrepaid)
                    binding.tvAmount.text = formatWithPrecision("${response.balance}")
                else{
                    binding.tvBalance.visibility = INVISIBLE
                    binding.tvAmount.visibility = INVISIBLE
                }

                if (response.transactions != null && response.transactions!!.isNotEmpty()) {

                    for (balance: CreditBalance in response.transactions!!)
                        if (balance.credit != 0.0 && balance.debit != 0.0)
                            creditBalanceList.add(balance)

                    creditBalanceList.reverse()
                    binding.recyclerView.adapter = CreditBalanceAdapter(creditBalanceList)
                } else {
                    creditBalanceList.clear()
                    binding.recyclerView.adapter = null
                }
                dismissDialog()
            }

            override fun onFailure(message: String) {
                creditBalanceList = ArrayList()
                binding.recyclerView.adapter = null
                binding.tvAgentName.text = ""
                binding.tvAmount.text = formatWithPrecision(0.0)
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }
}