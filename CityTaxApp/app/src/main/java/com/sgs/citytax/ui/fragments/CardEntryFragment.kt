package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.StoreCitizenIdentityCard
import com.sgs.citytax.api.response.CitizenIdentityCard
import com.sgs.citytax.databinding.FragmentCardEntryBinding
import com.sgs.citytax.util.*
import java.util.*

class CardEntryFragment : BaseFragment() {

    private lateinit var mBinding: FragmentCardEntryBinding
    private var mListener: Listener? = null
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mPrimaryKey: Int? = 0
    private var mCard: CitizenIdentityCard? = null

    override fun initComponents() {
        arguments?.let {
            mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPrimaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY)
            mCard = arguments?.getParcelable(Constant.KEY_CARD)
        }
        bindData()
        setEvents()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_card_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun bindData() {
        val calendar = Calendar.getInstance()
        mBinding.edtDeliveryDate.setDisplayDateFormat(Constant.DateFormat.DFddMMyyyy.value)
        mBinding.edtIssuedDate.setDisplayDateFormat(Constant.DateFormat.DFddMMyyyy.value)
        mBinding.edtIssuedDate.setText(formatDate(calendar.time, Constant.DateFormat.DFddMMyyyy))
        mBinding.edtDeliveryDate.setMinDate(calendar.timeInMillis)
        mCard?.let { it ->
            mBinding.tilDeliveryDate.isEnabled = false
            mBinding.tilIssuedDate.isEnabled = false
            mBinding.btnSave.visibility = GONE
            mBinding.tilCardNo.visibility = VISIBLE
            it.cardNo?.let {
                mBinding.edtCardNo.setText(it)
            }
            it.active?.let {
                mBinding.cbActive.isChecked = it == "Y"
            }
            it.deliveryDate.let {
                mBinding.edtDeliveryDate.setText(it?.let { it1 -> formatDate(it1, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyy) })
            }
            it.issuedDate.let {
                mBinding.edtIssuedDate.setText(it?.let { it1 -> formatDate(it1, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyy) })
            }
        }
    }

    private fun setEvents() {
        mBinding.btnSave.setOnClickListener {
            if (validate())
                save()
        }
        mBinding.edtIssuedDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.edtIssuedDate.text?.toString()?.let {
                    if (it.isNotEmpty())
                        mBinding.edtDeliveryDate.setMinDate(getTimeStamp(it, Constant.DateFormat.DFddMMyyyyhhmmssaa))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun save() {
        mListener?.showProgressDialog()
        val storeCitizenIdentityCard = StoreCitizenIdentityCard()
        val citizenIdentityCard = CitizenIdentityCard()
        citizenIdentityCard.active = "Y"
        citizenIdentityCard.contactID = mPrimaryKey
        citizenIdentityCard.issuedDate = formatDate(mBinding.edtIssuedDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyy, Constant.DateFormat.DFyyyyMMdd)
        if (!mBinding.edtDeliveryDate.text.isNullOrEmpty())
            citizenIdentityCard.deliveryDate = formatDate(mBinding.edtDeliveryDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyy, Constant.DateFormat.DFyyyyMMdd)
        storeCitizenIdentityCard.citizenIdentityCard = citizenIdentityCard
        APICall.storeCitizenIdentityCard(storeCitizenIdentityCard, object : ConnectionCallBack<CitizenIdentityCard> {
            override fun onSuccess(response: CitizenIdentityCard) {
                mListener?.dismissDialog()
                Handler().postDelayed({
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    mListener?.popBackStack()
                }, 500)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun validate(): Boolean {
        if (mBinding.edtIssuedDate.text == null || mBinding.edtIssuedDate.text.isNullOrEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) +" " +getString(R.string.issued_date))
            return false
        }
        if (!mBinding.edtDeliveryDate.text.isNullOrEmpty()) {
            if (addDays(convertToDate(mBinding.edtIssuedDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyy), 1).after(convertToDate(mBinding.edtDeliveryDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyy))) {
                mListener?.showSnackbarMsg(getString(R.string.check_delivery_date))
                return false
            }
        }
        return true
    }

    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String?)
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        var screenMode: Constant.ScreenMode

    }
}