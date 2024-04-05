package com.pru.lib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MyClass {
    public static void main(String[] args) {
        String name = "Jahnavi";
        LinkedHashMap<String, Integer> map = new LinkedHashMap();
        String[] n = name.split( "");
        for (String c : n) {
            if (map.containsKey(c)){
                int prev = map.get(c);
                map.put(c, prev +1);
            }else {
                map.put(c,1);
            }
        }
        for (Map.Entry<String, Integer> m: map.entrySet()) {
            System.out.println(m.getKey() +" "+ m.getValue());
        }

    }
}
