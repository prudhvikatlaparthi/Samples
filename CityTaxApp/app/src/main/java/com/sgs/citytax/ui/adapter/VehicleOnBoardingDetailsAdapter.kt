package com.sgs.citytax.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemNotesBinding
import com.sgs.citytax.databinding.ItemVehicleOwnershipEntryBinding
import com.sgs.citytax.model.VehicleDetails
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.displayFormatDate

class VehicleOnBoardingDetailsAdapter(iClickListener: IClickListener, private val fromScreen: Constant.QuickMenu, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<VehicleOnBoardingDetailsAdapter.NotesViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener
    private var mArrayList: ArrayList<VehicleDetails> = arrayListOf()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_vehicle_ownership_entry, parent, false
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

    fun reset(list: ArrayList<VehicleDetails>) {
        mArrayList = list
        notifyDataSetChanged()
    }

    fun update(list: List<VehicleDetails>) {
        for (item: VehicleDetails in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    class NotesViewHolder(var binding: ItemVehicleOwnershipEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(vehicleDetails: VehicleDetails, iClickListener: IClickListener?) {
            binding.tvOwnerName.text = vehicleDetails.accountName
            binding.tvFromDate.text = displayFormatDate(vehicleDetails.fromDate)
            if (!TextUtils.isEmpty(vehicleDetails.toDate)) {
                binding.llToDate.visibility = View.VISIBLE
                binding.tvToDate.text = displayFormatDate(vehicleDetails.toDate)
            } else {
                binding.llToDate.visibility = View.GONE
            }

            /* if (VehicleDetailsWithOwnerResponse.NoteID == 0)
                 binding.txtDelete.visibility = View.VISIBLE
             else binding.txtDelete.visibility = View.GONE*/

            binding.txtDelete.setOnClickListener (object : OnSingleClickListener(){
                override fun onSingleClick(v: View?) {
                    iClickListener?.onClick(v!!, adapterPosition, vehicleDetails)
                }
            })
            binding.txtEdit.setOnClickListener (object : OnSingleClickListener(){
                override fun onSingleClick(v: View?) {
                    iClickListener?.onClick(v!!, adapterPosition, vehicleDetails)
                }
            })
        }
    }
}