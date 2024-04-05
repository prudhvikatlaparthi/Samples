package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.TaxPaymentHistoryResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityPaymentHistoryBinding
import com.sgs.citytax.model.TaxPaymentHistory
import com.sgs.citytax.ui.adapter.PaymentHistoryAdapter
import com.sgs.citytax.util.Constant
import java.util.*

class PaymentHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityPaymentHistoryBinding
    var histories: List<TaxPaymentHistory> = ArrayList()
    var adapter: PaymentHistoryAdapter? = null
    var pageIndex: Int = 1
    val pageSize: Int = 50
    var customerID: Int = 0
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_history)
        showToolbarBackButton(R.string.payment_history)
        //setUpTaxPayerEntryFragment()
        setViews()
        bindData()
        setListeners()
    }

    private fun setViews() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        adapter = PaymentHistoryAdapter()
        binding.recyclerView.adapter = adapter
    }

    private fun bindData() {
        showProgressDialog(getString(R.string.msg_please_wait))
        val payment = MyApplication.getPayment()
        val details = TaxPaymentHistory()
        details.isLoading = true
        adapter?.add(details)
        isLoading = true
        if (payment.taxRuleBookCode == Constant.TaxRuleBook.COM_PROP.Code
            ||payment.taxRuleBookCode == Constant.TaxRuleBook.RES_PROP.Code
            ||payment.taxRuleBookCode == Constant.TaxRuleBook.LAND_PROP.Code
            ||payment.taxRuleBookCode == Constant.TaxRuleBook.LAND_CONTRIBUTION.Code
            )
        {
            customerID = 0
        }
        else
        {
            customerID = payment.customerID
        }
        APICall.getTaxPaymentHistory(customerID
                , payment.productCode, payment.voucherNo
                ?: 0, pageSize, pageIndex, object : ConnectionCallBack<TaxPaymentHistoryResponse> {
            override fun onSuccess(response: TaxPaymentHistoryResponse) {
                dismissDialog()
                if (response.paymentHistories != null && response.paymentHistories.isNotEmpty()) {
                    histories = response.paymentHistories
                    val count = histories.size
                    if (count < pageSize) {
                        hasMoreData = false
                    } else {
                        pageIndex += 1
                    }
                    adapter?.remove(details)
                    adapter?.addAll(histories)
                    isLoading = false
                } else {
                    adapter?.remove(details)
                    isLoading = false
                }
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
                adapter?.remove(details)
                isLoading = false
            }
        })
    }

    private fun setListeners() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = recyclerView.layoutManager!!.childCount
                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount: Int = linearLayoutManager.itemCount
                val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && hasMoreData && !isLoading) {
                    bindData()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

}