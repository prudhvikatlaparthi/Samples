package com.sgs.citytax.ui.viewHolder

import android.view.LayoutInflater
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.response.CMETaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemCmeTaxNoticeBinding
import com.sgs.citytax.databinding.ItemCmeTaxNoticeVehicleDetailsBinding
import com.sgs.citytax.model.CMETaxNoticeDetails
import com.sgs.citytax.model.CMEVehicleDetails
import com.sgs.citytax.util.*
import java.util.*

class CMETaxViewHolder(val mBinding: ItemCmeTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(taxDetails: CMETaxNoticeResponse, iClickListener: IClickListener?) {

        bindCME(taxDetails.cmeTaxNoticeDetails[0],taxDetails)
        bindVehicles(taxDetails.cmeVehicleDetails)

        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })
        }

    }

    private fun bindCME(cmeTaxNoticeDetails: CMETaxNoticeDetails?, taxDetails: CMETaxNoticeResponse) {
        mBinding.titleAddressLabel.text = String.format("%s%s", getString(R.string.title_address), getString(R.string.colon))
        mBinding.txtSectorLabel.text = String.format("%s%s", getString(R.string.sector), getString(R.string.colon))
        mBinding.titleProfession.text = String.format("%s%s", getString(R.string.profession), getString(R.string.colon))
        mBinding.titleZone.text = String.format("%s%s", getString(R.string.zone), getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = taxDetails.orgData
        )

        if (cmeTaxNoticeDetails != null) {

            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            if (cmeTaxNoticeDetails.printCounts != null && cmeTaxNoticeDetails.printCounts!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
  //              mBinding.txtPrintCounts.text = cmeTaxNoticeDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
  //              mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

            //region Qr Code
            mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, cmeTaxNoticeDetails.taxInvoiceId.toString(), cmeTaxNoticeDetails.sycoTaxId
                    ?: ""))
            //endregion

            //region InvoiceId
            mBinding.txtNoticeNo.text = getString(R.string.hyphen)
            cmeTaxNoticeDetails.noticeReferenceNo?.let {
                mBinding.txtNoticeNo.text = it
            }
            //endregion

            cmeTaxNoticeDetails.startDate?.let {
                mBinding.txtStartDate.text = displayFormatDate(it)
            }

            //region Invoice Date
            if (!cmeTaxNoticeDetails.taxInvoiceDate.isNullOrEmpty())
                mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(cmeTaxNoticeDetails.taxInvoiceDate)
            else
                mBinding.txtDateOfTaxation.text = ""
            //endregion

            //region Taxation year
            if (cmeTaxNoticeDetails.taxationYear != 0)
                mBinding.txtTaxationYear.text = "${cmeTaxNoticeDetails.taxationYear}"
            else
                mBinding.txtTaxationYear.text = ""
            //endregion

            cmeTaxNoticeDetails.businessName?.let {
                mBinding.txtBusinessName.text = it
            }

            cmeTaxNoticeDetails.businessType?.let {
                mBinding.txtBusinessType.text = it
            }

            //region Business Owner
            if (!cmeTaxNoticeDetails.businessOwner.isNullOrEmpty()) {
                val businessOwners = cmeTaxNoticeDetails.businessOwner
                if (!businessOwners.isNullOrEmpty() && businessOwners.contains(";"))
                    mBinding.txtBusinessOwner.text = businessOwners.replace(";", "\n")
                else
                    mBinding.txtBusinessOwner.text = businessOwners
            } else
                mBinding.txtBusinessOwner.text = ""
            //endregion

            //region Sycotax id
            if (!cmeTaxNoticeDetails.sycoTaxId.isNullOrEmpty())
                mBinding.txtSycoTaxID.text = cmeTaxNoticeDetails.sycoTaxId
            else
                mBinding.txtSycoTaxID.text = ""
            //endregion

            //region Business name
            if (!cmeTaxNoticeDetails.businessName.isNullOrEmpty())
                mBinding.txtSocialReason.text = cmeTaxNoticeDetails.businessName
            else
                mBinding.txtSocialReason.text = cmeTaxNoticeDetails.businessName
            //endregion

            cmeTaxNoticeDetails.trnNo?.let {
                mBinding.txtTradeRegisterNo.text = it
            }

            //region IFU number
            if (!cmeTaxNoticeDetails.ifuNumber.isNullOrEmpty())
                mBinding.txtIdentityDocumentNumber.text = cmeTaxNoticeDetails.ifuNumber
            else
                mBinding.txtIdentityDocumentNumber.text = ""
            //endregion

            //region Mobile no
            if (!cmeTaxNoticeDetails.businessMobile.isNullOrEmpty())
                mBinding.txtTelephoneNumber.text = cmeTaxNoticeDetails.businessMobile
            else
                mBinding.txtTelephoneNumber.text = ""
            //endregion

            //region Address
            var address: String? = ""


            cmeTaxNoticeDetails.city?.let {
                address += it
                address += ","
            }

            //region Zone
            if (!cmeTaxNoticeDetails.zone.isNullOrEmpty()) {
                mBinding.txtArdt.text = cmeTaxNoticeDetails.zone
                mBinding.txtZone.text = cmeTaxNoticeDetails.zone
                address += cmeTaxNoticeDetails.zone
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                mBinding.txtZone.text = ""
                address += ""
            }
            //endregion

            //region Sector
            if (!cmeTaxNoticeDetails.sector.isNullOrEmpty()) {
                mBinding.txtSector.text = cmeTaxNoticeDetails.sector
                address += cmeTaxNoticeDetails.sector
                address += ","
            } else {
                mBinding.txtSector.text = ""
                address += ""
            }
            //endregion

            //region plot
            if (!cmeTaxNoticeDetails.plot.isNullOrEmpty()) {
                mBinding.txtSection.text = cmeTaxNoticeDetails.plot
                address += cmeTaxNoticeDetails.plot
                address += ","
            } else {
                mBinding.txtSection.text = ""
                address += ""
            }
            //endregion

            //region block
            if (!cmeTaxNoticeDetails.block.isNullOrEmpty()) {
                address += cmeTaxNoticeDetails.block
                mBinding.txtLot.text = cmeTaxNoticeDetails.block
                address += ","
            } else {
                mBinding.txtLot.text = ""
                address += ""
            }
            //endregion

            //region door no
            if (!cmeTaxNoticeDetails.doorNo.isNullOrEmpty()) {
                mBinding.txtParcel.text = cmeTaxNoticeDetails.doorNo
                address += cmeTaxNoticeDetails.doorNo
                address += ","
            } else {
                mBinding.txtParcel.text = ""
                address += ""
            }
            //endregion

            // region door no
            if (!cmeTaxNoticeDetails.street.isNullOrEmpty()) {
                address += cmeTaxNoticeDetails.street
                address += ","
            } else {
                address += ""
            }
            //endregion
            // region door no
            if (!cmeTaxNoticeDetails.zipCode.isNullOrEmpty()) {
                address += cmeTaxNoticeDetails.zipCode
            } else {
                address += ""
            }
            //endregion

            if (!address.isNullOrEmpty()) {
                mBinding.txtAddress.text = address
            } else
                mBinding.txtAddress.text = ""

            //endregion

            //region Profession
            /*  if (!cmeTaxNoticeDetails.activityClass.isNullOrEmpty())
              mBinding.txtProfession.text = cmeTaxNoticeDetails.activityClass
          else
              mBinding.txtProfession.text = ""*/
            //endregion

            cmeTaxNoticeDetails.product?.let {
                mBinding.txtCmeType.text = it
            }

            //region Class
            if (!cmeTaxNoticeDetails.activityClass.isNullOrEmpty())
                mBinding.txtClass.text = cmeTaxNoticeDetails.activityClass
            else
                mBinding.txtClass.text = ""
            //endregion

            //region TurnOver amount
            if (cmeTaxNoticeDetails.turnOverTax != null && cmeTaxNoticeDetails.turnOverTax != 0.0)
                mBinding.txtCorporateTurnOverAmount.text = formatWithPrecision(cmeTaxNoticeDetails.turnOverTax)
            else
                mBinding.txtCorporateTurnOverAmount.text = "0.0"
            //endregion

            var totalDueAmount: Double? = 0.0

            //region Amount for current year
            if (cmeTaxNoticeDetails.amountDueCurrentYear != null && cmeTaxNoticeDetails.amountDueCurrentYear != 0.0) {

                mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(cmeTaxNoticeDetails.amountDueCurrentYear)

                totalDueAmount = totalDueAmount?.plus(cmeTaxNoticeDetails.amountDueCurrentYear
                        ?: 0.0)
            } else
                mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(0.0)
            //endregion

            //region RAR for anterior year
            if (cmeTaxNoticeDetails.amountDueAnteriorYear != null && cmeTaxNoticeDetails.amountDueAnteriorYear != 0.0) {

                mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(cmeTaxNoticeDetails.amountDueAnteriorYear)
                totalDueAmount = totalDueAmount?.plus(cmeTaxNoticeDetails.amountDueAnteriorYear
                        ?: 0.0)
            } else
                mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(0.0)
            //endregion

            // region RAR for previous year
            if (cmeTaxNoticeDetails.amountDuePreviousYear != null && cmeTaxNoticeDetails.amountDuePreviousYear != 0.0) {

                mBinding.txtRAROfPreviousYear.text = formatWithPrecision(cmeTaxNoticeDetails.amountDuePreviousYear)
                totalDueAmount = totalDueAmount?.plus(cmeTaxNoticeDetails.amountDuePreviousYear
                        ?: 0.0)
            } else
                mBinding.txtRAROfPreviousYear.text = formatWithPrecision(0.0)
            //endregion

            //region Penalties
            if (cmeTaxNoticeDetails.penaltyDue != null && cmeTaxNoticeDetails.penaltyDue != 0.0) {

                mBinding.txtAmountOfPenalties.text = formatWithPrecision(cmeTaxNoticeDetails.penaltyDue)
                totalDueAmount = totalDueAmount?.plus(cmeTaxNoticeDetails.penaltyDue ?: 0.0)
            } else
                mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            //endregion

            //region Amount in words
            if (totalDueAmount != null && totalDueAmount != 0.0) {
                mBinding.txtTotalDueAmount.text = formatWithPrecision(totalDueAmount)
                getAmountInWordsWithCurrency(totalDueAmount, mBinding.txtAmountInWords)
            } else
                mBinding.txtAmountInWords.text = ""
            //endregion

            //region Generated By
            if (!cmeTaxNoticeDetails.generatedBy.isNullOrEmpty())
                mBinding.txtGeneratedBy.text = cmeTaxNoticeDetails.generatedBy
            else
                mBinding.txtGeneratedBy.text = SecurityContext().loggedUserID
            //endregion

            cmeTaxNoticeDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(it, 0)
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(cmeTaxNoticeDetails.taxInvoiceId!!, mBinding.btnPrint)
            }
        }

    }

    private fun bindVehicles(vehicleDetails: ArrayList<CMEVehicleDetails>) {
        if (!vehicleDetails.isNullOrEmpty()) {
            mBinding.llVehiclesTypes.removeAllViews()
            for (cmeVehicleDetail in vehicleDetails) {

                val vehicleBinding: ItemCmeTaxNoticeVehicleDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context)
                        , R.layout.item_cme_tax_notice_vehicle_details, mBinding.llVehiclesTypes, false)


                if (!cmeVehicleDetail.vehicleType.isNullOrEmpty())
                    vehicleBinding.txtVehicleType.text = cmeVehicleDetail.vehicleType
                else
                    vehicleBinding.txtVehicleType.text = ""

                if (!cmeVehicleDetail.vehicleNo.isNullOrEmpty())
                    vehicleBinding.txtVehicleNo.text = cmeVehicleDetail.vehicleNo
                else
                    vehicleBinding.txtVehicleNo.text = ""

                if (cmeVehicleDetail.vehicleAmount != 0.0)
                    vehicleBinding.txtCarrierAmount.text = formatWithPrecision(cmeVehicleDetail.vehicleAmount)
                else
                    vehicleBinding.txtCarrierAmount.text = "0.0"

                mBinding.llVehiclesTypes.addView(vehicleBinding.root)
            }
        }
    }

}