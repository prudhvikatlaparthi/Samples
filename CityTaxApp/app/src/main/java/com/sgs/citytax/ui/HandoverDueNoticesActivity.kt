package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityHandoverDueNoticesBindingImpl
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.ImageHelper

class HandoverDueNoticesActivity : BaseActivity(), HandoverDueNoticesFragment.Listener,
    HandoverDueDocumentsMasterFragment.Listener {

    private lateinit var binding: ActivityHandoverDueNoticesBindingImpl
    private lateinit var sycoTaxID: String
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_NOTICES
    lateinit var fragment : HandoverDueNoticesFragment
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()
    private var count: Int = 0
    private var fileData: String? = ""
    private var fileNameWithExt: String? = ""
    private var reportingDateTime: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_handover_due_notices)
        showToolbarBackButton(R.string.title_handover_due_notices)
        processIntent()
        setFragments()

    }

    override fun onBackPressed() {
            when (currentFragment) {
                is HandoverDueDocumentsMasterFragment -> {
                    showConfirmationDialog(currentFragment as HandoverDueDocumentsMasterFragment)
                    return
                }
                is HandoverDueNoticesFragment -> {
                   finish()
                }

                else ->
                    super.onBackPressed()
            }

    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.container)
    }

    fun rebindView() {
        fragment.bindData()
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, true, R.id.container)
    }


    private fun processIntent() {
        intent?.extras?.let {
            sycoTaxID = it.getString(Constant.KEY_CUSTOMER_ID, "")
            mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun setFragments() {

        fragment = HandoverDueNoticesFragment.newInstance(sycoTaxID, mCode)
        showToolbarBackButton(R.string.title_handover_due_notices)
        addFragment(fragment, true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }


    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    private fun showConfirmationDialog(currentFragment: HandoverDueDocumentsMasterFragment) {
        var message :  Int = 0
        if (ObjectHolder.dueDocumentCount!! > 0)
        {
            if (ObjectHolder.dueDocumentCount!!>1)
            {
                message = R.string.exit_message_s
            }
            else {
                message = R.string.exit_message
            }
        }
        else
        {
            message = R.string.do_you_want_to_exit
        }
        showAlertDialog(message,
            R.string.yes,
            {
                val dialog = (it as Button).tag as AlertDialog
                dialog.dismiss()
                if (ObjectHolder.dueDocumentCount!! > 0)
                {
                    updateDueNotices(currentFragment)
                }
                else
                {
                    currentFragment.onBackPressed()
                    super.onBackPressed()
                    showToolbarBackButton(R.string.title_handover_due_notices)
                }

            },
            R.string.no,
            {
                val dialog = (it as Button).tag as AlertDialog
                dialog.dismiss()
                count = 0
                comDocumentReferences.clear()
                comDocumentReferences = ObjectHolder.comDocumentReferences
                if (ObjectHolder.comDocumentReferences.size!! > 0)
                {
                    comDocumentReferences = ObjectHolder.comDocumentReferences

                    for (obj in comDocumentReferences)
                    {
                        deleteDocument(obj,currentFragment)
                    }

                }
            })
    }

    private fun deleteDocument(comDocumentReference: COMDocumentReference?,currentFragment: HandoverDueDocumentsMasterFragment) {

        if (null != comDocumentReference?.documentID) {
            showProgressDialog(R.string.msg_please_wait)
            APICall.deleteDocument(comDocumentReference.documentReferenceID!!.toInt(),object : ConnectionCallBack<Boolean> {
                    override fun onSuccess(response: Boolean) {
                        dismissDialog()
                        count++

                        if (comDocumentReferences.size == count)
                        {
                            currentFragment.onBackPressed()
                            currentFragment.OnpopBackStack()
                            showToolbarBackButton(R.string.title_handover_due_notices)
                            comDocumentReferences.clear()
                        }
                    }

                    override fun onFailure(message: String) {
                        dismissDialog()
                        showAlertDialog(message)
                        comDocumentReferences.clear()
                    }
                })
        }
    }

    private fun updateDueNotices(currentFragment: HandoverDueDocumentsMasterFragment) {
        if(currentFragment.validateView()) {
            showProgressDialog(R.string.msg_please_wait)
            fileData = currentFragment.getBitmap()
            fileNameWithExt = ObjectHolder.dueNoticeID.toString() + "_HandoverDueNotice.jpg"
            val reportingDateTime = currentFragment.getReportingDateTime()
            val recipientName = currentFragment.getRecipientName()
            val mobileNum = currentFragment.getMobileNum()


            APICall.UpdateDueNotices(
                ObjectHolder.dueNoticeID,
                fileData,
                fileNameWithExt,
                reportingDateTime,
                recipientName,
                mobileNum,
                object :
                    ConnectionCallBack<Boolean> {
                    override fun onSuccess(response: Boolean) {
                        dismissDialog()
                        ObjectHolder.clearDueNoticeID()
                        currentFragment.OnpopBackStack()
                        showToolbarBackButton(R.string.title_handover_due_notices)
                        rebindView()
                    }

                    override fun onFailure(message: String) {
                        dismissDialog()
                        showAlertDialog(message)
                    }
                })
        }
    }



}