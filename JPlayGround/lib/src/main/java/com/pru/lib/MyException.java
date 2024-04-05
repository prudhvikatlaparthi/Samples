package com.pru.lib;

public class MyException {
    public static void main(String[] arg) {
        try {
            throw new ArithmeticException();
        } catch (ArithmeticException e) {
            System.out.println("ArithmeticException occurred");
        } catch (Exception e) {
            System.out.println("Exception occurred");
        } finally {
            System.out.println("Finally");
        }
        System.out.println("Out");
    }
}
// initial
// Exception / by zero
// Finally
