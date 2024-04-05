package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.databinding.ItemShowBinding
import com.sgs.citytax.model.ShowsDetailsTable
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class ShowListAdapter(val listener: IClickListener, val screenMode: Constant.ScreenMode?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mShows: ArrayList<ShowsDetailsTable> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1
    private val binderHelper = ViewBinderHelper()


    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = ShowViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_show, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val showDetails: ShowsDetailsTable = mShows[position]
        when (getItemViewType(position)) {
            mItem -> {
                binderHelper.bind((holder as ShowViewHolder).mBinding.swipeLayout, position.toString())
                binderHelper.closeAll()
                (holder as ShowViewHolder).bind(showDetails, listener, screenMode)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(showDetails)
            }
        }
    }

    override fun getItemCount(): Int {
        return mShows.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mShows[position].isLoading)
            mLoading
        else mItem
    }

    class ShowViewHolder(val mBinding: ItemShowBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(details: ShowsDetailsTable, iClickListener: IClickListener?, screenMode: Constant.ScreenMode?) {
            details.showName?.let {
                mBinding.txtShowName.text = it
            }
            details.operatorType?.let {
                mBinding.txtOperatorType.text = it
            }
            details.billingCycle?.let {
                mBinding.txtBillingCycle.text = it
            }
            if(details.allowDelete=="N") {
                mBinding.txtDelete.visibility = View.GONE
            }

            details.active?.let {
                mBinding.txtActive.text = if (it =="Y") mBinding.txtActive.context.getString(R.string.yes) else mBinding.txtActive.context.getString(R.string.no)
            }

            if (screenMode == Constant.ScreenMode.VIEW) {
                mBinding.txtDelete.visibility = View.GONE
            }

            if (iClickListener != null) {
                mBinding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, details)
                    }
                })
                mBinding.txtDelete.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, details)
                    }
                })
            }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(details: ShowsDetailsTable) {
            if (details.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(showsDetailsTable: ShowsDetailsTable?) {
        mShows.add(showsDetailsTable!!)
        notifyItemInserted(mShows.size - 1)
    }

    fun addAll(showsList: List<ShowsDetailsTable?>) {
        for (mShowsList in showsList) {
            add(mShowsList)
        }
    }

    fun remove(showsDetailsTable: ShowsDetailsTable?) {
        val position: Int = mShows.indexOf(showsDetailsTable)
        if (position > -1) {
            mShows.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<ShowsDetailsTable> {
        return mShows
    }

    fun clear() {
        mShows = arrayListOf()
        notifyDataSetChanged()
    }

}