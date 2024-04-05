package com.pru.lib;

class Car {

    Car(int tires) {
        this.tires = tires;
    }

    int tires = 4;

    public void displaySpeedometer() {

    }

/*    public void displaySpeedometer(int speed) {

    }

    public void displaySpeedometer(int speed, int distance) {
//        return speed + distance;
    }*/

    public void accelerate() {

    }

    public void brake() {

    }
}

class Maruti extends Car {

    Maruti(int tires) {
        super(tires);
    }

    @Override
    public void displaySpeedometer() {
        super.displaySpeedometer();
    }
}