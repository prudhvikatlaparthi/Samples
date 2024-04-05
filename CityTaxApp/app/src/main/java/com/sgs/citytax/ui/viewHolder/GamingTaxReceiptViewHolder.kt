package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CartTaxReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemGamingPaymentReceiptBinding
import com.sgs.citytax.model.CartTaxReceiptDetails
import com.sgs.citytax.model.TaxTypesDetails
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.*
import java.util.*

class GamingTaxReceiptViewHolder(val mBinding: ItemGamingPaymentReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(taxDetails: CartTaxReceiptResponse, iClickListener: IClickListener?) {

        bindTaxDetails(taxDetails.taxReceiptsDetails[0], taxDetails.taxTypes[0],taxDetails)

        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })
        }
    }

    private fun bindTaxDetails(
        receiptDetails: CartTaxReceiptDetails?,
        taxTypesDetails: TaxTypesDetails,
        taxDetails: CartTaxReceiptResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = taxDetails.orgData
        )
        mBinding.txtPaymentMethodLabel.text = String.format("%s%s", getString(R.string.payment_method), getString(R.string.colon))
        mBinding.txtTransactionNoLabel.text = String.format("%s%s", getString(R.string.reference_transaction_number), getString(R.string.colon))
        val collectBy = mBinding.txtCollectedByLabel.context.getString(R.string.collected_by)
        mBinding.txtCollectedByLabel.text = String.format("%s%s", collectBy, getString(R.string.colon))
        val chequeNo = mBinding.txtChequeNumber.context.getString(R.string.cheque_number)
        mBinding.txtChequeNumber.text = String.format("%s%s", chequeNo, getString(R.string.colon))

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport
        if (receiptDetails != null) {

            // region Date Of Print
            mBinding.txtPrintDate.text = formatDisplayDateTimeInMillisecond(Date())

            receiptDetails.advanceReceiptId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_RECEIPT, it, taxTypesDetails.sycoTaxID))//

            }

            receiptDetails.referenceNo?.let {
                mBinding.txtQuittanceNo.text = it

            }
            receiptDetails.advancedate?.let {
                mBinding.txtRecoveryDate.text = formatDisplayDateTimeInMillisecond(it)

            }

            taxTypesDetails.serialNo?.let {
                mBinding.txtGamingMachineNo.text = it
            }

            taxTypesDetails.type?.let {
                mBinding.txtGamingMachineType.text = it
            }
            taxTypesDetails.sycoTaxID?.let {
                mBinding.txtGamingmachineSycoTaxId.text = it
            }
            taxTypesDetails.serialNo?.let {
                mBinding.txtGamingMachineNo.text = it
            }
            taxTypesDetails.owner?.let {
                mBinding.txtGamingMachineOwner.text = it
            }

            receiptDetails.citizenSycoTaxId?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }
            receiptDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }
            receiptDetails.sycotaxID?.let {
                mBinding.llSycoTaxID.visibility = View.VISIBLE
                mBinding.txtSycoTaxID.text = it
            }

            receiptDetails.paymentmode?.let {
                mBinding.txtPaymentMethod.text = it
            }



                receiptDetails.walletTransactionNo?.let {
                    mBinding.llTansactionNumber.visibility = View.VISIBLE
                    mBinding.txtTransactionNo.text = it.toString()
                }
             if (receiptDetails.paymentModeCode?.toUpperCase(Locale.getDefault()) == Constant.PaymentMode.CHEQUE.toString()) {
                mBinding.llCheque.visibility = View.VISIBLE


                receiptDetails.bankName?.let {
                    mBinding.txtChequeBankName.text = it.toString()
                }
                receiptDetails.chequeNumber?.let {
                    mBinding.txtChequeNumber.text = it.toString()
                }
                receiptDetails.chequeDate?.let {
                    mBinding.txtChequeDate.text = displayFormatDate(it)
                }

            }

            receiptDetails.totalDeposits?.let {
                mBinding.txtTotalDeposits.text = formatWithPrecision(it)
            }


            var amountOfTaxImposed = 0.0
            var amountOfThisPayment = 0.0
            var remainingToPay = 0.0

            //region Penalty Due
            mBinding.txtPenaltyAmountPaid.text = formatWithPrecision(0.0) 
            var penaltyPayable = 0.0
            receiptDetails.PenaltyPaid?.let {
                amountOfThisPayment+=it
                penaltyPayable += it
                    mBinding.txtPenaltyAmountPaid.text = formatWithPrecision(it) 
            }

            mBinding.txtPenaltyRemainpay.text = formatWithPrecision(0.0) 
            receiptDetails.penaltyDue?.let {
                remainingToPay+=it
                penaltyPayable += it
                    mBinding.txtPenaltyRemainpay.text = formatWithPrecision(it) 
            }

            mBinding.txtPenaltyToBePaid.text = formatWithPrecision(0.0) 
            penaltyPayable.let {
                amountOfTaxImposed+=it
                    mBinding.txtPenaltyToBePaid.text = formatWithPrecision(it) 
            }
            //endregion

            //region Anterior Unrecovered

            mBinding.txtAnteriorUnrecoveredAmountPaid.text = formatWithPrecision(0.0) 
            var anteriorUnrecovered = 0.0
            receiptDetails.amountPaidAnteriorYear?.let {
                amountOfThisPayment+=it
                anteriorUnrecovered += it
                    mBinding.txtAnteriorUnrecoveredAmountPaid.text = formatWithPrecision(it) 
            }

            mBinding.txtAnteriorUnrecoveredRemainToPay.text = formatWithPrecision(0.0) 
            receiptDetails.amountDueAnteriorYear?.let {
                remainingToPay+=it
                anteriorUnrecovered += it
                    mBinding.txtAnteriorUnrecoveredRemainToPay.text = formatWithPrecision(it) 
            }

            mBinding.txtAnteriorUnrecoveredAmountToBePaid.text = formatWithPrecision(0.0) 
            anteriorUnrecovered.let {
                amountOfTaxImposed +=it
                    mBinding.txtAnteriorUnrecoveredAmountToBePaid.text = formatWithPrecision(it) 
            }
            //endregion


            //region Previous Unrecovered

            mBinding.txtPreviousUnrecoveredAmountPaid.text = formatWithPrecision(0.0) 
            var previousUnrecovered = 0.0
            receiptDetails.amountPaidPreviousYear?.let {
                amountOfThisPayment+=it
                previousUnrecovered += it
                    mBinding.txtPreviousUnrecoveredAmountPaid.text = formatWithPrecision(it) 
            }

            mBinding.txtPreviousUnrecoveredRemainToPay.text = formatWithPrecision(0.0) 
            receiptDetails.amountDuePreviousYear?.let {
                remainingToPay+=it
                previousUnrecovered += it
                    mBinding.txtPreviousUnrecoveredRemainToPay.text = formatWithPrecision(it) 
            }

            mBinding.txtPreviousUnrecoveredAmountToBePaid.text = formatWithPrecision(0.0) 
            previousUnrecovered.let {
                amountOfTaxImposed+=it
                    mBinding.txtPreviousUnrecoveredAmountToBePaid.text = formatWithPrecision(it) 
            }
            //endregion

            //region Amount Current Year

            mBinding.txtTaxAmountCurrentYearPaid.text = formatWithPrecision(0.0) 
            var amountCurrentYear = 0.0
            receiptDetails.amountPaidCurrentYear?.let {
                amountOfThisPayment+=it
                amountCurrentYear += it
                    mBinding.txtTaxAmountCurrentYearPaid.text = formatWithPrecision(it) 
            }

            mBinding.txtTaxAmountCurrentYearRemainToPay.text = formatWithPrecision(0.0) 
            receiptDetails.amountDueCurrentYear?.let {
                remainingToPay+=it
                amountCurrentYear += it
                    mBinding.txtTaxAmountCurrentYearRemainToPay.text = formatWithPrecision(it) 
            }

            mBinding.txtTaxAmountCurrentYearToBePaid.text = formatWithPrecision(0.0) 
            amountCurrentYear.let {
                amountOfTaxImposed +=it
                    mBinding.txtTaxAmountCurrentYearToBePaid.text = formatWithPrecision(it) 
            }
            //endregion

            //region AmountOfTaxImposed
            amountOfTaxImposed.let {
                mBinding.txtTotalAmounTaxCollected.text = formatWithPrecision(it)
            }
            //endregion

            //region AmountOfThisPayment
            amountOfThisPayment.let{
                mBinding.txtAmountOfThisPay.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }
            //endregion

            //region Remaining to pay
            remainingToPay.let {
                if (receiptDetails.paymentModeCode?.toUpperCase(Locale.getDefault()) == Constant.PaymentMode.CHEQUE.toString()){
                    if (amountOfThisPayment>0){
                        mBinding.txtRemainingPay.text = formatWithPrecision(it)
                    }else {
                        val chequeNote = mBinding.txtRemainingPay.context.getString(R.string.subject_to_cheque_clearance)
                        mBinding.txtRemainingPay.text = "${formatWithPrecision(it)} " +
                                "(${chequeNote} ${formatWithPrecision(receiptDetails.amountofThisPayment)})"
                        mBinding.txtNoteLable.visibility = View.VISIBLE
                        mBinding.txtTaxNoticeNote.visibility = View.VISIBLE
                        receiptDetails.chequeNote?.let {
                            mBinding.txtTaxNoticeNote.text = it
                        }
                    }
                }else{
                    mBinding.txtRemainingPay.text = formatWithPrecision(it)
                    mBinding.txtNoteLable.visibility = View.GONE
                    mBinding.txtTaxNoticeNote.visibility = View.GONE
                }
            }
            //endregion


            receiptDetails.generatedBy?.let {
                mBinding.txtCollectedBy.text = it
            }


            receiptDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
 //                   mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
 //                   mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getReceiptPrintFlag(receiptDetails.advanceReceiptId!!.toInt(), mBinding.btnPrint)
            }
        }
    }
}