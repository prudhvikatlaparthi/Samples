package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.BookingAdvanceReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemBookingAdvanceReceiptBinding
import com.sgs.citytax.model.BookingAdvanceReceiptTable
import com.sgs.citytax.util.*
import java.util.*

class BookingAdvanceReceiptViewHolder (val mBinding:ItemBookingAdvanceReceiptBinding): RecyclerView.ViewHolder(mBinding.root){


    fun bind(bookingAdvanceReceiptResponse: BookingAdvanceReceiptResponse, iClickListener: IClickListener){

        bindReceiptDetails(bookingAdvanceReceiptResponse.bookingAdvanceReceiptTable[0],bookingAdvanceReceiptResponse)

        if (iClickListener != null){
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, bookingAdvanceReceiptResponse)
                }
            })
        }
    }

    private fun bindReceiptDetails(
        receiptDetails: BookingAdvanceReceiptTable?,
        bookingAdvanceReceiptResponse: BookingAdvanceReceiptResponse
    ){
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = bookingAdvanceReceiptResponse.orgData
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

        if (receiptDetails != null){
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())
            receiptDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
                }
            }

            receiptDetails.advanceReceivedId?.let {
                mBinding.txtQuittanceNo.text = it.toString()
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.BOOKING_ADVANCE,it.toString(),receiptDetails.sycoTaxID,"","",receiptDetails.bookingRequestId.toString()))
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
                mBinding.llBusinessSycoTax.visibility = View.VISIBLE
                mBinding.txtSycoTaxID.text = it
            }
            receiptDetails.businessName?.let {
                mBinding.txtCompanyName.text = it
            }
            receiptDetails.phoneNo?.let {
                mBinding.txtContact.text = it
            }
            receiptDetails.email?.let {
                mBinding.txtEmail.text = it
            }
            receiptDetails.citizenSycoTaxId?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }
            receiptDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
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

            receiptDetails.walletTransactionNumber?.let{
                mBinding.llWalletTransactionNumber.visibility = View.VISIBLE
                mBinding.txtReferanceTransactionNumber.text =it.toString()
            }
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

            receiptDetails.depositAmount?.let {
                mBinding.txtAmountOfThisBooking.text = formatWithPrecision(it)
                mBinding.txtDepositBalance.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it.toDouble(),mBinding.txtAmountInWords)
            }

            receiptDetails.bookingDeposit?.let{
                mBinding.txtBookingDeposit.text = formatWithPrecision(it)
            }

            receiptDetails.securityDeposit?.let {
                mBinding.txtSecurityDeposit.text = formatWithPrecision(it)
            }

            receiptDetails.generateBy?.let {
                mBinding.txtBookingBy.text = it
            }

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
//                getReceiptPrintFlag(receiptDetails.advanceReceivedId!!, mBinding.btnPrint)
            }
        }

    }

}