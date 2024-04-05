package com.shopping.jewellery.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart_tbl")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartId;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.REMOVE)
    private List<CartItem> cartItems = new ArrayList<>();

    private int cartItemQuantity;
    private double cartTotalPrice;

    private boolean isActive;

    public Cart() {
    }

    public Cart(int cartId, User user, List<CartItem> cartItems, int cartItemQuantity,
                double cartTotalPrice, boolean isActive) {
        this.cartId = cartId;
        this.user = user;
        this.cartItems = cartItems;
        this.cartItemQuantity = cartItemQuantity;
        this.cartTotalPrice = cartTotalPrice;
        this.isActive = isActive;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public int getCartItemQuantity() {
        return cartItemQuantity;
    }

    public void setCartItemQuantity(int cartItemQuantity) {
        this.cartItemQuantity = cartItemQuantity;
    }

    public double getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(double cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}