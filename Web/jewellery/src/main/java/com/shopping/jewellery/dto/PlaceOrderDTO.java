package com.shopping.jewellery.dto;

import com.shopping.jewellery.utils.PaymentModeEnum;

public class PlaceOrderDTO {

    private int userId;
    private String paymentMode;

    public PlaceOrderDTO(int userId, String paymentMode) {
        this.userId = userId;
        this.paymentMode = paymentMode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}