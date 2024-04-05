package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetImpondmentReturnHistory
import com.sgs.citytax.api.response.GetLAWTaxTransactionsList
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.databinding.FragmentImpoundmentReturnHistoryBinding
import com.sgs.citytax.ui.adapter.ReturnImpoundmentListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatWithPrecision
import com.sgs.citytax.util.getQuantity

class ImpoundmentReturnHistoryfragment : BaseFragment(), View.OnClickListener, ReturnImpoundmentListAdapter.Listener {
    private lateinit var mBinding: FragmentImpoundmentReturnHistoryBinding
    private var mListener: Listener? = null
    private var mImpondmentReturn: ImpondmentReturn? = null
    private var adapter: ReturnImpoundmentListAdapter? = null
    private var mImpondmentReturnHistory: List<ImpondmentReturn> = arrayListOf()
    private var fromScreen: Any? = null
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mContext = context
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_impoundment_return_history, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            mImpondmentReturn = it.getParcelable(Constant.KEY_IMPOUNDMENT_RETURN)
            fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu

        }
        setViews()
        bindData()
    }

    private fun setViews() {
        mBinding.btnCancel.setOnClickListener(this)
        mBinding.btnProceed.setOnClickListener(this)
        mBinding.rcvImpundmentsList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = ReturnImpoundmentListAdapter(this, "2")
        mBinding.rcvImpundmentsList.adapter = adapter
    }

    private fun bindData() {
        val getImpondmentReturnHistory = GetImpondmentReturnHistory()

        mImpondmentReturn?.let {
            mBinding.tvImpoundment.text = it.impoundmentType
            if (it.applicableOnVehicle == "Y") {
                mBinding.llVoucherno.visibility = View.VISIBLE
                mBinding.tvVehicleNumber.text = it.vehicleNo
                mBinding.tvOwner.text = it.vehicleOwner
                mBinding.tvPhoneNumber.text = it.vehicleOwnerMobile
                getImpondmentReturnHistory.filterType = "VehicleNo"
                getImpondmentReturnHistory.filterString = it.vehicleNo ?: "" //"09iop"

            } else {
                mBinding.llVoucherno.visibility = View.GONE
                mBinding.tvVehicleNumber.text = ""
                mBinding.tvOwner.text = it.goodsOwner
                mBinding.tvPhoneNumber.text = it.goodsOwnerMobile
                getImpondmentReturnHistory.filterType = "TaxNoticeNo"
                getImpondmentReturnHistory.filterString = it.noticeReferenceNo
            }
            if (it.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code)
            {
                mBinding.llVoucherno.visibility = View.GONE
                mBinding.llOwner.visibility = View.GONE
                mBinding.llPhone.visibility = View.GONE
                mBinding.llImpoundQty.visibility = View.VISIBLE
                mBinding.llReturnQty.visibility = View.VISIBLE
                mBinding.tvImpoundqty.text = getQuantity(it.quantity.toString())
                if (it.pendingReturnQuantity ==  null)
                mBinding.tvReturnQty.text = "0.0"
                else
                mBinding.tvReturnQty.text = getQuantity(it.pendingReturnQuantity.toString())
            }
            mBinding.tvTotalDue.text = formatWithPrecision(it.currentDue)


        }

        mListener?.showProgressDialog()
        APICall.getImpondmentReturnHistory(getImpondmentReturnHistory, object : ConnectionCallBack<GetLAWTaxTransactionsList> {
            override fun onSuccess(response: GetLAWTaxTransactionsList) {
                mListener?.dismissDialog()
                if (response.results != null && response.results.isNotEmpty()) {
                    mImpondmentReturnHistory = response?.results
                    adapter!!.addAll(mImpondmentReturnHistory)
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                var messageData=message
                /*if(messageData=="")
                    messageData =mContext.getString(R.string.no_record)

                mListener?.showAlertDialog(messageData)*/
                mBinding.rcvImpundmentsList.adapter = null
            }
        })
    }

    interface Listener {
        fun popBackStack()
        fun finish()
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener)
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnProceed -> {

                val fragment = ImpoundmentReturnfragment()
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_IMPOUNDMENT_RETURN, mImpondmentReturn)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATOR)
                mListener?.showToolbarBackButton(R.string.title_return_impondment)
                mListener?.addFragment(fragment, true)

            }
            R.id.btnCancel -> {
                mListener?.finish()
            }

        }

    }

    override fun onItemClick(list: ImpondmentReturn, position: Int) {
    }
}