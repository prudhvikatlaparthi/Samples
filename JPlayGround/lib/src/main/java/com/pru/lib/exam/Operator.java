package com.pru.lib.exam;

public class Operator {
    private String name;
    private String ssn;
    private String dept;
    private int salary;

    public Operator(String name, String ssn, String dept, int salary) {
        this.name = name;
        this.ssn = ssn;
        this.dept = dept;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

class OperatorImplementation {
    public Operator getOperatorInfo(String str) {
        String[] value = str.split("@");
        String name = value[0];

        String[] last = str.split("#");
        int salary = Integer.parseInt(last[1]);

        String ssn = str.substring(str.indexOf("@") + 1);
        ssn = ssn.substring(0, ssn.indexOf("-"));

        String dept = str.substring(str.indexOf("-") + 1);
        dept = dept.substring(0, dept.indexOf("#"));

        return new Operator(name, ssn, dept, salary);
    }

    public String getOperatorLevel(Operator e){
        String ssn = e.getSsn();
        String last3 =ssn.substring(ssn.length() - 3).trim();
        int value = Integer.parseInt(last3);
        if (value > 50 && value <= 100){
            return "L1";
        } else  if (value > 101 && value <= 150){
            return "L2";
        } else  if (value > 151 && value <= 200){
            return "L3";
        }
        else  if (value > 201 && value <= 250){
            return "L4";
        }
       return "NA";
    }

    public static void main(String[] args) {
        OperatorImplementation op = new OperatorImplementation();
        System.out.println(op.getOperatorInfo("Alex David@PC16CS046-SDE#8").toString());
        System.out.println(op.getOperatorLevel(op.getOperatorInfo("Alex David@PC16CS200-SDE#8")));
    }
}
