package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AccountPhone
import com.sgs.citytax.api.payload.StockTransferListPayload
import com.sgs.citytax.api.response.StockTransferListReturn
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.databinding.FragmentStockListBinding
import com.sgs.citytax.model.StockTransferListResults
import com.sgs.citytax.ui.adapter.StockTransferListAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*


class StockListFragment : BaseFragment(), IClickListener {

    lateinit var mBinding: FragmentStockListBinding
    private var mListener: Listener? = null
    private val stockTransferListAdapter: StockTransferListAdapter by lazy {
        StockTransferListAdapter(
            this
        )
    }
    private lateinit var pagination: Pagination
    private val resultList: MutableList<StockTransferListResults> = mutableListOf()
    private var fromDate: String? = null
    private var toDate: String? = null

    override fun initComponents() {
        showDateRangeSelection()

        mBinding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
        mBinding.recyclerView.adapter = stockTransferListAdapter
        pagination = Pagination(1, 10, mBinding.recyclerView) { pageNumber, PageSize ->
            bindData(pageNumber, pageSize = PageSize)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_list, container, false)
        initComponents()
        setListeners()
        return mBinding.root
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mListener?.screenMode = Constant.ScreenMode.ADD
                showStockEntryScreen()
            }
        })
    }

    private fun showStockEntryScreen(stockTransfer: StockTransferListResults? = null) {
        val fragment = StockEntryFragment.newInstance()
        //region Set Arguments
        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            val bundle = Bundle()
            bundle.putParcelable(Constant.KEY_STOCK_TRANSFER, stockTransfer)
            fragment.arguments = bundle
        }
        //endregion
        fragment.setTargetFragment(this@StockListFragment, Constant.REQUEST_CODE_STOCKTRANSFER)
        mListener?.addFragment(fragment, true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_filter) {
            showDateRangeSelection()
        } /*else if (item.itemId == android.R.id.home)
            mListener?.finish()*/
        return true
    }

    private fun showDateRangeSelection() {
        val layoutInflater = LayoutInflater.from(activity)
        val binding = DataBindingUtil.inflate<FilterDateRangeBinding>(
            layoutInflater,
            R.layout.filter_date_range,
            date_dilog_linear_layout,
            false
        )
        val edtFromDate = binding.editTextFromDate
        val edtToDate = binding.editTextToDate
        edtToDate.isEnabled = false
        edtToDate.setDisplayDateFormat(displayDateFormat)
        edtToDate.setMaxDate(Calendar.getInstance().timeInMillis)
        edtFromDate.setDisplayDateFormat(displayDateFormat)
        edtFromDate.setMaxDate(Calendar.getInstance().timeInMillis)
        if (fromDate.isNullOrBlank() && fromDate.isNullOrBlank()) {
            val map = getFromAndToDate()
            fromDate = map["from_date"]
            toDate = map["to_date"]
        }
        edtFromDate.setText(fromDate?.let {
            displayFormatDate(formatDates(it))
        })
        edtToDate.setText(toDate?.let {
            displayFormatDate(formatDates(it))
        })

        binding.editTextFromDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                edtFromDate.text?.toString()?.let {
                    if (it.isNotEmpty()) {
                        edtToDate.isEnabled = true
                        edtToDate.setText("")
                        edtToDate.setMinDate(parseDate(it, displayDateFormat).time)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.txtInpLayFromDate.error = null
                }
            }
        })

        binding.editTextToDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.txtInpLayToDate.error = null
                }
            }
        })
        mListener?.showAlertDialog(R.string.title_select_date, R.string.ok, View.OnClickListener {
            val dialog = (it as Button).tag as AlertDialog
            var isValid = true
            fromDate = serverFormatDate(edtFromDate.text.toString())
            toDate = serverFormatDate(edtToDate.text.toString())
            if (TextUtils.isEmpty(fromDate)) {
                isValid = false
                binding.txtInpLayFromDate.error = getString(R.string.msg_from_date)
                edtFromDate.requestFocus()
            } else if (TextUtils.isEmpty(toDate)) {
                isValid = false
                binding.txtInpLayToDate.error = getString(R.string.msg_to_date)
                edtToDate.requestFocus()
            }
            if (isValid) {
                bindData()
                dialog.dismiss()
            }
        }, 0, null, R.string.cancel, View.OnClickListener {
            val dialog = (it as Button).tag as AlertDialog
            dialog.dismiss()
        }, binding.root)

    }

    private fun getFromAndToDate(): Map<String, String> {
        val dateFormat = SimpleDateFormat(DateFormat, Locale.getDefault())
        val fromCal = Calendar.getInstance()
        val toCal = Calendar.getInstance()
        fromCal.add(Calendar.DATE, -1)
        val map = HashMap<String, String>()
        map["from_date"] = dateFormat.format(fromCal.time)
        map["to_date"] = dateFormat.format(toCal.time)
        return map
    }

    private fun bindData(pageIndex: Int = 1, pageSize: Int = 10) {
        if (pageIndex == 1) {
            mBinding.recyclerView.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            resetRecyclerAdapter()
            mListener?.showProgressDialog()
        } else {
            mBinding.ProgressBar.isVisible = true
        }
        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
            val calender = Calendar.getInstance(Locale.getDefault())
            calender.add(Calendar.DAY_OF_YEAR, -1)
            fromDate = getDate(calender.time, DateFormat)
            toDate = getDate(Date(), DateFormat)
        }
        val payload = StockTransferListPayload(
            fromDate = fromDate,
            toDate = toDate,
            pageIndex = pageIndex,
            pageSize = pageSize
        )
        APICall.getStockTranferList(
            payload,
            object : ConnectionCallBack<StockTransferListReturn> {
                override fun onSuccess(response: StockTransferListReturn) {
                    try {
                        if (pageIndex == 1) {
                            response.totalRecordsFound?.let {
                                pagination.totalRecords = it
                            }
                            resultList.clear()
                        }
                        if (response.stockTransferListResults?.size ?: 0 > 0) {
                            pagination.stopPagination(response.stockTransferListResults?.size!!)
                            response.stockTransferListResults?.let { dataList ->
                                resultList.addAll(dataList)
                                stockTransferListAdapter.differ.submitList(resultList)
                                stockTransferListAdapter.notifyDataSetChanged()
                                pagination.setIsScrolled(false)
                            }
                        } else {
                            pagination.stopPagination(0)
                            if (pageIndex == 1) {
                                resetRecyclerAdapter()
                                mListener?.showAlertDialog(getString(R.string.msg_no_data))
                            }
                        }
                    } catch (ex: Exception) {
                        LogHelper.writeLog(exception = ex)
                    }
                    mListener?.dismissDialog()
                    mBinding.ProgressBar.isVisible = false
                }

                override fun onFailure(message: String) {
                    if (pageIndex == 1) {
                        resetRecyclerAdapter()
                    }
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    mBinding.ProgressBar.isVisible = false
                }

            })
    }

    fun onBackPressed() {
        mListener?.finish()
    }

    private fun resetRecyclerAdapter() {
        resultList.clear()
        stockTransferListAdapter.differ.submitList(resultList)
        stockTransferListAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_STOCKTRANSFER) {
            bindData()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun finish()
        var screenMode: Constant.ScreenMode
        fun showAlertDialog(
            message: Int,
            positiveButton: Int,
            positiveListener: View.OnClickListener,
            neutralButton: Int,
            neutralListener: View.OnClickListener?,
            negativeButton: Int,
            negativeListener: View.OnClickListener,
            view: View
        )
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        mListener?.screenMode = Constant.ScreenMode.VIEW
        showStockEntryScreen(obj as StockTransferListResults)
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

}