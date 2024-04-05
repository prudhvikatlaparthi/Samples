package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CommissionHistory
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityPayoutPreviewBinding
import com.sgs.citytax.util.*
import java.util.*

class PayoutPreviewActivity : BaseActivity() {

    private lateinit var binding: ActivityPayoutPreviewBinding
    private var commissionHistory: CommissionHistory? = null
    private var printHelper = PrintHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payout_preview)
        hideToolbar()
        processIntent()
        bindData()
        initialiseListeners()
    }

    fun processIntent() {
        if (intent.extras != null) {
            commissionHistory = intent.getParcelableExtra("CommissionHistory")
        }
    }

    fun bindData() {
        binding.txtVoucherNo.text = commissionHistory?.referenceNo ?: ""
        binding.txtAgentName.text = prefHelper.agentName
        binding.txtApprovedBy.text = commissionHistory?.approverName ?: ""
        binding.txtCommissionAmount.text = formatWithPrecision(commissionHistory?.netPayable)
        binding.txtApprovedTo.text = commissionHistory?.accountName ?: ""
        binding.txtApprovedDate.text = formatDisplayDateTimeInMillisecond(commissionHistory?.approvedDate)
    }

    private fun initialiseListeners() {
        binding.btnPrint.setOnClickListener {
            commissionHistory?.approvedDate = getDate(Date(), displayDateTimeTimeSecondFormat)
            commissionHistory?.status = "Approved"
            if (MyApplication.sunmiPrinterService != null) {
                val body = loadBitmapFromView(binding.llReceiptBody)
                val resizedBody = resize(body)
                printHelper.printBitmap(resizedBody)
          /*      val templateString = TxtTemplateUtils.getPayoutContent(commissionHistory, 32)
                printHelper.printUSBThermalPrinter(templateString, "", null)*/
            } else {
                showAlertDialog(getString(R.string.msg_print_not_support))
            }
        }

        binding.btnClose.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }

}