package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityImpoundmentBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_DOCUMENT
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU

class ImpoundmentActivity : BaseActivity(),
        ImpoundmentTypeMasterFragment.Listener,
        ImpoundmentTypeEntryFragment.Listener,
        ImpoundmentEntryFragment.Listener,
        BusinessOwnerSearchFragment.Listener,
        VehicleSearchFragment.Listener,
        LocateDialogFragment.Listener,
        BusinessSearchFragment.Listener,
        VehicleTicketHistoryFragment.Listener,
        DriverTicketHistoryFragment.Listener,
        BusinessOwnerEntryFragment.Listener,
        NotesMasterFragment.Listener,
        NotesEntryFragment.Listener,
        PhoneMasterFragment.Listener,
        PhoneEntryFragment.Listener,
        AddressMasterFragment.Listener,
        AddressEntryFragment.Listener,
        CardMasterFragment.Listener,
        CardEntryFragment.Listener,
        EmailMasterFragment.Listener,
        EmailEntryFragment.Listener,
        DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener,
        LocalDocumentsMasterFragment.Listener,
        LocalDocumentEntryFragment.Listener {

    private lateinit var mBinding: ActivityImpoundmentBinding
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE
    private var mDocument: COMDocumentReference = COMDocumentReference()
    private var fragment = ImpoundmentEntryFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_impoundment)
        showToolbarBackButton(R.string.title_impondment)
        processIntent()
        val bundle = Bundle()
        bundle.putSerializable(KEY_QUICK_MENU, mCode)
        bundle.putParcelable(KEY_DOCUMENT, mDocument)
        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment, false, R.id.container)
    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(KEY_QUICK_MENU))
                mCode = it.getSerializableExtra(KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.hasExtra(KEY_DOCUMENT))
                mDocument = it.getParcelableExtra(KEY_DOCUMENT) as COMDocumentReference
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return false
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onBackPressed() {
        when (currentFragment) {
            is LocalDocumentsMasterFragment -> {
                (currentFragment as LocalDocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is AddressMasterFragment -> {
                (currentFragment as AddressMasterFragment).onBackPressed()
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
            is PhoneMasterFragment -> {
                (currentFragment as PhoneMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is ImpoundmentTypeMasterFragment -> {
                (currentFragment as ImpoundmentTypeMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is EmailMasterFragment -> {
                (currentFragment as EmailMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is CardMasterFragment -> {
                (currentFragment as CardMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is ImpoundmentEntryFragment -> {
                val impEntryFrag = currentFragment as ImpoundmentEntryFragment
                when {
                    impEntryFrag.isAnimalOwnPopUpVisible() -> {
                        impEntryFrag.onBackPressed()
                    }
                    impEntryFrag.isAnimalImpoundPopUpVisible() -> {
                        impEntryFrag.onBackPressed()
                    }
                    impEntryFrag.isPoprcVehicleSearchUpVisible() -> {
                        impEntryFrag.onBackPressed()
                    }
                    impEntryFrag.isPoprcVehCtzOwnUpVisible() -> {
                        impEntryFrag.onBackPressed()
                    }
                    impEntryFrag.isPoprcvDriverUpVisible() -> {
                        impEntryFrag.onBackPressed()
                    }
                    impEntryFrag.isPopRcGoodsOwnerUpVisible() -> {
                        impEntryFrag.onBackPressed()
                    }
                    else -> {
                        showConfirmationDialog(impEntryFrag)
                    }
                }
            }
            else ->
                super.onBackPressed()

        }
    }

    private fun showConfirmationDialog(impEntryFrag: ImpoundmentEntryFragment) {
        showAlertDialog(R.string.do_you_want_to_exit,
                R.string.yes,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    if (impEntryFrag?.isImpoundTypeListAvailable()) impEntryFrag.onBackPressed()
                    finish()
                },
                R.string.no,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                })
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.container)
    }

    override fun onLatLonFound(latitude: Double?, longitude: Double?) {
        fragment.bindLatLongs(latitude, longitude)
    }

}