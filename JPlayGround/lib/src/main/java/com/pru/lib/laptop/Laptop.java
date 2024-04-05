package com.pru.lib.laptop;

public class Laptop {
    private final String details;
    private final int price;

    public Laptop(String details, int price) {
        this.details = details;
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public int getPrice() {
        return price;
    }
}
