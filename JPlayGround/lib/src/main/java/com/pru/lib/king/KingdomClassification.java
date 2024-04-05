package com.pru.lib.king;

import java.util.ArrayList;
import java.util.List;

public class KingdomClassification {

    KingdomClassification() {
    }

    public List<String> getKingdom(List<Kingdom> list) {
        ArrayList<String> resultList = new ArrayList<String>();
        if (list != null && !list.isEmpty()) {
            for (Kingdom kingdom : list) {
                resultList.add(kingdom.getLife());
            }
        }
        return resultList;
    }

    public Kingdom findNameWithValidity(List<Kingdom> list, String name, int lifeSpan) {
        Kingdom result = null;
        if (list != null && !list.isEmpty()) {
            for (Kingdom kingdom : list) {
                if (name != null
                        && !name.trim().isEmpty()
                        && (name.equals(kingdom.getLife()) || name.equals(kingdom.getNonLife()))
                        && lifeSpan == kingdom.getLifeSpan()) {
                    result = kingdom;
                    break;
                }
            }
        }
        return result;
    }
}
