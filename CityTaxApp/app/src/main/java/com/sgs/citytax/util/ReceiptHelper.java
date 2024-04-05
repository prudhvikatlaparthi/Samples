package com.sgs.citytax.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sgs.citytax.R;
import com.sgs.citytax.api.SecurityContext;
import com.sgs.citytax.api.response.GetInvoiceTemplateResponse;
import com.sgs.citytax.base.MyApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Hashtable;

public class ReceiptHelper {

    public static String getProfessionalHTMLTemplateData(Context context, GetInvoiceTemplateResponse invoiceTemplateResponse, int documentNo) {
        SecurityContext securityContext = new SecurityContext();
        String content = "";
        String taxLines = "";
        String cust = "";
        String paymentLines = "";

        content = readTemplate(context, Constant.ReceiptType.TAX_RECEIPT);

        if (invoiceTemplateResponse != null) {
            content = content.replaceAll("@InvoiceSubtitle@", context.getResources().getString(R.string.taxation_year) + ": " + invoiceTemplateResponse.getTaxationYear() + "");
            if (invoiceTemplateResponse.getPrintCounts() != null && invoiceTemplateResponse.getPrintCounts() > 1)
                content = content.replaceAll("@DuplicateCopy@", "Duplicate - " + invoiceTemplateResponse.getPrintCounts());
            else
                content = content.replaceAll("@DuplicateCopy@", "");

            cust += setTotalLinesInch(context.getResources().getString(R.string.syco_tax_id), invoiceTemplateResponse.getSycoTaxID());
            cust += setTotalLinesInch(context.getResources().getString(R.string.business_name), invoiceTemplateResponse.getMarketName());
            //cust += setTotalLinesInch("Business Owner", invoiceTemplateResponse.getCustomerName());
            cust += setTotalLinesInch(context.getResources().getString(R.string.ifu_no), invoiceTemplateResponse.getIFU());
            cust += setTotalLinesInch(context.getResources().getString(R.string.title_address), invoiceTemplateResponse.getSector());
            cust += setTotalLinesInch(context.getResources().getString(R.string.street), invoiceTemplateResponse.getStreet());

            if (!TextUtils.isEmpty(content)) {
                content = content.replace("@CustomerDetails@", cust);
            }

            content = content.replaceAll("@SalesInvoiceNo@", String.valueOf(invoiceTemplateResponse.getTaxInvoiceID()));
            content = content.replaceAll("@DateSubtitle@", context.getResources().getString(R.string.date));
            content = content.replaceAll("@Date@", DateHelperKt.formatDateTimeInMillisecond(invoiceTemplateResponse.getTaxInvoiceDate()));

            taxLines += getProductRowsInch(invoiceTemplateResponse);
            content = content.replaceAll("@TotalLines@", taxLines);
            content = content.replaceAll("@NetAmount@", GlobalKt.formatWithPrecision(invoiceTemplateResponse.getReceivedAmount(), true));

            content = content.replaceAll("@CurrentDue@", GlobalKt.formatWithPrecision(invoiceTemplateResponse.getInvoiceDueAmount().toString(), true));

          /*  try {
                content = content.replaceAll("@AmountInWords@", invoiceTemplateResponse.getReceivedAmount().isEmpty() ? "" : GlobalKt.getAmountInWordsWithCurrency(NumberFormat.getInstance().parse(invoiceTemplateResponse.getReceivedAmount()).intValue(),context));
            } catch (ParseException e) {
                
            }*/

            paymentLines = setPaymentLinesStaticInch(invoiceTemplateResponse);

            content = content.replaceAll("@PaymentModes@", paymentLines);
            if (MyApplication.getPrefHelper().getQrCodeEnabled())
                content = mergeDocumentQRcode(String.valueOf(documentNo), content, Constant.ReceiptType.TAX_RECEIPT);

        } else {
            content = content.replaceAll("@InvoiceSubtitle@", "");
            content = content.replaceAll("@DuplicateCopy", "");
            cust += setTotalLinesInch("Business Name", "");
            //cust += setTotalLinesInch("Business Owner", "");
            cust += setTotalLinesInch("Sycotax ID", "");
            cust += setTotalLinesInch("Sector", "");
            cust += setTotalLinesInch("Street", "");
            cust += setTotalLinesInch("Avenue", "");
            if (!TextUtils.isEmpty(content)) {
                content = content.replace("@CustomerDetails@", cust);
            }

            content = content.replaceAll("@SalesInvoiceNo@", "");
            content = content.replaceAll("@DateSubtitle@", "");
            content = content.replaceAll("@Date@", "");


            taxLines += getProductRowsInch(invoiceTemplateResponse);
            content = content.replaceAll("@TotalLines@", taxLines);
            content = content.replaceAll("@NetAmount@", "");

            content = content.replaceAll("@CurrentDue@", "");

            content = content.replaceAll("@AmountInWords@", "");

            paymentLines = setPaymentLinesStaticInch(invoiceTemplateResponse);

            content = content.replaceAll("@PaymentModes@", paymentLines);
            content = content.replace("@QRCode@", "");

        }
        content = content.replaceAll("@TaxableElement@", context.getResources().getString(R.string.taxable_element));
        content = content.replaceAll("@Amount@", context.getResources().getString(R.string.amount));

        content = content.replaceAll("@InvoiceNoSubtitle@", context.getResources().getString(R.string.tax_receipt_no));
        content = content.replaceAll("@TotalAmountSubtitle@", context.getResources().getString(R.string.amount_collected));
        content = content.replaceAll("@CurrentDueSubtitle@", context.getResources().getString(R.string.total_due_amount));
        content = content.replaceAll("@AmountInWordsSubtitle@", context.getResources().getString(R.string.amount_in_words));
        content = content.replaceAll("@Payments@", "<br>Payments:");

        content = content.replaceAll("@CreatedDate@", DateHelperKt.formatDateTimeInMillisecond(new Date()));
        content = content.replaceAll("@PoweredBy@", "Powered by SGS City Tax TM | www.sgs.com");
        content = content.replaceAll("@CreatedBy@",
                (TextUtils.isEmpty(securityContext.getLoggedUserID())) ? "Generated By" : "Generated By" + "\n" + securityContext.getLoggedUserID());
        content = content.replaceAll("@GeneratedBy@",
                (TextUtils.isEmpty(securityContext.getLoggedUserID())) ? "Generated By" : "Generated By" + "\n" + securityContext.getLoggedUserID());
        //return loadStaticData(context, html.toString());
        return content;
    }


    public static String getTaxNoticeHtmlData(Context context, GetInvoiceTemplateResponse invoiceTemplateResponse, Constant.ReceiptType receiptType) {
        SecurityContext securityContext = new SecurityContext();
        String content = "";
        String taxLines = "";
        String cust = "";

        content = readTemplate(context, receiptType);

        if (invoiceTemplateResponse != null) {
            content = content.replaceAll("@InvoiceSubtitle@", context.getResources().getString(R.string.taxation_year) + ": " + invoiceTemplateResponse.getTaxationYear() + "");
            if (invoiceTemplateResponse.getPrintCounts() != null && invoiceTemplateResponse.getPrintCounts() > 1)
                content = content.replaceAll("@DuplicateCopy@", "Duplicate - " + invoiceTemplateResponse.getPrintCounts());
            else
                content = content.replaceAll("@DuplicateCopy@", "");
            cust += setTotalLinesInch(context.getResources().getString(R.string.syco_tax_id), invoiceTemplateResponse.getSycoTaxID());
            cust += setTotalLinesInch(context.getResources().getString(R.string.business_name), invoiceTemplateResponse.getMarketName());
            //cust += setTotalLinesInch("Business Owner", invoiceTemplateResponse.getCustomerName());
            cust += setTotalLinesInch(context.getResources().getString(R.string.ifu_no), invoiceTemplateResponse.getIFU());
            cust += setTotalLinesInch(context.getResources().getString(R.string.title_address), invoiceTemplateResponse.getSector());
            cust += setTotalLinesInch(context.getResources().getString(R.string.street), invoiceTemplateResponse.getStreet());
            /*cust += setTotalLinesInch(context, "Avenue", invoiceTemplateResponse.getZone());*/
            if (!TextUtils.isEmpty(content)) {
                content = content.replace("@CustomerDetails@", cust);
            }

            content = content.replaceAll("@SalesInvoiceNo@", String.valueOf(invoiceTemplateResponse.getTaxInvoiceID()));
            content = content.replaceAll("@DateSubtitle@", context.getResources().getString(R.string.date));
            content = content.replaceAll("@Date@", DateHelperKt.formatDateTimeInMillisecond(invoiceTemplateResponse.getTaxInvoiceDate()));

            taxLines += getTaxNoticeProducts(invoiceTemplateResponse);
            content = content.replaceAll("@TotalLines@", taxLines);
            content = content.replaceAll("@PreviousDue@", " " + GlobalKt.formatWithPrecision(invoiceTemplateResponse.getDueAmount().toString(), true));

            content = content.replaceAll("@TotalAmount@", " " + GlobalKt.formatWithPrecision(String.valueOf(invoiceTemplateResponse.getSubTotal() + invoiceTemplateResponse.getDueAmount()), true));

           /* try {
                content = content.replaceAll("@AmountInWords@", GlobalKt.getAmountInWordsWithCurrency(NumberFormat.getInstance().parse(String.valueOf(invoiceTemplateResponse.getSubTotal() + invoiceTemplateResponse.getDueAmount())).intValue(),context));
            } catch (ParseException e) {
                
            }*/

            if (MyApplication.getPrefHelper().getQrCodeEnabled())
                content = mergeDocumentQRcode(String.valueOf(invoiceTemplateResponse.getTaxInvoiceID()), content, Constant.ReceiptType.TAX_NOTICE);

        } else {
            content = content.replaceAll("@InvoiceSubtitle@", "");
            content = content.replaceAll("@DuplicateCopy", "");
            cust += setTotalLinesInch("Business Name", "");
            //cust += setTotalLinesInch("Business Owner", "");
            cust += setTotalLinesInch("Sycotax ID", "");
            cust += setTotalLinesInch("Address", "");
            cust += setTotalLinesInch("Street", "");
            cust += setTotalLinesInch("Avenue", "");
            if (!TextUtils.isEmpty(content)) {
                content = content.replace("@CustomerDetails@", cust);
            }

            content = content.replaceAll("@SalesInvoiceNo@", "");
            content = content.replaceAll("@DateSubtitle@", "");
            content = content.replaceAll("@Date@", "");


            taxLines += getProductRowsInch(invoiceTemplateResponse);
            content = content.replaceAll("@TotalLines@", taxLines);
            content = content.replaceAll("@PreviousDue@", "");

            content = content.replaceAll("@TotalAmount@", "");

            content = content.replaceAll("@AmountInWords@", "");

            content = content.replace("@QRCode@", "");

        }
        content = content.replaceAll("@TaxableElement@", context.getResources().getString(R.string.taxable_element));
        content = content.replaceAll("@Amount@", context.getResources().getString(R.string.amount));
        content = content.replaceAll("@TaxNoticeNo@", context.getResources().getString(R.string.title_tax_notice_no));
        content = content.replaceAll("@PreviousDueSubtitle@", context.getResources().getString(R.string.label_previous_due));
        content = content.replaceAll("@TotalAmountSubtitle@", context.getResources().getString(R.string.total_amount));
        content = content.replaceAll("@AmountInWordsSubtitle@", context.getResources().getString(R.string.amount_in_words));

        content = content.replaceAll("@CreatedDate@", DateHelperKt.formatDateTimeInMillisecond(new Date()));
        content = content.replaceAll("@PoweredBy@", "Powered by SGS City Tax TM | www.sgs.com");
        content = content.replaceAll("@CreatedBy@",
                (TextUtils.isEmpty(securityContext.getLoggedUserID())) ? "Generated By" : "Generated By" + "\n" + securityContext.getLoggedUserID());
        content = content.replaceAll("@GeneratedBy@",
                (TextUtils.isEmpty(securityContext.getLoggedUserID())) ? "Generated By" : "Generated By" + "\n" + securityContext.getLoggedUserID());
        return content;
    }

    private static String readTemplate(Context context, Constant.ReceiptType receiptType) {
        InputStream inputStream = null;
        BufferedReader bufferedReader;
        String templateString = "";

        try {
            switch (receiptType) {
                case TAX_RECEIPT:
                    inputStream = context.getAssets().open("35Inch/3.5TaxReceipt.html");
                    break;
                case TAX_NOTICE:
                    inputStream = context.getAssets().open("35Inch/3.5TaxNotice.html");
                    break;
                case TAX_NOTICE_HISTORY:
                    inputStream = context.getAssets().open("35Inch/3.5TaxNotice.html");
            }

            StringBuilder html = new StringBuilder();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                if (bufferedReader != null) {
                    String htmlLine;
                    while ((htmlLine = bufferedReader.readLine()) != null) {
                        html.append(htmlLine);
                    }
                    templateString = html.toString();
                }
                //templateString = ReceiptHelper.getProfessionalHTMLTemplateData(context, html, invoiceTemplateResponse);

                bufferedReader.close();
                inputStream.close();
            }
        } catch (IOException e) {
            LogHelper.writeLog(e,null);
        }
        return templateString;
    }

    private static String getRowInchLeft(String value) {
        return "<td style=\"text-align: left;padding-left: 5px;\">" + value + "</td>";
    }

    private static String getRowInchLeftB(String value) {
        return "<tr><td style=\"text-align: left;padding-left: 5px;\"><b>" + value + "</b></td></tr>";
    }

    private static String getRowInchRight(String value) {
        return "<td style=\"text-align: right;padding-left: 5px;\">" + value + "</td>";
    }


    private static String getProductRowsInch(GetInvoiceTemplateResponse invoiceTemplateResponse) {
        return "<tr>" + getRowInchLeft(invoiceTemplateResponse.getOccupancyName()) +
                getRowInchRight(GlobalKt.formatWithPrecision(invoiceTemplateResponse.getReceivedAmount(), true)) + "</tr>";
    }

    private static String getTaxNoticeProducts(GetInvoiceTemplateResponse invoiceTemplateResponse) {
        return "<tr>" + getRowInchLeft(invoiceTemplateResponse.getOccupancyName()) +
                getRowInchRight(GlobalKt.formatWithPrecision(invoiceTemplateResponse.getSubTotal().toString(), true)) + "</tr>";
    }


    private static String setPaymentLinesStaticInch(GetInvoiceTemplateResponse invoiceTemplateResponse) {
        return "<tr><td style='text-align: left; font-weight: bold;'>" + invoiceTemplateResponse.getPaymentMode() + "</td>" +
                "<td style='text-align: right;'>" + GlobalKt.formatWithPrecision(invoiceTemplateResponse.getReceivedAmount(), true) + "</td>" +
                "</tr>";
    }


    private static String setTotalLinesInch(String key, String val) {
        if (TextUtils.isEmpty(val) || val.equalsIgnoreCase("null"))
            return "";
        return "<tr><td style=\"text-align: left; font-weight: bold;\">" + key + ": " + val + "</td>" +
                "</tr>";
    }


    public static String mergeDocumentQRcode(String documentId, String templateContent, Constant.ReceiptType receiptType) {
        if (!TextUtils.isEmpty(templateContent)) {
            String url = "";
            try {
                switch (receiptType) {
                    case TAX_RECEIPT:
                        url = GlobalKt.getURL(Constant.ReceiptType.TAX_RECEIPT);
                        break;
                    case TAX_NOTICE:
                        url = GlobalKt.getURL(Constant.ReceiptType.TAX_NOTICE);
                        break;
                    case TAX_NOTICE_HISTORY:
                        url = GlobalKt.getURL(Constant.ReceiptType.TAX_NOTICE_HISTORY);
                }

                Bitmap qrCodeBitmap = CreateCode((url.replaceAll("@documentNo@", documentId)), BarcodeFormat.QR_CODE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] image = stream.toByteArray();
                String qr = String.format("data:image/jpeg;base64,%s", Base64.encodeToString(image, Base64.DEFAULT));
                templateContent = templateContent.replaceAll("@QRCode@", qr);
            } catch (WriterException e) {
                LogHelper.writeLog(e,null);
            }
        } else {
            templateContent = templateContent.replaceAll("@QRCode@", "");

        }
        return templateContent;

    }

    static Bitmap CreateCode(String str, BarcodeFormat type) throws WriterException {
        Hashtable<EncodeHintType, String> mHashtable = new Hashtable<>();
        mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str, type, 256, 256, mHashtable);
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

}