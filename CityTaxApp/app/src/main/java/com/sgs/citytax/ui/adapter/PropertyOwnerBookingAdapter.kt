package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.payload.PropertyOwnersPayload
import com.sgs.citytax.databinding.ItemPropertyOwnerBookingBinding
import com.sgs.citytax.model.PropertyOwnersData
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickMultiListener

class PropertyOwnerBookingAdapter(iClickListener: IClickMultiListener, code: Constant.QuickMenu, swipeLock: Boolean) : RecyclerView.Adapter<PropertyOwnerBookingAdapter.ViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickMultiListener? = iClickListener

//    private var mPropOwnerList: ArrayList<BusinessOwnership> = arrayListOf()
//    private var mPropNomineeList: ArrayList<BusinessOwnership> = arrayListOf()
//    private var mPropRelation: ArrayList<ComComboStaticValues> = arrayListOf()
    private var mPropertyOwnersData: ArrayList<PropertyOwnersData> = arrayListOf()

    private val mCode: Constant.QuickMenu = code
    private val mSwipeLock: Boolean = swipeLock

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_property_owner_booking, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mCode == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT || mSwipeLock)
            mBinderHelper.lockSwipe(position.toString())
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        val mPropOwner: PropertyOwnersData = mPropertyOwnersData[position]

        holder.bind(mPropOwner, mCode, mIClickListener)
    }

    override fun getItemCount(): Int {
        return mPropertyOwnersData.size
    }

    fun add(propOwner: PropertyOwnersData) {
        mPropertyOwnersData.add(propOwner)
        notifyDataSetChanged()
    }

    fun getPropertyOwnersList(): ArrayList<PropertyOwnersPayload> {
        val propertyOwnersPayloadList: ArrayList<PropertyOwnersPayload> = arrayListOf()
        for (obj in mPropertyOwnersData){
            val propertyOwnersPayload = PropertyOwnersPayload()
            propertyOwnersPayload.ownerAccountID = obj.owner?.accountID
            propertyOwnersPayload.nomineeAccountID = obj.nominee?.accountID
            propertyOwnersPayload.relationshipType = obj.relation?.code
            propertyOwnersPayloadList.add(propertyOwnersPayload)
        }
        return propertyOwnersPayloadList
    }

    fun clear() {
        mPropertyOwnersData.clear()
        notifyDataSetChanged()
    }

    fun remove(propOwner: PropertyOwnersData) {
        mPropertyOwnersData.remove(propOwner)
        notifyDataSetChanged()
    }

    fun replace(position: Int, propOwner: PropertyOwnersData) {
        mPropertyOwnersData.removeAt(position)
        mPropertyOwnersData.add(position, propOwner)
        notifyDataSetChanged()
    }

    fun addAll(propertyOwnersDataList: java.util.ArrayList<PropertyOwnersData>) {
        mPropertyOwnersData.clear()
        mPropertyOwnersData.addAll(propertyOwnersDataList)
    }

    fun checkOwnerExist(newOwnerData: PropertyOwnersData):Boolean {
        for (propertyOwnerData in mPropertyOwnersData){
            if (propertyOwnerData.owner?.accountID == newOwnerData.owner?.accountID) {
                return true
            }
        }
        return false
    }

    class ViewHolder(var binding: ItemPropertyOwnerBookingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propOwner: PropertyOwnersData, code: Constant.QuickMenu, iClickListener: IClickMultiListener?) {

            binding.tvOwnerName.text = propOwner.owner?.accountName
            if (propOwner.nominee != null) {
                binding.tvNomineeName.text = propOwner.nominee?.accountName
                binding.tvRelationShip.text = propOwner.relation?.code
            } else {
                binding.tvNomineeName.text = ""
                binding.tvRelationShip.text = ""
            }

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, propOwner)
                }
                binding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, propOwner)
                }
            }
        }
    }

}