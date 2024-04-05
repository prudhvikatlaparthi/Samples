package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.InsertNotes
import com.sgs.citytax.databinding.FragmentNotesEntryBinding
import com.sgs.citytax.model.COMNotes
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.OnSingleClickListener

class NotesEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentNotesEntryBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var note: COMNotes? = null

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            note = arguments?.getParcelable(Constant.KEY_NOTES)
        }
        //endregion
        setViews()
        setListeners()
        bindData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {
        mBinding.edtTitle.isEnabled = action
        mBinding.edtDescription.isEnabled = action

        if (action)
            mBinding.btnSave.visibility = View.VISIBLE
        else
            mBinding.btnSave.visibility = View.GONE
    }

    private fun bindData() {
        mBinding.edtTitle.setText(note?.Subject)
        mBinding.edtDescription.setText(note?.Note)
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (validateViews())
                    saveNotes(preparePayload())
            }
        })
        //mBinding.btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
           /* R.id.btnSave -> {
                if (validateViews())
                    saveNotes(preparePayload())
            }*/
        }
    }

    private fun preparePayload(): COMNotes {

        val notes = COMNotes()

        if (mBinding.edtTitle.text != null)
            notes.Subject = mBinding.edtTitle.text.toString().trim()
        if (mBinding.edtDescription.text != null)
            notes.Note = mBinding.edtDescription.text.toString().trim()

        /*if (note?.UUID == null || TextUtils.isEmpty(note?.UUID))
            notes.UUID = java.util.UUID.randomUUID().toString()
        else
            notes.UUID = note?.UUID*/

        notes.NoteID = note?.NoteID ?: 0

        return notes

    }

    private fun saveNotes(notes: COMNotes) {
        val notesList: ArrayList<COMNotes> = arrayListOf()
        notesList.add(notes)

        val insertNotes = InsertNotes()
        if (NotesMasterFragment.primaryKey != 0) {
            insertNotes.primaryKeyValue = NotesMasterFragment.primaryKey.toString()
        } else if (!TextUtils.isEmpty(NotesMasterFragment.mPrimaryKey)) {
            insertNotes.primaryKeyValue = NotesMasterFragment.mPrimaryKey
        }
        insertNotes.notes = notesList
        insertNotes.tableName = NotesMasterFragment.getTableName(fromScreen)

        if (NotesMasterFragment.primaryKey != 0 || !TextUtils.isEmpty(NotesMasterFragment.mPrimaryKey)) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
            APICall.insertNotes(insertNotes, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 500)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        } /*else {
            mListener?.showAlertDialog("In complete flow")
            *//*when (fromScreen) {
                Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
                    ObjectHolder.registerBusiness.notes.add(notes)
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 500)
                }

                Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER -> {
                    ObjectHolder.registerBusiness.notes.add(notes)
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 500)
                }

                else -> {

                }
            }*//*
        }*/

    }

    private fun validateViews(): Boolean {
        if (TextUtils.isEmpty(mBinding.edtTitle.text)) {
            mListener?.showSnackbarMsg(getString(R.string.enter_subject))
            return false
        }
        if (TextUtils.isEmpty(mBinding.edtDescription.text)) {
            mListener?.showSnackbarMsg(getString(R.string.enter_description))
            return false
        }
        return true
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun popBackStack()
        fun showSnackbarMsg(message: String)
        fun showAlertDialog(message: String)
        fun showProgressDialog(message: Int)
        fun dismissDialog()
        var screenMode: Constant.ScreenMode

    }
}