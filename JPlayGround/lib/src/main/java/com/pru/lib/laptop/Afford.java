package com.pru.lib.laptop;

public class Afford {

    String checkConfiguration(Laptop laptop) throws Exception {
        String result = null;
        if (laptop != null) {
            if (laptop.getPrice() > 70000) {
                throw new LaptopException("Price too high");
            }
            if (laptop.getDetails() != null && !laptop.getDetails().isEmpty()) {
                String[] specs = laptop.getDetails().split("/");
                if (specs.length == 3) {
                    int ram = convertToInt(specs[0]);
                    if (ram < 8) {
                        throw new LaptopException("Minimum 8 RAM required");
                    }
                    if ("HDD".equals(specs[2])) {
                        throw new LaptopException("SSD required");
                    }
                    result = "Can be purchased";
                }
            }
        }

        return result;
    }

    String purchaseLaptop(Laptop laptop) {
        String purchaseResult;
        try {
            checkConfiguration(laptop);
            purchaseResult = "Perfect configuration";
        } catch (LaptopException e) {
            purchaseResult = "Change configuration";
        } catch (Exception e) {
            purchaseResult = "other exception";
        }
        return purchaseResult;
    }

    private int convertToInt(String value) {
        int number = 0;
        try {
            number = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return number;
    }
}
