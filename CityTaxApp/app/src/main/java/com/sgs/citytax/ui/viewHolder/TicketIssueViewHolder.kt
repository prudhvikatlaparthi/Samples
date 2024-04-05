package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.response.TicketIssueReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemTicketIssueReceiptBinding
import com.sgs.citytax.model.TicketIssueReceiptTable
import com.sgs.citytax.util.*
import com.sgs.citytax.util.CommonLogicUtils.checkNUpdateQRCodeNotes
import java.util.*

class TicketIssueViewHolder(var mBinding: ItemTicketIssueReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(response: TicketIssueReceiptResponse, iClickListener: IClickListener) {

        bindDetails(response.receiptDetails[0],response)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, response)
                }
            })
        }
    }

    private fun bindDetails(
        receiptDetails: TicketIssueReceiptTable?,
        response: TicketIssueReceiptResponse
    ) {
        checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = response.orgData
        )
        val owner = mBinding.txtVehicleOwnerLabel.context.getString(R.string.vehicle_owner)
        mBinding.txtVehicleOwnerLabel.text = String.format("%s%s", owner, getString(R.string.colon))

        val driver = mBinding.txtDriverLabel.context.getString(R.string.driver_imp)
        mBinding.txtDriverLabel.text = String.format("%s%s", driver, getString(R.string.colon))

        val licenseNo = mBinding.txtDrivingLicenseNumberLabel.context.getString(R.string.driving_license_number)
        mBinding.txtDrivingLicenseNumberLabel.text = String.format("%s%s", licenseNo, getString(R.string.colon))

        val violator = mBinding.txtViolatorLabel.context.getString(R.string.violator)
        mBinding.txtViolatorLabel.text = String.format("%s%s", violator, getString(R.string.colon))

        val addressLabel = mBinding.titleAddressLabel.context.getString(R.string.title_address_new)
        mBinding.titleAddressLabel.text = String.format("%s%s", addressLabel, getString(R.string.colon))

        val sector = mBinding.txtSectorLabel.context.getString(R.string.sector)
        mBinding.txtSectorLabel.text = String.format("%s%s", sector, getString(R.string.colon))

        val state = mBinding.titleStateLabel.context.getString(R.string.state)
        mBinding.titleStateLabel.text = String.format("%s%s", state, getString(R.string.colon))

        val city = mBinding.titleCityLabel.context.getString(R.string.city)
        mBinding.titleCityLabel.text = String.format("%s%s", city, getString(R.string.colon))

        val violationType = mBinding.txtViolationTypeLabel.context.getString(R.string.violation_type)
        mBinding.txtViolationTypeLabel.text = String.format("%s%s", violationType, getString(R.string.colon))

        val violationClass = mBinding.txtViolationClassLabel.context.getString(R.string.violation_class)
        mBinding.txtViolationClassLabel.text = String.format("%s%s", violationClass, getString(R.string.colon))

        val violationDetails = mBinding.txtViolationDetailsLabel.context.getString(R.string.violation_details)
        mBinding.txtViolationDetailsLabel.text = String.format("%s%s", violationDetails, getString(R.string.colon))

        val extraCharges = mBinding.txtViolationDetailsLabel.context.getString(R.string.extra_charges)
        mBinding.txtExtraCharges.text = String.format("%s%s", extraCharges, getString(R.string.colon))

        val idSycoTax = mBinding.txtSycoTaxID.context.getString(R.string.receipt_id_sycotax)
        mBinding.txtSycoTaxID.text = String.format("%s%s", idSycoTax, getString(R.string.colon))

        val citizenSycoTaxID = mBinding.txtCitizenSycoTaxID.context.getString(R.string.citizen_syco_tax_id)
        mBinding.txtCitizenSycoTaxID.text = String.format("%s%s", citizenSycoTaxID, getString(R.string.colon))

        val citizenIDCardNumber= mBinding.txtIDCardNumber.context.getString(R.string.citizen_id_number)
        mBinding.txtIDCardNumber.text = String.format("%s%s", citizenIDCardNumber, getString(R.string.colon))

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport


        if (receiptDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            if (receiptDetails.printCounts != null && receiptDetails.printCounts!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                mBinding.txtPrintCounts.text = receiptDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
 //               mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

            receiptDetails.violationDate?.let {
                mBinding.txtDateOfViolation.text = formatDisplayDateTimeInMillisecond(it)
            }

            receiptDetails.fineAmount?.let {
                mBinding.txtFineAmount.text = formatWithPrecision(it)
            }

            receiptDetails.extraCharge?.let {
                mBinding.txtExtraCharges.text = formatWithPrecision(it)
            }

            receiptDetails.ticketNo?.let {
                mBinding.txtTicketNumber.text = it
            }

            receiptDetails.taxInvoiceId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString(), receiptDetails.vehicleSycoTaxId
                        ?: "", receiptDetails.ticketNo, receiptDetails.ticketId.toString()))
            }

            receiptDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it.toString()
            }

            receiptDetails.vehicleNumber?.let {
                mBinding.llVehicleNo.visibility = View.VISIBLE
                mBinding.txtVehicleNo.text = it
            }

            receiptDetails.vehicleSycoTaxId?.let {
                mBinding.llVehicleIDSycotax.visibility = View.VISIBLE
                mBinding.txtVehicleSycoTaxID.text = it
            }

            receiptDetails.vehicleOwner?.let {
                mBinding.llVehicleOwner.visibility = View.VISIBLE
                mBinding.txtVehicleOwner.text = it
            }

            receiptDetails.violatorBusinessSycoTaxID?.let {
                if(receiptDetails.violatorTypeCode == Constant.ViolationTypeCode.BUSINESS.name){
                    mBinding.llBusinessSycoTax.visibility = View.VISIBLE
                    mBinding.txtSycoTaxID.text = receiptDetails.violatorBusinessSycoTaxID
                }
            }

            receiptDetails.violatorCitizenSycotaxID?.let {
                if(receiptDetails.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.name){
                    mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                    mBinding.txtCitizenSycoTaxID.text =  receiptDetails.violatorCitizenSycotaxID
                }
            }

            receiptDetails.violatorCitizenCardNo?.let {
                if(receiptDetails.violatorTypeCode == Constant.ViolationTypeCode.CITIZEN.name){
                    mBinding.llCardNumber.visibility = View.VISIBLE
                    mBinding.txtIDCardNumber.text =  receiptDetails.violatorCitizenCardNo
                }
            }

            receiptDetails.driver?.let {
                mBinding.llDriver.visibility = View.VISIBLE
                mBinding.txtDriver.text = it
            }
            receiptDetails.drivingLicenseNumber?.let {
                mBinding.llDrivingLicenseNo.visibility = View.VISIBLE
                mBinding.txtDrivingLicenseNumber.text = it
            }

            receiptDetails.violator?.let {
                mBinding.llContrevenant.visibility = View.VISIBLE
                mBinding.txtViolator.text = it
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

            receiptDetails.lat?.let {
                mBinding.txtLatitude.text = it
            }

            receiptDetails.longitude?.let {
                mBinding.txtLongitude.text = it
            }

            receiptDetails.violationType?.let {
                mBinding.txtViolationType.text = it
            }

            receiptDetails.violationClass?.let {
                mBinding.txtViolationClass.text = it
            }

            receiptDetails.violationDetails?.let {
                mBinding.txtViolationDetails.text = it
            }

            receiptDetails.ticketAmount?.let {
                mBinding.txtTicketAmount.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it, mBinding.txtAmountInWords)
            }

            receiptDetails.generatedBy?.let {
                mBinding.txtGeneratedBy.text = it
            }

            receiptDetails.badgeNo?.let {
                mBinding.txtBadgeNumber.text = it
            }

            receiptDetails.awsPath?.let {
                mBinding.imgSignature.visibility = View.VISIBLE
                Glide.with(mBinding.imgSignature.context).load(it).into(mBinding.imgSignature)
            }

            receiptDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(it, 0)
            }

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(receiptDetails.taxInvoiceId!!, mBinding.btnPrint)
            }
        }


    }

}