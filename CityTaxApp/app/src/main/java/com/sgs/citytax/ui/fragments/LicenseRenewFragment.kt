package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.SubscriptionRenewal
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.GetSubscriptionAmountDetails
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentLicenseRenewBinding
import com.sgs.citytax.model.AgentSubscriptionList
import com.sgs.citytax.model.SubscriptionModel
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.fragment_license_renew.*
import java.util.*

class LicenseRenewFragment : BaseFragment() {
    private var listener: Listener? = null
    private lateinit var binding: FragmentLicenseRenewBinding

    override fun initComponents() {
        bindData()
        setListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = if (parentFragment != null)
                parentFragment as Listener
            else context as Listener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_license_renew, container, false)
        initComponents()
        return binding.root
    }

    private fun bindData() {
        binding.edtStartDate.setDisplayDateFormat(displayDateFormat)
        binding.edtEndDate.setDisplayDateFormat(displayDateFormat)
        binding.edtStartDate.setText(getDate(Calendar.getInstance(Locale.getDefault()).toString(), DateTimeTimeZoneMillisecondFormat, displayDateTimeTimeSecondFormat))
        binding.edtEndDate.setText(getDate(Calendar.getInstance(Locale.getDefault()).toString(), DateTimeTimeZoneMillisecondFormat, displayDateTimeTimeSecondFormat))
        listener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_AgentSubscriptions", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                val userList = response.vuCrmAgentSubscriptions
                val subscriptionModelList = response.vuCrmSubscriptionModel

                if (userList.isNullOrEmpty())
                    binding.spnLicenseModel.adapter = null
                else {
                    userList.add(0, AgentSubscriptionList("-1", "", getString(R.string.select), "", ""))
                    binding.spnUserName.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, userList)
                }

                if (subscriptionModelList.isNullOrEmpty())
                    binding.spnLicenseModel.adapter = null
                else {
                    subscriptionModelList.add(0, SubscriptionModel(getString(R.string.select), "", "", -1, 0, 0))
                    binding.spnLicenseModel.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, subscriptionModelList)
                }
                listener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                listener?.dismissDialog()
            }
        })
    }

    fun getAmount(userID: String, licenseModelID: Int) {
        listener?.showProgressDialog()
        APICall.getSubscriptionAmount(userID, licenseModelID, object : ConnectionCallBack<GetSubscriptionAmountDetails> {
            override fun onSuccess(response: GetSubscriptionAmountDetails) {
                response.subscriptionAmount?.let {
                    val roundedAmount = getRoundValue(it, listener?.getRoundingPlace() ?: 0)
                   // binding.edtAmount.setText("$roundedAmount")
                   // binding.edtRounding.setText("${it.subtract(roundedAmount)}")

                    binding.edtAmount.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15 + 15))
                    binding.edtAmount.setText(formatWithPrecision(roundedAmount))
                    binding.edtRounding.setText(formatWithPrecision("${it.subtract(roundedAmount)}"))


                }
                response.startDate?.let {
                    binding.edtStartDate.setText(getDate(response.startDate.toString(), DateTimeTimeZoneMillisecondFormat, displayDateTimeTimeSecondFormat))
                    val endDate = formatDisplayDateTime(addDays(parseDate(response.startDate!!, DateTimeTimeZoneMillisecondFormat), response.days!!))
                    binding.edtEndDate.setText(endDate)
                }
                listener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                listener?.dismissDialog()
                listener?.showSnackbarMsg(message)
            }
        })

    }

    fun setListeners() {
        binding.btnSave.setOnClickListener {
            if (validateView()) {
                val subscriptionRenewal = SubscriptionRenewal()
                subscriptionRenewal.fromDate = serverFormatDateTimeInMilliSecond(binding.edtStartDate.text.toString())
                subscriptionRenewal.toDate = serverFormatDateTimeInMilliSecond(binding.edtEndDate.text.toString())
                if (binding.edtAmount.text != null && !TextUtils.isEmpty(binding.edtAmount.text.toString())) {
                    var amountSave = binding.edtAmount.text.toString()
                    subscriptionRenewal.amount = currencyToDouble(amountSave).toString().toBigDecimal()
                    //subscriptionRenewal.amount = binding.edtAmount.text.toString().toBigDecimal()
                }

                subscriptionRenewal.subscriptionModelID = (spnLicenseModel.selectedItem as SubscriptionModel).subscriptionModelID
                subscriptionRenewal.renewalDate = formatDateTime(Calendar.getInstance().time)
                subscriptionRenewal.customer = MyApplication.getPrefHelper().accountName
                listener?.onRenewClick((spnUserName.selectedItem as AgentSubscriptionList).userID
                        ?: "", subscriptionRenewal)
            }
        }

        binding.spnUserName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val user: AgentSubscriptionList?
                val subscriptionModel: SubscriptionModel?
                if (binding.spnUserName.selectedItem != null && (binding.spnUserName.selectedItem as AgentSubscriptionList).userID != "-1") {
                    user = binding.spnUserName.selectedItem as AgentSubscriptionList
                    binding.edtLicenseCode.setText(user.subscriptionCode)

                    if (binding.spnLicenseModel.selectedItem != null && (binding.spnLicenseModel.selectedItem as SubscriptionModel).paymentCycleID != -1) {
                        subscriptionModel = binding.spnLicenseModel.selectedItem as SubscriptionModel
                        getAmount(user.userID!!, subscriptionModel.subscriptionModelID!!)
                    }
                } else {
                    binding.edtLicenseCode.setText("")
                    binding.edtAmount.setText("")
                    binding.edtRounding.setText("")
                    binding.edtStartDate.setText("")
                    binding.edtEndDate.setText("")
                    binding.spnLicenseModel.setSelection(0)
                }
            }
        }


        binding.spnLicenseModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val user: AgentSubscriptionList?
                val subscriptionModel: SubscriptionModel?

                if (binding.spnLicenseModel.selectedItem != null && (binding.spnLicenseModel.selectedItem as SubscriptionModel).paymentCycleID != -1) {
                    subscriptionModel = binding.spnLicenseModel.selectedItem as SubscriptionModel

                    if (binding.spnUserName.selectedItem != null && (binding.spnUserName.selectedItem as AgentSubscriptionList).userID != "-1") {
                        user = binding.spnUserName.selectedItem as AgentSubscriptionList
                        getAmount(user.userID!!, subscriptionModel.subscriptionModelID!!)
                    }
                } else {
                    binding.edtAmount.setText("")
                    binding.edtRounding.setText("")
                    binding.edtStartDate.setText("")
                    binding.edtEndDate.setText("")
                }
            }
        }
    }

    fun validateView(): Boolean {
        if (binding.spnUserName.selectedItem == null || getString(R.string.select) == binding.spnUserName.selectedItem) {
            listener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.lbl_username))
            return false
        }

        if (binding.edtLicenseCode.text.toString().trim().isEmpty()) {
            listener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.label_license_code))
            return false
        }

        if (binding.spnLicenseModel.selectedItem == null || getString(R.string.select) == binding.spnLicenseModel.selectedItem) {
            listener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.license_model))
            return false
        }

        if (binding.edtAmount.text.toString().trim().isEmpty()) {
            listener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.amount))
            return false
        }

        if (binding.edtStartDate.text.toString().trim().isEmpty()) {
            listener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.start_date))
            return false
        }

        if (binding.edtEndDate.text.toString().trim().isEmpty()) {
            listener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.end_date))
            return false
        }
        return true
    }

    fun onBackPressed() {
        listener?.popBackStack()
    }

    interface Listener {
        fun popBackStack()
        fun showProgressDialog()
        fun dismissDialog()
        fun showSnackbarMsg(message: String)
        fun showToast(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun onRenewClick(userID: String, subscriptionRenewal: SubscriptionRenewal)
        fun getRoundingPlace(): Int
    }

}