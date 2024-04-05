package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ROPTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemRopTaxNoticeBinding
import com.sgs.citytax.model.ROPTaxNoticeDetails
import com.sgs.citytax.util.*
import com.sgs.citytax.util.CommonLogicUtils.checkNUpdateQRCodeNotes
import java.util.*

class ROPTaxViewHolder(val mBinding: ItemRopTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(taxDetails: ROPTaxNoticeResponse, iClickListener: IClickListener?) {

        bindROP(taxDetails.ropTaxNoticeDetails[0],taxDetails)

        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })
        }

    }

    private fun bindROP(ropTaxNoticeDetails: ROPTaxNoticeDetails?, taxDetails: ROPTaxNoticeResponse) {
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport
        checkNUpdateQRCodeNotes(qrCodeWrapper = mBinding.qrCodeWrapper, orgDataList = taxDetails.orgData)

        //region Binding Values
        if (ropTaxNoticeDetails != null) {

            if (ropTaxNoticeDetails.printCounts != null && ropTaxNoticeDetails.printCounts!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                mBinding.txtPrintCounts.text = ropTaxNoticeDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
 //               mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            //region QrCode
            mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, ropTaxNoticeDetails.taxInvoiceId.toString(), ropTaxNoticeDetails.sycotaxId
                    ?: ""))
            //endregion

            //region BusinessStartDate
            if (!ropTaxNoticeDetails.startDate.isNullOrEmpty())
                mBinding.txtStartDate.text = displayFormatDate(ropTaxNoticeDetails.startDate)
            else
                mBinding.txtStartDate.text = "-"
            //endregion

            //region TaxInvoiceId
            mBinding.txtNoticeNo.text = getString(R.string.hyphen)
            ropTaxNoticeDetails.noticeReferenceNo?.let {
                mBinding.txtNoticeNo.text = it
            }
            //endregion

            //region Taxation Year
            if (ropTaxNoticeDetails.taxationYear != 0)
                mBinding.txtTaxationYear.text = "${ropTaxNoticeDetails.taxationYear}"
            else
                mBinding.txtTaxationYear.text = "0"
            //endregion

            //region Date of Taxation
            if (!ropTaxNoticeDetails.taxInvoiceDate.isNullOrEmpty())
                mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(ropTaxNoticeDetails.taxInvoiceDate)
            else
                mBinding.txtDateOfTaxation.text = ""
            //endregion

            //region for Billingcycle code
            if(!ropTaxNoticeDetails.billingCycleCode.isNullOrEmpty()){
                if(ropTaxNoticeDetails.billingCycleCode=="DAYS"){
                    mBinding.tvBussinessOwnerName.text=getString(R.string.merchant_name)
                    mBinding.tvbBusinessOwnerPhone.text= getString(R.string.merchant_phone)
                    mBinding.lytBusinessName.visibility=View.GONE
                    mBinding.lytNotDaily.visibility=View.GONE
                    mBinding.dailyLLt.visibility=View.VISIBLE
                    mBinding.llyNotDaily.visibility=View.GONE
                    mBinding.llytNonDailyTaxationdate.visibility=View.GONE
                    mBinding.tvTaxNoticeNo.text=getString(R.string.rop_txt_notice_no_daily)
                }
                else{
                    mBinding.tvBussinessOwnerName.text=getString(R.string.receipt_business_owner_name)
                    mBinding.tvbBusinessOwnerPhone.text= getString(R.string.receipt_business_owner_phone)
                    mBinding.lytBusinessName.visibility=View.VISIBLE
                    mBinding.lytNotDaily.visibility=View.VISIBLE
                    mBinding.dailyLLt.visibility=View.GONE
                    mBinding.llyNotDaily.visibility=View.VISIBLE
                    mBinding.llytNonDailyTaxationdate.visibility=View.VISIBLE
                    mBinding.tvTaxNoticeNo.text=getString(R.string.rop_txt_notice_no)
                }
            }

            //region Business name
            if (!ropTaxNoticeDetails.businessName.isNullOrEmpty())
                mBinding.txtBusinessName.text = ropTaxNoticeDetails.businessName
            else
                mBinding.txtBusinessName.text = ""
            //endregion

            //region Business Owner
            mBinding.lytBusinessOwnername.visibility = View.GONE
            if (!ropTaxNoticeDetails.businessOwners.isNullOrEmpty()) {
                val businessOwner = ropTaxNoticeDetails.businessOwners
                if (!businessOwner.isNullOrEmpty() && businessOwner.contains(";"))
                    mBinding.txtBusinessOwner.text = businessOwner.replace(";", "\n")
                else
                    mBinding.txtBusinessOwner.text = ropTaxNoticeDetails.businessOwners
                mBinding.lytBusinessOwnername.visibility = View.VISIBLE
            }
            //endregion

            //region Business Owner phone
            mBinding.lytbBusinessOwnerPhone.visibility = View.GONE
            if (!ropTaxNoticeDetails.ownersNumber.isNullOrEmpty()) {
                val businessOwnerNumber = ropTaxNoticeDetails.ownersNumber
                if (!businessOwnerNumber.isNullOrEmpty() && businessOwnerNumber.contains(";"))
                    mBinding.txtBusinessOwnerPhone.text = businessOwnerNumber.replace(";", "\n")
                else
                    mBinding.txtBusinessOwnerPhone.text = ropTaxNoticeDetails.ownersNumber
                mBinding.lytbBusinessOwnerPhone.visibility = View.VISIBLE
            }
            //endregion

            //region Master
            if (!ropTaxNoticeDetails.market.isNullOrEmpty()) {
                    mBinding.txtMarketName.text = ropTaxNoticeDetails.market
            }else{
                mBinding.txtMarketName.visibility=View.GONE
            }
            //endregion

            //region Occupancy Name
            if (!ropTaxNoticeDetails.occupancyName.isNullOrEmpty())
                mBinding.txtNatureOfOccupancy.text = ropTaxNoticeDetails.occupancyName
            else
                mBinding.txtNatureOfOccupancy.text = ""
            //endregion

            //region Syco tax Id
            if (!ropTaxNoticeDetails.sycotaxId.isNullOrEmpty())
                mBinding.txtSycoTaxID.text = ropTaxNoticeDetails.sycotaxId
            else
                mBinding.txtSycoTaxID.text = ""
            //endregion

            //region Zone
            if (!ropTaxNoticeDetails.zone.isNullOrEmpty())
                mBinding.txtArdt.text = ropTaxNoticeDetails.zone
            else
                mBinding.txtArdt.text = ""
            //endregion

            //region Sector
            if (!ropTaxNoticeDetails.sector.isNullOrEmpty())
                mBinding.txtSector.text = ropTaxNoticeDetails.sector
            else
                mBinding.txtSector.text = ""
            //endregion

            //region Section
            if (!ropTaxNoticeDetails.plot.isNullOrEmpty())
                mBinding.txtSection.text = ropTaxNoticeDetails.plot
            else
                mBinding.txtSection.text = ""
            //endregion

            //region LOT
            if (!ropTaxNoticeDetails.block.isNullOrEmpty())
                mBinding.txtLot.text = ropTaxNoticeDetails.block
            else
                mBinding.txtLot.text = ""
            //endregion

            //region Parcel
            if (!ropTaxNoticeDetails.doorNo.isNullOrEmpty())
                mBinding.txtParcel.text = ropTaxNoticeDetails.doorNo
            else
                mBinding.txtParcel.text = ""
            //endregion

            // region Billing cycle
            if (!ropTaxNoticeDetails.billingCycle.isNullOrEmpty())
                mBinding.txtBillingCycle.text = ropTaxNoticeDetails.billingCycle
            else
                mBinding.txtBillingCycle.text = ""
            //endregion

            // region Linear length
            if (ropTaxNoticeDetails.length != null && ropTaxNoticeDetails.length != 0.0)
                mBinding.txtLinearLength.text = "${ropTaxNoticeDetails.length}"
            else
                mBinding.txtLinearLength.text = "-"
            //endregion

            // region Number of heads
            if (ropTaxNoticeDetails.numberOfHeads != null && ropTaxNoticeDetails.numberOfHeads != 0.0)
                mBinding.txtNumberOfHeads.text = "${ropTaxNoticeDetails.numberOfHeads}"
            else
                mBinding.txtNumberOfHeads.text = "-"
            //endregion

            // region Tarif

            ropTaxNoticeDetails.rate?.let {
               mBinding.txtTarif.text = getTariffWithCurrency(it)
            }

            //endregion

            /*  // region Tax amount for invoice
              if (ropTaxNoticeDetails.invoiceAmounnt != null && ropTaxNoticeDetails.invoiceAmounnt != 0.0)
                  mBinding.txtAmountOfTaxInvoice.text = formatWithPrecision(ropTaxNoticeDetails.invoiceAmounnt)
              else
                  mBinding.txtAmountOfTaxInvoice.text = "0.0"
              //endregion*/

            var totalDueAmount: Double? = 0.0

            // region Tax amount for current year
            mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(0.0)
            if (ropTaxNoticeDetails.amountDueForCurrentYear != null && ropTaxNoticeDetails.amountDueForCurrentYear != 0.0) {
                if (ropTaxNoticeDetails.amountDueForCurrentYear!! > 0.0)
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(ropTaxNoticeDetails.amountDueForCurrentYear)
                else
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(ropTaxNoticeDetails.amountDueForCurrentYear)
                totalDueAmount = totalDueAmount?.plus(ropTaxNoticeDetails.amountDueForCurrentYear
                        ?: 0.0)
            }
            //endregion

            // region RAR Anterior year
            mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(0.0)
            if (ropTaxNoticeDetails.amountDueAnteriorYear != null && ropTaxNoticeDetails.amountDueAnteriorYear != 0.0) {
                if (ropTaxNoticeDetails.amountDueAnteriorYear!! > 0.0)
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(ropTaxNoticeDetails.amountDueAnteriorYear)
                else
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(ropTaxNoticeDetails.amountDueAnteriorYear)
                totalDueAmount = totalDueAmount?.plus(ropTaxNoticeDetails.amountDueAnteriorYear
                        ?: 0.0)

            }
            //endregion

            // region RAR previous year
            mBinding.txtRAROfPreviousYear.text = formatWithPrecision(0.0)
            if (ropTaxNoticeDetails.amountDuePreviousYear != null && ropTaxNoticeDetails.amountDuePreviousYear != 0.0) {
                if (ropTaxNoticeDetails.amountDuePreviousYear!! > 0.0)
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(ropTaxNoticeDetails.amountDuePreviousYear)
                else
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(ropTaxNoticeDetails.amountDuePreviousYear)
                totalDueAmount = totalDueAmount?.plus(ropTaxNoticeDetails.amountDuePreviousYear
                        ?: 0.0)

            }
            //endregion

            // region Penalties
            mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            if (ropTaxNoticeDetails.penaltyDue != null && ropTaxNoticeDetails.penaltyDue != 0.0) {
                if (ropTaxNoticeDetails.penaltyDue!! > 0.0)
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(ropTaxNoticeDetails.penaltyDue)
                else
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(ropTaxNoticeDetails.penaltyDue)

                totalDueAmount = totalDueAmount?.plus(ropTaxNoticeDetails.penaltyDue ?: 0.0)

            }
            //endregion

            // region Total due Amount
            /*      if (ropTaxNoticeDetails.amountDueForCurrentYear != 0.0 && ropTaxNoticeDetails.amountDuePreviousYear != 0.0
                          && ropTaxNoticeDetails.amountDueAnteriorYear != 0.0
                          && ropTaxNoticeDetails.penaltyDue != 0.0) {
                      totalDueAmount = ropTaxNoticeDetails.amountDueForCurrentYear
                              ?: 0.0.plus(ropTaxNoticeDetails.amountDuePreviousYear ?: 0.0)

                      totalDueAmount += ropTaxNoticeDetails.amountDueAnteriorYear ?: 0.0
                      totalDueAmount += ropTaxNoticeDetails.penaltyDue ?: 0.0*/


            //endregion
            if (totalDueAmount != null && totalDueAmount != 0.0) {
                mBinding.txtTotalDueAmount.text = formatWithPrecision(totalDueAmount)
               getAmountInWordsWithCurrency(totalDueAmount,mBinding.txtAmountInWords)
            } else {
                mBinding.txtAmountInWords.text = ""
            }

            if (!ropTaxNoticeDetails.generatedBy.isNullOrEmpty())
                mBinding.txtGeneratedBy.text = ropTaxNoticeDetails.generatedBy


            if (!ropTaxNoticeDetails.note.isNullOrEmpty())
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(ropTaxNoticeDetails.note
                        ?: "", 0)
            else
                mBinding.txtTaxNoticeNote.text = ""

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(ropTaxNoticeDetails.taxInvoiceId!!, mBinding.btnPrint)
            }
        }
        //endregion
    }

}