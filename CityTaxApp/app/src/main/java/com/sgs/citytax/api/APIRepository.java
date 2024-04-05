package com.sgs.citytax.api;

import com.sgs.citytax.api.payload.*;
import com.sgs.citytax.api.response.*;
import com.sgs.citytax.model.AvailableDatesForAssetBooking;
import com.sgs.citytax.model.BusinessOwnership;
import com.sgs.citytax.model.CRMAgents;
import com.sgs.citytax.model.CartTax;
import com.sgs.citytax.model.DataTaxableMatter;
import com.sgs.citytax.model.GamingMachineTax;
import com.sgs.citytax.model.GetPropertyEstimatedTax4PropPayload;
import com.sgs.citytax.model.NewTicketCreationResponse;
import com.sgs.citytax.model.ProductDetails;
import com.sgs.citytax.model.ROPDetails;
import com.sgs.citytax.model.SAL_TaxDetails;
import com.sgs.citytax.model.TaxPayerDetails;
import com.sgs.citytax.model.TaxPayerListResponse;
import com.sgs.citytax.model.VehicleSycotaxListResponse;
import com.sgs.citytax.model.Weapon;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIRepository {

    // region Common
    @POST("/api/UMX/GetOrganizationLogo")
    Call<MethodReturn<UMXUserOrganizations>> getOrganizationLogo(@Body LoginPayload payload);
    // endregion

    // region Common
    @POST("api/Common/UpdateConnectedDevice/")
    Call<MethodReturn> updateConnectedDevice(@Body UpdateConnectedDevice payload);
    // endregion

    // region CRM
    @POST("api/CRM/UpdateEntityDocuments/")
    Call<MethodReturn> updateEntityDocuments(@Body UpdateEntityDocument payload);

    @POST("api/CRM/GetTaxPayerDetails/")
    Call<MethodReturn<TaxPayerDetails>> getTaxPayerDetails(@Body GetTaxPayerDetails payload);

    @POST("api/CRM/StoreCustomerB2B/")
    Call<MethodReturn<TaxPayerResponse>> storeCustomerB2B(@Body StoreCustomerB2B payload);

    @POST("api/CRM/GetCorporateOfficeLOVValues")
    Call<MethodReturn<DataResponse>> getCorporateOfficeLOVValues(@Body GetCorporateOfficeLOVValues payload);

    @POST("api/CRM/GetAgentTransactionDetails")
    Call<MethodReturn<List<CRMAgentTransaction>>> getAgentTransactionDetails(@Body GetAgentTransactionDetails payload);

    @POST("api/CRM/GetCorporateOfficeChildTabDetails")
    Call<MethodReturn<DataResponse>> getCorporateOfficeChildTabDetails(@Body GetCorporateOfficeChildTabDetails payload);

    @POST("api/CRM/GetCorporateOfficeChildTabDetails")
    Call<MethodReturn<TaxesResponse>> getTaxes(@Body GetCorporateOfficeChildTabDetails payload);

    @POST("api/CRM/GetCorporateOfficeChildTabList")
    Call<MethodReturn<DataResponse>> getCorporateOfficeChildTabList(@Body GetCorporateOfficeChildTabList payload);

    @POST("api/CRM/InsertVehicleOwnership/")
    Call<MethodReturn> insertVehicleOwnership(@Body GetInsertVehicleOwnershipDetails payload);

    @POST("api/CRM/SavePublicDmnOccupyDetails/")
    Call<MethodReturn> insertPDODetails(@Body GetInsertPODDetails payload);

    @POST("api/CRM/SaveRightOfPlaceDetails/")
    Call<MethodReturn> insertROPDetails(@Body GetInsertROPDetails payload);

    @POST("api/CRM/InsertPropertyOwnership/")
    Call<MethodReturn> insertPropertyOwnership(@Body InsertPropertyOwnership payload);

    @POST("api/CRM/SaveGeoAddress")
    Call<MethodReturn> saveGeoAddress(@Body SaveGeoAddress payload);

    @POST("api/CRM/InsertAccountPhone")
    Call<MethodReturn> insertAccountPhone(@Body InsertAccountPhone payload);

    @POST("api/CRM/SaveCustomerProductInterests")
    Call<MethodReturn> saveCustomerProductInterests(@Body SaveCustomerProductInterests payload);

    @POST("api/CRM/InsertAccountEmail")
    Call<MethodReturn> insertAccountEmail(@Body InsertAccountEmail payload);

    @POST("api/CRM/GetAgentDetails")
    Call<MethodReturn<DataResponse>> getAgentDetails(@Body GetAgentDetails payload);

    @POST("api/CRM/InsertAgent")
    Call<MethodReturn> storeAgentDetails(@Body StoreAgentDetails payload);

    @POST("api/CRM/AddServiceRequest")
    Call<MethodReturn> addServiceRequest(@Body AddServiceRequest payload);

    @POST("api/CRM/AddServiceRequest")
    Call<MethodReturn> newAddServiceRequest(@Body NewAddServiceRequest payload);

    @POST("api/CRM/AddServiceRequestUpdate")
    Call<MethodReturn> editServiceRequestUpdate(@Body EditServiceRequest payload);


    @POST("api/CRM/GetServiceRequestUpdates")
    Call<MethodReturn<DataResponse>> getCommentsAndDocuments(@Body GetServiceRequest payload);

    @POST("api/CRM/GetTaxPayers")
    Call<MethodReturn<DataResponse>> getTaxPayers(@Body GetTaxPayers payload);

    @POST("api/CRM/InsertAgent")
    Call<MethodReturn> insertAgent(@Body InsertAgent payload);

    @POST("api/CRM/GetAgents")
    Call<MethodReturn<AgentResponse>> getAgents(@Body GetAgents payload);

    @POST("api/CRM/GetTaxPayerList")
    Call<MethodReturn<DataResponse>> getTaxPayersList(@Body GetTaxPayerList payload);

    @POST("api/CRM/AgentSummaryDetails")
    Call<MethodReturn<AgentSummaryDetailsResponse>> getSummaryDetails(@Body GetAgents payload);

    @POST("api/CRM/ValidateTaxPayer")
    Call<MethodReturn<DataResponse>> getTaxPayersListForVerification(@Body ValidateTaxPayer payload);

    @POST("api/CRM/InsertDocuments")
    Call<MethodReturn> insertDocument(@Body InsertDocument payload);

    @POST("api/CRM/StoreDueAgreements")
    Call<MethodReturn<StoreDueAgreementResponse>> saveAgreement(@Body SaveAgreement payload);

    @POST("api/CRM/UpdateDueNotices")
    Call<MethodReturn> UpdateDueNotices(@Body DueNotice payload);

    @POST("api/CRM/InsertNotes")
    Call<MethodReturn> insertNotes(@Body InsertNotes payload);

    @POST("api/CRM/GetNotesDetails")
    Call<MethodReturn<DataResponse>> getNotesDetails(@Body GetNotesDetails payload);

    @POST("api/CRM/InsertPropertyRents")
    Call<MethodReturn> insertPropertyRents(@Body InsertPropertyRents payload);

    @POST("api/CRM/GetDocumentDetails")
    Call<MethodReturn<DataResponse>> getDocumentDetails(@Body GetCorporateOfficeChildTabDetails payload);

    @POST("api/CRM/GetDocumentDetails")
    Call<MethodReturn<DataResponse>> getDocumentDetails(@Body GetVehicleDocumentChildTabDetails payload);

    @POST("api/CRM/StoreCustomerB2C")
    Call<MethodReturn<BusinessOwnership>> storeCustomerB2C(@Body StoreCustomerB2C payload);

    @POST("api/CRM/GetChildAgentSummary")
    Call<MethodReturn<AgentCollectionSummaryResponse>> getChildAgentSummary(@Body GetChildAgentSummary payload);

    @POST("api/CRM/GetBusinessTaxNoticeHistory")
    Call<MethodReturn<DataResponse>> getBusinessTaxNoticeHistory(@Body GetBusinessTaxNoticeHistory payload);

    @POST("api/CRM/SearchDueNoticesBySycotaxID")
    Call<MethodReturn<DataResponse>> getHandoverDueNotices(@Body GetHandoverDueNotice payload);

    @POST("api/CRM/GetBusinessTransactionHistory")
    Call<MethodReturn<BusinessTransactionHistory>> getBusinessTransactionHistory(@Body GetBusinessTransactionHistory payload);

    @POST("api/LAW/GetTrankOnTransactions")
    Call<MethodReturn<TrackOnTransactionHistory>> getTrackOnTransactionHistory(@Body GetTrackOnTransactionHistory payload);

    @POST("api/CRM/GetActivityDomainList")
    Call<MethodReturn<ActivityDomains>> getActivityDomains(@Body GetActivityDomains payload);

    @POST("api/CRM/GetAgentsByAgentType")
    Call<MethodReturn<AgentResponse>> getAgentsByAgentType(@Body GetAgents payload);

    @POST("api/CRM/DeleteCustomerProductInterests")
    Call<MethodReturn> deleteCustomerProductInterests(@Body DeleteCustomerProductInterests payload);

    @POST("api/CRM/UpdateStatusCustomerProdIntrst")
    Call<MethodReturn> updateStatusCustomerProdIntrst(@Body UpdateStatusCustomerProdIntrst payload);

    //@POST("api/CRM/GetBusinessLocation4Agent")
    @POST("api/CRM/GetBusinessLocationByRadius")
    Call<MethodReturn<BusinessLocation4Agent>> getBusinessLocation4Agent(@Body GetBusinessLocation4Agent payload);

    @POST("api/CRM/DeleteAccountMappingData")
    Call<MethodReturn> deleteAccountMappingData(@Body DeleteAccountMappingData payload);

    @POST("api/CRM/SearchTaxPayerDetails/")
    Call<MethodReturn<List<TaxPayerDetails>>> searchTaxPayerDetails(@Body GetTaxPayerDetails payload);

 @POST("api/CRM/SearchForTaxPayer/")
    Call<MethodReturn<TaxPayerListResponse>> searchTaxPayerList(@Body GetTaxPayerDetails payload);

    @POST("api/CRM/TaxPayerDetails4Verification/")
    Call<MethodReturn<BusinessVerificationResponse>> getTaxPayerDetailsForVerification(@Body GetTaxPayerDetails payload);

    @POST("api/CRM/GetAgentCurrentLocations/")
    Call<MethodReturn<AgentCurrentLocations>> getAgentCurrentLocations(@Body GetLocations payload);

    @POST("api/CRM/GetIncidentLocations/")
    Call<MethodReturn<IncidentLocations>> getIncidentLocations(@Body GetLocations payload);

    @POST("api/CRM/GetComplaintIncidetLocations/")
    Call<MethodReturn<ComplaintIncidentLocations>> getComplaintIncidentLocations(@Body GetLocations payload);

    @POST("api/CRM/GetComplaintLocations/")
    Call<MethodReturn<ComplaintLocations>> getComplaintLocations(@Body GetComplaintLocations payload);

    @POST("api/CRM/getSellableProductForAgent")
    Call<MethodReturn<List<SellableProduct>>> getSellableProductForAgent(@Body GetProductByType payload);

    @POST("api/CRM/GetAgentSubscription")
    Call<MethodReturn<List<SubscriptionResponse>>> getSubscriptionDetails(@Body GetSubscriptionDetails details);

    @POST("api/CRM/GetSubscriptionDetils")
    Call<MethodReturn<GetSubscriptionAmountDetails>> getSubscriptionAmount(@Body SubscriptionDetails details);

    @POST("api/CRM/StoreAndPay4SubscriptionRenewal")
    Call<MethodReturn> storeAndPay4SubscriptionRenewal(@Body StoreAndPaySubscriptionRenewal payload);

    @POST("api/CRM/GetAgentCommissionBalance")
    Call<MethodReturn> getAgentCommissionBalance(@Body GetAgentCommissionBalance payload);

    @POST("api/CRM/GetChildAgents")
    Call<MethodReturn<List<CRMAgents>>> getChildAgentsBySearch(@Body GetChildAgents payload);

    @POST("api/CRM/GetChildAgents4Verification")
    Call<MethodReturn<List<CRMAgents>>> getChildAgentsForVerification(@Body GetChildAgents payload);

    @POST("api/CRM/InsertAdvertisements")
    Call<MethodReturn> insertAdvertisements(@Body InsertAdvertisements payload);

    @POST("/api/CRM/GetCustomerProductWithMultiInvoice")
    Call<MethodReturn<List<CustomerProduct>>> getTaxTypes(@Body GetTaxTypes payload);

    @POST("/api/CRM/GetVoucherNumberForInitialOutstanding")
    Call<MethodReturn<List<OutstandingVoucherNo>>> getVoucherNo(@Body GetVoucherNo payload);

    @POST("api/CRM/DeleteInitialOutstanding")
    Call<MethodReturn> deleteOutstanding(@Body DeleteOutstanding payload);

    @POST("api/CRM/BusinessInitialOutstandingList")
    Call<MethodReturn<ArrayList<GetOutstanding>>> getInitialOutstandingList(@Body GetOutstandingList payload);

    @POST("api/CRM/GetDynamicFormSpecs4Asset")
    Call<MethodReturn<List<AssetSpecs>>> getDynamicFormSpecs4Asset(@Body GetDynamicFormSpecs4Asset payload);

    @POST("api/CRM/GetAssetCertificateDetails")
    Call<MethodReturn<AssetInsuranceResponse>> getAssetCertificateDetailsInsurance(@Body GetAssetCertificateDetails payload);

    @POST("api/CRM/GetAssetCertificateDetails")
    Call<MethodReturn<AssetFitnessesResponse>> getAssetCertificateDetailsFitness(@Body GetAssetCertificateDetails payload);

    @POST("api/CRM/GetAssetCertificateDetails")
    Call<MethodReturn<AssetMaintenanceResponse>> getAssetCertificateDetailsMaintenance(@Body GetAssetCertificateDetails payload);

    @POST("api/CRM/GetBusinessTaxDueYearSummary")
    Call<MethodReturn<DataResponse>> getBusinessTaxDueYearSummary(@Body GetBusinessTaxDueYearSummary payload);

    @POST("api/CRM/GetBusinessDueSummary")
    Call<MethodReturn<BusinessDueSummaryResults>> getBusinessDueSummary(@Body GetBusinessDueSummaryDetails payload);

    @POST("api/CRM/IsCartSycotaxAvailable")
    Call<MethodReturn> isCartSycoTaxAvailable(@Body CartSycoTax payload);

    @POST("api/CRM/StoreCarts")
    Call<MethodReturn> storeCartTax(@Body StoreCartTax payload);

    @POST("api/CRM/GetIndividualTax")
    Call<MethodReturn<CartTax>> getIndividualTaxForCart(@Body GetIndividualTax payload);

    @POST("api/CRM/IsGamingSycotaxAvailable")
    Call<MethodReturn> isGamingSycotaxAvailable(@Body CartSycoTax payload);

    @POST("api/CRM/StoreGamingMachines")
    Call<MethodReturn> storeGamingMachine(@Body StoreGamingMachinesTax payload);

    @POST("api/CRM/GetIndividualTax")
    Call<MethodReturn<GamingMachineTax>> getIndividualTax(@Body GetIndividualTax payload);

    @POST("api/CRM/IsWeaponSycotaxAvailable")
    Call<MethodReturn> checkWeaponSycotaxAvailable(@Body CartSycoTax payload);

    @POST("api/CRM/StoreWeapons")
    Call<MethodReturn> storeWeapons(@Body StoreWeapon payload);

    @POST("api/CRM/GetIndividualTax")
    Call<MethodReturn<Weapon>> getIndividualTaxForWeapon(@Body GetIndividualTax payload);

    @POST("api/CRM/IsNoticeGen4IndividualTax")
    Call<MethodReturn> isNoticeGen4IndividualTax(@Body CartSycoTax payload);

    @POST("api/CRM/GetIndividualDueSummary")
    Call<MethodReturn<BusinessDueSummaryResults>> getIndividualDueSummary(@Body GetIndividualDueSummary payload);

    @POST("api/CRM/GetIndividualTaxDueYearSummary")
    Call<MethodReturn<DataResponse>> getIndividualTaxDueYearSummary(@Body GetIndividualTaxDueYearSummary payload);

    @POST("api/CRM/GetOrSearchIndividualTaxDetails")
    Call<MethodReturn<GetSearchIndividualTaxDetails>> getOrSearchIndividualTaxDetails(@Body CartSycoTax payload);

    @POST("api/CRM/GetIndividualTaxNoticeHistory")
    Call<MethodReturn<DataResponse>> getIndividualTaxNoticeHistory(@Body GetIndividualTaxNoticeHistory payload);

    @POST("api/CRM/IncidentAndComplaintsCounts")
    Call<MethodReturn<IncidentAndComplaintsCountsResponse>> getIncidentAndComplaintsCounts(@Body GetIncidentAndComplaintsCounts payload);

    @POST("api/CRM/GetIndividualTaxCount4Business")
    Call<MethodReturn<GetIndividualTaxCount>> getIndividualTaxCount4Business(@Body GetIndividualTaxCount4Business payload);

    @POST("api/CRM/GetIndividualTax4Business")
    Call<MethodReturn<WeaponTaxListResponse>> getWeaponList(@Body GetIndividualTax4Business payload);

    @POST("api/CRM/GetIndividualTax4Business")
    Call<MethodReturn<CartTaxListResponse>> getCartList(@Body GetIndividualTax4Business payload);

    @POST("api/CRM/GetIndividualTax4Business")
    Call<MethodReturn<GameMachineTaxListResponse>> getGameMachineList(@Body GetIndividualTax4Business payload);

    @POST("api/CRM/GetIndividualTax4Business")
    Call<MethodReturn<WeaponTaxSummaryResponse>> getWeaponSummary(@Body GetIndividualTax4Business payload);

    @POST("api/CRM/GetIndividualTax4Business")
    Call<MethodReturn<CartTaxSummaryResponse>> getCartSummary(@Body GetIndividualTax4Business payload);

    @POST("api/CRM/GetIndividualTax4Business")
    Call<MethodReturn<GammingTaxSummaryResponse>> getGammingSummary(@Body GetIndividualTax4Business payload);

    @POST("api/CRM/StoreShows")
    Call<MethodReturn> storeShow(@Body StoreShows payload);

    @POST("api/CRM/GetShowsDetails")
    Call<MethodReturn<ShowTaxListResponse>> getShowDetails(@Body GetShowsDetails payload);

    @POST(" api/CRM/DeleteShow")
    Call<MethodReturn> deleteShow(@Body DeleteShow payload);

    @POST("api/CRM/GetHotelDetails")
    Call<MethodReturn<HotelDetailsListResponse>> getHotelDetails(@Body GetHotelDetails payload);

    @POST("api/CRM/StoreHotels")
    Call<MethodReturn> storeHotel(@Body StoreHotels payload);

    @POST(" api/CRM/DeleteHotel")
    Call<MethodReturn> deleteHotel(@Body DeleteHotel payload);

    @POST("api/CRM/GetShowOrHotelBillingAndPricing/")
    Call<MethodReturn<ShowAndHotelBillingAndPricingResponse>> getShowOrHotelBillingResponse(@Body GetShowOrHotelBillingAndPricing payload);

    @POST("api/CRM/GetPendingLicenses4Agent/")
    Call<MethodReturn<GetPendingLicenses4AgentResponse>> getPendingLicenses4Agent(@Body GetPendingLicenses4Agent payload);

    @POST("api/CRM/ScanSearchPendingLicenses/")
    Call<MethodReturn<SearchPendingLicensesResponse>> scanSearchPendingLicenses(@Body SearchPendingLicenses payload);

    @POST("api/CRM/StoreLicenses")
    Call<MethodReturn> storeLicenses(@Body StoreLicenses payload);

    @POST("api/CRM/GetBillingCycleDates/")
    Call<MethodReturn<BillingCycleDatesResponse>> getBillingCycleDates(@Body GetBillingCycleDates payload);

    @POST("api/CRM/GetLicensesDetails")
    Call<MethodReturn<LicenseDetailsResponse>> getLicenseDetails(@Body GetLicensesDetails payload);

    @POST("api/CRM/DeleteLicenses")
    Call<MethodReturn> deleteLicense(@Body DeleteLicenses payload);

    @POST("api/CRM/GetLicenseRenewalHistory")
    Call<MethodReturn<LicenseRenewalResp>> getPropertyTaxTransactions(@Body LicenseRenewalPayload payload);

    @POST("api/CRM/StoreEditServiceTaxRequest")
    Call<MethodReturn> saveServiceTaxRequest(@Body SaveServiceTaxRequest payload);

    @POST("api/CRM/GetPendingServiceInvoiceList")
    Call<MethodReturn<PendingServiceListResponse>> getPendingServiceInvoiceList(@Body GetPendingServiceInvoiceList payload);

    @POST("api/CRM/GetServiceTaxRequestList")
    Call<MethodReturn<ServiceRequestResponse>> getServiceTaxRequests(@Body GetServiceTaxRequests payload);

    @POST("api/CRM/GetServiceRequestBookingAdvanceDetails")
    Call<MethodReturn<ServiceBookingAdvanceReceiptResponse>> getServiceBookingAdvanceReceiptDetails(@Body ServiceBookingAdvancePayment payload);

    @POST("api/CRM/GetRandomCitizenSycotaxIDList")
    Call<MethodReturn<CitizenSycoTaxResponse>> getRandomCitizenSycoTaxIDs(@Body GetRandomCitizenSycoTaxIDs payload);

    @POST("api/CRM/StoreCitizenIdentityCards")
    Call<MethodReturn<CitizenIdentityCard>> storeCitizenIdentityCard(@Body StoreCitizenIdentityCard payload);

    @POST("api/CRM/GetCitizenIdentityCardsList")
    Call<MethodReturn<GetCitizenIdentityCardsResponse>> getCitizenIdentityCards(@Body GetCitizenIdentityCards payload);

    @POST("api/CRM/IsCitizenSycotaxAvailable")
    Call<MethodReturn> isCitizenSycoTaxAvailable(@Body IsCitizenSycotaxAvailable payload);

    @POST("api/CRM/GetParentPropertyPlanImages")
    Call<MethodReturn> getParentPropertyPlanImages(@Body GetParentPropertyPlanImages payload);

    @POST("api/CRM/GetParentPropertyImages")
    Call<MethodReturn> getParentPropertyImages(@Body GetParentPropertyPlanImages payload);

    @POST("api/CRM/GetParentPropertyDocuments")
    Call<MethodReturn> getParentPropertyDocuments(@Body GetParentPropertyPlanImages payload);

    @POST("api/CRM/StoreCorporateTurnoverProportionalDuty")
    Call<MethodReturn<CorporateTurnoverResponse>> insertCorporateTurnOver(@Body InsertCorporateTurnover payload);

    @POST("api/CRM/GetLastBillingCycleActualAmount")
    Call<MethodReturn> getLastBillingCycleActualAmount(@Body LastBillingCycleActualAmount payload);

    @POST("api/CRM/GetVehicleOwnershipIDByVehicleNo")
    Call<MethodReturn<BigInteger>> getVehicleOwnershipIDByVehicleNo(@Body GetVehicleOwnershipIDbyVehNo payload);

    @POST("api/CRM/GetAgentTransactionDetails")
    Call<MethodReturn<CRMAgentTransactionResponse>> getAgentTransactionDetailsNew(@Body GetAgentTransactionDetails payload);


    @POST("api/CRM/GetCorporateOfficeLOVValues")
    Call<MethodReturn<GetDropdownFiltersForBusinessSearchResponse>> getDropdownFiltersForBusinessSearch(@Body GetDropdownFiltersForBusinessSearch payload);

    @POST("api/CRM/GetCorporateOfficeLOVValues")
    Call<MethodReturn<GetDropdownFiltersForLAWSearchResponse>> getDropdownFiltersForLawSearch(@Body GetDropdownFiltersForBusinessSearch payload);

    @POST("api/CRM/SearchForTaxPayerForMap")
    Call<MethodReturn<SearchForTaxPayerForMapResponse>> searchForTaxPayerForMap(@Body SearchForTaxPayerForMapPayload payload);

    @POST("api/CRM/GetDueNoticesByAccountID")
    Call<MethodReturn<ListDueNoticeResponse>> getListDueNotice(@Body ListDueNotice payload);

@POST("api/CRM/GetDueAgreementsByAccountID")
    Call<MethodReturn<AgreementDetailsList>> getAgreementList4Business(@Body GetAgreementList4Business payload);

@POST("api/CRM/GetPendingDueNoticeByAccountID")
    Call<MethodReturn<TaxDueListResponse>> searchTaxDueList(@Body GetTaxDueList payload);

    @POST("api/CRM/GetAdminOfficeAddress")
    Call<MethodReturn<AdminOfficeAddressResponse>> getAdminOfficeAddress(@Body GetAdminOfficeAddressPayload payload);

    // endregion

    // region FRM
    @POST("api/FRM/PaymentByCash4TaxInvoices/")
    Call<MethodReturn> paymentByCash4TaxInvoices(@Body PaymentByCash4TaxInvoices payload);

    //for ticket payment
    @POST("api/FRM/PaymentByCash4PRKTaxInvoices/")
    Call<MethodReturn> paymentByCash4ParkingTicketPayment(@Body PaymentByCash4TicketPayment payload);

    //for ticket payment
    @POST("api/FRM/PaymentByCash4LawTaxInvoices/")
    Call<MethodReturn> paymentByCash4TicketPayment(@Body PaymentByCash4TicketPayment payload);

    @POST("api/FRM/PaymentByCheque4LawTaxInvoices/")
    Call<MethodReturn> paymentByCheque4TicketPayment(@Body PaymentByCheque4TicketPayment payload);

    @POST("api/FRM/PaymentByWallet4TaxInvoices/")
    Call<MethodReturn> paymentByWallet4TaxInvoices(@Body PaymentByWallet4TaxInvoices payload);

    @POST("api/FRM/PaymentByWallet4TaxInvoices/")
    Call<MethodReturn> paymentByWallet4TicketPayment(@Body PaymentByWallet4TicketPayment payload);

    @POST("api/FRM/PaymentByWallet4LawTaxInvoices/")
    Call<MethodReturn> paymentByLAWWallet4TicketPayment(@Body PaymentByWallet4TicketPayment payload);

    @POST("api/FRM/PaymentByWallet4AssetBooking/")
    Call<MethodReturn> paymentByWallet4AssetBooking(@Body PaymentByWallet4AssetBooking payload);

    @POST("api/FRM/PaymentByCash4AssetBooking")
    Call<MethodReturn> paymentByCash4AssetBooking(@Body PaymentByCash4AssetBooking payload);

    @POST("api/FRM/AgentSelfRecharge")
    Call<MethodReturn> agentSelfRecharge(@Body AgentSelfRecharge payload);

    @POST("api/FRM/InsertAgentCommission")
    Call<MethodReturn> insertAgentCommission(@Body InsertAgentCommission payload);

    @POST("api/FRM/InsertRequestCashDeposit")
    Call<MethodReturn> insertRequestCashDeposit(@Body InsertRequestCashDeposit payload);

    @POST("api/FRM/StoreInitialOutstandings")
    Call<MethodReturn> saveOutstanding(@Body SaveInitialOutstanding payload);

    @POST("api/FRM/StoreAsset")
    Call<MethodReturn> storeAsset(@Body StoreAsset payload);

    @POST("api/FRM/StoreAssetInsurances")
    Call<MethodReturn> storeAssetInsurances(@Body StoreInsuranceData payload);

    @POST("api/FRM/DeleteAssetInsurances")
    Call<MethodReturn> deleteInsuranceDocument(@Body DeleteAssetInsurance payload);

    @POST("api/FRM/StoreAssetFitnesses")
    Call<MethodReturn> storeAssetFitness(@Body StoreFitnessData payload);

    @POST("api/FRM/DeleteAssetFitnesses")
    Call<MethodReturn> deleteAssetFitness(@Body DeleteAssetFitness payload);

    @POST("api/FRM/StoreAssetMaintenance")
    Call<MethodReturn> storeAssetMaintenance(@Body StoreMaintenanceData payload);

    @POST("api/FRM/DeleteAssetMaintenance")
    Call<MethodReturn> deleteAssetMaintenance(@Body DeleteAssetMaintenance payload);

    @POST("api/FRM/InsertAssetBooking")
    Call<MethodReturn> insertAssetBooking(@Body InsertAssetBookingRequest payload);

    @POST("api/FRM/ValidateAsset4AssignRent")
    Call<MethodReturn<ValidateAssetForAssignAndReturnResponse>> validateAsset4AssignRent(@Body ValidateAsset4AssignRent payload);

    @POST("api/FRM/ValidateAsset4Return")
    Call<MethodReturn<ValidateAssetForAssignAndReturnResponse>> validateAsset4Return(@Body ValidateAsset4Return payload);

    @POST("api/FRM/MakeMobicashPaymentRequest")
    Call<MethodReturn<MobiCashPayment>> makeMobicashPaymentRequest(@Body MobiCashTransaction payload);

    @POST("api/FRM/GetMobiCashPaymentStatus")
    Call<MethodReturn<MobiCashPayment>> getMobiCashPaymentStatus(@Body MobiCashPaymentStatus payload);
    @POST("api/FRM/PaymentByCheque4TaxInvoices")
    Call<MethodReturn> paymentByCheque4TaxInvoices(@Body PaymentByCheque payload);

    @POST("api/FRM/PaymentByCash4ServiceTax")
    Call<MethodReturn> paymentByCash4ServiceTax(@Body PaymentByCash4ServiceTax payload);

    @POST("api/FRM/PaymentByWallet4ServiceTax/")
    Call<MethodReturn> paymentByWallet4ServiceTax(@Body PaymentByWallet4ServiceTax payload);

    @POST("api/CRM/GetServiceRequestBookingDetails")
    Call<MethodReturn<ServiceRequestBookingReceiptResponse>> getServiceRequestBookingDetails(@Body ServiceRequestBookingDetails payload);

    @POST("api/FRM/AmountToText")
    Call<MethodReturn> convertAmountToText(@Body AmountToText payload);

    @POST("api/FRM/IsReceiptPrintAlllowed")
    Call<MethodReturn> getReceiptPrintFlag(@Body ReceiptPrintFlag payload);

    // endregion

    // region SAL
    @POST("api/SAL/GetTaxDetails")
    Call<MethodReturn<List<SAL_TaxDetails>>> getTaxDetails(@Body GetTaxDetails payload);

    @POST("api/SAL/GenerateTaxInvoice")
    Call<MethodReturn> getGenerateTaxInvoice(@Body GenerateCustomerTaxNotice payload);

    @POST("api/SAL/GetTaxPaymentHistory")
    Call<MethodReturn<TaxPaymentHistoryResponse>> getTaxPaymentHistory(@Body GetTaxPaymentHistory payload);

    @POST("api/SAL/GetROPDetails")
    Call<MethodReturn<List<ROPDetails>>> getROPDetails(@Body GetROPDetails payload);

    @POST("api/SAL/CreateROPTaxInvoice")
    Call<MethodReturn> createROPTaxInvoice(@Body CreateROPTaxInvoice payload);

    @POST("api/SAL/GetAgentBalance")
    Call<MethodReturn> getAgentBalance(@Body GetAgentBalance payload);

    @POST("api/SAL/GetAgentStatement")
    Call<MethodReturn<CreditBalanceResponse>> getAgentStatement(@Body GetAgentStatement payload);

    @POST("api/SAL/GetInvoiceTemplateInfo")
    Call<MethodReturn<List<GetInvoiceTemplateResponse>>> getInvoiceTemplateInfo(@Body GetInvoiceTemplateInfo payload);
//Newly added function
    @POST("api/SAL/AgentCommissionPayOutList")
    Call<MethodReturn<AgentCommissionResult>> getPayoutListForAgent(@Body AgentCommissionPayOut payload);

    @POST("api/SAL/CancelTaxNotice")
    Call<MethodReturn> cancelTaxNotice(@Body CancelTaxNotice payload);

    @POST("api/SAL/SellableProductGenInvAndPay")
    Call<MethodReturn> sellableProductGenInvAndPay(@Body Payload4SalesTax payload);

    @POST("api/SAL/GetInitialOutstandingPenalties")
    Call<MethodReturn<GetOutstandingWaiveOffResponse>> getInitialOutstandingPenalties(@Body InitialOutstandingPenalties payload);

    @POST("api/SAL/InitialOutstandingPenaltyWaiveOff")
    Call<MethodReturn> getInitialOutstandingPenaltyWaiveOff(@Body InitialOutstandingWaiveOff payload);

    @POST("api/SAL/PenaltyWaveOff")
    Call<MethodReturn> penaltyWaiveOff(@Body PenaltyWaiveOff payload);

    @POST("api/SAL/GetEstimatedTaxForProduct")
    Call<MethodReturn> getEstimatedTaxForProduct(@Body GetEstimatedTaxForProduct payload);

    @POST("api/SAL/GetTaxableMatterColumnData")
    Call<MethodReturn<List<DataTaxableMatter>>> getTaxableMatterColumnData(@Body GetTaxableMatterColumnData payload);

    @POST("api/SAL/GetIndividualTaxDetails")
    Call<MethodReturn<List<SAL_TaxDetails>>> getIndividualTaxDetails(@Body CartSycoTax payload);

    @POST("api/SAL/IndividualTaxInitOutstandingPenalties")
    Call<MethodReturn<GetOutstandingWaiveOffResponse>> individualTaxInitOutstandingPenalties(@Body GetIndividualTaxInitOutstandingPenalties payload);

    @POST("api/SAL/IndividualTaxInvoicePenalties")
    Call<MethodReturn<PenaltyList>> individualTaxInvoicePenalties(@Body GetIndividualTaxInvoicePenalties payload);

    @POST("api/SAL/PropertyTaxInvoicePenalties")
    Call<MethodReturn<PenaltyList>> getPropertyTaxInvoicePenalties(@Body GetPropertyTaxInvoicePenalties payload);

    @POST("api/SAL/GetPropertyTaxDetails")
    Call<MethodReturn<List<SAL_TaxDetails>>> getPropertyTaxDetails(@Body GetPropertyTaxDetails payload);

    @POST("api/SAL/PropertyTaxInitOutstandingPenalties")
    Call<MethodReturn<GetOutstandingWaiveOffResponse>> getPropertyInitialOutstandingPenalties(@Body PropertyTaxInitOutstandingPenalties payload);

    @POST("api/SAL/GetEstimatedTax4Licenses")
    Call<MethodReturn> getEstimatedTaxFoLicenses(@Body GetEstimatedTax4Licenses payload);

    @POST("api/SAL/GetEstimatedAmout4ServiceTax")
    Call<MethodReturn> getEstimatedAmount4ServiceTax(@Body GetEstimatedAmount4ServiceTax payload);


    @POST("api/SAL/GenerateServiceTaxInvoice")
    Call<MethodReturn<GenerateTaxNoticeResponse>> generateServiceTaxInvoice(@Body GenerateServiceTaxInvoice payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<ServiceTaxNoticeResponse>> getServiceTaxNoticeDetails(@Body TaxNoticePrintingDetails payload);

    @POST("api/SAL/GetEstimatedTax4Property")
    Call<MethodReturn<Double>> getPropertyEstimatedTax4Property(@Body GetPropertyEstimatedTax4PropPayload payload);

    @POST("api/SAL/IsNoticePrintAlllowed")
    Call<MethodReturn> getNoticePrintFlag(@Body NoticePrintFlag payload);

    @POST("api/SAL/GetInvoiceCount4Tax")
    Call<MethodReturn> getInvoiceCount4Tax(@Body CheckCurrentDue payload);
    // endregion

    //region INV
    @POST("api/INV/GetProductDetails")
    Call<MethodReturn<List<ProductDetails>>> getProducts(@Body GetProducts payload);

    @POST("api/INV/GetAllocationsForAccount")
    Call<MethodReturn<AllocatedStockResponse>> getAllocationsForAccount(@Body GetAllocationsForAccount payload);

    @POST("api/INV/GetCurrentStockForAccount")
    Call<MethodReturn<InventoryStatusResponse>> getCurrentStock(@Body GetCurrentStock payload);

    @POST("api/INV/GetAdjustmentDetails")
    Call<MethodReturn<AdjustmentListReturn>> getAdjustmentsDetailsList(@Body AdjustmentListDetails payload);

    @POST("api/CRM/GetStockAllocationHistory")
    Call<MethodReturn<StockTransferListReturn>> getStockTransferList(@Body StockTransferListPayload payload);
    //endregion

    // region AST
    @POST("api/AST/AssetBookingEstPrice")
    Call<MethodReturn> getAssetBookingEstimatedPrice(@Body AssetBookingEstimatedPrice payload);

    @POST("api/AST/GetAssetBookingTenureBookingAdvance")
    Call<MethodReturn<BookingTenureBookingAdvanceResponse>> getAssetBookingTenureBookingAdvance(@Body GetAssetBookingTenureBookingAdvance payload);

    @POST("api/AST/GetAssetBookingRequest/")
    Call<MethodReturn<List<AssetBooking>>> getBookings(@Body GetBookingsList payload);

    @POST("api/AST/AssignAsset")
    Call<MethodReturn> assignAsset(@Body AssignAsset payload);

    @POST("api/AST/ReturnAsset")
    Call<MethodReturn> returnAsset(@Body ReturnAsset payload);

    @POST("api/AST/GetAssetDetails")
    Call<MethodReturn<GetUpdateAsset>> updateAsset(@Body UpdateAsset payload);

    @POST("api/AST/GetAssetBookingStock")
    Call<MethodReturn<List<AvailableDatesForAssetBooking>>> getAssetBookingStock(@Body GetAssetBookingStock payload);

    @POST("api/AST/ValidateAssetBooking")
    Call<MethodReturn> validateAssetBooking(@Body ValidateAssetBooking payload);

    @POST("api/AST/GetAssetRentDetails")
    Call<MethodReturn<AssetRentalDetailsResponse>> getAssetRentalDetails(@Body GetAssetRentDetails payload);

    @POST("api/AST/SearchAssetDetailsBySycotax/")
    Call<MethodReturn<AssetDetailsBySycotax>> searchAssetDetails(@Body GetAssetsList payload);

    @POST("api/AST/SearchAssetDetails/")
    Call<MethodReturn<AssetDetailsBySearch>> searchAssetDetailsList(@Body GetTaxPayerDetails payload);

    @POST("api/AST/SearchAssetDetailsForUpdate/")
    Call<MethodReturn<AssetDetailsBySearch>> searchUpdateAssetDetailsList(@Body GetTaxPayerDetails payload);

    @POST("api/AST/SearchAvailableSycotaxID/")
    Call<MethodReturn<AssetSycoTaxIdBySearch>> searchAvailableSycotaxID(@Body GetTaxPayerDetails payload);

    @POST("api/AST/GetAssetRentTypeList/")
    Call<MethodReturn<DataResponse>> getAssetRentTypeList(@Body GetAssetRentType payload);

    @POST("api/AST/GetIndividualTaxTransactions")
    Call<MethodReturn<GetIndividualBusinessTransactionHistoryResults>> getIndividualTaxTransactions(@Body GetIndividualBusinessTransactionHistory payload);

    @POST("api/AST/GetFormSpecs4AssetPrecheckData")
    Call<MethodReturn<AssetPrePostResponse>> getAssetRentalSpecificationForDynamicForm(@Body GetFormSpecs4AssetPrecheckData payload);

    @POST("api/AST/GetAssetList4Return")
    Call<MethodReturn<AssetsForReturnResponse>> getAssetListForReturn(@Body GetAssetList4Return payload);

    @POST("api/AST/GetPendingAssetBookingRequest")
    Call<MethodReturn<PendingBookingResponse>> getPendingBookingsList(@Body GetPendingAssetBookingRequest payload);

    @POST("api/AST/PostchecklistSummary")
    Call<MethodReturn<AssetPrePostCheckListSummaryResponse>> getPostCheckListSummary(@Body PrePostCheckListSummary payload);

    @POST("api/AST/PrechecklistSummary")
    Call<MethodReturn<AssetPrePostCheckListSummaryResponse>> getPreCheckListSummary(@Body PrePostCheckListSummary payload);

    @POST("api/AST/GetAssetDurationDistancePrice")
    Call<MethodReturn<AssetDistanceAndDurationRateResponse>> getAssetDurationDistancePrice(@Body GetAssetDurationDistancePrice payload);
    // endregion

    // region UMX
    @POST("api/UMX/AuthenticateUser")
    Call<MethodReturn<AuthenticateUserResponse>> authenticateUser(@Body AuthenticateUser payload);

    @POST("api/UMX/VerifyBusinessContacts")
    Call<MethodReturn> verifyBusinessContacts(@Body VerifyBusinessContacts payload);

    @POST("api/UMX/AcceptPrivacyPolicy")
    Call<MethodReturn> acceptPrivacyPolicy(@Body AcceptPrivacyPolicy payload);

    @POST("api/UMX/InsertUserPolicyDetails")
    Call<MethodReturn> insertUSerPolicyDetails(@Body InsertUserPolicyDetails payload);

    @POST("api/UMX/UpdateLogoutTime")
    Call<MethodReturn> updateLogoutTime(@Body LogoutUserDetails payload);
    // endregion

    //region Orange Wallet
    @Headers({"content-type: application/xml", "Accept: application/xml"})
    @POST("/payment")
    Call<ResponseBody> doPaymentWithOrangeWallet(@Body OrangeWalletPayment user);
    //endregion

    //region APP
    @POST("api/APP/InsertPrintRequest")
    Call<MethodReturn> insertPrintRequest(@Body InsertPrintRequest payload);

    @POST("api/APP/DeleteEntityDocuments")
    Call<MethodReturn> deleteDocuments(@Body DeleteDocument payload);

    @POST("api/APP/DeleteEntityNotes")
    Call<MethodReturn> deleteNotes(@Body DeleteNote payload);

    @POST("api/APP/GetQrNoteAndLogo")
    Call<MethodReturn<List<OrgData>>> getQrNoteAndLogo(@Body GetQrNoteAndLogoPayload payload);
    //endregion

    // region GenericServices
    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<BusinessOwnerResponse>> getBusinessOwners(@Body GetBusinessOwners payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<ChildTabCountResponse>> getChildTabCount(@Body GetChildTabCount payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<BusinessAddressResponse>> getBusinessAddress(@Body GetBusinessOwners payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<CashDepositResponse>> getCashDepositHistory(@Body GetAgents payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<GetTaskList>> getTaskORIncidentList(@Body GetTaskORIncidentRequest payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<GetAgentList>> getAgentsBySearch(@Body GetAgents payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<PenaltyResponse>> getPenalityList(@Body GetPenalityList payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<GetSpecificationValueSetResult>> getDynamicValues(@Body GetDynamicValuesDropDown payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<GenericServiceResponse>> getTableOrViewData(@Body GenericServicePayload payload);

    @POST("api/GenericServices/DownloadFileFromAWS")
    Call<MethodReturn> downloadfilePathFromAWS(@Body DownloadFileFromAWS payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<GetSpecificationValueBusinessChildSet>> getDynamicValuesBusinessChildList(@Body GetDynamicValuesDropDown payload);

    // endregion

    // region Receipts
    @POST("api/SAL/GenerateCustomerAllTaxes")
    Call<MethodReturn<List<GenerateTaxNoticeResponse>>> generateCustomerAllTaxes(@Body GenerateCustomerAllTaxes payload);

    @POST("api/SAL/GenerateCustomerTaxInvoices")
    Call<MethodReturn<GenerateTaxNoticeResponse>> generateCustomerTaxNotice(@Body GenerateCustomerTaxNotice payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<ROPTaxNoticeResponse>> getROPTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<PDOTaxNoticeResponse>> getPDOTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<CMETaxNoticeResponse>> getCMETaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<CPTaxNoticeResponse>> getCPTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<SalesTaxNoticeResponse>> getSalesTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<RoadTaxNoticeResponse>> getRoadTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<TaxReceiptsResponse>> getTaxReceiptPrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/PayPointLicensePaymentPrinting")
    Call<MethodReturn<LicenseRenewalReceiptResponse>> getPayPointLicenseRenewalReceipt(@Body PayPointLicensePaymentPrinting payload);

    @POST("/api/SAL/AgentWalletRechargePaymentDetails")
    Call<MethodReturn<AgentRechargeReceiptResponse>> getAgentRechargeReceipt(@Body AgentWalletRechargePaymentDetails payload);

    @POST("/api/SAL/PenaltyWaveOffReceiptDetails")
    Call<MethodReturn<PenaltyWaiveOffReceiptResponse>> getPenaltyReceiptDetils(@Body PenaltyWaiveOffReceiptDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<AdvertisementTaxNoticeResponse>> getAdvertisementTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/OutstandingWaveOffReceipt")
    Call<MethodReturn<InitialOutstandingWaiveOffReceiptResponse>> getInitialOutStandingReceiptDetails(@Body OutstandingWaveOffReceipt payload);

    @POST("/api/AST/AssetBookingRequestDetails")
    Call<MethodReturn<BookingRequestReceiptResponse>> getAssetBookingRequestDetails(@Body AssetBookingRequestDetails payload);

    @POST("/api/AST/AssetBookingAdvancePayment")
    Call<MethodReturn<BookingAdvanceReceiptResponse>> getBookingAdvanceReceiptDetails(@Body AssetBookingAdvancePayment payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<CartTaxNoticeResponse>> getCartTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<WeaponTaxNoticeResponse>> getWeaponTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<GamingMachineTaxNoticeResponse>> getGamingMachineTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);


    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<CartTaxReceiptResponse>> getCartTaxReceiptPrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("api/AST/AssetRentReceiptDetails")
    Call<MethodReturn<AssetRentAndReturnReceiptResponse>> getAssetRentAndReturnReceiptDetails(@Body AssetRentReceiptDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<TicketIssueReceiptResponse>> getTicketIssueReceipt(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<ImpoundmentReceiptResponse>> getImpoundmentReceipt(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<TicketPaymentReceiptResponse>> getTrafficTicketReceiptPrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<ParkingTicketReceiptResponse>> getParkingTicketReceiptDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<ParkingTicketPaymentReceiptResponse>> getParkingTicketPaymentReceiptDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/GetTaxSubTypesByTaxRuleBookCode")
    Call<MethodReturn<TaxSubTypeListResponse>> getTaxSubTypesByTaxRuleBookCode(@Body GetTaxSubTypesByTaxRuleBookCode payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<PropertyLandTaxNoticeResponse>> getPropertyLandTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<HotelTaxNoticeResponse>> getHotelTaxNoticeDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<ShowTaxNoticeResponse>> getShowTaxNoticeDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<LicenseRenewakNoticeResponse>> getLicenseRenewalTaxNoticeDetails(@Body TaxNoticePrintingDetails payload);
    @POST("api/SAL/GenerateSalesTaxAndPayment")
    Call<MethodReturn<Integer>> generateSalesTaxAndPayment(@Body GenerateSalesTaxAndPaymentPayload payload);
    // endregion

    //region LAW
    @POST("api/LAW/GetVehiclesDetailsWithCurrentOwner")
    Call<MethodReturn<VehicleDetailsWithOwnerResponse>> getVehicleDetailsWithOwner(@Body GetVehiclesDetailsWithCurrentOwner payload);

    @POST("api/LAW/GetBussinessDetailsWithOwners")
    Call<MethodReturn<BusinessResponse>> getBusinessList(@Body GetBusiness payload);

    @POST("api/LAW/GetEstimatedFineAmount")
    Call<MethodReturn> getEstimatedAmount(@Body GetEstimatedFineAmount payload);

    @POST("api/LAW/StoreImpoundments")
    Call<MethodReturn<ImpoundmentResponse>> storeImpoundment(@Body InsertImpoundment payload);

    @POST("api/LAW/InsertViolationTicket")
    Call<MethodReturn<ViolationTicketResponse>> insertViolationTicket(@Body InsertViolationTicket payload);

    @POST("api/LAW/GetTicketsIssuedToVehicles")
    Call<MethodReturn<VehicleTicketHistoryResponse>> getVehicleTicketHistoryDetails(@Body GetTicketsIssuedToVehicles payload);

    @POST("api/LAW/GetTicketsIssuedToDriver")
    Call<MethodReturn<DriverTicketHistory>> getTicketsIssuedToDriver(@Body GetTicketsIssuedToDriver payload);

    @POST("api/LAW/GetImpImpondmentReturnList/")
    Call<MethodReturn<GetImpondmentReturnResponse>> getImpondmentReturnList(@Body GetImpondmentReturn payload);

    @POST("api/LAW/GetPenaltyTransactions/")
    Call<MethodReturn<GetLawPenaltyTransactionsResponse>> getLawPenaltyTransactions(@Body GetPenaltyTransactions payload);

    @POST("api/LAW/GetLAWTaxTransactionsList/")
    Call<MethodReturn<GetLAWTaxTransactionsList>> getImpondmentReturnHistory(@Body GetImpondmentReturnHistory payload);

    @POST("/api/LAW/GetImpoundmentSummary")
    Call<MethodReturn<ImpoundmentReturnReceiptResponse>> getImpoundmentReturnReceipt(@Body GetImpoundmentSummary payload);

    @POST("api/LAW/GetImpondmentDetails/")
    Call<MethodReturn<GetImpondmentDetailsResponse>> getImpondmentDetails(@Body GetImpondmentDetails payload);

    @POST("api/LAW/ImpoundmentReturn/")
    Call<MethodReturn<ArrayList<ImpoundmentReturnResponse>>> insertImpondmentDetails(@Body InsertImpondmentDetails payload);

    @POST("api/LAW/GetTaxInvoicesDetailsByNoticeRefNo/")
    Call<MethodReturn<TrackOnTaxNotice>> getTaxInvoicesDetailsByNoticeReferenceNo(@Body GetTaxInvoicesDetailsByNoticeReferenceNo payload);

    @POST("api/LAW/SearchVehicleDetailsBySycotax")
    Call<MethodReturn<VehicleDetailsResponse>> getVehicleDetailsFromSycoTaxId(@Body SearchVehicleDetailsBySycotax payload);

    @POST("api/LAW/SearchVehicleSummary")
    Call<MethodReturn<SearchVehicleResultResponse>> getVehicleSummary(@Body SearchVehicleDetails payload);

    @POST("api/LAW/GetVehicleSummaryBySycotax")
    Call<MethodReturn<SearchVehicleResultResponse>> getVehicleSummaryBySycotax(@Body SearchVehicleDetailsBySycotax payload);

    @POST("api/LAW/GetPendingViolationImpondmentList")
    Call<MethodReturn<ImpoundmentList>> getPendingViolationImpoundmentList(@Body GetPendingViolationImpoundmentList payload);

    @POST("api/LAW/GetPendingViolationImpondmentList")
    Call<MethodReturn<PendingViolationImpoundment>> getPendingViolation(@Body GetPendingViolationImpoundmentList payload);

    @POST("api/LAW/GetPendingTransaction4Agent")
    Call<MethodReturn<LAWPendingTransaction4Agent>> getPendingTransaction4Agent(@Body GetLawPendingTransactionLocation4Agent payload);

    @POST("api/LAW/GetViolationTicketsByViolationTicketID")
    Call<MethodReturn<ViolationTickets>> getViolationTicketsByViolationTicketID(@Body GetViolationTicketsByViolationTicketID payload);

    @POST("api/LAW/InsertMultipleViolationTicket")
    Call<MethodReturn<List<ViolationTicketResponse>>> insertMultipleViolations(@Body InsertMultipleViolationTicket payload);

    @POST("api/LAW/GetUnassignedVehiclesDetails")
    Call<MethodReturn<VehicleDetailsSearchOwnerResponse>> getUnassignedVehiclesDetails(@Body GetVehiclesDetailsWithCurrentOwner payload);


    @POST("api/LAW/GetEstimatedImpoundAmount")
    Call<MethodReturn<EstimatedImpoundAmountResponse>> getEstimatedImpoundAmount(@Body GetEstimatedImpoundAmount payload);

    @POST("/api/LAW/StoreMultipleImpoundmentTicket")
    Call<MethodReturn<ArrayList<ImpoundmentResponse>>> getStoreMultipleImpoundmentTicket(@Body StoreMultipleImpoundmentTicketPayload payload);
     //endregion

    //region PRK
    @POST("api/PRK/GetAgentParkingPlacesList")
    Call<MethodReturn<AgentParkingPlaces>> getAgentParkingPlaces(@Body GetAgentParkingPlaces payload);

    @POST("api/PRK/GetParkingTicketsByVehicleNo")
    Call<MethodReturn<ParkingTicketDetailsResponse>> getParkingDetails(@Body GetParkingTicketsByVehicleNo payload);

    @POST("api/PRK/GetPaymentPeriodForParking")
    Call<MethodReturn<GetPaymentPeriod>> getPaymentPeriodForParking(@Body GetPaymentPeriodForParking payload);

    @POST("api/PRK/InsertParkingTicket")
    Call<MethodReturn<ParkingTicketResponse>> insertParkingTicket(@Body InsertParkingTicket payload);

    @POST("api/PRK/GetTicketsForCancellation")
    Call<MethodReturn<ParkingTicketHistoryResponse>> getTicketsForCancellation(@Body GetTicketsForCancellation payload);

    //for parking ticket payment wallet
    @POST("api/FRM/PaymentByWallet4PRKTaxInvoices/")
    Call<MethodReturn> paymentByWallet4ParkingTicketPayment(@Body PaymentByWallet4TicketPayment payload);

    @POST("api/PRK/GetParkingTaxTransactionsList/")
    Call<MethodReturn<GetParkingTaxTransactionResponse>> getParkingPaymentList(@Body GetParkingTaxTransactionsList payload);


    @POST("api/PRK/GetParkingPenaltyTransactionsList/")
    Call<MethodReturn<GetParkingPenaltyTransactionsResponse>> getParkingPenaltyTransactionsList(@Body GetParkingPenaltyTransactionsList payload);

    @POST("api/PRK/GetLastParkingTicketOverStayCharge4Vehicle")
    Call<MethodReturn<LastParkingAndOverStayChargeResponse>> getLastParkingAndOverstayDetails(@Body GetLastParkingTicketOverStayCharge4Vehicle payload);

    @POST("api/PRK/StoreAndPayParkingTicket")
    Call<MethodReturn<StoreAndPayParkingTicketResponse>> storeAndPayParkingTicket(@Body StoreAndPayParkingTicket payload);

    @POST("api/PRK/StoreVehicleParkingInOuts")
    Call<MethodReturn> storeParkingInOUts(@Body StoreVehicleParkingInOuts payload);


    //endregion

    //region PRO
    @POST("api/PRO/IsPropertySycotaxAvailable")
    Call<MethodReturn> isPropertySycoTaxAvailable(@Body IsPropertySycoTaxAvailable payload);

    @POST("api/PRO/SearchPropertyDetailsBySycotax")
    Call<MethodReturn<PropertyDetailsBySycoTax>> searchPropertyDetailsBySycoTax(@Body GenericGetDetailsBySycotax payload);

    @POST("api/PRO/GetPropertyComfortLevels")
    Call<MethodReturn<PropertyComfortLevels>> getPropertyComfortLevels(@Body GetPropertyComfortLevels payload);

    @POST("api/PRO/StorePropertyImages")
    Call<MethodReturn> insertPropertyImage(@Body StorePropertyImage payload);

    @POST("api/PRO/GetPropertyImages")
    Call<MethodReturn<PropertyImageResponse>> getPropertyImages(@Body GetPropertyImages payload);

    @POST("api/PRO/DeletePropertyImage")
    Call<MethodReturn> deletePropertyImage(@Body DeletePropertyImage payload);

    @POST("api/PRO/GetPropertyOwners")
    Call<MethodReturn<PropertyOwnerResponse>> getPropertyOwners(@Body GetPropertyOwner payload);

    @POST("api/PRO/GetPropertyPlans")
    Call<MethodReturn<PropertyPlanImageResponse>> getPropertyPlans(@Body GetPropertyPlans payload);

    @POST("api/PRO/InsertPropertyPlans")
    Call<MethodReturn> insertPropertyPlans(@Body InsertPropertyPlans payload);

    @POST("api/PRO/DeletePropertyPlan")
    Call<MethodReturn> deletePropertyPlanImage(@Body DeletePropertyPlan payload);

    @POST("api/CRM/GetPropertyTaxDueYearSummary")
    Call<MethodReturn<PropertyDueSummaryResponse>> getPropertyTaxDueYearSummary(@Body GetPropertyTaxDueYearSummary payload);

    @POST("api/PRO/GetPropetyDetails")
    Call<MethodReturn<PropertyTaxResponse>> getPropertyDetails(@Body GetPropertyDetails payload);

    @POST("api/CRM/GetPropertyDueSummary")
    Call<MethodReturn<PropertyDueResponse>> getPropertyDueSummary(@Body GetPropertyDueSummary payload);

    @POST("api/CRM/GetPropertyTaxNoticeHistory")
    Call<MethodReturn<PropertyTaxNoticeResponse>> getPropertyTaxNoticeHistory(@Body GetPropertyTaxNoticeHistory payload);

    @POST(" api/PRO/GetPendingPropertyVerificationReq")
    Call<MethodReturn<PropertyPendingVerificationResponse>> getPropertyPendingVerificationList(@Body GetPendingPropertyVerificationRequests payload);

    @POST("api/PRO/ApprovePropertyVerificationReq")
    Call<MethodReturn> approvePropertyVerification(@Body ApproveRejectPropertyVerificationReq payload);

    @POST("api/PRO/RejectPropertyVerificationReq")
    Call<MethodReturn> rejectProperty(@Body ApproveRejectPropertyVerificationReq payload);

    @POST("api/PRO/StoreProperty")
    Call<MethodReturn<String>> storePropertyData(@Body StorePropertyPayload payload);

    @POST("api/PRO/StorePropertyOwnershipWithPropertyOwner")
    Call<MethodReturn<Integer>> storePropertyOwnershipWithPropertyOwner(@Body StorePropertyOwnershipWithPropertyOwnerPayload payload);

    @POST("api/PRO/GetPropertyOwnershipWithOwners")
    Call<MethodReturn<PropertyOwners>> getPropertyOwnersDetails(@Body GetPropertyOwner payload);

    @POST("api/SAL/GetEstimatedTax4Property")
    Call<MethodReturn<Double>> getEstimatedTax4Property(@Body GetEstimatedTax4PropPayload payload);

    @POST("api/PRO/GetPropertyTaxTransactions")
    Call<MethodReturn<TransactionHistoryGenResp>> getPropertyTaxTransactions(@Body TransactionHistoryGenPayload payload);

    @POST("api/PRO/GetPropertyTax4Business")
    Call<MethodReturn<PropertyTaxDetailsList>> getPropertyTax4Business(@Body GetPropertyTax4Business payload);

    @POST("api/PRO/GetPropertyTaxCount4Business")
    Call<MethodReturn<GetIndividualTaxCount>> getPropertyTaxCount4Business(@Body GetIndividualTaxCount4Business payload);

    @POST("api/PRO/GetChildPropertyCount4Property")
    Call<MethodReturn> getChildPropertyCount4Property(@Body GetChildPropertyCount4Property payload);
    @POST("/api/PRO/GetTaxes4InitialOutstanding")
    Call<MethodReturn<List<ProductTypes>>> getTaxes4InitialOutstanding(@Body GetTaxes4InitialOutstanding payload);

    @POST("api/PRO/GetPropertyTaxList4Business")
    Call<MethodReturn<PropertyLandTaxDetailsList>> getPropertyTaxList4Business(@Body GetPropertyTax4Business payload);

    //endregion

    @GET("?method=txtly.create")
    Call<ShortUrl> getShortURL(@Query("api_key") String key, @Query("url") String longURL);

    @POST("api/CRM/DeleteWeapons")
    Call<MethodReturn> deleteWeapon(@Body DeleteWeapons payload);

    @POST("api/CRM/DeleteCarts")
    Call<MethodReturn> deleteCarts(@Body DeleteCart payload);

    @POST("api/CRM/DeleteGamingMachines")
    Call<MethodReturn> deleteGamingMachines(@Body DeleteGamingMachine payload);

    @POST("/api/LAW/SearchVehicleDetails")
    Call<MethodReturn<SearchVehicleResultResponse>> getSearchVehicleDetails(@Body SearchVehicleDetails payload);

    @POST("api/LAW/InsertVehicles/")
    Call<MethodReturn> insertOnBoardVehicleOwnership(@Body GetInsertVehicleOnBoarding payload);

    @POST("api/LAW/EditVehicles/")
    Call<MethodReturn> updateOnBoardVehicleOwnership(@Body GetInsertVehicleOnBoarding payload);


    @POST("api/LAW/InsertVehicleOwnership/")
    Call<MethodReturn> insertVehicleOwnership(@Body GetInsertVehicleOwnership payload);

    @POST("api/LAW/GetVehicleOwnershipDetls")
    Call<MethodReturn<VehicleOwnershipDetailsResult>> getVehicleOwnershipDetails(@Body GetVehicleOwnershipDetails payload);


    @POST("api/LAW/DeleteVehicleOwnership")
    Call<MethodReturn> deleteVehicleOwnership(@Body VehicleOwnershipDeletePayload payload);

    @POST("api/PRK/GetRandomVehicleSycotaxIDList")
    Call<MethodReturn<VehicleSycotaxListResponse>> getRandomVehicleSycotaxIDList(@Body GetUnusedSycoTaxId payload);


    @POST("api/PRK/InsertParkingTicket")
    Call<MethodReturn<NewTicketCreationResponse>> newTicketCreation(@Body NewTicketCreation payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<ROPTaxNoticeResponse>> getVehicleTaxNoticePrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/SAL/TaxNoticePrintingDetails")
    Call<MethodReturn<PropertyTaxReceiptResponse>> getTransactionReceiptPrintingDetails(@Body TaxNoticePrintingDetails payload);

    @POST("/api/PRO/GetPropertyLocations")
    Call<MethodReturn<PropertyLocations>> getPropertyLocations(@Body GetComplaintLocations payload);

    @POST("/api/PRO/GetPropertyTree")
    Call<MethodReturn<PropertyTreeData>> getPropertyTree(@Body PropertyTreePayload payload);


    @POST("/api/SAL/GetEstimatedRentAmount")
    Call<MethodReturn<Double>> getEstimatedRentAmount(@Body GetEstimatedRentAmountPayload payload);

    @POST("/api/UMX/UpdateUser2FADetails")
    Call<MethodReturn> storeAuthSecretKey(@Body Auth2FA payload);

    @POST("/api/Common/GetMessageConnectionString")
    Call<MethodReturn<String>> getMessageConnectionString(@Body GetMessageConnectionPayload payload);

    @POST("api/CRM/GetCitizenForMobileNumber")
    Call<MethodReturn<CitizenDataForMobileNumber>> getCitizenForMobileNumbe(@Body GetCitizenForMobileNumberPayload payload);

    @POST("api/CRM/getSellableProductForAgent")
    Call<MethodReturn<List<SalesProductData>>> getProductsForAgent(@Body GetProductByType payload);

    @POST("api/CRM/GetInventoryProductsDetails")
    Call<MethodReturn<StockTransferProductsResponse>> getProductsForStockTransfer(@Body AccountsPayload payload);

    @POST("api/CRM/GetSalesHistoryDetails")
    Call<MethodReturn<SalesListReturn>> getSalesHistoryDetailsList(@Body SalesListDetails payload);

    @POST("api/INV/StoreAdjustment")
    Call<MethodReturn<Boolean>> storeAdjustment(@Body StoreAdjustmentsPayload payload);

    @POST("api/GenericServices/GetTableOrViewDataDynamic")
    Call<MethodReturn<GetAdjustmentTypeResponse>> getAdjustmentTypes(@Body GetDynamicValuesDropDown payload);

    @POST("api/CRM/GetAdministrationOffices")
    Call<MethodReturn<GetAdministrationOfficesResponse>> getAdministrationOffices(@Body GetAdminOfficeAddressPayload payload);

    @POST("api/INV/GetInventoryAccounts")
    Call<MethodReturn<AccountsResponse>> getToandFromAccounts(@Body AccountsPayload payload);

    @POST("api/INV/StoreStockAllocation")
    Call<MethodReturn<Boolean>> storeStockTransfer(@Body StoreStockTransferPayload payload);

    @POST("api/CRM/GetInventoryProductsDetails")
    Call<MethodReturn<InventoryProductsDetailsResponse>> getInventoryProductsDetails(@Body GetInventoryProductsDetailsPayload payload);

    @POST("api/GenericServices/UploadLogFile")
    Call<MethodReturn> uploadLogFile(@Body UploadLogFilePayload payload);

    @POST("api/CRM/GetSalesRepaymentDetails")
    Call<MethodReturn<SalesRepaymentResponse>> getSalesRepaymentDetails(@Body SalesListDetails payload);

    @POST("api/INV/GetGroupingSalesReport")
    Call<MethodReturn<GetGroupingSalesReportResponse>> getGroupingSalesReport(@Body GetGroupingSalesReportPayload payload);
}

