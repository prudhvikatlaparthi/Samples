package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.AdvertisementTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemAdvertisementTaxNoticeBinding
import com.sgs.citytax.model.AdvertisementTaxNoticeDetails
import com.sgs.citytax.util.*
import java.util.*

class AdvertisementTaxNoticeViewHolder(val mBinding: ItemAdvertisementTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(taxDetails: AdvertisementTaxNoticeResponse, iClickListener: IClickListener) {
        bindTaxNoticeDetails(taxDetails.advertisementTaxNoticeDetails[0],taxDetails)

        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })
        }
    }


    private fun bindTaxNoticeDetails(
        taxDetails: AdvertisementTaxNoticeDetails?,
        advertisementTaxNoticeResponse: AdvertisementTaxNoticeResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = advertisementTaxNoticeResponse.orgData
        )
        mBinding.txtTaxationYearLabel.text = String.format("%s%s", getString(R.string.taxation_year), getString(R.string.colon))
        mBinding.txtSectorLabel.text = String.format("%s%s", getString(R.string.sector), getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        if (taxDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            taxDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
  //                  mBinding.txtPrintCounts.text = it.toString() //TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
  //                  mBinding.txtPrintCounts.text = "" //TODO on requirement of Onsite Team, hiding duplicate print counts
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
            taxDetails.businessOwner?.let {
                if (it.contentEquals(";"))
                    it.replace(";", "\n")
                mBinding.txtBusinessOwner.text = it
            }
            taxDetails.sycoTaxId?.let {
                mBinding.txtSycoTaxID.text = it
            }
            taxDetails.zone?.let {
                mBinding.txtArdt.text = it
            }
            taxDetails.sector?.let {
                mBinding.txtSector.text = it
            }
            taxDetails.plot?.let {
                mBinding.txtSection.text = it
            }
            taxDetails.block?.let {
                mBinding.txtLot.text = it
            }
            taxDetails.doorNo?.let {
                mBinding.txtParcel.text = it
            }
            taxDetails.billingCycle?.let {
                mBinding.txtBillingCycle.text = it
            }
            taxDetails.advertisementTypeName?.let {
                mBinding.txtNatureOfAdvertisement.text = it
            }
            taxDetails.rate?.let {
                mBinding.txtRate.text = getTariffWithCurrency(it)
            }
            taxDetails.quantity?.let {
                mBinding.txtNoOfAdvertisementPanels.text = it.toString()
            }
            taxDetails.length?.let {
                mBinding.txtLength.text = formatWithPrecisionCustomDecimals(it.toString(),false,3)
            }
            taxDetails.wdth?.let {
                mBinding.txtWidth.text = formatWithPrecisionCustomDecimals(it.toString(),false,3)
            }
            taxDetails.area?.let {
                mBinding.txtArea.text = formatWithPrecisionCustomDecimals(it.toString(),false,3)
            }
            taxDetails.totalArea?.let {
                mBinding.txtTotalArea.text = formatWithPrecisionCustomDecimals(it.toString(),false,3)
            }
            taxDetails.invoiceAmount?.let {
                mBinding.txtAmountOfTaxInvoice.text = formatWithPrecision(it)
            }
            var totalDueAmount: Double? = 0.0

            mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(0.0)
            taxDetails.amountDueCurrentYear?.let {
                mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it)
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