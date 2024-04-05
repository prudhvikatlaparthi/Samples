package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.RowCartBinding
import com.sgs.citytax.model.CartTax
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*

class CartAdapter(iClickListener: IClickListener, private val fromScreen: Constant.QuickMenu, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var mArrayList: ArrayList<CartTax> = arrayListOf()
    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.row_cart, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (edit == Constant.ScreenMode.VIEW)
            holder.binding.txtDelete.visibility = View.GONE
        else
            holder.binding.txtDelete.visibility = View.VISIBLE

        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun update(list: List<CartTax>) {
        for (item: CartTax in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: RowCartBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartTax: CartTax, iClickListener: IClickListener?) {
            binding.tvRegistrationDate.text = displayFormatDate(cartTax.registrationDate)
            binding.tvCartSycotaxID.text = cartTax.cartSycoTaxID
            binding.tvCartNo.text = cartTax.cartNo
            binding.tvCartType.text = cartTax.cartType
            if ( cartTax.active == "Y")
                binding.tvStatus.text = getString(R.string.active)
            else
                binding.tvStatus.text = getString(R.string.inactive)

            binding.tvStatus.text = if (cartTax.active.equals("Y"))  binding.tvStatus?.context?.resources?.getString(R.string.active) else binding.tvStatus?.context?.resources?.getString(R.string.inactive)

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, cartTax)
                    }
                })
                binding.txtDelete.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, cartTax)
                    }
                })

            }

        }
    }

}
