package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityReturnImpoundmentBinding
import com.sgs.citytax.ui.fragments.ReturnImpoundmentListFragment
import com.sgs.citytax.util.Constant

class ReturnImpoundmentActivity : BaseActivity(),
        ReturnImpoundmentListFragment.Listener {
    private lateinit var mBinding: ActivityReturnImpoundmentBinding
    private var fromScreen: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_return_impoundment)
        showToolbarBackButton(R.string.title_return_impondment)

        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU)
        }
        val fragment = ReturnImpoundmentListFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        fragment.arguments = bundle
        addFragment(fragment, false, R.id.container)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scan, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_scan) {
            val intent = Intent(this, ScanActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
            startActivity(intent)
            finish()

        } else if (id == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}