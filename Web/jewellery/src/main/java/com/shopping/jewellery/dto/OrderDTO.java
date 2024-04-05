package com.shopping.jewellery.dto;

import com.shopping.jewellery.utils.OrderStatus;
import com.shopping.jewellery.utils.PaymentModeEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {


    private int orderId;

    private LocalDateTime date;
    private OrderStatus status;

    private int userId;
    private List<OrderItemDTO> orderItems = new ArrayList<>();

    private int totalQuantity;
    private double totalPrice;

    private PaymentModeEnum paymentMode;


    public OrderDTO() {

    }

    public OrderDTO(int orderId, LocalDateTime date, OrderStatus status, int userId, List<OrderItemDTO> orderItems, int totalQuantity, double totalPrice) {
        this.orderId = orderId;
        this.date = date;
        this.status = status;
        this.userId = userId;
        this.orderItems = orderItems;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentModeEnum paymentMode) {
        this.paymentMode = paymentMode;
    }
}