package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.RowGameMachineBinding
import com.sgs.citytax.model.GamingMachineTax
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.displayFormatDate

class GameMachineAdapter(iClickListener: IClickListener, private val fromScreen: Constant.QuickMenu, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<GameMachineAdapter.ViewHolder>() {

    private var mArrayList: ArrayList<GamingMachineTax> = arrayListOf()
    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.row_game_machine, parent, false), parent.context)
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

    fun add(gamingMachineTax: GamingMachineTax) {
        mArrayList.add(gamingMachineTax)
        notifyItemInserted(mArrayList.size - 1)

    }

    fun update(list: List<GamingMachineTax>) {
        for (item: GamingMachineTax in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: RowGameMachineBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(gamingMachineTax: GamingMachineTax, iClickListener: IClickListener?) {
            binding.tvRegistrationDate.text = displayFormatDate(gamingMachineTax.registrationDate)
            binding.tvGameMachineSycotaxID.text = gamingMachineTax.gamingMachineSycotaxID
            binding.tvGameSerialNumber.text = gamingMachineTax.serialNo
            binding.tvGameMachineType.text = gamingMachineTax.gamingMachineType
            binding.tvStatus.text = if (gamingMachineTax.active.equals("Y"))  binding.tvStatus?.context?.resources?.getString(R.string.active) else binding.tvStatus?.context?.resources?.getString(R.string.inactive)

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, gamingMachineTax)
                    }
                })
                binding.txtDelete.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, gamingMachineTax)
                    }
                })
            }

        }
    }

}
