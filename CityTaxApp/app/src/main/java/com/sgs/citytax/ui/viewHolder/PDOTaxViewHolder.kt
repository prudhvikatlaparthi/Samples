package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.response.PDOTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemPdoTaxNoticeBinding
import com.sgs.citytax.model.PDOTaxNoticeDetails
import com.sgs.citytax.util.*
import java.util.*

class PDOTaxViewHolder(val mBinding: ItemPdoTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(taxDetails: PDOTaxNoticeResponse, iClickListener: IClickListener?) {

        bindPDO(taxDetails.pdoTaxNoticeDetails[0],taxDetails)

        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })

        }

    }

    private fun bindPDO(pdoTaxNoticeDetails: PDOTaxNoticeDetails?, taxDetails: PDOTaxNoticeResponse) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = taxDetails.orgData
        )
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport
        //region Binding Values
        if (pdoTaxNoticeDetails != null) {

            if (pdoTaxNoticeDetails.printCounts != null && pdoTaxNoticeDetails.printCounts!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
 //               mBinding.txtPrintCounts.text = pdoTaxNoticeDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
 //               mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            //region QrCode
            mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, pdoTaxNoticeDetails.taxInvoiceId.toString(), pdoTaxNoticeDetails.sycotaxId
                    ?: ""))
            //endregion

            //region BusinessStartDate
            if (!pdoTaxNoticeDetails.startDate.isNullOrEmpty())
                mBinding.txtStartDate.text = displayFormatDate(pdoTaxNoticeDetails.startDate)
            else
                mBinding.txtStartDate.text = "-"
            //endregion

            //region TaxInvoiceId
            mBinding.txtNoticeNo.text = getString(R.string.hyphen)
            pdoTaxNoticeDetails.noticeReferenceNo?.let {
                mBinding.txtNoticeNo.text = it
            }
            //endregion

            //region Taxation Year
            if (pdoTaxNoticeDetails.taxationYear != 0)
                mBinding.txtTaxationYear.text = "${pdoTaxNoticeDetails.taxationYear}"
            else
                mBinding.txtTaxationYear.text = "0"
            //endregion

            //region Date of Taxation
            if (!pdoTaxNoticeDetails.taxInvoiceDate.isNullOrEmpty())
                mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(pdoTaxNoticeDetails.taxInvoiceDate)
            else
                mBinding.txtDateOfTaxation.text = ""
            //endregion

            //region Business name
            if (!pdoTaxNoticeDetails.businessName.isNullOrEmpty())
                mBinding.txtBusinessName.text = pdoTaxNoticeDetails.businessName
            else
                mBinding.txtBusinessName.text = ""
            //endregion

            //region Business Owner
            if (!pdoTaxNoticeDetails.businessOwners.isNullOrEmpty()) {
                val businessOwner = pdoTaxNoticeDetails.businessOwners
                if (!businessOwner.isNullOrEmpty() && businessOwner.contains(";"))
                    mBinding.txtBusinessOwner.text = businessOwner.replace(";", "\n")
                else
                    mBinding.txtBusinessOwner.text = pdoTaxNoticeDetails.businessOwners
            }
            //endregion

            //region Occupancy Name
            if (!pdoTaxNoticeDetails.occupancyName.isNullOrEmpty())
                mBinding.txtNatureOfOccupancy.text = pdoTaxNoticeDetails.occupancyName
            else
                mBinding.txtNatureOfOccupancy.text = ""
            //endregion

            //region Syco tax Id
            if (!pdoTaxNoticeDetails.sycotaxId.isNullOrEmpty())
                mBinding.txtSycoTaxID.text = pdoTaxNoticeDetails.sycotaxId
            else
                mBinding.txtSycoTaxID.text = ""
            //endregion

            //region Zone
            if (!pdoTaxNoticeDetails.zone.isNullOrEmpty())
                mBinding.txtArdt.text = pdoTaxNoticeDetails.zone
            else
                mBinding.txtArdt.text = ""
            //endregion

            //region Sector
            if (!pdoTaxNoticeDetails.sector.isNullOrEmpty())
                mBinding.txtSector.text = pdoTaxNoticeDetails.sector
            else
                mBinding.txtSector.text = ""
            //endregion

            //region Section
            if (!pdoTaxNoticeDetails.plot.isNullOrEmpty())
                mBinding.txtSection.text = pdoTaxNoticeDetails.plot
            else
                mBinding.txtSection.text = ""
            //endregion

            //region LOT
            if (!pdoTaxNoticeDetails.block.isNullOrEmpty())
                mBinding.txtLot.text = pdoTaxNoticeDetails.block
            else
                mBinding.txtLot.text = ""
            //endregion

            //region Parcel
            if (!pdoTaxNoticeDetails.doorNo.isNullOrEmpty())
                mBinding.txtParcel.text = pdoTaxNoticeDetails.doorNo
            else
                mBinding.txtParcel.text = ""
            //endregion

            // region Billing cycle
            if (!pdoTaxNoticeDetails.billingCycle.isNullOrEmpty())
                mBinding.txtBillingCycle.text = pdoTaxNoticeDetails.billingCycle
            else
                mBinding.txtBillingCycle.text = ""
            //endregion

            // region  Length
            if (pdoTaxNoticeDetails.length != null && pdoTaxNoticeDetails.length != 0.0)
                mBinding.txtLength.text=formatWithPrecisionCustomDecimals(pdoTaxNoticeDetails.length.toString(),false,3)
            // mBinding.txtLength.text = "${pdoTaxNoticeDetails.length}"
            else
                mBinding.txtLength.text = "-"
            //endregion

            // region  Width
            if (pdoTaxNoticeDetails.width != null && pdoTaxNoticeDetails.width != 0.0)
                mBinding.txtWidth.text =formatWithPrecisionCustomDecimals(pdoTaxNoticeDetails.width.toString(),false,3)
            else
                mBinding.txtWidth.text = "-"
            //endregion

            // region  Area
            if (pdoTaxNoticeDetails.area != null && pdoTaxNoticeDetails.area != 0.0)
                mBinding.txtArea.text = formatWithPrecisionCustomDecimals(pdoTaxNoticeDetails.area.toString(),false,3)
            else
                mBinding.txtArea.text = "-"
            //endregion

            // region Rate
            if (!pdoTaxNoticeDetails.rate.isNullOrEmpty())
                mBinding.txtTariff.text = getTariffWithCurrency(pdoTaxNoticeDetails.rate)
            else
                mBinding.txtTariff.text = "-"
            //endregion

            /* // region Tax amount for invoice
             if (pdoTaxNoticeDetails.invoiceAmounnt != null && pdoTaxNoticeDetails.invoiceAmounnt != 0.0)
                 mBinding.txtAmountOfTaxInvoice.text = formatWithPrecision(pdoTaxNoticeDetails.invoiceAmounnt)
             else
                 mBinding.txtAmountOfTaxInvoice.text = "0.0"
             //endregion*/

            var totalDueAmount: Double? = 0.0

            // region Tax amount for current year
            mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(0.0)
            if (pdoTaxNoticeDetails.amountDueForCurrentYear != null && pdoTaxNoticeDetails.amountDueForCurrentYear != 0.0) {
                if (pdoTaxNoticeDetails.amountDueForCurrentYear!! > 0.0)
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(pdoTaxNoticeDetails.amountDueForCurrentYear)
                else
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(pdoTaxNoticeDetails.amountDueForCurrentYear)
                totalDueAmount = totalDueAmount?.plus(pdoTaxNoticeDetails.amountDueForCurrentYear
                        ?: 0.0)
            }
            //endregion

            // region RAR Anterior year
            mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(0.0)
            if (pdoTaxNoticeDetails.amountDueAnteriorYear != null && pdoTaxNoticeDetails.amountDueAnteriorYear != 0.0) {
                if (pdoTaxNoticeDetails.amountDueAnteriorYear!! > 0.0)
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(pdoTaxNoticeDetails.amountDueAnteriorYear)
                else
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(pdoTaxNoticeDetails.amountDueAnteriorYear)
                totalDueAmount = totalDueAmount?.plus(pdoTaxNoticeDetails.amountDueAnteriorYear
                        ?: 0.0)

            }
            //endregion

            // region RAR previous year
            mBinding.txtRAROfPreviousYear.text = formatWithPrecision(0.0)
            if (pdoTaxNoticeDetails.amountDuePreviousYear != null && pdoTaxNoticeDetails.amountDuePreviousYear != 0.0) {
                if (pdoTaxNoticeDetails.amountDuePreviousYear!! > 0.0)
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(pdoTaxNoticeDetails.amountDuePreviousYear)
                else
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(pdoTaxNoticeDetails.amountDuePreviousYear)
                totalDueAmount = totalDueAmount?.plus(pdoTaxNoticeDetails.amountDuePreviousYear
                        ?: 0.0)

            }
            //endregion

            // region Penalties
            mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            if (pdoTaxNoticeDetails.penaltyDue != null && pdoTaxNoticeDetails.penaltyDue != 0.0) {
                if (pdoTaxNoticeDetails.penaltyDue!! > 0.0)
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(pdoTaxNoticeDetails.penaltyDue)
                else
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(pdoTaxNoticeDetails.penaltyDue)
                totalDueAmount = totalDueAmount?.plus(pdoTaxNoticeDetails.penaltyDue ?: 0.0)

            }
            //endregion

            /*      if (pdoTaxNoticeDetails.amountDueForCurrentYear != 0.0 && pdoTaxNoticeDetails.amountDuePreviousYear != 0.0
                          && pdoTaxNoticeDetails.amountDueAnteriorYear != 0.0
                          && pdoTaxNoticeDetails.penaltyDue != 0.0) {
                      totalDueAmount = pdoTaxNoticeDetails.amountDueForCurrentYear
                              ?: 0.0.plus(pdoTaxNoticeDetails.amountDuePreviousYear ?: 0.0)

                      totalDueAmount += pdoTaxNoticeDetails.amountDueAnteriorYear ?: 0.0
                      totalDueAmount += pdoTaxNoticeDetails.penaltyDue ?: 0.0*/


            //endregion

            //region Amount in words
            if (totalDueAmount != null && totalDueAmount != 0.0) {
                mBinding.txtTotalDueAmount.text = formatWithPrecision(totalDueAmount)
                getAmountInWordsWithCurrency(totalDueAmount,mBinding.txtAmountInWords)
            } else {
                mBinding.txtTotalDueAmount.text = "0.0"
                mBinding.txtAmountInWords.text = ""
            }
            //endregion

            //region Generated By
            if (!pdoTaxNoticeDetails.generatedBy.isNullOrEmpty())
                mBinding.txtGeneratedBy.text = pdoTaxNoticeDetails.generatedBy
            else
                mBinding.txtGeneratedBy.text = SecurityContext().loggedUserID
            //endregion

            //region Note
            if (!pdoTaxNoticeDetails.note.isNullOrEmpty())
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(pdoTaxNoticeDetails.note
                        ?: "", 0)
            else
                mBinding.txtTaxNoticeNote.text = ""
            //endregion

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(pdoTaxNoticeDetails.taxInvoiceId!!, mBinding.btnPrint)
            }
        }
        // region Total due Amount
        //endregion
    }

}