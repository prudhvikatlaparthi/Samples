package com.sgs.citytax.ui.viewHolder

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.BookingRequestReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemBookingRequestDetailsBinding
import com.sgs.citytax.databinding.ItemBookingRequestReceiptBinding
import com.sgs.citytax.model.BookingRequestDetails
import com.sgs.citytax.model.BookingRequestReceiptDetails
import com.sgs.citytax.util.*
import java.util.*

class BookingRequestReceiptViewHolder(val mBinding: ItemBookingRequestReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {

    private var totalBookingDeposit: Double = 0.0
    private var totalSecurityDeposit: Double = 0.0
    private var totalEstimatedAmount: Double = 0.0

    fun bind(receiptDetails: BookingRequestReceiptResponse, iClickListener: IClickListener) {

        bindBookingDetails(receiptDetails)
        bindReceiptDetails(receiptDetails.bookingRequestReceiptDetails[0],receiptDetails)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, receiptDetails)
                }
            })
        }
    }

    private fun bindReceiptDetails(
        receiptDetails: BookingRequestReceiptDetails?,
        bookingRequestReceiptResponse: BookingRequestReceiptResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = bookingRequestReceiptResponse.orgData
        )
        val addressLabel = mBinding.titleAddressLabel.context.getString(R.string.title_address)
        mBinding.titleAddressLabel.text = String.format("%s%s", addressLabel, getString(R.string.colon))

        val phoneNumber = mBinding.txtPhoneNumberLabel.context.getString(R.string.receipt_phone_number)
        mBinding.txtPhoneNumberLabel.text = String.format("%s%s", phoneNumber, getString(R.string.colon))
        mBinding.txtEmailLabel.text = String.format("%s%s", getString(R.string.email), getString(R.string.colon))

        val sector = mBinding.txtSectorLabel.context.getString(R.string.sector)
        mBinding.txtSectorLabel.text = String.format("%s%s", sector, getString(R.string.colon))

        val state = mBinding.titleStateLabel.context.getString(R.string.state)
        mBinding.titleStateLabel.text = String.format("%s%s", state, getString(R.string.colon))

        val city = mBinding.titleCityLabel.context.getString(R.string.city)
        mBinding.titleCityLabel.text = String.format("%s%s", city, getString(R.string.colon))

        val taxPayer = mBinding.txtCompanyLabel.context.getString(R.string.asset_txt_tax_payer_name)
        val company = mBinding.txtCompanyLabel.context.getString(R.string.company_name)
        mBinding.txtCompanyLabel.text = String.format("%s%s%s", taxPayer, "/", company)

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

            receiptDetails.bookingRequestDate?.let {
                mBinding.txtDateOfBooking.text = formatDisplayDateTimeInMillisecond(it)
            }

            receiptDetails.bookingRequestId?.let {
                mBinding.txtBookingNumber.text = it.toString()
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.BOOKING_REQUEST, it.toString(), receiptDetails.sycoTaxId))
            }
            receiptDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it.toString()
            }
            receiptDetails.sycoTaxId?.let {
                mBinding.llBusinessSycoTax.visibility = View.VISIBLE
                mBinding.txtSycoTaxID.text = it
            }
            receiptDetails.accountName?.let {
                mBinding.txtCompanyName.text = it
            }
            receiptDetails.citizenSycoTaxId?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }
            receiptDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }
            receiptDetails.mobile?.let {
                mBinding.txtContactPhone.text = it
            }
            receiptDetails.email?.let {
                mBinding.txtContactEmail.text = it
            }

            //region Address
            var address: String? = ""

            if (!receiptDetails.state.isNullOrEmpty()) {
                mBinding.txtState.text = receiptDetails.state
                address += receiptDetails.state
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }

            if (!receiptDetails.city.isNullOrEmpty()) {
                mBinding.txtCity.text = receiptDetails.city
                address += receiptDetails.city
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }

            //region Zone
            if (!receiptDetails.zone.isNullOrEmpty()) {
                mBinding.txtArdt.text = receiptDetails.zone
                address += receiptDetails.zone
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }
            //endregion

            //region Sector
            if (!receiptDetails.sector.isNullOrEmpty()) {
                mBinding.txtSector.text = receiptDetails.sector
                address += receiptDetails.sector
                address += ","
            } else {
                mBinding.txtSector.text = ""
                address += ""
            }
            //endregion

            //region plot
            if (!receiptDetails.plot.isNullOrEmpty()) {
                mBinding.txtSection.text = receiptDetails.plot
                address += receiptDetails.plot
                address += ","
            } else {
                mBinding.txtSection.text = ""
                address += ""
            }
            //endregion

            //region block
            if (!receiptDetails.block.isNullOrEmpty()) {
                address += receiptDetails.block
                mBinding.txtLot.text = receiptDetails.block
                address += ","
            } else {
                mBinding.txtLot.text = ""
                address += ""
            }
            //endregion

            //region door no
            if (!receiptDetails.doorNo.isNullOrEmpty()) {
                mBinding.txtParcel.text = receiptDetails.doorNo
                address += receiptDetails.doorNo
            } else {
                mBinding.txtParcel.text = ""
                address += ""
            }
            //endregion


            address?.let {
                mBinding.txtAddress.text = it
            }

            //endregion

            totalEstimatedAmount.let {
                mBinding.txtTotalEstimatedRentAmount.text = formatWithPrecision(it)
            }

            totalBookingDeposit.let {
                mBinding.txtTotalBookingDeposit.text = formatWithPrecision(it)
            }


            totalSecurityDeposit.let {
                mBinding.txtTotalSecurityDeposit.text = formatWithPrecision(it)
            }

            val totalDepositAmount = totalBookingDeposit.plus(totalSecurityDeposit)
            totalDepositAmount?.let {
                mBinding.txtEstimatedBookingRequest.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it, mBinding.txtAmountInWords)

            }

            receiptDetails.bookingRequestBy?.let {
                mBinding.txtCreatedBy.text = it
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
//                getReceiptPrintFlag(receiptDetails.bookingRequestId!!, mBinding.btnPrint)
            }
        }
    }

    private fun bindBookingDetails(requestDetails: BookingRequestReceiptResponse) {
        val bookingRequestDetails: ArrayList<BookingRequestDetails> = requestDetails.bookingRequestDetails
        val header = requestDetails.bookingRequestReceiptDetails[0]
        if (bookingRequestDetails.isNotEmpty()) {
            mBinding.llBookingDetails.removeAllViews()

            for (requestDetail in bookingRequestDetails) {
                val bookingDetailsBinding: ItemBookingRequestDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context)
                        , R.layout.item_booking_request_details, mBinding.llBookingDetails, false)


                if(!requestDetail.contractTenurePeriod.isNullOrEmpty()){
                    if(header.allowPeriodicInvoice=="Y"){
                      bookingDetailsBinding.lltContractTenure.visibility=View.VISIBLE
                      bookingDetailsBinding.txtContractTenure.text= requestDetail?.contractTenurePeriod
                    }
                }
                requestDetail.assetCategory?.let {
                    bookingDetailsBinding.txtBookingCategory.text = it
                }
                requestDetail.assetNo?.let {
                    bookingDetailsBinding.txtBookingAssetId.text = it.toString()
                }
                requestDetail.bookingQuantity?.let {
                    bookingDetailsBinding.txtBookingQuantity.text = it.toString()
                }

                var bookingPeriod: String? = ""

                requestDetail.bookingStartDate?.let {
                    bookingPeriod += formatDisplayDateTimeInMillisecond(it)
                }
                requestDetail.bookingEndDate?.let {
                    bookingPeriod = "$bookingPeriod - ${formatDisplayDateTimeInMillisecond(it)}"
                }

                bookingPeriod?.let {
                    bookingDetailsBinding.txtBookingPeriod.text = it
                }

                requestDetail.estimatedBookingAmount?.let {
                    bookingDetailsBinding.txtEstimatedRentAmount.text = formatWithPrecision(it)
                    totalEstimatedAmount += it
                }

                requestDetail.durationRate?.let {
                    bookingDetailsBinding.txtDurationRate.text = getTariffWithCurrency(it)
                }

                requestDetail.distanceRate?.let {
                    bookingDetailsBinding.txtDistanceRate.text = getTariffWithCurrency(it)
                }

                requestDetail.bookingDistance?.let {
                    bookingDetailsBinding.txtBookingDistance.text = it.toString()
                }

                requestDetail.bookingAdvance?.let {
                    bookingDetailsBinding.txtBookingDeposit.text = formatWithPrecision(it)
                    totalBookingDeposit += it
                }

                requestDetail.securityDeposit?.let {
                    bookingDetailsBinding.txtSecurityDeposit.text = formatWithPrecision(it)
                    totalSecurityDeposit += it
                }


                mBinding.llBookingDetails.addView(bookingDetailsBinding.root)

            }
        }

    }

}