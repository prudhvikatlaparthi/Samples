package com.sgs.citytax.util

import android.content.Context
import com.sgs.citytax.R
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.model.QuickMenuItem

fun getMenu(context: Context, code: Any): ArrayList<QuickMenuItem> {
    val list: ArrayList<QuickMenuItem> = arrayListOf()
    val prefHelper = MyApplication.getPrefHelper()
    when (code) {
        Constant.QuickMenu.QUICK_MENU_REGISTER -> {
            if (prefHelper.isInspector()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS))
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER))
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_agent), R.drawable.ic_agent, Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT))
                otherTaxesMenuItems(context, list)
                propertyTaxesMenuItem(context,list)

            } else if (prefHelper.isSupervisor()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS))
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER))
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_agent), R.drawable.ic_agent, Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT))
                otherTaxesMenuItems(context, list)
                propertyTaxesMenuItem(context,list)
            } else if (prefHelper.isMunicipalAgent()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS))
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER))
                otherTaxesMenuItems(context, list)
                propertyTaxesMenuItem(context,list)
            } else if (prefHelper.isThirdPartyAgent()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS))
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER))
                otherTaxesMenuItems(context, list)
                propertyTaxesMenuItem(context,list)
            } else if (prefHelper.isPayPoint() && prefHelper.isAdminUser) {

            } else if (prefHelper.isPayPoint() && !prefHelper.isAdminUser) {

            } else if (prefHelper.isAssociation()) {

            }
            // This is as per new condition => 25-05-2021
            else if (prefHelper.isAssociationAgent()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS))
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER))
                otherTaxesMenuItems(context, list)
                propertyTaxesMenuItem(context,list)
            }
            else if (prefHelper.isSales()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER))
            }

            else if (prefHelper.isLawEnforceAgent() ||prefHelper.isLawEnforeInspector() ||prefHelper.isLawEnforeSupervisor()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER))
            }

        }
        Constant.QuickMenu.QUICK_MENU_UPDATE -> {
            if (prefHelper.isInspector()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS))
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER))
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_agent), R.drawable.ic_agent, Constant.QuickMenu.QUICK_MENU_UPDATE_AGENT))
                otherTaxesMenuItemsUpdate(context, list)
                propertyTaxesMenuItemUpdate(context,list)

            } else if (prefHelper.isSupervisor()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS))
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER))
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_agent), R.drawable.ic_agent, Constant.QuickMenu.QUICK_MENU_UPDATE_AGENT))
                otherTaxesMenuItemsUpdate(context, list)
                propertyTaxesMenuItemUpdate(context,list)

            } else if (prefHelper.isMunicipalAgent()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS))
                otherTaxesMenuItemsUpdate(context, list)
//list.add(QuickMenuItem(context.resources.getString(R.string.menu_owner), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER))
            } else if (prefHelper.isThirdPartyAgent()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS))
                otherTaxesMenuItemsUpdate(context, list)
//list.add(QuickMenuItem(context.resources.getString(R.string.menu_owner), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER))
            } else if (prefHelper.isPayPoint() && prefHelper.isAdminUser) {

            } else if (prefHelper.isPayPoint() && !prefHelper.isAdminUser) {

            } else if (prefHelper.isAssociation()) {

            }
            //Updated condition same as the Third party Agent => 25-05-2021
            else if (prefHelper.isAssociationAgent()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_business), R.drawable.ic_business, Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS))
                otherTaxesMenuItemsUpdate(context, list)
            }
            else if (prefHelper.isSales()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER))
            }
            else if (prefHelper.isLawEnforceAgent() ||prefHelper.isLawEnforeInspector() ||prefHelper.isLawEnforeSupervisor()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.citizen), R.drawable.ic_citizen, Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER))
            }


        }
        Constant.QuickMenu.QUICK_MENU_TAX_COLLECTION -> {
            list.add(QuickMenuItem(context.resources.getString(R.string.title_corporate_tax_collection), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_COLLECTION))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_individual_tax_collection), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION))
            if (!prefHelper.isPayPoint() || (prefHelper.isPayPoint() && prefHelper.allowPropertyTaxCollection == "Y"))
            {
                list.add(QuickMenuItem(context.resources.getString(R.string.title_property_tax_collection), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION))
                list.add(QuickMenuItem(context.resources.getString(R.string.title_land_tax_collection), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION))
            }

        }
        Constant.QuickMenu.QUICK_MENU_TAX_NOTICE -> {
            list.add(QuickMenuItem(context.resources.getString(R.string.title_corporate_tax_notice), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_individual_tax_notice), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_property_tax_notice), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_land_tax_notice), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE))

        }
        Constant.QuickMenu.QUICK_MENU_TAX_NOTICE_HISTORY -> {
            list.add(QuickMenuItem(context.resources.getString(R.string.title_corporate_tax_notice_history), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE_HISTORY))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_individual_tax_notice_history), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_property_tax_notice_history), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_land_tax_notice_history), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY))
        }

        Constant.QuickMenu.QUICK_MENU_BUSINESS_TRANSACTION_HISTORY -> {
            list.add(QuickMenuItem(context.resources.getString(R.string.title_corporate_business_transcation_history), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_CORPORATE_BUSINESS_TRANSACTION_HISTORY))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_individual_business_transcation_history), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_BUSINESS_TRANSACTION_HISTORY))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_property_transcation_history), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_PROPERTY_TRANSACTION_HISTORY))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_land_transcation_history), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY))
        }
        Constant.QuickMenu.QUICK_MENU_WALLET -> {

            if (prefHelper.isInspector()) {

            } else if (prefHelper.isSupervisor()) {

            } else if (prefHelper.isMunicipalAgent()) {

            } else if (prefHelper.isThirdPartyAgent()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_wallet_recharge), R.drawable.ic_add_money, Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE))
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_wallet_history), R.drawable.ic_history, Constant.QuickMenu.QUICK_MENU_WALLET_HISTORY))

            } else if (prefHelper.isPayPoint() && prefHelper.isAdminUser) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_wallet_recharge), R.drawable.ic_add_money, Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE))
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_wallet_history), R.drawable.ic_history, Constant.QuickMenu.QUICK_MENU_WALLET_HISTORY))

            } else if (prefHelper.isPayPoint() && !prefHelper.isAdminUser) {

            } else if (prefHelper.isAssociation()) {

            } else if (prefHelper.isAssociationAgent()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_wallet_recharge), R.drawable.ic_add_money, Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE))
                list.add(QuickMenuItem(context.resources.getString(R.string.menu_wallet_history), R.drawable.ic_history, Constant.QuickMenu.QUICK_MENU_WALLET_HISTORY))
            }
        }
        Constant.QuickMenu.QUICK_MENU_INCIDENTS -> {

        }
        Constant.QuickMenu.QUICK_MENU_OTHER_TAXES -> {
            /*  list.add(QuickMenuItem(context.resources.getString(R.string.title_cart_tax), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_CART_TAX))
              list.add(QuickMenuItem(context.resources.getString(R.string.title_gaming_machine), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE))
              list.add(QuickMenuItem(context.resources.getString(R.string.title_weapon_tax), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_WEAPON_TAX))*/

            if (prefHelper.isInspector() || prefHelper.isSupervisor()
                    || prefHelper.isMunicipalAgent() || prefHelper.isThirdPartyAgent()||prefHelper.isAssociationAgent()) {
                list.add(QuickMenuItem(context.resources.getString(R.string.title_cart_tax), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_CART_TAX))
                list.add(QuickMenuItem(context.resources.getString(R.string.title_gaming_machine), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE))
                list.add(QuickMenuItem(context.resources.getString(R.string.title_weapon_tax), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_WEAPON_TAX))
            } else if (prefHelper.isPayPoint() && prefHelper.isAdminUser) {

            } else if (prefHelper.isPayPoint() && !prefHelper.isAdminUser) {

            } else if (prefHelper.isAssociation()) {

            } /*else if (prefHelper.isAssociationAgent()) {

            }*/

        }
        Constant.QuickMenu.QUICK_MENU_OUTSTANDING_WAIVE_OFF -> {
            list.add(QuickMenuItem(context.resources.getString(R.string.title_corporate_business_outstanding_waive_off), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_OUTSTANDING_WAIVE_OFF))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_individual_business_outstanding_waive_off), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_property_outstanding_waive_off), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_land_outstanding_waive_off), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF))
        }
        Constant.QuickMenu.QUICK_MENU_PENALTY_WAIVE_OFF -> {
            list.add(QuickMenuItem(context.resources.getString(R.string.title_corporate_business_penalty_waive_off), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_PENALTY_WAIVE_OFF))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_individual_business_penalty_waive_off), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_PENALTY_WAIVE_OFF))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_property_penalty_waive_off), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF))
            list.add(QuickMenuItem(context.resources.getString(R.string.title_land_penalty_waive_off), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF))
        }

        else -> {

        }
    }

    return list
}

fun getDashboardMenu(context: Context, complaintCount: Int = 0, incidentCount: Int = 0): ArrayList<QuickMenuItem> {
    val quickMenuItems: ArrayList<QuickMenuItem> = arrayListOf()
    val prefHelper = MyApplication.getPrefHelper()

    if (prefHelper.isInspector()) {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_register), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_REGISTER))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_update), R.drawable.ic_update, Constant.QuickMenu.QUICK_MENU_UPDATE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_collection), R.drawable.ic_tax_collection, Constant.QuickMenu.QUICK_MENU_TAX_COLLECTION))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_notice), R.drawable.ic_tax_notice, Constant.QuickMenu.QUICK_MENU_TAX_NOTICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_incidents), R.drawable.ic_incidents, Constant.QuickMenu.QUICK_MENU_INCIDENTS, incidentCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_tasks), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_TASKS, complaintCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_service_tax), R.drawable.ic_service_tax, Constant.QuickMenu.QUICK_MENU_SERVICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))
//        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_other_taxes), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_OTHER_TAXES))
    } else if (prefHelper.isSupervisor()) {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_register), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_REGISTER))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_update), R.drawable.ic_update, Constant.QuickMenu.QUICK_MENU_UPDATE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_collection), R.drawable.ic_tax_collection, Constant.QuickMenu.QUICK_MENU_TAX_COLLECTION))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_notice), R.drawable.ic_tax_notice, Constant.QuickMenu.QUICK_MENU_TAX_NOTICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_incidents), R.drawable.ic_incidents, Constant.QuickMenu.QUICK_MENU_INCIDENTS, incidentCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_tasks), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_TASKS, complaintCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_service_tax), R.drawable.ic_service_tax, Constant.QuickMenu.QUICK_MENU_SERVICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))
//        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_other_taxes), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_OTHER_TAXES))
    } else if (prefHelper.isMunicipalAgent()) {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_register), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_REGISTER))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_update), R.drawable.ic_update, Constant.QuickMenu.QUICK_MENU_UPDATE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_collection), R.drawable.ic_tax_collection, Constant.QuickMenu.QUICK_MENU_TAX_COLLECTION))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_notice), R.drawable.ic_tax_notice, Constant.QuickMenu.QUICK_MENU_TAX_NOTICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_incidents), R.drawable.ic_incidents, Constant.QuickMenu.QUICK_MENU_INCIDENTS, incidentCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_tasks), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_TASKS, complaintCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_service_tax), R.drawable.ic_service_tax, Constant.QuickMenu.QUICK_MENU_SERVICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))
//        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_other_taxes), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_OTHER_TAXES))
    } else if (prefHelper.isThirdPartyAgent()) {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_register), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_REGISTER))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_update), R.drawable.ic_update, Constant.QuickMenu.QUICK_MENU_UPDATE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_collection), R.drawable.ic_tax_collection, Constant.QuickMenu.QUICK_MENU_TAX_COLLECTION))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_notice), R.drawable.ic_tax_notice, Constant.QuickMenu.QUICK_MENU_TAX_NOTICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_incidents), R.drawable.ic_incidents, Constant.QuickMenu.QUICK_MENU_INCIDENTS, incidentCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_tasks), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_TASKS, complaintCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_service_tax), R.drawable.ic_service_tax, Constant.QuickMenu.QUICK_MENU_SERVICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))
//        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_other_taxes), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_OTHER_TAXES))
        if (prefHelper.agentIsPrepaid)
            quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_wallet), R.drawable.ic_wallet, Constant.QuickMenu.QUICK_MENU_WALLET))
    }

// true -> collection Owner
    else if (prefHelper.isPayPoint() && prefHelper.isAdminUser) {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_collection), R.drawable.ic_tax_collection, Constant.QuickMenu.QUICK_MENU_TAX_COLLECTION))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_incidents), R.drawable.ic_incidents, Constant.QuickMenu.QUICK_MENU_INCIDENTS, incidentCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_tasks), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_TASKS, complaintCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_service_tax), R.drawable.ic_service_tax, Constant.QuickMenu.QUICK_MENU_SERVICE))
        if (prefHelper.agentIsPrepaid)
            quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_wallet), R.drawable.ic_wallet, Constant.QuickMenu.QUICK_MENU_WALLET))

    } else if (prefHelper.isPayPoint() && !prefHelper.isAdminUser) {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_collection), R.drawable.ic_tax_collection, Constant.QuickMenu.QUICK_MENU_TAX_COLLECTION))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_incidents), R.drawable.ic_incidents, Constant.QuickMenu.QUICK_MENU_INCIDENTS, incidentCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_tasks), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_TASKS, complaintCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_service_tax), R.drawable.ic_service_tax, Constant.QuickMenu.QUICK_MENU_SERVICE))
//        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))

    } else if (prefHelper.isAssociation()) {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_incidents), R.drawable.ic_incidents, Constant.QuickMenu.QUICK_MENU_INCIDENTS, incidentCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_tasks), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_TASKS, complaintCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_service_tax), R.drawable.ic_service_tax, Constant.QuickMenu.QUICK_MENU_SERVICE))

    } else if (prefHelper.isAssociationAgent()) {

        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_register), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_REGISTER))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_update), R.drawable.ic_update, Constant.QuickMenu.QUICK_MENU_UPDATE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_collection), R.drawable.ic_tax_collection, Constant.QuickMenu.QUICK_MENU_TAX_COLLECTION))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_tax_notice), R.drawable.ic_tax_notice, Constant.QuickMenu.QUICK_MENU_TAX_NOTICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_incidents), R.drawable.ic_incidents, Constant.QuickMenu.QUICK_MENU_INCIDENTS, incidentCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_tasks), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_TASKS, complaintCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_service_tax), R.drawable.ic_service_tax, Constant.QuickMenu.QUICK_MENU_SERVICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))
        if (prefHelper.agentIsPrepaid)
            quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_wallet), R.drawable.ic_wallet, Constant.QuickMenu.QUICK_MENU_WALLET))
    } else if (prefHelper.isLawEnforceAgent() || prefHelper.isLawEnforeSupervisor()) {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_register), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_REGISTER))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_update), R.drawable.ic_update, Constant.QuickMenu.QUICK_MENU_UPDATE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_vehicle_ownership), R.drawable.ic_track_on_vehicle_registration, Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_ticket_issue), R.drawable.ic_track_on_traffic_ticket, Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_impondment), R.drawable.ic_track_on_impoundment, Constant.QuickMenu.QUICK_MENU_IMPONDMENT))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_return_impondment), R.drawable.ic_track_on_impoundment_return, Constant.QuickMenu.QUICK_MENU_RETURN_IMPONDMENT))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.ticket_payment), R.drawable.ic_track_on_payment, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))

    }
    else if(prefHelper.isLawEnforeInspector())
    {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_register), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_REGISTER))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_update), R.drawable.ic_update, Constant.QuickMenu.QUICK_MENU_UPDATE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_vehicle_ownership), R.drawable.ic_track_on_vehicle_registration, Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_ticket_issue), R.drawable.ic_track_on_traffic_ticket, Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_ticket_issue_issue), R.drawable.ic_track_on_traffic_ticket, Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_impondment), R.drawable.ic_track_on_impoundment, Constant.QuickMenu.QUICK_MENU_IMPONDMENT))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_return_impondment), R.drawable.ic_track_on_impoundment_return, Constant.QuickMenu.QUICK_MENU_RETURN_IMPONDMENT))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.ticket_payment), R.drawable.ic_track_on_payment, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))

    }
    else if (prefHelper.isSales()) {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_register), R.drawable.ic_registrations, Constant.QuickMenu.QUICK_MENU_REGISTER))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_update), R.drawable.ic_update, Constant.QuickMenu.QUICK_MENU_UPDATE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.menu_incidents), R.drawable.ic_incidents, Constant.QuickMenu.QUICK_MENU_INCIDENTS, incidentCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_tasks), R.drawable.ic_task, Constant.QuickMenu.QUICK_MENU_TASKS, complaintCount))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_service_tax), R.drawable.ic_service_tax, Constant.QuickMenu.QUICK_MENU_SERVICE))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))
    }

    if (prefHelper.agentAllowSales == "Y")
    {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_sales_tax), R.drawable.ic_sales_tax, Constant.QuickMenu.QUICK_MENU_SALES_TAX))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.adjustments), R.drawable.ic_stock_management, Constant.QuickMenu.QUICK_MENU_STOCK_MANAGEMENT))
//        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_security_service), R.drawable.ic_police_man, Constant.QuickMenu.QUICK_MENU_SECURITY_TAX))
    }

    if (prefHelper.allowParking == "Y") {
        quickMenuItems.clear()
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_select_vehicle_in), R.drawable.ic_parking_enter, Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_select_vehicle_Out), R.drawable.ic_parking_exit, Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OUT))
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_select_vehicle_Collect), R.drawable.ic_fee_payment, Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT))
    }
    if (prefHelper.isInspector() || prefHelper.isSupervisor()
        || prefHelper.isMunicipalAgent()|| prefHelper.isThirdPartyAgent()
        || prefHelper.isPayPoint()|| prefHelper.isAssociation()
        || prefHelper.isAssociationAgent())
    {
        quickMenuItems.add(QuickMenuItem(context.resources.getString(R.string.title_handover_due_notices), R.drawable.ic_handover_due_notice, Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_NOTICES))

    }



    return quickMenuItems
}

fun otherTaxesMenuItems(context: Context, list: ArrayList<QuickMenuItem>) {
    val prefHelper = MyApplication.getPrefHelper()

    if (prefHelper.isInspector() || prefHelper.isSupervisor()
            || prefHelper.isMunicipalAgent() || prefHelper.isThirdPartyAgent() ||prefHelper.isAssociationAgent()) {
        list.add(QuickMenuItem(context.resources.getString(R.string.title_cart_tax), R.drawable.ic_cart, Constant.QuickMenu.QUICK_MENU_CART_TAX))
        list.add(QuickMenuItem(context.resources.getString(R.string.title_gaming_machine), R.drawable.ic_gaming_machine, Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE))
        list.add(QuickMenuItem(context.resources.getString(R.string.title_weapon_tax), R.drawable.ic_weapon, Constant.QuickMenu.QUICK_MENU_WEAPON_TAX))
    } else if (prefHelper.isPayPoint() && prefHelper.isAdminUser) {

    } else if (prefHelper.isPayPoint() && !prefHelper.isAdminUser) {

    } else if (prefHelper.isAssociation()) {

    } /*else if (prefHelper.isAssociationAgent()) {

    }*/
}
fun propertyTaxesMenuItem(context: Context, list: ArrayList<QuickMenuItem>) {
    val prefHelper = MyApplication.getPrefHelper()

    if (prefHelper.isInspector() || prefHelper.isSupervisor()
            || prefHelper.isMunicipalAgent() || prefHelper.isThirdPartyAgent() || prefHelper.isAssociationAgent()) {
        list.add(QuickMenuItem(context.resources.getString(R.string.title_land_txt), R.drawable.ic_land, Constant.QuickMenu.QUICK_MENU_CREATE_LAND))
        list.add(QuickMenuItem(context.resources.getString(R.string.title_property_txt), R.drawable.ic_property, Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY))

    } else if (prefHelper.isPayPoint() && prefHelper.isAdminUser) {

    } else if (prefHelper.isPayPoint() && !prefHelper.isAdminUser) {

    } else if (prefHelper.isAssociation()) {

    }/* else if (prefHelper.isAssociationAgent()) {

    }*/
}

fun propertyTaxesMenuItemUpdate(context: Context, list: ArrayList<QuickMenuItem>) {
    val prefHelper = MyApplication.getPrefHelper()

    if (prefHelper.isInspector() || prefHelper.isSupervisor()
            || prefHelper.isMunicipalAgent() || prefHelper.isThirdPartyAgent() ||prefHelper.isAssociationAgent()) {
        list.add(QuickMenuItem(context.resources.getString(R.string.title_land_txt), R.drawable.ic_land, Constant.QuickMenu.QUICK_MENU_UPDATE_LAND))
        list.add(QuickMenuItem(context.resources.getString(R.string.title_property_txt), R.drawable.ic_property, Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY))

    } else if (prefHelper.isPayPoint() && prefHelper.isAdminUser) {

    } else if (prefHelper.isPayPoint() && !prefHelper.isAdminUser) {

    } else if (prefHelper.isAssociation()) {

    } /*else if (prefHelper.isAssociationAgent()) {

    }*/
}

fun otherTaxesMenuItemsUpdate(context: Context, list: ArrayList<QuickMenuItem>) {
    val prefHelper = MyApplication.getPrefHelper()

    if (prefHelper.isInspector() || prefHelper.isSupervisor()
            || prefHelper.isMunicipalAgent() || prefHelper.isThirdPartyAgent() || prefHelper.isAssociationAgent()) {
        list.add(QuickMenuItem(context.resources.getString(R.string.title_cart_tax), R.drawable.ic_cart, Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX))
        list.add(QuickMenuItem(context.resources.getString(R.string.title_gaming_machine), R.drawable.ic_gaming_machine, Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE))
        list.add(QuickMenuItem(context.resources.getString(R.string.title_weapon_tax), R.drawable.ic_weapon, Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX))
    } else if (prefHelper.isPayPoint() && prefHelper.isAdminUser) {

    } else if (prefHelper.isPayPoint() && !prefHelper.isAdminUser) {

    } else if (prefHelper.isAssociation()) {

    } /*else if (prefHelper.isAssociationAgent()) {

    }*/
}
