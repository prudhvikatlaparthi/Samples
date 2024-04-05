package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityImpoundmentReturnHistoryBinding
import com.sgs.citytax.ui.fragments.DocumentEntryFragment
import com.sgs.citytax.ui.fragments.DocumentsMasterFragment
import com.sgs.citytax.ui.fragments.ImpoundmentReturnHistoryfragment
import com.sgs.citytax.ui.fragments.ImpoundmentReturnfragment
import com.sgs.citytax.util.Constant


class ImpoundmentReturnHistoryActivity : BaseActivity(),
        ImpoundmentReturnHistoryfragment.Listener,
        ImpoundmentReturnfragment.Listener,
        DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener {

    private lateinit var binding: ActivityImpoundmentReturnHistoryBinding
    private var fromScreen: Any? = null
    private var mImpondmentReturn: ImpondmentReturn? = null
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_impoundment_return_history)
        showToolbarBackButton(R.string.title_return_impondment)
        processIntent()
        setUpMasterFragment()
    }


    private fun processIntent() {
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_IMPOUNDMENT_RETURN))
                mImpondmentReturn = it.getParcelable(Constant.KEY_IMPOUNDMENT_RETURN)

        }

    }

    private fun setUpMasterFragment() {
        val fragment = ImpoundmentReturnHistoryfragment()
        val bundle = Bundle()
        bundle.putParcelable(Constant.KEY_IMPOUNDMENT_RETURN, mImpondmentReturn)
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        fragment.arguments = bundle
        showToolbarBackButton(R.string.title_return_impondment)
        addFragment(fragment, true)
    }


    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onBackPressed() {
        when (currentFragment) {
            is ImpoundmentReturnfragment -> {
                (currentFragment as ImpoundmentReturnfragment).onBackPressed()
            }
            is DocumentEntryFragment -> {
                (currentFragment as DocumentEntryFragment).onBackPressed()
            }
            is DocumentsMasterFragment -> {
                (currentFragment as DocumentsMasterFragment).onBackPressed()
            }
            else ->
                super.onBackPressed()
        }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }


    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

}