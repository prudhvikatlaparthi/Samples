package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.RowPropertyTaxBinding
import com.sgs.citytax.model.PropertyTax4Business
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.formatDate

class PropertyTaxMasterAdapter(iClickListener: IClickListener, private val fromScreen: Constant.QuickMenu, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<PropertyTaxMasterAdapter.ViewHolder>() {

    private var mArrayList: ArrayList<PropertyTax4Business> = arrayListOf()
    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener
    private var taxRuleBook:Constant.TaxRuleBook = Constant.TaxRuleBook.COM_PROP

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.row_property_tax, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if (edit == Constant.ScreenMode.VIEW)
//            holder.binding.txtDelete.visibility = View.GONE
//        else
//            holder.binding.txtDelete.visibility = View.VISIBLE

        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(this.taxRuleBook, mArrayList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun update(list: List<PropertyTax4Business>) {
        for (item: PropertyTax4Business in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    fun setTaxRuleBook(taxRuleBook: Constant.TaxRuleBook) {
        this.taxRuleBook = taxRuleBook
    }

    class ViewHolder(var binding: RowPropertyTaxBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taxRuleBook: Constant.TaxRuleBook, taxDetails: PropertyTax4Business, iClickListener: IClickListener?) {
            if(taxRuleBook.Code == Constant.TaxRuleBook.LAND_PROP.name){
                binding.tvpropertyName.text= binding.tvPropertyName.context.getString(R.string.land_name)
                binding.tvSycotaxId.text= binding.tvPropertyName.context.getString(R.string.land_syco_tax_id)
                binding.tvPropertyType.text= binding.tvPropertyName.context.getString(R.string.land_type)
            }
            else{
                binding.tvpropertyName.text= binding.tvPropertyName.context.getString(R.string.property_name)
                binding.tvSycotaxId.text= binding.tvPropertyName.context.getString(R.string.property_syco_tax_id)
                binding.tvPropertyType.text= binding.tvPropertyName.context.getString(R.string.property_type)
            }


            binding.tvPropertyRegistartionDate.text = formatDate( taxDetails.registrationDate)
            binding.tvPropertyName.text = taxDetails.propertyName
            binding.tvPropertySycoTaxId.text = taxDetails.propertySycoTaxID
            binding.tvPropetyType.text = taxDetails.propertyType
            binding.tvStatus.text = taxDetails.status

//                    binding.tvStatus.text = if (taxDetails.status.equals("Y"))  binding.tvStatus?.context?.resources?.getString(R.string.active) else binding.tvStatus?.context?.resources?.getString(R.string.inactive)


            if (iClickListener != null) {
//                binding.txtDelete.setOnClickListener {
//                    iClickListener.onClick(it, adapterPosition, taxDetails)
//                }
                binding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, taxDetails)
                    }
                })
            }

        }
    }

}
