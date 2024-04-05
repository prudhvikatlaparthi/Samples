package com.sgs.citytax.ui.viewHolder

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.PropertyLandTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemPropertyTaxNoticeBinding
import com.sgs.citytax.model.PropertyLandTaxNoticeDetails
import com.sgs.citytax.util.*
import java.util.*

class PropertyLandTaxNoticeViewHolder(val mBinding: ItemPropertyTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(taxNoticeResponse: PropertyLandTaxNoticeResponse, iClickListener: IClickListener) {


        bindtaxDetails(taxNoticeResponse.propertLandTaxNoticeDetails[0],taxNoticeResponse)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxNoticeResponse)
                }
            })
        }
    }

    private fun bindtaxDetails(
        taxNoticeDetails: PropertyLandTaxNoticeDetails?,
        taxNoticeResponse: PropertyLandTaxNoticeResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = taxNoticeResponse.orgData
        )
        mBinding.txtSectorLabel.text = String.format("%s%s", getString(R.string.sector), getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

        if (taxNoticeDetails != null) {

            if (taxNoticeDetails.printCounts != null && taxNoticeDetails.printCounts!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                mBinding.txtPrintCounts.text = taxNoticeDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
 //               mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

//            taxNoticeDetails.product?.let {
//                mBinding.txtTaxNoticeHeader.text = mBinding.txtTaxNoticeHeader.context.getString(R.string.place_holder_tax_notice_header, it)
//            }

            taxNoticeDetails.registrationDate?.let {
                mBinding.txtStartDate.text = displayFormatDate(it)
            }

            taxNoticeDetails.noticeReferanceNo?.let {
                mBinding.txtNoticeNo.text = it
            }

            taxNoticeDetails.taxInvoiceId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString(), taxNoticeDetails.propertySycotaxID,taxNoticeDetails.noticeReferanceNo
                        ?: ""))
            }
            taxNoticeDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = "$it"
            }
            taxNoticeDetails.taxInvoiceDate?.let {
                mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(it)
            }

            taxNoticeDetails.propertyName?.let {
                mBinding.txtPropertyName.text = it
            }

            taxNoticeDetails.propertyOwners?.let {
                val businessOwners: String = it
                if (businessOwners.contains(",")) {
                    businessOwners.replace(",", "\n")
                    mBinding.txtPropertyOwner.text = businessOwners
                } else
                    mBinding.txtPropertyOwner.text = businessOwners
            }

            taxNoticeDetails.propertyOwnerIDSycoTax?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }

            taxNoticeDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }

            taxNoticeDetails.exemption?.let {
                mBinding.txtExemption.text = it
            }
            if (!taxNoticeDetails.propertyExemptionReason.isNullOrEmpty())
            {
                taxNoticeDetails.propertyExemptionReason?.let {
                    mBinding.llExemptionReason.visibility = View.VISIBLE
                    mBinding.txtExemptionCriteria.text = it
                }
            }

            taxNoticeDetails.propertySycotaxID?.let {
                mBinding.txtSycoTaxID.text = it
            }

            var address: String? = ""

            taxNoticeDetails.state?.let {
                mBinding.txtState.text = it
                address += it
                address += ","
            }
            taxNoticeDetails.city?.let {
                mBinding.txtCity.text = it
                address += it
                address += ","
            }
            taxNoticeDetails.zone?.let {
                mBinding.txtArdt.text = it
                address += it
                address += ","
            }

            taxNoticeDetails.sector?.let {
                mBinding.txtSector.text = it
                address += it
                address += ","
            }
            taxNoticeDetails.plot?.let {
                mBinding.txtSection.text = it
                address += it
                address += ","
            }
            taxNoticeDetails.block?.let {
                mBinding.txtLot.text = it
                address += it
                address += ","
            }
            taxNoticeDetails.doorNo?.let {
                mBinding.txtParcel.text = it
                address += it

            }

            if (!address.isNullOrEmpty()) {
                mBinding.txtAddress.text = address
            } else
                mBinding.txtAddress.text = ""


            if (taxNoticeDetails.taxRuleBookCode == Constant.TaxRuleBook.COM_PROP.Code)
            {
                mBinding.txtTaxNoticeHeader.text = mBinding.txtTaxNoticeHeader.context.getString(R.string.place_holder_commercial_property_tax_notice)

                mBinding.llComercial.visibility = View.VISIBLE
                taxNoticeDetails.propertyType?.let {
                    mBinding.txtPropertyType.text = it
                }
                taxNoticeDetails.propertyValue?.let {
                    mBinding.txtPropertyValue.text = formatWithPrecision(it)
                }
                taxNoticeDetails.propertyRent?.let {
                    mBinding.txtPropertyRentAmount.text = formatWithPrecision(it)
                }


            }
            if (taxNoticeDetails.taxRuleBookCode == Constant.TaxRuleBook.RES_PROP.Code)
            {
                mBinding.txtTaxNoticeHeader.text = mBinding.txtTaxNoticeHeader.context.getString(R.string.place_holder_residential_property_tax_notice)

                mBinding.llResidentail.visibility = View.VISIBLE
                taxNoticeDetails.propertyType?.let {
                    mBinding.txtResPropertyType.text = it
                }
                taxNoticeDetails.waterConsumption?.let {
                    mBinding.txtWaterConnection.text = it
                }
                taxNoticeDetails.electricityConsumption?.let {
                    mBinding.txtElectricityConnection.text = it
                }
                taxNoticeDetails.phaseOfElectricity?.let {
                    mBinding.txtElectricityPhase.text = it
                }
                taxNoticeDetails.comfortLevel?.let {
                    mBinding.txtTaxableComfortLevel.text = it
                }
            }
            if (taxNoticeDetails.taxRuleBookCode == Constant.TaxRuleBook.LAND_PROP.Code)
            {

                mBinding.txtTaxNoticeHeader.text = mBinding.txtTaxNoticeHeader.context.getString(R.string.place_holder_land_property_tax_notice)
                mBinding.tvSycotaxId.text=mBinding.txtTaxNoticeHeader.context.getString(R.string.land_property_id_sycotax)
                mBinding.tvLandName.text=mBinding.txtTaxNoticeHeader.context.getString(R.string.receipt_land_property_name)
                mBinding.tvPropertyOwner.text=mBinding.txtTaxNoticeHeader.context.getString(R.string.receipt_land_property_owner)
                mBinding.tvOwnerSycotaxId.text=mBinding.txtTaxNoticeHeader.context.getString(R.string.receipt_land_property_owner_id_sycotax)

                mBinding.llLand.visibility=View.VISIBLE

                taxNoticeDetails.propertyType?.let {
                    mBinding.txtPropertyTypeLand.text = it
                }
                taxNoticeDetails.landUseType?.let {
                    mBinding.txtLandUseType.text = it
                }
                taxNoticeDetails.billingCycle?.let {
                    mBinding.txtLandBillingCycle.text = it
                }
                taxNoticeDetails.AreaType?.let {
                    mBinding.txtLandAreaType.text = it
                }
                taxNoticeDetails.length?.let {
                    mBinding.txtLandLength.text = formatWithPrecisionCustomDecimals(it.toString(), false, 3)
                }
                taxNoticeDetails.width?.let {
                    mBinding.txtLandWidth.text = formatWithPrecisionCustomDecimals(it.toString(), false, 3)
                }
                taxNoticeDetails.area?.let {
                    mBinding.txtLandArea.text = formatWithPrecisionCustomDecimals(it.toString(), false, 3)
                }
            }

            if (taxNoticeDetails.taxRuleBookCode == Constant.TaxRuleBook.LAND_CONTRIBUTION.Code)
            {
                mBinding.txtTaxNoticeHeader.visibility=GONE
                mBinding.txtLandCTaxNoticeHeader.visibility= VISIBLE
                mBinding.tvSycotaxId.text=mBinding.tvSycotaxId.context.getString(R.string.land_property_id_sycotax)
                mBinding.tvLandName.text=mBinding.tvLandName.context.getString(R.string.receipt_land_property_name)
                mBinding.tvPropertyOwner.text=mBinding.tvPropertyOwner.context.getString(R.string.receipt_land_property_owner)
                mBinding.tvOwnerSycotaxId.text=mBinding.tvOwnerSycotaxId.context.getString(R.string.receipt_land_property_owner_id_sycotax)
                mBinding.llLandContribution.visibility=View.VISIBLE

                taxNoticeDetails.propertyType?.let {
                    mBinding.txtPropertyTypeLandContribution.text = it
                }
                taxNoticeDetails.propertyBuildType?.let {
                    mBinding.txtPropertyBuildType.text = it
                }
                taxNoticeDetails.propertyValue?.let {
                    mBinding.txtLandPropertyValue.text = formatWithPrecision(it)
                }
                taxNoticeDetails.rate?.let {
                    //mBinding.txtLandContributionTaxTariff.text = getTariffWithCurrency(it)
                    if (it.contains("/")) {
                        mBinding.txtLandContributionTaxTariff.text = getTariffWithPercentage(it.replace("/","%"))
                      }
                    else{

                        mBinding.txtLandContributionTaxTariff.text=getTariffWithPercentage("$it%")
                    }

                }

            }

            taxNoticeDetails.invoiceAmount?.let {
                mBinding.txtAmountOfTaxInvoice.text = formatWithPrecision(it)
            }
            var totalDueAmount: Double? = 0.0

            mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(0.0)
            taxNoticeDetails.amountDueForCurrentYear?.let {
                if (it > 0.0)
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it) 
                else
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(0.0)
            taxNoticeDetails.amountDueAnteriorYear?.let {
                if (it > 0.0)
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it) 
                else
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfPreviousYear.text = formatWithPrecision(0.0)
            taxNoticeDetails.amountDuePreviousYear?.let {
                if (it > 0.0)
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it) 
                else
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            taxNoticeDetails.penaltyDue?.let {
                if (it > 0.0)
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(it) 
                else
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            totalDueAmount?.let {
                mBinding.txtTotalDueAmount.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }

            //endregion
            taxNoticeDetails.generatedBy?.let {
                mBinding.txtGeneratedBy.text = it
            }
            taxNoticeDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(it, 0)
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(taxNoticeDetails.taxInvoiceId!!, mBinding.btnPrint)
            }
        }
    }

}
