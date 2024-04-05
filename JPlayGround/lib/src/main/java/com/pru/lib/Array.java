package com.pru.lib;

import java.util.Scanner;

public class Array {

    static int linearSearch(int[] arr, int ele) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == ele) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String args[]) {

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter no of elements Array ");
        int n = sc.nextInt();
        int[] arr = new int[n];

        System.out.println("Enter Array elements");

        for (int i = 0; i < n; i++)
            arr[i] = sc.nextInt();

        System.out.println("Enter searching element");
        int ele = sc.nextInt();

        int res = linearSearch(arr, ele);
        if (res == -1) {
            System.out.println("Element not found");
        } else {
            System.out.println("Element found at " + res);
        }
    }
}