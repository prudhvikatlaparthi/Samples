package com.shopping.jewellery.entity;

import com.shopping.jewellery.utils.OrderStatus;
import com.shopping.jewellery.utils.PaymentModeEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_tbl")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name="order_status")
    private OrderStatus status;

    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    private int totalQuantity;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name="payment_mode")
    private PaymentModeEnum paymentMode;

    public Order() {
    }

    public Order(int orderId, LocalDateTime date, OrderStatus status, User user,
                 List<OrderItem> orderItems, int totalQuantity, double totalPrice) {
        this.orderId = orderId;
        this.date = date;
        this.status = status;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
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