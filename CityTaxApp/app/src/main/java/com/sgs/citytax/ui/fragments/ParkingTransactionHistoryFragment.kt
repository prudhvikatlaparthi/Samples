package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetParkingTaxTransactionsList
import com.sgs.citytax.api.response.GetParkingTaxTransactionResponse
import com.sgs.citytax.api.response.ParkingPaymentTrans
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentParkingTransactionHistoryBinding
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.adapter.ParkingTransactionHistoryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class ParkingTransactionHistoryFragment : BaseFragment(), IClickListener {

    private lateinit var binding: FragmentParkingTransactionHistoryBinding
    var pageIndex: Int = 1
    val pageSize: Int = 10
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    private var adapter: ParkingTransactionHistoryAdapter? = null
    private var mListener: Listener? = null
    private lateinit var mSycoTaxID: String

    private var fromScreen: Any? = null
    private var vehicleNo: String? = ""
    private var parkingPlaceID: Int = 0

    companion object {
        @JvmStatic
        fun newInstance() = ParkingTransactionHistoryFragment().apply {

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_parking_transaction_history, container, false)
        initComponents()
        return binding.root
    }

    override fun initComponents() {

        arguments?.let {
            fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU)
            vehicleNo = it.getString(Constant.KEY_VEHICLE_NO)
            parkingPlaceID = it.getInt(Constant.KEY_PARKING_PLACE_ID, 0)
        }
        initViews()
        initEvents()
        bindData()
    }

    private fun initViews() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = ParkingTransactionHistoryAdapter(this)
        binding.recyclerView.adapter = adapter
    }

    private fun initEvents() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = recyclerView.layoutManager!!.childCount
                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount: Int = linearLayoutManager.itemCount
                val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && hasMoreData && !isLoading) {
                    bindData()
                }
            }
        })
    }

    private fun bindData() {

        val getParkingTaxTransactionsList = GetParkingTaxTransactionsList()
        getParkingTaxTransactionsList.vehno = vehicleNo //"TS03EX0230"
        getParkingTaxTransactionsList.parkingplcid = MyApplication.getPrefHelper().parkingPlaceID //3
        getParkingTaxTransactionsList.onlydue = "N"

        val transaction = ParkingPaymentTrans()
        transaction.isLoading = true
        adapter?.add(transaction)
        isLoading = true

        APICall.getParkingTicketPaymentList(getParkingTaxTransactionsList, object : ConnectionCallBack<GetParkingTaxTransactionResponse> {
            override fun onSuccess(response: GetParkingTaxTransactionResponse) {
                binding.recyclerView.visibility = View.VISIBLE
                val count: Int = response.results.size
                if (count < pageSize) {
                    hasMoreData = false
                } else
                    pageIndex += 1
                adapter?.remove(transaction)
                adapter?.addAll(response.results)
                isLoading = false
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                adapter?.remove(transaction)
                isLoading = false
            }
        })

    }

    interface Listener {
        fun finish()
        fun showProgressDialog()
        fun popBackStack()
        fun dismissDialog()
        fun showAlertDialog(message: String)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val trackOnTransaction: ParkingPaymentTrans = obj as ParkingPaymentTrans
        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
//        intent.putExtra(KEY_ADVANCE_RECEIVED_ID, trackOnTransaction.advancerecievedid)
//        intent.putExtra(KEY_TAX_RULE_BOOK_CODE, trackOnTransaction.taxRuleBookCode)
//        startActivity(intent)
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}