package com.pru.lib.laptop;

public class Test {
    public static void main(String[] args) {
        try {
            Laptop laptop = new Laptop("8/512/SDD", 60000);
            Afford af = new Afford();
            String s = af.checkConfiguration(laptop);
            String t = af.purchaseLaptop(laptop);
            System.out.println(s.toLowerCase()); // can be purchased
            System.out.println(t.toLowerCase()); // perfect configuration
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
