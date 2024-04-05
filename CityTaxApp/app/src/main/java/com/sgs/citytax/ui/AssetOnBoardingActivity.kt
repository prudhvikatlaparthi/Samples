package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GetUpdateAsset
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityAssetOnboardingBinding
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class AssetOnBoardingActivity : BaseActivity(), AssetOnBoardingFragment.Listener,
        AssetInsuranceMasterFragment.Listener, AssetInsuranceEntryFragment.Listener,
        AssetFitnessMasterFragment.Listener, AssetFitnessEntryFragment.Listener,
        AssetMaintenanceMasterFragment.Listener, AssetMaintenanceEntryFragment.Listener,
        DocumentsMasterFragment.Listener, DocumentEntryFragment.Listener,
        LocateDialogFragment.Listener,NotesMasterFragment.Listener,NotesEntryFragment.Listener {

    private lateinit var binding: ActivityAssetOnboardingBinding
    private var fromScreen: Constant.QuickMenu? = null
    private val assetOnBoardingFragment = AssetOnBoardingFragment()
    private var updateAsset: GetUpdateAsset? = null
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var mSycoTaxID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_asset_onboarding)
        mScreenMode = Constant.ScreenMode.EDIT
        processIntent()
        setTitle()
        attachFragment()
    }

    private fun setTitle() {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING)
            showToolbarBackButton(R.string.title_asset_onboarding)
        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET)
            showToolbarBackButton(R.string.title_update_asset)
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.QuickMenu
            if (intent.hasExtra(Constant.KEY_UPDATE_ASSET))
                updateAsset = intent.getParcelableExtra<GetUpdateAsset>(Constant.KEY_UPDATE_ASSET)
            if (intent.hasExtra(Constant.KEY_SYCO_TAX_ID))
                mSycoTaxID = intent.getStringExtra(Constant.KEY_SYCO_TAX_ID)
        }
    }

    private fun attachFragment() {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_UPDATE_ASSET, updateAsset)
        bundle.putString(Constant.KEY_SYCO_TAX_ID, mSycoTaxID)
        assetOnBoardingFragment.arguments = bundle
        addFragment(assetOnBoardingFragment, false, R.id.assetContainer)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.assetContainer)

    override fun onBackPressed() {
        when (currentFragment) {
            is AssetInsuranceMasterFragment -> {
                (currentFragment as AssetInsuranceMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is AssetFitnessMasterFragment -> {
                (currentFragment as AssetFitnessMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is AssetMaintenanceMasterFragment -> {
                (currentFragment as AssetMaintenanceMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is AssetOnBoardingFragment -> {
                (currentFragment as AssetOnBoardingFragment).onBackPressed()
                super.onBackPressed()
            }

            is DocumentsMasterFragment -> {
                (currentFragment as DocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }

            is NotesMasterFragment -> {
                (currentFragment as NotesMasterFragment).onBackPressed()
                super.onBackPressed()
            }

            else ->
                super.onBackPressed()
        }
        when (currentFragment) {
            is AssetOnBoardingFragment -> {
                setTitle()
            }
            is AssetInsuranceMasterFragment -> {
                showToolbarBackButton(R.string.insurance_details)
            }
            is AssetFitnessMasterFragment -> {
                showToolbarBackButton(R.string.fitness_details)
            }
            is AssetMaintenanceMasterFragment -> {
                showToolbarBackButton(R.string.maintenance_details)
            }
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.assetContainer)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onLatLonFound(latitude: Double?, longitude: Double?) {
        assetOnBoardingFragment.let {
            assetOnBoardingFragment.bindLatLongs(latitude, longitude)
        }
    }
}