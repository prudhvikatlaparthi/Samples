package com.pru.lib;

public class EmpMain {
    public static void main(String[] arg) {
        Loan loan = new Loan();
        PermanentEmployee pe = new PermanentEmployee(1, "Raju", 2000);
        double loanAmount = loan.calculateLoanAmount(pe);
        System.out.println("Loan Amount of "+pe.getEmpName()+" is "+loanAmount);

        TemporaryEmployee te = new TemporaryEmployee(2, "Rani", 8, 1000);
        loanAmount = loan.calculateLoanAmount(te);
        System.out.println("Loan Amount of "+te.getEmpName()+" is "+loanAmount);

    }
}
