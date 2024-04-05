package com.sgs.citytax.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.RowTaxBinding
import com.sgs.citytax.model.ProductDetails
import java.util.*
import kotlin.collections.ArrayList


class MunicipalTaxesAdapter(private var productsList: List<ProductDetails>) : RecyclerView.Adapter<MunicipalTaxesAdapter.TaxAdapterViewHolder>(), Filterable {
    private var filteredList: List<ProductDetails> = arrayListOf()

    init {
        filteredList = productsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxAdapterViewHolder {
        return TaxAdapterViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_tax, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: TaxAdapterViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    class TaxAdapterViewHolder(var binding: RowTaxBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(details: ProductDetails) {
            binding.txtCategory.text = details.category
            binding.txtProductCode.text = details.productcode
            binding.txtProductName.text = details.productname
            binding.txtActive.text= if (details.active.equals("Y")) binding.txtActive?.context?.resources?.getString(R.string.yes) else binding.txtActive?.context?.resources?.getString(R.string.no)
            Glide.with(context).load(details.defaultImage).placeholder(R.drawable.ic_place_holder).override(92, 92).into(binding.imgProduct)
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                filteredList = if (TextUtils.isEmpty(constraint)) {
                    productsList
                } else {
                    val newFilteredList: MutableList<ProductDetails> = ArrayList()
                    for (o in productsList) {
                        if (o.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
                            newFilteredList.add(o)
                        }
                    }
                    newFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                filterResults.count = filteredList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredList = results.values as List<ProductDetails>
                notifyDataSetChanged()
            }

        }
    }
}
