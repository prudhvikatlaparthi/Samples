package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ServiceTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemServiceTaxNoticeBinding
import com.sgs.citytax.model.ServiceTaxNoticeReceiptDetails
import com.sgs.citytax.util.*
import com.sgs.citytax.util.CommonLogicUtils.checkNUpdateQRCodeNotes
import java.util.*

class ServiceTaxNoticeViewHolder(val mBinding: ItemServiceTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(taxDetails: ServiceTaxNoticeResponse, iClickListener: IClickListener?) {

        bindReceiptDetails(taxDetails.receiptDetails[0],taxDetails)

        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })
        }
    }

    private fun bindReceiptDetails(
        receiptDetails: ServiceTaxNoticeReceiptDetails?,
        taxDetails: ServiceTaxNoticeResponse
    ) {
        checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = taxDetails.orgData
        )

        val taxableMatter = mBinding.txtTaxableMatterLabel.context.getString(R.string.taxable_matter)
        mBinding.txtTaxableMatterLabel.text = String.format("%s%s", taxableMatter, getString(R.string.colon))

        if (receiptDetails != null) {
            if (receiptDetails.printCouts != null && receiptDetails.printCouts!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                mBinding.txtPrintCounts.text = taxNoticeDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
//                mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

            mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            receiptDetails.taxationDate?.let {
                mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(it)
            }
            receiptDetails.referanceNo?.let {
                mBinding.txtNoticeNo.text = it
            }
            receiptDetails.serviceRequestDate?.let {
                mBinding.txtServiceRequestDate.text = getDate(it, DateTimeTimeZoneMillisecondFormat, parkingdisplayDateTimeTimeSecondFormat)
            }
            receiptDetails.taxInvoiceID?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString()))
            }
            receiptDetails.serviceRequestNo?.let {
                mBinding.txtServiceRequestNumber.text = it.toString()
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
            receiptDetails.accountName?.let {
                mBinding.txtCompanyName.text = it
            }
            receiptDetails.mobile?.let {
                mBinding.txtContact.text = it
            }
            receiptDetails.email?.let {
                mBinding.txtEmail.text = it
            }

            //region Address
            var address: String? = ""

            receiptDetails.state?.let {
                mBinding.txtState.text = it
                address += it
                address += ","
            }
            receiptDetails.city?.let {
                mBinding.txtCity.text = it
                address += it
                address += ","
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
            if (!address.isNullOrEmpty()) {
                mBinding.txtAddress.text = address
            } else
                mBinding.txtAddress.text = ""

            //endregion

            receiptDetails.serviceType?.let {
                mBinding.txtServiceCategory.text = it
            }
            receiptDetails.serviceSubType?.let {
                mBinding.txtServiceCategoryType.text = it
            }
            receiptDetails.area?.let {
                mBinding.txtServiceArea.text = "$it" + " " + "${receiptDetails.unit?:""}"
            }

            receiptDetails.serviceAmount?.let {
                mBinding.txtServiceAmount.text = formatWithPrecision(it)
                //servicecharge
                mBinding.txtServiceCharges.text = formatWithPrecision(it)
            }
            receiptDetails.extraCharges?.let {
                mBinding.txtAdditionalCharges.text = formatWithPrecision(it)
            }



            receiptDetails.bookingDeposit?.let {
                mBinding.txtTotalBookingDeposit.text = formatWithPrecision(it)
            }
            receiptDetails.remainingToPay?.let {
                mBinding.txtRemainingPay.text = formatWithPrecision(it)
                //amount of this payment
                mBinding.txtAmountOfThisPayment.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it.toDouble(), mBinding.txtAmountInWords)
                getAmountInWordsWithCurrency(it.toDouble(), mBinding.txtPaymentAmountInWords)
                getAmountInWordsWithCurrency(it.toDouble(), mBinding.txtNoticeSumAmount)
            }
            receiptDetails.generatedBy?.let {
                mBinding.txtGenaratedBy.text = it
            }
            receiptDetails.paymentMode?.let {
                mBinding.txtPaymentMode.text = it
            }
            receiptDetails.walletTransactionNo?.let {
                mBinding.txtReferanceTransactionNumber.text = it
                mBinding.llWalletTransactionNumber.visibility = View.VISIBLE
            }
            receiptDetails.productCode?.let {
                mBinding.txtProductCode.text = it
            }
            receiptDetails.product?.let {
                mBinding.txtProduct.text = it
            }
            receiptDetails.rate?.let {
                mBinding.txtUnitPrice.text = getTariffWithCurrency(it)
            }
            receiptDetails.collectedBy?.let {
                mBinding.txtCollectedBy.text = it
            }

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(receiptDetails.taxInvoiceID!!, mBinding.btnPrint)
            }
        }
    }
}