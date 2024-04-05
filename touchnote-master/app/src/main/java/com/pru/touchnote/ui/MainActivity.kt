package com.pru.touchnote.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pru.newsapp.mvvm.adapters.UsersAdapter
import com.pru.touchnote.R
import com.pru.touchnote.utils.Constants.PAGE_LIMIT
import com.pru.touchnote.utils.Resource
import com.pru.touchnote.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private val usersAdapter: UsersAdapter by lazy { UsersAdapter() }
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        listeners()
        usersAdapter.setOnClickListener {
            Intent(this, UserDetailActivity::class.java).apply {
                putExtra("DETAILS", it)
                startActivity(this)
            }
        }
        mainViewModel.usersData.observe(this, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    errorWrapper.visibility = GONE
                    rvUsers.visibility = VISIBLE
                    response.data?.let { newsResponse ->
                        usersAdapter.differ.submitList(newsResponse.data.toList())
                        val totalPages = newsResponse.meta?.pagination?.pages ?: 0
                        isLastPage =
                            if (totalPages == 0) true else (totalPages + 1) == mainViewModel.page
                        if (isLastPage) {
                            rvUsers.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    if (mainViewModel.page == 1) {
                        errorWrapper.visibility = VISIBLE
                        rvUsers.visibility = GONE
                    }
                    response.message?.let { message ->
                        tv_error.text = message
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    errorWrapper.visibility = GONE
                    showProgressBar()
                }

            }
        })
    }

    private fun listeners() {
        floatingActionButton.setOnClickListener {
            Intent(this, PostUserActivity::class.java).apply {
                startActivity(this)
            }
        }
        bt_retry.setOnClickListener {
//            mainViewModel.fetchUsers()
            mainViewModel.fetchUsersRXJ()
        }
    }

    private fun hideProgressBar() {
        userProgressBar.visibility = GONE
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        if (mainViewModel.page == 1) {
            userProgressBar.visibility = VISIBLE
        } else {
            paginationProgressBar.visibility = VISIBLE
        }
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= PAGE_LIMIT
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
//                mainViewModel.fetchUsers()
                mainViewModel.fetchUsersRXJ()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        rvUsers.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = usersAdapter
            addOnScrollListener(scrollListener)
        }
    }
}