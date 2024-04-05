package com.sgs.citytax.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.databinding.FragmentDashboardDialogBinding
import com.sgs.citytax.model.QuickMenuItem
import com.sgs.citytax.ui.*
import com.sgs.citytax.ui.adapter.DashboardAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.getMenu

class DashboardDialogFragment : DialogFragment(), IClickListener {

    private lateinit var binding: FragmentDashboardDialogBinding
    private lateinit var code: Constant.QuickMenu

    companion object {
        @JvmStatic
        fun newInstance(code: Constant.QuickMenu) = DashboardDialogFragment().apply {
            this.code = code
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard_dialog, container, false)
        initComponents()
        return binding.root
    }

    private fun initComponents() {
        val quickMenuItems = getMenu(requireContext(), code)
        if (quickMenuItems.size == 1) {
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        }
        binding.recyclerView.adapter = DashboardAdapter(quickMenuItems, this)
    }

    override fun onClick(view: View, position: Int, obj: Any) {

        when (val menu = (obj as QuickMenuItem).code) {
            Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS,
            Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS,
            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_COLLECTION,
            Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION,
            Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION,
            Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION,
            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE,
            Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE,
            Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE,
            Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE,
            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE_HISTORY,
            Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY,
            Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY,
            Constant.QuickMenu.QUICK_MENU_CORPORATE_BUSINESS_TRANSACTION_HISTORY,
            Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_BUSINESS_TRANSACTION_HISTORY,
            Constant.QuickMenu.QUICK_MENU_CREATE_LAND,
            Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY,
            Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY,
            Constant.QuickMenu.QUICK_MENU_UPDATE_LAND,
            Constant.QuickMenu.QUICK_MENU_PROPERTY_TRANSACTION_HISTORY,
            Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY,
            Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY,
            Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_NOTICES -> {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }
            Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT -> {
                val intent = Intent(context, AgentOnboardingActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }
            Constant.QuickMenu.QUICK_MENU_UPDATE_AGENT -> {
                val intent = Intent(context, AgentOnboardingActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }
            Constant.QuickMenu.QUICK_MENU_WALLET_HISTORY -> {
                val intent = Intent(context, CreditBalanceActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }

            Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE -> {
                val intent = Intent(context, PaymentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }

            Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER -> {
                val intent = Intent(context, RegisterOwnerActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }

            Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER -> {
                val intent = Intent(context, BusinessOwnerSearchActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }
            Constant.QuickMenu.QUICK_MENU_OTHER_TAXES,
            Constant.QuickMenu.QUICK_MENU_CART_TAX,
            Constant.QuickMenu.QUICK_MENU_WEAPON_TAX,
            Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE,
            Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX,
            Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX,
            Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE
            -> {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }

            Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF,
            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_OUTSTANDING_WAIVE_OFF,
            Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF,
            Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF-> {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }

            Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_PENALTY_WAIVE_OFF,
            Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_PENALTY_WAIVE_OFF,
            Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF,
            Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF -> {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, menu)
                startActivity(intent)
                dismiss()
            }

            else -> {

            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}