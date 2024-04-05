package com.sgs.citytax.util;

import android.text.TextUtils;

import com.sgs.citytax.R;
import com.sgs.citytax.api.SecurityContext;
import com.sgs.citytax.api.payload.Organization;
import com.sgs.citytax.api.response.CommissionHistory;
import com.sgs.citytax.api.response.GetInvoiceTemplateResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.sgs.citytax.base.MyApplication.getContext;

public class TxtTemplateUtils {

    public static String getTemplateContent(GetInvoiceTemplateResponse invoiceTemplateResponse, int printerCount, Boolean isTaxNotice) {
        StringBuilder stringBuilder = new StringBuilder();

        if (invoiceTemplateResponse.getPrintCounts() != null && invoiceTemplateResponse.getPrintCounts() > 1)
            stringBuilder.append(getTextAlignCenter("Duplicate - " + invoiceTemplateResponse.getPrintCounts() + "", printerCount));
        stringBuilder.append("\n");

        stringBuilder.append(getTextAlignCenter(getContext().getResources().getString(R.string.taxation_year) + ": " + invoiceTemplateResponse.getTaxationYear() + "", printerCount));
        stringBuilder.append("\n");
        stringBuilder.append(getTextLeftAlign((isTaxNotice ? getContext().getResources().getString(R.string.tax_notice_no) : getContext().getResources().getString(R.string.tax_receipt_no)) + ": " + invoiceTemplateResponse.getTaxInvoiceID(), printerCount).toString()).append("\n"); //SalesInvoiceNo
        stringBuilder.append(getTextLeftAlign(getContext().getString(R.string.date) + ": " + DateHelperKt.formatDateTimeInMillisecond(invoiceTemplateResponse.getTaxInvoiceDate()), printerCount)).append("\n");
        fillInCustomerDetails(invoiceTemplateResponse, printerCount, stringBuilder);
        stringBuilder.append("\n");
        stringBuilder.append(getItemHeaderAlignment(printerCount));
        stringBuilder.append(generateDotLineString(printerCount, "-")).append("\n");
        stringBuilder.append(productItemDetailsI(printerCount, invoiceTemplateResponse, isTaxNotice));
        stringBuilder.append(generateDotLineString(printerCount, "-")).append("\n");
        if (!isTaxNotice) {
            stringBuilder.append(getTextTotalAlignRight(getContext().getResources().getString(R.string.amount_collected) + " : @); " + GlobalKt.formatWithPrecision(invoiceTemplateResponse.getReceivedAmount(), true), printerCount));
            stringBuilder.append(getTextTotalAlignRight(getContext().getResources().getString(R.string.total_due_amount) + " : @); " + GlobalKt.formatWithPrecision(invoiceTemplateResponse.getInvoiceDueAmount().toString(), true), printerCount));
        } else
            stringBuilder.append(getTextTotalAlignRight(getContext().getResources().getString(R.string.label_previous_due) + " :@);" + GlobalKt.formatWithPrecision(invoiceTemplateResponse.getDueAmount().toString(), true), printerCount));
        stringBuilder.append("\n");
        if (isTaxNotice) {
            stringBuilder.append(getTextTotalAlignRight(getContext().getResources().getString(R.string.total_amount) + " :@);" + GlobalKt.formatWithPrecision(String.valueOf(invoiceTemplateResponse.getSubTotal() + invoiceTemplateResponse.getDueAmount()), true), printerCount));
            stringBuilder.append("\n");
        }
        stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.amount_in_words) + ": ", printerCount));
       /* try {
            if (!isTaxNotice)
                stringBuilder.append(getTextLeftAlign(GlobalKt.getAmountInWordsWithCurrency(NumberFormat.getInstance().parse(invoiceTemplateResponse.getReceivedAmount()).intValue(),getContext()), printerCount)).append("\n");
            else
                stringBuilder.append(getTextLeftAlign(GlobalKt.getAmountInWordsWithCurrency(NumberFormat.getInstance().parse(String.valueOf(invoiceTemplateResponse.getSubTotal() + invoiceTemplateResponse.getDueAmount())).intValue(),getContext()), printerCount)).append("\n");
        } catch (ParseException e) {
            LogHelper.writeLog(exception = e);
        }*/
        stringBuilder.append("\n");
        if (!isTaxNotice) {
            stringBuilder.append("Payments : ").append("\n");
            stringBuilder.append(getTextAlignLeftRight(invoiceTemplateResponse.getPaymentMode() + "@);" + GlobalKt.formatWithPrecision(invoiceTemplateResponse.getReceivedAmount().toString(), true), printerCount)).append("\n");
        }
        stringBuilder.append(getGeneratedBy(printerCount, invoiceTemplateResponse)).append("\n\n");
        return stringBuilder.toString();

    }

    public static String getSummaryTemplateContent(Organization organization, String businessOwner, BigDecimal estimatedTax, int printerCount) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getTextAlignCenter(getContext().getResources().getString(R.string.title_business_summary), printerCount));
        stringBuilder.append(generateDotLineString(printerCount, "-")).append("\n");

        stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.syco_tax_id) + ": " + organization.getSycotaxID() + "", printerCount));
        stringBuilder.append("\n");
        stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.business_name) + ": " + organization.getOrganization() + "", printerCount));
        stringBuilder.append("\n");

        if (businessOwner != null && !TextUtils.isEmpty(businessOwner)) {
            stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.title_business_owner) + ": " + businessOwner + "", printerCount));
            stringBuilder.append("\n");
        }
        if (organization.getPhone() != null && !organization.getPhone().isEmpty()) {
            stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.business_ph_no) + ": " + organization.getPhone() + "", printerCount));
            stringBuilder.append("\n");
        }
        if (organization.getEmail() != null && !organization.getEmail().isEmpty()) {
            stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.business_email) + ": " + organization.getEmail() + "", printerCount));
            stringBuilder.append("\n");
        }
        if (estimatedTax.compareTo(BigDecimal.ZERO) > 0) {
            stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.estimated_tax) + ": " + estimatedTax + "", printerCount));
            stringBuilder.append("\n");
        }

        stringBuilder.append(getTextAlignCenter(DateHelperKt.formatDateTimeInMillisecond(new Date()), printerCount));

        return stringBuilder.toString();
    }

    public static String getPayoutContent(CommissionHistory commissionHistory, int printerCount) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getTextAlignCenter("Agent Commission Preview", printerCount));
        stringBuilder.append(generateDotLineString(printerCount, "-")).append("\n");

        stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.voucher_no) + ": " + commissionHistory.getReferenceNo() + "", printerCount));
        stringBuilder.append("\n");
        stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.agent_name) + ": " + commissionHistory.getAccountName() + "", printerCount));
        stringBuilder.append("\n");
        stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.amount) + ": " + GlobalKt.formatWithPrecision(commissionHistory.getNetPayable(), true) + "", printerCount));
        stringBuilder.append("\n");
        stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.approved_by) + ": " + commissionHistory.getApproverName() + "", printerCount));
        stringBuilder.append("\n");

        stringBuilder.append(getTextLeftAlign(getContext().getResources().getString(R.string.status) + ": " + commissionHistory.getStatus() + "", printerCount));
        stringBuilder.append("\n,\n");
        stringBuilder.append(getTextAlignCenter(DateHelperKt.formatDateTimeInMillisecond(new Date()), printerCount));

        return stringBuilder.toString();
    }

    private static StringBuilder getTextAlignCenter(String text, int totalCount) {
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(text))
            return builder;

        int equalSpace = (totalCount - text.length()) / 2;
        builder.append(appendPrefixSuffixText(text.trim(), equalSpace));

        return builder;
    }

    private static StringBuilder getTextLeftAlign(String text, int totalCount) {
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(text))
            return builder;
        if (text.length() >= totalCount) {
            StringBuilder c = new StringBuilder();
            int pos = 0;
            boolean isAdd = false;
            for (int i = 0; i < text.length(); i++) {
                pos++;
                if (pos <= totalCount) {
                    c.append(text.charAt(i));
                    isAdd = true;
                } else {
                    isAdd = false;
                    builder.append(c);
                    builder.append("\n");
                    c = new StringBuilder();
                    c.append(text.charAt(i));
                    pos = 0;
                }
            }
            if (isAdd) {
                builder.append(c);
            }
        } else {
            builder.append(text);
        }
        return builder;
    }

    private static StringBuilder emailAlignCenter(String text, int totalCount) {
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(text))
            return builder;
        String[] split = text.split(" ");
        for (String mText : split) {
            int equalSpace = (totalCount - mText.trim().length()) / 2;
            builder.append(appendEmpty(equalSpace)).append(mText).append(appendEmpty(equalSpace)).append("\n");
        }
        return builder;
    }

    private static StringBuilder appendEmpty(int spaceCount) {
        StringBuilder builder = new StringBuilder();
        String empty = " ";
        int i = 0;
        while (i < spaceCount) {
            builder.append(empty);
            i++;
        }
        return builder;
    }

    private static StringBuilder appendPrefixSuffixText(String text, int equalSpace) {
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(text))
            return builder;
        builder.append(appendEmpty(equalSpace)).append(text).append(appendEmpty(equalSpace)).append("\n");
        return builder;
    }


    private static void fillInCustomerDetails(GetInvoiceTemplateResponse invoiceTemplateResponse, int printerCount,
                                              StringBuilder builder) {
        if (invoiceTemplateResponse != null) {

            if (!TextUtils.isEmpty(invoiceTemplateResponse.getSycoTaxID())) {
                builder.append(getTextLeftAlign(getContext().getResources().getString(R.string.syco_tax_id) + ": " + invoiceTemplateResponse.getSycoTaxID(), printerCount)).append("\n");
            }

            if (!TextUtils.isEmpty(invoiceTemplateResponse.getMarketName())) {
                builder.append(getTextLeftAlign(getContext().getResources().getString(R.string.business_name) + ": " + invoiceTemplateResponse.getMarketName(), printerCount)).append("\n");
            }

            /*if (!TextUtils.isEmpty(invoiceTemplateResponse.getCustomerName())) {
                builder.append(getTextLeftAlign("Business Owner" + ": " + invoiceTemplateResponse.getCustomerName(), printerCount)).append("\n");
            }*/

            if (!TextUtils.isEmpty(invoiceTemplateResponse.getZone())) {
                builder.append(getTextLeftAlign(getContext().getResources().getString(R.string.ifu_no) + ": " + invoiceTemplateResponse.getIFU(), printerCount)).append("\n");
            }

            if (!TextUtils.isEmpty(invoiceTemplateResponse.getSector())) {
                builder.append(getTextLeftAlign(getContext().getResources().getString(R.string.title_address) + ": " + invoiceTemplateResponse.getSector(), printerCount)).append("\n");
            }

            if (!TextUtils.isEmpty(invoiceTemplateResponse.getStreet())) {
                builder.append(getTextLeftAlign(getContext().getResources().getString(R.string.street) + ": " + invoiceTemplateResponse.getStreet(), printerCount).append("\n"));
            }

        }
    }

    private static String generateDotLineString(int paperSize, String dotType) {
        String lineString = "";
        lineString = String.format("" + "%1$" + (paperSize - lineString.length()) + "s", "");
        lineString = lineString.replaceAll(" ", dotType);
        return lineString;
    }

    private static StringBuilder getItemHeaderAlignment(int totalLength) {
        StringBuilder builder = new StringBuilder();
        int partition = 2;

        int equalParts = totalLength / partition;

        String itemTitle = "Taxable Element";


        builder.append((itemTitle));
        builder.append(appendEmpty(equalParts - "amount".length())).append("Amount").append("\n");
        return builder;
    }

    private static StringBuilder prefixSuffix(String text, int length) {
        StringBuilder builder = new StringBuilder();
        if (text.trim().length() > length) {
            String temp = text.substring(0, length);
            String temp2 = text.substring(temp.length());
            builder.append(temp).append("\n").append(appendEmpty(length - temp2.length())).append(temp2);
        } else {
            builder.append(appendEmpty(length - text.length())).append(text);
        }
        return builder;

    }

    private static StringBuilder getAlignLeft(String text) {
        return new StringBuilder().append(text).append("\n");
    }

    private static StringBuilder getTextAlignLeftRight(String text, int totalCount) {
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(text))
            return builder;
        String temptxt = "";
        String[] split = text.split("@\\);");
        int leftLength = split[0].length();
        int rightLength = split[1].length();
        int spaceLeftLength = totalCount - (leftLength + rightLength);
        temptxt = split[0] + appendEmpty(spaceLeftLength).toString() + split[1];
        int equalSpace = (totalCount - temptxt.length());
        builder.append(appendEmpty(equalSpace));
        builder.append(temptxt);
        builder.append("\n");
        return builder;
    }


    private static StringBuilder productItemDetailsI(int printerCount,
                                                     GetInvoiceTemplateResponse invoiceTemplateResponse, Boolean isTaxNotice) {
        StringBuilder builder = new StringBuilder();

        if (invoiceTemplateResponse != null) {

            String taxableElement = invoiceTemplateResponse.getOccupancyName();
            String amount;
            if (!isTaxNotice)
                amount = GlobalKt.formatWithPrecision(invoiceTemplateResponse.getReceivedAmount(), true);
            else
                amount = GlobalKt.formatWithPrecision(invoiceTemplateResponse.getSubTotal() != null ? invoiceTemplateResponse.getSubTotal() : 0.0, true);
            List<HashMap<String, Object>> hashMapList = getItemProductLine(taxableElement, amount, printerCount);
            int equalParts = printerCount / 2;
            int itemParts = 0;
            if (equalParts * 2 == printerCount) {
                itemParts = equalParts;
            } else {
                itemParts = equalParts + (printerCount - (equalParts * 2));
            }

            getItemLine(hashMapList, builder, itemParts, equalParts);

        }
        return builder;
    }

    private static List<HashMap<String, Object>> getItemProductLine(String taxableElement, String amount, int totalLength) {
        List<HashMap<String, Object>> hashMapList = new ArrayList<>();
        List<String> itemList = new ArrayList<>();
        List<String> unitList = new ArrayList<>();
        int partition = 2;


        taxableElement = taxableElement + " ";
        String unitPrice = " " + amount;


        int equalParts = totalLength / partition;
        int itemParts = 2;
        if (equalParts * partition == totalLength) {
            itemParts = equalParts;
        } else {
            itemParts = equalParts + (totalLength - (equalParts * partition));
        }
        int taxableLength = taxableElement.length();
        int amountLength = unitPrice.length();

        if (taxableLength >= itemParts) {
            StringBuilder c = new StringBuilder();
            int pos = 0;
            boolean isAdd = false;
            char[] charArr = taxableElement.toCharArray();
            for (int i = 0; i < taxableLength; i++) {
                pos++;
                if (pos < itemParts) {
                    c.append(charArr[i]);
                    isAdd = true;
                } else {
                    c.append(charArr[i]);
                    isAdd = false;
                    itemList.add(c.toString());
                    c = new StringBuilder();
                    pos = 0;
                }
            }
            if (isAdd) {
                itemList.add(c.toString());
            }
        } else {
            itemList.add(taxableElement);
        }

        if (amountLength >= equalParts) {
            StringBuilder c = new StringBuilder();
            int pos = 0;
            boolean isAdd = false;
            char[] charArr = unitPrice.toCharArray();
            for (int i = 0; i < amountLength; i++) {
                pos++;
                if (pos < equalParts) {
                    c.append(charArr[i]);
                    isAdd = true;
                } else {
                    c.append(charArr[i]);
                    isAdd = false;
                    unitList.add(c.toString());
                    c = new StringBuilder();
                    pos = 0;
                }
            }
            if (isAdd) {
                unitList.add(c.toString());
            }
        } else {
            unitList.add(unitPrice);
        }


        /*  combine each items into line group */
        List<ProductTxtReceiptModel> txtReceiptModels = new ArrayList<>();
        ProductTxtReceiptModel model = null;
        for (int i = 0; i < itemList.size(); i++) {
            model = new ProductTxtReceiptModel();
            model.item = itemList.get(i);
            txtReceiptModels.add(model);
        }

        for (int i = 0; i < unitList.size(); i++) {
            model = new ProductTxtReceiptModel();
            if (txtReceiptModels.size() - 1 >= i) {
                txtReceiptModels.get(i).unitPrice = unitList.get(i);
            } else {
                model.unitPrice = unitList.get(i);
                txtReceiptModels.add(model);
            }
        }
        for (int i = 0; i < txtReceiptModels.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            model = txtReceiptModels.get(i);
            if (!TextUtils.isEmpty(model.item))
                map.put("item_payout_details", model.item);
            if (!TextUtils.isEmpty(model.unitPrice))
                map.put("unit", model.unitPrice);

            hashMapList.add(map);
        }

        return hashMapList;

    }

    private static void getItemLine(List<HashMap<String, Object>> hashMapList, StringBuilder builder, int itemParts, int equalParts) {
        int i = 0;
        int count = 0;
        for (HashMap<String, Object> objectHashMap : hashMapList) {
            if (objectHashMap.containsKey("item_payout_details"))
                if (!objectHashMap.get("item_payout_details").equals("@"))
                    builder.append(objectHashMap.get("item_payout_details")).append(appendEmpty(itemParts - String.valueOf(objectHashMap.get("item_payout_details")).length()));
                else
                    builder.append(appendEmpty(itemParts));
            if (objectHashMap.containsKey("unit"))
                builder.append(appendEmpty(count).append(prefixSuffix(String.valueOf(objectHashMap.get("unit")), equalParts)));
            else
                builder.append(appendEmpty(equalParts));
            builder.append("\n");
            i++;
        }
    }

    private static StringBuilder getGeneratedBy(int printerCount,
                                                GetInvoiceTemplateResponse invoiceTemplateResponse) {
        SecurityContext securityContext = new SecurityContext();
        return new StringBuilder(String.valueOf(getTextAlignCenter(getContext().getResources().getString(R.string.generated_by), printerCount)) +
                getTextAlignCenter(securityContext.getLoggedUserID(), printerCount - 1) +
                getTextAlignCenter(DateHelperKt.formatDateTimeInMillisecond(new Date()), printerCount) +
                getTextAlignCenter("Powered by" + " " + "SGS City Tax TM | www.sgs.com", printerCount));
    }

    private static StringBuilder getTextTotalAlignRight(String text, int totalCount) {
        StringBuilder builder = new StringBuilder();

        if (TextUtils.isEmpty(text))
            return builder;
        String temptxt = "";
        String[] split = text.split("@\\);");
        int amtLength = split[1].trim().length();
        int spaceLength = 5 - amtLength;
        temptxt = split[0] + appendEmpty(spaceLength).toString() + split[1];
        int equalSpace = (totalCount - temptxt.length());
        builder.append(appendEmpty(equalSpace));
        builder.append(temptxt);
        builder.append("\n");
        return builder;
    }

    public static class ProductTxtReceiptModel {
        String item, unitPrice;
    }
}
