package com.sgs.citytax.api;

import static com.sgs.citytax.util.GlobalKt.checkRemarks;
import static com.sgs.citytax.util.GlobalKt.checkVerified;
import static com.sgs.citytax.util.GlobalKt.getString;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sgs.citytax.BuildConfig;
import com.sgs.citytax.R;
import com.sgs.citytax.api.APIHelper;
import com.sgs.citytax.api.APIRepository;
import com.sgs.citytax.api.payload.*;
import com.sgs.citytax.api.response.*;
import com.sgs.citytax.base.MyApplication;
import com.sgs.citytax.model.ASTAssetRentPreCheckLists;
import com.sgs.citytax.model.ASTAssetRents;
import com.sgs.citytax.model.Account;
import com.sgs.citytax.model.AgentParkingPlace;
import com.sgs.citytax.model.AppReceiptPrint;
import com.sgs.citytax.model.AvailableDatesForAssetBooking;
import com.sgs.citytax.model.BusinessOwnership;
import com.sgs.citytax.model.COMDocumentReference;
import com.sgs.citytax.model.COMNotes;
import com.sgs.citytax.model.COMPropertyImage;
import com.sgs.citytax.model.COMPropertyPlanImage;
import com.sgs.citytax.model.CRMAccountEmails;
import com.sgs.citytax.model.CRMAdvertisements;
import com.sgs.citytax.model.CRMAgentSummaryDetails;
import com.sgs.citytax.model.CRMAgents;
import com.sgs.citytax.model.CRMPropertyRent;
import com.sgs.citytax.model.CartTax;
import com.sgs.citytax.model.CommissionDetails;
import com.sgs.citytax.model.DataTaxableMatter;
import com.sgs.citytax.model.GamingMachineTax;
import com.sgs.citytax.model.GeoAddress;
import com.sgs.citytax.model.GetPropertyEstimatedTax4PropPayload;
import com.sgs.citytax.model.HotelPayloadData;
import com.sgs.citytax.model.LicenseEstimatedTax;
import com.sgs.citytax.model.LicensePayloadData;
import com.sgs.citytax.model.NewTicketCreationResponse;
import com.sgs.citytax.model.ObjectHolder;
import com.sgs.citytax.model.ParkingInOutsData;
import com.sgs.citytax.model.ParkingTicket;
import com.sgs.citytax.model.ParkingTicketPayloadData;
import com.sgs.citytax.model.Payment;
import com.sgs.citytax.model.ProEstimatedTax;
import com.sgs.citytax.model.ProPropertyEstimatedTax;
import com.sgs.citytax.model.ProductDetails;
import com.sgs.citytax.model.PropertyVerificationRequestData;
import com.sgs.citytax.model.ROPDetails;
import com.sgs.citytax.model.SAL_TaxDetails;
import com.sgs.citytax.model.ShowTaxData;
import com.sgs.citytax.model.TaxNoticeDetail;
import com.sgs.citytax.model.TaxPayerDetails;
import com.sgs.citytax.model.TaxPayerListResponse;
import com.sgs.citytax.model.TicketHistory;
import com.sgs.citytax.model.UMXUserPolicyDetails;
import com.sgs.citytax.model.VehicleSycotaxListResponse;
import com.sgs.citytax.model.VehicleTicketData;
import com.sgs.citytax.model.ViolationDetail;
import com.sgs.citytax.model.ViolationSignature;
import com.sgs.citytax.model.VuTax;
import com.sgs.citytax.model.Weapon;
import com.sgs.citytax.util.Constant;
import com.sgs.citytax.util.DateHelperKt;
import com.sgs.citytax.util.EncryptionHelperKt;
import com.sgs.citytax.util.NetworkHelperKt;
import com.sgs.citytax.util.PrefHelper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APICall {

    private static APIRepository getAPIRepository() {
        return APIHelper.getInstance().create(APIRepository.class);
    }

    private static <T> void callAPI(Call<T> call, final ConnectionCallBack<MethodReturn> callBack) {

        if (!NetworkHelperKt.isOnline()) {
            callBack.onFailure(MyApplication.getContext().getResources().getString(R.string.msg_no_internet));
            return;
        }

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
//                try {
                    if (response.code() == 200 && response.body() != null) {
                        if (response.body() instanceof MethodReturn) {
                            MethodReturn methodReturn = (MethodReturn) response.body();
                            if (methodReturn.getToken() != null && !methodReturn.getToken().isEmpty()) {
                                String decryptedDynamicToken = EncryptionHelperKt.doDecrypt(methodReturn.getToken(), MyApplication.getPrefHelper().getStaticToken());
                                MyApplication.getPrefHelper().setDynamicToken(decryptedDynamicToken);
                                call.clone().enqueue(this);
                                //callAPI(call, callBack);
                            } else if (methodReturn.isSuccess()) {
                                callBack.onSuccess(methodReturn);
                            } else {
//                                LogHelper.writeLog(null, methodReturn.getMsg());
                                callBack.onFailure(methodReturn.getMsg() + "");
                            }
                        } else callBack.onFailure("Response type incompatible");
                    } else if (response.errorBody() != null) {
                        callBack.onFailure("Internal server error");
                    } else {
                        callBack.onFailure("Please try again.");
                    }
//                } catch (Error e) {
////                    LogHelper.writeLog(e, null);
//                    callBack.onFailure(e.getMessage() + "");
//                }
            }

            @Override
            public void onFailure(@NotNull Call<T> call, @NotNull Throwable t) {
//                LogHelper.writeLog(new Exception(t), null);
                callBack.onFailure(t.getMessage() + "");
            }
        });
    }

    // region Common
    public static void updateConnectedDevice(LatLng latLng, ConnectionCallBack<Boolean> callBack) {
        APIRepository apiRepository = APIHelper.getInstanceForStaticCall().create(APIRepository.class);

        ConnectedDevice connectedDevice = new ConnectedDevice();
        connectedDevice.setDeviceCode(MyApplication.getPrefHelper().getSerialNumber());
        connectedDevice.setDeviceID(MyApplication.getPrefHelper().getSerialNumber());
        connectedDevice.setDeviceName(MyApplication.getPrefHelper().getDeviceName());
        connectedDevice.setDeviceType("A");
        connectedDevice.setLastPingTime(DateHelperKt.formatDateTimeInMillisecond(new Date()));
        connectedDevice.setUserOrgBranchID(MyApplication.getPrefHelper().getUserOrgBranchID());
//        connectedDevice.setVersion("0.0");
        if (latLng != null) {
            connectedDevice.setLatitude(latLng.latitude);
            connectedDevice.setLongitude(latLng.longitude);
        }

        List<ConnectedDevice> deviceList = new ArrayList<>();
        deviceList.add(connectedDevice);
        UpdateConnectedDevice updateConnectedDevice = new UpdateConnectedDevice();
        updateConnectedDevice.setConnectedDevice(deviceList);

        callAPI(apiRepository.updateConnectedDevice(updateConnectedDevice), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    // endregion

    // region CRM
    public static void updateEntityDocuments(UpdateEntityDocument updateEntityDocument, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().updateEntityDocuments(updateEntityDocument), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Boolean) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxPayerDetails(String filterString, ConnectionCallBack<TaxPayerDetails> callBack) {
        GetTaxPayerDetails taxPayerDetails = new GetTaxPayerDetails();
        taxPayerDetails.setFilterString(filterString);

        callAPI(getAPIRepository().getTaxPayerDetails(taxPayerDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((TaxPayerDetails) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeCustomerB2B(StoreCustomerB2B customer, ConnectionCallBack<TaxPayerResponse> callBack) {
        callAPI(getAPIRepository().storeCustomerB2B(customer), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((TaxPayerResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getCorporateOfficeLOVValues(String tableName, ConnectionCallBack<DataResponse> callBack) {
        GetCorporateOfficeLOVValues getCorporateOfficeLOVValues = new GetCorporateOfficeLOVValues();
        getCorporateOfficeLOVValues.setTableName(tableName);

        callAPI(getAPIRepository().getCorporateOfficeLOVValues(getCorporateOfficeLOVValues), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertAccountPhone(AccountPhone accountPhone, ConnectionCallBack<Boolean> callBack) {
        InsertAccountPhone payload = new InsertAccountPhone();
        payload.setAccountPhone(accountPhone);
        callAPI(getAPIRepository().insertAccountPhone(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void saveCustomerProductInterests(CustomerProductInterests productInterests, ConnectionCallBack<Boolean> callBack) {
        SaveCustomerProductInterests payload = new SaveCustomerProductInterests();
        payload.setCustomerProductInterests(productInterests);
        callAPI(getAPIRepository().saveCustomerProductInterests(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getCorporateOfficeChildTabDetails(String tableName, int accountID, int organizationID, String taskCode, ConnectionCallBack<DataResponse> callBack) {
        GetCorporateOfficeChildTabDetails getCorporateOfficeChildTabDetails = new GetCorporateOfficeChildTabDetails();
        getCorporateOfficeChildTabDetails.setTableName(tableName);
        getCorporateOfficeChildTabDetails.setTaxPayerAccountID(accountID);
        getCorporateOfficeChildTabDetails.setTaxPayerOrganizationID(organizationID);
        getCorporateOfficeChildTabDetails.setTaskCode(taskCode);

        callAPI(getAPIRepository().getCorporateOfficeChildTabDetails(getCorporateOfficeChildTabDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null)
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxes(String tableName, int accountID, int organizationID, ConnectionCallBack<List<VuTax>> callBack) {
        GetCorporateOfficeChildTabDetails getCorporateOfficeChildTabDetails = new GetCorporateOfficeChildTabDetails();
        getCorporateOfficeChildTabDetails.setTableName(tableName);
        getCorporateOfficeChildTabDetails.setTaxPayerAccountID(accountID);
        getCorporateOfficeChildTabDetails.setTaxPayerOrganizationID(organizationID);

        callAPI(getAPIRepository().getTaxes(getCorporateOfficeChildTabDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    List<VuTax> typeOfTaxes = ((TaxesResponse) response.getReturnValue()).getTypeOfTaxes();
                    if (typeOfTaxes != null && typeOfTaxes.size() > 0)
                        callBack.onSuccess(typeOfTaxes);
                    else callBack.onFailure("");
                } else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getCorporateOfficeChildTabList(ArrayList<String> tableNames, int accountID, int organizationID, ConnectionCallBack<DataResponse> callBack) {
        GetCorporateOfficeChildTabList getCorporateOfficeChildTabList = new GetCorporateOfficeChildTabList();
        getCorporateOfficeChildTabList.setTableNames(tableNames);
        getCorporateOfficeChildTabList.setTaxPayerAccountID(accountID);
        getCorporateOfficeChildTabList.setTaxPayerOrganizationID(organizationID);

        callAPI(getAPIRepository().getCorporateOfficeChildTabList(getCorporateOfficeChildTabList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null)
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertAccountEmail(CRMAccountEmails accountEmails, ConnectionCallBack<Boolean> callBack) {
        InsertAccountEmail insertAccountEmail = new InsertAccountEmail();
        insertAccountEmail.setAccountEmails(accountEmails);

        callAPI(getAPIRepository().insertAccountEmail(insertAccountEmail), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(response.isSuccess());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAgentTransactionDetails(String fromDate, String toDate, String s_agent_acctid, ConnectionCallBack<List<CRMAgentTransaction>> callBack) {
        GetAgentTransactionDetails agentTransactionDetails = new GetAgentTransactionDetails(new SecurityContext(), fromDate, toDate, s_agent_acctid, null, 1, 10);
        agentTransactionDetails.setFromDate(fromDate);
        agentTransactionDetails.setToDate(toDate);
        agentTransactionDetails.setContext(new SecurityContext());
        agentTransactionDetails.setAcctid(s_agent_acctid);

        callAPI(getAPIRepository().getAgentTransactionDetails(agentTransactionDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<CRMAgentTransaction> returnValue = (List<CRMAgentTransaction>) response.getReturnValue();
                if (returnValue != null && returnValue.size() > 0) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAgentDetailsValues(int accountID, ConnectionCallBack<DataResponse> callBack) {
        GetAgentDetails getAgentDetails = new GetAgentDetails();
        getAgentDetails.setAgentAccountID(accountID);

        callAPI(getAPIRepository().getAgentDetails(getAgentDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeAgentDetails(StoreAgentDetails agentDetails, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().storeAgentDetails(agentDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static <T> void insertPDOAndROPDetails(T payload, ConnectionCallBack<Integer> callBack) {
        GetInsertPODDetails getInsertPODDetails;
        GetInsertROPDetails getInsertROPDetails;
        Call<MethodReturn> call = null;
        if (payload instanceof GetInsertPODDetails) {
            getInsertPODDetails = (GetInsertPODDetails) payload;
            call = getAPIRepository().insertPDODetails(getInsertPODDetails);
        } else if (payload instanceof GetInsertROPDetails) {
            getInsertROPDetails = (GetInsertROPDetails) payload;
            call = getAPIRepository().insertROPDetails(getInsertROPDetails);
        }
        if (call == null) {
            callBack.onFailure("Something went wrong");
        } else
            callAPI(call, new ConnectionCallBack<MethodReturn>() {
                @Override
                public void onSuccess(MethodReturn response) {
                    if (response.getReturnValue() != null) {
                        String res = (String) response.getReturnValue();
                        callBack.onSuccess(Integer.valueOf(res));
                    } else
                        callBack.onFailure("Something went wrong");
                }

                @Override
                public void onFailure(@NotNull String message) {
                    callBack.onFailure(message);
                }
            });
    }

    public static void insertVehicleOwnership(GetInsertVehicleOwnershipDetails getInsertVehicleOwnershipDetails, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().insertVehicleOwnership(getInsertVehicleOwnershipDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void saveGeoAddress(GeoAddress geoAddress, ConnectionCallBack<Integer> callBack) {
        SaveGeoAddress saveGeoAddress = new SaveGeoAddress();
        saveGeoAddress.setGeoAddress(geoAddress);

        callAPI(getAPIRepository().saveGeoAddress(saveGeoAddress), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertPropertyOwnershipDetails(InsertPropertyOwnership payload, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().insertPropertyOwnership(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void addServiceRequest(AddServiceRequest addServiceRequest, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().addServiceRequest(addServiceRequest), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess(response.isSuccess());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void newAddServiceRequest(NewAddServiceRequest addServiceRequest, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().newAddServiceRequest(addServiceRequest), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess(response.isSuccess());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void editServiceRequestUpdate(EditServiceRequest editServiceRequest, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().editServiceRequestUpdate(editServiceRequest), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess(response.isSuccess());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void insertCorporateTurnOver(InsertCorporateTurnover insertCorporateTurnover, ConnectionCallBack<CorporateTurnoverResponse> callBack) {
        callAPI(getAPIRepository().insertCorporateTurnOver(insertCorporateTurnover), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() == null) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess((CorporateTurnoverResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getDocumentDetails(String primaryKeyValue, String tableName, ConnectionCallBack<List<COMDocumentReference>> callBack) {
        GetCorporateOfficeChildTabDetails childTabDetails = new GetCorporateOfficeChildTabDetails();
        childTabDetails.setPrimaryKeyValue(Integer.valueOf(primaryKeyValue));
        childTabDetails.setTableName(tableName);

        callAPI(getAPIRepository().getDocumentDetails(childTabDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<COMDocumentReference> documents = ((DataResponse) response.getReturnValue()).getComDocumentReferences();
                if (documents != null && documents.size() >= 0)
                    callBack.onSuccess(documents);
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getVehicleDocumentDetails(String primaryKeyValue, String tableName, ConnectionCallBack<List<COMDocumentReference>> callBack) {
        GetVehicleDocumentChildTabDetails childTabDetails = new GetVehicleDocumentChildTabDetails();
        childTabDetails.setPrimaryKeyValue(primaryKeyValue);
        childTabDetails.setTableName(tableName);

        callAPI(getAPIRepository().getDocumentDetails(childTabDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<COMDocumentReference> documents = ((DataResponse) response.getReturnValue()).getComDocumentReferences();
                if (documents != null && documents.size() >= 0)
                    callBack.onSuccess(documents);
                else
                    callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getDueandAgreementDocumentDetails(String primaryKeyValue, String tableName, ConnectionCallBack<List<COMDocumentReference>> callBack) {
        GetVehicleDocumentChildTabDetails childTabDetails = new GetVehicleDocumentChildTabDetails();
        childTabDetails.setPrimaryKeyValue(primaryKeyValue);
        childTabDetails.setTableName(tableName);

        callAPI(getAPIRepository().getDocumentDetails(childTabDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<COMDocumentReference> documents = ((DataResponse) response.getReturnValue()).getComDocumentReferences();
                if (documents != null && documents.size() >= 0)
                    callBack.onSuccess(documents);
                else
                    callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getSummaryDetails(SearchFilter searchFilter, ConnectionCallBack<List<CRMAgentSummaryDetails>> callBack) {
        GetAgents getAgents = new GetAgents();
        getAgents.setSearchFilter(searchFilter);

        callAPI(getAPIRepository().getSummaryDetails(getAgents), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                AgentSummaryDetailsResponse summaryDetailsResponse = (AgentSummaryDetailsResponse) response.getReturnValue();
                if (summaryDetailsResponse != null)
                    callBack.onSuccess(summaryDetailsResponse.getAgents());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@Nullable String message) {
                callBack.onFailure(message + "");
            }
        });

    }

    public static void insertAgent(CRMAgents crmAgents, List<COMNotes> notes, List<COMDocumentReference> documents, ConnectionCallBack<Double> callBack) {
        InsertAgent insertAgent = new InsertAgent();
        insertAgent.setCrmAgents(crmAgents);
        insertAgent.setNotes(notes);
        insertAgent.setAttachments(documents);

        callAPI(getAPIRepository().insertAgent(insertAgent), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                Double id = (Double) response.getReturnValue();
                if (id == 0 && response.getMsg() != null && !TextUtils.isEmpty(response.getMsg()))
                    callBack.onFailure(response.getMsg());
                else
                    callBack.onSuccess(id);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxPayersList(String filterCondition, ConnectionCallBack<DataResponse> callBack) {
        GetTaxPayerList getTaxPayerList = new GetTaxPayerList();
        getTaxPayerList.setFilterCondition(filterCondition);

        callAPI(getAPIRepository().getTaxPayersList(getTaxPayerList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxPayersForVerification(SearchFilter searchFilter, ConnectionCallBack<DataResponse> callBack) {
        ValidateTaxPayer validateTaxPayer = new ValidateTaxPayer();
        validateTaxPayer.setSearchFilter(searchFilter);

        callAPI(getAPIRepository().getTaxPayersListForVerification(validateTaxPayer), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void insertDocument(InsertDocument insertDocument, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().insertDocument(insertDocument), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Boolean) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    public static void saveAgreement(SaveAgreement saveAgreement, ConnectionCallBack<StoreDueAgreementResponse> callBack) {
        callAPI(getAPIRepository().saveAgreement(saveAgreement), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((StoreDueAgreementResponse) response.getReturnValue());
            }
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    public static void UpdateDueNotices(Integer dueNoticeID,String fileData,String fileName,String reportingDate,String recipientName,String mobileNumber, ConnectionCallBack<Boolean> callBack) {
        DueNotice dueNotice =  new DueNotice();
        dueNotice.setDuenoticeid(dueNoticeID);
        dueNotice.setFileData(fileData);
        dueNotice.setFilenameWithExt(fileName);
        dueNotice.setReportingDateTime(reportingDate);
        dueNotice.setRecipientName(recipientName);
        dueNotice.setMobileNumber(mobileNumber);


        callAPI(getAPIRepository().UpdateDueNotices(dueNotice), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Boolean) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertNotes(InsertNotes insertNotes, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().insertNotes(insertNotes), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Boolean) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getNotesDetails(String primaryKeyValue, String tableName, ConnectionCallBack<List<COMNotes>> callBack) {
        GetNotesDetails getNotesDetails = new GetNotesDetails();
        getNotesDetails.setPrimaryKeyValue(primaryKeyValue);
        getNotesDetails.setTableName(tableName);

        callAPI(getAPIRepository().getNotesDetails(getNotesDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<COMNotes> notes = ((DataResponse) response.getReturnValue()).getNotes();
                if (notes != null && notes.size() > 0)
                    callBack.onSuccess(notes);
                else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void insertPropertyRents(CRMPropertyRent propertyRents, List<COMDocumentReference> documentReferences, ConnectionCallBack<Double> callBack) {
        InsertPropertyRents insertPropertyRents = new InsertPropertyRents();
        insertPropertyRents.setPropertyRent(propertyRents);
        insertPropertyRents.setAttachments(documentReferences);

        callAPI(getAPIRepository().insertPropertyRents(insertPropertyRents), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Double) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeCustomerB2C(StoreCustomerB2C customer, ConnectionCallBack<BusinessOwnership> callBack) {
        APIRepository apiRepository = APIHelper.getInstanceForStaticCall().create(APIRepository.class);
        callAPI(apiRepository.storeCustomerB2C(customer), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((BusinessOwnership) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getChildAgentSummary(GetChildAgentSummary childAgentSummary, ConnectionCallBack<AgentCollectionSummaryResponse> callBack) {

        callAPI(getAPIRepository().getChildAgentSummary(childAgentSummary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((AgentCollectionSummaryResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    public static void getAssetRentTypeList(GetAssetRentType getAssetRentType, ConnectionCallBack<DataResponse> callBack) {
        callAPI(getAPIRepository().getAssetRentTypeList(getAssetRentType), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBusinessTaxNoticeHistory(SearchFilter searchFilter, ConnectionCallBack<DataResponse> callBack) {
        GetBusinessTaxNoticeHistory getBusinessTaxNoticeHistory = new GetBusinessTaxNoticeHistory();
        getBusinessTaxNoticeHistory.setSearchFilter(searchFilter);

        callAPI(getAPIRepository().getBusinessTaxNoticeHistory(getBusinessTaxNoticeHistory), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getHandoverDueNotices(String mSycoTaxID, Integer pageIndex,Integer pageSize, ConnectionCallBack<DataResponse> callBack) {
        GetHandoverDueNotice getBusinessTaxNoticeHistory = new GetHandoverDueNotice();
        getBusinessTaxNoticeHistory.setFilterString(mSycoTaxID);
        getBusinessTaxNoticeHistory.setPageIndex(pageIndex);
        getBusinessTaxNoticeHistory.setPageSize(pageSize);

        callAPI(getAPIRepository().getHandoverDueNotices(getBusinessTaxNoticeHistory), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBusinessTransactionHistory(SearchFilter searchFilter, ConnectionCallBack<List<BusinessTransaction>> callBack) {
        GetBusinessTransactionHistory history = new GetBusinessTransactionHistory();
        history.setSearchFilter(searchFilter);

        callAPI(getAPIRepository().getBusinessTransactionHistory(history), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                BusinessTransactionHistory transactionHistory = (BusinessTransactionHistory) response.getReturnValue();
                if (transactionHistory != null)
                    callBack.onSuccess(transactionHistory.getTransactions());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getActivityDomains(String productCode, int businessAccountID, ConnectionCallBack<ActivityDomains> callBack) {
        GetActivityDomains getActivityDomains = new GetActivityDomains();
        getActivityDomains.setBusinessAccountID(businessAccountID);
        getActivityDomains.setProductCode(productCode);

        callAPI(getAPIRepository().getActivityDomains(getActivityDomains), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((ActivityDomains) response.getReturnValue());
                else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAgentsByAgentType(AdvanceSearchFilter searchFilter, ConnectionCallBack<List<CRMAgents>> callBack) {
        GetAgents getAgents = new GetAgents();
        getAgents.setAdvsrchFilter(searchFilter);

        callAPI(getAPIRepository().getAgentsByAgentType(getAgents), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                AgentResponse agentResponse = (AgentResponse) response.getReturnValue();
                if (agentResponse != null)
                    callBack.onSuccess(agentResponse.getAgents());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getCashDepositHistory(AdvanceSearchFilter searchFilter, ConnectionCallBack<CashDepositResponse> callBack) {
        GetAgents getAgents = new GetAgents();
        getAgents.setAdvsrchFilter(searchFilter);

        callAPI(getAPIRepository().getCashDepositHistory(getAgents), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((CashDepositResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void deleteCustomerProductInterests(int customerID, String productCode, ConnectionCallBack<Boolean> callBack) {
        DeleteCustomerProductInterests payload = new DeleteCustomerProductInterests();
        payload.setCustomerId(customerID);
        payload.setProductCode(productCode);

        callAPI(getAPIRepository().deleteCustomerProductInterests(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void updateStatusCustomerProdIntrst(int customerID, String productCode, String sts, ConnectionCallBack<Boolean> callBack) {
        UpdateStatusCustomerProdIntrst payload = new UpdateStatusCustomerProdIntrst();
        payload.setCustomerId(customerID);
        payload.setProductCode(productCode);
        payload.setSts(sts);

        callAPI(getAPIRepository().updateStatusCustomerProdIntrst(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else
                    callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBusinessLocationForAgent(int accountId,/*int pageNumber, int PageSize, BusinessMapFilterdata businessMapFilterdata,*/ConnectionCallBack<BusinessLocation4Agent> callBack) {
        GetBusinessLocation4Agent getBusinessLocation4Agent = new GetBusinessLocation4Agent();
        getBusinessLocation4Agent.setAccountId(accountId);
//        getBusinessLocation4Agent.setPageindex(pageNumber);
//        getBusinessLocation4Agent.setPagesize(PageSize);
//        if (businessMapFilterdata!=null) {
//            getBusinessLocation4Agent.setFltrdata(businessMapFilterdata);
//        }

        callAPI(getAPIRepository().getBusinessLocation4Agent(getBusinessLocation4Agent), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((BusinessLocation4Agent) response.getReturnValue());
                } else {
//                    if (businessMapFilterdata != null && pageNumber == 1) {
//                        callBack.onFailure(getString(R.string.msg_no_data));
//                    } else {
                    callBack.onFailure(response.getMsg());
//                    }
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }
    public static void deleteAccountMappingData(int accountID, String tableName, String primaryKey, ConnectionCallBack<Boolean> callBack) {
        DeleteAccountMappingData payload = new DeleteAccountMappingData();
        payload.setAccountID(accountID);
        payload.setTableName(tableName);
        payload.setVoucherNo(primaryKey);

        callAPI(getAPIRepository().deleteAccountMappingData(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getSearchTaxPayerDetails(String filterString, ConnectionCallBack<List<TaxPayerDetails>> callBack) {
        GetTaxPayerDetails taxPayerDetails = new GetTaxPayerDetails();
        taxPayerDetails.setFilterString(filterString);
        callAPI(getAPIRepository().searchTaxPayerDetails(taxPayerDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((List<TaxPayerDetails>) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getSearchTaxPayerList(GetTaxPayerDetails taxPayerDetails, ConnectionCallBack<TaxPayerListResponse> callBack) {
        callAPI(getAPIRepository().searchTaxPayerList(taxPayerDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                TaxPayerListResponse res = (TaxPayerListResponse) response.getReturnValue();
                if (res != null && res.getSearchResults() != null && res.getSearchResults().size() > 0) {

                    callBack.onSuccess((TaxPayerListResponse) response.getReturnValue());
                }
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }
    public static void getTaxPayerDetailsForVerification(GetTaxPayerDetails taxPayerDetails, ConnectionCallBack<BusinessVerificationResponse> callBack) {
        callAPI(getAPIRepository().getTaxPayerDetailsForVerification(taxPayerDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((BusinessVerificationResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }


    public static void getAgentCurrentLocations(int agentAccountID, int pageNumber, int PageSize, ConnectionCallBack<AgentCurrentLocations> callBack) {
        GetLocations getLocations = new GetLocations();
        getLocations.setAgentAccountID(agentAccountID);
        getLocations.setPageindex(pageNumber);
        getLocations.setPagesize(PageSize);

        callAPI(getAPIRepository().getAgentCurrentLocations(getLocations), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((AgentCurrentLocations) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getIncidentLocations(int agentAccountID, int pageNumber, int PageSize, ConnectionCallBack<IncidentLocations> callBack) {
        GetLocations getAgentLocations = new GetLocations();
        getAgentLocations.setAgentAccountID(agentAccountID);
        getAgentLocations.setPageindex(pageNumber);
        getAgentLocations.setPagesize(PageSize);

        callAPI(getAPIRepository().getIncidentLocations(getAgentLocations), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((IncidentLocations) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getComplaintLocations(int pageNumber, int PageSize, ConnectionCallBack<ComplaintLocations> callBack) {
        GetComplaintLocations getAgentLocations = new GetComplaintLocations();
        getAgentLocations.setPageindex(pageNumber);
        getAgentLocations.setPagesize(PageSize);
        callAPI(getAPIRepository().getComplaintLocations(getAgentLocations), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ComplaintLocations) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyLocations(int pageNumber, int PageSize, ConnectionCallBack<PropertyLocations> callBack) {
        GetComplaintLocations getAgentLocations = new GetComplaintLocations();
        getAgentLocations.setPageindex(pageNumber);
        getAgentLocations.setPagesize(PageSize);

        callAPI(getAPIRepository().getPropertyLocations(getAgentLocations), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PropertyLocations) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyTree(int id, ConnectionCallBack<PropertyTreeData> callBack) {
        PropertyTreePayload propertyTreePayload = new PropertyTreePayload();
        propertyTreePayload.setProprtyid(id);

        callAPI(getAPIRepository().getPropertyTree(propertyTreePayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PropertyTreeData) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getEstimatedRentAmount(GetEstimatedRentAmountPayload getEstimatedRentAmountPayload, ConnectionCallBack<Double> callBack) {
        callAPI(getAPIRepository().getEstimatedRentAmount(getEstimatedRentAmountPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((Double) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getCommentsAndDocuments(String serviceRequestNo, Boolean isServiceTax, ConnectionCallBack<DataResponse> callBack) {
        GetServiceRequest getServiceRequest = new GetServiceRequest();
        getServiceRequest.setContext(new SecurityContext());
        getServiceRequest.setServiceRequestNo(serviceRequestNo);
        if (isServiceTax)
            getServiceRequest.setServiceTax(true);

        callAPI(getAPIRepository().getCommentsAndDocuments(getServiceRequest), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getProductByType(ConnectionCallBack<List<SellableProduct>> callBack) {
        GetProductByType getProductByType = new GetProductByType();

        callAPI(getAPIRepository().getSellableProductForAgent(getProductByType), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<SellableProduct> sellableProductList = (List<SellableProduct>) response.getReturnValue();
                if (sellableProductList != null && sellableProductList.size() > 0)
                    callBack.onSuccess(sellableProductList);
                else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getSubscriptionDetails(String acctid, String usrid, ConnectionCallBack<List<SubscriptionResponse>> callBack) {
        GetSubscriptionDetails getSubscriptionDetails = new GetSubscriptionDetails();
        getSubscriptionDetails.setAcctid(acctid);
        getSubscriptionDetails.setUsrid(usrid);
        callAPI(getAPIRepository().getSubscriptionDetails(getSubscriptionDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<SubscriptionResponse> returnValue = (List<SubscriptionResponse>) response.getReturnValue();
                if (returnValue != null && returnValue.size() > 0) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });


    }

    public static void getSubscriptionAmount(String userID, int licenseModelID, ConnectionCallBack<GetSubscriptionAmountDetails> callBack) {
        SubscriptionDetails subscriptionResponse = new SubscriptionDetails();
        subscriptionResponse.setUserID(userID);
        subscriptionResponse.setModelID(licenseModelID);

        callAPI(getAPIRepository().getSubscriptionAmount(subscriptionResponse), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((GetSubscriptionAmountDetails) response.getReturnValue());
                else callBack.onFailure(response.getMsg() == null ? "" : response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeAndPay4SubscriptionRenewal(Payment payment, String userID, String remarks, String transactionID,String mode, SubscriptionRenewal subscriptionRenewal, ConnectionCallBack<Integer> callBack) {
        PaymentByWallet wallet = new PaymentByWallet();
        wallet.setAmount(subscriptionRenewal.getAmount() == null ? BigDecimal.ZERO : subscriptionRenewal.getAmount());
        wallet.setWalletCode(mode);
        wallet.setPaymentModeCode(mode);
        if (!transactionID.isEmpty())
            wallet.setMobiTransactionID(transactionID);

        SALWalletPaymentDetails walletPaymentDetails = new SALWalletPaymentDetails();
        if(!TextUtils.isEmpty(payment.getOtp())) {
            walletPaymentDetails.setOtp(Integer.parseInt(payment.getOtp()));
        }
        walletPaymentDetails.setMobileNo(payment.getCustomerMobileNo());
        walletPaymentDetails.setAmount(subscriptionRenewal.getAmount() == null ? BigDecimal.ZERO : subscriptionRenewal.getAmount());
        walletPaymentDetails.setOrgId(MyApplication.getPrefHelper().getUserOrgID());
        if (!transactionID.isEmpty())
            walletPaymentDetails.setTransactionId(transactionID);
        if(MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.TPA.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.PPS.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.ASA.name())
        )
            walletPaymentDetails.setAgentAccountID(MyApplication.getPrefHelper().getAccountId());

        StoreAndPaySubscriptionRenewal storeAndPaySubscriptionRenewal = new StoreAndPaySubscriptionRenewal();
        storeAndPaySubscriptionRenewal.setCustomerID(payment.getCustomerID());
        storeAndPaySubscriptionRenewal.setWallet(wallet);
        storeAndPaySubscriptionRenewal.setMakewalletpayment(walletPaymentDetails);
        storeAndPaySubscriptionRenewal.setContext(new SecurityContext());
        storeAndPaySubscriptionRenewal.setUserID(userID);
        storeAndPaySubscriptionRenewal.setSubscriptionRenewal(subscriptionRenewal);
        storeAndPaySubscriptionRenewal.setRemarks(remarks);

        callAPI(getAPIRepository().storeAndPay4SubscriptionRenewal(storeAndPaySubscriptionRenewal), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });

    }

    public static void getAgentCommissionBalance(int accountId, ConnectionCallBack<Double> callBack) {
        GetAgentCommissionBalance getAgentCommissionBalance = new GetAgentCommissionBalance();
        getAgentCommissionBalance.setAccountId(accountId);

        callAPI(getAPIRepository().getAgentCommissionBalance(getAgentCommissionBalance), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && (Double) response.getReturnValue() != 0.0)
                    callBack.onSuccess(((Double) response.getReturnValue()));
                else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getChildAgentsBySearch(String data, ConnectionCallBack<List<CRMAgents>> callBack) {
        GetChildAgents getChildAgents = new GetChildAgents();
        getChildAgents.setQuery(data);

        callAPI(getAPIRepository().getChildAgentsBySearch(getChildAgents), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((List<CRMAgents>) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getChildAgentsForVerification(String data, ConnectionCallBack<List<CRMAgents>> callBack) {
        GetChildAgents getChildAgents = new GetChildAgents();
        getChildAgents.setQuery(data);

        callAPI(getAPIRepository().getChildAgentsForVerification(getChildAgents), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((List<CRMAgents>) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertAdvertisements(CRMAdvertisements crmAdvertisements, ArrayList<COMDocumentReference> documentReferences, ConnectionCallBack<Integer> callBack) {
        InsertAdvertisements insertAdvertisements = new InsertAdvertisements();
        insertAdvertisements.setCrmAdvertisements(crmAdvertisements);
        insertAdvertisements.setComDocumentReference(documentReferences);
        callAPI(getAPIRepository().insertAdvertisements(insertAdvertisements), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getDynamicFormSpecs4Asset(int assetCategoryId, ConnectionCallBack<List<AssetSpecs>> callBack) {
        GetDynamicFormSpecs4Asset getDynamicFormSpecs4Asset = new GetDynamicFormSpecs4Asset();
        getDynamicFormSpecs4Asset.setAssetCategoryId(assetCategoryId);

        callAPI(getAPIRepository().getDynamicFormSpecs4Asset(getDynamicFormSpecs4Asset), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((List<AssetSpecs>) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxTypes(String customerId, ConnectionCallBack<List<CustomerProduct>> callBack) {
        GetTaxTypes getTaxTypes = new GetTaxTypes();
        getTaxTypes.setCustomerID(customerId);

        callAPI(getAPIRepository().getTaxTypes(getTaxTypes), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<CustomerProduct> products = (List<CustomerProduct>) response.getReturnValue();
                if (products != null && !products.isEmpty())
                    callBack.onSuccess(products);
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(getString(R.string.msg_no_data));
            }
        });
    }

    public static void getVoucherNumber(Integer customerId, String productCode, ConnectionCallBack<List<OutstandingVoucherNo>> callBack) {

        GetVoucherNo getVoucherNo = new GetVoucherNo();
        getVoucherNo.setAccountID(customerId);
        getVoucherNo.setTaxRuleBookCode(productCode);

        callAPI(getAPIRepository().getVoucherNo(getVoucherNo), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<OutstandingVoucherNo> returnValue = (List<OutstandingVoucherNo>) response.getReturnValue();
                if (returnValue != null && !returnValue.isEmpty())
                    callBack.onSuccess(returnValue);
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(getString(R.string.msg_no_data));
            }
        });
    }

    public static void getOutStandingsList(Integer accountID, String productCode, Integer voucherNo, ConnectionCallBack<ArrayList<GetOutstanding>> callBack) {
        GetOutstandingList getOutstandingList = new GetOutstandingList();
        getOutstandingList.setAccountID(accountID);
        getOutstandingList.setProdcode(productCode);
        getOutstandingList.setVoucherNo(voucherNo);

        callAPI(getAPIRepository().getInitialOutstandingList(getOutstandingList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                ArrayList<GetOutstanding> returnValue = (ArrayList<GetOutstanding>) response.getReturnValue();
                if (returnValue != null && !returnValue.isEmpty())
                    callBack.onSuccess(returnValue);
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(getString(R.string.msg_no_data));
            }
        });
    }

    public static void getOutStandingsList(Integer accountID, ConnectionCallBack<ArrayList<GetOutstanding>> callBack) {
        GetOutstandingList getOutstandingList = new GetOutstandingList();
        getOutstandingList.setAccountID(accountID);

        callAPI(getAPIRepository().getInitialOutstandingList(getOutstandingList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                ArrayList<GetOutstanding> returnValue = (ArrayList<GetOutstanding>) response.getReturnValue();
                if (returnValue != null && !returnValue.isEmpty())
                    callBack.onSuccess(returnValue);
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(getString(R.string.msg_no_data));
            }
        });
    }

    public static void deleteOutStanding(Integer initialOutstandingID, ConnectionCallBack<Boolean> callBack) {
        DeleteOutstanding outstandings = new DeleteOutstanding();
        outstandings.setInitialOutstandingID(initialOutstandingID);

        callAPI(getAPIRepository().deleteOutstanding(outstandings), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getAssetCertificateDetailsInsurance(int assetPrimaryID, String tableName, ConnectionCallBack<AssetInsuranceResponse> callBack) {
        GetAssetCertificateDetails getAssetCertificateDetails = new GetAssetCertificateDetails();
        getAssetCertificateDetails.setTableName(tableName);
        getAssetCertificateDetails.setAssetID(assetPrimaryID);

        callAPI(getAPIRepository().getAssetCertificateDetailsInsurance(getAssetCertificateDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((AssetInsuranceResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAssetCertificateDetailsFitness(int assetPrimaryID, String tableName, ConnectionCallBack<AssetFitnessesResponse> callBack) {
        GetAssetCertificateDetails getAssetCertificateDetails = new GetAssetCertificateDetails();
        getAssetCertificateDetails.setTableName(tableName);
        getAssetCertificateDetails.setAssetID(assetPrimaryID);

        callAPI(getAPIRepository().getAssetCertificateDetailsFitness(getAssetCertificateDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((AssetFitnessesResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAssetCertificateDetailsMaintenance(int assetPrimaryID, String tableName, ConnectionCallBack<AssetMaintenanceResponse> callBack) {
        GetAssetCertificateDetails getAssetCertificateDetails = new GetAssetCertificateDetails();
        getAssetCertificateDetails.setTableName(tableName);
        getAssetCertificateDetails.setAssetID(assetPrimaryID);

        callAPI(getAPIRepository().getAssetCertificateDetailsMaintenance(getAssetCertificateDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((AssetMaintenanceResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getbusinessTaxDueYearSummary(int accountID, ConnectionCallBack<DataResponse> callBack) {
        GetBusinessTaxDueYearSummary getBusinessTaxDueYearSummary = new GetBusinessTaxDueYearSummary();
        getBusinessTaxDueYearSummary.setAcountID(accountID);

        callAPI(getAPIRepository().getBusinessTaxDueYearSummary(getBusinessTaxDueYearSummary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBusinessDueSummary(int accountID, ConnectionCallBack<BusinessDueSummaryResults> callBack) {
        GetBusinessDueSummaryDetails getBusinessDueSummaryDetails = new GetBusinessDueSummaryDetails();
        getBusinessDueSummaryDetails.setAccountID(accountID);

        callAPI(getAPIRepository().getBusinessDueSummary(getBusinessDueSummaryDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((BusinessDueSummaryResults) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void isCartSycoTaxAvailable(CartSycoTax cartSycoTax, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().isCartSycoTaxAvailable(cartSycoTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (!response.isSuccess())
                    callBack.onFailure(getString(R.string.msg_no_data));
                if (response.getReturnValue() != null)
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void storeCartTax(StoreCartTax storeCartTax, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().storeCartTax(storeCartTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() < 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getIndividualTaxForCart(GetIndividualTax getIndividualTax, ConnectionCallBack<CartTax> callBack) {
        callAPI(getAPIRepository().getIndividualTaxForCart(getIndividualTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() == null)
                    callBack.onFailure(getString(R.string.msg_no_data));
                CartTax cartTax = (CartTax) response.getReturnValue();
                callBack.onSuccess(cartTax);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void isGamingSycotaxAvailable(CartSycoTax cartSycoTax, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().isGamingSycotaxAvailable(cartSycoTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeGamingMachine(StoreGamingMachinesTax storeGamingMachinesTax, ConnectionCallBack<Integer> callBack) {

        callAPI(getAPIRepository().storeGamingMachine(storeGamingMachinesTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() < 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });
    }

    public static void getIndividualGamingTax(GetIndividualTax getIndividualTax, ConnectionCallBack<GamingMachineTax> callBack) {
        callAPI(getAPIRepository().getIndividualTax(getIndividualTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((GamingMachineTax) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void checkWeaponSycotaxAvailable(String sycoTaxId, ConnectionCallBack<Integer> callBack) {
        CartSycoTax weaponSycotax = new CartSycoTax();
        weaponSycotax.setSycoTaxID(sycoTaxId);
        callAPI(getAPIRepository().checkWeaponSycotaxAvailable(weaponSycotax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (!response.isSuccess())
                    callBack.onFailure(getString(R.string.msg_no_data));
                if (response.getReturnValue() != null)
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getIndividualTaxForWeapon(GetIndividualTax getIndividualTax, ConnectionCallBack<Weapon> callBack) {
        callAPI(getAPIRepository().getIndividualTaxForWeapon(getIndividualTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() == null)
                    callBack.onFailure(getString(R.string.msg_no_data));
                Weapon weaponTax = (Weapon) response.getReturnValue();
                callBack.onSuccess(weaponTax);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeWeapons(StoreWeapon payload, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().storeWeapons(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() < 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void isNoticeGen4IndividualTax(CartSycoTax cartSycoTax, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().isNoticeGen4IndividualTax(cartSycoTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((Boolean) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getIndividualDueSummary(GetIndividualDueSummary getIndividualDueSummary, ConnectionCallBack<BusinessDueSummaryResults> callBack) {
        callAPI(getAPIRepository().getIndividualDueSummary(getIndividualDueSummary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((BusinessDueSummaryResults) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getIndividualTaxDueYearSummary(GetIndividualTaxDueYearSummary getIndividualTaxDueYearSummary, ConnectionCallBack<DataResponse> callBack) {
        callAPI(getAPIRepository().getIndividualTaxDueYearSummary(getIndividualTaxDueYearSummary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    DataResponse dataResponse = (DataResponse) response.getReturnValue();
                    if (dataResponse != null)
                        callBack.onSuccess(dataResponse);
                    else callBack.onFailure(getString(R.string.msg_no_data));
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getOrSearchIndividualTaxDetails(CartSycoTax cartSycoTax, ConnectionCallBack<GetSearchIndividualTaxDetails> callBack) {
        callAPI(getAPIRepository().getOrSearchIndividualTaxDetails(cartSycoTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((GetSearchIndividualTaxDetails) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void searchVehicleDetails(SearchVehicleDetails vehicleDetails, ConnectionCallBack<SearchVehicleResultResponse> callBack) {
        callAPI(getAPIRepository().getSearchVehicleDetails(vehicleDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((SearchVehicleResultResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getIndividualTaxNoticeHistory(GetIndividualTaxNoticeHistory getIndividualTaxNoticeHistory, ConnectionCallBack<DataResponse> callBack) {
        callAPI(getAPIRepository().getIndividualTaxNoticeHistory(getIndividualTaxNoticeHistory), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((DataResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getIncidentAndComplaintsCounts(ConnectionCallBack<IncidentAndComplaintsCountsResponse> callBack) {
        GetIncidentAndComplaintsCounts getIncidentAndComplaintsCounts = new GetIncidentAndComplaintsCounts();
        callAPI(getAPIRepository().getIncidentAndComplaintsCounts(getIncidentAndComplaintsCounts), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {

                    callBack.onSuccess((IncidentAndComplaintsCountsResponse) response.getReturnValue());

                } else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getWeaponList(Integer primaryKeyValue, String tableName, Integer acctID, ConnectionCallBack<List<Weapon>> callBack) {
        GetIndividualTax4Business getIndividualTax4Business = new GetIndividualTax4Business();
        getIndividualTax4Business.setPrimaryKeyValue(primaryKeyValue);
        getIndividualTax4Business.setTableName(tableName);
        getIndividualTax4Business.setIndtaxvchrno(acctID);

        callAPI(getAPIRepository().getWeaponList(getIndividualTax4Business), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<Weapon> weapons = ((WeaponTaxListResponse) response.getReturnValue()).getWeaponIndividualTaxDtls();
                if (weapons != null && weapons.size() > 0)
                    callBack.onSuccess(weapons);
                else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }


    public static void getCartList(Integer primaryKeyValue, String tableName, Integer acctID, ConnectionCallBack<List<CartTax>> callBack) {
        GetIndividualTax4Business getIndividualTax4Business = new GetIndividualTax4Business();
        getIndividualTax4Business.setPrimaryKeyValue(primaryKeyValue);
        getIndividualTax4Business.setTableName(tableName);
        getIndividualTax4Business.setIndtaxvchrno(acctID);

        callAPI(getAPIRepository().getCartList(getIndividualTax4Business), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<CartTax> cartTaxes = ((CartTaxListResponse) response.getReturnValue()).getCartIndividualTaxDtls();
                if (cartTaxes != null && cartTaxes.size() > 0)
                    callBack.onSuccess(cartTaxes);
                else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getGamingMachineList(Integer primaryKeyValue, String tableName, Integer acctID, ConnectionCallBack<List<GamingMachineTax>> callBack) {
        GetIndividualTax4Business getIndividualTax4Business = new GetIndividualTax4Business();
        getIndividualTax4Business.setPrimaryKeyValue(primaryKeyValue);
        getIndividualTax4Business.setTableName(tableName);
        getIndividualTax4Business.setIndtaxvchrno(acctID);

        callAPI(getAPIRepository().getGameMachineList(getIndividualTax4Business), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<GamingMachineTax> gamingMachineTaxes = ((GameMachineTaxListResponse) response.getReturnValue()).getGameMachineIndividualTaxDtls();
                if (gamingMachineTaxes != null && gamingMachineTaxes.size() > 0)
                    callBack.onSuccess(gamingMachineTaxes);
                else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }
    public static void getIndividualTaxCount4Business(Integer primaryKeyValue, ConnectionCallBack<GetIndividualTaxCount> callBack) {
        GetIndividualTaxCount4Business getIndividualTaxCount4Business = new GetIndividualTaxCount4Business();
        getIndividualTaxCount4Business.setPrimaryKeyValue(primaryKeyValue);

        callAPI(getAPIRepository().getIndividualTaxCount4Business(getIndividualTaxCount4Business), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((GetIndividualTaxCount) response.getReturnValue());
                else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }


    public static void getWeaponSummary(Integer primaryKeyValue, String tableName, Boolean status, ConnectionCallBack<WeaponTaxSummaryResponse> callBack) {
        GetIndividualTax4Business getIndividualTax4Business = new GetIndividualTax4Business();
        getIndividualTax4Business.setPrimaryKeyValue(primaryKeyValue);
        getIndividualTax4Business.setTableName(tableName);
        getIndividualTax4Business.setIsdetailsforsummary(status);

        callAPI(getAPIRepository().getWeaponSummary(getIndividualTax4Business), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                WeaponTaxSummaryResponse weapons = ((WeaponTaxSummaryResponse) response.getReturnValue());
                callBack.onSuccess(weapons);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getCartSummary(Integer primaryKeyValue, String tableName, Boolean status, ConnectionCallBack<CartTaxSummaryResponse> callBack) {
        GetIndividualTax4Business getIndividualTax4Business = new GetIndividualTax4Business();
        getIndividualTax4Business.setPrimaryKeyValue(primaryKeyValue);
        getIndividualTax4Business.setTableName(tableName);
        getIndividualTax4Business.setIsdetailsforsummary(status);

        callAPI(getAPIRepository().getCartSummary(getIndividualTax4Business), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                CartTaxSummaryResponse cart = ((CartTaxSummaryResponse) response.getReturnValue());
                callBack.onSuccess(cart);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getGammingSummary(Integer primaryKeyValue, String tableName, Boolean status, ConnectionCallBack<GammingTaxSummaryResponse> callBack) {
        GetIndividualTax4Business getIndividualTax4Business = new GetIndividualTax4Business();
        getIndividualTax4Business.setPrimaryKeyValue(primaryKeyValue);
        getIndividualTax4Business.setTableName(tableName);
        getIndividualTax4Business.setIsdetailsforsummary(status);

        callAPI(getAPIRepository().getGammingSummary(getIndividualTax4Business), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                GammingTaxSummaryResponse game = ((GammingTaxSummaryResponse) response.getReturnValue());
                callBack.onSuccess(game);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void storeShows(ShowTaxData taxData, GeoAddress address, ConnectionCallBack<Integer> callBack) {
        StoreShows shows = new StoreShows();
        shows.setTaxData(taxData);
        shows.setGeoAddress(address);

        callAPI(getAPIRepository().storeShow(shows), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getShowsDetails(Integer organisationId, Integer pageSize, Integer pageIndex, ConnectionCallBack<ShowTaxListResponse> callBack) {
        GetShowsDetails showsDetails = new GetShowsDetails();
        showsDetails.setOrganisationID(organisationId);
        showsDetails.setPageIndex(pageIndex);
        showsDetails.setPageSize(pageSize);

        callAPI(getAPIRepository().getShowDetails(showsDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ShowTaxListResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deleteShow(Integer showID, ConnectionCallBack<Boolean> callBack) {
        DeleteShow deleteShow = new DeleteShow();
        deleteShow.setShowID(showID);

        callAPI(getAPIRepository().deleteShow(deleteShow), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Boolean) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getHotelDetails(Integer organisationId, Integer pageSize, Integer pageIndex, ConnectionCallBack<HotelDetailsListResponse> callBack) {
        GetHotelDetails hotelDetails = new GetHotelDetails();
        hotelDetails.setOrganisationID(organisationId);
        hotelDetails.setPageIndex(pageIndex);
        hotelDetails.setPageSize(pageSize);

        callAPI(getAPIRepository().getHotelDetails(hotelDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((HotelDetailsListResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeHotel(HotelPayloadData payloadData, GeoAddress address, ConnectionCallBack<Integer> callBack) {
        StoreHotels hotels = new StoreHotels();
        hotels.setData(payloadData);
        hotels.setGeoAddress(address);

        callAPI(getAPIRepository().storeHotel(hotels), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deleteHotel(Integer hotelID, ConnectionCallBack<Boolean> callBack) {
        DeleteHotel deleteHotel = new DeleteHotel();
        deleteHotel.setHotelID(hotelID);

        callAPI(getAPIRepository().deleteHotel(deleteHotel), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Boolean) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getBillingDetailsForShowAndHotel(GetShowOrHotelBillingAndPricing getShowOrHotelBillingAndPricing, ConnectionCallBack<ShowAndHotelBillingAndPricingResponse> callBack) {
        callAPI(getAPIRepository().getShowOrHotelBillingResponse(getShowOrHotelBillingAndPricing), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((ShowAndHotelBillingAndPricingResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getPendingLicenses4Agent(GetPendingLicenses4Agent getPendingLicenses4Agent, ConnectionCallBack<GetPendingLicenses4AgentResponse> callBack) {
        callAPI(getAPIRepository().getPendingLicenses4Agent(getPendingLicenses4Agent), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((GetPendingLicenses4AgentResponse) response.getReturnValue());
                } else {
                    callBack.onFailure(getString(R.string.msg_no_data));
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }
    public static void scanSearchPendingLicenses(SearchPendingLicenses getPendingLicenses, ConnectionCallBack<SearchPendingLicensesResponse> callBack) {
        callAPI(getAPIRepository().scanSearchPendingLicenses(getPendingLicenses), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((SearchPendingLicensesResponse) response.getReturnValue());
                } else {
                    callBack.onFailure(getString(R.string.msg_no_data));
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }
    public static void storeLicenses(LicensePayloadData data, ConnectionCallBack<Integer> callBack) {
        StoreLicenses licenses = new StoreLicenses();
        licenses.setData(data);
        callAPI(getAPIRepository().storeLicenses(licenses), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBillingCycleDate(GetBillingCycleDates billingCycleDates, ConnectionCallBack<BillingCycleDatesResponse> callBack) {
        callAPI(getAPIRepository().getBillingCycleDates(billingCycleDates), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((BillingCycleDatesResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getLicenseDetails(Integer organisationId, Integer pageSize, Integer pageIndex, ConnectionCallBack<LicenseDetailsResponse> callBack) {
        GetLicensesDetails licensesDetails = new GetLicensesDetails();
        licensesDetails.setOrganisationID(organisationId);
        licensesDetails.setPageIndex(pageIndex);
        licensesDetails.setPageSize(pageSize);

        callAPI(getAPIRepository().getLicenseDetails(licensesDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((LicenseDetailsResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deleteLicense(Integer licenseId, ConnectionCallBack<Boolean> callBack) {
        DeleteLicenses deletLicense = new DeleteLicenses();
        deletLicense.setLicenseID(licenseId);

        callAPI(getAPIRepository().deleteLicense(deletLicense), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Boolean) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getLicenseRenewalHistory(int licenseID, ConnectionCallBack<LicenseRenewalResp> callBack) {
        LicenseRenewalPayload licenseRenewalPayload = new LicenseRenewalPayload();
        licenseRenewalPayload.setLicenseId(licenseID);
        callAPI(getAPIRepository().getPropertyTaxTransactions(licenseRenewalPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((LicenseRenewalResp) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void saveServiceTaxRequest(SaveServiceTaxRequest saveServiceTaxRequest, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().saveServiceTaxRequest(saveServiceTaxRequest), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() < 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPendingServiceList(Integer pageSize, Integer pageIndex, ConnectionCallBack<PendingServiceListResponse> callBack) {
        GetPendingServiceInvoiceList list = new GetPendingServiceInvoiceList();
        list.setPageIndex(pageIndex);
        list.setPageSize(pageSize);

        callAPI(getAPIRepository().getPendingServiceInvoiceList(list), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PendingServiceListResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getServiceTaxRequests(GetServiceTaxRequests getServiceTaxRequests, ConnectionCallBack<ServiceRequestResponse> callBack) {
        callAPI(getAPIRepository().getServiceTaxRequests(getServiceTaxRequests), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                ServiceRequestResponse serviceRequestResponse = (ServiceRequestResponse) response.getReturnValue();
                if (serviceRequestResponse != null) {
                    callBack.onSuccess(serviceRequestResponse);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    public static void getServiceRequestBookingDetails(int serviceReqNo, ConnectionCallBack<ServiceRequestBookingReceiptResponse> callBack) {
        ServiceRequestBookingDetails serviceRequestBookingDetails = new ServiceRequestBookingDetails();
        serviceRequestBookingDetails.setServiceRequestNo(serviceReqNo);
        serviceRequestBookingDetails.setReceiptCode("Service_Booking_Request");

        callAPI(getAPIRepository().getServiceRequestBookingDetails(serviceRequestBookingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ServiceRequestBookingReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getRandomCitizenSycoTaxIDs(GetRandomCitizenSycoTaxIDs getRandomCitizenSycoTaxIDs, ConnectionCallBack<CitizenSycoTaxResponse> callBack) {
        callAPI(getAPIRepository().getRandomCitizenSycoTaxIDs(getRandomCitizenSycoTaxIDs), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((CitizenSycoTaxResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getCitizenIdentityCards(GetCitizenIdentityCards getCitizenIdentityCards, ConnectionCallBack<GetCitizenIdentityCardsResponse> callBack) {
        callAPI(getAPIRepository().getCitizenIdentityCards(getCitizenIdentityCards), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    if (response.getReturnValue() != null) {
                        GetCitizenIdentityCardsResponse res = (GetCitizenIdentityCardsResponse) response.getReturnValue();
                        callBack.onSuccess(res);
                    } else callBack.onFailure(getString(R.string.msg_no_data));
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void isCitizenSycoTaxAvailable(String sycoTaxID, ConnectionCallBack<Boolean> callBack){
        IsCitizenSycotaxAvailable isCitizenSycotaxAvailable = new IsCitizenSycotaxAvailable();
        isCitizenSycotaxAvailable.setSycoTaxID(sycoTaxID);
        callAPI(getAPIRepository().isCitizenSycoTaxAvailable(isCitizenSycotaxAvailable), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeCitizenIdentityCard(StoreCitizenIdentityCard storeCitizenIdentityCard, ConnectionCallBack<CitizenIdentityCard> callBack) {
        callAPI(getAPIRepository().storeCitizenIdentityCard(storeCitizenIdentityCard), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((CitizenIdentityCard) response.getReturnValue());
                else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getParentPropertyPlanImages(Integer propertyID, Boolean needCount, ConnectionCallBack<LinkedTreeMap<String, ArrayList<String>>> callBack) {
        GetParentPropertyPlanImages getPropertyImages = new GetParentPropertyPlanImages();
        getPropertyImages.setPropertyId(propertyID);
        getPropertyImages.setNeedCount(needCount);
        callAPI(getAPIRepository().getParentPropertyPlanImages(getPropertyImages), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.isSuccess() && response.getReturnValue() != null)
                    callBack.onSuccess((LinkedTreeMap) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getParentPropertyImages(Integer propertyID, Boolean needCount, ConnectionCallBack<LinkedTreeMap<String, ArrayList<String>>> callBack) {
        GetParentPropertyPlanImages getPropertyImages = new GetParentPropertyPlanImages();
        getPropertyImages.setPropertyId(propertyID);
        getPropertyImages.setNeedCount(needCount);
        callAPI(getAPIRepository().getParentPropertyImages(getPropertyImages), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.isSuccess() && response.getReturnValue() != null)
                    callBack.onSuccess((LinkedTreeMap) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getParentPropertyDocuments(Integer propertyID, Boolean needCount, ConnectionCallBack<LinkedTreeMap<String, ArrayList<String>>> callBack) {
        GetParentPropertyPlanImages getPropertyImages = new GetParentPropertyPlanImages();
        getPropertyImages.setPropertyId(propertyID);
        getPropertyImages.setNeedCount(needCount);
        callAPI(getAPIRepository().getParentPropertyDocuments(getPropertyImages), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.isSuccess() && response.getReturnValue()!= null)
                    callBack.onSuccess((LinkedTreeMap) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getLastBillingCycleActualAmount(LastBillingCycleActualAmount lastBillingCycleActualAmount, ConnectionCallBack<Double> callBack) {
        callAPI(getAPIRepository().getLastBillingCycleActualAmount(lastBillingCycleActualAmount), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Double) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getVehicleOwnershipIDByVehicleNo(String accountId, String vehicleNo, ConnectionCallBack<BigInteger> callBack) {
        GetVehicleOwnershipIDbyVehNo getVehicleOwnershipIDbyVehNo = new GetVehicleOwnershipIDbyVehNo();
        getVehicleOwnershipIDbyVehNo.setAcctid(accountId);
        getVehicleOwnershipIDbyVehNo.setVehicleNo(vehicleNo);

        callAPI(getAPIRepository().getVehicleOwnershipIDByVehicleNo(getVehicleOwnershipIDbyVehNo), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((BigInteger) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAgentTransactionDetailsNew(GetAgentTransactionDetails agentTransactionDetails, ConnectionCallBack<CRMAgentTransactionResponse> callBack) {
        callAPI(getAPIRepository().getAgentTransactionDetailsNew(agentTransactionDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                CRMAgentTransactionResponse returnValue = (CRMAgentTransactionResponse) response.getReturnValue();
                if (returnValue != null) {
                    callBack.onSuccess(returnValue);
                } else {
                    callBack.onFailure(getString(R.string.msg_no_data));
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getDropdownFiltersForBusinessSearch(GetDropdownFiltersForBusinessSearch getDropdownFiltersForBusinessSearch, ConnectionCallBack<GetDropdownFiltersForBusinessSearchResponse> callBack) {

        callAPI(getAPIRepository().getDropdownFiltersForBusinessSearch(getDropdownFiltersForBusinessSearch), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((GetDropdownFiltersForBusinessSearchResponse) response.getReturnValue());
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getDropdownFiltersForLAWSearch(GetDropdownFiltersForBusinessSearch getDropdownFiltersForBusinessSearch, ConnectionCallBack<GetDropdownFiltersForLAWSearchResponse> callBack) {

        callAPI(getAPIRepository().getDropdownFiltersForLawSearch(getDropdownFiltersForBusinessSearch), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((GetDropdownFiltersForLAWSearchResponse) response.getReturnValue());
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getSearchMapFilterData(SearchTaxPayerFltrData filter, int pageIndex, ConnectionCallBack<SearchForTaxPayerForMapResponse> callBack){
        SearchForTaxPayerForMapPayload payerForMapPayload = new SearchForTaxPayerForMapPayload();
        payerForMapPayload.setContext(new SecurityContext());
        payerForMapPayload.setFltrdata(filter);
        payerForMapPayload.setPageindex(pageIndex);
        callAPI(getAPIRepository().searchForTaxPayerForMap(payerForMapPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                SearchForTaxPayerForMapResponse search = (SearchForTaxPayerForMapResponse) response.getReturnValue();
                if (search.getSearchResults() != null) {
                    callBack.onSuccess(search);
                } else {
                    callBack.onFailure(getString(R.string.msg_no_data));
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getAgreementList4Business(int accountID,int pageIndex, int pageSize,
                                                 ConnectionCallBack<AgreementDetailsList> callBack) {


        GetAgreementList4Business getAgreementListDetails = new GetAgreementList4Business();
        getAgreementListDetails.setAccountId(accountID);
        getAgreementListDetails.setPageIndex(pageIndex);
        getAgreementListDetails.setPageSize(pageSize);

        callAPI(getAPIRepository().getAgreementList4Business(getAgreementListDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((AgreementDetailsList) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getListDueNotice(int accountId,int pageSize,int pageIndex, ConnectionCallBack<ListDueNoticeResponse> callBack) {

        ListDueNotice dueNotice=new ListDueNotice();
        dueNotice.setPageIndex(pageIndex);
        dueNotice.setPageSize(pageSize);
        dueNotice.setAccountId(accountId);

        callAPI(getAPIRepository().getListDueNotice(dueNotice), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ListDueNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getSearchTaxDueList(GetTaxDueList taxPayerDetails, ConnectionCallBack<TaxDueListResponse> callBack) {
        callAPI(getAPIRepository().searchTaxDueList(taxPayerDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                TaxDueListResponse res = (TaxDueListResponse) response.getReturnValue();
                if (res != null && res.getSearchResults() != null && res.getSearchResults().size() > 0) {

                    callBack.onSuccess((TaxDueListResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    // endregion

    // region FRM
    public static void paymentByCash4SalesInvoices(Payment payment, String remarks, SecurityContext context, ConnectionCallBack<Integer> callBack) {
        PaymentByCash4TaxInvoices paymentByCash4TaxInvoices = new PaymentByCash4TaxInvoices();
        paymentByCash4TaxInvoices.setCustomerId(payment.getCustomerID());
        paymentByCash4TaxInvoices.setCashAmount(payment.getAmountPaid());
        paymentByCash4TaxInvoices.setProductCode(payment.getProductCode());
        paymentByCash4TaxInvoices.setVoucherNo(payment.getVoucherNo());
        paymentByCash4TaxInvoices.setRemarks(remarks);
        paymentByCash4TaxInvoices.setContext(context);

        callAPI(getAPIRepository().paymentByCash4TaxInvoices(paymentByCash4TaxInvoices), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });
    }

    //this is for ticket payment
    public static void paymentByCash4ParkingTicketPayment(Payment payment, String remarks, SecurityContext context, ConnectionCallBack<Integer> callBack) {

        TicketPaymentData ticketPaymentData = new TicketPaymentData();
        ticketPaymentData.setCustomerId(payment.getCustomerID());
        ticketPaymentData.setProductCode(payment.getProductCode());
        ticketPaymentData.setVoucherNo(payment.getVoucherNo());
        ticketPaymentData.setTransactionNo(payment.getTransactionNo());
        ticketPaymentData.setMinPayAmount(payment.getMinimumPayAmount());
        ticketPaymentData.setTransactionTypeCode(payment.getTransactionTypeCode());

        ticketPaymentData.setRemarks(remarks);
        ticketPaymentData.setParkingPlaceID(payment.getParkingPlaceID());
        ticketPaymentData.setVehno(payment.getVehicleNo());
        ticketPaymentData.setParkingAmount(payment.getAmountPaid());


        PaymentByCash4TicketPayment paymentByCash4TicketPayment = new PaymentByCash4TicketPayment();
        paymentByCash4TicketPayment.setData(ticketPaymentData);
        paymentByCash4TicketPayment.setContext(context);

        callAPI(getAPIRepository().paymentByCash4ParkingTicketPayment(paymentByCash4TicketPayment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });
    }

    //this is for ticket payment - By Cash
    public static void paymentByCash4TicketPayment(Payment payment, String remarks, SecurityContext context, ConnectionCallBack<Integer> callBack) {

        TicketPaymentData ticketPaymentData = new TicketPaymentData();
        ticketPaymentData.setCustomerId(payment.getCustomerID());
        ticketPaymentData.setCashAmount(payment.getAmountPaid());
        ticketPaymentData.setProductCode(payment.getProductCode());
        ticketPaymentData.setVoucherNo(payment.getVoucherNo());
        ticketPaymentData.setTransactionNo(payment.getTransactionNo());
        ticketPaymentData.setMinPayAmount(payment.getMinimumPayAmount());
        ticketPaymentData.setTransactionTypeCode(payment.getTransactionTypeCode());
        ticketPaymentData.setSearchType(payment.getSearchType());
        ticketPaymentData.setSearchValue(payment.getSearchValue());
        ticketPaymentData.setRemarks(remarks);
        ticketPaymentData.setQty(payment.getQty());


        PaymentByCash4TicketPayment paymentByCash4TicketPayment = new PaymentByCash4TicketPayment();
        paymentByCash4TicketPayment.setData(ticketPaymentData);
        paymentByCash4TicketPayment.setContext(context);

        callAPI(getAPIRepository().paymentByCash4TicketPayment(paymentByCash4TicketPayment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });
    }

    //this is for ticket payment - By cheque
    public static void paymentByCheque4TicketPayment(Payment payment, String remarks, SecurityContext context, ConnectionCallBack<Integer> callBack) {

        TicketPaymentData ticketPaymentData = new TicketPaymentData();
        ticketPaymentData.setCustomerId(payment.getCustomerID());
        ticketPaymentData.setCashAmount(payment.getAmountPaid());
        ticketPaymentData.setProductCode(payment.getProductCode());
        ticketPaymentData.setVoucherNo(payment.getVoucherNo());
        ticketPaymentData.setTransactionNo(payment.getTransactionNo());
        ticketPaymentData.setMinPayAmount(payment.getMinimumPayAmount());
        ticketPaymentData.setTransactionTypeCode(payment.getTransactionTypeCode());
        ticketPaymentData.setSearchType(payment.getSearchType());
        ticketPaymentData.setSearchValue(payment.getSearchValue());
        ticketPaymentData.setRemarks(remarks);
        ticketPaymentData.setQty(payment.getQty());
        ticketPaymentData.setFilenameWithExt(payment.getFilenameWithExt());
        ticketPaymentData.setFileData(payment.getFileData());

        PaymentByCheque4TicketPayment paymentByCheque4TicketPayment = new PaymentByCheque4TicketPayment();
        paymentByCheque4TicketPayment.setData(ticketPaymentData);
        paymentByCheque4TicketPayment.setChequeDetails(payment.getChequeDetails());
        paymentByCheque4TicketPayment.setContext(context);

        callAPI(getAPIRepository().paymentByCheque4TicketPayment(paymentByCheque4TicketPayment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void paymentByLawWalletForTicketPayment(Payment payment, String remarks, String transactionID,String mode, SecurityContext context, ConnectionCallBack<Integer> callBack) {
        TicketPaymentData ticketPaymentData = new TicketPaymentData();
        ticketPaymentData.setCustomerId(payment.getCustomerID());
        ticketPaymentData.setCashAmount(payment.getAmountPaid());
        ticketPaymentData.setProductCode(payment.getProductCode());
        ticketPaymentData.setVoucherNo(payment.getVoucherNo());
        ticketPaymentData.setTransactionNo(payment.getTransactionNo());
        ticketPaymentData.setMinPayAmount(payment.getMinimumPayAmount());
        ticketPaymentData.setTransactionTypeCode(payment.getTransactionTypeCode());
        ticketPaymentData.setSearchType(payment.getSearchType());
        ticketPaymentData.setSearchValue(payment.getSearchValue());
        ticketPaymentData.setRemarks(remarks);
        ticketPaymentData.setQty(payment.getQty());



        PaymentByWallet wallet = new PaymentByWallet();
        wallet.setAmount(payment.getAmountPaid());
        wallet.setPaymentModeCode(mode);
        wallet.setWalletCode(mode);
        if (!transactionID.isEmpty())
            wallet.setMobiTransactionID(transactionID);

        SALWalletPaymentDetails walletPaymentDetails = new SALWalletPaymentDetails();
        if(!TextUtils.isEmpty(payment.getOtp())) {
            walletPaymentDetails.setOtp(Integer.parseInt(payment.getOtp()));}
        walletPaymentDetails.setMobileNo(payment.getCustomerMobileNo());
        walletPaymentDetails.setAmount(payment.getAmountPaid());
       // if((MyApplication.getPrefHelper().agentTypeCode==Constant.AgentTypeCode.TPA.name))
        if(MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.TPA.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.PPS.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.ASA.name())
        )
            walletPaymentDetails.setAgentAccountID(MyApplication.getPrefHelper().getAccountId());

        if (transactionID != null)
            walletPaymentDetails.setTransactionId(transactionID);

        PaymentByWallet4TicketPayment paymentByWallet4TicketPayment = new PaymentByWallet4TicketPayment();
        paymentByWallet4TicketPayment.setCustomerId(payment.getCustomerID());
        paymentByWallet4TicketPayment.setWallet(wallet);
        paymentByWallet4TicketPayment.setProductCode(payment.getProductCode());
        paymentByWallet4TicketPayment.setMakewalletpayment(walletPaymentDetails);
        paymentByWallet4TicketPayment.setContext(context);
        paymentByWallet4TicketPayment.setVoucherNo(payment.getVoucherNo());
        paymentByWallet4TicketPayment.setRemarks(remarks);
        paymentByWallet4TicketPayment.setWalletdata(ticketPaymentData);

        paymentByWallet4TicketPayment.setTransactionNo(payment.getTransactionNo());
        paymentByWallet4TicketPayment.setMinPayAmount(payment.getMinimumPayAmount());
        paymentByWallet4TicketPayment.setTransactionTypeCode(payment.getTransactionTypeCode());

        paymentByWallet4TicketPayment.setSearchType(payment.getSearchType());
        paymentByWallet4TicketPayment.setSearchValue(payment.getSearchValue());

        callAPI(getAPIRepository().paymentByLAWWallet4TicketPayment(paymentByWallet4TicketPayment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });

    }

    public static void sellableProductGenInvAndPay(Payload4SalesTax payload, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().sellableProductGenInvAndPay(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() <= 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });
    }

    public static void paymentByWalletForTicketPayment(Payment payment, String remarks, String transactionID, SecurityContext context, ConnectionCallBack<Integer> callBack) {
        PaymentByWallet wallet = new PaymentByWallet();
        wallet.setAmount(payment.getAmountPaid());

        SALWalletPaymentDetails walletPaymentDetails = new SALWalletPaymentDetails();
        if(!TextUtils.isEmpty(payment.getOtp())) {
            walletPaymentDetails.setOtp(Integer.parseInt(payment.getOtp()));}
        walletPaymentDetails.setMobileNo(payment.getCustomerMobileNo());
        walletPaymentDetails.setAmount(payment.getAmountPaid());
        if(MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.TPA.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.PPS.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.ASA.name())
        )
            walletPaymentDetails.setAgentAccountID(MyApplication.getPrefHelper().getAccountId());


        if (transactionID != null)
            walletPaymentDetails.setTransactionId(transactionID);

        PaymentByWallet4TicketPayment paymentByWallet4TicketPayment = new PaymentByWallet4TicketPayment();
        paymentByWallet4TicketPayment.setCustomerId(payment.getCustomerID());
        paymentByWallet4TicketPayment.setWallet(wallet);
        paymentByWallet4TicketPayment.setProductCode(payment.getProductCode());
        paymentByWallet4TicketPayment.setMakewalletpayment(walletPaymentDetails);
        paymentByWallet4TicketPayment.setContext(context);
        paymentByWallet4TicketPayment.setVoucherNo(payment.getVoucherNo());
        paymentByWallet4TicketPayment.setRemarks(remarks);

        paymentByWallet4TicketPayment.setTransactionNo(payment.getTransactionNo());
        paymentByWallet4TicketPayment.setMinPayAmount(payment.getMinimumPayAmount());
        paymentByWallet4TicketPayment.setTransactionTypeCode(payment.getTransactionTypeCode());

        paymentByWallet4TicketPayment.setSearchType(payment.getSearchType());
        paymentByWallet4TicketPayment.setSearchValue(payment.getSearchValue());

        callAPI(getAPIRepository().paymentByWallet4TicketPayment(paymentByWallet4TicketPayment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });

    }

    public static void paymentByWalletForTaxInvoices(Payment payment, String remarks, String transactionID, String mode,SecurityContext context, ConnectionCallBack<Integer> callBack) {
        PaymentByWallet wallet = new PaymentByWallet();
        wallet.setAmount(payment.getAmountPaid());
        wallet.setPaymentModeCode(mode);
        wallet.setWalletCode(mode);
        if (!transactionID.isEmpty())
            wallet.setMobiTransactionID(transactionID);

        SALWalletPaymentDetails walletPaymentDetails = new SALWalletPaymentDetails();
        if(!TextUtils.isEmpty(payment.getOtp())) {
            walletPaymentDetails.setOtp(Integer.parseInt(payment.getOtp()));}
        walletPaymentDetails.setMobileNo(payment.getCustomerMobileNo());
        walletPaymentDetails.setAmount(payment.getAmountPaid());
        if(MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.TPA.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.PPS.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.ASA.name())
        )
            walletPaymentDetails.setAgentAccountID(MyApplication.getPrefHelper().getAccountId());

        if (transactionID != null)
            walletPaymentDetails.setTransactionId(transactionID);

        PaymentByWallet4TaxInvoices paymentByWallet4TaxInvoices = new PaymentByWallet4TaxInvoices();
        paymentByWallet4TaxInvoices.setCustomerId(payment.getCustomerID());
        paymentByWallet4TaxInvoices.setWallet(wallet);
        paymentByWallet4TaxInvoices.setProductCode(payment.getProductCode());
        paymentByWallet4TaxInvoices.setMakewalletpayment(walletPaymentDetails);
        paymentByWallet4TaxInvoices.setContext(context);
        paymentByWallet4TaxInvoices.setVoucherNo(payment.getVoucherNo());
        paymentByWallet4TaxInvoices.setRemarks(remarks);


        callAPI(getAPIRepository().paymentByWallet4TaxInvoices(paymentByWallet4TaxInvoices), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });

    }

    public static void paymentByCash4AssetBooking(Payment payment, String remarks, SecurityContext context, ConnectionCallBack<Integer> callBack) {
        PaymentByCash4AssetBooking paymentByCash4AssetBooking = new PaymentByCash4AssetBooking();

        paymentByCash4AssetBooking.setCustomerID(payment.getCustomerID());
        paymentByCash4AssetBooking.setCashAmount(payment.getAmountPaid());
        paymentByCash4AssetBooking.setVoucherNo(payment.getVoucherNo());
        paymentByCash4AssetBooking.setRemarks(remarks);
        paymentByCash4AssetBooking.setContext(context);

        callAPI(getAPIRepository().paymentByCash4AssetBooking(paymentByCash4AssetBooking), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() != 0) {
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
                } else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void paymentByWallet4AssetBooking(Payment payment, String remarks, String transactionID, String mode,SecurityContext context, ConnectionCallBack<Integer> callBack) {
        PaymentByWallet wallet = new PaymentByWallet();
        wallet.setAmount(payment.getAmountPaid());
        wallet.setPaymentModeCode(mode);
        wallet.setWalletCode(mode);
        if (!transactionID.isEmpty())
            wallet.setMobiTransactionID(transactionID);

        SALWalletPaymentDetails walletPaymentDetails = new SALWalletPaymentDetails();
        if(!TextUtils.isEmpty(payment.getOtp())) {
            walletPaymentDetails.setOtp(Integer.parseInt(payment.getOtp()));
        }
        walletPaymentDetails.setMobileNo(payment.getCustomerMobileNo());
        walletPaymentDetails.setAmount(payment.getAmountPaid());
        if(MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.TPA.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.PPS.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.ASA.name())
        )
            walletPaymentDetails.setAgentAccountID(MyApplication.getPrefHelper().getAccountId());

        if (!transactionID.isEmpty())
            walletPaymentDetails.setTransactionId(transactionID);

        PaymentByWallet4AssetBooking paymentByWallet4AssetBooking = new PaymentByWallet4AssetBooking();
        paymentByWallet4AssetBooking.setCustomerId(payment.getCustomerID());
        paymentByWallet4AssetBooking.setWallet(wallet);
        paymentByWallet4AssetBooking.setMakewalletpayment(walletPaymentDetails);
        paymentByWallet4AssetBooking.setContext(context);
        paymentByWallet4AssetBooking.setVoucherNo(payment.getVoucherNo());
        paymentByWallet4AssetBooking.setRemarks(remarks);

        callAPI(getAPIRepository().paymentByWallet4AssetBooking(paymentByWallet4AssetBooking), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });

    }

    public static void agentSelfRecharge(PrefHelper prefhelper,Payment payment, String remarks, String transactionID, String mode,SecurityContext context, ConnectionCallBack<Integer> callBack) {

        PaymentByWallet wallet = new PaymentByWallet();
        wallet.setAmount(payment.getAmountPaid());
        wallet.setPaymentModeCode(mode);
        wallet.setWalletCode(mode);
        if (!transactionID.isEmpty())
            wallet.setMobiTransactionID(transactionID);

        SALWalletPaymentDetails walletPaymentDetails = new SALWalletPaymentDetails();
        if(!TextUtils.isEmpty(payment.getOtp())) {
            walletPaymentDetails.setOtp(Integer.parseInt(payment.getOtp()));}
        walletPaymentDetails.setAmount(payment.getAmountPaid());


        if(MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.TPA.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.PPS.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.ASA.name())
        )
            walletPaymentDetails.setAgentAccountID(MyApplication.getPrefHelper().getAccountId());

        walletPaymentDetails.setMobileNo(payment.getCustomerMobileNo());
        if (transactionID != null)
            walletPaymentDetails.setTransactionId(transactionID);

        AgentSelfRecharge agentSelfRecharge = new AgentSelfRecharge();
        agentSelfRecharge.setWallet(wallet);
        agentSelfRecharge.setMakewalletpayment(walletPaymentDetails);
        agentSelfRecharge.setRemarks(remarks);
        agentSelfRecharge.setContext(context);

        callAPI(getAPIRepository().agentSelfRecharge(agentSelfRecharge), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() instanceof Double) {
                    if (((Double) response.getReturnValue()).intValue() <= 0) {
                        callBack.onFailure(response.getMsg());
                    } else
                        callBack.onSuccess(((Double) response.getReturnValue()).intValue());
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertAgentCommission(CommissionDetails commissionDetails, ConnectionCallBack<String> callBack) {
        InsertAgentCommission agentCommission = new InsertAgentCommission();
        agentCommission.setCommissionDetails(commissionDetails);

        callAPI(getAPIRepository().insertAgentCommission(agentCommission), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && (Double) response.getReturnValue() >= 0) {
                    callBack.onSuccess(response.getMsg());
                } else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void insertRequestCashDeposit(int accountId, ConnectionCallBack<String> callBack) {
        InsertRequestCashDeposit insertRequestCashDeposit = new InsertRequestCashDeposit();
        insertRequestCashDeposit.setAccountID(accountId);

        callAPI(getAPIRepository().insertRequestCashDeposit(insertRequestCashDeposit), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                String value = (String) response.getReturnValue();
                if (TextUtils.isEmpty(value))
                    callBack.onFailure(response.getMsg());
                else
                    callBack.onSuccess(value);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void saveOutStanding(SaveOutstanding saveOutstanding, ConnectionCallBack<Boolean> callBack) {
        SaveInitialOutstanding outStanding = new SaveInitialOutstanding();
        outStanding.setSaveOutstanding(saveOutstanding);

        callAPI(getAPIRepository().saveOutstanding(outStanding), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeAssetInsurances(StoreInsuranceDocument storeInsuranceDocument, String fileNameWithExt, String fileData, ConnectionCallBack<Integer> callBack) {
        StoreInsuranceData storeDataInsurance = new StoreInsuranceData();
        storeDataInsurance.setData(storeInsuranceDocument);
        storeDataInsurance.setFilenameWithExt(fileNameWithExt);
        storeDataInsurance.setFileData(fileData);

        callAPI(getAPIRepository().storeAssetInsurances(storeDataInsurance), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() <= 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deleteInsuranceDocument(int primaryKey, ConnectionCallBack<Boolean> callBack) {
        DeleteAssetInsurance payload = new DeleteAssetInsurance();
        payload.setPrimaryKeyValue(primaryKey);

        callAPI(getAPIRepository().deleteInsuranceDocument(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeAssetFitness(StoreFitness storeFitness, String fileNameWithExt, String fileData, ConnectionCallBack<Integer> callBack) {
        StoreFitnessData storeFitnessData = new StoreFitnessData();
        storeFitnessData.setData(storeFitness);
        storeFitnessData.setFilenameWithExt(fileNameWithExt);
        storeFitnessData.setFileData(fileData);

        callAPI(getAPIRepository().storeAssetFitness(storeFitnessData), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() <= 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deleteFitnessDocument(int primaryKey, ConnectionCallBack<Boolean> callBack) {
        DeleteAssetFitness payload = new DeleteAssetFitness();
        payload.setPrimaryKeyValue(primaryKey);

        callAPI(getAPIRepository().deleteAssetFitness(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeAssetMaintenance(StoreMaintenance storeMaintenance, String fileNameWithExt, String fileData, ConnectionCallBack<Integer> callBack) {
        StoreMaintenanceData storeMaintenanceData = new StoreMaintenanceData();
        storeMaintenanceData.setData(storeMaintenance);
        storeMaintenanceData.setFilenameWithExt(fileNameWithExt);
        storeMaintenanceData.setFileData(fileData);

        callAPI(getAPIRepository().storeAssetMaintenance(storeMaintenanceData), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() <= 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deleteAssetMaintenance(int primaryKey, ConnectionCallBack<Boolean> callBack) {
        DeleteAssetMaintenance payload = new DeleteAssetMaintenance();
        payload.setPrimaryKeyValue(primaryKey);

        callAPI(getAPIRepository().deleteAssetMaintenance(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertAssetBooking(InsertAssetBookingRequest assetBookingRequest, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().insertAssetBooking(assetBookingRequest), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() < 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeAsset(StoreAsset storeAsset, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().storeAsset(storeAsset), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() < 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void validateAsset4AssignRent(String assetNo, int bookingQuantity, int bookingRequestLineId, ConnectionCallBack<ValidateAssetForAssignAndReturnResponse> callBack) {
        ValidateAsset4AssignRent validateAsset4AssignRent = new ValidateAsset4AssignRent();
        validateAsset4AssignRent.setAssetNo(assetNo);
        validateAsset4AssignRent.setBookingQuantity(bookingQuantity);
        validateAsset4AssignRent.setBookingLineId(bookingRequestLineId);

        callAPI(getAPIRepository().validateAsset4AssignRent(validateAsset4AssignRent), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                ValidateAssetForAssignAndReturnResponse returnValue = (ValidateAssetForAssignAndReturnResponse) response.getReturnValue();
                if (returnValue != null) {
                    callBack.onSuccess(returnValue);
                } else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void validateAsset4Return(String assetNo, ConnectionCallBack<ValidateAssetForAssignAndReturnResponse> callBack) {
        ValidateAsset4Return validateAsset4Return = new ValidateAsset4Return();
        validateAsset4Return.setAssetNo(assetNo);

        callAPI(getAPIRepository().validateAsset4Return(validateAsset4Return), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                ValidateAssetForAssignAndReturnResponse returnValue = (ValidateAssetForAssignAndReturnResponse) response.getReturnValue();
                if (returnValue != null) {
                    callBack.onSuccess(returnValue);
                } else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void makeMobicashPaymentRequest(MobiCashTransaction mobiCashTransaction, SecurityContext context, ConnectionCallBack<MobiCashPayment> callBack) {
        mobiCashTransaction.setContext(context);
        callAPI(getAPIRepository().makeMobicashPaymentRequest(mobiCashTransaction), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                MobiCashPayment mobiCashPayment = (MobiCashPayment) response.getReturnValue();
                if (mobiCashPayment != null) {
                    callBack.onSuccess(mobiCashPayment);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void makeMobicashPaymentRequestStatus(MobiCashPaymentStatus mobiCashPaymentStatus, SecurityContext context, ConnectionCallBack<MobiCashPayment> callBack) {
        mobiCashPaymentStatus.setContext(context);
        callAPI(getAPIRepository().getMobiCashPaymentStatus(mobiCashPaymentStatus), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
//                MobiCashPaymentStatusResponse mobiCashPaymentStatusResponse = (MobiCashPaymentStatusResponse) response.getReturnValue();
                MobiCashPayment mobiCashPaymentStatusResponse = (MobiCashPayment) response.getReturnValue();
                if (mobiCashPaymentStatusResponse != null) {
                    callBack.onSuccess(mobiCashPaymentStatusResponse);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void paymentByCheque4TaxInvoices(Payment payment, String remarks, SecurityContext context, ConnectionCallBack<Integer> callBack) {
        PaymentByCheque paymentByCheque = new PaymentByCheque();
        paymentByCheque.setCustomerId(payment.getCustomerID());
        paymentByCheque.setChequeamt(payment.getAmountPaid());
        paymentByCheque.setProdcode(payment.getProductCode());
        paymentByCheque.setVoucherNo(payment.getVoucherNo());
        paymentByCheque.setChequeDetails(payment.getChequeDetails());
        paymentByCheque.setFileData(payment.getFileData());
        paymentByCheque.setFilenameWithExt(payment.getFilenameWithExt());
        paymentByCheque.setRemarks(remarks);
        paymentByCheque.setContext(context);

        callAPI(getAPIRepository().paymentByCheque4TaxInvoices(paymentByCheque), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });
    }

    public static void paymentByCash4ServiceTax(Payment payment, String remarks, SecurityContext context, ConnectionCallBack<Integer> callBack) {
        PaymentByCash4ServiceTax paymentByCash4ServiceTax = new PaymentByCash4ServiceTax();

        paymentByCash4ServiceTax.setCustomerID(payment.getCustomerID());
        paymentByCash4ServiceTax.setCashAmount(payment.getAmountPaid());
        paymentByCash4ServiceTax.setServiceRequestNo(payment.getServiceRequestNo());
        paymentByCash4ServiceTax.setRemarks(remarks);
        paymentByCash4ServiceTax.setContext(context);

        callAPI(getAPIRepository().paymentByCash4ServiceTax(paymentByCash4ServiceTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() != 0) {
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
                } else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void paymentByWallet4ServiceTax(Payment payment, String remarks,String transactionID,String mode, SecurityContext context, ConnectionCallBack<Integer> callBack) {
        PaymentByWallet wallet = new PaymentByWallet();
        wallet.setAmount(payment.getAmountPaid());
        wallet.setWalletCode(mode);
        wallet.setPaymentModeCode(mode);
        if (!transactionID.isEmpty())
            wallet.setMobiTransactionID(transactionID);

        SALWalletPaymentDetails walletPaymentDetails = new SALWalletPaymentDetails();
        if(!TextUtils.isEmpty(payment.getOtp())) {
            walletPaymentDetails.setOtp(Integer.parseInt(payment.getOtp()));
        }
        walletPaymentDetails.setMobileNo(payment.getCustomerMobileNo());
        walletPaymentDetails.setAmount(payment.getAmountPaid());

        if (!transactionID.isEmpty())
            walletPaymentDetails.setTransactionId(transactionID);

        if(MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.TPA.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.PPS.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.ASA.name())
        )
            walletPaymentDetails.setAgentAccountID(MyApplication.getPrefHelper().getAccountId());


        PaymentByWallet4ServiceTax paymentByWallet4ServiceTax = new PaymentByWallet4ServiceTax();
        paymentByWallet4ServiceTax.setCustomerId(payment.getCustomerID());
        paymentByWallet4ServiceTax.setWallet(wallet);
        paymentByWallet4ServiceTax.setMakewalletpayment(walletPaymentDetails);
        paymentByWallet4ServiceTax.setContext(context);
        paymentByWallet4ServiceTax.setServiceRequetNo(payment.getServiceRequestNo());
        paymentByWallet4ServiceTax.setRemarks(remarks);

        callAPI(getAPIRepository().paymentByWallet4ServiceTax(paymentByWallet4ServiceTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });

    }

    public static void convertAmountToText(Double amount, ConnectionCallBack<String> callBack) {
        AmountToText amountToText = new AmountToText();
        amountToText.setAmount(amount);
        callAPI(getAPIRepository().convertAmountToText(amountToText), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((String) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getNoticePrintFlag(NoticePrintFlag printFlag, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().getNoticePrintFlag(printFlag), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((Boolean) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getInvoiceCount4Tax(CheckCurrentDue due, ConnectionCallBack<Integer> callBack) {

        callAPI(getAPIRepository().getInvoiceCount4Tax(due), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    public static void getReceiptPrintFlag(Integer id, ConnectionCallBack<Boolean> callBack) {
        ReceiptPrintFlag printFlag = new ReceiptPrintFlag();
        printFlag.setAdvrecdId(id);
        callAPI(getAPIRepository().getReceiptPrintFlag(printFlag), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((Boolean) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    // endregion

    // region SAL
    public static void getTaxDetails(int customerID, String licenseNumber, ConnectionCallBack<List<SAL_TaxDetails>> callBack) {
        GetTaxDetails taxDetails = new GetTaxDetails();
        taxDetails.setCustomerId(customerID);
        taxDetails.setLicensesNumber(licenseNumber);

        callAPI(getAPIRepository().getTaxDetails(taxDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<SAL_TaxDetails> returnValue = (List<SAL_TaxDetails>) response.getReturnValue();
                if (returnValue != null && returnValue.size() > 0) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getGenerateTaxInvoice(GenerateCustomerTaxNotice salGenerateTaxInvoiceModel, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().getGenerateTaxInvoice(salGenerateTaxInvoiceModel), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxPaymentHistory(int customerId, String productCode, int voucherNo, int pageSize, int pageIndex, ConnectionCallBack<TaxPaymentHistoryResponse> callBack) {
        GetTaxPaymentHistory paymentHistory = new GetTaxPaymentHistory();
        paymentHistory.setCustomerId(customerId);
        paymentHistory.setProductCode(productCode);
        paymentHistory.setVoucherNo(voucherNo);
        paymentHistory.setPageIndex(pageIndex);
        paymentHistory.setPageSize(pageSize);

        callAPI(getAPIRepository().getTaxPaymentHistory(paymentHistory), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                TaxPaymentHistoryResponse returnValue = (TaxPaymentHistoryResponse) response.getReturnValue();
                if (returnValue != null) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getROPDetails(int customerID, ConnectionCallBack<List<ROPDetails>> callBack) {
        GetROPDetails ropDetails = new GetROPDetails();
        ropDetails.setCustomerId(customerID);

        callAPI(getAPIRepository().getROPDetails(ropDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<ROPDetails> returnValue = (List<ROPDetails>) response.getReturnValue();
                if (returnValue != null && returnValue.size() > 0) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void createROPTaxInvoice(Payment payment, SecurityContext context, ConnectionCallBack<Integer> callBack) {

        PaymentByWallet wallet = null;
        if (payment.getPaymentMode() == Constant.PaymentMode.WALLET) {
            wallet = new PaymentByWallet();
            wallet.setAmount(payment.getAmountPaid());
        }

        CreateROPTaxInvoice createROPTaxInvoice = new CreateROPTaxInvoice();
        createROPTaxInvoice.setVoucherNo(payment.getCurrentTaxInvoiceNo());
        createROPTaxInvoice.setCashAmount(payment.getAmountPaid());
        createROPTaxInvoice.setWallet(wallet);
        createROPTaxInvoice.setContext(context);

        callAPI(getAPIRepository().createROPTaxInvoice(createROPTaxInvoice), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });
    }

    public static void getAgentBalance(ConnectionCallBack<Double> callBack) {
        GetAgentBalance agentBalance = new GetAgentBalance();
        agentBalance.setAccountID(MyApplication.getPrefHelper().getAccountId());

        callAPI(getAPIRepository().getAgentBalance(agentBalance), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Double) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getAgentStatement(String fromDate, String toDate, ConnectionCallBack<CreditBalanceResponse> callBack) {
        GetAgentStatement statement = new GetAgentStatement();
        statement.setFromDate(fromDate);
        statement.setToDate(toDate);
        callAPI(getAPIRepository().getAgentStatement(statement), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                CreditBalanceResponse creditBalance = (CreditBalanceResponse) response.getReturnValue();
                if (creditBalance != null) {
                    callBack.onSuccess(creditBalance);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getInvoiceTemplateInfo(String receiptCode, int invoiceNo, int advanceReceivedId, ConnectionCallBack<List<GetInvoiceTemplateResponse>> callBack) {
        GetInvoiceTemplateInfo getInvoiceTemplateInfo = new GetInvoiceTemplateInfo();

        getInvoiceTemplateInfo.setInvoiceId(invoiceNo);
        getInvoiceTemplateInfo.setAdvanceReceivedId(advanceReceivedId);
        getInvoiceTemplateInfo.setReceiptcode(receiptCode);

        callAPI(getAPIRepository().getInvoiceTemplateInfo(getInvoiceTemplateInfo), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<GetInvoiceTemplateResponse> returnValue = (List<GetInvoiceTemplateResponse>) response.getReturnValue();
                if (returnValue != null && returnValue.size() > 0) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }
    //Newly added function
    public static void getPayoutListForAgent(int accountId,int mPageindex, int mPageSize, ConnectionCallBack<AgentCommissionResult> callBack) {
        AgentCommissionPayOut agentCommissionPayOut = new AgentCommissionPayOut();
        agentCommissionPayOut.setAccountId(accountId);
        agentCommissionPayOut.setPageindex(mPageindex);
        agentCommissionPayOut.setPagesize(mPageSize);

        callAPI(getAPIRepository().getPayoutListForAgent(agentCommissionPayOut), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((AgentCommissionResult) response.getReturnValue());
                } else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    public static void cancelTaxNotice(int invoiceID, String remarks, ConnectionCallBack<Boolean> callBack) {
        CancelTaxNotice taxNotice = new CancelTaxNotice();
        taxNotice.setInvoiceID(invoiceID);
        taxNotice.setRemarks(remarks);

        callAPI(getAPIRepository().cancelTaxNotice(taxNotice), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(true);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getInitialOutstandingPenalties(int accountId, ConnectionCallBack<GetOutstandingWaiveOffResponse> callBack) {
        InitialOutstandingPenalties initialOutstandingPenalties = new InitialOutstandingPenalties();
        initialOutstandingPenalties.setAccountID(accountId);

        callAPI(getAPIRepository().getInitialOutstandingPenalties(initialOutstandingPenalties), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((GetOutstandingWaiveOffResponse) response.getReturnValue());
                } else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getInitialOutstandingWaiveOff(InitialOutstandingWaiveOff initialOutstandingWaiveOff, ConnectionCallBack<Integer> callBack) {

        callAPI(getAPIRepository().getInitialOutstandingPenaltyWaiveOff(initialOutstandingWaiveOff), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
                } else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getEstimatedTaxForProduct(GetEstimatedTaxForProduct getEstimatedTaxForProduct, ConnectionCallBack<Double> callBack) {
        callAPI(getAPIRepository().getEstimatedTaxForProduct(getEstimatedTaxForProduct), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Double) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxableMatterColumnData(GetTaxableMatterColumnData getTaxableMatterColumnData, ConnectionCallBack<List<DataTaxableMatter>> callBack) {
        callAPI(getAPIRepository().getTaxableMatterColumnData(getTaxableMatterColumnData), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<DataTaxableMatter> list = (List<DataTaxableMatter>) response.getReturnValue();
                if (list.isEmpty())
                    callBack.onFailure(getString(R.string.msg_no_data));
                else
                    callBack.onSuccess(list);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getIndividualTaxDetails(CartSycoTax cartSycoTax, ConnectionCallBack<List<SAL_TaxDetails>> callBack) {
        callAPI(getAPIRepository().getIndividualTaxDetails(cartSycoTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<SAL_TaxDetails> list = (List<SAL_TaxDetails>) response.getReturnValue();
                if (list.isEmpty())
                    callBack.onFailure(getString(R.string.msg_no_data));
                else
                    callBack.onSuccess(list);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void individualTaxInitOutstandingPenalties(GetIndividualTaxInitOutstandingPenalties getIndividualTaxInitOutstandingPenalties, ConnectionCallBack<GetOutstandingWaiveOffResponse> callBack) {
        callAPI(getAPIRepository().individualTaxInitOutstandingPenalties(getIndividualTaxInitOutstandingPenalties), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((GetOutstandingWaiveOffResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void individualTaxInvoicePenalties(GetIndividualTaxInvoicePenalties getIndividualTaxInvoicePenalties, ConnectionCallBack<PenaltyList> callBack) {
        callAPI(getAPIRepository().individualTaxInvoicePenalties(getIndividualTaxInvoicePenalties), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PenaltyList) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyTaxInvoicePenalties(GetPropertyTaxInvoicePenalties getPropertyTaxInvoicePenalties, ConnectionCallBack<PenaltyList> callBack) {
        callAPI(getAPIRepository().getPropertyTaxInvoicePenalties(getPropertyTaxInvoicePenalties), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PenaltyList) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyLandTaxDetails(String sycoTaxId, ConnectionCallBack<List<SAL_TaxDetails>> callBack) {


        GetPropertyTaxDetails propertyTaxDetails = new GetPropertyTaxDetails();
        propertyTaxDetails.setSycoTaxId(sycoTaxId);

        callAPI(getAPIRepository().getPropertyTaxDetails(propertyTaxDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<SAL_TaxDetails> returnValue = (List<SAL_TaxDetails>) response.getReturnValue();
                if (returnValue != null && returnValue.size() > 0) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getPropertyTaxInitialOutstandings(Integer voucherNo, String productCode, ConnectionCallBack<GetOutstandingWaiveOffResponse> callBack) {
        PropertyTaxInitOutstandingPenalties penalties = new PropertyTaxInitOutstandingPenalties();
        penalties.setVoucherNo(voucherNo);
        penalties.setProductCode(productCode);

        callAPI(getAPIRepository().getPropertyInitialOutstandingPenalties(penalties), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((GetOutstandingWaiveOffResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getEstimatedTaxForLicense(LicenseEstimatedTax licenseEstimatedTax, ConnectionCallBack<Double> callBack) {
        GetEstimatedTax4Licenses getEstimatedTax4Licenses = new GetEstimatedTax4Licenses();
        getEstimatedTax4Licenses.setLicenseEstimatedData(licenseEstimatedTax);

        callAPI(getAPIRepository().getEstimatedTaxFoLicenses(getEstimatedTax4Licenses), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((Double) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getEstimatedAmount4ServiceTax(GetEstimatedAmount4ServiceTax getEstimatedAmount4ServiceTax, ConnectionCallBack<Double> callBack) {
        callAPI(getAPIRepository().getEstimatedAmount4ServiceTax(getEstimatedAmount4ServiceTax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() != 0)
                    callBack.onSuccess((Double) response.getReturnValue());
                else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getServiceBookingAdvanceReceiptDetails(int advanceReceivedId, ConnectionCallBack<ServiceBookingAdvanceReceiptResponse> callBack) {
        ServiceBookingAdvancePayment serviceBookingAdvancePayment = new ServiceBookingAdvancePayment();
        serviceBookingAdvancePayment.setAdvanceReceivedId(advanceReceivedId);
        serviceBookingAdvancePayment.setReceiptCode("Service_Booking_Advance_Collection");


        callAPI(getAPIRepository().getServiceBookingAdvanceReceiptDetails(serviceBookingAdvancePayment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ServiceBookingAdvanceReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void generateServiceTaxNotice(GenerateServiceTaxInvoice generateServiceTaxInvoice, ConnectionCallBack<GenerateTaxNoticeResponse> callBack) {
        callAPI(getAPIRepository().generateServiceTaxInvoice(generateServiceTaxInvoice), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if ((GenerateTaxNoticeResponse) response.getReturnValue() != null)
                    callBack.onSuccess((GenerateTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getServiceTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<ServiceTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getServiceTaxNoticeDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ServiceTaxNoticeResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyEstimatedTax4Property(ProPropertyEstimatedTax proPropertyEstimatedTax, ConnectionCallBack<Double> callBack) {
        GetPropertyEstimatedTax4PropPayload getPropertyEstimatedTax4PropPayload = new GetPropertyEstimatedTax4PropPayload();
        getPropertyEstimatedTax4PropPayload.setProPropertyEstimatedTax(proPropertyEstimatedTax);
        callAPI(getAPIRepository().getPropertyEstimatedTax4Property(getPropertyEstimatedTax4PropPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((Double) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    // endregion

    //region INV
    public static void getProducts(ConnectionCallBack<List<ProductDetails>> callBack) {
        GetProducts getProducts = new GetProducts();

        callAPI(getAPIRepository().getProducts(getProducts), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<ProductDetails> returnValue = (List<ProductDetails>) response.getReturnValue();
                if (returnValue != null && returnValue.size() > 0) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAllocationsForAccount(int accountID, String fromDate, String toDate, ConnectionCallBack<AllocatedStockResponse> callBack) {
        GetAllocationsForAccount allocationsForAccount = new GetAllocationsForAccount();
        allocationsForAccount.setAccountID(accountID);
        allocationsForAccount.setFromDate(fromDate);
        allocationsForAccount.setToDate(toDate);

        callAPI(getAPIRepository().getAllocationsForAccount(allocationsForAccount), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((AllocatedStockResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getInventoryStatus(int accountID, ConnectionCallBack<InventoryStatusResponse> callBack) {
        GetCurrentStock getCurrentStock = new GetCurrentStock();
        getCurrentStock.setAccountID(accountID);

        callAPI(getAPIRepository().getCurrentStock(getCurrentStock), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((InventoryStatusResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    //endregion

    // region AST
    public static void getAssetBookingEstimatedPrice(AssetBookingEstimatedPrice assetBookingEstimatedPrice, ConnectionCallBack<Double> callBack) {
        callAPI(getAPIRepository().getAssetBookingEstimatedPrice(assetBookingEstimatedPrice), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() instanceof Double)
                    callBack.onSuccess(((Double) response.getReturnValue()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAssetBookingTenureBookingAdvance(GetAssetBookingTenureBookingAdvance assetBookingTenureBookingAdvance, ConnectionCallBack<BookingTenureBookingAdvanceResponse> callBack) {
        callAPI(getAPIRepository().getAssetBookingTenureBookingAdvance(assetBookingTenureBookingAdvance), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((BookingTenureBookingAdvanceResponse) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBookings(GetBookingsList getBookingsList, ConnectionCallBack<List<AssetBooking>> callBack) {
        callAPI(getAPIRepository().getBookings(getBookingsList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<AssetBooking> returnValue = (List<AssetBooking>) response.getReturnValue();
                if (returnValue != null && returnValue.size() > 0)
                    callBack.onSuccess((List<AssetBooking>) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void assignAsset(Integer rentTypeID, ASTAssetRents astAssetRents, ArrayList<ASTAssetRentPreCheckLists> assetRentPreCheckLists, ArrayList<COMDocumentReference> comDocumentReferences, ConnectionCallBack<Integer> callBack) {
        AssignAsset assignAsset = new AssignAsset();
        assignAsset.setAssetRents(astAssetRents);
        assignAsset.setAssetRentPreCheckLists(assetRentPreCheckLists);
        assignAsset.setAttachment(comDocumentReferences);
        assignAsset.setAssetRentTypeID(rentTypeID);

        callAPI(getAPIRepository().assignAsset(assignAsset), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() != 0)
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
                else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }



    public static void returnAsset(ASTAssetRents astAssetRents, ArrayList<ASTAssetRentPreCheckLists> assetRentPreCheckLists, ArrayList<COMDocumentReference> comDocumentReferences, ConnectionCallBack<Integer> callBack) {
        ReturnAsset returnAsset = new ReturnAsset();
        returnAsset.setAssetRents(astAssetRents);
        returnAsset.setAssetRentPreCheckLists(assetRentPreCheckLists);
        returnAsset.setAttachment(comDocumentReferences);

        callAPI(getAPIRepository().returnAsset(returnAsset), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() != 0)
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
                else
                    callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void updateAsset(Integer assetID, ConnectionCallBack<GetUpdateAsset> callBack) {
        UpdateAsset updateAsset = new UpdateAsset();
        updateAsset.setAssetID(assetID);

        callAPI(getAPIRepository().updateAsset(updateAsset), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null)
                    callBack.onSuccess((GetUpdateAsset) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAssetBookingStock(GetAssetBookingStock stock, ConnectionCallBack<List<AvailableDatesForAssetBooking>> callBack) {
        callAPI(getAPIRepository().getAssetBookingStock(stock), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && !((List<AvailableDatesForAssetBooking>) response.getReturnValue()).isEmpty()) {
                    List<AvailableDatesForAssetBooking> availableDatesForAssetBookings = (List<AvailableDatesForAssetBooking>) response.getReturnValue();
                    callBack.onSuccess(availableDatesForAssetBookings);
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void validateAssetBooking(ValidateAssetBooking validateAssetBooking, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().validateAssetBooking(validateAssetBooking), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && (Boolean) response.getReturnValue())
                    callBack.onSuccess((Boolean) response.getReturnValue());
                else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAssetRentalDetails(Integer assetReturnId, ConnectionCallBack<AssetRentalDetailsResponse> callBack) {
        GetAssetRentDetails getAssetRentDetails = new GetAssetRentDetails();
        getAssetRentDetails.setAssetRentId(assetReturnId);

        callAPI(getAPIRepository().getAssetRentalDetails(getAssetRentDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((AssetRentalDetailsResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void searchAssetDetails(String filterString, ConnectionCallBack<AssetDetailsBySycotax> callBack) {
        GetAssetsList getAssetsList = new GetAssetsList();
        getAssetsList.setSycoTaxId(filterString);

        callAPI(getAPIRepository().searchAssetDetails(getAssetsList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((AssetDetailsBySycotax) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void searchAssetList(String filterString, ConnectionCallBack<AssetDetailsBySearch> callBack) {
        GetTaxPayerDetails getAssetsList = new GetTaxPayerDetails();
        getAssetsList.setFilterString(filterString);
        getAssetsList.setPageIndex(1);
        getAssetsList.setPageSize(20);

        callAPI(getAPIRepository().searchAssetDetailsList(getAssetsList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && ((AssetDetailsBySearch) response.getReturnValue()).getResults() != null)
                    callBack.onSuccess((AssetDetailsBySearch) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void searchUpdateAssetList(String filterString, ConnectionCallBack<AssetDetailsBySearch> callBack) {
        GetTaxPayerDetails getAssetsList = new GetTaxPayerDetails();
        getAssetsList.setFilterString(filterString);
        getAssetsList.setPageIndex(1);
        getAssetsList.setPageSize(20);

        callAPI(getAPIRepository().searchUpdateAssetDetailsList(getAssetsList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && ((AssetDetailsBySearch) response.getReturnValue()).getResults() != null)
                    callBack.onSuccess((AssetDetailsBySearch) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void searchAssetDetailsList(String filterString, ConnectionCallBack<AssetSycoTaxIdBySearch> callBack) {
        GetTaxPayerDetails getAssetsList = new GetTaxPayerDetails();
        getAssetsList.setFilterString(filterString);
        getAssetsList.setPageIndex(1);
        getAssetsList.setPageSize(20);

        callAPI(getAPIRepository().searchAvailableSycotaxID(getAssetsList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((AssetSycoTaxIdBySearch) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getIndividualTaxTransactions(GetIndividualBusinessTransactionHistory getIndividualBusinessTransactionHistory, ConnectionCallBack<GetIndividualBusinessTransactionHistoryResults> callBack) {
        callAPI(getAPIRepository().getIndividualTaxTransactions(getIndividualBusinessTransactionHistory), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((GetIndividualBusinessTransactionHistoryResults) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPrePostCheckListData(Integer assetRentID, Integer assetCategoryID, ConnectionCallBack<AssetPrePostResponse> callBack) {
        GetFormSpecs4AssetPrecheckData getFormSpecs4AssetPrecheckData = new GetFormSpecs4AssetPrecheckData();
        getFormSpecs4AssetPrecheckData.setAssetRentId(assetRentID);
        getFormSpecs4AssetPrecheckData.setAssetCategoryId(assetCategoryID);

        callAPI(getAPIRepository().getAssetRentalSpecificationForDynamicForm(getFormSpecs4AssetPrecheckData), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    AssetPrePostResponse returnValue = (AssetPrePostResponse) response.getReturnValue();
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAssetListForReturn(GetAssetList4Return getAssetList4Return, ConnectionCallBack<AssetsForReturnResponse> callBack) {
        callAPI(getAPIRepository().getAssetListForReturn(getAssetList4Return), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((AssetsForReturnResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPendingBookingsList(Integer pageSize, Integer pageIndex, ConnectionCallBack<PendingBookingResponse> callBack) {
        GetPendingAssetBookingRequest getPendingAssetBookingRequest = new GetPendingAssetBookingRequest();
        getPendingAssetBookingRequest.setPageIndex(pageIndex);
        getPendingAssetBookingRequest.setPageSize(pageSize);

        callAPI(getAPIRepository().getPendingBookingsList(getPendingAssetBookingRequest), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((PendingBookingResponse) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPostCheckListSummaryData(Integer assetRentID, ConnectionCallBack<AssetPrePostCheckListSummaryResponse> callBack) {
        PrePostCheckListSummary prePostCheckListSummary = new PrePostCheckListSummary();
        prePostCheckListSummary.setAssetRentId(assetRentID);

        callAPI(getAPIRepository().getPostCheckListSummary(prePostCheckListSummary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((AssetPrePostCheckListSummaryResponse) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPreCheckListSummaryData(Integer assetRentID, ConnectionCallBack<AssetPrePostCheckListSummaryResponse> callBack) {
        PrePostCheckListSummary prePostCheckListSummary = new PrePostCheckListSummary();
        prePostCheckListSummary.setAssetRentId(assetRentID);

        callAPI(getAPIRepository().getPreCheckListSummary(prePostCheckListSummary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((AssetPrePostCheckListSummaryResponse) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(getString(R.string.msg_no_data));
            }
        });
    }

    public static void getAssetDurationAndDistancePrice(GetAssetDurationDistancePrice assetDurationDistancePrice, ConnectionCallBack<AssetDistanceAndDurationRateResponse> callBack) {
        callAPI(getAPIRepository().getAssetDurationDistancePrice(assetDurationDistancePrice), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((AssetDistanceAndDurationRateResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    // endregion

    // region UMX
    public static void authenticateUser(String username, String password, LatLng latLng, ConnectionCallBack<Boolean> callBack) {
        APIRepository apiRepository = APIHelper.getInstanceForStaticCall().create(APIRepository.class);

        AuthenticateUser authenticateUser = new AuthenticateUser();
        final PrefHelper prefHelper = MyApplication.getPrefHelper();
        authenticateUser.setDomain(prefHelper.getDomain());
        authenticateUser.setUserName(username);
        authenticateUser.setPassword(password);
        authenticateUser.setSessionID("");
        authenticateUser.setLatitude(String.valueOf(latLng.latitude));
        authenticateUser.setLongitude(String.valueOf(latLng.longitude));

        callAPI(apiRepository.authenticateUser(authenticateUser), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                //region Save token
                if (response.getReturnValue2() != null && response.getReturnValue2().trim().length() != 0)
                    prefHelper.setSecretKey(EncryptionHelperKt.doDecrypt(response.getReturnValue2(), prefHelper.getStaticToken()));
                //endregion

                //region save Context details
                AuthenticateUserResponse userResponse = (AuthenticateUserResponse) response.getReturnValue1();
                if (userResponse != null && userResponse.getAgentDetails() != null) {

                    prefHelper.setDomain(userResponse.getDomain());
                    prefHelper.setJedisLogoutEntryID(userResponse.getLastStreamEntryID() == null ?
                            "" : userResponse.getLastStreamEntryID());
                    prefHelper.setRoleCode(userResponse.getRoleCode());
                    prefHelper.setAdminUser(userResponse.getAdminUser());
                    prefHelper.setIsApprover(userResponse.getAgentDetails().isApprover());
                    prefHelper.setLoggedInUserID(userResponse.getLoggedUserId());
                    prefHelper.setCurrencyCode(userResponse.getCurrencyCode());
                    prefHelper.setCurrencySymbol(userResponse.getCurrencySymbol());
                    prefHelper.setUserOrgID(userResponse.getUserOrgId());
                    prefHelper.setUserOrgBranchID(userResponse.getUserOrgBranchID());
                    prefHelper.setCurrencyPrecision(userResponse.getCurrencyPrecision());
                    prefHelper.setAppSessionTimeOut(userResponse.getAppSessionTimeOut());
                    prefHelper.setCurrency(userResponse.getCurrency());
                    prefHelper.setCopyrightReport(userResponse.getCopyrightReport());
                    prefHelper.setRightSide(userResponse.getRightSide());
                    prefHelper.setSymbolAtRight(userResponse.getCurrencySymbolAtRight());
                    prefHelper.setLoginCount(userResponse.getLoginCounts());
                    prefHelper.setCultureCode(userResponse.getCultrCode());
                    prefHelper.setAuthUniqueKey(userResponse.getAuthSecertKey()!= null ?userResponse.getAuthSecertKey() :"");

                    //region Save Agent Details
                    AgentDetails agentDetails = userResponse.getAgentDetails();
                    prefHelper.setAllowParkingCount(agentDetails.getAllowParkingCounts());
                    prefHelper.setAssignedZoneCode(agentDetails.getAssignedZoneCode());
                    prefHelper.setAllowParking(agentDetails.getAllowParking());
                    prefHelper.setAgentName(agentDetails.getAgentName());
                    prefHelper.setAgentFName(agentDetails.getFirstName());
                    prefHelper.setAgentMName(agentDetails.getMiddleName());
                    prefHelper.setAgentLName(agentDetails.getLastName());
                    prefHelper.setAgentEmail(agentDetails.getEmail());
                    prefHelper.setAgentMobile(agentDetails.getMobile());
                    prefHelper.setAgentContryCode(agentDetails.getTelcode());
                    prefHelper.setAgentID(agentDetails.getAgentID());
                    prefHelper.setAgentType(agentDetails.getAgentType());
                    prefHelper.setAgentTypeCode(agentDetails.getAgentTypeCode());
                    prefHelper.setAgentTypeID(agentDetails.getAgentTypeID());
                    prefHelper.setAgentSalutation(agentDetails.getSalutation());
                    prefHelper.setParentAgentID(agentDetails.getParentAgentID());
                    prefHelper.setParentAgentName(agentDetails.getParentAgentName());
                    prefHelper.setAgentUserID(agentDetails.getAgentUserID());
                    prefHelper.setAgentOwnerOrgBranchID(agentDetails.getOwnerOrgBranchID());
                    prefHelper.setAgentPassword(agentDetails.getPassword());
                    prefHelper.setAgentBranch(agentDetails.getBranchName());
                    prefHelper.setAgentFromDate(agentDetails.getFromDate());
                    prefHelper.setAgentToDate(agentDetails.getToDate());
                    prefHelper.setAgentTargetAmount(String.valueOf(agentDetails.getTargetAmount()));
                    prefHelper.setAgentCollectionAmount(String.valueOf(agentDetails.getCollectionAmount()));
                    prefHelper.setAccountId(agentDetails.getAccountID());
                    prefHelper.setSuperiorTo(agentDetails.getSuperiorTo());
                    prefHelper.setAgentIsPrepaid(agentDetails.getPrepaid() == 'Y');
                    prefHelper.setAgentAllowSales(agentDetails.getAllowSales());
                    prefHelper.setAllowPropertyTaxCollection(agentDetails.getAllowPropertyTaxCollection());
                    prefHelper.setJedisLogoutEntryID(userResponse.getLastStreamEntryID() == null ? "" : userResponse.getLastStreamEntryID());

                    if (!agentDetails.getAccountName().isEmpty())
                        prefHelper.setAccountName(agentDetails.getAccountName());
                    prefHelper.setAllowCombinedPayoutRequest(agentDetails.getAllowCombinedPayoutRequest() == 'Y');
                    //endregion

                    callBack.onSuccess(response.isSuccess());
                } else callBack.onFailure(getString(R.string.msg_invalid_response));
                //endregion

            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void verifyBusinessContacts(String mob, @NotNull String mobileOTP, @NotNull String emailOTP, @NotNull String email,String smsTemplate, Integer accountid,
                                              @NotNull ConnectionCallBack<Boolean> callBack) {

        VerifyBusinessContacts contacts = new VerifyBusinessContacts();
        contacts.setMobile(mob);
        contacts.setSmsTemplateCode(smsTemplate);
        contacts.setMobileOTP(mobileOTP);
        contacts.setEmailTemplateCode("Email_BusinessOnBoarding");
        contacts.setEmailOTP(emailOTP);
        contacts.setEmail(email);
        contacts.setAccountId(accountid);
        callAPI(getAPIRepository().verifyBusinessContacts(contacts), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {

                callBack.onSuccess(response.isSuccess());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void updateAuth2FA(String authSetUpCode, String authSecretKey, @NotNull ConnectionCallBack<Boolean> callBack) {

        Auth2FA auth2FA = new Auth2FA();

        auth2FA.setAuthenticatorSetupCodeVal(authSetUpCode);
        auth2FA.setAuthenticatorSecretKey(authSecretKey);

        callAPI(getAPIRepository().storeAuthSecretKey(auth2FA), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {

                callBack.onSuccess(response.isSuccess());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void acceptPrivacyPolicy(ConnectionCallBack<String> callBack) {
        AcceptPrivacyPolicy acceptPrivacyPolicy = new AcceptPrivacyPolicy();

        callAPI(getAPIRepository().acceptPrivacyPolicy(acceptPrivacyPolicy), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                String returnValue = (String) response.getReturnValue();
                callBack.onSuccess(returnValue);
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void insertUserPolicyDetails(UMXUserPolicyDetails userPolicyDetails, ConnectionCallBack<String> callBack) {
        InsertUserPolicyDetails insertUserPolicyDetails = new InsertUserPolicyDetails();
        insertUserPolicyDetails.setUserPolicyDetails(userPolicyDetails);

        callAPI(getAPIRepository().insertUSerPolicyDetails(insertUserPolicyDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                String returnValue = (String) response.getReturnValue();
                if (returnValue != null && !returnValue.isEmpty()) {
                    callBack.onSuccess(returnValue);
                } else {
                    callBack.onFailure(response.getMsg());
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    // endregion

    //region APP
    public static void insertPrintRequest(AppReceiptPrint appReceiptPrint, ConnectionCallBack<Integer> callBack) {
        InsertPrintRequest insertPrintRequest = new InsertPrintRequest();
        insertPrintRequest.setAppReceiptPrint(appReceiptPrint);

        callAPI(getAPIRepository().insertPrintRequest(insertPrintRequest), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deleteDocument(int primaryKey, ConnectionCallBack<Boolean> callBack) {
        DeleteDocument payload = new DeleteDocument();
        payload.setPrimaryKeyValue(primaryKey);

        callAPI(getAPIRepository().deleteDocuments(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deleteNote(int primaryKey, ConnectionCallBack<Boolean> callBack) {
        DeleteNote payload = new DeleteNote();
        payload.setPrimaryKeyValue(primaryKey);

        callAPI(getAPIRepository().deleteNotes(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void updateLogoutUser(ConnectionCallBack<Boolean> callBack) {
        LogoutUserDetails logoutUserDetails = new LogoutUserDetails();

        callAPI(getAPIRepository().updateLogoutTime(logoutUserDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((Boolean) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    //endregion

    //region Orange Wallet Payment
    public static void doPaymentWithOrangeWallet(Payment payment, ConnectionCallBack<Boolean> callBack) {
        OrangeWalletPayment payload = new OrangeWalletPayment();
        if(!TextUtils.isEmpty(payment.getOtp())) {
        payload.setOtp(payment.getOtp());}
        payload.setAmount(String.valueOf(payment.getAmountPaid()));
        //payload.setCustomerMsisdn(payment.getCustomerMobileNo());
        payload.setCustomerMsisdn("76449670");

        APIRepository apiRepository = APIHelper.getInstanceForOrangeWallet().create(APIRepository.class);
        Call<ResponseBody> call = apiRepository.doPaymentWithOrangeWallet(payload);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                try {
                    String rawResponse = response.body() == null ? "" : response.body().string();
                    if (!TextUtils.isEmpty(rawResponse)) {
                        if (rawResponse.startsWith("<status>200</status>")) {
                            callBack.onSuccess(true);
                        } else if (rawResponse.contains("<message>") && rawResponse.contains("</message>")) {
                            callBack.onFailure(rawResponse.substring(rawResponse.indexOf("<message>") + 9, rawResponse.indexOf("</message>")));
                        } else
                            callBack.onFailure(getString(R.string.msg_payment_failed) + "\n\nError: " + rawResponse);
                        /*OrangeWalletPaymentResponse paymentResponse = response.body();
                        if ("200".equals(paymentResponse.getStatus())) {
                        } else {
                            callBack.onFailure(getString(R.string.msg_payment_failed) + "\nError : " + paymentResponse.getMessage());
                        }*/
                    } else {
                        callBack.onFailure(getString(R.string.msg_payment_failed));
                    }
                } catch (IOException e) {
                    
                    callBack.onFailure(getString(R.string.msg_payment_failed) + "\n\nError: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                callBack.onFailure(getString(R.string.msg_payment_failed) + "\n\nError: " + t.getMessage());
            }
        });
    }
    //endregion

    //region Short URL
    public static void getEstimatedImpoundAmountgetShortURL(String longUrl, ConnectionCallBack<String> callBack) {
        APIRepository repository = APIHelper.getInstanceForShortURL().create(APIRepository.class);
        repository.getShortURL(BuildConfig.SHORT_URL_API_KEY, longUrl).enqueue(new Callback<ShortUrl>() {
            @Override
            public void onResponse(@NotNull Call<ShortUrl> call, @NotNull Response<ShortUrl> response) {
                if (response.body() != null) {
                    ShortUrl shortUrl = response.body();
                    if ("OK".equalsIgnoreCase(shortUrl.getStatus()) && !TextUtils.isEmpty(shortUrl.getShortUrl()))
                        callBack.onSuccess(shortUrl.getShortUrl());
                    else {
                        callBack.onFailure(shortUrl.getMessage() + "");
                    }
                } else callBack.onFailure(getString(R.string.msg_try_again));
            }

            @Override
            public void onFailure(@NotNull Call<ShortUrl> call, @NotNull Throwable t) {
                callBack.onFailure(t.getMessage() + "");
            }
        });
    }
    //endregion

    // region GenericServices
    public static void getBusinessOwners(OwnerSearchFilter ownerSearchFilter, ConnectionCallBack<BusinessOwnerResponse> callBack) {
        GetBusinessOwners businessOwners = new GetBusinessOwners();
        businessOwners.setOwnerSearchFilter(ownerSearchFilter);

        callAPI(getAPIRepository().getBusinessOwners(businessOwners), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && ((BusinessOwnerResponse) response.getReturnValue()).getResults() != null) {
                    callBack.onSuccess((BusinessOwnerResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getChildTabCount(GetChildTabCount getChildTabCount, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().getChildTabCount(getChildTabCount), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    if (((ChildTabCountResponse) response.getReturnValue()).getCount() != null)
                        callBack.onSuccess(((ChildTabCountResponse) response.getReturnValue()).getCount());
                    else callBack.onFailure("");
                } else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBusinessAddress(OwnerSearchFilter ownerSearchFilter, ConnectionCallBack<BusinessAddressResponse> callBack) {
        GetBusinessOwners businessOwners = new GetBusinessOwners();
        businessOwners.setOwnerSearchFilter(ownerSearchFilter);

        callAPI(getAPIRepository().getBusinessAddress(businessOwners), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && ((BusinessAddressResponse) response.getReturnValue()).getResults() != null) {
                    callBack.onSuccess((BusinessAddressResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaskORIncidentDetails(GetTaskORIncidentRequest getTaskRequest, ConnectionCallBack<GetTaskList> callBack) {
        callAPI(getAPIRepository().getTaskORIncidentList(getTaskRequest), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && ((GetTaskList) response.getReturnValue()).getResults() != null) {
                    callBack.onSuccess((GetTaskList) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAgentsBySearch(AdvanceSearchFilter advanceSearchFilter, ConnectionCallBack<GetAgentList> callBack) {
        GetAgents getAgents = new GetAgents();
        getAgents.setAdvsrchFilter(advanceSearchFilter);

        callAPI(getAPIRepository().getAgentsBySearch(getAgents), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && ((GetAgentList) response.getReturnValue()).getResults() != null) {
                    callBack.onSuccess((GetAgentList) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPenaltyList(PenalitySearchFilter searchFilter, ConnectionCallBack<PenaltyResponse> callBack) {
        GetPenalityList getPenalityList = new GetPenalityList();
        getPenalityList.setPenalitySearchFilter(searchFilter);

        callAPI(getAPIRepository().getPenalityList(getPenalityList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && ((PenaltyResponse) response.getReturnValue()).getResults() != null) {
                    callBack.onSuccess((PenaltyResponse) response.getReturnValue());
                } else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void penaltyWaiveOff(PenaltyWaiveOff penalityWaiveOff, ConnectionCallBack<Integer> callBack) {

        callAPI(getAPIRepository().penaltyWaiveOff(penalityWaiveOff), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getPropertyTaxPenaltyWaiveOffList(PenaltyWaiveOff penalityWaiveOff, ConnectionCallBack<Integer> callBack) {

        callAPI(getAPIRepository().penaltyWaiveOff(penalityWaiveOff), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getDynamicValuesDropdown(AdvanceSearchFilter searchFilter, ConnectionCallBack<GetSpecificationValueSetResult> callBack) {
        GetDynamicValuesDropDown getDynamicValuesDropDown = new GetDynamicValuesDropDown();
        getDynamicValuesDropDown.setDropdownSearchFilter(searchFilter);

        callAPI(getAPIRepository().getDynamicValues(getDynamicValuesDropDown), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((GetSpecificationValueSetResult) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getTableOrViewData(AdvanceSearchFilter searchFilter, ConnectionCallBack<GenericServiceResponse> callBack) {
        GenericServicePayload payload = new GenericServicePayload();
        payload.setSearchFilter(searchFilter);

        callAPI(getAPIRepository().getTableOrViewData(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (((GenericServiceResponse) response.getReturnValue()).getResult() != null && response.getReturnValue() != null) {
                    callBack.onSuccess((GenericServiceResponse) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void downloadAWSPath(Integer documentId, ConnectionCallBack<String> callBack) {
        APIRepository apiRepository = APIHelper.getInstanceForStaticCall().create(APIRepository.class);
        DownloadFileFromAWS file = new DownloadFileFromAWS();
        file.setDocumentId(documentId);

        callAPI(apiRepository.downloadfilePathFromAWS(file), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((String) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getDynamicValuesBusinessChildList(AdvanceSearchFilter searchFilter, ConnectionCallBack<GetSpecificationValueBusinessChildSet> callBack) {
        GetDynamicValuesDropDown getDynamicValuesDropDown = new GetDynamicValuesDropDown();
        getDynamicValuesDropDown.setDropdownSearchFilter(searchFilter);

        callAPI(getAPIRepository().getDynamicValuesBusinessChildList(getDynamicValuesDropDown), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((GetSpecificationValueBusinessChildSet) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }
    // endregion

    // region Receipts
    public static void generateCustomerTaxNotice(GenerateCustomerTaxNotice generateTaxInvoiceModel, ConnectionCallBack<GenerateTaxNoticeResponse> callBack) {
        callAPI(getAPIRepository().generateCustomerTaxNotice(generateTaxInvoiceModel), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                GenerateTaxNoticeResponse returnValue = (GenerateTaxNoticeResponse) response.getReturnValue();
                if (returnValue != null) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void generateCustomerAllTaxes(GenerateCustomerAllTaxes generateCustomerAllTaxes, ConnectionCallBack<List<GenerateTaxNoticeResponse>> callBack) {
        callAPI(getAPIRepository().generateCustomerAllTaxes(generateCustomerAllTaxes), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<GenerateTaxNoticeResponse> returnValue = (List<GenerateTaxNoticeResponse>) response.getReturnValue();
                if (returnValue != null && returnValue.size() > 0) {
                    callBack.onSuccess(returnValue);
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getROPTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<ROPTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getROPTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ROPTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPDOTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<PDOTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getPDOTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PDOTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getCMETaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<CMETaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getCMETaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((CMETaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getCPTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<CPTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getCPTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((CPTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getSalesTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<SalesTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(null);
        taxNoticePrintingDetails.setSalesOrderNo(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Sales_Order_Receipt");

        callAPI(getAPIRepository().getSalesTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((SalesTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getRoadTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<RoadTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getRoadTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((RoadTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxReceiptsDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<TaxReceiptsResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Payment_Receipt");

        callAPI(getAPIRepository().getTaxReceiptPrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((TaxReceiptsResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getLicenseRenewalReceiptDetails(int advanceReceivedId, ConnectionCallBack<LicenseRenewalReceiptResponse> callBack) {
        PayPointLicensePaymentPrinting payPointLicensePaymentPrinting = new PayPointLicensePaymentPrinting();
        payPointLicensePaymentPrinting.setAdvanceReceivedId(advanceReceivedId);

        callAPI(getAPIRepository().getPayPointLicenseRenewalReceipt(payPointLicensePaymentPrinting), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((LicenseRenewalReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAgentRechargeReceiptDetails(int advanceReceivedId, ConnectionCallBack<AgentRechargeReceiptResponse> callBack) {
        AgentWalletRechargePaymentDetails agentWalletRechargePaymentDetails = new AgentWalletRechargePaymentDetails();
        agentWalletRechargePaymentDetails.setAdvanceReceivedId(advanceReceivedId);

        callAPI(getAPIRepository().getAgentRechargeReceipt(agentWalletRechargePaymentDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((AgentRechargeReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPenaltyReceiptDetails(int waiveOffId, ConnectionCallBack<PenaltyWaiveOffReceiptResponse> callBack) {
        PenaltyWaiveOffReceiptDetails penaltyWaiveOffReceiptDetails = new PenaltyWaiveOffReceiptDetails();
        penaltyWaiveOffReceiptDetails.setWaiveOffId(waiveOffId);
        penaltyWaiveOffReceiptDetails.setReceiptCode("Penalty_WaiveOff_Receipt");

        callAPI(getAPIRepository().getPenaltyReceiptDetils(penaltyWaiveOffReceiptDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PenaltyWaiveOffReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAdvertisementTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<AdvertisementTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getAdvertisementTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((AdvertisementTaxNoticeResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getOutstandingReceiptDetails(int waiveOffId, ConnectionCallBack<InitialOutstandingWaiveOffReceiptResponse> callBack) {
        OutstandingWaveOffReceipt outstandingWaveOffReceipt = new OutstandingWaveOffReceipt();
        outstandingWaveOffReceipt.setOutStandingWaiveOffId(waiveOffId);
        outstandingWaveOffReceipt.setReceiptCode("OUTSTANDING_PENALTY_WAVEOFF");

        callAPI(getAPIRepository().getInitialOutStandingReceiptDetails(outstandingWaveOffReceipt), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((InitialOutstandingWaiveOffReceiptResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBookingRequestReceiptDetails(int bookingRequestId, ConnectionCallBack<BookingRequestReceiptResponse> callBack) {
        AssetBookingRequestDetails assetBookingRequestDetails = new AssetBookingRequestDetails();
        assetBookingRequestDetails.setBookingRequestId(bookingRequestId);
        assetBookingRequestDetails.setReceiptCode("Payment_Receipt");

        callAPI(getAPIRepository().getAssetBookingRequestDetails(assetBookingRequestDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((BookingRequestReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBookingAdvanceReceiptDetails(int advanceReceivedId, ConnectionCallBack<BookingAdvanceReceiptResponse> callBack) {
        AssetBookingAdvancePayment assetBookingAdvancePayment = new AssetBookingAdvancePayment();
        assetBookingAdvancePayment.setAdvanceReceivedID(advanceReceivedId);

        callAPI(getAPIRepository().getBookingAdvanceReceiptDetails(assetBookingAdvancePayment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((BookingAdvanceReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getCartTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<CartTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getCartTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((CartTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getWeaponTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<WeaponTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getWeaponTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((WeaponTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getGamingMachineTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<GamingMachineTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getGamingMachineTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((GamingMachineTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getCartTaxReceiptsDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<CartTaxReceiptResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Payment_Receipt");

        callAPI(getAPIRepository().getCartTaxReceiptPrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((CartTaxReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAssetAssignmentAndReturnReceiptDetails(int assetRentId, ConnectionCallBack<AssetRentAndReturnReceiptResponse> callBack) {
        AssetRentReceiptDetails details = new AssetRentReceiptDetails();
        details.setAssetRentId(assetRentId);

        callAPI(getAPIRepository().getAssetRentAndReturnReceiptDetails(details), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((AssetRentAndReturnReceiptResponse) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTicketReceiptDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<TicketIssueReceiptResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getTicketIssueReceipt(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((TicketIssueReceiptResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getImpoundmentReceiptDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<ImpoundmentReceiptResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getImpoundmentReceipt(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ImpoundmentReceiptResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTicketPaymentReceiptDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<TicketPaymentReceiptResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Payment_Receipt");

        callAPI(getAPIRepository().getTrafficTicketReceiptPrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((TicketPaymentReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxPaymentReceiptDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<PropertyTaxReceiptResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Payment_Receipt");

        callAPI(getAPIRepository().getTransactionReceiptPrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PropertyTaxReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getParkingReceiptDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<ParkingTicketReceiptResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getParkingTicketReceiptDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ParkingTicketReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getParkingTicketPaymentReceiptDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<ParkingTicketPaymentReceiptResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Payment_Receipt");

        callAPI(getAPIRepository().getParkingTicketPaymentReceiptDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ParkingTicketPaymentReceiptResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxSubTypesByTaxRuleBookCode(String taxrulebookcode, ConnectionCallBack<TaxSubTypeListResponse> callBack) {
        GetTaxSubTypesByTaxRuleBookCode taxSubTypes = new GetTaxSubTypesByTaxRuleBookCode();
        taxSubTypes.setTaxRuleBookCode(taxrulebookcode);
        callAPI(getAPIRepository().getTaxSubTypesByTaxRuleBookCode(taxSubTypes), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((TaxSubTypeListResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyLandTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<PropertyLandTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getPropertyLandTaxNoticePrintingDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PropertyLandTaxNoticeResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getHotelTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<HotelTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getHotelTaxNoticeDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((HotelTaxNoticeResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getShowTaxNoticePrintingDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<ShowTaxNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getShowTaxNoticeDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ShowTaxNoticeResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getLicenseRenewalTaxNoticeDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<LicenseRenewakNoticeResponse> callBack) {
        TaxNoticePrintingDetails taxNoticePrintingDetails = new TaxNoticePrintingDetails();
        taxNoticePrintingDetails.setInvoiceId(invoiceId);
        taxNoticePrintingDetails.setAdvanceReceivedId(advanceReceivedId);
        taxNoticePrintingDetails.setReceiptCode("Tax_Notice_Receipt");

        callAPI(getAPIRepository().getLicenseRenewalTaxNoticeDetails(taxNoticePrintingDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((LicenseRenewakNoticeResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    // endregion


    public static void getVehicleDetailsWithOwner(String filterString, ConnectionCallBack<VehicleDetailsWithOwnerResponse> callBack) {
        GetVehiclesDetailsWithCurrentOwner vehiclesDetailsWithCurrentOwner = new GetVehiclesDetailsWithCurrentOwner();
        vehiclesDetailsWithCurrentOwner.setFilterData(filterString);

        callAPI(getAPIRepository().getVehicleDetailsWithOwner(vehiclesDetailsWithCurrentOwner), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((VehicleDetailsWithOwnerResponse) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    //region LAW
    public static void getVehicleOwnershipDetails(String vehicleNo, ConnectionCallBack<VehicleOwnershipDetailsResult> callBack) {
        GetVehicleOwnershipDetails getVehicleOwnershipDetails = new GetVehicleOwnershipDetails();
        getVehicleOwnershipDetails.setVehicleNo(vehicleNo);

        callAPI(getAPIRepository().getVehicleOwnershipDetails(getVehicleOwnershipDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((VehicleOwnershipDetailsResult) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getBusiness(String filterData, ConnectionCallBack<BusinessResponse> callBack) {
        GetBusiness business = new GetBusiness();
        business.setFilterdata(filterData);

        callAPI(getAPIRepository().getBusinessList(business), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && response.getReturnValue() != null) {
                    callBack.onSuccess((BusinessResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getEstimatedFineAmount(Integer violationTypeId, ConnectionCallBack<Double> callBack) {
        GetEstimatedFineAmount estimatedFineAmount = new GetEstimatedFineAmount();
        estimatedFineAmount.setViolationTypeId(violationTypeId);

        callAPI(getAPIRepository().getEstimatedAmount(estimatedFineAmount), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeImpoundment(InsertImpoundment insertImpoundment, ConnectionCallBack<ImpoundmentResponse> callBack) {
        callAPI(getAPIRepository().storeImpoundment(insertImpoundment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((ImpoundmentResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertViolationTicket(VehicleTicketData ticketData, GeoAddress address, String fileExtension
            , String fileData, ViolationSignature signature, ConnectionCallBack<ViolationTicketResponse> callBack) {
        InsertViolationTicket insertViolationTicket = new InsertViolationTicket();
        insertViolationTicket.setVehicleTicketData(ticketData);
        insertViolationTicket.setGeoAddress(address);
        insertViolationTicket.setDocumentExtension(fileExtension);
        insertViolationTicket.setFileData(fileData);
        insertViolationTicket.setSignature(signature);
        checkVerified();
        checkRemarks();
        insertViolationTicket.setDocumentsList(ObjectHolder.INSTANCE.getDocuments());

        callAPI(getAPIRepository().insertViolationTicket(insertViolationTicket), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ViolationTicketResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getVehicleTicketsList(String filterString, ConnectionCallBack<VehicleTicketHistoryResponse> callBack) {
        GetTicketsIssuedToVehicles ticketsIssuedToVehicles = new GetTicketsIssuedToVehicles();
        ticketsIssuedToVehicles.setFilterData(filterString);

        callAPI(getAPIRepository().getVehicleTicketHistoryDetails(ticketsIssuedToVehicles), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((VehicleTicketHistoryResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTicketsIssuedToDriver(GetTicketsIssuedToDriver getTicketsIssuedToDriver, ConnectionCallBack<List<TicketHistory>> callBack) {
        callAPI(getAPIRepository().getTicketsIssuedToDriver(getTicketsIssuedToDriver), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    DriverTicketHistory driverTicketHistory = (DriverTicketHistory) response.getReturnValue();
                    if (driverTicketHistory.getTicketHistory() != null && !driverTicketHistory.getTicketHistory().isEmpty())
                        callBack.onSuccess(driverTicketHistory.getTicketHistory());
                    else
                        callBack.onFailure(getString(R.string.msg_no_data));
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getImpondmentReturnList(GetImpondmentReturn getImpondmentReturn, @NotNull ConnectionCallBack<GetImpondmentReturnResponse> callBack) {
        callAPI(getAPIRepository().getImpondmentReturnList(getImpondmentReturn), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null && response.getReturnValue() instanceof GetImpondmentReturnResponse &&
                        ((GetImpondmentReturnResponse) response.getReturnValue()).getResults() != null) {
                    callBack.onSuccess((GetImpondmentReturnResponse) response.getReturnValue());
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getLawPenaltyTransactions(GetPenaltyTransactions getPenaltyTransactions, @NotNull ConnectionCallBack<GetLawPenaltyTransactionsResponse> callBack) {
        callAPI(getAPIRepository().getLawPenaltyTransactions(getPenaltyTransactions), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((GetLawPenaltyTransactionsResponse) response.getReturnValue());
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getImpondmentReturnHistory(GetImpondmentReturnHistory getImpondmentReturnHistory, @NotNull ConnectionCallBack<GetLAWTaxTransactionsList> callBack) {
        callAPI(getAPIRepository().getImpondmentReturnHistory(getImpondmentReturnHistory), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((GetLAWTaxTransactionsList) response.getReturnValue());
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getImpoundmentReturnReceiptDetails(int invoiceId, int advanceReceivedId, ConnectionCallBack<ImpoundmentReturnReceiptResponse> callBack) {
        GetImpoundmentSummary getImpoundmentSummary = new GetImpoundmentSummary();
        getImpoundmentSummary.setImpoundmentid(invoiceId);

        callAPI(getAPIRepository().getImpoundmentReturnReceipt(getImpoundmentSummary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ImpoundmentReturnReceiptResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getImpondmentDetails(GetImpondmentDetails getImpondmentDetails, @NotNull ConnectionCallBack<GetImpondmentDetailsResponse> callBack) {
        callAPI(getAPIRepository().getImpondmentDetails(getImpondmentDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((GetImpondmentDetailsResponse) response.getReturnValue());
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertImpondmentDetails(InsertImpondmentDetails impondmentDetails, @NotNull ConnectionCallBack<ArrayList<ImpoundmentReturnResponse>> connectionCallBack) {
        callAPI(getAPIRepository().insertImpondmentDetails(impondmentDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    connectionCallBack.onSuccess((ArrayList<ImpoundmentReturnResponse>) response.getReturnValue());
                } else connectionCallBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                connectionCallBack.onFailure(message);
            }
        });
    }

    public static void getTaxInvoicesDetailsByNoticeReferenceNo(GetTaxInvoicesDetailsByNoticeReferenceNo getTaxInvoicesDetailsByNoticeReferenceNo, @NotNull ConnectionCallBack<List<TaxNoticeDetail>> callback) {
        callAPI(getAPIRepository().getTaxInvoicesDetailsByNoticeReferenceNo(getTaxInvoicesDetailsByNoticeReferenceNo), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    TrackOnTaxNotice trackOnTaxNotice = (TrackOnTaxNotice) response.getReturnValue();
                    if (trackOnTaxNotice.getTaxNoticeDetails() != null && !trackOnTaxNotice.getTaxNoticeDetails().isEmpty())
                        callback.onSuccess(trackOnTaxNotice.getTaxNoticeDetails());
                    else
                        callback.onFailure(getString(R.string.msg_no_data));
                } else callback.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callback.onFailure(message);
            }
        });
    }

    public static void getTrackOnTransactionHistory(GetTrackOnTransactionHistory history, ConnectionCallBack<TrackOnTransactionHistory> callBack) {

        callAPI(getAPIRepository().getTrackOnTransactionHistory(history), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {

                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess(((TrackOnTransactionHistory) response.getReturnValue()));
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    public static void searchVehicleDetailsFromSycoTaxId(String sycoTaxID, ConnectionCallBack<VehicleDetailsResponse> callBack) {
        SearchVehicleDetailsBySycotax searchVehicleDetailsBySycotax = new SearchVehicleDetailsBySycotax();
        searchVehicleDetailsBySycotax.setSycoTaxId(sycoTaxID);

        callAPI(getAPIRepository().getVehicleDetailsFromSycoTaxId(searchVehicleDetailsBySycotax), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((VehicleDetailsResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void searchVehicleSummary(SearchVehicleDetails vehicleDetails, ConnectionCallBack<SearchVehicleResultResponse> callBack) {
        callAPI(getAPIRepository().getVehicleSummary(vehicleDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((SearchVehicleResultResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getVehicleSummaryBySycotax(SearchVehicleDetailsBySycotax vehicleDetails, ConnectionCallBack<SearchVehicleResultResponse> callBack) {
        callAPI(getAPIRepository().getVehicleSummaryBySycotax(vehicleDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((SearchVehicleResultResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getPendingViolations(GetPendingViolationImpoundmentList getPendingViolationImpoundmentList, ConnectionCallBack<PendingViolationImpoundment> callBack) {
        callAPI(getAPIRepository().getPendingViolation(getPendingViolationImpoundmentList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((PendingViolationImpoundment) response.getReturnValue());
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getLawPendingTransactionLocationForAgent(int accountId, int pageNumber, int PageSize, ImpondmentMapFilterData impondmentMapFilterData, ConnectionCallBack<LAWPendingTransaction4Agent> callBack) {
        GetLawPendingTransactionLocation4Agent getLawPendingTransactionLocation4Agent = new GetLawPendingTransactionLocation4Agent();
        getLawPendingTransactionLocation4Agent.setAccountId(accountId);
        getLawPendingTransactionLocation4Agent.setPageindex(pageNumber);
        getLawPendingTransactionLocation4Agent.setPagesize(PageSize);
        if (impondmentMapFilterData!=null) {
            getLawPendingTransactionLocation4Agent.setFltrdata(impondmentMapFilterData);
        }
        callAPI(getAPIRepository().getPendingTransaction4Agent(getLawPendingTransactionLocation4Agent), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((LAWPendingTransaction4Agent) response.getReturnValue());
                } else {
                    if (impondmentMapFilterData != null && pageNumber == 1) {
                        callBack.onFailure(getString(R.string.msg_no_data));
                    } else {
                        callBack.onFailure(response.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getViolationTicketsByViolationTicketID(GetViolationTicketsByViolationTicketID getViolationTicketsByViolationTicketID, ConnectionCallBack<ViolationDetail> callBack) {
        callAPI(getAPIRepository().getViolationTicketsByViolationTicketID(getViolationTicketsByViolationTicketID), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    ViolationTickets tickets = (ViolationTickets) response.getReturnValue();
                    List<ViolationDetail> details = tickets.getViolationDetails();
                    if (details.isEmpty() || details == null)
                        callBack.onFailure(getString(R.string.msg_no_data));
                    else
                        callBack.onSuccess(details.get(0));
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void insertMultipleViolations(VehicleTicketData ticketData, GeoAddress address, String fileExtension
            , String fileData, ViolationSignature signature, ConnectionCallBack<List<ViolationTicketResponse>> callBack) {
        InsertMultipleViolationTicket insertViolationTicket = new InsertMultipleViolationTicket();
        insertViolationTicket.setVehicleTicketData(ticketData);
        insertViolationTicket.setGeoAddress(address);
        insertViolationTicket.setDocumentExtension(fileExtension);
        insertViolationTicket.setFileData(fileData);
        insertViolationTicket.setSignature(signature);
        checkVerified();
        checkRemarks();
        insertViolationTicket.setDocumentsList(ObjectHolder.INSTANCE.getDocuments());
        insertViolationTicket.setMultiViolationTypes(ObjectHolder.INSTANCE.getViolations());

        callAPI(getAPIRepository().insertMultipleViolations(insertViolationTicket), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((List<ViolationTicketResponse>) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getUnassignedVehiclesDetailsAPI(String filterString, ConnectionCallBack<VehicleDetailsSearchOwnerResponse> callBack) {
        GetVehiclesDetailsWithCurrentOwner vehiclesDetailsWithCurrentOwner = new GetVehiclesDetailsWithCurrentOwner();
        vehiclesDetailsWithCurrentOwner.setFilterData(filterString);

        callAPI(getAPIRepository().getUnassignedVehiclesDetails(vehiclesDetailsWithCurrentOwner), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((VehicleDetailsSearchOwnerResponse) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    public static void getEstimatedImpoundAmount(GetEstimatedImpoundAmount getEstimatedImpoundAmount, ConnectionCallBack<EstimatedImpoundAmountResponse> callBack) {
        callAPI(getAPIRepository().getEstimatedImpoundAmount(getEstimatedImpoundAmount), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if(response == null)
                    callBack.onFailure(getString(R.string.msg_no_data));
                else callBack.onSuccess((EstimatedImpoundAmountResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getStoreMultipleImpoundmentTicket(StoreMultipleImpoundmentTicketPayload storeMultipleImpoundmentTicketPayload, ConnectionCallBack<ArrayList<ImpoundmentResponse>> callBack){
        callAPI(getAPIRepository().getStoreMultipleImpoundmentTicket(storeMultipleImpoundmentTicketPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((ArrayList<ImpoundmentResponse>) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getImpoundmentAnimalReturnReceiptDetails(int impoundId, int returnLineId, ConnectionCallBack<ImpoundmentReturnReceiptResponse> callBack) {
        GetImpoundmentSummary getImpoundmentSummary = new GetImpoundmentSummary();
        getImpoundmentSummary.setImpoundmentid(impoundId);
        getImpoundmentSummary.setReturnLineID(returnLineId);

        callAPI(getAPIRepository().getImpoundmentReturnReceipt(getImpoundmentSummary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((ImpoundmentReturnReceiptResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    //endregion

    //region PRK
    public static void getAgentParkingPlaces(GetAgentParkingPlaces getAgentParkingPlaces, @NotNull ConnectionCallBack<List<AgentParkingPlace>> callback) {
        callAPI(getAPIRepository().getAgentParkingPlaces(getAgentParkingPlaces), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    AgentParkingPlaces parkingPlaces = (AgentParkingPlaces) response.getReturnValue();
                    if (parkingPlaces != null && parkingPlaces.getParkingPlaces() != null && !parkingPlaces.getParkingPlaces().isEmpty())
                        callback.onSuccess(parkingPlaces.getParkingPlaces());
                    else callback.onFailure(getString(R.string.msg_no_data));
                } else callback.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callback.onFailure(message);
            }
        });
    }

    public static void getParkingTicketDetails(String vehicleNumber, Integer parkingPlaceId, String inOut, ConnectionCallBack<ParkingTicketDetailsResponse> callBack) {
        GetParkingTicketsByVehicleNo tickets = new GetParkingTicketsByVehicleNo();
        tickets.setVehicleNo(vehicleNumber);
        tickets.setParkingPlaceId(parkingPlaceId);
        tickets.setInOut(inOut);

        callAPI(getAPIRepository().getParkingDetails(tickets), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((ParkingTicketDetailsResponse) response.getReturnValue());
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPaymentPeriodForParking(GetPaymentPeriodForParking getPaymentPeriodForParking, @NotNull ConnectionCallBack<GetPaymentPeriod> callback) {
        callAPI(getAPIRepository().getPaymentPeriodForParking(getPaymentPeriodForParking), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callback.onSuccess((GetPaymentPeriod) response.getReturnValue());
                } else callback.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callback.onFailure(message);
            }
        });
    }


    public static void paymentByWalletForParkingTicketPayment(Payment payment, String remarks, String transactionID, String mode,SecurityContext context, ConnectionCallBack<Integer> callBack) {
        PaymentByWallet wallet = new PaymentByWallet();
        wallet.setAmount(payment.getAmountPaid());
        wallet.setWalletCode(mode);
        wallet.setPaymentModeCode(mode);
        if (!transactionID.isEmpty())
            wallet.setMobiTransactionID(transactionID);

        SALWalletPaymentDetails walletPaymentDetails = new SALWalletPaymentDetails();
        if(!TextUtils.isEmpty(payment.getOtp())) {
            walletPaymentDetails.setOtp(Integer.parseInt(payment.getOtp()));
        }
        walletPaymentDetails.setMobileNo(payment.getCustomerMobileNo());
        walletPaymentDetails.setAmount(payment.getAmountPaid());

        if (transactionID != null)
            walletPaymentDetails.setTransactionId(transactionID);

        if(MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.TPA.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.PPS.name()) ||
                MyApplication.getPrefHelper().getAgentTypeCode().equals(Constant.AgentTypeCode.ASA.name())
        )
            walletPaymentDetails.setAgentAccountID(MyApplication.getPrefHelper().getAccountId());


        //data payload
        ParkingTicketDataWalletPayload parkingTicketDataWalletPayload = new ParkingTicketDataWalletPayload();
        parkingTicketDataWalletPayload.setVehicleNo(payment.getVehicleNo());
        parkingTicketDataWalletPayload.setProdCode(payment.getProductCode());
        parkingTicketDataWalletPayload.setParkingAmount(payment.getAmountPaid());
        parkingTicketDataWalletPayload.setMinPayAmount(payment.getMinimumPayAmount());
        parkingTicketDataWalletPayload.setCustomerID(payment.getCustomerID());
        parkingTicketDataWalletPayload.setRemakrs(remarks);
        parkingTicketDataWalletPayload.setTransactionTypeCode(payment.getTransactionTypeCode());
        parkingTicketDataWalletPayload.setTransactionNo(payment.getTransactionNo());
        parkingTicketDataWalletPayload.setParkingPlaceID(payment.getParkingPlaceID());


        PaymentByWallet4TicketPayment paymentByWallet4TicketPayment = new PaymentByWallet4TicketPayment();
        paymentByWallet4TicketPayment.setCustomerId(payment.getCustomerID());
        paymentByWallet4TicketPayment.setWallet(wallet);
        paymentByWallet4TicketPayment.setProductCode(payment.getProductCode());
        paymentByWallet4TicketPayment.setMakewalletpayment(walletPaymentDetails);
        paymentByWallet4TicketPayment.setData(parkingTicketDataWalletPayload);
        paymentByWallet4TicketPayment.setContext(context);
        paymentByWallet4TicketPayment.setVoucherNo(payment.getVoucherNo());
        paymentByWallet4TicketPayment.setRemarks(remarks);

        paymentByWallet4TicketPayment.setParkingPlaceID(payment.getParkingPlaceID());
        paymentByWallet4TicketPayment.setVehno(payment.getVehicleNo());
        paymentByWallet4TicketPayment.setParkingAmount(payment.getAmountPaid());

        paymentByWallet4TicketPayment.setTransactionNo(payment.getTransactionNo());
        paymentByWallet4TicketPayment.setMinPayAmount(payment.getMinimumPayAmount());
        paymentByWallet4TicketPayment.setTransactionTypeCode(payment.getTransactionTypeCode());

        paymentByWallet4TicketPayment.setSearchType(payment.getSearchType());
        paymentByWallet4TicketPayment.setSearchValue(payment.getSearchValue());

        callAPI(getAPIRepository().paymentByWallet4ParkingTicketPayment(paymentByWallet4TicketPayment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (((Double) response.getReturnValue()).intValue() == 0) {
                    callBack.onFailure(response.getMsg());
                } else
                    callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }
        });

    }

    public static void getTicketsForCancellation(GetTicketsForCancellation getTicketsForCancellation, @NotNull ConnectionCallBack<ParkingTicket> callback) {
        callAPI(getAPIRepository().getTicketsForCancellation(getTicketsForCancellation), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    ParkingTicketHistoryResponse parkingTicketHistoryResponse = (ParkingTicketHistoryResponse) response.getReturnValue();
                    if (parkingTicketHistoryResponse.getTicket() != null)
                        callback.onSuccess(parkingTicketHistoryResponse.getTicket());
                    else
                        callback.onFailure(getString(R.string.msg_no_data));
                } else callback.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callback.onFailure(message);
            }
        });
    }


    public static void getParkingTicketPaymentList(GetParkingTaxTransactionsList getParkingTaxTransactionsList, @NotNull ConnectionCallBack<GetParkingTaxTransactionResponse> callBack) {
        callAPI(getAPIRepository().getParkingPaymentList(getParkingTaxTransactionsList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((GetParkingTaxTransactionResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.no_record));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getParkingPenaltyTransactionsList(GetParkingPenaltyTransactionsList getParkingPenaltyTransactionsList, @NotNull ConnectionCallBack<GetParkingPenaltyTransactionsResponse> callBack) {
        callAPI(getAPIRepository().getParkingPenaltyTransactionsList(getParkingPenaltyTransactionsList), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((GetParkingPenaltyTransactionsResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.no_record));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getLastParkingAndOverstayChargeDetails(String vehicleNo, Integer parkingPlaceId, ConnectionCallBack<LastParkingAndOverStayChargeResponse> callBack) {
        GetLastParkingTicketOverStayCharge4Vehicle lastParking = new GetLastParkingTicketOverStayCharge4Vehicle();
        lastParking.setFilterString(vehicleNo);
        lastParking.setParkingPlaceId(parkingPlaceId);

        callAPI(getAPIRepository().getLastParkingAndOverstayDetails(lastParking), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((LastParkingAndOverStayChargeResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void saveParkingTicket(ParkingTicketPayloadData payloadData, ConnectionCallBack<ParkingTicketResponse> callBack) {
        InsertParkingTicket insertParkingTicket = new InsertParkingTicket();
        insertParkingTicket.setData(payloadData);
        insertParkingTicket.setIsfromapp(true);

        callAPI(getAPIRepository().insertParkingTicket(insertParkingTicket), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((ParkingTicketResponse) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeAndPayParkingTicket(StoreAndPayParkingTicket storeAndPayParkingTicket, ConnectionCallBack<StoreAndPayParkingTicketResponse> callBack) {
        callAPI(getAPIRepository().storeAndPayParkingTicket(storeAndPayParkingTicket), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }

            @Override
            public void onSuccess(MethodReturn response) {
                if(response != null)
                    callBack.onSuccess((StoreAndPayParkingTicketResponse) response.getReturnValue());
                else
                    callBack.onFailure(response.getMsg());
            }
        });
    }

    public static void storeParkingInOuts(ParkingInOutsData data, ConnectionCallBack<Integer> callBack) {
        StoreVehicleParkingInOuts storeVehicleParkingInOuts = new StoreVehicleParkingInOuts();
        storeVehicleParkingInOuts.setData(data);

        callAPI(getAPIRepository().storeParkingInOUts(storeVehicleParkingInOuts), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }


    //endregion

    //region PRO
    public static void isPropertySycoTaxAvailable(IsPropertySycoTaxAvailable isPropertySycoTaxAvailable, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().isPropertySycoTaxAvailable(isPropertySycoTaxAvailable), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    boolean value = (boolean) response.getReturnValue();
                    if (value)
                        callBack.onSuccess(value);
                    else
                        callBack.onFailure(getString(R.string.msg_no_data));
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void searchPropertyDetailsBySycoTax(GenericGetDetailsBySycotax genericGetDetails, ConnectionCallBack<PropertyDetailsBySycoTax> callBack) {
        callAPI(getAPIRepository().searchPropertyDetailsBySycoTax(genericGetDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((PropertyDetailsBySycoTax) response.getReturnValue());
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyComfortLevels(GetPropertyComfortLevels getPropertyComfortLevels, ConnectionCallBack<PropertyComfortLevels> callBack) {
        callAPI(getAPIRepository().getPropertyComfortLevels(getPropertyComfortLevels), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((PropertyComfortLevels) response.getReturnValue());
                } else callBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storePropertyImage(COMPropertyImage propertyImage, ConnectionCallBack<Integer> callBack) {
        StorePropertyImage storePropertyImages = new StorePropertyImage();
        storePropertyImages.setPropertyImage(propertyImage);
        callAPI(getAPIRepository().insertPropertyImage(storePropertyImages), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyImages(Integer propertyID, ConnectionCallBack<PropertyImageResponse> callBack) {
        GetPropertyImages getPropertyImages = new GetPropertyImages();
        getPropertyImages.setPropertyId(propertyID);
        callAPI(getAPIRepository().getPropertyImages(getPropertyImages), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PropertyImageResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deletePropertyImage(Integer propertyImageId, ConnectionCallBack<Boolean> callBack) {
        DeletePropertyImage deletePropertyImage = new DeletePropertyImage();
        deletePropertyImage.setPropertyImageId(propertyImageId);

        callAPI(getAPIRepository().deletePropertyImage(deletePropertyImage), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {

            }
        });
    }

    public static void getPropertyOwners(Integer propertyID, ConnectionCallBack<PropertyOwnerResponse> callBack) {
        GetPropertyOwner getPropertyOwners = new GetPropertyOwner();
        getPropertyOwners.setPropertyId(propertyID);
        callAPI(getAPIRepository().getPropertyOwners(getPropertyOwners), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PropertyOwnerResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyOwnersDetails(Integer propertyID, ConnectionCallBack<PropertyOwners> callBack) {
        GetPropertyOwner getPropertyOwners = new GetPropertyOwner();
        getPropertyOwners.setPropertyId(propertyID);
        callAPI(getAPIRepository().getPropertyOwnersDetails(getPropertyOwners), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PropertyOwners) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getEstimatedTax4Property(ProEstimatedTax proEstimatedTax, ConnectionCallBack<Double> callBack) {
        GetEstimatedTax4PropPayload getEstimatedTax4PropPayload = new GetEstimatedTax4PropPayload();
        getEstimatedTax4PropPayload.setPropEstimatedTax(proEstimatedTax);
        callAPI(getAPIRepository().getEstimatedTax4Property(getEstimatedTax4PropPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((Double) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }




   /* public static void storePropertyDetails(StorePropertyData storePropertyData, ConnectionCallBack<String> callBack) {
        StorePropertyPayload storePropertyPayload = new StorePropertyPayload();
        storePropertyPayload.setData(storePropertyData);
        callAPI(getAPIRepository().storePropertyData(storePropertyPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((String) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }*/

    public static void storePropertyDetails(StorePropertyPayload payload, ConnectionCallBack<String> callBack) {
        StorePropertyPayload spayload = new StorePropertyPayload();
        spayload.setData(payload.getData());
        if (payload.getOwnershipdata() != null)
            spayload.setOwnershipdata(payload.getOwnershipdata());

        callAPI(getAPIRepository().storePropertyData(spayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((String) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyPlans(Integer propertyID, ConnectionCallBack<PropertyPlanImageResponse> callBack) {
        GetPropertyPlans propertyPlans = new GetPropertyPlans();
        propertyPlans.setPropertyID(propertyID);
        callAPI(getAPIRepository().getPropertyPlans(propertyPlans), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PropertyPlanImageResponse) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storePropertyPlanImage(COMPropertyPlanImage propertyPlanImage, ConnectionCallBack<Integer> callBack) {
        InsertPropertyPlans save = new InsertPropertyPlans();
        save.setPropertyPlan(propertyPlanImage);
        callAPI(getAPIRepository().insertPropertyPlans(save), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void deletePropertyPlanImage(Integer propertyPlanId, ConnectionCallBack<Boolean> callBack) {
        DeletePropertyPlan deletePropertyImage = new DeletePropertyPlan();
        deletePropertyImage.setPlanId(propertyPlanId);

        callAPI(getAPIRepository().deletePropertyPlanImage(deletePropertyImage), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {

            }
        });
    }

    public static void getPropertyTaxDueYearSummary(String taxRuleBookCode, Integer voucherNo, ConnectionCallBack<PropertyDueSummaryResponse> callBack) {
        GetPropertyTaxDueYearSummary summary = new GetPropertyTaxDueYearSummary();
        summary.setVoucherNo(voucherNo);
        summary.setTaxRuleBookCode(taxRuleBookCode);

        callAPI(getAPIRepository().getPropertyTaxDueYearSummary(summary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((PropertyDueSummaryResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyDetails(Integer propertyId, ConnectionCallBack<PropertyTaxResponse> callBack) {
        GetPropertyDetails details = new GetPropertyDetails();
        details.setPropertyId(propertyId);

        callAPI(getAPIRepository().getPropertyDetails(details), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((PropertyTaxResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyDueSummary(Integer propertyId, ConnectionCallBack<PropertyDueResponse> callBack) {
        GetPropertyDueSummary summary = new GetPropertyDueSummary();
        summary.setPropertyId(propertyId);

        callAPI(getAPIRepository().getPropertyDueSummary(summary), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((PropertyDueResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyTaxNoticeHistory(GetPropertyTaxNoticeHistory getPropertyTaxNoticeHistory, ConnectionCallBack<PropertyTaxNoticeResponse> callBack) {
        callAPI(getAPIRepository().getPropertyTaxNoticeHistory(getPropertyTaxNoticeHistory), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((PropertyTaxNoticeResponse) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPendingPropertyList(GetPendingPropertyVerificationRequests verificationRequests, ConnectionCallBack<PropertyPendingVerificationResponse> callBack) {
        callAPI(getAPIRepository().getPropertyPendingVerificationList(verificationRequests), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((PropertyPendingVerificationResponse) response.getReturnValue());
                } else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {

            }
        });
    }

    public static void approvePropertyVerification(PropertyVerificationRequestData data, ConnectionCallBack<Integer> callBack) {
        ApproveRejectPropertyVerificationReq payload = new ApproveRejectPropertyVerificationReq();
        payload.setData(data);
        callAPI(getAPIRepository().approvePropertyVerification(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void rejectPropertyVerification(PropertyVerificationRequestData data, ConnectionCallBack<Integer> callBack) {
        ApproveRejectPropertyVerificationReq payload = new ApproveRejectPropertyVerificationReq();
        payload.setData(data);
        callAPI(getAPIRepository().rejectProperty(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

  /*  public static void getPropertyTax4Business(int accountID, ConnectionCallBack<PropertyTaxDetailsList> callBack) {


        GetPropertyTax4Business getPropertyTaxDetails = new GetPropertyTax4Business();
        getPropertyTaxDetails.setAccountId(accountID);

        callAPI(getAPIRepository().getPropertyTax4Business(getPropertyTaxDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null)
                    callBack.onSuccess((PropertyTaxDetailsList) response.getReturnValue());
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }
*/
  public static void getPropertyTax4Business(int accountID,int pageIndex, int pageSize,
                                             boolean isProperty, boolean island,
                                             ConnectionCallBack<PropertyLandTaxDetailsList> callBack) {


      GetPropertyTax4Business getPropertyTaxDetails = new GetPropertyTax4Business();
      getPropertyTaxDetails.setAccountId(accountID);
      getPropertyTaxDetails.setPageIndex(pageIndex);
      getPropertyTaxDetails.setPageSize(pageSize);
      getPropertyTaxDetails.setProperty(isProperty);
      getPropertyTaxDetails.setLand(island)
      ;

      callAPI(getAPIRepository().getPropertyTaxList4Business(getPropertyTaxDetails), new ConnectionCallBack<MethodReturn>() {
          @Override
          public void onSuccess(MethodReturn response) {
              if (response.getReturnValue() != null)
                  callBack.onSuccess((PropertyLandTaxDetailsList) response.getReturnValue());
              else
                  callBack.onFailure(getString(R.string.msg_no_data));
          }

          @Override
          public void onFailure(@NotNull String message) {
              callBack.onFailure(message);
          }
      });

  }

    public static void getPropertyTaxCount4Business(Integer primaryKeyValue, ConnectionCallBack<GetIndividualTaxCount> callBack) {
        GetIndividualTaxCount4Business getIndividualTaxCount4Business = new GetIndividualTaxCount4Business();
        getIndividualTaxCount4Business.setPrimaryKeyValue(primaryKeyValue);

        callAPI(getAPIRepository().getPropertyTaxCount4Business(getIndividualTaxCount4Business), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null)
                    callBack.onSuccess((GetIndividualTaxCount) response.getReturnValue());
                else callBack.onFailure("");
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getChildPropertyCount4Property(Integer propertyId, ConnectionCallBack<Integer> callBack) {

        GetChildPropertyCount4Property details = new GetChildPropertyCount4Property();
        details.setPropertyId(propertyId);

        callAPI(getAPIRepository().getChildPropertyCount4Property(details), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((Double) response.getReturnValue()).intValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getTaxes4InitialOutstanding(Integer propertyID, ConnectionCallBack<List<ProductTypes>> callBack) {
        GetTaxes4InitialOutstanding getTaxes4InitialOutstanding = new GetTaxes4InitialOutstanding();
        getTaxes4InitialOutstanding.setPropertyID(propertyID);

        callAPI(getAPIRepository().getTaxes4InitialOutstanding(getTaxes4InitialOutstanding), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<ProductTypes> productTypes = (List<ProductTypes>) response.getReturnValue();
                if (productTypes != null && !productTypes.isEmpty())
                    callBack.onSuccess(productTypes);
                else
                    callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(getString(R.string.msg_no_data));
            }
        });
    }
    //endregion
    //DeleteWeapon
    public static void deleteWeapon(int weaponId, ConnectionCallBack<Boolean> callBack) {
        DeleteWeapons payload = new DeleteWeapons();
        payload.setWeaponid(weaponId);

        callAPI(getAPIRepository().deleteWeapon(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    //DeleteCart
    public static void deleteCart(int cartId, ConnectionCallBack<Boolean> callBack) {
        DeleteCart payload = new DeleteCart();
        payload.setCartId(cartId);

        callAPI(getAPIRepository().deleteCarts(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    //DeleteGamingMachines
    public static void deleteGamingMachines(int gamingMachineId, ConnectionCallBack<Boolean> callBack) {
        DeleteGamingMachine payload = new DeleteGamingMachine();
        payload.setGamingMachineId(gamingMachineId);

        callAPI(getAPIRepository().deleteGamingMachines(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response == null || response.getReturnValue() == null || !Boolean.parseBoolean(response.getReturnValue().toString()))
                    callBack.onFailure(response == null ? "Error" : response.getMsg());
                else callBack.onSuccess(Boolean.parseBoolean(response.getReturnValue().toString()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void onBoardVehicle(GetInsertVehicleOnBoarding insertVehicleOwnershipDetails, @NotNull ConnectionCallBack<String> connectionCallBack) {
        callAPI(getAPIRepository().insertOnBoardVehicleOwnership(insertVehicleOwnershipDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    connectionCallBack.onSuccess((String) response.getReturnValue());
                } else connectionCallBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                connectionCallBack.onFailure(message);
            }
        });
    }

    public static void onUpdateVehicleonBoard(GetInsertVehicleOnBoarding insertVehicleOwnershipDetails, @NotNull ConnectionCallBack<String> connectionCallBack) {
        callAPI(getAPIRepository().updateOnBoardVehicleOwnership(insertVehicleOwnershipDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    connectionCallBack.onSuccess((String) response.getReturnValue());
                } else connectionCallBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                connectionCallBack.onFailure(message);
            }
        });
    }

    public static void onBoardVehicleOwenership(GetInsertVehicleOwnership insertVehicleOwnership, @NotNull ConnectionCallBack<Double> connectionCallBack) {
        callAPI(getAPIRepository().insertVehicleOwnership(insertVehicleOwnership), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    connectionCallBack.onSuccess((Double) response.getReturnValue());
                } else connectionCallBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                connectionCallBack.onFailure(message);
            }
        });
    }

    public static void deleteOnBoardVehicleOwnership(VehicleOwnershipDeletePayload vehicleOwnershipDeletePayload, @NotNull ConnectionCallBack<Boolean> connectionCallBack) {
        callAPI(getAPIRepository().deleteVehicleOwnership(vehicleOwnershipDeletePayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    connectionCallBack.onSuccess((Boolean) response.getReturnValue());
                } else connectionCallBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                connectionCallBack.onFailure(message);
            }
        });
    }

    public static void getRandomVehicleSycotaxIDList(@NotNull ConnectionCallBack<VehicleSycotaxListResponse> connectionCallBack) {
        GetUnusedSycoTaxId getUnusedSycoTaxId = new GetUnusedSycoTaxId();
        getUnusedSycoTaxId.setContext(new SecurityContext());
        callAPI(getAPIRepository().getRandomVehicleSycotaxIDList(getUnusedSycoTaxId), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    connectionCallBack.onSuccess((VehicleSycotaxListResponse) response.getReturnValue());
                } else connectionCallBack.onFailure(response.getMsg());
            }

            @Override
            public void onFailure(@NotNull String message) {
                connectionCallBack.onFailure(message);
            }
        });
    }

    public static void createNewParkingTicket(NewTicketCreationData newTicketCreationData, ConnectionCallBack<NewTicketCreationResponse> connectionCallBack) {
        NewTicketCreation data = new NewTicketCreation();
        data.setContext(new SecurityContext());
        data.setData(newTicketCreationData);

        callAPI(getAPIRepository().newTicketCreation(data), new ConnectionCallBack<MethodReturn>() {

            @Override
            public void onFailure(@NotNull String message) {
                connectionCallBack.onFailure(message);

            }

            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    connectionCallBack.onSuccess((NewTicketCreationResponse) response.getReturnValue());
                } else connectionCallBack.onFailure(response.getMsg());
            }
        });
    }

    public static void storePropertyOwnershipWithPropertyOwner(
            PropertyOwnershipPayload propertyOwnersPayload,
            ArrayList<PropertyOwnersPayload> propertyOwnersPayloads,
            ArrayList<COMDocumentReference> documents,
            ArrayList<COMNotes> notes, ConnectionCallBack<Integer> callBack) {
        StorePropertyOwnershipWithPropertyOwnerPayload ownershipWithPropertyOwnerPayload = new StorePropertyOwnershipWithPropertyOwnerPayload();
        ownershipWithPropertyOwnerPayload.setPropertyownership(propertyOwnersPayload);
        ownershipWithPropertyOwnerPayload.setPropertyowners(propertyOwnersPayloads);
        ownershipWithPropertyOwnerPayload.setAttachments(documents);
        ownershipWithPropertyOwnerPayload.setNotes(notes);
        callAPI(getAPIRepository().storePropertyOwnershipWithPropertyOwner(ownershipWithPropertyOwnerPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess(((int) response.getReturnValue()));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getPropertyTaxTransactions(String sycoTaxId, ConnectionCallBack<TransactionHistoryGenResp> callBack) {
        TransactionHistoryGenPayload historyGenPayload = new TransactionHistoryGenPayload();
        historyGenPayload.setSycoTaxID(sycoTaxId);
        callAPI(getAPIRepository().getPropertyTaxTransactions(historyGenPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                callBack.onSuccess((TransactionHistoryGenResp) response.getReturnValue());
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });

    }

    public static void getOrganizationLogo(LoginPayload context, ConnectionCallBack<UMXUserOrganizations> callBack) {
        APIRepository apiRepository = APIHelper.getInstanceForStaticCall().create(APIRepository.class);
        callAPI(apiRepository.getOrganizationLogo(context), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((UMXUserOrganizations) response.getReturnValue());
                } else {
                    callBack.onFailure(getString(R.string.msg_no_data));
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    //to acquire credentials, requuired for jedis connection
    public static void getMessageConnectionString(GetMessageConnectionPayload context,
                                                  ConnectionCallBack<GetMessageConnectionStringResponse> callBack) {
        APIRepository apiRepository = APIHelper.getInstance().create(APIRepository.class);
        callAPI(apiRepository.getMessageConnectionString(context), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {

                if (response.getReturnValue() != null) {
                    GetMessageConnectionStringResponse getMessageConnectionStringResponse =
                            new Gson().fromJson(response.getReturnValue().toString(),
                                    GetMessageConnectionStringResponse.class);
                    callBack.onSuccess(getMessageConnectionStringResponse);

                } else {
                    callBack.onFailure(getString(R.string.msg_no_data));
                }
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    //to get Citizen data through mobile number
    public static void getCitizenForMobileNumber(GetCitizenForMobileNumberPayload context,ConnectionCallBack<CitizenDataForMobileNumber> callBack){
      APIRepository apiRepository = APIHelper.getInstance().create(APIRepository.class);
      callAPI(apiRepository.getCitizenForMobileNumbe(context), new ConnectionCallBack<MethodReturn>() {
          @Override
          public void onSuccess(MethodReturn response) {
              if (response.getReturnValue() != null) {
                  callBack.onSuccess((CitizenDataForMobileNumber)response.getReturnValue());
                  Log.d("TAG", "onSuccess:" );
              } else {
                  Log.d("TAG", "onFailure:" + response);
                  callBack.onFailure(getString(R.string.msg_no_data));
              }

          }

          @Override
          public void onFailure(@NonNull String message) {
              Log.d("TAG", "onFailure:" + message);
              callBack.onFailure(message);
          }
      });
    }

    //to fetch all products list in Sales Tax and Security Tax page
    public static void getProductsByType(GetProductByType getProductByType,
                                         ConnectionCallBack<List<SalesProductData>> callBack) {
        callAPI(getAPIRepository().getProductsForAgent(getProductByType), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                List<SalesProductData> sellableProductList = (List<SalesProductData>) response.getReturnValue();
                if (sellableProductList != null && sellableProductList.size() > 0)
                    callBack.onSuccess(sellableProductList);
                else callBack.onFailure("");
            }
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getProductsByTypeForStockTransfer(AccountsPayload payload,
                                         ConnectionCallBack<List<SalesProductData>> callBack) {
        callAPI(getAPIRepository().getProductsForStockTransfer(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                StockTransferProductsResponse stockTransferProductsResponse = (StockTransferProductsResponse) response.getReturnValue();
                if (stockTransferProductsResponse != null && stockTransferProductsResponse.getProductList().size() > 0)
                    callBack.onSuccess(stockTransferProductsResponse.getProductList());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }
            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void generateSalesTaxAndPayment(GenerateSalesTaxAndPaymentPayload generateSalesTaxAndPayment, ConnectionCallBack<Integer> callBack) {
        callAPI(getAPIRepository().generateSalesTaxAndPayment(generateSalesTaxAndPayment), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() instanceof Integer && ((Integer) response.getReturnValue()) > 0)
                    callBack.onSuccess((Integer) response.getReturnValue());
                else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAdminOfficeAddress(GetAdminOfficeAddressPayload getAdminOfficeAddressPayload, ConnectionCallBack<AdminOfficeAdress> callBack) {
        callAPI(getAPIRepository().getAdminOfficeAddress(getAdminOfficeAddressPayload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null && response.getReturnValue() instanceof AdminOfficeAddressResponse && ((AdminOfficeAddressResponse) response.getReturnValue()).getTable() != null
                        && ((AdminOfficeAddressResponse) response.getReturnValue()).getTable().size() > 0) {
                    callBack.onSuccess(((AdminOfficeAddressResponse) response.getReturnValue()).getTable().get(0));
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    public static void getSalesHistoryDetails(SalesListDetails salesListDetails, ConnectionCallBack<SalesListReturn> callBack) {
        callAPI(getAPIRepository().getSalesHistoryDetailsList(salesListDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((SalesListReturn) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
    public static void getAdjustmentsList(AdjustmentListDetails adjustmentListDetails, ConnectionCallBack<AdjustmentListReturn> callBack) {
        callAPI(getAPIRepository().getAdjustmentsDetailsList(adjustmentListDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((AdjustmentListReturn) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getStockTranferList(StockTransferListPayload payload, ConnectionCallBack<StockTransferListReturn> callBack) {
        callAPI(getAPIRepository().getStockTransferList(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((StockTransferListReturn) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAdjustmentTypes(AdvanceSearchFilter searchFilter, ConnectionCallBack<List<INVAdjustmentType>> callBack) {
        GetDynamicValuesDropDown getDynamicValuesDropDown = new GetDynamicValuesDropDown();
        getDynamicValuesDropDown.setDropdownSearchFilter(searchFilter);

        callAPI(getAPIRepository().getAdjustmentTypes(getDynamicValuesDropDown), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    GetAdjustmentTypeResponse result = (GetAdjustmentTypeResponse) response.getReturnValue();
                    if (result.getResults() != null && result.getResults().getINVAdjustmentTypes() != null) {
                        callBack.onSuccess(result.getResults().getINVAdjustmentTypes());
                    } else callBack.onFailure(getString(R.string.msg_no_data));
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getAdministrationOffices(GetAdminOfficeAddressPayload payload, ConnectionCallBack<List<GetAdministrationOffice>> callBack) {

        callAPI(getAPIRepository().getAdministrationOffices(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    GetAdministrationOfficesResponse result = (GetAdministrationOfficesResponse) response.getReturnValue();
                    if (result.getGetAdministrationOffice() != null) {
                        callBack.onSuccess(result.getGetAdministrationOffice());
                    } else callBack.onFailure(getString(R.string.msg_no_data));
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getToandFromAccounts(AccountsPayload payload, ConnectionCallBack<List<Account>> callBack) {

        callAPI(getAPIRepository().getToandFromAccounts(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    AccountsResponse result = (AccountsResponse) response.getReturnValue();
                    if (result.getTable() != null) {
                        callBack.onSuccess(result.getTable());
                    } else callBack.onFailure(getString(R.string.msg_no_data));
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeAdjustment(StoreAdjustmentsPayload payload, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().storeAdjustment(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((Boolean) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void storeStockTransfer(StoreStockTransferPayload payload, ConnectionCallBack<Boolean> callBack) {
        callAPI(getAPIRepository().storeStockTransfer(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    callBack.onSuccess((Boolean) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getInventoryProductsDetails(GetInventoryProductsDetailsPayload payload, ConnectionCallBack<List<InventoryProductsDetails>> callBack) {
        callAPI(getAPIRepository().getInventoryProductsDetails(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response.getReturnValue() != null) {
                    InventoryProductsDetailsResponse result = (InventoryProductsDetailsResponse) response.getReturnValue();
                    if (result.getProductList() != null) {
                        callBack.onSuccess(result.getProductList());
                    } else callBack.onFailure(getString(R.string.msg_no_data));
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void uploadLogFileAPI(String fileName, String logFileString, ConnectionCallBack<Boolean> callBack) {
        UploadLogFilePayload uploadLogFile = new UploadLogFilePayload();
        uploadLogFile.setFileName(fileName);
        uploadLogFile.setLogFileString(logFileString);

        callAPI(getAPIRepository().uploadLogFile(uploadLogFile), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                Boolean res = (Boolean) response.getReturnValue();
                if (res) {
                    callBack.onSuccess(true);
                }else{
                    callBack.onFailure(getString(R.string.msg_no_data));
                }
            }

            @Override
            public void onFailure(@NonNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getQrNoteAndLogo(GetQrNoteAndLogoPayload payload, ConnectionCallBack<List<OrgData>> callBack) {
        callAPI(getAPIRepository().getQrNoteAndLogo(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    List<OrgData> data = (List<OrgData>) response.getReturnValue();
                    callBack.onSuccess(data);
                } else {
                    callBack.onFailure(getString(R.string.msg_no_data));
                }
            }

            @Override
            public void onFailure(@NonNull String message) {

            }
        });
    }

    public static void getSalesRepaymentDetails(SalesListDetails salesListDetails, ConnectionCallBack<SalesRepaymentResponse> callBack) {
        callAPI(getAPIRepository().getSalesRepaymentDetails(salesListDetails), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((SalesRepaymentResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }

    public static void getGroupingSalesReport(GetGroupingSalesReportPayload payload, ConnectionCallBack<GetGroupingSalesReportResponse> callBack) {
        callAPI(getAPIRepository().getGroupingSalesReport(payload), new ConnectionCallBack<MethodReturn>() {
            @Override
            public void onSuccess(MethodReturn response) {
                if (response != null && response.getReturnValue() != null) {
                    callBack.onSuccess((GetGroupingSalesReportResponse) response.getReturnValue());
                } else callBack.onFailure(getString(R.string.msg_no_data));
            }

            @Override
            public void onFailure(@NotNull String message) {
                callBack.onFailure(message);
            }
        });
    }
}
