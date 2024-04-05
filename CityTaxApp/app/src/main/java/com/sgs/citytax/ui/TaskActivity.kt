package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityTaskBinding
import com.sgs.citytax.ui.fragments.LocateDialogFragment
import com.sgs.citytax.ui.fragments.TaskEntryFragment
import com.sgs.citytax.ui.fragments.TaskFragment

class TaskActivity : BaseActivity(), TaskFragment.Listener, TaskEntryFragment.Listener, LocateDialogFragment.Listener {
    private lateinit var binding: ActivityTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_task)
        showToolbarBackButton(R.string.title_tasks)
        attachFragment()
    }

    private fun attachFragment() {
        addFragmentWithOutAnimation(TaskFragment(), true, R.id.taskContainer)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.taskContainer)

    override fun onBackPressed() {
        when (currentFragment) {
            is TaskFragment -> {
                (currentFragment as TaskFragment).onBackPressed()
            }
            is TaskEntryFragment -> {
                (currentFragment as TaskEntryFragment).onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, addToBackStack, R.id.taskContainer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onLatLonFound(inputLat: Double?, inputLon: Double?) {
        inputLat?.let { lat ->
            inputLon?.let {
                val fragment = this.supportFragmentManager.findFragmentById(R.id.taskContainer)
                if (fragment is TaskEntryFragment)
                    fragment.updateText(lat, it)
            }
        }
    }
}