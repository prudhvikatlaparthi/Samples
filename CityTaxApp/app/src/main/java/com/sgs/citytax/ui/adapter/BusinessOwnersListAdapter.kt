package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemBusinessOwnerBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.getString

class BusinessOwnersListAdapter(var iClickListener: IClickListener, private val isImpContra : Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val binderHelper = ViewBinderHelper()
    private val mItem = 0
    private val mLoading = 1
    private var mbusinessOwners: ArrayList<BusinessOwnership> = arrayListOf()
    private lateinit var context: Context
    private var mCode: Constant.QuickMenu? = null

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    fun setScreenCode(code: Constant.QuickMenu) {
        this.mCode = code
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        context = parent.context
        when (viewType) {
            mItem -> {
                viewHolder = BusinessOwnerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_business_owner, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val businessOwner: BusinessOwnership = mbusinessOwners[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as BusinessOwnerViewHolder).bind(businessOwner, mCode, iClickListener,isImpContra)

            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(businessOwner)
            }
        }
    }

    class BusinessOwnerViewHolder(var binding: ItemBusinessOwnerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(businessOwner: BusinessOwnership, code: Constant.QuickMenu?, iClickListener: IClickListener?,isImpContra: Boolean) {
            if (isImpContra){
                binding.llMobileNo.isVisible = true
                binding.llSycoTaxID.isVisible = false
                binding.tvMblNo.text = businessOwner.phone
            }
            if (businessOwner.accountTypeCode.equals("CUS")) {
                binding.txtOwnerNameLabel.text = binding.txtOwnerNameLabel.context.getString(R.string.citizen_name)
                binding.txtIdLabel.text = binding.txtIdLabel.context.getString(R.string.citizen_id)
                binding.tvOwner.text = businessOwner.accountName
                binding.tvBusinessOwnerID.text = businessOwner.citizenID
                if(!businessOwner.citizenSycoTaxID.isNullOrEmpty() && !isImpContra){
                    binding.tvSycoTaxID.text = businessOwner.citizenSycoTaxID
                    binding.llSycoTaxID.visibility = View.VISIBLE
                }else{
                    binding.tvSycoTaxID.text = ""
                    binding.llSycoTaxID.visibility = View.GONE
                }
            }else if(businessOwner.accountTypeCode.equals("CRO")){
                if(businessOwner.statusCode == Constant.OrganizationStatus.ACTIVE.value){
                    binding.llMobileNo.isVisible = true
                    binding.txtOwnerNameLabel.text = binding.txtOwnerNameLabel.context.getString(R.string.business_name)
                    binding.tvOwner.text = businessOwner.accountName
                    binding.txtIdLabel.text = binding.txtOwnerNameLabel.context.getString(R.string.title_business_owner)
                    binding.tvBusinessOwnerID.text = businessOwner.owners

                }else {
                    binding.txtOwnerNameLabel.text =
                        binding.txtOwnerNameLabel.context.getString(R.string.business_owner_name)
                    binding.txtIdLabel.text =
                        binding.txtOwnerNameLabel.context.getString(R.string.syco_tax_id)
                    binding.tvOwner.text = businessOwner.accountName
                    binding.tvBusinessOwnerID.text = businessOwner.sycoTaxId
                }
            }else{
                binding.txtOwnerNameLabel.text = binding.txtOwnerNameLabel.context.getString(R.string.citizen_name)
                binding.txtIdLabel.text = binding.txtIdLabel.context.getString(R.string.citizen_id)
                if(isImpContra){
                    binding.llMobileNo.isVisible = true
                    binding.llSycoTaxID.isVisible = false
                    binding.tvMblNo.text = businessOwner.phone
                }else{
                    binding.llSycoTaxID.visibility = View.VISIBLE
                }
            }

            if (iClickListener != null) {
                binding.container.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        iClickListener.onClick(it, adapterPosition, businessOwner)
                    }
                }
            }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(businessOwner: BusinessOwnership) {
            if (businessOwner.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mbusinessOwners.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mbusinessOwners[position].isLoading)
            mLoading
        else mItem
    }

    fun add(businessOwner: BusinessOwnership) {
        mbusinessOwners.add(businessOwner)
        notifyItemInserted(mbusinessOwners.size - 1)

    }

    fun addAll(businessOwner: List<BusinessOwnership>) {
        for (business in businessOwner) {
            add(business)
        }
    }

    fun clear() {
        mbusinessOwners.clear()
        notifyDataSetChanged()
    }

    fun remove(businessOwner: BusinessOwnership?) {
        val position: Int = mbusinessOwners.indexOf(businessOwner)
        if (position > -1) {
            mbusinessOwners.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}