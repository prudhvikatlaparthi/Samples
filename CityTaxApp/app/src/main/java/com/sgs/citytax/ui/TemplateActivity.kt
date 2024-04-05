package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.GetInvoiceTemplateResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityTemplateBinding
import com.sgs.citytax.model.AppReceiptPrint
import com.sgs.citytax.ui.adapter.TemplateAdapter
import com.sgs.citytax.util.*
import java.util.*

class TemplateActivity : BaseActivity() {
    private lateinit var binding: ActivityTemplateBinding

    private var printHelper = PrintHelper()
    private var templateString: String? = ""
    private var advanceReceivedId: Int = 0
    private var taxReceiptID: Int = 0
    private var invoiceTemplateResponse: ArrayList<GetInvoiceTemplateResponse>? = null
    private var customerID: Int? = 0
    var receiptType: Constant.ReceiptType? = null
    private var adapter: TemplateAdapter? = null
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_template)
        processIntent()
        setToolBarTitle()
        setViews()
        getReceiptsTemplateContent()
        setListeners()
    }

    private fun setViews() {
        val itemDecor = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecor)
        adapter = TemplateAdapter(arrayListOf(), Constant.ReceiptType.TAX_NOTICE)
        binding.recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (receiptType) {
            Constant.ReceiptType.TAX_NOTICE -> {
                val intent = Intent(this, TaxDetailsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                intent.putExtra(Constant.KEY_CUSTOMER_ID, customerID)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TAX_NOTICE)
                startActivity(intent)
                finish()
            }
            else -> {
            }
        }
        super.onBackPressed()
    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                advanceReceivedId = it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0)
            customerID = it.getIntExtra("KEY_CUSTOMER_ID", 0)
            if (it.hasExtra(Constant.KEY_TAX_INVOICE_ID))
                taxReceiptID = it.getIntExtra(Constant.KEY_TAX_INVOICE_ID, 0)
            invoiceTemplateResponse = intent.getParcelableArrayListExtra("INVOICE_TEMPLATE_RESPONSE")
            if (it.hasExtra(Constant.KEY_RECEIPT_TYPE))
                receiptType = it.getSerializableExtra(Constant.KEY_RECEIPT_TYPE) as Constant.ReceiptType
        }
    }

    private fun setToolBarTitle() {
        when (receiptType) {
            Constant.ReceiptType.TAX_RECEIPT, Constant.ReceiptType.BUSINESS_TRANSACTION -> {
                showToolbarBackButton(R.string.title_tax_receipt)
            }
            Constant.ReceiptType.TAX_NOTICE, Constant.ReceiptType.TAX_NOTICE_HISTORY -> {
                showToolbarBackButton(R.string.title_tax_notice)
            }
        }
    }

    private fun setListeners() {
        if (receiptType == Constant.ReceiptType.BUSINESS_TRANSACTION) {
            binding.btnClose.visibility = VISIBLE
        }

        binding.btnClose.setOnClickListener {
            /*   val homeIntent = Intent(this, ScanActivity::class.java)
               homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
               homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
               startActivity(homeIntent)*/
            onBackPressed()
        }

        binding.btnPrint.setOnClickListener {
            if (prefHelper.printEnabled/* && MyApplication.sunmiPrinterService != null*/) {
                invoiceTemplateResponse?.let { it ->
                    for (item: GetInvoiceTemplateResponse in it) {
                        when (receiptType) {
                            Constant.ReceiptType.TAX_NOTICE -> {
                                item.taxInvoiceID?.let { invoiceNo ->
                                    insertReceiptPrint(invoiceNo, item, "Tax_Notice_Receipt", Constant.ReceiptType.TAX_NOTICE)
                                }
                            }
                            Constant.ReceiptType.TAX_RECEIPT -> {
                                insertReceiptPrint(advanceReceivedId, item, "Tax_Payment_Receipt", Constant.ReceiptType.TAX_RECEIPT)
                            }
                            Constant.ReceiptType.TAX_NOTICE_HISTORY -> {
                                insertReceiptPrint(taxReceiptID, item, "Tax_Notice_Receipt", Constant.ReceiptType.TAX_NOTICE_HISTORY)
                            }
                            Constant.ReceiptType.BUSINESS_TRANSACTION -> {
                                insertReceiptPrint(advanceReceivedId, item, "Tax_Payment_Receipt", Constant.ReceiptType.BUSINESS_TRANSACTION)
                            }
                        }
                    }
                }
            } else {
                showAlertDialog(getString(R.string.msg_print_not_support))
            }
        }
    }

    private fun insertReceiptPrint(Id: Int, invoiceResponse: GetInvoiceTemplateResponse, receiptCode: String, receiptType: Constant.ReceiptType) {
        APICall.insertPrintRequest(getAppReceiptPrint(Id, receiptCode), object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                when (receiptType) {
                    Constant.ReceiptType.TAX_NOTICE -> {
                        templateString = TxtTemplateUtils.getTemplateContent(invoiceResponse, 32, true)
                        printHelper.printUSBThermalPrinter(templateString, Id.toString(), receiptType)
                        count++
                        if (count == invoiceTemplateResponse?.size) {
                            count = 0
                            val intent = Intent(this@TemplateActivity, TaxNoticeCaptureActivity::class.java)
                            intent.putExtra("KEY_TAX_NOTICE_ID", invoiceResponse.taxInvoiceID.toString())
                            startActivity(intent)
                            finish()
                        }
                    }
                    Constant.ReceiptType.TAX_NOTICE_HISTORY -> {
                        templateString = TxtTemplateUtils.getTemplateContent(invoiceResponse, 32, true)
                        printHelper.printUSBThermalPrinter(templateString, Id.toString(), receiptType)
                    }
                    else -> {
                        templateString = TxtTemplateUtils.getTemplateContent(invoiceResponse, 32, false)
                        printHelper.printUSBThermalPrinter(templateString, Id.toString(), receiptType)
                    }
                }
            }

            override fun onFailure(message: String) {
                count = 0
                showAlertDialog(message)
            }
        })
    }

    private fun getAppReceiptPrint(Id: Int, receiptType: String): AppReceiptPrint {
        val appReceiptPrint = AppReceiptPrint()
        appReceiptPrint.printDateTime = getDate(Date(), DateTimeTimeZoneMillisecondFormat)
        appReceiptPrint.receiptCode = receiptType
        appReceiptPrint.primaryKeyValue = Id
        return appReceiptPrint
    }

    private fun getReceiptsTemplateContent() {
        when (receiptType) {
            Constant.ReceiptType.TAX_NOTICE -> {
                invoiceTemplateResponse?.let { it ->
                    for (response: GetInvoiceTemplateResponse? in it) {
                        response?.let {
                            val template = loadWebViewData(it, Constant.ReceiptType.TAX_NOTICE)
                            adapter?.addTemplate(template)
                        }
                    }
                }
            }
            Constant.ReceiptType.TAX_NOTICE_HISTORY -> {
                getInvoiceTemplateResponse("Tax_Notice_Receipt", taxReceiptID, 0)
            }
            Constant.ReceiptType.TAX_RECEIPT -> {
                getInvoiceTemplateResponse("Tax_Payment_Receipt", 0, advanceReceivedId)
            }
            Constant.ReceiptType.BUSINESS_TRANSACTION -> {
                getInvoiceTemplateResponse("Tax_Payment_Receipt", 0, advanceReceivedId)
            }
            else -> {
            }
        }
    }

    private fun getInvoiceTemplateResponse(receiptCode: String, taxReceiptId: Int, advanceReceivedId: Int) {
        showProgressDialog()
        APICall.getInvoiceTemplateInfo(receiptCode, taxReceiptId, advanceReceivedId, object : ConnectionCallBack<List<GetInvoiceTemplateResponse>> {
            override fun onSuccess(response: List<GetInvoiceTemplateResponse>) {
                if (response.isNotEmpty()) {
                    response[1].let {
                        response[0].taxableMatterName = it.taxableMatterName
                    }
                    if (invoiceTemplateResponse == null)
                        invoiceTemplateResponse = arrayListOf()
                    invoiceTemplateResponse?.clear()
                    invoiceTemplateResponse?.add(response[0])
                    when (receiptType) {
                        Constant.ReceiptType.TAX_RECEIPT -> {
                            adapter?.addTemplate(loadWebViewData(response[0], Constant.ReceiptType.TAX_RECEIPT))
                        }
                        Constant.ReceiptType.TAX_NOTICE_HISTORY -> {
                            adapter?.addTemplate(loadWebViewData(response[0], Constant.ReceiptType.TAX_NOTICE_HISTORY))
                        }
                        Constant.ReceiptType.BUSINESS_TRANSACTION -> {
                            adapter?.addTemplate(loadWebViewData(response[0], Constant.ReceiptType.BUSINESS_TRANSACTION))
                        }
                        else -> {

                        }
                    }
                }
                dismissDialog()
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }

    private fun loadWebViewData(invoiceTemplateResponse: GetInvoiceTemplateResponse?, receiptType: Constant.ReceiptType): String {
        var templateContent = ""
        if (invoiceTemplateResponse != null) {

            when (receiptType) {
                Constant.ReceiptType.TAX_RECEIPT -> {
                    templateContent = ReceiptHelper.getProfessionalHTMLTemplateData(this, invoiceTemplateResponse, advanceReceivedId)
                }
                Constant.ReceiptType.BUSINESS_TRANSACTION -> {
                    templateContent = ReceiptHelper.getProfessionalHTMLTemplateData(this, invoiceTemplateResponse, advanceReceivedId)
                }
                Constant.ReceiptType.TAX_NOTICE -> {
                    templateContent = ReceiptHelper.getTaxNoticeHtmlData(this, invoiceTemplateResponse, Constant.ReceiptType.TAX_NOTICE)
                }
                Constant.ReceiptType.TAX_NOTICE_HISTORY -> {
                    templateContent = ReceiptHelper.getTaxNoticeHtmlData(this, invoiceTemplateResponse, Constant.ReceiptType.TAX_NOTICE_HISTORY)
                }
                else -> {
                    templateContent = ""
                }
            }

        }
        return templateContent
    }

}