package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemSecurityTaxProductBinding
import com.sgs.citytax.model.ProductItem
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatWithPrecision
import com.sgs.citytax.util.getString
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class SecurityTaxAdapter(
    private val mList: ArrayList<ProductItem>,
    iClickListener: IClickListener, notifyListener: NotifyQuantityChangeListener
) :
    RecyclerView.Adapter<SecurityTaxAdapter.ProductViewHolder>(), Filterable {

    private var iClickListener: IClickListener? = iClickListener
    private var notifyListener: NotifyQuantityChangeListener? = notifyListener
    var filterList: List<ProductItem> = mList

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SecurityTaxAdapter.ProductViewHolder {
        val mBinding: ItemSecurityTaxProductBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_security_tax_product, parent, false
        )
        val viewHolder = ProductViewHolder(mBinding, parent.context)
        viewHolder.setListeners()
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: SecurityTaxAdapter.ProductViewHolder,
        position: Int
    ) {

        holder.bind(filterList[position], iClickListener)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    /* override fun getItemViewType(position: Int): Int {
         return position
     }

     override fun getItemId(position: Int): Long {
         return position.toLong()
     }*/

    inner class ProductViewHolder(
        val binding: ItemSecurityTaxProductBinding,
        val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun setListeners() {
            binding.edtPersons.addTextChangedListener {
                //to prevent 0's prior to number (donot allow 0001 e.t.c)
                it?.toString()?.let { enteredVal ->
                    if (enteredVal.startsWith("0") && (enteredVal.length > 1) && enteredVal[1]
                            .toString() != "."
                    ) {
                        with(binding) {
                            edtPersons.setText(enteredVal.substring(1))
                            binding.edtPersons.setSelection(binding.edtPersons.length())
                        }
                    }
                }
                //end
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return@addTextChangedListener
                }
                filterList[adapterPosition].no_of_persons =
                    binding.edtPersons.text.toString().toBigIntegerOrNull() ?: BigInteger.ZERO
                filterList[adapterPosition].quantity =
                    filterList[adapterPosition].no_of_days.times(filterList[adapterPosition].no_of_persons)
                        .toBigDecimal()
                binding.tvManDaysValue.text =
                    String.format(
                        Locale.getDefault(),
                        "%d",
                        filterList[adapterPosition].quantity.toBigInteger()
                    )
                notifyListener?.onQuantityUpdated(filterList[adapterPosition])
            }

            binding.edtDays.addTextChangedListener {
                //to prevent 0's prior to number (donot allow 0001 e.t.c)
                it?.toString()?.let { enteredVal ->
                    if (enteredVal.startsWith("0") && (enteredVal.length > 1) && !(enteredVal.get(1)
                            .toString().equals("."))
                    ) {
                        binding.edtDays.setText(enteredVal.substring(1))
                        binding.edtDays.setSelection(binding.edtDays.length())
                    }
                }
                //end
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return@addTextChangedListener
                }
                filterList[adapterPosition].no_of_days =
                    binding.edtDays.text.toString().toBigIntegerOrNull() ?: BigInteger.ZERO
                filterList[adapterPosition].quantity =
                    filterList[adapterPosition].no_of_days.times(filterList[adapterPosition].no_of_persons)
                        .toBigDecimal()
                binding.tvManDaysValue.text =
                    String.format(
                        Locale.getDefault(),
                        "%d",
                        filterList[adapterPosition].quantity.toBigInteger()
                    )
                notifyListener?.onQuantityUpdated(filterList[adapterPosition])
            }
        }

        fun bind(
            cartItem: ProductItem,
            iClickListener: IClickListener?
        ) {
            binding.tvSalesProductName.text = cartItem.product?.item
            binding.tvNoOfDays.text = getString(R.string.no_of_days)
            binding.tvNoOfPersons.text = getString(R.string.no_of_persons)
            binding.tvManDays.text = getString(R.string.man_days)
            binding.edtDays.setText(String.format(Locale.getDefault(), "%d", cartItem.no_of_days))
            binding.edtPersons.setText(
                String.format(
                    Locale.getDefault(),
                    "%d",
                    cartItem.no_of_persons
                )
            )
            binding.tvManDaysValue.text =
                String.format(Locale.getDefault(), "%d", cartItem.quantity.toBigInteger())
            binding.tvPrdctUnitPrice.text = formatWithPrecision(
                cartItem.product?.unitPrice
                    ?: BigDecimal.ZERO
            )

            iClickListener?.let {
                binding.imbMinusDays.setOnClickListener {
                    binding.edtDays.requestFocus()
                    iClickListener.onClick(it, adapterPosition, cartItem)
                    binding.edtDays.setText(
                        String.format(
                            Locale.getDefault(),
                            "%d",
                            cartItem.no_of_days
                        )
                    )
                    binding.edtDays.setSelection(binding.edtDays.length())
                }
            }

            binding.imbPlusDays.setOnClickListener {
                binding.edtDays.requestFocus()
                iClickListener?.onClick(it, adapterPosition, cartItem)
                binding.edtDays.setText(
                    String.format(
                        Locale.getDefault(),
                        "%d",
                        cartItem.no_of_days
                    )
                )
                binding.edtDays.setSelection(binding.edtDays.length())
            }

            binding.imbMinusPersons.setOnClickListener {
                binding.edtPersons.requestFocus()
                iClickListener?.onClick(it, adapterPosition, cartItem)
                binding.edtPersons.setText(
                    String.format(
                        Locale.getDefault(),
                        "%d",
                        cartItem.no_of_persons
                    )
                )
                binding.edtPersons.setSelection(binding.edtPersons.length())
            }

            binding.imbPlusPersons.setOnClickListener {
                binding.edtPersons.requestFocus()
                iClickListener?.onClick(it, adapterPosition, cartItem)
                binding.edtPersons.setText(
                    String.format(
                        Locale.getDefault(),
                        "%d",
                        cartItem.no_of_persons
                    )
                )
                binding.edtPersons.setSelection(binding.edtPersons.length())
            }
        }
    }

    interface NotifyQuantityChangeListener {
        fun onQuantityUpdated(cartItem: ProductItem)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterString = p0.toString()
                filterList = if (filterString.isEmpty()) {
                    mList
                } else {
                    val resultList = ArrayList<ProductItem>()
                    for (row in mList) {
                        if (row.product?.item?.lowercase()
                                ?.contains(p0.toString().lowercase()) == true
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults


            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filterList = p1?.values as ArrayList<ProductItem>
                notifyDataSetChanged()
            }

        }
    }
}