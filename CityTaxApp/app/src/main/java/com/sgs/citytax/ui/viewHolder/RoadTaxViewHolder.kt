package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.RoadTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemRoadTaxNoticeBinding
import com.sgs.citytax.model.RoadTaxNoticeDetails
import com.sgs.citytax.util.*
import java.util.*

class RoadTaxViewHolder(val mBinding: ItemRoadTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(roadTaxNoticeResponse: RoadTaxNoticeResponse, iClickListener: IClickListener) {


        bindRoadTaxDetails(roadTaxNoticeResponse.roadTaxNoticeDetails[0], roadTaxNoticeResponse)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, roadTaxNoticeResponse)
                }
            })
        }
    }

    private fun bindRoadTaxDetails(
        roadTaxNoticeDetails: RoadTaxNoticeDetails?,
        roadTaxNoticeResponse: RoadTaxNoticeResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = roadTaxNoticeResponse.orgData
        )
        mBinding.txtSectorLabel.text = String.format("%s%s", getString(R.string.sector), getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

        if (roadTaxNoticeDetails != null) {

            if (roadTaxNoticeDetails.printCounts != null && roadTaxNoticeDetails.printCounts!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
 //               mBinding.txtPrintCounts.text = roadTaxNoticeDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
 //               mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

            roadTaxNoticeDetails.product?.let {
                mBinding.txtTaxNoticeHeader.text = mBinding.txtTaxNoticeHeader.context.getString(R.string.place_holder_tax_notice_header, it)
            }

            mBinding.txtNoticeNo.text = getString(R.string.hyphen)
            roadTaxNoticeDetails.noticeReferanceNo?.let {
                mBinding.txtNoticeNo.text = it
            }

            roadTaxNoticeDetails.taxInvoiceId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString(), roadTaxNoticeDetails.sycotaxId
                        ?: ""))
            }
            roadTaxNoticeDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = "$it"
            }
            roadTaxNoticeDetails.taxInvoiceDate?.let {
                mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(it)
            }
            roadTaxNoticeDetails.businessName?.let {
                mBinding.txtBusinessName.text = it
            }

            roadTaxNoticeDetails.businessOwners?.let {
                val businessOwners: String = it
                if (businessOwners.contains(";")) {
                    businessOwners.replace(";", "\n")
                    mBinding.txtBusinessOwner.text = businessOwners
                } else
                    mBinding.txtBusinessOwner.text = businessOwners
            }

            roadTaxNoticeDetails.sycotaxId?.let {
                mBinding.txtSycoTaxID.text = it
            }
            roadTaxNoticeDetails.zone?.let {
                mBinding.txtArdt.text = it
            }
            roadTaxNoticeDetails.sector?.let {
                mBinding.txtSector.text = it
            }
            roadTaxNoticeDetails.plot?.let {
                mBinding.txtSection.text = it
            }
            roadTaxNoticeDetails.block?.let {
                mBinding.txtLot.text = it
            }
            roadTaxNoticeDetails.doorNo?.let {
                mBinding.txtParcel.text = it
            }
            roadTaxNoticeDetails.billingCycle?.let {
                mBinding.txtBillingCycle.text = it
            }
            roadTaxNoticeDetails.product?.let {
                mBinding.txtTaxType.text = it
            }
            roadTaxNoticeDetails.invoiceAmount?.let {
                mBinding.txtAmountOfTaxInvoice.text = formatWithPrecision(it)
            }
            var totalDueAmount: Double? = 0.0

            mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(0.0)
            roadTaxNoticeDetails.amountDueForCurrentYear?.let {
                if (it > 0.0)
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it) 
                else
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(0.0)
            roadTaxNoticeDetails.amountDueAnteriorYear?.let {
                if (it > 0.0)
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it) 
                else
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfPreviousYear.text = formatWithPrecision(0.0)
            roadTaxNoticeDetails.amountDuePreviousYear?.let {
                if (it > 0.0)
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it) 
                else
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            roadTaxNoticeDetails.penaltyDue?.let {
                if (it > 0.0)
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(it) 
                else
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            // region Total due Amount

            /*     if (roadTaxNoticeDetails.amountDueForCurrentYear != 0.0 && roadTaxNoticeDetails.amountDuePreviousYear != 0.0
                         && roadTaxNoticeDetails.amountDueAnteriorYear != 0.0
                         && roadTaxNoticeDetails.penaltyDue != 0.0) {
                     totalDueAmount = roadTaxNoticeDetails.amountDueForCurrentYear
                             ?: 0.0.plus(roadTaxNoticeDetails.amountDuePreviousYear ?: 0.0)

                     totalDueAmount += roadTaxNoticeDetails.amountDueAnteriorYear ?: 0.0
                     totalDueAmount += roadTaxNoticeDetails.penaltyDue ?: 0.0*/
            totalDueAmount?.let {
                mBinding.txtTotalDueAmount.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }

            //endregion
            roadTaxNoticeDetails.generatedBy?.let {
                mBinding.txtGeneratedBy.text = it
            }
            roadTaxNoticeDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(it, 0)
            }

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(roadTaxNoticeDetails.taxInvoiceId!!, mBinding.btnPrint)
            }
        }
    }

}
