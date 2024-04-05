package com.pru.hiltarchi.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.pru.hiltarchi.R
import com.pru.hiltarchi.adapters.TodoAdapter
import com.pru.hiltarchi.databinding.FragmentTodoBinding
import com.pru.hiltarchi.listeners.OnResumeListener
import com.pru.hiltarchi.models.TodoItem
import com.pru.hiltarchi.ui.BaseFragment
import com.pru.hiltarchi.utils.Pagination
import com.pru.hiltarchi.utils.Resource
import com.pru.hiltarchi.viewmodels.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoFragment : BaseFragment(R.layout.fragment_todo), OnResumeListener {
    private lateinit var todoBinding: FragmentTodoBinding
    private val todoAdapter: TodoAdapter by lazy { TodoAdapter(::adapterItemClickListener) }

    private val todoViewModel by viewModels<TodoViewModel>()
    private lateinit var pagination: Pagination
    private val resultList: MutableList<TodoItem> = mutableListOf()
    private val TAG = "TodoFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoBinding = FragmentTodoBinding.bind(view)
        setupObserver()
        todoBinding.apply {
            rcView.run {
                this.layoutManager = LinearLayoutManager(requireContext())
                this.adapter = todoAdapter
            }
        }
        pagination = Pagination(1, 10, todoBinding.rcView) { pageIndex, pageSize ->
            todoViewModel.getTodos()
        }
        setFragmentResultListener("Task") { _, _ ->
            todoViewModel.getTodos()
        }
        Glide.with(requireContext())
            .load("https://s3.ap-south-1.amazonaws.com/uat.justbilling/androidretailpro/10195.png?X-Amz-Expires=86400&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIASG4FHB7ULOOTV36B/20210312/ap-south-1/s3/aws4_request&X-Amz-Date=20210312T063446Z&X-Amz-SignedHeaders=host&X-Amz-Signature=20a2cbc437bee94c8b21ff1dda4aa45e0746bc7633e8700e861d90b8e28c767b")
            .circleCrop()
            .into(todoBinding.topView)
        todoBinding.relativeLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                todoViewModel.motionProgress = p3
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {

            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

        })
        todoBinding.relativeLayout.setProgress(todoViewModel.motionProgress)
        todoBinding.topView.setOnClickListener {
            todoViewModel.displayToast()
        }
    }

    private fun adapterItemClickListener(position: Int) {
        val action =
            TodoFragmentDirections.actionHomeFragmentToTodoDetailFragment(todoID = position ?: -1)
        findNavController().navigate(action)

        /*resultList.removeAt(it)
        todoAdapter.differ.submitList(resultList)
        todoAdapter.notifyDataSetChanged()*/
    }

    private fun setupObserver() {
        todoViewModel.todos.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Log.i(TAG, "setupObserver: ${it.data?.data}")
                    pagination.setIsScrolled(false)
                    resultList.addAll(it.data?.data!!)
                    todoAdapter.differ.submitList(resultList)
                }
                is Resource.Error -> {
                }
            }
        })
        todoViewModel.dos.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResumedCalled() {
        todoViewModel.getTodos()
    }
}

