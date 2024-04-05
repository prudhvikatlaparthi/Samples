package com.pru.lib.king;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        KingdomClassification k = new KingdomClassification();
        List<Kingdom> list = new ArrayList<>();
        list.add(new Kingdom("Protista", "Regnum", 3));
        list.add(new Kingdom("Plantae", "Lapideum", 5));

        List<String> names = k.getKingdom(list);
        System.out.println(names); // [Protista, Plantae]

        Kingdom kingdom = k.findNameWithValidity(list, "Lapideum", 5);
        System.out.println(kingdom); // Kingdom{life='Plantae', nonLife='Lapideum', lifeSpan=5}

        Car car = new Car();
        /*car.cookVegetableDal("Tomato",30);
        car.cookVegetableDal("Mango",15);
        car.cookVegetableDal("Brinjal",20);*/
        car.cookVegetableDal("Mango",15);
        car.cookNonVegCurry("chicken");
        car.cookNonVegCurry("Mutton");
    }
}

class Car {
    String carName;

    void display() {
        System.out.println("This is a display method");
    }

    int age() {
        return 0;
    }

    int s1() {
        int random = Math.round(100);
        return random;
    }

    void s2() {

    }

    void s() {
       int random =  s1();
        s2();
        System.out.println();
    }

    void cookNonVegCurry(String meat){
        prepareInitialStepsForCooking();
        System.out.println("put onions in vessel");
        System.out.println("put "+meat+" in vessel");
        System.out.println("add extra spieces");
        finalStep();
    }

    void  cookVegetableDal(String vegetable,int timeForDalCook){
        prepareInitialStepsForCooking();
        System.out.println("3 cook for " + timeForDalCook + "min");
        System.out.println("4 add salt 3 spoons");
        System.out.println("5 put "+vegetable);
        System.out.println("6 thalinpu");
        finalStep();
    }

    void finalStep(){
        System.out.println("7 serve");
    }

    void prepareInitialStepsForCooking(){
        System.out.println("1 On Stove");
        System.out.println("2 put Vessel");
    }
}