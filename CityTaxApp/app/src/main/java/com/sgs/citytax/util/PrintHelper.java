package com.sgs.citytax.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.RemoteException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sgs.citytax.BuildConfig;
import com.sgs.citytax.R;
import com.sgs.citytax.api.APICall;
import com.sgs.citytax.api.payload.BusinessTaxDueYearSummary;
import com.sgs.citytax.api.payload.Organization;
import com.sgs.citytax.api.response.BusinessDueSummary;
import com.sgs.citytax.api.response.OrgData;
import com.sgs.citytax.base.MyApplication;
import com.sgs.citytax.model.CartTax;
import com.sgs.citytax.model.GamingMachineTax;
import com.sgs.citytax.model.VUCRMCustomerProductInterestLines;
import com.sgs.citytax.model.Weapon;
import com.sunmi.peripheral.printer.InnerResultCallbcak;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import sunmi.sunmiui.utils.LogUtil;

import static com.sgs.citytax.util.DateHelperKt.displayFormatDate;
import static com.sgs.citytax.util.GlobalKt.formatWithPrecision;
import static com.sgs.citytax.util.GlobalKt.getString;

public class PrintHelper {

    private SunmiPrinterService sunmiPrinterService = MyApplication.sunmiPrinterService;
    private boolean is = true;
    private InnerResultCallbcak innerResultCallbcak = new InnerResultCallbcak() {
        @Override
        public void onRunResult(boolean isSuccess) {
//            LogUtil.e("lxy", "isSuccess:" + isSuccess);
            if (is) {
                try {
                    sunmiPrinterService.lineWrap(6, innerResultCallbcak);
                    is = false;
                } catch (RemoteException e) {
                    LogHelper.writeLog(e,null);
                }
            }
        }

        @Override
        public void onReturnString(String result) {
            LogUtil.e("lxy", "result:" + result);
        }

        @Override
        public void onRaiseException(int code, String msg) {
            LogUtil.e("lxy", "code:" + code + ",msg:" + msg);
        }

        @Override
        public void onPrintResult(int code, String msg) {
            LogUtil.e("lxy", "code:" + code + ",msg:" + msg);
        }

    };

    public void printUSBThermalPrinter(String templateContent, String documentNo, Constant.ReceiptType receiptType) throws RemoteException {
        /*int textSize = templateContent.length();
        if (receiptType == Constant.ReceiptType.BUSINESS_SUMMARY)
            textSize = 24;*/
        //setHeight(0x12);
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.enterPrinterBuffer(true);
        sunmiPrinterService.printTextWithFont(templateContent, "", 24, innerResultCallbcak);
        if (receiptType != null && (receiptType == Constant.ReceiptType.BUSINESS_SUMMARY
                || receiptType == Constant.ReceiptType.TAX_NOTICE
                || MyApplication.getPrefHelper().getQrCodeEnabled()
                || receiptType == Constant.ReceiptType.TAX_NOTICE_HISTORY
                || receiptType == Constant.ReceiptType.BUSINESS_TRANSACTION)) {
            sunmiPrinterService.setAlignment(1, innerResultCallbcak);
            sunmiPrinterService.printQRCode(getURL(receiptType, documentNo), 5, 0, innerResultCallbcak);
        }
        sunmiPrinterService.autoOutPaper(innerResultCallbcak);
        sunmiPrinterService.exitPrinterBufferWithCallback(true, innerResultCallbcak);
    }

    private String getURL(Constant.ReceiptType receiptType, String documentNo) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(BuildConfig.RECEIPT_BASE_URL);
        stringBuilder.append("CustomerBill.aspx?");
        switch (receiptType) {
            case TAX_NOTICE:
                stringBuilder.append("TaxInvoiceNo=");
                break;
            case TAX_RECEIPT:
            case BUSINESS_TRANSACTION:
                stringBuilder.append("AdvReceivedID=");
                break;
            case BUSINESS_SUMMARY:
                stringBuilder.append("AccountID=");
                break;
        }
        stringBuilder.append(documentNo);
        return stringBuilder.toString();
    }

    private Bitmap CreateCode(String str) throws WriterException {
        Hashtable<EncodeHintType, String> mHashtable = new Hashtable<>();
        mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 30, 30, mHashtable);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public void setHeight(int height) throws RemoteException {
        byte[] returnText = new byte[3];
        returnText[0] = 0x1B;
        returnText[1] = 0x33;
        returnText[2] = (byte) height;
        sunmiPrinterService.sendRAWData(returnText, null);
    }

    public void printBitmap(Bitmap bitmap) throws RemoteException {
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.enterPrinterBuffer(true);
        sunmiPrinterService.printBitmap(bitmap, innerResultCallbcak);
        sunmiPrinterService.autoOutPaper(innerResultCallbcak);
        sunmiPrinterService.exitPrinterBufferWithCallback(true, innerResultCallbcak);
    }

    public void printBusinessSummaryTemplateContent(Context context, Organization organization, String businessOwner, String businessOwnerId, BigDecimal estimatedTax
            , ArrayList<VUCRMCustomerProductInterestLines> taxes, BusinessDueSummary businessDueSummary, String locale, String citizenIDSycotax, String citizenIDCardNumber,List<OrgData> orgData) throws RemoteException {

        setLocale(context, locale);
        sunmiPrinterService.enterPrinterBuffer(true);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.title_business_summary) + "\n", String.valueOf(Typeface.BOLD), 36, innerResultCallbcak);

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region QR Code
        String url = GlobalKt.getURL(Constant.ReceiptType.BUSINESS_SUMMARY);
        String qrCodeData = "";
        if (organization.getSycotaxID() != null) {
            qrCodeData = url.replace("@documentNo@", organization.getSycotaxID())
                    .replace("@sycoTaxID@", organization.getSycotaxID())
                    .replace("@LanguageCode@", MyApplication.getPrefHelper().getLanguage());
        }

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printQRCode(qrCodeData, 5, 0, innerResultCallbcak);
        //endregion

        sunmiPrinterService.printText("\n", innerResultCallbcak);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printText(orgData.get(0).getQRCodeNote()+""+orgData.get(0).getQRCodeNote2(), innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region Syco Tax id
        sunmiPrinterService.printText(context.getResources().getString(R.string.syco_tax_id) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(organization.getSycotaxID() + "\n", innerResultCallbcak);
        //endregion

        //region BusinessName
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.business_name) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(organization.getOrganization() + "\n", innerResultCallbcak);
        //endregion

        //region BusinessOwner
        if (businessOwner != null && !businessOwner.isEmpty()) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.business_owner_name) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(businessOwner + "\n", innerResultCallbcak);
        }
        //endregion
        
        //region Citizen ID Sycotax
        if (citizenIDSycotax != null && !citizenIDSycotax.isEmpty())
        {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.citizen_syco_tax_id_label) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(citizenIDSycotax + "\n", innerResultCallbcak);
        }
        //endregion

        //region ID card Number
        if (citizenIDCardNumber != null && !citizenIDCardNumber.isEmpty()) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.citizen_id_number_label) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(citizenIDCardNumber + "\n", innerResultCallbcak);
        }
        //endregion

        // region BusinessOwnerID
        if (businessOwnerId != null && !businessOwnerId.isEmpty()) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.title_business_owner_id) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(businessOwnerId + "\n", innerResultCallbcak);
        }
        //endregion

        //region Business Phone number
        if (organization.getPhone() != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.business_ph_no) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(organization.getPhone() + "\n", innerResultCallbcak);
        }
        //endregion

        //region Business Email
        if (organization.getEmail() != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.business_email) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(organization.getEmail() + "\n", innerResultCallbcak);
        }
        //endregion

        //region Estimated Tax
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(formatWithPrecision(estimatedTax, true) + "\n", innerResultCallbcak);
        //endregion


        int paper = sunmiPrinterService.getPrinterPaper();

        //region Taxes
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", null);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", null);
        }

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        sunmiPrinterService.printText(context.getResources().getString(R.string.title_taxes) + "\n", innerResultCallbcak);
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", innerResultCallbcak);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", innerResultCallbcak);
        }

        if (taxes != null) {
            for (VUCRMCustomerProductInterestLines tax : taxes) {
                if (tax != null) {
                    if (tax.getTaxRuleBookCode() != null) {
                        if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.CME.getCode())
                                || tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.CP.getCode())
                                /*|| tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.ROP.getCode())*/) {

                            //region TaxType
                            if (tax.getProduct() != null && !tax.getProduct().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_type) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getProduct() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region TaxStartDate
                            if (tax.getTaxStartDate() != null && !tax.getTaxStartDate().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_start_date) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(displayFormatDate(tax.getTaxStartDate()) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region TurnOver
                            if (tax.getTurnOver() != null && !tax.getTurnOver().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.turn_over_amount) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(formatWithPrecision(tax.getTurnOver(), true) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Billing Cycle
                            if (tax.getBillingCycleName() != null && !tax.getBillingCycleName().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.billing_cycle) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getBillingCycleName() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Estimated Tax
                            if (tax.getTaxAmount() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(formatWithPrecision(tax.getTaxAmount(), true) + "\n", innerResultCallbcak);
                            }

//                            // region Market
//                            if (tax.getMarket() != null && !tax.getMarket().isEmpty()) {
//                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
//                                sunmiPrinterService.printText(context.getResources().getString(R.string.title_market) + ": ", innerResultCallbcak);
//                                sunmiPrinterService.printText(tax.getMarket() + "\n", innerResultCallbcak);
//                            }
//                            //endregion

                            sunmiPrinterService.printText("\n", innerResultCallbcak);

                            if (paper == 1) {
                                sunmiPrinterService.printText("--------------------------------\n", null);
                            } else {
                                sunmiPrinterService.printText("------------------------------------------------\n", null);
                            }

                        } else if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.DEFAULT.getCode())) {

                            //region TaxType
                            if (tax.getProduct() != null && !tax.getProduct().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_type) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getProduct() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Billing Cycle
                            if (tax.getBillingCycleName() != null && !tax.getBillingCycleName().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.billing_cycle) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getBillingCycleName() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region Taxable Matter
                            if (tax.getTaxableMatter() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.taxable_matter) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(String.valueOf(tax.getTaxableMatter()) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Estimated Tax
                            if (tax.getTaxAmount() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(formatWithPrecision(tax.getTaxAmount(), true) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            sunmiPrinterService.printText("\n", innerResultCallbcak);

                            if (paper == 1) {
                                sunmiPrinterService.printText("--------------------------------\n", null);
                            } else {
                                sunmiPrinterService.printText("------------------------------------------------\n", null);
                            }

                        } else {
                            //region TaxType
                            if (tax.getProduct() != null && !tax.getProduct().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_type) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getProduct() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region Tax SubType
                            if (tax.getAttributeName() != null && !tax.getAttributeName().isEmpty()) {
                                if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.SHOW.getCode())) {
                                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.operator_type) + ": ", innerResultCallbcak);
                                    sunmiPrinterService.printText(tax.getAttributeName() + "\n", innerResultCallbcak);
                                } else if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.HOTEL.getCode())) {
                                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.star) + ": ", innerResultCallbcak);
                                    sunmiPrinterService.printText(tax.getAttributeName() + "\n", innerResultCallbcak);
                                } else if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.LICENSE.getCode())) {
                                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.license_category) + ": ", innerResultCallbcak);
                                    sunmiPrinterService.printText(tax.getAttributeName() + "\n", innerResultCallbcak);
                                }

                            }
                            //endregion

                            //region Occupancy Type
                            if (tax.getOccupancyName() != null && !tax.getOccupancyName().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                if (tax.getEntityName().equals("CRM_Advertisements"))
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.advertisement_type) + ": ", innerResultCallbcak);
                                else
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.occupancy_type) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getOccupancyName() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region TaxStartDate
                            if (tax.getTaxStartDate() != null && !tax.getTaxStartDate().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_start_date) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(displayFormatDate(tax.getTaxStartDate()) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region TaxableElements
                            if (tax.getTaxableElement() != null && !tax.getTaxableElement().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.taxable_element) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getTaxableElement() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Billing Cycle
                            if (tax.getBillingCycleName() != null && !tax.getBillingCycleName().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.billing_cycle) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getBillingCycleName() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region Taxable Matter
                            if (tax.getTaxableMatter() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.taxable_matter) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(String.valueOf(tax.getTaxableMatter()) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Estimated Tax
                            if (tax.getTaxAmount() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(formatWithPrecision(tax.getTaxAmount(), true) + "\n", innerResultCallbcak);
                            }
                            //endregion
                            // region Market
                            if (tax.getMarket() != null && !tax.getMarket().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.title_market) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getMarket() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            sunmiPrinterService.printText("\n", innerResultCallbcak);

                            if (paper == 1) {
                                sunmiPrinterService.printText("--------------------------------\n", null);
                            } else {
                                sunmiPrinterService.printText("------------------------------------------------\n", null);
                            }

                        }
                    }
                }
            }
        }
        //endregion

        //region Fixed Expenses
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        sunmiPrinterService.printText(context.getResources().getString(R.string.title_outstandings) + "\n", innerResultCallbcak);
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", innerResultCallbcak);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", innerResultCallbcak);
        }

        if (businessDueSummary != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.initial_outstanding_current_year_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getInitialOutstandingCurrentYearDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.current_year_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getCurrentYearDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.current_year_penalty_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getCurrentYearPenaltyDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.previous_year_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getPreviousYearDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.previous_year_penalty_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getPreviousYearPenaltyDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.anterior_year_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getAnteriorYearDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.anterior_year_penalty_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getAnteriorYearPenaltyDue(), true) + "\n", innerResultCallbcak);

        }

        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", null);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", null);
        }

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //endregion

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(DateHelperKt.formatDisplayDateTimeInMillisecond(new Date()), String.valueOf(Typeface.ITALIC), 24, innerResultCallbcak);
        sunmiPrinterService.autoOutPaper(innerResultCallbcak);
        sunmiPrinterService.exitPrinterBuffer(true);
    }

    public void printBusinessSummary(Context context, Organization organization, String businessOwner, String businessOwnerId, BigDecimal estimatedTax
            , ArrayList<VUCRMCustomerProductInterestLines> taxes, BusinessDueSummary businessDueSummary, String locale, List<OrgData> orgData) throws RemoteException {

        setLocale(context, locale);
        sunmiPrinterService.enterPrinterBuffer(true);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.title_business_summary) + "\n", String.valueOf(Typeface.BOLD), 36, innerResultCallbcak);

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region QR Code
        String url = GlobalKt.getURL(Constant.ReceiptType.BUSINESS_SUMMARY);
        String qrCodeData = "";
        if (organization.getSycotaxID() != null) {
            qrCodeData = url.replace("@documentNo@", organization.getSycotaxID())
                    .replace("@sycoTaxID@", organization.getSycotaxID())
                    .replace("@LanguageCode@", MyApplication.getPrefHelper().getLanguage());
        }

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printQRCode(qrCodeData, 5, 0, innerResultCallbcak);
        //endregion

        sunmiPrinterService.printText("\n", innerResultCallbcak);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printText(orgData.get(0).getQRCodeNote()+""+orgData.get(0).getQRCodeNote2(), innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region Syco Tax id
        sunmiPrinterService.printText(context.getResources().getString(R.string.syco_tax_id) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(organization.getSycotaxID() + "\n", innerResultCallbcak);
        //endregion

        //region BusinessName
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.business_name) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(organization.getOrganization() + "\n", innerResultCallbcak);
        //endregion

        //region BusinessOwner
        if (businessOwner != null && !businessOwner.isEmpty()) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.business_owner_name) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(businessOwner + "\n", innerResultCallbcak);
        }
        //endregion

        // region BusinessOwnerID
        if (businessOwnerId != null && !businessOwnerId.isEmpty()) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.title_business_owner_id) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(businessOwnerId + "\n", innerResultCallbcak);
        }
        //endregion

        //region Business Phone number
        if (organization.getPhone() != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.business_ph_no) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(organization.getPhone() + "\n", innerResultCallbcak);
        }
        //endregion

        //region Business Email
        if (organization.getEmail() != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.business_email) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(organization.getEmail() + "\n", innerResultCallbcak);
        }
        //endregion

        //region Estimated Tax
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(formatWithPrecision(estimatedTax, true) + "\n", innerResultCallbcak);
        //endregion


        int paper = sunmiPrinterService.getPrinterPaper();

        //region Taxes
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", null);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", null);
        }

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        sunmiPrinterService.printText(context.getResources().getString(R.string.title_taxes) + "\n", innerResultCallbcak);
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", innerResultCallbcak);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", innerResultCallbcak);
        }

        if (taxes != null) {
            for (VUCRMCustomerProductInterestLines tax : taxes) {
                if (tax != null) {
                    if (tax.getTaxRuleBookCode() != null) {
                        if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.CME.getCode())
                                || tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.CP.getCode())
                                /*|| tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.ROP.getCode())*/) {

                            //region TaxType
                            if (tax.getProduct() != null && !tax.getProduct().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_type) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getProduct() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region TaxStartDate
                            if (tax.getTaxStartDate() != null && !tax.getTaxStartDate().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_start_date) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(displayFormatDate(tax.getTaxStartDate()) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region TurnOver
                            if (tax.getTurnOver() != null && !tax.getTurnOver().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.turn_over_amount) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(formatWithPrecision(tax.getTurnOver(), true) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Billing Cycle
                            if (tax.getBillingCycleName() != null && !tax.getBillingCycleName().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.billing_cycle) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getBillingCycleName() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Estimated Tax
                            if (tax.getTaxAmount() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(formatWithPrecision(tax.getTaxAmount(), true) + "\n", innerResultCallbcak);
                            }

//                            // region Market
//                            if (tax.getMarket() != null && !tax.getMarket().isEmpty()) {
//                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
//                                sunmiPrinterService.printText(context.getResources().getString(R.string.title_market) + ": ", innerResultCallbcak);
//                                sunmiPrinterService.printText(tax.getMarket() + "\n", innerResultCallbcak);
//                            }
//                            //endregion

                            sunmiPrinterService.printText("\n", innerResultCallbcak);

                            if (paper == 1) {
                                sunmiPrinterService.printText("--------------------------------\n", null);
                            } else {
                                sunmiPrinterService.printText("------------------------------------------------\n", null);
                            }

                        } else if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.DEFAULT.getCode())) {

                            //region TaxType
                            if (tax.getProduct() != null && !tax.getProduct().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_type) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getProduct() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Billing Cycle
                            if (tax.getBillingCycleName() != null && !tax.getBillingCycleName().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.billing_cycle) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getBillingCycleName() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region Taxable Matter
                            if (tax.getTaxableMatter() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.taxable_matter) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(String.valueOf(tax.getTaxableMatter()) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Estimated Tax
                            if (tax.getTaxAmount() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(formatWithPrecision(tax.getTaxAmount(), true) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            sunmiPrinterService.printText("\n", innerResultCallbcak);

                            if (paper == 1) {
                                sunmiPrinterService.printText("--------------------------------\n", null);
                            } else {
                                sunmiPrinterService.printText("------------------------------------------------\n", null);
                            }

                        } else {
                            //region TaxType
                            if (tax.getProduct() != null && !tax.getProduct().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_type) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getProduct() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region Tax SubType
                            if (tax.getAttributeName() != null && !tax.getAttributeName().isEmpty()) {
                                if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.SHOW.getCode())) {
                                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.operator_type) + ": ", innerResultCallbcak);
                                    sunmiPrinterService.printText(tax.getAttributeName() + "\n", innerResultCallbcak);
                                } else if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.HOTEL.getCode())) {
                                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.star) + ": ", innerResultCallbcak);
                                    sunmiPrinterService.printText(tax.getAttributeName() + "\n", innerResultCallbcak);
                                } else if (tax.getTaxRuleBookCode().toUpperCase(Locale.getDefault()).equals(Constant.TaxRuleBook.LICENSE.getCode())) {
                                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.license_category) + ": ", innerResultCallbcak);
                                    sunmiPrinterService.printText(tax.getAttributeName() + "\n", innerResultCallbcak);
                                }

                            }
                            //endregion

                            //region Occupancy Type
                            if (tax.getOccupancyName() != null && !tax.getOccupancyName().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                if (tax.getEntityName().equals("CRM_Advertisements"))
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.advertisement_type) + ": ", innerResultCallbcak);
                                else
                                    sunmiPrinterService.printText(context.getResources().getString(R.string.occupancy_type) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getOccupancyName() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region TaxStartDate
                            if (tax.getTaxStartDate() != null && !tax.getTaxStartDate().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.tax_start_date) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(displayFormatDate(tax.getTaxStartDate()) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region TaxableElements
                            if (tax.getTaxableElement() != null && !tax.getTaxableElement().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.taxable_element) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getTaxableElement() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Billing Cycle
                            if (tax.getBillingCycleName() != null && !tax.getBillingCycleName().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.billing_cycle) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getBillingCycleName() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            //region Taxable Matter
                            if (tax.getTaxableMatter() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.taxable_matter) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(String.valueOf(tax.getTaxableMatter()) + "\n", innerResultCallbcak);
                            }
                            //endregion

                            // region Estimated Tax
                            if (tax.getTaxAmount() != null) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(formatWithPrecision(tax.getTaxAmount(), true) + "\n", innerResultCallbcak);
                            }
                            //endregion
                            // region Market
                            if (tax.getMarket() != null && !tax.getMarket().isEmpty()) {
                                sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                                sunmiPrinterService.printText(context.getResources().getString(R.string.title_market) + ": ", innerResultCallbcak);
                                sunmiPrinterService.printText(tax.getMarket() + "\n", innerResultCallbcak);
                            }
                            //endregion

                            sunmiPrinterService.printText("\n", innerResultCallbcak);

                            if (paper == 1) {
                                sunmiPrinterService.printText("--------------------------------\n", null);
                            } else {
                                sunmiPrinterService.printText("------------------------------------------------\n", null);
                            }

                        }
                    }
                }
            }
        }
        //endregion

        //region Fixed Expenses
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        sunmiPrinterService.printText(context.getResources().getString(R.string.title_outstandings) + "\n", innerResultCallbcak);
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", innerResultCallbcak);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", innerResultCallbcak);
        }

        if (businessDueSummary != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.initial_outstanding_current_year_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getInitialOutstandingCurrentYearDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.current_year_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getCurrentYearDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.current_year_penalty_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getCurrentYearPenaltyDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.previous_year_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getPreviousYearDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.previous_year_penalty_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getPreviousYearPenaltyDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.anterior_year_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getAnteriorYearDue(), true) + "\n", innerResultCallbcak);

            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.anterior_year_penalty_due) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(formatWithPrecision(businessDueSummary.getAnteriorYearPenaltyDue(), true) + "\n", innerResultCallbcak);

        }

        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", null);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", null);
        }

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //endregion

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(DateHelperKt.formatDisplayDateTimeInMillisecond(new Date()), String.valueOf(Typeface.ITALIC), 24, innerResultCallbcak);
        sunmiPrinterService.autoOutPaper(innerResultCallbcak);
        sunmiPrinterService.exitPrinterBuffer(true);
    }

    public void printCartSummaryContent(Context context, CartTax tax, List<BusinessTaxDueYearSummary> taxDueYearSummaries, String locale, List<OrgData> orgData) throws RemoteException {
        setLocale(context, locale);
        sunmiPrinterService.enterPrinterBuffer(true);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.title_cart_tax_summary) + "\n", String.valueOf(Typeface.BOLD), 36, innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region QR Code
        String url = GlobalKt.getURL(Constant.ReceiptType.INDIVIDUAL_TAX_SUMMARY);
        String qrCodeData = "";
        if (tax.getCartSycoTaxID() != null) {
            qrCodeData = url.replace("@documentNo@", tax.getCartID().toString())
                    .replace("@sycoTaxID@", tax.getCartSycoTaxID())
                    .replace("@taxSycoTaxID@", tax.getCartSycoTaxID())
                    .replace("@LanguageCode@", MyApplication.getPrefHelper().getLanguage());
        }

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printQRCode(qrCodeData, 5, 0, innerResultCallbcak);
        //endregion
        sunmiPrinterService.printText("\n", innerResultCallbcak);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printText(orgData.get(0).getQRCodeNote()+""+orgData.get(0).getQRCodeNote2(), innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region Syco Tax id
        sunmiPrinterService.printText(context.getResources().getString(R.string.syco_tax_id) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getCartSycoTaxID() + "\n", innerResultCallbcak);
        //endregion

        //region Registration date
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.registration_date) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(displayFormatDate(tax.getRegistrationDate()) + "\n", innerResultCallbcak);
        //endregion

        //region Cart no
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.cart_no) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getCartNo() + "\n", innerResultCallbcak);
        //endregion

        //region Cart type
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.cart_type) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getCartType() + "\n", innerResultCallbcak);
        //endregion

        //region Estimated tax
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax_amount) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(formatWithPrecision(tax.getEstimatedTax(), true) + "\n", innerResultCallbcak);
        //endregion

        // region Status
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.status) + ": ", innerResultCallbcak);
        String status = "";
        if (tax.getActive().equalsIgnoreCase("Y"))
            status = getString(R.string.active);
        else
            status = getString(R.string.inactive);

        sunmiPrinterService.printText(status + "\n", innerResultCallbcak);
        //endregion

        // region Owner
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.owner_name) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getOwner() + "\n", innerResultCallbcak);
        //endregion

        // region phonenumber
        if (tax.getAccountPhone() != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.phone_number) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(tax.getAccountPhone() + "\n", innerResultCallbcak);
        }
        //endregion

        int paper = sunmiPrinterService.getPrinterPaper();

        //region Outstandings
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", null);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", null);
        }

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        sunmiPrinterService.printText(context.getResources().getString(R.string.title_outstandings) + "\n", innerResultCallbcak);
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", innerResultCallbcak);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", innerResultCallbcak);
        }

        if (taxDueYearSummaries != null && !taxDueYearSummaries.isEmpty()) {
            for (BusinessTaxDueYearSummary taxDueYearSummary : taxDueYearSummaries) {
                if (taxDueYearSummary != null) {
                    //region Year
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.tax_year) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(taxDueYearSummary.getYear() + "\n", innerResultCallbcak);
                    //endregion

                    // region Product
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.product) + ": ", innerResultCallbcak);
                    if (taxDueYearSummary.getTaxSubType() == null || taxDueYearSummary.getTaxSubType().isEmpty())
                        sunmiPrinterService.printText(taxDueYearSummary.getProduct() + "\n", innerResultCallbcak);
                    else
                        sunmiPrinterService.printText(taxDueYearSummary.getTaxSubType() + "\n", innerResultCallbcak);
                    //endregion.

                    //region Invoice amount
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.invoice_amount) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getInvoiceAmount(), true) + "\n", innerResultCallbcak);
                    //endregion

                    // region Invoice due
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.invoice_due) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getInvoiceDue(), true) + "\n", innerResultCallbcak);
                    //endregion

                    // region Penalty amount
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.penalty_amount) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getPenaltyAmount(), true) + "\n", innerResultCallbcak);
                    //endregion

                    // region Penalty amount
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.penalty_due) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getPenaltyDue(), true) + "\n", innerResultCallbcak);
                    //endregion

                    if (paper == 1) {
                        sunmiPrinterService.printText("--------------------------------\n", null);
                    } else {
                        sunmiPrinterService.printText("------------------------------------------------\n", null);
                    }

                }
            }

        }
        //endregion

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(DateHelperKt.formatDisplayDateTimeInMillisecond(new Date()), String.valueOf(Typeface.ITALIC), 24, innerResultCallbcak);
        sunmiPrinterService.autoOutPaper(innerResultCallbcak);
        sunmiPrinterService.exitPrinterBuffer(true);

    }

    public void printGamingSummaryContent(Context context, GamingMachineTax tax, List<BusinessTaxDueYearSummary> dueYearSummaries, String locale,List<OrgData> orgData) throws RemoteException {
        setLocale(context, locale);
        sunmiPrinterService.enterPrinterBuffer(true);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.title_gaming_machine_tax_summary) + "\n", String.valueOf(Typeface.BOLD), 36, innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region QR Code
        String url = GlobalKt.getURL(Constant.ReceiptType.INDIVIDUAL_TAX_SUMMARY);
        String qrCodeData = "";
        if (tax.getGamingMachineSycotaxID() != null) {
            qrCodeData = url.replace("@documentNo@", tax.getGamingMachineID().toString())
                    .replace("@sycoTaxID@", tax.getGamingMachineSycotaxID())
                    .replace("@taxSycoTaxID@", tax.getGamingMachineSycotaxID())
                    .replace("@LanguageCode@", MyApplication.getPrefHelper().getLanguage());
        }

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printQRCode(qrCodeData, 5, 0, innerResultCallbcak);
        //endregion
        sunmiPrinterService.printText("\n", innerResultCallbcak);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printText(orgData.get(0).getQRCodeNote()+""+orgData.get(0).getQRCodeNote2(), innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region Syco Tax id
        sunmiPrinterService.printText(context.getResources().getString(R.string.syco_tax_id) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getGamingMachineSycotaxID() + "\n", innerResultCallbcak);
        //endregion

        //region Registration date
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.registration_date) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(displayFormatDate(tax.getRegistrationDate()) + "\n", innerResultCallbcak);
        //endregion

        //region Serial no
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.serial_no) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getSerialNo() + "\n", innerResultCallbcak);
        //endregion

        //region Gaming type
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.gaming_machine_type) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getGamingMachineType() + "\n", innerResultCallbcak);
        //endregion

        //region Estimated tax
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax_amount) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(formatWithPrecision(tax.getEstimatedTax(), true) + "\n", innerResultCallbcak);
        //endregion

        // region Status
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.status) + ": ", innerResultCallbcak);
        String status = "";
        if (tax.getActive().equalsIgnoreCase("Y"))
            status = getString(R.string.active);
        else
            status = getString(R.string.inactive);
        sunmiPrinterService.printText(status + "\n", innerResultCallbcak);
        //endregion

        // region Owner
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.owner_name) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getOwner() + "\n", innerResultCallbcak);
        //endregion

        //region Phone number
        if (tax.getAccountPhone() != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.phone_number) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(tax.getAccountPhone() + "\n", innerResultCallbcak);
        }
        //endregion

        int paper = sunmiPrinterService.getPrinterPaper();

        //region Outstandings
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", null);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", null);
        }

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        sunmiPrinterService.printText(context.getResources().getString(R.string.title_outstandings) + "\n", innerResultCallbcak);
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", innerResultCallbcak);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", innerResultCallbcak);
        }

        if (dueYearSummaries != null && !dueYearSummaries.isEmpty()) {
            for (BusinessTaxDueYearSummary taxDueYearSummary : dueYearSummaries) {
                if (taxDueYearSummary != null) {
                    //region Year
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.tax_year) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(taxDueYearSummary.getYear() + "\n", innerResultCallbcak);
                    //endregion

                    // region Product
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.product) + ": ", innerResultCallbcak);
                    if (taxDueYearSummary.getTaxSubType() == null || taxDueYearSummary.getTaxSubType().isEmpty())
                        sunmiPrinterService.printText(taxDueYearSummary.getProduct() + "\n", innerResultCallbcak);
                    else
                        sunmiPrinterService.printText(taxDueYearSummary.getTaxSubType() + "\n", innerResultCallbcak);
                    //endregion.

                    //region Invoice amount
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.invoice_amount) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getInvoiceAmount(), true) + "\n", innerResultCallbcak);
                    //endregion

                    // region Invoice due
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.invoice_due) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getInvoiceDue(), true) + "\n", innerResultCallbcak);
                    //endregion

                    // region Penalty amount
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.penalty_amount) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getPenaltyAmount(), true) + "\n", innerResultCallbcak);
                    //endregion

                    // region Penalty amount
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.penalty_due) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getPenaltyDue(), true) + "\n", innerResultCallbcak);
                    //endregion

                    if (paper == 1) {
                        sunmiPrinterService.printText("--------------------------------\n", null);
                    } else {
                        sunmiPrinterService.printText("------------------------------------------------\n", null);
                    }

                }
            }

        }
        //endregion

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(DateHelperKt.formatDisplayDateTimeInMillisecond(new Date()), String.valueOf(Typeface.ITALIC), 24, innerResultCallbcak);
        sunmiPrinterService.autoOutPaper(innerResultCallbcak);
        sunmiPrinterService.exitPrinterBuffer(true);

    }

    public void printWeaponSummaryContent(Context context, Weapon tax, List<BusinessTaxDueYearSummary> dueYearSummaries, String locale, List<OrgData> orgData) throws RemoteException {
        setLocale(context, locale);
        sunmiPrinterService.enterPrinterBuffer(true);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(context.getResources().getString(R.string.title_weapon_tax_summary) + "\n", String.valueOf(Typeface.BOLD), 36, innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region QR Code
        String url = GlobalKt.getURL(Constant.ReceiptType.INDIVIDUAL_TAX_SUMMARY);
        String qrCodeData = "";
        if (tax.getWeaponSycotaxID() != null) {
            qrCodeData = url.replace("@documentNo@", tax.getWeaponID().toString())
                    .replace("@sycoTaxID@", tax.getWeaponSycotaxID())
                    .replace("@taxSycoTaxID@", tax.getWeaponSycotaxID())
                    .replace("@LanguageCode@", MyApplication.getPrefHelper().getLanguage());
        }

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printQRCode(qrCodeData, 5, 0, innerResultCallbcak);
        //endregion
        sunmiPrinterService.printText("\n", innerResultCallbcak);
        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printText(orgData.get(0).getQRCodeNote()+""+orgData.get(0).getQRCodeNote2(), innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);
        sunmiPrinterService.printText("\n", innerResultCallbcak);

        //region Syco Tax id
        sunmiPrinterService.printText(context.getResources().getString(R.string.syco_tax_id) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getWeaponSycotaxID() + "\n", innerResultCallbcak);
        //endregion

        //region Registration date
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.registration_date) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(displayFormatDate(tax.getRegistrationDate()) + "\n", innerResultCallbcak);
        //endregion

        //region Serial no
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.serial_no) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getSerialNo() + "\n", innerResultCallbcak);
        //endregion

        //region Weapon type
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.weapon_type) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getWeaponType() + "\n", innerResultCallbcak);
        //endregion

        //region Make
        if (tax.getMake() != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.make) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(tax.getMake() + "\n", innerResultCallbcak);
        }
        //endregion

        //region Model
        if (tax.getModel() != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.model) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(tax.getModel() + "\n", innerResultCallbcak);
        }
        //endregion

        //region Purpose of possession
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.purpose_of_possession) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getPurposeOfPossession() + "\n", innerResultCallbcak);
        //endregion

        //region Description
        if(tax.getDescription()!=null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.description) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(tax.getDescription() + "\n", innerResultCallbcak);
        }
        //endregion

        //region Estimated tax
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.estimated_tax_amount) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(formatWithPrecision(tax.getEstimatedTax(), true) + "\n", innerResultCallbcak);
        //endregion

        // region Status
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.status) + ": ", innerResultCallbcak);

        String status = "";
        if (tax.getActive().equalsIgnoreCase("Y"))
            status = getString(R.string.active);
        else
            status = getString(R.string.inactive);

        sunmiPrinterService.printText(status + "\n", innerResultCallbcak);
        //endregion

        // region Owner
        sunmiPrinterService.setAlignment(0, innerResultCallbcak);
        sunmiPrinterService.printText(context.getResources().getString(R.string.owner_name) + ": ", innerResultCallbcak);
        sunmiPrinterService.printText(tax.getOwner() + "\n", innerResultCallbcak);
        //endregion

        //region Phone number
        if (tax.getAccountPhone() != null) {
            sunmiPrinterService.setAlignment(0, innerResultCallbcak);
            sunmiPrinterService.printText(context.getResources().getString(R.string.phone_number) + ": ", innerResultCallbcak);
            sunmiPrinterService.printText(tax.getAccountPhone() + "\n", innerResultCallbcak);
        }
        //endregion

        int paper = sunmiPrinterService.getPrinterPaper();

        //region Outstandings
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", null);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", null);
        }

        sunmiPrinterService.printText("\n", innerResultCallbcak);

        sunmiPrinterService.printText(context.getResources().getString(R.string.title_outstandings) + "\n", innerResultCallbcak);
        if (paper == 1) {
            sunmiPrinterService.printText("--------------------------------\n", innerResultCallbcak);
        } else {
            sunmiPrinterService.printText("------------------------------------------------\n", innerResultCallbcak);
        }

        if (dueYearSummaries != null && !dueYearSummaries.isEmpty()) {
            for (BusinessTaxDueYearSummary taxDueYearSummary : dueYearSummaries) {
                if (taxDueYearSummary != null) {
                    //region Year
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.tax_year) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(taxDueYearSummary.getYear() + "\n", innerResultCallbcak);
                    //endregion

                    // region Product
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.product) + ": ", innerResultCallbcak);
                    if (taxDueYearSummary.getTaxSubType() == null || taxDueYearSummary.getTaxSubType().isEmpty())
                        sunmiPrinterService.printText(taxDueYearSummary.getProduct() + "\n", innerResultCallbcak);
                    else
                        sunmiPrinterService.printText(taxDueYearSummary.getTaxSubType() + "\n", innerResultCallbcak);
                    //endregion.

                    //region Invoice amount
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.invoice_amount) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getInvoiceAmount(), true) + "\n", innerResultCallbcak);
                    //endregion

                    // region Invoice due
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.invoice_due) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getInvoiceDue(), true) + "\n", innerResultCallbcak);
                    //endregion

                    // region Penalty amount
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.penalty_amount) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getPenaltyAmount(), true) + "\n", innerResultCallbcak);
                    //endregion

                    // region Penalty amount
                    sunmiPrinterService.setAlignment(0, innerResultCallbcak);
                    sunmiPrinterService.printText(context.getResources().getString(R.string.penalty_due) + ": ", innerResultCallbcak);
                    sunmiPrinterService.printText(formatWithPrecision(taxDueYearSummary.getPenaltyDue(), true) + "\n", innerResultCallbcak);
                    //endregion

                    if (paper == 1) {
                        sunmiPrinterService.printText("--------------------------------\n", null);
                    } else {
                        sunmiPrinterService.printText("------------------------------------------------\n", null);
                    }

                }
            }

        }
        //endregion

        sunmiPrinterService.setAlignment(1, innerResultCallbcak);
        sunmiPrinterService.printTextWithFont(DateHelperKt.formatDisplayDateTimeInMillisecond(new Date()), String.valueOf(Typeface.ITALIC), 24, innerResultCallbcak);
        sunmiPrinterService.autoOutPaper(innerResultCallbcak);
        sunmiPrinterService.exitPrinterBuffer(true);
    }

    private void setLocale(Context context, String locale) {
        Locale locale1 = new Locale(locale);
        Locale.setDefault(locale1);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale1);
        configuration.setLayoutDirection(locale1);
        context.createConfigurationContext(configuration);
    }
}
