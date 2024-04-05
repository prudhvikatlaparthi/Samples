package com.sgs.citytax.ui


import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityAdjustmentsBinding
import com.sgs.citytax.ui.fragments.AdjustmentsEntryFragment
import com.sgs.citytax.ui.fragments.AdjustmentsListFragment
import com.sgs.citytax.ui.fragments.DueNoticeImagesFragment
import com.sgs.citytax.util.Constant

class AdjustmentsActivity : BaseActivity(), AdjustmentsListFragment.Listener,
    AdjustmentsEntryFragment.Listener,FragmentCommunicator,DueNoticeImagesFragment.Listener {
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var binding: ActivityAdjustmentsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_adjustments)

        showToolbarBackButton(getString(R.string.adjustments))
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun attachFragment() {
        addFragmentWithOutAnimation(AdjustmentsListFragment(), true, R.id.container)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }
    override fun onBackPressed() {
        showToolbarBackButton(getString(R.string.adjustments))
        when (currentFragment) {
            is AdjustmentsListFragment -> {
                (currentFragment as AdjustmentsListFragment).onBackPressed()
            }
            is AdjustmentsEntryFragment -> {
                (currentFragment as AdjustmentsEntryFragment).onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }
}