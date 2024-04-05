package com.sgs.citytax.ui.custom.customDropDown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R

class CustomDropdownMenu<T>(mContext: Context?, var dropDownClickListener: DropDownClickListener) : PopupWindow(mContext), CustomDropdownAdapter.CategorySelectedListener {

    var context: Context? = mContext
    var recyclerView: RecyclerView? = null
    var dropdownAdapter: CustomDropdownAdapter<T>? = null

    init {
        setUpView()
    }

    private fun setUpView() {

        val view: View = LayoutInflater.from(context).inflate(R.layout.popup_window_layout, null)
        recyclerView = view.findViewById(R.id.popupWindow_recyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        dropdownAdapter = CustomDropdownAdapter(this)
        recyclerView?.adapter = dropdownAdapter

        contentView = view;
    }

    fun updateList(list: ArrayList<T>) {
        dropdownAdapter?.updateList(list)
    }

    fun clearList() {
        dropdownAdapter?.clearList()
    }

    interface DropDownClickListener {
        fun onItemClick(position: Int, item: Any?)
    }

    override fun onCategorySelected(position: Int, item: Any?) {
        dropDownClickListener.onItemClick(position, item)
    }
}