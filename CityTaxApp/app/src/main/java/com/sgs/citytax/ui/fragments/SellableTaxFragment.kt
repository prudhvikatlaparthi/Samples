package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.GenericServiceResponse
import com.sgs.citytax.api.response.RoundingMethod
import com.sgs.citytax.api.response.SellableProduct
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentSellableTaxBinding
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.model.CartItem
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.FragmentCommunicator
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.util.*
import java.math.BigDecimal

class SellableTaxFragment : BaseFragment() {

    private lateinit var binding: FragmentSellableTaxBinding
    private var listener: FragmentCommunicator? = null
    private var selllableProductsList: List<SellableProduct> = listOf()
    private var customer: BusinessOwnership? = null
    private var roundingMethod: RoundingMethod? = null

    companion object {
        fun newInstance() = SellableTaxFragment()
        var cartItem: CartItem? = null
    }

    override fun initComponents() {
        setProductViews()
        initListeners()
        loadProducts()
        getDefaultRoundingMethod()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = if (parentFragment != null)
                parentFragment as FragmentCommunicator
            else context as FragmentCommunicator
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sellable_tax, container, false)
        initComponents()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_SCAN_PRODUCT && resultCode == Activity.RESULT_OK) {
            listener?.showToolbarBackButton(R.string.title_sales_tax)
            data?.let {
                var count: Int = 0
                val productCode = it.getStringExtra(Constant.KEY_SCAN_PRODUCT_CODE)
                for (product in selllableProductsList) {
                    if (product.productCode == productCode) {
                        addProduct(product)
                        count++
                        break
                    }
                }
                if (count == 0)
                {
                    listener?.showAlertDialog(getString(R.string.msg_product_isinvalid))
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH) {
            listener?.showToolbarBackButton(R.string.title_sales_tax)
            if (data != null && data.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                customer = data.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                setCustomerInfo()
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER) {
            data?.let {
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER)) {
                    customer = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                    setCustomerInfo()
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.let {
                if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID) && it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0) > 0) {
                    clearScreen()
                    val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                    if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0))
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.SALES.Code)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cartItem = null
        customer = null
        listener = null
    }

    private fun initListeners() {

        binding.btnAddProduct.setOnClickListener {
            if (selllableProductsList.isNotEmpty()) {
                SearchableDialog.showSpinnerSelectionDialog(activity, selllableProductsList) { selObj ->
                    addProduct(selObj as SellableProduct)
                }
            } else {
                listener?.showAlertDialog(getString(R.string.msg_product_not_assigned_to_sell))
            }
        }

        binding.btnScanProduct.setOnClickListener {
            val intent = Intent(requireContext(), ScanActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SALES_TAX)
            startActivityForResult(intent, Constant.REQUEST_CODE_SCAN_PRODUCT)
        }

        binding.layoutProduct.imgBtnPlusQty.setOnClickListener {
            cartItem?.quantity = cartItem?.quantity!! + 1
            updateProductInfo()
        }

        binding.layoutProduct.imgBtnMinusQty.setOnClickListener {
            if (cartItem?.quantity!! > 1) {
                cartItem?.quantity = cartItem?.quantity!! - 1
                updateProductInfo()
            }
        }

        binding.layoutProduct.imgBtnDelete.setOnClickListener {
            cartItem = null
            setProductViews()
            updateProductInfo()
        }

        binding.edtCustomerName.setOnClickListener {
            showCustomers()
        }

        binding.edtPhoneNumber.setOnClickListener {
            showCustomers()
        }

        binding.tvCreateCustomer.setOnClickListener {

            val fragment = BusinessOwnerEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SALES_TAX)
            fragment.arguments = bundle
            //endregion

            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)

            listener?.showToolbarBackButton(R.string.citizen)
            listener?.addFragment(fragment, true)
        }

        binding.btnProceed.setOnClickListener {

            try {
                if (cartItem == null) {
                    listener?.showToast(getString(R.string.msg_add_product))
                    return@setOnClickListener
                }

                if (customer == null) {
                    listener?.showToast(getString(R.string.msg_provide_customer_info))
                    return@setOnClickListener
                }

                if (cartItem?.item?.productTypeCode.equals("S") && (cartItem?.item?.stockInHand ?: BigDecimal.ZERO <= BigDecimal.ZERO || cartItem?.item?.stockInHand ?: BigDecimal.ZERO < BigDecimal(cartItem?.quantity
                                ?: 0))) {
                    listener?.showToast(getString(R.string.msg_insufficient_stok))
                    return@setOnClickListener
                }

                if (customer?.accountID == null || customer?.accountID!! == 0) {
                    throw Exception("Invalid Account ID")
                }

                if (cartItem?.getRoundedFinalPrice(roundingMethod?.roundingPlace
                                ?: 0) == BigDecimal.ZERO) {
                    listener?.showSnackbarMsg(R.string.payment_cannot_be_done)
                    return@setOnClickListener
                }

                val payment = MyApplication.resetPayment()
                val roundedAmount = cartItem?.getRoundedFinalPrice(roundingMethod?.roundingPlace
                        ?: 0) ?: BigDecimal.ZERO
                payment.amountDue = roundedAmount
                payment.amountTotal = roundedAmount
                payment.minimumPayAmount = roundedAmount
                payment.customerID = customer?.accountID!!
                payment.paymentType = Constant.PaymentType.SALES_TAX
                payment.productCode = cartItem?.item?.productCode!!
                payment.cartItem = cartItem
                payment.customer = customer

                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SALES_TAX)
                startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)

            } catch (e: Exception) {
                listener?.showAlertDialog(e.message ?: "")
            }
        }
    }

    private fun addProduct(sellableProduct: SellableProduct) {
        //region add product to cart
        cartItem = CartItem()
        cartItem?.item = sellableProduct
        cartItem?.quantity = 1
        setProductViews()
        setProductInfo()
        //endregion
    }

    private fun setProductViews() {
        if (cartItem == null) {
            binding.layoutProduct.rootConstraintLayout.visibility = View.GONE
            binding.llProductAddControls.visibility = View.VISIBLE
            binding.tvTotal.text = formatWithPrecision(0.0)
            binding.tvRounding.text = formatWithPrecision(0.0, false)
        } else {
            binding.layoutProduct.rootConstraintLayout.visibility = View.VISIBLE
            binding.llProductAddControls.visibility = View.GONE
        }
    }

    private fun setProductInfo() {
        cartItem?.item?.let {
            it.photo?.let { url ->
                Glide.with(requireContext()).load(url).into(binding.layoutProduct.ivProduct)
            }
            binding.layoutProduct.tvProductName.text = it.product
            if (it.productTypeCode.equals("S")) {
                binding.layoutProduct.tvStockInHand.visibility = View.VISIBLE
                binding.layoutProduct.tvStockInHandValue.visibility = View.VISIBLE
                binding.layoutProduct.tvStockInHandValue.text = getQuantity(it.stockInHand.toString())
            } else {
                binding.layoutProduct.tvStockInHand.visibility = View.GONE
                binding.layoutProduct.tvStockInHandValue.visibility = View.GONE
            }
            if (it.validForMonths ?: 0 > 0) {
                binding.layoutProduct.tvValidity.visibility = View.VISIBLE
                binding.layoutProduct.tvValidity.text = getString(R.string.place_holder_valid_for_months, it.validForMonths)
                binding.layoutProduct.imgBtnPlusQty.isEnabled = false
                binding.layoutProduct.imgBtnMinusQty.isEnabled = false
            } else {
                binding.layoutProduct.tvValidity.visibility = View.GONE
                binding.layoutProduct.imgBtnPlusQty.isEnabled = true
                binding.layoutProduct.imgBtnMinusQty.isEnabled = true
            }

            binding.layoutProduct.tvUnitPrice.text = formatWithPrecision(it.unitPrice
                    ?: BigDecimal.ZERO)
            binding.layoutProduct.tvQuantity.text = cartItem?.quantity.toString()
            binding.tvTotal.text = (cartItem?.getRoundedFinalPrice(roundingMethod?.roundingPlace
                    ?: 0) ?: BigDecimal.ZERO).appendCurrencyCode()
            binding.tvRounding.text = cartItem?.getRounding(roundingMethod?.roundingPlace
                    ?: 0).toString()
        }
    }

    private fun updateProductInfo() {
        if (cartItem == null) {
            binding.tvTotal.text = formatWithPrecision(0.0)
            binding.tvRounding.text = formatWithPrecision(0.0, false)
        }

        cartItem?.item?.let {
            binding.layoutProduct.tvQuantity.text = cartItem?.quantity.toString()
            binding.tvTotal.text = (cartItem?.getRoundedFinalPrice(roundingMethod?.roundingPlace
                    ?: 0) ?: BigDecimal.ZERO).appendCurrencyCode()
            binding.tvRounding.text = cartItem?.getRounding(roundingMethod?.roundingPlace
                    ?: 0).toString()
        }
    }

    private fun loadProducts() {
        listener?.showProgressDialog()
        APICall.getProductByType(object : ConnectionCallBack<List<SellableProduct>> {
            override fun onSuccess(response: List<SellableProduct>) {
                listener?.dismissDialog()
                selllableProductsList = response
            }

            override fun onFailure(message: String) {
                listener?.dismissDialog()
                if (message.isNotEmpty())
                    listener?.showAlertDialog(message)
            }
        })
    }

    private fun getDefaultRoundingMethod() {
        listener?.showProgressDialog()
        APICall.getTableOrViewData(getDefaultRoundingMethodPayload(), object : ConnectionCallBack<GenericServiceResponse> {
            override fun onSuccess(response: GenericServiceResponse) {
                listener?.dismissDialog()
                if (response.result?.roundingMethods?.size ?: 0 > 0)
                    roundingMethod = response.result?.roundingMethods?.get(0)
            }

            override fun onFailure(message: String) {
                listener?.dismissDialog()
                if (message.isNotEmpty())
                    listener?.showAlertDialog(message)
            }
        })
    }

    private fun getDefaultRoundingMethodPayload(): AdvanceSearchFilter {
        val searchFilter = AdvanceSearchFilter()

        //region Apply Filters
        val filterList: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "[Default]"
        filterColumn.columnValue = "Y"
        filterColumn.srchType = "equal"

        filterList.add(filterColumn)
        searchFilter.filterColumns = filterList
        //endregion

        //region Apply Table/View details
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "COM_RoundingMethods"
        tableDetails.primaryKeyColumnName = "RoundingMethodID"
        tableDetails.selectColoumns = "RoundingMethodID, RoundingMethod, RoundingPlace, [Default]"
        tableDetails.TableCondition = "OR"

        searchFilter.tableDetails = tableDetails
        //endregion

        return searchFilter
    }

    private fun showCustomers() {
        val fragment = BusinessOwnerSearchFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SALES_TAX)
        fragment.arguments = bundle
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_SEARCH)
        listener?.showToolbarBackButton(R.string.citizen)
        listener?.addFragment(fragment, true)
    }

    private fun setCustomerInfo() {
        customer?.let {
            binding.edtCustomerName.setText(it.accountName)
            binding.edtPhoneNumber.setText(it.phone ?: "")
        }
    }

    private fun clearScreen() {
        customer = null
        cartItem = null
        MyApplication.resetPayment()

        setProductViews()
        loadProducts()
        updateProductInfo()

        binding.edtCustomerName.setText("")
        binding.edtPhoneNumber.setText("")

    }

}
