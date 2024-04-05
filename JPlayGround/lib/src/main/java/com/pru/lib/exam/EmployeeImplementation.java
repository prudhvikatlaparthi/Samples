package com.pru.lib.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

class Employee {
    private String name;
    private int salary;

    public Employee(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}

class EmployeeNameComparator implements Comparator<Employee> {

    @Override
    public int compare(Employee obj1, Employee obj2) {
        if (obj1 == obj2) {
            return 0;
        } else if (obj1 == null) {
            return -1;
        } else {
            return 1;
        }
    }
}

public class EmployeeImplementation {
    private ArrayList<Employee> empList;

    public EmployeeImplementation(ArrayList<Employee> empList) {
        this.empList = empList;
    }

    public ArrayList<Employee> sortyByName() {
        Comparator<Employee> comparator = new Comparator<Employee>() {
            @Override
            public int compare(Employee p1, Employee p2) {
                return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
            }
        };
        Collections.sort(empList,comparator);
        return empList;
    }

    public ArrayList<Employee> sortOnTheBasisOfSalary() {
        Comparator<Employee> comparator = new Comparator<Employee>() {
            @Override
            public int compare(Employee p1, Employee p2) {
                return p1.getSalary() - p2.getSalary();
            }
        };
        Collections.sort(empList,comparator);
        Collections.reverse(empList);
        return empList;
    }


}

class Tes {
    public static void main(String[] args) {
        ArrayList<Employee> em = new ArrayList<Employee>();
        em.add(new Employee("Rahul", 300));
        em.add(new Employee("app", 4000));
        em.add(new Employee("pru", 10));
        em.add(new Employee("bat", 20));
        EmployeeImplementation employeeImplementation = new EmployeeImplementation(em);
        System.out.println(employeeImplementation.sortOnTheBasisOfSalary().toString());
    }
}
