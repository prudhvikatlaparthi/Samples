package com.shopping.jewellery.dto;

public class CartItemDTO {

    private Long id;
    private int quantity;

    private int productId;

    private String productName;

    private double price;

    private boolean all;

    private int productActualQty;

    public CartItemDTO() {
    }

    public CartItemDTO(Long id, int quantity, int productId, double price, String productName, int productActualQty) {
        this.id = id;
        this.quantity = quantity;
        this.productId = productId;
        this.price = price;
        this.productName = productName;
        this.productActualQty = productActualQty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public int getProductActualQty() {
        return productActualQty;
    }

    public void setProductActualQty(int productActualQty) {
        this.productActualQty = productActualQty;
    }
}
