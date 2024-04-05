package com.pru.shopping.androidApp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pru.shopping.androidApp.R
import com.pru.shopping.androidApp.adapters.TodoAdapter
import com.pru.shopping.androidApp.databinding.FragmentHomeBinding
import com.pru.shopping.androidApp.ui.BaseFragment
import com.pru.shopping.androidApp.utils.*
import com.pru.shopping.androidApp.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private lateinit var homeBinding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()
    private val todoAdapter: TodoAdapter by lazy {
        TodoAdapter(
            listOf()
        )
    }
    private val TAG = "HomeFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeBinding = FragmentHomeBinding.bind(view)
        setupToolBar("Home", true, false)
        initialize()
        setupObservers()
    }

    private fun initialize() {
        homeBinding.rcView.apply {
            layoutManager = LinearLayoutManager(requireContext()).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            adapter = todoAdapter
        }

        listeners()
    }

    private fun listeners() {
        todoAdapter.setItemClickListener {
            Log.i(TAG, "listeners: $it")
            val action = HomeFragmentDirections.actionHomeFragmentToTodoDetailFragment()
            findNavController().navigate(action)
        }

        /*homeBinding.fabView.setOnClickListener {
            homeViewModel.fetchData()
        }*/
    }

    private fun setupObservers() {
        homeViewModel.todosState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    homeBinding.pbView.show()
                }
                is Resource.Success -> {
                    it.data?.apply {
                        homeBinding.pbView.hide()
                        todoAdapter.updateData(it.data)
                    } ?: run {
                        displayError(kNoDataMessage)
                    }
                }
                is Resource.Error -> {
                    displayError(it.errorMessage ?: kErrorMessage)
                }
            }
        })
    }

    private fun displayError(error: String) {
        homeBinding.pbView.hide()
        homeBinding.errorView.show()
        homeBinding.errorView.text = error
    }
}