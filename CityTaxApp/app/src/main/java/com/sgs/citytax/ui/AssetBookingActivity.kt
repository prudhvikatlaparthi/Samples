package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.AssetBooking
import com.sgs.citytax.api.response.ValidateAssetForAssignAndReturnResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityAssetBookingBinding
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class AssetBookingActivity : BaseActivity(),
        AssetBookingEntryFragment.Listener,
        AssetBookingLineEntryFragment.Listener,
        BusinessOwnerEntryFragment.Listener,
        BusinessOwnerSearchFragment.Listener,
        DocumentsMasterFragment.Listener,
        NotesMasterFragment.Listener,
        NotesEntryFragment.Listener,
        DocumentEntryFragment.Listener,
        EmailMasterFragment.Listener, EmailEntryFragment.Listener,
        CardMasterFragment.Listener, CardEntryFragment.Listener,
        PhoneMasterFragment.Listener, PhoneEntryFragment.Listener,
        AddressMasterFragment.Listener, AddressEntryFragment.Listener,
        AssetPostCheckListFragment.Listener,
        AssetPreCheckListFragment.Listener,
        AssetPostCheckListSummaryFragment.Listener,
        AssetPreCheckListSummaryFragment.Listener,
        AssetBookingDateSelectionFragment.Listener{

    private var binding: ActivityAssetBookingBinding? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mAssetBooking: AssetBooking? = null
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var assetId: Int? = 0
    private var validateAssetForAssignAndReturnResponse: ValidateAssetForAssignAndReturnResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_asset_booking)
        mScreenMode = Constant.ScreenMode.EDIT
        processIntent()
        showToolbarBackButton(R.string.asset_booking)
        if (mCode == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT)
            showToolbarBackButton(R.string.asset_assignment)
        else if (mCode == Constant.QuickMenu.QUICK_MENU_ASSET_RETURN)
            showToolbarBackButton(R.string.title_return_asset)
        setUpMasterFragment()
    }

    private fun processIntent() {
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_ASSET_BOOKING))
                mAssetBooking = it.getParcelable(Constant.KEY_ASSET_BOOKING)
            if (it.containsKey(Constant.KEY_ASSET_ID))
                assetId = it.getInt(Constant.KEY_ASSET_ID, 0)
            if (it.containsKey(Constant.KEY_VALIDATE_ASSET))
                validateAssetForAssignAndReturnResponse = it.getParcelable(Constant.KEY_VALIDATE_ASSET)

        }
    }

    private fun setUpMasterFragment() {
        when (mCode) {
            Constant.QuickMenu.QUICK_MENU_ASSET_RETURN -> {
                val fragment = AssetPostCheckListFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                bundle.putInt(Constant.KEY_ASSET_ID, assetId ?: 0)
                bundle.putParcelable(Constant.KEY_VALIDATE_ASSET, validateAssetForAssignAndReturnResponse)
                fragment.arguments = bundle
                addFragmentWithOutAnimation(fragment, false, R.id.container)
            }
            else -> {
                val fragment = AssetBookingEntryFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                bundle.putParcelable(Constant.KEY_ASSET_BOOKING, mAssetBooking)
                fragment.arguments = bundle
                addFragmentWithOutAnimation(fragment, false, R.id.container)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.container)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onBackPressed() {
        when (currentFragment) {
            is AssetBookingEntryFragment -> {
                if (mCode == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT)
                    showToolbarBackButton(R.string.asset_assignment)
                else if (mCode == Constant.QuickMenu.QUICK_MENU_ASSET_RETURN)
                    showToolbarBackButton(R.string.title_return_asset)
                else
                    showToolbarBackButton(R.string.asset_booking)

            }
            is DocumentsMasterFragment ->
                (currentFragment as DocumentsMasterFragment).onBackPressed()
            is NotesMasterFragment ->
                (currentFragment as NotesMasterFragment).onBackPressed()
            is EmailMasterFragment ->
                (currentFragment as EmailMasterFragment).onBackPressed()
            is CardMasterFragment ->
                (currentFragment as CardMasterFragment).onBackPressed()
            is PhoneMasterFragment ->
                (currentFragment as PhoneMasterFragment).onBackPressed()
            is AddressMasterFragment ->
                (currentFragment as AddressMasterFragment).onBackPressed()
        }
        super.onBackPressed()
    }

}