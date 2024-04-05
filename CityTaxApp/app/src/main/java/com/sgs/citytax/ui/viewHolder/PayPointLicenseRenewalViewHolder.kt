package com.sgs.citytax.ui.viewHolder

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.LicenseRenewalReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemLicensePlanElementsBinding
import com.sgs.citytax.databinding.ItemLicenseRenewalPaymentReceiptBinding
import com.sgs.citytax.model.LicensePlanElements
import com.sgs.citytax.model.LicenseReceiptDetails
import com.sgs.citytax.util.*
import java.util.*

class PayPointLicenseRenewalViewHolder (val mBinding:ItemLicenseRenewalPaymentReceiptBinding):RecyclerView.ViewHolder(mBinding.root){

    fun bind(licenseRenewalReceiptResponse: LicenseRenewalReceiptResponse,iClickListener: IClickListener){

        bindLicenseRenewalDetails(licenseRenewalReceiptResponse.licenseReceiptDetails[0],licenseRenewalReceiptResponse)
        bindLicensePlanElements(licenseRenewalReceiptResponse.licenseElementPlans)


       if (iClickListener != null){
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
               override fun onSingleClick(v: View) {
                   iClickListener.onClick(v, adapterPosition, licenseRenewalReceiptResponse)
               }
           })
       }
    }

    private fun bindLicenseRenewalDetails(
        licenseReceiptDetails: LicenseReceiptDetails?,
        licenseRenewalReceiptResponse: LicenseRenewalReceiptResponse
    ){
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = licenseRenewalReceiptResponse.orgData
        )
        val collectBy = mBinding.txtCollectedByLabel.context.getString(R.string.collected_by)
        mBinding.txtCollectedByLabel.text = String.format("%s%s", collectBy, getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport


        if (licenseReceiptDetails != null){
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            licenseReceiptDetails.printCounts?.let {
                if (it>1){
                    mBinding.llDuplicatePrints.visibility = View.GONE
//                    mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
 //                   mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }

            licenseReceiptDetails.advanceReceivedId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_RECEIPT,it.toString()))
            }

            licenseReceiptDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it.toString()
            }
            licenseReceiptDetails.advanceDate?.let {
                mBinding.txtDateOfPurchase.text = formatDisplayDateTimeInMillisecond(it)
            }
            licenseReceiptDetails.payPointName?.let {
                mBinding.txtOrganisationName.text = it
            }
            licenseReceiptDetails.payPointCode?.let {
                mBinding.txtPayPointCode.text = it
            }
            licenseReceiptDetails.branchName?.let {
                mBinding.txtAdminOffice.text = it
            }
            licenseReceiptDetails.referanceNo?.let {
                mBinding.txtVoucherNo.text = it
            }
            licenseReceiptDetails.zone?.let {
                mBinding.txtArdt.text = it
            }
            licenseReceiptDetails.sector?.let {
                mBinding.txtSector.text = it
            }
            licenseReceiptDetails.plot?.let {
                mBinding.txtSection.text = it
            }
            licenseReceiptDetails.block?.let {
                mBinding.txtLot.text = it
            }
            licenseReceiptDetails.doorNo?.let {
                mBinding.txtParcel.text = it
            }
            licenseReceiptDetails.paymentMode?.let {
                mBinding.txtPaymentMethod.text = it
            }
            licenseReceiptDetails.walletTransactionNumber?.let {
                mBinding.txtReferanceTransactionNumber.text = it.toString()
            }
            licenseReceiptDetails.amountPaid?.let {
                mBinding.txtAmountPaid.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }
            licenseReceiptDetails.collectedBy?.let {
                mBinding.txtCollectedBy.text = it
            }

        }

    }
    fun bindLicensePlanElements(licensePlanElements: ArrayList<LicensePlanElements>){
        if (licensePlanElements.isNotEmpty()){

            for (licensePlanElement in licensePlanElements) {
                val licenseBinding:ItemLicensePlanElementsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context),
                R.layout.item_license_plan_elements,mBinding.llLicensePlanElements,false)
                licenseBinding.txtAmountLabel.text = String.format("%s%s", getString(R.string.amount), getString(R.string.colon))

                licensePlanElement.userName?.let {
                    licenseBinding.txtUserName.text = it
                }
                licensePlanElement.licenseCode?.let {
                    licenseBinding.txtLicenseCode.text = it
                }
                licensePlanElement.modelName?.let {
                    licenseBinding.txtSubscriptionPlan.text = it
                }
                licensePlanElement.fromDate?.let {
                    licenseBinding.txtLicenseRenewalStartDate.text = formatDisplayDateTimeInMillisecond(it)
                }
                licensePlanElement.toDate?.let {
                    licenseBinding.txtLicenseRenewalEndDate.text = formatDisplayDateTimeInMillisecond(it)
                }
                licensePlanElement.amount?.let {
                    licenseBinding.txtAmount.text = formatWithPrecision(it)
                }

                mBinding.llLicensePlanElements.addView(licenseBinding.root)
            }
        }
    }
}