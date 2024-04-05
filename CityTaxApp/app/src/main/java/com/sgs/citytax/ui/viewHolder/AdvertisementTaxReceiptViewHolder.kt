package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.TaxReceiptsResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemAdvertisementTaxReceiptBinding
import com.sgs.citytax.model.TaxReceiptsDetails
import com.sgs.citytax.util.*
import java.util.*

class AdvertisementTaxReceiptViewHolder(val mBinding: ItemAdvertisementTaxReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(taxReceiptsResponse: TaxReceiptsResponse, iClickListener: IClickListener) {

        bindReceiptDetails(taxReceiptsResponse.taxReceiptsDetails[0],taxReceiptsResponse)
        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxReceiptsResponse)
                }
            })
        }
    }


    private fun bindReceiptDetails(
        taxDetails: TaxReceiptsDetails?,
        taxReceiptsResponse: TaxReceiptsResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = taxReceiptsResponse.orgData
        )
        val collectBy = mBinding.txtCollectedByLabel.context.getString(R.string.collected_by)
        mBinding.txtCollectedByLabel.text = String.format("%s%s", collectBy, getString(R.string.colon))
        val chequeNo = mBinding.txtChequeNumber.context.getString(R.string.cheque_number)
        mBinding.txtChequeNumber.text = String.format("%s%s", chequeNo, getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        if (taxDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            taxDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
 //                   mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
  //                  mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }

            taxDetails.referanceNo?.let {
                mBinding.txtQuittanceNo.text = it
            }
            taxDetails.advanceReceivedID?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_RECEIPT, it.toString(), taxDetails.sycoTaxId
                        ?: ""))
            }

            taxDetails.advanceDate?.let {
                mBinding.txtRecoveryDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            taxDetails.businessOwner?.let {
                var value = it
                if (it.contains(";")) value = it.replace(";", "\n")
                mBinding.txtBusinessOwner.text = value
            }

            taxDetails.businessName?.let {
                mBinding.txtBusinessName.text = it
            }
            taxDetails.sycoTaxId?.let {
                mBinding.txtSycoTaxID.text = it
            }
            taxDetails.taxSubType?.let {
                mBinding.txtNatureOfOccupancy.text = it
            }
            taxDetails.paymentMode?.let {
                mBinding.txtPaymentMethod.text = it
            }

            //region Check and wallet
            taxDetails.walletTransactionNo?.let {
                mBinding.llWalletTransactionNumber.visibility = View.VISIBLE
                mBinding.txtReferanceTransactionNumber.text = it.toString()
            }

            taxDetails.chequeNumber?.let {
                mBinding.llChequeNumber.visibility = View.VISIBLE
                mBinding.txtChequeNumber.text = it.toString()
            }

            taxDetails.bankName?.let {
                mBinding.llBankName.visibility = View.VISIBLE
                mBinding.txtChequeBankName.text = it
            }

            taxDetails.chequeDate?.let {
                mBinding.llChequeDate.visibility = View.VISIBLE
                mBinding.txtChequeDate.text = displayFormatDate(it)
            }
            //endregion

            taxDetails.totalDeposits?.let {
                mBinding.txtTotalDeposits.text = formatWithPrecision(it)
            }

            /* var amountOfTaxImposed = 0.0
             taxDetails.taxImposed?.let {
                 mBinding.txtAmountOfTaxImposed.text = formatWithPrecision(it)
                 amountOfTaxImposed = it
             }*/

            var amountOfTaxImposed = 0.0
            var amountOfThisPayment = 0.0
            var remainingPay = 0.0

            //region Penalty Due
            mBinding.txtPenaltyTaxReceived.text = formatWithPrecision(0.0)
            var penaltyPayable = 0.0
            taxDetails.penaltyPaid?.let {
                amountOfThisPayment += it
                penaltyPayable += it
                    mBinding.txtPenaltyTaxReceived.text = formatWithPrecision(it)
            }

            mBinding.txtPenaltyTaxBalance.text = formatWithPrecision(0.0)
            taxDetails.penaltyDue?.let {
                remainingPay += it
                penaltyPayable += it
                    mBinding.txtPenaltyTaxBalance.text = formatWithPrecision(it)
            }

            mBinding.txtPenaltyTaxPayable.text = formatWithPrecision(0.0)
            penaltyPayable.let {
                amountOfTaxImposed += it
                    mBinding.txtPenaltyTaxPayable.text = formatWithPrecision(it)
            }
            //endregion

            //region RAR AnteriorYear
            var anteriorYearPayable = 0.0
            mBinding.txtAnteriorYearTaxReceived.text = formatWithPrecision(0.0)
            taxDetails.amountPaidAnteriorYear?.let {
                amountOfThisPayment += it
                anteriorYearPayable += it
                    mBinding.txtAnteriorYearTaxReceived.text = formatWithPrecision(it)
            }

            mBinding.txtAnteriorYearTaxBalance.text = formatWithPrecision(0.0)
            taxDetails.amountDueAnteriorYear?.let {
                remainingPay += it
                anteriorYearPayable += it
                    mBinding.txtAnteriorYearTaxBalance.text = formatWithPrecision(it)
            }

            mBinding.txtAnteriorYearTaxPayable.text = formatWithPrecision(0.0)
            anteriorYearPayable.let {
                amountOfTaxImposed += it
                    mBinding.txtAnteriorYearTaxPayable.text = formatWithPrecision(it)
            }
            //endregion

            //region RAR Previous year
            var previousYearPayable = 0.0
            mBinding.txtPreviousYearTaxReceived.text = formatWithPrecision(0.0)
            taxDetails.amountPaidPreviousYear?.let {
                amountOfThisPayment += it
                previousYearPayable += it
                    mBinding.txtPreviousYearTaxReceived.text = formatWithPrecision(it)
            }

            mBinding.txtPreviousYearTaxBalance.text = formatWithPrecision(0.0)
            taxDetails.amountDuePreviousYear?.let {
                remainingPay += it
                previousYearPayable += it
                    mBinding.txtPreviousYearTaxBalance.text = formatWithPrecision(it)
            }

            mBinding.txtPreviousYearTaxPayable.text = formatWithPrecision(00.0)
            previousYearPayable.let {
                amountOfTaxImposed += it
                    mBinding.txtPreviousYearTaxPayable.text = formatWithPrecision(it)
            }
            //endregion

            //region Amount Due Current Year
            var currentYearPayableAmount = 0.0
            mBinding.txtCurrentYearTaxReceived.text = formatWithPrecision(0.0)
            taxDetails.amountPaidCurrentYear?.let {
                amountOfThisPayment += it
                currentYearPayableAmount += it
                    mBinding.txtCurrentYearTaxReceived.text = formatWithPrecision(it)

            }

            mBinding.txtCurrentYearTaxBalance.text = formatWithPrecision(0.0)
            taxDetails.amountDueCurrentYear?.let {
                remainingPay += it
                currentYearPayableAmount += it
                    mBinding.txtCurrentYearTaxBalance.text = formatWithPrecision(it)
            }

            mBinding.txtCurrentYearTaxPayable.text = formatWithPrecision(0.0)
            currentYearPayableAmount.let {
                amountOfTaxImposed += it
                    mBinding.txtCurrentYearTaxPayable.text = formatWithPrecision(it)
            }
            //endregion


            //region Amount of tax imposed
            amountOfTaxImposed.let {
                mBinding.txtAmountOfTaxImposed.text = formatWithPrecision(it)
            }
            //endregion

            //region Amount of this payment
            amountOfThisPayment.let {
                mBinding.txtAmountOfThisPayment.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }
            //endregion

            //region Remaining to pay
            remainingPay.let {
                if (taxDetails.paymentModeCode?.toUpperCase(Locale.getDefault()) == Constant.PaymentMode.CHEQUE.toString()){
                    if (amountOfThisPayment>0){
                        mBinding.txtRemainingPay.text = formatWithPrecision(it)
                    }else {
                        val chequeNote = mBinding.txtRemainingPay.context.getString(R.string.subject_to_cheque_clearance)
                        mBinding.txtRemainingPay.text = "${formatWithPrecision(it)} " +
                                "(${chequeNote} ${formatWithPrecision(taxDetails.amountOfThisPayment)})"
                        mBinding.txtNoteLable.visibility = View.VISIBLE
                        mBinding.txtTaxNoticeNote.visibility = View.VISIBLE
                        taxDetails.chequeNote?.let {
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


            taxDetails.generatedBy?.let {
                mBinding.txtCollectedBy.text = it
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getReceiptPrintFlag(taxDetails.advanceReceivedID!!, mBinding.btnPrint)
            }

        }
    }
}