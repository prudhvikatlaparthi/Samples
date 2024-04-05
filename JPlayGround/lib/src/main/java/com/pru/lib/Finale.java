package com.pru.lib;

public class Finale {
    private final Person person;

    Finale(Person person) {
        this.person = person;
    }

    public String checkPerson() {
        String result = "Failed";
        if (person != null && person.getName() != null && person.getName().trim().length() > 0 && person.getGame() != null && person.getGame().trim().length() > 0) {
            String[] nWords = person.getName().trim().split("\\s+");
            String[] gWords = person.getGame().trim().split("\\s+");
            if (nWords.length >= 2 && gWords.length == 3) {
                int wins = ConvertToInt(gWords[0]);
                int loss = ConvertToInt(gWords[1]);
                if (wins > 0 && loss > 0 && (wins + loss) <= 9) {
                    result = "Person valid";
                }
            }

        }
        return result;
    }

    public int numberOfTrophies(){
        int tropies = 0;
        // input and description is not unclear
        return tropies;
    }

    private int ConvertToInt(String value) {
        int number = 0;
        try {
            number = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return number;
    }
}
