package com.sgs.citytax.api.payload

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "COMMAND", strict = false)
data class OrangeWalletPayment(
        @field:Element(name = "otp") @param:Element(name = "otp")
        var otp: String? = null,
        @field:Element(name = "customer_msisdn") @param:Element(name = "customer_msisdn")
        var customerMsisdn: String? = null,
        @field:Element(name = "amount") @param:Element(name = "amount")
        var amount: String? = null,
        @field:Element(name = "TYPE")
        var type: String = "OMPREQ", //Fixed value
        @field:Element(name = "merchant_msisdn") @param:Element(name = "merchant_msisdn")
        var merchantMsisdn: String = "65330166", //Partner(who receives payment) mobile no.
        @field:Element(name = "api_username") @param:Element(name = "api_username")
        var apiUsername: String = "S.G.S-BURKINA", //Partner username
        @field:Element(name = "api_password") @param:Element(name = "api_password")
        var apiPassword: String = "Sgs@2019", //Partner password
        @field:Element(name = "PROVIDER") @param:Element(name = "PROVIDER")
        var provider: String = "101", //Fixed value
        @field:Element(name = "PROVIDER2") @param:Element(name = "PROVIDER2")
        var provider2: String = "101", //Fixed value
        @field:Element(name = "PAYID") @param:Element(name = "PAYID")
        var payID: String = "12", //Fixed value
        @field:Element(name = "PAYID2") @param:Element(name = "PAYID2")
        var payID2: String = "12", //Fixed value
        @field:Element(name = "reference_number") @param:Element(name = "reference_number")
        var referenceNumber: String = System.currentTimeMillis().toString(), //Additional info that the partner can send
        @field:Element(name = "ext_txn_id") @param:Element(name = "ext_txn_id")
        var extTxnID: String = System.currentTimeMillis().toString() //Partner / Acceptor Transaction Reference
)