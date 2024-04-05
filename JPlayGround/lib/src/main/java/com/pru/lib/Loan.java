package com.pru.lib;

public class Loan {
    public double calculateLoanAmount(Employee emp) {
        double loanAmount = 0;
        emp.calculateSalary();
        if (emp instanceof PermanentEmployee) {
            loanAmount = emp.getSalary() * 0.15;
        } else if (emp instanceof TemporaryEmployee) {
            loanAmount = emp.getSalary() * 0.10;
        }
        return loanAmount;
    }
}
