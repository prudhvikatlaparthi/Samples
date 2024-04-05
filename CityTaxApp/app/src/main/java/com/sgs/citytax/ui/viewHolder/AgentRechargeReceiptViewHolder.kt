package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.AgentRechargeReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemAgentWalletRechargeReceiptBinding
import com.sgs.citytax.model.AgentRechargeReceiptDetails
import com.sgs.citytax.util.*
import java.util.*

class AgentRechargeReceiptViewHolder(val mBinding: ItemAgentWalletRechargeReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(agentRechargeReceiptResponse: AgentRechargeReceiptResponse, iClickListener: IClickListener) {

        bindAgentReceiptDetails(agentRechargeReceiptResponse.agentRechargeReceiptDetails[0],agentRechargeReceiptResponse)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, agentRechargeReceiptResponse)
                }
            })
        }
    }

    private fun bindAgentReceiptDetails(
        receiptDetails: AgentRechargeReceiptDetails?,
        agentRechargeReceiptResponse: AgentRechargeReceiptResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = agentRechargeReceiptResponse.orgData
        )
        mBinding.txtAgentNameLabel.text = String.format("%s%s", getString(R.string.agent_name), getString(R.string.colon))
        mBinding.txtAgentCodeLabel.text = String.format("%s%s", getString(R.string.agent_code), getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport


        if (receiptDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())
            receiptDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
                }
            }
             receiptDetails.taxationYear?.let {
                 mBinding.txtTaxationYear.text = it.toString()
             }
            receiptDetails.advanceReceivedId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.AGENT_RECHARGE, it.toString()))
            }
            receiptDetails.advanceDate?.let {
                mBinding.txtDateOfRecharge.text = formatDisplayDateTimeInMillisecond(it)
            }
            receiptDetails.agentName?.let {
                mBinding.txtAgentName.text = it
            }
            receiptDetails.agentCode?.let {
                mBinding.txtAgentCode.text = it.toString()
            }
            receiptDetails.referanceNo?.let {
                mBinding.txtVoucherNo.text =it
            }
            receiptDetails.branchName?.let {
                mBinding.txtAdminOffice.text = it
            }
            receiptDetails.zone?.let {
                mBinding.txtArdt.text = it
            }
            receiptDetails.sector?.let {
                mBinding.txtSector.text = it
            }
            receiptDetails.plot?.let {
                mBinding.txtSection.text = it
            }
            receiptDetails.block?.let {
                mBinding.txtLot.text = it
            }
            receiptDetails.doorNo?.let {
                mBinding.txtParcel.text = it
            }
            receiptDetails.walletTransactionNo?.let {
                mBinding.txtReferanceTransactionNumber.text = it.toString()
            }
            receiptDetails.amountPaid?.let {
                mBinding.txtRechargeAmount.text = formatWithPrecision(it)
                mBinding.txtAmountPaid.text = formatWithPrecision(it)
                 getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }
            receiptDetails.paymentMode?.let {
                mBinding.txtPaymentMethod.text = it
            }
            receiptDetails.collectedBy?.let {
                mBinding.txtCollectedBy.text = it
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getReceiptPrintFlag(receiptDetails.advanceReceivedId!!, mBinding.btnPrint)
            }
        }
    }
}