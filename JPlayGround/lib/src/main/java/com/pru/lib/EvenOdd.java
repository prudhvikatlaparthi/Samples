package com.pru.lib;

import java.util.*;
public class EvenOdd {
    public static void main(String arg[]){
        System.out.println("Enter a number ");
        Scanner sc = new Scanner(System.in);
        int num = sc.nextInt();
        int rem = num % 2;
        if (rem == 0){
            System.out.println("This is Even");
        }else {
            System.out.println("This is Odd");
        }
    }
}

//4 -> EVEN
//3 -> ODD
