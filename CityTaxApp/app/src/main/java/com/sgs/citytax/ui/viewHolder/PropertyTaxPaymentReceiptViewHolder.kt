package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.PropertyTaxReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemPropertyPaymentReceiptBinding
import com.sgs.citytax.model.PropertyTaxNoticeModel
import com.sgs.citytax.model.PropertyTaxNoticeOwnerShipModel
import com.sgs.citytax.model.PropertyTaxNoticePropertyDetailsModel
import com.sgs.citytax.util.*
import java.util.*

class PropertyTaxPaymentReceiptViewHolder(val mBinding: ItemPropertyPaymentReceiptBinding, private val screenType: String) : RecyclerView.ViewHolder(mBinding.root) {

    var ticketImposedAmount = 0.0
    var amountOfThisPayment = 0.0
    var remainingPay = 0.0

    fun bind(response: PropertyTaxReceiptResponse, iClickListener: IClickListener?) {
        propertyTaxNotice(response.table.get(0), response.table1.get(0), response.table2.get(0),response)
//        propertyTaxNoticeOwnerShip(response.table1)
//        propertyTaxNoticePropertyDetails(response.table2)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, response)
                }
            })
        }
    }

    private fun propertyTaxNotice(
        receiptDetails: PropertyTaxNoticeModel?,
        ownership: PropertyTaxNoticeOwnerShipModel,
        propertyDetails: PropertyTaxNoticePropertyDetailsModel?,
        response: PropertyTaxReceiptResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = response.orgData
        )
        mBinding.txtPaymentMethodLabel.text = String.format("%s%s", getString(R.string.payment_method), getString(R.string.colon))
        mBinding.txtTransactionNoLabel.text = String.format("%s%s", getString(R.string.reference_transaction_number), getString(R.string.colon))
        val collectBy = mBinding.txtCollectedByLabel.context.getString(R.string.collected_by)
        mBinding.txtCollectedByLabel.text = String.format("%s%s", collectBy, getString(R.string.colon))
        val chequeNo = mBinding.txtChequeNumber.context.getString(R.string.cheque_number)
        mBinding.txtChequeNumber.text = String.format("%s%s", chequeNo, getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        // region Date Of Print
        mBinding.txtPrintDate.text = formatDisplayDateTimeInMillisecond(Date())



        if (receiptDetails != null) {

            if (screenType == Constant.TaxRuleBook.LAND_PROP.Code || screenType == Constant.TaxRuleBook.LAND_CONTRIBUTION.Code)
                mBinding.receiptId.text = getString(R.string.payment_receipt_land_tax)

            // region Date Of Print
            mBinding.txtPrintDate.text = formatDisplayDateTimeInMillisecond(Date())
            receiptDetails.advrecdid?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_RECEIPT, it, propertyDetails?.PropertySycotaxID))//

            }
            receiptDetails.refno?.let {
                mBinding.txtpaymentReceiptNo.text = it
            }
            receiptDetails.advdt?.let {
                mBinding.txtPaymentDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            receiptDetails.TaxationYear?.let {
                mBinding.txtTaxationYear.text = it
            }

            propertyDetails?.PropertySycotaxID?.let {
                mBinding.txtSycoTaxId.text = it
            }

            ownership.acctname?.let {
                mBinding.txtOwner.text = it
            }

            ownership.mob?.let {
                mBinding.txtOwnerPhone.text = it
            }

            ownership.propertyOwnerIDSycoTax?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }

            ownership.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }

            var address: String? = ""

            propertyDetails?.state?.let {
                mBinding.txtState.text = it
                address += it
                address += ","
            }
            propertyDetails?.city?.let {
                mBinding.txtCity.text = it
                address += it
                address += ","
            }
            propertyDetails?.zone?.let {
                mBinding.txtArdt.text = it
                address += it
                address += ","
            }

            propertyDetails?.sector?.let {
                mBinding.txtSector.text = it
                address += it
                address += ","
            }
            propertyDetails?.plot?.let {
                mBinding.txtSection.text = it
                address += it
                address += ","
            }
            propertyDetails?.block?.let {
                mBinding.txtLot.text = it
                address += it
                address += ","
            }
            propertyDetails?.doorNo?.let {
                mBinding.txtParcel.text = it
                address += it
                address += ","
            }
            propertyDetails?.street?.let {
                address += it
                address += ","
            }
            propertyDetails?.zipCode?.let {
                address += it
            }
            propertyDetails?.PropertyName?.let {
                mBinding.txtPropertyName.text = it
            }

            if (!address.isNullOrEmpty()) {
                mBinding.txtAddress.text = address
            } else
                mBinding.txtAddress.text = ""

            receiptDetails.pmtmode?.let {
                mBinding.txtPaymentMethod.text = it
            }

            if (receiptDetails.pmtmodecode?.toUpperCase(Locale.getDefault()) == Constant.PaymentMode.WALLET.toString()) {
                mBinding.llTansactionNumber.visibility = View.VISIBLE
                receiptDetails.WalletTransactionNo?.let {
                    mBinding.txtTransactionNo.text = it.toString()
                }
            } else if (receiptDetails.pmtmodecode?.toUpperCase(Locale.getDefault()) == Constant.PaymentMode.ORANGE.toString()) {
                mBinding.llTansactionNumber.visibility = View.VISIBLE
                receiptDetails.WalletTransactionNo?.let {
                    mBinding.txtTransactionNo.text = it.toString()
                }
            } else if (receiptDetails.pmtmodecode?.toUpperCase(Locale.getDefault()) == Constant.PaymentMode.MOBICASH.toString()) {
                mBinding.llTansactionNumber.visibility = View.VISIBLE
                receiptDetails.WalletTransactionNo?.let {
                    mBinding.txtTransactionNo.text = it.toString()
                }
            }


            else if (receiptDetails.pmtmodecode?.toUpperCase(Locale.getDefault()) == Constant.PaymentMode.CHEQUE.toString()) {
                mBinding.llCheque.visibility = View.VISIBLE

                receiptDetails.bnkname?.let {
                    mBinding.txtChequeBankName.text = it.toString()
                }
                receiptDetails.chqno?.let {
                    mBinding.txtChequeNumber.text = it.toString()
                }
                receiptDetails.chqdt?.let {
                    mBinding.txtChequeDate.text = displayFormatDate(it)
                }

            }

            receiptDetails.TotalDeposit?.let {
                mBinding.txtTotalDeposits.text = formatWithPrecision(it)
            }


            var amountOfTaxImposed = 0.0
            var amountOfThisPayment = 0.0
            var remainingToPay = 0.0

            //region Penalty Due
            mBinding.txtPenaltyAmountPaid.text = formatWithPrecision(0.0)
            var penaltyPayable = 0.0
            receiptDetails.PenaltyPaid?.let {
                amountOfThisPayment += it
                penaltyPayable += it
                mBinding.txtPenaltyAmountPaid.text = formatWithPrecision(it)
            }

            mBinding.txtPenaltyRemainpay.text = formatWithPrecision(0.0)
            receiptDetails.PenaltyDue?.let {
                remainingToPay += it
                penaltyPayable += it
                mBinding.txtPenaltyRemainpay.text = formatWithPrecision(it)
            }

            mBinding.txtPenaltyToBePaid.text = formatWithPrecision(0.0)
            penaltyPayable.let {
                amountOfTaxImposed += it
                mBinding.txtPenaltyToBePaid.text = formatWithPrecision(it)
            }
            //endregion


            //region Anterior Unrecovered

            mBinding.txtAnteriorUnrecoveredAmountPaid.text = formatWithPrecision(0.0)
            var anteriorUnrecovered = 0.0
            receiptDetails.AmountPaidAnteriorYear?.let {
                amountOfThisPayment += it
                anteriorUnrecovered += it
                mBinding.txtAnteriorUnrecoveredAmountPaid.text = formatWithPrecision(it)
            }

            mBinding.txtAnteriorUnrecoveredRemainToPay.text = formatWithPrecision(0.0)
            receiptDetails.AmountDueAnteriorYear?.let {
                remainingToPay += it
                anteriorUnrecovered += it
                mBinding.txtAnteriorUnrecoveredRemainToPay.text = formatWithPrecision(it)
            }

            mBinding.txtAnteriorUnrecoveredAmountToBePaid.text = formatWithPrecision(0.0)
            anteriorUnrecovered.let {
                amountOfTaxImposed += it
                mBinding.txtAnteriorUnrecoveredAmountToBePaid.text = formatWithPrecision(it)
            }
            //endregion


            //region Previous Unrecovered

            mBinding.txtPreviousUnrecoveredAmountPaid.text = formatWithPrecision(0.0)
            var previousUnrecovered = 0.0
            receiptDetails.AmountPaidPreviousYear?.let {
                amountOfThisPayment += it
                previousUnrecovered += it
                mBinding.txtPreviousUnrecoveredAmountPaid.text = formatWithPrecision(it)
            }

            mBinding.txtPreviousUnrecoveredRemainToPay.text = formatWithPrecision(0.0)
            receiptDetails.AmountDuePreviousYear?.let {
                remainingToPay += it
                previousUnrecovered += it
                mBinding.txtPreviousUnrecoveredRemainToPay.text = formatWithPrecision(it)
            }

            mBinding.txtPreviousUnrecoveredAmountToBePaid.text = formatWithPrecision(0.0)
            previousUnrecovered.let {
                amountOfTaxImposed += it
                mBinding.txtPreviousUnrecoveredAmountToBePaid.text = formatWithPrecision(it)
            }
            //endregion


            //region Amount Current Year

            mBinding.txtTaxAmountCurrentYearPaid.text = formatWithPrecision(0.0)
            var amountCurrentYear = 0.0
            receiptDetails.AmountPaidCurrentYear?.let {
                amountOfThisPayment += it
                amountCurrentYear += it
                mBinding.txtTaxAmountCurrentYearPaid.text = formatWithPrecision(it)
            }

            mBinding.txtTaxAmountCurrentYearRemainToPay.text = formatWithPrecision(0.0)
            receiptDetails.AmountDueCurrentYear?.let {
                remainingToPay += it
                amountCurrentYear += it
                mBinding.txtTaxAmountCurrentYearRemainToPay.text = formatWithPrecision(it)
            }

            mBinding.txtTaxAmountCurrentYearToBePaid.text = formatWithPrecision(0.0)
            amountCurrentYear.let {
                amountOfTaxImposed += it
                mBinding.txtTaxAmountCurrentYearToBePaid.text = formatWithPrecision(it)
            }
            //endregion

            //region AmountOfTaxImposed
            amountOfTaxImposed.let {
                mBinding.txtTotalAmounTaxCollected.text = formatWithPrecision(it)
            }
            //endregion

            //region AmountOfThisPayment
            amountOfThisPayment.let {
                mBinding.txtAmountOfThisPay.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it, mBinding.txtAmountInWords)
            }
            //endregion

            //region Remaining to pay
            remainingToPay.let {
                if (receiptDetails.pmtmodecode?.toUpperCase(Locale.getDefault()) == Constant.PaymentMode.CHEQUE.toString()) {
                    if (amountOfThisPayment > 0) {
                        mBinding.txtRemainingPay.text = formatWithPrecision(it)
                    } else {
                        val chequeNote = mBinding.txtRemainingPay.context.getString(R.string.subject_to_cheque_clearance)
                        mBinding.txtRemainingPay.text = "${formatWithPrecision(it)} " +
                                "(${chequeNote} ${formatWithPrecision(receiptDetails.AmountofThisPayment)})"
                        mBinding.txtNoteLable.visibility = View.VISIBLE
                        mBinding.txtTaxNoticeNote.visibility = View.VISIBLE
                        receiptDetails.ChequeNote?.let {
                            mBinding.txtTaxNoticeNote.text = it
                        }
                    }
                } else {
                    mBinding.txtRemainingPay.text = formatWithPrecision(it)
                    mBinding.txtNoteLable.visibility = View.GONE
                    mBinding.txtTaxNoticeNote.visibility = View.GONE
                }
            }
            //endregion


            receiptDetails.GeneratedBy?.let {
                mBinding.txtCollectedBy.text = it
            }

            receiptDetails.PrintCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
 //                   mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
 //                   mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getReceiptPrintFlag(receiptDetails.advrecdid!!.toInt(), mBinding.btnPrint)
            }
        }

    }
}