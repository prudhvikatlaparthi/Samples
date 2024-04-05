package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.payload.AccountPhone
import com.sgs.citytax.api.payload.Asset
import com.sgs.citytax.api.payload.BusinessTaxDueYearSummary
import com.sgs.citytax.model.*

data class DataResponse(
        @SerializedName("VU_CRM_Organizations")
        var organizations: MutableList<VUCRMOrganization> = arrayListOf(),
        @SerializedName("COM_StatusCodes")
        var statusCodes: MutableList<COMStatusCode> = arrayListOf(),
        @SerializedName("CRM_ActivityDomains")
        var activityDomain: MutableList<CRMActivityDomain> = arrayListOf(),
        @SerializedName("CRM_ActivityClasses")
        var activityClass: MutableList<CRMActivityClass> = arrayListOf(),
        @SerializedName("ADM_VehicleTypes")
        var vehicleTypes: MutableList<ADMVehicleTypes>? = null,
        @SerializedName("CRM_WeaponTypes")
        var weaponTypes: MutableList<CRMWeaponTypes> = arrayListOf(),
        @SerializedName("CRM_WeaponExemptionReasons")
        var weaponExemptionReasons: MutableList<CRMWeaponExemptionReasons> = arrayListOf(),
        @SerializedName("VU_CRM_NatureOfOccupancy")
        var occupancyTypes: MutableList<VUCRMNatureOfOccupancy> = arrayListOf(),
        @SerializedName("CRM_Markets")
        var mMarkets: MutableList<VUCRMMarkets> = arrayListOf(),
        @SerializedName("VU_INV_Products")
        var products: MutableList<VuInvProducts> = arrayListOf(),
        @SerializedName("COM_ComboStaticValues")
        var comboStaticValues: MutableList<ComComboStaticValues> = arrayListOf(),
        @SerializedName("COM_PropertyBuildTypes")
        var comPropertyBuildTypes: MutableList<COMPropertyBuildTypes> = arrayListOf(),
        @SerializedName("COM_CountryMaster")
        var countryMaster: List<COMCountryMaster> = arrayListOf(),
        @SerializedName("COM_StateMaster")
        var stateMaster: List<COMStateMaster> = arrayListOf(),
        @SerializedName("VU_COM_CityMaster")
        var cityMaster: List<VUCOMCityMaster> = arrayListOf(),
        @SerializedName("COM_ZoneMaster")
        var zoneMaster: List<COMZoneMaster> = arrayListOf(),
        @SerializedName("COM_Sectors")
        var sectors: List<COMSectors> = arrayListOf(),
        @SerializedName("CRM_AccountEmails")
        val emailAccounts: List<CRMAccountEmails> = arrayListOf(),
        @SerializedName("VU_CRM_AccountAddresses")
        val accountAddresses: List<GeoAddress> = arrayListOf(),
        @SerializedName("CRM_AgentDetails")
        var agentDetails: List<CRMAgentDetails> = arrayListOf(),
        @SerializedName("COM_DocumentTypes")
        var documentTypes: MutableList<COMDocumentType> = arrayListOf(),
        @SerializedName("VU_CRM_CustomerProductInterests")
        var typeOfTaxes: List<VUCRMCustomerProductInterests> = arrayListOf(),
        @SerializedName("CRM_AccountPhones")
        var accountPhones: List<AccountPhone> = arrayListOf(),
        @SerializedName("VU_ADM_VehicleOwnership")
        var vehicleOwnerships: List<VUADMVehicleOwnership> = arrayListOf(),
        @SerializedName("VU_CRM_RightOfPlaces")
        var ropList: List<ROPListItem> = arrayListOf(),
        @SerializedName("VU_CRM_PublicDomainOccupancy")
        var podList: List<ROPListItem> = arrayListOf(),
        @SerializedName("CRM_IncidentMaster")
        var incidentMgmtType: MutableList<CRMIncidentMaster> = arrayListOf(),
        @SerializedName("ServiceRequestTable")
        var serviceRequestTable: List<ServiceRequestTable> = arrayListOf(),
        @SerializedName("VU_CRM_ServiceRequests")
         var vUCRMServiceRequests :List<VUCRMServiceRequest> = arrayListOf(),
        @SerializedName("VU_CRM_ServiceTaxRequests")
        var vUCRMServiceTaxRequests :List<VUCRMServiceTaxRequest> = arrayListOf(),
        @SerializedName("CRM_CorporateTurnover")
        var corporateTurnOver: List<CRMCorporateTurnover> = arrayListOf(),
        @SerializedName("VU_CRM_CorporateTurnover")
        var vuCorporateTurnOver: List<VUCRMCorporateTurnover> = arrayListOf(),
        @SerializedName("VU_CRM_Accounts")
        var crmAccounts: List<VUCRMAccounts> = arrayListOf(),
        @SerializedName("COM_DocumentReference")
        var comDocumentReferences: List<COMDocumentReference> = arrayListOf(),
        @SerializedName("VU_CRM_ChainAgentTypes")
        var crmAgentTypes: MutableList<CRMAgentTypes> = arrayListOf(),
        @SerializedName("VU_CRM_Agents")
        var vuCrmAgents: MutableList<VUCRMAgents> = arrayListOf(),
        @SerializedName("UMX_UserOrgBranches")
        var userOrgBranches: MutableList<UMXUserOrgBranches> = arrayListOf(),
        @SerializedName("COM_PropertyTypes")
        var propertyTypes: List<COMPropertyTypes> = arrayListOf(),
        @SerializedName("VU_COM_ExistingProperties")
        var existingProperties: MutableList<VUCOMExistingProperties> = arrayListOf(),
        @SerializedName("VU_INV_MeasurementUnits")
        var measurementUnits: List<VUINVMeasurementUnits> = arrayListOf(),
        @SerializedName("VU_CRM_PropertyOnwerships")
        var propertyOwnership: List<VUCRMPropertyOwnership> = arrayListOf(),
        @SerializedName("VU_COM_GeoAddresses", alternate = ["COM_GeoAddresses"])
        var geoAddress: MutableList<GeoAddress> = arrayListOf(),
        @SerializedName("COM_Notes")
        var notes: MutableList<COMNotes> = arrayListOf(),
        @SerializedName("CRM_PropertyRentTypes")
        var rentTypes: MutableList<CRMPropertyRentTypes> = arrayListOf(),
        @SerializedName("VU_CRM_PropertyRents")
        var propertyRents: List<CRMPropertyRent> = arrayListOf(),
        @SerializedName("VU_CRM_TaxPayerAccountContacts")
        var businessOwnerships: List<BusinessOwnership> = arrayListOf(),
        @SerializedName("CRM_Professions")
        var professions: MutableList<CRMProfessions> = arrayListOf(),
        @SerializedName("CRM_CustomerSegments")
        var businessTypes: MutableList<CRMCustomerSegments> = arrayListOf(),
        @SerializedName("CommissionHistory")
        var commissionHistories: List<CommissionHistory> = arrayListOf(),
        @SerializedName("Results")
        var taxNoticeHistoryList: List<TaxNoticeHistoryList>? = null,
        @SerializedName("SearchResults")
        var handoverDueNoticesList: List<HandoverDueNoticesList>? = null,
        @SerializedName("VU_CRM_AgentSubscriptions")
        var vuCrmAgentSubscriptions: MutableList<AgentSubscriptionList> = arrayListOf(),
        @SerializedName("VU_CRM_SubscriptionModel")
        var vuCrmSubscriptionModel: MutableList<SubscriptionModel> = arrayListOf(),
        @SerializedName("CRM_AdvertisementTypes")
        var crmAdvertisementTypes: MutableList<CRMAdvertisementTypes> = arrayListOf(),
        @SerializedName("VU_CRM_Advertisements")
        var vuCrmAdvertisements: MutableList<VUCRMAdvertisements> = arrayListOf(),
        @SerializedName("VU_CRM_Shows")
        var shows: MutableList<ShowsDetailsTable> = arrayListOf(),
        @SerializedName("VU_CRM_Hotels")
        var hotels: MutableList<HotelDetails> = arrayListOf(),
        @SerializedName("ACC_OutstandingTypes")
        var outstandingTypes: MutableList<OutstandingType> = arrayListOf(),
        @SerializedName("VU_AST_AssetCategories")
        var assetCategories: MutableList<AssetCategory> = arrayListOf(),
        @SerializedName("COM_PaymentCycles")
        var comPaymentCycles: MutableList<PaymentCycles> = arrayListOf(),
        @SerializedName("COM_PricingRules")
        var comPricingRules: MutableList<PricingRules> = arrayListOf(),
        @SerializedName("AST_AssetInsuranceTypes")
        var assetInsuranceTypes: MutableList<GetInsuranceTypes> = arrayListOf(),
        @SerializedName("AST_AssetFitnessTypes")
        var assetFitnessTypes: MutableList<GetFitnessTypes> = arrayListOf(),
        @SerializedName("AST_AssetMaintenanceTypes")
        var assetMaintenanceeTypes: MutableList<GetMaintenanceTypes> = arrayListOf(),
        @SerializedName("VU_AST_Assets")
        var assets: MutableList<Asset> = arrayListOf(),
        @SerializedName("AST_AssetTypes")
        var assetTypes: MutableList<AssetType> = arrayListOf(),
        @SerializedName("BusinessTaxDueYearSummary")
        var businessTaxDueYearSummary: List<BusinessTaxDueYearSummary> = arrayListOf(),
        @SerializedName("VU_INV_ProductCategories")
        var productCategories: ArrayList<ProductCategory> = arrayListOf(),
        @SerializedName("CRM_IncidentSubtype")
        var incidentSubType: MutableList<CRMIncidentSubtype> = arrayListOf(),
        @SerializedName("CRM_CartTypes")
        var cartTypes: MutableList<CRMCartType> = arrayListOf(),
        @SerializedName("CRM_GamingMachineTypes")
        var gamingMachineTypes: ArrayList<GetGamingMachineTypes> = arrayListOf(),
        @SerializedName("IndividualTaxDueYearSummary")
        var individualTaxSummary: List<BusinessTaxDueYearSummary> = arrayListOf(),
        @SerializedName("IndividualTaxNoticeHistory")
        var individualTaxNoticeHistoryList: List<TaxNoticeHistoryList>? = null,
        @SerializedName("COM_BankMaster")
        var comBankMasters: ArrayList<COMBankMaster> = arrayListOf(),
        @SerializedName("AssetRentTypeList")
        var assetRentTypes: ArrayList<AssetRentType> = arrayListOf(),
        @SerializedName("VU_CRM_PoliceStations")
        var policeStations: MutableList<VUCRMPoliceStation> = arrayListOf(),
        @SerializedName("VU_LAW_ViolationTypes", alternate = ["LAW_ViolationTypes"])
        var violationTypes: MutableList<LAWViolationType> = arrayListOf(),
        @SerializedName("LAW_ImpoundmentReasons")
        var impoundmentReasons: MutableList<LAWImpoundmentReason> = arrayListOf(),
        @SerializedName("LAW_ImpoundmentSubTypes")
        var impoundmentSubTypes: MutableList<LAWImpoundmentSubType> = arrayListOf(),
        @SerializedName("LAW_ImpoundmentTypes")
        var impoundmentTypes: MutableList<LAWImpoundmentType> = arrayListOf(),
        @SerializedName("VU_ADM_ParkingTypes")
        var parkingTypes: MutableList<ADMParkingType> = arrayListOf(),
        @SerializedName("VU_ADM_PoliceStationYards")
        var policeStationYards: MutableList<PoliceStationYards> = arrayListOf(),
        @SerializedName("VU_LAW_TowingCraneTypes")
        var craneTypes: MutableList<VULAWTowingCraneTypes> = arrayListOf(),
        @SerializedName("LAW_ViolatorTypes")
        var violatorTypes: MutableList<LAWViolatorTypes> = arrayListOf(),
        @SerializedName("COM_ElectricityConsumptions")
        var electricityConsumptions: MutableList<COMElectricityConsumption> = arrayListOf(),
        @SerializedName("COM_PhaseOfElectricity")
        var phasesOfElectricity: MutableList<COMPhaseOfElectricity> = arrayListOf(),
        @SerializedName("COM_WaterConsumptions")
        var waterConsumptions: MutableList<COMWaterConsumption> = arrayListOf(),
        @SerializedName("VU_COM_LANDS", alternate = ["VU_COM_Lands"])
        var lands: MutableList<VUCOMLand> = arrayListOf(),
        @SerializedName("VU_COM_PropertyTypes")
        var propertyTypesVU: MutableList<COMPropertyTypes> = arrayListOf(),
        @SerializedName("VU_COM_PROPERTIES", alternate = ["VU_COM_Properties"])
        var properties: MutableList<VUCOMProperty> = arrayListOf(),
        @SerializedName("COM_PropertyExemptionReasons")
        var propertyExemption: MutableList<COMPropertyExemptionReasons> = arrayListOf(),
        @SerializedName("COM_LandUseTypes")
        var landUseTypes: MutableList<COMLandUseTypes> = arrayListOf(),
        @SerializedName("VU_CRM_TypeOfOperators")
        var operatorTypes: MutableList<VUCRMTypeOfOperators> = arrayListOf(),
        @SerializedName("VU_CRM_StarOfHotels")
        var starOfHotel: MutableList<VUCRMStarOfHotels> = arrayListOf(),
        @SerializedName("COM_PropertyRegistrationTypes")
        var propertyRegistrationTypes: MutableList<COMPropertyRegistrationTypes> = arrayListOf(),
        @SerializedName("VU_CRM_CategoryOfLicenses")
        var licenseCategories: MutableList<VUCRMCategoryOfLicenses> = arrayListOf(),
        @SerializedName("CRM_ServiceTypes")
        var serviceTypes: MutableList<CRMServiceType> = arrayListOf(),
        @SerializedName("CRM_ServiceSubTypes")
        var serviceSubTypes: MutableList<CRMServiceSubType> = arrayListOf(),
        @SerializedName("CRM_ComplaintMaster")
        var complaintMaster: MutableList<CRMComplaintMaster> = arrayListOf(),
        @SerializedName("CRM_ComplaintSubtype")
        var complaintSubtype: MutableList<CRMComplaintSubtype> = arrayListOf(),
        @SerializedName("COM_HotelDesFinances")
        var hotelDesFinances: MutableList<COMHotelDesFinances> = arrayListOf(),
        @SerializedName("TotalRecordCounts" , alternate = ["TotalRecordsFound"])
        var totalRecordCounts : Int? = null
)
