package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.COMNotes
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.adapter.NotesAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener


class LocalNotesMasterFragment : BaseFragment(), IClickListener, View.OnClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER


    override fun initComponents() {
        //endregion
        setViews()
        bindData()
        setListeners()
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


    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setViews() {

        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            mBinding.fabAdd.visibility = View.GONE

        } else {
            mBinding.fabAdd.visibility = View.VISIBLE
        }

        val itemDecor = DividerItemDecoration(context, RecyclerView.VERTICAL)
        mBinding.recyclerView.addItemDecoration(itemDecor)
        mBinding.recyclerView.adapter = NotesAdapter(this, fromScreen, mListener?.screenMode)
    }

    private fun bindData() {
        val adapter = (mBinding.recyclerView.adapter as NotesAdapter)
        adapter.clear()
        adapter.update(ObjectHolder.notes)
    }

    private fun setListeners() {
        //mBinding.fabAdd.setOnClickListener(this)
        mBinding.fabAdd.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = LocalNotesEntryFragment()
                fragment.setTargetFragment(this@LocalNotesMasterFragment, Constant.REQUEST_CODE_NOTES_ENTRY)
                mListener?.showToolbarBackButton(R.string.notes)
                mListener?.addFragment(fragment, true)
            }
        })
    }

    override fun onClick(v: View?) {
       /* if (R.id.fabAdd == v?.id) {
            val fragment = LocalNotesEntryFragment()
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_ENTRY)
            mListener?.showToolbarBackButton(R.string.notes)
            mListener?.addFragment(fragment, true)
        }*/
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.txtEdit -> {
                val fragment = LocalNotesEntryFragment()
                //region SetArguments
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_NOTES, obj as COMNotes?)
                fragment.arguments = bundle
                //endregion
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_ENTRY)
                mListener?.showToolbarBackButton(R.string.notes)
                mListener?.addFragment(fragment, true)
            }
            R.id.txtDelete -> {
                deleteNote(obj as COMNotes)
            }
        }
    }

    private fun deleteNote(comNotes: COMNotes) {
        if (null != comNotes?.NoteID) {
            val list = ObjectHolder.notes
            list.remove(comNotes)
            ObjectHolder.notes = list
            bindData()
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_NOTES_ENTRY)
            bindData()
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode
    }
}