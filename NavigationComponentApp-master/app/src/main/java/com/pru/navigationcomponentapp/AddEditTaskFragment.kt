package com.pru.navigationcomponentapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pru.navigationcomponentapp.databinding.FragmentAddEditBinding

class AddEditTaskFragment : BaseFragment(R.layout.fragment_add_edit), OnBackPressedListener {

    private val TAG = "AddEditTaskFragment"
    private lateinit var secondBinding: FragmentAddEditBinding
    private val args: AddEditTaskFragmentArgs by navArgs()
    private val viewType: String by lazy { args.viewType }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        secondBinding = FragmentAddEditBinding.bind(view)
        if (viewType == RequestOption.REQUEST_ADD_TASK.name) {
            setupToolBar("Add Task", enableDrawer = false)
        } else {
            secondBinding.etValue.setText(args.task?.taskName ?: "")
            setupToolBar(
                "Update Task", enableDrawer = false
            )
        }
        secondBinding.fabFix.setOnClickListener {
            val action = AddEditTaskFragmentDirections.actionSecondFragmentToFixFragment()
            findNavController().navigate(action)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.submit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_submit -> {
                if (viewType == RequestOption.REQUEST_ADD_TASK.name) {
                    setFragmentResult(
                        RequestOption.REQUEST_ADD_TASK.name,
                        bundleOf("NewTask" to secondBinding.etValue.text.toString())
                    )
                } else {
                    val task = args.task!!
                    task.taskName = secondBinding.etValue.text.toString()
                    setFragmentResult(
                        RequestOption.REQUEST_UPDATE_TASK.name,
                        bundleOf(
                            "TASK" to task,
                        )
                    )
                }
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onBackPressed() {
        showAlertDialog(
            message = "Are you sure to go back?",
            positiveButtonName = "Yes",
            positiveListener = {
                Log.i(TAG, "onBackPressed: yes")
                findNavController().popBackStack()
            },
            negativeButtonName = "No",
            negativeListener = {
                Log.i(TAG, "onBackPressed: No")
            })
    }

}