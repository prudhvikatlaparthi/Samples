package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PropertyLandTaxNoticeDetails(
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("regno")
        var regstrationNo: String? = "",
        @SerializedName("RegistrationDate")
        var registrationDate: String? = "",
        @SerializedName("ConstructedDate")
        var constructedDate: String? = "",
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("NoticeReferenceNo")
        val noticeReferanceNo:String?="",
        @SerializedName("Owners")
        var propertyOwners: String? = "",
        @SerializedName("SycotaxID")
        var sycotaxId: String? = "",
        @SerializedName("mob")
        var phone:String?="",
        @SerializedName("email")
        var email:String?="",
        @SerializedName("PropertyType")
        var propertyType: String? = "",
        @SerializedName("PropertyValue")
        var propertyValue: Double? = 0.0,
        @SerializedName("PropertySycotaxID")
        var propertySycotaxID: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("sec")
        var sector: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("ComfortLevel")
        var comfortLevel: String? = "",
        @SerializedName("ElectricityConsumption")
        var electricityConsumption: String? = "",
        @SerializedName("PhaseOfElectricity")
        var phaseOfElectricity: String? = "",
        @SerializedName("WaterConsumption")
        var waterConsumption: String? = "",
        @SerializedName("LandUseType")
        var landUseType: String? = "",
        @SerializedName("AreaType")
        var AreaType: String? = "",
        @SerializedName("PropertyRent")
        var propertyRent: Double? = 0.0,
        @SerializedName("PropertyExemptionReason")
        var propertyExemptionReason: String? = "",
        @SerializedName("Exemption")
        var exemption: String? = "",
        @SerializedName("InvoiceAmount")
        var invoiceAmount: Double? = 0.0,
        @SerializedName("AmountDueCurrentYear")
        var amountDueForCurrentYear: Double? = 0.0,
        @SerializedName("AmountDueAnteriorYear")
        var amountDueAnteriorYear: Double? = 0.0,
        @SerializedName("AmountDuePreviousYear")
        var amountDuePreviousYear: Double? = 0.0,
        @SerializedName("PenaltyDue")
        var penaltyDue: Double? = 0.0,
        @SerializedName("note")
        var note: String? = "",
        @SerializedName("vchrno")
        var voucherNo: String? = "",
        @SerializedName("Length")
        var length: Double? = 0.0,
        @SerializedName("wdth")
        var width: Double? = 0.0,
        @SerializedName("area")
        var area: Int? = 0,
        @SerializedName("GeneratedBy")
        var generatedBy: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        @SerializedName("PropertyOwnerIDSycoTax")
        var propertyOwnerIDSycoTax: String? = "",
        @SerializedName("IDCardNumbers")
        var citizenCardNumber: String? = "",
        @SerializedName("PropertyBuildType")
        var propertyBuildType: String? = "",
        @SerializedName("Rate")
        var rate: String? = ""
)