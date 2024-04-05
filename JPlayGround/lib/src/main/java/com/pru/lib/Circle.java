package com.pru.lib;

class Shape {
    public double calArea() {
        return 0.0;
    }
}
public class Circle extends Shape {
    double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double calArea() {
        return 3.14 * radius * radius;
    }

    public static void main(String arg[]){
        Shape test = new Circle(5);
        System.out.println(test.calArea());
    }
}
