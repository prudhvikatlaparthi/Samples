package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemTaxBinding
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.model.VuTax
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.ICheckListener
import com.sgs.citytax.util.IClickListener

class TaxMasterAdapter(val listener: IClickListener, private val checkListener: ICheckListener, private val edit: Constant.ScreenMode?, private val fromScreen: Constant.QuickMenu?) : RecyclerView.Adapter<TaxMasterAdapter.ViewHolder>() {

    private var mList: MutableList<VuTax> = arrayListOf()

    class ViewHolder(val binding: ItemTaxBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: VuTax, listener: IClickListener, checkListener: ICheckListener, edit: Constant.ScreenMode?, fromScreen: Constant.QuickMenu?) {
            if (edit == Constant.ScreenMode.VIEW) {
                binding.btnDelete.visibility = View.GONE
                binding.checkbox.isEnabled=false
            }
            binding.llCorporateTurnOverChildTab.visibility = View.GONE
            binding.llPDOChildTab.visibility = View.GONE
            binding.llROPChildTab.visibility = View.GONE
            binding.llPropertyOwnerShipChildTab.visibility = View.GONE
            binding.llRentalDetailsChildTab.visibility = View.GONE
            binding.llVehicleOwnerShipChildTab.visibility = View.GONE
            binding.llAdvertisementChildTab.visibility = View.GONE
            binding.llShowTaxChildTab.visibility = View.GONE
            binding.llHotelTaxChildTab.visibility = View.GONE
            binding.llLicenseTaxChildTab.visibility = View.GONE

            binding.txtProduct.text = product.product
            binding.btnDelete.tag = product.productCode
            binding.checkbox.tag = product.productCode

            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = product.active.equals("Y")

            product.taskCodes?.let {
                for (taskCode: TaskCode in it) {
                    when (taskCode.taskCode) {
                        "CRM_CorporateTurnoverEntry" -> {
                            binding.llCorporateTurnOverChildTab.tag = taskCode
                            binding.llCorporateTurnOverChildTab.visibility = View.VISIBLE
                            binding.txtNumberOfCorporateTurnover.text = "${product.noOfCorporateTurnOver}"
                        }
                        "CRM_PropertyOwnership" -> {
                            binding.llPropertyOwnerShipChildTab.tag = taskCode
                            binding.llPropertyOwnerShipChildTab.visibility = View.VISIBLE
                        }
                        "ADM_VehicleOwnership" -> {
                            binding.llVehicleOwnerShipChildTab.tag = taskCode
                            binding.llVehicleOwnerShipChildTab.visibility = View.VISIBLE
                            binding.txtNumberOfVehicleOwnership.text = "${product.noOfVehicleOwnership}"
                        }
                        "CRM_PublicDomainOccupancy" -> {
                            binding.llPDOChildTab.tag = taskCode
                            binding.llPDOChildTab.visibility = View.VISIBLE
                            binding.txtNumberOfPDO.text = "${product.noOfPDO}"
                        }
                        "CRM_RightOfPlacesEntry" -> {
                            binding.llROPChildTab.tag = taskCode
                            binding.llROPChildTab.visibility = View.VISIBLE
                            binding.txtNumberOfROP.text = "${product.noOfROP}"
                        }
                        "VU_CRM_PropertyRents" -> {
                            binding.llRentalDetailsChildTab.tag = taskCode
                            binding.llRentalDetailsChildTab.visibility = View.VISIBLE
                        }
                        "CRM_AdvertisementsEntry" -> {
                            binding.llAdvertisementChildTab.tag = taskCode
                            binding.llAdvertisementChildTab.visibility = View.VISIBLE
                            binding.txtNumberOfAdvertisements.text = "${product.noOfAdvertisements}"
                        }
                        "CRM_ShowEntry" -> {
                            binding.llShowTaxChildTab.tag = taskCode
                            binding.llShowTaxChildTab.visibility = View.VISIBLE
                            binding.txtNumberOfShows.text = "${product.noOfShows}"
                        }
                        "CRM_HotelEntry" -> {
                            binding.llHotelTaxChildTab.tag = taskCode
                            binding.llHotelTaxChildTab.visibility = View.VISIBLE
                            binding.txtNoOfHotels.text = "${product.noOfHotel}"
                        }
                        "CRM_LicensesEntry" ->{
                            binding.llLicenseTaxChildTab.tag = taskCode
                            binding.llLicenseTaxChildTab.visibility = View.VISIBLE
                            binding.txtNoOfLicenses.text = "${product.noOfLicenses}"
                        }
                    }
                }
            }

            binding.checkbox.setOnCheckedChangeListener { it, isChecked ->
                if (isChecked)
                    checkListener.onCheckedChange(it, adapterPosition, product, "Y")
                else
                    checkListener.onCheckedChange(it, adapterPosition, product, "N")
            }

            binding.btnDelete.setOnClickListener {
                if (adapterPosition>=0) {
                    listener.onClick(it, adapterPosition, product)
                }
            }

            binding.llCorporateTurnOverChildTab.setOnClickListener {
//                if (edit == Constant.ScreenMode.VIEW && binding.txtNumberOfCorporateTurnover.text.toString()
//                        .toInt() == 0
//                ) {
//                } else {
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
//                    }
                }
            }

            binding.llPDOChildTab.setOnClickListener {
//                if (edit == Constant.ScreenMode.VIEW && binding.txtNumberOfPDO.text.toString().toInt() == 0)
//                else{
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
                    }
//                }
            }

            binding.llROPChildTab.setOnClickListener {
//                if (edit == Constant.ScreenMode.VIEW && binding.txtNumberOfROP.text.toString().toInt() == 0)
//                else{
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
                    }
//                }
            }

            binding.llPropertyOwnerShipChildTab.setOnClickListener {
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
                    }
            }

            binding.llRentalDetailsChildTab.setOnClickListener {
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
                    }
            }

            binding.llVehicleOwnerShipChildTab.setOnClickListener {
//                if (edit == Constant.ScreenMode.VIEW && binding.txtNumberOfVehicleOwnership.text.toString().toInt() == 0)
//                else{
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
//                    }
                }
            }
            binding.llAdvertisementChildTab.setOnClickListener {
//                if (edit == Constant.ScreenMode.VIEW && binding.txtNumberOfAdvertisements.text.toString().toInt() == 0)
//                else{
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
                    }
//                }
            }
            binding.llShowTaxChildTab.setOnClickListener {
//                if (edit == Constant.ScreenMode.VIEW && binding.txtNumberOfShows.text.toString().toInt() == 0)
//                else{
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
                    }
//                }
            }
            binding.llHotelTaxChildTab.setOnClickListener {
//                if (edit == Constant.ScreenMode.VIEW && binding.txtNoOfHotels.text.toString().toInt() == 0)
//                else{
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
                    }
//                }
            }
            binding.llLicenseTaxChildTab.setOnClickListener{
//                if (edit == Constant.ScreenMode.VIEW && binding.txtNoOfLicenses.text.toString().toInt() == 0)
//                else{
                    if (adapterPosition>=0) {
                        listener.onClick(it, adapterPosition, product)
                    }
//                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTaxBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_tax, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = mList[position]
        holder.bind(product, listener, checkListener, edit, fromScreen)
    }

    private fun add(product: VuTax) {
        mList.add(product)
        notifyDataSetChanged()
    }

    fun update(product: VuTax) {
        var index = -1
        mList.forEach {
            if (it.productCode == product.productCode)
                index = mList.indexOf(it)
        }
        if (index != -1)
            mList.add(index, product)
    }

    fun add(products: List<VuTax>) {
        products.forEach {
            add(it)
        }
    }

    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }
}
