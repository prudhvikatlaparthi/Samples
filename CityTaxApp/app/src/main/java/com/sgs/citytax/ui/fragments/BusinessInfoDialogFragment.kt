package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.databinding.FragmentBusinessInfoBinding
import com.sgs.citytax.model.BusinessLocations
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.TaxPayerDetails
import com.sgs.citytax.ui.BusinessSummaryActivity
import com.sgs.citytax.ui.RegisterBusinessActivity
import com.sgs.citytax.ui.TaxDetailsActivity
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LogHelper
import com.sgs.citytax.util.PrefHelper
import com.sgs.citytax.util.formatWithPrecision
import java.io.FileNotFoundException
import java.lang.RuntimeException
import java.math.BigDecimal

class   BusinessInfoDialogFragment : DialogFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentBusinessInfoBinding
    private var mListener: Listener? = null
    private var location: BusinessLocations? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(locations: BusinessLocations) = BusinessInfoDialogFragment().apply {
            this.location = locations
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_info, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {
        bindData()
        setViewsVisibility()
        setListeners()
    }

    private fun setViewsVisibility() {
        if (location?.taxDue?.toDouble() == 0.0) {
            mBinding.btnCollectTax.visibility = View.GONE
        } else {
            mBinding.btnCollectTax.visibility = View.VISIBLE
        }
    }

    private fun bindData() {
        mBinding.txtSycoTaxID.text = location?.sycotaxID
        mBinding.tstBusinessName.text = location?.business
        mBinding.txtTaxDue.text = formatWithPrecision(location?.taxDue)
        mBinding.txtSector.text = location?.sector
        mBinding.txtZone.text = location?.zone
        mBinding.txtLatitude.text = location?.latitude
        mBinding.txtLongitude.text = location?.longitude

        mBinding.ivNavigate.setOnClickListener {
           openMap(location?.latitude,location?.longitude)
        }
    }

    private fun openMap(latitude: String?, longitude: String?) {
        try {
        val uri =
            "http://maps.google.com/maps?saddr=" + PrefHelper().latitude + "," + PrefHelper().longitude + "&daddr=" + latitude.toString() + "," +longitude
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
        startActivity(intent)
        }
        catch (e: RuntimeException)
        {
            LogHelper.writeLog(e)
        }
    }

    private fun setListeners() {
        mBinding.btnGenerateTaxNotice.setOnClickListener(this)
        mBinding.btnCollectTax.setOnClickListener(this)

        mBinding.btnBusinessSummary.setOnClickListener {
            mListener?.showProgressDialog()
            APICall.getTaxPayerDetails(location?.sycotaxID, object : ConnectionCallBack<TaxPayerDetails> {
                override fun onSuccess(response: TaxPayerDetails) {

                    if(response.businessDues?.businessDueSummary?.size == 0){
                        mListener?.dismissDialog()
                        mListener?.showAlertDialog(getString(R.string.msg_no_data),
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                            })
                        return
                    }

                    val intent = Intent(context, BusinessSummaryActivity::class.java)
                    ObjectHolder.registerBusiness.sycoTaxID = response.sycoTaxID ?: ""
                    ObjectHolder.registerBusiness.vuCrmAccounts = response.vuCrmAccounts
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSINESS_SUMMARY)
                    intent.putExtra(Constant.KEY_CUSTOMER_ID, location?.sycotaxID)
                    startActivity(intent)
                    mListener?.onClick()
                    mListener?.dismissDialog()
                    dismiss()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                }
            })

        }

        mBinding.btnBusinessRecord.setOnClickListener {
            mListener?.showProgressDialog()
            APICall.getTaxPayerDetails(location?.sycotaxID, object : ConnectionCallBack<TaxPayerDetails> {
                override fun onSuccess(response: TaxPayerDetails) {
                    mListener?.onClick()

                    var currentInvoiceDue = BigDecimal.ZERO
                    //Initial Current year outstanding > 0 && CurrentInvoiceDue == 0
                    if(response.businessDues?.businessDueSummary?.size == 0){
                        mListener?.dismissDialog()
                        mListener?.showAlertDialog(getString(R.string.msg_no_data),
                            DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                        return
                    }
                    if ((response.businessDues?.businessDueSummary?.get(0)?.initialOutstandingCurrentYearDue!! > BigDecimal.ZERO
                                    && response.businessDues?.businessDueSummary?.get(0)?.currentInvoiceDue?.compareTo(BigDecimal(0)) == 0) ||
                            response.businessDues?.businessDueSummary?.get(0)?.initialOutstandingCurrentYearDue!!.compareTo(BigDecimal(0)) == 0
                            && response.businessDues?.businessDueSummary?.get(0)?.currentInvoiceDue?.compareTo(BigDecimal(0)) == 0) {
                        currentInvoiceDue = BigDecimal.ZERO
                    } else {
                        currentInvoiceDue = BigDecimal.ONE
                    }
                    /*val currentInvoiceDue = response.businessDues?.businessDueSummary?.get(0)?.currentYearDue
                            ?: BigDecimal.ZERO*/

                    val intent = Intent(context, RegisterBusinessActivity::class.java)
                    ObjectHolder.registerBusiness.sycoTaxID = response.sycoTaxID ?: ""
                    ObjectHolder.registerBusiness.vuCrmAccounts = response.vuCrmAccounts
                    ObjectHolder.registerBusiness.vuCrmAccounts?.email = response.email
                    ObjectHolder.registerBusiness.vuCrmAccounts?.phone = response.number
                    ObjectHolder.registerBusiness.vuCrmAccounts?.estimatedTax = response.estimatedTax
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    intent.putExtra(Constant.KEY_CUSTOMER_ID, response.sycoTaxID)
                    if (currentInvoiceDue > BigDecimal.ZERO) {
                        intent.putExtra(Constant.KEY_EDIT, true)
                    } else {
                        intent.putExtra(Constant.KEY_EDIT, false)
                    }
                    startActivity(intent)
                    mListener?.dismissDialog()
                    dismiss()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                }
            })


        }
    }


    interface Listener {
        fun showAlertDialog(message: String)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun onClick()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mBinding.btnGenerateTaxNotice.id,
            mBinding.btnCollectTax.id -> {
                mListener?.showProgressDialog()
                APICall.getTaxPayerDetails(location?.sycotaxID
                        ?: "", object : ConnectionCallBack<TaxPayerDetails> {
                    override fun onSuccess(response: TaxPayerDetails) {
                        if(response.businessDues?.businessDueSummary?.size == 0){
                            mListener?.dismissDialog()
                            mListener?.showAlertDialog(getString(R.string.msg_no_data),
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                })
                            return
                        }
                        mListener?.onClick()
                        mListener?.dismissDialog()
                        dismiss()
                        val intent = Intent(context, TaxDetailsActivity::class.java)
                        intent.putExtra(Constant.KEY_CUSTOMER_ID, response.CustomerID)
                        intent.putExtra(Constant.KEY_QUICK_MENU, if (v.id == mBinding.btnGenerateTaxNotice.id) Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE else Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_COLLECTION)
                        startActivity(intent)
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        mListener?.showAlertDialog(message)
                    }
                })
            }
        }
    }
}