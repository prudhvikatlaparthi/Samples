package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.GetPendingViolationImpoundmentList
import com.sgs.citytax.api.response.PendingViolationImpoundment
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentImpoundmentListBinding
import com.sgs.citytax.model.TicketHistory
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.adapter.ImpoundmentListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.Pagination
import java.math.BigDecimal

class ImpoundmentListFragment : BaseFragment(), IClickListener, ImpoundmentListAdapter.Listener {

    private lateinit var binding: FragmentImpoundmentListBinding
    private lateinit var adapter: ImpoundmentListAdapter
    private var mListener: Listener? = null
    private lateinit var pagination: Pagination
    private val resultList: MutableList<TicketHistory> = mutableListOf()

    companion object {
        @JvmStatic
        fun newInstance() = ImpoundmentListFragment().apply {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_impoundment_list, container, false)
        initComponents()
        return binding.root
    }

    override fun initComponents() {
        initViews()
        bindData()
    }

    private fun initViews() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = ImpoundmentListAdapter(this, "2")
        binding.recyclerView.adapter = adapter
        pagination = Pagination(1, 10, binding.recyclerView) { pageNumber, PageSize ->
            bindData(pageIndex = pageNumber, pageSize = PageSize)
        }
    }

    fun getFromSearchFilter(impoundType: Int = 0, impoundSubType: Int = 0, vehNo: String = "", mobile: String = "") {
        Log.e("get values", ">>>>>>>>>>$impoundType >>>>>>> $impoundSubType")
        bindData(impoundType, impoundSubType, vehNo, mobile, true)
    }

    private fun bindData(impoundType: Int = 0, impoundSubType: Int = 0, vehNo: String = "", mobile: String = "", isFromSearch: Boolean = false, pageIndex: Int = 1, pageSize: Int = 10) {

        val impoundmentList = GetPendingViolationImpoundmentList()
        impoundmentList.context = SecurityContext()
        impoundmentList.filterType = "impoundment"
        //search with impound types
        impoundmentList.typeID = impoundType
        impoundmentList.subTypeID = impoundSubType
        //search with vehicle no
        impoundmentList.vehicleFilter = "VehicleNo"
        impoundmentList.vehicleFilterString = vehNo
        //search with mobile
        impoundmentList.mobileFilter = "Mobile"
        impoundmentList.mobileFilterString = mobile
        impoundmentList.pageIndex = pageIndex
        impoundmentList.pageSize = pageSize
        if (pageIndex == 1) {
            resultList.clear()
            binding.recyclerView.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            mListener?.showProgressDialog()
        } else {
            binding.ProgressBar.isVisible = true
        }

        APICall.getPendingViolations(impoundmentList, object : ConnectionCallBack<PendingViolationImpoundment> {
            override fun onSuccess(response: PendingViolationImpoundment) {
                response.totalSearchedRecords?.let {
                    pagination.totalRecords = it
                }

                if (response.results?.ticketHistory?.size ?: 0 > 0) {
                    response.results?.ticketHistory?.let {
                        resultList.addAll(it)
                        pagination.stopPagination(it.size)
                        adapter.differ.submitList(resultList)
                        adapter.notifyDataSetChanged()
                        pagination.setIsScrolled(false)
                    }
                } else {
                    pagination.stopPagination(0)
                    if (pageIndex == 1) {
                        resetRecyclerAdapter()
                        mListener?.showAlertDialog(getString(R.string.msg_no_data))
                    }
                }

                //calling this when the search reuslt is empty we show alert and calling api without filter
                /*if (arryList!!.size == 0 && isFromSearch) {
                    bindData()
                }*/
                mListener?.dismissDialog()
                binding.ProgressBar.isVisible = false
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (pageIndex == 1) {
                    mListener?.showAlertDialog(message ?: getString(R.string.no_records_found))
                }
                binding.ProgressBar.isVisible = false
            }
        })

    }

    private fun resetRecyclerAdapter() {
        resultList.clear()
        adapter.differ.submitList(resultList)
        adapter.notifyDataSetChanged()
    }

    interface Listener {
        fun finish()
        fun showProgressDialog()
        fun popBackStack()
        fun dismissDialog()
        fun showAlertDialog(message: String)
    }

    override fun onClick(view: View, position: Int, obj: Any) {

        val impoundReturn = obj as TicketHistory
        if (impoundReturn.violatorTypeCode == "ANIMAL") {
            if (impoundReturn.quantity == impoundReturn.paidQuantity &&
                impoundReturn.transactionDue ?: BigDecimal.ZERO > BigDecimal.ZERO
            ) {
                val payment = MyApplication.resetPayment()

                payment.amountDue = impoundReturn.transactionDue!!
                payment.amountTotal = impoundReturn.transactionDue!!
                payment.minimumPayAmount = impoundReturn.minmumPayAmount!!
                payment.customerID = impoundReturn.transactionNo!!
                payment.paymentType = Constant.PaymentType.TICKET_PAY
                payment.productCode = impoundReturn.prodcode!!
                payment.TransactionTypeCode = impoundReturn.transactiontypcode!!
                payment.TransactionNo = impoundReturn.transactionNo!!
                payment.customerID = impoundReturn.accounttId!!
//                payment.qty = impoundReturn.quantity ?: 0

                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
                startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
            } else {
                val dialogFragment: PendingAnimalReturnPayFragment =
                    PendingAnimalReturnPayFragment.newInstance(impoundReturn)
                dialogFragment.show(
                    childFragmentManager,
                    ImpoundmentListFragment::class.java.simpleName
                )
            }
        } else {
            val payment = MyApplication.resetPayment()

            payment.amountDue = impoundReturn.transactionDue!!
            payment.amountTotal = impoundReturn.transactionDue!!
            payment.minimumPayAmount = impoundReturn.minmumPayAmount!!
            payment.customerID = impoundReturn.transactionNo!!
            payment.paymentType = Constant.PaymentType.TICKET_PAY
            payment.productCode = impoundReturn.prodcode!!
            payment.TransactionTypeCode = impoundReturn.transactiontypcode!!
            payment.TransactionNo = impoundReturn.transactionNo!!
            payment.customerID = impoundReturn?.accounttId!!

            val intent = Intent(requireContext(), PaymentActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
            startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemClick(list: TicketHistory, position: Int) {
        TODO("Not yet implemented")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.let {
                if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID) && it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0) > 0) {
                    Log.e("Ticket payment frag", ">>>>>>>> Payment response came")

                    val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                    if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0))
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.TICKET_PAYMENT.Code)
                    startActivityForResult(intent, Constant.REQUEST_CODE_TAX_PRINT_PAGE)
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_TAX_PRINT_PAGE) {
            //this will clear the adapter and bind the list again once the payment and receipt are done successfully
            bindData()
        }
    }

}