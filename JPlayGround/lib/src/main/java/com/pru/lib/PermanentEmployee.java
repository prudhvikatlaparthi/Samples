package com.pru.lib;

public class PermanentEmployee extends Employee {
    double basicPay;

    public PermanentEmployee(int empId, String empName, double basicPay) {
        super(empId, empName);
        this.basicPay = basicPay;
    }

    @Override
    public void calculateSalary() {
        double pfAmount = basicPay * 0.12;
        salary = basicPay - pfAmount;
    }
}
