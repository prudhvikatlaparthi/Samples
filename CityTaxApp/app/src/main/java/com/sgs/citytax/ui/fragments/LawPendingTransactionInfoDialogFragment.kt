package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.databinding.FragmentLawPendingTransactionInfoBinding
import com.sgs.citytax.model.LawPendingTransactionLocations

class LawPendingTransactionInfoDialogFragment : DialogFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentLawPendingTransactionInfoBinding
    private var mListener: Listener? = null
    private var location: LawPendingTransactionLocations? = null

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
        fun newInstance(locations: LawPendingTransactionLocations) = LawPendingTransactionInfoDialogFragment().apply {
            this.location = locations
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_law_pending_transaction_info, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {
        bindData()
        setViewsVisibility()
        setListeners()
    }

    private fun setViewsVisibility() {
    }

    private fun bindData() {
        mBinding.lawLocationVM = location
//        mBinding.txtSycoTaxID.text = location?.VehicleSycotaxID
//        mBinding.tstBusinessName.text = location?.business
//        mBinding.txtTaxDue.text = formatWithPrecision(location?.taxDue)
//        mBinding.txtSector.text = location?.sector
//        mBinding.txtZone.text = location?.zone
    }

    private fun setListeners() {

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

        }
    }
}