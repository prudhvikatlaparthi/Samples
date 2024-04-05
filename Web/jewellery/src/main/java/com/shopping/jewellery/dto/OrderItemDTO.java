package com.shopping.jewellery.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopping.jewellery.entity.Order;

public class OrderItemDTO {
    private Long id;

    private int productId;

    private String productName;

    private int quantity;

    private double price;

    @JsonIgnore
    private Order order;

    public OrderItemDTO() {

    }

    public OrderItemDTO(Long id, int productId, int quantity, double price, Order order) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
