package com.pru.lib.country;

import java.util.HashMap;
import java.util.Map;

public class Country {
    private final HashMap<String, String> countryMap = new HashMap<>();

    public Country() {
    }

    HashMap<String, String> addCountryCapital(String countryName, String capital) {
        countryMap.put(countryName, capital);
        return countryMap;
    }

    String getCapital(String countryName) {
        return countryMap.get(countryName);
    }

    String getCountry(String capitalName) {
        String countryName = null;
        for (Map.Entry<String, String> entry : countryMap.entrySet()) {
            if (entry.getValue().equals(capitalName)) {
                countryName = entry.getKey();
                break;
            }
        }
        return countryName;
    }


    HashMap<String, String> swapKeyValue() {
        HashMap<String, String> resultMap = new HashMap<>();
        for (Map.Entry<String, String> entry : countryMap.entrySet()) {
            resultMap.put(entry.getValue(), entry.getKey());
        }
        return resultMap;
    }

}
