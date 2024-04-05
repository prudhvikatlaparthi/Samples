package com.pru.lib;

public class Test {
    public static void main(String[] args) {
        String str1 = new String("hello"); //101
        String str2 = "hello"; // 103
        if (str1 == str2) System.out.println("Equals");
        else System.out.println("Not Equal");
    }
}

class Demo1 {

    public int sum(int a, int b) {
        return a + b;
    }
}

class SubDemo1 extends Demo1 {
    @Override
    public int sum(int a, int b) {
        return super.sum(a, b);
    }
}