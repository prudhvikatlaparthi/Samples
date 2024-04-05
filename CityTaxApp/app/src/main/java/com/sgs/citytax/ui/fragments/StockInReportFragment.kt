package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.AllocatedStockResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.databinding.FragmentStockInReportBinding
import com.sgs.citytax.model.AllocatedStock
import com.sgs.citytax.ui.adapter.StockInReportAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.text.SimpleDateFormat
import java.util.*

class StockInReportFragment : BaseFragment() {

    private lateinit var mBinding: FragmentStockInReportBinding
    private var mListener: Listener? = null
    private var prefHelper: PrefHelper = MyApplication.getPrefHelper()
    private var toDate: String? = null
    private var fromDate: String? = null

    override fun initComponents() {
        //region getArguments
        arguments?.let {
        }
        //endregion
        setViews()
        bindData()
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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_in_report, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    private fun bindData() {
        val dateFormat = SimpleDateFormat(displayDateFormat, Locale.getDefault())
        if (fromDate.isNullOrEmpty()) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -7)
            fromDate = dateFormat.format(calendar.time)
        }
        if (toDate.isNullOrEmpty()) {
            val calendar = Calendar.getInstance()
            toDate = dateFormat.format(calendar.time)
        }
        fetchData(fromDate, toDate)
    }

    private fun fetchData(fromDate: String?, toDate: String?) {
        mListener?.showProgressDialog()
        APICall.getAllocationsForAccount(prefHelper.accountId, serverFormatDate(fromDate), serverFormatDate(toDate), object : ConnectionCallBack<AllocatedStockResponse> {
            override fun onSuccess(response: AllocatedStockResponse) {
                mListener?.dismissDialog()
                val allocatedStock:ArrayList<AllocatedStock> = response.allocatedStock
                allocatedStock.reverse()
                allocatedStock.let {
                    mBinding.recyclerView.adapter = StockInReportAdapter(it)
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mBinding.recyclerView.adapter = null
                if (message.isNotEmpty())
                    mListener?.showAlertDialog(message)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_filter)
            showDateRangeSelection()
        return true
    }

    private fun showDateRangeSelection() {
        // region View
        val layoutInflater = LayoutInflater.from(mBinding.recyclerView.context)
        val binding = DataBindingUtil.inflate<FilterDateRangeBinding>(layoutInflater, R.layout.filter_date_range, date_dilog_linear_layout, false)
        val edtFromDate = binding.editTextFromDate
        val txtInputLayoutFromDate = binding.txtInpLayFromDate
        val txtInputLayoutToDate = binding.txtInpLayToDate
        val edtToDate = binding.editTextToDate
        edtToDate.isEnabled=false
        edtToDate.setDisplayDateFormat(displayDateFormat)
        edtFromDate.setDisplayDateFormat(displayDateFormat)
        // endregion

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

        // region Default dates
        edtFromDate.setText(fromDate)
        edtToDate.setText(toDate)
        // endregion

        mListener?.showAlertDialog(R.string.title_select_date,
                R.string.ok,
                View.OnClickListener {
                    var isValid = true
                    fromDate = edtFromDate.text.toString()
                    toDate = edtToDate.text.toString()
                    if (TextUtils.isEmpty(fromDate)) {
                        isValid = false
                        txtInputLayoutFromDate.error = getString(R.string.msg_from_date)
                    } else if (TextUtils.isEmpty(toDate)) {
                        isValid = false
                        txtInputLayoutToDate.error = getString(R.string.msg_to_date)
                    }
                    val fromDateInMillis = getTimeStampFromDate(serverFormatDate(fromDate))
                    val toDateInMillis = getTimeStampFromDate(serverFormatDate(toDate))
                    if (isValid && fromDateInMillis > toDateInMillis) {
                        isValid = false
                        txtInputLayoutFromDate.error = getString(R.string.msg_from_date_is_greater_than_to_date)
                    }
                    if (isValid) {
                        txtInputLayoutFromDate.error = null
                        txtInputLayoutToDate.error = null
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        fetchData(fromDate, toDate)
                    }
                },
                R.string.label_clear_filter,
                View.OnClickListener {
                    fromDate = ""
                    toDate = ""
                    edtFromDate.setText(fromDate)
                    edtToDate.setText(toDate)
                    val dialog = (it as Button).tag as AlertDialog
                    //dialog.dismiss()
                },
                R.string.cancel,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                binding.root
        )
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    }
}