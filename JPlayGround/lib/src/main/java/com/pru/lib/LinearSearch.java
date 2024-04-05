package com.pru.lib;

import java.util.Scanner;

public class LinearSearch {
    public static void main(String arg[]) {
        System.out.println("Enter Array size");
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];
        System.out.println("Enter Array elements");
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }
        System.out.println("Enter search element");
        int ele = sc.nextInt();
        for (int i = 0; i < n; i++) {
            if (arr[i] == ele) {
                System.out.println("Element found at " + i);
            }
        }

    }
}

