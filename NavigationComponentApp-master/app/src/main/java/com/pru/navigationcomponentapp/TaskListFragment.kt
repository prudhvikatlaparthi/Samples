package com.pru.navigationcomponentapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pru.navigationcomponentapp.databinding.FragmentTaskListBinding

enum class RequestOption {
    REQUEST_ADD_TASK,
    REQUEST_UPDATE_TASK,
}

class TaskListFragment() : BaseFragment(R.layout.fragment_task_list) {

    private lateinit var firstBinding: FragmentTaskListBinding

    private val dataList = mutableListOf(
        Task(taskName = "Apple", id = 0), Task(taskName = "Banana", id = 1),
        Task(taskName = "Tomato", id = 2), Task(taskName = "PineApple", id = 3),
    )

    private val taskListAdapter: TaskListAdapter by lazy { TaskListAdapter(dataList) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolBar("Tasks")
        firstBinding = FragmentTaskListBinding.bind(view)

        firstBinding.rcContainerList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskListAdapter
        }

        taskListAdapter.setOnCardListener {
            val action = TaskListFragmentDirections.actionFirstFragmentToSecondFragment(
                viewType = RequestOption.REQUEST_UPDATE_TASK.name,
                task = it
            )
            findNavController().navigate(action)
        }

        setHasOptionsMenu(true)

        setFragmentResultListener(RequestOption.REQUEST_ADD_TASK.name) { _, bundle ->
            val data = bundle.getString("NewTask")
            data?.let {
                dataList.add(Task(taskName = it, id = dataList.size))
                taskListAdapter.notifyItemInserted(dataList.size)
            }
        }

        setFragmentResultListener(RequestOption.REQUEST_UPDATE_TASK.name) { _, bundle ->
            val data = bundle.getParcelable("TASK") as Task?
            data?.let {
                dataList[it.id].taskName = it.taskName
                taskListAdapter.notifyItemChanged(it.id)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_add -> {
                val action =
                    TaskListFragmentDirections.actionFirstFragmentToSecondFragment(viewType = RequestOption.REQUEST_ADD_TASK.name)
                findNavController().navigate(action)
                true
            }
            R.id.item_logout -> {
                val action = TaskListFragmentDirections.actionNavOneFragmentToNavLogout()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}