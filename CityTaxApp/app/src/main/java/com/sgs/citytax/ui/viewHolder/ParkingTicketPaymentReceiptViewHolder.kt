package com.sgs.citytax.ui.viewHolder

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ParkingTicketPaymentReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemParkingTicketPaymentReceiptBinding
import com.sgs.citytax.databinding.ItemReceiptPenaltyDetailsBinding
import com.sgs.citytax.databinding.ReceiptParkingDetailsBinding
import com.sgs.citytax.model.ParkingDetails
import com.sgs.citytax.model.TicketPenaltyDetails
import com.sgs.citytax.model.TicketReceiptDetails
import com.sgs.citytax.util.*
import java.util.*

class ParkingTicketPaymentReceiptViewHolder(val mBinding: ItemParkingTicketPaymentReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {

    var ticketImposedAmount = 0.0
    var amountOfThisPayment = 0.0
    var remainingPay = 0.0

    fun bind(response: ParkingTicketPaymentReceiptResponse, iClickListener: IClickListener?) {


        bindParkingDetails(response.parkingDetails)
        bindPenaltyDetails(response.penaltyDetails)
        bindReceiptDetails(response.receiptDetails[0],response.parkingDetails,response)

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
        parkingDetails: ArrayList<ParkingDetails>?,
        response: ParkingTicketPaymentReceiptResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
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
 //               mBinding.txtPrintCounts.text = receiptDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
 //               mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
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
            receiptDetails.vehicleNumber?.let {
                mBinding.txtVehicleNo.text = it
            }
            receiptDetails.vehicleSycotaxID?.let {
                mBinding.txtVehicleSycoTaxID.text = it
            }

            receiptDetails.walletTransactionNumber?.let {
                mBinding.llWalletTransactionNumber.visibility = View.VISIBLE
                mBinding.txtReferanceTransactionNumber.text = it
            }

            ticketImposedAmount.let {
                mBinding.txtAmountOfTicketImposed.text = formatWithPrecision(it)
            }

            amountOfThisPayment.let {
                mBinding.txtAmountOfThisPayment.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it, mBinding.txtAmountInWords)
            }

            //remainingPay = ticketImposedAmount.minus(amountOfThisPayment)

            remainingPay.let {
                mBinding.txtRemainingPay.text = formatWithPrecision(it)
            }

            receiptDetails.collectedBy?.let {
                mBinding.txtCollectedBy.text = it
            }

            if (!parkingDetails.isNullOrEmpty()) {
            parkingDetails[0].amt.let {
                mBinding.txtAmountImposed.text = formatWithPrecision(it)
            }
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getReceiptPrintFlag(receiptDetails.advanceReceivedId!!.toInt(), mBinding.btnPrint)
            }
        }
    }

    private fun bindParkingDetails(parkingDetails: ArrayList<ParkingDetails>?) {
        if (!parkingDetails.isNullOrEmpty()) {
            mBinding.llParkingDetails.removeAllViews()
            for (detail in parkingDetails) {
                val mParkingDetailsBinding: ReceiptParkingDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context)
                        , R.layout.receipt_parking_details, mBinding.llParkingDetails, false)

                val ticketNumber = mParkingDetailsBinding.txtTicketNoLabel.context.getString(R.string.parking_number)
                mParkingDetailsBinding.txtTicketNoLabel.text = String.format("%s%s", ticketNumber, getString(R.string.colon))

                val ticketDate = mParkingDetailsBinding.txtTicketDateLabel.context.getString(R.string.parking_date)
                mParkingDetailsBinding.txtTicketDateLabel.text = String.format("%s%s", ticketDate, getString(R.string.colon))



                detail.referanceNumber?.let {
                    mParkingDetailsBinding.txtTicketNumber.text = it.toString()
                }

                detail.transactionDate?.let {
                    mParkingDetailsBinding.txtTicketDate.text = displayFormatDate(it)
                }

                detail.parkingType?.let {
                    mParkingDetailsBinding.txtParkingType.text = it
                }
                detail.parkingPlace?.let {
                    mParkingDetailsBinding.txtParkingPalce.text = it
                }


                detail.parkingStartDate?.let {
                    mParkingDetailsBinding.txtParkingStartDate.text = getDate(it, DateTimeTimeZoneMillisecondFormat, parkingdisplayDateTimeTimeSecondFormat)
                }

                detail.parkingEndDate?.let {
                    mParkingDetailsBinding.txtParkingEndDate.text = getDate(it, DateTimeTimeZoneMillisecondFormat, parkingdisplayDateTimeTimeSecondFormat)

                }
                detail.settledAmount?.let {
                    mParkingDetailsBinding.txtSettledAmount.text = formatWithPrecision(it)
                    amountOfThisPayment += it
                }

                detail.amt?.let {
                    mParkingDetailsBinding.txtAmount.text = formatWithPrecision(it)
                    ticketImposedAmount = it
                }
                detail.currentDue?.let {
                    remainingPay = it
                }

                mBinding.llParkingDetails.addView(mParkingDetailsBinding.root)

            }
        } else {
            mBinding.parkingHeading.visibility = View.GONE
        }
    }

    private fun bindPenaltyDetails(penaltyDetails: ArrayList<TicketPenaltyDetails>?) {
        if (!penaltyDetails.isNullOrEmpty()) {
            mBinding.llPenaltyDetails.removeAllViews()
            for (detail in penaltyDetails) {
                val mPenaltyBinding: ItemReceiptPenaltyDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context)
                        , R.layout.item_receipt_penalty_details, mBinding.llPenaltyDetails, false)

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

                mBinding.llPenaltyDetails.addView(mPenaltyBinding.root)
            }
        } else {
            mBinding.penalityHeading.visibility = View.GONE
        }
    }


}