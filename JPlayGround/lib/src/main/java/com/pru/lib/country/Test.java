package com.pru.lib.country;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        Country country = new Country();
        country.addCountryCapital("India", "Delhi");
        country.addCountryCapital("United Kingdom", "London");
        String s = country.getCapital("United Kingdom");
        String t = country.getCountry("Delhi");
        HashMap<String, String> swapped = country.swapKeyValue();
        System.out.println(s); // London
        System.out.println(t); // India
        System.out.println(swapped); // {Delhi=India, London=United Kingdom}
    }
}
