package com.sgs.citytax.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityImpoundmentListBinding
import com.sgs.citytax.ui.fragments.ImpoundFilterDialogFragment
import com.sgs.citytax.ui.fragments.ImpoundmentListFragment
import com.sgs.citytax.ui.fragments.PendingAnimalReturnPayFragment

class ImpoundmentListActivity : BaseActivity(), ImpoundmentListFragment.Listener, ImpoundFilterDialogFragment.Listener,
    PendingAnimalReturnPayFragment.Listener {

    private lateinit var binding: ActivityImpoundmentListBinding
    private var fromScreen: Any? = null
    var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_impoundment_list)

        showToolbarBackButton(R.string.impoundment_list)
        attachFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_search_business, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        if (item.itemId == R.id.action_search) {
            val dialogFragment: ImpoundFilterDialogFragment = ImpoundFilterDialogFragment.newInstance()
            this.supportFragmentManager.let {
                dialogFragment.show(it, ImpoundFilterDialogFragment::class.java.simpleName)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun attachFragment() {
        fragment = ImpoundmentListFragment.newInstance()
        addFragment(fragment, true, R.id.container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {

    }

    override fun onApplyClick(impoundType: Int?, impoundSubType: Int?, vehNo: String, mobile: String) {
        (fragment as ImpoundmentListFragment).getFromSearchFilter(impoundType!!, impoundSubType!!, vehNo, mobile)

    }

    override fun onClearClick() {
        (fragment as ImpoundmentListFragment).getFromSearchFilter(0, 0, "", "")
    }
}
