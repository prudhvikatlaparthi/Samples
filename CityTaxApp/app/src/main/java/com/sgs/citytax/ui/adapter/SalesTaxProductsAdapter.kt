package com.sgs.citytax.ui.adapter

import android.content.Context
import android.content.DialogInterface
import android.text.InputFilter.LengthFilter
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemSalesTaxProductBinding
import com.sgs.citytax.model.ProductItem
import com.sgs.citytax.util.*
import java.math.BigDecimal
import java.util.*

class SalesTaxProductsAdapter(
    private val mList: ArrayList<ProductItem>,
    iClickListener: IClickListener, notifyListener: NotifyQuantityChangeListener
) :
    RecyclerView.Adapter<SalesTaxProductsAdapter.ProductViewHolder>(), Filterable {

    private var dialog: AlertDialog? = null
    private var iClickListener: IClickListener? = iClickListener
    private var notifyListener: NotifyQuantityChangeListener? = notifyListener
    var filterList: List<ProductItem> = mList

//    var mBinding: ItemSalesTaxProductBinding? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SalesTaxProductsAdapter.ProductViewHolder {
        val mBinding: ItemSalesTaxProductBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_sales_tax_product, parent, false
        )
        val viewHolder = ProductViewHolder(mBinding, parent.context)
        viewHolder.setListeners()
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: SalesTaxProductsAdapter.ProductViewHolder,
        position: Int
    ) {

        holder.bind(filterList[position], iClickListener)
    }

    override fun getItemCount(): Int {

        return filterList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ProductViewHolder(val binding: ItemSalesTaxProductBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun setListeners() {
            binding.edtPrdctQuantity.addTextChangedListener {
                //to prevent 0's prior to number (donot allow 0001 e.t.c)
                it?.toString()?.let { enteredQty ->
                    if (enteredQty.startsWith("0") && (enteredQty.length > 1) && enteredQty[1]
                            .toString() != "."
                    )
                        binding.edtPrdctQuantity.setText(it.toString().substring(1))
                }

                if (filterList[adapterPosition].product?.inventoryAllowed == "Y") {
                    if (it.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO >
                        filterList[adapterPosition].product?.stockInHand ?: BigDecimal.ZERO
                    ) {
                        // do not allow more than StockInHand or less than 0
                        //if amount entered more than StockInHand value, then replace edittext with existing amount
                        if (allowFraction(filterList[adapterPosition])) {
                            binding.edtPrdctQuantity.setText(filterList[adapterPosition].quantity.toString())
                        } else {
                            binding.edtPrdctQuantity.setText(
                                filterList[adapterPosition].quantity.toInt().toString()
                            )
                        }

                        if (dialog == null) {
                            val dialogBuilder = AlertDialog.Builder(context)
                            dialogBuilder.setTitle(null)
                            dialogBuilder.setMessage(context.getString(R.string.qyt_greater_value_warning))
                            dialogBuilder.setPositiveButton(
                                getString(R.string.ok)
                            ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
                            dialogBuilder.setCancelable(false)
                            dialog = dialogBuilder.create()
                            dialog!!.show()
                        } else if (!dialog!!.isShowing) {
                            dialog?.show()
                        }
                    } else {
                        filterList[adapterPosition].quantity =
                            binding.edtPrdctQuantity.text?.toString()?.toBigDecimalOrNull()
                                ?: BigDecimal.ZERO
                        notifyListener?.onQuantityUpdated(filterList[adapterPosition])
                    }
                } else {
                    filterList[adapterPosition].quantity =
                        binding.edtPrdctQuantity.text?.toString()?.toBigDecimalOrNull()
                            ?: BigDecimal.ZERO
                    notifyListener?.onQuantityUpdated(filterList[adapterPosition])
                }
            }
        }

        fun bind(
            cartItem: ProductItem,
            iClickListener: IClickListener?
        ) {
            if (allowFraction(cartItem)) {
                binding.edtPrdctQuantity.keyListener = DigitsKeyListener.getInstance("0123456789.")
                binding.edtPrdctQuantity.filters =
                    arrayOf(DecimalInputFilter(2), LengthFilter(12))
            } else {
                binding.edtPrdctQuantity.keyListener = DigitsKeyListener.getInstance("0123456789")
                binding.edtPrdctQuantity.filters =
                    arrayOf(DecimalInputFilter(0), LengthFilter(8))
            }

            //if StockInHand is negative or 0 then disable quantity edittext
            if (cartItem.product?.inventoryAllowed == "Y") {
                binding.edtPrdctQuantity.isEnabled =
                    cartItem.product?.stockInHand ?: BigDecimal.ZERO > BigDecimal.ZERO
            } else {
                binding.edtPrdctQuantity.isEnabled = true
            }

            if (cartItem.product?.validityApplicable == "Y") {
                binding.tvPrdctValidity.isVisible = true
                var exprydt = Date()
                if (cartItem.product?.validForMonths ?: 0 > 0) {
                    exprydt = addMoths(
                        Date(),
                        cartItem.product?.validForMonths!!
                    )
                }
                binding.tvPrdctValidity.text = HtmlCompat.fromHtml(
                    getString(R.string.txt_subscription_end_date) + ": <b>" + displayFormatDate(
                        exprydt
                    ) + "</b>", HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            } else {
                binding.tvPrdctValidity.isVisible = false
                binding.tvPrdctValidity.text = getString(R.string.txt_subscription_end_date)
            }

            binding.tvSalesProductName.text = cartItem.product?.item

            if (cartItem.product?.inventoryAllowed == "N") {
                binding.stockInHandLayout.visibility = View.GONE
            } else {
                binding.stockInHandLayout.visibility = View.VISIBLE
                binding.tvPrdctStockInHand.text = getString(R.string.stock_in_hand)
                binding.tvPrdctStockInHandValue.text =
                    cartItem.product?.stockInHand?.stripTrailingZeros()?.toPlainString()
            }


            binding.tvPrdctUnitPrice.text = formatWithPrecision(
                cartItem.product?.unitPrice
                    ?: BigDecimal.ZERO
            )
            if (allowFraction(cartItem)) {
                binding.edtPrdctQuantity.setText(cartItem.quantity.toString())
            } else {
                binding.edtPrdctQuantity.setText(cartItem.quantity.toInt().toString())
            }
            iClickListener?.let {
                binding.imbPrdctMinusQty.setOnClickListener {
                    if (cartItem.product?.inventoryAllowed == "Y" && cartItem.product?.stockInHand ?: BigDecimal.ZERO <= BigDecimal.ZERO) {
                        return@setOnClickListener
                    }
                    binding.edtPrdctQuantity.requestFocus()
                    iClickListener.onClick(it, adapterPosition, cartItem)
                    if (allowFraction(cartItem)) {
                        binding.edtPrdctQuantity.setText(cartItem.quantity.toString())
                    } else {
                        binding.edtPrdctQuantity.setText(cartItem.quantity.toInt().toString())
                    }
                }
                binding.imbPrdctPlusQty.setOnClickListener {
                    if (cartItem.product?.inventoryAllowed == "Y" && cartItem.product?.stockInHand ?: BigDecimal.ZERO <= BigDecimal.ZERO) {
                        return@setOnClickListener
                    }
                    binding.edtPrdctQuantity.requestFocus()
                    iClickListener.onClick(it, adapterPosition, cartItem)
                    if (allowFraction(cartItem)) {
                        binding.edtPrdctQuantity.setText(cartItem.quantity.toString())
                    } else {
                        binding.edtPrdctQuantity.setText(cartItem.quantity.toInt().toString())
                    }
                }
            }
        }
    }

    fun allowFraction(cartItem: ProductItem) =
        cartItem.product?.allwfrctnlqty == "Y"

    interface NotifyQuantityChangeListener {
        fun onQuantityUpdated(cartItem: ProductItem)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterString = p0.toString()
                if (filterString.isEmpty()) {
                    filterList = mList
                } else {
                    val resultList = ArrayList<ProductItem>()
                    for (row in mList) {
                        if (row.product?.item?.lowercase()
                                ?.contains(p0.toString().lowercase()) == true
                        ) {
                            resultList.add(row)
                        }
                    }
                    filterList = resultList
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