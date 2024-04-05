package com.shopping.jewellery.dto;

import java.util.ArrayList;
import java.util.List;

public class CartDTO {

    private int cartId;
    private int userId;
    private List<Integer> productIds = new ArrayList<>();

    private int quantity;

    public CartDTO() {
    }

    public CartDTO(int cartId, int userId, List<Integer> productIds, int quantity) {
        this.cartId = cartId;
        this.userId = userId;
        this.productIds = productIds;
        this.quantity = quantity;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Integer> productIds) {
        this.productIds = productIds;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
