package com.sgs.citytax.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.VerificationDetails
import com.sgs.citytax.api.response.PropertyDueSummaryResponse
import com.sgs.citytax.api.response.PropertyTaxResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityPropertySummaryReceiptBinding
import com.sgs.citytax.databinding.ItemLandPropertyTaxDetailsBinding
import com.sgs.citytax.databinding.ItemPropertyOutstandingDetailsBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.*

class PropertySummaryReceiptActivity : BaseActivity() {
    private lateinit var mBinding: ActivityPropertySummaryReceiptBinding
    private var propertyId: Int? = 0
    private val printHelper = PrintHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_property_summary_receipt)
        hideToolbar()
        intent.extras?.let {
            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                propertyId = it.getInt(Constant.KEY_PRIMARY_KEY)
        }
        getReceiptDetails()
        setListeners()
    }

    private fun getReceiptDetails() {
        showProgressDialog()
        APICall.getPropertyDetails(propertyId
                ?: 0, object : ConnectionCallBack<PropertyTaxResponse> {
            override fun onSuccess(response: PropertyTaxResponse) {
                dismissDialog()
                if (response.propertyTax.isNotEmpty())
                    bindData(response.propertyTax[0], response.address[0])
                bindOwners(response.propertyOwners)
                if (response.verificationDetails != null && response.verificationDetails.isNotEmpty())
                    bindVerifiedBy(response.verificationDetails[0])
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }

    private fun bindVerifiedBy(verificationDetails: VerificationDetails) {
        verificationDetails?.let {
            if (it.allowPhysicalVerification.equals("Y")) {
                mBinding.llVerifiedBy.visibility = View.VISIBLE
                mBinding.txtVerifiedBy.text = it.physicalVerificationByUser
            }
        }
    }

    private fun bindData(propertyDetails: PropertyTax?, addressDetails: GeoAddress?) {
        if (propertyDetails != null) {
            when (propertyDetails.taxRuleBookCode) {
                Constant.TaxRuleBook.COM_PROP.Code -> {
                    mBinding.titleProperty.text = mBinding.titleProperty.context.getString(R.string.title_commercial_summary)
                    mBinding.propertyDetails.visibility = View.VISIBLE
                    mBinding.llMonthRent.visibility = View.VISIBLE
                    mBinding.llYearlyRent.visibility = View.VISIBLE
                    mBinding.llConstructionDate.visibility = View.VISIBLE


                }
                Constant.TaxRuleBook.RES_PROP.Code -> {
                    mBinding.titleProperty.text = mBinding.titleProperty.context.getString(R.string.title_residential_summary)
                    mBinding.propertyDetails.visibility = View.VISIBLE
                    mBinding.llwater.visibility = View.VISIBLE
                    // mBinding.llElectricityConne.visibility=View.VISIBLE
                    mBinding.llElecPhase.visibility = View.VISIBLE
                    mBinding.llElectConsumption.visibility = View.VISIBLE
                    mBinding.llComfortlevel.visibility = View.VISIBLE
                    mBinding.llConstructionDate.visibility = View.VISIBLE

                    if (propertyDetails.isApartment == Constant.ACTIVE_Y) {
                        mBinding.llwater.visibility = View.GONE
                        mBinding.llElecPhase.visibility = View.GONE
                        mBinding.llElectConsumption.visibility = View.GONE
                        mBinding.llComfortlevel.visibility = View.GONE
                        mBinding.llConstructionDate.visibility = View.GONE
                    } else {
                        mBinding.llFloorNo.visibility = View.VISIBLE
                        mBinding.txtFloornumber.text = propertyDetails.floorNo
                    }
                }
                else -> {
                    mBinding.titleProperty.text = mBinding.titleProperty.context.getString(R.string.land_summary)
                    mBinding.propertySubDetails.visibility = View.GONE
                    mBinding.landSubDetails.visibility = View.VISIBLE
                    mBinding.llConstructionDate.visibility = View.GONE
                    //mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                    //mBinding.llCardNumber.visibility = View.VISIBLE
                    mBinding.tvSycotaxId.text = mBinding.titleProperty.context.getString(R.string.receipt_property_id_sycotax)
                    mBinding.tvRegistrationNo.text = mBinding.titleProperty.context.getString(R.string.receipt_land_registration_number)
                    mBinding.tvRegistrationDate.text = mBinding.titleProperty.context.getString(R.string.receipt_land_registration_date)
                    mBinding.tvSurveyNo.text = mBinding.titleProperty.context.getString(R.string.receipt_land_survey_number)
                    mBinding.tvLandName.text = mBinding.titleProperty.context.getString(R.string.receipt_land_name)
                    mBinding.tvLandOwner.text = mBinding.titleProperty.context.getString(R.string.receipt_land_owner)
                    mBinding.tvLandOwnerID.text = mBinding.titleProperty.context.getString(R.string.receipt_land_owner_id_sycotax)
                }
            }
            propertyDetails.noOfFloors?.let {
                mBinding.txtNoOfFloors.text = it.toString()
            }
            propertyDetails.totalBuiltUpArea?.let {
                mBinding.txtBuiltUpArea.text = it.toString()
            }
            propertyDetails.openSpace?.let {
                mBinding.txtOpenSpace.text = it.toString()
            }


            propertyDetails.MonthlyRentAmount?.let {
                mBinding.txtEstimatedMonthly.text = formatWithPrecision(it)
            }
            propertyDetails.EstimatedRentAmount?.let {
                mBinding.txtEstimatedYearly.text = formatWithPrecision(it)
            }


            propertyDetails.sycotaxID?.let {
                mBinding.txtSycoTaxID.text = it
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.PROPERTY_SUMMARY, "", it))
                CommonLogicUtils.checkNUpdateQRCodeNotes(mBinding.qrCodeWrapper)
            }
            propertyDetails.registrationNumber?.let {
                mBinding.txtRegistrationNo.text = it
            }
            propertyDetails.regDate?.let {
                mBinding.txtRegistrationDate.text = displayFormatDate(it)
            }
            propertyDetails.constructedDate?.let {
                mBinding.txtConstructionDate.text = displayFormatDate(it)
            }
            propertyDetails.surveyNumber?.let {
                mBinding.txtPropertySurveyNumber.text = it
            }
            propertyDetails.propertyName?.let {
                mBinding.txtPropertyName.text = it
            }

            propertyDetails.propertyType?.let {
                mBinding.txtLandType.text = it
            }

            propertyDetails.landUseType?.let {
                mBinding.txtNewLandUseType.text = it
            }
            propertyDetails.propertyBuildType?.let {
                mBinding.txtPropertyBuildType.text = it
            }
            propertyDetails.propertyValue?.let {
                mBinding.txtLandPropertyValue.text = propertyDetails.propertyValue()
            }
            propertyDetails.areaType?.let {
                mBinding.txtLandAreaType.text = it
            }

            propertyDetails.Length?.let {
                mBinding.txtLandLength.text = formatWithPrecisionCustomDecimals(it.toString(), false, 3)
            }
            propertyDetails.wdth?.let {
                mBinding.txtLandWidth.text = formatWithPrecisionCustomDecimals(it.toString(), false, 3)
            }
            propertyDetails.area?.let {
                mBinding.txtLandArea.text = formatWithPrecisionCustomDecimals(it.toString(), false, 3)
            }



            if (addressDetails != null) {
                //region Address
                var address: String? = ""

                if (!addressDetails.state.isNullOrEmpty()) {
                    address += addressDetails.state
                    address += ","
                } else {
                    address += ""
                }

                if (!addressDetails.city.isNullOrEmpty()) {
                    address += addressDetails.city
                    address += ","
                } else {
                    address += ""
                }

                //region Zone
                if (!addressDetails.zone.isNullOrEmpty()) {
                    address += addressDetails.zone
                    address += ","
                } else {
                    address += ""
                }
                //endregion

                //region Sector
                if (!addressDetails.sector.isNullOrEmpty()) {
                    address += addressDetails.sector
                    address += ","
                } else {
                    address += ""
                }
                //endregion

                //region plot
                if (!addressDetails.plot.isNullOrEmpty()) {
                    address += addressDetails.plot
                    address += ","
                } else {
                    address += ""
                }
                //endregion

                //region block
                if (!addressDetails.block.isNullOrEmpty()) {
                    address += addressDetails.block
                    address += ","
                } else {
                    address += ""
                }
                //endregion

                //region door no
                if (!addressDetails.doorNo.isNullOrEmpty()) {
                    address += addressDetails.doorNo
                } else {
                    address += ""
                }
                //endregion
                /* if (address != null) {
                     if (address.endsWith(",")){
                         val a = StringBuilder(address)
                         a.replace(address.lastIndexOf(","),address.lastIndexOf(",") + 1,"")
                         address = a.toString()
                     }
                     mBinding.txtAddresss.text = address
                 }*/

                address?.let {
                    mBinding.txtAddresss.text = it
                }
                //endregion
            }

            propertyDetails.landUseType?.let {
                mBinding.txtNewLandUseType.text = it
            }

            propertyDetails.areaType?.let {
                mBinding.txtAreaType.text = it
            }

            propertyDetails.propertyType?.let {
                mBinding.txtPropertyType.text = it
            }

            propertyDetails.phaseOfElectricity?.let {
                mBinding.txtElectricityPhase.text = it
            }
            propertyDetails.electricityConsumption?.let {
                mBinding.txtElectricityConsumption.text = it
            }
            propertyDetails.waterConsumption?.let {
               if(propertyDetails.waterConsumption=="No")
                mBinding.txtWaterConnection.text = mBinding.titleProperty.context.getString(R.string.no)
                else
                   mBinding.txtWaterConnection.text = mBinding.titleProperty.context.getString(R.string.yes)
            }
            propertyDetails.comfortLevel?.let {
                mBinding.txtComfortLevel.text = it
            }
            propertyDetails.onboardedBy?.let {
                mBinding.txtOnBoardBy.text = it
                //  mBinding.llOnBoardBy.visibility = View.VISIBLE
            }
            getPropertyTaxDetails(propertyDetails.sycotaxID ?: "")
            getPropertyOutstandingDues(propertyDetails.taxRuleBookCode ?: "")

        }
    }

    private fun bindOwners(owners: ArrayList<COMPropertyOwner>) {
        var ownerNames = ""
        var ownerPhones = ""
        var ownerEmails = ""
        var ownerID = ""
        var sycoTaxId = ""
        var cardNo=""
if(owners.size>0 && owners!=null) {
    if (!owners[0].propertyOwnerIDSycoTax.isNullOrEmpty()) {
        mBinding.txtPropertyOwnerID.text = owners[0].propertyOwnerIDSycoTax
    }
}
        if (owners.isNotEmpty()) {
            for (owner in owners) {

                if (!owner.owner.isNullOrEmpty())
                    ownerNames += owner.owner + ", "

                if (!owner.phoneNumber.isNullOrEmpty())
                    ownerPhones += owner.phoneNumber + ", "

                if (!owner.email.isNullOrEmpty())
                    ownerEmails += owner.email + ", "

//                if (!owner.sycotaxID.isNullOrEmpty())
//                    sycoTaxId += owner.sycotaxID + ", "
//                else {


                if (!owner.citizenID.isNullOrEmpty())
                    ownerID += owner.citizenID + ", "


                owner.citizenSycoTaxId?.let {
                    mBinding.txtCitizenSycoTaxID.text = it
                }
                owner.citizenCardNumber?.let {
                    cardNo += owner.citizenCardNumber+", "

                  /*  if (!owner.citizenCardNumber.isNullOrEmpty()) {

                        *//*mBinding.llCardNumber.visibility = View.VISIBLE
                        mBinding.txtIDCardNumber.text = it*//*
                    } else
                        mBinding.llCardNumber.visibility = View.GONE*/
                }

            }

            if (ownerNames.endsWith(", ")) {
                val b = StringBuilder(ownerNames)
                b.replace(ownerNames.lastIndexOf(", "), ownerNames.lastIndexOf(", ") + 1, " ")
                ownerNames = b.toString()
            }

            if (ownerPhones.endsWith(", ")) {
                val p = StringBuilder(ownerPhones)
                p.replace(ownerPhones.lastIndexOf(", "), ownerPhones.lastIndexOf(", ") + 1, " ")
                ownerPhones = p.toString()
            }

            if (ownerEmails.endsWith(", ")) {
                val e = StringBuilder(ownerEmails)
                e.replace(ownerEmails.lastIndexOf(", "), ownerEmails.lastIndexOf(", ") + 1, " ")
                ownerEmails = e.toString()
            }
            /* if (ownerID.endsWith(", ")) {
                 val e = StringBuilder(ownerID)
                 e.replace(ownerID.lastIndexOf(", "), ownerID.lastIndexOf(", ") + 1, " ")
                 ownerID = e.toString()
             }*/

            if (ownerID.endsWith(", ")) {
                val e = StringBuilder(ownerID)
                e.replace(ownerID.lastIndexOf(", "), ownerID.lastIndexOf(", ") + 1, " ")
                ownerID = e.toString()
            }

            if (cardNo.endsWith(", ")) {
                val f = StringBuilder(cardNo)
                f.replace(cardNo.lastIndexOf(", "), cardNo.lastIndexOf(", ") + 1, " ")
                cardNo = f.toString()
            }

          /*  if (sycoTaxId.endsWith(", ")) {
                val e = StringBuilder(sycoTaxId)
                e.replace(sycoTaxId.lastIndexOf(", "), sycoTaxId.lastIndexOf(", ") + 1, " ")
                sycoTaxId = e.toString()
            }*/

            ownerNames.let {
                mBinding.txtPropertyOwner.text = it
            }

            ownerPhones.let {
                mBinding.txtPhonNumeber.text = it
            }

            ownerEmails.let {
                mBinding.txtEmail.text = it
            }
            cardNo.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }
           /* if (sycoTaxId != null && !TextUtils.isEmpty(sycoTaxId)) {
                sycoTaxId.let {
                    mBinding.txtPropertyOwnerID.text = it
                }
            } else {
                ownerID.let {
                    mBinding.txtPropertyOwnerID.text = it
                }
            }*/


        }
    }

    private fun getPropertyOutstandingDues(taxRuleBookCode: String) {
        showProgressDialog()

        APICall.getPropertyTaxDueYearSummary(taxRuleBookCode, propertyId, object : ConnectionCallBack<PropertyDueSummaryResponse> {
            override fun onSuccess(response: PropertyDueSummaryResponse) {
                dismissDialog()
                var propertyDueSummary: List<PropertyDueSummary> = arrayListOf()
                propertyDueSummary = response.propertyDueSummary.sortedByDescending { it.year }
                bindOutStandings(propertyDueSummary)
            }

            override fun onFailure(message: String) {
                dismissDialog()
            }
        })
    }

    private fun bindPropertyTaxDetails(taxDetailsList: List<SAL_TaxDetails>) {
        if (taxDetailsList.isNotEmpty()) {
            mBinding.llTaxDetails.removeAllViews()
            taxDetailsList.forEach { item ->
                val taxBinding: ItemLandPropertyTaxDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context), R.layout.item_land_property_tax_details, mBinding.llTaxDetails, false)
                item.product?.let {
                    taxBinding.txtTaxType.text = it
                }
                item.estimatedTax?.let {
                    taxBinding.txtTaxAmount.text = formatWithPrecision(it)
                }
                taxBinding.llStartTaxDateHeader.isVisible = item.taxStartDate?.let {
                    taxBinding.txtStartDate.text = displayFormatDate(it)
                    it.isNotBlank()
                } ?: false
                item.taxYear?.let {
                    taxBinding.txtTaxationYear.text = it.toString()
                }
                item.billingCycle?.let {
                    taxBinding.txtBillingCycle.text = it
                }
                val pendingTaxAmount = item.currentDue.plus(item.previousDue)

                pendingTaxAmount?.let {
                    taxBinding.txtPendingAmount.text = formatWithPrecision(it)
                }
                mBinding.llTaxDetails.addView(taxBinding.root)
            }
        }
    }

    private fun bindOutStandings(propertyDueSummary: List<PropertyDueSummary>) {
        if (propertyDueSummary.isNotEmpty()) {

            mBinding.llOutstandings.removeAllViews()

            for (summary in propertyDueSummary) {
                val summaryBinding: ItemPropertyOutstandingDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context), R.layout.item_property_outstanding_details, mBinding.llOutstandings, false)
                summaryBinding.llyear.visibility = View.VISIBLE
                summary.year?.let {
                    summaryBinding.txtYear.text = it.toString()
                }

                summary.product?.let {
                    summaryBinding.txtProduct.text = it
                }

                summary.taxSubType?.let {
                    summaryBinding.txtSubType.text = it
                }

                summary.voucherNo?.let {
                    summaryBinding.txtVoucherNo.text = it.toString()
                }

                summary.invoiceAmount?.let {
                    summaryBinding.txtInvoiceAmount.text = formatWithPrecision(it)
                }

                summary.invoiceDue?.let {
                    summaryBinding.txtInvoiceDue.text = formatWithPrecision(it)
                }

                summary.penaltyAmount?.let {
                    summaryBinding.txtPenaltyAmount.text = formatWithPrecision(it)
                }

                summary.penaltyDue?.let {
                    summaryBinding.txtPenaltyDue.text = formatWithPrecision(it)
                }

                mBinding.llOutstandings.addView(summaryBinding.root)
            }

        } else {
            mBinding.txtOutstandings.visibility = View.GONE
            mBinding.llOutstandings.visibility = View.GONE
        }
    }

    private fun getPropertyTaxDetails(sycoTaxId: String) {
        showProgressDialog()
        APICall.getPropertyLandTaxDetails(sycoTaxId, object : ConnectionCallBack<List<SAL_TaxDetails>> {
            override fun onSuccess(response: List<SAL_TaxDetails>) {
                dismissDialog()
                bindPropertyTaxDetails(response)
            }

            override fun onFailure(message: String) {
                dismissDialog()
            }
        })
    }

    private fun setListeners() {
        mBinding.btnPrint.setOnClickListener {
            if (MyApplication.sunmiPrinterService != null) {
                val view = loadBitmapFromView(mBinding.llPropertyPreview)
                val resizedBitmap = resize(view)
                printHelper.printBitmap(resizedBitmap)
                finish()
            } else {
                showAlertDialog(getString(R.string.msg_print_not_support))
            }
        }
    }

}