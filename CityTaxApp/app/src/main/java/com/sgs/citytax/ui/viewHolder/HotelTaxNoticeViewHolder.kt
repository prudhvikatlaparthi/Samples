package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.api.response.HotelTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemHotelTaxNoticeBinding
import com.sgs.citytax.model.HotelTaxNoticeReceiptDetails
import com.sgs.citytax.util.*
import java.util.*

class HotelTaxNoticeViewHolder(val mBinding: ItemHotelTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(receiptDetails: HotelTaxNoticeResponse, iClickListener: IClickListener?) {

        bindReceiptDetails(receiptDetails.receiptDetails[0],receiptDetails)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, receiptDetails)
                }
            })
        }

    }

    private fun bindReceiptDetails(
        taxDetails: HotelTaxNoticeReceiptDetails?,
        receiptDetails: HotelTaxNoticeResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = receiptDetails.orgData
        )
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        if (taxDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())
            taxDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                    mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
 //                   mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }

            taxDetails.taxInvoiceId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString(), taxDetails.sycoTaxId
                        ?: ""))
            }
            taxDetails.startDate?.let {
                mBinding.txtStartDate.text = displayFormatDate(it)
            }
            taxDetails.noticeReferenceNo?.let {
                mBinding.txtNoticeNo.text = it
            }
            taxDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it.toString()
            }
            taxDetails.taxInvoiceDate?.let {
                mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(it)
            }
            taxDetails.businessName?.let {
                mBinding.txtBusinessName.text = it
            }
            taxDetails.businessOwners?.let {
                if (it.contentEquals(";"))
                    it.replace(";", "\n")
                mBinding.txtBusinessOwner.text = it
            }
            taxDetails.sycoTaxId?.let {
                mBinding.txtSycoTaxID.text = it
            }
            taxDetails.profession?.let {
                mBinding.txtProfession.text = it
            }
            taxDetails.phoneNumber?.let {
                mBinding.txtContact.text = it
            }

            //region Address
            var address: String? = ""

            if (!taxDetails.state.isNullOrEmpty()) {
                mBinding.txtState.text = taxDetails.state
                address += taxDetails.state
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }

            if (!taxDetails.city.isNullOrEmpty()) {
                mBinding.txtCity.text = taxDetails.city
                address += taxDetails.city
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }

            //region Zone
            if (!taxDetails.zone.isNullOrEmpty()) {
                mBinding.txtArdt.text = taxDetails.zone
                address += taxDetails.zone
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }
            //endregion

            //region Sector
            if (!taxDetails.sector.isNullOrEmpty()) {
                mBinding.txtSector.text = taxDetails.sector
                address += taxDetails.sector
                address += ","
            } else {
                mBinding.txtSector.text = ""
                address += ""
            }
            //endregion

            //region plot
            if (!taxDetails.plot.isNullOrEmpty()) {
                mBinding.txtSection.text = taxDetails.plot
                address += taxDetails.plot
                address += ","
            } else {
                mBinding.txtSection.text = ""
                address += ""
            }
            //endregion

            //region block
            if (!taxDetails.block.isNullOrEmpty()) {
                address += taxDetails.block
                mBinding.txtLot.text = taxDetails.block
                address += ","
            } else {
                mBinding.txtLot.text = ""
                address += ""
            }
            //endregion

            //region door no
            if (!taxDetails.doorNo.isNullOrEmpty()) {
                mBinding.txtParcel.text = taxDetails.doorNo
                address += taxDetails.doorNo
            } else {
                mBinding.txtParcel.text = ""
                address += ""
            }
            //endregion



            address?.let {
                mBinding.txtAddress.text = it
            }

            //endregion

            taxDetails.product?.let {
                mBinding.txtTaxType.text = it
            }
            taxDetails.hotelName?.let {
                mBinding.txtHotelName.text = it
            }
            taxDetails.noOfRooms?.let {
                mBinding.txtNoOfRooms.text = it.toString()
            }
            taxDetails.star?.let {
                mBinding.txtStarRating.text = it
            }
            taxDetails.rate?.let {
                mBinding.txtTariff.text = getTariffWithCurrency(it)
            }
            taxDetails.billingCycle?.let {
                mBinding.txtBillingCycle.text = it
            }
            taxDetails.roomNight?.let {
                mBinding.txtDeclarationOfRoom.text = it.toString()
            }
            taxDetails.invoiceAmount?.let {
                mBinding.txtTaxAmountOfThisNotice.text = formatWithPrecision(it)
            }
            var totalDueAmount: Double? = 0.0

            taxDetails.amountDueForCurrentYear?.let {
                mBinding.txtAmountOfCurrentDue.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(0.0)
            taxDetails.amountDueAnteriorYear?.let {
                mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfPreviousYear.text = formatWithPrecision(0.0)
            taxDetails.amountDuePreviousYear?.let {
                mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            taxDetails.penaltyDue?.let {
                mBinding.txtAmountOfPenalties.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            totalDueAmount?.let {
                mBinding.txtTotalDueAmount.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it, mBinding.txtAmountInWords)
            }
            taxDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(it, 0)
            }
            taxDetails.generatedBy?.let {
                mBinding.txtGeneratedBy.text = it
            }

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(taxDetails.taxInvoiceId!!, mBinding.btnPrint)
            }

        }
    }
}