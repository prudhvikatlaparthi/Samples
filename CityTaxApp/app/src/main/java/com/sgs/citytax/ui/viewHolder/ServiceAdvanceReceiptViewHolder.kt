package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ServiceBookingAdvanceReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemServiceBookingAdvanceReceiptBinding
import com.sgs.citytax.model.ServiceBookingAdvanceReceiptTable
import com.sgs.citytax.util.*
import com.sgs.citytax.util.CommonLogicUtils.checkNUpdateQRCodeNotes
import java.util.*

class ServiceAdvanceReceiptViewHolder(val mBinding: ItemServiceBookingAdvanceReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {


    fun bind(serviceBookingAdvanceReceiptResponse: ServiceBookingAdvanceReceiptResponse, iClickListener: IClickListener) {

        bindReceiptDetails(serviceBookingAdvanceReceiptResponse.bookingAdvanceReceiptTable[0],serviceBookingAdvanceReceiptResponse)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, serviceBookingAdvanceReceiptResponse)
                }
            })
        }
    }

    private fun bindReceiptDetails(
        receiptDetails: ServiceBookingAdvanceReceiptTable?,
        serviceBookingAdvanceReceiptResponse: ServiceBookingAdvanceReceiptResponse
    ) {
        checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = serviceBookingAdvanceReceiptResponse.orgData
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

        val collectBy = mBinding.txtCollectedByLabel.context.getString(R.string.collected_by)
        mBinding.txtCollectedByLabel.text = String.format("%s%s", collectBy, getString(R.string.colon))
        val chequeNo = mBinding.txtChequeNumber.context.getString(R.string.cheque_number)
        mBinding.txtChequeNumber.text = String.format("%s%s", chequeNo, getString(R.string.colon))

        val taxPayer = mBinding.txtCompanyLabel.context.getString(R.string.txt_tax_payer_name)
        val company = mBinding.txtCompanyLabel.context.getString(R.string.company_name)
        mBinding.txtCompanyLabel.text = String.format("%s%s%s", taxPayer, "/", company)

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        if (receiptDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            receiptDetails.advanceReceivedId?.let {
                mBinding.txtQuittanceNo.text = it.toString()
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.BOOKING_ADVANCE, it.toString(), receiptDetails.sycoTaxID, "", "", receiptDetails.bookingRequestId.toString()))
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
                mBinding.txtDateOfBooking.text = getDate(it, DateTimeTimeZoneMillisecondFormat, parkingdisplayDateTimeTimeSecondFormat)
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

            receiptDetails.paymentMode?.let {
                mBinding.txtPaymentMode.text = it
            }

            //region Check and wallet
            receiptDetails.walletTransactionNo?.let{
                mBinding.llWalletTransactionNumber.visibility = View.VISIBLE
                mBinding.txtReferanceTransactionNumber.text =it.toString()
            }

            receiptDetails.chqno?.let {
                mBinding.llChequeNumber.visibility = View.VISIBLE
                mBinding.txtChequeNumber.text = it.toString()
            }

            receiptDetails.bnkname?.let {
                mBinding.llBankName.visibility = View.VISIBLE
                mBinding.txtChequeBankName.text = it
            }

            /*receiptDetails.chequeDate?.let {
                mBinding.llChequeDate.visibility = View.VISIBLE
                mBinding.txtChequeDate.text = formatDisplayDateTimeInMillisecond(it)
            }*/
            //endregion

            receiptDetails.depositAmount?.let {
                mBinding.txtAmountOfThisBooking.text = formatWithPrecision(it)
              //  mBinding.txtDepositBalance.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it.toDouble(), mBinding.txtAmountInWords)
            }
            /*  val totalDepositBalance = securityDeposit?.minus(amountOfThisBooking?: BigDecimal.ZERO)

              totalDepositBalance?.let {
              }*/


            receiptDetails.collectedBy?.let {
                mBinding.txtBookingBy.text = it
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getReceiptPrintFlag(receiptDetails.advanceReceivedId!!, mBinding.btnPrint)
            }
        }

    }

}