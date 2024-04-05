package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemAssetListForReturnBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.AssetListForReturn
import com.sgs.citytax.model.TaxNoticeHistoryList
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond

class AssetListForReturnAdapter(val listener:Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var assetsList:ArrayList<AssetListForReturn> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder :RecyclerView.ViewHolder?=null
        when(viewType){
            mItem ->{
               viewHolder = AssetListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_asset_list_for_return,parent,false))
            }
            mLoading ->{
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_progress,parent,false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val assetList = assetsList[position]
        when(getItemViewType(position)){
            mItem ->{
                (holder as AssetListViewHolder).bind(assetList,listener,position)
            }
            mLoading ->{
                (holder as LoadingViewHolder).bind(assetList)
            }
        }
    }

    override fun getItemCount(): Int {
        return assetsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (assetsList[position].isLoading)
            mLoading
        else mItem
    }



    class AssetListViewHolder(val mBinding:ItemAssetListForReturnBinding): RecyclerView.ViewHolder(mBinding.root){
        fun bind(assetList:AssetListForReturn,listener: Listener,position:Int){
            assetList.assetNumber?.let {
                mBinding.tvAssetName.text = it
            }
            assetList.assetSycoTaxId?.let {
                mBinding.tvAssetSycoTaxId.text = it
            }
            assetList.assetCategory?.let {
                mBinding.tvAssetCategory.text =it
            }
            assetList.assignDate?.let {
                mBinding.tvAssignDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            assetList.bookingEndDate?.let {
                mBinding.tvReturnDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            mBinding.llRootView.setOnClickListener {
                listener.onItemClick(assetList,position)
            }

        }
    }
    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetList: AssetListForReturn) {
            if (assetList.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(assetList: AssetListForReturn?){
        assetsList.add(assetList!!)
        notifyItemInserted(assetsList.size -1)
    }

    fun addAll(assetsList: List<AssetListForReturn?>) {
        for (mAsset in assetsList) {
            add(mAsset)
        }
    }

    fun remove(mAssetList: AssetListForReturn?) {
        val position: Int = assetsList.indexOf(mAssetList)
        if (position > -1) {
            assetsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<AssetListForReturn> {
        return assetsList
    }

    fun clear() {
        assetsList = arrayListOf()
        notifyDataSetChanged()
    }

    interface Listener{
        fun onItemClick(assetList: AssetListForReturn,position: Int)
    }
}