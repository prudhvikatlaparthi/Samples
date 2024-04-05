package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetBookingsList
import com.sgs.citytax.api.response.AssetBooking
import com.sgs.citytax.api.response.PendingBookingResponse
import com.sgs.citytax.databinding.FragmentAssetPendingBookingsBinding
import com.sgs.citytax.model.PendingBookingsList
import com.sgs.citytax.ui.AssetBookingActivity
import com.sgs.citytax.ui.adapter.AssetPendingBookingsAdapter
import com.sgs.citytax.util.Constant

class AssetPendingBookingListFragment : BaseFragment(), AssetPendingBookingsAdapter.Listener {
    private lateinit var mBinding: FragmentAssetPendingBookingsBinding
    private var mListener: Listener? = null
    private var pendingBookings: ArrayList<PendingBookingsList> = arrayListOf()
    private var adapter: AssetPendingBookingsAdapter? = null
    var pageIndex: Int = 1
    val pageSize: Int = 50
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

    private var fromScreen: Constant.QuickMenu? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_asset_pending_bookings, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun initComponents() {
        processIntent()
        setViews()
        bindData()
        setListeners()
    }

    private fun processIntent() {
        arguments?.let {
            fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun setViews() {
        mBinding.rcvPendingBookings.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = AssetPendingBookingsAdapter(this)
        mBinding.rcvPendingBookings.adapter = adapter
    }

    private fun bindData() {
        mListener?.showProgressDialog()
        val details = PendingBookingsList()
        details.isLoading = true
        adapter?.clear()
        adapter?.add(details)
        isLoading = true
        APICall.getPendingBookingsList(pageSize, pageIndex, object : ConnectionCallBack<PendingBookingResponse> {
            override fun onSuccess(response: PendingBookingResponse) {
                mListener?.dismissDialog()
                if (response != null && response.pendingBookings.isNotEmpty()) {
                    pendingBookings = response.pendingBookings
                    val count = pendingBookings.size
                    if (count < pageSize)
                        hasMoreData = false
                    else
                        pageIndex += 1
                    adapter?.remove(details)
                    adapter?.addAll(pendingBookings)
                    isLoading = false
                } else {
                    adapter?.remove(details)
                    isLoading = false
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                adapter?.remove(details)
                isLoading = false
            }
        })
    }

    private fun setListeners() {
        mBinding.rcvPendingBookings.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun onItemClick(pendingBookings: PendingBookingsList, position: Int) {
        mListener?.showProgressDialog()
        val getBookingsList = GetBookingsList()
        getBookingsList.bookingRequestID = pendingBookings.bookingRequestId
        getBookingsList.bookingRequestLineId = 0
        getBookingsList.isAssetBookingUpdate = false
        APICall.getBookings(getBookingsList, object : ConnectionCallBack<List<AssetBooking>> {
            override fun onSuccess(response: List<AssetBooking>) {
                mListener?.dismissDialog()
                navigateToAssetBooking(response[0])
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    //activity?.finish()
                })
            }
        })

    }

    private fun navigateToAssetBooking(booking: AssetBooking) {
        val intent = Intent(context, AssetBookingActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_ASSET_BOOKING, booking)
//        startActivity(intent)
        startActivityForResult(intent, Constant.REQUEST_CODE_ASSET)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bindData()
    }

    interface Listener {
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
}