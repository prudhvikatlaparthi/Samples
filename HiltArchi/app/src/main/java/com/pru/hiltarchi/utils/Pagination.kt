package com.pru.hiltarchi.utils

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Pagination(
    initialPageNumber: Int,
    defaultPageSize: Int,
    recyclerView: RecyclerView?,
    listener: (Int, Int) -> Unit
) {
    private var defaultInitialpagenumber = 1
    private var defaultDefaultpagesize = 10
    var initialPageNumber = 1
        private set
    var defaultPageSize = 10
        private set
    var totalRecords = 0
    private var stop_pagination = false
    private var isScrolled = false
    private val recyclerView: RecyclerView?
    private val listener: (Int, Int) -> Unit
    private val TAG = "Pagination"

    init {
        this.initialPageNumber = initialPageNumber
        this.defaultPageSize = defaultPageSize
        defaultInitialpagenumber = initialPageNumber
        defaultDefaultpagesize = defaultPageSize
        this.recyclerView = recyclerView
        this.listener = listener
        setListener()
    }

    fun resetInitialPageNumber() {
        initialPageNumber = 1
    }

    fun setDefaultValues() {
        initialPageNumber = defaultInitialpagenumber
        defaultPageSize = defaultDefaultpagesize
        stop_pagination = true
        isScrolled = false
        listener.invoke(initialPageNumber, defaultPageSize)
    }

    private fun setListener() {
        if (recyclerView == null) {
            return
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = recyclerView.layoutManager!!.childCount
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                var firstVisibleItemPosition = 0
                if (recyclerView.layoutManager is LinearLayoutManager) {
                    firstVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                } else if (recyclerView.layoutManager is GridLayoutManager) {
                    firstVisibleItemPosition =
                        (recyclerView.layoutManager as GridLayoutManager?)!!.findFirstVisibleItemPosition()
                }
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 3 && firstVisibleItemPosition >= 0) {
                    if (!stop_pagination && !isScrolled && totalItemCount != totalRecords) {
                        initialPageNumber++
                        isScrolled = true
                        listener.invoke(initialPageNumber, defaultPageSize)
                    }
                }
            }
        })
    }

    fun stopPagination(size: Int) {
        stop_pagination = if (size == 0) {
            true
        } else {
            size % defaultPageSize != 0 || defaultPageSize == totalRecords
        }
    }

    fun setIsScrolled(isScrolled: Boolean) {
        this.isScrolled = isScrolled
    }

    fun doNextCall() {
        val totalItemCount = recyclerView!!.layoutManager!!.itemCount
        if (totalItemCount < totalRecords) {
            initialPageNumber++
            listener.invoke(initialPageNumber, defaultPageSize)
        } else {
            Log.d(TAG, "nextCall: finished")
        }
    }

    fun doForceNextCall() {
        initialPageNumber++
        listener.invoke(initialPageNumber, defaultPageSize)
    }

}