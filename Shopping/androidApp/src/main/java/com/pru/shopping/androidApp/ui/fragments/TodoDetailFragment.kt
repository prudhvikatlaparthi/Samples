package com.pru.shopping.androidApp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pru.shopping.androidApp.R
import com.pru.shopping.androidApp.databinding.FragmentTodoDetailsBinding
import com.pru.shopping.androidApp.ui.BaseFragment
import com.pru.shopping.androidApp.utils.Resource
import com.pru.shopping.androidApp.utils.hide
import com.pru.shopping.androidApp.utils.show
import com.pru.shopping.androidApp.viewmodels.TodoDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoDetailFragment : BaseFragment(R.layout.fragment_todo_details) {
    private lateinit var todoDetailBinding: FragmentTodoDetailsBinding
    private val todoViewModel by viewModels<TodoDetailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar("Post user check",false, false)
        todoDetailBinding = FragmentTodoDetailsBinding.bind(view)
        todoDetailBinding.saveSubmit.setOnClickListener {
            todoViewModel.postUser(todoDetailBinding.etEmail.text.toString())
        }
        setupObserver()
    }

    private fun setupObserver() {
        todoViewModel.todoDetail.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    todoDetailBinding.pbView.show()
                }
                is Resource.Success -> {
                    todoDetailBinding.pbView.hide()
                    Toast.makeText(requireContext(), "Success saved", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    todoDetailBinding.pbView.hide()
                    Toast.makeText(
                        requireContext(),
                        "Error! ${it.errorMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}