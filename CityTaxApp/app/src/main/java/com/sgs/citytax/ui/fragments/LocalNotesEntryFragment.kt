package com.sgs.citytax.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.databinding.FragmentNotesEntryBinding
import com.sgs.citytax.model.COMNotes
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.OnSingleClickListener
import java.util.*

class LocalNotesEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentNotesEntryBinding
    private var mListener: Listener? = null

    private var note: COMNotes? = null

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            note = arguments?.getParcelable(Constant.KEY_NOTES)
        }
        if (note == null) note = COMNotes()

        setViews()
        setListeners()
        bindData()
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


    private fun bindData() {
        note?.let {
            mBinding.edtTitle.setText(note?.Subject)
            mBinding.edtDescription.setText(note?.Note)
        }
    }

    private fun setListeners() {
        //mBinding.btnSave.setOnClickListener(this)
        mBinding.btnSave.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (validateViews())
                    saveNotes()
            }
        })
    }




    private fun saveNotes() {

        val mNotes = COMNotes()
        if (mBinding.edtTitle.text != null)
            mNotes.Subject = mBinding.edtTitle.text.toString().trim()
        if (mBinding.edtDescription.text != null)
            mNotes.Note = mBinding.edtDescription.text.toString().trim()
        if (note?.NoteID == 0)
            mNotes.NoteID = 0
        else mNotes.NoteID = note?.NoteID


        val list = ObjectHolder.notes
        var index = -1


        if (list.isNotEmpty()) {
            for (item in list) {
                if (item.NoteID == note?.NoteID && index == -1) {
                    index = list.indexOf(item)
                    break
                }
            }
        }
        if (index == -1)
            list.add(mNotes)
        else
            list[index] = mNotes
        ObjectHolder.notes = arrayListOf()
        ObjectHolder.notes.addAll(list)


        Handler().postDelayed({
            targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, null)
            mListener?.popBackStack()
        }, 500)


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
        targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, null)
    }

    interface Listener {
        fun popBackStack()
        fun showSnackbarMsg(message: String)
        fun showAlertDialog(message: String)
        fun showProgressDialog(message: Int)
        fun dismissDialog()
        var screenMode: Constant.ScreenMode

    }

    private fun getRandomNumber(): Int {
        val min = 1000
        val max = 9999
        return Random().nextInt(max - min + 1) + min
    }

    override fun onClick(v: View?) {

    }
}