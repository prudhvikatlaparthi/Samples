package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AdjustmentListDetails
import com.sgs.citytax.api.response.AdjustmentListReturn
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.databinding.FragmentAdjustmentsListBinding
import com.sgs.citytax.model.AdjustmentsListResults
import com.sgs.citytax.ui.adapter.AdjustmentsListAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.text.SimpleDateFormat
import java.util.*

class AdjustmentsListFragment : BaseFragment(), IClickListener {
    private lateinit var binding: FragmentAdjustmentsListBinding
    private val adjustmentsListAdapter: AdjustmentsListAdapter by lazy { AdjustmentsListAdapter(this) }
    private lateinit var pagination: Pagination
    private val resultList: MutableList<AdjustmentsListResults> = mutableListOf()
    private var fromDate: String? = null
    private var toDate: String? = null
    private var mListener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_adjustments_list, container, false)
        initComponents()
        return binding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    override fun initComponents() {
        /*if (MyApplication.getPrefHelper().adjustmentsFromDate.isEmpty() && MyApplication.getPrefHelper().adjustmentsToDate.isEmpty()) {
            showDateRangeSelection()
        } else {
            bindData()
        }*/
        showDateRangeSelection()
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.recyclerView.adapter = adjustmentsListAdapter
        pagination = Pagination(1, 10, binding.recyclerView) { pageNumber, PageSize ->
            bindData(pageNumber, pageSize = PageSize)
        }
        binding.fabAdd.setOnClickListener (object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val fragment = AdjustmentsEntryFragment.newInstance()
                mListener?.screenMode = Constant.ScreenMode.ADD
                fragment.setTargetFragment(this@AdjustmentsListFragment, Constant.REQUEST_CODE_ADJUSTMENT)
                mListener?.addFragment(fragment, true)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_filter) {
            showDateRangeSelection()
        }
        return true
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

    private fun bindData(pageIndex: Int = 1, pageSize: Int = 10) {
        if (pageIndex == 1) {
            binding.recyclerView.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            resetRecyclerAdapter()
            mListener?.showProgressDialog()
        } else {
            binding.ProgressBar.isVisible = true
        }
        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
            val calender = Calendar.getInstance(Locale.getDefault())
            calender.add(Calendar.DAY_OF_YEAR, -1)
            fromDate = getDate(calender.time, DateFormat)
            toDate = getDate(Date(), DateFormat)
        }
        val adjstmentsList = AdjustmentListDetails(
            fromDate = fromDate,
            toDate = toDate,
            pageIndex = pageIndex,
            pageSize = pageSize
        )
        APICall.getAdjustmentsList(
            adjstmentsList,
            object : ConnectionCallBack<AdjustmentListReturn> {
                override fun onSuccess(response: AdjustmentListReturn) {
                    try {
                        if (pageIndex == 1) {
                            response.totalRecordsFound?.let {
                                pagination.totalRecords = it
                            }
                            resultList.clear()
                        }
                        if (response.adjustmentsListResults?.size ?: 0 > 0) {
                            pagination.stopPagination(response.adjustmentsListResults?.size!!)
                            response.adjustmentsListResults?.let { dataList ->
                                resultList.addAll(dataList)
                                adjustmentsListAdapter.differ.submitList(resultList)
                                adjustmentsListAdapter.notifyDataSetChanged()
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
                    binding.ProgressBar.isVisible = false
                }

                override fun onFailure(message: String) {
                    if (pageIndex == 1) {
                        resetRecyclerAdapter()
                    }
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    binding.ProgressBar.isVisible = false
                }

            })
    }

    private fun resetRecyclerAdapter() {
        resultList.clear()
        adjustmentsListAdapter.differ.submitList(resultList)
        adjustmentsListAdapter.notifyDataSetChanged()
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun finish()
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
        var screenMode: Constant.ScreenMode
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val adjustmentsListItem : AdjustmentsListResults = obj as AdjustmentsListResults
        val fragment = AdjustmentsEntryFragment()
        mListener?.screenMode = Constant.ScreenMode.VIEW
        val bundle = Bundle()
        bundle.putParcelable(Constant.KEY_STOCK_MANAGEMENT,adjustmentsListItem)
        fragment.arguments = bundle
        fragment.setTargetFragment(this@AdjustmentsListFragment, Constant.REQUEST_CODE_ADJUSTMENT)
        mListener?.addFragment(fragment, true)
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    fun onBackPressed() {
        mListener?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_ADJUSTMENT) {
            bindData()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}