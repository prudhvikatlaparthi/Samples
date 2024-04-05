package com.pru.lib;

public class Demo extends Exception {
}

class SubDemo extends Demo {
    public static void main(String[] args) {
        try {
            int n = Integer.parseInt("sdfsd");
        } catch (NumberFormatException t) {
            System.out.println("Exception");
        } finally {
            System.out.println("Finally");
        }
    }
}

class Main {
    float result;
    public static void main(String[] args) {

    }
}
