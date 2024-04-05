package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityStockTransferBinding
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class StockTransferActivity : BaseActivity(), StockListFragment.Listener,
StockEntryFragment.Listener, DueNoticeImagesFragment.Listener{

    lateinit var mBinding : ActivityStockTransferBinding

    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_stock_transfer)
        setContentView(mBinding.root)

        showToolbarBackButton(getString(R.string.stock_transfer))
        attachFragment()
    }

    private fun attachFragment() {
        addFragmentWithOutAnimation(StockListFragment(), true, R.id.container)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onBackPressed() {
        showToolbarBackButton(getString(R.string.stock_transfer))
        when (currentFragment) {
            is StockListFragment -> {
                (currentFragment as StockListFragment).onBackPressed()
            }
            is StockEntryFragment -> {
               (currentFragment as StockEntryFragment).onBackPressed()
            }
            is DueNoticeImagesFragment -> {
                (currentFragment as DueNoticeImagesFragment).OnpopBackStack()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}