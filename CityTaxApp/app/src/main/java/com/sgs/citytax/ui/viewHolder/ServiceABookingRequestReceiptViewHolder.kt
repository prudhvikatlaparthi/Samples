package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ServiceRequestBookingReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemServiceBookingRequestReceiptBinding
import com.sgs.citytax.model.ServiceBookingAdvanceReceiptTable
import com.sgs.citytax.util.*
import com.sgs.citytax.util.CommonLogicUtils.checkNUpdateQRCodeNotes
import java.util.*

class ServiceABookingRequestReceiptViewHolder(val mBinding: ItemServiceBookingRequestReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {


    fun bind(serviceRequestBookingReceiptResponse: ServiceRequestBookingReceiptResponse, iClickListener: IClickListener) {

        bindReceiptDetails(serviceRequestBookingReceiptResponse.bookingAdvanceReceiptTable[0],serviceRequestBookingReceiptResponse)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, serviceRequestBookingReceiptResponse)
                }
            })
        }
    }

    private fun bindReceiptDetails(
        receiptDetails: ServiceBookingAdvanceReceiptTable?,
        serviceRequestBookingReceiptResponse: ServiceRequestBookingReceiptResponse
    ) {
        checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = serviceRequestBookingReceiptResponse.orgData
        )
        val addressLabel = mBinding.titleAddressLabel.context.getString(R.string.title_address)
        mBinding.titleAddressLabel.text = String.format("%s%s", addressLabel, getString(R.string.colon))

        val sector = mBinding.txtSectorLabel.context.getString(R.string.sector)
        mBinding.txtSectorLabel.text = String.format("%s%s", sector, getString(R.string.colon))

        val state = mBinding.titleStateLabel.context.getString(R.string.state)
        mBinding.titleStateLabel.text = String.format("%s%s", state, getString(R.string.colon))

        val city = mBinding.titleCityLabel.context.getString(R.string.city)
        mBinding.titleCityLabel.text = String.format("%s%s", city, getString(R.string.colon))

        val email = mBinding.txtEmailIDLabel.context.getString(R.string.email)
        mBinding.txtEmailIDLabel.text = String.format("%s%s", email, getString(R.string.colon))

        val taxableMatter = mBinding.txtTaxableMatterLabel.context.getString(R.string.taxable_matter)
        mBinding.txtTaxableMatterLabel.text = String.format("%s%s", taxableMatter, getString(R.string.colon))

       /* val collectBy = mBinding.txtCollectedByLabel.context.getString(R.string.collected_by)
        mBinding.txtCollectedByLabel.text = String.format("%s%s", collectBy, getString(R.string.colon))*/
        val chequeNo = mBinding.txtChequeNumber.context.getString(R.string.cheque_number)
        mBinding.txtChequeNumber.text = String.format("%s%s", chequeNo, getString(R.string.colon))

        val taxPayer = mBinding.txtCompanyLabel.context.getString(R.string.asset_txt_tax_payer_name)
        val company = mBinding.txtCompanyLabel.context.getString(R.string.company_name)
        mBinding.txtCompanyLabel.text = String.format("%s%s%s", taxPayer, "/", company)

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        if (receiptDetails != null) {

            if (receiptDetails.printCouts != null && receiptDetails.printCouts!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                mBinding.txtPrintCounts.text = taxNoticeDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
//                mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

         /*   receiptDetails.serviceReqNo?.let {
                mBinding.txtQuittanceNo.text = it.toString()
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.BOOKING_ADVANCE, it.toString(), receiptDetails.sycoTaxID, "", "", receiptDetails.bookingRequestId.toString()))
            }*/

            if (receiptDetails.advanceReceivedId == 0)
            {
                receiptDetails.serviceReqNo?.let {
                    mBinding.txtQuittanceNo.text = it.toString()
                    mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.SERVICE_BOOKING, it.toString(), receiptDetails.sycoTaxID, "", "", receiptDetails.bookingRequestId.toString()))
                }
            }
            else
            {
                receiptDetails.advanceReceivedId?.let {
                    mBinding.txtQuittanceNo.text = it.toString()
                    mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.BOOKING_ADVANCE, it.toString(), receiptDetails.sycoTaxID, "", "", receiptDetails.bookingRequestId.toString()))
                }
            }

            receiptDetails.bookingRequestId?.let {
                mBinding.txtBookingRequestNumber.text = it.toString()
            }
            receiptDetails.bookingRequestDate?.let {
                mBinding.txtDateOfBooking.text = formatDisplayDateTimeInMillisecond(it)
            }
            receiptDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it.toString()
            }
            receiptDetails.sycoTaxID?.let {
                mBinding.llBusinessSycoTaxID.visibility = View.VISIBLE
                mBinding.txtSycoTaxID.text = it
            }
            receiptDetails.citizenSycoTaxId?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }
            receiptDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }
            receiptDetails.businessName?.let {
                mBinding.txtCompanyName.text = it
            }
            receiptDetails.phoneNo?.let {
                mBinding.txtContact.text = it
            }

            receiptDetails.serviceReqDate?.let {
                mBinding.txtDateOfBooking.text =getDate(it, DateTimeTimeZoneMillisecondFormat, displayDateTimeTimeFormat)
            }

            receiptDetails.serviceReqNo?.let {
                mBinding.txtBookingRequestNumber.text = it.toString()
            }

            receiptDetails.email?.let {
                mBinding.txtEmail.text = it.toString()
            }


            //region Address
            var address: String? = ""

            if (!receiptDetails.state.isNullOrEmpty()) {
                mBinding.txtState.text = receiptDetails.state
                address += receiptDetails.state
            } else {
                mBinding.txtState.text = ""
                address += ""
            }

            if (!receiptDetails.city.isNullOrEmpty()) {
                mBinding.txtCity.text = receiptDetails.city
                if(address!!.isNotEmpty())
                    address += ","
                address += receiptDetails.city
            } else {
                mBinding.txtCity.text = ""
                address += ""
            }

            //region Zone
            if (!receiptDetails.zone.isNullOrEmpty()) {
                mBinding.txtArdt.text = receiptDetails.zone
                if(address!!.isNotEmpty())
                    address += ","
                address += receiptDetails.zone
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }
            //endregion

            //region Sector
            if (!receiptDetails.sector.isNullOrEmpty()) {
                mBinding.txtSector.text = receiptDetails.sector
                if(address!!.isNotEmpty())
                    address += ","
                address += receiptDetails.sector
            } else {
                mBinding.txtSector.text = ""
                address += ""
            }
            //endregion

            //region plot
            if (!receiptDetails.plot.isNullOrEmpty()) {
                mBinding.txtSection.text = receiptDetails.plot
                if(address!!.isNotEmpty())
                    address += ","
                address += receiptDetails.plot
            } else {
                mBinding.txtSection.text = ""
                address += ""
            }
            //endregion

            //region block
            if (!receiptDetails.block.isNullOrEmpty()) {
                if(address!!.isNotEmpty())
                    address += ","
                address += receiptDetails.block
                mBinding.txtLot.text = receiptDetails.block
            } else {
                mBinding.txtLot.text = ""
                address += ""
            }
            //endregion

            //region door no
            if (!receiptDetails.doorNo.isNullOrEmpty()) {
                mBinding.txtParcel.text = receiptDetails.doorNo
                if(address!!.isNotEmpty())
                    address += ","
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

            /*receiptDetails.paymentMode?.let {
                mBinding.txtPaymentMode.text = it
            }*/

            /*//region Check and wallet
            receiptDetails.walletTransactionNo?.let{
                mBinding.llWalletTransactionNumber.visibility = View.VISIBLE
                mBinding.txtReferanceTransactionNumber.text =it.toString()
            }

            receiptDetails.chequeNumber?.let {
                mBinding.llChequeNumber.visibility = View.VISIBLE
                mBinding.txtChequeNumber.text = it.toString()
            }

            receiptDetails.bankName?.let {
                mBinding.llBankName.visibility = View.VISIBLE
                mBinding.txtChequeBankName.text = it
            }

            receiptDetails.chequeDate?.let {
                mBinding.llChequeDate.visibility = View.VISIBLE
                mBinding.txtChequeDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            //endregion*/

            receiptDetails.serviceType?.let {
                mBinding.txtServiceCategory.text = it
            }

            receiptDetails.area?.let {
                mBinding.txtServiceReqQuantity.text = formatWithPrecisionCustomDecimals(it.toString(),false,3)
            }

            receiptDetails.rate?.let {
                if(receiptDetails.unit!=null) {
                    mBinding.txtServiceTariff.text = getTariffWithCurrency(it)
                }
                else{
                    mBinding.txtServiceTariff.text= formatWithPrecision(it)
                }
            }

            receiptDetails.serviceSubType?.let {
                mBinding.txtServiceCategoryType.text = it
            }

            receiptDetails.advanceAmount?.let {
                mBinding.txtServiceAdvance.text = formatWithPrecision(it)
            }

            receiptDetails.estimatedAmount?.let {
                mBinding.txtEstimatedAmount.text = formatWithPrecision(it)
              //  mBinding.txtTotalEstimatedServiceAmount.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it.toDouble(), mBinding.txtAmountInWords)
            }
            /*  val totalDepositBalance = securityDeposit?.minus(amountOfThisBooking?: BigDecimal.ZERO)

              totalDepositBalance?.let {
              }*/


            receiptDetails.createdBy?.let {
                mBinding.txtCreatedBy.text = it
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(0, mBinding.btnPrint, receiptDetails.productCode)
            }
        }

    }

}