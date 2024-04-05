package com.sgs.citytax.ui.viewHolder

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.TicketPaymentReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemReceiptImpoundDetailsBinding
import com.sgs.citytax.databinding.ItemReceiptPenaltyDetailsBinding
import com.sgs.citytax.databinding.ItemReceiptViolationDetailsBinding
import com.sgs.citytax.databinding.ItemTrafficTicketPaymentReceiptBinding
import com.sgs.citytax.model.TicketImpoundDetails
import com.sgs.citytax.model.TicketPenaltyDetails
import com.sgs.citytax.model.TicketReceiptDetails
import com.sgs.citytax.model.TicketViolationDetails
import com.sgs.citytax.util.*
import com.sgs.citytax.util.CommonLogicUtils.checkNUpdateQRCodeNotes
import java.math.BigDecimal
import java.util.*

class TicketPaymentReceiptViewHolder(
    val mBinding: ItemTrafficTicketPaymentReceiptBinding,
    val fromScreenType: Int,
    private val stopPrintAPI: Boolean = false
) : RecyclerView.ViewHolder(mBinding.root) {

    var ticketImposedAmount = 0.0
    var amountOfThisPayment = 0.0
    var remainingPay = 0.0

    fun bind(response: TicketPaymentReceiptResponse, iClickListener: IClickListener?) {


        bindViolationDetails(response.violationDetails)
        bindImpoundDetails(response.impoundDetails)
        bindPenaltyDetails(response.penaltyDetails)
        bindReceiptDetails(response.receiptDetails[0],response)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, response)
                }
            })
        }
    }

    private fun bindReceiptDetails(
        receiptDetails: TicketReceiptDetails?,
        response: TicketPaymentReceiptResponse
    ) {
        checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = response.orgData
        )
        val collectBy = mBinding.txtCollectedByLabel.context.getString(R.string.collected_by)
        mBinding.txtCollectedByLabel.text = String.format("%s%s", collectBy, getString(R.string.colon))

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        if (receiptDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            if (receiptDetails.printCounts != null && receiptDetails.printCounts!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                mBinding.txtPrintCounts.text = receiptDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
//                mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

            receiptDetails.referanceNo?.let {
                mBinding.txtQuittanceNo.text = it
            }
            receiptDetails.advanceDate?.let {
                mBinding.txtRecoveryDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            receiptDetails.paymentMode?.let {
                mBinding.txtPaymentMethod.text = it
            }
            receiptDetails.advanceReceivedId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_RECEIPT, it.toString()))
            }

            receiptDetails.walletTransactionNumber?.let {
                mBinding.llWalletTransactionNumber.visibility = View.VISIBLE
                mBinding.txtReferanceTransactionNumber.text = it
            }

            /*ticketImposedAmount.let {
                mBinding.txtAmountOfTicketImposed.text = formatWithPrecision(it)
            }*/

            /* amountOfThisPayment.let {
                 mBinding.txtAmountOfThisPayment.text = formatWithPrecision(it)
                 getAmountInWordsWithCurrency(it, mBinding.txtAmountInWords)
             }*/
            //as to get the paid amount we are getting the same in total amount. Hence commented the above
            receiptDetails.totalAmount?.let {
                mBinding.txtAmountOfThisPayment.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it, mBinding.txtAmountInWords)
            }

            //remainingPay = ticketImposedAmount.minus(amountOfThisPayment)
            //comment above as we get the currentDue directly from impoudment list table
            remainingPay.let {
                mBinding.txtRemainingPay.text = formatWithPrecision(it)
            }

            receiptDetails.collectedBy?.let {
                mBinding.txtCollectedBy.text = it
            }


            if(receiptDetails.paymentModeCode == Constant.PaymentMode.CHEQUE.name){
                mBinding.receiptChequeDetailsWrapper.chequeRootWrapper.visibility = View.VISIBLE
                mBinding.receiptChequeDetailsWrapper.apply {

                    llBankName.isVisible = receiptDetails.bankName?.let {
                        txtChequeBankName.text = it
                        true
                    } ?: false

                    llChequeNumber.isVisible = receiptDetails.chequeNumber?.let {
                        txtChequeNumber.text = it
                        true
                    } ?: false

                    llChequeDate.isVisible = receiptDetails.chequeDate?.let {
                        txtChequeDate.text = displayFormatDate(it)
                        true
                    } ?: false

                    llChequeAmount.isVisible = receiptDetails.chequeAmount?.let {
                        txtChequeAmount.text = formatWithPrecision(it)
                        it > BigDecimal.ZERO
                    } ?: false

                    llChequeStatus.isVisible = receiptDetails.chequeStatus?.let {
                        txtChequeStatus.text = it
                        true
                    } ?: false
                }
            }
            if(fromScreenType == Constant.TaxReceipt.TICKET_PAYMENT_TRANSACTION.Type){
                mBinding.btnPrint.isVisible = stopPrintAPI
            } else if (!stopPrintAPI) {
                if (MyApplication.getPrefHelper().isFromHistory == false) {
                    getReceiptPrintFlag(receiptDetails.advanceReceivedId!!, mBinding.btnPrint)
                }
            }
        }
    }

    private fun bindViolationDetails(violationDetails: ArrayList<TicketViolationDetails>?) {
        if (!violationDetails.isNullOrEmpty()) {
            mBinding.llViolationDetails.removeAllViews()

            for (detail in violationDetails) {
                val mViolationBinding: ItemReceiptViolationDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context), R.layout.item_receipt_violation_details, mBinding.llViolationDetails, false)

                mViolationBinding.llViolationD.visibility=View.GONE
                val ticketDate = mViolationBinding.txtTicketDateLabel.context.getString(R.string.ticket_date)
                mViolationBinding.txtTicketDateLabel.text = String.format("%s%s", ticketDate, getString(R.string.colon))

                val violationType = mViolationBinding.txtViolationTypeLabel.context.getString(R.string.violation_type)
                mViolationBinding.txtViolationTypeLabel.text = String.format("%s%s", violationType, getString(R.string.colon))

                val violationClass = mViolationBinding.txtViolationClassLabel.context.getString(R.string.violation_class)
                mViolationBinding.txtViolationClassLabel.text = String.format("%s%s", violationClass, getString(R.string.colon))

                val extraCharges = mViolationBinding.txtExtraChargesLabel.context.getString(R.string.extra_charges)
                mViolationBinding.txtExtraChargesLabel.text = String.format("%s%S", extraCharges, getString(R.string.colon))
                /* val violationDetails = mViolationBinding.txtViolationDetailsLabel.context.getString(R.string.violation_details)
                 mViolationBinding.txtViolationDetailsLabel.text = String.format("%s%s", violationDetails, getString(R.string.colon))
 */
                var totalCharges = 0.0
                detail.referanceNumber?.let {
                    mViolationBinding.txtTicketNumber.text = it
                }

                detail.transactionDate?.let {
                    mViolationBinding.txtTicketDate.text = displayFormatDate(it)
                }
                detail.vehicleNumber?.let {
                    mBinding.txtVehicleNo.text = it
                    mViolationBinding.txtVehicleNo.text = it
                }
                detail.vehicleSycoTaxID?.let {
                    mBinding.txtVehicleSycoTaxID.text = it
                   // mViolationBinding.txtVehicleSycoTaxID.text = it
                }

                detail.violationType?.let {
                    mViolationBinding.txtViolationType.text = it
                }

                detail.violationClass?.let {
                    mViolationBinding.txtViolationClass.text = it
                }

                detail.extraCharge?.let {
                    mViolationBinding.txtExtraCharges.text = formatWithPrecision(it)
                    totalCharges += it
                }

                detail.violationDetails?.let {
                    mViolationBinding.txtViolationDetails.text = it

                }
                detail.fineAmount?.let {
                    mViolationBinding.txtFineAmount.text = formatWithPrecision(it)
                    ticketImposedAmount += it
                }

                detail.totalCharges?.let {
                    mViolationBinding.txtTotalCharges.text = formatWithPrecision(it)
                }

                detail.settledAmount?.let {
                    mViolationBinding.txtSettledAmount.text = formatWithPrecision(it)
                    amountOfThisPayment += it
                }
//this condition is to check weather the navigating screen is from trackon transaction history or payment history
                if (fromScreenType == Constant.TaxReceipt.TICKET_PAYMENT_TRANSACTION.Type) {
                    detail.dueAfterSettlement?.let {
                        remainingPay = it
                    }
                } else {
                    detail.currentDue?.let {
                        remainingPay = it
                    }
                }


                detail.vehicleNumber?.let {
                    mBinding.txtVehicleNo.text = it
                    //  mImpoundBinding.txtVehicleNo.text = it
                    mBinding.llVehicleDetail.visibility = View.VISIBLE
                }

                detail.vehicleSycoTaxID?.let {
                    mBinding.txtVehicleSycoTaxID.text = it
                    //  mImpoundBinding.txtVehicleNo.text = it
                }

                detail.violatorCitizenSycotaxID?.let {
                    if(detail.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.name){
                        mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                        mBinding.txtCitizenSycoTaxID.text =  detail.violatorCitizenSycotaxID
                    }
                }

                detail.violatorCitizenCardNo?.let {
                    if(detail.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.name){
                        mBinding.llCardNumber.visibility = View.VISIBLE
                        mBinding.txtIDCardNumber.text =  detail.violatorCitizenCardNo
                    }
                }
                mBinding.llViolationDetails.addView(mViolationBinding.root)

            }


        } else {
            mBinding.violationHeading.visibility = View.GONE
        }
    }

    private fun bindImpoundDetails(impoundDetails: ArrayList<TicketImpoundDetails>?) {
        if (!impoundDetails.isNullOrEmpty()) {
            mBinding.llImpoundDetails.removeAllViews()
            for (detail in impoundDetails) {
                val mImpoundBinding: ItemReceiptImpoundDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context), R.layout.item_receipt_impound_details, mBinding.llImpoundDetails, false)

                mImpoundBinding.llViolationD.visibility=View.GONE
                val violationType = mImpoundBinding.txtViolationTypeLabel.context.getString(R.string.violation_type)
                mImpoundBinding.txtViolationTypeLabel.text = String.format("%s%s", violationType, getString(R.string.colon))

                val violationClass = mImpoundBinding.txtViolationClassLabel.context.getString(R.string.violation_class)
                mImpoundBinding.txtViolationClassLabel.text = String.format("%s%s", violationClass, getString(R.string.colon))

                val impoundType = mImpoundBinding.impoundTypeLabel.context.getString(R.string.impond_type)
                mImpoundBinding.impoundTypeLabel.text = String.format("%s%s", impoundType, getString(R.string.colon))

                val towingCraneType = mImpoundBinding.towingCraneTypeLabel.context.getString(R.string.towing_crane_type)
                mImpoundBinding.towingCraneTypeLabel.text = String.format("%s%s", towingCraneType, getString(R.string.colon))

                val towingTripCount = mImpoundBinding.towingTripCountLabel.context.getString(R.string.towing_trip_count)
                mImpoundBinding.towingTripCountLabel.text = String.format("%s%s", towingTripCount, getString(R.string.colon))

                val towingCharge = mImpoundBinding.towingChargeLabel.context.getString(R.string.towing_charge)
                mImpoundBinding.towingChargeLabel.text = String.format("%s%s", towingCharge, getString(R.string.colon))

                val extraCharges = mImpoundBinding.extraChargesLabel.context.getString(R.string.extra_charges)
                mImpoundBinding.extraChargesLabel.text = String.format("%s%s", extraCharges, getString(R.string.colon))

               /* val violationDetails = mImpoundBinding.txtViolationDetailsLabel.context.getString(R.string.violation_details)
                mImpoundBinding.txtViolationDetailsLabel.text = String.format("%s%s", violationDetails, getString(R.string.colon))*/


                detail.referanceNumber?.let {
                    mImpoundBinding.txtImpoundNumber.text = it
                }
                detail.transactionDate?.let {
                    mImpoundBinding.txtImpoundDate.text = displayFormatDate(it)
                }
                detail.violationType?.let {
                    mImpoundBinding.txtViolationType.text = it
                }

                detail.violationClass?.let {
                    mImpoundBinding.txtViolationClass.text = it
                }

                detail.violationDetails?.let {
                    mImpoundBinding.txtViolationDetails.text = it
                }
                detail.fineAmount?.let {
                    mImpoundBinding.txtFineAmount.text = formatWithPrecision(it)
                    mImpoundBinding.txtTotalFineAmount.text = formatWithPrecision(it)
                    ticketImposedAmount += it
                }
                detail.impoundType?.let {
                    mImpoundBinding.txtImpoundType.text = it
                }
                detail.impSubType?.let {
                    mImpoundBinding.txtImpoundmentSubType.text = it
                }
                detail.impoundCharge?.let {
                    mImpoundBinding.txtImpoundCharge.text = formatWithPrecision(it)
                    ticketImposedAmount += it
                }

                detail.towingCraneType?.let {
                    mImpoundBinding.txtTowingCraneType.text = it
                }

                detail.towingTripCount?.let {
                    mImpoundBinding.txtTowingTripCount.text = it
                }

                detail.towingCharge?.let {
                    mImpoundBinding.txtTowingCharge.text = formatWithPrecision(it)
                    ticketImposedAmount += it.toDouble()
                }

                detail.extraCharge?.let {
                    mImpoundBinding.txtExtraCharges.text = formatWithPrecision(it)
                    ticketImposedAmount += it
                }

                detail.settledAmount?.let {
                    mImpoundBinding.txtSettledAmount.text = formatWithPrecision(it)
                    amountOfThisPayment += it
                }

                detail.idSycoTax?.let {
                    mBinding.llBusinessSycoTax.isVisible = true
                    mBinding.txtSycoTaxID.text = it
                }
                if (detail.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code) {
                    mBinding.llVehicleDetail.visibility = View.GONE
                    mBinding.llAnimalDetail.isVisible = true

                    detail.goodsOwner?.let {
                        mBinding.llAnimalOwner.isVisible = true
                        mBinding.txtAnimalOwner.text = it
                    }

                  /*  detail.idSycoTax?.let {
                        mBinding.llBusinessSycoTax.isVisible = true
                        mBinding.txtSycoTaxID.text = it
                    }*/

                    detail.citizenIDSycotax?.let {
                        mBinding.llCitizenSycoTax.isVisible = true
                        mBinding.txtCitizenSycoTaxID.text = it
                    }

                    detail.idCardNumber?.let {
                        mBinding.llCardNumber.isVisible = true
                        mBinding.txtIDCardNumber.text = it
                    }


                    detail.violationCharge?.let {
                        mImpoundBinding.llViolationCharge.isVisible = true
                        mImpoundBinding.txtViolationCharge.text = formatWithPrecision(it)
                        ticketImposedAmount += it
                    }

                    mImpoundBinding.txtTotalFineAmount.text = formatWithPrecision(ticketImposedAmount)

                } else {

                    detail.vehicleNumber?.let {
                        mBinding.llVehicleDetail.visibility = View.VISIBLE
                        mBinding.txtVehicleNo.text = it
                        //  mImpoundBinding.txtVehicleNo.text = it
                    }

                    detail.vehicleSycoTaxID?.let {
                        mBinding.txtVehicleSycoTaxID.text = it
                        //  mImpoundBinding.txtVehicleNo.text = it
                    }
                }
                mImpoundBinding.txtTotalFineAmount.text = formatWithPrecision(ticketImposedAmount)

                //this condition is to check weather the navigating screen is from trackon transaction history or payment history
                if (fromScreenType == Constant.TaxReceipt.TICKET_PAYMENT_TRANSACTION.Type) {
                    detail.dueAfterSettlement?.let {
                        remainingPay = it
                    }
                } else {
                    detail.currentDue?.let {
                        remainingPay = it
                    }
                }

                mBinding.llImpoundDetails.addView(mImpoundBinding.root)

            }
        } else {
            mBinding.impoundHeading.visibility = View.GONE
        }
    }

    private fun bindPenaltyDetails(penaltyDetails: ArrayList<TicketPenaltyDetails>?) {
        if (!penaltyDetails.isNullOrEmpty()) {
            mBinding.llPenaltyDetails.removeAllViews()
            for (detail in penaltyDetails) {
                val mPenaltyBinding: ItemReceiptPenaltyDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context), R.layout.item_receipt_penalty_details, mBinding.llImpoundDetails, false)

                val violationDetails = mPenaltyBinding.txtViolationDetailsLabel.context.getString(R.string.violation_details)
                mPenaltyBinding.txtViolationDetailsLabel.text = String.format("%s%s", violationDetails, getString(R.string.colon))

                detail.referanceNumber?.let {
                    mPenaltyBinding.txtImpoundNumber.text = it
                }
                detail.violationType?.let {
                    mPenaltyBinding.txtAppliedOn.text = it
                }
                detail.violationDetails?.let {
                    mPenaltyBinding.txtViolationDetails.text = it
                }
                detail.taxDate?.let {
                    mPenaltyBinding.txtPenaltyDate.text = displayFormatDate(it)
                }
                detail.fineAmount?.let {
                    mPenaltyBinding.txtPenaltyAmount.text = formatWithPrecision(it)
                    ticketImposedAmount += it
                }
                detail.settledAmount?.let {
                    mPenaltyBinding.txtSettledAmount.text = formatWithPrecision(it)
                    amountOfThisPayment += it
                }
                detail.vehicleNumber?.let {
                    mBinding.txtVehicleNo.text = it
                }
                if (fromScreenType == Constant.TaxReceipt.TICKET_PAYMENT_TRANSACTION.Type) {
                    detail.dueAfterSettlement?.let {
                        remainingPay = it
                    }
                } else {
                    detail.currentDue?.let {
                        remainingPay = it
                    }
                }

                mBinding.llPenaltyDetails.addView(mPenaltyBinding.root)
            }
        } else {
            mBinding.penalityHeading.visibility = View.GONE
        }
    }


}