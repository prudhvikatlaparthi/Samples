package com.pru.lib;

public class TemporaryEmployee extends Employee{
    int hoursWorked;
    int hourlyWages;
    public TemporaryEmployee(int empId, String empName,
                             int hoursWorked, int hourlyWages) {
        super(empId, empName);
        this.hoursWorked = hoursWorked;
        this.hourlyWages = hourlyWages;
    }

    @Override
    public void calculateSalary() {
        salary = hoursWorked * hourlyWages;
    }
}
