package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemNotesBinding
import com.sgs.citytax.model.COMNotes
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class NotesAdapter(iClickListener: IClickListener, private val fromScreen: Constant.QuickMenu, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener
    private var mArrayList: ArrayList<COMNotes> = arrayListOf()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_notes, parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        if (edit == Constant.ScreenMode.VIEW) {
            holder.binding.txtDelete.visibility = View.GONE
        }

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT)
            mBinderHelper.lockSwipe(position.toString())
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun reset(list: ArrayList<COMNotes>) {
        mArrayList = list
        notifyDataSetChanged()
    }

    fun update(list: List<COMNotes>) {
        for (item: COMNotes in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    class NotesViewHolder(var binding: ItemNotesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comNotes: COMNotes, iClickListener: IClickListener?) {
            binding.tvNoteTitle.text = comNotes.Subject
            binding.tvDescription.text = comNotes.Note

            /* if (comNotes.NoteID == 0)
                 binding.txtDelete.visibility = View.VISIBLE
             else binding.txtDelete.visibility = View.GONE*/


            binding.txtEdit.setOnClickListener(object : OnSingleClickListener(){
                override fun onSingleClick(v: View?) {
                    iClickListener?.onClick(v!!, adapterPosition, comNotes)

                }
            })
            binding.txtDelete.setOnClickListener(object : OnSingleClickListener(){
                override fun onSingleClick(v: View?) {
                    iClickListener?.onClick(v!!, adapterPosition, comNotes)

                }
            })
        }
    }
}